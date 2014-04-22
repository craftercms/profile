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

import org.bson.types.ObjectId;
import org.craftercms.commons.crypto.CipherUtils;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.*;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.v2.exceptions.InvalidEmailAddressException;
import org.craftercms.profile.v2.permissions.Application;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.craftercms.profile.v2.services.VerificationService;
import org.craftercms.profile.v2.services.VerificationSuccessCallback;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * Unit test for {@link org.craftercms.profile.v2.services.impl.ProfileServiceImpl}.
 *
 * @author avasquez
 */
public class ProfileServiceImplTest {
    
    private static final String ATTRIB_NAME_FIRST_NAME =    "firstName";
    private static final String ATTRIB_NAME_LAST_NAME =     "lastName";
    private static final String ATTRIB_NAME_GENDER =        "gender";

    private static final String TENANT1_NAME =  "tenant1";
    private static final String TENANT2_NAME =  "tenant2";

    private static final ObjectId PROFILE1_ID =             new ObjectId("507c7f79bcf86cd7994f6c0e");
    private static final ObjectId PROFILE2_ID =             new ObjectId("50f1d54e9beb36a0f45c6452");
    private static final List<String> TENANT1_PROFILE_IDS = Arrays.asList(PROFILE1_ID.toString());
    private static final String USERNAME1 =                 "user1";
    private static final String USERNAME2 =                 "user2";
    private static final String PASSWORD1 =                 "12345";
    private static final String PASSWORD2 =                 "54321";
    private static final String EMAIL1 =                    "user1@craftersoftware.com";
    private static final String EMAIL2 =                    "user2@craftersoftware.com";
    private static final String ROLE1 =                     "role1";
    private static final String ROLE2 =                     "role2";
    private static final Set<String> ROLES1 =               new HashSet<>(Arrays.asList(ROLE1));
    private static final Set<String> ROLES2 =               new HashSet<>(Arrays.asList(ROLE2));
    private static final String FIRST_NAME =                "John";
    private static final String LAST_NAME =                 "Doe";
    private static final String GENDER =                    "male";

    private static final String VERIFICATION_URL =      "http://localhost:8080/verifyProfile";
    private static final String VERIFICATION_TOKEN_ID = "000000000000000000000001";

    private static final ObjectId TICKET_ID = new ObjectId("4d6e5acebcd1b3fac9000002");

    private static final String SORT_BY =   "username";
    private static final int START =        0;
    private static final int COUNT =        10;

    private static final String RESET_PASSWORD_URL =        "http://localhost:8080/resetPassword";
    private static final String RESET_PASSWORD_TOKEN_ID =   "000000000000000000000002";

    private ProfileServiceImpl profileService;
    @Mock
    private PermissionEvaluator<Application, String> tenantPermissionEvaluator;
    @Mock
    private PermissionEvaluator<Application, AttributeDefinition> attributePermissionEvaluator;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private TenantService tenantService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private VerificationService newProfileVerificationService;
    @Mock
    private VerificationService resetPasswordVerificationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(tenantPermissionEvaluator.isAllowed(anyString(), anyString())).thenReturn(true);
        when(attributePermissionEvaluator.isAllowed(any(AttributeDefinition.class), anyString())).thenReturn(true);

        when(profileRepository.findById(PROFILE1_ID.toString(), new String[0]))
                .thenReturn(getTenant1Profile());
        when(profileRepository.findById(PROFILE1_ID.toString(), NO_ATTRIBUTE))
                .thenReturn(getTenant1ProfileNoAttributes());
        when(profileRepository.findById(PROFILE1_ID.toString(), ATTRIB_NAME_FIRST_NAME))
                .thenReturn(getTenant1ProfileNoLastName());
        when(profileRepository.findById(PROFILE2_ID.toString(), new String[0]))
                .thenReturn(getTenant2Profile());
        when(profileRepository.findByTenantAndUsername(TENANT1_NAME, USERNAME1, new String[0]))
                .thenReturn(getTenant1Profile());
        when(profileRepository.findByIds(TENANT1_PROFILE_IDS, SORT_BY, SortOrder.ASC))
                .thenReturn(getAllTenant1Profiles());
        when(profileRepository.findRange(TENANT1_NAME, SORT_BY, SortOrder.ASC, START, COUNT))
                .thenReturn(getAllTenant1Profiles());
        when(profileRepository.findByTenantAndRole(TENANT1_NAME, ROLE1, SORT_BY, SortOrder.ASC))
                .thenReturn(getAllTenant1Profiles());
        when(profileRepository.findByTenantAndAttributeValue(TENANT1_NAME, ATTRIB_NAME_FIRST_NAME, FIRST_NAME, SORT_BY,
                SortOrder.ASC)).thenReturn(getAllTenant1Profiles());
        when(profileRepository.countByTenant(TENANT1_NAME)).thenReturn(10L);

        when(tenantService.getTenant(TENANT1_NAME)).thenReturn(getTenant1());
        when(tenantService.getTenant(TENANT2_NAME)).thenReturn(getTenant2());

        when(authenticationService.getTicket(TICKET_ID.toString())).thenReturn(getTicket());

        when(newProfileVerificationService.verifyToken(eq(VERIFICATION_TOKEN_ID), any(
                VerificationSuccessCallback.class))).then(new VerifyTokenAnswer(PROFILE2_ID.toString()));
        when(resetPasswordVerificationService.verifyToken(eq(RESET_PASSWORD_TOKEN_ID), any(
                VerificationSuccessCallback.class))).then(new VerifyTokenAnswer(PROFILE1_ID.toString()));

        profileService = new ProfileServiceImpl();
        profileService.setTenantPermissionEvaluator(tenantPermissionEvaluator);
        profileService.setAttributePermissionEvaluator(attributePermissionEvaluator);
        profileService.setProfileRepository(profileRepository);
        profileService.setTenantService(tenantService);
        profileService.setAuthenticationService(authenticationService);
        profileService.setNewProfileVerificationService(newProfileVerificationService);
        profileService.setResetPasswordVerificationService(resetPasswordVerificationService);
    }

    @Test
    public void testCreateProfile() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setTenant(TENANT1_NAME);

        Profile actual = profileService.createProfile(TENANT1_NAME, USERNAME2, PASSWORD2, EMAIL2, true, ROLES2,
                VERIFICATION_URL);

        assertEqualProfiles(expected, actual);
        assertTrue(CipherUtils.matchPassword(actual.getPassword(), PASSWORD2));
        assertNotNull(actual.getCreated());
        assertNotNull(actual.getModified());

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(tenantService).getTenant(TENANT1_NAME);
        verify(profileRepository).insert(actual);
        verify(newProfileVerificationService).sendEmail(actual, VERIFICATION_URL);
    }

    @Test
    public void testCreateProfileInvalidEmail() throws Exception {
        try {
            profileService.createProfile(TENANT1_NAME, USERNAME2, PASSWORD2, "a.com", true, ROLES2, VERIFICATION_URL);
            fail("Exception " + InvalidEmailAddressException.class.getName() + " expected");
        } catch (InvalidEmailAddressException e) {
        }
    }

    @Test
    public void testCreateProfileNotVerify() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setEnabled(true);

        Profile actual = profileService.createProfile(TENANT2_NAME, USERNAME2, PASSWORD2, EMAIL2, true, ROLES2,
                VERIFICATION_URL);

        assertEqualProfiles(expected, actual);
        assertTrue(CipherUtils.matchPassword(actual.getPassword(), PASSWORD2));
        assertNotNull(actual.getCreated());
        assertNotNull(actual.getModified());

        verify(tenantPermissionEvaluator).isAllowed(TENANT2_NAME, TenantActions.MANAGE_PROFILES);
        verify(tenantService).getTenant(TENANT2_NAME);
        verify(profileRepository).insert(actual);
        verify(newProfileVerificationService, never()).sendEmail(actual, VERIFICATION_URL);
    }

    @Test
    public void testUpdateProfile() throws Exception {
        Profile expected = new Profile();
        expected.setId(PROFILE1_ID);
        expected.setTenant(TENANT1_NAME);
        expected.setUsername(USERNAME2);
        expected.setPassword(CipherUtils.hashPassword(PASSWORD2));
        expected.setEmail(EMAIL2);
        expected.setRoles(ROLES2);
        expected.setVerified(true);
        expected.setEnabled(false);
        expected.getAttributes().put(ATTRIB_NAME_FIRST_NAME, FIRST_NAME);
        expected.getAttributes().put(ATTRIB_NAME_LAST_NAME, LAST_NAME);

        Profile actual = profileService.updateProfile(PROFILE1_ID.toString(), USERNAME2, PASSWORD2, EMAIL2, false,
                ROLES2);

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    @Test
    public void testUpdateProfileInvalidEmail() throws Exception {
        try {
            profileService.updateProfile(PROFILE1_ID.toString(), USERNAME2, PASSWORD2, "a.com", false, ROLES2);
            fail("Exception " + InvalidEmailAddressException.class.getName() + " expected");
        } catch (InvalidEmailAddressException e) {
        }
    }

    @Test
    public void testVerifyProfile() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setVerified(true);
        expected.setEnabled(true);

        Profile actual = profileService.verifyProfile(VERIFICATION_TOKEN_ID);

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT2_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE2_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    @Test
    public void testEnableProfile() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setEnabled(true);

        Profile actual = profileService.enableProfile(PROFILE2_ID.toString());

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT2_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE2_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    @Test
    public void testDisableProfile() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setEnabled(false);

        Profile actual = profileService.disableProfile(PROFILE1_ID.toString());

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    @Test
    public void testAddRoles() throws Exception {
        Profile expected = getTenant1Profile();
        expected.getRoles().add(ROLE2);

        Profile actual = profileService.addRoles(PROFILE1_ID.toString(), Arrays.asList(ROLE2));

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    @Test
    public void testRemoveRoles() throws Exception {
        Profile expected = getTenant1Profile();
        expected.getRoles().remove(ROLE1);

        Profile actual = profileService.removeRoles(PROFILE1_ID.toString(), Arrays.asList(ROLE1));

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }


    @Test
    public void testGetAllAttributes() throws Exception {
        Map<String, Object> attributes = profileService.getAttributes(PROFILE1_ID.toString());

        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals(FIRST_NAME, attributes.get(ATTRIB_NAME_FIRST_NAME));
        assertEquals(LAST_NAME, attributes.get(ATTRIB_NAME_LAST_NAME));

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
    }

    @Test
    public void testGetOneAttribute() throws Exception {
        Map<String, Object> attributes = profileService.getAttributes(PROFILE1_ID.toString(), ATTRIB_NAME_FIRST_NAME);

        assertNotNull(attributes);
        assertEquals(1, attributes.size());
        assertEquals(FIRST_NAME, attributes.get(ATTRIB_NAME_FIRST_NAME));

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), ATTRIB_NAME_FIRST_NAME);
    }

    @Test
    public void testGetNoAttributes() throws Exception {
        Map<String, Object> attributes = profileService.getAttributes(PROFILE1_ID.toString(), NO_ATTRIBUTE);

        assertNotNull(attributes);
        assertEquals(0, attributes.size());
    }

    @Test
    public void testUpdateAttributes() throws Exception {
        Profile expected = getTenant1Profile();
        expected.getAttributes().put(ATTRIB_NAME_GENDER, GENDER);

        Map<String, Object> newAttributes = Collections.<String, Object>singletonMap(ATTRIB_NAME_GENDER, GENDER);
        Profile actual = profileService.updateAttributes(PROFILE1_ID.toString(), newAttributes);

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    @Test
    public void testRemoveAttributes() throws Exception {
        Profile expected = getTenant1Profile();
        expected.getAttributes().remove(ATTRIB_NAME_LAST_NAME);

        Profile actual = profileService.removeAttributes(PROFILE1_ID.toString(), Arrays.asList(ATTRIB_NAME_LAST_NAME));

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    @Test
    public void testDeleteProfile() throws Exception {
        profileService.deleteProfile(PROFILE1_ID.toString());

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).removeById(PROFILE1_ID.toString());
    }

    @Test
    public void testGetProfile() throws Exception {
        Profile expected = getTenant1Profile();
        Profile actual = profileService.getProfile(PROFILE1_ID.toString());

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
    }

    @Test
    public void testGetProfileByUsername() throws Exception {
        Profile expected = getTenant1Profile();
        Profile actual = profileService.getProfileByUsername(TENANT1_NAME, USERNAME1);

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findByTenantAndUsername(TENANT1_NAME, USERNAME1, new String[0]);
    }

    @Test
    public void testGetProfileCount() throws Exception {
        long expected = 10L;
        long actual = profileService.getProfileCount(TENANT1_NAME);

        assertEquals(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).countByTenant(TENANT1_NAME);
    }

    @Test
    public void testGetProfilesByIds() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        List<Profile> actual = profileService.getProfilesByIds(TENANT1_PROFILE_IDS, "username", SortOrder.ASC);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findByIds(TENANT1_PROFILE_IDS, "username", SortOrder.ASC);
    }

    @Test
    public void testGetProfileRange() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        List<Profile> actual = profileService.getProfileRange(TENANT1_NAME, SORT_BY, SortOrder.ASC, START, COUNT);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findRange(TENANT1_NAME, SORT_BY, SortOrder.ASC, START, COUNT);
    }

    @Test
    public void testGetProfileByRole() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        List<Profile> actual = profileService.getProfilesByRole(TENANT1_NAME, ROLE1, SORT_BY, SortOrder.ASC);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findByTenantAndRole(TENANT1_NAME, ROLE1, SORT_BY, SortOrder.ASC);
    }

    @Test
    public void testGetProfilesByAttribute() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        List<Profile> actual = profileService.getProfilesByAttributeValue(TENANT1_NAME, ATTRIB_NAME_FIRST_NAME,
                FIRST_NAME, SORT_BY, SortOrder.ASC);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findByTenantAndAttributeValue(TENANT1_NAME, ATTRIB_NAME_FIRST_NAME, FIRST_NAME, SORT_BY,
                SortOrder.ASC);
    }

    @Test
    public void testForgotPassword() throws Exception {
        Profile expected = getTenant1Profile();
        Profile actual = profileService.forgotPassword(PROFILE1_ID.toString(), RESET_PASSWORD_URL);

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(resetPasswordVerificationService).sendEmail(actual, RESET_PASSWORD_URL);
    }

    @Test
    public void testResetPassword() throws Exception {
        Profile expected = getTenant1Profile();
        Profile actual = profileService.resetPassword(RESET_PASSWORD_TOKEN_ID, PASSWORD2);

        assertEqualProfiles(expected, actual);
        assertTrue(CipherUtils.matchPassword(actual.getPassword(), PASSWORD2));

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantActions.MANAGE_PROFILES);
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).save(actual);
    }

    private Tenant getTenant1() {
        AttributePermission anyAppCanDoAnything = new AttributePermission(AttributePermission.ANY_APPLICATION);
        anyAppCanDoAnything.allow(AttributePermission.ANY_ACTION);

        AttributeDefinition firstNameDefinition = new AttributeDefinition();
        firstNameDefinition.setName(ATTRIB_NAME_FIRST_NAME);
        firstNameDefinition.addPermission(anyAppCanDoAnything);

        AttributeDefinition lastNameDefinition = new AttributeDefinition();
        lastNameDefinition.setName(ATTRIB_NAME_LAST_NAME);
        lastNameDefinition.addPermission(anyAppCanDoAnything);

        AttributeDefinition genderDefinition = new AttributeDefinition();
        genderDefinition.setName(ATTRIB_NAME_GENDER);
        genderDefinition.addPermission(anyAppCanDoAnything);

        Tenant tenant = new Tenant();
        tenant.setName(TENANT1_NAME);
        tenant.setVerifyNewProfiles(true);
        tenant.getAttributeDefinitions().add(firstNameDefinition);
        tenant.getAttributeDefinitions().add(lastNameDefinition);
        tenant.getAttributeDefinitions().add(genderDefinition);

        return tenant;
    }

    private Tenant getTenant2() {
        Tenant tenant = new Tenant();
        tenant.setName(TENANT2_NAME);
        tenant.setVerifyNewProfiles(false);

        return tenant;
    }

    private Profile getTenant1Profile() {
        Profile profile = new Profile();
        profile.setId(PROFILE1_ID);
        profile.setTenant(TENANT1_NAME);
        profile.setUsername(USERNAME1);
        profile.setPassword(CipherUtils.hashPassword(PASSWORD1));
        profile.setEmail(EMAIL1);
        profile.setRoles(ROLES1);
        profile.setVerified(true);
        profile.setEnabled(true);
        profile.getAttributes().put(ATTRIB_NAME_FIRST_NAME, FIRST_NAME);
        profile.getAttributes().put(ATTRIB_NAME_LAST_NAME, LAST_NAME);

        return profile;
    }

    private Profile getTenant1ProfileNoAttributes() {
        Profile profile = getTenant1Profile();
        profile.getAttributes().clear();

        return profile;
    }

    private Profile getTenant1ProfileNoLastName() {
        Profile profile = getTenant1Profile();
        profile.getAttributes().remove(ATTRIB_NAME_LAST_NAME);

        return profile;
    }

    private List<Profile> getAllTenant1Profiles() {
        return Arrays.asList(getTenant1Profile());
    }

    private Profile getTenant2Profile() {
        Profile profile = new Profile();
        profile.setId(PROFILE2_ID);
        profile.setTenant(TENANT2_NAME);
        profile.setUsername(USERNAME2);
        profile.setPassword(CipherUtils.hashPassword(PASSWORD2));
        profile.setEmail(EMAIL2);
        profile.setRoles(ROLES2);
        profile.setVerified(false);
        profile.setEnabled(false);

        return profile;
    }

    private Ticket getTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(TICKET_ID);
        ticket.setTenant(TENANT1_NAME);
        ticket.setProfileId(PROFILE1_ID.toString());
        ticket.setLastRequestTime(new Date());

        return ticket;
    }

    private void assertEqualProfiles(Profile expected, Profile actual) {
        assertNotNull(actual);
        assertEquals(expected.getTenant(), actual.getTenant());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getRoles(), actual.getRoles());
        assertEquals(expected.isVerified(), actual.isVerified());
        assertEquals(expected.isEnabled(), actual.isEnabled());
        assertEquals(expected.getAttributes(), actual.getAttributes());
    }

    private void assertEqualProfileLists(List<Profile> expected, List<Profile> actual) {
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEqualProfiles(expected.get(i), actual.get(i));
        }
    }

    private static class VerifyTokenAnswer implements Answer<Profile> {

        private String profileId;

        private VerifyTokenAnswer(String profileId) {
            this.profileId = profileId;
        }

        @Override
        public Profile answer(InvocationOnMock invocation) throws Throwable {
            VerificationSuccessCallback callback = (VerificationSuccessCallback) invocation.getArguments()[1];
            VerificationToken token = new VerificationToken(profileId, new Date());

            return callback.doOnSuccess(token);
        }

    }

}
