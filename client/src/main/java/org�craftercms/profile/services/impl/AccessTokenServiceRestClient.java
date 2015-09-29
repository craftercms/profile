package org.craftercms.profile.services.impl;

import java.util.List;

import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AccessTokenService;
import org.springframework.core.ParameterizedTypeReference;

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

        return doGetForObject(url, AccessToken.class, id);
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
