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
package org.craftercms.profile.v2.attributes.impl;

import org.craftercms.profile.v2.attributes.AttributesProcessor;
import org.craftercms.profile.v2.exceptions.AttributeProcessorException;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;

/**
 * Composite pattern of {@link org.craftercms.profile.v2.attributes.AttributesProcessor}.
 *
 * @author avasquez
 */
public class CompositeAttributeProcessor implements AttributesProcessor {

    private List<AttributesProcessor> processors;

    @Required
    public void setProcessors(List<AttributesProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public Map<String, Object> process(Map<String, Object> attributes) throws AttributeProcessorException {
        for (AttributesProcessor processor : processors) {
            attributes = processor.process(attributes);
        }

        return attributes;
    }

}
