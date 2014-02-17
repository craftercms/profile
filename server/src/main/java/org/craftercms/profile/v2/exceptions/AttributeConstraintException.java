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
package org.craftercms.profile.v2.exceptions;

/**
 * Thrown when an attribute doesn't satisfy it's definition constraint.
 *
 * @author avasquez
 */
public class AttributeConstraintException extends AttributeProcessorException {

    public static final String MESSAGE_FORMAT = "Attribute '%s' doesn't match the constraint '%s'";

    public AttributeConstraintException(String attributeName, String constraint) {
        super(String.format(MESSAGE_FORMAT, attributeName, constraint));
    }

}
