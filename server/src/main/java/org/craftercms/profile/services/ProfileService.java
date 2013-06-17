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

import org.craftercms.profile.domain.Profile;


public interface ProfileService {

	/**
	 * Create profile
	 * 
	 * @param appToken
	 * @param origin
	 * @param userName
	 * @param password
	 * @param email
	 * @param prefix
	 * @param firstName
	 * @param lastName
	 * @param role
	 * @param suffix
	 * @param attributes
	 */
	Profile createProfile(String userName, String password, Boolean active, String tenantName, Map<String, Serializable> attributes, List<String> roles);

	/**
	 * Update profile
	 * 
	 * @param appToken
	 * @param profileId
	 * @param origin
	 * @param userName
	 * @param password
	 * @param email
	 * @param prefix
	 * @param firstName
	 * @param lastName
	 * @param suffix
	 * @param attributes
	 */
	Profile updateProfile(String profileId, String userName, String password, Boolean active, String tenantName, 
			Map<String, Serializable> attributes, List<String> roles);

	/**
	 * Get Profile
	 * 
	 * @param ticket
	 * @return
	 */
	Profile getProfileByTicket(String ticket);

	/**
	 * Get Profile
	 * 
	 * @param ticket
	 * @param attributes
	 * @return
	 */
	Profile getProfileByTicket(String ticket, List<String> attributes);

	/**
	 * Get Profile
	 * 
	 * @param ticket
	 * @return
	 */
	Profile getProfileByTicketWithAllAttributes(String ticket);
	
	/**
	 * Get Profile
	 * 
	 * @param appToken
	 * @param profileId
	 * @return
	 */
	Profile getProfile(String profileId);

	/**
	 * Get Profile with requested Attributes
	 * 
	 * @param profileId
	 * @param attributes
	 * @return
	 */
	Profile getProfile(String profileId, List<String> attributes);

	/**
	 * Get Profile with Attributes
	 * 
	 * @param appToken
	 * @param profileId
	 * @return
	 */
	Profile getProfileWithAllAttributes(String profileId);

	/**
	 * Get Profiles in Range with Sort Order
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder, List<String> attributesList, int start, int end);

	/**
	 * Get Profiles
	 * 
	 * @param appToken
	 * @param profileIdList
	 * @return
	 */
	List<Profile> getProfiles(List<String> profileIdList);

	/**
	 * Get Profiles with Attributes
	 * 
	 * @param appToken
	 * @param profileIdList
	 * @return
	 */
	List<Profile> getProfilesWithAttributes(List<String> profileIdList);

	/**
	 * Get Total Number of Profiles Count
	 * 
	 * @return
	 */
	long getProfilesCount(String tenantName);

	/**
	 * Delete Profile
	 * 
	 * @param appToken
	 * @param profileId
	 */
	void deleteProfile(String profileId);

	/**
	 * Delete all profiles
	 */
	void deleteProfiles();

	/**
	 * Set attributes to Profile
	 * 
	 * @param appToken
	 * @param profileId
	 * @param key
	 * @param value
	 */
	void setAttributes(String profileId, Map<String, Serializable> attributes);

	/**
	 * 
	 * Get Attributes requested
	 * 
	 * @param profileId
	 * @param attributes
	 * @return
	 */
	Map<String, Serializable> getAttributes(String profileId, List<String> attributes);

	/**
	 * Get Attributes
	 * 
	 * @param appToken
	 * @param profileId
	 * @return
	 */
	Map<String, Serializable> getAllAttributes(String profileId);

	/**
	 * Get Attribute
	 * 
	 * @param profileId
	 * @param attributeKey
	 * @return
	 */
	Map<String, Serializable> getAttribute(String profileId, String attributeKey);

	/**
	 * Delete All Attributes
	 * 
	 * @param appToken
	 * @param profileId
	 */
	void deleteAllAttributes(String profileId);

	/**
	 * Delete Attributes
	 * 
	 * @param attributesMap
	 */
	void deleteAttributes(String profileId, List<String> attributes);

	/**
	 * Get Profile
	 * 
	 * @param userName
	 * @return
	 */
	Profile getProfileByUserName(String userName, String tenantName);

	/**
	 * Get Profile
	 * 
	 * @param userName
	 * @param attributes
	 * @return
	 */
	Profile getProfileByUserName(String userName, String tenantName, List<String> attributes);

	/**
	 * Get Profile
	 * 
	 * @param userName
	 * @return
	 */
	Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName);

	void deleteProfiles(String tenantName);
	
	List<Profile> getProfilesByRoleName(String roleName, String tenantName);

	void deleteRole(String roleName, String tenantName);

}