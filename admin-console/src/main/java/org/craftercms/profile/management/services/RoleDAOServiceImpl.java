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
package org.craftercms.profile.management.services;

import org.apache.log4j.Logger;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author David Escalante
 */
@Service
public class RoleDAOServiceImpl implements RoleDAOService{

    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private ProfileClient profileRestClient;

    private String username;
    private String password;
    private String crafterProfileAppTenantName;

    private String appToken;

    private static final Logger log = Logger.getLogger(RoleDAOServiceImpl.class);

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Interface Implementations">

    @Override
    public List<Role> getAllRoles() throws AppAuthenticationFailedException {
        if (appToken == null) {
            setAppToken();
        }
        return profileRestClient.getAllRoles(appToken);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Util Functions">

    private void setAppToken() throws AppAuthenticationFailedException {
        appToken = profileRestClient.getAppToken(username, password);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Attribute Setters">

    @Value("${crafter.profile.app.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${crafter.profile.app.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Autowired
    public void setProfileRestClient(ProfileClient profileRestClient) {
        this.profileRestClient = profileRestClient;
    }

    @Value("${crafter.profile.app.tenant.name}")
    public void setCrafterProfileAppTenantName(String crafterProfileAppTenantName) {
        this.crafterProfileAppTenantName = crafterProfileAppTenantName;
    }

    // </editor-fold>
}
