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

import org.craftercms.security.api.*;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.security.authentication.AuthenticationTokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Processes logout requests.
 *
 * @author Alfonso Vásquez
 */
public class LogoutProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(LogoutProcessor.class);

    public static final String DEFAULT_LOGOUT_URL =     "/crafter-security-logout";
    public static final String DEFAULT_LOGOUT_METHOD =  "GET";

    protected String logoutUrl;
    protected String logoutMethod;
    protected String targetUrl;

    protected AuthenticationTokenCache authenticationTokenCache;
    protected AuthenticationService authenticationService;

    public LogoutProcessor() {
        logoutUrl = DEFAULT_LOGOUT_URL;
        logoutMethod = DEFAULT_LOGOUT_METHOD;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public void setLogoutMethod(String logoutMethod) {
        this.logoutMethod = logoutMethod;
    }

    @Required
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Required
    public void setAuthenticationTokenCache(AuthenticationTokenCache authenticationTokenCache) {
        this.authenticationTokenCache = authenticationTokenCache;
    }

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        if (isLogoutRequest(context.getRequest())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing logout request");
            }

            if (context.getAuthenticationToken() == null) {
                throw new IllegalArgumentException("Request context doesn't contain an authentication token");
            }
            if (context.getAuthenticationToken().getProfile() == null) {
                throw new IllegalArgumentException("Authentication token of request context doesn't contain a user profile");
            }

            if (context.getAuthenticationToken().getProfile().isAuthenticated()) {
                AuthenticationToken token = context.getAuthenticationToken();
                UserProfile profile = token.getProfile();

                if (logger.isDebugEnabled()) {
                    logger.debug("Removing profile from cache for user " + profile.getUserName());
                    logger.debug("Invalidating authentication ticket '" + token.getTicket() + "' for user '" + profile.getUserName() + "'");
                }

                authenticationTokenCache.removeToken(context, token);
                authenticationService.invalidateTicket(token.getTicket());

                logger.info("Logout for user '" + profile.getUserName() + "' successful");
            }

            redirectToTargetUrl(context);
        }  else {
            processorChain.processRequest(context);
        }
    }

    protected boolean isLogoutRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + logoutUrl) && request.getMethod().equals(logoutMethod);
    }

    protected void redirectToTargetUrl(RequestContext context) throws IOException {
        context.getResponse().sendRedirect(context.getRequest().getContextPath() + targetUrl);
    }

}
