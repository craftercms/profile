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

/**
 * Thrown if no ticket with a specified ID was found.
 *
 * @author avasquez
 */
public class NoSuchTicketException extends I10nProfileException {

    public static final String KEY = "profile.auth.noSuchTicket";

    public NoSuchTicketException(String ticketId) {
        super(KEY, ticketId);
    }

}