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
package org.craftercms.profile.v2.services.impl;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.v2.utils.rest.RestClientUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.craftercms.profile.api.RestConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
public class ProfileServiceRestClient implements ProfileService {

    public static final String DEFAULT_EXTENSION = ".json";

    protected String extension;
    protected RestTemplate restTemplate;

    public ProfileServiceRestClient() {
        this.extension = DEFAULT_EXTENSION;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Required
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Profile createProfile(String tenantName, String username, String password, String email, boolean enabled,
                                 Set<String> roles, String verificationUrl) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValue(PARAM_PASSWORD, password, params);
        RestClientUtils.addValue(PARAM_EMAIL, email, params);
        RestClientUtils.addValue(PARAM_ENABLED, enabled, params);
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValue(PARAM_VERIFICATION_URL, verificationUrl, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_CREATE + extension;

        return restTemplate.postForObject(url, params, Profile.class);
    }

    @Override
    public Profile updateProfile(String profileId, String username, String password, String email, Boolean enabled,
                                 Set<String> roles, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValue(PARAM_PASSWORD, password, params);
        RestClientUtils.addValue(PARAM_EMAIL, email, params);
        RestClientUtils.addValue(PARAM_ENABLED, enabled, params);
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_UPDATE + extension;

        return restTemplate.postForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile verifyProfile(String verificationTokenId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_VERIFICATION_TOKEN_ID, verificationTokenId, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_VERIFY + extension;

        return restTemplate.postForObject(url, params, Profile.class);
    }

    @Override
    public Profile enableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_ENABLE + extension;

        return restTemplate.postForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile disableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_DISABLE + extension;

        return restTemplate.postForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile addRoles(String profileId, Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_ADD_ROLES + extension;

        return restTemplate.postForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile removeRoles(String profileId, Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_REMOVE_ROLES + extension;

        return restTemplate.postForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Map<String, Object> getAttributes(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_ATTRIBUTES + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Map.class, profileId);
    }

    @Override
    public Profile updateAttributes(String profileId, Map<String, Object> attributes, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_UPDATE_ATTRIBUTES + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.postForObject(url, attributes, Profile.class, profileId);
    }

    @Override
    public Profile removeAttributes(String profileId, Collection<String> attributeNames, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_NAME, attributeNames, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_REMOVE_ATTRIBUTES + extension;

        return restTemplate.postForObject(url, params, Profile.class);
    }

    @Override
    public void deleteProfile(String profileId) throws ProfileException {
        String url = BASE_URL_PROFILE + URL_PROFILE_DELETE_PROFILE;

        restTemplate.postForLocation(url, null, profileId);
    }

    @Override
    public Profile getProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Profile.class, profileId);
    }

    @Override
    public Profile getProfileByUsername(String tenantName, String username, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_BY_USERNAME + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Profile.class);
    }

    @Override
    public Profile getProfileByTicket(String ticketId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TICKET_ID, ticketId, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_BY_TICKET + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Profile.class);
    }

    @Override
    public long getProfileCount(String tenantName) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_COUNT + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Long.class);
    }

    @Override
    public Iterable<Profile> getProfilesByIds(List<String> profileIds, String sortBy, SortOrder sortOrder,
                                              String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValues(PARAM_ID, profileIds, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_BY_IDS + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Iterable.class);
    }

    @Override
    public Iterable<Profile> getProfileRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start,
                                             Integer count, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValue(PARAM_START, start, params);
        RestClientUtils.addValue(PARAM_COUNT, count, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_RANGE + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Iterable.class);
    }

    @Override
    public Iterable<Profile> getProfilesByRole(String tenantName, String role, String sortBy, SortOrder sortOrder,
                                               String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_ROLE, role, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_BY_ROLE + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Iterable.class);
    }

    @Override
    public Iterable<Profile> getProfilesByAttribute(String tenantName, String attributeName, String attributeValue,
                                                    String sortBy, SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_ATTRIBUTE_NAME, attributeName, params);
        RestClientUtils.addValue(PARAM_ATTRIBUTE_VALUE, attributeValue, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_GET_ATTRIBUTES + extension;
        url = RestClientUtils.addQueryParams(url, params, false);

        return restTemplate.getForObject(url, Iterable.class);
    }

    @Override
    public Profile forgotPassword(String profileId, String resetPasswordUrl, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_RESET_PASSWORD_URL, resetPasswordUrl, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_FORGOT_PASSWORD + extension;

        return restTemplate.postForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile resetPassword(String resetTokenId, String newPassword, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_RESET_TOKEN_ID, resetTokenId, params);
        RestClientUtils.addValue(PARAM_NEW_PASSWORD, newPassword, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = BASE_URL_PROFILE + URL_PROFILE_RESET_PASSWORD + extension;

        return restTemplate.postForObject(url, params, Profile.class);
    }

}
