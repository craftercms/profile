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
package org.craftercms.profile.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class UserRestUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException,
        ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }

        getRememberMeServices().loginSuccess(request, response, authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }

        getSuccessHandler().onAuthenticationSuccess(request, response, authResult);

        // Continue processing through the filter chain, this is needed for ReqeustMapping annotations to be processed.
        chain.doFilter(request, response);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

        if ("".equals(request.getContextPath())) {
            return (existingAuth != null) && (uri.indexOf(getFilterProcessesUrl()) >= 0);
        }

        return (existingAuth != null) && (uri.indexOf(request.getContextPath() + getFilterProcessesUrl()) >= 0);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
        AuthenticationException {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String tenantName = request.getParameter("tenantName");
        boolean isSSO = Boolean.valueOf(request.getParameter("sso"));

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }
        if (tenantName != null && !tenantName.equals("")) {
            username = username.trim() + "@" + tenantName;
        } else {
            username = username.trim();
        }

        UsernamePasswordAuthenticationToken authRequest = null;

        if (isSSO) {
            // by calling this constructor, the isAuthenticated flag is set to true
            // in the SSODaoAuthenticationProvider, we can look at this to tell is authentication
            // needs to be carried out or not
            authRequest = new UsernamePasswordAuthenticationToken(username, password, null);
        } else {
            authRequest = new UsernamePasswordAuthenticationToken(username, password);
        }

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
