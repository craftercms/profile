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
package org.craftercms.profile.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.domain.*;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;

/**
 * Profile Rest client is the client should be used to communicate to Profile Server
 * 
 * @author Alvaro Gonzalez
 *
 */
public interface ProfileClient {
	/**
	 * Gets the number of profiles
	 * 
	 * @param appToken The appToken previously returned by Profile server
	 * @param tenantName Current tenant name for profiles
	 * @return number of profiles.
	 */
	public long getProfileCount(String appToken, String tenantName);

	/**
	 * Gets a profile based on a profile id.
	 * 
	 * @param appToken The appToken previously returned by Profile server
	 * 
	 * @param profileId used to get the profile data
	 * 
	 * @return a profile account object
	 */
	public Profile getProfile(String appToken, String profileId);
	
	/**
	 * Gets a profile based on a profile id and that profile is going to have only the attributes passed in the attributes names list parameter
	 * 
	 * @param appToken The appToken previously returned by Profile server
	 * 
	 * @param profileId used to get the profile data
	 * 
	 * @param attributes The attributes name list that is going to be used to populate the profile object will be returned
	 * 
	 * @return a profile object with the attributes passed in the attributes parameter.
	 */
	public Profile getProfileWithAttributes(String appToken, String profileId, List<String> attributes);
	
	/**
	 * Gets a profile based on a profile id and that profile will have all the attributes.
	 * @param appToken The appToken previously returned by Profile server
	 * @param profileId used to get the profile data
	 * @return a profile object with all the attributes.
	 */
	public Profile getProfileWithAllAttributes(String appToken, String profileId);

	/**
	 * Get a profile base on a username.
	 * @param appToken The appToken previously returned by Profile server
	 * @param username used to get the profile data
	 * @param tenantName Current tenantName for profiles
	 * @return the profile object found.
	 */
	public Profile getProfileByUsername(String appToken, String username, String tenantName);
	/**
	 * Gets a profile based on a profile id and that profile is going to have only the attributes passed in the attributes names list parameter 
	 * @param appToken  The appToken previously returned by Profile server
	 * @param username used to get the profile data
	 * @param tenantName Current tenantName for profiles
	 * @param attributes The attributes name list that is going to be used to populate the profile object will be returned
	 * @return a profile object with some attributes.
	 */
	public Profile getProfileByUsernameWithAttributes(String appToken, String username, String tenantName, List<String> attributes);
	/**
	 * Gets a profile based on a username and that profile will have all the attributes.
	 * @param appToken The appToken previously returned by Profile server
	 * @param username used to get the profile data
	 * @param tenantName Current tenantName for profiles
	 * @return a profile object with all the attributes.
	 */
	public Profile getProfileByUsernameWithAllAttributes(String appToken, String username, String tenantName);

	/**
	 * Get a profile based on a ticket.
	 * @param appToken The appToken previously returned by Profile server
	 * @param ticket used to get the profile data
	 * @return a profile object
	 */
	public Profile getProfileByTicket(String appToken, String ticket);
	/**
	 * Gets a profile based on a ticket and that profile is going to have only the attributes passed in the attributes names list parameter
	 * @param appToken previously returned by Profile server
	 * @param ticket used to get the profile data
	 * @param attributes name list that is going to be used to populate the profile object will be returned
	 * @return a profile object with some attributes
	 */
	public Profile getProfileByTicketWithAttributes(String appToken, String ticket, List<String> attributes);
	/**
	 * Gets a profile based on a ticket and that profile will have all the attributes.
	 * @param appToken previously returned by Profile server
	 * @param ticket used to get the profile data
	 * @return a profile object with all the attributes
	 */
	public Profile getProfileByTicketWithAllAttributes(String appToken, String ticket);

	/**
	 * Creates a new profile
	 * @param appToken previously returned by Profile server
	 * @param queryParams is a key pair list of parameters that will be sent to the profile server to create the new profile
	 * @return the new profile object.
	 */
	public Profile createProfile(String appToken, Map<String, Serializable> queryParams);
	/**
	 * Creates a new profile based on the parameters values received.
	 * @param appToken previously returned by Profile server
	 * @param userName of the new profile
	 * @param password of the new profile
	 * @param active <code>true</code indicates the new profile will be activated otherwise <code>false</code>
	 * @param tenantName of the new profile
	 * @param queryParams is a key pair list of parameters that will be sent to the profile server to create the new profile
	 * @return the new profile object
	 */
	public Profile createProfile(String appToken, String userName, String password, Boolean active, String tenantName, Map<String, Serializable> queryParams);

	/**
	 * Updates a profile using the parameters data
	 * @param appToken previously returned by Profile server
	 * @param queryParams is a key pair list of parameters that will be sent to the profile server to update the profile
	 * @return the profile just updated
	 */
	public Profile updateProfile(String appToken, Map<String, Serializable> queryParams);
	/**
	 * Updates the profile's attributes
	 * @param appToken previously returned by Profile server
	 * @param profileId is going to be updated with the new attributes values
	 * @param queryParams key pair list of attributes that will be updated
	 */
	public void setAttributesForProfile(String appToken, String profileId, Map<String, Serializable> queryParams);

	/**
	 * Deletes a profiles
	 * @param appToken previously returned by Profile server
	 * @param profileId is going to be deleted
	 */
	public void deleteProfile(String appToken, String profileId);
	/**
	 * Deletes all the attributes for a profile
	 * 
	 * @param appToken previously returned by Profile server
	 * @param profileId is going to be used to delete all its attributes
	 */
	public void deleteAllAttributesForProfile(String appToken, String profileId);
	/**
	 * Deletes some attributes for a profile
	 * 
	 * @param appToken previously returned by Profile server
	 * @param profileId is going to be used to delete all its attributes
	 * @param attributes name list that is going to be used to be deleted
	 */
	public void deleteAttributesForProfile(String appToken, String profileId, List<String> attributes);
	/**
	 * Get a list of profiles based on the arguments passed.
	 * @param tenantName current tenant id
	 * @param appToken previously returned by Profile server
	 * @param start profile index to start
	 * @param end profile index to end
	 * @param sortBy is the field used to sort
	 * @param sortOrder valid value asc or desc
	 * @param attributes will be used to populate each profile with those attributes.
	 * @return a list of profiles
	 */
	public List<Profile> getProfileRange(String appToken, String tenantName, int start, int end, String sortBy, String sortOrder, List<String> attributes);
	/**
	 * Gets a list profiles
	 * @param appToken previously returned by Profile server
	 * @param profileIds these ids list is used to filter the profiles
	 * @return a list of profiles
	 */
	public List<Profile> getProfiles(String appToken, List<String> profileIds);
	/**
	 * Gets a list profiles which are going to have all the attributes
	 * @param appToken previously returned by Profile server
	 * @param profileIds these ids list is used to filter the profiles
	 * @return a list of profiles
	 */
	public List<Profile> getProfilesWithAllAttributes(String appToken, List<String> profileIds);

	/**
	 * Gets attributes for a profile
	 * @param appToken previously returned by Profile server
	 * @param profileId used to get the attributes
	 * @param attributes will be used to filter the attributes.
	 * @return an attribute pair list
	 */
	public Map<String, Serializable> getAttributesForProfile(String appToken, String profileId, List<String> attributes);
	/**
	 * Get all the attributes for a profile
	 * @param appToken previously returned by Profile server
	 * @param profileId used to get the attributes
	 * @return all the attributes of a profile 
	 */
	public Map<String, Serializable> getAllAttributesForProfile(String appToken, String profileId);
	
	/**
	 * Gets the app token
	 * @param appUsername used to filter the app token
	 * @param appPassword used to filter the app token
	 * @return the app token
	 * @throws AppAuthenticationFailedException if an authentication error occurs.
	 */
	public String getAppToken(String appUsername, String appPassword) throws AppAuthenticationFailedException;
	/**
	 * Gets a ticket based of the username and password 
	 * @param appToken previously returned by Profile server
	 * @param username ticket's user name
	 * @param password ticket's password
	 * @param tenantName
	 * @return The ticket found
	 * @throws UserAuthenticationFailedException if an exception occurs
	 */
	public String getTicket(String appToken, String username, String password, String tenantName) throws UserAuthenticationFailedException;
	/**
	 * Gets if a ticket value is valid
	 * @param appToken previously returned by Profile server
	 * @param ticket to be verified
	 * @return <code>true</code> if it is valid otherwise <code>false</code>
	 */
	public boolean isTicketValid(String appToken, String ticket);
	/**
	 * Invalidate a ticket value
	 * 
	 * @param appToken previously returned by Profile server
	 * @param ticket to be invalidated
	 */
	public void invalidateTicket(String appToken, String ticket);

	/**
	 * Sets attributes for one schema
	 * @param appToken previously returned by Profile server
	 * @param tenantName that is going to be updated
	 * @param attribute Attribute to be set
	 */
	public void setAttributeForSchema(String appToken, String tenantName, Attribute attribute);

    /**
	 * Gets the schema by tenant Id
	 * @param appToken previously returned by Profile server
	 * @param tenantName Current tenant identifier
	 * @return updated schema
	 */
	public Schema getSchema(String appToken, String tenantName);
	/**
	 * Deletes all the attributes for one Schema
	 * @param appToken previously returned by Profile server
	 * @param tenantName that is going to be updated with the attributes
	 * @param attributeName name of the attribute to be deleted
	 */
	public void deleteAttributeForSchema(String appToken, String tenantName, String attributeName);
	/**
	 * Sets the property configuration file. If this file is not set then the Profile Rest Client will try to find a property file name:
	 * profile-client-custom.properties.
	 * If that file is not found then it will used the default values:
	 * port=8080
	 * scheme = "http";
	 * host = "localhost";
	 * profileAppPath = "/crafter-profile";
	 * @param fileUrl Path and filename to the properties file. 
	 */
	public void setPropertiesFile(String fileUrl);
	
	/**
	 * Gets the config properties file name
	 * 
	 * @return properties file config name
	 */
	public String getPropertiesFile();

	/**
	 * Get all roles from the tenant
	 * 
	 * @param appToken Current app token
	 * @param tenantName Tenant identifier
	 * @return the role list already setup
	 */
	public List<Role> getAllRoles(String appToken, String tenantName);

    /**
     * Get all roles from the tenant
     *
     * @param appToken Current app token
     * @return the role list already setup
     */
    public List<Role> getAllRoles(String appToken);

	/**
	 * Creates a new role
	 * @param appToken Current app token
	 * @param roleName
	 * @param tenantName
	 * @return
	 */
	public Role createRole(String appToken, String roleName,String tenantName);
	
	public void deleteRole(String appToken, String roleName, String tenantName);

    /**
     * Creates a new Tenant
     *
     * @param appToken
     * @param tenantName
     * @param roles
     * @param domains
     * @param createDefaults
     * @return
     */

	public Tenant createTenant(String appToken, String tenantName, List<String> roles, List<String> domains, boolean createDefaults);

    /**
     * Update the Tenant that match the given Id
     * @param appToken
     * @param id
     * @param tenantName
     * @param roles
     * @param domains
     * @return
     */
    public Tenant updateTenant(String appToken,  String id,  String tenantName, List<String> roles, List<String> domains);

    /**
     * Delete a Tenant that match the given Id
     * @param appToken
     * @param tenantName
     */
    public void deleteTenant(String appToken, String tenantName);

    /**
     * Retrieve a Tenant that match the given name
     * @param appToken
     * @param tenantName
     * @return
     */
    public Tenant getTenantByName(String appToken, String tenantName);

    /**
     *  Retrieve a Tenant that match the given id
     * @param appToken
     * @param tenantId
     * @return
     */
    public Tenant getTenantById(String appToken, String tenantId);

    /**
     *  Retrieve a Tenant with the current ticket
     * @param appToken
     * @param ticket
     * @return
     */
    public Tenant getTenantByTicket(String appToken, String ticket);

    /**
     * Get a true value if there is a Tenant with the given Id
     * @param appToken
     * @param tenantName
     * @return
     */
    public boolean exitsTenant(String appToken, String tenantName);


    /**
     * Get the number of Tenants
     * @param appToken
     * @return
     */
    public long getTenantCount(String appToken);

    /**
     * Get a list of Tenants in the given range
     * @param appToken
     * @param sortBy
     * @param sortOrder
     * @param start
     * @param end
     * @return
     */
    public List<Tenant> getTenantRange(String appToken, String sortBy, String sortOrder, int start, int end);

    /**
     * Get a list of all the tenants
     *
     * @return
     */
    public List<Tenant> getAllTenants(String appToken);

    /**
     * Get a list of all the profiles that has the given Role
     * @param appToken
     * @param roleName
     * @param tenantName
     * @return
     */
    public List<Profile> getProfilesByRoleName(String appToken, String roleName, String tenantName);
    
    public void setMaxTotal(int maxTotal);
    
	/**
	 * Sets the max per route
	 * 
	 * @param maxPerRoute number will be used
	 */
	public void setMaxPerRoute(int maxPerRoute);
	
	/**
	 * Sets default max per route
	 * @param defaultMaxPerRoute
	 */
	public void setDefaultMaxPerRoute(int defaultMaxPerRoute);
	
	/**
	 * Sets port. Default port 8080
	 * @param port number
	 */
	public void setPort(int port);
	/**
	 * Sets the host name.
	 * @param host name
	 */
	public void setHost(String host);
	/**
	 * Sets scheme
	 * @param scheme
	 */
	public void setScheme(String scheme);
	/**
	 * Sets profile path value.
	 * @param path to the server
	 */
	public void setProfileAppPath(String path);
		
}