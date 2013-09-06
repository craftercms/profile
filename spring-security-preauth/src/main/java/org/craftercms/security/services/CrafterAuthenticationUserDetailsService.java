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
package org.craftercms.security.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.user.ProfileUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CrafterAuthenticationUserDetailsService implements AuthenticationUserDetailsService {
    private ProfileClient profileClient;

    private String crafterProfileAppUsername;
    private String crafterProfileAppPassword;

    private String crafterProfileAppTenantName;

    private static final Logger log = Logger.getLogger(CrafterAuthenticationUserDetailsService.class);

    @Override
    public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
        ProfileUserDetails userDetails = null;
        String username = token.getName();
        String password = token.getCredentials().toString();

        try {
            String appToken = profileClient.getAppToken(crafterProfileAppUsername, crafterProfileAppPassword);
            //Tenant tenant = profileClient.getTenantByName(appToken, crafterProfileAppTenantName);
            // authenticate (if the user is inactive, this will also fail)
            profileClient.getTicket(appToken, username, password, crafterProfileAppTenantName);

            Profile profile = profileClient.getProfileByUsernameWithAllAttributes(appToken, username,
                crafterProfileAppTenantName);

            userDetails = new ProfileUserDetails(profile, getAuthorities(profile));

        } catch (AppAuthenticationFailedException e) {
            log.error("Error authenticating at app level=" + username);
            throw new BadCredentialsException("Error authenticating username=" + username, e);
        } catch (UserAuthenticationFailedException e) {
            log.error("Error authenticating username=" + username);
            throw new BadCredentialsException("Error authenticating username=" + username, e);
        }

        return userDetails;
    }

    public void setProfileClient(ProfileClient profileClient) {
        this.profileClient = profileClient;
    }

    @Value("${crafter.profile.app.username}")
    public void setCrafterProfileAppUsername(String crafterProfileAppUsername) {
        this.crafterProfileAppUsername = crafterProfileAppUsername;
    }

    @Value("${crafter.profile.app.password}")
    public void setCrafterProfileAppPassword(String crafterProfileAppPassword) {
        this.crafterProfileAppPassword = crafterProfileAppPassword;
    }

    @Value("${crafter.profile.app.tenantName}")
    public void setCrafterProfileAppTenantName(String crafterProfileAppTenantName) {
        this.crafterProfileAppTenantName = crafterProfileAppTenantName;
    }

    private List<GrantedAuthority> getAuthorities(Profile p) {
        List<GrantedAuthority> lg = new ArrayList<GrantedAuthority>();

        for (String role : p.getRoles()) {
            lg.add(new SimpleGrantedAuthority(role));
        }

        return lg;
    }
}
