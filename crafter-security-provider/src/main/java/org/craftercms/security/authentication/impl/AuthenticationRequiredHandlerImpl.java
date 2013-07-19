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
package org.craftercms.security.authentication.impl;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.authentication.AuthenticationRequiredHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.io.IOException;

/**
 * Default implementation of {@link AuthenticationRequiredHandler}:
 *
 * <ol>
 *     <li>Saves the current request so it can be reused after successful login.</li>
 *     <li>Redirects to the login form URL.</li>
 * </ol>
 *
 * @author Alfonso Vásquez
 */
public class AuthenticationRequiredHandlerImpl implements AuthenticationRequiredHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationRequiredHandlerImpl.class);

    protected String loginFormUrl;
    protected RequestCache requestCache;

    /**
     * Default constructor
     */
    public AuthenticationRequiredHandlerImpl() {
        requestCache = new HttpSessionRequestCache();
    }

    /**
     * Sets the URL of the login form page.
     */
    @Required
    public void setLoginFormUrl(String loginFormUrl) {
        this.loginFormUrl = loginFormUrl;
    }

    /**
     * Sets the cache where the current request is saved.
     */
    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    /**
     * Saves the current request in the request cache and then redirects to the login form page.
     *
     * @param e
     *          the exception with the reason for requiring authentication
     * @param context
     *          the request security context
     * @throws CrafterSecurityException
     * @throws IOException
     */
    public void onAuthenticationRequired(AuthenticationException e, RequestContext context) throws CrafterSecurityException, IOException {
        saveRequest(context);
        redirectToLoginForm(context);
    }

    /**
     * Saves the specified request in the request cache.
     */
    protected void saveRequest(RequestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving current request for use after login");
        }

        requestCache.saveRequest(context.getRequest(), context.getResponse());
    }

    /**
     * Redirects the request to the login form page.
     */
    protected void redirectToLoginForm(RequestContext context) throws IOException {
        String redirectUrl = context.getRequest().getContextPath() + loginFormUrl;

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to login form at " + redirectUrl);
        }

        context.getResponse().sendRedirect(redirectUrl);
    }

}
