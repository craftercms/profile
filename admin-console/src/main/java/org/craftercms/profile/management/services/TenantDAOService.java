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
package org.craftercms.profile.management.services;

import java.util.List;

import org.craftercms.profile.client.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.client.impl.domain.Attribute;
import org.craftercms.profile.client.impl.domain.Schema;
import org.craftercms.profile.client.impl.domain.Tenant;
import org.craftercms.profile.management.model.TenantFilterForm;

/**
 * @author David Escalante
 */

public interface TenantDAOService {

    /**
     * Create a new Empty Tenant with all default values if needed
     *
     * @return
     */
    public Tenant createEmptyTenant();

    /**
     * Insert a new Tenant in the Tenant Collection
     *
     * @param tenant
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public Tenant createNewTenant(Tenant tenant) throws AppAuthenticationFailedException;

    /**
     * Get a true value if there is a Tenant with the given Id
     *
     * @param tenantName
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public boolean exists(String tenantName) throws AppAuthenticationFailedException;

    /**
     * Get the number of Tenants
     *
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public long getTenantCount() throws AppAuthenticationFailedException;

    /**
     * Get the current Tenant Page
     *
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public List<Tenant> getTenantPage() throws AppAuthenticationFailedException;

    /**
     * Get the next Tenant Page
     *
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public List<Tenant> getNextTenantPage() throws AppAuthenticationFailedException;

    /**
     * Get the previous Tenant Page
     *
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public List<Tenant> getPrevTenantPage() throws AppAuthenticationFailedException;

    /**
     * Get a list of tenant with the given filter
     *
     * @param tenantFilterForm
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public List<Tenant> getSearchTenants(TenantFilterForm tenantFilterForm) throws AppAuthenticationFailedException;

    /**
     * Get a list of all the tenants
     *
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public List<Tenant> getAllTenants() throws AppAuthenticationFailedException;

    /**
     * Retrieve a Tenant that match the given name
     *
     * @param tenantName
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public Tenant getTenantByName(String tenantName) throws AppAuthenticationFailedException;

    /**
     * Retrieve a Tenant that match the given name
     * @param tenantName
     * @return
     * @throws AppAuthenticationFailedException
     */
    //    public Tenant getTenantById(String tenantName) throws AppAuthenticationFailedException;

    /**
     * Get the Tenant that match the given Id for update
     *
     * @param tenantName
     * @return
     * @throws AppAuthenticationFailedException
     *
     */
    public Tenant getTenantForUpdate(String tenantName) throws AppAuthenticationFailedException;

    /**
     * Update the given Tenant
     *
     * @param tenant
     * @throws AppAuthenticationFailedException
     *
     */
    public Tenant updateTenant(Tenant tenant) throws AppAuthenticationFailedException;

    /**
     * Create a new Attribute Tenant with all default values if needed
     *
     * @param schema
     * @return
     */
    public Attribute createNewAttribute(Schema schema);


    /**
     * Update or insert the attribute in the given tenant
     *
     * @param attribute
     * @param tenant
     */
    public void setSchemaAttribute(Attribute attribute, Tenant tenant) throws AppAuthenticationFailedException;

    /**
     * Delete the attribute list from the given tenant
     *
     * @param attributes
     * @param tenant
     */
    public void deleteSchemaAttributes(List<String> attributes, Tenant tenant) throws AppAuthenticationFailedException;

    public void restartAppToken();
}
