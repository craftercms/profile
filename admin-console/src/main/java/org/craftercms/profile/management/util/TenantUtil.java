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
package org.craftercms.profile.management.util;

import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Tenant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author David Escalante
 */

public class TenantUtil {

    public static Map<String, String> getTenantsMap(List<Tenant> tenantList){
        Map<String, String> tenantsMap = new HashMap<String, String>();
        for(Tenant tenant: tenantList){
            tenantsMap.put(tenant.getTenantName(), tenant.getTenantName());
        }

        return tenantsMap;
    }

    public static Attribute findSchemaAttributeByName(String attributeName, Tenant tenant) {
        Attribute find = null;
        Attribute current;
        Iterator<Attribute> it = tenant.getSchema().getAttributes().iterator();
        while (it.hasNext()) {
            current = it.next();
            if (current.getName().trim().equals(attributeName.trim())) {
                find = current;
                break;
            }
        }
        return find;
    }

    public static Map<String, Object> getSchemaAttributesAsMap(Tenant tenant) {
        Map attributesMap = new HashMap<String,Serializable>();
        Attribute current;
        Iterator<Attribute> it = tenant.getSchema().getAttributes().iterator();
        while (it.hasNext()) {
            current = it.next();
            attributesMap.put(current.getName().trim(),"");
        }
        return attributesMap;
    }

    public static Map<String, Object> getSchemaAttributesAsMap(Tenant tenant, Profile profile) {
        Map attributesMap = new HashMap<String,Serializable>();
        Attribute current;
        Iterator<Attribute> it = tenant.getSchema().getAttributes().iterator();
        while (it.hasNext()) {
            current = it.next();
            if(profile.getAttributes() != null && profile.getAttributes().containsKey(current.getName()))
            {
                attributesMap.put(current.getName().trim(), profile.getAttributes().get(current.getName()));
            }else{
                attributesMap.put(current.getName().trim(),"");
            }
        }
        return attributesMap;
    }
}
