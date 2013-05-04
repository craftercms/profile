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
package org.craftercms.security.utils.request;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.authentication.impl.AuthenticationCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Response wrapper that saves or deletes an authentication cookie (depending on the save flag), before any response is written to the
 * client. The authentication cookie is saved in this way because there are cases where in the same request a cookie is saved and then
 * deleted (like in a logout request, where the cookie's date is updated but then it's deleted), or saved twice, so this avoids having
 * duplicates Set-Cookie headers in the response.
 *
 * @author Alfonso Vásquez
 */
public class SaveOrDeleteAuthenticationCookieResponse extends HttpServletResponseWrapper {

    private static final Logger logger = LoggerFactory.getLogger(SaveOrDeleteAuthenticationCookieResponse.class);

    private RequestContext context;
    private AuthenticationCookie cookie;
    private int cookieMaxAge;
    private boolean save;
    private boolean done;

    public SaveOrDeleteAuthenticationCookieResponse(RequestContext context, AuthenticationCookie cookie, int cookieMaxAge, boolean save) {
        super(context.getResponse());

        this.context = context;
        this.cookie = cookie;
        this.cookieMaxAge = cookieMaxAge;
        this.save = save;
    }

    public RequestContext getContext() {
        return context;
    }

    public void setContext(RequestContext context) {
        this.context = context;
    }

    public AuthenticationCookie getCookie() {
        return cookie;
    }

    public void setCookie(AuthenticationCookie cookie) {
        this.cookie = cookie;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    @Override
    public void sendError(int sc) throws IOException {
        saveOrDeleteCookie();

        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        saveOrDeleteCookie();

        super.sendError(sc, msg);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        saveOrDeleteCookie();

        super.sendRedirect(location);
    }

    @Override
    public void flushBuffer() throws IOException {
        saveOrDeleteCookie();

        super.flushBuffer();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        saveOrDeleteCookie();

        return super.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        saveOrDeleteCookie();

        return super.getWriter();
    }

    protected void saveOrDeleteCookie() {
        if (!done) {
            if (save) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding " + cookie + " to response");
                }

                cookie.save(context, cookieMaxAge);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Deleting " + cookie + " from response");
                }

                cookie.delete(context);
            }

            done = true;
        }
    }

}
