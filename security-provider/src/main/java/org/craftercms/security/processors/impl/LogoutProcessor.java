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
package org.craftercms.security.processors.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.authentication.LogoutSuccessHandler;
import org.craftercms.security.authentication.RememberMeManager;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
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
    protected AuthenticationManager authenticationManager;
    protected LogoutSuccessHandler logoutSuccessHandler;
    protected RememberMeManager rememberMeManager;

    /**
     * Default constructor.
     */
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
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Required
    public void setLogoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Required
    public void setRememberMeManager(final RememberMeManager rememberMeManager) {
        this.rememberMeManager = rememberMeManager;
    }

    /**
     * Checks if the request URL matches the {@code logoutUrl} and the HTTP method matches the {@code logoutMethod}.
     * If it does, it proceeds to logout the user, by invalidating the authentication through
     * {@link AuthenticationManager#invalidateAuthentication(Authentication)}
     *
     * @param context        the context which holds the current request and response
     * @param processorChain the processor chain, used to call the next processor
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        if (isLogoutRequest(context.getRequest())) {
            logger.debug("Processing logout request");

            Authentication auth = SecurityUtils.getAuthentication(context.getRequest());
            if (auth != null) {
                authenticationManager.invalidateAuthentication(auth);
            }

            onLogoutSuccess(context, auth);
        } else {
            processorChain.processRequest(context);
        }
    }

    protected boolean isLogoutRequest(HttpServletRequest request) {
        return HttpUtils.getRequestUriWithoutContextPath(request).equals(logoutUrl) && request.getMethod().equals(
                logoutMethod);
    }

    protected void onLogoutSuccess(RequestContext context, Authentication authentication) throws IOException {
        if (authentication != null) {
            logger.debug("Logout for user '" + authentication.getProfile().getUsername() + "' successful");

            if (authentication.isRemembered()) {
                rememberMeManager.disableRememberMe(context);
            }

            SecurityUtils.removeAuthentication(context.getRequest());
            final HttpSession session = context.getRequest().getSession();
            if (session != null) {
                try {
                    session.invalidate();
                    context.getRequest().getSession(true);//New Session after old stuff is killed
                } catch (IllegalStateException ex) {
                    // DO noting
                    logger.debug("Http Session was already invalidated");
                }
            }

        } else {
            logger.debug("No logout done: user wasn't authenticated");
        }

        logoutSuccessHandler.handle(context);
    }

}
