/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;

/**
 * DB repository for {@link org.craftercms.profile.api.Profile}s.
 *
 * @author avasquez
 */
public interface ProfileRepository extends CrudRepository<Profile> {

    /**
     * Returns the single profile that matches the specified query
     *
     * @param query                 the Mongo query used to search for the profiles
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the profile, or null if not found
     */
    Profile findOneByQuery(String query, String... attributesToReturn) throws MongoDataException;

    /**
     * Returns the profile with the given ID.
     *
     * @param id                    the profile's ID
     * @param attributesToReturn    the name of the attributes to return (null to return all)
     * @return the profile found, or null if not found
     */
    Profile findById(String id, String... attributesToReturn) throws MongoDataException;

    /**
     * Returns the profile for the given tenant name and username.
     *
     * @param tenantName            the tenant's name
     * @param username              the profile's username
     * @param attributesToReturn    the name of the attributes to return (null to return all)
     * @return the profile found, or null if not found
     */
    Profile findByTenantAndUsername(String tenantName, String username, String... attributesToReturn)
            throws MongoDataException;

    /**
     * Returns the profiles that match the specified query.
     *
     * @param query                 the Mongo query used to search for the profiles. Must not contain the $where
     *                              operator, the tenant's name (already specified) or any non-readable attribute
     *                              by the application
     * @param attributesToReturn    the names of the attributes to return with the profile (null to return all
     *                              attributes)
     *
     * @return the list of profiles found, or null if none match the query
     */
    Iterable<Profile> findByQuery(String query, String sortBy, SortOrder sortOrder, Integer start, Integer count,
                                  String... attributesToReturn) throws MongoDataException;

    /**
     * Returns the profiles with the given IDs.
     *
     * @param ids                   the IDs of the profiles to return
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the name of the attributes to return (null to return all)
     * @return the profiles for the given IDs
     */
    Iterable<Profile> findByIds(List<String> ids, String sortBy, SortOrder sortOrder, String... attributesToReturn)
            throws MongoDataException;

    /**
     * Returns a range of profiles for the given tenant.
     *
     * @param tenantName            the tenant's name
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param start                 from the entire list of results, the position where the actual results should start
     *                              (useful for pagination) (optional)
     * @param count                 the number of profiles to return (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all)
     * @return the range of profiles
     */
    Iterable<Profile> findRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start, Integer count,
                                String... attributesToReturn) throws MongoDataException;

    /**
     * Returns the profiles with the given roles for the given tenant.
     *
     * @param tenantName            the tenant's name
     * @param role                  the role the profile's must have
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all)
     * @return the matching profiles
     */
    Iterable<Profile> findByTenantAndRole(String tenantName, String role, String sortBy, SortOrder sortOrder,
                                          String... attributesToReturn) throws MongoDataException;

    /**
     * Returns the profiles that have the given attribute, with any value, for the given tenant.
     *
     * @param tenantName            the tenant's name
     * @param attributeName         the name of the attribute profiles must have
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all)
     * @return the matching profiles
     */
    Iterable<Profile> findByTenantAndExistingAttribute(String tenantName, String attributeName, String sortBy,
                                                       SortOrder sortOrder, String... attributesToReturn)
            throws MongoDataException;

    /**
     * Returns the profiles that have the given attribute with the given value for the given tenant.
     *
     * @param tenantName            the tenant's name
     * @param attributeName         the name of the attribute profiles must have
     * @param attributeValue        the value of the attribute profiles must have
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all)
     * @return the matching profiles
     */
    Iterable<Profile> findByTenantAndAttributeValue(String tenantName, String attributeName, String attributeValue,
                                                    String sortBy, SortOrder sortOrder, String... attributesToReturn)
            throws MongoDataException;

    /**
     * Returns the count of profiles for the given tenant.
     *
     * @param tenantName the tenant's name
     * @return the count of profiles for the tenant
     */
    long countByTenant(String tenantName) throws MongoDataException;

    /**
     * Removes all the profiles associated to the given tenant.
     *
     * @param tenantName the tenant's name
     */
    void removeAll(String tenantName) throws MongoDataException;

    /**
     * Removes the role of all profiles of to the given tenant.
     *
     * @param tenantName    the tenant's name
     * @param role          the name of the role to remove
     */
    void removeRoleFromAll(String tenantName, String role) throws MongoDataException;

    /**
     * Removes the attribute of all profiles of to the given tenant.
     *
     * @param tenantName    the tenant's name
     * @param attributeName the name of the attribute to remove
     */
    void removeAttributeFromAll(String tenantName, String attributeName) throws MongoDataException;

    /**
     * Updates all the profiles of a given tenant with the default value of an attribute, only if they don't have
     * the attribute value set yet.
     *
     * @param tenantName    the tenant's name
     * @param attributeName the attribute's name
     * @param defaultValue  the default attribute value
     */
    void updateAllWithDefaultValue(String tenantName, String attributeName,
                                   Object defaultValue) throws MongoDataException;

}
