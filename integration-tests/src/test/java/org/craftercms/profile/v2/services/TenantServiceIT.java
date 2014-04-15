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

import org.apache.commons.collections4.IteratorUtils;
import org.craftercms.profile.api.*;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.v2.exceptions.ProfileRestServiceException;
import org.craftercms.profile.v2.services.impl.SingleAccessTokenIdResolver;
import org.craftercms.profile.v2.services.impl.TenantServiceRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Integration tests for the {@link org.craftercms.profile.services.ProfileService}.
 *
 * @author avasquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:crafter/profile/client-context.xml")
public class TenantServiceIT {

    private static final String INVALID_ACCESS_TOKEN_ID =           "ab785de0-c327-11e3-9c1a-0800200c9a66";
    private static final String EXPIRED_ACCESS_TOKEN_ID =           "9161fb80-c329-11e3-9c1a-0800200c9a66";
    private static final String UNALLOWED_ACCESS_TOKEN_ID =         "f9929b40-c358-11e3-9c1a-0800200c9a66";
    private static final String ADMIN_CONSOLE_ACCESS_TOKEN_ID =     "e8f5170c-877b-416f-b70f-4b09772f8e2d";
    private static final String CRAFTER_SOCIAL_ACCESS_TOKEN_ID =    "2ba3ac10-c43e-11e3-9c1a-0800200c9a66";

    private static final String ADMIN_ROLE =            "ADMIN";
    private static final String AUTHOR_ROLE =           "AUTHOR";
    private static final String VIEWER_ROLE =           "VIEWER";
    private static final String DEFAULT_TENANT =        "default";
    private static final Set<String> DEFAULT_ROLES =    new HashSet<>(Arrays.asList(ADMIN_ROLE));
    private static final String CORPORATE_TENANT =      "corporate";
    private static final Set<String> CORPORATE_ROLES =  new HashSet<>(Arrays.asList(ADMIN_ROLE, AUTHOR_ROLE));

    private static final String ATTRIBUTE_NAME =        "testAttribute";
    private static final String ATTRIBUTE_LABEL =       "Test Attribute";
    private static final String ATTRIBUTE_TYPE =        "java.lang.String";
    private static final String ATTRIBUTE_OWNER =       "adminconsole";

    @Autowired
    private TenantServiceRestClient tenantService;
    @Autowired
    private SingleAccessTokenIdResolver accessTokenIdResolver;

    @Test
    @DirtiesContext
    public void testMissingAccessTokenIdParamError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(null);

        try {
            tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals(ErrorCode.MISSING_ACCESS_TOKEN_ID_PARAM, e.getErrorCode());
        }
    }

    @Test
    @DirtiesContext
    public void testNoSuchAccessTokenIdError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(INVALID_ACCESS_TOKEN_ID);

        try {
            tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.NO_SUCH_ACCESS_TOKEN_ID, e.getErrorCode());
        }
    }

    @Test
    @DirtiesContext
    public void testExpiredAccessTokenError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(EXPIRED_ACCESS_TOKEN_ID);

        try {
            tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.EXPIRED_ACCESS_TOKEN, e.getErrorCode());
        }
    }

    @Test
    @DirtiesContext
    public void testUnallowedAccessTokenError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(UNALLOWED_ACCESS_TOKEN_ID);

        try {
            tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.ACTION_DENIED, e.getErrorCode());
        }
    }

    @Test
    public void testCreateTenant() throws Exception {
        Tenant tenant = tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);

        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertEquals(CORPORATE_TENANT, tenant.getName());
        assertEquals(true, tenant.isVerifyNewProfiles());
        assertEquals(CORPORATE_ROLES, tenant.getRoles());

        try {
            tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.TENANT_EXISTS, e.getErrorCode());
        }

        tenantService.deleteTenant(CORPORATE_TENANT);
    }

    @Test
    public void testGetTenant() throws Exception {
        Tenant tenant = tenantService.getTenant(DEFAULT_TENANT);

        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertEquals(DEFAULT_TENANT, tenant.getName());
        assertEquals(false, tenant.isVerifyNewProfiles());
        assertEquals(DEFAULT_ROLES, tenant.getRoles());
    }

    @Test
    public void testDeleteTenant() throws Exception {
        tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);

        Tenant tenant = tenantService.getTenant(CORPORATE_TENANT);

        assertNotNull(tenant);

        tenantService.deleteTenant(CORPORATE_TENANT);

        tenant = tenantService.getTenant(CORPORATE_TENANT);

        assertNull(tenant);
    }

    @Test
    public void testGetTenantCount() throws Exception {
        long count = tenantService.getTenantCount();

        assertEquals(1, count);
    }

    @Test
    public void testGetAllTenants() throws Exception {
        List<Tenant> tenants = IteratorUtils.toList(tenantService.getAllTenants().iterator());

        assertNotNull(tenants);
        assertEquals(1, tenants.size());
        assertEquals(DEFAULT_TENANT, tenants.get(0).getName());
        assertEquals(false, tenants.get(0).isVerifyNewProfiles());
        assertEquals(DEFAULT_ROLES, tenants.get(0).getRoles());
    }

    @Test
    public void testVerifyNewProfiles() throws Exception {
        tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);

        Tenant tenant = tenantService.verifyNewProfiles(CORPORATE_TENANT, false);

        assertNotNull(tenant);
        assertEquals(false, tenant.isVerifyNewProfiles());

        tenantService.deleteTenant(CORPORATE_TENANT);
    }

    @Test
    public void testAddRoles() throws Exception {
        tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);

        Tenant tenant = tenantService.addRoles(CORPORATE_TENANT, Arrays.asList(VIEWER_ROLE));

        Set<String> expectedRoles = new HashSet<>(CORPORATE_ROLES);
        expectedRoles.add(VIEWER_ROLE);

        assertNotNull(tenant);
        assertEquals(expectedRoles, tenant.getRoles());

        tenantService.deleteTenant(CORPORATE_TENANT);
    }

    @Test
    public void testRemoveRoles() throws Exception {
        tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);

        Tenant tenant = tenantService.removeRoles(CORPORATE_TENANT, Arrays.asList(AUTHOR_ROLE));

        Set<String> expectedRoles = new HashSet<>(CORPORATE_ROLES);
        expectedRoles.remove(AUTHOR_ROLE);

        assertNotNull(tenant);
        assertEquals(expectedRoles, tenant.getRoles());

        tenantService.deleteTenant(CORPORATE_TENANT);
    }

    @Test
    public void testAddAttributeDefinitions() throws Exception {
        tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);

        List<AttributeDefinition> definitions = Arrays.asList(getDefaultAttributeDefinition());
        Tenant tenant = tenantService.addAttributeDefinitions(CORPORATE_TENANT, definitions);

        AttributeDefinition expected = getDefaultAttributeDefinition();

        assertNotNull(tenant);
        assertNotNull(tenant.getAttributeDefinitions());
        assertEquals(1, tenant.getAttributeDefinitions().size());

        AttributeDefinition actual = tenant.getAttributeDefinitions().iterator().next();

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getLabel(), actual.getLabel());
        assertEquals(expected.getOrder(), actual.getOrder());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getConstraint(), actual.getConstraint());
        assertEquals(expected.isRequired(), actual.isRequired());
        assertEquals(expected.getOwner(), actual.getOwner());
        assertNotNull(actual.getPermissions());
        assertEquals(1, actual.getPermissions().size());
        assertEquals(expected.getPermissions().get(0).getApplication(), actual.getPermissions().get(0)
                .getApplication());
        assertEquals(expected.getPermissions().get(0).getAllowedActions(), actual.getPermissions().get(0)
                .getAllowedActions());

        try {
            tenantService.addAttributeDefinitions(CORPORATE_TENANT, definitions);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.ATTRIBUTE_ALREADY_DEFINED, e.getErrorCode());
        }

        tenantService.deleteTenant(CORPORATE_TENANT);
    }

    @Test
    @DirtiesContext
    public void testRemoveAttributeDefinitions() throws Exception {
        tenantService.createTenant(CORPORATE_TENANT, true, CORPORATE_ROLES);

        List<AttributeDefinition> definitions = Arrays.asList(getDefaultAttributeDefinition());
        Tenant tenant = tenantService.addAttributeDefinitions(CORPORATE_TENANT, definitions);

        assertNotNull(tenant);
        assertNotNull(tenant.getAttributeDefinitions());
        assertEquals(1, tenant.getAttributeDefinitions().size());

        accessTokenIdResolver.setAccessTokenId(CRAFTER_SOCIAL_ACCESS_TOKEN_ID);

        try {
            tenantService.removeAttributeDefinitions(CORPORATE_TENANT, Arrays.asList(ATTRIBUTE_NAME));
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.ACTION_DENIED, e.getErrorCode());
        }

        accessTokenIdResolver.setAccessTokenId(ADMIN_CONSOLE_ACCESS_TOKEN_ID);

        tenant = tenantService.removeAttributeDefinitions(CORPORATE_TENANT, Arrays.asList(ATTRIBUTE_NAME));

        assertNotNull(tenant);
        assertNotNull(tenant.getAttributeDefinitions());
        assertEquals(0, tenant.getAttributeDefinitions().size());

        tenantService.deleteTenant(CORPORATE_TENANT);
    }

    private AttributeDefinition getDefaultAttributeDefinition() {
        AttributePermission permission = new AttributePermission(ATTRIBUTE_OWNER);
        permission.allow(AttributeActions.ALL_ACTIONS);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(ATTRIBUTE_NAME);
        definition.setLabel(ATTRIBUTE_LABEL);
        definition.setOrder(0);
        definition.setType(ATTRIBUTE_TYPE);
        definition.setConstraint("");
        definition.setRequired(false);
        definition.setOwner(ATTRIBUTE_OWNER);
        definition.addPermission(permission);

        return definition;
    }

}
