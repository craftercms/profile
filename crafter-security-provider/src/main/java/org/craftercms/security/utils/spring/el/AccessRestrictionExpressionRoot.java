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
package org.craftercms.security.utils.spring.el;

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

    /**
     * Returns trues if the profile is anonymous.
     */
    public boolean isAnonymous() {
        return profile.isAnonymous();
    }

    /**
     * Returns trues if the profile is authenticated.
     */
    public boolean isAuthenticated() {
        return profile.isAuthenticated();
    }

    /**
     * Returns trues if the profile has the specified role.
     */
    public boolean hasRole(String role) {
        return profile.hasRole(role);
    }

    /**
     * Returns trues if the profile has any of the specified roles.
     */
    public boolean hasAnyRole(List<String> roles) {
        return profile.hasAnyRole(roles);
    }

    /**
     * Always returns true (allow access to everyone).
     */
    public boolean permitAll() {
        return true;
    }

    /**
     * Always returns false (deny access to everyone).
     */
    public boolean denyAll() {
        return false;
    }

}
