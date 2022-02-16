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

package org.craftercms.security.utils.tenant;

import java.util.List;

import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link TenantsResolver} that uses all the available tenants.
 *
 * @author avasquez
 */
public class AllTenantsResolver implements TenantsResolver {

    private static final Logger logger = LoggerFactory.getLogger(AllTenantsResolver.class);

    protected TenantService tenantService;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public String[] getTenants() {
        try {
            List<String> tenants = TenantUtils.getTenantNames(tenantService);
            if (tenants != null) {
                return tenants.toArray(new String[tenants.size()]);
            } else {
                return null;
            }
        } catch (ProfileException e) {
            logger.warn("Unable to retrieve tenants", e);

            return null;
        }
    }

}
