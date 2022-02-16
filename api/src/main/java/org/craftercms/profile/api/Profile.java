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

package org.craftercms.profile.api;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.craftercms.commons.jackson.mvc.annotations.Exclude;

/**
 * Representation of a user.
 *
 * @author avasquez
 */
public class Profile {

    private ObjectId _id;
    private String username;
    @Exclude
    private String password;
    private String email;
    private boolean verified;
    private boolean enabled;
    private Date createdOn;
    private Date lastModified;
    private String tenant;
    private Set<String> roles;
    private Map<String, Object> attributes;
    @Exclude
    private int failedLoginAttempts;
    @Exclude
    private Date lastFailedLogin;

    public ObjectId getId() {
        return _id;
    }

    public void setId(final ObjectId id) {
        this._id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }


    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Date getLastFailedLogin() {
        return lastFailedLogin;
    }

    public void setLastFailedLogin(Date lastFailedLogin) {
        this.lastFailedLogin = lastFailedLogin;
    }

    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAnyRole(Collection<String> roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    public Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }

        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        return attributes;
    }

    public <T> T getAttribute(String name) {
        return (T) getAttributes().get(name);
    }

    public void setAttributes(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(String name, Object value) {
        getAttributes().put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Profile profile = (Profile) o;

        if (!_id.equals(profile._id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + _id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", verified=" + verified +
                ", enabled=" + enabled +
                ", createdOn=" + createdOn +
                ", lastModified=" + lastModified +
                ", tenant='" + tenant + '\'' +
                ", roles=" + roles +
                ", attributes=" + attributes +
                '}';
    }

    public void increaseFailedLoginAttempts() {
        failedLoginAttempts++;
    }

}
