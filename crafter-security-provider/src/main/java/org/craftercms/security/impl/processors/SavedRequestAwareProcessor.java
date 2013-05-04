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

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Uses {@link RequestCache} to reconstitute a previously saved request (if there's one).
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

    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        HttpServletRequest wrappedSavedRequest = requestCache.getMatchingRequest(request, response);

        if (wrappedSavedRequest != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("A previously saved request was found, and has been merged with the current request");
            }

            context.setRequest(wrappedSavedRequest);
        }

        processorChain.processRequest(context);
    }

}
