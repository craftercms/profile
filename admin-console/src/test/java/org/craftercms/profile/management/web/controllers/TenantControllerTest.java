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

package org.craftercms.profile.management.web.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.commons.collections.SetUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.SubjectResolver;
import org.craftercms.commons.security.permissions.impl.PermissionEvaluatorImpl;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.management.security.permissions.CurrentUserSubjectResolver;
import org.craftercms.profile.management.security.permissions.TenantPermissionResolver;
import org.craftercms.security.authentication.impl.DefaultAuthentication;
import org.craftercms.security.utils.SecurityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.craftercms.profile.management.security.AuthorizationUtils.PROFILE_ADMIN_ROLE;
import static org.craftercms.profile.management.security.AuthorizationUtils.SUPERADMIN_ROLE;
import static org.craftercms.profile.management.security.AuthorizationUtils.TENANT_ADMIN_ROLE;
import static org.craftercms.profile.management.web.controllers.TenantController.MODEL_MESSAGE;
import static org.craftercms.profile.management.web.controllers.TenantController.MSG_TENANT_CREATED_FORMAT;
import static org.craftercms.profile.management.web.controllers.TenantController.MSG_TENANT_DELETED_FORMAT;
import static org.craftercms.profile.management.web.controllers.TenantController.MSG_TENANT_UPDATED_FORMAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.profile.management.web.controllers.TenantController}.
 *
 * @author avasquez
 */
public class TenantControllerTest {

    private static final String TENANT_NAME1 = "tenant1";
    private static final String TENANT_NAME2 = "tenant2";
    private static final String TENANT_NAME3 = "tenant3";
    private static final ObjectId TENANT_ID1 = ObjectId.get();
    private static final ObjectId TENANT_ID2 = ObjectId.get();
    private static final ObjectId TENANT_ID3 = ObjectId.get();

    private TenantController controller;
    @Mock
    private TenantService tenantService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Tenant tenant1 = getTenant(TENANT_ID1, TENANT_NAME1, SUPERADMIN_ROLE, TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE);
        Tenant tenant2 = getTenant(TENANT_ID2, TENANT_NAME2, TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE);
        Tenant tenant3 = getTenant(TENANT_ID3, TENANT_NAME3, PROFILE_ADMIN_ROLE);

        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(tenant1, tenant2));
        when(tenantService.getTenant(TENANT_NAME1)).thenReturn(tenant1);
        when(tenantService.getTenant(TENANT_NAME2)).thenReturn(tenant2);
        when(tenantService.createTenant(tenant3)).thenReturn(tenant3);

        SubjectResolver<Profile> subjectResolver = new CurrentUserSubjectResolver();
        TenantPermissionResolver permissionResolver = new TenantPermissionResolver();

        PermissionEvaluatorImpl<Profile, String> permissionEvaluator = new PermissionEvaluatorImpl<>();
        permissionEvaluator.setSubjectResolver(subjectResolver);
        permissionEvaluator.setPermissionResolver(permissionResolver);

        controller = new TenantController();
        controller.setTenantService(tenantService);
        controller.setTenantPermissionEvaluator(permissionEvaluator);

        setCurrentRequestContext();
    }

    @After
    public void tearDown() throws Exception {
        clearCurrentRequestContext();
    }

    @Test
    public void testGetTenantNamesBySuperadmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, SUPERADMIN_ROLE));

        List<String> tenantNames = controller.getTenantNames();

        assertNotNull(tenantNames);
        assertEquals(2, tenantNames.size());
        assertEquals(TENANT_NAME1, tenantNames.get(0));
        assertEquals(TENANT_NAME2, tenantNames.get(1));
    }

    @Test
    public void testGetTenantNamesByTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, TENANT_ADMIN_ROLE));

        List<String> tenantNames = controller.getTenantNames();

        assertNotNull(tenantNames);
        assertEquals(1, tenantNames.size());
        assertEquals(TENANT_NAME1, tenantNames.get(0));
    }

    @Test
    public void testGetTenantNamesByProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ADMIN_ROLE));

        List<String> tenantNames = controller.getTenantNames();

        assertNotNull(tenantNames);
        assertEquals(1, tenantNames.size());
        assertEquals(TENANT_NAME1, tenantNames.get(0));
    }

    @Test
    public void testGetTenant() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ADMIN_ROLE));

        Tenant tenant = controller.getTenant(TENANT_NAME1);

        assertNotNull(tenant);
        assertEquals(TENANT_NAME1, tenant.getName());
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetTenantByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, TENANT_ADMIN_ROLE));

        controller.getTenant(TENANT_NAME1);
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetTenantByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ADMIN_ROLE));

        controller.getTenant(TENANT_NAME1);
    }

    @Test
    public void testCreateTenant() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, SUPERADMIN_ROLE));

        Tenant tenant = getTenant(TENANT_ID3, TENANT_NAME3, PROFILE_ADMIN_ROLE);

        Map<String, String> model = controller.createTenant(tenant);

        assertNotNull(model);
        assertEquals(1, model.size());
        assertEquals(String.format(MSG_TENANT_CREATED_FORMAT, TENANT_NAME3), model.get(MODEL_MESSAGE));

        verify(tenantService).createTenant(tenant);
    }

    @Test(expected = ActionDeniedException.class)
    public void testCreateTenantByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, TENANT_ADMIN_ROLE));

        controller.createTenant(getTenant(TENANT_ID3, TENANT_NAME3, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testCreateTenantByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ADMIN_ROLE));

        controller.createTenant(getTenant(TENANT_ID3, TENANT_NAME3, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testCreateTenantWithReservedSuperadminRole() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, SUPERADMIN_ROLE));

        controller.createTenant(getTenant(TENANT_ID3, TENANT_NAME3, SUPERADMIN_ROLE));
    }

    @Test
    public void testUpdateTenant() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, TENANT_ADMIN_ROLE));

        Tenant tenant = getTenant(TENANT_ID2, TENANT_NAME2, TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE);

        Map<String, String> model = controller.updateTenant(tenant);

        assertNotNull(model);
        assertEquals(1, model.size());
        assertEquals(String.format(MSG_TENANT_UPDATED_FORMAT, TENANT_NAME2), model.get(MODEL_MESSAGE));

        verify(tenantService).updateTenant(tenant);
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateTenantByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, TENANT_ADMIN_ROLE));

        controller.updateTenant(getTenant(TENANT_ID2, TENANT_NAME2, TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateTenantByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ADMIN_ROLE));

        controller.createTenant(getTenant(TENANT_ID2, TENANT_NAME2, TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateTenantWithReservedSuperadminRoleRemoved() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, TENANT_ADMIN_ROLE));

        controller.updateTenant(getTenant(TENANT_ID1, TENANT_NAME1, TENANT_ADMIN_ROLE, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateTenantWithReservedSuperadminRoleAdded() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, TENANT_ADMIN_ROLE));

        controller.updateTenant(getTenant(TENANT_ID2, TENANT_NAME2, SUPERADMIN_ROLE, TENANT_ADMIN_ROLE,
                                          PROFILE_ADMIN_ROLE));
    }

    @Test
    public void testDeleteTenant() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, SUPERADMIN_ROLE));

        Map<String, String> model = controller.deleteTenant(TENANT_NAME2);

        assertNotNull(model);
        assertEquals(1, model.size());
        assertEquals(String.format(MSG_TENANT_DELETED_FORMAT, TENANT_NAME2), model.get(MODEL_MESSAGE));

        verify(tenantService).deleteTenant(TENANT_NAME2);
    }

    @Test(expected = ActionDeniedException.class)
    public void testDeleteTenantByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, TENANT_ADMIN_ROLE));

        controller.deleteTenant(TENANT_NAME2);
    }

    @Test(expected = ActionDeniedException.class)
    public void testDeleteTenantByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ADMIN_ROLE));

        controller.deleteTenant(TENANT_NAME2);
    }

    private void setCurrentRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        RequestContext.setCurrent(context);
    }

    private void clearCurrentRequestContext() {
        RequestContext.clear();
    }

    private void setCurrentUser(Profile profile) {
        DefaultAuthentication auth = new DefaultAuthentication(null, profile);

        SecurityUtils.setCurrentAuthentication(auth);
    }

    private Tenant getTenant(ObjectId id, String name, String... availableRoles) {
        Tenant tenant = new Tenant();
        tenant.setId(id);
        tenant.setName(name);
        tenant.setAvailableRoles(SetUtils.asSet(availableRoles));

        return tenant;
    }

    private Profile getProfile(String tenantName, String role) {
        Profile profile = new Profile();
        profile.setTenant(tenantName);
        profile.getRoles().add(role);

        return profile;
    }

}
