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

import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.Cookie;

import org.bson.types.ObjectId;
import org.craftercms.commons.http.CookieManager;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.AddSecurityCookiesProcessor}.
 *
 * @author avasquez
 */
public class AddSecurityCookiesProcessorTest {

    private AddSecurityCookiesProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new AddSecurityCookiesProcessor();
        processor.setTicketCookieManager(new CookieManager());
        processor.setProfileLastModifiedCookieManager(new CookieManager());
    }

    @Test
    public void testAddCookiesLoggedIn() throws Exception {
        String ticket = new ObjectId().toString();
        Date lastModified = new Date();

        Profile profile = new Profile();
        profile.setLastModified(lastModified);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessor flushResponseProcessor = new RequestSecurityProcessor() {

            @Override
            public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain)
                    throws Exception {
                context.getResponse().getOutputStream().flush();
            }

        };

        RequestSecurityProcessorChain chain = new RequestSecurityProcessorChainImpl(Arrays.asList(processor,
                flushResponseProcessor).iterator());

        Authentication auth = new DefaultAuthentication(ticket, profile);
        SecurityUtils.setAuthentication(request, auth);

        processor.processRequest(context, chain);

        Cookie ticketCookie = response.getCookie(SecurityUtils.TICKET_COOKIE_NAME);

        assertNotNull(ticketCookie);
        assertEquals(ticket, ticketCookie.getValue());

        Cookie profileLastModifiedCookie = response.getCookie(SecurityUtils.PROFILE_LAST_MODIFIED_COOKIE_NAME);

        assertNotNull(profileLastModifiedCookie);
        assertEquals(profile.getLastModified().getTime(), Long.parseLong(profileLastModifiedCookie.getValue()));
    }

    @Test
    public void testAddCookiesLoggedOut() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessor flushResponseProcessor = new RequestSecurityProcessor() {

            @Override
            public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain)
                    throws Exception {
                context.getResponse().getOutputStream().flush();
            }

        };

        Cookie ticketCookie = new Cookie(SecurityUtils.TICKET_COOKIE_NAME, new ObjectId().toString());
        Cookie profileLastModifiedCookie = new Cookie(SecurityUtils.PROFILE_LAST_MODIFIED_COOKIE_NAME,
                String.valueOf(System.currentTimeMillis()));

        request.setCookies(ticketCookie, profileLastModifiedCookie);

        RequestSecurityProcessorChain chain = new RequestSecurityProcessorChainImpl(Arrays.asList(processor,
                flushResponseProcessor).iterator());

        processor.processRequest(context, chain);

        ticketCookie = response.getCookie(SecurityUtils.TICKET_COOKIE_NAME);

        assertNotNull(ticketCookie);
        assertEquals(null, ticketCookie.getValue());
        assertEquals(0, ticketCookie.getMaxAge());

        profileLastModifiedCookie = response.getCookie(SecurityUtils.PROFILE_LAST_MODIFIED_COOKIE_NAME);

        assertNotNull(profileLastModifiedCookie);
        assertEquals(null, profileLastModifiedCookie.getValue());
        assertEquals(0, profileLastModifiedCookie.getMaxAge());
    }

}
