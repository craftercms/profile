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

import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.services.MultiTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/api/2/tenant/")
public class TenantController {

    // TODO: Exists seems like unnecessary
    // TODO: Should we be searching tenants by their name?

    @Autowired
    private MultiTenantService multiTenantService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Tenant createTenant(@RequestParam String tenantName,
                               @RequestParam(required = false) Boolean createDefaultRoles,
                               @RequestParam(value = ProfileConstants.ROLES) String[] roles,
                               @RequestParam(required=false, defaultValue="true") Boolean emailNewProfile,
                               @RequestParam(value = ProfileConstants.DOMAINS) String[] domains) {
        return multiTenantService.createTenant(tenantName, createDefaultRoles == null? false: Boolean.valueOf
            (createDefaultRoles).booleanValue(), (roles != null? Arrays.asList(roles): null),
            (domains != null? Arrays.asList(domains): null), emailNewProfile, null);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ModelAttribute
    public Tenant updateTenant(@RequestParam(ProfileConstants.FIELD_ID) String id,
                               @RequestParam(value = ProfileConstants.ROLES) String[] rolesArray,
                               @RequestParam(required=false, value = ProfileConstants.EMAIL_NEW_PROFILE, defaultValue="true")
                               Boolean emailNewProfile,
                               @RequestParam(value = ProfileConstants.DOMAINS) String[] domains) {
        return multiTenantService.updateTenant(id, null, (rolesArray != null? Arrays.asList(rolesArray): null),
            (domains != null? Arrays.asList(domains): null), emailNewProfile);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ModelAttribute
    public void deleteTenant(@RequestParam(ProfileConstants.FIELD_ID) String id) {
        // TODO
    }

    @RequestMapping(value = "delete_by_name", method = RequestMethod.GET)
    @ModelAttribute
    public void deleteTenantByName(@RequestParam(value = ProfileConstants.TENANT_NAME) String tenantName) {
        multiTenantService.deleteTenant(tenantName);
    }

    @RequestMapping(value = "get_by_id", method = RequestMethod.GET)
    @ModelAttribute
    public Tenant getTenantById(@RequestParam(ProfileConstants.FIELD_ID) String id) {
        return multiTenantService.getTenantById(id);
    }

    @RequestMapping(value = "get_by_name", method = RequestMethod.GET)
    @ModelAttribute
    public Tenant getTenantByName(@RequestParam(value = ProfileConstants.TENANT_NAME) String tenantName) {
        return multiTenantService.getTenantByName(tenantName);
    }

    @RequestMapping(value = "get_by_role", method = RequestMethod.GET)
    @ModelAttribute
    public List<Tenant> getTenantsByRole(@RequestParam String roleName) {
        return multiTenantService.getTenantsByRoleName(roleName);
    }

    @RequestMapping(value = "get_by_ticket", method = RequestMethod.GET)
    @ModelAttribute
    public Tenant getTenantByTicket(@RequestParam String ticket) {
        return multiTenantService.getTenantByTicket(ticket);
    }

    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ModelAttribute
    public long getTenantCount() {
        return multiTenantService.getTenantsCount();
    }

//    @RequestMapping(value = "exists", method = RequestMethod.GET)
//    @ModelAttribute
//    public boolean tenantExists(@RequestParam(ProfileConstants.APP_TOKEN) String appToken, @PathVariable String tenantName,
//                          HttpServletResponse response) {
//        return multiTenantService.exists(tenantName);
//    }

    @RequestMapping(value = "range", method = RequestMethod.GET)
    @ModelAttribute
    public List<Tenant> getTenantRange(@RequestParam(required = false, value = ProfileConstants.SORT_BY) String sortBy,
                                       @RequestParam(required = false, value = ProfileConstants.SORT_ORDER) String sortOrder,
                                       @RequestParam(ProfileConstants.START) int start, @RequestParam(ProfileConstants.END) int end) {
        return multiTenantService.getTenantRange(sortBy, sortOrder, start, end);
    }

    @RequestMapping(value = "get_all", method = RequestMethod.GET)
    @ModelAttribute
    public List<Tenant> getAllTenants() {
        return multiTenantService.getAllTenants();
    }

}
