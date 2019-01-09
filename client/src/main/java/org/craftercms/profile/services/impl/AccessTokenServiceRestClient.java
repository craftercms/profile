/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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

import java.util.List;

import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AccessTokenService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import static org.craftercms.profile.api.ProfileConstants.BASE_URL_ACCESS_TOKEN;
import static org.craftercms.profile.api.ProfileConstants.URL_ACCESS_TOKEN_CREATE;
import static org.craftercms.profile.api.ProfileConstants.URL_ACCESS_TOKEN_DELETE;
import static org.craftercms.profile.api.ProfileConstants.URL_ACCESS_TOKEN_GET;
import static org.craftercms.profile.api.ProfileConstants.URL_ACCESS_TOKEN_GET_ALL;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.AccessTokenService}.
 *
 * @author avasquez
 */
public class AccessTokenServiceRestClient extends AbstractProfileRestClientBase implements AccessTokenService {

    public static final ParameterizedTypeReference<List<AccessToken>> accessTokenListTypeRef =
        new ParameterizedTypeReference<List<AccessToken>>() {};

    @Override
    public AccessToken createToken(AccessToken token) throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_ACCESS_TOKEN + URL_ACCESS_TOKEN_CREATE);

        return doPostForObject(url, token, AccessToken.class);
    }

    @Override
    public AccessToken getToken(String id) throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_ACCESS_TOKEN + URL_ACCESS_TOKEN_GET);

        try {
            return doGetForObject(url, AccessToken.class, id);
        } catch (ProfileRestServiceException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public List<AccessToken> getAllTokens() throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_ACCESS_TOKEN + URL_ACCESS_TOKEN_GET_ALL);

        return doGetForObject(url, accessTokenListTypeRef);
    }

    @Override
    public void deleteToken(String id) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_ACCESS_TOKEN + URL_ACCESS_TOKEN_DELETE);

        doPostForLocation(url, createBaseParams(), id);
    }

}
