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

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.InvalidEmailException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.VerifyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/2/profile/")
public class ProfileRestController {

    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private VerifyAccountService verifyAccountService;

    /**
     * Create profile
     *
     * @param appToken
     * @param origin
     * @param userName
     * @param password
     * @param email
     * @param prefix
     * @param firstName
     * @param lastName
     * @param suffix
     * @return
     * @throws InvalidEmailException
     * @throws NoSuchProfileException 
     * @throws MailException 
     * @throws CipherException 
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ModelAttribute
    public Profile createProfile(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String
        appToken, @RequestParam(ProfileConstants.USER_NAME) String userName, @RequestParam(ProfileConstants.PASSWORD)
    String password, @RequestParam(ProfileConstants.ACTIVE) Boolean active,
                                 @RequestParam(ProfileConstants.TENANT_NAME) String tenantName,
                                 @RequestParam(ProfileConstants.EMAIL) String email,
                                 @RequestParam(required=false, value = ProfileConstants.ROLES) String[] rolesArray,
                                 @RequestParam(required=false) String verificationAccountUrl,
                                 HttpServletResponse response) throws InvalidEmailException, CipherException, MailException, NoSuchProfileException {
        return profileService.createProfile(userName, password, active, tenantName, email, getAttributeMap(request),
            (rolesArray != null? Arrays.asList(rolesArray): null), verificationAccountUrl, response, request);
    }

    /**
     * Get Profiles Count
     *
     * @param appToken
     * @param response
     * @return
     */
    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ModelAttribute
    public long getProfilesCount(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String
        appToken, @RequestParam(ProfileConstants.TENANT_NAME) String tenantName, HttpServletResponse response) {
        return profileService.getProfilesCount(tenantName);
    }

    /**
     * Get Profiles in given range
     *
     * @param request
     * @param start
     * @param end
     * @param response
     * @return
     */
    @RequestMapping(value = "range", method = RequestMethod.GET)
    @ModelAttribute
    public List<Profile> getProfileRange(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String
        appToken, @RequestParam(ProfileConstants.TENANT_NAME) String tenantName, @RequestParam(required = false,
        value = ProfileConstants.SORT_BY) String sortBy, @RequestParam(required = false,
        value = ProfileConstants.SORT_ORDER) String sortOrder, @RequestParam(ProfileConstants.START) int start,
                                         @RequestParam(ProfileConstants.END) int end, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes, HttpServletResponse response) {
        return profileService.getProfileRange(tenantName, sortBy, sortOrder, attributes, start, end);
    }

    /**
     * Get Profile
     *
     * @param appToken
     * @param profileId
     * @param response
     * @return
     */
    @RequestMapping(value = "{profileId}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfile(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                              @PathVariable String profileId, HttpServletResponse response) {
        return profileService.getProfile(profileId);
    }

    /**
     * Get Profile with Attributes
     *
     * @param appToken
     * @param profileId
     * @param response
     * @return
     */
    @RequestMapping(value = "{profileId}/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileWithAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                            @PathVariable String profileId,
                                            @RequestParam(ProfileConstants.ATTRIBUTES) List<String> attributes,
                                            HttpServletResponse response) {
        return profileService.getProfile(profileId, attributes);
    }

    /**
     * Get Profile with all Attributes
     *
     * @param appToken
     * @param profileId
     * @param response
     * @return
     */
    @RequestMapping(value = "{profileId}/with_all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileWithAllAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                               @PathVariable String profileId, HttpServletResponse response) {
        return profileService.getProfileWithAllAttributes(profileId);
    }

    /**
     * Get Profile By UserName
     *
     * @param appToken
     * @param username
     * @return
     * @throws NoSuchProfileException
     */
    @RequestMapping(value = "username/{username}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByUsername(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                        @PathVariable String username, @RequestParam(ProfileConstants.TENANT_NAME)
    String tenantName) throws NoSuchProfileException {
        Profile profile = profileService.getProfileByUserName(username, tenantName);

        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for username='%s'.", username));
        }

        return profile;
    }

    /**
     * Get Profile By UserName with attributes
     *
     * @param appToken
     * @param username
     * @param tenantName
     * @param attributes
     * @return
     * @throws NoSuchProfileException
     */
    @RequestMapping(value = "username/{username}/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByUsernameWithAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                      @PathVariable String username,
                                                      @RequestParam(ProfileConstants.TENANT_NAME) String tenantName,
                                                      @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes) throws NoSuchProfileException {

        Profile profile = profileService.getProfileByUserName(username, tenantName, attributes);

        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for username='%s'.", username));
        }

        return profile;
    }

    /**
     * Get Profile By UserName with All Attributes
     *
     * @param appToken
     * @param username
     * @return
     * @throws NoSuchProfileException
     */
    @RequestMapping(value = "username/{username}/with_all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByUsernameWithAllAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                         @PathVariable String username,
                                                         @RequestParam(ProfileConstants.TENANT_NAME) String
                                                             tenantName) throws NoSuchProfileException {
        Profile profile = profileService.getProfileByUserNameWithAllAttributes(username, tenantName);

        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for username='%s'.", username));
        }

        return profile;
    }

    /**
     * Get a Profile based on the ticket passed as path parameter
     *
     * @param appToken The application token
     * @param ticket   Ticket used to get the Profile
     * @return Profile instance gets from the database.
     * @throws NoSuchProfileException if there is not a Profile associated to the ticket passed.
     */
    @RequestMapping(value = "ticket/{ticket}", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByTicket(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                      @PathVariable String ticket) throws NoSuchProfileException {

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
     * @param appToken   The application token
     * @param ticket     Ticket used to get the Profile
     * @param attributes included in the profile
     * @return Profile instance gets from the database.
     * @throws NoSuchProfileException: if there is not a Profile associated to the ticket passed.
     */
    @RequestMapping(value = "ticket/{ticket}/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByTicketWithAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                    @PathVariable String ticket, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes) throws NoSuchProfileException {

        Profile profile = profileService.getProfileByTicket(ticket, attributes);
        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for ticket='%s'.", ticket));
        }
        return profile;
    }

    /**
     * Gets a Profile based on the ticket passes as path parameter and the profile will include all the attributes
     *
     * @param appToken The application token
     * @param ticket   Ticket used to get the Profile
     * @return Profile instance gets from the database.
     * @throws NoSuchProfileException: if there is not a Profile associated to the ticket passed.
     */
    @RequestMapping(value = "ticket/{ticket}/with_all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Profile getProfileByTicketWithAllAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                       @PathVariable String ticket) throws NoSuchProfileException {

        Profile profile = profileService.getProfileByTicketWithAllAttributes(ticket);
        if (profile == null) {
            throw new NoSuchProfileException(String.format("Could not find a profile for ticket='%s'.", ticket));
        }
        return profile;
    }

    /**
     * Get Profiles for IDs
     *
     * @param appToken      The application token
     * @param profileIdList ids used to get the profile list
     * @param response      Servlet response instance
     * @return a list of Profiles
     */
    @RequestMapping(value = "ids", method = RequestMethod.GET)
    @ModelAttribute
    public List<Profile> getProfiles(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                     @RequestParam(ProfileConstants.PROFILE_ID_LIST) List<String> profileIdList,
                                     HttpServletResponse response) {
        return profileService.getProfiles(profileIdList);
    }

    /**
     * Get Profiles for IDs with Attributes
     *
     * @param appToken      The application token
     * @param profileIdList ids used to get the profile list
     * @param response      Servlet response instance
     * @return a list of profiles
     */
    @RequestMapping(value = "ids/with_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public List<Profile> getProfilesWithAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                   @RequestParam(ProfileConstants.PROFILE_ID_LIST) List<String>
                                                       profileIdList, HttpServletResponse response) {
        return profileService.getProfilesWithAttributes(profileIdList);
    }

    /**
     * Update a Profile
     *
     * @param appToken   The application token
     * @param profileId  that is going to be updated
     * @param userName   the new username
     * @param password   the new password
     * @param active     the active indicator
     * @param tenantName the tenantName
     * @param roles      The list of roles
     * @return Profile instance updated
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ModelAttribute
    public Profile updateProfile(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String
        appToken, @RequestParam(ProfileConstants.PROFILE_ID) String profileId, @RequestParam(required = false,
        value = ProfileConstants.USER_NAME) String userName, @RequestParam(required = false,
        value = ProfileConstants.PASSWORD) String password, @RequestParam(required = false,
        value = ProfileConstants.ACTIVE) Boolean active, @RequestParam(required = false,
        value = ProfileConstants.TENANT_NAME) String tenantName, @RequestParam(ProfileConstants.EMAIL) String email,
                                 @RequestParam(required = false, value = ProfileConstants.ROLES) String[] rolesArray,
                                 HttpServletResponse response) {
        return profileService.updateProfile(profileId, userName, password, active, tenantName, email,
            getAttributeMap(request), (rolesArray != null? Arrays.asList(rolesArray): null));
    }

    /**
     * Actives and inactives All Profiles
     */
    @RequestMapping(value = "active/all", method = RequestMethod.GET)
    @ModelAttribute
    public void activeProfiles(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                               @RequestParam(ProfileConstants.ACTIVE) Boolean active, HttpServletResponse response) {
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
                              HttpServletResponse response) {
        profileService.activateProfile(profileId, active);
    }
    
    /**
     * Active Profile for appToken and profile Id
     *
     * @param appToken  The application token
     * @param profileId that is going to be deleted
     * @param active    indicates if the profile will be actived or inactived.
     * @param response  Servlet response instance
     * @throws ExpiryDateException 
     * @throws ParseException 
     * @throws NoSuchProfileException 
     * @throws CipherException 
     */
    @RequestMapping(value = "verify", method = RequestMethod.POST)
    @ModelAttribute
    public Profile verifyProfile(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                              @RequestParam(ProfileConstants.TOKEN) String token,
                              HttpServletResponse response) throws CipherException, NoSuchProfileException, ParseException, ExpiryDateException {
        return this.verifyAccountService.verifyAccount(token);
    }

    /**
     * Set attributes to profile
     *
     * @param appToken      The application token
     * @param profileId     that is going to be updated
     */
    @RequestMapping(value = "set_attributes/{profileId}", method = RequestMethod.POST)
    @ModelAttribute
    public void setAttributes(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                              @PathVariable String profileId, HttpServletResponse response) {
        profileService.setAttributes(profileId, getAttributeMap(request));
    }

    /**
     * Update attributes to profile
     *
     * @param appToken      The application token
     * @param profileId     that is going to be updated
     * @param attributes    the attributes to update
     */
    @RequestMapping(value = "update_attributes/{profileId}", method = RequestMethod.POST)
    @ModelAttribute
    public void updateAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                 @PathVariable String profileId,
                                 @RequestBody Map<String, Serializable> attributes) {
        profileService.setAttributes(profileId, attributes);
    }

    /**
     * Get all Attributes for a profile
     *
     * @param appToken  The application token
     * @param profileId is going to be used to get attributes
     * @param response  Servlet response instance
     * @return All attributes for a profile
     */
    @RequestMapping(value = "{profileId}/all_attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Serializable> getAllAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                      @PathVariable String profileId, HttpServletResponse response) {
        return profileService.getAllAttributes(profileId);
    }

    /**
     * Get Attributes for a profile
     *
     * @param appToken   The application token
     * @param profileId  the id of the profile is going to be consulted
     * @param attributes Attributes keys that are going to be get.
     * @param response   The response instance
     * @return Attrites values
     */
    @RequestMapping(value = "{profileId}/attributes", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Serializable> getAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                   @PathVariable String profileId, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes, HttpServletResponse response) {
        return profileService.getAttributes(profileId, attributes);
    }

    /**
     * Get one Attribute for a profile
     *
     * @param appToken     The application token
     * @param profileId    the id for the profile consulted
     * @param attributeKey is going to be used to get its value
     * @param response     instance
     * @return the attribute key/value pair
     */
    @RequestMapping(value = "{profileId}/attribute", method = RequestMethod.GET)
    @ModelAttribute
    public Map<String, Serializable> getAttribute(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                                  @PathVariable String profileId,
                                                  @RequestParam("attributeKey") String attributeKey,
                                                  HttpServletResponse response) {
        return profileService.getAttribute(profileId, attributeKey);
    }

    /**
     * Delete Attributes from a profile
     *
     * @param appToken   The application token
     * @param profileId  the profile id is going to be updated
     * @param attributes The attribute keys are going to be deleted
     * @param response   instance
     */
    @RequestMapping(value = "{profileId}/delete_attributes", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAttributes(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String
        appToken, @PathVariable String profileId, @RequestParam(required = false,
        value = ProfileConstants.ATTRIBUTES) List<String> attributes, HttpServletResponse response) {
        profileService.deleteAttributes(profileId, attributes);
    }


    /**
     * Delete All Attributes
     *
     * @param appToken  The application token
     * @param profileId the profile id is going to be updated
     * @param response  isntance
     */
    @RequestMapping(value = "{profileId}/delete_all_attributes", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAttributes(@RequestParam(ProfileConstants.APP_TOKEN) String appToken,
                                 @PathVariable String profileId, HttpServletResponse response) {
        profileService.deleteAllAttributes(profileId);
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

    /**
     * Get Profiles list which all of them have the role passed as parameter
     *
     * @param appToken   The application token
     * @param request    servlet instance
     * @param tenantName the tenant name
     * @param roleName   is used to get the profile list
     * @return a profile list
     */
    @RequestMapping(value = "profile_role", method = RequestMethod.GET)
    @ModelAttribute
    public List<Profile> getProfilesByRole(HttpServletRequest request, @RequestParam(ProfileConstants.APP_TOKEN) String appToken, @RequestParam(ProfileConstants.TENANT_ID) String tenantName, @RequestParam(ProfileConstants.ROLE_NAME) String roleName) {
        return profileService.getProfilesByRoleName(roleName, tenantName);
    }

}