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

package org.craftercms.profile.management.security.permissions;

import org.craftercms.commons.security.permissions.CompositePermission;
import org.craftercms.profile.api.Profile;

import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE_COUNT;
import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE_LIST;
import static org.craftercms.profile.management.security.permissions.Action.GET_TENANT;
import static org.craftercms.profile.management.security.permissions.Action.UPDATE_TENANT;

/**
 * Tenant permission for the PROFILE_TENANT_ADMIN role.
 *
 * @author avasquez
 */
public class TenantAdminTenantPermission extends CompositePermission {

    public TenantAdminTenantPermission(Profile currentUser, String tenant) {
        super(new SubjectTenantIsSamePermission(currentUser.getTenant(), tenant),
              new DefaultAdminConsolePermission(GET_TENANT, UPDATE_TENANT, GET_PROFILE_COUNT, GET_PROFILE_LIST));
    }

}
