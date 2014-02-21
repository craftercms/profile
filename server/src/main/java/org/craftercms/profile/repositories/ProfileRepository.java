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

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.ProfileException;

/**
 * Defines the Profile repository services.
 */
public interface ProfileRepository extends CrudRepository<Profile> {

    /**
     * Finds a Profile by its id.
     * @param id Id of the profile.
     * @return The Profile is one exists with the given Id.<b>null</b> if nothing is found.
     * @throws ProfileException If Profile can't be search for.
     */
    Profile findById(ObjectId id) throws ProfileException;

    /**
     * Finds all the profiles with a given Tenant and Role.
     *
     * @param role Role of the profile looking for.
     * @param tenantName Tenant Name of the profile.
     * @return A Iterator of all profiles found.<b>null</b> if nothing is found.
     * @throws ProfileException If Profiles can't be search for.
     */
    Iterable<Profile> findByRolesAndTenantName(String role, String tenantName) throws ProfileException;

    Iterable<Profile> getProfileRange(String tenantName, String sortBy, String sortOrder,
                                      List<String> attributesList, int start, int end) throws ProfileException;

    /**
     * Counts the profiles for a given tenant.
     * @param tenantName Name of the tenant to count.
     * @return the amount of profiles for a given tenant.
     * @throws ProfileException If Profiles can't be count.
     */
    long getProfilesCount(String tenantName) throws ProfileException;

    /**
     * Returns the Profile with the given id.<br/>
     * <i>short hand of <code>findById(new ObjectId(profileId))</code</i>
     * @see org.craftercms.profile.repositories.ProfileRepository#findById(org.bson.types.ObjectId)
     * @param profileId Id of the profile.
     * @return The Profile is one exists with the given Id.<b>null</b> if nothing is found.
     * @throws ProfileException If Profile can't be search for.
     */
    Profile getProfile(String profileId) throws ProfileException;

    Profile getProfile(String profileId, List<String> attributes) throws ProfileException;

    Iterable<Profile> getProfiles(List<String> profileIdList) throws ProfileException;

    Iterable<Profile> getProfilesWithAttributes(List<String> profileIdList) throws ProfileException;

    void setAttributes(String profileId, Map<String, Object> attributes) throws ProfileException;

    Map<String, Object> getAllAttributes(String profileId) throws ProfileException;

    Map<String, Object> getAttributes(String profileId, List<String> attributes) throws ProfileException;

    Map<String, Object> getAttribute(String profileId, String attributeKey) throws ProfileException;

    void deleteAllAttributes(String profileId) throws ProfileException;

    void deleteAttributes(String profileId, List<String> attributesMap) throws ProfileException;

    Profile getProfileByUserName(String userName, String tenantName) throws ProfileException;

    Profile getProfileByUserName(String userName, String tenantName, List<String> attributes) throws ProfileException;

    Iterable<Profile> getProfilesByTenantName(String tenantName) throws ProfileException;

    void deleteRole(String profileId, String roleName) throws ProfileException;

    Iterable<Profile> findByAttributeAndValue(String attribute, String attributeValue) throws ProfileException;

    void delete(Iterable<Profile> profilesByTenant) throws ProfileException;
}