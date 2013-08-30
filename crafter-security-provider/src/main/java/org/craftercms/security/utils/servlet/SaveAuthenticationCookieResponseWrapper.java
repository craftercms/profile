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
package org.craftercms.security.utils.servlet;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.craftercms.security.authentication.impl.AuthenticationCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response wrapper that saves the authentication cookie until the response is about to be committed. This is done to
 * avoid adding
 * the cookie several times to the response.
 *
 * @author Alfonso Vásquez
 */
public class SaveAuthenticationCookieResponseWrapper extends HttpServletResponseWrapper {

    public static final Logger logger = LoggerFactory.getLogger(SaveAuthenticationCookieResponseWrapper.class);

    protected boolean cookieAdded;
    protected Cookie authenticationCookie;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @throws IllegalArgumentException if the response is null
     */
    public SaveAuthenticationCookieResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (cookie.getName().equals(AuthenticationCookie.COOKIE)) {
            authenticationCookie = cookie;
        } else {
            super.addCookie(cookie);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        saveAuthenticationCookie();

        return super.getOutputStream();
    }

    @Override
    public ServletResponse getResponse() {
        saveAuthenticationCookie();

        return super.getResponse();
    }

    @Override
    public void sendError(int sc) throws IOException {
        saveAuthenticationCookie();

        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        saveAuthenticationCookie();

        super.sendError(sc, msg);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        saveAuthenticationCookie();

        super.sendRedirect(location);
    }

    @Override
    public void flushBuffer() throws IOException {
        saveAuthenticationCookie();

        super.flushBuffer();
    }

    public void saveAuthenticationCookie() {
        if (authenticationCookie != null && !cookieAdded) {
            if (logger.isDebugEnabled()) {
                logger.debug("Saving authentication cookie: " + cookieAsString(authenticationCookie));
            }

            super.addCookie(authenticationCookie);

            cookieAdded = true;
        }
    }

    protected String cookieAsString(Cookie cookie) {
        return "[domain='" + cookie.getDomain() + "'" +
            ", path='" + cookie.getPath() + "'" +
            ", name='" + cookie.getName() + "'" +
            ", maxAge=" + cookie.getMaxAge() +
            ", value='" + cookie.getValue() + "']";

    }

}
