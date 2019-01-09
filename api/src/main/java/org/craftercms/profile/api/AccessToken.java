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

import java.util.Date;
import java.util.List;

/**
 * Access token given to applications that need to access the REST API, acting for any tenant of a list.
 *
 * @author avasquez
 */
public class AccessToken {

    private String _id;
    private String application;
    private boolean master;
    private List<TenantPermission> tenantPermissions;
    private Date expiresOn;

    /**
     * Returns the ID of the access token.
     */
    public String getId() {
        return _id;
    }

    /**
     * Sets the ID of the access token.
     *
     * @param id    the token's ID
     */
    public void setId(String id) {
        this._id = id;
    }

    /**
     * Returns the name of the application accessing Crafter Profile.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Sets the name of the application accessing Crafter Profile.
     *
     * @param application   the application name
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Returns true if this is a master token. A master token can be used to create and delete other tokens.
     */
    public boolean isMaster() {
        return master;
    }

    /**
     * Sets if this is a master token. A master token can be used to create and delete other tokens.
     *
     * @param master trues if this should be a master token, false otherwise
     */
    public void setMaster(boolean master) {
        this.master = master;
    }

    /**
     * Returns the tenant permissions the application has.
     */
    public List<TenantPermission> getTenantPermissions() {
        return tenantPermissions;
    }

    /**
     * Sets the he tenant permissions the application has.
     *
     * @param tenantPermissions the tenant permissions
     */
    public void setTenantPermissions(List<TenantPermission> tenantPermissions) {
        this.tenantPermissions = tenantPermissions;
    }

    /**
     * Returns the date of expiration of this token (when it becomes invalid)
     */
    public Date getExpiresOn() {
        return expiresOn;
    }

    /**
     * Sets the date of expiration of this token (when it becomes invalid)
     *
     * @param expiresOn the expiration date of the token
     */
    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccessToken token = (AccessToken)o;

        return _id.equals(token._id);

    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public String toString() {
        return "AccessToken{" +
               "_id='" + _id + '\'' +
               ", application='" + application + '\'' +
               ", master=" + master +
               ", tenantPermissions=" + tenantPermissions +
               ", expiresOn=" + expiresOn +
               '}';
    }

}
