/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.profile.v2.attributes.impl;

import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.v2.attributes.AttributeFilter;
import org.craftercms.profile.v2.exceptions.AttributeFilterException;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;

/**
 * Composite pattern of {@link org.craftercms.profile.v2.attributes.AttributeFilter}.
 *
 * @author avasquez
 */
public class CompositeAttributeFilter implements AttributeFilter {

    private List<AttributeFilter> filters;

    @Required
    public void setFilters(List<AttributeFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Map<String, Object> filter(Tenant tenant, Map<String, Object> attributes) throws AttributeFilterException {
        for (AttributeFilter filter : filters) {
            attributes = filter.filter(tenant, attributes);
        }

        return attributes;
    }

}
