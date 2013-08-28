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
package org.craftercms.profile.management.services.impl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.RestException;
import org.craftercms.profile.impl.domain.*;

import org.craftercms.profile.management.model.SchemaModel;
import org.craftercms.profile.management.services.ProfileDAOService;
import org.craftercms.profile.management.util.ProfileUserAccountConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
@Service
public class ProfileDAOServiceImpl implements ProfileDAOService {

	private static final Logger log = Logger.getLogger(ProfileDAOServiceImpl.class);
	
	
	public ProfileDAOServiceImpl() { 

	}

//	public void deleteUser(String profileId) throws AppAuthenticationFailedException {
//		if (!ProfileServiceManager.isAppTokenInit()) {
//			ProfileServiceManager.setAppToken();
//		}
//		try {
//			ProfileServiceManager.getProfileClient().deleteProfile(ProfileServiceManager.getAppToken(), profileId);
//		} catch(AppAuthenticationException e) {
//			try {
//				ProfileServiceManager.setAppToken();
//			} catch (AppAuthenticationFailedException e1) {
//				log.error("could not get an AppToken", e);
//			}
//			ProfileServiceManager.getProfileClient().deleteProfile(ProfileServiceManager.getAppToken(), profileId);
//		}
//	}
	
	public void activeUser(String profileId, boolean active) throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		try {
			ProfileServiceManager.getProfileClient().activeProfile(ProfileServiceManager.getAppToken(), profileId, active);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			ProfileServiceManager.getProfileClient().activeProfile(ProfileServiceManager.getAppToken(), profileId, active);
		}
	}
	
//	public void deleteUsers(List<String> users) throws AppAuthenticationFailedException {
//		if (!ProfileServiceManager.isAppTokenInit()) {
//			ProfileServiceManager.setAppToken();
//		}
//		for (String currentUser:users) {
//			deleteUser(currentUser);
//		}
//	}
	
	public void activeUsers(List<String> users, boolean active) throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		for (String currentUser:users) {
			activeUser(currentUser, active);
		}
	}

	public Profile createUser(Map<String, Serializable> data)
			throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		} 
		try {
			return ProfileServiceManager.getProfileClient().createProfile(ProfileServiceManager.getAppToken(), data);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().createProfile(ProfileServiceManager.getAppToken(), data);
		}
	}
	
	public List<Role> getRoles(String tenantName) throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		try {
			return ProfileServiceManager.getProfileClient().getAllRoles(ProfileServiceManager.getAppToken());
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().getAllRoles(ProfileServiceManager.getAppToken());
		}
	}
	 
//	public void setAppToken() throws AppAuthenticationFailedException {
//		appToken = profileRestClient.getAppToken(username, password);		
//	}

	public Profile updateUser(Map<String, Serializable> data)
			throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		} 
		try {
			return ProfileServiceManager.getProfileClient().updateProfile(ProfileServiceManager.getAppToken(), data);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().updateProfile(ProfileServiceManager.getAppToken(), data);
		}
	}


    public void setSchemaAttribute(Attribute attribute, String tenantName) 
    		throws AppAuthenticationFailedException {
    	if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
    	try{ 
    		ProfileServiceManager.getProfileClient().setAttributeForSchema(ProfileServiceManager.getAppToken(), tenantName,attribute);
    	} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			ProfileServiceManager.getProfileClient().setAttributeForSchema(ProfileServiceManager.getAppToken(), tenantName,attribute);
		}
	}

	public Profile getUser(String username, String tenantName)
			throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		try {
			return ProfileServiceManager.getProfileClient().getProfileByUsername(ProfileServiceManager.getAppToken(), username, tenantName);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().getProfileByUsername(ProfileServiceManager.getAppToken(), username, tenantName);
		}
	}

    public SchemaModel getSchema(String tenantName)
			throws AppAuthenticationFailedException {
    	if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		} 
		Tenant t = null; 
		try { 
			t = ProfileServiceManager.getProfileClient().getTenantByName(ProfileServiceManager.getAppToken(), tenantName);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			t = ProfileServiceManager.getProfileClient().getTenantByName(ProfileServiceManager.getAppToken(), tenantName);
		}
		return new SchemaModel(t.getSchema(),t.getTenantName());
	}
	
	public Profile getUserWithAllAttributes(String username, String tenantName)
			throws AppAuthenticationFailedException, RestException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		Profile p = null;
		try {
			p = ProfileServiceManager.getProfileClient().getProfileByUsernameWithAllAttributes(ProfileServiceManager.getAppToken(), username, tenantName);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			p = ProfileServiceManager.getProfileClient().getProfileByUsernameWithAllAttributes(ProfileServiceManager.getAppToken(), username, tenantName);
		}
		if (p == null) { 
			throw new RestException("Username was not valid");
		}
		
		return p;
	}

	public List<Profile> getUsersByModifiedDate(int start, int end, String tenantName) 
	throws AppAuthenticationFailedException {
		
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		List<String> attributes = new ArrayList<String>();
		attributes.add(ProfileUserAccountConstants.USERNAME_PROPERTY);
		try {
			return ProfileServiceManager.getProfileClient().getProfileRange(ProfileServiceManager.getAppToken(), tenantName, start, end, ProfileUserAccountConstants.MODIFIED_PROPERTY, ProfileUserAccountConstants.SORT_ORDER_ASC, attributes);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().getProfileRange(ProfileServiceManager.getAppToken(), tenantName, start, end, ProfileUserAccountConstants.MODIFIED_PROPERTY, ProfileUserAccountConstants.SORT_ORDER_ASC, attributes);
		}
	}
	
	public List<Profile> getUsers(int start, int end, String sortBy, String sortOrder, List<String> attributes, String tenantName)
		throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		try {
			return ProfileServiceManager.getProfileClient().getProfileRange(ProfileServiceManager.getAppToken(), tenantName, start, end, sortBy, sortOrder, attributes);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().getProfileRange(ProfileServiceManager.getAppToken(), tenantName, start, end, sortBy, sortOrder, attributes);
		}
	}
	
	public long getProfileCount(String tenantName)
			throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		try {
			return ProfileServiceManager.getProfileClient().getProfileCount(ProfileServiceManager.getAppToken(), tenantName);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().getProfileCount(ProfileServiceManager.getAppToken(), tenantName);
		}
	}
	
	public void setAttributes(String profileId, Map<String, Serializable> attributes) throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		try {
			ProfileServiceManager.getProfileClient().setAttributesForProfile(ProfileServiceManager.getAppToken(), profileId, attributes);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			ProfileServiceManager.getProfileClient().setAttributesForProfile(ProfileServiceManager.getAppToken(), profileId, attributes);
		}
	}
	
	public Profile getProfileWithAllAttributes(String profileId) throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		}
		try {
			return ProfileServiceManager.getProfileClient().getProfileWithAllAttributes(ProfileServiceManager.getAppToken(), profileId);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return ProfileServiceManager.getProfileClient().getProfileWithAllAttributes(ProfileServiceManager.getAppToken(), profileId);
		}
	}
	
	public void deleteAttributes(String profileId, List<String> attributes)
		throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		} 
		try {
			ProfileServiceManager.getProfileClient().deleteAttributesForProfile(ProfileServiceManager.getAppToken(), profileId,attributes);
		} catch(AppAuthenticationException e) {
			try {
				ProfileServiceManager.setAppToken();
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			ProfileServiceManager.getProfileClient().deleteAttributesForProfile(ProfileServiceManager.getAppToken(), profileId,attributes);
		}
	}
	
	public void deleteSchemaAttributes(String schemaId, List<String> attributes) throws AppAuthenticationFailedException {
		if (!ProfileServiceManager.isAppTokenInit()) {
			ProfileServiceManager.setAppToken();
		} 
		
		for (String attribute: attributes){
			try {
				ProfileServiceManager.getProfileClient().deleteAttributeForSchema(ProfileServiceManager.getAppToken(), schemaId, attribute);
			} catch(AppAuthenticationException e) {
    			try {
    				ProfileServiceManager.setAppToken();
    			} catch (AppAuthenticationFailedException e1) {
    				log.error("could not get an AppToken", e);
    			}
    			ProfileServiceManager.getProfileClient().deleteAttributeForSchema(ProfileServiceManager.getAppToken(), schemaId, attribute);
    		}
        }
		
	}

}
