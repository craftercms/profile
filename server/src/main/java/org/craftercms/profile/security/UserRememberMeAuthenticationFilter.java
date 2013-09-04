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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

public class UserRememberMeAuthenticationFilter extends RememberMeAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public UserRememberMeAuthenticationFilter(AuthenticationManager authenticationManager,
                                              RememberMeServices rememberMeServices) {
        super(authenticationManager, rememberMeServices);
        this.authenticationManager = authenticationManager;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
        ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;

        Authentication rememberMeAuth = getRememberMeServices().autoLogin(request, response);

        if (rememberMeAuth != null) {
            // Attempt authenticaton via AuthenticationManager
            try {
                rememberMeAuth = authenticationManager.authenticate(rememberMeAuth);

                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    onSuccessfulAuthentication(request, response, rememberMeAuth);
                }


            } catch (AuthenticationException authenticationException) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SecurityContextHolder not populated with remember-me token, " +
                        "as " + "AuthenticationManager rejected Authentication returned by RememberMeServices: '" +
                        rememberMeAuth + "'; invalidating remember-me token", authenticationException);
                }

                getRememberMeServices().loginFail(request, response);

                onUnsuccessfulAuthentication(request, response, authenticationException);
            }
        }

        chain.doFilter(request, response);
    }


}
