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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        RequestContext context = new RequestContext(request, response);

        rememberMeManager.enableRememberMe(getAuthentication(), context);

        String cookieValue = response.getCookie(RememberMeManagerImpl.REMEMBER_ME_COOKIE_NAME).getValue();

        assertEquals(getSerializedLogin(), cookieValue);
    }

    @Test
    public void testDisableRememberMe() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response);

        request.setCookies(new Cookie(RememberMeManagerImpl.REMEMBER_ME_COOKIE_NAME, getSerializedLogin()));

        rememberMeManager.disableRememberMe(context);

        assertNull(response.getCookie(RememberMeManagerImpl.REMEMBER_ME_COOKIE_NAME).getValue());

        verify(authenticationService).deletePersistentLogin(LOGIN_ID);
    }

    @Test
    public void testAutoLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response);

        request.setCookies(new Cookie(RememberMeManagerImpl.REMEMBER_ME_COOKIE_NAME, getSerializedLogin()));

        rememberMeManager.autoLogin(context);

        String cookieValue = response.getCookie(RememberMeManagerImpl.REMEMBER_ME_COOKIE_NAME).getValue();

        assertEquals(getSerializedLogin2(), cookieValue);
    }

    protected String getSerializedLogin() {
        StringBuilder serializedLogin = new StringBuilder();
        serializedLogin.append(LOGIN_ID).append(RememberMeManagerImpl.SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(PROFILE_ID.toString()).append(RememberMeManagerImpl.SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(LOGIN_TOKEN);

        return serializedLogin.toString();
    }

    protected String getSerializedLogin2() {
        StringBuilder serializedLogin = new StringBuilder();
        serializedLogin.append(LOGIN_ID).append(RememberMeManagerImpl.SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(PROFILE_ID.toString()).append(RememberMeManagerImpl.SERIALIZED_LOGIN_SEPARATOR);
        serializedLogin.append(LOGIN_TOKEN2);

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
