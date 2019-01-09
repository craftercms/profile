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
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.craftercms.security.authentication.impl.LogoutSuccessHandlerImpl}.
 *
 * @author avasquez
 */
public class LogoutSuccessHandlerImplTest {

    private static final String TARGET_URl = "/home";

    private LogoutSuccessHandlerImpl handler;

    @Before
    public void setUp() throws Exception {
        handler = new LogoutSuccessHandlerImpl();
        handler.setTargetUrl(TARGET_URl);
    }

    @Test
    public void testRedirectToTargetUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        handler.handle(context);

        assertEquals(TARGET_URl, response.getRedirectedUrl());
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
        assertTrue(response.isCommitted());
    }

}
