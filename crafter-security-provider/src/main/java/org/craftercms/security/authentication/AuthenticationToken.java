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
package org.craftercms.security.authentication;

import org.craftercms.security.api.UserProfile;

/**
 * Represents a successful authentication, and houses security information like the authentication ticket, the user profile, and if
 * the profile is outdated (needs to be updated from the authentication service).
 *
 * @author Alfonso Vásquez
 */
public class AuthenticationToken {

    private String ticket;
    private UserProfile profile;
    private boolean profileOutdated;

    /**
     * Returns the user profile.
     */
    public UserProfile getProfile() {
        return profile;
    }

    /**
     * Sets the user profile.
     */
    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    /**
     * Returns true if the profile is outdated (needs to be updated from the authentication service).
     */
    public boolean isProfileOutdated() {
        return profileOutdated;
    }

    /**
     * Sets if the profile is outdated (needs to be updated from the authentication service).
     */
    public void setProfileOutdated(boolean profileOutdated) {
        this.profileOutdated = profileOutdated;
    }

    /**
     * Returns the authentication ticket.
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * Sets the authentication ticket.
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "AuthenticationToken[" +
                "ticket='" + ticket + '\'' +
                ", profile=" + profile +
                ", profileOutdated=" + profileOutdated +
                ']';
    }

}
