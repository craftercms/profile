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
package org.craftercms.profile.interceptors;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.exceptions.ExpiredAccessTokenException;
import org.craftercms.profile.exceptions.MissingAccessTokenIdParamException;
import org.craftercms.profile.exceptions.NoSuchAccessTokenIdException;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.craftercms.profile.services.impl.AccessTokenServiceImpl;
import org.craftercms.profile.utils.AccessTokenUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Filter that checks that in every call the access token ID is specified. If no access token ID is specified, a 401
 * is returned to the caller. If a token ID is found, and the token's expiration date hasn't been reached, the access
 * token is bound to the current request.
 *
 * @author avasquez
 */
public class AccessTokenCheckingInterceptor extends HandlerInterceptorAdapter {

    private static final I10nLogger logger = new I10nLogger(AccessTokenCheckingInterceptor.class,
                                                            "crafter.profile.messages.logging");

    public static final String LOG_KEY_ACCESS_TOKEN_FOUND = "profile.accessToken.accessTokenFound";

    protected AccessTokenRepository accessTokenRepository ;
    protected String[] urlsToInclude;

    @Required
    public void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }

    @Required
    public void setUrlsToInclude(final String[] urlsToInclude) {
        this.urlsToInclude = urlsToInclude;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (includeRequest(request)) {
            AccessToken token = getAccessToken(request);
            Date now = new Date();

            if (token.getExpiresOn() == null || now.before(token.getExpiresOn())) {
                AccessTokenUtils.setAccessToken(request, token);
            } else {
                throw new ExpiredAccessTokenException(token.getId(), token.getApplication(), token.getExpiresOn());
            }
        }

        return true;
    }

    protected boolean includeRequest(HttpServletRequest request) {
        if (ArrayUtils.isNotEmpty(urlsToInclude)) {
            for (String pathPattern : urlsToInclude) {
                if (HttpUtils.getRequestUriWithoutContextPath(request).matches(pathPattern)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected AccessToken getAccessToken(HttpServletRequest request) throws ProfileException {
        String tokenId = request.getParameter(ProfileConstants.PARAM_ACCESS_TOKEN_ID);

        if (StringUtils.isNotEmpty(tokenId)) {
            AccessToken token;
            try {
                token = accessTokenRepository.findByStringId(tokenId);
            } catch (MongoDataException e) {
                throw new I10nProfileException(AccessTokenServiceImpl.ERROR_KEY_GET_ACCESS_TOKEN_ERROR, e, tokenId);
            }

            if (token != null) {
                logger.debug(LOG_KEY_ACCESS_TOKEN_FOUND, tokenId, token);

                return token;
            } else {
                throw new NoSuchAccessTokenIdException(tokenId);
            }
        } else {
            throw new MissingAccessTokenIdParamException();
        }
    }

}
