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

package org.craftercms.profile.services;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.craftercms.commons.collections.SetUtils;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.api.services.AccessTokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link AccessTokenService}.
 *
 * @author avasquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:crafter/profile/extension/client-context.xml")
public class AccessTokenServiceIT  {

    private static final String ADMIN_CONSOLE_TOKEN_ID = "e8f5170c-877b-416f-b70f-4b09772f8e2d";
    private static final String CRAFTER_SOCIAL_TOKEN_ID = "2ba3ac10-c43e-11e3-9c1a-0800200c9a66";
    private static final String RANDOM_APP_TOKEN_ID = "f91cdaf0-e5c6-11e3-ac10-0800200c9a66";

    private static final String ADMIN_CONSOLE_APPLICATION = "profile-admin";
    private static final String CRAFTER_STUDIO_APPLICATION = "studio";

    private static final Date EXPIRES_ON = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365));

    @Autowired
    private AccessTokenService accessTokenService;

    @Test
    public void testCreateAccessToken() throws Exception {
        AccessToken token = accessTokenService.createToken(getCrafterStudioAccessToken());
        try {
            assertNotNull(token);
            assertNotNull(token.getId());
            assertEquals(CRAFTER_STUDIO_APPLICATION, token.getApplication());
            assertEquals(true, token.isMaster());
            assertEquals(EXPIRES_ON, token.getExpiresOn());

            assertEquals(1, token.getTenantPermissions().size());
            assertEquals("*", token.getTenantPermissions().get(0).getTenant());
            assertEquals(SetUtils.asSet("*"), token.getTenantPermissions().get(0).getAllowedActions());
        } finally {
            accessTokenService.deleteToken(token.getId());
        }
    }

    @Test
    public void testGetToken() throws Exception {
        AccessToken token = accessTokenService.getToken(ADMIN_CONSOLE_TOKEN_ID);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        Date expiresOn = format.parse("01/01/24");

        assertNotNull(token);
        assertNotNull(token.getId());
        assertEquals(ADMIN_CONSOLE_APPLICATION, token.getApplication());
        assertEquals(true, token.isMaster());
        assertEquals(expiresOn, token.getExpiresOn());

        assertEquals(1, token.getTenantPermissions().size());
        assertEquals("*", token.getTenantPermissions().get(0).getTenant());
        assertEquals(SetUtils.asSet("*"), token.getTenantPermissions().get(0).getAllowedActions());
    }

    @Test
    public void testGetAllTokens() throws Exception {
        List<AccessToken> tokens = accessTokenService.getAllTokens();

        assertNotNull(tokens);
        assertEquals(3, tokens.size());
        assertEquals(ADMIN_CONSOLE_TOKEN_ID, tokens.get(0).getId());
        assertEquals(CRAFTER_SOCIAL_TOKEN_ID, tokens.get(1).getId());
        assertEquals(RANDOM_APP_TOKEN_ID, tokens.get(2).getId());
    }

    @Test
    public void testDeleteToken() throws Exception {
        AccessToken token = accessTokenService.createToken(getCrafterStudioAccessToken());

        assertNotNull(token);

        accessTokenService.deleteToken(token.getId());

        token = accessTokenService.getToken(token.getId());

        assertNull(token);
    }

    private AccessToken getCrafterStudioAccessToken() {
        TenantPermission permission = new TenantPermission();
        permission.allowAny();

        AccessToken token = new AccessToken();
        token.setApplication(CRAFTER_STUDIO_APPLICATION);
        token.setMaster(true);
        token.setTenantPermissions(Arrays.asList(permission));
        token.setExpiresOn(EXPIRES_ON);

        return token;
    }

}
