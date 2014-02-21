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
package org.craftercms.profile.services;

import java.util.Date;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * Persistent ticket services
 * @author Alvaro Gonzalez
 *
 */
public interface PersistentTicketService {

	/**
	 * Creates a new ticket based on the information provides in the PersistentRememberMeToken instance
	 * @param token It has the information will be used to persist the ticket data
	 */
    void createNewToken(final PersistentRememberMeToken token);

    /**
     * Updates the ticket data using the series to find the ticket data and updates the tocken value and the last updated date
     *  
     * @param series Unique value will be used to get the ticket value
     * 
     * @param tokenValue New token value
     * 
     * @param lastUsed The last update date
     */
    void updateToken(String series, String tokenValue, Date lastUsed);

    /**
     * Gets the tickets data based on the unique series passed as a parameter
     * 
     * @param seriesId Unique series data
     * 
     * @return the Ticket data found in the mongo repository
     */
    PersistentRememberMeToken getTokenForSeries(String seriesId);

    /**
     * Removes the ticket from the ticket repository using the username to get the data to be removed
     * 
     * @param username to be deleted
     */
    void removeUserTokens(String username);

}