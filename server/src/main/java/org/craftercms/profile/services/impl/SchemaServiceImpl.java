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
package org.craftercms.profile.services.impl;

import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.services.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchemaServiceImpl implements SchemaService {

    @Autowired
    private TenantRepository tenantRepository;

	@Override
	public void setAttribute(String tenantName, Attribute attribute) {
        tenantRepository.setAttribute(tenantName, attribute);
		
	}
	
	@Override
	public Schema geSchemaByTenantName(String tenantName) {
        Tenant tenant = tenantRepository.getTenantByName(tenantName);
        Schema schema = null;
        if (tenant != null) {
        	return tenant.getSchema();
        }
        return schema;
	}

    @Override
	public void deleteAttribute(String tenantName, String attributeName) {
        tenantRepository.deleteAttribute(tenantName, attributeName);
	}
	
}
