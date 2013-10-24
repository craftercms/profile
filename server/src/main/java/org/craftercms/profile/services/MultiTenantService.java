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
import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.domain.Tenant;

public interface MultiTenantService {

    /**
     * Create a new tenant
     *
     * @param tenantName
     * @param createDefaults
     * @param roles          roles that can be assigned to profiles created in this tenant
     * @param domains        domains that can access content in this tenant
     * @param emailNewProfile <code>true</code> New profiles will send a verification email, otherwise it will just create the account
     * @return
     */
    Tenant createTenant(String tenantName, boolean createDefaults, List<String> roles, List<String> domains, boolean emailNewProfile,
                        HttpServletResponse response);
    
    Tenant createTenant(String tenantName, boolean createDefaults, List<String> roles, List<String> domains,
    		HttpServletResponse response);

    Tenant updateTenant(String id, String tenantName, List<String> roles, List<String> domains, boolean emailNewProfile);
    
    /**
     * Updates a tenant
     * @param id valid id value
     * @param tenantName the tenantName
     * @param roles          roles that can be assigned to profiles created in this tenant
     * @param domains        domains that can access content in this tenant
     * @param emailNewProfile <code>true</code> New profiles will send a verification email, otherwise it will just create the account
     * @return
     */
    Tenant updateTenant(String id, String tenantName, List<String> roles, List<String> domains);

    void deleteTenant(String tenantName);

    Tenant getTenantByName(String tenantName);

    Tenant getTenantByTicket(String ticket);

    Tenant getTenantById(String tenantName);

    long getTenantsCount();

    boolean exists(String tenantName);

    List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end);

    List<Tenant> getAllTenants();

    List<Tenant> getTenantsByRoleName(String roleName);

}
