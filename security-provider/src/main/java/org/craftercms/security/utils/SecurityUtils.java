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
package org.craftercms.security.utils;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;

/**
 * Contains security utility methods.
 *
 * @author Alfonso Vásquez
 */
public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    public static final String AUTHENTICATION_SYSTEM_EXCEPTION_SESSION_ATTRIBUTE = "authenticationSystemException";
    public static final String BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE = "badCredentialsException";
    public static final String ACCESS_DENIED_EXCEPTION_SESSION_ATTRIBUTE = "accessDeniedException";

    public static final String TICKET_COOKIE_NAME = "ticket";
    public static final String PROFILE_LAST_MODIFIED_COOKIE_NAME = "profile-last-modified";
    public static final String TENANT_REQUEST_ATTRIBUTE_NAME = "tenant";
    public static final String AUTHENTICATION_REQUEST_ATTRIBUTE_NAME = "authentication";

    public static final String FIRST_NAME_ATTRIBUTE_NAME = "firstName";
    public static final String LAST_NAME_ATTRIBUTE_NAME = "lastName";
    public static final String CONNECTIONS_ATTRIBUTE_NAME = "connections";

    public static final String PROFILE_TO_REGISTER_SESSION_ATTRIBUTE = "profileToRegister";

    private SecurityUtils() {
    }

    /**
     * Returns the ticket cookie value from the request.
     *
     * @param request the request where to retrieve the ticket from
     *
     * @return the ticket
     */
    public static String getTicketCookie(HttpServletRequest request) {
        return HttpUtils.getCookieValue(TICKET_COOKIE_NAME, request);
    }

    /**
     * Returns the last modified timestamp cookie from the request.
     *
     * @param request the request where to retrieve the last modified timestamp from
     *
     * @return the last modified timestamp of the authenticated profile
     */
    public static Long getProfileLastModifiedCookie(HttpServletRequest request) {
        String profileLastModified = HttpUtils.getCookieValue(PROFILE_LAST_MODIFIED_COOKIE_NAME, request);
        if (StringUtils.isNotEmpty(profileLastModified)) {
            try {
                return new Long(profileLastModified);
            } catch (NumberFormatException e) {
                logger.error("Invalid profile last modified cookie format: {}", profileLastModified);
            }
        }

        return null;
    }

    /**
     * Returns the tenant attribute from the specified request.
     *
     * @param request the request where to get the attribute from
     *
     * @return the tenant name
     */
    public static String getTenant(HttpServletRequest request) {
        return (String) request.getAttribute(TENANT_REQUEST_ATTRIBUTE_NAME);
    }

    /**
     * Sets the tenant attribute in the specified request.
     *
     * @param request   the request where to add the attribute to
     * @param tenant    the tenant name to set as request attribute
     */
    public static void setTenant(HttpServletRequest request, String tenant) {
        request.setAttribute(TENANT_REQUEST_ATTRIBUTE_NAME, tenant);
    }

    /**
     * Returns the authentication attribute from the current request.
     *
     * @return the authentication object
     */
    public static Authentication getCurrentAuthentication() {
        return getAuthentication(RequestContext.getCurrent().getRequest());
    }

    /**
     * Sets the authentication attribute in the current request.
     *
     * @param authentication    the authentication object to set as request attribute
     */
    public static void setCurrentAuthentication(Authentication authentication) {
        setAuthentication(RequestContext.getCurrent().getRequest(), authentication);
    }

    /**
     * Removes the authentication attribute from the current request.
     */
    public static void removeCurrentAuthentication() {
        removeAuthentication(RequestContext.getCurrent().getRequest());
    }

    /**
     * Returns the authentication attribute from the specified request.
     *
     * @param request the request where to get the attribute from
     *
     * @return the authentication object
     */
    public static Authentication getAuthentication(HttpServletRequest request) {
        return (Authentication) request.getAttribute(AUTHENTICATION_REQUEST_ATTRIBUTE_NAME);
    }

    /**
     * Sets the authentication attribute in the specified request.
     *
     * @param request           the request where to add the attribute to
     * @param authentication    the authentication object to set as request attribute
     */
    public static void setAuthentication(HttpServletRequest request, Authentication authentication) {
        request.setAttribute(AUTHENTICATION_REQUEST_ATTRIBUTE_NAME, authentication);
    }

    /**
     * Removes the authentication attribute from the specified request.
     *
     * @param request the request where to remove the attribute from
     */
    public static void removeAuthentication(HttpServletRequest request) {
        request.removeAttribute(AUTHENTICATION_REQUEST_ATTRIBUTE_NAME);
    }

    /**
     * Adds the specified {@link org.springframework.social.connect.ConnectionData} to the profile
     *
     * @param connectionData    the connection data to add
     * @param profile           the profile
     */
    public static void addConnectionData(ConnectionData connectionData, Profile profile) {
        Map<String, Map<String, Object>> connections = profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);
        if (connections == null) {
            connections = new HashMap<>();
            profile.setAttribute(CONNECTIONS_ATTRIBUTE_NAME, connections);
        }

        Map<String, Object> connectionDataMap = new HashMap<>();
        connectionDataMap.put("providerUserId", connectionData.getProviderUserId());
        connectionDataMap.put("displayName", connectionData.getDisplayName());
        connectionDataMap.put("profileUrl", connectionData.getProfileUrl());
        connectionDataMap.put("imageUrl", connectionData.getImageUrl());
        connectionDataMap.put("accessToken", connectionData.getAccessToken());
        connectionDataMap.put("secret", connectionData.getSecret());
        connectionDataMap.put("refreshToken", connectionData.getRefreshToken());
        connectionDataMap.put("expireTime", connectionData.getExpireTime());

        connections.put(connectionData.getProviderId(), connectionDataMap);
    }

    /**
     * Returns the {@link org.springframework.social.connect.ConnectionData} associated to the provider ID of
     * the specified profile
     *
     * @param providerId    the provider ID of the connection
     * @param profile       the profile that contains the connection data in its attributes
     */
    public static ConnectionData getConnectionData(String providerId, Profile profile) {
        Map<String, Map<String, Object>> connections = profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);
        if (MapUtils.isNotEmpty(connections)) {
            if (connections.containsKey(providerId)) {
                Map<String, Object> connectionData = connections.get(providerId);
                String providerUserId = (String) connectionData.get("providerUserId");
                String displayName = (String) connectionData.get("displayName");
                String profileUrl = (String) connectionData.get("profileUrl");
                String imageUrl = (String) connectionData.get("imageUrl");
                String accessToken = (String) connectionData.get("accessToken");
                String secret = (String) connectionData.get("secret");
                String refreshToken = (String) connectionData.get("refreshToken");
                Long expireTime = (Long) connectionData.get("expireTime");

                return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl,
                    accessToken, secret, refreshToken, expireTime);
            }
        }

        return null;
    }

    /**
     * Creates a new profile instance based on a provider connection. The profile is not saved to the DB, so
     * modifications can be made.
     *
     * @param connection the connection to create the profile from
     */
    public static Profile createProfileFromConnection(Connection<?> connection) {
        UserProfile providerProfile = connection.fetchUserProfile();
        Profile profile = new Profile();

        profile.setUsername(providerProfile.getUsername());
        profile.setEmail(providerProfile.getEmail());
        profile.setEnabled(true);
        profile.setTenant(getTenant(RequestContext.getCurrent().getRequest()));
        profile.setAttribute(FIRST_NAME_ATTRIBUTE_NAME, providerProfile.getFirstName());
        profile.setAttribute(LAST_NAME_ATTRIBUTE_NAME, providerProfile.getLastName());

        addConnectionData(connection.createData(), profile);

        return profile;
    }

}
