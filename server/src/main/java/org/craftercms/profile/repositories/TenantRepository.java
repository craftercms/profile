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

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Tenant;

/**
 * DB repository for {@link org.craftercms.profile.api.Tenant}s
 */
public interface TenantRepository extends CrudRepository<Tenant> {

    /**
     * Returns the tenant for the given name.
     *
     * @param name the tenant's name
     * @return the tenant, or null if not found.
     */
    Tenant findByName(String name) throws MongoDataException;

    /**
     * Removes the tenant for the given name.
     *
     * @param name  the tenant's name
     */
    void removeByName(String name) throws MongoDataException;

}
