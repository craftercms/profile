package org.craftercms.profile.v2.services.impl;

import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.v2.utils.rest.RestClientUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.craftercms.profile.api.RestConstants.*;

/**
 * REST client implementation of {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
public class AuthenticationServiceRestClient implements AuthenticationService {

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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        RestClientUtils.addValue(PARAM_TENANT_NAME, tenantName, params);
        RestClientUtils.addValue(PARAM_USERNAME, username, params);
        RestClientUtils.addValue(PARAM_PASSWORD, password, params);

        String url = BASE_URL_AUTHENTICATION + URL_AUTH_AUTHENTICATE + extension;

        return restTemplate.postForObject(url, params, Ticket.class);
    }

    @Override
    public Ticket getTicket(String ticketId) throws ProfileException {
        String url = BASE_URL_AUTHENTICATION + URL_AUTH_GET_TICKET + extension;

        return restTemplate.getForObject(url, Ticket.class, ticketId);
    }

    @Override
    public void invalidateTicket(String ticketId) throws ProfileException {
        String url = BASE_URL_AUTHENTICATION + URL_AUTH_GET_TICKET + extension;

        restTemplate.postForLocation(url, null, ticketId);
    }

}