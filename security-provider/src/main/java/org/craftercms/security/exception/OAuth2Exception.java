/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.security.exception;

/**
 * Exception used when a OAuth2 provider returns an error on a login attempt. See http://tools.ietf
 * .org/html/rfc6749#section-4.1.2.1 for details on the error params returned.
 *
 * @author avasquez
 */
public class OAuth2Exception extends AuthenticationException {

    private String error;
    private String errorDescription;
    private String errorUri;

    public OAuth2Exception(final String error, final String errorDescription, final String errorUri) {
        super("[" + error + "] " + errorDescription);

        this.error = error;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorUri() {
        return errorUri;
    }

}
