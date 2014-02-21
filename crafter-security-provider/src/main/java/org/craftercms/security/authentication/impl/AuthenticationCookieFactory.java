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
package org.craftercms.security.authentication.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.exception.InvalidCookieException;

/**
 * Factory for {@link AuthenticationCookie}s.
 *
 * @author Alfonso Vásquez
 */
public class AuthenticationCookieFactory {

    public static final int TICKET = 0;
    public static final int PROFILE_OUTDATED_AFTER = 1;

    /**
     * Creates a new cookie for the specified ticket and profile outdated after date.
     *
     * @param ticket              the ticket
     * @param profileOutdateAfter date when the profile is considered outdated and should be refreshed
     * @return the newly created profile.
     */
    public AuthenticationCookie getCookie(String ticket, Date profileOutdateAfter) {
        return createCookie(ticket, profileOutdateAfter);
    }

    /**
     * Returns the authentication cookie for the context's request.
     *
     * @param context the context that holds the request.
     * @return the authentication cookie for the context's request, or null if not found.
     * @throws InvalidCookieException if the authentication cookie found in the request is in an invalid format
     */
    public AuthenticationCookie getCookie(RequestContext context) throws InvalidCookieException {
        String cookieValue = getCookieValueFromRequest(context.getRequest());

        if (cookieValue != null) {
            String[] cookieData = StringUtils.split(cookieValue, AuthenticationCookie.COOKIE_SEP);

            checkCookieDataLength(cookieData);

            return createCookie(getTicket(cookieData), getProfileOutdatedAfterDate(cookieData));
        } else {
            return null;
        }
    }

    /**
     * Returns the authentication cookie string value from the request.
     */
    protected String getCookieValueFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AuthenticationCookie.COOKIE)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Checks if the cookie has the expected number of components. If not, an {@link InvalidCookieException} is thrown.
     */
    protected void checkCookieDataLength(String[] cookieData) throws InvalidCookieException {
        if (cookieData.length != 2) {
            throw new InvalidCookieException("Profile cookie: cookie should be composed of TICKET" +
                AuthenticationCookie.COOKIE_SEP +
                "PROFILE_OUTDATED_AFTER");
        }
    }

    /**
     * Returns the ticket component of the cookie.
     */
    protected String getTicket(String[] cookieData) {
        return cookieData[TICKET];
    }

    /**
     * Returns the profile outdated after component of the cookie.
     */
    protected Date getProfileOutdatedAfterDate(String[] cookieData) {
        try {
            return DateFormat.getDateTimeInstance().parse(cookieData[PROFILE_OUTDATED_AFTER]);
        } catch (ParseException e) {
            throw new InvalidCookieException("Profile cookie: profile-outdated-after date has an invalid format", e);
        }
    }

    /**
     * Creates a new cookie for the specified ticket and profile outdated after date.
     */
    protected AuthenticationCookie createCookie(String ticket, Date profileOutdatedAfter) {
        return new AuthenticationCookie(ticket, profileOutdatedAfter);
    }

}
