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
package org.craftercms.profile.interceptors;

import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.TenantActions;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.craftercms.profile.exceptions.ExpiredAccessTokenException;
import org.craftercms.profile.exceptions.MissingAccessTokenIdParamException;
import org.craftercms.profile.permissions.Application;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.profile.interceptors.AccessTokenCheckingInterceptor}.
 *
 * @author avasquez
 */
public class AccessTokenCheckingInterceptorTest {

    private static final String APPLICATION = "crafterengine";

    private static final String NORMAL_TOKEN_ID =    "bfb7fb40-c04c-11e3-8a33-0800200c9a66";
    private static final String EXPIRED_TOKEN_ID =   "d09bb640-c04c-11e3-8a33-0800200c9a66";

    private AccessTokenCheckingInterceptor interceptor;
    @Mock
    private AccessTokenRepository tokenRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(tokenRepository.findById(NORMAL_TOKEN_ID.toString())).thenReturn(getNormalToken());
        when(tokenRepository.findById(EXPIRED_TOKEN_ID.toString())).thenReturn(getExpiredToken());
        
        interceptor = new AccessTokenCheckingInterceptor();
        interceptor.setTokenRepository(tokenRepository);
    }

    @Test
    public void testPreHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(ProfileConstants.PARAM_ACCESS_TOKEN_ID, NORMAL_TOKEN_ID.toString());

        interceptor.preHandle(request, null, null);

        Application app = Application.getCurrent();

        TenantPermission permission = new TenantPermission();
        permission.allow(TenantActions.ALL_ACTIONS);

        assertNotNull(app);
        assertEquals(APPLICATION, app.getName());
        assertEquals(Arrays.asList(permission), app.getTenantPermissions());

        verify(tokenRepository).findById(NORMAL_TOKEN_ID.toString());

        Application.clear();
    }

    @Test(expected = MissingAccessTokenIdParamException.class)
    public void testPreHandleMissingAccessTokenIdParam() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();

        interceptor.preHandle(request, null, null);
    }

    @Test(expected = ExpiredAccessTokenException.class)
    public void testPreHandleExpiredAccessToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(ProfileConstants.PARAM_ACCESS_TOKEN_ID, EXPIRED_TOKEN_ID.toString());

        interceptor.preHandle(request, null, null);
    }

    private AccessToken getNormalToken() {
        TenantPermission permission = new TenantPermission();
        permission.allow(TenantActions.ALL_ACTIONS);

        AccessToken token = new AccessToken();
        token.setId(NORMAL_TOKEN_ID);
        token.setApplication(APPLICATION);
        token.setTenantPermissions(Arrays.asList(permission));
        token.setExpiresOn(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)));

        return token;
    }

    private AccessToken getExpiredToken() {
        TenantPermission permission = new TenantPermission();
        permission.allow(TenantActions.ALL_ACTIONS);

        AccessToken token = new AccessToken();
        token.setId(EXPIRED_TOKEN_ID);
        token.setApplication(APPLICATION);
        token.setTenantPermissions(Arrays.asList(permission));
        token.setExpiresOn(new Date());

        return token;
    }

}
