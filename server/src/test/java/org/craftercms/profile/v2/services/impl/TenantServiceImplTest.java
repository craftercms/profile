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
package org.craftercms.profile.v2.services.impl;

import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.v2.exceptions.AttributeAlreadyDefinedException;
import org.craftercms.profile.v2.permissions.Application;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.craftercms.profile.v2.repositories.TenantRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link org.craftercms.profile.v2.services.impl.TenantServiceImpl}.
 *
 * @author avasquez
 */
public class TenantServiceImplTest {

    private static final String TENANT_NAME =            "tenant1";
    private static final boolean VERIFY_NEW_PROFILES =   true;
    private static final String ROLE1 =                  "role1";
    private static final String ROLE2 =                  "role2";
    private static final Set<String> ROLES =             new HashSet<>(Arrays.asList(ROLE1));

    private static final String ATTRIB1_NAME =   "attrib1";
    private static final String ATTRIB2_NAME =   "attrib2";
    private static final String APP1_NAME =      "app1";
    private static final String APP2_NAME =      "app2";

    private TenantServiceImpl tenantService;
    @Mock
    private PermissionEvaluator<Application, String> permissionEvaluator;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private ProfileRepository profileRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(permissionEvaluator.isAllowed(anyString(), anyString())).thenReturn(true);

        when(tenantRepository.findByName(TENANT_NAME)).thenReturn(getTenant());
        when(tenantRepository.findAll()).thenReturn(Arrays.asList(getTenant()));
        when(tenantRepository.count()).thenReturn(3L);

        tenantService = new TenantServiceImpl();
        tenantService.setPermissionEvaluator(permissionEvaluator);
        tenantService.setTenantRepository(tenantRepository);
        tenantService.setProfileRepository(profileRepository);
    }

    @Test
    public void testCreateTenant() throws Exception {
        Tenant actual = tenantService.createTenant(TENANT_NAME, VERIFY_NEW_PROFILES, ROLES);
        Tenant expected = getTenant();

        expected.setAttributeDefinitions(Collections.<AttributeDefinition>emptySet());

        assertEqualTenants(expected, actual);

        verify(tenantRepository).save(actual);
    }

    @Test
    public void testGetTenant() throws Exception {
        Tenant actual = tenantService.getTenant(TENANT_NAME);
        Tenant expected = getTenant();

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT_NAME);
    }

    @Test
    public void testDeleteTenant() throws Exception {
        tenantService.deleteTenant(TENANT_NAME);

        verify(profileRepository).removeAllForTenant(TENANT_NAME);
        verify(tenantRepository).removeByName(TENANT_NAME);
    }

    @Test
    public void testGetTenantCount() throws Exception {
        long expected = 3L;
        long actual = tenantService.getTenantCount();

        assertEquals(expected, actual);

        verify(tenantRepository).count();
    }

    @Test
    public void testGetAllTenants() throws Exception {
        List<Tenant> expected = Arrays.asList(getTenant());
        List<Tenant> actual = (List<Tenant>) tenantService.getAllTenants();

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEqualTenants(expected.get(0), actual.get(0));

        verify(tenantRepository).findAll();
    }

    @Test
    public void testVerifyNewProfiles() throws Exception {
        Tenant expected = getTenant();
        expected.setVerifyNewProfiles(false);

        Tenant actual = tenantService.verifyNewProfiles(TENANT_NAME, false);

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT_NAME);
        verify(tenantRepository).save(actual);
    }

    @Test
    public void testAddRoles() throws Exception {
        Tenant expected = getTenant();
        expected.getRoles().add(ROLE2);

        Tenant actual = tenantService.addRoles(TENANT_NAME, Arrays.asList(ROLE2));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT_NAME);
        verify(tenantRepository).save(actual);
    }

    @Test
    public void testRemoveRoles() throws Exception {
        Tenant expected = getTenant();
        expected.getRoles().remove("role1");

        Tenant actual = tenantService.removeRoles(TENANT_NAME, Arrays.asList("role1"));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT_NAME);
        verify(tenantRepository).save(actual);
    }

    @Test
    public void testAddAttributeDefinitions() throws Exception {
        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB2_NAME);
        def.setOwner(APP1_NAME);

        Tenant expected = getTenant();
        expected.getAttributeDefinitions().add(def);

        Tenant actual = tenantService.addAttributeDefinitions(TENANT_NAME, Arrays.asList(def));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT_NAME);
        verify(tenantRepository).save(actual);


    }

    @Test
    public void testAddRepeatedAttributeDefinition() throws Exception {
        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB1_NAME);
        def.setOwner(APP1_NAME);

        try {
            tenantService.addAttributeDefinitions(TENANT_NAME, Arrays.asList(def));
            fail("Expected " + AttributeAlreadyDefinedException.class.getSimpleName() + " exception");
        } catch (AttributeAlreadyDefinedException e) {
        }
    }

    @Test
    public void testRemoveAttributeDefinitions() throws Exception {
        Application.setCurrent(new Application(APP1_NAME, Collections.<TenantPermission>emptyList()));

        Tenant expected = getTenant();
        expected.getAttributeDefinitions().clear();

        Tenant actual = tenantService.removeAttributeDefinitions(TENANT_NAME, Arrays.asList(ATTRIB1_NAME));

        assertEqualTenants(expected, actual);

        verify(tenantRepository).findByName(TENANT_NAME);
        verify(tenantRepository).save(actual);

        Application.clear();
    }

    @Test
    public void testRemoveAttributeDefinitionNotOwned() throws Exception {
        Application.setCurrent(new Application(APP2_NAME, Collections.<TenantPermission>emptyList()));

        try {
            tenantService.removeAttributeDefinitions(TENANT_NAME, Arrays.asList(ATTRIB1_NAME));
            fail("Expected " + ActionDeniedException.class.getSimpleName() + " exception");
        } catch (ActionDeniedException e) {
        }

        Application.clear();
    }

    private Tenant getTenant() {
        AttributeDefinition def = new AttributeDefinition();
        def.setName(ATTRIB1_NAME);
        def.setOwner(APP1_NAME);

        Tenant tenant = new Tenant();
        tenant.setName(TENANT_NAME);
        tenant.setVerifyNewProfiles(VERIFY_NEW_PROFILES);
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
