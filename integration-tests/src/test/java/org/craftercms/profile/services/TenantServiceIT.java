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
package org.craftercms.profile.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.craftercms.commons.collections.SetUtils;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.AttributePermission;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Integration tests for the {@link org.craftercms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:crafter/profile/extension/client-context.xml")
public class TenantServiceIT {

    private static final String ADMIN_CONSOLE_APPLICATION = "profile-admin";
    private static final String CRAFTER_SOCIAL_APPLICATION = "social";

    private static final String DEFAULT_TENANT_NAME = "default";
    private static final String CORPORATE_TENANT_NAME = "corporate";

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";
    private static final Set<String> DEFAULT_ROLES = SetUtils.asSet("PROFILE_SUPERADMIN", "SOCIAL_USER",
                                                                    "SOCIAL_MODERATOR", "SOCIAL_AUTHOR",
                                                                    "SOCIAL_ADMIN");
    private static final Set<String> CORPORATE_ROLES = SetUtils.asSet(ADMIN_ROLE);

    private static final String FIRST_NAME_ATTRIBUTE_NAME = "firstName";
    private static final String LAST_NAME_ATTRIBUTE_NAME = "lastName";
    private static final String SUBSCRIPTIONS_ATTRIBUTE_NAME = "subscriptions";
    private static final String GENDER_ATTRIBUTE_NAME = "gender";

    @Autowired
    private TenantService tenantService;

    @Test
    public void testCreateTenant() throws Exception {
        Tenant tenant = tenantService.createTenant(getCorporateTenant());
        try {
            assertNotNull(tenant);
            assertNotNull(tenant.getId());
            assertEquals(CORPORATE_TENANT_NAME, tenant.getName());
            assertEquals(false, tenant.isVerifyNewProfiles());
            assertEquals(CORPORATE_ROLES, tenant.getAvailableRoles());

            try {
                tenantService.createTenant(getCorporateTenant());
                fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
            } catch (ProfileRestServiceException e) {
                assertEquals(HttpStatus.CONFLICT, e.getStatus());
                assertEquals(ErrorCode.TENANT_EXISTS, e.getErrorCode());
            }
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    @Test
    public void testGetTenant() throws Exception {
        Tenant tenant = tenantService.getTenant(DEFAULT_TENANT_NAME);

        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertEquals(DEFAULT_TENANT_NAME, tenant.getName());
        assertEquals(false, tenant.isVerifyNewProfiles());
        assertEquals(DEFAULT_ROLES, tenant.getAvailableRoles());
        assertEqualAttributeDefinitions(getAttributeDefinitions(), tenant.getAttributeDefinitions());
    }

    @Test
    public void testUpdateTenant() throws Exception {
        Tenant tenant = tenantService.createTenant(getCorporateTenant());
        try {
            tenant.setVerifyNewProfiles(true);
            tenant.getAvailableRoles().remove(ADMIN_ROLE);
            tenant.getAvailableRoles().add(USER_ROLE);
            tenant.getAttributeDefinitions().remove(getSubscriptionsAttributeDefinition());
            tenant.getAttributeDefinitions().add(getGenderAttributeDefinition());

            Tenant result = tenantService.updateTenant(tenant);

            assertNotNull(result);
            assertEquals(tenant.isVerifyNewProfiles(), result.isVerifyNewProfiles());
            assertEquals(tenant.getAvailableRoles(), result.getAvailableRoles());
            assertEqualAttributeDefinitions(tenant.getAttributeDefinitions(), result.getAttributeDefinitions());
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    @Test
    public void testDeleteTenant() throws Exception {
        Tenant tenant = tenantService.createTenant(getCorporateTenant());

        assertNotNull(tenant);

        tenantService.deleteTenant(tenant.getName());

        tenant = tenantService.getTenant(tenant.getName());

        assertNull(tenant);
    }

    @Test
    public void testGetTenantCount() throws Exception {
        long count = tenantService.getTenantCount();

        assertEquals(1, count);
    }

    @Test
    public void testGetAllTenants() throws Exception {
        List<Tenant> tenants = tenantService.getAllTenants();

        assertNotNull(tenants);
        assertEquals(1, tenants.size());
        assertEquals(DEFAULT_TENANT_NAME, tenants.get(0).getName());
        assertEquals(false, tenants.get(0).isVerifyNewProfiles());
        assertEquals(DEFAULT_ROLES, tenants.get(0).getAvailableRoles());
        assertEqualAttributeDefinitions(getAttributeDefinitions(), tenants.get(0).getAttributeDefinitions());
    }

    @Test
    public void testVerifyNewProfiles() throws Exception {
        tenantService.createTenant(getCorporateTenant());
        try {
            Tenant tenant = tenantService.verifyNewProfiles(CORPORATE_TENANT_NAME, false);

            assertNotNull(tenant);
            assertEquals(false, tenant.isVerifyNewProfiles());
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    @Test
    public void testAddRoles() throws Exception {
        tenantService.createTenant(getCorporateTenant());
        try {
            Tenant tenant = tenantService.addRoles(CORPORATE_TENANT_NAME, Arrays.asList(USER_ROLE));

            Set<String> expectedRoles = new HashSet<>(CORPORATE_ROLES);
            expectedRoles.add(USER_ROLE);

            assertNotNull(tenant);
            assertEquals(expectedRoles, tenant.getAvailableRoles());
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    @Test
    public void testRemoveRoles() throws Exception {
        tenantService.createTenant(getCorporateTenant());
        try {
            Tenant tenant = tenantService.removeRoles(CORPORATE_TENANT_NAME, Arrays.asList(ADMIN_ROLE));

            Set<String> expectedRoles = new HashSet<>(CORPORATE_ROLES);
            expectedRoles.remove(ADMIN_ROLE);

            assertNotNull(tenant);
            assertEquals(expectedRoles, tenant.getAvailableRoles());
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    @Test
    public void testAddAttributeDefinitions() throws Exception {
        tenantService.createTenant(getCorporateTenant());
        try {
            List<AttributeDefinition> definitions = Arrays.asList(getGenderAttributeDefinition());
            Tenant tenant = tenantService.addAttributeDefinitions(CORPORATE_TENANT_NAME, definitions);

            List<AttributeDefinition> expected = getAttributeDefinitions();
            expected.addAll(definitions);

            assertNotNull(tenant);
            assertNotNull(tenant.getAttributeDefinitions());
            assertEquals(4, tenant.getAttributeDefinitions().size());
            assertEquals(expected, tenant.getAttributeDefinitions());
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    @Test
    @DirtiesContext
    public void testRemoveAttributeDefinitions() throws Exception {
        tenantService.createTenant(getCorporateTenant());
        try {
            Collection<String> attributeNames = Arrays.asList(FIRST_NAME_ATTRIBUTE_NAME, LAST_NAME_ATTRIBUTE_NAME);
            Tenant tenant = tenantService.removeAttributeDefinitions(CORPORATE_TENANT_NAME, attributeNames);

            assertNotNull(tenant);
            assertNotNull(tenant.getAttributeDefinitions());
            assertEquals(1, tenant.getAttributeDefinitions().size());
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    private Tenant getCorporateTenant() {
        Tenant tenant = new Tenant();
        tenant.setName(CORPORATE_TENANT_NAME);
        tenant.setAvailableRoles(CORPORATE_ROLES);
        tenant.setVerifyNewProfiles(false);
        tenant.setAttributeDefinitions(getAttributeDefinitions());

        return tenant;
    }

    private List<AttributeDefinition> getAttributeDefinitions() {
        return new ArrayList<>(Arrays.asList(getFirstNameAttributeDefinition(), getLastNameAttributeDefinition(),
                                             getSubscriptionsAttributeDefinition()));
    }

    private AttributeDefinition getFirstNameAttributeDefinition() {
        AttributePermission permission = new AttributePermission(AttributePermission.ANY_APPLICATION);
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(FIRST_NAME_ATTRIBUTE_NAME);
        definition.addPermission(permission);

        return definition;
    }

    private AttributeDefinition getLastNameAttributeDefinition() {
        AttributePermission permission = new AttributePermission(AttributePermission.ANY_APPLICATION);
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(LAST_NAME_ATTRIBUTE_NAME);
        definition.addPermission(permission);

        return definition;
    }

    private AttributeDefinition getSubscriptionsAttributeDefinition() {
        AttributePermission permission1 = new AttributePermission(ADMIN_CONSOLE_APPLICATION);
        permission1.allow(AttributePermission.ANY_ACTION);

        AttributePermission permission2 = new AttributePermission(CRAFTER_SOCIAL_APPLICATION);
        permission2.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(SUBSCRIPTIONS_ATTRIBUTE_NAME);
        definition.addPermission(permission1);
        definition.addPermission(permission2);

        return definition;
    }

    private AttributeDefinition getGenderAttributeDefinition() {
        AttributePermission permission = new AttributePermission(AttributePermission.ANY_APPLICATION);
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(GENDER_ATTRIBUTE_NAME);
        definition.addPermission(permission);

        return definition;
    }

    private void assertEqualAttributeDefinitions(List<AttributeDefinition> expected,
                                                 List<AttributeDefinition> actual) {
        assertNotNull(expected);
        assertEquals(expected.size(), actual.size());

        Iterator<AttributeDefinition> expectedIter = expected.iterator();
        Iterator<AttributeDefinition> actualIter = actual.iterator();

        while (expectedIter.hasNext()) {
            AttributeDefinition expectedDefinition = expectedIter.next();
            AttributeDefinition actualDefinition = actualIter.next();

            assertEqualAttributeDefinitions(expectedDefinition, actualDefinition);
        }
    }

    private void assertEqualAttributeDefinitions(AttributeDefinition expected, AttributeDefinition actual) {
        List<AttributePermission> expectedPermissions = expected.getPermissions();
        List<AttributePermission> actualPermissions = actual.getPermissions();

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getMetadata(), actual.getMetadata());

        assertNotNull(actualPermissions);
        assertEquals(expectedPermissions.size(), actualPermissions.size());

        for (int i = 0; i < expectedPermissions.size(); i++) {
            AttributePermission expectedPermission = expectedPermissions.get(i);
            AttributePermission actualPermission = actualPermissions.get(i);

            assertEquals(expectedPermission.getApplication(), actualPermission.getApplication());
            assertEquals(expectedPermission.getAllowedActions(), actualPermission.getAllowedActions());
        }
    }

}
