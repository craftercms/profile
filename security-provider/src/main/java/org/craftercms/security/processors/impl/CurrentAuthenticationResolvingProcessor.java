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
package org.craftercms.security.processors.impl;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Obtains and sets the authentication for the current request.
 *
 * @author Alfonso Vásquez
 */
public class CurrentAuthenticationResolvingProcessor implements RequestSecurityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CurrentAuthenticationResolvingProcessor.class);

    protected AuthenticationManager authenticationManager;

    @Required
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Sets the authentication for the current request. If the {@code profileLastModified} timestamp is in the
     * request, and it doesn't match the one from the current profile, a reload of the profile is forced.
     *
     * @param context        the context which holds the current request and other security info pertinent to the
     *                       request
     * @param processorChain the processor chain, used to call the next processor
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();
        Authentication auth = null;

        // Make sure not to run the logic if there's already an authentication
        if (SecurityUtils.getAuthentication(request) == null) {
            String ticket = SecurityUtils.getTicketCookie(request);
            if (ticket != null) {
                auth = authenticationManager.getAuthentication(ticket, false);
                if (auth != null) {
                    // Check to see if profile was updated by another app
                    Long profileLastModified = SecurityUtils.getProfileLastModifiedCookie(request);
                    long currentProfileLastModified = auth.getProfile().getLastModified().getTime();

                    if (profileLastModified == null || currentProfileLastModified != profileLastModified) {
                        if (profileLastModified == null) {
                            logger.debug("Not profile last modified timestamp specified in request");
                        } else {
                            logger.debug("The last modified timestamp in request doesn't match the current one");
                        }

                        auth = authenticationManager.getAuthentication(ticket, true);
                    }
                }
            }
        }

        if (auth != null) {
            SecurityUtils.setAuthentication(request, auth);
        }

        processorChain.processRequest(context);
    }

}
