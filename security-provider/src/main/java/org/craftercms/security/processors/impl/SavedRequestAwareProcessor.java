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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * Uses {@link RequestCache} to reconstitute a previously saved request (if there's one). This processor is used
 * primarily when a user is redirected to the login page because authentication is required, and the the user is
 * redirected back to the previous page.
 *
 * @author Alfonso Vásquez
 */
public class SavedRequestAwareProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(SavedRequestAwareProcessor.class);

    protected RequestCache requestCache;

    public SavedRequestAwareProcessor() {
        requestCache = new HttpSessionRequestCache();
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    /**
     * Checks if there's a request in the request cache (which means that a previous request was cached). If there's
     * one, the request cache creates a new request by merging the saved request with the current request. The new
     * request is used through the rest of the processor chain.
     *
     * @param context        the context which holds the current request and response
     * @param processorChain the processor chain, used to call the next processor
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest wrappedSavedRequest = requestCache.getMatchingRequest(request, response);

        if (wrappedSavedRequest != null) {
            logger.debug("A previously saved request was found, and has been merged with the current request");

            context.setRequest(wrappedSavedRequest);
        }

        processorChain.processRequest(context);
    }

}
