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
	public Profile createProfile(String userName, String password, Boolean active, String tenantName, Map<String, Serializable> attributes, List<String> roles);

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
	public Profile updateProfile(String profileId, String userName, String password, Boolean active, String tenantName, 
			Map<String, Serializable> attributes, List<String> roles);

	/**
	 * Get Profile
	 * 
	 * @param ticket
	 * @return
	 */
	public Profile getProfileByTicket(String ticket);

	/**
	 * Get Profile
	 * 
	 * @param ticket
	 * @param attributes
	 * @return
	 */
	public Profile getProfileByTicket(String ticket, List<String> attributes);

	/**
	 * Get Profile
	 * 
	 * @param ticket
	 * @return
	 */
	public Profile getProfileByTicketWithAllAttributes(String ticket);
	
	/**
	 * Get Profile
	 * 
	 * @param appToken
	 * @param profileId
	 * @return
	 */
	public Profile getProfile(String profileId);

	/**
	 * Get Profile with requested Attributes
	 * 
	 * @param profileId
	 * @param attributes
	 * @return
	 */
	public Profile getProfile(String profileId, List<String> attributes);

	/**
	 * Get Profile with Attributes
	 * 
	 * @param appToken
	 * @param profileId
	 * @return
	 */
	public Profile getProfileWithAllAttributes(String profileId);

	/**
	 * Get Profiles in Range with Sort Order
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder, List<String> attributesList, int start, int end);

	/**
	 * Get Profiles
	 * 
	 * @param appToken
	 * @param profileIdList
	 * @return
	 */
	public List<Profile> getProfiles(List<String> profileIdList);

	/**
	 * Get Profiles with Attributes
	 * 
	 * @param appToken
	 * @param profileIdList
	 * @return
	 */
	public List<Profile> getProfilesWithAttributes(List<String> profileIdList);

	/**
	 * Get Total Number of Profiles Count
	 * 
	 * @return
	 */
	public long getProfilesCount(String tenantName);

	/**
	 * Delete Profile
	 * 
	 * @param appToken
	 * @param profileId
	 */
	public void deleteProfile(String profileId);

	/**
	 * Delete all profiles
	 */
	public void deleteProfiles();

	/**
	 * Set attributes to Profile
	 * 
	 * @param appToken
	 * @param profileId
	 * @param key
	 * @param value
	 */
	public void setAttributes(String profileId, Map<String, Serializable> attributes);

	/**
	 * 
	 * Get Attributes requested
	 * 
	 * @param profileId
	 * @param attributes
	 * @return
	 */
	public Map<String, Serializable> getAttributes(String profileId, List<String> attributes);

	/**
	 * Get Attributes
	 * 
	 * @param appToken
	 * @param profileId
	 * @return
	 */
	public Map<String, Serializable> getAllAttributes(String profileId);

	/**
	 * Get Attribute
	 * 
	 * @param profileId
	 * @param attributeKey
	 * @return
	 */
	public Map<String, Serializable> getAttribute(String profileId, String attributeKey);

	/**
	 * Delete All Attributes
	 * 
	 * @param appToken
	 * @param profileId
	 */
	public void deleteAllAttributes(String profileId);

	/**
	 * Delete Attributes
	 * 
	 * @param attributesMap
	 */
	public void deleteAttributes(String profileId, List<String> attributes);

	/**
	 * Get Profile
	 * 
	 * @param userName
	 * @return
	 */
	public Profile getProfileByUserName(String userName, String tenantName);

	/**
	 * Get Profile
	 * 
	 * @param userName
	 * @param attributes
	 * @return
	 */
	public Profile getProfileByUserName(String userName, String tenantName, List<String> attributes);

	/**
	 * Get Profile
	 * 
	 * @param userName
	 * @return
	 */
	public Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName);

	void deleteProfiles(String tenantName);
	
	public List<Profile> getProfilesByRoleName(String roleName, String tenantName);

	void deleteRole(String roleName, String tenantName);

}