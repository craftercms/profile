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

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.TenantException;

public interface TenantRepository extends CrudRepository<Tenant>, TenantRepositoryCustom {

    Tenant findTenantById(ObjectId id) throws TenantException;

    List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end);

    Tenant getTenantByName(String tenantName) throws TenantException;

    void setAttribute(String tenantName, Attribute attribute);

    void deleteAttribute(String tenantName, String attributeName);

    Iterable<Tenant> getTenants(String[] roles) throws TenantException;

    /**
     * Counts how many tenants are with a given set of roles.
     *
     * @param roles Roles that tenant should have
     * @return number of Tenants with given roles.
     * @throws TenantException If unable to count tenants
     */
    long countTenantsWithRoles(String[] roles) throws TenantException;

    /**
     * Deletes a Tenant by its tenantId.
     *
     * @param tenantId ObjectId that represent the tenant Id.
     * @throws org.craftercms.profile.exceptions.TenantException if tenant can't be deleted.
     */
    void deleteTenant(final ObjectId tenantId) throws MongoDataException;

    /**
     * Counts all the tenants.
     *
     * @return number of tenants store.
     */
    long count() throws MongoDataException;
}
