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
package org.craftercms.profile.services.impl;

import java.net.URI;

import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.rest.AbstractRestClientBase;
import org.craftercms.commons.rest.RestServiceException;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.exceptions.ErrorDetails;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
        HttpUtils.addValue(ProfileConstants.PARAM_ACCESS_TOKEN_ID, accessTokenIdResolver.getAccessTokenId(),
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

    protected <T> T doPostForUpload(String url, MultiValueMap<String, Object> request, Class<T> responseType,
                                    Object... uriVariables) throws ProfileException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(request, headers);

            return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType, uriVariables).getBody();
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

    protected <T> T doGetForObject(URI url, Class<T> responseType) throws ProfileException {
        try {
            return restTemplate.getForObject(url, responseType);
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
            return restTemplate.exchange(url, HttpMethod.GET, null, responseType, uriVariables).getBody();
        } catch (RestServiceException e) {
            handleRestServiceException(e);
        } catch (Exception e) {
            handleException(e);
        }

        return null;
    }

    protected <T> T doGetForObject(URI url, ParameterizedTypeReference<T> responseType) throws ProfileException {
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
        HttpStatus responseStatus = e.getResponseStatus();
        Object details = e.getErrorDetails();
        if (details instanceof ErrorDetails) {
            ErrorDetails errorDetails = (ErrorDetails) details;
            ErrorCode errorCode = errorDetails.getErrorCode();
            String message = errorDetails.getMessage();

            throw new ProfileRestServiceException(responseStatus, errorCode, message);
        } else {
            String message = details != null? details.toString() : e.getMessage();

            throw new ProfileRestServiceException(responseStatus, message);
        }
    }

    protected void handleException(Exception e) throws ProfileException {
        throw new ProfileException(e.getMessage(), e);
    }

}
