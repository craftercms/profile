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
package org.craftercms.profile.api;

/**
 * Common constants.
 *
 * @author avasquez
 */
public class ProfileConstants {

    /**
     * Constant used to indicate in Profile service methods that receive an array of {@code attributeToReturn},
     * that no attribute should be returned.
     */
    public static final String NO_ATTRIBUTE = "$none";

    public static final String ATTRIBUTE_TYPE_TEXT = "TEXT";
    public static final String ATTRIBUTE_TYPE_LARGE_TEXT = "LARGE_TEXT";
    public static final String ATTRIBUTE_TYPE_STRING_LIST = "STRING_LIST";

    public static final String ATTRIBUTE_TYPE_KEY = "type";

    // ********** REST Constants **********

    public static final String BASE_URL_REST_API = "/api/1";
    public static final String BASE_URL_ACCESS_TOKEN = BASE_URL_REST_API + "/access_token";
    public static final String BASE_URL_TENANT = BASE_URL_REST_API + "/tenant";
    public static final String BASE_URL_PROFILE = BASE_URL_REST_API + "/profile";
    public static final String BASE_URL_AUTHENTICATION = BASE_URL_REST_API + "/authentication";

    public static final String PATH_VAR_NAME = "name";
    public static final String PATH_VAR_ID = "id";
    public static final String PATH_VAR_ATTACHMENT = "attachmentId";

    public static final String URL_ACCESS_TOKEN_CREATE = "/create";
    public static final String URL_ACCESS_TOKEN_GET = "/{id}";
    public static final String URL_ACCESS_TOKEN_GET_ALL = "/all";
    public static final String URL_ACCESS_TOKEN_DELETE = "/{id}/delete";

    public static final String URL_TENANT_CREATE = "/create";
    public static final String URL_TENANT_GET = "/{name}";
    public static final String URL_TENANT_UPDATE = "/update";
    public static final String URL_TENANT_DELETE = "/{name}/delete";
    public static final String URL_TENANT_COUNT = "/count";
    public static final String URL_TENANT_COUNT_BY_QUERY = "/count_by_query";
    public static final String URL_TENANT_GET_ALL = "/all";
    public static final String URL_TENANT_VERIFY_NEW_PROFILES = "/{name}/verify_new_profiles";
    public static final String URL_TENANT_ADD_ROLES = "/{name}/roles/add";
    public static final String URL_TENANT_REMOVE_ROLES = "/{name}/roles/remove";
    public static final String URL_TENANT_ADD_ATTRIBUTE_DEFINITIONS = "/{name}/attribute_definitions/add";
    public static final String URL_TENANT_UPDATE_ATTRIBUTE_DEFINITIONS = "/{name}/attribute_definitions/update";
    public static final String URL_TENANT_REMOVE_ATTRIBUTE_DEFINITIONS = "/{name}/attribute_definitions/remove";

    public static final String URL_PROFILE_CREATE = "/create";
    public static final String URL_PROFILE_UPDATE = "/{id}/update";
    public static final String URL_PROFILE_VERIFY = "/verify";
    public static final String URL_PROFILE_ENABLE = "/{id}/enable";
    public static final String URL_PROFILE_DISABLE = "/{id}/disable";
    public static final String URL_PROFILE_ADD_ROLES = "/{id}/roles/add";
    public static final String URL_PROFILE_REMOVE_ROLES = "/{id}/roles/remove";
    public static final String URL_PROFILE_GET_ATTRIBUTES = "/{id}/attributes";
    public static final String URL_PROFILE_UPDATE_ATTRIBUTES = "/{id}/attributes/update";
    public static final String URL_PROFILE_REMOVE_ATTRIBUTES = "/{id}/attributes/remove";
    public static final String URL_PROFILE_DELETE_PROFILE = "/{id}/delete";
    public static final String URL_PROFILE_GET_ONE_BY_QUERY = "/one_by_query";
    public static final String URL_PROFILE_GET = "/{id}";
    public static final String URL_PROFILE_GET_BY_USERNAME = "/by_username";
    public static final String URL_PROFILE_GET_BY_TICKET = "/by_ticket";
    public static final String URL_PROFILE_GET_COUNT = "/count";
    public static final String URL_PROFILE_GET_BY_QUERY = "/by_query";
    public static final String URL_PROFILE_GET_BY_IDS = "/by_ids";
    public static final String URL_PROFILE_GET_RANGE = "/range";
    public static final String URL_PROFILE_GET_BY_ROLE = "/by_role";
    public static final String URL_PROFILE_GET_BY_EXISTING_ATTRIB = "/by_existing_attribute";
    public static final String URL_PROFILE_GET_BY_ATTRIB_VALUE = "/by_attribute_value";
    public static final String URL_PROFILE_RESET_PASSWORD = "/{id}/reset_password";
    public static final String URL_PROFILE_CHANGE_PASSWORD = "/change_password";
    public static final String URL_PROFILE_CREATE_VERIFICATION_TOKEN = "/{id}/verification_token/create";
    public static final String URL_PROFILE_GET_VERIFICATION_TOKEN = "/verification_token/{id}";
    public static final String URL_PROFILE_DELETE_VERIFICATION_TOKEN = "/verification_token/{id}/delete";
    public static final String URL_PROFILE_UPLOAD_ATTACHMENT = "/{id}/uploadAttachment";
    public static final String URL_PROFILE_GET_ATTACHMENT = "/{id}/attachment/{attachmentId}";
    public static final String URL_PROFILE_GET_ATTACHMENTS = "/{id}/attachments/";
    public static final String URL_PROFILE_GET_ATTACHMENTS_DETAILS = "/{id}/attachment/{attachmentId}/details";

    public static final String URL_AUTH_AUTHENTICATE = "/authenticate";
    public static final String URL_AUTH_CREATE_TICKET = "/ticket/create";
    public static final String URL_AUTH_GET_TICKET = "/ticket/{id}";
    public static final String URL_AUTH_INVALIDATE_TICKET = "/ticket/{id}/invalidate";
    public static final String URL_AUTH_CREATE_PERSISTENT_LOGIN = "/persistent_login/create";
    public static final String URL_AUTH_GET_PERSISTENT_LOGIN = "/persistent_login/{id}";
    public static final String URL_AUTH_REFRESH_PERSISTENT_LOGIN_TOKEN = "/persistent_login/{id}/refresh_token";
    public static final String URL_AUTH_DELETE_PERSISTENT_LOGIN = "/persistent_login/{id}/delete";

    public static final String PARAM_ACCESS_TOKEN_ID = "accessTokenId";
    public static final String PARAM_TENANT_NAME = "tenantName";
    public static final String PARAM_ROLE = "role";
    public static final String PARAM_VERIFY = "verify";
    public static final String PARAM_ATTRIBUTE_NAME = "attributeName";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_ENABLED = "enabled";
    public static final String PARAM_ATTRIBUTES = "attributes";
    public static final String PARAM_ID = "id";
    public static final String PARAM_VERIFICATION_URL = "verificationUrl";
    public static final String PARAM_VERIFICATION_TOKEN_ID = "verificationTokenId";
    public static final String PARAM_ATTRIBUTE_TO_RETURN = "attributeToReturn";
    public static final String PARAM_TICKET_ID = "ticketId";
    public static final String PARAM_SORT_BY = "sortBy";
    public static final String PARAM_SORT_ORDER = "sortOrder";
    public static final String PARAM_START = "start";
    public static final String PARAM_COUNT = "count";
    public static final String PARAM_ATTRIBUTE_VALUE = "attributeValue";
    public static final String PARAM_RESET_PASSWORD_URL = "resetPasswordUrl";
    public static final String PARAM_RESET_TOKEN_ID = "resetTokenId";
    public static final String PARAM_NEW_PASSWORD = "newPassword";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_TOKEN_ID = "tokenId";
    public static final String PARAM_PROFILE_ID = "profileId";
    public static final String PARAM_FILENAME = "filename";

    private ProfileConstants() {
    }

}
