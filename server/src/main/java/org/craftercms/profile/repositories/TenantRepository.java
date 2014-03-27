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

import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.TenantException;

/**
 * Definition of Tenant Repository Services.
 */
public interface TenantRepository extends CrudRepository<Tenant> {
    /**
     * Finds a tenant by its Object Id.
     *
     * @param id Object Id of the tenant.
     * @return The Tenant with that id, null if not found.
     * @throws org.craftercms.profile.exceptions.TenantException If tenant can't be search for.
     */
    Tenant findTenantById(ObjectId id) throws TenantException;

    /**
     * All tenants uses pagination.
     *
     * @param sortBy    Field used by the sorting.
     * @param sortOrder Order of the sorting.
     * @param start     Start of the Range.
     * @param end       End of the Range.
     * @return A iterator of with the tenants ordered by the given sort field and sort order.<br/>
     * empty if nothing is found.
     */
    Iterable<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end) throws TenantException;

    /**
     * Finds the a Tenant with the given name.
     *
     * @param tenantName Name of the tenant looking for.
     * @return Tenant with the given name <br/> <b>null</b> if nothing is found.
     * @throws org.craftercms.profile.exceptions.TenantException If tenant can't be search for.
     */
    Tenant getTenantByName(String tenantName) throws TenantException;

    /**
     * Sets a Tenant Attribute.<b/>
     * <i>If the tenant does not exist or is not found with the given name <b>Nothing is done</b></></i>
     *
     * @param tenantName Name of the tenant to set Attribute.
     * @param attribute  Attribute to set.
     * @throws org.craftercms.profile.exceptions.TenantException If tenant can't be search for or Attribute can be
     * written.
     */
    void setAttribute(String tenantName, Attribute attribute) throws TenantException;

    /**
     * Deletes a the given attribute form the tenant.
     * <i>If the tenant does not exist or is not found with the given name <b>Nothing is done</b></></i>
     *
     * @param tenantName    Name of the tenant to delete Attribute.
     * @param attributeName Name of the Attribute to delete.
     * @throws org.craftercms.profile.exceptions.TenantException If tenant can't be search for or Attribute can be
     * written.
     */
    void deleteAttribute(String tenantName, String attributeName) throws TenantException;

    /**
     * Gets a complete list of all tenants. by its assigned roles.
     *
     * @param roles Roles that tenant should have.
     * @return A list of the tenants with the given roles.
     * @throws org.craftercms.profile.exceptions.TenantException If tenants can't be search for.
     */
    Iterable<Tenant> getTenants(String[] roles) throws TenantException;

    /**
     * Counts how many tenants are with a given set of roles.
     *
     * @param roles Roles that tenant should have
     * @return number of Tenants with given roles.
     * @throws org.craftercms.profile.exceptions.TenantException If unable to count tenants
     */
    long countTenantsWithRoles(String[] roles) throws TenantException;

    /**
     * Deletes a Tenant by its tenantId.
     *
     * @param tenantId ObjectId that represent the tenant Id.
     * @throws org.craftercms.profile.exceptions.TenantException if tenant can't be deleted.
     */
    void deleteTenant(final ObjectId tenantId) throws TenantException;

}
