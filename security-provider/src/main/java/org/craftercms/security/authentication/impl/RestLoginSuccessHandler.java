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
package org.craftercms.security.authentication.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.handlers.AbstractRestHandlerBase;

/**
 * Implementation of {@link org.craftercms.security.authentication.LoginSuccessHandler} for REST based applications,
 * which returns the {@link org.craftercms.security.authentication.Authentication} object as the response body.
 *
 * @author avasquez
 */
public class RestLoginSuccessHandler extends AbstractRestHandlerBase implements LoginSuccessHandler {

    @Override
    public void handle(RequestContext context, Authentication authentication) throws SecurityProviderException,
            IOException {
        sendObject(HttpServletResponse.SC_OK, authentication, context);
    }

}
