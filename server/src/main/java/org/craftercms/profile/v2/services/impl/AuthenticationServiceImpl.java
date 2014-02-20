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
import org.craftercms.profile.api.TenantActions;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.api.services.AuthenticationService;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
@HasPermission(type = TenantPermission.class, action = TenantActions.MANAGE_USERS)
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public String authenticate(@SecuredObject String tenant, String username, String password) {
        return null;
    }

    @Override
    public boolean isTicketValid(@SecuredObject String tenant, String ticket) {
        return false;
    }

    @Override
    public void invalidateTicket(@SecuredObject String tenant, String ticket) {

    }

}
