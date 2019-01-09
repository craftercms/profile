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

import static org.craftercms.profile.management.security.permissions.Action.CREATE_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.DELETE_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.UPDATE_PROFILE;
import static org.craftercms.profile.management.security.AuthorizationUtils.TENANT_ADMIN_ROLE;

/**
 * Profile permission for the PROFILE_TENANT_ADMIN role.
 *
 * @author avasquez
 */
public class TenantAdminProfilePermission extends CompositePermission {

    public TenantAdminProfilePermission(Profile currentUser, Profile object) {
        super(new SubjectTenantIsSamePermission(currentUser.getTenant(), object.getTenant()),
              new SubjectRoleIsNotInferiorPermission(TENANT_ADMIN_ROLE, object.getRoles()),
              new DefaultAdminConsolePermission(GET_PROFILE, CREATE_PROFILE, UPDATE_PROFILE, DELETE_PROFILE));
    }

}
