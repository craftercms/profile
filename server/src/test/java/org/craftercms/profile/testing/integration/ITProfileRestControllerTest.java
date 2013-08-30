/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.profile.testing.integration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.testing.BaseTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ITProfileRestControllerTest extends BaseTest {

    @Test
    public void testGetProfileCount() throws AppAuthenticationFailedException {
        initAppToken();
        long count = profileRestClientImpl.getProfileCount(appToken, tenantName);
        assertTrue(count > 0);
    }

    @Test
    public void testGetProfileById() throws AppAuthenticationFailedException {
        initAppToken();
        Profile profile = profileRestClientImpl.getProfileByUsername(appToken, "admin", tenantName);
        assertNotNull(profile);
        Profile expectedProfile = profileRestClientImpl.getProfile(appToken, profile.getId().toString());
        assertEquals(profile.getUserName(), expectedProfile.getUserName());
    }

    @Test
    public void testGetProfileByIdWithAttributes() throws AppAuthenticationFailedException {
        initAppToken();
        Profile profile = profileRestClientImpl.getProfileByUsername(appToken, "admin", tenantName);
        assertNotNull(profile);
        List<String> attributes = new ArrayList<String>();
        attributes.add("first-name");
        Profile expectedProfile = profileRestClientImpl.getProfileWithAttributes(appToken,
            profile.getId().toString(), attributes);
        assertNotNull(expectedProfile);
        assertEquals(profile.getUserName(), expectedProfile.getUserName());
    }

    @Test
    public void testGetProfileByIdWithAllAttributes() throws AppAuthenticationFailedException {
        initAppToken();
        Profile profile = profileRestClientImpl.getProfileByUsername(appToken, "admin", tenantName);
        assertNotNull(profile);

        Profile expectedProfile = profileRestClientImpl.getProfileWithAllAttributes(appToken,
            profile.getId().toString());
        assertNotNull(expectedProfile);
        assertEquals(profile.getUserName(), expectedProfile.getUserName());
    }

    @Test
    public void testGetProfileByTicket() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "admin", "admin", tenantName);
        assertNotNull(ticket);
        Profile profile = profileRestClientImpl.getProfileByTicket(appToken, ticket);
        assertNotNull(profile);

        assertEquals("admin", profile.getUserName());
    }

    @Test
    public void testGetProfileByTicketWithAttributes() throws AppAuthenticationFailedException,
        UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "admin", "admin", tenantName);
        assertNotNull(ticket);
        List<String> attributes = new ArrayList<String>();
        attributes.add("first-name");
        Profile profile = profileRestClientImpl.getProfileByTicketWithAttributes(appToken, ticket, attributes);
        assertNotNull(profile);
        assertEquals("admin", profile.getUserName());
    }

    @Test
    public void testGetProfileByTicketWithAllAttributes() throws AppAuthenticationFailedException,
        UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "admin", "admin", tenantName);
        assertNotNull(ticket);

        Profile profile = profileRestClientImpl.getProfileByTicketWithAllAttributes(appToken, ticket);
        assertNotNull(profile);
        assertEquals("admin", profile.getUserName());
        //assertTrue(profile.getAttributes().size() > 0);
    }

    @Test
    public void testGetProfileByUsername() throws AppAuthenticationFailedException {
        initAppToken();
        Profile profile = profileRestClientImpl.getProfileByUsername(appToken, "admin", tenantName);
        assertNotNull(profile);
        assertEquals("Wrong Username", "admin", profile.getUserName());
    }

    @Test
    public void testGetProfileByUsernameWithAttributes() throws AppAuthenticationFailedException {
        initAppToken();
        List<String> attributes = new ArrayList<String>();
        attributes.add("first-name");
        Profile profile = profileRestClientImpl.getProfileByUsernameWithAttributes(appToken, "admin", tenantName,
            attributes);
        assertNotNull(profile);
        assertEquals("Wrong Username", "admin", profile.getUserName());
    }

    @Test
    public void testGetProfileByUsernameWithAllAttributes() throws AppAuthenticationFailedException {
        initAppToken();

        Profile profile = profileRestClientImpl.getProfileByUsernameWithAllAttributes(appToken, "admin", tenantName);

        assertNotNull(profile);
        assertEquals("Wrong Username", "admin", profile.getUserName());
        //assertTrue(profile.getAttributes().size() > 0);
    }

    @Test
    public void testCreateProfile() throws AppAuthenticationFailedException {
        initAppToken();
        try {
            Profile p = profileRestClientImpl.getProfileByUsername(appToken, "adminnewuser", "craftercms");
            //			if (p!=null && p.getUserName().equals("adminnewuser")) {
            //				profileRestClientImpl.deleteProfile(appToken, p.getId().toString());
            //			}
        } catch (Exception e) {

        }

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "adminnewuser");
        params.put("password", "admin3");

        params.put("active", "true");
        params.put("first-name", "Administrator");
        params.put("last-name", "Admin");
        params.put("email", "admin@profile.com");
        params.put("telephone", "911");
        params.put("roles", roles);

        Profile newProfile = profileRestClientImpl.createProfile(appToken, params);
        assertNotNull(newProfile);
        assertEquals("adminnewuser", newProfile.getUserName());


    }

    @Test
    public void testUpdateProfile() throws AppAuthenticationFailedException {
        initAppToken();
        try {
            Profile p = profileRestClientImpl.getProfileByUsername(appToken, "adminupdateuser", "craftercms");
            //			if (p!=null && p.getUserName().equals("adminupdateuser")) {
            //				profileRestClientImpl.deleteProfile(appToken, p.getId().toString());
            //			}
        } catch (Exception e) {

        }

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "adminupdateuser");
        params.put("email", "adminupdateuser@email.com");
        params.put("password", "admin3");

        params.put("active", "true");
        params.put("first-name", "Administrator");
        params.put("last-name", "Admin");
        params.put("email", "admin@profile.com");
        params.put("telephone", "911");
        params.put("roles", roles);

        Profile updateProfile = profileRestClientImpl.createProfile(appToken, params);
        assertNotNull(updateProfile);
        assertEquals("adminupdateuser", updateProfile.getUserName());

        params = new HashMap<String, Serializable>();
        params.put("appToken", appToken);
        params.put("profileId", updateProfile.getId());
        params.put("first-name", "adminupdateuser");
        params.put("last-name", "adminupdateuser");
        params.put("email", "adminupdateuser@email.com");
        params.put("tenantName", "craftercms");
        profileRestClientImpl.updateProfile(appToken, params);

        Profile updatedProfile = profileRestClientImpl.getProfileWithAllAttributes(appToken,
            updateProfile.getId().toString());
        assertNotNull(updatedProfile);
        assertEquals("Update profile failed to update the first-name attribute", "adminupdateuser",
            updatedProfile.getAttributes().get("first-name"));

    }


    @Test
    public void testSetAttributesProfile() throws AppAuthenticationFailedException {
        initAppToken();
        try {
            Profile p = profileRestClientImpl.getProfileByUsername(appToken, "adminattruser", "craftercms");
            //			if (p!=null && p.getUserName().equals("adminattruser")) {
            //				profileRestClientImpl.deleteProfile(appToken, p.getId().toString());
            //			}
        } catch (Exception e) {

        }
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "adminattruser");
        params.put("password", "admin3");
        params.put("email", "adminattruser@profile.com");
        params.put("active", "true");

        params.put("roles", roles);
        Profile adminattruser = profileRestClientImpl.createProfile(appToken, params);
        assertNotNull(adminattruser);
        assertEquals("adminattruser", adminattruser.getUserName());
        params = new HashMap<String, Serializable>();
        params.put("first-name", "adminattruser");
        params.put("last-name", "adminattruser");
        params.put("telephone", "911");
        profileRestClientImpl.setAttributesForProfile(appToken, adminattruser.getId().toString(), params);
        Profile attrProfile = profileRestClientImpl.getProfileWithAllAttributes(appToken,
            adminattruser.getId().toString());
        assertNotNull(attrProfile);
        assertEquals("Set attributes failed to set the first-name attribute", "adminattruser",
            attrProfile.getAttributes().get("first-name"));

    }

    @Test
    public void testActiveProfile() throws AppAuthenticationFailedException {
        initAppToken();
        try {
            Profile p = profileRestClientImpl.getProfileByUsername(appToken, "admindeleteuser", "craftercms");
            //			if (p!=null && p.getUserName().equals("admindeleteuser")) {
            //				profileRestClientImpl.deleteProfile(appToken, p.getId().toString());
            //			}
        } catch (Exception e) {

        }

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "adminactiveuser");
        params.put("password", "admin3");
        params.put("email", "admindeleteuser@profile.com");
        params.put("active", "true");

        params.put("roles", roles);

        Profile admindeleteuser = profileRestClientImpl.createProfile(appToken, params);
        assertNotNull(admindeleteuser);
        assertEquals("adminactiveuser", admindeleteuser.getUserName());

        profileRestClientImpl.activeProfile(appToken, admindeleteuser.getId().toString(), false);

        Profile inactive = profileRestClientImpl.getProfileByUsername(appToken, "adminactiveuser", "craftercms");
        assertTrue(inactive.getActive() == false);


    }

    @Test
    public void testDeleteAttributesProfile() throws AppAuthenticationFailedException {
        initAppToken();
        try {
            Profile p = profileRestClientImpl.getProfileByUsername(appToken, "admindeleteattruser", "craftercms");
            //			if (p!=null && p.getUserName().equals("admindeleteattruser")) {
            //				profileRestClientImpl.deleteProfile(appToken, p.getId().toString());
            //			}
        } catch (Exception e) {

        }

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "admindeleteattruser");
        params.put("password", "admin3");
        params.put("email", "admindeleteuser@profile.com");
        params.put("first-name", "First");
        params.put("last-name", "Last");
        params.put("active", "true");
        params.put("roles", roles);

        Profile admindeleteattruser = profileRestClientImpl.createProfile(appToken, params);
        assertNotNull(admindeleteattruser);
        assertEquals("admindeleteattruser", admindeleteattruser.getUserName());
        List<String> attributes = new ArrayList<String>();
        attributes.add("email");
        profileRestClientImpl.deleteAttributesForProfile(appToken, admindeleteattruser.getId().toString(), attributes);

        Profile notAttributeProfile = profileRestClientImpl.getProfileWithAllAttributes(appToken,
            admindeleteattruser.getId());
        assertNotNull(notAttributeProfile);
        assertTrue(notAttributeProfile.getAttributes().get("email") == null);


    }

    @Test
    public void testGetAtttributesForProfile() throws AppAuthenticationFailedException {
        initAppToken();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "adminGetAttrsuser");
        params.put("password", "adminGetAttrsuser");

        params.put("active", "true");
        params.put("first-name", "adminGetAttrsuser");
        params.put("last-name", "Admin");
        params.put("email", "adminGetAttrsuser@profile.com");
        params.put("telephone", "999999");
        params.put("roles", roles);

        Profile newProfile = profileRestClientImpl.createProfile(appToken, params);
        assertNotNull(newProfile);
        if (newProfile.getUserName() == null) {
            newProfile = profileRestClientImpl.getProfileByUsernameWithAllAttributes(appToken, "adminGetAttrsuser",
                "craftercms");
        }
        assertEquals("adminGetAttrsuser", newProfile.getUserName());

        List<String> attributes = new ArrayList<String>();
        attributes.add("telephone");
        Map<String, Serializable> result = profileRestClientImpl.getAttributesForProfile(appToken,
            newProfile.getId(), attributes);

        assertNotNull(result);
        //assertNotNull(result.get("email"));
        assertTrue(result.get("first-name") == null);


    }

    @Test
    public void testGetAllAtttributesForProfile() throws AppAuthenticationFailedException {
        initAppToken();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "adminGetAllAttrsuser");
        params.put("password", "adminGetAllAttrsuser");
        params.put("active", "true");
        params.put("first-name", "adminGetAllAttrsuser");
        params.put("last-name", "Admin");
        params.put("email", "adminGetAllAttruser@profile.com");
        params.put("roles", roles);
        Profile newProfile = profileRestClientImpl.createProfile(appToken, params);
        assertNotNull(newProfile);
        if (newProfile.getUserName() == null) {
            newProfile = profileRestClientImpl.getProfileByUsernameWithAllAttributes(appToken,
                "adminGetAllAttrsuser", "craftercms");
        }
        assertNotNull(newProfile);
        //		List<String> attributes = new ArrayList<String>();
        //		attributes.add("telephone");
        //		Map<String, Serializable> result = profileRestClientImpl.getAllAttributesForProfile(appToken,
        // newProfile.getId());
        //		assertNotNull(result);
        //		assertTrue(result.get("first-name")!=null);
    }

    @Test
    public void testGetProfileRange() throws AppAuthenticationFailedException {
        initAppToken();

        Map<String, Serializable> params = new HashMap<String, Serializable>();

        List<String> attributes = new ArrayList<String>();
        attributes.add("first-name");
        attributes.add("last-name");
        List<Profile> profiles = profileRestClientImpl.getProfileRange(appToken, "craftercms", 0, 9, "modified",
            "ASC", attributes);

        assertNotNull(profiles);
        assertTrue(profiles.size() > 0);


    }

    @Test
    public void testGetProfiles() throws AppAuthenticationFailedException {
        initAppToken();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        Profile admin = profileRestClientImpl.getProfileByUsername(appToken, "admin", "craftercms");
        List<String> ids = new ArrayList<String>();
        ids.add(admin.getId());
        List<Profile> profiles = profileRestClientImpl.getProfiles(appToken, ids);

        assertNotNull(profiles);
        assertTrue(profiles.size() == 1);
        assertTrue(profiles.get(0).getUserName().equals("admin"));

    }

    @Test
    public void testGetProfilesWithAllAttributes() throws AppAuthenticationFailedException {
        initAppToken();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_ADMIN");
        params.put("appToken", appToken);
        params.put("tenantName", "craftercms");
        params.put("userName", "adminProfilesAllAtrs");
        params.put("password", "admin3");
        params.put("email", "admindeleteuser@profile.com");
        params.put("first-name", "First");
        params.put("last-name", "Last");
        params.put("active", "true");
        params.put("roles", roles);

        Profile newProfile = profileRestClientImpl.createProfile(appToken, params);

        List<String> ids = new ArrayList<String>();
        ids.add(newProfile.getId());
        List<Profile> profiles = profileRestClientImpl.getProfilesWithAllAttributes(appToken, ids);

        assertNotNull(profiles);
        assertTrue(profiles.size() == 1);
        assertTrue(profiles.get(0).getUserName().equals("adminProfilesAllAtrs"));
        assertTrue(profiles.get(0).getAttributes().size() >= 2);

        //		profileRestClientImpl.deleteProfile(appToken, newProfile.getId());

    }

    //	protected static Properties sConfig;
    //    protected static String baseUrl;
    //	protected static ProfileRestClientImpl profileRestClientImpl;
    //	protected static String appToken = null;
    //	private static final String CONFIG_FILE = "test.properties";
    //	private static String appPassword;
    //	private static String appUsername;
    //	protected static String tenantName;


    //@Before
    //public void configure() throws Exception {
    // Read config file
    //		sConfig = new Properties();
    //
    //		sConfig.load(ProfileRestControllerTest.class.getClassLoader().getResourceAsStream(
    //				CONFIG_FILE));
    //
    //		baseUrl = sConfig.getProperty("craftercms.test.base.url");
    //		appUsername = sConfig.getProperty("craftercms.test.profile.appUsername");
    //		appPassword = sConfig.getProperty("craftercms.test.profile.appPassword");
    //		tenantName = sConfig.getProperty("craftercms.test.profile.tenantName");
    //		profileRestClientImpl = new ProfileRestClientImpl();
    //		profileRestClientImpl.setPort(8181);


    //		if (sConfig.getProperty("craftercms.test.profile.port") != null) {
    //			profileRestClientImpl.setPort(Integer.parseInt(sConfig.getProperty("craftercms.test.profile.port")));
    //		}
    //		if (sConfig.getProperty("craftercms.test.profile.scheme") != null) {
    //			profileRestClientImpl.setScheme(sConfig.getProperty("craftercms.test.profile.scheme"));
    //		}
    //		if (sConfig.getProperty("craftercms.test.profile.host") != null) {
    //			profileRestClientImpl.setHost(sConfig.getProperty("craftercms.test.profile.host"));
    //		}
    //		if (sConfig.getProperty("craftercms.test.profile.profileAppPath") != null) {
    //			profileRestClientImpl.setProfileAppPath(sConfig.getProperty("craftercms.test.profile.profileAppPath"));
    //		}


    //	}
    //	@Test
    //	public void testAppToken() throws AppAuthenticationFailedException {
    //		String appToken = null;
    //		if (appToken == null) {
    //			appToken = profileRestClientImpl.getAppToken("crafterengine", "crafterengine");
    //		}
    //		assertNotNull(appToken);
    //	}


}