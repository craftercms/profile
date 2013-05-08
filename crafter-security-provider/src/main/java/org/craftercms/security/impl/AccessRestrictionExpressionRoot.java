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
package org.craftercms.security.impl;

import org.craftercms.security.api.UserProfile;

import java.util.List;

/**
 * Instances of this class are used as the root object for Spring EL {@link org.springframework.expression.Expression}s that are
 * used to evaluate access restrictions.
 *
 * @author Alfonso Vásquez
 */
public class AccessRestrictionExpressionRoot {

    private UserProfile profile;

    public AccessRestrictionExpressionRoot(UserProfile profile) {
        this.profile = profile;
    }

    public boolean isAnonymous() {
        return profile.isAnonymous();
    }

    public boolean isAuthenticated() {
        return profile.isAuthenticated();
    }

    public boolean hasRole(String role) {
        return profile.hasRole(role);
    }

    public boolean hasAnyRole(List<String> roles) {
        return profile.hasAnyRole(roles);
    }

    public boolean permitAll() {
        return true;
    }

    public boolean denyAll() {
        return false;
    }

}
