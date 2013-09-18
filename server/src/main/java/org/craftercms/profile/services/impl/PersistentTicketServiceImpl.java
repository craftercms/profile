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

import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.repositories.TicketRepository;
import org.craftercms.profile.security.PersistentTenantRememberMeToken;
import org.craftercms.profile.services.PersistentTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;


@Service("persistentTicketService")
public class PersistentTicketServiceImpl implements PersistentTokenRepository, PersistentTicketService {

    @Autowired
    private TicketRepository ticketRepository;

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#createNewToken(org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken)
     */
    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        ticketRepository.save(new Ticket((PersistentTenantRememberMeToken)token));
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#updateToken(java.lang.String, java.lang.String, java.util.Date)
     */
    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        Ticket ticket = ticketRepository.findBySeries(series);
        ticket.setTokenValue(tokenValue);
        ticket.setDate(lastUsed);
        ticketRepository.save(ticket);
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#getTokenForSeries(java.lang.String)
     */
    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        Ticket ticket = ticketRepository.findBySeries(seriesId);
        return (ticket != null)? ticket.toPersistentRememberMeToken(): null;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#removeUserTokens(java.lang.String)
     */
    @Override
    public void removeUserTokens(String username) {
        ticketRepository.removeUserTickets(username);
    }
}
