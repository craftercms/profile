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
package org.craftercms.profile.v2.utils.rest;

import org.craftercms.commons.rest.HttpMessageConvertingResponseErrorHandler;
import org.craftercms.profile.api.exceptions.ErrorInfo;
import org.craftercms.profile.v2.exceptions.ProfileClientException;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * {@link org.craftercms.commons.rest.HttpMessageConvertingResponseErrorHandler} for Profile's
 * {@link org.craftercms.profile.api.exceptions.ErrorInfo}.
 *
 * @author avasquez
 */
public class ProfileResponseErrorHandler extends HttpMessageConvertingResponseErrorHandler<ErrorInfo> {

    @Override
    protected Class<? extends ErrorInfo> getErrorResponseType() {
        return ErrorInfo.class;
    }

    @Override
    protected void handleErrorInternal(ErrorInfo errorObject, ClientHttpResponse response) throws IOException {
        throw new ProfileClientException(response.getStatusCode(), errorObject.getErrorCode(), errorObject.getMessage());
    }

}
