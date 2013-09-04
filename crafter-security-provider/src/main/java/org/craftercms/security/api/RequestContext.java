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
package org.craftercms.security.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.security.authentication.AuthenticationToken;

/**
 * Houses security information for a single request.
 *
 * @author Alfonso Vásquez
 */
public class RequestContext {

    private static ThreadLocal<RequestContext> threadLocal = new ThreadLocal<RequestContext>();

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String tenantName;
    private AuthenticationToken authenticationToken;

    /**
     * Returns the context for the current thread.
     */
    public static RequestContext getCurrent() {
        return threadLocal.get();
    }

    /**
     * Sets the context for the current thread.
     */
    public static void setCurrent(RequestContext current) {
        threadLocal.set(current);
    }

    /**
     * Removes the context from the current thread.
     */
    public static void clear() {
        threadLocal.remove();
    }

    public String getRequestUri() {
        if (request != null) {
            return request.getRequestURI();
        } else {
            return null;
        }
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public AuthenticationToken getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(AuthenticationToken authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

}
