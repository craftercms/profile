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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.constants.ProfileConstants;

import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.services.MultiTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/api/2/tenant/")
public class MultiTenantRestController {
	
	@Autowired
	private MultiTenantService multiTenantService;
	
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ModelAttribute
	public Tenant createTenant(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
			@RequestParam String tenantName, 
			@RequestParam(required = false) String createDefaultRoles,
            @RequestParam(value=ProfileConstants.ROLES) String[] rolesArray,
            @RequestParam(value=ProfileConstants.DOMAINS) String[] domainsArray,
			HttpServletResponse response) {

        return multiTenantService.createTenant(tenantName, createDefaultRoles==null?false:Boolean.valueOf(createDefaultRoles).booleanValue(),
                (rolesArray != null ? Arrays.asList(rolesArray) : null),
                (domainsArray != null ? Arrays.asList(domainsArray) : null), response);
	}

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ModelAttribute
    public Tenant updateTenant(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                 @RequestParam(ProfileConstants.FIELD_ID) String id,
                                 @RequestParam(required = false, value = ProfileConstants.TENANT_NAME) String tenantName,
                                 @RequestParam(value=ProfileConstants.ROLES) String[] rolesArray,
                                 @RequestParam(value=ProfileConstants.DOMAINS) String[] domainsArray,
                                 HttpServletResponse response) {
        return multiTenantService.updateTenant(id, tenantName,
                (rolesArray != null ? Arrays.asList(rolesArray) : null),
                (domainsArray != null ? Arrays.asList(domainsArray) : null));
    }


	@RequestMapping(value = "delete/{tenantName}", method = RequestMethod.GET)
	@ModelAttribute
	public void deleteTenant(@RequestParam(ProfileConstants.APP_TOKEN) String appToken, @PathVariable String tenantName,
			HttpServletResponse response) {
		multiTenantService.deleteTenant(tenantName);
	}
	
	@RequestMapping(value = "get/{tenantName}", method = RequestMethod.GET)
	@ModelAttribute
	public Tenant getTenantByName(@RequestParam(ProfileConstants.APP_TOKEN) String appToken, @PathVariable String tenantName,
			HttpServletResponse response) {
		return multiTenantService.getTenantByName(tenantName);
	}

	@RequestMapping(value = "{tenantId}/get_id", method = RequestMethod.GET)
	@ModelAttribute
	public Tenant getTenantByTenantId(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
			@PathVariable String tenantId,
			HttpServletResponse response) {
		return multiTenantService.getTenantById(tenantId);
	}
	
	@RequestMapping(value = "get/by_role_name", method = RequestMethod.GET)
	@ModelAttribute
	public List<Tenant> getTenantByRoleName(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
			@RequestParam String roleName,
			HttpServletResponse response) {
		return multiTenantService.getTenantsByRoleName(roleName);
	}
	
	@RequestMapping(value = "ticket/{ticket}", method = RequestMethod.GET)
	@ModelAttribute
	public Tenant getTenantByTicket(@RequestParam(ProfileConstants.APP_TOKEN) String appToken, @PathVariable String ticket) 
											throws NoSuchProfileException {

		Tenant tenant = multiTenantService.getTenantByTicket(ticket);
		if (tenant == null) {
			throw new NoSuchProfileException(String.format("Could not find a Tenant for ticket='%s'.", ticket));
		}
		return tenant;
	}

    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ModelAttribute
    public long getTenantCount(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                 HttpServletResponse response) {
        return multiTenantService.getTenantsCount();
    }

    @RequestMapping(value = "exists/{tenantName}", method = RequestMethod.GET)
    @ModelAttribute
    public boolean exists(@RequestParam(ProfileConstants.APP_TOKEN) String appToken, @PathVariable String tenantName,
                          HttpServletResponse response) {
        return multiTenantService.exists(tenantName);
    }

    @RequestMapping(value = "range", method = RequestMethod.GET)
    @ModelAttribute
    public List<Tenant> getTenantRange(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
           @RequestParam(required = false, value = ProfileConstants.SORT_BY) String sortBy,
           @RequestParam(required = false, value = ProfileConstants.SORT_ORDER) String sortOrder,
           @RequestParam(ProfileConstants.START) int start,
           @RequestParam(ProfileConstants.END) int end){
        return multiTenantService.getTenantRange(sortBy, sortOrder, start, end);
    }

    @RequestMapping(value = "get_all_tenants", method = RequestMethod.GET)
    @ModelAttribute
    public List<Tenant> getAllTenants(@RequestParam(ProfileConstants.APP_TOKEN) String appToken)
            throws NoSuchProfileException {

        return multiTenantService.getAllTenants();
    }

}
