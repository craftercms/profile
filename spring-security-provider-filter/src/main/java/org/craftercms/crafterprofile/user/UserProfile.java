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
package org.craftercms.crafterprofile.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.craftercms.profile.impl.domain.Profile;

/**
 * Base Crafter Profile user profile object
 *
 * @author Sandra O'Keeffe
 */
public class UserProfile implements Serializable {

    public static final String ROLE = "role";

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String id;
    private Date createdDate;
    private Date modifiedDate;
    private boolean active;
    private boolean temporaryPassword;
    private List<String> roles;
    private String ticket;

    private String tenantName;


    /**
     * Building the base profile properties
     *
     * @param profile
     */
    public UserProfile(Profile profile) {
        this.username = profile.getUserName();
        this.password = profile.getPassword();
        this.id = profile.getId();
        this.createdDate = profile.getCreated();
        this.modifiedDate = profile.getModified();
        this.active = profile.getActive();
        this.roles = new ArrayList<String>();

    }

    public UserProfile() {
        this.active = true;
        this.roles = new ArrayList<String>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(boolean temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRole(String role) {
        roles.add(role);
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

}
