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
package org.craftercms.security.api;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.craftercms.profile.impl.domain.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends {@link Profile} to hold extra utility information and some methods useful for authorization.
 *
 * @author Alfonso Vásquez
 */
public class UserProfile extends Profile {

    public UserProfile() {
    }

    /**
     * Copy constructor
     */
    public UserProfile(Profile profile) {
        super(profile.getId(),
                profile.getUserName(),
                profile.getPassword(),
                profile.getActive(),
                profile.getCreated(),
                profile.getModified(),
                profile.getAttributes(),
                profile.getRoles(),
                profile.getTenantName(),
                profile.getEmail());
        
    }

    /**
     * Returns true if user is anonymous
     */
    public boolean isAnonymous() {
        return getUserName().equalsIgnoreCase(SecurityConstants.ANONYMOUS_USERNAME);
    }

    /**
     * Returns true if the user is authenticated (not anonymous).
     */
    public boolean isAuthenticated() {
        return !isAnonymous();
    }

    /**
     * Returns true if the user has the given role.
     */
    public boolean hasRole(String role) {
        if (CollectionUtils.isEmpty(getRoles())) {
            return false;
        }

        for (String r : getRoles()) {
            if (r.equals(role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the user has at least one of the given roles.
     */
    public boolean hasAnyRole(List<String> roles) {
        if (CollectionUtils.isEmpty(getRoles())) {
            return false;
        }

        for (String r : roles) {
            if (getRoles().contains(r)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the value for the specified attribute name, or null if attribute not found.
     */
    public Object getAttribute(String name) {
        Map<String, Object> attributes = getAttributes();
        if (MapUtils.isNotEmpty(attributes)) {
            return attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Sets the value for the specified attribute name.
     */
    public void setAttribute(String name, Object value) {
        Map<String, Object> attributes = getAttributes();
        if (MapUtils.isEmpty(attributes)) {
            setAttributes(new HashMap<String, Object>());
        }

        getAttributes().put(name, value);
    }

    /**
     * Returns the value for the specified attribute name, or null if attribute not found.
     */
    public Object get(String attributeName) {
        return getAttribute(attributeName);
    }

}
