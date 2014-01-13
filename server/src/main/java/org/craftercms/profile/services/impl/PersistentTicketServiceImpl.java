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
package org.craftercms.profile.services.impl;

import java.util.Date;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.exceptions.TicketException;
import org.craftercms.profile.repositories.TicketRepository;
import org.craftercms.profile.security.PersistentTenantRememberMeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

/**
 * Crafter Profile PersistentTokenRepository implementation.
 */
@Service("persistentTicketService")
public class PersistentTicketServiceImpl implements PersistentTokenRepository {

    private Logger log = LoggerFactory.getLogger(PersistentTicketServiceImpl.class);
    @Autowired
    private TicketRepository ticketRepository;

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#createNewToken(org
     * .springframework.security.web.authentication.rememberme.PersistentRememberMeToken)
     */
    @Override
    public void createNewToken(final PersistentRememberMeToken token) {

        try {
            ticketRepository.save(new Ticket((PersistentTenantRememberMeToken)token));
        } catch (MongoDataException e) {
            log.error("Unable to save Ticket", e);
            throw new TicketException("Unable to create ticket", e);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#updateToken(java
     * .lang.String, java.lang.String, java.util.Date)
     */
    @Override
    public void updateToken(final String series, final String tokenValue, final Date lastUsed) {
        try {
            Ticket ticket = ticketRepository.findBySeries(series);
            if (ticket == null) {
                throw new TicketException("Ticket for series " + series + " not found");
            }
            ticket.setTokenValue(tokenValue);
            ticket.setDate(lastUsed);
            ticketRepository.save(ticket);
        } catch (MongoDataException e) {
            log.error("Unable to update ticket ", e);
            throw new TicketException("Unable to update given ticket", e);
        }

    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#getTokenForSeries
     * (java.lang.String)
     */
    @Override
    public PersistentRememberMeToken getTokenForSeries(final String seriesId) {
        Ticket ticket;
        try {
            ticket = ticketRepository.findBySeries(seriesId);
        } catch (MongoDataException e) {
            log.error("Unable to find ticket ", e);
            throw new TicketException("Unable to find ticket", e);
        }
        if (ticket == null) {
            log.debug("Ticket {} not found", seriesId);
            return null;
        } else {
            return ticket.toPersistentRememberMeToken();
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#removeUserTokens
     * (java.lang.String)
     */
    @Override
    public void removeUserTokens(final String username) {
        ticketRepository.removeUserTickets(username);
    }
}
