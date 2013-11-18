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
package org.craftercms.profile.controllers.rest.v3;

import org.bson.types.ObjectId;
import org.craftercms.profile.constants.GroupRoleConstants;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.GroupRole;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.services.GroupRoleService;
import org.craftercms.profile.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/api/2/role/")
public class RoleController {

    // TODO: Check if we REALLY need the appToken
    // TODO: Refactor RoleService to throw and exception instead of setting response error

    @Autowired
    private RoleService roleService;

    /**
     * Create a new role
     *
     * @param roleName  the role name
     *
     * @return the new role instance
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Role createRole(@RequestParam(ProfileConstants.ROLE_NAME) String roleName) {
        return roleService.createRole(roleName, null);
    }

    /**
     * Delete role.
     *
     * @param roleName  used to delete the role
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteRole(@RequestParam(ProfileConstants.ROLE_NAME) String roleName) {
        roleService.deleteRole(roleName, null);
    }

    /**
     * Get all roles in the database.
     *
     * @return list of all roles
     */
    @RequestMapping(value = "get_all", method = RequestMethod.GET)
    @ModelAttribute
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

}
