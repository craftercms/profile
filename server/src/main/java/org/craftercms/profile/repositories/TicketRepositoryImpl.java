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
package org.craftercms.profile.repositories;

import java.sql.Date;

import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.security.util.TicketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component("ticketRepositoryImpl")
public class TicketRepositoryImpl implements TicketRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Ticket getByTicket(String ticketStr) {
        String series = TicketUtils.getTicketSeries(ticketStr);
        Ticket ticket = null;
        if (series != null) {
            Query q = new Query(Criteria.where("_id").is(series));
            q.fields().include("username");
            q.fields().include("tenantName");
            ticket = (Ticket)mongoTemplate.findOne(q, Ticket.class);

        }
        return ticket;
    }

    @Override
    public void removeUserTickets(String username) {
        mongoTemplate.remove(Query.query(Criteria.where("username").is(username)), Ticket.class);
    }

    @Override
    public void removeTicketsOlderThan(long expirationSeconds) {
        Date expireBefore = new Date(System.currentTimeMillis() - (expirationSeconds * 1000));
        mongoTemplate.remove(Query.query(Criteria.where("date").lt(expireBefore)), Ticket.class);
    }
}
