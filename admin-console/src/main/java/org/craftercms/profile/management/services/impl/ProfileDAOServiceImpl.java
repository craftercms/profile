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

	private ProfileClient profileRestClient;
	
	private String username;
	private String password;
	
	private String appToken;
	
	private static final Logger log = Logger.getLogger(ProfileDAOServiceImpl.class);
	
	
	public ProfileDAOServiceImpl() { 

	}

	public void deleteUser(String profileId) throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		}
		try {
			profileRestClient.deleteProfile(appToken, profileId);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			profileRestClient.deleteProfile(appToken, profileId);
		}
	}
	
	public void deleteUsers(List<String> users) throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		}
		for (String currentUser:users) {
			deleteUser(currentUser);
		}
	}

	public Profile createUser(Map<String, Serializable> data)
			throws AppAuthenticationFailedException {
		
		if (appToken == null) {
			setAppToken();
		} 
		try {
			return profileRestClient.createProfile(appToken, data);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.createProfile(appToken, data);
		}
	}
	
	public List<Role> getRoles(String tenantName) throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		}
		try {
			return profileRestClient.getAllRoles(appToken);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getAllRoles(appToken);
		}
	}
	 
	public void setAppToken() throws AppAuthenticationFailedException {
		appToken = profileRestClient.getAppToken(username, password);		
	}

	public Profile updateUser(Map<String, Serializable> data)
			throws AppAuthenticationFailedException {
		
		if (appToken == null) {
			setAppToken();
		} 
		try {
			return profileRestClient.updateProfile(appToken, data);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.updateProfile(appToken, data);
		}
	}


    public void setSchemaAttribute(Attribute attribute, String tenantName) {
    	try{ 
            profileRestClient.setAttributeForSchema(appToken, tenantName,attribute);
    	} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			profileRestClient.setAttributeForSchema(appToken, tenantName,attribute);
		}
	}

	public Profile getUser(String username, String tenantName)
			throws AppAuthenticationFailedException {
		
		if (appToken == null) {
			setAppToken();
		}
		try {
			return profileRestClient.getProfileByUsername(appToken, username, tenantName);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getProfileByUsername(appToken, username, tenantName);
		}
	}

    public SchemaModel getSchema(String tenantName)
			throws AppAuthenticationFailedException {
		
		if (appToken == null) {
			setAppToken();
		} 
		Tenant t = null; 
		try { 
			t = profileRestClient.getTenantByName(appToken, tenantName);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			t = profileRestClient.getTenantByName(appToken, tenantName);
		}
		return new SchemaModel(t.getSchema(),t.getTenantName());
	}
	
	public Profile getUserWithAllAttributes(String username, String tenantName)
			throws AppAuthenticationFailedException, RestException {
		
		if (appToken == null) {
			setAppToken();
		}
		Profile p = null;
		try {
			p = profileRestClient.getProfileByUsernameWithAllAttributes(appToken, username, tenantName);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			p = profileRestClient.getProfileByUsernameWithAllAttributes(appToken, username, tenantName);
		}
		if (p == null) { 
			throw new RestException("Username was not valid");
		}
		
		return p;
	}

	public List<Profile> getUsersByModifiedDate(int start, int end, String tenantName) {
		
		List<String> attributes = new ArrayList<String>();
		attributes.add(ProfileUserAccountConstants.USERNAME_PROPERTY);
		try {
			return profileRestClient.getProfileRange(appToken, tenantName, start, end, ProfileUserAccountConstants.MODIFIED_PROPERTY, ProfileUserAccountConstants.SORT_ORDER_ASC, attributes);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getProfileRange(appToken, tenantName, start, end, ProfileUserAccountConstants.MODIFIED_PROPERTY, ProfileUserAccountConstants.SORT_ORDER_ASC, attributes);
		}
	}
	
	public List<Profile> getUsers(int start, int end, String sortBy, String sortOrder, List<String> attributes, String tenantName)
		throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		}
		try {
			return profileRestClient.getProfileRange(appToken, tenantName, start, end, sortBy, sortOrder, attributes);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getProfileRange(appToken, tenantName, start, end, sortBy, sortOrder, attributes);
		}
	}
	
	public long getProfileCount(String tenantName)
			throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		}
		try {
			return profileRestClient.getProfileCount(appToken, tenantName);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return profileRestClient.getProfileCount(appToken, tenantName);
		}
	}
	
	public void setAttributes(String profileId, Map<String, Serializable> attributes) throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		}
		try {
			this.profileRestClient.setAttributesForProfile(appToken, profileId, attributes);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			this.profileRestClient.setAttributesForProfile(appToken, profileId, attributes);
		}
	}
	
	public Profile getProfileWithAllAttributes(String profileId) throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		}
		try {
			return this.profileRestClient.getProfileWithAllAttributes(appToken, profileId);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			return this.profileRestClient.getProfileWithAllAttributes(appToken, profileId);
		}
	}
	
	public void deleteAttributes(String profileId, List<String> attributes)
		throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		} 
		try {
			profileRestClient.deleteAttributesForProfile(appToken, profileId,attributes);
		} catch(AppAuthenticationException e) {
			try {
				
				setAppToken();
				
			} catch (AppAuthenticationFailedException e1) {
				log.error("could not get an AppToken", e);
			}
			profileRestClient.deleteAttributesForProfile(appToken, profileId,attributes);
		}
	}
	
	@Value("${crafter.profile.app.username}")
	public void setUsername(String username) {
		this.username = username;
	}

	@Value("${crafter.profile.app.password}")
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Autowired
	public void setProfileRestClient(ProfileClient profileRestClient) {
		this.profileRestClient = profileRestClient;
	}

	public void deleteSchemaAttributes(String schemaId, List<String> attributes) throws AppAuthenticationFailedException {
		if (appToken == null) {
			setAppToken();
		} 
		
		for (String attribute: attributes){
			try {
				profileRestClient.deleteAttributeForSchema(appToken, schemaId, attribute);
			} catch(AppAuthenticationException e) {
    			try {
    				
    				setAppToken();
    				
    			} catch (AppAuthenticationFailedException e1) {
    				log.error("could not get an AppToken", e);
    			}
    			 profileRestClient.deleteAttributeForSchema(appToken, schemaId, attribute);
    		}
        }
		
	}

	public void restartAppToken() {
		this.appToken = null;
		
	}
	
}
