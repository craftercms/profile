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
package org.craftercms.profile.management.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.impl.domain.Attribute;
import org.craftercms.profile.management.util.AttributeFieldsComparator;

public class SchemaForm extends SchemaModel implements Serializable {
	
	public SchemaForm(SchemaModel schema) {
		this.setTenantName(schema.getTenantName());
		this.setAttributes(schema.getAttributes());
        Collections.sort(getAttributes(), new AttributeFieldsComparator());
	}

    /**
     * Insert a new Attribute to the Attributes List
     *
     * @param attribute
     */

    public void addAttribute(Attribute attribute) {

        getAttributes().add(attribute);
        Collections.sort(getAttributes(), new AttributeFieldsComparator());
    }

    /**
     * Update an existing attribute with the given Attribute name
     * @param attribute
     */
    public void updateAttribute(Attribute attribute) {
        Attribute current;
        Iterator<Attribute> it = getAttributes().iterator();
        while (it.hasNext()) {
            current = it.next();
            if (current.getName().trim().equals(attribute.getName().trim())) {
                current.setLabel(attribute.getLabel());
                current.setOrder(attribute.getOrder());
                current.setType(attribute.getType());
                current.setConstraint(attribute.getConstraint());
                current.setRequired(attribute.isRequired());
                break;
            }
        }
    }

    /**
     * Return the Attribue that match the given name
     * @param attributeName
     * @return
     */

    public Attribute findAttributeByName(String attributeName) {
        Attribute find = null;
        Attribute current;
        Iterator<Attribute> it = getAttributes().iterator();
        while (it.hasNext()) {
            current = it.next();
            if (current.getName().trim().equals(attributeName.trim())) {
                find = current;
                break;
            }
        }
        return find;
    }

    /**
     * Remove all Attributes from the names list
     * @param names
     */

    public void deleteProperties(List<String> names) {
        Iterator<Attribute> it = getAttributes().iterator();
        while (it.hasNext()) {
            if (names.contains(it.next().getName().trim())) {
                it.remove();
                break;
            }
        }
    }


    /**
     * Get a map wth the name of the attributes list as keys and null values
     * @return
     */
    public Map<String, Object> getAttributesAsMap(){
        Map attributesMap = new HashMap<String,Serializable>();
        Attribute current;
        Iterator<Attribute> it = this.getAttributes().iterator();
        while (it.hasNext()) {
            current = it.next();
            attributesMap.put(current.getName().trim(),"");
        }
        return attributesMap;
    }
}
