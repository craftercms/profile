/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.processors.impl;

import java.util.UUID;
import javax.servlet.http.HttpSession;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.authentication.RememberMeManager;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.BadCredentialsException;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.security.utils.tenant.DefaultTenantsResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.LoginProcessor}.
 *
 * @author avasquez
 */
public class LoginProcessorTest {

    private static final String[] TENANTS = new String[] {"default"};
    private static final String USERNAME = "jdoe";
    private static final String VALID_PASSWORD = "1234";
    private static final String INVALID_PASSWORD = "4321";
    private static final String TICKET = UUID.randomUUID().toString();

    private LoginProcessor processor;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private LoginSuccessHandler loginSuccessHandler;
    @Mock
    private LoginFailureHandler loginFailureHandler;
    @Mock
    private RememberMeManager rememberMeManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        DefaultTenantsResolver resolver = new DefaultTenantsResolver();
        resolver.setDefaultTenantNames(TENANTS);

        processor = new LoginProcessor();
        processor.setTenantsResolver(resolver);
        processor.setAuthenticationManager(authenticationManager);
        processor.setLoginSuccessHandler(loginSuccessHandler);
        processor.setLoginFailureHandler(loginFailureHandler);
        processor.setRememberMeManager(rememberMeManager);

        Profile profile = new Profile();
        profile.setUsername(USERNAME);

        when(authenticationManager.authenticateUser(TENANTS, USERNAME, VALID_PASSWORD)).thenReturn(
            new DefaultAuthentication(TICKET, profile));
        doThrow(BadCredentialsException.class).when(authenticationManager).authenticateUser(TENANTS, USERNAME,
                                                                                            INVALID_PASSWORD);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(LoginProcessor.DEFAULT_LOGIN_METHOD,
                LoginProcessor.DEFAULT_LOGIN_URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSession session = request.getSession(true);
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        request.setParameter(LoginProcessor.DEFAULT_USERNAME_PARAM, USERNAME);
        request.setParameter(LoginProcessor.DEFAULT_PASSWORD_PARAM, VALID_PASSWORD);
        session.setAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE,
                             new BadCredentialsException());
        session.setAttribute(SecurityUtils.AUTHENTICATION_EXCEPTION_SESSION_ATTRIBUTE,
                             new AuthenticationSystemException());

        processor.processRequest(context, chain);

        verify(chain, never()).processRequest(context);

        /** Removed Session are invalidated after login is ok.
         assertNull(session.getAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE));
         assertNull(session.getAttribute(SecurityUtils.AUTHENTICATION_EXCEPTION_SESSION_ATTRIBUTE));
         **/

        Authentication auth = SecurityUtils.getAuthentication(request);

        assertNotNull(auth);
        assertEquals(TICKET, auth.getTicket());
        assertNotNull(auth.getProfile());
        assertEquals(USERNAME, auth.getProfile().getUsername());

        verify(authenticationManager).authenticateUser(TENANTS, USERNAME, VALID_PASSWORD);
        verify(rememberMeManager).disableRememberMe(context);
        verify(loginSuccessHandler).handle(context, auth);

        request.setParameter(LoginProcessor.DEFAULT_REMEMBER_ME_PARAM, "true");

        processor.processRequest(context, chain);

        auth = SecurityUtils.getAuthentication(request);

        assertNotNull(auth);

        verify(rememberMeManager).enableRememberMe(auth, context);
    }

    @Test
    public void testLoginFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(LoginProcessor.DEFAULT_LOGIN_METHOD,
                                                                    LoginProcessor.DEFAULT_LOGIN_URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSession session = request.getSession(true);
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        request.setParameter(LoginProcessor.DEFAULT_USERNAME_PARAM, USERNAME);
        request.setParameter(LoginProcessor.DEFAULT_PASSWORD_PARAM, INVALID_PASSWORD);

        processor.processRequest(context, chain);

        verify(chain, never()).processRequest(context);

        assertNotNull(session.getAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE));

        Authentication auth = SecurityUtils.getAuthentication(request);

        assertNull(auth);

        verify(authenticationManager).authenticateUser(TENANTS, USERNAME, INVALID_PASSWORD);
        verify(loginFailureHandler).handle(eq(context), any(BadCredentialsException.class));
    }

}
