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

package org.craftercms.security.processors.impl;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.RememberMeManager;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link org.craftercms.security.processors.RequestSecurityProcessor} that executes auto login when a valid remember
 * me cookie is found in the request and there's no authentication present yet.
 *
 * @author avasquez
 */
public class RememberMeAutoLoginProcessor implements RequestSecurityProcessor {

    protected RememberMeManager rememberMeManager;

    @Required
    public void setRememberMeManager(final RememberMeManager rememberMeManager) {
        this.rememberMeManager = rememberMeManager;
    }

    @Override
    public void processRequest(RequestContext context,
                               RequestSecurityProcessorChain processorChain) throws Exception {
        Authentication auth = SecurityUtils.getAuthentication(context.getRequest());

        if (auth == null) {
            auth = rememberMeManager.autoLogin(context);
            if (auth != null) {
                SecurityUtils.setAuthentication(context.getRequest(), auth);
            }
        }

        processorChain.processRequest(context);
    }

}
