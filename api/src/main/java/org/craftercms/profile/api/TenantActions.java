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
package org.craftercms.profile.api;

/**
 * Actions that can be executed on tenants and their user profiles by applications.
 *
 * @author avasquez
 */
public class TenantActions {

    public static final String CREATE =         "create";
    public static final String READ =           "read";
    public static final String READ_ALL =       "readAll";
    public static final String COUNT =          "count";
    public static final String UPDATE =         "update";
    public static final String DELETE =         "delete";
    public static final String MANAGE_PROFILES =   "manageUsers";

    public static final String[] ALL_ACTIONS = { CREATE, READ, READ_ALL, COUNT, UPDATE, DELETE, MANAGE_PROFILES};

    private TenantActions() {
    }

}