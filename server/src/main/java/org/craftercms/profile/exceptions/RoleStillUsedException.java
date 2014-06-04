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
package org.craftercms.profile.exceptions;

/**
 * Thrown if a role is to be removed from a tenant but there are some profiles still with the attribute.
 *
 * @author avasquez
 */
public class RoleStillUsedException extends I10nProfileException {

    public static final String KEY = "profile.role.roleStillUsed";

    public RoleStillUsedException(String role, String tenant) {
        super(KEY, role, tenant);
    }

}
