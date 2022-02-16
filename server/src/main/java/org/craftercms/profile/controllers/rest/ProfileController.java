/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileAttachment;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.exceptions.NoSuchVerificationTokenException;
import org.craftercms.profile.exceptions.ParamDeserializationException;
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
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST controller for the profile service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_PROFILE)
public class ProfileController {

    private static final TypeReference<Map<String, Object>> ATTRIBUTES_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {};

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

    @RequestMapping(value = URL_PROFILE_CREATE, method = RequestMethod.POST)
    @ResponseBody
    public Profile createProfile(
        @RequestParam(PARAM_TENANT_NAME) String tenantName,
        @RequestParam(PARAM_USERNAME) String username,
        @RequestParam(value = PARAM_PASSWORD, required = false) String password,
        @RequestParam(PARAM_EMAIL) String email,
        @RequestParam(PARAM_ENABLED) boolean enabled,
        @RequestParam(value = PARAM_ROLE, required = false) Set<String> roles,
        @RequestParam(value = PARAM_ATTRIBUTES, required = false) String serializedAttributes,
        @RequestParam(value = PARAM_VERIFICATION_URL, required = false)
        String verificationUrl) throws ProfileException {
        Map<String, Object> attributes = deserializeAttributes(serializedAttributes);

        return profileService.createProfile(tenantName, username, password, email, enabled, roles, attributes, verificationUrl);
    }

    @RequestMapping(value = URL_PROFILE_UPDATE, method = RequestMethod.POST)
    @ResponseBody
    public Profile updateProfile(@PathVariable(PATH_VAR_ID) String profileId,
                                 @RequestParam(value = PARAM_USERNAME, required = false) String username,
                                 @RequestParam(value = PARAM_PASSWORD, required = false) String password,
                                 @RequestParam(value = PARAM_EMAIL, required = false) String email,
                                 @RequestParam(value = PARAM_ENABLED, required = false) Boolean enabled,
                                 @RequestParam(value = PARAM_ROLE, required = false) Set<String> roles,
                                 @RequestParam(value = PARAM_ATTRIBUTES, required = false) String serializedAttributes,
                                 @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                 String[] attributesToReturn) throws ProfileException {
        Map<String, Object> attributes = deserializeAttributes(serializedAttributes);

        return profileService.updateProfile(profileId, username, password, email, enabled, roles, attributes, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_ENABLE, method = RequestMethod.POST)
    @ResponseBody
    public Profile enableProfile(@PathVariable(PATH_VAR_ID) String profileId,
                                 @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                 String[] attributesToReturn) throws ProfileException {
        return profileService.enableProfile(profileId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_DISABLE, method = RequestMethod.POST)
    @ResponseBody
    public Profile disableProfile(@PathVariable(PATH_VAR_ID) String profileId,
                                  @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                  String[] attributesToReturn) throws ProfileException {
        return profileService.disableProfile(profileId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_ADD_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Profile addRoles(@PathVariable(PATH_VAR_ID) String profileId,
                            @RequestParam(PARAM_ROLE) Collection<String> roles,
                            @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                            String[] attributesToReturn) throws ProfileException {
        return profileService.addRoles(profileId, roles, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_REMOVE_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Profile removeRoles(@PathVariable(PATH_VAR_ID) String profileId,
                               @RequestParam(PARAM_ROLE) Collection<String> roles,
                               @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                               String[] attributesToReturn) throws ProfileException {
        return profileService.removeRoles(profileId, roles, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_VERIFY, method = RequestMethod.POST)
    @ResponseBody
    public Profile verifyProfile(
        @RequestParam(PARAM_VERIFICATION_TOKEN_ID) String verificationTokenId,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
        String[] attributesToReturn) throws ProfileException {
        return profileService.verifyProfile(verificationTokenId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_GET_ATTRIBUTES, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAttributes(@PathVariable(PATH_VAR_ID) String profileId,
                                             @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                             String[] attributesToReturn) throws ProfileException {
        return profileService.getAttributes(profileId, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_UPDATE_ATTRIBUTES, method = RequestMethod.POST)
    @ResponseBody
    public Profile updateAttributes(@PathVariable(PATH_VAR_ID) String profileId,
                                    @RequestBody Map<String, Object> attributes,
                                    @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                    String[] attributesToReturn) throws ProfileException {
        return profileService.updateAttributes(profileId, attributes, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_REMOVE_ATTRIBUTES, method = RequestMethod.POST)
    @ResponseBody
    public Profile removeAttributes(@PathVariable(PATH_VAR_ID) String profileId,
                                    @RequestParam(PARAM_ATTRIBUTE_NAME) Collection<String> attributeNames,
                                    @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                    String[] attributesToReturn) throws ProfileException {
        return profileService.removeAttributes(profileId, attributeNames, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_DELETE_PROFILE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deleteProfile(@PathVariable(PATH_VAR_ID) String profileId) throws ProfileException {
        profileService.deleteProfile(profileId);
    }

    @RequestMapping(value = URL_PROFILE_GET_ONE_BY_QUERY, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfileByQuery(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                     @RequestParam(PARAM_QUERY) String query,
                                     @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                     String[] attributesToReturn) throws ProfileException {
        Profile profile = profileService.getProfileByQuery(tenantName, query, attributesToReturn);
        if (profile != null) {
            return profile;
        } else {
            throw new NoSuchProfileException.ByQuery(tenantName, query);
        }
    }

    @RequestMapping(value = URL_PROFILE_GET, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfile(@PathVariable(PATH_VAR_ID) String profileId,
                              @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                              String[] attributesToReturn) throws ProfileException {
        Profile profile = profileService.getProfile(profileId, attributesToReturn);
        if (profile != null) {
            return profile;
        } else {
            throw new NoSuchProfileException.ById(profileId);
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_USERNAME, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfileByUsername(
            @RequestParam(PARAM_TENANT_NAME) String tenantName,
            @RequestParam(PARAM_USERNAME) String username,
            @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        Profile profile = profileService.getProfileByUsername(tenantName, username, attributesToReturn);
        if (profile != null) {
            return profile;
        } else {
            throw new NoSuchProfileException.ByUsername(tenantName, username);
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_TICKET, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfileByTicket(
        @RequestParam(PARAM_TICKET_ID) String ticketId,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        Profile profile = profileService.getProfileByTicket(ticketId, attributesToReturn);
        if (profile != null) {
            return profile;
        } else {
            throw new NoSuchProfileException.ByTicket(ticketId);
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public long getProfileCount(@RequestParam(PARAM_TENANT_NAME) String tenantName) throws ProfileException {
        return profileService.getProfileCount(tenantName);
    }

    @RequestMapping(value = URL_TENANT_COUNT_BY_QUERY, method = RequestMethod.GET)
    @ResponseBody
    public long getProfileCount(@RequestParam(PARAM_TENANT_NAME) String tenantName,
                                @RequestParam(PARAM_QUERY) String query) throws ProfileException {
        return profileService.getProfileCountByQuery(tenantName, query);
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_QUERY, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfilesByQuery(
        @RequestParam(PARAM_TENANT_NAME) String tenantName,
        @RequestParam(PARAM_QUERY) String query,
        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
        @RequestParam(value = PARAM_START, required = false) Integer start,
        @RequestParam(value = PARAM_COUNT, required = false) Integer count,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        List<Profile> profiles = profileService.getProfilesByQuery(tenantName, query, sortBy, sortOrder, start, count, attributesToReturn);
        if (profiles != null) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_IDS, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfileByIds(
        @RequestParam(PATH_VAR_ID) List<String> profileIds,
        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        List<Profile> profiles = profileService.getProfilesByIds(profileIds, sortBy, sortOrder, attributesToReturn);
        if (profiles != null) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_RANGE, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfileRange(
        @RequestParam(PARAM_TENANT_NAME) String tenantName,
        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
        @RequestParam(value = PARAM_START, required = false) Integer start,
        @RequestParam(value = PARAM_COUNT, required = false) Integer count,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        List<Profile> profiles = profileService.getProfileRange(tenantName, sortBy, sortOrder, start, count, attributesToReturn);
        if (profiles != null) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_ROLE, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfilesByRole(
        @RequestParam(PARAM_TENANT_NAME) String tenantName,
        @RequestParam(PARAM_ROLE) String role,
        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        List<Profile> profiles = profileService.getProfilesByRole(tenantName, role, sortBy, sortOrder, attributesToReturn);
        if (profiles != null) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_EXISTING_ATTRIB, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfilesByExistingAttribute(
        @RequestParam(PARAM_TENANT_NAME) String tenantName,
        @RequestParam(PARAM_ATTRIBUTE_NAME) String attributeName,
        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        List<Profile> profiles = profileService.getProfilesByExistingAttribute(tenantName, attributeName, sortBy, sortOrder,
                                                                               attributesToReturn);
        if (profiles != null) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_BY_ATTRIB_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfilesByAttributeValue(
        @RequestParam(PARAM_TENANT_NAME) String tenantName,
        @RequestParam(PARAM_ATTRIBUTE_NAME) String attributeName,
        @RequestParam(PARAM_ATTRIBUTE_VALUE) String attributeValue,
        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        List<Profile> profiles = profileService.getProfilesByAttributeValue(tenantName, attributeName, attributeValue, sortBy,
                                                                            sortOrder, attributesToReturn);
        if (profiles != null) {
            return profiles;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_PROFILE_RESET_PASSWORD, method = RequestMethod.POST)
    @ResponseBody
    public Profile resetPassword(@PathVariable(PATH_VAR_ID) String profileId,
                                 @RequestParam(PARAM_RESET_PASSWORD_URL) String resetPasswordUrl,
                                 @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false)
                                 String[] attributesToReturn) throws ProfileException {
        return profileService.resetPassword(profileId, resetPasswordUrl, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_CHANGE_PASSWORD, method = RequestMethod.POST)
    @ResponseBody
    public Profile changePassword(
        @RequestParam(PARAM_RESET_TOKEN_ID) String resetTokenId,
        @RequestParam(PARAM_NEW_PASSWORD) String newPassword,
        @RequestParam(value = PARAM_ATTRIBUTE_TO_RETURN, required = false) String[] attributesToReturn) throws ProfileException {
        return profileService.changePassword(resetTokenId, newPassword, attributesToReturn);
    }

    @RequestMapping(value = URL_PROFILE_CREATE_VERIFICATION_TOKEN, method = RequestMethod.POST)
    @ResponseBody
    public VerificationToken createVerificationToken(@PathVariable(PATH_VAR_ID) String profileId) throws ProfileException {
        return profileService.createVerificationToken(profileId);
    }

    @RequestMapping(value = URL_PROFILE_GET_VERIFICATION_TOKEN, method = RequestMethod.GET)
    @ResponseBody
    public VerificationToken getVerificationToken(@PathVariable(PATH_VAR_ID) String tokenId) throws ProfileException {
        VerificationToken token = profileService.getVerificationToken(tokenId);
        if (token != null) {
            return token;
        } else {
            throw new NoSuchVerificationTokenException(tokenId);
        }
    }

    @RequestMapping(value = URL_PROFILE_DELETE_VERIFICATION_TOKEN, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deleteVerificationToken(@PathVariable(PATH_VAR_ID) String tokenId) throws ProfileException {
        profileService.deleteVerificationToken(tokenId);
    }

    @ResponseBody
    @RequestMapping(value = URL_PROFILE_UPLOAD_ATTACHMENT, method = RequestMethod.POST)
    public ProfileAttachment uploadProfileAttachment(@PathVariable(PATH_VAR_ID) String profileId,
                                                     @RequestParam(name=PARAM_FILENAME, required = false) String filename,
                                                     MultipartFile attachment) throws ProfileException {
        Profile profile = profileService.getProfile(profileId);
        if (profile != null) {
            String attachmentName = StringUtils.isNotBlank(filename) ? filename : attachment.getOriginalFilename();
            try {
                return profileService.addProfileAttachment(profile.getId().toString(), attachmentName,
                                                           attachment.getInputStream());
            } catch (IOException e) {
                throw new ProfileException("Unable to upload Attachment", e);
            }
        } else {
            throw new NoSuchProfileException.ById(profileId);
        }
    }


    @RequestMapping(value = URL_PROFILE_GET_ATTACHMENTS, method = RequestMethod.GET)
    @ResponseBody
    public List<ProfileAttachment> getAttachments(
            String profileId) throws ProfileException, IOException {
        Profile profile = profileService.getProfile(profileId);
        if (profile != null) {
            return profileService.getProfileAttachments(profile.getId().toString());
        } else {
            throw new NoSuchProfileException.ById(profileId);
        }
    }


    @RequestMapping(value = URL_PROFILE_GET_ATTACHMENTS_DETAILS, method = RequestMethod.GET)
    @ResponseBody
    public ProfileAttachment getAttachmentDetails(
            @PathVariable(PATH_VAR_ID) String profileId,
            @PathVariable(PATH_VAR_ATTACHMENT) String attachmentId) throws ProfileException, IOException {
        Profile profile = profileService.getProfile(profileId);
        if (profile != null) {
            return profileService.getProfileAttachmentInformation(profile.getId().toString(), attachmentId);
        } else {
            throw new NoSuchProfileException.ById(profileId);
        }
    }

    @RequestMapping(value = URL_PROFILE_GET_ATTACHMENT, method = RequestMethod.GET)
    public void getAttachment(@PathVariable(PATH_VAR_ID) String profileId,
                              @PathVariable(PATH_VAR_ATTACHMENT) String attachmentId,
                              HttpServletResponse response) throws ProfileException, IOException {
        Profile profile = profileService.getProfile(profileId);
        if (profile != null) {
            InputStream input = null;
            try {
                input = profileService.getProfileAttachment(attachmentId, profile.getId().toString());
                if (input != null) {
                    ProfileAttachment attachment = profileService.getProfileAttachmentInformation(profile.getId().toString(),
                                                                                                  attachmentId);

                    response.setContentType(attachment.getContentType());
                    response.setContentLength((int)attachment.getFileSizeBytes());

                    IOUtils.copy(input, response.getOutputStream());
                }
            } catch (ProfileException ex) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                response.setContentLength(0);
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } else {
            throw new NoSuchProfileException.ById(profileId);
        }
    }

    protected Map<String, Object> deserializeAttributes(String serializedAttributes)
        throws ParamDeserializationException {
        Map<String, Object> attributes = null;

        if (StringUtils.isNotEmpty(serializedAttributes)) {
            try {
                attributes = objectMapper.readValue(serializedAttributes, ATTRIBUTES_TYPE_REFERENCE);
            } catch (IOException e) {
                throw new ParamDeserializationException(e);
            }
        }

        return attributes;
    }

}
