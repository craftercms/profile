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

import java.util.Arrays;

import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.exceptions.ProfileException;
import org.craftercms.profile.exceptions.TenantException;
import org.craftercms.profile.exceptions.TicketException;
import org.craftercms.profile.services.MultiTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Multitenant Rest controller.
 */
@Controller
@RequestMapping("/api/2/tenant/")
public class MultiTenantRestController {

    /**
     * Multitenant Services.
     */
    @Autowired
    private MultiTenantService multiTenantService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Tenant createTenant(@RequestParam final String tenantName, @RequestParam(required = false) final String
        createDefaultRoles, @RequestParam(value = ProfileConstants.ROLES) final String[] rolesArray,
                               @RequestParam(required = false, defaultValue = "true") final boolean emailNewProfile,
                               @RequestParam(value = ProfileConstants.DOMAINS) final String[] domainsArray) throws TenantException {
        return multiTenantService.createTenant(tenantName, createDefaultRoles == null? false: Boolean.valueOf
            (createDefaultRoles).booleanValue(), (rolesArray != null? Arrays.asList(rolesArray): null),
            (domainsArray != null? Arrays.asList(domainsArray): null), emailNewProfile);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ModelAttribute
    public Tenant updateTenant(@RequestParam(ProfileConstants.FIELD_ID) final String id, @RequestParam(required = false,
        value = ProfileConstants.TENANT_NAME) final String tenantName, @RequestParam(value = ProfileConstants.ROLES)
                               final String[] rolesArray, @RequestParam(required = false,
        value = ProfileConstants.EMAIL_NEW_PROFILE,
        defaultValue = "true") final boolean emailNewProfile, @RequestParam(value = ProfileConstants.DOMAINS)
                               final String[] domainsArray) throws TenantException {
        return multiTenantService.updateTenant(id, tenantName, (rolesArray != null? Arrays.asList(rolesArray): null),
            (domainsArray != null? Arrays.asList(domainsArray): null), emailNewProfile);
    }

    @RequestMapping(value = "delete/{tenantName}", method = RequestMethod.GET)
    @ModelAttribute
    public void deleteTenant(@PathVariable final String tenantName) throws TenantException, ProfileException {
        multiTenantService.deleteTenant(tenantName);
    }

    @RequestMapping(value = "get/{tenantName}", method = RequestMethod.GET)
    @ModelAttribute
    public Tenant getTenantByName(@PathVariable final String tenantName) throws TenantException {
        return multiTenantService.getTenantByName(tenantName);
    }

    @RequestMapping(value = "{tenantId}/get_id", method = RequestMethod.GET)
    @ModelAttribute
    public Tenant getTenantByTenantId(@PathVariable final String tenantId) throws TenantException {
        return multiTenantService.getTenantById(tenantId);
    }

    @RequestMapping(value = "get/by_role_name", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Tenant> getTenantByRoleName(@RequestParam final String roleName) throws TenantException {
        return multiTenantService.getTenantsByRoleName(roleName);
    }

    @RequestMapping(value = "ticket/{ticket}", method = RequestMethod.GET)
    @ModelAttribute
    public Tenant getTenantByTicket(@PathVariable final String ticket) throws NoSuchProfileException,
        TenantException, TicketException, ProfileException {

        Tenant tenant = multiTenantService.getTenantByTicket(ticket);
        if (tenant == null) {
            throw new NoSuchProfileException(String.format("Could not find a Tenant for ticket='%s'.", ticket));
        }
        return tenant;
    }

    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ModelAttribute
    public long getTenantCount() throws TenantException {
        return multiTenantService.getTenantsCount();
    }

    @RequestMapping(value = "exists/{tenantName}", method = RequestMethod.GET)
    @ModelAttribute
    public boolean exists(@PathVariable final String tenantName) throws TenantException {
        return multiTenantService.exists(tenantName);
    }

    @RequestMapping(value = "range", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Tenant> getTenantRange(@RequestParam(required = false, value = ProfileConstants.SORT_BY)
                                           final String sortBy, @RequestParam(required = false,
        value = ProfileConstants.SORT_ORDER) final String sortOrder, @RequestParam(ProfileConstants.START) final int
        start, @RequestParam(ProfileConstants.END) final int end) throws TenantException {
        return multiTenantService.getTenantRange(sortBy, sortOrder, start, end);
    }

    @RequestMapping(value = "get_all_tenants", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Tenant> getAllTenants() throws NoSuchProfileException, TenantException {
        return multiTenantService.getAllTenants();
    }

}
