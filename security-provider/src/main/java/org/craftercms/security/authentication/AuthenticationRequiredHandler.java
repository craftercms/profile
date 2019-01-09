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
package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.SecurityProviderException;

/**
 * Handles the request when authentication is required.
 *
 * @author Alfonso Vásquez
 */
public interface AuthenticationRequiredHandler {

    /**
     * Handles the request when authentication is required.
     *
     * @param context the request context
     * @param e       the exception with the reason for requiring authentication
     */
    void handle(RequestContext context, AuthenticationException e) throws SecurityProviderException, IOException;

}
