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

import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.AttributePermission;
import org.craftercms.profile.api.Tenant;
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
    private static final String USER_ROLE =             "USER";
    private static final String DEFAULT_TENANT_NAME =   "default";
    private static final Set<String> DEFAULT_ROLES =    new HashSet<>(Arrays.asList(
            "PROFILE_ADMIN", "SOCIAL_USER", "SOCIAL_MODERATOR", "SOCIAL_AUTHOR", "SOCIAL_ADMIN"));
    private static final String CORPORATE_TENANT_NAME = "corporate";
    private static final Set<String> CORPORATE_ROLES =  new HashSet<>(Arrays.asList(ADMIN_ROLE));

    private static final String FIRST_NAME_ATTRIBUTE_NAME =     "firstName";
    private static final String FIRST_NAME_ATTRIBUTE_LABEL =    "First Name";
    private static final String FIRST_NAME_ATTRIBUTE_TYPE =     "java.lang.String";
    private static final String FIRST_NAME_ATTRIBUTE_OWNER =    "adminconsole";

    private static final String LAST_NAME_ATTRIBUTE_NAME =     "lastName";
    private static final String LAST_NAME_ATTRIBUTE_LABEL =    "Last Name";
    private static final String LAST_NAME_ATTRIBUTE_TYPE =     "java.lang.String";
    private static final String LAST_NAME_ATTRIBUTE_OWNER =    "adminconsole";

    private static final String GENDER_ATTRIBUTE_NAME =     "gender";
    private static final String GENDER_ATTRIBUTE_LABEL =    "Gender";
    private static final String GENDER_ATTRIBUTE_TYPE =     "java.lang.String";
    private static final String GENDER_ATTRIBUTE_OWNER =    "adminconsole";

    @Autowired
    private TenantServiceRestClient tenantService;
    @Autowired
    private SingleAccessTokenIdResolver accessTokenIdResolver;

    @Test
    @DirtiesContext
    public void testMissingAccessTokenIdParamError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(null);

        try {
            tenantService.createTenant(getCorporateTenant());
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
            tenantService.createTenant(getCorporateTenant());
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
            tenantService.createTenant(getCorporateTenant());
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
            tenantService.createTenant(getCorporateTenant());
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.ACTION_DENIED, e.getErrorCode());
        }
    }

    @Test
    public void testCreateTenant() throws Exception {
        Tenant tenant = tenantService.createTenant(getCorporateTenant());

        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertEquals(CORPORATE_TENANT_NAME, tenant.getName());
        assertEquals(false, tenant.isVerifyNewProfiles());
        assertEquals(CORPORATE_ROLES, tenant.getRoles());

        try {
            tenantService.createTenant(getCorporateTenant());
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.TENANT_EXISTS, e.getErrorCode());
        }

        tenantService.deleteTenant(CORPORATE_TENANT_NAME);
    }

    @Test
    public void testGetTenant() throws Exception {
        Tenant tenant = tenantService.getTenant(DEFAULT_TENANT_NAME);

        assertNotNull(tenant);
        assertNotNull(tenant.getId());
        assertEquals(DEFAULT_TENANT_NAME, tenant.getName());
        assertEquals(true, tenant.isVerifyNewProfiles());
        assertEquals(DEFAULT_ROLES, tenant.getRoles());
        assertEqualAttributeDefinitionSets(getAttributeDefinitions(), tenant.getAttributeDefinitions());
    }

    @Test
    public void testDeleteTenant() throws Exception {
        tenantService.createTenant(getCorporateTenant());

        Tenant tenant = tenantService.getTenant(CORPORATE_TENANT_NAME);

        assertNotNull(tenant);

        tenantService.deleteTenant(CORPORATE_TENANT_NAME);

        tenant = tenantService.getTenant(CORPORATE_TENANT_NAME);

        assertNull(tenant);
    }

    @Test
    public void testGetTenantCount() throws Exception {
        long count = tenantService.getTenantCount();

        assertEquals(1, count);
    }

    @Test
    public void testGetAllTenants() throws Exception {
        List<Tenant> tenants = IterableUtils.toList(tenantService.getAllTenants());

        assertNotNull(tenants);
        assertEquals(1, tenants.size());
        assertEquals(DEFAULT_TENANT_NAME, tenants.get(0).getName());
        assertEquals(true, tenants.get(0).isVerifyNewProfiles());
        assertEquals(DEFAULT_ROLES, tenants.get(0).getRoles());
        assertEqualAttributeDefinitionSets(getAttributeDefinitions(), tenants.get(0).getAttributeDefinitions());
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
            assertEquals(expectedRoles, tenant.getRoles());
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
            assertEquals(expectedRoles, tenant.getRoles());
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

            Set<AttributeDefinition> expected = getAttributeDefinitions();
            expected.addAll(definitions);

            assertNotNull(tenant);
            assertNotNull(tenant.getAttributeDefinitions());
            assertEquals(3, tenant.getAttributeDefinitions().size());
            assertEquals(expected, tenant.getAttributeDefinitions());

            try {
                tenantService.addAttributeDefinitions(CORPORATE_TENANT_NAME, definitions);
                fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
            } catch (ProfileRestServiceException e) {
                assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
                assertEquals(ErrorCode.ATTRIBUTE_ALREADY_DEFINED, e.getErrorCode());
            }
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

            accessTokenIdResolver.setAccessTokenId(CRAFTER_SOCIAL_ACCESS_TOKEN_ID);

            try {
                tenantService.removeAttributeDefinitions(CORPORATE_TENANT_NAME, attributeNames);
                fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
            } catch (ProfileRestServiceException e) {
                assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
                assertEquals(ErrorCode.ACTION_DENIED, e.getErrorCode());
            }

            accessTokenIdResolver.setAccessTokenId(ADMIN_CONSOLE_ACCESS_TOKEN_ID);

            Tenant tenant = tenantService.removeAttributeDefinitions(CORPORATE_TENANT_NAME, attributeNames);

            assertNotNull(tenant);
            assertNotNull(tenant.getAttributeDefinitions());
            assertEquals(0, tenant.getAttributeDefinitions().size());

            try {
                tenantService.removeAttributeDefinitions(DEFAULT_TENANT_NAME, attributeNames);
                fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
            } catch (ProfileRestServiceException e) {
                assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
                assertEquals(ErrorCode.ATTRIBUTE_DEFINITION_STILL_USED, e.getErrorCode());
            }
        } finally {
            tenantService.deleteTenant(CORPORATE_TENANT_NAME);
        }
    }

    private Tenant getCorporateTenant() {
        Tenant tenant = new Tenant();
        tenant.setName(CORPORATE_TENANT_NAME);
        tenant.setRoles(CORPORATE_ROLES);
        tenant.setVerifyNewProfiles(false);
        tenant.setAttributeDefinitions(getAttributeDefinitions());

        return tenant;
    }

    private Set<AttributeDefinition> getAttributeDefinitions() {
        return new HashSet<>(Arrays.asList(getFirstNameAttributeDefinition(), getLastNameAttributeDefinition()));
    }

    private AttributeDefinition getFirstNameAttributeDefinition() {
        AttributePermission permission = new AttributePermission(AttributePermission.ANY_APPLICATION);
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(FIRST_NAME_ATTRIBUTE_NAME);
        definition.setLabel(FIRST_NAME_ATTRIBUTE_LABEL);
        definition.setOrder(0);
        definition.setType(FIRST_NAME_ATTRIBUTE_TYPE);
        definition.setConstraint("");
        definition.setRequired(false);
        definition.setOwner(FIRST_NAME_ATTRIBUTE_OWNER);
        definition.addPermission(permission);

        return definition;
    }

    private AttributeDefinition getLastNameAttributeDefinition() {
        AttributePermission permission = new AttributePermission(AttributePermission.ANY_APPLICATION);
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(LAST_NAME_ATTRIBUTE_NAME);
        definition.setLabel(LAST_NAME_ATTRIBUTE_LABEL);
        definition.setOrder(0);
        definition.setType(LAST_NAME_ATTRIBUTE_TYPE);
        definition.setConstraint("");
        definition.setRequired(false);
        definition.setOwner(LAST_NAME_ATTRIBUTE_OWNER);
        definition.addPermission(permission);

        return definition;
    }

    private AttributeDefinition getGenderAttributeDefinition() {
        AttributePermission permission = new AttributePermission(AttributePermission.ANY_APPLICATION);
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition definition = new AttributeDefinition();
        definition.setName(GENDER_ATTRIBUTE_NAME);
        definition.setLabel(GENDER_ATTRIBUTE_LABEL);
        definition.setOrder(0);
        definition.setType(GENDER_ATTRIBUTE_TYPE);
        definition.setConstraint("");
        definition.setRequired(false);
        definition.setOwner(GENDER_ATTRIBUTE_OWNER);
        definition.addPermission(permission);

        return definition;
    }

    private void assertEqualAttributeDefinitionSets(Set<AttributeDefinition> expected,  Set<AttributeDefinition> actual) {
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
        assertEquals(expected.getLabel(), actual.getLabel());
        assertEquals(expected.getOrder(), actual.getOrder());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getConstraint(), actual.getConstraint());
        assertEquals(expected.isRequired(), actual.isRequired());
        assertEquals(expected.getOwner(), actual.getOwner());
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
