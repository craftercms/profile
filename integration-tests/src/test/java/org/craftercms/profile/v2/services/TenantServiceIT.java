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
package org.craftercms.profile.v2.services;

import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.v2.exceptions.ProfileRestServiceException;
import org.craftercms.profile.v2.services.impl.TenantServiceRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Integration tests for the {@link org.craftercms.profile.services.ProfileService}.
 *
 * @author avasquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:crafter/profile/client-context.xml")
public class TenantServiceIT {

    private static final String TENANT_NAME =   "corporate";
    private static final String USER_ROLE =     "USER";
    private static final String ADMIN_ROLE =    "ADMIN";
    private static final Set<String> ROLES =    new HashSet<>(Arrays.asList(USER_ROLE, ADMIN_ROLE));

    @Autowired
    private TenantServiceRestClient tenantService;

    @Test
    @DirtiesContext
    public void testAccessTokenNotSpecifiedError() throws Exception {
        tenantService.setAccessTokenId(null);

        try {
            testCreateTenant();
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(e.getStatus(), HttpStatus.UNAUTHORIZED);
            assertEquals(e.getErrorCode(), ErrorCode.MISSING_ACCESS_TOKEN_ID_PARAM);
        }
    }

    @Test
    public void testCreateTenant() throws Exception {
        Tenant tenant = tenantService.createTenant(TENANT_NAME, true, ROLES);

        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertEquals(TENANT_NAME, tenant.getName());
        assertEquals(true, tenant.isVerifyNewProfiles());
        assertEquals(ROLES, tenant.getRoles());
    }

}
