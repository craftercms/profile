/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.security.processors.impl;

import javax.servlet.http.HttpSession;

import org.bson.types.ObjectId;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.BadCredentialsException;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
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

    private static final String TENANT =            "default";
    private static final String USERNAME =          "jdoe";
    private static final String VALID_PASSWORD =    "1234";
    private static final String INVALID_PASSWORD =  "4321";
    private static final String TICKET =            new ObjectId().toString();

    private LoginProcessor processor;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private LoginSuccessHandler loginSuccessHandler;
    @Mock
    private LoginFailureHandler loginFailureHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        processor = new LoginProcessor();
        processor.setAuthenticationManager(authenticationManager);
        processor.setLoginSuccessHandler(loginSuccessHandler);
        processor.setLoginFailureHandler(loginFailureHandler);

        Profile profile = new Profile();
        profile.setUsername(USERNAME);

        when(authenticationManager.authenticateUser(TENANT, USERNAME, VALID_PASSWORD)).thenReturn(
                new DefaultAuthentication(TICKET, profile));
        doThrow(BadCredentialsException.class).when(authenticationManager).authenticateUser(TENANT, USERNAME,
                INVALID_PASSWORD);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(LoginProcessor.DEFAULT_LOGIN_METHOD,
                LoginProcessor.DEFAULT_LOGIN_URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSession session = request.getSession(true);
        RequestContext context = new RequestContext(request, response);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        SecurityUtils.setTenant(request, TENANT);

        request.setParameter(LoginProcessor.DEFAULT_USERNAME_PARAM, USERNAME);
        request.setParameter(LoginProcessor.DEFAULT_PASSWORD_PARAM, VALID_PASSWORD);
        session.setAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE,
                new BadCredentialsException());
        session.setAttribute(SecurityUtils.AUTHENTICATION_SYSTEM_EXCEPTION_SESSION_ATTRIBUTE,
                new AuthenticationSystemException());

        processor.processRequest(context, chain);

        verify(chain, never()).processRequest(context);

        assertNull(session.getAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE));
        assertNull(session.getAttribute(SecurityUtils.AUTHENTICATION_SYSTEM_EXCEPTION_SESSION_ATTRIBUTE));

        Authentication auth = SecurityUtils.getAuthentication(request);

        assertNotNull(auth);
        assertEquals(TICKET, auth.getTicket());
        assertNotNull(auth.getProfile());
        assertEquals(USERNAME, auth.getProfile().getUsername());

        verify(authenticationManager).authenticateUser(TENANT, USERNAME, VALID_PASSWORD);
        verify(loginSuccessHandler).handle(context, auth);
    }

    @Test
    public void testLoginFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(LoginProcessor.DEFAULT_LOGIN_METHOD,
                LoginProcessor.DEFAULT_LOGIN_URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSession session = request.getSession(true);
        RequestContext context = new RequestContext(request, response);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        SecurityUtils.setTenant(request, TENANT);

        request.setParameter(LoginProcessor.DEFAULT_USERNAME_PARAM, USERNAME);
        request.setParameter(LoginProcessor.DEFAULT_PASSWORD_PARAM, INVALID_PASSWORD);

        processor.processRequest(context, chain);

        verify(chain, never()).processRequest(context);

        assertNotNull(session.getAttribute(SecurityUtils.BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE));

        Authentication auth = SecurityUtils.getAuthentication(request);

        assertNull(auth);

        verify(authenticationManager).authenticateUser(TENANT, USERNAME, INVALID_PASSWORD);
        verify(loginFailureHandler).handle(eq(context), any(BadCredentialsException.class));
    }

}
