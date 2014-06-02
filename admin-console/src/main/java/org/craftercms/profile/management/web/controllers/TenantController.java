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
package org.craftercms.profile.management.web.controllers;

import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.management.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * MVC Controller for displaying and modifying tenants.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(TenantController.BASE_URL_TENANT)
public class TenantController {

    public static final String PATH_VAR_NAME = "name";

    public static final String BASE_URL_TENANT =            "/tenant";

    public static final String URL_VIEW_TENANT_LIST =       "/list/view";
    public static final String URL_VIEW_TENANT =            "/view";

    public static final String URL_GET_TENANT_NAMES =       "/names";
    public static final String URL_GET_AVAILABLE_ROLES =    "/available_roles";
    public static final String URL_GET_TENANT_LIST =        "/list";
    public static final String URL_GET_TENANT =             "/{" + PATH_VAR_NAME + "}";

    public static final String PARAM_TENANT_NAME = "tenantName";

    public static final String VIEW_TENANT_LIST =   "tenant-list";
    public static final String VIEW_TENANT =        "tenant";

    private TenantService tenantService;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequestMapping(value = URL_VIEW_TENANT_LIST, method = RequestMethod.GET)
    public String viewTenantList() throws ProfileException {
        return VIEW_TENANT_LIST;
    }

    @RequestMapping(value = URL_VIEW_TENANT, method = RequestMethod.GET)
    public String viewTenant() throws ProfileException {
        return VIEW_TENANT;
    }

    @RequestMapping(value = URL_GET_TENANT_NAMES, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getTenantNames() throws ProfileException {
        List<Tenant> tenants = tenantService.getAllTenants();
        List<String> tenantNames = new ArrayList<>(tenants.size());

        for (Tenant tenant : tenants) {
            tenantNames.add(tenant.getName());
        }

        return tenantNames;
    }

    @RequestMapping(value = URL_GET_AVAILABLE_ROLES, method = RequestMethod.GET, params = PARAM_TENANT_NAME)
    @ResponseBody
    public Set<String> getAvailableRoles(@RequestParam(PARAM_TENANT_NAME) String tenantName) throws ProfileException {
        return getTenant(tenantName).getAvailableRoles();
    }

    @RequestMapping(value = URL_GET_AVAILABLE_ROLES, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Set<String>> getAvailableRoles() throws ProfileException {
        List<Tenant> tenants = tenantService.getAllTenants();
        Map<String, Set<String>> availableRoles = new LinkedHashMap<>(tenants.size());

        for (Tenant tenant : tenants) {
            availableRoles.put(tenant.getName(), tenant.getAvailableRoles());
        }

        return availableRoles;
    }

    @RequestMapping(value = URL_GET_TENANT_LIST, method = RequestMethod.GET)
    @ResponseBody
    public List<Tenant> getTenantList() throws ProfileException {
        return tenantService.getAllTenants();
    }

    @RequestMapping(value = URL_GET_TENANT, method = RequestMethod.GET)
    @ResponseBody
    public Tenant getTenant(@PathVariable(PATH_VAR_NAME) String name) throws ProfileException {
        Tenant tenant = tenantService.getTenant(name);
        if (tenant != null) {
            return tenant;
        } else {
            throw new ResourceNotFoundException("No tenant found with name '" + name + "'");
        }
    }

}
