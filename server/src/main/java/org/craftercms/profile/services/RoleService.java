/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.profile.services;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.domain.Role;

/**
 * Manage services for the Roles
 * @author Alvaro Gonzalez
 *
 */
public interface RoleService {
	/**
	 * Creates new Role
	 * 
	 * @param roleName The new role name
	 * @param response The current HttpServletResponse instance
	 * 
	 * @return new Role instance
	 */
    Role createRole(String roleName, HttpServletResponse response);

    /**
     * Deletes a Role from the repository
     * 
     * @param roleName is going to be removed
     * 
     * @param response The current HttpServletResponse instance
     */
    void deleteRole(String roleName, HttpServletResponse response);

    /**
     * Gets the list of all the roles
     * 
     * @return a list of roles in the repository
     */
    List<Role> getAllRoles();

    /**
     * Deletes all the roles in the repository
     */
    void deleteAllRoles();

    /**
     * Gets a role from the repo based on the role name
     * 
     * @param roleName is going to be used to get the role
     * 
     * @return role instance
     */
    Role getRole(String roleName);
}
