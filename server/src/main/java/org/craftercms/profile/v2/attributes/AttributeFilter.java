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
package org.craftercms.profile.v2.attributes;

import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.v2.exceptions.AttributeFilterException;

import java.util.Map;

/**
 * Attributes filter interface. Used on attribute read and write to check attribute permissions and constraints.
 *
 * @author avasquez
 */
public interface AttributeFilter {

    /**
     * Filters the attributes on read/write.
     *
     * @param tenant        the tenant that contains the attribute definitions
     * @param attributes    the attributes to filter
     *
     * @return the modified attributes
     */
    Map<String, Object> filter(Tenant tenant, Map<String, Object> attributes) throws AttributeFilterException;

}
