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
package org.craftercms.profile.controllers.rest.v1;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/1/role/")
public class RoleRestController {

    @Autowired
    private RoleService roleService;

    /**
     * Create a new role
     *
     * @param appToken The application token
     * @param roleName The role name
     * @return the new role instance
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Role createRole(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                           @RequestParam(ProfileConstants.ROLE_NAME) String roleName, HttpServletResponse response) {
        return roleService.createRole(roleName, response);
    }

    /**
     * Delete role
     *
     * @param appToken The application token
     * @param roleName used to delete the role
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteRole(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                           @RequestParam(ProfileConstants.ROLE_NAME) String roleName, HttpServletResponse response) {
        roleService.deleteRole(roleName, response);
    }

    /**
     * Get all roles in the database
     *
     * @param request  instance
     * @param appToken The application token
     * @param response instance
     * @return list of all roles
     */

    @RequestMapping(value = "get_all_roles", method = RequestMethod.GET)
    @ModelAttribute
    public List<Role> getAllRoles(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String
        appToken, HttpServletResponse response) {
        return roleService.getAllRoles();
    }

}
