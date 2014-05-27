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

import org.apache.commons.lang3.StringUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * MVC Controller for displaying and modifying profiles.
 *
 * @author avasquez
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final String VIEW_PROFILE_LIST = "profile-list";

    private static final String PARAM_TENANT = "tenant";

    private static final String MODEL_TENANTS =     "tenants";
    private static final String MODEL_TENANT =      "tenant";
    private static final String MODEL_PROFILES =    "profiles";

    private String defaultSortBy;
    private SortOrder defaultSortOrder;
    private int defaultStart;
    private int defaultLimit;

    private ProfileService profileService;
    private TenantService tenantService;

    @Required
    public void setDefaultSortBy(String defaultSortBy) {
        this.defaultSortBy = defaultSortBy;
    }

    @Required
    public void setDefaultSortOrder(SortOrder defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    @Required
    public void setDefaultStart(int defaultStart) {
        this.defaultStart = defaultStart;
    }

    @Required
    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @ModelAttribute(MODEL_TENANTS)
    public List<Tenant> getTenants() throws ProfileException {
        return tenantService.getAllTenants();
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ModelAndView showAll(@RequestParam(value = PARAM_TENANT, required = false) String tenant,
                                HttpServletRequest request) throws ProfileException {
        if (StringUtils.isEmpty(tenant)) {
            tenant = SecurityUtils.getTenant(request);
        }

        List<Profile> profiles = profileService.getProfileRange(tenant, defaultSortBy, defaultSortOrder,
                defaultStart, defaultLimit);

        ModelAndView mav = new ModelAndView(VIEW_PROFILE_LIST);
        mav.addObject(MODEL_TENANT, tenant);
        mav.addObject(MODEL_PROFILES, profiles);

        return mav;
    }

}
