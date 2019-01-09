/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.management.exceptions.InvalidRequestParameterException;
import org.craftercms.profile.management.exceptions.ResourceNotFoundException;
import org.craftercms.profile.management.security.permissions.Action;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MVC Controller for displaying and modifying profiles.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(ProfileController.BASE_URL_PROFILE)
public class ProfileController {

    public static final String BASE_URL_PROFILE = "/profile";

    public static final String PATH_VAR_ID = "id";

    public static final String URL_VIEW_PROFILE_LIST = "/list/view";
    public static final String URL_VIEW_NEW_PROFILE = "/new/view";
    public static final String URL_VIEW_PROFILE = "/view";

    public static final String URL_GET_PROFILE_COUNT = "/count";
    public static final String URL_GET_PROFILE_LIST = "/list";
    public static final String URL_GET_PROFILE = "/{" + PATH_VAR_ID + "}";
    public static final String URL_CREATE_PROFILE = "/create";
    public static final String URL_UPDATE_PROFILE = "/update";
    public static final String URL_DELETE_PROFILE = "/{" + PATH_VAR_ID + "}/delete";

    public static final String PARAM_TENANT_NAME = "tenantName";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_SORT_BY = "sortBy";
    public static final String PARAM_SORT_ORDER = "sortOrder";
    public static final String PARAM_START = "start";
    public static final String PARAM_COUNT = "count";

    public static final String VIEW_PROFILE_LIST = "profile-list";
    public static final String VIEW_NEW_PROFILE = "new-profile";
    public static final String VIEW_PROFILE = "profile";

    public static final String MODEL_MESSAGE = "message";

    public static final String MSG_PROFILE_CREATED_FORMAT = "Profile '%s' created";
    public static final String MSG_PROFILE_UPDATED_FORMAT = "Profile '%s' updated";
    public static final String MSG_PROFILE_DELETED_FORMAT = "Profile '%s' deleted";

    public static final Pattern QUERY_PATTERN = Pattern.compile("\\w+");
    public static final String FINAL_QUERY_FORMAT = "{username: {$regex: '.*%s.*', $options: 'i'}}";

    private String verificationUrl;
    private ProfileService profileService;
    private PermissionEvaluator<Profile, String> tenantPermissionEvaluator;
    private PermissionEvaluator<Profile, Profile> profilePermissionEvaluator;

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setTenantPermissionEvaluator(PermissionEvaluator<Profile, String> tenantPermissionEvaluator) {
        this.tenantPermissionEvaluator = tenantPermissionEvaluator;
    }

    @Required
    public void setProfilePermissionEvaluator(PermissionEvaluator<Profile, Profile> profilePermissionEvaluator) {
        this.profilePermissionEvaluator = profilePermissionEvaluator;
    }

    @RequestMapping(value = URL_VIEW_PROFILE_LIST, method = RequestMethod.GET)
    public String viewProfileList() {
        return VIEW_PROFILE_LIST;
    }

    @RequestMapping(value = URL_VIEW_NEW_PROFILE, method = RequestMethod.GET)
    public String viewNewProfile() {
        return VIEW_NEW_PROFILE;
    }

    @RequestMapping(value = URL_VIEW_PROFILE, method = RequestMethod.GET)
    public String viewProfile() {
        return VIEW_PROFILE;
    }

    @RequestMapping(value = URL_GET_PROFILE_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public long getProfileCount(@RequestParam(value = PARAM_TENANT_NAME, required = false) String tenantName,
                                @RequestParam(value = PARAM_QUERY, required = false) String query)
        throws ProfileException {
        if (StringUtils.isEmpty(tenantName)) {
            tenantName = SecurityUtils.getCurrentProfile().getTenant();
        } else {
            checkIfAllowed(tenantName, Action.GET_PROFILE_COUNT);
        }

        if (StringUtils.isNotEmpty(query)) {
            if (QUERY_PATTERN.matcher(query).matches()) {
                query = String.format(FINAL_QUERY_FORMAT, query);

                return profileService.getProfileCountByQuery(tenantName, query);
            } else {
                throw new InvalidRequestParameterException("Parameter '" + PARAM_QUERY + "' must match regex " +
                                                           QUERY_PATTERN.pattern());
            }
        } else {
            return profileService.getProfileCount(tenantName);
        }
    }

    @RequestMapping(value = URL_GET_PROFILE_LIST, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfileList(@RequestParam(value = PARAM_TENANT_NAME, required = false) String tenantName,
                                        @RequestParam(value = PARAM_QUERY, required = false) String query,
                                        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
                                        @RequestParam(value = PARAM_START, required = false) Integer start,
                                        @RequestParam(value = PARAM_COUNT, required = false) Integer limit)
        throws ProfileException {
        if (StringUtils.isEmpty(tenantName)) {
            tenantName = SecurityUtils.getCurrentProfile().getTenant();
        } else {
            checkIfAllowed(tenantName, Action.GET_PROFILE_LIST);
        }

        if (StringUtils.isNotEmpty(query)) {
            if (QUERY_PATTERN.matcher(query).matches()) {
                query = String.format(FINAL_QUERY_FORMAT, query);

                return profileService.getProfilesByQuery(tenantName, query, sortBy, sortOrder, start, limit);
            } else {
                throw new InvalidRequestParameterException("Parameter '" + PARAM_QUERY + "' must match regex " +
                                                           QUERY_PATTERN.pattern());
            }
        } else {
            return profileService.getProfileRange(tenantName, sortBy, sortOrder, start, limit);
        }
    }

    @RequestMapping(value = URL_GET_PROFILE, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfile(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        Profile profile = profileService.getProfile(id);
        if (profile != null) {
            checkIfAllowed(profile, Action.GET_PROFILE);

            return profile;
        } else {
            throw new ResourceNotFoundException("No profile found for ID '" + id + "'");
        }
    }

    @RequestMapping(value = URL_CREATE_PROFILE, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createProfile(@RequestBody Profile profile) throws ProfileException {
        checkIfAllowed(profile, Action.CREATE_PROFILE);

        profile = profileService.createProfile(profile.getTenant(), profile.getUsername(), profile.getPassword(),
                                               profile.getEmail(), profile.isEnabled(), profile.getRoles(),
                                               profile.getAttributes(), verificationUrl);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_PROFILE_CREATED_FORMAT, profile.getId()));
    }

    @RequestMapping(value = URL_UPDATE_PROFILE, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> updateProfile(@RequestBody Profile profile) throws ProfileException {
        String id = profile.getId().toString();
        Profile currentProfile = profileService.getProfile(id);

        if (currentProfile != null) {
            checkIfAllowed(currentProfile, Action.UPDATE_PROFILE);

            profileService.updateProfile(id, profile.getUsername(), profile.getPassword(), profile.getEmail(),
                                         profile.isEnabled(), profile.getRoles(), profile.getAttributes(),
                                         ProfileConstants.NO_ATTRIBUTE);

            return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_PROFILE_UPDATED_FORMAT, id));
        } else {
            throw new ResourceNotFoundException("No profile found for ID '" + id + "'");
        }
    }

    @RequestMapping(value = URL_DELETE_PROFILE, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteProfile(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        Profile profile = profileService.getProfile(id);
        if (profile != null) {
            checkIfAllowed(profile, Action.DELETE_PROFILE);

            profileService.deleteProfile(id);

            return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_PROFILE_DELETED_FORMAT, id));
        } else {
            throw new ResourceNotFoundException("No profile found for ID '" + id + "'");
        }
    }

    private void checkIfAllowed(String tenant, Action action) throws ActionDeniedException {
        if (!tenantPermissionEvaluator.isAllowed(tenant, action.toString())) {
            if (tenant != null) {
                throw new ActionDeniedException(action.toString(), tenant);
            } else {
                throw new ActionDeniedException(action.toString());
            }
        }
    }

    private void checkIfAllowed(Profile profile, Action action) throws ActionDeniedException {
        if (!profilePermissionEvaluator.isAllowed(profile, action.toString())) {
            if (profile != null) {
                throw new ActionDeniedException(action.toString(), profile);
            } else {
                throw new ActionDeniedException(action.toString());
            }
        }
    }

}
