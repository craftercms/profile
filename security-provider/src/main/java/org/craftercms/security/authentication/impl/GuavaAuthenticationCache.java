/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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

import com.google.common.cache.Cache;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationCache;

import java.beans.ConstructorProperties;

/**
 * Implementation of {@link org.craftercms.security.authentication.AuthenticationCache} that uses a Guava {@link Cache}.
 *
 * @author avasquez
 * @author joseross
 * @since 4.0.0
 */
public class GuavaAuthenticationCache implements AuthenticationCache {

    protected Cache<String, Authentication> cache;

    @ConstructorProperties({"cache"})
    public GuavaAuthenticationCache(Cache<String, Authentication> cache) {
        this.cache = cache;
    }

    @Override
    public Authentication getAuthentication(String ticket) {
        return cache.getIfPresent(ticket);
    }

    @Override
    public void putAuthentication(Authentication authentication) {
        cache.put(authentication.getTicket(), authentication);
    }

    @Override
    public void removeAuthentication(String ticket) {
        cache.invalidate(ticket);
    }

}
