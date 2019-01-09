/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.security.utils.handlers;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.commons.rest.HttpMessageConvertingResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

/**
 * Base for login, logout and access denied REST handlers.
 *
 * @author avasquez
 */
public abstract class AbstractRestHandlerBase {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRestHandlerBase.class);

    protected HttpMessageConvertingResponseWriter responseWriter;

    @Required
    public void setResponseWriter(HttpMessageConvertingResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    protected <T> void sendObject(int status, T responseBody, RequestContext context) throws IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        response.setStatus(status);

        try {
            responseWriter.writeWithMessageConverters(responseBody, request, response);
        } catch (HttpMediaTypeNotAcceptableException e) {
            logger.error(e.getMessage(), e);

            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    protected void sendMessage(int status, String message, RequestContext context) throws IOException {
        sendObject(status, Collections.singletonMap("message", message), context);
    }

    protected void sendErrorMessage(int status, Throwable e, RequestContext context) throws IOException {
        sendMessage(status, e.getLocalizedMessage(), context);
    }

}
