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
package org.craftercms.profile.services.impl;

import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.*;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.AttributeAlreadyDefinedException;
import org.craftercms.profile.exceptions.AttributeDefinitionStillUsedException;
import org.craftercms.profile.permissions.Application;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link org.craftercms.profile.services.impl.TenantServiceImpl}.
 *
 * @author avasquez
 */
public class TenantServiceImplTest {

    private static final String TENANT1_NAME =  "tenant1";
    private static final String TENANT2_NAME =  "tenant2";
    private static final String ROLE1 =         "role1";
    private static final String ROLE2 =         "role2";
    private static final Set<String> ROLES =    new HashSet<>(Arrays.asList(ROLE1));

    private static final String ATTRIB1_NAME =  "attrib1";
    private static final String ATTRIB2_NAME =  "attrib2";
    private static final String APP_NAME =      "app";

    private TenantServiceImpl tenantService;
    @Mock
    private PermissionEvaluator<Application, String> permissionEvaluator;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ProfileService profileService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(permissionEvaluator.isAllowed(anyString(), anyString())).thenReturn(true);

        when(tenantRepository.findByName(TENANT1_NAME)).thenReturn(getTenant1());
        when(tenantRepository.findByName(TENANT2_NAME)).thenReturn(getTenant2());
        when(tenantRepository.findAll()).thenReturn(Arrays.asList(getTenant1(), getTenant2()));
        when(tenantRepository.count()).thenReturn(2L);

        when(profileService.getProfilesByExistingAttribute(TENANT2_NAME, ATTRIB1_NAME, null, null, ProfileConstants
                .NO_ATTRIBUTE)).thenReturn(Arrays.asList(mock(Profile.class)));

        tenantService = new TenantServiceImpl();
        tenantService.setTenantPermissionEvaluator(permissionEvaluator);
        tenantService.setTenantRepository(tenantRepository);
        tenantService.setProfileRepository(profileRepository);
        tenantService.setProfileService(profileService);
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
    public void testDeleteTenant() throws Exception {
        tenantService.deleteTenant(TENANT1_NAME);

        verify(profileRepository).removeAllForTenant(TENANT1_NAME);
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

        Tenant actual = tenantService.verifyNewProfiles(TENANT1_NAME, false);

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).save(actual);
    }

    @Test
    public void testAddRoles() throws Exception {
        Tenant expected = getTenant1();
        expected.getRoles().add(ROLE2);

        Tenant actual = tenantService.addRoles(TENANT1_NAME, Arrays.asList(ROLE2));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).save(actual);
    }

    @Test
    public void testRemoveRoles() throws Exception {
        Tenant expected = getTenant1();
        expected.getRoles().remove("role1");

        Tenant actual = tenantService.removeRoles(TENANT1_NAME, Arrays.asList("role1"));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).save(actual);
    }

    @Test
    public void testAddAttributeDefinitions() throws Exception {
        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB2_NAME);

        Tenant expected = getTenant1();
        expected.getAttributeDefinitions().add(def);

        Tenant actual = tenantService.addAttributeDefinitions(TENANT1_NAME, Arrays.asList(def));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).save(actual);


    }

    @Test
    public void testAddRepeatedAttributeDefinition() throws Exception {
        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB1_NAME);

        try {
            tenantService.addAttributeDefinitions(TENANT1_NAME, Arrays.asList(def));
            fail("Expected " + AttributeAlreadyDefinedException.class.getSimpleName() + " exception");
        } catch (AttributeAlreadyDefinedException e) {
        }
    }

    @Test
    public void testRemoveAttributeDefinitions() throws Exception {
        Application.setCurrent(new Application(APP_NAME, Collections.<TenantPermission>emptyList()));

        Tenant expected = getTenant1();
        expected.getAttributeDefinitions().clear();

        Tenant actual = tenantService.removeAttributeDefinitions(TENANT1_NAME, Arrays.asList(ATTRIB1_NAME));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT1_NAME);
        verify(tenantRepository).save(actual);

        Application.clear();
    }

    @Test
    public void testRemoveAttributeDefinitionStillUsed() throws Exception {
        Application.setCurrent(new Application(APP_NAME, Collections.<TenantPermission>emptyList()));

        try {
            tenantService.removeAttributeDefinitions(TENANT2_NAME, Arrays.asList(ATTRIB1_NAME));
            fail("Expected " + AttributeDefinitionStillUsedException.class.getSimpleName() + " exception");
        } catch (AttributeDefinitionStillUsedException e) {
        }
    }

    private Tenant getTenant1() {
        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB1_NAME);

        Tenant tenant = new Tenant();
        tenant.setName(TENANT1_NAME);
        tenant.setVerifyNewProfiles(true);
        tenant.setRoles(ROLES);
        tenant.setAttributeDefinitions(new HashSet<>(Arrays.asList(def)));

        return tenant;
    }

    private Tenant getTenant2() {
        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB1_NAME);

        Tenant tenant = new Tenant();
        tenant.setName(TENANT2_NAME);
        tenant.setVerifyNewProfiles(true);
        tenant.setRoles(ROLES);
        tenant.setAttributeDefinitions(new HashSet<>(Arrays.asList(def)));

        return tenant;
    }

    private void assertEqualTenants(Tenant expected, Tenant actual) {
        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.isVerifyNewProfiles(), actual.isVerifyNewProfiles());
        assertEquals(expected.getRoles(), actual.getRoles());
        assertEquals(expected.getAttributeDefinitions(), actual.getAttributeDefinitions());
    }

}
