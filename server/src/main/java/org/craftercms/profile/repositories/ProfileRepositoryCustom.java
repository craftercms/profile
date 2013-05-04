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
package org.craftercms.profile.repositories;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.domain.Profile;

public interface ProfileRepositoryCustom {
	public List<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder, List<String> attributesList, int start, int end);
	public long getProfilesCount(String tenantName);
	public Profile getProfile(String profileId);
	public Profile getProfile(String profileId, List<String> attributes);
	public List<Profile> getProfiles(List<String> profileIdList);
	public List<Profile> getProfilesWithAttributes(List<String> profileIdList);
	public void setAttributes(String profileId, Map<String, Serializable> attributes);
	public Map<String, Serializable> getAllAttributes(String profileId);
	public Map<String, Serializable> getAttributes(String profileId, List<String> attributes);
	public Map<String, Serializable> getAttribute(String profileId, String attributeKey);
	public void deleteAllAttributes(String profileId);
	public void deleteAttributes(String profileId, List<String> attributesMap);
	public Profile getProfileByUserName(String userName, String tenantName);
	public Profile getProfileByUserName(String userName, String tenantName, List<String> attributes);
	public Profile getProfileByUserNameWithAllAttributes(String userName, String tenantName);
	public List<Profile> getProfilesByTenantName(String tenantName);
	void deleteRole(String profileId, String roleName);
	
}