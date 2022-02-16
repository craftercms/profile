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
package org.craftercms.profile.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileAttachment;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
public class ProfileServiceRestClient extends AbstractProfileRestClientBase implements ProfileService {

    public static final ParameterizedTypeReference<List<Profile>> profileListTypeRef =
            new ParameterizedTypeReference<List<Profile>>() {};

    public static final ParameterizedTypeReference<byte[]> byteArrayTypeRef = new ParameterizedTypeReference<byte[]>() {
    };


    public static final ParameterizedTypeReference<List<ProfileAttachment>> profileAttachmentListTypeRef = new
        ParameterizedTypeReference<List<ProfileAttachment>>() {
    };

    public static final String ERROR_KEY_ATTRIBUTES_SERIALIZATION_ERROR = "profile.client.attributes.serializationError";
    public static final String ERROR_KEY_INVALID_URI_ERROR = "profile.client.invalidUri";
    public static final String ERROR_KEY_TMP_COPY_FAILED = "profile.client.attachment.tmpCopyFailed";

    private ObjectMapper objectMapper;

    @Required
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Profile createProfile(String tenantName, String username, String password, String email, boolean enabled,
                                 Set<String> roles, Map<String, Object> attributes, String verificationUrl)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_USERNAME, username, params);
        HttpUtils.addValue(PARAM_PASSWORD, password, params);
        HttpUtils.addValue(PARAM_EMAIL, email, params);
        HttpUtils.addValue(PARAM_ENABLED, enabled, params);
        HttpUtils.addValues(PARAM_ROLE, roles, params);
        if (MapUtils.isNotEmpty(attributes)) {
            HttpUtils.addValue(PARAM_ATTRIBUTES, serializeAttributes(attributes), params);
        }
        HttpUtils.addValue(PARAM_VERIFICATION_URL, verificationUrl, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_CREATE);

        return doPostForObject(url, params, Profile.class);
    }

    @Override
    public Profile updateProfile(String profileId, String username, String password, String email, Boolean enabled,
                                 Set<String> roles, Map<String, Object> attributes, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_USERNAME, username, params);
        HttpUtils.addValue(PARAM_PASSWORD, password, params);
        HttpUtils.addValue(PARAM_EMAIL, email, params);
        HttpUtils.addValue(PARAM_ENABLED, enabled, params);

        // Send empty role to indicate that all roles should be deleted
        if (roles != null && roles.isEmpty()) {
            HttpUtils.addValue(PARAM_ROLE, "", params);
        } else {
            HttpUtils.addValues(PARAM_ROLE, roles, params);
        }

        if (MapUtils.isNotEmpty(attributes)) {
            HttpUtils.addValue(PARAM_ATTRIBUTES, serializeAttributes(attributes), params);
        }

        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_UPDATE);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile verifyProfile(String verificationTokenId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_VERIFICATION_TOKEN_ID, verificationTokenId, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_VERIFY);

        return doPostForObject(url, params, Profile.class);
    }

    @Override
    public Profile enableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_ENABLE);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile disableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_DISABLE);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile addRoles(String profileId, Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ROLE, roles, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_ADD_ROLES);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile removeRoles(String profileId, Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ROLE, roles, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_REMOVE_ROLES);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Map<String, Object> getAttributes(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_ATTRIBUTES);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, Map.class, profileId);
    }

    @Override
    public Profile updateAttributes(String profileId, Map<String, Object> attributes, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_UPDATE_ATTRIBUTES);
        url = addQueryParams(url, params, false);

        return doPostForObject(url, attributes, Profile.class, profileId);
    }

    @Override
    public Profile removeAttributes(String profileId, Collection<String> attributeNames, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ATTRIBUTE_NAME, attributeNames, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_REMOVE_ATTRIBUTES);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public void deleteProfile(String profileId) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_DELETE_PROFILE);

        doPostForLocation(url, createBaseParams(), profileId);
    }

    @Override
    public Profile getProfileByQuery(String tenantName, String query, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_QUERY, query, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_ONE_BY_QUERY);
        url = addQueryParams(url, params, true);

        try {
            return doGetForObject(new URI(url), Profile.class);
        } catch (URISyntaxException e) {
            throw new I10nProfileException(ERROR_KEY_INVALID_URI_ERROR, url);
        } catch (ProfileRestServiceException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public Profile getProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET);
        url = addQueryParams(url, params, false);

        try {
            return doGetForObject(url, Profile.class, profileId);
        } catch (ProfileRestServiceException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public Profile getProfileByUsername(String tenantName, String username, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_USERNAME, username, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_USERNAME);
        url = addQueryParams(url, params, false);

        try {
            return doGetForObject(url, Profile.class);
        } catch (ProfileRestServiceException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public Profile getProfileByTicket(String ticketId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TICKET_ID, ticketId, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_TICKET);
        url = addQueryParams(url, params, false);

        try {
            return doGetForObject(url, Profile.class);
        } catch (ProfileRestServiceException e) {
            if(e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public long getProfileCount(String tenantName) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_COUNT);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, Long.class);
    }

    @Override
    public long getProfileCountByQuery(String tenantName, String query) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_QUERY, query, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_TENANT_COUNT_BY_QUERY);
        url = addQueryParams(url, params, true);

        try {
            return doGetForObject(new URI(url), Long.class);
        } catch (URISyntaxException e) {
            throw new I10nProfileException(ERROR_KEY_INVALID_URI_ERROR, url);
        }
    }

    @Override
    public List<Profile> getProfilesByQuery(String tenantName, String query, String sortBy, SortOrder sortOrder,
                                            Integer start, Integer count, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_QUERY, query, params);
        HttpUtils.addValue(PARAM_SORT_BY, sortBy, params);
        HttpUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        HttpUtils.addValue(PARAM_START, start, params);
        HttpUtils.addValue(PARAM_COUNT, count, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_QUERY);
        url = addQueryParams(url, params, true);

        try {
            return doGetForObject(new URI(url), profileListTypeRef);
        } catch (URISyntaxException e) {
            throw new I10nProfileException(ERROR_KEY_INVALID_URI_ERROR, url);
        }
    }

    @Override
    public List<Profile> getProfilesByIds(List<String> profileIds, String sortBy, SortOrder sortOrder,
                                          String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValues(PARAM_ID, profileIds, params);
        HttpUtils.addValue(PARAM_SORT_BY, sortBy, params);
        HttpUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_IDS);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfileRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start,
                                         Integer count, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_SORT_BY, sortBy, params);
        HttpUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        HttpUtils.addValue(PARAM_START, start, params);
        HttpUtils.addValue(PARAM_COUNT, count, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_RANGE);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfilesByRole(String tenantName, String role, String sortBy, SortOrder sortOrder,
                                           String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_ROLE, role, params);
        HttpUtils.addValue(PARAM_SORT_BY, sortBy, params);
        HttpUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_ROLE);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfilesByExistingAttribute(String tenantName, String attributeName, String sortBy,
                                                        SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_ATTRIBUTE_NAME, attributeName, params);
        HttpUtils.addValue(PARAM_SORT_BY, sortBy, params);
        HttpUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_EXISTING_ATTRIB);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfilesByAttributeValue(String tenantName, String attributeName,
                                                     String attributeValue, String sortBy, SortOrder sortOrder,
                                                     String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_ATTRIBUTE_NAME, attributeName, params);
        HttpUtils.addValue(PARAM_ATTRIBUTE_VALUE, attributeValue, params);
        HttpUtils.addValue(PARAM_SORT_BY, sortBy, params);
        HttpUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_ATTRIB_VALUE);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public Profile resetPassword(String profileId, String resetPasswordUrl, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_RESET_PASSWORD_URL, resetPasswordUrl, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_RESET_PASSWORD);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile changePassword(String resetTokenId, String newPassword, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_RESET_TOKEN_ID, resetTokenId, params);
        HttpUtils.addValue(PARAM_NEW_PASSWORD, newPassword, params);
        HttpUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_CHANGE_PASSWORD);

        return doPostForObject(url, params, Profile.class);
    }

    @Override
    public VerificationToken createVerificationToken(final String profileId) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_CREATE_VERIFICATION_TOKEN);

        return doPostForObject(url, createBaseParams(), VerificationToken.class, profileId);
    }

    @Override
    public VerificationToken getVerificationToken(String tokenId) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_VERIFICATION_TOKEN);
        url = addQueryParams(url, createBaseParams(), false);

        try {
            return doGetForObject(url, VerificationToken.class, tokenId);
        } catch (ProfileRestServiceException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public void deleteVerificationToken(String tokenId) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_DELETE_VERIFICATION_TOKEN);

        doPostForLocation(url, createBaseParams(), tokenId);
    }

    @Override
    public ProfileAttachment addProfileAttachment(String profileId, String attachmentName, InputStream file) throws ProfileException {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add(PARAM_ACCESS_TOKEN_ID, accessTokenIdResolver.getAccessTokenId());
        params.add(PARAM_FILENAME, attachmentName);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_UPLOAD_ATTACHMENT);

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(profileId + "-" + attachmentName, "attachment");
            FileUtils.copyInputStreamToFile(file, tmpFile);

            params.add("attachment", new FileSystemResource(tmpFile));

            return doPostForUpload(url, params, ProfileAttachment.class, profileId);
        } catch (IOException e) {
            throw new I10nProfileException(ERROR_KEY_TMP_COPY_FAILED, e, attachmentName, profileId);
        } finally {
            try {
                file.close();

                FileUtils.forceDelete(tmpFile);
            } catch (Throwable e) {
            }
        }
    }

    @Override
    public ProfileAttachment getProfileAttachmentInformation(String profileId, String attachmentId) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_ATTACHMENTS_DETAILS);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, ProfileAttachment.class, profileId, attachmentId);
    }

    @Override
    public InputStream getProfileAttachment(final String attachmentId, final String profileId) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_ATTACHMENT);
        url = addQueryParams(url, params, false);

        return new ByteArrayInputStream(doGetForObject(url, byteArrayTypeRef, profileId, attachmentId));
    }

    @Override
    public List<ProfileAttachment> getProfileAttachments(final String profileId) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_ATTACHMENTS);
        url = addQueryParams(url, params, false);

        return doGetForObject(url, profileAttachmentListTypeRef, profileId);
    }

    protected String serializeAttributes(Map<String, Object> attributes) throws ProfileException {
        try {
            return objectMapper.writeValueAsString(attributes);
        } catch (Exception e) {
            throw new I10nProfileException(ERROR_KEY_ATTRIBUTES_SERIALIZATION_ERROR, e);
        }
    }

    @Override
    public Profile setLastFailedLogin(final String profileId, final Date lastFailedLogin,
                                      final String... attributesToReturn) throws ProfileException {
        throw new NotImplementedException("This call is not intended to be call by external clients");
    }

    @Override
    public Profile setFailedLoginAttempts(final String profileId, final int failedLoginAttempts,
                                          final String... attributesToReturn) throws ProfileException {
        throw new NotImplementedException("This call is not intended to be call by external clients");
    }

    protected String addQueryParams(String url, MultiValueMap<String, String> params, boolean encodeValues) {
        return url + HttpUtils.getQueryStringFromParams(params, encodeValues);
    }

}
