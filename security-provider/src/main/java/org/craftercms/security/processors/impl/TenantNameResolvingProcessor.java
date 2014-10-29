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
package org.craftercms.security.processors.impl;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Obtains and sets the tenant name for the current request. A tenant parameter is checked in the request,
 * and if not present, the default tenant name is used.
 *
 * @author Alfonso Vásquez
 */
public class TenantNameResolvingProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(TenantNameResolvingProcessor.class);

    public static final String PARAM_TENANT_NAME = "tenantName";

    protected String defaultTenantName;

    /**
     * Sets a default tenant name.
     */
    @Required
    public void setDefaultTenantName(String defaultTenantName) {
        this.defaultTenantName = defaultTenantName;
    }

    /**
     * Sets the tenant name in the context, from the parameter or the default.
     *
     * @param context        the context which holds the current request and other security info pertinent to the
     *                       request
     * @param processorChain the processor chain, used to call the next processor
     */
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        String tenantName = context.getRequest().getParameter(PARAM_TENANT_NAME);

        if (StringUtils.isEmpty(tenantName)) {
            tenantName = defaultTenantName;
        }

        logger.debug("Tenant name resolved for current request: {}", tenantName);

        SecurityUtils.setTenant(context.getRequest(), tenantName);

        processorChain.processRequest(context);
    }



}
