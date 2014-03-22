/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.profile.v2.repositories;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.utils.SortOrder;

import java.util.List;

/**
 * DB repository for {@link org.craftercms.profile.api.Profile}s.
 *
 * @author avasquez
 */
public interface ProfileRepository extends CrudRepository<Profile> {

    /**
     * Returns the profile with the given ID.
     *
     * @param id                    the profile's ID
     * @param attributesToReturn    the name of the attributes to returns (null to return all)
     * @return the profile found, or null if not found
     */
    Profile findById(String id, String... attributesToReturn) throws MongoDataException;

    /**
     * Returns the profile for the given tenant name and username.
     *
     * @param tenantName            the tenant's name
     * @param username              the profile's username
     * @param attributesToReturn    the name of the attributes to returns (null to return all)
     * @return the profile found, or null if not found
     */
    Profile findByTenantAndUsername(String tenantName, String username, String... attributesToReturn)
            throws MongoDataException;

    /**
     * Returns the profiles with the given IDs.
     *
     * @param ids                   the IDs of the profiles to return
     * @param sortBy                profile attribute to sort the list by (optional)
     * @param sortOrder             the sort order (either ASC or DESC) (optional)
     * @param attributesToReturn    the name of the attributes to returns (null to return all)
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
     * @param attributesToReturn    the names of the attributes to return for each profile (null to return all
     *                              attributes)
     * @return the range of profiles
     */
    Iterable<Profile> findRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start, Integer count,
                                String... attributesToReturn) throws MongoDataException;

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
    void removeAllForTenant(String tenantName) throws MongoDataException;

}
