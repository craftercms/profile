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
package org.craftercms.profile.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.ProfileUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("profileUserDetailsService")
public class ProfileUserDetailsServiceImpl implements ProfileUserDetailsService {

    @Autowired
    private ProfileService profileService;

    private List<SimpleGrantedAuthority> defaultGrantedAuthorities = new ArrayList<SimpleGrantedAuthority>();


    public ProfileUserDetailsServiceImpl() {
        super();
        defaultGrantedAuthorities.add(new SimpleGrantedAuthority("USER"));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String tenantName = null;
        String newUsername = username;
        if (username.lastIndexOf('@') > 0) {

            int idx = username.lastIndexOf('@');
            newUsername = username.substring(0, idx);
            tenantName = username.substring(idx + 1);
        }

        Profile profile = profileService.getProfileByUserName(newUsername, tenantName, null);
        return new User(profile.getUserName(), profile.getPassword(), profile.getActive(), true, true, true,
            defaultGrantedAuthorities);
    }
}
