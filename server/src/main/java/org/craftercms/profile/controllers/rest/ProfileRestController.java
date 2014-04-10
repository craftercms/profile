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

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.*;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.VerifyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping("/api/2/profile/")
public class ProfileRestController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private VerifyAccountService verifyAccountService;

    /**
     * Create a profile.
     *
     * @param request
     * @param userName
     * @param password
     * @param active
     * @param tenantName
     * @param email
     * @param rolesArray
     * @param verificationAccountUrl
     * @param response
     * @return The created Profile.
     * @throws InvalidEmailException
     * @throws CipherException
     * @throws MailException
     * @throws NoSuchProfileException
     * @throws TenantException
     * @throws MongoDataException
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Profile createProfile(final HttpServletRequest request, @RequestParam(ProfileConstants.USER_NAME) final
    String userName, @RequestParam(ProfileConstants.PASSWORD) final String password, @RequestParam(ProfileConstants.ACTIVE) final Boolean active,
                                 @RequestParam(ProfileConstants.TENANT_NAME) final String tenantName,
                                 @RequestParam(ProfileConstants.EMAIL) final String email,
                                 @RequestParam(required = false, value = ProfileConstants.ROLES) final String[]
                                     rolesArray, @RequestParam(required = false) final String verificationAccountUrl,
                                 final HttpServletResponse response) throws InvalidEmailException, CipherException, MailException, NoSuchProfileException, TenantException, MongoDataException {
        return profileService.createProfile(userName, password, active, tenantName, email, getAttributeMap(request),
            (rolesArray != null? Arrays.asList(rolesArray): null), verificationAccountUrl, response, request);
    }

    /**
     * Get Profiles Count.
     *
     * @return
     */
    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ModelAttribute
    public long getProfilesCount(@RequestParam(ProfileConstants.TENANT_NAME) final String tenantName) throws
        ProfileException {
        return profileService.getProfilesCount(tenantName);
    }

    /**
     * Get Profiles in given range.
     *
     *
     * @param start
     * @param end
     * @return
     */
    @RequestMapping(value = "range", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Profile> getProfileRange(@RequestParam(ProfileConstants.TENANT_NAME) final String tenantName,
                                             @RequestParam(required = false,
        value = ProfileConstants.SORT_BY) final String sortBy, @RequestParam(required = false,
        value = ProfileConstants.SORT_ORDER) final String sortOrder, @RequestParam(ProfileConstants.START) final int
        start, @RequestParam(ProfileConstants.END) final int end, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) final List<String> attributes) throws ProfileException {
        return profileService.getProfileRange(tenantName, sortBy, sortOrder, attributes, start, end);
    }

    /**
     * Get Profile
     *
     * @param profileId
     * @return
     */
    @RequestMapping(value = "{profileId}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfile(@PathVariable final String profileId) throws ProfileException {
        return profileService.getProfile(profileId);
    }

    @RequestMapping(value = "profiles/{attribute}/{attributeValue}", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Profile> getProfilesByAttributeValue(@PathVariable final String attribute,
                                                         @PathVariable final String attributeValue) throws
        ProfileException {

        return profileService.getProfilesByAttributeValue(attribute, attributeValue);
    }

    /**
     * Get Profile with Attributes
     *
     * @param profileId
     * @return
     */
    @RequestMapping(value = "{profileId}/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileWithAttributes(@PathVariable final String profileId, @RequestParam(ProfileConstants
        .ATTRIBUTES) final List<String> attributes) throws ProfileException {
        return profileService.getProfile(profileId, attributes);
    }

    /**
     * Get Profile with all Attributes
     *
     * @param profileId
     * @return
     */
    @RequestMapping(value = "{profileId}/with_all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileWithAllAttributes(@PathVariable final String profileId) throws ProfileException {
        return profileService.getProfileWithAllAttributes(profileId);
    }

    /**
     * Get Profile By UserName
     *
     * @param username
     * @return
     * @throws NoSuchProfileException
     */
    @RequestMapping(value = "username/{username}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByUsername(@PathVariable final String username, @RequestParam(ProfileConstants
        .TENANT_NAME) final String tenantName) throws NoSuchProfileException, ProfileException {
        Profile profile = profileService.getProfileByUserName(username, tenantName);

        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for username='%s'.", username));
        }

        return profile;
    }

    /**
     * Get Profile By UserName with attributes
     *
     * @param username
     * @param tenantName
     * @param attributes
     * @return
     * @throws NoSuchProfileException
     */
    @RequestMapping(value = "username/{username}/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByUsernameWithAttributes(@PathVariable final String username, @RequestParam(ProfileConstants.TENANT_NAME) final String tenantName, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) final List<String> attributes) throws NoSuchProfileException,
        ProfileException {

        Profile profile = profileService.getProfileByUserName(username, tenantName, attributes);

        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for username='%s'.", username));
        }

        return profile;
    }

    /**
     * Get Profile By UserName with All Attributes
     *
     * @param username
     * @return
     * @throws NoSuchProfileException
     */
    @RequestMapping(value = "username/{username}/with_all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByUsernameWithAllAttributes(@PathVariable final String username, @RequestParam(ProfileConstants.TENANT_NAME) final String tenantName) throws NoSuchProfileException, ProfileException {
        Profile profile = profileService.getProfileByUserNameWithAllAttributes(username, tenantName);

        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for username='%s'.", username));
        }

        return profile;
    }

    /**
     * Get a Profile based on the ticket passed as path parameter
     *
     * @param ticket Ticket used to get the Profile
     * @return Profile instance gets from the database.
     * @throws NoSuchProfileException if there is not a Profile associated to the ticket passed.
     */
    @RequestMapping(value = "ticket/{ticket}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByTicket(@PathVariable final String ticket) throws NoSuchProfileException, TicketException, ProfileException {

        Profile profile = profileService.getProfileByTicket(ticket);
        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for ticket='%s'.", ticket));
        }
        return profile;
    }

    /**
     * Gets a Profile based on the ticket passes as path parameter and the profile will include the attributes
     * passed as request parameter.
     *
     * @param ticket     Ticket used to get the Profile
     * @param attributes included in the profile
     * @return Profile instance gets from the database.
     * @throws NoSuchProfileException: if there is not a Profile associated to the ticket passed.
     */
    @RequestMapping(value = "ticket/{ticket}/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByTicketWithAttributes(@PathVariable final String ticket, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) final List<String> attributes) throws NoSuchProfileException, TicketException, ProfileException {

        Profile profile = profileService.getProfileByTicket(ticket, attributes);
        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for ticket='%s'.", ticket));
        }
        return profile;
    }

    /**
     * Gets a Profile based on the ticket passes as path parameter and the profile will include all the attributes
     *
     * @param ticket Ticket used to get the Profile
     * @return Profile instance gets from the database.
     * @throws NoSuchProfileException: if there is not a Profile associated to the ticket passed.
     */
    @RequestMapping(value = "ticket/{ticket}/with_all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByTicketWithAllAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken, @PathVariable String ticket) throws NoSuchProfileException, TicketException, ProfileException {

        Profile profile = profileService.getProfileByTicketWithAllAttributes(ticket);
        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for ticket='%s'.", ticket));
        }
        return profile;
    }

    /**
     * Get Profiles for IDs
     *
     * @param profileIdList ids used to get the profile list
     * @return a list of Profiles
     */
    @RequestMapping(value = "ids", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Profile> getProfiles(@RequestParam(ProfileConstants.PROFILE_ID_LIST) final List<String> profileIdList) throws ProfileException {
        return profileService.getProfiles(profileIdList);
    }

    /**
     * Get Profiles for IDs with Attributes
     *
     * @param profileIdList ids used to get the profile list
     * @return a list of profiles
     */
    @RequestMapping(value = "ids/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Profile> getProfilesWithAttributes(@RequestParam(ProfileConstants.PROFILE_ID_LIST) final
                                                           List<String> profileIdList) throws ProfileException {
        return profileService.getProfilesWithAttributes(profileIdList);
    }

    /**
     * Update a Profile
     *
     * @param profileId  that is going to be updated
     * @param userName   the new username
     * @param password   the new password
     * @param active     the active indicator
     * @param tenantName the tenantName
     * @return Profile instance updated
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ModelAttribute
    public Profile updateProfile(HttpServletRequest request, @RequestParam(ProfileConstants.PROFILE_ID) final String profileId, @RequestParam(required = false,
        value = ProfileConstants.USER_NAME) final String userName, @RequestParam(required = false,
        value = ProfileConstants.PASSWORD) final String password, @RequestParam(required = false,
        value = ProfileConstants.ACTIVE) final boolean active, @RequestParam(required = false,
        value = ProfileConstants.TENANT_NAME) final String tenantName, @RequestParam(ProfileConstants.EMAIL)
                                 final String email, @RequestParam(required = false,
        value = ProfileConstants.ROLES) final String[] rolesArray) throws ProfileException {
        return profileService.updateProfile(profileId, userName, password, active, tenantName, email,
            getAttributeMap(request), (rolesArray != null? Arrays.asList(rolesArray): null));
    }

    /**
     * Actives and inactives All Profiles
     */
    @RequestMapping(value = "active/all", method = RequestMethod.GET)
    @ModelAttribute
    public void activeProfiles(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                               @RequestParam(ProfileConstants.ACTIVE) Boolean active,
                               HttpServletResponse response) throws ProfileException {
        profileService.activateProfiles(active);
    }

    /**
     * Active Profile for appToken and profile Id
     *
     * @param appToken  The application token
     * @param profileId that is going to be deleted
     * @param active    indicates if the profile will be actived or inactived.
     * @param response  Servlet response instance
     */
    @RequestMapping(value = "active/{profileId}", method = RequestMethod.GET)
    @ModelAttribute
    public void activeProfile(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                              @PathVariable String profileId, @RequestParam(ProfileConstants.ACTIVE) Boolean active,
                              HttpServletResponse response) throws ProfileException {
        profileService.activateProfile(profileId, active);
    }

    /**
     * Active Profile for appToken and profile Id
     *
     * @throws ExpiryDateException
     * @throws ParseException
     * @throws NoSuchProfileException
     * @throws CipherException
     */
    @RequestMapping(value = "verify", method = RequestMethod.POST)
    @ModelAttribute
    public Profile verifyProfile(@RequestParam(ProfileConstants.TOKEN) final String token) throws CipherException,
        NoSuchProfileException, ParseException, ExpiryDateException, ProfileException {
        return this.verifyAccountService.verifyAccount(token);
    }

    /**
     * Set attributes to profile
     *
     * @param profileId that is going to be updated
     */
    @RequestMapping(value = "set_attributes/{profileId}", method = RequestMethod.POST)
    @ModelAttribute
    public void setAttributes(final HttpServletRequest request, @PathVariable final String profileId) throws ProfileException {
        profileService.setAttributes(profileId, getAttributeMap(request));
    }

    /**
     * Update attributes to profile
     *
     * @param profileId  that is going to be updated
     * @param attributes the attributes to update
     */
    @RequestMapping(value = "update_attributes/{profileId}", method = RequestMethod.POST)
    @ModelAttribute
    public void updateAttributes(@PathVariable final String profileId, @RequestBody final Map<String,
        Object> attributes) throws ProfileException {
        profileService.setAttributes(profileId, attributes);
    }

    /**
     * Get all Attributes for a profile
     *
     *
     * @param profileId is going to be used to get attributes
     * @return All attributes for a profile
     */
    @RequestMapping(value = "{profileId}/all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Object> getAllAttributes(@PathVariable final String profileId) throws ProfileException {
        return profileService.getAllAttributes(profileId);
    }

    /**
     * Get Attributes for a profile
     *
     *
     * @param profileId  the id of the profile is going to be consulted
     * @param attributes Attributes keys that are going to be get.
     * @return Attributes values
     */
    @RequestMapping(value = "{profileId}/attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Object> getAttributes(@PathVariable final String profileId, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) final List<String> attributes) throws ProfileException {
        return profileService.getAttributes(profileId, attributes);
    }

    /**
     * Get one Attribute for a profile
     *
     *
     * @param profileId    the id for the profile consulted
     * @param attributeKey is going to be used to get its value
     * @return the attribute key/value pair
     */
    @RequestMapping(value = "{profileId}/attribute", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Object> getAttribute(@PathVariable final String profileId, @RequestParam("attributeKey") final String attributeKey) throws ProfileException {
        return profileService.getAttribute(profileId, attributeKey);
    }

    /**
     * Delete Attributes from a profile
     *
     * @param profileId  the profile id is going to be updated
     * @param attributes The attribute keys are going to be deleted
     */
    @RequestMapping(value = "{profileId}/delete_attributes", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAttributes(@PathVariable String profileId, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes) throws ProfileException {
        profileService.deleteAttributes(profileId, attributes);
    }


    /**
     * Delete All Attributes
     *
     * @param profileId the profile id is going to be updated
     */
    @RequestMapping(value = "{profileId}/delete_all_attributes", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAttributes(@PathVariable final String profileId) throws ProfileException {
        profileService.deleteAllAttributes(profileId);
    }

    /**
     * Helper method to get attributes from request
     *
     * @param request
     * @return
     */
    private Map<String, Object> getAttributeMap(final HttpServletRequest request) {
        Map<String, Object> attributes = new HashMap<String, Object>();
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

    /**
     * Get Profiles list which all of them have the role passed as parameter
     *
     * @param tenantName the tenant name
     * @param roleName   is used to get the profile list
     * @return a profile list
     */
    @RequestMapping(value = "profile_role", method = RequestMethod.GET)
    @ModelAttribute
    public Iterable<Profile> getProfilesByRole(@RequestParam(ProfileConstants.TENANT_ID) final String tenantName, @RequestParam(ProfileConstants.ROLE_NAME) final String roleName) throws ProfileException {
        return profileService.getProfilesByRoleName(roleName, tenantName);
    }

}