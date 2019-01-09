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

import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.management.security.AuthorizationUtils;

/**
 * {@link org.craftercms.commons.security.permissions.PermissionResolver} for profile permissions.
 *
 * @author avasquez
 */
public class ProfilePermissionResolver implements PermissionResolver<Profile, Profile> {

    @Override
    public Permission getGlobalPermission(Profile currentUser) throws PermissionException {
        throw  new UnsupportedOperationException();
    }

    @Override
    public Permission getPermission(Profile currentUser, Profile profile) throws PermissionException {
        if (AuthorizationUtils.isSuperadmin(currentUser)) {
            return new SuperadminPermission();
        } else if (AuthorizationUtils.isTenantAdmin(currentUser)) {
            return new TenantAdminProfilePermission(currentUser, profile);
        } else {
            return new ProfileAdminProfilePermission(currentUser, profile);
        }
    }

}
