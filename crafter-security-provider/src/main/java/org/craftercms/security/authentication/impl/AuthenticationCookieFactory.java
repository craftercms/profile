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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.exception.InvalidCookieException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Factory for {@link AuthenticationCookie}s.
 *
 * @author Alfonso Vásquez
 */
public class AuthenticationCookieFactory {

    public static final int TICKET =                    0;
    public static final int PROFILE_OUTDATED_AFTER =    1;

    public AuthenticationCookie getCookie(String ticket, Date expirationDate) {
        return createCookie(ticket, expirationDate);
    }

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

    protected void checkCookieDataLength(String[] cookieData) throws InvalidCookieException {
        if (cookieData.length != 2) {
            throw new InvalidCookieException("Profile cookie: cookie should be composed of TICKET" + AuthenticationCookie.COOKIE_SEP +
                    "PROFILE_OUTDATED_AFTER");
        }
    }

    protected String getTicket(String[] cookieData) {
        return cookieData[TICKET];
    }

    protected Date getProfileOutdatedAfterDate(String[] cookieData) {
        try {
            return DateFormat.getDateTimeInstance().parse(cookieData[PROFILE_OUTDATED_AFTER]);
        } catch (ParseException e) {
            throw new InvalidCookieException("Profile cookie: profile-outdated-after date has an invalid format", e);
        }
    }

    protected AuthenticationCookie createCookie(String ticket, Date profileOutdatedAfter) {
        return new AuthenticationCookie(ticket, profileOutdatedAfter);
    }

}
