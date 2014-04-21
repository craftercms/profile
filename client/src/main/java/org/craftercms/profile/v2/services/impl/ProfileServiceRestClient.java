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

import org.craftercms.commons.rest.RestClientUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
public class ProfileServiceRestClient extends ProfileRestClientBase implements ProfileService {

    public static final ParameterizedTypeReference<List<Profile>> profileListTypeRef =
            new ParameterizedTypeReference<List<Profile>>() {};

    @Override
    public Profile createProfile(String tenantName, String username, String password, String email, boolean enabled,
                                 Set<String> roles, String verificationUrl) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValue(PARAM_PASSWORD, password, params);
        RestClientUtils.addValue(PARAM_EMAIL, email, params);
        RestClientUtils.addValue(PARAM_ENABLED, enabled, params);
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValue(PARAM_VERIFICATION_URL, verificationUrl, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_CREATE);

        return doPostForObject(url, params, Profile.class);
    }

    @Override
    public Profile updateProfile(String profileId, String username, String password, String email, Boolean enabled,
                                 Set<String> roles, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValue(PARAM_PASSWORD, password, params);
        RestClientUtils.addValue(PARAM_EMAIL, email, params);
        RestClientUtils.addValue(PARAM_ENABLED, enabled, params);
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_UPDATE);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile verifyProfile(String verificationTokenId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_VERIFICATION_TOKEN_ID, verificationTokenId, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_VERIFY);

        return doPostForObject(url, params, Profile.class);
    }

    @Override
    public Profile enableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_ENABLE);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile disableProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_DISABLE);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile addRoles(String profileId, Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_ADD_ROLES);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile removeRoles(String profileId, Collection<String> roles, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ROLE, roles, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_REMOVE_ROLES);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Map<String, Object> getAttributes(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_ATTRIBUTES);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, Map.class, profileId);
    }

    @Override
    public Profile updateAttributes(String profileId, Map<String, Object> attributes, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_UPDATE_ATTRIBUTES);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doPostForObject(url, attributes, Profile.class, profileId);
    }

    @Override
    public Profile removeAttributes(String profileId, Collection<String> attributeNames, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_NAME, attributeNames, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_REMOVE_ATTRIBUTES);

        return doPostForObject(url, params, Profile.class);
    }

    @Override
    public void deleteProfile(String profileId) throws ProfileException {
        String url = BASE_URL_PROFILE + URL_PROFILE_DELETE_PROFILE;

        doPostForLocation(url, createBaseParams(), profileId);
    }

    @Override
    public Profile getProfile(String profileId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, Profile.class, profileId);
    }

    @Override
    public Profile getProfileByUsername(String tenantName, String username, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_USERNAME);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, Profile.class);
    }

    @Override
    public Profile getProfileByTicket(String ticketId, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TICKET_ID, ticketId, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_TICKET);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, Profile.class);
    }

    @Override
    public long getProfileCount(String tenantName) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_COUNT);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, Long.class);
    }

    @Override
    public List<Profile> getProfilesByIds(List<String> profileIds, String sortBy, SortOrder sortOrder,
                                              String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValues(PARAM_ID, profileIds, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_IDS);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfileRange(String tenantName, String sortBy, SortOrder sortOrder, Integer start,
                                             Integer count, String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValue(PARAM_START, start, params);
        RestClientUtils.addValue(PARAM_COUNT, count, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_RANGE);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfilesByRole(String tenantName, String role, String sortBy, SortOrder sortOrder,
                                               String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_ROLE, role, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_ROLE);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfilesByExistingAttribute(String tenantName, String attributeName, String sortBy,
                                                            SortOrder sortOrder, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_ATTRIBUTE_NAME, attributeName, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_EXISTING_ATTRIB);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public List<Profile> getProfilesByAttributeValue(String tenantName, String attributeName,
                                                         String attributeValue, String sortBy, SortOrder sortOrder,
                                                         String... attributesToReturn) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_ATTRIBUTE_NAME, attributeName, params);
        RestClientUtils.addValue(PARAM_ATTRIBUTE_VALUE, attributeValue, params);
        RestClientUtils.addValue(PARAM_SORT_BY, sortBy, params);
        RestClientUtils.addValue(PARAM_SORT_ORDER, sortOrder, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_GET_BY_ATTRIB_VALUE);
        url = RestClientUtils.addQueryParams(url, params, false);

        return doGetForObject(url, profileListTypeRef);
    }

    @Override
    public Profile forgotPassword(String profileId, String resetPasswordUrl, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_RESET_PASSWORD_URL, resetPasswordUrl, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_FORGOT_PASSWORD);

        return doPostForObject(url, params, Profile.class, profileId);
    }

    @Override
    public Profile resetPassword(String resetTokenId, String newPassword, String... attributesToReturn)
            throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_RESET_TOKEN_ID, resetTokenId, params);
        RestClientUtils.addValue(PARAM_NEW_PASSWORD, newPassword, params);
        RestClientUtils.addValues(PARAM_ATTRIBUTE_TO_RETURN, attributesToReturn, params);

        String url = getAbsoluteUrl(BASE_URL_PROFILE + URL_PROFILE_RESET_PASSWORD);

        return doPostForObject(url, params, Profile.class);
    }

}
