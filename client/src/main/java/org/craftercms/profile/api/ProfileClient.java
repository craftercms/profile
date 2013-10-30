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

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.impl.domain.*;

/**
 * Crafter Profile Client exposes an API that can be used to communicate with Crafter Profile Server
 *
 * @author Alvaro Gonzalez
 */
public interface ProfileClient {
    /**
     * Gets the total number of profiles for a particular tenant
     *
     * @param appToken   The appToken previously returned by Crafter Profile Server
     * @param tenantName Tenant name for profiles
     * @return number of profiles.
     */
    public long getProfileCount(String appToken, String tenantName);

    /**
     * Gets a profile based on a profile id.
     *
     * @param appToken  The appToken previously returned by Crafter Profile Server
     * @param profileId used to get the profile data
     * @return a profile account object
     */
    public Profile getProfile(String appToken, String profileId);

    /**
     * Gets a profile based on a profile id and return only the attributes specified in the attributes parameter
     *
     * @param appToken   The appToken previously returned by Crafter Profile Server
     * @param profileId  used to get the profile data
     * @param attributes The attributes name list that is going to be used to populate the profile object will be
     *                   returned
     * @return a profile object with the attributes passed in the attributes parameter.
     */
    public Profile getProfileWithAttributes(String appToken, String profileId, List<String> attributes);

    /**
     * Gets a profile based on a profile id and that profile will have all the attributes.
     *
     * @param appToken  The appToken previously returned by Crafter Profile Server
     * @param profileId used to get the profile data
     * @return a profile object with all the attributes.
     */
    public Profile getProfileWithAllAttributes(String appToken, String profileId);

    /**
     * Get a profile base on a username.
     *
     * @param appToken   The appToken previously returned by Crafter Profile Server
     * @param username   used to get the profile data
     * @param tenantName Current tenantName for profiles
     * @return the profile object found.
     */
    public Profile getProfileByUsername(String appToken, String username, String tenantName);

    /**
     * Gets a profile based on the profile's unique username & tenant pairing and return only the attributes
     * specified in the attributes parameter
     *
     * @param appToken   The appToken previously returned by Crafter Profile Server
     * @param username   used to get the profile data
     * @param tenantName Current tenantName for profiles
     * @param attributes The attributes name list that is going to be used to populate the profile object will be
     *                   returned
     * @return a profile object with the specified attributes.
     */
    public Profile getProfileByUsernameWithAttributes(String appToken, String username, String tenantName,
                                                      List<String> attributes);

    /**
     * Gets a profile based on a username & tenant unique pairing and that profile will have all the attributes.
     *
     * @param appToken   The appToken previously returned by Crafter Profile Server
     * @param username   used to get the profile data
     * @param tenantName Current tenantName for profiles
     * @return a profile object with all the attributes.
     */
    public Profile getProfileByUsernameWithAllAttributes(String appToken, String username, String tenantName);

    /**
     * Get a profile based on a ticket.
     *
     * @param appToken The appToken previously returned by Crafter Profile Server
     * @param ticket   used to get the profile data
     * @return a profile object
     */
    public Profile getProfileByTicket(String appToken, String ticket);

    /**
     * Gets a profile based on a ticket and return only the attributes specified in the attributes parameter
     *
     * @param appToken   previously returned by Crafter Profile Server
     * @param ticket     used to get the profile data
     * @param attributes name list that is going to be used to populate the profile object will be returned
     * @return a profile object with some attributes
     */
    public Profile getProfileByTicketWithAttributes(String appToken, String ticket, List<String> attributes);

    /**
     * Gets a profile based on a ticket and that profile will have all the attributes.
     *
     * @param appToken previously returned by Crafter Profile Server
     * @param ticket   used to get the profile data
     * @return a profile object with all the attributes
     */
    public Profile getProfileByTicketWithAllAttributes(String appToken, String ticket);

    /**
     * Creates a new profile with the specified parameters
     *
     * @param appToken    previously returned by Crafter Profile Server
     * @param queryParams is a key pair list of parameters that will be sent to the profile server to create the new
     *                    profile
     * @return the new profile object.
     */
    public Profile createProfile(String appToken, Map<String, Serializable> queryParams);

    /**
     * Creates a new profile based on the parameters values received.
     *
     * @param appToken    previously returned by Crafter Profile Server
     * @param userName    of the new profile
     * @param password    of the new profile
     * @param active      <code>true</code indicates the new profile will be activated otherwise <code>false</code>
     * @param tenantName  of the new profile
     * @param email       Email profile
     * @param queryParams is a key pair list of parameters that will be sent to the profile server to create the new
     *                    profile
     * @return the new profile object
     */
    public Profile createProfile(String appToken, String userName, String password, Boolean active,
                                 String tenantName, String email, Map<String, Serializable> queryParams);

    /**
     * Updates a profile using the parameters data
     *
     * @param appToken    previously returned by Crafter Profile Server
     * @param queryParams is a key pair list of parameters that will be sent to the profile server to update the profile
     * @return the profile just updated
     */
    public Profile updateProfile(String appToken, Map<String, Serializable> queryParams);

    /**
     * Updates the profile's attributes
     *
     * @param appToken    previously returned by Crafter Profile Server
     * @param profileId   is going to be updated with the new attributes values
     * @param queryParams key pair list of attributes that will be updated
     */
    public void setAttributesForProfile(String appToken, String profileId, Map<String, Serializable> queryParams);

    /**
     * Actives a profile
     *
     * @param appToken  previously returned by Crafter Profile Server
     * @param profileId is going to be deleted
     * @param active    indicates if the profile will be actived.
     */
    public void activeProfile(String appToken, String profileId, boolean active);

    /**
     * Deletes all the attributes for a profile
     *
     * @param appToken  previously returned by Crafter Profile Server
     * @param profileId is going to be used to delete all its attributes
     */
    public void deleteAllAttributesForProfile(String appToken, String profileId);

    /**
     * Deletes some attributes for a profile
     *
     * @param appToken   previously returned by Crafter Profile Server
     * @param profileId  is going to be used to delete all its attributes
     * @param attributes name list that is going to be used to be deleted
     */
    public void deleteAttributesForProfile(String appToken, String profileId, List<String> attributes);

    /**
     * Get a list of profiles based on the arguments passed.
     *
     * @param tenantName current tenant id
     * @param appToken   previously returned by Crafter Profile Server
     * @param start      profile index to start
     * @param end        profile index to end
     * @param sortBy     is the field used to sort
     * @param sortOrder  valid value asc or desc
     * @param attributes will be used to populate each profile with those attributes.
     * @return a list of profiles
     */
    public List<Profile> getProfileRange(String appToken, String tenantName, int start, int end, String sortBy,
                                         String sortOrder, List<String> attributes);

    /**
     * Gets a list profile but profile IDs
     *
     * @param appToken   previously returned by Crafter Profile Server
     * @param profileIds these ids list is used to filter the profiles
     * @return a list of profiles
     */
    public List<Profile> getProfiles(String appToken, List<String> profileIds);

    /**
     * Gets a list profiles which are going to have all the attributes
     *
     * @param appToken   previously returned by Crafter Profile Server
     * @param profileIds these ids list is used to filter the profiles
     * @return a list of profiles
     */
    public List<Profile> getProfilesWithAllAttributes(String appToken, List<String> profileIds);

    /**
     * Gets attributes for a profile
     *
     * @param appToken   previously returned by Crafter Profile Server
     * @param profileId  used to get the attributes
     * @param attributes will be used to filter the attributes.
     * @return an attribute pair list
     */
    public Map<String, Serializable> getAttributesForProfile(String appToken, String profileId,
                                                             List<String> attributes);

    /**
     * Get all the attributes for a profile
     *
     * @param appToken  previously returned by Crafter Profile Server
     * @param profileId used to get the attributes
     * @return all the attributes of a profile
     */
    public Map<String, Serializable> getAllAttributesForProfile(String appToken, String profileId);

    /**
     * Authenticates the app credentials against Crafter Profile Server and returns the app authentication token
     *
     * @param appUsername used to filter the app token
     * @param appPassword used to filter the app token
     * @return the app token
     * @throws AppAuthenticationFailedException
     *          if an authentication error occurs.
     */
    public String getAppToken(String appUsername, String appPassword) throws AppAuthenticationFailedException;

    /**
     * Authenticates the user and returns the generated authentication ticket
     *
     * @param appToken   previously returned by Crafter Profile Server
     * @param username   ticket's user name
     * @param password   ticket's password
     * @param tenantName
     * @return The ticket found
     * @throws UserAuthenticationFailedException
     *          if an exception occurs
     */
    public String getTicket(String appToken, String username, String password,
                            String tenantName) throws UserAuthenticationFailedException;

    /**
     * Authenticates the user and returns the generated authentication ticket.  Password will be ignored if SSO is
     * false.
     *
     * @param appToken   previously returned by Crafter Profile Server
     * @param username   ticket's user name
     * @param password   ticket's password
     * @param tenantName
     * @param sso        indication whether this is an sso request or not, if not password is ignored if provided
     * @return The ticket found
     * @throws UserAuthenticationFailedException
     *          if an exception occurs
     */
    public String getTicket(String appToken, String username, String password, String tenantName,
                            boolean sso) throws UserAuthenticationFailedException;

    /**
     * True if the ticket is valid
     *
     * @param appToken previously returned by Crafter Profile Server
     * @param ticket   to be verified
     * @return <code>true</code> if it is valid otherwise <code>false</code>
     */
    public boolean isTicketValid(String appToken, String ticket);

    /**
     * Invalidate a ticket value
     *
     * @param appToken previously returned by Crafter Profile Server
     * @param ticket   to be invalidated
     */
    public void invalidateTicket(String appToken, String ticket);


    /**
     * Sets attributes for one schema
     *
     * @param appToken   Current app token previously returned by Crafter Profile Server
     * @param tenantName that is going to be updated
     * @param attribute  Attribute to be set
     */
    public void setAttributeForSchema(String appToken, String tenantName, Attribute attribute);

    /**
     * Gets the schema by tenant Id
     *
     * @param appToken   Current app token previously returned by Crafter Profile Server
     * @param tenantName Current tenant identifier
     * @return updated schema
     */
    public Schema getSchema(String appToken, String tenantName);

    /**
     * Deletes all the attributes for one Schema
     *
     * @param appToken      Current app token previously returned by Crafter Profile Server
     * @param tenantName    that is going to be updated with the attributes
     * @param attributeName name of the attribute to be deleted
     */
    public void deleteAttributeForSchema(String appToken, String tenantName, String attributeName);

    /**
     * Get system roles
     *
     * @param appToken Current app token previously returned by Crafter Profile Server
     * @return the role list already setup
     */
    public List<Role> getAllRoles(String appToken);

    /**
     * Creates a new role
     *
     * @param appToken Current app token previously returned by Crafter Profile Server
     * @param roleName
     * @return newly created Role
     */
    public Role createRole(String appToken, String roleName);

    /**
     * Deletes a system role based on its name
     *
     * @param appToken Current app token previously returned by Crafter Profile Server
     * @param roleName The role name to be deleted
     */
    public void deleteRole(String appToken, String roleName);

    /**
     * Creates a new Tenant
     *
     * @param appToken       Current app token previously returned by Crafter Profile Server
     * @param tenantName
     * @param roles
     * @param domains
     * @param createDefaults
     * @param emailNewProfile Indicates if a verification email is going to send to the email account to verify the account
     * @return new tenant
     */
    public Tenant createTenant(String appToken, String tenantName, List<String> roles, List<String> domains,
                               boolean createDefaults, boolean emailNewProfile);

    /**
     * Update the Tenant that match the given Id
     *
     * @param appToken   Current app token previously returned by Crafter Profile Server
     * @param id
     * @param tenantName
     * @param roles
     * @param domains
     * @param emailNewProfile Indicates if a verification email is sent whenever a new user profile is create
     * @return updated tenant
     */
    public Tenant updateTenant(String appToken, String id, String tenantName, List<String> roles, List<String> domains, boolean emailNewProfile);

    /**
     * Delete a Tenant that match the given tenant name
     *
     * @param appToken   Current app token previously returned by Crafter Profile Server
     * @param tenantName
     */
    public void deleteTenant(String appToken, String tenantName);

    /**
     * Retrieve a Tenant that match the given name
     *
     * @param appToken   Current app token previously returned by Crafter Profile Server
     * @param tenantName
     * @return Tenant
     */
    public Tenant getTenantByName(String appToken, String tenantName);

    /**
     * Retrieve a Tenant that match the given id
     *
     * @param appToken Current app token previously returned by Crafter Profile Server
     * @param tenantId
     * @return
     */
    public Tenant getTenantById(String appToken, String tenantId);

    /**
     * Retrieve a Tenant with the current ticket
     *
     * @param appToken
     * @param ticket
     * @return Tenant
     */
    public Tenant getTenantByTicket(String appToken, String ticket);

    /**
     * Returns trueif there is a Tenant with the given name
     *
     * @param appToken   Current app token previously returned by Crafter Profile Server
     * @param tenantName
     * @return
     */
    public boolean exitsTenant(String appToken, String tenantName);


    /**
     * Get the number of Tenants
     *
     * @param appToken Current app token previously returned by Crafter Profile Server
     * @return tenant count
     */
    public long getTenantCount(String appToken);

    /**
     * Get a list of Tenants in the given range
     *
     * @param appToken  Current app token previously returned by Crafter Profile Server
     * @param sortBy
     * @param sortOrder
     * @param start
     * @param end
     * @return List of tenants matching the parameters
     */
    public List<Tenant> getTenantRange(String appToken, String sortBy, String sortOrder, int start, int end);

    /**
     * Get a list of all the tenants
     *
     * @param appToken Current app token previously returned by Crafter Profile Server
     * @return Complete list of tenants in the system
     */
    public List<Tenant> getAllTenants(String appToken);

    /**
     * Get a list of all the tenants configured with the specified role name
     *
     * @param appToken Current app token previously returned by Crafter Profile Server
     * @param roleName
     * @return List of tenants
     */
    public List<Tenant> getTenantsByRoleName(String appToken, String roleName);

    /**
     * Get a list of all the profiles that has the given role and are in the specific tenant
     *
     * @param appToken   Current app token previously returned by Crafter Profile Server
     * @param roleName
     * @param tenantName
     * @return Profile list
     */
    public List<Profile> getProfilesByRoleName(String appToken, String roleName, String tenantName);

    /**
     * Sets the max total
     *
     * @param maxTotal
     */
    public void setMaxTotal(int maxTotal);

    /**
     * Sets the max per route
     *
     * @param maxPerRoute number will be used
     */
    public void setMaxPerRoute(int maxPerRoute);

    /**
     * Sets default max per route
     *
     * @param defaultMaxPerRoute
     */
    public void setDefaultMaxPerRoute(int defaultMaxPerRoute);

    /**
     * Sets port. Default port 8080
     *
     * @param port number
     */
    public void setPort(int port);

    /**
     * Sets the host name.
     *
     * @param host name
     */
    public void setHost(String host);

    /**
     * Sets scheme
     *
     * @param scheme
     */
    public void setScheme(String scheme);

    /**
     * Sets profile path value.
     *
     * @param path to the server
     */
    public void setProfileAppPath(String path);

    /**
     * Creates a new GroupRole Mapping
     *
     * @param appToken   Current app token
     * @param tenantName Tenant name
     * @param groupName  The group name
     * @param roles      The roles mapped to this group
     * @return the new group mapped instance.
     */
    public GroupRole createGroupRoleMapping(String appToken, String tenantName, String groupName, List<String> roles);

    /**
     * Update the group-role mapping that match the given group id
     *
     * @param appToken   Current application token
     * @param groupId    Group id to be update
     * @param tenantName Tenant name
     * @param role       new role list to be mapped
     * @return the group mapped updated.
     */
    public GroupRole updateGroupRoleMapping(String appToken, String groupId, String tenantName, List<String> role);

    /**
     * Gets a list of roles that are in one profile and also are in the groups passed as parameters
     *
     * @param appToken   Current application token
     * @param profileId  to be used to compare its roles with the groups
     * @param tenantName The tenant name
     * @param groups     is an array of group mapping role names.
     * @return Lists of roles that are in the profile and also in the groups pass as argument
     */
    public List<String> getRoles(String appToken, String profileId, String tenantName, String[] groups);

    /**
     * Gets a list of roles that are in one profile and also are in the groups mapping of the tenant passes as argument
     *
     * @param appToken   Current application token
     * @param profileId  to be used to compare its roles with the groups
     * @param tenantName The tenant name
     * @return Lists of roles that are in the profile and also in the groups pass as argument
     */
    public List<String> getRoles(String appToken, String profileId, String tenantName);

    /**
     * Gets the list of Groups- Role mapping for a tenant
     *
     * @param appToken   Current application token
     * @param tenantName used to get the Group - Role mapping list
     * @return
     */
    public List<GroupRole> getGroupRoleMappingByTenant(String appToken, String tenantName);

    /**
     * Deletes a group role Mapping
     *
     * @param appToken Current application token
     * @param groupId  will be used to delte the group mapping
     */
    public void deleteGroupRoleMapping(String appToken, String groupId);

    /**
     * Gets a group - role mapping based on the groupId passes as argument
     *
     * @param appToken Current application token
     * @param groupId  Used to get the group role mapping
     * @return The group role retrieve
     */
    public GroupRole getGroupRoleMapping(String appToken, String groupId);

    /**
     * Forgot password service request to start the change password process
     *
     * @param changePasswordUrl valid url to the form will be used to capture the new password
     * @param username          id to the profile that will be changed its password
     * @param tenantName        of the username
     * @return a profile instance that the password was forgotten.
     */
    public Profile forgotPassword(String appToken, String changePasswordUrl, String tenantName, String username);

    /**
     * Reset profile password service request
     *
     * @param password new password will be set for the profile
     * @param a        token sent by email the user email account
     * @return a profile instance that the password was reset.
     */
    public Profile resetPassword(String appToken, String token, String newPassword);

    /**
     * Verifies a profile account using the encrypted token
     * 
     * @param appToken Application token
     * 
     * @param token Encrypted token sent by email
     * 
     * @return The profile instance verified
     */
	Profile verifyProfile(String appToken, String token);

	Profile addSubscription(String appToken, String profileId, String targetId,
			String targetDescription, String targetUrl);

	Profile addSubscription(String appToken, String profileId, Target target);
	
	Profile updateSubscription(String appToken, String profileId, Target target);

	Profile updateSubscription(String appToken, String profileId, String targetId,
			String targetDescription, String targetUrl);
	
	Profile removeSubscription(String appToken, String profileId, String targetId);
	
	Subscriptions getSubscriptions(String appToken, String profileId);

	Subscriptions setSubscriptions(String appToken, String profileId,
			Subscriptions subscriptions);

}