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
package org.craftercms.profile.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.AttributesDeserializationException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST controller for the profile service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_PROFILE)
@Api(value = "profile", basePath = BASE_URL_PROFILE, description = "Profile operations")
public class ProfileController {

    private static final TypeReference<Map<String, Object>> ATTRIBUTES_TYPE_REFERENCE =
            new TypeReference<Map<String, Object>>() { };

    protected ProfileService profileService;
    protected ObjectMapper objectMapper;

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ApiOperation("Creates a new profile for a specific tenant name")
    @RequestMapping(value = URL_PROFILE_CREATE, method = RequestMethod.POST)
    @ResponseBody
    public Profile createProfile(@ApiParam("The name of the tenant to add the profile to")
                                 @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                 @ApiParam("The profile's username")
                                 @RequestParam(PARAM_USERNAME) String username,
                                 @ApiParam("The profile's password")
                                 @RequestParam(PARAM_PASSWORD) String password,
                                 @ApiParam("The profile's email")
                                 @RequestParam(PARAM_EMAIL) String email,
                                 @ApiParam("If the profile should be enabled or not")
                                 @RequestParam(PARAM_ENABLED) boolean enabled,
                                 @ApiParam("The profile's roles")
                                 @RequestParam(value = PARAM_ROLE, required = false) Set<String> roles,
                                 @ApiParam("The additional attributes to add to the profile (specify a JSON string)")
                                 @RequestParam(value = PARAM_ATTRIBUTES, required = false) String serializedAttributes,
                                 @ApiParam("The URL (sans token) the user needs to go in case it needs to verify " +
                                     "the created profile (verification depends on tenant)")
                                 @RequestParam(value = PARAM_VERIFICATION_URL, required = false) String verificationUrl)
            throws ProfileException {
        Map<String, Object> attributes = deserializeAttributes(serializedAttributes);

        return profileService.createProfile(tenantName, username, password, email, enabled, roles, attributes,
                verificationUrl);
    }

    @ApiOperation("Updates the profile's info")
    @RequestMapping(value = URL_PROFILE_UPDATE, method = RequestMethod.POST)
    @ResponseBody
    public Profile updateProfile(@ApiParam("The profile's ID")
                                 @PathVariable(PATH_VAR_ID) String profileId,
                                 @ApiParam("The new username for the profile")
                                 @RequestParam(value = PARAM_USERNAME, required = false) String username,
                                 @ApiParam("The new password for the profile")
                                 @RequestParam(value = PARAM_PASSWORD, required = false) String password,
                                 @ApiParam("The new email for the profile")
                                 @RequestParam(value = PARAM_EMAIL, required = false) String email,
                                 @ApiParam("If the profile should be enabled or not")
                                 @RequestParam(value = PARAM_ENABLED, required = false) Boolean enabled,
                                 @ApiParam("The new roles for the profile")
                                 @RequestParam(value = PARAM_ROLE, required = false) Set<String> roles,
                                 @ApiParam("The attributes to update (specify a JSON string)")
                                 @RequestParam(value = PARAM_ATTRIBUTES, required = false) String serializedAttributes,
                                 @ApiParam("The name of the attributes to return (don't specify to return all)")
                                 @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                 String[] attributesToReturn)
            throws ProfileException {
        Map<String, Object> attributes = deserializeAttributes(serializedAttributes);

        return profileService.updateProfile(profileId, username, password, email, enabled, roles, attributes,
                attributesToReturn);
    }

    @ApiOperation("Enables a profile")
    @RequestMapping(value = URL_PROFILE_ENABLE, method = RequestMethod.POST)
    @ResponseBody
    public Profile enableProfile(@ApiParam("The profile's ID")
                                 @PathVariable(PATH_VAR_ID) String profileId,
                                 @ApiParam("The name of the attributes to return (don't specify to return all)")
                                 @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                 String[] attributesToReturn)
            throws ProfileException {
        return profileService.enableProfile(profileId, attributesToReturn);
    }

    @ApiOperation("Disables a profile")
    @RequestMapping(value = URL_PROFILE_DISABLE, method = RequestMethod.POST)
    @ResponseBody
    public Profile disableProfile(@ApiParam("The profile's ID")
                                  @PathVariable(PATH_VAR_ID) String profileId,
                                  @ApiParam("The name of the attributes to return (don't specify to return all)")
                                  @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                  String[] attributesToReturn) throws ProfileException {
        return profileService.disableProfile(profileId, attributesToReturn);
    }

    @ApiOperation("Assigns roles to a profile")
    @RequestMapping(value = URL_PROFILE_ADD_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Profile addRoles(@ApiParam("The profile's ID")
                            @PathVariable(PATH_VAR_ID) String profileId,
                            @ApiParam("The roles to assign")
                            @RequestParam(PARAM_ROLE) Collection<String> roles,
                            @ApiParam("The name of the attributes to return (don't specify to return all)")
                            @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                            String[] attributesToReturn) throws ProfileException {
        return profileService.addRoles(profileId, roles, attributesToReturn);
    }

    @ApiOperation("Removes assigned roles from a profile")
    @RequestMapping(value = URL_PROFILE_REMOVE_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Profile removeRoles(@ApiParam("The profile's ID")
                               @PathVariable(PATH_VAR_ID) String profileId,
                               @ApiParam("The roles to remove")
                               @RequestParam(PARAM_ROLE) Collection<String> roles,
                               @ApiParam("The name of the attributes to return (don't specify to return all)")
                               @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                               String[] attributesToReturn) throws ProfileException {
        return profileService.removeRoles(profileId, roles, attributesToReturn);
    }

    @ApiOperation("Sets the profile as verified if the verification token is valid")
    @RequestMapping(value = URL_PROFILE_VERIFY, method = RequestMethod.POST)
    @ResponseBody
    public Profile verifyProfile(@ApiParam("The verification token ID")
                                 @RequestParam(PARAM_VERIFICATION_TOKEN_ID) String verificationTokenId,
                                 @ApiParam("The name of the attributes to return (don't specify to return all)")
                                 @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                 String[] attributesToReturn)
            throws ProfileException {
        return profileService.verifyProfile(verificationTokenId, attributesToReturn);
    }


    @ApiOperation("Returns the attributes of a profile")
    @RequestMapping(value = URL_PROFILE_GET_ATTRIBUTES, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAttributes(@ApiParam("The profile's ID")
                                             @PathVariable(PATH_VAR_ID) String profileId,
                                             @ApiParam("The name of the attributes to return (don't specify to " +
                                                 "return all)")
                                             @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                             String[] attributesToReturn)
            throws ProfileException {
        return profileService.getAttributes(profileId, attributesToReturn);
    }

    @ApiOperation(value = "Updates the attributes of a profile", notes = "The specified attributes will be merged " +
        "with existing attributes")
    @RequestMapping(value = URL_PROFILE_UPDATE_ATTRIBUTES, method = RequestMethod.POST)
    @ResponseBody
    public Profile updateAttributes(@ApiParam("The profile's ID")
                                    @PathVariable(PATH_VAR_ID) String profileId,
                                    @ApiParam("The new attributes")
                                    @RequestBody Map<String, Object> attributes,
                                    @ApiParam("The name of the attributes to return (don't specify to return all)")
                                    @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                    String[] attributesToReturn) throws ProfileException {
        return profileService.updateAttributes(profileId, attributes, attributesToReturn);
    }

    @ApiOperation("Removes a list of attributes of a profile")
    @RequestMapping(value = URL_PROFILE_REMOVE_ATTRIBUTES, method = RequestMethod.POST)
    @ResponseBody
    public Profile removeAttributes(@ApiParam("The profile's ID")
                                    @PathVariable(PATH_VAR_ID) String profileId,
                                    @ApiParam("The name of the attributes to remove")
                                    @RequestParam(PARAM_ATTRIBUTE_NAME) Collection<String> attributeNames,
                                    @ApiParam("The name of the attributes to return (don't specify to return all)")
                                    @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                    String[] attributesToReturn)
        throws ProfileException {
        return profileService.removeAttributes(profileId, attributeNames, attributesToReturn);
    }

    @ApiOperation("Deletes a profile")
    @RequestMapping(value = URL_PROFILE_DELETE_PROFILE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deleteProfile(@ApiParam("The profile's ID")
                              @PathVariable(PATH_VAR_ID) String profileId) throws ProfileException {
        profileService.deleteProfile(profileId);
    }

    @ApiOperation("Returns the single profile that matches the specified query")
    @RequestMapping(value = URL_PROFILE_GET_ONE_BY_QUERY, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfileByQuery(@ApiParam("The tenant's name")
                                     @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                     @ApiParam("The Mongo query used to search for the profiles. Must not contain " +
                                         "the $where operator, the tenant's name (already specified) or any " +
                                         "non-readable attribute by the application")
                                     @RequestParam(PARAM_QUERY) String query,
                                     @ApiParam("The name of the attributes to return (don't specify to return all)")
                                     @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                     String[] attributesToReturn) throws ProfileException {
        return profileService.getProfileByQuery(tenantName, query, attributesToReturn);
    }

    @ApiOperation("Returns the profile for the specified ID")
    @RequestMapping(value = URL_PROFILE_GET, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfile(@ApiParam("The profile's ID")
                              @PathVariable(PATH_VAR_ID) String profileId,
                              @ApiParam("The name of the attributes to return (don't specify to return all)")
                              @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                              String[] attributesToReturn) throws ProfileException {
        return profileService.getProfile(profileId, attributesToReturn);
    }

    @ApiOperation("Returns the user for the specified tenant and username")
    @RequestMapping(value = URL_PROFILE_GET_BY_USERNAME, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfileByUsername(@ApiParam("The tenant's name")
                                        @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                        @ApiParam("The profile's username")
                                        @RequestParam(PARAM_USERNAME) String username,
                                        @ApiParam("The name of the attributes to return (don't specify to return all)")
                                        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                        String[] attributesToReturn) throws ProfileException {
        return profileService.getProfileByUsername(tenantName, username, attributesToReturn);
    }

    @ApiOperation("Returns the profile for the specified ticket")
    @RequestMapping(value = URL_PROFILE_GET_BY_TICKET, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfileByTicket(@ApiParam("The ID ticket of the authenticated profile")
                                      @RequestParam (PARAM_TICKET_ID) String ticketId,
                                      @ApiParam("The name of the attributes to return (don't specify to return all)")
                                      @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                      String[] attributesToReturn) throws ProfileException {
        return profileService.getProfileByTicket(ticketId, attributesToReturn);
    }

    @ApiOperation("Returns the number of profiles of the specified tenant")
    @RequestMapping(value = URL_PROFILE_GET_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public long getProfileCount(@ApiParam("The tenant's name")
                                @RequestParam(PARAM_TENANT_NAME) String tenantName) throws ProfileException {
        return profileService.getProfileCount(tenantName);
    }

    @ApiOperation("Returns the number of profiles that match the query for the specified tenant")
    @RequestMapping(value = URL_TENANT_COUNT_BY_QUERY, method = RequestMethod.GET)
    @ResponseBody
    public long getProfileCount(@ApiParam("The tenant's name")
                                @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                @ApiParam("The Mongo query used to search for the profiles. Must not contain " +
                                    "the $where operator, the tenant's name (already specified) or any " +
                                    "non-readable attribute by the application")
                                @RequestParam(PARAM_QUERY) String query) throws ProfileException {
        return profileService.getProfileCountByQuery(tenantName, query);
    }

    @ApiOperation("Returns the profiles that match the specified query")
    @RequestMapping(value = URL_PROFILE_GET_BY_QUERY, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfilesByQuery(@ApiParam("The tenant's name")
                                            @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                            @ApiParam("The Mongo query used to search for the profiles. Must not " +
                                                "contain the $where operator, the tenant's name (already specified) " +
                                                "or any non-readable attribute by the application")
                                            @RequestParam(PARAM_QUERY) String query,
                                            @ApiParam("Profile attribute to sort the list by")
                                            @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                            @ApiParam("The sort order (either ASC or DESC)")
                                            @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                            SortOrder sortOrder,
                                            @ApiParam("From the entire list of results, the position where the " +
                                                "actual results should start (useful for pagination)")
                                            @RequestParam(value = PARAM_START, required = false) Integer start,
                                            @ApiParam("The number of profiles to return")
                                            @RequestParam(value = PARAM_COUNT, required = false) Integer count,
                                            @ApiParam("The name of the attributes to return (don't specify to " +
                                                "return all)")
                                            @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                            String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByQuery(tenantName, query, sortBy, sortOrder, start, count, attributesToReturn);
    }

    @ApiOperation("Returns a list of profiles for the specified list of IDs")
    @RequestMapping(value = URL_PROFILE_GET_BY_IDS, method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Profile> getProfileByIds(@ApiParam("The IDs of the profiles to look for")
                                             @RequestParam(PATH_VAR_ID) List<String> profileIds,
                                             @ApiParam("Profile attribute to sort the list by")
                                             @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                             @ApiParam("The sort order (either ASC or DESC)")
                                             @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                             SortOrder sortOrder,
                                             @ApiParam("The name of the attributes to return (don't specify to " +
                                                 "return all)")
                                             @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                             String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByIds(profileIds, sortBy, sortOrder, attributesToReturn);
    }

    @ApiOperation("Returns a range of profiles for the specified tenant")
    @RequestMapping(value = URL_PROFILE_GET_RANGE, method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Profile> getProfileRange(@ApiParam("The tenant's name")
                                             @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                             @ApiParam("Profile attribute to sort the list by")
                                             @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                             @ApiParam("The sort order (either ASC or DESC)")
                                             @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                             SortOrder sortOrder,
                                             @ApiParam("From the entire list of results, the position where the " +
                                                 "actual results should start (useful for pagination)")
                                             @RequestParam(value = PARAM_START, required = false) Integer start,
                                             @ApiParam("The number of profiles to return")
                                             @RequestParam(value = PARAM_COUNT, required = false) Integer count,
                                             @ApiParam("The name of the attributes to return (don't specify to " +
                                                 "return all)")
                                             @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                             String[] attributesToReturn) throws ProfileException {
        return profileService.getProfileRange(tenantName, sortBy, sortOrder, start, count, attributesToReturn);
    }

    @ApiOperation("Returns a list of profiles for a specific role and tenant")
    @RequestMapping(value = URL_PROFILE_GET_BY_ROLE, method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Profile> getProfilesByRole(@ApiParam("The tenant's name")
                                               @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                               @ApiParam("The role's name")
                                               @RequestParam (PARAM_ROLE) String role,
                                               @ApiParam("Profile attribute to sort the list by")
                                               @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                               @ApiParam("The sort order (either ASC or DESC)")
                                               @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                               SortOrder sortOrder,
                                               @ApiParam("The name of the attributes to return (don't specify to " +
                                                   "return all)")
                                               @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                               String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByRole(tenantName, role, sortBy, sortOrder, attributesToReturn);
    }

    @ApiOperation("Returns the list of profiles that have the given attribute, with any value")
    @RequestMapping(value = URL_PROFILE_GET_BY_EXISTING_ATTRIB, method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Profile> getProfilesByExistingAttribute(@ApiParam("The tenant's name")
                                                            @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                                            @ApiParam("The name of the attribute profiles must have")
                                                            @RequestParam(PARAM_ATTRIBUTE_NAME) String attributeName,
                                                            @ApiParam("Profile attribute to sort the list by")
                                                            @RequestParam(value = PARAM_SORT_BY, required = false)
                                                            String sortBy,
                                                            @ApiParam("The sort order (either ASC or DESC)")
                                                            @RequestParam(value = PARAM_SORT_ORDER, required = false)
                                                            SortOrder sortOrder,
                                                            @ApiParam("The name of the attributes to return (don't " +
                                                                "specify to return all)")
                                                            @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN,
                                                                   required = false)
                                                            String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByExistingAttribute(tenantName, attributeName, sortBy, sortOrder,
                attributesToReturn);
    }

    @ApiOperation("Returns the list of profiles that have the given attribute with the given value")
    @RequestMapping(value = URL_PROFILE_GET_BY_ATTRIB_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Profile> getProfilesByAttributeValue(@ApiParam("The tenant's name")
                                                         @RequestParam(PARAM_TENANT_NAME) String tenantName,
                                                         @ApiParam("The name of the attribute profiles must have")
                                                         @RequestParam(PARAM_ATTRIBUTE_NAME) String attributeName,
                                                         @ApiParam("The value of the attribute profiles must have")
                                                         @RequestParam(PARAM_ATTRIBUTE_VALUE) String attributeValue,
                                                         @ApiParam("Profile attribute to sort the list by")
                                                         @RequestParam(value = PARAM_SORT_BY,
                                                             required = false) String sortBy,
                                                         @ApiParam("The sort order (either ASC or DESC)")
                                                         @RequestParam(value = PARAM_SORT_ORDER,
                                                             required = false) SortOrder sortOrder,
                                                         @ApiParam("The name of the attributes to return (don't " +
                                                             "specify to return all)")
                                                         @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN,
                                                             required = false)
                                                         String[] attributesToReturn) throws ProfileException {
        return profileService.getProfilesByAttributeValue(tenantName, attributeName, attributeValue, sortBy, sortOrder,
                attributesToReturn);
    }

    @ApiOperation("Common forgot password functionality: sends the profile an email with an URL to reset their " +
        "password")
    @RequestMapping(value = URL_PROFILE_FORGOT_PASSWORD, method = RequestMethod.POST)
    @ResponseBody
    public Profile forgotPassword(@ApiParam("The profile's ID")
                                  @PathVariable(PATH_VAR_ID) String profileId,
                                  @ApiParam("The base URL to use to build the final URL the profile will use to " +
                                      "reset their password.")
                                  @RequestParam(PARAM_RESET_PASSWORD_URL) String resetPasswordUrl,
                                  @ApiParam("The name of the attributes to return (don't specify to return all)")
                                  @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                  String[] attributesToReturn) throws ProfileException {
        return profileService.resetPassword(profileId, resetPasswordUrl, attributesToReturn);
    }

    @ApiOperation("Resets a profile's password")
    @RequestMapping(value = URL_PROFILE_RESET_PASSWORD, method = RequestMethod.POST)
    @ResponseBody
    public Profile resetPassword(@ApiParam("The reset token ID")
                                 @RequestParam(PARAM_RESET_TOKEN_ID) String resetTokenId,
                                 @ApiParam("The new password")
                                 @RequestParam(PARAM_NEW_PASSWORD) String newPassword,
                                 @ApiParam("The name of the attributes to return (don't specify to return all)")
                                 @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                 String[] attributesToReturn) throws ProfileException {
        return profileService.changePassword(resetTokenId, newPassword, attributesToReturn);
    }

    protected Map<String, Object> deserializeAttributes(String serializedAttributes)
            throws AttributesDeserializationException {
        Map<String, Object> attributes = null;
        if (StringUtils.isNotEmpty(serializedAttributes)) {
            try {
                attributes = objectMapper.readValue(serializedAttributes, ATTRIBUTES_TYPE_REFERENCE);
            } catch (Exception e) {
                throw new AttributesDeserializationException(e);
            }
        }

        return attributes;
    }

}
