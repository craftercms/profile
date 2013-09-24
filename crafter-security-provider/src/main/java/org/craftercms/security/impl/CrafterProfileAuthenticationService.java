/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.security.impl;

import java.io.Serializable;
import java.util.Map;

import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.UserAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link AuthenticationService}, using a Crafter Profile client.
 *
 * @author Alfonso Vásquez
 */
public class CrafterProfileAuthenticationService implements AuthenticationService {

    protected ProfileClient profileClient;
    protected String appUsername;
    protected String appPassword;

    public static final Logger logger = LoggerFactory.getLogger(CrafterProfileAuthenticationService.class);

    /**
     * Sets the profile client.
     */
    @Required
    public void setProfileClient(ProfileClient profileClient) {
        this.profileClient = profileClient;
    }

    /**
     * Sets the username for app authentication with Crafter Profile.
     */
    @Required
    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }

    /**
     * Sets the password for app authentication with Crafter Profile.
     */
    @Required
    public void setAppPassword(String appPassword) {
        this.appPassword = appPassword;
    }

    /**
     * {@inheritDoc}
     */
    public UserProfile getProfile(String ticket) throws AuthenticationException {
        Profile profile = profileClient.getProfileByTicketWithAllAttributes(getAppToken(), ticket);
        if (profile != null) {
            return new UserProfile(profile);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String authenticate(String tenantName, String username, String password) throws AuthenticationException {
        String appToken = getAppToken();

        try {
            return profileClient.getTicket(appToken, username, password, tenantName);
        } catch (UserAuthenticationFailedException e) {
            throw new UserAuthenticationException("User authentication for '" + username + "' failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void invalidateTicket(String ticket) throws AuthenticationException {
        profileClient.invalidateTicket(getAppToken(), ticket);
    }

    /**
     * Returns the authentication token for the application, by login with the application username and password.
     *
     * @return the app authentication token, which is used for all calls to Crafter Profile
     * @throws AuthenticationException
     */
    protected String getAppToken() throws AuthenticationException {
        try {
            return profileClient.getAppToken(appUsername, appPassword);
        } catch (AppAuthenticationFailedException e) {
            throw new AuthenticationSystemException("App authentication for '" + appUsername + "' failed", e);
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public UserProfile forgotPassword(String changePasswordUrl, String username,
                                      String tenantName) throws AuthenticationException {
        Profile profile = profileClient.forgotPassword(getAppToken(), changePasswordUrl, tenantName, username);
        if (profile != null) {
            return new UserProfile(profile);
        } else {
            return null;
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public UserProfile resetPassword(String password, String token) throws AuthenticationException {
        Profile profile = profileClient.resetPassword(getAppToken(), token, password);
        if (profile != null) {
            return new UserProfile(profile);
        } else {
            return null;
        }

    }
    
    @Override
    /**
     * {@inheritDoc}
     */
    public UserProfile createProfile(Map<String, Serializable>  queryParams) throws AuthenticationException {
        Profile profile = profileClient.createProfile(getAppToken(), queryParams);
        if (profile != null) {
            return new UserProfile(profile);
        } else {
            return null;
        }

    }
    
    @Override
    /**
     * {@inheritDoc}
     */
    public UserProfile verifyAccount(String token) throws AuthenticationException {
        Profile profile = profileClient.verifyProfile(getAppToken(), token);
        if (profile != null) {
            return new UserProfile(profile);
        } else {
            return null;
        }

    }

}
