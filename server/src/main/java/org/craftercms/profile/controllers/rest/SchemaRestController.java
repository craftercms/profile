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
package org.craftercms.profile.controllers.rest;

import org.craftercms.profile.constants.AttributeConstants;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.exceptions.TenantException;
import org.craftercms.profile.services.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/2/schema/")
public class SchemaRestController {

    @Autowired
    private SchemaService schemaService;

    /**
     * Get a schema based on a tenant id
     *
     * @param tenantName
     */
    @RequestMapping(value = "get_schema/{tenantName}", method = RequestMethod.GET)
    @ModelAttribute
    public Schema getSchemaByTenantName(@PathVariable final String tenantName) throws TenantException {
        return schemaService.geSchemaByTenantName(tenantName);
    }

    /**
     * Set attributes to profile
     *
     * @param tenantName
     * @param attribute
     */
    @RequestMapping(value = "set_attribute", method = RequestMethod.POST)
    @ModelAttribute
    public void setAttributes(@RequestParam final String tenantName, final Attribute attribute) throws TenantException {
        schemaService.setAttribute(tenantName, attribute);
    }

    /**
     * Delete Attributes
     *
     * @param tenantName tenant name used to delete attributes of a schema
     * @param name       role name
     */
    @RequestMapping(value = "delete_attribute", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAttribute(@RequestParam final String tenantName, @RequestParam(required = false,
        value = AttributeConstants.NAME) final String name) throws TenantException {
        schemaService.deleteAttribute(tenantName, name);
    }
}
