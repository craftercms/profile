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
package org.craftercms.security.impl.processors;

import java.util.Iterator;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;

/**
 * Default implementation of a handler chain, using an iterator.
 *
 * @author Alfonso Vásquez
 */
public class RequestSecurityProcessorChainImpl implements RequestSecurityProcessorChain {

    private Iterator<RequestSecurityProcessor> processorIterator;

    /**
     * Default constructor
     *
     * @param processorIterator iterator of {@link RequestSecurityProcessor}s.
     */
    public RequestSecurityProcessorChainImpl(Iterator<RequestSecurityProcessor> processorIterator) {
        this.processorIterator = processorIterator;
    }

    /**
     * Calls the next {@link RequestSecurityProcessor} of the iterator.
     *
     * @param context the request context
     * @throws Exception
     */
    public void processRequest(RequestContext context) throws Exception {
        if (processorIterator.hasNext()) {
            processorIterator.next().processRequest(context, this);
        }
    }

}
