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
package org.craftercms.profile.api;

/**
 * Constants for REST services.
 *
 * @author avasquez
 */
public class RestConstants {

    public static final String BASE_URL_REST_API =          "/api/3";
    public static final String BASE_URL_TENANT =            BASE_URL_REST_API + "/tenant";
    public static final String BASE_URL_PROFILE =           BASE_URL_REST_API + "/profile";
    public static final String BASE_URL_AUTHENTICATION =    BASE_URL_REST_API + "/authentication";

    public static final String URL_TENANT_CREATE =                          "/create";
    public static final String URL_TENANT_GET =                             "";
    public static final String URL_TENANT_UPDATE =                          "/update";
    public static final String URL_TENANT_DELETE =                          "/delete";
    public static final String URL_TENANT_COUNT =                           "/count";
    public static final String URL_TENANT_GET_ALL =                         "/all";
    public static final String URL_TENANT_VERIFY_NEW_PROFILES =             "/verify_new_profiles";
    public static final String URL_TENANT_ADD_ROLES =                       "/add_roles";
    public static final String URL_TENANT_REMOVE_ROLES =                    "/remove_roles";
    public static final String URL_TENANT_ADD_ATTRIBUTE_DEFINITIONS =       "/add_attribute_definitions";
    public static final String URL_TENANT_REMOVE_ATTRIBUTE_DEFINITIONS =    "/remove_attribute_definitions";

    public static final String URL_PROFILE_CREATE =             "/create";
    public static final String URL_PROFILE_UPDATE =             "/update";
    public static final String URL_PROFILE_VERIFY =             "/verify";
    public static final String URL_PROFILE_ENABLE =             "/enable";
    public static final String URL_PROFILE_DISABLE =            "/disable";
    public static final String URL_PROFILE_ADD_ROLES =          "/add_roles";
    public static final String URL_PROFILE_REMOVE_ROLES =       "/remove_roles";
    public static final String URL_PROFILE_GET_ATTRIBUTES =     "/attributes";
    public static final String URL_PROFILE_UPDATE_ATTRIBUTES =  "/update_attributes";
    public static final String URL_PROFILE_REMOVE_ATTRIBUTES =  "/remove_attributes";
    public static final String URL_PROFILE_DELETE_PROFILE =     "/delete";
    public static final String URL_PROFILE_GET =                "";
    public static final String URL_PROFILE_GET_BY_USERNAME =    "/by_username";
    public static final String URL_PROFILE_GET_BY_TICKET =      "/by_ticket";
    public static final String URL_PROFILE_GET_COUNT =          "/count";
    public static final String URL_PROFILE_GET_BY_IDS =         "/by_ids";
    public static final String URL_PROFILE_GET_RANGE =          "/range";
    public static final String URL_PROFILE_GET_BY_ROLE =        "/by_role";
    public static final String URL_PROFILE_GET_BY_ATTRIBUTE =   "/by_attribute";
    public static final String URL_PROFILE_FORGOT_PASSWORD =    "/forgot_password";
    public static final String URL_PROFILE_RESET_PASSWORD =     "/reset_password";

    public static final String URL_AUTH_AUTHENTICATE =      "/authenticate";
    public static final String URL_AUTH_GET_TICKET =        "/ticket";
    public static final String URL_AUTH_INVALIDATE_TICKET = "/invalidate_ticket";

    public static final String PARAM_TENANT_NAME =              "tenantName";
    public static final String PARAM_VERIFY_NEW_PROFILES =      "verifyNewProfiles";
    public static final String PARAM_ROLE =                     "role";
    public static final String PARAM_VERIFY =                   "verify";
    public static final String PARAM_ATTRIBUTE_NAME =           "attributeName";
    public static final String PARAM_USERNAME =                 "username";
    public static final String PARAM_PASSWORD =                 "password";
    public static final String PARAM_EMAIL =                    "email";
    public static final String PARAM_ENABLED =                  "enabled";
    public static final String PARAM_PROFILE_ID =               "profileId";
    public static final String PARAM_VERIFICATION_URL =         "verificationUrl";
    public static final String PARAM_VERIFICATION_TOKEN_ID =    "verificationTokenId";
    public static final String PARAM_ATTRIBUTES_TO_RETURN =     "attributeToReturn";
    public static final String PARAM_TICKET_ID =                "ticketId";
    public static final String PARAM_SORT_BY =                  "sortBy";
    public static final String PARAM_SORT_ORDER =               "sortOrder";
    public static final String PARAM_START =                    "start";
    public static final String PARAM_COUNT =                    "count";
    public static final String PARAM_ATTRIBUTE_VALUE =          "attributeValue";
    public static final String PARAM_RESET_PASSWORD_URL =       "resetPasswordUrl";
    public static final String PARAM_RESET_TOKEN_ID =           "resetTokenId";
    public static final String PARAM_NEW_PASSWORD =             "newPassword";

    private RestConstants() {
    }

}
