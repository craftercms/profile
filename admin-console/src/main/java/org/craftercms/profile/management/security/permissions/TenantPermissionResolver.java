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

import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.management.security.AuthorizationUtils;

/**
 * {@link org.craftercms.commons.security.permissions.PermissionResolver} for tenant permissions.
 *
 * @author avasquez
 */
public class TenantPermissionResolver implements PermissionResolver<Profile, String> {

    @Override
    public Permission getGlobalPermission(Profile currentUser) throws PermissionException {
        if (AuthorizationUtils.isSuperadmin(currentUser)) {
            return new SuperadminPermission();
        } else {
            return null;
        }
    }

    @Override
    public Permission getPermission(Profile currentUser, String tenant) throws PermissionException {
        if (AuthorizationUtils.isSuperadmin(currentUser)) {
            return new SuperadminPermission();
        } else if (AuthorizationUtils.isTenantAdmin(currentUser)) {
            return new TenantAdminTenantPermission(currentUser, tenant);
        } else {
            return new ProfileAdminTenantPermission(currentUser, tenant);
        }
    }

}
