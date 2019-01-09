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
package org.craftercms.profile.services;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.craftercms.commons.collections.SetUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.craftercms.profile.services.impl.SingleAccessTokenIdResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.craftercms.profile.api.ProfileConstants.BASE_URL_PROFILE;
import static org.craftercms.profile.api.ProfileConstants.NO_ATTRIBUTE;
import static org.craftercms.profile.api.ProfileConstants.URL_PROFILE_CHANGE_PASSWORD;
import static org.craftercms.profile.api.ProfileConstants.URL_PROFILE_VERIFY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration tests for {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:crafter/profile/extension/client-context.xml")
public class ProfileServiceIT {

    private static final String VERIFICATION_EMAIL_REGEX = ".+<a id=\"verificationLink\" href=\".+\\?tokenId=(.+)\">.+";

    private static final String ADMIN_CONSOLE_ACCESS_TOKEN_ID = "e8f5170c-877b-416f-b70f-4b09772f8e2d";
    private static final String RANDOM_APP_ACCESS_TOKEN_ID = "f91cdaf0-e5c6-11e3-ac10-0800200c9a66";

    private static final String DEFAULT_TENANT = "default";

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final Set<String> ADMIN_ROLES = new HashSet<>(Arrays.asList("PROFILE_SUPERADMIN", "SOCIAL_ADMIN"));

    private static final String JDOE_USERNAME = "jdoe";
    private static final String JDOE_EMAIL = "john.doe@example.com";
    private static final Set<String> JDOE_ROLES = new HashSet<>(Arrays.asList("SOCIAL_ADMIN"));
    private static final String JDOE_FIRST_NAME = "John";
    private static final String JDOE_LAST_NAME = "Doe";
    private static final String JDOE_SUBSCRIPTIONS_FREQUENCY = "instant";
    private static final boolean JDOE_SUBSCRIPTIONS_AUTO_WATCH = true;
    private static final List<String> JDOE_SUBSCRIPTIONS_TARGETS = Arrays.asList("news");

    private static final String AVASQUEZ_USERNAME = "avasquez";
    private static final String AVASQUEZ_PASSWORD1 = "1234";
    private static final String AVASQUEZ_PASSWORD2 = "4321";
    private static final String AVASQUEZ_EMAIL1 = "alfonso.vasquez@example.com";
    private static final String AVASQUEZ_EMAIL2 = "avasquez@example.com";
    private static final Set<String> AVASQUEZ_ROLES1 = SetUtils.asSet("PROFILE_SUPERADMIN", "SOCIAL_MODERATOR");
    private static final Set<String> AVASQUEZ_ROLES2 = SetUtils.asSet("SOCIAL_AUTHOR");
    private static final String AVASQUEZ_FIRST_NAME = "Alfonso";
    private static final String AVASQUEZ_LAST_NAME = "Vasquez";

    private static final String VERIFICATION_URL = "http://localhost:8983/crafter-profile" + BASE_URL_PROFILE +
                                                   URL_PROFILE_VERIFY;
    private static final String RESET_PASSWORD_URL = "http://localhost:8983/crafter-profile" + BASE_URL_PROFILE +
                                                     URL_PROFILE_CHANGE_PASSWORD;

    private static final String QUERY1 = "{email: 'admin@example.com'}";
    private static final String QUERY2 = "{attributes.subscriptions.targets: 'news'}";
    private static final String QUERY3 = "{roles: 'SOCIAL_ADMIN'}";
    private static final String INVALID_QUERY1 = "{tenant: 'default'}";
    private static final String INVALID_QUERY2 = "{$where: \"this.email == 'admin@example.com'\"}";

    @Autowired
    private ProfileService profileService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SingleAccessTokenIdResolver accessTokenIdResolver;

    @Test
    public void testCreateProfile() throws Exception {
        Map<String, Object> attributes = new LinkedHashMap<>(2);
        attributes.put("firstName", AVASQUEZ_FIRST_NAME);
        attributes.put("lastName", AVASQUEZ_LAST_NAME);

        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, true, AVASQUEZ_ROLES1, attributes,
                                                       VERIFICATION_URL);
        try {
            assertNotNull(profile);
            assertNotNull(profile.getId());
            assertEquals(AVASQUEZ_USERNAME, profile.getUsername());
            assertNull(profile.getPassword());
            assertEquals(AVASQUEZ_EMAIL1, profile.getEmail());
            assertFalse(profile.isVerified());
            assertTrue(profile.isEnabled());
            assertNotNull(profile.getCreatedOn());
            assertNotNull(profile.getLastModified());
            assertEquals(DEFAULT_TENANT, profile.getTenant());
            assertEquals(AVASQUEZ_ROLES1, profile.getRoles());
            assertNotNull(profile.getAttributes());
            assertEquals(attributes, profile.getAttributes());
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    public void testCreateAndVerifyProfile() throws Exception {
        GreenMail mailServer = new GreenMail(ServerSetupTest.SMTP);
        mailServer.start();

        tenantService.verifyNewProfiles(DEFAULT_TENANT, true);

        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, true, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        try {
            assertNotNull(profile);
            assertNotNull(profile.getId());
            assertEquals(AVASQUEZ_USERNAME, profile.getUsername());
            assertNull(profile.getPassword());
            assertEquals(AVASQUEZ_EMAIL1, profile.getEmail());
            assertFalse(profile.isVerified());
            assertFalse(profile.isEnabled());
            assertNotNull(profile.getCreatedOn());
            assertNotNull(profile.getLastModified());
            assertEquals(DEFAULT_TENANT, profile.getTenant());
            assertEquals(AVASQUEZ_ROLES1, profile.getRoles());
            assertNotNull(profile.getAttributes());
            assertEquals(0, profile.getAttributes().size());

            // Wait a few seconds so that the email can be sent
            Thread.sleep(3000);

            String email = GreenMailUtil.getBody(mailServer.getReceivedMessages()[0]);

            assertNotNull(email);

            Pattern emailPattern = Pattern.compile(VERIFICATION_EMAIL_REGEX, Pattern.DOTALL);
            Matcher emailMatcher = emailPattern.matcher(email);

            assertTrue(emailMatcher.matches());

            String verificationTokenId = emailMatcher.group(1);

            Profile verifiedProfile = profileService.verifyProfile(verificationTokenId);

            assertNotNull(verifiedProfile);
            assertEquals(profile.getId(), verifiedProfile.getId());
            assertTrue(verifiedProfile.isEnabled());
            assertTrue(verifiedProfile.isVerified());
        } finally {
            profileService.deleteProfile(profile.getId().toString());

            tenantService.verifyNewProfiles(DEFAULT_TENANT, false);

            mailServer.stop();
        }
    }

    @Test
    public void testUpdateProfile() throws Exception {
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, true, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        try {
            assertNotNull(profile);

            Map<String, Object> attributes = new LinkedHashMap<>(2);
            attributes.put("firstName", AVASQUEZ_FIRST_NAME);
            attributes.put("lastName", AVASQUEZ_LAST_NAME);

            Profile updatedProfile = profileService.updateProfile(profile.getId().toString(), AVASQUEZ_USERNAME,
                                                                  AVASQUEZ_PASSWORD2, AVASQUEZ_EMAIL2, false,
                                                                  AVASQUEZ_ROLES2, attributes);

            assertNotNull(updatedProfile);
            assertEquals(profile.getId(), updatedProfile.getId());
            assertEquals(profile.getUsername(), updatedProfile.getUsername());
            assertNull(updatedProfile.getPassword());
            assertEquals(AVASQUEZ_EMAIL2, updatedProfile.getEmail());
            assertEquals(profile.isVerified(), updatedProfile.isVerified());
            assertFalse(updatedProfile.isEnabled());
            assertEquals(profile.getCreatedOn(), updatedProfile.getCreatedOn());
            assertTrue(profile.getLastModified().before(updatedProfile.getLastModified()));
            assertEquals(profile.getTenant(), updatedProfile.getTenant());
            assertEquals(AVASQUEZ_ROLES2, updatedProfile.getRoles());
            assertEquals(attributes, updatedProfile.getAttributes());
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    public void testEnableProfile() throws Exception {
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, false, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        try {
            assertNotNull(profile);
            assertFalse(profile.isEnabled());

            Profile updatedProfile = profileService.enableProfile(profile.getId().toString());

            assertNotNull(updatedProfile);
            assertEquals(profile.getId(), updatedProfile.getId());
            assertEquals(profile.getUsername(), updatedProfile.getUsername());
            assertNull(updatedProfile.getPassword());
            assertEquals(profile.getEmail(), updatedProfile.getEmail());
            assertEquals(profile.isVerified(), updatedProfile.isVerified());
            assertTrue(updatedProfile.isEnabled());
            assertEquals(profile.getCreatedOn(), updatedProfile.getCreatedOn());
            assertTrue(profile.getLastModified().before(updatedProfile.getLastModified()));
            assertEquals(profile.getTenant(), updatedProfile.getTenant());
            assertEquals(profile.getRoles(), updatedProfile.getRoles());
            assertEquals(profile.getAttributes(), updatedProfile.getAttributes());
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    public void testDisableProfile() throws Exception {
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, true, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        try {
            assertNotNull(profile);
            assertTrue(profile.isEnabled());

            Profile updatedProfile = profileService.disableProfile(profile.getId().toString());

            assertNotNull(updatedProfile);
            assertEquals(profile.getId(), updatedProfile.getId());
            assertEquals(profile.getUsername(), updatedProfile.getUsername());
            assertNull(updatedProfile.getPassword());
            assertEquals(profile.getEmail(), updatedProfile.getEmail());
            assertEquals(profile.isVerified(), updatedProfile.isVerified());
            assertFalse(updatedProfile.isEnabled());
            assertEquals(profile.getCreatedOn(), updatedProfile.getCreatedOn());
            assertTrue(profile.getLastModified().before(updatedProfile.getLastModified()));
            assertEquals(profile.getTenant(), updatedProfile.getTenant());
            assertEquals(profile.getRoles(), updatedProfile.getRoles());
            assertEquals(profile.getAttributes(), updatedProfile.getAttributes());
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    public void testAddRoles() throws Exception {
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, false, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        try {
            assertNotNull(profile);
            assertEquals(AVASQUEZ_ROLES1, profile.getRoles());

            Profile updatedProfile = profileService.addRoles(profile.getId().toString(),
                                                             Arrays.asList("SOCIAL_AUTHOR"));

            Set<String> expectedRoles = new HashSet<>(AVASQUEZ_ROLES1);
            expectedRoles.add("SOCIAL_AUTHOR");

            assertNotNull(updatedProfile);
            assertEquals(profile.getId(), updatedProfile.getId());
            assertEquals(profile.getUsername(), updatedProfile.getUsername());
            assertNull(updatedProfile.getPassword());
            assertEquals(profile.getEmail(), updatedProfile.getEmail());
            assertEquals(profile.isVerified(), updatedProfile.isVerified());
            assertEquals(profile.isEnabled(), updatedProfile.isEnabled());
            assertEquals(profile.getCreatedOn(), updatedProfile.getCreatedOn());
            assertTrue(profile.getLastModified().before(updatedProfile.getLastModified()));
            assertEquals(profile.getTenant(), updatedProfile.getTenant());
            assertEquals(expectedRoles, updatedProfile.getRoles());
            assertEquals(profile.getAttributes(), updatedProfile.getAttributes());
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    public void testRemoveRoles() throws Exception {
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, false, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        try {
            assertNotNull(profile);
            assertEquals(AVASQUEZ_ROLES1, profile.getRoles());

            Profile updatedProfile = profileService.removeRoles(profile.getId().toString(), Arrays.asList
                ("SOCIAL_MODERATOR"));

            Set<String> expectedRoles = new HashSet<>(AVASQUEZ_ROLES1);
            expectedRoles.remove("SOCIAL_MODERATOR");

            assertNotNull(updatedProfile);
            assertEquals(profile.getId(), updatedProfile.getId());
            assertEquals(profile.getUsername(), updatedProfile.getUsername());
            assertNull(updatedProfile.getPassword());
            assertEquals(profile.getEmail(), updatedProfile.getEmail());
            assertEquals(profile.isVerified(), updatedProfile.isVerified());
            assertEquals(profile.isEnabled(), updatedProfile.isEnabled());
            assertEquals(profile.getCreatedOn(), updatedProfile.getCreatedOn());
            assertTrue(profile.getLastModified().before(updatedProfile.getLastModified()));
            assertEquals(profile.getTenant(), updatedProfile.getTenant());
            assertEquals(expectedRoles, updatedProfile.getRoles());
            assertEquals(profile.getAttributes(), updatedProfile.getAttributes());
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    @DirtiesContext
    public void testGetAllAttributes() throws Exception {
        // Get all attributes
        ObjectId profileId = profileService.getProfileByUsername(DEFAULT_TENANT, JDOE_USERNAME).getId();
        Map<String, Object> attributes = profileService.getAttributes(profileId.toString());

        assertNotNull(attributes);
        assertEquals(3, attributes.size());
        assertEquals(JDOE_FIRST_NAME, attributes.get("firstName"));
        assertEquals(JDOE_LAST_NAME, attributes.get("lastName"));

        Map<String, Object> subscriptions = (Map<String, Object>)attributes.get("subscriptions");

        assertNotNull(subscriptions);
        assertEquals(3, subscriptions.size());
        assertEquals(JDOE_SUBSCRIPTIONS_FREQUENCY, subscriptions.get("frequency"));
        assertEquals(JDOE_SUBSCRIPTIONS_AUTO_WATCH, subscriptions.get("autoWatch"));
        assertEquals(JDOE_SUBSCRIPTIONS_TARGETS, subscriptions.get("targets"));

        accessTokenIdResolver.setAccessTokenId(RANDOM_APP_ACCESS_TOKEN_ID);

        // Get only allowed attributes
        attributes = profileService.getAttributes(profileId.toString());

        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals(JDOE_FIRST_NAME, attributes.get("firstName"));
        assertEquals(JDOE_LAST_NAME, attributes.get("lastName"));

        // Get a specific attribute
        attributes = profileService.getAttributes(profileId.toString(), "firstName");

        assertNotNull(attributes);
        assertEquals(1, attributes.size());
        assertEquals(JDOE_FIRST_NAME, attributes.get("firstName"));

        // Get no attributes
        attributes = profileService.getAttributes(profileId.toString(), NO_ATTRIBUTE);

        assertNotNull(attributes);
        assertEquals(0, attributes.size());
    }

    @Test
    @DirtiesContext
    public void testUpdateAttributes() throws Exception {
        // Update a bunch attributes
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, false, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        Map<String, Object> attributes = new HashMap<>();
        try {
            Map<String, Object> subscriptions = new HashMap<>();
            subscriptions.put("frequency", JDOE_SUBSCRIPTIONS_FREQUENCY);
            subscriptions.put("autoWatch", JDOE_SUBSCRIPTIONS_AUTO_WATCH);
            subscriptions.put("targets", JDOE_SUBSCRIPTIONS_TARGETS);

            attributes.put("subscriptions", subscriptions);

            profile = profileService.updateAttributes(profile.getId().toString(), attributes);
            attributes = profile.getAttributes();

            assertNotNull(attributes);
            assertEquals(1, attributes.size());

            subscriptions = (Map<String, Object>)attributes.get("subscriptions");

            assertNotNull(subscriptions);
            assertEquals(3, subscriptions.size());
            assertEquals(JDOE_SUBSCRIPTIONS_FREQUENCY, subscriptions.get("frequency"));
            assertEquals(JDOE_SUBSCRIPTIONS_AUTO_WATCH, subscriptions.get("autoWatch"));
            assertEquals(JDOE_SUBSCRIPTIONS_TARGETS, subscriptions.get("targets"));

            accessTokenIdResolver.setAccessTokenId(RANDOM_APP_ACCESS_TOKEN_ID);

            // Unallowed updates should be rejected
            try {
                profileService.updateAttributes(profile.getId().toString(), attributes);
                fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
            } catch (ProfileRestServiceException e) {
                assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
                assertEquals(ErrorCode.ACTION_DENIED, e.getErrorCode());
            }
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    @DirtiesContext
    public void testDeleteAttributes() throws Exception {
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, false, AVASQUEZ_ROLES1, null, VERIFICATION_URL);
        Map<String, Object> attributes = new HashMap<>();
        try {
            Map<String, Object> subscriptions = new HashMap<>();
            subscriptions.put("frequency", JDOE_SUBSCRIPTIONS_FREQUENCY);
            subscriptions.put("autoWatch", JDOE_SUBSCRIPTIONS_AUTO_WATCH);
            subscriptions.put("targets", JDOE_SUBSCRIPTIONS_TARGETS);

            attributes.put("subscriptions", subscriptions);

            profileService.updateAttributes(profile.getId().toString(), attributes);

            accessTokenIdResolver.setAccessTokenId(RANDOM_APP_ACCESS_TOKEN_ID);

            // Unallowed deletes should be rejected
            try {
                profileService.removeAttributes(profile.getId().toString(), Arrays.asList("subscriptions"));
                fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
            } catch (ProfileRestServiceException e) {
                assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
                assertEquals(ErrorCode.ACTION_DENIED, e.getErrorCode());
            }

            accessTokenIdResolver.setAccessTokenId(ADMIN_CONSOLE_ACCESS_TOKEN_ID);

            // Delete an attribute
            profile = profileService.removeAttributes(profile.getId().toString(), Arrays.asList("subscriptions"));
            attributes = profile.getAttributes();

            assertNotNull(attributes);
            assertEquals(0, attributes.size());
        } finally {
            profileService.deleteProfile(profile.getId().toString());
        }
    }

    @Test
    public void testDeleteProfile() throws Exception {
        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, false, AVASQUEZ_ROLES1, null, VERIFICATION_URL);

        assertNotNull(profile);

        profileService.deleteProfile(profile.getId().toString());

        profile = profileService.getProfile(profile.getId().toString());

        assertNull(profile);
    }

    @Test
    @DirtiesContext
    public void testGetProfileByQuery() throws Exception {
        Profile profile = profileService.getProfileByQuery(DEFAULT_TENANT, QUERY1);

        assertAdminProfile(profile);

        // Try with tenant field in query
        try {
            profileService.getProfileByQuery(DEFAULT_TENANT, INVALID_QUERY1);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }

        // Try with $where operator in query
        try {
            profileService.getProfileByQuery(DEFAULT_TENANT, INVALID_QUERY2);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }

        accessTokenIdResolver.setAccessTokenId(RANDOM_APP_ACCESS_TOKEN_ID);

        // Try with unreadable attribute in query
        try {
            profileService.getProfileByQuery(DEFAULT_TENANT, QUERY2);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }
    }

    @Test
    public void testGetProfile() throws Exception {
        ObjectId profileId = profileService.getProfileByUsername(DEFAULT_TENANT, ADMIN_USERNAME).getId();
        Profile profile = profileService.getProfile(profileId.toString());

        assertAdminProfile(profile);

        // Try with unknown profile ID
        profileId = ObjectId.get();
        profile = profileService.getProfile(profileId.toString());

        assertNull(profile);
    }

    @Test
    public void testGetProfileByUsername() throws Exception {
        Profile profile = profileService.getProfileByUsername(DEFAULT_TENANT, ADMIN_USERNAME);

        assertAdminProfile(profile);

        // Try with unknown username
        profile = profileService.getProfileByUsername(DEFAULT_TENANT, "unknown");

        assertNull(profile);
    }

    @Test(expected = ProfileRestServiceException.class)
    public void testGetProfileByTicket() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT, ADMIN_USERNAME, ADMIN_PASSWORD);
        assertNotNull(ticket);

        Profile profile = profileService.getProfileByTicket(ticket.getId());
        assertAdminProfile(profile);

        authenticationService.invalidateTicket(ticket.getId());

        // Try with invalid ticket
        profileService.getProfileByTicket("507c7f79bcf86cd7994f6c0e");
    }

    @Test
    public void testGetProfileCount() throws Exception {
        long count = profileService.getProfileCount(DEFAULT_TENANT);

        assertEquals(2, count);
    }

    @Test
    @DirtiesContext
    public void testGetProfileCountByQuery() throws Exception {
        long count = profileService.getProfileCountByQuery(DEFAULT_TENANT, QUERY2);

        assertEquals(1, count);

        // Try with tenant field in query
        try {
            profileService.getProfileCountByQuery(DEFAULT_TENANT, INVALID_QUERY1);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }

        // Try with $where operator in query
        try {
            profileService.getProfileCountByQuery(DEFAULT_TENANT, INVALID_QUERY2);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }

        accessTokenIdResolver.setAccessTokenId(RANDOM_APP_ACCESS_TOKEN_ID);

        // Try with unreadable attribute in query
        try {
            profileService.getProfileCountByQuery(DEFAULT_TENANT, QUERY2);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }
    }

    @Test
    @DirtiesContext
    public void testGetProfilesByQuery() throws Exception {
        List<Profile> profiles = profileService.getProfilesByQuery(DEFAULT_TENANT, QUERY2, null, null, null, null);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertJdoeProfile(profiles.get(0));

        // With sort and start/limit
        profiles = profileService.getProfilesByQuery(DEFAULT_TENANT, QUERY3, "username", SortOrder.DESC, 1, 1);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertAdminProfile(profiles.get(0));

        // Try with tenant field in query
        try {
            profileService.getProfilesByQuery(DEFAULT_TENANT, INVALID_QUERY1, null, null, null, null);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }

        // Try with $where operator in query
        try {
            profileService.getProfilesByQuery(DEFAULT_TENANT, INVALID_QUERY2, null, null, null, null);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }

        accessTokenIdResolver.setAccessTokenId(RANDOM_APP_ACCESS_TOKEN_ID);

        // Try with unreadable attribute in query
        try {
            profileService.getProfilesByQuery(DEFAULT_TENANT, QUERY2, null, null, null, null);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals(ErrorCode.INVALID_QUERY, e.getErrorCode());
        }
    }

    @Test
    public void testGetProfileByIds() throws Exception {
        ObjectId adminProfileId = profileService.getProfileByUsername(DEFAULT_TENANT, ADMIN_USERNAME).getId();
        ObjectId jdoeProfileId = profileService.getProfileByUsername(DEFAULT_TENANT, JDOE_USERNAME).getId();

        List<Profile> profiles = profileService.getProfilesByIds(Arrays.asList(adminProfileId.toString(),
                                                                               jdoeProfileId.toString()), null, null);

        assertNotNull(profiles);
        assertEquals(2, profiles.size());
        assertAdminProfile(profiles.get(0));
        assertJdoeProfile(profiles.get(1));

        // With sort
        profiles = profileService.getProfilesByIds(Arrays.asList(adminProfileId.toString(), jdoeProfileId.toString()),
                                                   "username", SortOrder.DESC);

        assertNotNull(profiles);
        assertEquals(2, profiles.size());
        assertJdoeProfile(profiles.get(0));
        assertAdminProfile(profiles.get(1));
    }

    @Test
    public void testGetProfileRange() throws Exception {
        List<Profile> profiles = profileService.getProfileRange(DEFAULT_TENANT, null, null, 1, 1);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertJdoeProfile(profiles.get(0));

        // With sort
        profiles = profileService.getProfileRange(DEFAULT_TENANT, "username", SortOrder.DESC, 1, 1);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertAdminProfile(profiles.get(0));
    }

    @Test
    public void testGetProfileByRole() throws Exception {
        List<Profile> profiles = profileService.getProfilesByRole(DEFAULT_TENANT, "SOCIAL_ADMIN", null, null);

        assertNotNull(profiles);
        assertEquals(2, profiles.size());
        assertAdminProfile(profiles.get(0));
        assertJdoeProfile(profiles.get(1));

        // With sort
        profiles = profileService.getProfilesByRole(DEFAULT_TENANT, "SOCIAL_ADMIN", "username", SortOrder.DESC);

        assertNotNull(profiles);
        assertEquals(2, profiles.size());
        assertJdoeProfile(profiles.get(0));
        assertAdminProfile(profiles.get(1));
    }

    @Test
    public void testGetProfileByExistingAttribute() throws Exception {
        List<Profile> profiles = profileService.getProfilesByExistingAttribute(DEFAULT_TENANT, "subscriptions." +
                                                                                               "frequency", null, null);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertJdoeProfile(profiles.get(0));

        // With sort
        profiles = profileService.getProfilesByExistingAttribute(DEFAULT_TENANT, "subscriptions.frequency",
                                                                 "username", SortOrder.DESC);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertJdoeProfile(profiles.get(0));
    }

    @Test
    public void testGetProfileByAttributeValue() throws Exception {
        List<Profile> profiles = profileService.getProfilesByAttributeValue(DEFAULT_TENANT, "subscriptions." +
                                                                                            "frequency", "instant",
                                                                            null, null);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertJdoeProfile(profiles.get(0));

        // With sort
        profiles = profileService.getProfilesByAttributeValue(DEFAULT_TENANT, "subscriptions.frequency", "instant",
                                                              "username", SortOrder.DESC);

        assertNotNull(profiles);
        assertEquals(1, profiles.size());
        assertJdoeProfile(profiles.get(0));
    }

    @Test
    public void testResetAndChangePassword() throws Exception {
        GreenMail mailServer = new GreenMail(ServerSetupTest.SMTP);
        mailServer.start();

        Profile profile = profileService.createProfile(DEFAULT_TENANT, AVASQUEZ_USERNAME, AVASQUEZ_PASSWORD1,
                                                       AVASQUEZ_EMAIL1, true, AVASQUEZ_ROLES1, null, VERIFICATION_URL);

        try {
            profile = profileService.resetPassword(profile.getId().toString(), RESET_PASSWORD_URL);

            assertNotNull(profile);

            // Wait a few seconds so that the email can be sent
            Thread.sleep(3000);

            String email = GreenMailUtil.getBody(mailServer.getReceivedMessages()[0]);

            assertNotNull(email);

            Pattern emailPattern = Pattern.compile(VERIFICATION_EMAIL_REGEX, Pattern.DOTALL);
            Matcher emailMatcher = emailPattern.matcher(email);

            assertTrue(emailMatcher.matches());

            String resetTokenId = emailMatcher.group(1);

            Profile profileAfterPswdReset = profileService.changePassword(resetTokenId, AVASQUEZ_PASSWORD2);

            assertNotNull(profileAfterPswdReset);
            assertEquals(profile.getId(), profileAfterPswdReset.getId());
            assertNull(profileAfterPswdReset.getPassword());
        } finally {
            profileService.deleteProfile(profile.getId().toString());

            mailServer.stop();
        }
    }

    @Test
    public void testCreateVerificationToken() throws Exception {
        Profile profile = profileService.getProfileByUsername(DEFAULT_TENANT, ADMIN_USERNAME);
        String profileId = profile.getId().toString();
        VerificationToken token = profileService.createVerificationToken(profileId);

        assertNotNull(token);

        try {
            assertNotNull(token.getId());
            assertEquals(profile.getTenant(), token.getTenant());
            assertEquals(profileId, token.getProfileId());
            assertNotNull(token.getTimestamp());
        } finally {
            profileService.deleteVerificationToken(token.getId());
        }
    }

    @Test
    public void testGetVerificationToken() throws Exception {
        Profile profile = profileService.getProfileByUsername(DEFAULT_TENANT, ADMIN_USERNAME);
        String profileId = profile.getId().toString();
        VerificationToken originalToken = profileService.createVerificationToken(profileId);

        assertNotNull(originalToken);

        try {
            VerificationToken token = profileService.getVerificationToken(originalToken.getId());

            assertNotNull(token);
            assertEquals(originalToken.getId(), token.getId());
            assertEquals(originalToken.getTenant(), token.getTenant());
            assertEquals(originalToken.getProfileId(), token.getProfileId());
            assertEquals(originalToken.getTimestamp(), token.getTimestamp());
        } finally {
            profileService.deleteVerificationToken(originalToken.getId());
        }
    }

    @Test
    public void testDeleteVerificationToken() throws Exception {
        Profile profile = profileService.getProfileByUsername(DEFAULT_TENANT, ADMIN_USERNAME);
        String profileId = profile.getId().toString();
        VerificationToken token = profileService.createVerificationToken(profileId);

        assertNotNull(token);

        profileService.deleteVerificationToken(token.getId());

        token = profileService.getVerificationToken(token.getId());

        assertNull(token);
    }

    private void assertAdminProfile(Profile profile) {
        assertNotNull(profile);
        assertEquals(ADMIN_USERNAME, profile.getUsername());
        assertNull(profile.getPassword());
        assertEquals(ADMIN_EMAIL, profile.getEmail());
        assertFalse(profile.isVerified());
        assertTrue(profile.isEnabled());
        assertNotNull(profile.getCreatedOn());
        assertNotNull(profile.getLastModified());
        assertEquals(DEFAULT_TENANT, profile.getTenant());
        assertEquals(ADMIN_ROLES, profile.getRoles());
        assertNotNull(profile.getAttributes());
        assertEquals(0, profile.getAttributes().size());
    }

    private void assertJdoeProfile(Profile profile) {
        assertNotNull(profile);
        assertEquals(JDOE_USERNAME, profile.getUsername());
        assertNull(profile.getPassword());
        assertEquals(JDOE_EMAIL, profile.getEmail());
        assertFalse(profile.isVerified());
        assertFalse(profile.isEnabled());
        assertNotNull(profile.getCreatedOn());
        assertNotNull(profile.getLastModified());
        assertEquals(DEFAULT_TENANT, profile.getTenant());
        assertEquals(JDOE_ROLES, profile.getRoles());

        Map<String, Object> attributes = profile.getAttributes();

        assertNotNull(attributes);
        assertEquals(3, attributes.size());
        assertEquals(JDOE_FIRST_NAME, attributes.get("firstName"));
        assertEquals(JDOE_LAST_NAME, attributes.get("lastName"));

        Map<String, Object> subscriptions = (Map<String, Object>)attributes.get("subscriptions");
        assertNotNull(subscriptions);
        assertEquals(3, subscriptions.size());
        assertEquals(JDOE_SUBSCRIPTIONS_FREQUENCY, subscriptions.get("frequency"));
        assertEquals(JDOE_SUBSCRIPTIONS_AUTO_WATCH, subscriptions.get("autoWatch"));
        assertEquals(JDOE_SUBSCRIPTIONS_TARGETS, subscriptions.get("targets"));
    }

}
