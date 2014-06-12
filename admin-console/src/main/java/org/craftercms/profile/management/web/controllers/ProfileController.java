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
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.management.exceptions.ResourceNotFoundException;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MVC Controller for displaying and modifying profiles.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(ProfileController.BASE_URL_PROFILE)
public class ProfileController {

    public static final String PATH_VAR_ID = "id";
    
    public static final String BASE_URL_PROFILE = "/profile";

    public static final String URL_VIEW_PROFILE_LIST =      "/list/view";
    public static final String URL_VIEW_NEW_PROFILE =       "/new/view";
    public static final String URL_VIEW_UPDATE_PROFILE =    "/update/view";

    public static final String URL_GET_PROFILE_COUNT =  "/count";
    public static final String URL_GET_PROFILE_LIST =   "/list";
    public static final String URL_GET_PROFILE =        "/{" + PATH_VAR_ID + "}";
    public static final String URL_CREATE_PROFILE =     "/new";
    public static final String URL_UPDATE_PROFILE =     "/update";
    public static final String URL_DELETE_PROFILE =     "/{" + PATH_VAR_ID + "}/delete";

    public static final String PARAM_TENANT_NAME =  "tenantName";
    public static final String PARAM_SORT_BY =      "sortBy";
    public static final String PARAM_SORT_ORDER =   "sortOrder";
    public static final String PARAM_START =        "start";
    public static final String PARAM_COUNT =        "count";

    public static final String VIEW_PROFILE_LIST =      "profile-list";
    public static final String VIEW_NEW_PROFILE =       "new-profile";
    public static final String VIEW_UPDATE_PROFILE =    "update-profile";

    public static final String MODEL_MESSAGE = "message";

    public static final String MSG_PROFILE_CREATED_FORMAT = "Profile '%s' created";
    public static final String MSG_PROFILE_UPDATED_FORMAT = "Profile '%s' updated";
    public static final String MSG_PROFILE_DELETED_FORMAT = "Profile '%s' deleted";

    private String defaultSortBy;
    private SortOrder defaultSortOrder;
    private int defaultStart;
    private int defaultCount;
    private String verificationUrl;

    private ProfileService profileService;

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
    public void setDefaultCount(int defaultCount) {
        this.defaultCount = defaultCount;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = URL_VIEW_PROFILE_LIST, method = RequestMethod.GET)
    public String viewProfileList() {
        return VIEW_PROFILE_LIST;
    }

    @RequestMapping(value = URL_VIEW_NEW_PROFILE, method = RequestMethod.GET)
    public String viewNewProfile() {
        return VIEW_NEW_PROFILE;
    }

    @RequestMapping(value = URL_VIEW_UPDATE_PROFILE, method = RequestMethod.GET)
    public String viewUpdateProfile() {
        return VIEW_UPDATE_PROFILE;
    }

    @RequestMapping(value = URL_GET_PROFILE_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public long getProfileCount(@RequestParam(value = PARAM_TENANT_NAME, required = false) String tenantName,
                                HttpServletRequest request) throws ProfileException {
        if (StringUtils.isEmpty(tenantName)) {
            tenantName = SecurityUtils.getTenant(request);
        }

        return profileService.getProfileCount(tenantName);
    }

    @RequestMapping(value = URL_GET_PROFILE_LIST, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfileList(@RequestParam(value = PARAM_TENANT_NAME, required = false) String tenantName,
                                        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
                                        @RequestParam(value = PARAM_START, required = false) Integer start,
                                        @RequestParam(value = PARAM_COUNT, required = false) Integer limit,
                                        HttpServletRequest request) throws ProfileException {
        if (StringUtils.isEmpty(tenantName)) {
            tenantName = SecurityUtils.getTenant(request);
        }
        if (StringUtils.isEmpty(sortBy)) {
            sortBy = defaultSortBy;
        }
        if (sortOrder == null) {
            sortOrder = defaultSortOrder;
        }
        if (start == null) {
            start = defaultStart;
        }
        if (limit == null) {
            limit = defaultCount;
        }

        return profileService.getProfileRange(tenantName, sortBy, sortOrder, start, limit);
    }

    @RequestMapping(value = URL_GET_PROFILE, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfile(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        Profile profile = profileService.getProfile(id);
        if (profile != null) {
            return profile;
        } else {
            throw new ResourceNotFoundException("No profile found for ID '" + id + "'");
        }
    }

    @RequestMapping(value = URL_CREATE_PROFILE, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createProfile(@RequestBody Profile profile) throws ProfileException {
        profile = profileService.createProfile(profile.getTenant(), profile.getUsername(),
                profile.getPassword(), profile.getEmail(), profile.isEnabled(), profile.getRoles(),
                profile.getAttributes(), verificationUrl);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_PROFILE_CREATED_FORMAT, profile.getId()));
    }

    @RequestMapping(value = URL_UPDATE_PROFILE, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> updateProfile(@RequestBody Profile profile) throws ProfileException {
        profile = profileService.updateProfile(profile.getId().toString(), profile.getUsername(),
                profile.getPassword(), profile.getEmail(), profile.isEnabled(), profile.getRoles(),
                profile.getAttributes(), ProfileConstants.NO_ATTRIBUTE);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_PROFILE_UPDATED_FORMAT, profile.getId()));
    }

    @RequestMapping(value = URL_DELETE_PROFILE, method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, String> deleteProfile(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        profileService.deleteProfile(id);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_PROFILE_DELETED_FORMAT, id));
    }

}
