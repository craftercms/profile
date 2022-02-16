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
package org.craftercms.profile.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.commons.collections.SetUtils;
import org.craftercms.commons.entitlements.validator.EntitlementValidator;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.AttributePermission;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.profile.services.impl.TenantServiceImpl}.
 *
 * @author avasquez
 */
public class TenantServiceImplTest {

    private static final String LABEL_KEY = "label";

    private static final ObjectId TENANT1_ID = new ObjectId();
    private static final ObjectId TENANT2_ID = new ObjectId();
    private static final String TENANT1_NAME = "tenant1";
    private static final String TENANT2_NAME = "tenant2";
    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";

    private static final String ATTRIB1_NAME = "attrib1";
    private static final String ATTRIB1_LABEL = "Attribute #1";
    private static final String ATTRIB2_NAME = "attrib2";
    private static final String ATTRIB2_LABEL = "Attribute #2";
    private static final Object DEFAULT_ATTRIB_VALUE = "test";
    private static final String APP_NAME = "app";

    private TenantServiceImpl tenantService;
    @Mock
    private PermissionEvaluator<AccessToken, String> permissionEvaluator;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private EntitlementValidator entitlementValidator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(permissionEvaluator.isAllowed(anyString(), anyString()))
            .thenReturn(true);

        when(tenantRepository.findByName(TENANT1_NAME))
            .thenReturn(getTenant1());
        when(tenantRepository.findByName(TENANT2_NAME))
            .thenReturn(getTenant2());
        when(tenantRepository.findAll())
            .thenReturn(Arrays.asList(getTenant1(), getTenant2()));
        when(tenantRepository.count())
            .thenReturn(2L);

        when(profileService.getProfilesByRole(TENANT2_NAME, ROLE1, null, null, ProfileConstants.NO_ATTRIBUTE))
                .thenReturn(Arrays.asList(mock(Profile.class)));
        when(profileService.getProfilesByExistingAttribute(TENANT2_NAME, ATTRIB1_NAME, null, null,
                                                           ProfileConstants.NO_ATTRIBUTE))
            .thenReturn(Arrays.asList(mock(Profile.class)));

        tenantService = new TenantServiceImpl();
        tenantService.setTenantPermissionEvaluator(permissionEvaluator);
        tenantService.setTenantRepository(tenantRepository);
        tenantService.setProfileService(profileService);
        tenantService.setProfileRepository(profileRepository);
        tenantService.setEntitlementValidator(entitlementValidator);
    }

    @Test
    public void testCreateTenant() throws Exception {
        Tenant actual = tenantService.createTenant(getTenant1());
        Tenant expected = getTenant1();

        assertEqualTenants(expected, actual);

        verify(tenantRepository).insert(actual);
    }

    @Test
    public void testGetTenant() throws Exception {
        Tenant actual = tenantService.getTenant(TENANT1_NAME);
        Tenant expected = getTenant1();

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
    }

    @Test
    public void testUpdateTenant() throws Exception {
        AttributeDefinition def1 = new AttributeDefinition();
        def1.setName(ATTRIB1_NAME);

        AttributeDefinition def2 = new AttributeDefinition();
        def2.setName(ATTRIB2_NAME);
        def2.setDefaultValue(DEFAULT_ATTRIB_VALUE);

        Tenant expected = getTenant1();
        expected.getAvailableRoles().remove(ROLE1);
        expected.getAttributeDefinitions().remove(def1);
        expected.getAttributeDefinitions().add(def2);

        Map<String, Object> expectedSetParams = new HashMap<>();
        expectedSetParams.put("verifyNewProfiles", expected.isVerifyNewProfiles());
        expectedSetParams.put("availableRoles", expected.getAvailableRoles());
        expectedSetParams.put("ssoEnabled", expected.isSsoEnabled());
        expectedSetParams.put("attributeDefinitions", expected.getAttributeDefinitions());
        expectedSetParams.put("cleanseAttributes", expected.isCleanseAttributes());

        Tenant actual = tenantService.updateTenant(expected);

        assertEqualTenants(expected, actual);

        verify(profileRepository).removeRoleFromAll(TENANT1_NAME, ROLE1);
        verify(profileRepository).removeAttributeFromAll(TENANT1_NAME, ATTRIB1_NAME);
        verify(profileRepository).updateAllWithDefaultValue(TENANT1_NAME, ATTRIB2_NAME, DEFAULT_ATTRIB_VALUE);
        verify(tenantRepository).update(TENANT1_ID.toString(), "{$set: #}", false, false, expectedSetParams);
    }

    @Test
    public void testDeleteTenant() throws Exception {
        tenantService.deleteTenant(TENANT1_NAME);

        verify(profileRepository).removeAll(TENANT1_NAME);
        verify(tenantRepository).removeByName(TENANT1_NAME);
    }

    @Test
    public void testGetTenantCount() throws Exception {
        long expected = 2L;
        long actual = tenantService.getTenantCount();

        assertEquals(expected, actual);

        verify(tenantRepository).count();
    }

    @Test
    public void testGetAllTenants() throws Exception {
        List<Tenant> expected = Arrays.asList(getTenant1(), getTenant2());
        List<Tenant> actual = tenantService.getAllTenants();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEqualTenants(expected.get(0), actual.get(0));
        assertEqualTenants(expected.get(1), actual.get(1));

        verify(tenantRepository).findAll();
    }

    @Test
    public void testVerifyNewProfiles() throws Exception {
        Tenant expected = getTenant1();
        expected.setVerifyNewProfiles(false);

        Map<String, Object> expectedSetParams = new HashMap<>();
        expectedSetParams.put("verifyNewProfiles", expected.isVerifyNewProfiles());

        Tenant actual = tenantService.verifyNewProfiles(TENANT1_NAME, false);

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).update(TENANT1_ID.toString(), "{$set: #}", false, false, expectedSetParams);
    }

    @Test
    public void testAddAvailableRoles() throws Exception {
        Tenant expected = getTenant1();
        expected.getAvailableRoles().add(ROLE2);

        List<String> rolesToAdd = Collections.singletonList(ROLE2);

        Map<String, Object> expectedPushParams = new HashMap<>();
        expectedPushParams.put("availableRoles", Collections.singletonMap("$each", rolesToAdd));

        Tenant actual = tenantService.addRoles(TENANT1_NAME, rolesToAdd);

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).update(TENANT1_ID.toString(), "{$push: #}", false, false, expectedPushParams);
    }

    @Test
    public void testRemoveAvailableRoles() throws Exception {
        Tenant expected = getTenant1();
        expected.getAvailableRoles().remove(ROLE1);

        List<String> rolesToRemove = Collections.singletonList(ROLE1);

        Map<String, Object> expectedPullParams = new HashMap<>();
        expectedPullParams.put("availableRoles", Collections.singletonMap("$in", rolesToRemove));

        Tenant actual = tenantService.removeRoles(TENANT1_NAME, rolesToRemove);

        assertEqualTenants(expected, actual);

        verify(profileRepository).removeRoleFromAll(TENANT1_NAME, ROLE1);
        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).update(TENANT1_ID.toString(), "{$pull: #}", false, false, expectedPullParams);
    }

    @Test
    public void testAddAttributeDefinitions() throws Exception {
        AttributeDefinition def = getAttribute2Definition();
        List<AttributeDefinition> defsToAdd = Collections.singletonList(def);

        Tenant expected = getTenant1();
        expected.getAttributeDefinitions().add(def);

        Map<String, Object> expectedPushParams = new HashMap<>();
        expectedPushParams.put("attributeDefinitions", Collections.singletonMap("$each", defsToAdd));

        Tenant actual = tenantService.addAttributeDefinitions(TENANT1_NAME, defsToAdd);

        assertEqualTenants(expected, actual);

        verify(profileRepository).updateAllWithDefaultValue(TENANT1_NAME, ATTRIB2_NAME, DEFAULT_ATTRIB_VALUE);
        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).update(TENANT1_ID.toString(), "{$push: #}", false, false, expectedPushParams);
    }

    @Test
    public void testUpdateAttributeDefinitions() throws Exception {
        AttributeDefinition def = getAttribute2Definition();
        def.setName(ATTRIB1_NAME);

        Tenant expected = getTenant1();
        expected.getAttributeDefinitions().clear();
        expected.getAttributeDefinitions().add(def);

        Map<String, Object> expectedSetParams = new HashMap<>();
        expectedSetParams.put("attributeDefinitions.0", def);

        Tenant actual = tenantService.updateAttributeDefinitions(TENANT1_NAME, Collections.singletonList(def));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).update(TENANT1_ID.toString(), "{$set: #}", false, false, expectedSetParams);
    }

    @Test
    public void testRemoveAttributeDefinitions() throws Exception {
        Tenant expected = getTenant1();
        expected.getAttributeDefinitions().clear();

        Map<String, Object> attrNameCondition = Collections.singletonMap("$in", Collections.singletonList(ATTRIB1_NAME));

        Map<String, Object> expectedPullParams = new HashMap<>();
        expectedPullParams.put("attributeDefinitions",
                               Collections.singletonMap("name", attrNameCondition));

        Tenant actual = tenantService.removeAttributeDefinitions(TENANT1_NAME, Collections.singletonList(ATTRIB1_NAME));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).update(TENANT1_ID.toString(), "{$pull: #}", false, false, expectedPullParams);
    }

    private Tenant getTenant1() {
        Tenant tenant = new Tenant();
        tenant.setId(TENANT1_ID);
        tenant.setName(TENANT1_NAME);
        tenant.setVerifyNewProfiles(true);
        tenant.setAvailableRoles(SetUtils.asSet(ROLE1));
        tenant.setAttributeDefinitions(new ArrayList<>(Collections.singletonList(getAttribute1Definition())));
        tenant.setCleanseAttributes(false);

        return tenant;
    }

    private Tenant getTenant2() {
        Tenant tenant = new Tenant();
        tenant.setId(TENANT2_ID);
        tenant.setName(TENANT2_NAME);
        tenant.setVerifyNewProfiles(true);
        tenant.setAvailableRoles(SetUtils.asSet(ROLE1, ROLE2));
        tenant.setAttributeDefinitions(new ArrayList<>(Collections.singletonList(getAttribute1Definition())));
        tenant.setCleanseAttributes(false);

        return tenant;
    }

    private AttributeDefinition getAttribute1Definition() {
        AttributePermission permission = new AttributePermission();
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB1_NAME);
        def.setMetadata(Collections.<String, Object>singletonMap(LABEL_KEY, ATTRIB1_LABEL));
        def.addPermission(permission);

        return def;
    }

    private AttributeDefinition getAttribute2Definition() {
        AttributePermission permission = new AttributePermission(APP_NAME);
        permission.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB2_NAME);
        def.setMetadata(Collections.<String, Object>singletonMap(LABEL_KEY, ATTRIB2_LABEL));
        def.addPermission(permission);
        def.setDefaultValue(DEFAULT_ATTRIB_VALUE);

        return def;
    }

    private void assertEqualTenants(Tenant expected, Tenant actual) {
        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.isVerifyNewProfiles(), actual.isVerifyNewProfiles());
        assertEquals(expected.getAvailableRoles(), actual.getAvailableRoles());
        assertEqualAttributeDefinitions(expected.getAttributeDefinitions(), actual.getAttributeDefinitions());
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
