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
package org.craftercms.profile.repositories.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.repositories.TicketRepository;

/**
 * Default implementation of {@link org.craftercms.profile.repositories.TicketRepository}.
 *
 * @author avasquez
 */
public class TicketRepositoryImpl extends AbstractJongoRepository<Ticket> implements TicketRepository {

    public static final String KEY_REMOVE_WITH_LAST_REQUEST_TIME_OLDER_THAN_QUERY = "profile.ticket" +
        ".removeWithLastRequestTimeOlderThan";

    @Override
    public void removeWithLastRequestTimeOlderThan(long seconds) throws MongoDataException {
        long millis = TimeUnit.SECONDS.toMillis(seconds);
        Date limit = new Date(System.currentTimeMillis() - millis);

        remove(getQueryFor(KEY_REMOVE_WITH_LAST_REQUEST_TIME_OLDER_THAN_QUERY), limit);
    }

}
