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

import org.apache.commons.lang.ArrayUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.impl.processors.RequestSecurityProcessorChainImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter for running security. Uses a list of {@link RequestSecurityProcessor}. The last processor should basically call
 * the filter chain.
 *
 * @author Alfonso Vásquez
 */
public class RequestSecurityFilter extends GenericFilterBean {

    protected boolean securityEnabled;
    protected List<RequestSecurityProcessor> securityProcessors;
    protected String[] urlsToInclude;
    protected String[] urlsToExclude;

    protected PathMatcher pathMatcher;

    public RequestSecurityFilter() {
        pathMatcher = new AntPathMatcher();
    }

    /**
     * Sets if security is enabled or disabled. If disabled, the security processor chain is not run.
     */
    @Required
    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    /**
     * Sets the chain of {@link RequestSecurityProcessor}.
     */
    @Required
    public void setSecurityProcessors(List<RequestSecurityProcessor> securityProcessors) {
        this.securityProcessors = securityProcessors;
    }

    /**
     * Sets the regular expressions used to match the URLs of requests that should be processed by the security chain.
     */
    public void setUrlsToInclude(String[] urlsToInclude) {
        this.urlsToInclude = urlsToInclude;
    }

    /**
     * Sets the regular expressions used to match the URLs of requests that should NOT be processed by the security chain.
     */
    public void setUrlsToExclude(String[] urlsToExclude) {
        this.urlsToExclude = urlsToExclude;
    }

    /**
     * If {@code securityEnabled}, passes the request through the chain of {@link RequestSecurityProcessor}s, depending if the request URL
     * matches or not the {@code urlsToInclude} or the {@code urlsToExclude}. The last processor of the chain calls the actual filter
     * chain.
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (securityEnabled && (includeRequest(httpRequest) || !excludeRequest(httpRequest))) {
            doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Passes the request through the chain of {@link RequestSecurityProcessor}s.
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        RequestContext context = RequestContext.getCurrent();
        if (context == null) {
            context = createRequestContext(request, response);
        }

        List<RequestSecurityProcessor> finalSecurityProcessors = new ArrayList<RequestSecurityProcessor>(securityProcessors);
        finalSecurityProcessors.add(getLastProcessorInChain(chain));

        RequestSecurityProcessorChain processorChain = new RequestSecurityProcessorChainImpl(finalSecurityProcessors.iterator());
        try {
            processorChain.processRequest(context);
        } catch (IOException e) {
            throw e;
        } catch (ServletException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * Returns trues if the request should be excluded from processing.
     */
    protected boolean excludeRequest(HttpServletRequest request) {
        if (ArrayUtils.isNotEmpty(urlsToExclude)) {
            for (String pathPattern : urlsToExclude) {
                if (pathMatcher.match(pathPattern, request.getRequestURI())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns trues if the request should be included for processing.
     */
    protected boolean includeRequest(HttpServletRequest request) {
        if (ArrayUtils.isNotEmpty(urlsToInclude)) {
            for (String pathPattern : urlsToInclude) {
                if (pathMatcher.match(pathPattern, request.getRequestURI())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns a new {@link RequestContext}, using the specified {@link HttpServletRequest} and {@link HttpServletResponse}.
     */
    protected RequestContext createRequestContext(HttpServletRequest request, HttpServletResponse response) {
        RequestContext context = new RequestContext();
        context.setRequest(request);
        context.setResponse(response);

        return context;
    }

    /**
     * Returns the last processor of the chain, which should actually call the {@link FilterChain}.
     */
    protected RequestSecurityProcessor getLastProcessorInChain(final FilterChain chain) {
        return new RequestSecurityProcessor() {

            public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
                chain.doFilter(context.getRequest(), context.getResponse());
            }

        };
    }

}
