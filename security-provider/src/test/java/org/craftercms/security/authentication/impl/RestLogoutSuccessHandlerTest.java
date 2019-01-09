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
import org.craftercms.security.utils.testing.AbstractRestHandlerTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link org.craftercms.security.authentication.impl.RestLogoutSuccessHandler}.
 *
 * @author avasquez
 */
public class RestLogoutSuccessHandlerTest extends AbstractRestHandlerTestBase {

    private static final String EXPECTED_RESPONSE_CONTENT = "{\"message\":\"Logout successful\"}";

    private RestLogoutSuccessHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new RestLogoutSuccessHandler();
        handler.setResponseWriter(createResponseWriter());
    }

    @Test
    public void testHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/logout.json");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        handler.handle(context);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(EXPECTED_RESPONSE_CONTENT, response.getContentAsString());
    }

}
