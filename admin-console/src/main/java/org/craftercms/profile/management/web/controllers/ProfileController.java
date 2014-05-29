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
import org.craftercms.profile.management.exceptions.ResourceNotFoundException;
import org.craftercms.profile.management.web.model.ProfileForm;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * MVC Controller for displaying and modifying profiles.
 *
 * @author avasquez
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final String VIEW_PROFILE_LIST = "profile-list";
    private static final String VIEW_NEW_PROFILE =  "new-profile";
    private static final String VIEW_PROFILE =      "profile";

    private static final String PATH_VAR_ID = "id";

    private static final String PARAM_TENANT_NAME = "tenantName";

    private static final String MODEL_TENANTS =         "tenants";
    private static final String MODEL_CURRENT_TENANT =  "currentTenant";
    private static final String MODEL_PROFILES =        "profiles";
    private static final String MODEL_PROFILE =         "profile";
    private static final String MODEL_AVAILABLE_ROLES = "availableRoles";

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

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ModelAndView listAllProfiles(@RequestParam(value = PARAM_TENANT_NAME, required = false) String tenantName,
                                        HttpServletRequest request) throws ProfileException {
        if (StringUtils.isEmpty(tenantName)) {
            tenantName = SecurityUtils.getTenant(request);
        }

        List<Profile> profiles = profileService.getProfileRange(
                tenantName,
                defaultSortBy,
                defaultSortOrder,
                defaultStart,
                defaultLimit);

        ModelAndView mav = new ModelAndView(VIEW_PROFILE_LIST);
        mav.addObject(MODEL_TENANTS, tenantService.getAllTenants());
        mav.addObject(MODEL_CURRENT_TENANT, tenantName);
        mav.addObject(MODEL_PROFILES, profiles);

        return mav;
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ModelAndView showNewProfile() throws ProfileException {
        ModelAndView mav = new ModelAndView(VIEW_NEW_PROFILE);
        mav.addObject(MODEL_TENANTS, tenantService.getAllTenants());
        mav.addObject(MODEL_PROFILE, new ProfileForm());

        return mav;
    }

    @RequestMapping(value = "/{" + PATH_VAR_ID + "}", method = RequestMethod.GET)
    public ModelAndView showProfile(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        ProfileForm profile = getProfile(id);
        Tenant tenant = tenantService.getTenant(profile.getTenant());

        ModelAndView mav = new ModelAndView(VIEW_PROFILE);
        mav.addObject(MODEL_PROFILE, profile);
        mav.addObject(MODEL_AVAILABLE_ROLES, tenant.getAvailableRoles());

        return mav;
    }

    @RequestMapping(value = "/{" + PATH_VAR_ID + "}", method = RequestMethod.POST)
    public String updateProfile(@PathVariable(PATH_VAR_ID) String id,
                                @ModelAttribute(MODEL_PROFILE) ProfileForm profile, BindingResult result,
                                Model model) throws ProfileException {
        if (!result.hasErrors()) {
            profileService.updateProfile(
                    id,
                    profile.getUsername(),
                    profile.getPassword(),
                    profile.getEmail(),
                    profile.isEnabled(),
                    profile.getRoles());

            return "redirect:/";
        } else {
            model.addAttribute(MODEL_PROFILE, profile);

            return VIEW_PROFILE;
        }
    }

    protected ProfileForm getProfile(String id) throws ProfileException, ResourceNotFoundException {
        Profile profile = profileService.getProfile(id);
        if (profile != null) {
            return new ProfileForm(profile);
        } else {
            throw new ResourceNotFoundException("No profile found for ID '" + id + "'");
        }
    }

}
