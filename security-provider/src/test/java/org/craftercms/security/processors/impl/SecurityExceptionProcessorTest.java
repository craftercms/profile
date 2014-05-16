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

import org.bson.types.ObjectId;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.AuthenticationRequiredHandler;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.authorization.AccessDeniedHandler;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.exception.AuthenticationRequiredException;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.SecurityExceptionProcessor}.
 *
 * @author avasquez
 */
public class SecurityExceptionProcessorTest {

    private SecurityExceptionProcessor processor;
    @Mock
    private AuthenticationRequiredHandler authenticationRequiredHandler;
    @Mock
    private AccessDeniedHandler accessDeniedHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        processor = new SecurityExceptionProcessor();
        processor.setAuthenticationRequiredHandler(authenticationRequiredHandler);
        processor.setAccessDeniedHandler(accessDeniedHandler);
    }

    @Test
    public void testAuthenticationRequired() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        doThrow(AuthenticationRequiredException.class).when(chain).processRequest(context);

        processor.processRequest(context, chain);

        verify(chain).processRequest(context);
        verify(authenticationRequiredHandler).handle(eq(context), any(AuthenticationRequiredException.class));
    }

    @Test
    public void testAccessDeniedNoAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        doThrow(AccessDeniedException.class).when(chain).processRequest(context);

        processor.processRequest(context, chain);

        verify(chain).processRequest(context);
        verify(authenticationRequiredHandler).handle(eq(context), any(AuthenticationRequiredException.class));
    }

    @Test
    public void testAccessDeniedWithAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        doThrow(AccessDeniedException.class).when(chain).processRequest(context);

        SecurityUtils.setAuthentication(request, new DefaultAuthentication(new ObjectId().toString(), new Profile()));

        processor.processRequest(context, chain);

        verify(chain).processRequest(context);
        verify(accessDeniedHandler).handle(eq(context), any(AccessDeniedException.class));
    }

    @Test(expected = Exception.class)
    public void testNonSecurityException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        doThrow(Exception.class).when(chain).processRequest(context);

        processor.processRequest(context, chain);
    }

}
