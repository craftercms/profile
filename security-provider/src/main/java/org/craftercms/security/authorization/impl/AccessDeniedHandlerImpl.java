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
package org.craftercms.security.authorization.impl;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authorization.AccessDeniedHandler;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link AccessDeniedHandler}, which forwards to the error page URL, so that the original
 * URL is preserved in the browser. If not error URL is specified, a 403 FORBIDDEN error is sent.
 *
 * @author Alfonso Vásquez
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    protected String errorPageUrl;

    public AccessDeniedHandlerImpl() {
        super();
    }

    /**
     * Sets the error page URL to forward to.
     */
    public void setErrorPageUrl(String errorPageUrl) {
        this.errorPageUrl = errorPageUrl;
    }

    protected String getErrorPageUrl() {
        return errorPageUrl;
    }

    /**
     * Forwards to the error page, but if not error page was specified, a 403 error is sent.
     *
     * @param context the request context
     * @param e       the exception with the reason of the access deny
     */
    @Override
    public void handle(RequestContext context, AccessDeniedException e) throws SecurityProviderException, IOException {
        saveException(context, e);

        if (StringUtils.isNotEmpty(getErrorPageUrl())) {
            forwardToErrorPage(context);
        } else {
            sendError(e, context);
        }
    }

    protected void saveException(RequestContext context, AccessDeniedException e) {
        logger.debug("Saving access denied exception in request to use after forward");

        context.getRequest().setAttribute(SecurityUtils.ACCESS_DENIED_EXCEPTION_SESSION_ATTRIBUTE, e);
    }

    protected void forwardToErrorPage(RequestContext context) throws SecurityProviderException, IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        String errorPageUrl = getErrorPageUrl();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        logger.debug("Forwarding to error page at {}, with 403 FORBIDDEN status", errorPageUrl);

        RequestDispatcher dispatcher = request.getRequestDispatcher(errorPageUrl);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            throw new SecurityProviderException(e.getMessage(), e);
        }
    }

    protected void sendError(AccessDeniedException e, RequestContext requestContext) throws IOException {
        logger.debug("Sending 403 FORBIDDEN error");

        requestContext.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }

}
