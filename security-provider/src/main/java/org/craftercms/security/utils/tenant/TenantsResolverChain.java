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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@link TenantsResolver} that returns the first tenants resolved by any resolver.
 * If the flag {@code chainAllTenants} is set, instead all non empty tenants returned by the resolvers are returned.
 *
 * @author avasquez
 */
public class TenantsResolverChain implements TenantsResolver {

    protected List<TenantsResolver> resolvers;
    protected boolean chainAllTenants;

    @Required
    public void setResolvers(final List<TenantsResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public void setChainAllTenants(final boolean chainAllTenants) {
        this.chainAllTenants = chainAllTenants;
    }

    @Override
    public String[] getTenants() {
        List<String> finalTenants = null;

        for (TenantsResolver resolver : resolvers) {
            String[] tenants = resolver.getTenants();
            if (ArrayUtils.isNotEmpty(tenants)) {
                if (chainAllTenants) {
                    if (finalTenants == null) {
                        finalTenants = new ArrayList<>();
                    }

                    finalTenants.addAll(Arrays.asList(tenants));
                } else {
                    return tenants;
                }
            }
        }

        return finalTenants != null ? finalTenants.toArray(new String[finalTenants.size()]) : null;
    }

}
