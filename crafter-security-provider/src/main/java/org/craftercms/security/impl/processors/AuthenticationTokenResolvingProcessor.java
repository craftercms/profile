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
package org.craftercms.security.impl.processors;

import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.security.authentication.AuthenticationTokenCache;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Obtains and sets the authentication token for the current request.
 *
 * @author Alfonso Vásquez
 */
public class AuthenticationTokenResolvingProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(AuthenticationTokenResolvingProcessor.class);

    protected AuthenticationService authenticationService;
    protected AuthenticationTokenCache authenticationTokenCache;

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Required
    public void setAuthenticationTokenCache(AuthenticationTokenCache authenticationTokenCache) {
        this.authenticationTokenCache = authenticationTokenCache;
    }

    /**
     * Sets the authentication token for the current request. It first tries to retrieve the token from the cache,
     * and if a ticket was
     * found in the request but no profile, or if the token's profile iss outdated,
     * it tries to retrieve the profile from the authentication
     * service. If not even the authentication service has the profile, or if no ticket was found in the request,
     * an anonymous profile is
     * set.
     *
     * @param context        the context which holds the current request and other security info pertinent to the
     *                       request
     * @param processorChain the processor chain, used to call the next processor
     * @throws Exception
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        // Make sure not to run the logic if there's already a token in the context
        if (context.getAuthenticationToken() == null) {
            AuthenticationToken token;

            if (logger.isDebugEnabled()) {
                logger.debug("Retrieving authentication token for request '" + context.getRequestUri() + "' from " +
                    "cache");
            }

            // Get the token from cache.
            token = authenticationTokenCache.getToken(context);
            if (token != null && (token.getProfile() == null || token.isProfileOutdated())) {
                if (logger.isDebugEnabled()) {
                    if (token.getProfile() == null) {
                        logger.debug("No authentication token cached for request '" + context.getRequestUri() + "'");
                    }
                    if (token.isProfileOutdated()) {
                        logger.debug("Profile for user '" + token.getProfile().getUserName() + "' is outdated and " +
                            "needs to be refreshed");
                    }
                    logger.debug("Retrieving profile for ticket '" + token.getTicket() + "' from authentication " +
                        "service");
                }

                // If token doesn't have a profile or the profile is outdated,
                // retrieve the profile from the authentication service.
                UserProfile profile = authenticationService.getProfile(token.getTicket());
                if (profile != null) {
                    token.setProfile(profile);

                    if (logger.isDebugEnabled()) {
                        logger.debug("Caching authentication token " + token);
                    }

                    // If profile is not null, set it in token and cache the token.
                    authenticationTokenCache.saveToken(context, token);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("No profile found for ticket '" + token.getTicket() + "'");
                    }

                    // Authentication token was cached, profile was outdated and authentication service couldn't
                    // retrieve a profile
                    // for the ticket, which means the authentication as a whole expired, so remove token from cache.
                    if (token.isProfileOutdated()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Authentication expired: removing authentication token " + token + " from " +
                                "cache");
                        }

                        authenticationTokenCache.removeToken(context, token);
                    }

                    token.setTicket(null);
                    token.setProfile(SecurityUtils.getAnonymousProfile());
                }
            }

            if (token == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No ticket found in request '" + context.getRequestUri() + "'");
                }

                token = new AuthenticationToken();
                token.setProfile(SecurityUtils.getAnonymousProfile());
            }

            context.setAuthenticationToken(token);

            if (logger.isDebugEnabled()) {
                logger.debug("Authentication token for current request: " + token);
            }
        }

        processorChain.processRequest(context);
    }

}
