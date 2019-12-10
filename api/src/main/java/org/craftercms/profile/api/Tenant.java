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

package org.craftercms.profile.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

/**
 * A tenant is normally an application that shares common configuration.
 *
 * @author avasquez
 */
public class Tenant {

    private ObjectId _id;
    private String name;
    private boolean verifyNewProfiles;
    private Set<String> availableRoles;
    private boolean ssoEnabled;
    private List<AttributeDefinition> attributeDefinitions;

    protected boolean cleanseAttributes = true;

    /**
     * Returns the tenant's DB ID.
     */
    public ObjectId getId() {
        return _id;
    }

    /**
     * Sets the tenant's DB ID.
     *
     * @param id the ID
     */
    public void setId(ObjectId id) {
        this._id = id;
    }

    /**
     * Returns the name of the tenant.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the tenant.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns true if new profiles should be verified through email by the user, for the accounts or users of
     * this tenant.
     */
    public boolean isVerifyNewProfiles() {
        return verifyNewProfiles;
    }

    /**
     * Sets if new profiles should be verified through email by the user, for the accounts or users of
     * this tenant.
     *
     * @param verifyNewProfiles true to verify new profiles, false otherwise
     */
    public void setVerifyNewProfiles(boolean verifyNewProfiles) {
        this.verifyNewProfiles = verifyNewProfiles;
    }

    /**
     * Returns the available roles that can be assigned to users of this tenant.
     */
    public Set<String> getAvailableRoles() {
        if (availableRoles == null) {
            availableRoles = new HashSet<>();
        }

        return availableRoles;
    }

    /**
     * Sets the available roles that can be assigned to users of this tenant.
     *
     * @param availableRoles the available roles for users of the tenant.
     */
    public void setAvailableRoles(Set<String> availableRoles) {
        this.availableRoles = availableRoles;
    }

    /**
     * Returns true is single-sign on (SSO) is enabled for this tenant .
     */
    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    /**
     * Sets if single sign-on (SSO) should be enabled for this tenant.
     */
    public void setSsoEnabled(boolean ssoEnabled) {
        this.ssoEnabled = ssoEnabled;
    }

    /**
     * Returns the definitions of attributes that users of this tenant can have.
     */
    public List<AttributeDefinition> getAttributeDefinitions() {
        if (attributeDefinitions == null) {
            attributeDefinitions = new ArrayList<>();
        }

        return attributeDefinitions;
    }

    /**
     * Sets the definitions of attributes that users of this tenant can have.
     *
     * @param attributeDefinitions  the available attribute definitions for users of the tenant
     */
    public void setAttributeDefinitions(List<AttributeDefinition> attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    public boolean isCleanseAttributes() {
        return cleanseAttributes;
    }

    public void setCleanseAttributes(final boolean cleanseAttributes) {
        this.cleanseAttributes = cleanseAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tenant tenant = (Tenant) o;

        if (!_id.equals(tenant._id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return "Tenant{" +
               "_id=" + _id +
               ", name='" + name + '\'' +
               ", verifyNewProfiles=" + verifyNewProfiles +
               ", availableRoles=" + availableRoles +
               ", ssoEnabled=" + ssoEnabled +
               ", attributeDefinitions=" + attributeDefinitions +
               '}';
    }

}
