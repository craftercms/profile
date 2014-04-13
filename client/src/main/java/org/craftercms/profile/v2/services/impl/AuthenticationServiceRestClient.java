package org.craftercms.profile.v2.services.impl;

import org.craftercms.commons.rest.RestClientUtils;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.craftercms.profile.api.RestConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
public class AuthenticationServiceRestClient extends ProfileRestClientBase implements AuthenticationService {

    public static final String DEFAULT_EXTENSION = ".json";

    protected String extension;
    protected RestTemplate restTemplate;

    public AuthenticationServiceRestClient() {
        this.extension = DEFAULT_EXTENSION;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Required
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
        String url = BASE_URL_AUTHENTICATION + URL_AUTH_GET_TICKET + extension;

        doPostForLocation(url, createBaseParams(), ticketId);
    }

}
