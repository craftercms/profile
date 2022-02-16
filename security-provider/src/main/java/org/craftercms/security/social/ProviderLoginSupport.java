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

package org.craftercms.security.social;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.exception.AuthenticationException;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.util.MultiValueMap;

/**
 * Helper class that allows for executing logins with a social service provider like Facebook, Twitter, LinkedIn, etc.
 *
 * @author avasquez
 */
public interface ProviderLoginSupport {

    /**
     * Starts the OAuth login process. Returns a URL that the app should redirect to.
     *
     * @param tenant        the current tenant the authenticated user belongs too
     * @param providerId    the social service provider ID: facebook, twitter, linkedin
     * @param request       the current request
     *
     * @return the provider specific URL the current app should redirect too.
     */
    String start(String tenant, String providerId, HttpServletRequest request) throws AuthenticationException;

    /**
     * Starts the OAuth login process. Returns a URL that the app should redirect to.
     *
     * @param tenant                the current tenant the authenticated user belongs too
     * @param providerId            the social service provider ID: facebook, twitter, linkedin
     * @param request               the current request
     * @param additionalUrlParams   additional parameters that should be added to the redirect URL
     *
     * @return the provider specific URL the current app should redirect too.
     */
    String start(String tenant, String providerId, HttpServletRequest request,
                 MultiValueMap<String, String> additionalUrlParams) throws AuthenticationException;

    /**
     * Starts the OAuth login process. Returns a URL that the app should redirect to.
     *
     * @param tenant                the current tenant the authenticated user belongs too
     * @param providerId            the social service provider ID: facebook, twitter, linkedin
     * @param request               the current request
     * @param additionalUrlParams   additional parameters that should be added to the redirect URL
     * @param connectSupport        helper class instance for establishing the connections with the providers
     *
     *
     * @return the provider specific URL the current app should redirect too.
     */
    String start(String tenant, String providerId, HttpServletRequest request,
                 MultiValueMap<String, String> additionalUrlParams, ConnectSupport connectSupport)
        throws AuthenticationException;


    /**
     * Completes the OAuth authentication, returning the resulting {@link Authentication} object, or null if it
     * couldn't be completed.
     *
     * @param tenant        the current tenant the authenticated user belongs too
     * @param providerId    the social service provider ID: facebook, twitter, linkedin
     * @param request       the current request
     *
     * @return the authentication
     */
    Authentication complete(String tenant, String providerId,
                            HttpServletRequest request) throws AuthenticationException;

    /**
     * Completes the OAuth authentication, returning the resulting {@link Authentication} object, or null if it
     * couldn't be completed.
     *
     * @param tenant            the current tenant the authenticated user belongs too
     * @param providerId        the social service provider ID: facebook, twitter, linkedin
     * @param request           the current request
     * @param newUserRoles      roles to add to a new user
     * @param newUserAttributes attributes to add to a new user
     *
     * @return the authentication
     */
    Authentication complete(String tenant, String providerId, HttpServletRequest request, Set<String> newUserRoles,
                            Map<String, Object> newUserAttributes) throws AuthenticationException;

    /**
     * Completes the OAuth authentication, returning the resulting {@link Authentication} object, or null if it
     * couldn't be completed.
     *
     * @param tenant            the current tenant the authenticated user belongs too
     * @param providerId        the social service provider ID: facebook, twitter, linkedin
     * @param request           the current request
     * @param newUserRoles      roles to add to a new user
     * @param newUserAttributes attributes to add to a new user
     * @param connectSupport    helper class instance for establishing the connections with the providers
     *
     * @return the authentication
     */
    Authentication complete(String tenant, String providerId, HttpServletRequest request, Set<String> newUserRoles,
                            Map<String, Object> newUserAttributes, ConnectSupport connectSupport)
        throws AuthenticationException;


}

