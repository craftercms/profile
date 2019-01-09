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

/**
 * Represents a persistent authentication or login, triggered by remember me functionality. The login information
 * stored is basically the one proposed in <a href="http://jaspan.com/improved_persistent_login_cookie_best_practice">
 * Improved Persistent Login Cookie Best Practice</a>. The ID is basically the login series identifier.
 *
 * @author avasquez
 */
public class PersistentLogin {

    private String _id;
    private String tenant;
    private String profileId;
    private String token;
    private Date timestamp;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersistentLogin login = (PersistentLogin) o;

        if (!_id.equals(login._id)) {
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
        return "PersistentLogin{" +
            "id='" + _id + '\'' +
            ", tenant='" + tenant + '\'' +
            ", profileId='" + profileId + '\'' +
            ", token='" + token + '\'' +
            ", timestamp='" + timestamp + '\'' +
            '}';
    }

}
