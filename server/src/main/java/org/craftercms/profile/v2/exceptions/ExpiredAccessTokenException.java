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
package org.craftercms.profile.v2.exceptions;

import org.craftercms.profile.api.exceptions.ProfileException;

import java.util.Date;

/**
 * Thrown when the access token has already expired.
 *
 * @author avasquez
 */
public class ExpiredAccessTokenException extends ProfileException {

    public static final String MESSAGE_FORMAT = "Access token for application '%s' and tenant '%s' expired on %tD";

    public ExpiredAccessTokenException(String application, String tenant, Date expiredOn) {
        super(String.format(MESSAGE_FORMAT, application, tenant, expiredOn));
    }

}
