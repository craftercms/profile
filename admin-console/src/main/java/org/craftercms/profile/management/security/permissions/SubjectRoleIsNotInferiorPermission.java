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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.craftercms.commons.security.permissions.Permission;

import static org.craftercms.profile.management.security.AuthorizationUtils.PROFILE_ADMIN_ROLE;
import static org.craftercms.profile.management.security.AuthorizationUtils.SUPERADMIN_ROLE;
import static org.craftercms.profile.management.security.AuthorizationUtils.TENANT_ADMIN_ROLE;

/**
 * Permission that ensures that the current profile isn't trying to modify another profile if the former has an
 * inferior role than the later.
 *
 * @author avasquez
 */
public class SubjectRoleIsNotInferiorPermission implements Permission {

    private static final Map<String, String[]> superiorRolesMap = new HashMap<>();

    static {
        superiorRolesMap.put(SUPERADMIN_ROLE, new String[0]);
        superiorRolesMap.put(TENANT_ADMIN_ROLE, new String[] {SUPERADMIN_ROLE});
        superiorRolesMap.put(PROFILE_ADMIN_ROLE, new String[] {TENANT_ADMIN_ROLE, SUPERADMIN_ROLE});
    }

    protected String subjectRole;
    protected Set<String> objectRoles;

    public SubjectRoleIsNotInferiorPermission(String subjectRole, Set<String> objectRoles) {
        this.subjectRole = subjectRole;
        this.objectRoles = objectRoles;
    }

    @Override
    public boolean isAllowed(String action) {
        String[] superiorRoles = superiorRolesMap.get(subjectRole);
        for (String superiorRole : superiorRoles) {
            if (objectRoles.contains(superiorRole)) {
                return false;
            }
        }

        return true;
    }

}
