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
package org.craftercms.profile.services;

import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.exceptions.TenantException;

/**
 * Encapsulates services for the schema
 *
 * @author Alvaro Gonzalez
 */
public interface SchemaService {
    /**
     * Sets a new attributes for a tenant
     *
     * @param tenantName is going to be updated
     * @param attribute  new attribute data
     */
    void setAttribute(String tenantName, Attribute attribute) throws TenantException;

    /**
     * Deletes an attribute
     *
     * @param tenantName    used to delete the attribute
     * @param attributeName attribute is going to be deleted
     */
    void deleteAttribute(String tenantName, String attributeName) throws TenantException;

    /**
     * Gets a schema based on a tenant name
     *
     * @param tenantName used to get the schema
     * @return a schema instance
     */
    Schema geSchemaByTenantName(String tenantName) throws TenantException;
}
