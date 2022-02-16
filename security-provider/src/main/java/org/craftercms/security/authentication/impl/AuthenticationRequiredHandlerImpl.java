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
package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.AuthenticationRequiredHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.RedirectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * Default implementation of {@link AuthenticationRequiredHandler}:
 * <p/>
 * <ol>
 * <li>Saves the current request so it can be reused after successful login.</li>
 * <li>Redirects to the login form URL.</li>
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
        super();
        requestCache = new HttpSessionRequestCache();
    }

    /**
     * Sets the URL of the login form page.
     */
    public void setLoginFormUrl(String loginFormUrl) {
        this.loginFormUrl = loginFormUrl;
    }

    protected String getLoginFormUrl() {
        return loginFormUrl;
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
     * @param context the request security context
     * @param e       the exception with the reason for requiring authentication
     */
    public void handle(RequestContext context, AuthenticationException e) throws SecurityProviderException,
            IOException {
        saveRequest(context);

        String loginFormUrl = getLoginFormUrl();

        if (StringUtils.isNotEmpty(loginFormUrl)) {
            RedirectUtils.redirect(context.getRequest(), context.getResponse(), loginFormUrl);
        } else {
            sendError(e, context);
        }
    }

    protected void saveRequest(RequestContext context) {
        logger.debug("Saving current request for use after login");

        requestCache.saveRequest(context.getRequest(), context.getResponse());
    }

    protected void sendError(AuthenticationException e, RequestContext context) throws IOException {
        logger.debug("Sending 401 UNAUTHORIZED error");

        context.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

}
