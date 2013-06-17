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
package org.craftercms.profile.repositories;

import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Tenant;

import java.util.List;

public interface TenantRepositoryCustom {

    List<Tenant> getTenantRange(String sortBy, String sortOrder, int start, int end);
	Tenant getTenantByName(String tenantName);
    void setAttribute(String tenantName, Attribute attribute);
    void deleteAttribute(String tenantName, String attributeName);
}
