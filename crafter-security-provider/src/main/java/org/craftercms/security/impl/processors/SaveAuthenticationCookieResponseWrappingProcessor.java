/*
 * Copyright (C) 2007-2013 Rivet Logic Corporation.
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
import org.craftercms.security.utils.servlet.SaveAuthenticationCookieResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor that wraps the response in a {@link SaveAuthenticationCookieResponseWrapper}. This wrapper avoids adding the authentication
 * cookie multiple times in the response (which can happen depending on the request, like a logout or login request). The processor also
 * calls {@link SaveAuthenticationCookieResponseWrapper#saveAuthenticationCookie()} in a finally block in case the cookie wasn't saved
 * (this could happen if an exception wasn't handled in the processor chain, for example).
 *
 * @author Alfonso Vásquez
 */
public class SaveAuthenticationCookieResponseWrappingProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(SaveAuthenticationCookieResponseWrappingProcessor.class);

    /**
     * Wraps the current {@link javax.servlet.http.HttpServletResponse} in a {@link SaveAuthenticationCookieResponseWrappingProcessor}.
     * This processor also ensures that {@link SaveAuthenticationCookieResponseWrapper#saveAuthenticationCookie()} is called even if the
     * processor chain throws an exception.
     *
     * @param context
     *      the context which holds the current request and other security info pertinent to the request
     * @param processorChain
     *          the processor chain, used to call the next processor
     * @throws Exception
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        SaveAuthenticationCookieResponseWrapper response = wrapResponse(context);
        context.setResponse(response);

        if (logger.isDebugEnabled()) {
            logger.debug("Wrapped response in a " + response.getClass().getName());
        }

        try {
            processorChain.processRequest(context);
        } finally {
            response.saveAuthenticationCookie();
        }
    }

    /**
     * Wraps the response in a {@link SaveAuthenticationCookieResponseWrappingProcessor}.
     */
    protected SaveAuthenticationCookieResponseWrapper wrapResponse(RequestContext context) {
        return new SaveAuthenticationCookieResponseWrapper(context.getResponse());
    }

}
