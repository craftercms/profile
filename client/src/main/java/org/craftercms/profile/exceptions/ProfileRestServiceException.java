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
package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.springframework.http.HttpStatus;

/**
 * {@link org.craftercms.profile.api.exceptions.ProfileException} used by clients to indicate a REST service error.
 *
 * @author avasquez
 */
public class ProfileRestServiceException extends ProfileException {

    protected HttpStatus status;
    protected ErrorCode errorCode;
    protected String detailMessage;

    public ProfileRestServiceException(HttpStatus status, String detailMessage) {
        super("status = " + status + ", detailMessage = " + detailMessage);

        this.status = status;
        this.detailMessage = detailMessage;
    }

    public ProfileRestServiceException(HttpStatus status, ErrorCode errorCode, String detailMessage) {
        super("status = " + status + ", errorCode = " + errorCode + ", detailMessage = " + detailMessage);

        this.status = status;
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

}
