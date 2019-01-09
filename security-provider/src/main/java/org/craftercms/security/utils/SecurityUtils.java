/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains security utility methods.
 *
 * @author Alfonso Vásquez
 */
public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    public static final String AUTHENTICATION_EXCEPTION_SESSION_ATTRIBUTE = "authenticationException";
    public static final String BAD_CREDENTIALS_EXCEPTION_SESSION_ATTRIBUTE = "badCredentialsException";
    public static final String ACCESS_DENIED_EXCEPTION_SESSION_ATTRIBUTE = "accessDeniedException";

    public static final String TICKET_COOKIE_NAME = "ticket";
    public static final String PROFILE_LAST_MODIFIED_COOKIE_NAME = "profile-last-modified";
    public static final String TENANT_REQUEST_ATTRIBUTE_NAME = "tenant";
    public static final String AUTHENTICATION_REQUEST_ATTRIBUTE_NAME = "authentication";

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
     * Returns the authentication attribute from the current request.
     *
     * @return the authentication object
     */
    public static Authentication getCurrentAuthentication() {
        RequestContext context = RequestContext.getCurrent();
        if (context != null) {
            return getAuthentication(context.getRequest());
        } else {
            return null;
        }
    }

    /**
     * Sets the authentication attribute in the current request.
     *
     * @param authentication    the authentication object to set as request attribute
     */
    public static void setCurrentAuthentication(Authentication authentication) {
        RequestContext context = RequestContext.getCurrent();
        if (context != null) {
            setAuthentication(context.getRequest(), authentication);
        }
    }

    /**
     * Removes the authentication attribute from the current request.
     */
    public static void removeCurrentAuthentication() {
        RequestContext context = RequestContext.getCurrent();
        if (context != null) {
            removeAuthentication(context.getRequest());
        }
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
     * Returns the profile from authentication attribute from the current request.
     *
     * @return the profile object, or null if there's no authentication
     */
    public static Profile getCurrentProfile() {
        RequestContext context = RequestContext.getCurrent();
        if (context != null) {
            return getProfile(context.getRequest());
        } else {
            return null;
        }
    }

    /**
     * Returns the profile from authentication attribute from the specified request.
     *
     * @return the profile object, or null if there's no authentication
     */
    public static Profile getProfile(HttpServletRequest request) {
        Authentication auth = getAuthentication(request);
        if (auth != null) {
            return auth.getProfile();
        } else {
            return null;
        }
    }

}
