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
package org.craftercms.profile.v2.controllers.rest;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.utils.SortOrder;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.craftercms.profile.api.RestConstants.*;

/**
 * REST controller for the profile service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_PROFILE)
public class ProfileController {

    protected ProfileService profileService;

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = URL_PROFILE_CREATE, method = RequestMethod.POST)
    public Profile createProfile(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                 @RequestParam(PARAM_USERNAME) String username,
                                 @RequestParam(PARAM_PASSWORD) String password,
                                 @RequestParam(PARAM_EMAIL) String email,
                                 @RequestParam(PARAM_ENABLED) boolean enabled,
                                 @RequestParam(value = PARAM_ROLE, required = false) Set<String> roles,
                                 @RequestParam(value = PARAM_VERIFICATION_URL) String verificationUrl)
            throws ProfileException {
        return profileService.createProfile(tenantName, username, password, email, enabled, roles, verificationUrl);
    }

    @RequestMapping(value = URL_PROFILE_UPDATE, method = RequestMethod.POST)
    public Profile updateProfile(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                 @RequestParam(PARAM_PROFILE_ID) String profileId,
                                 @RequestParam(value = PARAM_USERNAME, required = false) String username,
                                 @RequestParam(value = PARAM_PASSWORD, required = false) String password,
                                 @RequestParam(value = PARAM_EMAIL, required = false) String email,
                                 @RequestParam(value = PARAM_ENABLED, required = false) Boolean enabled,
                                 @RequestParam(value = PARAM_ROLE, required = false) Set<String> roles,
                                 @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                 String[] attributesToReturn)
            throws ProfileException {
        return profileService.updateProfile(tenantName, profileId, username, password, email, enabled, roles,
                attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_ENABLE, method = RequestMethod.POST)
    public Profile enableProfile(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                 @RequestParam(PARAM_PROFILE_ID) String profileId,
                                 @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                 String[] attributesToReturn)
            throws ProfileException {
        return profileService.enableProfile(tenantName, profileId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_DISABLE, method = RequestMethod.POST)
    public Profile disableProfile(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                  @RequestParam(PARAM_PROFILE_ID) String profileId,
                                  @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                  String[] attributesToReturn) throws ProfileException {
        return profileService.disableProfile(tenantName, profileId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_ADD_ROLES, method = RequestMethod.POST)
    public Profile addRoles(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                            @RequestParam(PARAM_PROFILE_ID) String profileId,
                            @RequestParam(PARAM_ROLE) Collection<String> roles,
                            @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                            String[] attributesToReturn) throws ProfileException {
        return profileService.addRoles(tenantName, profileId, roles, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_REMOVE_ROLES, method = RequestMethod.POST)
    public Profile removeRoles(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                               @RequestParam(PARAM_PROFILE_ID) String profileId,
                               @RequestParam(PARAM_ROLE) Collection<String> roles,
                               @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                               String[] attributesToReturn) throws ProfileException {
        return profileService.removeRoles(tenantName, profileId, roles, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_VERIFY, method = RequestMethod.POST)
    public Profile verifyProfile(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                 @RequestParam(PARAM_VERIFICATION_TOKEN_ID) String verificationTokenId,
                                 @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                 String[] attributesToReturn)
            throws ProfileException {
        return profileService.verifyProfile(tenantName, verificationTokenId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_ATTRIBUTES, method = RequestMethod.GET)
    public Map<String, Object> getAttributes(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                             @RequestParam(PARAM_PROFILE_ID) String profileId,
                                             @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                             String[] attributesToReturn)
            throws ProfileException {
        return profileService.getAttributes(tenantName, profileId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_UPDATE_ATTRIBUTES, method = RequestMethod.POST)
    public Profile updateAttributes(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                    @RequestParam(PARAM_PROFILE_ID) String profileId,
                                    @RequestBody Map<String, Object> attributes,
                                    @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                    String[] attributesToReturn) throws ProfileException {
        return profileService.updateAttributes(tenantName, profileId, attributes, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_REMOVE_ATTRIBUTES, method = RequestMethod.POST)
    public Profile removeAttributes(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                    @RequestParam(PARAM_PROFILE_ID) String profileId,
                                    @RequestParam(PARAM_ATTRIBUTES_TO_RETURN) Collection<String> attributeNames,
                                    @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                    String[] attributesToReturn)
        throws ProfileException {
        return profileService.removeAttributes(tenantName, profileId, attributeNames, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_DELETE_PROFILE, method = RequestMethod.POST)
    public void deleteProfile(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                              @RequestParam(PARAM_PROFILE_ID) String profileId) throws ProfileException {
        profileService.deleteProfile(tenantName, profileId);
    }

    @RequestMapping(value = URL_PROFILE_GET, method = RequestMethod.GET)
    public Profile getProfile(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                              @RequestParam(PARAM_PROFILE_ID) String profileId,
                              @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                              String[] attributesToReturn) throws ProfileException {
        return profileService.getProfile(tenantName, profileId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_TICKET, method = RequestMethod.GET)
    public Profile getProfileByUsername(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                        @RequestParam(PARAM_USERNAME) String username,
                                        @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                        String[] attributesToReturn) throws ProfileException {
        return profileService.getProfileByUsername(tenantName, username, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_USERNAME, method = RequestMethod.GET)
    public Profile getProfileByTicket(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                      @RequestParam(PARAM_TICKET_ID) String ticketId,
                                      @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                      String[] attributesToReturn) throws ProfileException {
        return profileService.getProfileByTicket(tenantName, ticketId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_COUNT, method = RequestMethod.GET)
    public long getProfileCount(@RequestParam(PARAM_TENANT_NAME) String tenantName) throws ProfileException {
        return profileService.getProfileCount(tenantName);
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_IDS, method = RequestMethod.GET)
    public Iterable<Profile> getProfileByIds(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                             @RequestParam(PARAM_PROFILE_ID) List<String> profileIds,
                                             @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                             @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                             SortOrder sortOrder,
                                             @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                             String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByIds(tenantName, profileIds, sortBy, sortOrder, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_RANGE, method = RequestMethod.GET)
    public Iterable<Profile> getProfileRange(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                             @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                             @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                             SortOrder sortOrder,
                                             @RequestParam(value = PARAM_START, required = false) Integer start,
                                             @RequestParam(value = PARAM_COUNT, required = false) Integer count,
                                             @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                             String[] attributesToReturn) throws ProfileException {
        return profileService.getProfileRange(tenantName, sortBy, sortOrder, start, count, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_ROLE, method = RequestMethod.GET)
    public Iterable<Profile> getProfileByRole(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                              @RequestParam(PARAM_ROLE) String role,
                                              @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                              @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                              SortOrder sortOrder,
                                              @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                              String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByRole(tenantName, role, sortBy, sortOrder, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_ATTRIBUTE, method = RequestMethod.GET)
    public Iterable<Profile> getProfileByAttribute(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                                   @RequestParam(PARAM_ATTRIBUTE_NAME) String attributeName,
                                                   @RequestParam(PARAM_ATTRIBUTE_VALUE) String attributeValue,
                                                   @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                                   @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                                   SortOrder sortOrder,
                                                   @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                                   String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByAttribute(tenantName, attributeName, attributeValue, sortBy, sortOrder,
                attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_FORGOT_PASSWORD, method = RequestMethod.POST)
    public Profile forgotPassword(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                  @RequestParam(PARAM_PROFILE_ID) String profileId,
                                  @RequestParam(PARAM_RESET_PASSWORD_URL) String resetPasswordUrl,
                                  @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                  String[] attributesToReturn) throws ProfileException {
        return profileService.forgotPassword(tenantName, profileId, resetPasswordUrl, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_RESET_PASSWORD, method = RequestMethod.POST)
    public Profile resetPassword(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                 @RequestParam(PARAM_RESET_TOKEN_ID) String resetTokenId,
                                 @RequestParam(PARAM_NEW_PASSWORD) String newPassword,
                                 @RequestParam(value = PARAM_ATTRIBUTES_TO_RETURN, required = false)
                                 String[] attributesToReturn) throws ProfileException {
        return profileService.resetPassword(tenantName, resetTokenId, newPassword, attributesToReturn);
    }

}
