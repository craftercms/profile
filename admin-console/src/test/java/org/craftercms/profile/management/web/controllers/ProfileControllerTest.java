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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.commons.collections.SetUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.SubjectResolver;
import org.craftercms.commons.security.permissions.impl.PermissionEvaluatorImpl;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.management.security.permissions.CurrentUserSubjectResolver;
import org.craftercms.profile.management.security.permissions.ProfilePermissionResolver;
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
import static org.craftercms.profile.management.web.controllers.ProfileController.FINAL_QUERY_FORMAT;
import static org.craftercms.profile.management.web.controllers.ProfileController.MODEL_MESSAGE;
import static org.craftercms.profile.management.web.controllers.ProfileController.MSG_PROFILE_CREATED_FORMAT;
import static org.craftercms.profile.management.web.controllers.ProfileController.MSG_PROFILE_DELETED_FORMAT;
import static org.craftercms.profile.management.web.controllers.ProfileController.MSG_PROFILE_UPDATED_FORMAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.profile.management.web.controllers.ProfileController}.
 *
 * @author avasquez
 */
public class ProfileControllerTest {

    private static final String TENANT_NAME1 = "tenant1";
    private static final String TENANT_NAME2 = "tenant2";
    private static final ObjectId PROFILE_ID1 = ObjectId.get();
    private static final ObjectId PROFILE_ID2 = ObjectId.get();
    private static final ObjectId PROFILE_ID3 = ObjectId.get();
    private static final ObjectId PROFILE_ID4 = ObjectId.get();
    private static final String USERNAME1 = "user1";
    private static final String USERNAME2 = "user2";
    private static final String USERNAME3 = "user3";
    private static final String USERNAME4 = "user4";

    private ProfileController controller;
    @Mock
    private ProfileService profileService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        String query = String.format(FINAL_QUERY_FORMAT, USERNAME1);
        Profile profile1 = getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE);
        Profile profile2 = getProfile(TENANT_NAME1, PROFILE_ID2, USERNAME2, PROFILE_ADMIN_ROLE);
        Profile profile3 = getProfile(TENANT_NAME1, PROFILE_ID3, USERNAME3, SUPERADMIN_ROLE);
        Profile profile4 = getProfile(TENANT_NAME1, PROFILE_ID4, USERNAME4, TENANT_ADMIN_ROLE);

        when(profileService.getProfileCountByQuery(TENANT_NAME1, query)).thenReturn(1L);
        when(profileService.getProfilesByQuery(TENANT_NAME1, query, null, null, null, null, new String[0]))
            .thenReturn(Arrays.asList(profile1));
        when(profileService.getProfile(PROFILE_ID1.toString(), new String[0])).thenReturn(profile1);
        when(profileService.getProfile(PROFILE_ID3.toString(), new String[0])).thenReturn(profile3);
        when(profileService.getProfile(PROFILE_ID4.toString(), new String[0])).thenReturn(profile4);
        when(profileService.createProfile(TENANT_NAME1, USERNAME2, null, null, false, SetUtils.asSet(
            PROFILE_ADMIN_ROLE), new HashMap<String, Object>(), null)).thenReturn(profile2);

        SubjectResolver<Profile> subjectResolver = new CurrentUserSubjectResolver();
        TenantPermissionResolver tenantPermissionResolver = new TenantPermissionResolver();
        ProfilePermissionResolver profilePermissionResolver = new ProfilePermissionResolver();

        PermissionEvaluatorImpl<Profile, String> tenantPermissionEvaluator = new PermissionEvaluatorImpl<>();
        tenantPermissionEvaluator.setSubjectResolver(subjectResolver);
        tenantPermissionEvaluator.setPermissionResolver(tenantPermissionResolver);

        PermissionEvaluatorImpl<Profile, Profile> profilePermissionEvaluator = new PermissionEvaluatorImpl<>();
        profilePermissionEvaluator.setSubjectResolver(subjectResolver);
        profilePermissionEvaluator.setPermissionResolver(profilePermissionResolver);

        controller = new ProfileController();
        controller.setProfileService(profileService);
        controller.setTenantPermissionEvaluator(tenantPermissionEvaluator);
        controller.setProfilePermissionEvaluator(profilePermissionEvaluator);

        setCurrentRequestContext();
    }

    @After
    public void tearDown() throws Exception {
        clearCurrentRequestContext();
    }

    @Test
    public void testGetProfileCount() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        long count = controller.getProfileCount(TENANT_NAME1, USERNAME1);

        assertEquals(1L, count);
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetProfileCountByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.getProfileCount(TENANT_NAME1, USERNAME1);
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetProfileCountByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.getProfileCount(TENANT_NAME1, USERNAME1);
    }

    @Test
    public void testGetProfileList() throws Exception {
        Profile currentUser = getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE);

        setCurrentUser(currentUser);

        List<Profile> profiles = controller.getProfileList(TENANT_NAME1, USERNAME1, null, null, null, null);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertProfiles(currentUser, profiles.get(0));
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetProfileListByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.getProfileList(TENANT_NAME1, USERNAME1, null, null, null, null);
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetProfileListByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.getProfileList(TENANT_NAME1, USERNAME1, null, null, null, null);
    }

    @Test
    public void testGetProfile() throws Exception {
        Profile currentUser = getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE);

        setCurrentUser(currentUser);

        Profile profile = controller.getProfile(PROFILE_ID1.toString());

        assertNotNull(profile);
        assertProfiles(currentUser, profile);
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetProfileByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.getProfile(PROFILE_ID1.toString());
    }

    @Test(expected = ActionDeniedException.class)
    public void testGetProfileByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.getProfile(PROFILE_ID1.toString());
    }

    @Test
    public void testCreateProfile() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        Map<String, String> model = controller.createProfile(getProfile(TENANT_NAME1, null, USERNAME2,
                                                                        PROFILE_ADMIN_ROLE));
        
        assertNotNull(model);
        assertEquals(1, model.size());
        assertEquals(String.format(MSG_PROFILE_CREATED_FORMAT, PROFILE_ID2.toString()), model.get(MODEL_MESSAGE));

        verify(profileService).createProfile(TENANT_NAME1, USERNAME2, null, null, false,
                                             SetUtils.asSet(PROFILE_ADMIN_ROLE), new HashMap<String, Object>(), null);
    }

    @Test(expected = ActionDeniedException.class)
    public void testCreateProfileByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.createProfile(getProfile(TENANT_NAME1, null, USERNAME2, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testCreateProfileByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.createProfile(getProfile(TENANT_NAME1, null, USERNAME2, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testCreateSuperAdminByTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.createProfile(getProfile(TENANT_NAME1, null, USERNAME2, SUPERADMIN_ROLE));
    }
    @Test(expected = ActionDeniedException.class)
    public void testCreateSuperAdminByProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.createProfile(getProfile(TENANT_NAME1, null, USERNAME2, SUPERADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testCreateTenantAdminByProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.createProfile(getProfile(TENANT_NAME1, null, USERNAME2, TENANT_ADMIN_ROLE));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        Profile currentUser = getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE);

        setCurrentUser(currentUser);

        Map<String, String> model = controller.updateProfile(currentUser);

        assertNotNull(model);
        assertEquals(1, model.size());
        assertEquals(String.format(MSG_PROFILE_UPDATED_FORMAT, PROFILE_ID1.toString()), model.get(MODEL_MESSAGE));

        verify(profileService).updateProfile(PROFILE_ID1.toString(), USERNAME1, null, null, false,
                                             SetUtils.asSet(PROFILE_ADMIN_ROLE), new HashMap<String, Object>(),
                                             ProfileConstants.NO_ATTRIBUTE);
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateProfileByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.updateProfile(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateProfileByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.updateProfile(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateSuperadminByTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.updateProfile(getProfile(TENANT_NAME1, PROFILE_ID3, USERNAME3, SUPERADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateSuperadminByProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.updateProfile(getProfile(TENANT_NAME1, PROFILE_ID3, USERNAME3, SUPERADMIN_ROLE));
    }

    @Test(expected = ActionDeniedException.class)
    public void testUpdateTenantAdminByProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.updateProfile(getProfile(TENANT_NAME1, PROFILE_ID4, USERNAME4, TENANT_ADMIN_ROLE));
    }

    @Test
    public void testDeleteProfile() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        Map<String, String> model = controller.deleteProfile(PROFILE_ID1.toString());

        assertNotNull(model);
        assertEquals(1, model.size());
        assertEquals(String.format(MSG_PROFILE_DELETED_FORMAT, PROFILE_ID1.toString()), model.get(MODEL_MESSAGE));

        verify(profileService).deleteProfile(PROFILE_ID1.toString());
    }

    @Test(expected = ActionDeniedException.class)
    public void testDeleteProfileByInvalidTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.deleteProfile(PROFILE_ID1.toString());
    }

    @Test(expected = ActionDeniedException.class)
    public void testDeleteProfileByInvalidProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME2, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.deleteProfile(PROFILE_ID1.toString());
    }

    @Test(expected = ActionDeniedException.class)
    public void testDeleteSuperadminByTenantAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, TENANT_ADMIN_ROLE));

        controller.deleteProfile(PROFILE_ID3.toString());
    }

    @Test(expected = ActionDeniedException.class)
    public void testDeleteSuperadminByProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.deleteProfile(PROFILE_ID3.toString());
    }

    @Test(expected = ActionDeniedException.class)
    public void testDeleteTenantAdminByProfileAdmin() throws Exception {
        setCurrentUser(getProfile(TENANT_NAME1, PROFILE_ID1, USERNAME1, PROFILE_ADMIN_ROLE));

        controller.deleteProfile(PROFILE_ID4.toString());
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

    private Profile getProfile(String tenantName, ObjectId id, String username, String role) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setTenant(tenantName);
        profile.setUsername(username);
        profile.getRoles().add(role);

        return profile;
    }

    private void assertProfiles(Profile expected, Profile actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTenant(), actual.getTenant());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getRoles(), actual.getRoles());
    }

}
