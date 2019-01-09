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

package org.craftercms.profile.api.services;

import java.util.Collection;
import java.util.List;

import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;

/**
 * Service for managing tenants.
 *
 * @author avasquez
 */
public interface TenantService {

    /**
     * Creates the given tenant, failing if it already has been created.
     *
     * @param tenant the tenant to create
     *
     * @return the created tenant
     */
    Tenant createTenant(Tenant tenant) throws ProfileException;

    /**
     * Returns a tenant.
     *
     * @param name  the tenant's name
     *
     * @return the tenant
     */
    Tenant getTenant(String name) throws ProfileException;

    /**
     * Updates the given tenant.
     *
     * @param tenant the tenant to update
     *
     * @return the updated tenant
     */
    Tenant updateTenant(Tenant tenant) throws ProfileException;

    /**
     * Deletes a tenant.
     *
     * @param name  the tenant's name
     */
    void deleteTenant(String name) throws ProfileException;

    /**
     * Returns the total number of tenants.
     *
     * @return the number of tenants
     */
    long getTenantCount() throws ProfileException;

    /**
     * Returns a list with all the tenants.
     *
     * @return a list with al the tenants.
     */
    List<Tenant> getAllTenants() throws ProfileException;

    /**
     * Sets if new profiles for the specified tenant should be verified or not.
     *
     * @param tenantName    the tenant's name
     * @param verify        true to verify new profiles through email, false otherwise
     *
     * @return the tenant
     */
    Tenant verifyNewProfiles(String tenantName, boolean verify) throws ProfileException;

    /**
     * Adds the given roles to the specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param roles         the roles to add
     *
     * @return the tenant
     */
    Tenant addRoles(String tenantName, Collection<String> roles) throws ProfileException;

    /**
     * Removes the given roles from the specified tenant.
     *
     * @param tenantName    the tenant's name
     * @param roles         the roles to remove
     *
     * @return the tenant
     */
    Tenant removeRoles(String tenantName, Collection<String> roles) throws ProfileException;

    /**
     * Adds the given attribute definitions to the specified tenant.
     *
     * @param tenantName            the tenant's name
     * @param attributeDefinitions  the definitions to add
     *
     * @return the tenant
     */
    Tenant addAttributeDefinitions(String tenantName, Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException;

    /**
     * Updates the given attribute definitions of the specified tenant.
     *
     * @param tenantName            the tenant's name
     * @param attributeDefinitions  the definitions to update (should have the same name as definitions that the
     *                              tenant already has)
     *
     * @return the tenant
     */
    Tenant updateAttributeDefinitions(String tenantName, Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException;

    /**
     * Removes the given attribute definitions from the specified tenant.
     *
     * @param tenantName        the tenant's name
     * @param attributeNames    the name of the attributes whose definitions should be removed
     *
     * @return the tenant
     */
    Tenant removeAttributeDefinitions(String tenantName, Collection<String> attributeNames) throws ProfileException;

}
