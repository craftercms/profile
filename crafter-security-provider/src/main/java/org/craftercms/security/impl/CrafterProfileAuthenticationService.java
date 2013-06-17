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
package org.craftercms.security.impl;

import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.UserAuthenticationException;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link AuthenticationService}, using a Crafter Profile client.
 *
 * @author Alfonso Vásquez
 */
public class CrafterProfileAuthenticationService implements AuthenticationService {

    protected ProfileClient profileClient;
    protected String appUsername;
    protected String appPassword;

    @Required
    public void setProfileClient(ProfileClient profileClient) {
        this.profileClient = profileClient;
    }

    @Required
    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }

    @Required
    public void setAppPassword(String appPassword) {
        this.appPassword = appPassword;
    }

    public UserProfile getProfile(String ticket) throws AuthenticationException {
        Profile profile = profileClient.getProfileByTicketWithAllAttributes(getAppToken(), ticket);
        if (profile != null) {
            return new UserProfile(profile);
        } else {
            return null;
        }
    }

    public String authenticate(String tenantName, String username, String password) throws AuthenticationException {
        String appToken = getAppToken();

        try {
            return profileClient.getTicket(appToken, username, password, tenantName);
        } catch (UserAuthenticationFailedException e) {
            throw new UserAuthenticationException("User authentication for '" + username + "' failed", e);
        }
    }

    public void invalidateTicket(String ticket) throws AuthenticationException{
        profileClient.invalidateTicket(getAppToken(), ticket);
    }

    protected String getAppToken() throws AuthenticationException {
        try {
            return profileClient.getAppToken(appUsername, appPassword);
        } catch (AppAuthenticationFailedException e) {
            throw new AuthenticationSystemException("App authentication for '" + appUsername + "' failed", e);
        }
    }

}
