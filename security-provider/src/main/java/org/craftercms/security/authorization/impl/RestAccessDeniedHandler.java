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
package org.craftercms.security.authorization.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authorization.AccessDeniedHandler;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.handlers.AbstractRestHandlerBase;

/**
 * Implementation of {@link org.craftercms.security.authorization.AccessDeniedHandler} for REST based applications,
 * which returns a 403 FORBIDDEN status with the access denied exception message.
 *
 * @author avasquez
 */
public class RestAccessDeniedHandler extends AbstractRestHandlerBase implements AccessDeniedHandler {

    @Override
    public void handle(RequestContext context, AccessDeniedException e) throws SecurityProviderException, IOException {
        sendErrorMessage(HttpServletResponse.SC_FORBIDDEN, e, context);
    }

}
