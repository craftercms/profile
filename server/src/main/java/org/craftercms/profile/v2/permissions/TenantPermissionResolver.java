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
package org.craftercms.profile.v2.permissions;

import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.TenantPermission;

/**
 * {@link org.craftercms.commons.security.permissions.PermissionResolver} for tenants.
 *
 * @author avasquez
 */
public class TenantPermissionResolver implements PermissionResolver<Application, String> {

    @Override
    public Permission getGlobalPermission(Application app) throws IllegalArgumentException, PermissionException {
        return getPermission(app, TenantPermission.ANY_TENANT);
    }

    @Override
    public Permission getPermission(Application app, String tenantName) throws PermissionException {
        for (TenantPermission permission : app.getTenantPermissions()) {
            String permittedTenant = permission.getTenant();

            if (permittedTenant.equals(TenantPermission.ANY_TENANT) || permittedTenant.equals(tenantName)) {
                return permission;
            }
        }

        return null;
    }

}
