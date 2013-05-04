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

import java.util.Comparator;

import org.craftercms.profile.domain.Attribute;

public class AttributeFieldsComparator implements Comparator {
	
	private int compareProfilePropertyForm(Attribute obj1, Attribute obj2) {
		
		int x = obj1.getOrder();
		int y = obj2.getOrder();
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 instanceof Attribute && o2 instanceof Attribute) {
			return compareProfilePropertyForm((Attribute)o1,(Attribute)o2);
		}
		return -1;
	}

}
