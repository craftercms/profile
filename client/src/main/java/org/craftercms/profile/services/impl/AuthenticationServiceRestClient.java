package org.craftercms.profile.services.impl;

import org.craftercms.commons.http.HttpUtils;
import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
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

        return doGetForObject(url, Ticket.class, ticketId);
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

        return doGetForObject(url, PersistentLogin.class, loginId);
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
