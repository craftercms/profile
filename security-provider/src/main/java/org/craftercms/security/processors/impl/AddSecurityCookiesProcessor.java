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

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.CookieManager;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.security.processors.RequestSecurityProcessor} implementation that creates a wrapper so that
 * the ticket and profile last update cookies are added to the response, just before it is sent to the client.
 *
 * @author avasquez
 */
public class AddSecurityCookiesProcessor implements RequestSecurityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AddSecurityCookiesProcessor.class);

    protected CookieManager ticketCookieManager;
    protected CookieManager profileLastModifiedCookieManager;

    @Required
    public void setTicketCookieManager(CookieManager ticketCookieManager) {
        this.ticketCookieManager = ticketCookieManager;
    }

    @Required
    public void setProfileLastModifiedCookieManager(CookieManager profileLastModifiedCookieManager) {
        this.profileLastModifiedCookieManager = profileLastModifiedCookieManager;
    }

    /**
     * Wraps the response in a wrapper that adds (or deletes) the security cookies before the response is sent.
     *
     * @param context        the context which holds the current request and response
     * @param processorChain the {@link RequestSecurityProcessorChain}, used to call the next processor
     */
    @Override
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        AddSecurityCookiesResponseWrapper response = wrapResponse(context);
        context.setResponse(response);

        logger.debug("Wrapped response in a {}", response.getClass().getName());

        try {
            processorChain.processRequest(context);
        } finally {
            response.addCookies();
        }
    }

    protected AddSecurityCookiesResponseWrapper wrapResponse(RequestContext context) {
        return new AddSecurityCookiesResponseWrapper(context.getRequest(), context.getResponse());
    }

    protected class AddSecurityCookiesResponseWrapper extends HttpServletResponseWrapper {

        protected HttpServletRequest request;
        protected boolean cookiesAdded;

        public AddSecurityCookiesResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
            super(response);

            this.request = request;
            this.cookiesAdded = false;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            addCookies();

            return super.getOutputStream();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            addCookies();

            return super.getWriter();
        }

        @Override
        public void sendError(int sc) throws IOException {
            addCookies();

            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            addCookies();

            super.sendError(sc, msg);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            addCookies();

            super.sendRedirect(location);
        }

        @Override
        public void flushBuffer() throws IOException {
            addCookies();

            super.flushBuffer();
        }

        public void addCookies() {
            if (!cookiesAdded) {
                Authentication auth = SecurityUtils.getAuthentication(request);
                if (auth != null) {
                    // If the user is authenticated, but there are no cookies set, it means that user just logged in
                    // and the cookies must be set. Also, if the profile last modified cookie is different from the
                    // last modified of the current profile, the cookie is updated
                    String ticket = SecurityUtils.getTicketCookie(request);
                    Long profileLastModified = SecurityUtils.getProfileLastModifiedCookie(request);
                    long currentProfileLastModified = auth.getProfile().getLastModified().getTime();

                    // If both cookie and auth tickets are diff, it means the user has logged in with a new ticket,
                    // so set the new ticket as the cookie
                    if (StringUtils.isEmpty(ticket) || !ticket.equals(auth.getTicket())) {
                        addTicketCookie(auth.getTicket());
                    }

                    if (profileLastModified == null || currentProfileLastModified != profileLastModified) {
                        addProfileLastModifiedCookie(currentProfileLastModified);
                    }
                } else {
                    // If there's no authentication, but the cookies still exist, they should be deleted because the
                    // user just logged out
                    String ticket = SecurityUtils.getTicketCookie(request);
                    Long profileLastModified = SecurityUtils.getProfileLastModifiedCookie(request);

                    if (StringUtils.isNotEmpty(ticket)) {
                        deleteTicketCookie();
                    }
                    if (profileLastModified != null) {
                        deleteProfileLastModifiedCookie();
                    }
                }

                cookiesAdded = true;
            }
        }

        protected void addTicketCookie(String ticket) {
            ticketCookieManager.addCookie(SecurityUtils.TICKET_COOKIE_NAME, ticket, this);
        }

        protected void addProfileLastModifiedCookie(long lastModified) {
            profileLastModifiedCookieManager.addCookie(SecurityUtils.PROFILE_LAST_MODIFIED_COOKIE_NAME,
                                                       String.valueOf(lastModified), this);
        }

        protected void deleteTicketCookie() {
            ticketCookieManager.deleteCookie(SecurityUtils.TICKET_COOKIE_NAME, this);
        }

        protected void deleteProfileLastModifiedCookie() {
            profileLastModifiedCookieManager.deleteCookie(SecurityUtils.PROFILE_LAST_MODIFIED_COOKIE_NAME, this);
        }

    }


}
