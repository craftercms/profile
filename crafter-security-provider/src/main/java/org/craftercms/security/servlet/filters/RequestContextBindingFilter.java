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
package org.craftercms.security.servlet.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.security.api.RequestContext;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Binds a new request context to the current thread before the chain is called, and then removes it after the chain
 * is called.
 *
 * @author Alfonso Vásquez
 */
public class RequestContextBindingFilter extends OncePerRequestFilter {

    /**
     * Binds a new {@link RequestContext} to the current thread, and after the the filter chain has finished
     * executing, removes it
     * from the current thread.
     *
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
    	RequestContext context = createRequestContext(request, response);

        if (logger.isDebugEnabled()) {
            logger.debug("Binding request context for request '" + request.getRequestURI() + "' to current thread");
        }

        RequestContext.setCurrent(context);

        try {
            chain.doFilter(request, response);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("Removing request context for request '" + request.getRequestURI() + "' from current " +
                    "thread");
            }

            RequestContext.clear();
        }
    }

    /**
     * Returns a new {@link RequestContext}, using the specified {@link HttpServletRequest} and {@link
     * HttpServletResponse}.
     */
    protected RequestContext createRequestContext(HttpServletRequest request, HttpServletResponse response) {
        RequestContext context = new RequestContext();
        context.setRequest(request);
        context.setResponse(response);

        return context;
    }

}
