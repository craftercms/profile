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

import java.util.List;

import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.TenantException;
import org.craftercms.profile.exceptions.TicketException;

public interface MultiTenantService {

    /**
     * Create a new tenant
     *
     * @param tenantName
     * @param createDefaults
     * @param roles           roles that can be assigned to profiles created in this tenant
     * @param domains         domains that can access content in this tenant
     * @param emailNewProfile <code>true</code> New profiles will send a verification email,
     *                        otherwise it will just create the account
     * @return
     */
    Tenant createTenant(String tenantName, boolean createDefaults, List<String> roles, List<String> domains,
                        boolean emailNewProfile) throws TenantException;

    Tenant createTenant(String tenantName, boolean createDefaults, List<String> roles,
                        List<String> domains) throws TenantException;

    Tenant updateTenant(String id, String tenantName, List<String> roles, List<String> domains,
                        boolean emailNewProfile) throws TenantException;

    /**
     * Updates a tenant
     *
     * @param id              valid id value
     * @param tenantName      the tenantName
     * @param roles           roles that can be assigned to profiles created in this tenant
     * @param domains         domains that can access content in this tenant
     * @param emailNewProfile <code>true</code> New profiles will send a verification email,
     *                        otherwise it will just create the account
     * @return
     */
    Tenant updateTenant(String id, String tenantName, List<String> roles, List<String> domains) throws TenantException;

    void deleteTenant(String tenantName) throws TenantException;

    Tenant getTenantByName(String tenantName) throws TenantException;

    Tenant getTenantByTicket(String ticket) throws TenantException, TicketException;

    Tenant getTenantById(String tenantName) throws TenantException;

    long getTenantsCount() throws TenantException;

    boolean exists(String tenantName) throws TenantException;

    List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end);

    Iterable<Tenant> getAllTenants() throws TenantException;

    Iterable<Tenant> getTenantsByRoleName(String roleName) throws TenantException;

}
