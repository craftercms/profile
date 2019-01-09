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
import org.craftercms.security.exception.SecurityProviderException;

/**
 * Handles the request after a successful logout.
 *
 * @author avasquez
 */
public interface LogoutSuccessHandler {

    /**
     * Handles the request after a successful logout.
     *
     * @param context           the request context
     */
    void handle(RequestContext context) throws SecurityProviderException, IOException;

}
