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
package org.craftercms.security.authentication.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationCache;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@link org.craftercms.security.authentication.AuthenticationCache} that uses an EhCache.
 *
 * @author avasquez
 */
public class EhCacheAuthenticationCache implements AuthenticationCache {

    protected Cache cache;

    @Required
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public Authentication getAuthentication(String ticket) {
        Element element = cache.get(ticket);
        if (element != null) {
            return (Authentication) element.getObjectValue();
        } else {
            return null;
        }
    }

    @Override
    public void putAuthentication(Authentication authentication) {
        cache.put(new Element(authentication.getTicket(), authentication));
    }

    @Override
    public void removeAuthentication(String ticket) {
        cache.remove(ticket);
    }

}
