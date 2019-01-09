/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.security.authentication.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.CryptoException;
import org.craftercms.commons.crypto.TextEncryptor;
import org.craftercms.commons.http.CookieManager;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.RememberMeManager;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.rememberme.CookieTheftException;
import org.craftercms.security.exception.rememberme.InvalidCookieException;
import org.craftercms.security.exception.rememberme.RememberMeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.security.authentication.RememberMeManager}.
 *
 * @author avasquez
 */
public class RememberMeManagerImpl implements RememberMeManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationManagerImpl.class);

    public static final String REMEMBER_ME_COOKIE_NAME = "remember-me";
    public static final char SERIALIZED_LOGIN_SEPARATOR = ':';

    protected AuthenticationService authenticationService;
    protected AuthenticationManager authenticationManager;
    protected ProfileService profileService;
    protected TextEncryptor encryptor;
    protected CookieManager rememberMeCookieManager;

    @Required
    public void setAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Required
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Required
    public void setProfileService(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setEncryptor(final TextEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Required
    public void setRememberMeCookieManager(final CookieManager rememberMeCookieManager) {
        this.rememberMeCookieManager = rememberMeCookieManager;
    }

    @Override
    public Authentication autoLogin(RequestContext context) throws RememberMeException {
        PersistentLogin login = getPersistentLoginFromCookie(context.getRequest());

        if (login != null) {
            PersistentLogin actualLogin;

            try {
                actualLogin = authenticationService.getPersistentLogin(login.getId());
            } catch (ProfileException e) {
                throw new RememberMeException("Error retrieving persistent login '" + login.getProfileId() + "'");
            }

            if (actualLogin != null) {
                if (!login.getProfileId().equals(actualLogin.getProfileId())) {
                    throw new InvalidCookieException("Profile ID mismatch");
                } else if (!login.getToken().equals(actualLogin.getToken())) {
                    throw new CookieTheftException("Token mismatch. Implies a cookie theft");
                } else {
                    String loginId = actualLogin.getId();
                    String profileId = actualLogin.getProfileId();

                    logger.debug("Remember me cookie match for {}. Starting auto-login", actualLogin);

                    Authentication auth;
                    try {
                        auth = authenticate(profileId);
                    } catch (AuthenticationException e) {
                        // Delete remember me cookie so that we don't retry auto login in next request
                        disableRememberMe(loginId, context);

                        throw new RememberMeException("Unable to auto-login user '" + profileId + "'", e);
                    }

                    updateRememberMe(loginId, context);

                    return auth;
                }
            } else {
                logger.debug("No persistent login found for ID '{}' (has possibly expired)", login.getId());

                deleteRememberMeCookie(context.getResponse());

                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void enableRememberMe(Authentication authentication, RequestContext context) throws RememberMeException {
        String profileId = authentication.getProfile().getId().toString();
        PersistentLogin login;

        try {
            login = authenticationService.createPersistentLogin(profileId);
        } catch (ProfileException e) {
            throw new RememberMeException("Error creating persistent login for profile '" + profileId + "'", e);
        }

        logger.debug("Persistent login created: {}", login);

        addRememberMeCookie(serializeLogin(login), context.getResponse());
    }

    @Override
    public void disableRememberMe(RequestContext context) throws RememberMeException {
        PersistentLogin login = getPersistentLoginFromCookie(context.getRequest());
        if (login != null) {
            disableRememberMe(login.getId(), context);
        }
    }

    protected void disableRememberMe(String loginId, RequestContext context) throws RememberMeException {
        deleteRememberMeCookie(context.getResponse());

        try {
            authenticationService.deletePersistentLogin(loginId);
        } catch (ProfileException e) {
            throw new RememberMeException("Error invalidating persistent login '" + loginId + "'");
        }

        logger.debug("Persistent login '{}' invalidated", loginId);
    }

    protected void updateRememberMe(String loginId, RequestContext context) throws RememberMeException {
        PersistentLogin login;
        try {
            login = authenticationService.refreshPersistentLoginToken(loginId);
        } catch (ProfileException e) {
            throw new RememberMeException("Unable to update persistent login '" + loginId + "'", e);
        }

        logger.debug("Persistent login updated: {}", login);

        addRememberMeCookie(serializeLogin(login), context.getResponse());
    }

    protected String serializeLogin(PersistentLogin login) throws RememberMeException {
        StringBuilder serializedLogin = new StringBuilder();
        serializedLogin.append(login.getId()).append(SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(login.getProfileId()).append(SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(login.getToken());

        try {
            return encryptor.encrypt(serializedLogin.toString());
        } catch (CryptoException e) {
            throw new RememberMeException("Unable to encrypt remember me cookie", e);
        }
    }

    protected PersistentLogin deserializeLogin(String serializedLogin) throws RememberMeException {
        String decryptedLogin;
        try {
            decryptedLogin = encryptor.decrypt(serializedLogin);
        } catch (CryptoException e) {
            throw new RememberMeException("Unable to decrypt remember me cookie", e);
        }

        String[] splitSerializedLogin = StringUtils.split(decryptedLogin, SERIALIZED_LOGIN_SEPARATOR);

        if (ArrayUtils.isNotEmpty(splitSerializedLogin) && splitSerializedLogin.length == 3) {
            PersistentLogin login = new PersistentLogin();
            login.setId(splitSerializedLogin[0]);
            login.setProfileId(splitSerializedLogin[1]);
            login.setToken(splitSerializedLogin[2]);

            return login;
        } else {
            throw new InvalidCookieException("Invalid format of remember me cookie");
        }
    }

    protected void addRememberMeCookie(String cookieValue, HttpServletResponse response) {
        rememberMeCookieManager.addCookie(REMEMBER_ME_COOKIE_NAME, cookieValue, response);
    }

    protected String getRememberMeCookie(HttpServletRequest request) {
        return HttpUtils.getCookieValue(REMEMBER_ME_COOKIE_NAME, request);
    }

    protected void deleteRememberMeCookie(HttpServletResponse response) {
        rememberMeCookieManager.deleteCookie(REMEMBER_ME_COOKIE_NAME, response);
    }

    protected PersistentLogin getPersistentLoginFromCookie(HttpServletRequest request) {
        String cookie = getRememberMeCookie(request);
        if (StringUtils.isNotEmpty(cookie)) {
            return deserializeLogin(cookie);
        } else {
            return null;
        }
    }

    protected Authentication authenticate(String profileId) throws AuthenticationException {
        Profile profile;
        try {
            profile = profileService.getProfile(profileId);
        } catch (ProfileException e) {
            throw new AuthenticationSystemException("Error retrieving profile '" + profileId + "'", e);
        }

        if (profile != null) {
            return authenticationManager.authenticateUser(profile, true);
        } else {
            throw new AuthenticationSystemException("No profile found for ID '" + profileId + "'");
        }
    }

}
