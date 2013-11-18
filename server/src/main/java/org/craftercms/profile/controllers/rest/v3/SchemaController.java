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
package org.craftercms.profile.controllers.rest.v3;

import org.craftercms.profile.constants.AttributeConstants;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.services.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/2/schema/")
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(value = "get_by_tenant_name", method = RequestMethod.GET)
    @ModelAttribute
    public Schema getSchemaByTenantName(@RequestParam String tenantName) {
        return schemaService.geSchemaByTenantName(tenantName);
    }
    @RequestMapping(value = "add_attribute", method = RequestMethod.POST)
    @ModelAttribute
    public void addAttribute(@RequestParam String tenantName, Attribute attribute) {
        schemaService.setAttribute(tenantName, attribute);
    }

    @RequestMapping(value = "delete_attribute", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteAttribute(@RequestParam String tenantName,
                                @RequestParam(required = false, value = AttributeConstants.NAME) String attributeName) {
        schemaService.deleteAttribute(tenantName, attributeName);
    }

}
