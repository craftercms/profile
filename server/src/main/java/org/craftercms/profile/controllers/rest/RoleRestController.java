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
package org.craftercms.profile.controllers.rest;

import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.exceptions.ProfileException;
import org.craftercms.profile.exceptions.RoleException;
import org.craftercms.profile.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Role rest Controller.
 */
@Controller
@RequestMapping("/api/2/role/")
public class RoleRestController {

    @Autowired
    private RoleService roleService;

    /**
     * Create a new role.
     *
     * @param roleName The role name.
     * @return the new role instance.
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Role createRole(@RequestParam(ProfileConstants.ROLE_NAME) final String roleName) throws RoleException {
        return roleService.createRole(roleName);
    }

    /**
     * Deletes role.
     *
     * @param roleName used to delete the role.
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteRole(@RequestParam(ProfileConstants.ROLE_NAME) final String roleName) throws ProfileException {
        roleService.deleteRole(roleName);
    }

    /**
     * Get all roles in the database.
     *
     * @return list of all roles.
     */

    @RequestMapping(value = "get_all_roles", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Role> getAllRoles() throws RoleException {
        return roleService.getAllRoles();
    }
}
