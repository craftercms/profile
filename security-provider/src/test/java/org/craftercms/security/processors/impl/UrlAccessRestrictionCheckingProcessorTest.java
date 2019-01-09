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

import java.util.Collections;

import org.bson.types.ObjectId;
import org.craftercms.commons.collections.SetUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.UrlAccessRestrictionCheckingProcessor}.
 *
 * @author avasquez
 */
public class UrlAccessRestrictionCheckingProcessorTest {

    private static final String URL =           "/admin";
    private static final String ADMIN_ROLE =    "ADMIN";

    private UrlAccessRestrictionCheckingProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new UrlAccessRestrictionCheckingProcessor();
        processor.setUrlRestrictions(Collections.singletonMap(URL, "hasRole('" + ADMIN_ROLE + "')"));
    }

    @Test
    public void testAllowedAccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        Profile profile = new Profile();
        profile.setRoles(SetUtils.asSet(ADMIN_ROLE));

        SecurityUtils.setAuthentication(request, new DefaultAuthentication(new ObjectId().toString(), profile));

        processor.processRequest(context, chain);

        verify(chain).processRequest(context);
    }

    @Test(expected = AccessDeniedException.class)
    public void testUnAllowedAccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        SecurityUtils.setAuthentication(request, new DefaultAuthentication(new ObjectId().toString(), new Profile()));

        processor.processRequest(context, chain);
    }

}
