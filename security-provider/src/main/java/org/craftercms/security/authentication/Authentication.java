/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
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

import org.craftercms.profile.api.Profile;

/**
 * Represents a successful authentication.
 *
 * @author avasquez
 */
public interface Authentication {

    /**
     * Returns the ticket that identifies the authentication.
     */
    String getTicket();

    /**
     * Returns the profile associated to the authentication.
     */
    Profile getProfile();

    /**
     * Indicates if it's a remembered authentication.
     */
    boolean isRemembered();

}
