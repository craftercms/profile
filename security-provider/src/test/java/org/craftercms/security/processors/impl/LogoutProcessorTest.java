/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.LogoutSuccessHandler;
import org.craftercms.security.authentication.RememberMeManager;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.LogoutProcessor}.
 *
 * @author avasquez
 */
public class LogoutProcessorTest {

    private static final String USERNAME = "jdoe";

    private LogoutProcessor processor;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private LogoutSuccessHandler logoutSuccessHandler;

    @Mock
    private RememberMeManager rememberMeManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        processor = new LogoutProcessor(authenticationManager, logoutSuccessHandler, rememberMeManager);
    }

    @Test
    public void testLogout() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(LogoutProcessor.DEFAULT_LOGOUT_METHOD,
                                                                    LogoutProcessor.DEFAULT_LOGOUT_URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);
        RequestSecurityProcessorChain chain = mock(RequestSecurityProcessorChain.class);

        Profile profile = new Profile();
        profile.setUsername(USERNAME);

        Authentication auth = new DefaultAuthentication(new ObjectId().toString(), profile);

        SecurityUtils.setAuthentication(request, auth);

        processor.processRequest(context, chain);

        verify(chain, never()).processRequest(context);

        assertNull(SecurityUtils.getAuthentication(request));

        verify(logoutSuccessHandler).handle(context);
    }

}
