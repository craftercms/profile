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

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.exceptions.TicketException;
import org.springframework.stereotype.Repository;

/**
 * Defines all persistence operations for a Ticket.
 */
@Repository("ticketRepository")
public interface TicketRepository extends CrudRepository<Ticket> {
    /**
     * Finds the ticket for the given username.
     *
     * @param username Username which ticket will be search.
     * @return Current valid ticket for the given user,<b>Null</b> if nothing is found.
     */
    Ticket findByUsername(final String username) throws TicketException;

    /**
     * Finds the Ticket for the given series.
     *
     * @param series Series to search ticket.
     * @return Current Valid Ticket for the given series ,<b>Null</b> if nothing is found.
     */
    Ticket findBySeries(final String series) throws TicketException;

    /**
     * Removes all tickets for the given username.
     *
     * @param username Username to delete all tickets.
     */
    void removeUserTickets(final String username) throws TicketException;

    /**
     * Removes all tickets older that the date calculated.
     *
     * @param expirationSeconds Time in milliseconds to calculate the expiration Date/time of a ticket.
     */
    void removeTicketsOlderThan(final long expirationSeconds) throws TicketException;

    /**
     * Gets the ticket by its string representation.
     *
     * @param ticketStr String representation of a Ticket.
     * @return Ticket with the given representation,<b>Null</b> if ticket with given representation couldn't be found.§
     */
    Ticket findByTicket(final String ticketStr) throws TicketException;
}
