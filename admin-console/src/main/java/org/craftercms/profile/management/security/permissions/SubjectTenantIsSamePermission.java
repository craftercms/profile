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

import org.craftercms.commons.security.permissions.Permission;

/**
 * Permission that ensures that the current profile isn't trying to modify a tenant that it's not its own tenant or
 * a profile that doesn't correspond to its own tenant.
 *
 * @author avasquez
 */
public class SubjectTenantIsSamePermission implements Permission {

    private String subjectTenant;
    private String tenant;

    public SubjectTenantIsSamePermission(String subjectTenant, String tenant) {
        this.subjectTenant = subjectTenant;
        this.tenant = tenant;
    }

    @Override
    public boolean isAllowed(String action) {
        return subjectTenant.equals(tenant);
    }

}
