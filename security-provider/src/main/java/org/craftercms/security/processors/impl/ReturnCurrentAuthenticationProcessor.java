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
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.commons.rest.HttpMessageConvertingResponseWriter;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

/**
 * {@link org.craftercms.security.processors.RequestSecurityProcessor} that returns the current authentication to the
 * client.
 *
 * @author avasquez
 */
public class ReturnCurrentAuthenticationProcessor implements RequestSecurityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ReturnCurrentAuthenticationProcessor.class);

    public static final String DEFAULT_SERVICE_URL = "/crafter-security-current-auth";
    public static final String DEFAULT_SERVICE_METHOD = "GET";

    private String serviceUrl;
    private String serviceMethod;
    private HttpMessageConvertingResponseWriter responseWriter;

    public ReturnCurrentAuthenticationProcessor() {
        serviceUrl = DEFAULT_SERVICE_URL;
        serviceMethod = DEFAULT_SERVICE_METHOD;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public void setResponseWriter(HttpMessageConvertingResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    @Override
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isServiceRequest(request)) {
            sendAuthentication(SecurityUtils.getAuthentication(request), context);
        } else {
            processorChain.processRequest(context);
        }
    }

    protected boolean isServiceRequest(HttpServletRequest request) {
        return HttpUtils.getRequestUriWithoutContextPath(request).equals(serviceUrl) && request.getMethod().equals(
            serviceMethod);
    }

    protected <T> void sendAuthentication(Authentication auth, RequestContext context) throws IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        if (auth != null) {
            response.setStatus(HttpServletResponse.SC_OK);

            try {
                responseWriter.writeWithMessageConverters(auth, request, response);
            } catch (HttpMediaTypeNotAcceptableException e) {
                logger.error(e.getMessage(), e);

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

}
