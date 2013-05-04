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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.security.authentication.AuthenticationTokenCache;
import org.craftercms.security.utils.request.SaveOrDeleteAuthenticationCookieResponse;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Default {@link AuthenticationTokenCache} implementation, using EhCache as the underlying cache, and a cookie to persist the
 * basic authentication information across requests.
 *
 * @author Alfonso Vásquez
 */
public class AuthenticationTokenCacheImpl implements AuthenticationTokenCache {

    public static final String CACHE_NAME = "CrafterSecurityAuthCache";

    protected int maxElementsInCache;
    protected int profileTimeToOutdated;
    protected int cookieMaxAge;
    protected AuthenticationCookieFactory cookieFactory;

    protected CacheManager cacheManager;
    protected Cache cache;

    @Required
    public void setMaxElementsInCache(int maxElementsInCache) {
        this.maxElementsInCache = maxElementsInCache;
    }

    @Required
    public void setProfileTimeToOutdated(int profileTimeToOutdated) {
        this.profileTimeToOutdated = profileTimeToOutdated;
    }

    @Required
    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    @Required
    public void setCookieFactory(AuthenticationCookieFactory cookieFactory) {
        this.cookieFactory = cookieFactory;
    }

    @PostConstruct
    public void init() {
        cacheManager = CacheManager.create();
        cache = new Cache(new CacheConfiguration(CACHE_NAME, maxElementsInCache));

        cacheManager.addCache(cache);
    }

    @PreDestroy
    public void destroy() {
        cacheManager.shutdown();
    }

    public AuthenticationToken getToken(RequestContext context) {
        AuthenticationCookie cookie = cookieFactory.getCookie(context);
        if (cookie != null) {
            AuthenticationToken token = getToken(cookie.getTicket());

            // If token is not null and profile isn't outdated, update the profile-outdated-after date and save the cookie.
            if (token != null && cookie.getProfileOutdatedAfter().after(new Date())) {
                cookie.setProfileOutdatedAfter(createProfileOutdatedAfterDate());

                registerCookieForSaving(context, cookie);
            } else {
                // If profile is outdated, set profile outdated to true in token and remove token from the cache.
                if (token != null) {
                    token.setProfileOutdated(true);

                    removeToken(token);
                // If token is null, and we have a cookie, delete the cookie from the response in case it's not longer necessary, and
                // return a token with just the ticket.
                } else {
                    token = new AuthenticationToken();
                    token.setTicket(cookie.getTicket());

                    registerCookieForDeleting(context, cookie);
                }
            }

            return token;
        } else {
            return null;
        }
    }

    public void saveToken(RequestContext context, AuthenticationToken authToken) {
        saveToken(authToken);

        AuthenticationCookie cookie = cookieFactory.getCookie(authToken.getTicket(), createProfileOutdatedAfterDate());
        registerCookieForSaving(context, cookie);
    }

    public void removeToken(RequestContext context, AuthenticationToken authToken) {
        removeToken(authToken);

        AuthenticationCookie cookie = cookieFactory.getCookie(authToken.getTicket(), new Date());
        registerCookieForDeleting(context, cookie);
    }

    protected Date createProfileOutdatedAfterDate() {
        long timeToOutdatedMillis = profileTimeToOutdated * 1000;
        long now = System.currentTimeMillis();

        return new Date(now + timeToOutdatedMillis);
    }

    protected AuthenticationToken getToken(String ticket) {
        Element element = cache.get(ticket);
        if (element != null) {
            return (AuthenticationToken) element.getObjectValue();
        } else {
            return null;
        }
    }

    protected void saveToken(AuthenticationToken token) {
        cache.put(new Element(token.getTicket(), token));
    }

    protected void removeToken(AuthenticationToken token) {
        cache.remove(token.getTicket());
    }

    protected void registerCookieForSaving(RequestContext context, AuthenticationCookie cookie) {
        // Save the cookie only on error, redirect, or before writing response to client.
        SaveOrDeleteAuthenticationCookieResponse response;
        if (context.getResponse() instanceof SaveOrDeleteAuthenticationCookieResponse) {
            response = (SaveOrDeleteAuthenticationCookieResponse) context.getResponse();
            response.setCookie(cookie);
            response.setSave(true);
        } else {
            response = new SaveOrDeleteAuthenticationCookieResponse(context, cookie, cookieMaxAge, true);
        }

        context.setResponse(response);
    }

    protected void registerCookieForDeleting(RequestContext context, AuthenticationCookie cookie) {
        // Delete the cookie only on error, redirect, or before writing response to client.
        SaveOrDeleteAuthenticationCookieResponse response;
        if (context.getResponse() instanceof SaveOrDeleteAuthenticationCookieResponse) {
            response = (SaveOrDeleteAuthenticationCookieResponse) context.getResponse();
            response.setCookie(cookie);
            response.setSave(false);
        } else {
            response = new SaveOrDeleteAuthenticationCookieResponse(context, cookie, cookieMaxAge, false);
        }

        context.setResponse(response);
    }

}
