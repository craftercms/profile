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
package org.craftercms.profile.attributes;

import org.craftercms.profile.exceptions.AttributeProcessorException;

import java.util.Map;

/**
 * Processor for attributes. Used on attribute read and write.
 *
 * @author avasquez
 */
public interface AttributesProcessor {

    /**
     * Process the attributes on read/write.
     *
     * @param attributes    the attributes to process
     *
     * @return the modified attributes
     */
    Map<String, Object> process(Map<String, Object> attributes) throws AttributeProcessorException;

}
