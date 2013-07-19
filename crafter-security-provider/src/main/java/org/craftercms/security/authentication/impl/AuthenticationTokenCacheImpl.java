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
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

    /**
     * Sets the max number of tokens the cache can hold. Any more than that, and old tokens are evicted.
     */
    @Required
    public void setMaxElementsInCache(int maxElementsInCache) {
        this.maxElementsInCache = maxElementsInCache;
    }

    /**
     * Sets the time until a profile is considered outdated (in seconds).
     */
    @Required
    public void setProfileTimeToOutdated(int profileTimeToOutdated) {
        this.profileTimeToOutdated = profileTimeToOutdated;
    }

    /**
     * Sets the max age of the authentication cookies.
     */
    @Required
    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    /**
     * Sets the {@link AuthenticationCookieFactory}.
     */
    @Required
    public void setCookieFactory(AuthenticationCookieFactory cookieFactory) {
        this.cookieFactory = cookieFactory;
    }

    /**
     * Create a new {@link CacheManager} and {@link Cache} with the {@code maxElementsInCache}.
     */
    @PostConstruct
    public void init() {
        cacheManager = CacheManager.create();
        cache = new Cache(new CacheConfiguration(CACHE_NAME, maxElementsInCache));

        cacheManager.addCache(cache);
    }

    /**
     * Shutdowns the {@link CacheManager}.
     */
    @PreDestroy
    public void destroy() {
        cacheManager.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    public AuthenticationToken getToken(RequestContext context) {
        AuthenticationCookie cookie = cookieFactory.getCookie(context);
        if (cookie != null) {
            AuthenticationToken token = getToken(cookie.getTicket());

            // If token is not null and profile isn't outdated, update the profile-outdated-after date and save the cookie.
            if (token != null && cookie.getProfileOutdatedAfter().after(new Date())) {
                cookie.setProfileOutdatedAfter(createProfileOutdatedAfterDate());
                cookie.save(context, cookieMaxAge);
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

                    cookie.delete(context);
                }
            }

            return token;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void saveToken(RequestContext context, AuthenticationToken authToken) {
        saveToken(authToken);

        AuthenticationCookie cookie = cookieFactory.getCookie(authToken.getTicket(), createProfileOutdatedAfterDate());
        cookie.save(context, cookieMaxAge);
    }

    /**
     * {@inheritDoc}
     */
    public void removeToken(RequestContext context, AuthenticationToken authToken) {
        removeToken(authToken);

        AuthenticationCookie cookie = cookieFactory.getCookie(authToken.getTicket(), new Date());
        cookie.delete(context);
    }

    /**
     * Returns a new profile outdated after date based on the current time and the time to outdate a profile.
     */
    protected Date createProfileOutdatedAfterDate() {
        long timeToOutdatedMillis = profileTimeToOutdated * 1000;
        long now = System.currentTimeMillis();

        return new Date(now + timeToOutdatedMillis);
    }

    /**
     * Gets the token associated to the specified ticket in the cache.
     */
    protected AuthenticationToken getToken(String ticket) {
        Element element = cache.get(ticket);
        if (element != null) {
            return (AuthenticationToken) element.getObjectValue();
        } else {
            return null;
        }
    }

    /**
     * Saves the specified token in the cache, using the token's ticket as cache key.
     */
    protected void saveToken(AuthenticationToken token) {
        cache.put(new Element(token.getTicket(), token));
    }

    /**
     * Removes the specified token from the cache.
     */
    protected void removeToken(AuthenticationToken token) {
        cache.remove(token.getTicket());
    }

}
