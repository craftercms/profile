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

import java.util.UUID;

import javax.servlet.http.Cookie;

import org.bson.types.ObjectId;
import org.craftercms.commons.crypto.impl.NoOpTextEncryptor;
import org.craftercms.commons.http.CookieManager;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.exception.rememberme.CookieTheftException;
import org.craftercms.security.exception.rememberme.InvalidCookieException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.craftercms.security.authentication.impl.RememberMeManagerImpl.*;

/**
 * Unit tests for {@link org.craftercms.security.authentication.impl.RememberMeManagerImpl}.
 *
 * @author avasquez
 */
public class RememberMeManagerImplTest {

    private static final String LOGIN_ID = UUID.randomUUID().toString();
    private static final String LOGIN_TOKEN = UUID.randomUUID().toString();
    private static final String LOGIN_TOKEN2 = UUID.randomUUID().toString();
    private static final ObjectId PROFILE_ID = ObjectId.get();

    private RememberMeManagerImpl rememberMeManager;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ProfileService profileService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(authenticationService.createPersistentLogin(PROFILE_ID.toString())).thenReturn(getLogin());
        when(authenticationService.getPersistentLogin(LOGIN_ID)).thenReturn(getLogin());
        when(authenticationService.refreshPersistentLoginToken(LOGIN_ID)).thenReturn(getLogin2());
        when(authenticationManager.authenticateUser(getProfile(), true)).thenReturn(getAuthentication());
        when(profileService.getProfile(PROFILE_ID.toString(), new String[0])).thenReturn(getProfile());

        rememberMeManager = new RememberMeManagerImpl();
        rememberMeManager.setAuthenticationService(authenticationService);
        rememberMeManager.setAuthenticationManager(authenticationManager);
        rememberMeManager.setProfileService(profileService);
        rememberMeManager.setEncryptor(new NoOpTextEncryptor());
        rememberMeManager.setRememberMeCookieManager(new CookieManager());
    }

    @Test
    public void testEnableRememberMe() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        rememberMeManager.enableRememberMe(getAuthentication(), context);

        String cookieValue = response.getCookie(REMEMBER_ME_COOKIE_NAME).getValue();

        assertEquals(getSerializedLogin(), cookieValue);
    }

    @Test
    public void testDisableRememberMe() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        request.setCookies(new Cookie(REMEMBER_ME_COOKIE_NAME, getSerializedLogin()));

        rememberMeManager.disableRememberMe(context);

        assertNull(response.getCookie(REMEMBER_ME_COOKIE_NAME).getValue());

        verify(authenticationService).deletePersistentLogin(LOGIN_ID);
    }

    @Test
    public void testAutoLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        request.setCookies(new Cookie(REMEMBER_ME_COOKIE_NAME, getSerializedLogin()));

        Authentication auth = rememberMeManager.autoLogin(context);

        assertNotNull(auth);
        assertEquals(getProfile(), auth.getProfile());

        String cookieValue = response.getCookie(REMEMBER_ME_COOKIE_NAME).getValue();

        assertEquals(getSerializedLoginWithRefreshedToken(), cookieValue);
    }

    @Test
    public void testAutoLoginWithInvalidId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        request.setCookies(new Cookie(REMEMBER_ME_COOKIE_NAME, getSerializedLoginWithInvalidId()));

        Authentication auth = rememberMeManager.autoLogin(context);

        assertNull(auth);

        assertNull(response.getCookie(REMEMBER_ME_COOKIE_NAME).getValue());
    }

    @Test(expected = InvalidCookieException.class)
    public void testAutoLoginWithInvalidProfile() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        request.setCookies(new Cookie(REMEMBER_ME_COOKIE_NAME, getSerializedLoginWithInvalidProfile()));

        rememberMeManager.autoLogin(context);
    }

    @Test(expected = CookieTheftException.class)
    public void testAutoLoginWithInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        request.setCookies(new Cookie(REMEMBER_ME_COOKIE_NAME, getSerializedLoginWithInvalidToken()));

        rememberMeManager.autoLogin(context);
    }

    protected String getSerializedLogin() {
        return serializeLogin(LOGIN_ID, PROFILE_ID.toString(), LOGIN_TOKEN);
    }

    protected String getSerializedLoginWithRefreshedToken() {
        return serializeLogin(LOGIN_ID, PROFILE_ID.toString(), LOGIN_TOKEN2);
    }

    protected String getSerializedLoginWithInvalidId() {
        return serializeLogin(UUID.randomUUID().toString(), PROFILE_ID.toString(), LOGIN_TOKEN);
    }

    protected String getSerializedLoginWithInvalidProfile() {
        return serializeLogin(LOGIN_ID, ObjectId.get().toString(), LOGIN_TOKEN);
    }

    protected String getSerializedLoginWithInvalidToken() {
        return serializeLogin(LOGIN_ID, PROFILE_ID.toString(), UUID.randomUUID().toString());
    }

    protected String serializeLogin(String id, String profileId, String token) {
        StringBuilder serializedLogin = new StringBuilder();
        serializedLogin.append(id).append(SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(profileId).append(SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(token);

        return serializedLogin.toString();
    }

    protected PersistentLogin getLogin() {
        PersistentLogin login = new PersistentLogin();
        login.setId(LOGIN_ID);
        login.setToken(LOGIN_TOKEN);
        login.setProfileId(PROFILE_ID.toString());

        return login;
    }

    protected PersistentLogin getLogin2() {
        PersistentLogin login = new PersistentLogin();
        login.setId(LOGIN_ID);
        login.setToken(LOGIN_TOKEN2);
        login.setProfileId(PROFILE_ID.toString());

        return login;
    }

    protected Profile getProfile() {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID);

        return profile;
    }

    protected Authentication getAuthentication() {
        return new DefaultAuthentication(null, getProfile(), true);
    }

}
