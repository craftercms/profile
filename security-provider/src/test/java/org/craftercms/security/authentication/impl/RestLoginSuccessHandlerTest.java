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
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.testing.AbstractRestHandlerTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link org.craftercms.security.authentication.impl.RestLoginSuccessHandler}.
 *
 * @author avasquez
 */
public class RestLoginSuccessHandlerTest extends AbstractRestHandlerTestBase {

    private RestLoginSuccessHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new RestLoginSuccessHandler();
        handler.setResponseWriter(createResponseWriter());
    }

    @Test
    public void testHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login.json");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        String ticket = UUID.randomUUID().toString();
        ObjectId profileId = new ObjectId();

        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUsername("jdoe");
        profile.setPassword("1234");
        profile.setEmail("jdoe@craftercms.org");

        Authentication auth = new DefaultAuthentication(ticket.toString(), profile);

        handler.handle(context, auth);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("{\"ticket\":\"" + ticket + "\",\"profile\":{\"username\":\"jdoe\"," +
                     "\"password\":\"1234\",\"email\":\"jdoe@craftercms.org\",\"verified\":false," +
                     "\"enabled\":false,\"createdOn\":null,\"lastModified\":null,\"tenant\":null,\"roles\":[]," +
                     "\"attributes\":{},\"failedLoginAttempts\":0,\"lastFailedLogin\":null,\"id\":\"" +
                     profileId.toString() + "\"},\"remembered\":false}", response.getContentAsString());
    }

}
