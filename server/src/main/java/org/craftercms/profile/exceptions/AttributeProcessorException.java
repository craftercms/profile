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
package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.ProfileException;

/**
 * Thrown when processing attributes fails for some reason.
 *
 * @author avasquez
 */
public class AttributeProcessorException extends ProfileException {

    public AttributeProcessorException() {
    }

    public AttributeProcessorException(String message) {
        super(message);
    }

    public AttributeProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttributeProcessorException(Throwable cause) {
        super(cause);
    }

}
