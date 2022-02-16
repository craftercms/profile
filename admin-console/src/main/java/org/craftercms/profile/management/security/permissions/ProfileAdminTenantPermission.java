/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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

/**
 * Tenant permission for the PROFILE_ADMIN role.
 *
 * @author avasquez
 */
public class ProfileAdminTenantPermission extends CompositePermission {

    public ProfileAdminTenantPermission(Profile currentUser, String tenant) {
        super(new SubjectTenantIsSamePermission(currentUser.getTenant(), tenant),
              new DefaultAdminConsolePermission(GET_TENANT, GET_PROFILE_COUNT, GET_PROFILE_LIST));
    }

}
