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

import org.craftercms.commons.http.HttpUtils;
import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import static org.craftercms.profile.api.ProfileConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
public class AuthenticationServiceRestClient extends AbstractProfileRestClientBase implements AuthenticationService {

    @Override
    public Ticket authenticate(String tenantName, String username, String password) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        HttpUtils.addValue(PARAM_USERNAME, username, params);
        HttpUtils.addValue(PARAM_PASSWORD, password, params);

        String url = getAbsoluteUrl(BASE_URL_AUTHENTICATION + URL_AUTH_AUTHENTICATE);

        return doPostForObject(url, params, Ticket.class);
    }

    @Override
    public Ticket createTicket(String profileId) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_PROFILE_ID, profileId, params);

        String url = getAbsoluteUrl(BASE_URL_AUTHENTICATION + URL_AUTH_CREATE_TICKET);

        return doPostForObject(url, params, Ticket.class);
    }

    @Override
    public Ticket getTicket(String ticketId) throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_AUTHENTICATION + URL_AUTH_GET_TICKET);

        try {
            return doGetForObject(url, Ticket.class, ticketId);
        } catch (ProfileRestServiceException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public void invalidateTicket(String ticketId) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_AUTHENTICATION + URL_AUTH_INVALIDATE_TICKET);

        doPostForLocation(url, createBaseParams(), ticketId);
    }

    @Override
    public PersistentLogin createPersistentLogin(String profileId) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        HttpUtils.addValue(PARAM_PROFILE_ID, profileId, params);

        String url = getAbsoluteUrl(BASE_URL_AUTHENTICATION + URL_AUTH_CREATE_PERSISTENT_LOGIN);

        return doPostForObject(url, params, PersistentLogin.class);
    }

    @Override
    public PersistentLogin getPersistentLogin(String loginId) throws ProfileException {
        String url = getAbsoluteUrlWithAccessTokenIdParam(BASE_URL_AUTHENTICATION + URL_AUTH_GET_PERSISTENT_LOGIN);

        try {
            return doGetForObject(url, PersistentLogin.class, loginId);
        } catch (ProfileRestServiceException e) {
            if (e.getStatus() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public PersistentLogin refreshPersistentLoginToken(String loginId) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_AUTHENTICATION + URL_AUTH_REFRESH_PERSISTENT_LOGIN_TOKEN);

        return doPostForObject(url, createBaseParams(), PersistentLogin.class, loginId);
    }

    @Override
    public void deletePersistentLogin(String loginId) throws ProfileException {
        String url = getAbsoluteUrl(BASE_URL_AUTHENTICATION + URL_AUTH_DELETE_PERSISTENT_LOGIN);

        doPostForLocation(url, createBaseParams(), loginId);
    }

}
