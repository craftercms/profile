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

import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * MVC Controller for displaying and modifying tenants.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(TenantController.URL_TENANT_BASE)
public class TenantController {

    public static final String URL_TENANT_BASE =        "/tenant";
    public static final String URL_LIST_ALL_TENANTS =   "/all";

    public static final String VIEW_TENANT_LIST =   "tenant-list";

    public static final String MODEL_TENANTS =  "tenants";

    private TenantService tenantService;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequestMapping(value = URL_LIST_ALL_TENANTS, method = RequestMethod.GET)
    public ModelAndView listAllProfiles() throws ProfileException {
        ModelAndView mav = new ModelAndView(VIEW_TENANT_LIST);
        mav.addObject(MODEL_TENANTS, tenantService.getAllTenants());

        return mav;
    }

}
