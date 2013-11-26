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
package org.craftercms.profile.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.InvalidEmailException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;

/**
 * Encapsulates the services to manage the profiles data.
 * 
 * @author Alvaro Gonzalez
 *
 */
public interface ProfileService {

    /**
     * Create a new profile in the profile repository, using the information provided in the parameters
     * @param userName The new profile username to identify the new profile account
     * 
     * @param password The new profile's password
     * 
     * @param active indicates if the account is activated
     * 
     * @param tenantName The tenant name
     * 
     * @param email Valid email account for this profile account
     * 
     * @param attributes a map (name, value) of addition information of this profile account
     * 
     * @param roles list of roles of the profile account
     * 
     * @param verificationAccountUrl verification account url
     * 
     * @param response current HttpServletResponse instance
     * 
     * @param request current HttpServletRequest instance
     * 
     * @return new Profile instance has just been created
     * 
     * @throws InvalidEmailException If the email account is not following a valid format
     * @throws NoSuchProfileException 
     * @throws MailException 
     * @throws CipherException 
     */
    Profile createProfile(String userName, String password, Boolean active, String tenantName, String email,
                          Map<String, Serializable> attributes, List<String> roles, String verificationAccountUrl,
                          HttpServletResponse response, HttpServletRequest request) throws InvalidEmailException, CipherException, MailException, NoSuchProfileException;


    /**
     * Updates a profile account with the information passed as a parameter
     * 
     * @param profileId unique identifier of the profile account that is going to be updated
     * 
     * @param userName The new profile username to identify the new profile account
     * 
     * @param password The new profile's password
     * 
     * @param active indicates if the account is activated
     * 
     * @param tenantName The tenant name
     * 
     * @param email Valid email account for this profile account
     * 
     * @param attributes a map (name, value) of addition information of this profile account
     * 
     * @param roles list of roles of the profile account
     * 
     * @return a profile instance updated
     */
    Profile updateProfile(String profileId, String userName, String password, Boolean active, String tenantName,
                          String email, Map<String, Serializable> attributes, List<String> roles);
    
    /**
     * Updates a profile account with the profile instance passes as parameter
     * 
     * @return a profile instance updated
     */
    Profile updateProfile(Profile profile);

    /**
     * Gets a profile account based on the ticket passed as a parameter
     * 
     * @param ticket a valid ticket used to query the profile
     * 
     * @return the profile account found
     */
    Profile getProfileByTicket(String ticket);

    /**
     * Gets a profile account based on the ticket passed as a parameter. The profile instance will have the attributes passed as parameter
     *
     * @param ticket a valid ticket used to query the profile
     * 
     * @param attributes the list of attributes will have the profile account returned
     * 
     * @return a profile account instance
     */
    Profile getProfileByTicket(String ticket, List<String> attributes);

    /**
     * Gets a profile account based on the ticket passed as a parameter.
     * 
     * @param ticket a valid ticket used to query the profile
     * 
     * @return a profile account instance that includes all the attributes
     */
    Profile getProfileByTicketWithAllAttributes(String ticket);

    /**
     * Get Profile using the unique identifier passed as a parameter
     *
     * @param profileId the unique identifier used to get the profile
     * 
     * @return a profile account instance 
     */
    Profile getProfile(String profileId);

    /**
     * Get Profile using the unique identifier passed as a parameter
     *
     * @param profileId the unique identifier used to get the profile
     * 
     * @param attributes list of attributes included in the profile instance returned
     * 
     * @return a profile account instance found
     */
    Profile getProfile(String profileId, List<String> attributes);

    /**
     * Get Profile using the unique identifier passed as a parameter and the instance will have all the attributes
     *
     * @param profileId the unique identifier used to get the profile
     * 
     * @return a profile account instance found
     */
    Profile getProfileWithAllAttributes(String profileId);

    /**
     * Gets a list of profile using pagination
     * 
     * @param tenantName the profiles returned belongs to the tenant name passed as a parameter
     * 
     * @param sortBy the profile property will be used to sort the profiles
     * 
     * @param sortOrder valid values DESC or ASC
     * 
     * @param attributesList The list of attributes included in the profile list
     * 
     * @param start marks the index where the pagination will start 
     * 
     * @param end marks the index where the pagination will end
     * 
     * @return
     */
    List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder, List<String> attributesList,
                                  int start, int end);

    /**
     * Get a profile list 
     *
     * @param profileIdList The profile unique identifiers list used to get the profiles
     * 
     * @return a profile list found in the repository
     */
    List<Profile> getProfiles(List<String> profileIdList);

    /**
     * Get a profile list 
     *
     * @param profileIdList The profile unique identifiers list used to get the profiles
     * 
     * @return a profile list found in the repository which all include the attributes
     */
    List<Profile> getProfilesWithAttributes(List<String> profileIdList);

    /**
     * Get Total Number of Profiles Count for a tenant
     *
     * @return the number of profiles for a tenant
     */
    long getProfilesCount(String tenantName);

    /**
     * Activate or in-activate a profile account, based on the <code>active</code> passed as parameter
     *
     * @param profileId unique identifier for the profile account that is going to be active or inactive
     * 
     * @param active <code>true</code> the profile accounts are going to be activated, <code>false</code> otherwise the account is going to be in-activated
     */
    void activateProfile(String profileId, boolean active);

    /**
     * Activate or in-activate all profiles based on the <code>active</code> passed as parameter
     * 
     * @param active <code>true</code> the profile accounts are going to be activated, <code>false</code> otherwise the account is going to be in-activated
     */
    void activateProfiles(boolean active);

    /**
     * Set attributes to Profile account based on a unique profile identifier
     *
     * @param profileId The unique profile identifier used to set the attributes
     * 
     * @param attributes the list of attributes to be set
     */
    void setAttributes(String profileId, Map<String, Serializable> attributes);

    /**
     * Gets the attributes mapping (name - value) for a profile account based on the unique profile identifier
     *
     * @param profileId The unique profile identifier used to get the attributes
     * 
     * @param attributes the attributes names are going to be returned
     * 
     * @return the attributes values
     */
    Map<String, Serializable> getAttributes(String profileId, List<String> attributes);

    /**
     * Gets the all attributes mapping (name - value) for a profile account based on the unique profile identifier passed as argument
     *
     * @param profileId The unique profile identifier used to get the attributes
     * 
     * 
     * @return all attributes values for the profile
     */
    Map<String, Serializable> getAllAttributes(String profileId);

    /**
     * Gets a attribute value (name - value) for a profile account based on the unique profile identifier and the attribute key passed as argument
     *
     * @param profileId The unique profile identifier used to get the attributes
     * 
     * @param attributeKey the attribute key name
     * 
     * @return the attribute key-value
     */
    Map<String, Serializable> getAttribute(String profileId, String attributeKey);

    /**
     * Delete All Attributes for a profile account based on the unique profile identifier passed as parameter
     *
     * @param profileId The profile identifier will be updated
     */
    void deleteAllAttributes(String profileId);

    /**
     * Delete the Attributes passed as parameter for a profile account also passed in the parameter
     *
     * @param profileId The profile identifier will be updated
     * 
     * @param attributes the attributes keys that are going to be deleted
     * 
     */
    void deleteAttributes(String profileId, List<String> attributes);

    /**
     * Gets a Profile instance based on the username and the tenant name passed as parameter
     *
     * @param userName the profile username is going to be used to get the profile account
     * 
     * @param tenantName the tenant name is going to be used to get the profile account
     * 
     * @return a profile instance
     */
    Profile getProfileByUserName(String userName, String tenantName);

    /**
     * Gets a Profile instance based on the username and the tenant name passed as parameter. The profile returned will include the attributes passed as a parameter list
     *
     * @param userName the profile username is going to be used to get the profile account
     * 
     * @param tenantName the tenant name is going to be used to get the profile account
     * 
     * @param attributes  the attributes keys that are going to be returned
     * 
     * @return a profile account
     */
    Profile getProfileByUserName(String userName, String tenantName, List<String> attributes);

    /**
     * Gets a Profile instance based on the username and the tenant name passed as parameter
     *
     * @param userName the profile username is going to be used to get the profile account
     * 
     * @param tenantName the tenant name is going to be used to get the profile account
     * 
     * @return a profile account which includes all the profile attributes
     */
    Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName);

    /**
     * Deletes all the profiles for a tenant name
     * 
     * @param tenantName name used to delete all the profiles
     */
    void deleteProfiles(String tenantName);

    /**
     * Get profile list based on the role and tenant name passed argument
     * 
     * @param roleName The role name
     * 
     * @param tenantName the tenant name
     * 
     * @return List of profiles that belongs to the tenant name and have the role name
     */
    List<Profile> getProfilesByRoleName(String roleName, String tenantName);

}