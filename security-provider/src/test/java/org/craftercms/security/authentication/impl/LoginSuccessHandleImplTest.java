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

import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.security.authentication.impl.LogoutSuccessHandlerImpl}.
 *
 * @author avasquez
 */
public class LoginSuccessHandleImplTest {

    private static final String SAVED_REQUEST_URL =     "/myprofile";
    private static final String DEFAULT_TARGET_URL =    "/home";

    private LoginSuccessHandlerImpl handler;
    @Mock
    private RequestCache requestCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        handler = new LoginSuccessHandlerImpl();
        handler.setRequestCache(requestCache);
        handler.setDefaultTargetUrl(DEFAULT_TARGET_URL);
    }

    @Test
    public void testRedirectToSavedRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        SavedRequest savedRequest = mock(SavedRequest.class);

        when(savedRequest.getRedirectUrl()).thenReturn(SAVED_REQUEST_URL);
        when(requestCache.getRequest(request, response)).thenReturn(savedRequest);

        handler.handle(context, mock(Authentication.class));

        assertEquals(SAVED_REQUEST_URL, response.getRedirectedUrl());
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
        assertTrue(response.isCommitted());
    }

    @Test
    public void testRedirectToDefaultTargetUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        handler.handle(context, mock(Authentication.class));

        assertEquals(DEFAULT_TARGET_URL, response.getRedirectedUrl());
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
        assertTrue(response.isCommitted());
    }

}
