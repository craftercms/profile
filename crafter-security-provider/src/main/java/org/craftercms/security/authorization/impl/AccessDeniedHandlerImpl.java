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
package org.craftercms.security.authorization.impl;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.authorization.AccessDeniedHandler;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link AccessDeniedHandler}: forwards to the error page URL,
 * so that the original URL is preserved in
 * the browser. If not error URL is specified, a 403 FORBIDDEN error is sent.
 *
 * @author Alfonso Vásquez
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    protected String errorPageUrl;

    /**
     * Sets the error page URL to forward to.
     */
    public void setErrorPageUrl(String errorPageUrl) {
        this.errorPageUrl = errorPageUrl;
    }

    /**
     * Saves the access denied in the request, so it can be used after the request is forwarded. It then forwards to
     * the error page,
     * but if not error page was specified, a 403 error is sent.
     *
     * @param e       the exception with the reason of the access deny
     * @param context the request context
     * @throws CrafterSecurityException
     * @throws IOException
     */
    public void onAccessDenied(AccessDeniedException e, RequestContext context) throws CrafterSecurityException,
        IOException {
        saveException(e, context);

        if (StringUtils.isNotEmpty(errorPageUrl)) {
            forwardToErrorPage(context);
        } else {
            sendError(e, context);
        }
    }

    /**
     * Saves the exception in the request.
     */
    protected void saveException(AccessDeniedException e, RequestContext context) {
        if (logger.isDebugEnabled()) {
            logger.debug("Saving access denied exception in request to use after forward");
        }

        context.getRequest().setAttribute(SecurityConstants.ACCESS_DENIED_EXCEPTION_ATTRIBUTE, e);
    }

    /**
     * Forwards the request to the error page (to preserve the browser URL).
     */
    protected void forwardToErrorPage(RequestContext context) throws CrafterSecurityException, IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        if (logger.isDebugEnabled()) {
            logger.debug("Forwarding to error page at " + errorPageUrl + ", with 403 FORBIDDEN status");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(errorPageUrl);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            throw new CrafterSecurityException(e.getMessage(), e);
        }
    }

    /**
     * Sends a 403 FORBIDDEN error.
     */
    protected void sendError(AccessDeniedException e, RequestContext requestContext) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Sending 403 FORBIDDEN error");
        }

        requestContext.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }

}
