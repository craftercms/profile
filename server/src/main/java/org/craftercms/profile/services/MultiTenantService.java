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

import org.craftercms.profile.domain.Tenant;

import java.util.List;

public interface MultiTenantService {

    /**
     * Create a new tenant
     * @param tenantName
     * @param createDefaults
     * @param roles
     *          roles that can be assigned to profiles created in this tenant
     * @param domains
     *          domains that can access content in this tenant
     * @return
     */
	Tenant createTenant(String tenantName, boolean createDefaults,
                               List<String> roles, List<String> domains);

    Tenant updateTenant(String id, String tenantName, List<String> roles, List<String> domains);

    void deleteTenant(String tenantName);
	
	Tenant getTenantByName(String tenantName);

	Tenant getTenantByTicket(String ticket);

	Tenant getTenantById(String tenantName);

    long getTenantsCount();

    boolean exists(String tenantName);

    List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end);

    List<Tenant> getAllTenants();

}
