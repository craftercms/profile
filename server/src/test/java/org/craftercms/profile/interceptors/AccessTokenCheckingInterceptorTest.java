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
package org.craftercms.profile.interceptors;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.exceptions.AccessDeniedException;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.craftercms.profile.utils.AccessTokenUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.profile.interceptors.AccessTokenCheckingInterceptor}.
 *
 * @author avasquez
 */
public class AccessTokenCheckingInterceptorTest {

    private static final String NORMAL_TOKEN_ID = UUID.randomUUID().toString();
    private static final String EXPIRED_TOKEN_ID =   UUID.randomUUID().toString();

    private static final String APPLICATION = "profile-admin";

    private AccessTokenCheckingInterceptor interceptor;
    @Mock
    private AccessTokenRepository tokenRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(tokenRepository.findByStringId(NORMAL_TOKEN_ID)).thenReturn(getNormalToken());
        when(tokenRepository.findByStringId(EXPIRED_TOKEN_ID)).thenReturn(getExpiredToken());
        
        interceptor = new AccessTokenCheckingInterceptor();
        interceptor.setAccessTokenRepository(tokenRepository);
        interceptor.setUrlsToInclude(new String[] { ".*" });
    }

    @Test
    public void testPreHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(ProfileConstants.PARAM_ACCESS_TOKEN_ID, NORMAL_TOKEN_ID);

        interceptor.preHandle(request, null, null);

        AccessToken token = AccessTokenUtils.getAccessToken(request);

        TenantPermission permission = new TenantPermission();
        permission.allow("*");

        assertNotNull(token);
        assertEquals(APPLICATION, token.getApplication());
        assertTrue(token.isMaster());
        assertEquals(Arrays.asList(permission), token.getTenantPermissions());

        verify(tokenRepository).findByStringId(NORMAL_TOKEN_ID);
    }

    @Test(expected = AccessDeniedException.MissingAccessToken.class)
    public void testPreHandleMissingAccessTokenIdParam() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();

        interceptor.preHandle(request, null, null);
    }

    @Test(expected = AccessDeniedException.ExpiredAccessToken.class)
    public void testPreHandleExpiredAccessToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(ProfileConstants.PARAM_ACCESS_TOKEN_ID, EXPIRED_TOKEN_ID);

        interceptor.preHandle(request, null, null);
    }

    private AccessToken getNormalToken() {
        TenantPermission permission = new TenantPermission();
        permission.allowAny();

        AccessToken token = new AccessToken();
        token.setId(NORMAL_TOKEN_ID);
        token.setApplication(APPLICATION);
        token.setMaster(true);
        token.setTenantPermissions(Arrays.asList(permission));
        token.setExpiresOn(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)));

        return token;
    }

    private AccessToken getExpiredToken() {
        TenantPermission permission = new TenantPermission();
        permission.allowAny();

        AccessToken token = new AccessToken();
        token.setId(EXPIRED_TOKEN_ID);
        token.setApplication(APPLICATION);
        token.setMaster(true);
        token.setTenantPermissions(Arrays.asList(permission));
        token.setExpiresOn(new Date());

        return token;
    }

}
