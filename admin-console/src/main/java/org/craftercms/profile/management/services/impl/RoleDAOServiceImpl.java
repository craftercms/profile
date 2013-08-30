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
package org.craftercms.profile.management.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.impl.domain.Role;
import org.craftercms.profile.management.services.RoleDAOService;
import org.springframework.stereotype.Service;

/**
 * @author David Escalante
 */
@Service
public class RoleDAOServiceImpl implements RoleDAOService {

    private static final Logger log = Logger.getLogger(RoleDAOServiceImpl.class);

    @Override
    public List<Role> getAllRoles() throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            return ProfileServiceManager.getProfileClient().getAllRoles(ProfileServiceManager.getAppToken());
        } catch (AppAuthenticationException e) {
            try {

                ProfileServiceManager.setAppToken();

            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            return ProfileServiceManager.getProfileClient().getAllRoles(ProfileServiceManager.getAppToken());
        }
    }

}
