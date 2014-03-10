/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.profile.v2.services.impl;

import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.TenantActions;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.api.utils.SortOrder;

import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.ProfileService}.
 *
 * @author avasquez
 */
@HasPermission(type = TenantPermission.class, action = TenantActions.MANAGE_USERS)
public class ProfileServiceImpl implements ProfileService {

    @Override
    public Profile createUser(@SecuredObject String tenant, String userId, String password, String email, boolean enabled,
                           List<String> roles, List<String> groups, String verifyAccountUrl) {
        return null;
    }

    @Override
    public Profile updateUser(@SecuredObject String tenant, String userId, String password, String email, boolean enabled,
                           List<String> roles, List<String> groups) {
        return null;
    }

    @Override
    public Profile enableUser(@SecuredObject String tenant, String userId) {
        return null;
    }

    @Override
    public Profile disableUser(@SecuredObject String tenant, String userId) {
        return null;
    }

    @Override
    public Profile addRoles(@SecuredObject String tenant, String userId, List<String> roles) {
        return null;
    }

    @Override
    public Profile removeRoles(@SecuredObject String tenant, String userId, List<String> roles) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes(@SecuredObject String tenant, String userId) {
        return null;
    }

    @Override
    public Map<String, Object> updateAttributes(@SecuredObject String tenant, String userId,
                                                Map<String, Object> attributes) {
        return null;
    }

    @Override
    public Profile deleteUser(@SecuredObject String tenant, String userId) {
        return null;
    }

    @Override
    public Profile getUser(@SecuredObject String tenant, String userId) {
        return null;
    }

    @Override
    public Profile getUserByTenantAndUsername(@SecuredObject String tenant, String username) {
        return null;
    }

    @Override
    public Profile getUserByTicket(@SecuredObject String tenant, String ticket) {
        return null;
    }

    @Override
    public int getUserCount(@SecuredObject String tenant) {
        return 0;
    }

    @Override
    public List<Profile> getUsers(@SecuredObject String tenant, List<String> userIds, String sortBy, SortOrder sortOrder) {
        return null;
    }

    @Override
    public List<Profile> getAllUsers(@SecuredObject String tenant, String sortBy, String sortOrder, Integer start,
                                  Integer count) {
        return null;
    }

    @Override
    public List<Profile> getUsersByRole(@SecuredObject String tenant, String role, String sortBy, SortOrder sortOrder,
                                     Integer start, Integer count) {
        return null;
    }

    @Override
    public List<Profile> getUsersByGroup(@SecuredObject String tenant, String group, String sortBy, SortOrder sortOrder,
                                      Integer start, Integer count) {
        return null;
    }

    @Override
    public void forgotPassword(@SecuredObject String tenant, String userId, String changePasswordUrl) {

    }

    @Override
    public void resetPassword(@SecuredObject String tenant, String resetToken, String newPassword) {

    }

}
