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

import java.util.Date;
import javax.servlet.http.Cookie;

import org.bson.types.ObjectId;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.CurrentAuthenticationResolvingProcessor}.
 *
 * @author avasquez
 */
public class CurrentAuthenticationResolvingProcessorTest {

    private static final String TICKET = new ObjectId().toString();

    private CurrentAuthenticationResolvingProcessor processor;
    @Mock
    private AuthenticationManager authenticationManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        processor = new CurrentAuthenticationResolvingProcessor();
        processor.setAuthenticationManager(authenticationManager);
    }

    @Test
    public void testGetAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);
        Date profileLastModified = new Date();
        Cookie ticketCookie = new Cookie(SecurityUtils.TICKET_COOKIE_NAME, TICKET);
        Cookie profileLastModifiedCookie = new Cookie(SecurityUtils.PROFILE_LAST_MODIFIED_COOKIE_NAME,
                                                      String.valueOf(profileLastModified.getTime()));

        request.setCookies(ticketCookie, profileLastModifiedCookie);

        Profile profile = new Profile();
        profile.setLastModified(profileLastModified);

        Authentication auth = new DefaultAuthentication(TICKET, profile);

        when(authenticationManager.getAuthentication(TICKET, false)).thenReturn(auth);

        processor.processRequest(context, chain);

        verify(chain).processRequest(context);

        Authentication newAuth = SecurityUtils.getAuthentication(request);

        assertNotNull(newAuth);
        assertEquals(auth.getTicket(), newAuth.getTicket());
        assertEquals(auth.getProfile().getLastModified(), newAuth.getProfile().getLastModified());
    }

    @Test
    public void testGetAuthenticationProfileLastModifiedChanged() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);
        Date profileLastModified = new Date();
        Cookie ticketCookie = new Cookie(SecurityUtils.TICKET_COOKIE_NAME, TICKET);
        Cookie profileLastModifiedCookie = new Cookie(SecurityUtils.PROFILE_LAST_MODIFIED_COOKIE_NAME,
                String.valueOf(profileLastModified.getTime() + 60000));

        request.setCookies(ticketCookie, profileLastModifiedCookie);

        Profile profile = new Profile();
        profile.setLastModified(profileLastModified);

        Profile modifiedProfile = new Profile();
        modifiedProfile.setLastModified(new Date(profileLastModified.getTime() + 60000));

        Authentication auth = new DefaultAuthentication(TICKET, profile);
        Authentication modifiedAuth = new DefaultAuthentication(TICKET, modifiedProfile);

        when(authenticationManager.getAuthentication(TICKET, false)).thenReturn(auth);
        when(authenticationManager.getAuthentication(TICKET, true)).thenReturn(modifiedAuth);

        processor.processRequest(context, chain);

        verify(chain).processRequest(context);

        Authentication newAuth = SecurityUtils.getAuthentication(request);

        assertNotNull(newAuth);
        assertEquals(modifiedAuth.getTicket(), newAuth.getTicket());
        assertEquals(modifiedAuth.getProfile().getLastModified(), newAuth.getProfile().getLastModified());
    }

}
