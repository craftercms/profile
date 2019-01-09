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

package org.craftercms.security.utils.tenant;

import org.craftercms.commons.http.RequestContext;

/**
 * {@link TenantsResolver} that resolves the tenants through a param.
 *
 * @author avasquez
 */
public class ParamTenantsResolver implements TenantsResolver {

    public static final String DEFAULT_TENANT_NAME_PARAM = "tenantName";

    protected String tenantNameParam;

    public ParamTenantsResolver() {
        tenantNameParam = DEFAULT_TENANT_NAME_PARAM;
    }

    public void setTenantNameParam(String tenantNameParam) {
        this.tenantNameParam = tenantNameParam;
    }

    @Override
    public String[] getTenants() {
        RequestContext context = RequestContext.getCurrent();
        if (context != null) {
            return context.getRequest().getParameterValues(tenantNameParam);
        } else {
            return null;
        }
    }

}
