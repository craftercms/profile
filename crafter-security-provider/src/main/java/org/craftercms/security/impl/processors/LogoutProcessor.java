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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.security.authentication.AuthenticationTokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Processes logout requests.
 *
 * @author Alfonso Vásquez
 */
public class LogoutProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(LogoutProcessor.class);

    public static final String DEFAULT_LOGOUT_URL = "/crafter-security-logout";
    public static final String DEFAULT_LOGOUT_METHOD = "GET";

    protected String logoutUrl;
    protected String logoutMethod;
    protected String targetUrl;

    protected AuthenticationTokenCache authenticationTokenCache;
    protected AuthenticationService authenticationService;

    /**
     * Default constructor.
     */
    public LogoutProcessor() {
        logoutUrl = DEFAULT_LOGOUT_URL;
        logoutMethod = DEFAULT_LOGOUT_METHOD;
    }

    /**
     * Sets the logout URL this processor should respond to.
     */
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    /**
     * Sets the HTTP method for the logout request this processor should respond to.
     */
    public void setLogoutMethod(String logoutMethod) {
        this.logoutMethod = logoutMethod;
    }

    /**
     * Sets the target URL, to redirect to after logout.
     */
    @Required
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Sets the {@link AuthenticationTokenCache}.
     */
    @Required
    public void setAuthenticationTokenCache(AuthenticationTokenCache authenticationTokenCache) {
        this.authenticationTokenCache = authenticationTokenCache;
    }

    /**
     * Sets the {@link AuthenticationService}.
     */
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Checks if the request URL matches the {@code logoutUrl} and the HTTP method matches the {@code logoutMethod}.
     * If it does, it
     * proceeds to logout the user, by removing the user's token from the {@link AuthenticationToken} and
     * invalidating the user ticket
     * from the {@link AuthenticationService}. After this, the user is redirected to the {@code targetUrl}.
     *
     * @param context        the context which holds the current request and other security info pertinent to the
     *                       request
     * @param processorChain the processor chain, used to call the next processor
     * @throws Exception
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        if (isLogoutRequest(context.getRequest())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing logout request");
            }

            if (context.getAuthenticationToken() == null) {
                throw new IllegalArgumentException("Request context doesn't contain an authentication token");
            }
            if (context.getAuthenticationToken().getProfile() == null) {
                throw new IllegalArgumentException("Authentication token of request context doesn't contain a user " +
                    "profile");
            }

            if (context.getAuthenticationToken().getProfile().isAuthenticated()) {
                AuthenticationToken token = context.getAuthenticationToken();
                UserProfile profile = token.getProfile();

                if (logger.isDebugEnabled()) {
                    logger.debug("Removing profile from cache for user " + profile.getUserName());
                    logger.debug("Invalidating authentication ticket '" + token.getTicket() + "' for user '" +
                        profile.getUserName() + "'");
                }

                authenticationTokenCache.removeToken(context, token);
                authenticationService.invalidateTicket(token.getTicket());

                logger.info("Logout for user '" + profile.getUserName() + "' successful");
            }

            redirectToTargetUrl(context);
        } else {
            processorChain.processRequest(context);
        }
    }

    /**
     * Returns true if the request URL matches the {@code logoutUrl} and the HTTP method matches the {@code
     * logoutMethod}.
     */
    protected boolean isLogoutRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + logoutUrl) && request.getMethod().equals
            (logoutMethod);
    }

    /**
     * Redirects the user to the {@code targetUrl}.
     */
    protected void redirectToTargetUrl(RequestContext context) throws IOException {
        context.getResponse().sendRedirect(context.getRequest().getContextPath() + targetUrl);
    }

}
