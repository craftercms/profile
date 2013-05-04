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
package org.craftercms.security.impl.processors;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Obtains and sets the tenant name for the current request. A default tenant name is used by this implementation.
 *
 * @author Alfonso Vásquez
 */
public class TenantNameResolvingProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(TenantNameResolvingProcessor.class);

    protected String defaultTenantName;

    @Required
    public void setDefaultTenantName(String defaultTenantName) {
        this.defaultTenantName = defaultTenantName;
    }

    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Tenant name resolved for current request: " + defaultTenantName);
        }

        context.setTenantName(defaultTenantName);

        processorChain.processRequest(context);
    }

}
