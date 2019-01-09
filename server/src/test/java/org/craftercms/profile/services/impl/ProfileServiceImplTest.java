/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.craftercms.commons.crypto.CryptoUtils;
import org.craftercms.commons.entitlements.validator.EntitlementValidator;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.AttributePermission;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.TenantAction;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.exceptions.InvalidEmailAddressException;
import org.craftercms.profile.exceptions.InvalidQueryException;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.services.VerificationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.craftercms.profile.api.ProfileConstants.NO_ATTRIBUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link org.craftercms.profile.services.impl.ProfileServiceImpl}.
 *
 * @author avasquez
 */
public class ProfileServiceImplTest {

    private static final String ATTRIB_NAME_FIRST_NAME = "firstName";
    private static final String ATTRIB_NAME_LAST_NAME = "lastName";
    private static final String ATTRIB_NAME_GENDER = "gender";
    private static final String ATTRIB_NAME_PRIVATE = "private";

    private static final String TENANT1_NAME = "tenant1";
    private static final String TENANT2_NAME = "tenant2";

    private static final ObjectId PROFILE1_ID = new ObjectId();
    private static final ObjectId PROFILE2_ID = new ObjectId();
    private static final List<String> TENANT1_PROFILE_IDS = Arrays.asList(PROFILE1_ID.toString());
    private static final String USERNAME1 = "user1";
    private static final String USERNAME2 = "user2";
    private static final String PASSWORD1 = "12345";
    private static final String PASSWORD2 = "54321";
    private static final String EMAIL1 = "user1@example.com";
    private static final String EMAIL2 = "user2@example.com";
    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final Set<String> ROLES1 = new HashSet<>(Arrays.asList(ROLE1));
    private static final Set<String> ROLES2 = new HashSet<>(Arrays.asList(ROLE2));
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String GENDER = "male";

    private static final String QUERY = "{attributes.firstName: 'John'}";
    private static final String INVALID_QUERY1 = "{tenant: 'tenant1'}";
    private static final String INVALID_QUERY2 = "{$where: \"this.tenant == 'tenant1'\"}";
    private static final String INVALID_QUERY3 = "{attributes.private.sub: 'test'}";

    private static final String VERIFICATION_URL = "http://localhost:8080/verifyProfile";
    private static final String VERIFICATION_FROM_ADDRESS = "noreply@example.com";
    private static final String VERIFICATION_SUBJECT = "Verify Account";
    private static final String VERIFICATION_TEMPLATE_NAME = "verify-new-profile-email.ftl";

    private static final String TICKET_ID = UUID.randomUUID().toString();

    private static final String SORT_BY = "username";
    private static final int START = 0;
    private static final int COUNT = 10;

    private static final String RESET_PASSWORD_URL = "http://localhost:8080/resetPassword";
    private static final String RESET_PASSWORD_FROM_ADDRESS = "noreply@example.com";
    private static final String RESET_PASSWORD_SUBJECT = "Reset Password";
    private static final String RESET_PASSWORD_TEMPLATE_NAME = "reset-password-email.ftl";

    private static final String VERIFICATION_TOKEN_ID1 = UUID.randomUUID().toString();
    private static final String VERIFICATION_TOKEN_ID2 = UUID.randomUUID().toString();

    private ProfileServiceImpl profileService;
    @Mock
    private PermissionEvaluator<AccessToken, String> tenantPermissionEvaluator;
    @Mock
    private PermissionEvaluator<AccessToken, AttributeDefinition> attributePermissionEvaluator;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private TenantService tenantService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private VerificationService verificationService;
    @Mock
    private EntitlementValidator entitlementValidator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(tenantPermissionEvaluator.isAllowed(anyString(), anyString()))
            .thenReturn(true);
        when(attributePermissionEvaluator.isAllowed(any(AttributeDefinition.class), anyString()))
            .thenReturn(true);
        when(attributePermissionEvaluator.isAllowed(eq(new AttributeDefinition(ATTRIB_NAME_PRIVATE)), anyString()))
            .thenReturn(false);

        when(tenantService.getTenant(TENANT1_NAME)).thenReturn(getTenant1());
        when(tenantService.getTenant(TENANT2_NAME)).thenReturn(getTenant2());

        when(authenticationService.getTicket(TICKET_ID)).thenReturn(getTicket());

        doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                Profile profile = (Profile)invocation.getArguments()[0];
                profile.setId(new ObjectId());

                return null;
            }

        }).when(profileRepository).insert(any(Profile.class));

        when(profileRepository.findOneByQuery(String.format(ProfileServiceImpl.QUERY_FINAL_FORMAT, TENANT1_NAME,
                                                            QUERY), new String[0]))
            .thenReturn(getTenant1Profile());

        when(profileRepository.findById(PROFILE1_ID.toString(), new String[0]))
            .thenReturn(getTenant1Profile());

        when(profileRepository.findById(PROFILE1_ID.toString(), NO_ATTRIBUTE))
            .thenReturn(getTenant1ProfileNoAttributes());

        when(profileRepository.findById(PROFILE1_ID.toString(), ATTRIB_NAME_FIRST_NAME))
            .thenReturn(getTenant1ProfileNoLastName());

        when(profileRepository.findById(PROFILE2_ID.toString(), new String[0]))
            .thenReturn(getTenant2Profile());

        when(profileRepository.findByQuery(String.format(ProfileServiceImpl.QUERY_FINAL_FORMAT, TENANT1_NAME, QUERY),
                                           SORT_BY, SortOrder.ASC, START, COUNT, new String[0]))
            .thenReturn(getAllTenant1Profiles());

        when(profileRepository.findByTenantAndUsername(TENANT1_NAME, USERNAME1, new String[0]))
            .thenReturn(getTenant1Profile());

        when(profileRepository.findByIds(TENANT1_PROFILE_IDS, SORT_BY, SortOrder.ASC))
            .thenReturn(getAllTenant1Profiles());

        when(profileRepository.findRange(TENANT1_NAME, SORT_BY, SortOrder.ASC, START, COUNT))
            .thenReturn(getAllTenant1Profiles());

        when(profileRepository.findByTenantAndRole(TENANT1_NAME, ROLE1, SORT_BY, SortOrder.ASC))
            .thenReturn(getAllTenant1Profiles());

        when(profileRepository.findByTenantAndAttributeValue(TENANT1_NAME, ATTRIB_NAME_FIRST_NAME, FIRST_NAME,
                                                             SORT_BY, SortOrder.ASC))
            .thenReturn(getAllTenant1Profiles());

        when(profileRepository.countByTenant(TENANT1_NAME))
            .thenReturn(10L);

        when(profileRepository.count(String.format(ProfileServiceImpl.QUERY_FINAL_FORMAT, TENANT1_NAME, QUERY)))
            .thenReturn(1L);

        when(verificationService.createToken(any(Profile.class))).then(new Answer<VerificationToken>() {

            @Override
            public VerificationToken answer(final InvocationOnMock invocation) throws Throwable {
                Profile profile = (Profile)invocation.getArguments()[0];

                VerificationToken token = new VerificationToken();
                token.setId(VERIFICATION_TOKEN_ID1);
                token.setTenant(profile.getTenant());
                token.setProfileId(profile.getId().toString());
                token.setTimestamp(new Date());

                return token;
            }

        });

        VerificationToken token1 = new VerificationToken();
        token1.setId(VERIFICATION_TOKEN_ID1);
        token1.setTenant(TENANT1_NAME);
        token1.setProfileId(PROFILE1_ID.toString());
        token1.setTimestamp(new Date());

        VerificationToken token2 = new VerificationToken();
        token2.setId(VERIFICATION_TOKEN_ID2);
        token2.setTenant(TENANT2_NAME);
        token2.setProfileId(PROFILE2_ID.toString());
        token2.setTimestamp(new Date());

        when(verificationService.getToken(VERIFICATION_TOKEN_ID1)).thenReturn(token1);
        when(verificationService.getToken(VERIFICATION_TOKEN_ID2)).thenReturn(token2);

        profileService = new ProfileServiceImpl();
        profileService.setTenantPermissionEvaluator(tenantPermissionEvaluator);
        profileService.setAttributePermissionEvaluator(attributePermissionEvaluator);
        profileService.setProfileRepository(profileRepository);
        profileService.setTenantService(tenantService);
        profileService.setVerificationService(verificationService);
        profileService.setNewProfileEmailFromAddress(VERIFICATION_FROM_ADDRESS);
        profileService.setNewProfileEmailSubject(VERIFICATION_SUBJECT);
        profileService.setNewProfileEmailTemplateName(VERIFICATION_TEMPLATE_NAME);
        profileService.setResetPwdEmailFromAddress(RESET_PASSWORD_FROM_ADDRESS);
        profileService.setResetPwdEmailSubject(RESET_PASSWORD_SUBJECT);
        profileService.setResetPwdEmailTemplateName(RESET_PASSWORD_TEMPLATE_NAME);
        profileService.setEntitlementValidator(entitlementValidator);
    }

    @Test
    public void testCreateProfile() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setTenant(TENANT1_NAME);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());
        expected.setAttribute(ATTRIB_NAME_GENDER, GENDER);

        Profile actual = profileService.createProfile(TENANT1_NAME, USERNAME2, PASSWORD2, EMAIL2, true, ROLES2,
                                                      getAttributesWithoutPrivateAttribute(), VERIFICATION_URL);

        assertEqualProfiles(expected, actual);
        assertTrue(CryptoUtils.matchPassword(actual.getPassword(), PASSWORD2));
        assertNotNull(actual.getCreatedOn());
        assertNotNull(actual.getLastModified());

        VerificationToken token = new VerificationToken();
        token.setId(VERIFICATION_TOKEN_ID1);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(tenantService).getTenant(TENANT1_NAME);
        verify(profileRepository).insert(actual);
        verify(verificationService).createToken(actual);
        verify(verificationService).sendEmail(token, actual, VERIFICATION_URL, VERIFICATION_FROM_ADDRESS,
                                              VERIFICATION_SUBJECT, VERIFICATION_TEMPLATE_NAME);
    }

    @Test
    public void testCreateProfileWithUnwritableAttribute() throws Exception {
        try {
            profileService.createProfile(TENANT1_NAME, USERNAME2, PASSWORD2, EMAIL2, true, ROLES2, getAttributes(),
                                         VERIFICATION_URL);
            fail("Exception " + ActionDeniedException.class.getName() + " expected");
        } catch (ActionDeniedException e) {
        }
    }

    @Test
    public void testCreateProfileInvalidEmail() throws Exception {
        try {
            profileService.createProfile(TENANT1_NAME, USERNAME2, PASSWORD2, "a.com", true, ROLES2, null,
                                         VERIFICATION_URL);
            fail("Exception " + InvalidEmailAddressException.class.getName() + " expected");
        } catch (InvalidEmailAddressException e) {
        }
    }

    @Test
    public void testCreateProfileNotVerify() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setEnabled(true);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());
        expected.setAttribute(ATTRIB_NAME_GENDER, GENDER);

        Profile actual = profileService.createProfile(TENANT2_NAME, USERNAME2, PASSWORD2, EMAIL2, true, ROLES2,
                                                      getAttributesWithoutPrivateAttribute(), VERIFICATION_URL);

        assertEqualProfiles(expected, actual);
        assertTrue(CryptoUtils.matchPassword(actual.getPassword(), PASSWORD2));
        assertNotNull(actual.getCreatedOn());
        assertNotNull(actual.getLastModified());

        verify(tenantPermissionEvaluator).isAllowed(TENANT2_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(tenantService).getTenant(TENANT2_NAME);
        verify(profileRepository).insert(actual);
        verify(verificationService, never()).createToken(any(Profile.class));
        verify(verificationService, never()).sendEmail(any(VerificationToken.class), any(Profile.class), anyString(),
                                                       anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateProfile() throws Exception {
        final Profile expected = new Profile();
        expected.setId(PROFILE1_ID);
        expected.setTenant(TENANT1_NAME);
        expected.setUsername(USERNAME2);
        expected.setPassword(CryptoUtils.hashPassword(PASSWORD2));
        expected.setEmail(EMAIL2);
        expected.setRoles(ROLES2);
        expected.setVerified(true);
        expected.setEnabled(false);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());
        expected.getAttributes().put(ATTRIB_NAME_GENDER, GENDER);

        final Map<String, Object> newAttributes = Collections.<String, Object>singletonMap(ATTRIB_NAME_GENDER, GENDER);

        Profile actual = profileService.updateProfile(PROFILE1_ID.toString(), USERNAME2, PASSWORD2, EMAIL2, false,
                                                      ROLES2, newAttributes);

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 7 &&
                       param.get("username").equals(USERNAME2) &&
                       param.containsKey("password") &&
                       param.get("email").equals(EMAIL2) &&
                       param.get("roles").equals(ROLES2) &&
                       param.get("enabled").equals(false) &&
                       param.containsKey("lastModified") &&
                       param.get("attributes." + ATTRIB_NAME_GENDER).equals(GENDER);
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE1_ID.toString()), eq("{$set: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher));
    }

    @Test
    public void testUpdateProfileWithUnwritableAttribute() throws Exception {
        try {
            profileService.updateProfile(PROFILE1_ID.toString(), USERNAME2, PASSWORD2, EMAIL2, false, ROLES2,
                                         Collections.<String, Object>singletonMap(ATTRIB_NAME_PRIVATE, 0));
            fail("Exception " + ActionDeniedException.class.getName() + " expected");
        } catch (ActionDeniedException e) {
        }
    }

    @Test
    public void testUpdateProfileInvalidEmail() throws Exception {
        try {
            profileService.updateProfile(PROFILE1_ID.toString(), USERNAME2, PASSWORD2, "a.com", false, ROLES2, null);
            fail("Exception " + InvalidEmailAddressException.class.getName() + " expected");
        } catch (InvalidEmailAddressException e) {
        }
    }

    @Test
    public void testVerifyProfile() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setVerified(true);
        expected.setEnabled(true);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.verifyProfile(VERIFICATION_TOKEN_ID2);

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 3 &&
                       param.get("verified").equals(true) &&
                       param.get("enabled").equals(true) &&
                       param.containsKey("lastModified");
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT2_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE2_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE2_ID.toString()), eq("{$set: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher));
        verify(verificationService).getToken(VERIFICATION_TOKEN_ID2);
        verify(verificationService).deleteToken(VERIFICATION_TOKEN_ID2);
    }

    @Test
    public void testEnableProfile() throws Exception {
        Profile expected = getTenant2Profile();
        expected.setEnabled(true);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.enableProfile(PROFILE2_ID.toString());

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 2 &&
                       param.get("enabled").equals(true) &&
                       param.containsKey("lastModified");
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT2_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE2_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE2_ID.toString()), eq("{$set: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher));
    }

    @Test
    public void testDisableProfile() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setEnabled(false);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.disableProfile(PROFILE1_ID.toString());

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 2 &&
                       param.get("enabled").equals(false) &&
                       param.containsKey("lastModified");
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE1_ID.toString()), eq("{$set: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher));
    }

    @Test
    public void testAddRoles() throws Exception {
        Profile expected = getTenant1Profile();
        expected.getRoles().add(ROLE2);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.addRoles(PROFILE1_ID.toString(), Collections.singletonList(ROLE2));

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 1 &&
                       param.containsKey("lastModified");
            }

        };

        ArgumentMatcher<Object> pushParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 1 &&
                       param.get("roles").equals(Collections.singletonMap("$each", Collections.singletonList(ROLE2)));
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE1_ID.toString()), eq("{$set: #, $push: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher), argThat(pushParamMatcher));
    }

    @Test
    public void testRemoveRoles() throws Exception {
        Profile expected = getTenant1Profile();
        expected.getRoles().remove(ROLE1);
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.removeRoles(PROFILE1_ID.toString(), Collections.singletonList(ROLE1));

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 1 &&
                       param.containsKey("lastModified");
            }

        };

        ArgumentMatcher<Object> pullParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 1 &&
                       param.get("roles").equals(Collections.singletonMap("$in", Collections.singletonList(ROLE1)));
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE1_ID.toString()), eq("{$set: #, $pull: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher), argThat(pullParamMatcher));
    }


    @Test
    public void testGetAllAttributes() throws Exception {
        Map<String, Object> attributes = profileService.getAttributes(PROFILE1_ID.toString());

        assertNotNull(attributes);
        assertEquals(getAttributesWithoutPrivateAttribute(), attributes);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
    }

    @Test
    public void testGetOneAttribute() throws Exception {
        Map<String, Object> attributes = profileService.getAttributes(PROFILE1_ID.toString(), ATTRIB_NAME_FIRST_NAME);

        assertNotNull(attributes);
        assertEquals(1, attributes.size());
        assertEquals(FIRST_NAME, attributes.get(ATTRIB_NAME_FIRST_NAME));

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
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
        expected.setAttributes(getAttributesWithoutPrivateAttribute());
        expected.getAttributes().put(ATTRIB_NAME_GENDER, GENDER);

        Map<String, Object> newAttributes = Collections.<String, Object>singletonMap(ATTRIB_NAME_GENDER, GENDER);
        Profile actual = profileService.updateAttributes(PROFILE1_ID.toString(), newAttributes);

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 2 &&
                       param.containsKey("lastModified") &&
                       param.get("attributes." + ATTRIB_NAME_GENDER).equals(GENDER);
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE1_ID.toString()), eq("{$set: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher));
    }

    @Test
    public void testUpdateUnwritableAttribute() throws Exception {
        try {
            Map<String, Object> newAttributes = Collections.<String, Object>singletonMap(ATTRIB_NAME_PRIVATE, 0);
            profileService.updateAttributes(PROFILE1_ID.toString(), newAttributes);
            fail("Exception " + ActionDeniedException.class.getName() + " expected");
        } catch (ActionDeniedException e) {
        }
    }

    @Test
    public void testRemoveAttributes() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setAttributes(getAttributesWithoutPrivateAttribute());
        expected.getAttributes().remove(ATTRIB_NAME_LAST_NAME);

        Profile actual = profileService.removeAttributes(PROFILE1_ID.toString(), Arrays.asList(ATTRIB_NAME_LAST_NAME));

        assertEqualProfiles(expected, actual);

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 1 &&
                       param.containsKey("lastModified");
            }

        };

        ArgumentMatcher<Object> unsetParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 1 &&
                       param.get("attributes." + ATTRIB_NAME_LAST_NAME).equals("");
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE1_ID.toString()), eq("{$set: #, $unset: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher), argThat(unsetParamMatcher));
    }

    @Test
    public void testRemoveUnremovableAttribute() throws Exception {
        try {
            profileService.removeAttributes(PROFILE1_ID.toString(), Arrays.asList(ATTRIB_NAME_PRIVATE));
            fail("Exception " + ActionDeniedException.class.getName() + " expected");
        } catch (ActionDeniedException e) {
        }
    }

    @Test
    public void testDeleteProfile() throws Exception {
        profileService.deleteProfile(PROFILE1_ID.toString());

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).removeById(PROFILE1_ID.toString());
    }

    @Test
    public void testGetProfileByQuery() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.getProfileByQuery(TENANT1_NAME, QUERY);

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findOneByQuery(String.format(ProfileServiceImpl.QUERY_FINAL_FORMAT, TENANT1_NAME,
                                                               QUERY), new String[0]);
    }

    @Test
    public void testGetProfileByQueryWithTenant() throws Exception {
        try {
            profileService.getProfileByQuery(TENANT1_NAME, INVALID_QUERY1);
            fail("Expected " + InvalidQueryException.class.getSimpleName() + " exception");
        } catch (InvalidQueryException e) {
        }
    }

    @Test
    public void testGetProfileByQueryWithWhereOperator() throws Exception {
        try {
            profileService.getProfileByQuery(TENANT1_NAME, INVALID_QUERY2);
            fail("Expected " + InvalidQueryException.class.getSimpleName() + " exception");
        } catch (InvalidQueryException e) {
        }
    }

    @Test
    public void testGetProfileByQueryWithUnreadableAttribute() throws Exception {
        try {
            profileService.getProfileByQuery(TENANT1_NAME, INVALID_QUERY3);
            fail("Expected " + InvalidQueryException.class.getSimpleName() + " exception");
        } catch (InvalidQueryException e) {
        }
    }

    @Test
    public void testGetProfilesByQuery() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        for (Profile profile : expected) {
            profile.setAttributes(getAttributesWithoutPrivateAttribute());
        }

        List<Profile> actual = profileService.getProfilesByQuery(TENANT1_NAME, QUERY, SORT_BY, SortOrder.ASC, START,
                                                                 COUNT);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findByQuery(String.format(ProfileServiceImpl.QUERY_FINAL_FORMAT, TENANT1_NAME,
                                                            QUERY), SORT_BY, SortOrder.ASC, START, COUNT,
                                              new String[0]);
    }

    @Test
    public void testGetProfile() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.getProfile(PROFILE1_ID.toString());

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
    }

    @Test
    public void testGetProfileByUsername() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.getProfileByUsername(TENANT1_NAME, USERNAME1);

        assertEqualProfiles(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findByTenantAndUsername(TENANT1_NAME, USERNAME1, new String[0]);
    }

    @Test
    public void testGetProfileCount() throws Exception {
        long expected = 10L;
        long actual = profileService.getProfileCount(TENANT1_NAME);

        assertEquals(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).countByTenant(TENANT1_NAME);
    }

    @Test
    public void testGetProfileCountByQuery() throws Exception {
        long expected = 1L;
        long actual = profileService.getProfileCountByQuery(TENANT1_NAME, QUERY);

        assertEquals(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).count(String.format(ProfileServiceImpl.QUERY_FINAL_FORMAT, TENANT1_NAME, QUERY));
    }

    @Test
    public void testGetProfilesByIds() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        for (Profile profile : expected) {
            profile.setAttributes(getAttributesWithoutPrivateAttribute());
        }

        List<Profile> actual = profileService.getProfilesByIds(TENANT1_PROFILE_IDS, "username", SortOrder.ASC);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findByIds(TENANT1_PROFILE_IDS, "username", SortOrder.ASC);
    }

    @Test
    public void testGetProfileRange() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        for (Profile profile : expected) {
            profile.setAttributes(getAttributesWithoutPrivateAttribute());
        }

        List<Profile> actual = profileService.getProfileRange(TENANT1_NAME, SORT_BY, SortOrder.ASC, START, COUNT);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findRange(TENANT1_NAME, SORT_BY, SortOrder.ASC, START, COUNT);
    }

    @Test
    public void testGetProfileByRole() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        for (Profile profile : expected) {
            profile.setAttributes(getAttributesWithoutPrivateAttribute());
        }

        List<Profile> actual = profileService.getProfilesByRole(TENANT1_NAME, ROLE1, SORT_BY, SortOrder.ASC);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findByTenantAndRole(TENANT1_NAME, ROLE1, SORT_BY, SortOrder.ASC);
    }

    @Test
    public void testGetProfilesByAttribute() throws Exception {
        List<Profile> expected = getAllTenant1Profiles();
        for (Profile profile : expected) {
            profile.setAttributes(getAttributesWithoutPrivateAttribute());
        }

        List<Profile> actual = profileService.getProfilesByAttributeValue(TENANT1_NAME, ATTRIB_NAME_FIRST_NAME,
                                                                          FIRST_NAME, SORT_BY, SortOrder.ASC);

        assertEqualProfileLists(expected, actual);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findByTenantAndAttributeValue(TENANT1_NAME, ATTRIB_NAME_FIRST_NAME, FIRST_NAME,
                                                                SORT_BY, SortOrder.ASC);
    }

    @Test
    public void testResetPassword() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.resetPassword(PROFILE1_ID.toString(), RESET_PASSWORD_URL);

        assertEqualProfiles(expected, actual);

        VerificationToken token = new VerificationToken();
        token.setId(VERIFICATION_TOKEN_ID1);

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(verificationService).createToken(actual);
        verify(verificationService).sendEmail(token, actual, RESET_PASSWORD_URL, RESET_PASSWORD_FROM_ADDRESS,
                                              RESET_PASSWORD_SUBJECT, RESET_PASSWORD_TEMPLATE_NAME);
    }

    @Test
    public void testChangePassword() throws Exception {
        Profile expected = getTenant1Profile();
        expected.setAttributes(getAttributesWithoutPrivateAttribute());

        Profile actual = profileService.changePassword(VERIFICATION_TOKEN_ID1, PASSWORD2);

        assertEqualProfiles(expected, actual);
        assertTrue(CryptoUtils.matchPassword(actual.getPassword(), PASSWORD2));

        ArgumentMatcher<Object> setParamMatcher = new ArgumentMatcher<Object>() {

            @Override
            public boolean matches(Object argument) {
                Map<String, Object> param = (Map<String, Object>)argument;

                return param.size() == 2 &&
                       param.containsKey("password") &&
                       param.containsKey("lastModified");
            }

        };

        verify(tenantPermissionEvaluator).isAllowed(TENANT1_NAME, TenantAction.MANAGE_PROFILES.toString());
        verify(profileRepository).findById(PROFILE1_ID.toString(), new String[0]);
        verify(profileRepository).update(eq(PROFILE1_ID.toString()), eq("{$set: #}"), eq(false), eq(false),
                                         argThat(setParamMatcher));
        verify(verificationService).getToken(VERIFICATION_TOKEN_ID1);
        verify(verificationService).deleteToken(VERIFICATION_TOKEN_ID1);
    }

    @Test
    public void testCreateVerificationToken() throws Exception {
        VerificationToken token = profileService.createVerificationToken(PROFILE1_ID.toString());

        assertNotNull(token);
        assertEquals(VERIFICATION_TOKEN_ID1, token.getId());
        assertEquals(TENANT1_NAME, token.getTenant());
        assertEquals(PROFILE1_ID.toString(), token.getProfileId());
        assertNotNull(token.getTimestamp());

        verify(verificationService).createToken(getTenant1Profile());
    }

    @Test
    public void testGetVerificationToken() throws Exception {
        VerificationToken token = profileService.getVerificationToken(VERIFICATION_TOKEN_ID1);

        assertNotNull(token);
        assertEquals(VERIFICATION_TOKEN_ID1, token.getId());
        assertEquals(TENANT1_NAME, token.getTenant());
        assertEquals(PROFILE1_ID.toString(), token.getProfileId());
        assertNotNull(token.getTimestamp());

        verify(verificationService).getToken(VERIFICATION_TOKEN_ID1);
    }

    @Test
    public void deleteVerificationToken() throws Exception {
        profileService.deleteVerificationToken(VERIFICATION_TOKEN_ID1);

        verify(verificationService).deleteToken(VERIFICATION_TOKEN_ID1);
    }

    private Tenant getTenant1() {
        Tenant tenant = new Tenant();
        tenant.setName(TENANT1_NAME);
        tenant.setVerifyNewProfiles(true);
        tenant.setAttributeDefinitions(getAttributeDefinitions());

        return tenant;
    }

    private Tenant getTenant2() {
        Tenant tenant = new Tenant();
        tenant.setName(TENANT2_NAME);
        tenant.setVerifyNewProfiles(false);
        tenant.setAttributeDefinitions(getAttributeDefinitions());

        return tenant;
    }

    private List<AttributeDefinition> getAttributeDefinitions() {
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
        genderDefinition.setDefaultValue(GENDER);

        AttributeDefinition privateDefinition = new AttributeDefinition();
        privateDefinition.setName(ATTRIB_NAME_PRIVATE);

        return Arrays.asList(firstNameDefinition, lastNameDefinition, genderDefinition, privateDefinition);
    }

    private Profile getTenant1Profile() {
        Profile profile = new Profile();
        profile.setId(PROFILE1_ID);
        profile.setTenant(TENANT1_NAME);
        profile.setUsername(USERNAME1);
        profile.setPassword(CryptoUtils.hashPassword(PASSWORD1));
        profile.setEmail(EMAIL1);
        profile.setRoles(new HashSet<>(ROLES1));
        profile.setVerified(true);
        profile.setEnabled(true);
        profile.setAttributes(getAttributes());

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
        profile.setPassword(CryptoUtils.hashPassword(PASSWORD2));
        profile.setEmail(EMAIL2);
        profile.setRoles(new HashSet<>(ROLES2));
        profile.setVerified(false);
        profile.setEnabled(false);
        profile.setAttributes(getAttributes());

        return profile;
    }

    private Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<>(2);
        attributes.put(ATTRIB_NAME_FIRST_NAME, FIRST_NAME);
        attributes.put(ATTRIB_NAME_LAST_NAME, LAST_NAME);
        attributes.put(ATTRIB_NAME_PRIVATE, Collections.singletonMap("sub", "test"));

        return attributes;
    }

    private Map<String, Object> getAttributesWithoutPrivateAttribute() {
        Map<String, Object> attributes = new LinkedHashMap<>(2);
        attributes.put(ATTRIB_NAME_FIRST_NAME, FIRST_NAME);
        attributes.put(ATTRIB_NAME_LAST_NAME, LAST_NAME);

        return attributes;
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

}
