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
package org.craftercms.profile.httpclient;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.impl.ProfileRestClientImpl;

public class ProfileRestClientTest extends TestCase {

	ProfileClient profileRestClient = new ProfileRestClientImpl();
	Log log = LogFactory.getLog(ProfileRestClientTest.class);

	@Before
	public void init() {
	}
	
	public void createProfile() {
		
	}
	
	public void testGetRoles() throws AppAuthenticationFailedException {
//		log.info("testGetRoles");
//		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//		String token = profileRestClient.getAppToken("crafterengine", "crafterengine");
		
//		attributes.put("appToken", token);
//		attributes.put("userName", "admin3");
//		attributes.put("password", "admin3");
//		attributes.put("role", "ADMIN");
//		attributes.put("active", "true");
//		attributes.put("first-name", "Administrator");
//		attributes.put("last-name", "Secondary");
//		attributes.put("email", "admin@profile.com");
//		attributes.put("telephone", "911");
		
//		List<Role> roles = profileRestClient.getAllRoles(token);
//		if (roles != null) {
//			log.info("Already there are some roles");
//		} else {
//			log.info("NOT ROLES WERE CREATED");
//		}
	}
	
	public void testCreateProfile() throws AppAuthenticationFailedException {
//		log.info("testCreateProfile");
//		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//		String token = profileRestClient.getAppToken("crafterengine", "crafterengine");
//		
//		attributes.put("appToken", token);
//		attributes.put("userName", "admin3");
//		attributes.put("password", "admin3");
//		attributes.put("role", "ADMIN");
//		attributes.put("active", "true");
//		attributes.put("first-name", "Administrator");
//		attributes.put("last-name", "Secondary");
//		attributes.put("email", "admin@profile.com");
//		attributes.put("telephone", "911");
//		
//		Profile profile = profileRestClient.createProfile(token, attributes);
//		if (profile != null) {
//			log.info("PROFILE WAS CREATED");
//		} else {
//			log.info("NOT PROFILE WAS CREATED");
//		}
	}

//	public void testProfileCount() throws AppAuthenticationFailedException {
//		String appToken = profileRestClient.getAppToken("craftercms", "craftercms");
//		System.out.println(" TOKEN " + appToken);
//		System.out.println(profileRestClient.getProfileCount(appToken));
//		
//		String appToken1 = profileRestClient.getAppToken("craftersocial", "craftersocial");
//		System.out.println(" TOKEN " + appToken1);
//		System.out.println(profileRestClient.getProfileCount(appToken1));
//	}

	/*public void testGetProfile() throws AppAuthenticationFailedException {
		
 		log.info("get profile 1");
		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		System.out.println("*** TOKEN " + profileRestClient.getAppToken("craftersocial", "craftersocial"));
		attributes.put("appToken", profileRestClient.getAppToken("craftersocial", "craftersocial"));
		attributes.put("userName", "admin");
		attributes.put("password", "admin");
		attributes.put("active", "true");
		attributes.put("fromclient", "admin-fromclient");
		attributes.put("fromhome", "admin-fromhome");
		
		System.out.println("*** TOKEN " + attributes.get("appToken"));

		Profile profile = profileRestClient.createProfile("craftersocial", attributes);

		List<String> profileAttributes = new ArrayList<String>();
		profileAttributes.add("fromclient");
		
		
		Profile testing = profileRestClient.getProfile((String)attributes.get("appToken"), profile.getId());
		System.out.println("***>> TOKEN " + testing.toString());
		

		Profile profile1 = profileRestClient.getProfileByUsername("craftersocial", "admin");
		
//		log.info("get profile username " + profile.getUserName());
//		log.info("get profile ID " + profile.getId());
		log.info("get profile string " + profile1.toString());

		Profile profile3 = profileRestClient.getProfileByUsernameWithAttributes("", "admin", profileAttributes);
		System.out.println("***>> ATRRIBUTES " + profile3.toString());
//		
//		Profile profile = profileRestClient.getProfileByUsernameWithAllAttributes("craftersocial", "testingclientuser");
		
		//profileRestClient.deleteProfile("craftersocial", profile1.getId());

	}*/
	
//	public void testGetProfileInAnotherMethod() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
//		log.info("get profile by ticket");
//		String appToken = profileRestClient.getAppToken("craftersocial", "craftersocial");
//		
//		Profile profile1 = profileRestClient.getProfileByUsername("craftersocial", "testingclientuser11");
//		Profile testing = profileRestClient.getProfile(appToken, profile1.getId());
//		System.out.println("***NewTOKEN " + testing.toString());
//		
//		//profileRestClient.deleteProfile("craftersocial", profile1.getId());
//	}
	
//	public void testGetProfileByTicket() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
//		log.info("get profile by ticket");
//		String appToken = profileRestClient.getAppToken("craftersocial", "craftersocial");
//
//		String ticket = profileRestClient.getTicket(appToken, "testingclientuser11", "testingclientuser11");
//		Profile profile = profileRestClient.getProfileByTicket(appToken, ticket);
//
//		List<String> profileAttributes = new ArrayList<String>();
//		profileAttributes.add("fromclient");
//
//		Profile profile1 = profileRestClient.getProfileByTicketWithAllAttributes(appToken, ticket);
//		Profile profile2 = profileRestClient.getProfileByTicketWithAttributes(appToken, ticket, profileAttributes);
//		
////		System.out.println(profile.getUserName());
//		System.out.println("***testGetProfileByTicket " + profile2.toString());
//		//profileRestClient.deleteProfile("craftersocial", profile2.getId());
//	}

	public void testGetProfileWithAttributes() {
/*		log.info("get profile with attributes");
		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		attributes.put("appToken", "testingclientuser");
		attributes.put("userName", "testingclientuser");
		attributes.put("password", "testingclientuser");
		attributes.put("active", "true");
		attributes.put("fromclient", "testingclientuser-fromclient");
		attributes.put("fromhome", "testingclientuser-fromhome");

		Profile profile = profileRestClient.createProfile("craftersocial", attributes);

		List<String> profileAttributes = new ArrayList<String>();
		profileAttributes.add("fromclient");

		profile = profileRestClient.getProfileWithAttributes("craftersocial", profile.getId(), profileAttributes);
		assertEquals("testingclientuser", profile.getUserName());
		assertEquals("testingclientuser-fromclient", profile.getAttributes().get("fromclient"));
		assertEquals(1, profile.getAttributes().size());

		profileRestClient.deleteProfile("craftersocial", profile.getId());
*/	}

	public void testGetProfileWithAllAttributes() {		log.info("get profile with all attributes");
//		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//		attributes.put("appToken", "testingclientuser");
//		attributes.put("userName", "testingclientuser");
//		attributes.put("password", "testingclientuser");
//		attributes.put("active", "true");
//		attributes.put("fromclient", "testingclientuser-fromclient");
//		attributes.put("fromhome", "testingclientuser-fromhome");
//
//		Profile profile = profileRestClient.createProfile("crafterengine", attributes);
//
//		profile = profileRestClient.getProfileWithAllAttributes("crafterengine", profile.getId());
//		assertEquals("testingclientuser", profile.getUserName());
//		assertEquals("testingclientuser-fromclient", profile.getAttributes().get("fromclient"));
//		assertEquals(2, profile.getAttributes().size());
//
//		profileRestClient.deleteProfile("crafterengine", profile.getId());
	}

	public void testGetProfiles() {
/*		List<String> profileIds = new ArrayList<String>();
		profileRestClient.getProfiles("craftersocial", profileIds);
*/	}

	public void testGetProfilesWithAttributes() {
//		List<String> profileIds = new ArrayList<String>();
////		profileIds.add("4f86f0978608a0d070ba18fa");
////		profileIds.add("4f85c2f98608dcd243c64377");
//		profileIds.add("5069f47a036483ff3b3d8709");
//		
//		//List<Profile> profiles = profileRestClient.getProfilesWithAllAttributes("craftersocial", profileIds);
//		List<Profile> profiles = profileRestClient.getProfiles("craftersocial", profileIds);
//		for(Profile profile: profiles) {
//			System.out.println("<<< testGetProfilesWithAttributes " + profile.toString());;
//			profileRestClient.deleteProfile("craftersocial", profile.getId());
//		}
	}

	public void testGetProfileRange() throws Exception {
		//log.info("---> testGetProfileRange");
	//	List<String> attributes = new ArrayList<String>();
		//String appToken = profileRestClient.getAppToken("craftercms", "craftercms");
		//String appToken = profileRestClient.getAppToken("crafterengine", "crafterengine");
//		System.out.println(" TOKEN " + appToken);
//		System.out.println(profileRestClient.getProfileCount(appToken));
		//attributes.add("first-name");
		//attributes.add("last-name");

		//List<Profile> profiles = profileRestClient.getProfileRange(appToken, 0, 60, "userName", "asc", attributes);
		//appToken = profileRestClient.getAppToken("crafterengine", "crafterengine");
		//Profile profile = profileRestClient.getProfileWithAllAttributes(appToken, profiles.get(0).getId());
//		for(Profile p:profiles) {
//			System.out.println(p.toString());
//			List<Object> valueList = Collections.list(Collections.enumeration(p.getAttributes().values()));
//		    List<String> keyList = Collections.list(Collections.enumeration(p.getAttributes().keySet()));
//	        int idx =0;
//		    for(Object s: valueList) {
//		    	System.out.println("***Attribute " + keyList.get(idx) +"= " +s);
//		    	idx++;
//		    }
//			
//		}
	}

	

	public void testUpdateProfile() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
/*		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		attributes.put("profileId", "4f871fcc86088bf02f0ad4da");
		attributes.put("userName", "craftercmsclientupdate5");
		attributes.put("testing", "costarica");

		// Profile profile = profileRestClient.updateProfile("craftersocial", attributes);
*/	
		
//		log.info("testUpdateProfileInAnotherMethod");
//		String appToken = profileRestClient.getAppToken("craftersocial", "craftersocial");
//		
//		Profile profile1 = profileRestClient.getProfileByUsername("craftersocial", "testingclientuser11");
//		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//		attributes.put("profileId", profile1.getId());
//		attributes.put("userName", profile1.getUserName());
//		
//		attributes.put("fromclient", "no");
//		attributes.put("testing", "costarica");
//		Profile profile = profileRestClient.updateProfile("craftersocial", attributes);
//		
//		System.out.println("***UPDATING " + profile.toString());
	}

	public void testSetAttributesForProfile() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
//		System.out.println(".....testSetAttributesForProfile");
//		Map<String, Serializable> attributesMap = new HashMap<String, Serializable>();
//		attributesMap.put("appToken", "cs");
//		attributesMap.put("clientcall", "yes");
//		attributesMap.put("fromhome", "yes");
//		attributesMap.put("working", "true");
//		
//		String appToken = profileRestClient.getAppToken("craftersocial", "craftersocial");
//		
//		Profile profile1 = profileRestClient.getProfileByUsername("craftersocial", "testingclientuser11");
//
//	    profileRestClient.setAttributesForProfile("craftersocial", profile1.getId(), attributesMap);
//	    
//	    profile1 = profileRestClient.getProfileByUsername("craftersocial", "testingclientuser11");
//	    
//	    System.out.println("***UPDATING " + profile1.toString());
//	    
//	
//	    List<Object> valueList = Collections.list(Collections.enumeration(profile1.getAttributes().values()));
//	    List<String> keyList = Collections.list(Collections.enumeration(profile1.getAttributes().keySet()));
//        int idx =0;
//	    for(Object s: valueList) {
//	    	System.out.println("***Attribute " + keyList.get(idx) +" " +profile1.toString());
//	    	idx++;
//	    }
	}

	public void testGetAttributesForProfile() {
/*		List<String> attributes = new ArrayList<String>();
		attributes.add("clientcall");
		attributes.add("fromhome");

		// Map<String, Serializable> attribs = profileRestClient.getAttributesForProfile("craftersocial", "4f7ca5f48608f0a3342d74fb", attributes);
*/	}

	public void testGetAllAttributesForProfile() {
		// Map<String, Serializable> attribs = profileRestClient.getAllAttributesForProfile("craftersocial", "4f7ca5f48608f0a3342d74fb");
	}

	public void testDeleteAttributesForProfile() {
/*		List<String> attributeNames = new ArrayList<String>();
		attributeNames.add("clientcall");

		// profileRestClient.deleteAttributesForProfile("craftersocial", "4f7ca5f48608f0a3342d74fb", attributeNames);
*/	}

	public void testDeleteAllAttributesForProfile() {
/*		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
		attributes.put("appToken", "craftersocial");
		
		// profileRestClient.deleteAllAttributesForProfile("4f8489418608c13b70aa828b", attributes);
*/	}

	public void testDeleteProfile() {
		// profileRestClient.deleteProfile("craftersocial", "4f88778686088bf02f0ad4dd");
	}
	
	public void testDeleteProfileInAnotherMethod() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
//		log.info("testDeleteProfileInAnotherMethod");
//		String appToken = profileRestClient.getAppToken("craftersocial", "craftersocial");
//		
//		Profile profile1 = profileRestClient.getProfileByUsername("craftersocial", "testingclientuser11");
//		Profile testing = profileRestClient.getProfile(appToken, profile1.getId());
//		System.out.println("***testDeleteProfileInAnotherMethod " + testing.toString());
//		
//		profileRestClient.deleteProfile("craftersocial", profile1.getId());
	}

	@After
	public void release() {		
	}
	
//	public static void updateAdmin() throws Exception {
//		ProfileRestClient profileRestClient = new ProfileRestClientImpl();
//		String token = profileRestClient.getAppToken("craftersocial", "craftersocial");
//		
//		Profile p = profileRestClient.getProfileByUsername(token, "admin");
//		List<Role> roles = profileRestClient.getAllRoles(token);
//		ArrayList<String> lr=new ArrayList<String>();
//		for(Role r:roles) {
//			lr.add(r.getRoleName());
//		}
//		
//		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//		attributes.put("appToken", token);
//		attributes.put("profileId", p.getId());
//		attributes.put("userName", p.getUserName());
//		attributes.put("roles", lr);
//		profileRestClient.updateProfile(token, attributes);
//	}
//	
//	public static void getProfiles() throws Exception {
//		ProfileRestClient profileRestClient = new ProfileRestClientImpl();
//		String token = profileRestClient.getAppToken("craftersocial", "craftersocial");
//		List<String> attributes =  new ArrayList<String>();
//		List<Profile> pl = profileRestClient.getProfileRange(token, 0, 10, "userName", "asc", attributes);
//		
//		for(Profile p:pl) {
//			System.out.println(p.getUserName());
//		}
//		
//	}
	
	public static void main(String[] args) throws Exception{
		//getProfiles();
		//updateAdmin();
		//log.info("testGetRoles");
//		ProfileRestClient profileRestClient = new ProfileRestClientImpl();
//		//Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//		String token = profileRestClient.getAppToken("craftersocial", "craftersocial");
		
//		attributes.put("appToken", token);
//		attributes.put("userName", "admin3");
//		attributes.put("password", "admin3");
//		attributes.put("role", "ADMIN");
//		attributes.put("active", "true");
//		attributes.put("first-name", "Administrator");
//		attributes.put("last-name", "Secondary");
//		attributes.put("email", "admin@profile.com");
//		attributes.put("telephone", "911");
		
		//List<Role> roles = profileRestClient.getAllRoles(token);
		//Role role = profileRestClient.createRole(token, "MODERATOR");
		//System.out.println("NEW ROLE " + role.getRoleName());
//		List<Role> roles = profileRestClient.getAllRoles(token);
//		for(Role r:roles) {
//			System.out.println(r.getRoleName());
//		}
//		
//		Map<String, Serializable> attributes = new HashMap<String, Serializable>();
//		ArrayList<String> rolesList = new ArrayList<String>();
//		for(Role r:roles) {
//			rolesList.add(r.getRoleName());
//		}
//		
//		
//		attributes.put("appToken", token);
//		attributes.put("userName", "admin15");
//		attributes.put("password", "admin15");
//		attributes.put("role", "ADMIN");
//		attributes.put("roles", rolesList);
//		attributes.put("active", "true");
//		attributes.put("first-name", "Administrator");
//		attributes.put("last-name", "Secondary");
//		attributes.put("email", "admin@profile.com");
//		attributes.put("telephone", "911");
//		
//		Profile profile = profileRestClient.createProfile(token, attributes);
//		for (String r:profile.getRoles()) {
//			System.out.println("ROLE RETURNED " + r);
//			if (r.equals("USER")) {
//				profile.getRoles().remove(r);
//				break;
//			}
//		}
//		attributes.put("roles", (ArrayList<String>)profile.getRoles());
//		attributes.put("profileId", profile.getId());
//		profile = profileRestClient.updateProfile(token, attributes);
//		for (String r:profile.getRoles()) {
//			System.out.println("ROLE RETURNED " + r);
//			
//		}
//		profileRestClient.deleteRole(token, "USER");
		
		
//		if (roles != null && roles.size() > 0 ) {
//			System.out.println("Already there are some roles");
//		} else {
//			System.out.println("NOT ROLES WERE CREATED");
//		}
	}
}