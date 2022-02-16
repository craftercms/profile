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

/**
 * The actions that a user can execute in the admin console.
 *
 * @author avasquez
 */
public enum Action {
    GET_ACCESS_TOKEN,
    GET_ALL_ACCESS_TOKENS,
    CREATE_ACCESS_TOKEN,
    DELETE_ACCESS_TOKEN,
    GET_TENANT,
    CREATE_TENANT,
    UPDATE_TENANT,
    DELETE_TENANT,
    GET_PROFILE_COUNT,
    GET_PROFILE_LIST,
    GET_PROFILE,
    CREATE_PROFILE,
    UPDATE_PROFILE,
    DELETE_PROFILE
}
