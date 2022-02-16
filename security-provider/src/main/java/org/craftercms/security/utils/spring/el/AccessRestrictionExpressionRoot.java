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
package org.craftercms.security.utils.spring.el;

import java.util.Collection;

import org.craftercms.profile.api.Profile;

/**
 * Instances of this class are used as the root object for Spring EL {@code Expression}s that are used to evaluate
 * access restrictions.
 *
 * @author Alfonso Vásquez
 */
public class AccessRestrictionExpressionRoot {

    private Profile profile;

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * Returns trues if user is anonymous.
     */
    public boolean isAnonymous() {
        return profile == null;
    }

    /**
     * Returns trues if user is authenticated.
     */
    public boolean isAuthenticated() {
        return profile != null;
    }

    /**
     * Returns trues if the profile has the specified role.
     */
    public boolean hasRole(String role) {
        return isAuthenticated() && profile.hasRole(role);
    }

    /**
     * Returns trues if the profile has any of the specified roles.
     */
    public boolean hasAnyRole(Collection<String> roles) {
        return isAuthenticated() && profile.hasAnyRole(roles);
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
