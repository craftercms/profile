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

package org.craftercms.profile.management.security;

import org.craftercms.profile.api.Profile;

/**
 * Authorization related utility methods.
 *
 * @author avasquez
 */
public class AuthorizationUtils {

    public static final String SUPERADMIN_ROLE = "PROFILE_SUPERADMIN";
    public static final String TENANT_ADMIN_ROLE = "PROFILE_TENANT_ADMIN";
    public static final String PROFILE_ADMIN_ROLE = "PROFILE_ADMIN";

    private AuthorizationUtils() {
    }

    public static boolean isSuperadmin(Profile profile) {
        return profile.hasRole(SUPERADMIN_ROLE);
    }

    public static boolean isTenantAdmin(Profile profile) {
        return profile.hasRole(TENANT_ADMIN_ROLE);
    }

    public static boolean isProfileAdmin(Profile profile) {
        return profile.hasRole(PROFILE_ADMIN_ROLE);
    }

}
