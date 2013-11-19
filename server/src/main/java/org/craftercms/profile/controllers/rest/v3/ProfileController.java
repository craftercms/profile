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
package org.craftercms.profile.controllers.rest.v3;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;

import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.InvalidEmailException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.services.PasswordService;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.VerifyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/2/profile/")
public class ProfileController {

    // TODO: Eliminate NoSuchProfileException from ProfileService.createProfile.
    // TODO: Instead of returning null or throwing NoSuchProfileException, return 404.
    // TODO: Getting users through username doesn't make sense.
    // TODO: Active/inactive all profiles doesn't make sense.
    // TODO: What does verifyToken do? What is a token (not an app token)?
    // TODO: Check exceptions better, to see which ones don't make sense to throw
    // TODO: Use tenants Id and not names.

    @Autowired
    private ProfileService profileService;
    @Autowired
    private VerifyAccountService verifyAccountService;
    @Autowired
    private PasswordService passwordService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Profile createProfile(@RequestParam(ProfileConstants.USER_NAME) String userName,
                                 @RequestParam(ProfileConstants.PASSWORD) String password,
                                 @RequestParam(ProfileConstants.ACTIVE) Boolean active,
                                 @RequestParam(ProfileConstants.TENANT_ID) String tenantId,
                                 @RequestParam(ProfileConstants.EMAIL) String email, @RequestParam(required = false,
        value = ProfileConstants.ROLES) String[] roles, @RequestParam(required = false) String
        verificationAccountUrl, HttpServletRequest request) throws InvalidEmailException, CipherException,
        MailException {
        try {
            return profileService.createProfile(userName, password, active, tenantId, email,
                getAttributeMap(request), (roles != null? Arrays.asList(roles): null), verificationAccountUrl, null,
                request);
        } catch (NoSuchProfileException e) {
            // TODO: Shouldn't be thrown, doesn't make sense
            return null;
        }
    }

    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ModelAttribute
    public long getProfilesCount(@RequestParam(ProfileConstants.TENANT_NAME) String tenantName) {
        return profileService.getProfilesCount(tenantName);
    }

    @RequestMapping(value = "range", method = RequestMethod.GET)
    @ModelAttribute
    public List<Profile> getProfileRange(@RequestParam(ProfileConstants.TENANT_NAME) String tenantName,
                                         @RequestParam(required = false, value = ProfileConstants.SORT_BY) String
                                             sortBy, @RequestParam(required = false,
        value = ProfileConstants.SORT_ORDER) String sortOrder, @RequestParam(ProfileConstants.START) int start,
                                         @RequestParam(ProfileConstants.END) int end, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes) {
        return profileService.getProfileRange(tenantName, sortBy, sortOrder, attributes, start, end);
    }

    @RequestMapping(value = "get/id/{profileId}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfile(@PathVariable String profileId, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return profileService.getProfile(profileId);
        } else if (attributes.contains("ALL")) {
            return profileService.getProfileWithAllAttributes(profileId);
        } else {
            return profileService.getProfile(profileId, attributes);
        }
    }

    //
    //    /**
    //     * Get Profile By UserName
    //     *
    //     * @param appToken
    //     * @param username
    //     * @return
    //     * @throws org.craftercms.profile.exceptions.NoSuchProfileException
    //     */
    //    @RequestMapping(value = "get_by_username", method = RequestMethod.GET)
    //    @ModelAttribute
    //    public Profile getProfileByUsername(@RequestParam String username,
    //                                        @RequestParam(ProfileConstants.TENANT_NAME) String tenantName)  {
    //        return profileService.getProfileByUserName(username, tenantName);
    //    }
    //
    //    /**
    //     * Get Profile By UserName with attributes
    //     *
    //     * @param appToken
    //     * @param username
    //     * @param tenantName
    //     * @param attributes
    //     * @return
    //     * @throws org.craftercms.profile.exceptions.NoSuchProfileException
    //     */
    //    @RequestMapping(value = "get_by_username_with_attributes", method = RequestMethod.GET)
    //    @ModelAttribute
    //    public Profile getProfileByUsernameWithAttributes(@PathVariable String username,
    //                                                      @RequestParam(ProfileConstants.TENANT_NAME) String
    // tenantName,
    //                                                      @RequestParam(required = false,
    // value = ProfileConstants.ATTRIBUTES)
    //                                                      List<String> attributes) throws NoSuchProfileException {
    //
    //        Profile profile = profileService.getProfileByUserName(username, tenantName, attributes);
    //
    //        return profile;
    //    }
    //
    //    /**
    //     * Get Profile By UserName with All Attributes
    //     *
    //     * @param appToken
    //     * @param username
    //     * @return
    //     * @throws org.craftercms.profile.exceptions.NoSuchProfileException
    //     */
    //    @RequestMapping(value = "username/{username}/with_all_attributes", method = RequestMethod.GET)
    //    @ModelAttribute
    //    public Profile getProfileByUsernameWithAllAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String
    // appToken,
    //                                                         @PathVariable String username,
    //                                                         @RequestParam(ProfileConstants.TENANT_NAME) String
    //                                                             tenantName) throws NoSuchProfileException {
    //        Profile profile = profileService.getProfileByUserNameWithAllAttributes(username, tenantName);
    //
    //        if (profile == null) {
    //            throw new NoSuchProfileException(String.format("Could not find a profile for username='%s'.",
    // username));
    //        }
    //
    //        return profile;
    //    }

    @RequestMapping(value = "ticket/{ticket}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByTicket(@PathVariable String ticket, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return profileService.getProfileByTicket(ticket);
        } else if (attributes.contains("ALL")) {
            return profileService.getProfileByTicketWithAllAttributes(ticket);
        } else {
            return profileService.getProfileByTicket(ticket, attributes);
        }
    }

    @RequestMapping(value = "get_profiles", method = RequestMethod.GET)
    @ModelAttribute
    public List<Profile> getProfiles(@RequestParam(ProfileConstants.PROFILE_ID_LIST) List<String> ids,
                                     @RequestParam Boolean allAttributes) {
        if (allAttributes != null && allAttributes == true) {
            return profileService.getProfilesWithAttributes(ids);
        } else {
            return profileService.getProfiles(ids);
        }
    }

    @RequestMapping(value = "update/{profileId}", method = RequestMethod.POST)
    @ModelAttribute
    public Profile updateProfile(@PathParam(ProfileConstants.PROFILE_ID) String profileId,
                                 @RequestParam(required = false, value = ProfileConstants.PASSWORD) String password,
                                 @RequestParam(required = false, value = ProfileConstants.ACTIVE) Boolean active,
                                 @RequestParam(ProfileConstants.EMAIL) String email, @RequestParam(required = false,
        value = ProfileConstants.ROLES) String[] roles, HttpServletRequest request) {
        return profileService.updateProfile(profileId, null, password, active, null,
            //TODO Allow to change Tenant????
            email, getAttributeMap(request), (roles != null? Arrays.asList(roles): null));
    }

    //    /**
    //     * Actives and inactives All Profiles
    //     */
    //    @RequestMapping(value = "active/all", method = RequestMethod.GET)
    //    @ModelAttribute
    //    public void activeProfiles(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String
    // appToken,
    //                               @RequestParam(ProfileConstants.ACTIVE) Boolean active,
    // HttpServletResponse response) {
    //        profileService.activateProfiles(active);
    //    }

    @RequestMapping(value = "update/{profileId}/status", method = RequestMethod.GET)
    @ModelAttribute
    public void updateActiveStatus(@PathParam(ProfileConstants.PROFILE_ID) String profileId,
                                   @RequestParam(ProfileConstants.ACTIVE) boolean active) {
        profileService.activateProfile(profileId, active);
    }

    //    @RequestMapping(value = "verify", method = RequestMethod.POST)
    //    @ModelAttribute
    //    public Profile verifyProfile(@RequestParam(ProfileConstants.TOKEN) String token) throws CipherException,
    // ParseException, ExpiryDateException {
    //        try {
    //            return this.verifyAccountService.verifyAccount(token);
    //        } catch (NoSuchProfileException e) {
    //            return null;
    //        }
    //    }

    @RequestMapping(value = "/{profile}add_attributes", method = RequestMethod.POST)
    @ModelAttribute
    public void setAttributes(@RequestParam String profileId, HttpServletRequest request) {
        profileService.setAttributes(profileId, getAttributeMap(request));
    }

    @RequestMapping(value = "get_all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Serializable> getAllAttributes(@RequestParam String profileId) {
        return profileService.getAllAttributes(profileId);
    }

    @RequestMapping(value = "get_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Serializable> getAttributes(@RequestParam String profileId, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributeNames) {
        return profileService.getAttributes(profileId, attributeNames);
    }

    @RequestMapping(value = "get_attribute", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Serializable> getAttribute(@RequestParam String profileId, @RequestParam String attributeName) {
        return profileService.getAttribute(profileId, attributeName);
    }

    @RequestMapping(value = "delete_attributes", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAttributes(@RequestParam String profileId, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributeNames) {
        profileService.deleteAttributes(profileId, attributeNames);
    }

    @RequestMapping(value = "delete_all_attributes", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAllAttributes(@RequestParam String profileId) {
        profileService.deleteAllAttributes(profileId);
    }

    @RequestMapping(value = "get_by_role", method = RequestMethod.GET)
    @ModelAttribute
    public List<Profile> getProfilesByRole(@RequestParam(ProfileConstants.TENANT_ID) String tenantName,
                                           @RequestParam(ProfileConstants.ROLE_NAME) String roleName) {
        return profileService.getProfilesByRoleName(roleName, tenantName);
    }

    @RequestMapping(value = "add_role", method = RequestMethod.GET)
    @ModelAttribute
    public void addRole(@RequestParam String profileId, @RequestParam String roleName) {

    }

    @RequestMapping(value = "remove_role", method = RequestMethod.GET)
    @ModelAttribute
    public void removeRole(@RequestParam String profileId, @RequestParam String roleName) {

    }

    @RequestMapping(value = "send_forgot_password_email", method = RequestMethod.POST)
    @ModelAttribute
    public Profile sendForgotPasswordEmail(@RequestParam(required = true) String changePasswordUrl,
                                           @RequestParam String username, @RequestParam String tenantName) throws
        CipherException, MailException, NoSuchProfileException {
        return passwordService.forgotPassword(changePasswordUrl, username, tenantName);
    }

    @RequestMapping(value = "reset_password", method = RequestMethod.POST)
    @ModelAttribute
    public Profile resetPassword(@RequestParam String token, @RequestParam String newPassword) throws
        CipherException, MailException, NoSuchProfileException, ParseException, ExpiryDateException {
        return passwordService.resetPassword(newPassword, token);
    }

    /**
     * Helper method to get attributes from request
     *
     * @param request
     * @return
     */
    private Map<String, Serializable> getAttributeMap(HttpServletRequest request) {
        Map<String, Serializable> attributes = new HashMap<String, Serializable>();
        Map reqParams = request.getParameterMap();
        if (!reqParams.isEmpty() && reqParams.keySet() != null) {
            Iterator it = reqParams.keySet().iterator();

            while (it.hasNext()) {
                String key = (String)it.next();
                if (!Arrays.asList(ProfileConstants.BASE_PROFILE_FIELDS).contains(key)) {
                    String[] value = (String[])reqParams.get(key);
                    attributes.put(key, value[0]);
                }
            }
        }
        return attributes;
    }

}