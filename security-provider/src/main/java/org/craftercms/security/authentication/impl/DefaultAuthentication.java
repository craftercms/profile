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
package org.craftercms.security.authentication.impl;

import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;

/**
 * Default implementation of {@link org.craftercms.security.authentication.Authentication}.
 *
 * @author avasquez
 */
public class DefaultAuthentication implements Authentication {

    private String ticket;
    private Profile profile;
    private boolean remembered;

    public DefaultAuthentication(String ticket, Profile profile) {
        this.ticket = ticket;
        this.profile = profile;
        this.remembered = false;
    }

    public DefaultAuthentication(String ticket, Profile profile, boolean remembered) {
        this.ticket = ticket;
        this.profile = profile;
        this.remembered = remembered;
    }

    @Override
    public String getTicket() {
        return ticket;
    }

    @Override
    public Profile getProfile() {
        return profile;
    }

    @Override
    public boolean isRemembered() {
        return remembered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultAuthentication auth = (DefaultAuthentication) o;

        if (ticket != null ? !ticket.equals(auth.ticket) : auth.ticket != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return ticket != null ? ticket.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DefaultAuthentication{" +
               "ticket='" + ticket + '\'' +
               ", profile=" + profile +
               ", remembered=" + remembered +
               '}';
    }

}
