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

import java.util.Date;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when the access token has already expired.
 *
 * @author avasquez
 */
public class ExpiredAccessTokenException extends I10nProfileException {

    private static final String KEY = "profile.accessToken.expiredAccessToken";

    public ExpiredAccessTokenException(String id, String application, Date expiredOn) {
        super(KEY, id, application, expiredOn);
    }

}
