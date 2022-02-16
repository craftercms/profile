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
import org.craftercms.security.authentication.LoginFailureHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.RedirectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link org.craftercms.security.authentication.LoginFailureHandler}, which redirects the
 * response to a target URL or 401 is sent if there's no target URL.
 *
 * @author Alfonso Vásquez
 */
public class LoginFailureHandlerImpl implements LoginFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailureHandlerImpl.class);

    protected String targetUrl;

    public LoginFailureHandlerImpl() {
        super();
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    protected String getTargetUrl() {
        return targetUrl;
    }

    /**
     * Redirects the response to target URL if target URL is not empty. If not, a 401 UNAUTHORIZED error is sent.
     *
     * @param context the request context
     * @param e       the exception that caused the login to fail.
     */
    @Override
    public void handle(RequestContext context, AuthenticationException e) throws SecurityProviderException,
            IOException {
        String targetUrl = getTargetUrl();
        if (StringUtils.isNotEmpty(targetUrl)) {
            RedirectUtils.redirect(context.getRequest(), context.getResponse(), targetUrl);
        } else {
            sendError(e, context);
        }
    }

    protected void sendError(AuthenticationException e, RequestContext context) throws IOException {
        logger.debug("Sending 401 UNAUTHORIZED error");

        context.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

}
