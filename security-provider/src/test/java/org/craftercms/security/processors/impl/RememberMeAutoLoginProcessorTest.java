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

package org.craftercms.security.processors.impl;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.RememberMeManager;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.RememberMeAutoLoginProcessor}.
 *
 * @author avasquez
 */
public class RememberMeAutoLoginProcessorTest {

    private RememberMeAutoLoginProcessor processor;
    @Mock
    private RememberMeManager rememberMeManager;
    @Mock
    private Authentication authentication;
    @Mock
    private RequestSecurityProcessorChain chain;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(rememberMeManager.autoLogin(any(RequestContext.class))).thenReturn(authentication);

        processor = new RememberMeAutoLoginProcessor();
        processor.setRememberMeManager(rememberMeManager);
    }

    @Test
    public void testProcessRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        processor.processRequest(context, chain);

        assertNotNull(SecurityUtils.getAuthentication(request));
        assertEquals(authentication, SecurityUtils.getAuthentication(request));

        verify(rememberMeManager).autoLogin(context);
        verify(chain).processRequest(context);
    }

    @Test
    public void testProcessRequestWithPreviousAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        SecurityUtils.setAuthentication(request, authentication);

        processor.processRequest(context, chain);

        verify(rememberMeManager, never()).autoLogin(context);
        verify(chain).processRequest(context);
    }

}
