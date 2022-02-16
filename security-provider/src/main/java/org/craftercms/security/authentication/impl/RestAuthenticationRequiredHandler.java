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
package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.AuthenticationRequiredHandler;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.handlers.AbstractRestHandlerBase;

/**
 * Implementation of {@link org.craftercms.security.authentication.AuthenticationRequiredHandler} for REST based
 * applications, which returns a 401 UNAUTHORIZED status with the authentication exception message.
 *
 * @author avasquez
 */
public class RestAuthenticationRequiredHandler extends AbstractRestHandlerBase implements AuthenticationRequiredHandler {

    @Override
    public void handle(RequestContext context, AuthenticationException e) throws SecurityProviderException,
            IOException {
        sendErrorMessage(HttpServletResponse.SC_UNAUTHORIZED, e, context);
    }

}
