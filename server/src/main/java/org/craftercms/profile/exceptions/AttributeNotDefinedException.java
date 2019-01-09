/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when an attribute definition is being updated or when an attribute value is being set but not attribute
 * definition was found.
 *
 * @author avasquez
 */
public class AttributeNotDefinedException extends I10nProfileException {

    public static final String KEY = "profile.attribute.attributeNotDefined";

    public AttributeNotDefinedException(String attributeName, String tenant) {
        super(KEY, attributeName, tenant);
    }

}
