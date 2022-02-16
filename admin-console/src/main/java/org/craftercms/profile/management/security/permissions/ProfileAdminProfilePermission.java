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

import static org.craftercms.profile.management.security.permissions.Action.CREATE_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.DELETE_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.GET_PROFILE;
import static org.craftercms.profile.management.security.permissions.Action.UPDATE_PROFILE;
import static org.craftercms.profile.management.security.AuthorizationUtils.PROFILE_ADMIN_ROLE;

/**
 * Profile permission for the PROFILE_ADMIN role.
 *
 * @author avasquez
 */
public class ProfileAdminProfilePermission extends CompositePermission {

    public ProfileAdminProfilePermission(Profile currentUser, Profile object) {
        super(new SubjectTenantIsSamePermission(currentUser.getTenant(), object.getTenant()),
              new SubjectRoleIsNotInferiorPermission(PROFILE_ADMIN_ROLE, object.getRoles()),
              new DefaultAdminConsolePermission(GET_PROFILE, CREATE_PROFILE, UPDATE_PROFILE, DELETE_PROFILE));
    }

}
