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
package org.craftercms.profile.services.impl;

import org.craftercms.commons.rest.AbstractRestClientBase;
import org.craftercms.commons.rest.RestClientUtils;
import org.craftercms.commons.rest.RestServiceException;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.exceptions.ErrorDetails;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * Base class for all Profile REST clients.
 *
 * @author avasquez
 */
public abstract class AbstractProfileRestClientBase extends AbstractRestClientBase {

    protected AccessTokenIdResolver accessTokenIdResolver;

    @Required
    public void setAccessTokenIdResolver(AccessTokenIdResolver accessTokenIdResolver) {
        this.accessTokenIdResolver = accessTokenIdResolver;
    }

    protected String getAbsoluteUrlWithAccessTokenIdParam(String relativeUrl) {
        String absoluteUrl = getAbsoluteUrl(relativeUrl);
        String accessTokenId = accessTokenIdResolver.getAccessTokenId();

        if (accessTokenId != null) {
            if (absoluteUrl.contains("?")) {
                return absoluteUrl + "&" + ProfileConstants.PARAM_ACCESS_TOKEN_ID + "=" + accessTokenId;
            } else {
                return absoluteUrl + "?" + ProfileConstants.PARAM_ACCESS_TOKEN_ID + "=" + accessTokenId;
            }
        } else {
            return absoluteUrl;
        }
    }

    protected MultiValueMap<String, String> createBaseParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(ProfileConstants.PARAM_ACCESS_TOKEN_ID, accessTokenIdResolver.getAccessTokenId(),
                params);

        return params;
    }


    protected <T> T doPostForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
            throws ProfileException {
        try {
            return restTemplate.postForObject(url, request, responseType, uriVariables);
        } catch (RestServiceException e) {
            handleRestServiceException(e);
        } catch (Exception e) {
            handleException(e);
        }

        return null;
    }

    protected URI doPostForLocation(String url, Object request, Object... uriVariables) throws ProfileException {
        try {
            return restTemplate.postForLocation(url, request, uriVariables);
        } catch (RestServiceException e) {
            handleRestServiceException(e);
        } catch (Exception e) {
            handleException(e);
        }

        return null;
    }

    protected <T> T doGetForObject(String url, Class<T> responseType, Object... uriVariables) throws ProfileException {
        try {
            return restTemplate.getForObject(url, responseType, uriVariables);
        } catch (RestServiceException e) {
            handleRestServiceException(e);
        } catch (Exception e) {
            handleException(e);
        }

        return null;
    }

    protected <T> T doGetForObject(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables)
            throws ProfileException {
        try {
            return restTemplate.exchange(url, HttpMethod.GET, null, responseType).getBody();
        } catch (RestServiceException e) {
            handleRestServiceException(e);
        } catch (Exception e) {
            handleException(e);
        }

        return null;
    }

    protected void handleRestServiceException(RestServiceException e) throws ProfileException {
        if (e.getErrorDetails() instanceof ErrorDetails) {
            ErrorDetails errorDetails = (ErrorDetails) e.getErrorDetails();
            HttpStatus responseStatus = e.getResponseStatus();
            ErrorCode errorCode = errorDetails.getErrorCode();
            String message = errorDetails.getMessage();

            throw new ProfileRestServiceException(responseStatus, errorCode, message);
        } else {
            HttpStatus responseStatus = e.getResponseStatus();
            String message = e.getErrorDetails().toString();

            throw new ProfileRestServiceException(responseStatus, message);
        }
    }

    protected void handleException(Exception e) throws ProfileException {
        throw new ProfileException(e.getMessage(), e);
    }

}
