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
package org.craftercms.profile.controllers.rest;

import org.craftercms.profile.api.AttributeDefinition;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST controller for the tenant service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_TENANT)
public class TenantController {

    private TenantService tenantService;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequestMapping(value = URL_TENANT_CREATE, method = RequestMethod.POST)
    @ResponseBody
    public Tenant createTenant(@RequestBody Tenant tenant) throws ProfileException {
        return tenantService.createTenant(tenant);
    }

    @RequestMapping(value = URL_TENANT_GET, method = RequestMethod.GET)
    @ResponseBody
    public Tenant getTenant(@PathVariable(PATH_VAR_NAME) String name) throws ProfileException {
        return tenantService.getTenant(name);
    }

    @RequestMapping(value = URL_TENANT_UPDATE, method = RequestMethod.POST)
    @ResponseBody
    public Tenant updateTenant(@RequestBody Tenant tenant) throws ProfileException {
        return tenantService.updateTenant(tenant);
    }

    @RequestMapping(value = URL_TENANT_DELETE, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteTenant(@PathVariable(PATH_VAR_NAME) String name) throws ProfileException {
        tenantService.deleteTenant(name);
    }

    @RequestMapping(value = URL_TENANT_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public long getTenantCount() throws ProfileException {
        return tenantService.getTenantCount();
    }

    @RequestMapping(value = URL_TENANT_GET_ALL, method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Tenant> getAllTenants() throws ProfileException {
        return tenantService.getAllTenants();
    }

    @RequestMapping(value = URL_TENANT_VERIFY_NEW_PROFILES, method = RequestMethod.POST)
    @ResponseBody
    public Tenant verifyNewProfiles(@PathVariable(PATH_VAR_NAME) String tenantName,
                                    @RequestParam(PARAM_VERIFY) boolean verify) throws ProfileException {
        return tenantService.verifyNewProfiles(tenantName, verify);
    }

    @RequestMapping(value = URL_TENANT_ADD_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Tenant addRoles(@PathVariable(PATH_VAR_NAME) String tenantName,
                           @RequestParam(PARAM_ROLE) Collection<String> roles) throws ProfileException {
        return tenantService.addRoles(tenantName, roles);
    }

    @RequestMapping(value = URL_TENANT_REMOVE_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Tenant removeRoles(@PathVariable(PATH_VAR_NAME) String tenantName,
                              @RequestParam(PARAM_ROLE) Collection<String> roles) throws ProfileException {
        return tenantService.removeRoles(tenantName, roles);
    }

    @RequestMapping(value = URL_TENANT_ADD_ATTRIBUTE_DEFINITIONS, method = RequestMethod.POST)
    @ResponseBody
    public Tenant addAttributeDefinitions(@PathVariable(PATH_VAR_NAME) String tenantName,
                                          @RequestBody Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        return tenantService.addAttributeDefinitions(tenantName, attributeDefinitions);
    }

    @RequestMapping(value = URL_TENANT_UPDATE_ATTRIBUTE_DEFINITIONS, method = RequestMethod.POST)
    @ResponseBody
    public Tenant updateAttributeDefinitions(@PathVariable(PATH_VAR_NAME) String tenantName,
                                             @RequestBody Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        return tenantService.updateAttributeDefinitions(tenantName, attributeDefinitions);
    }

    @RequestMapping(value = URL_TENANT_REMOVE_ATTRIBUTE_DEFINITIONS, method = RequestMethod.POST)
    @ResponseBody
    public Tenant removeAttributeDefinitions(@PathVariable(PATH_VAR_NAME) String tenantName,
                                             @RequestParam(PARAM_ATTRIBUTE_NAME) Collection<String> attributeNames)
            throws ProfileException {
        return tenantService.removeAttributeDefinitions(tenantName, attributeNames);
    }

}
