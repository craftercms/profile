package org.craftercms.profile.services.impl;

import org.craftercms.commons.rest.RestClientUtils;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.springframework.util.MultiValueMap;

import static org.craftercms.profile.api.ProfileConstants.BASE_URL_AUTHENTICATION;
import static org.craftercms.profile.api.ProfileConstants.PARAM_PASSWORD;
import static org.craftercms.profile.api.ProfileConstants.PARAM_TENANT_NAME;
import static org.craftercms.profile.api.ProfileConstants.PARAM_USERNAME;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_AUTHENTICATE;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_GET_TICKET;
import static org.craftercms.profile.api.ProfileConstants.URL_AUTH_INVALIDATE_TICKET;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
public class AuthenticationServiceRestClient extends AbstractProfileRestClientBase implements AuthenticationService {

    @Override
    public Ticket authenticate(String tenantName, String username, String password) throws ProfileException {
        MultiValueMap<String, String> params = createBaseParams();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValue(PARAM_PASSWORD, password, params);

        String url = getAbsoluteUrl(BASE_URL_AUTHENTICATION + URL_AUTH_AUTHENTICATE);

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

}
