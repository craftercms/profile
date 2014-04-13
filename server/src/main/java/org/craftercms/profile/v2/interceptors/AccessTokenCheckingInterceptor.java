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
package org.craftercms.profile.v2.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.RestConstants;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.craftercms.profile.v2.exceptions.ExpiredAccessTokenException;
import org.craftercms.profile.v2.exceptions.I10nProfileException;
import org.craftercms.profile.v2.exceptions.MissingAccessTokenIdParamException;
import org.craftercms.profile.v2.exceptions.NoSuchAccessTokenIdException;
import org.craftercms.profile.v2.permissions.Application;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Filter that checks that in every call the access token ID is specified. If no access token ID is specified, a 401
 * is returned to the caller. If a token ID is found, and the token's expiration date hasn't been reached, a new
 * {@link org.craftercms.profile.v2.permissions.Application} is created and bound to the current thread.
 *
 * @author avasquez
 */
public class AccessTokenCheckingInterceptor extends HandlerInterceptorAdapter {

    private static final I10nLogger logger = new I10nLogger(AccessTokenCheckingInterceptor.class,
            "crafter.profile.messages.logging");

    public static final String ERROR_KEY_GET_ACCESS_TOKEN_ERROR = "profile.accessToken.getAccessTokenError";

    public static final String LOG_KEY_ACCESS_TOKEN_FOUND =    "profile.accessToken.accessTokenFound";
    public static final String LOG_KEY_APP_BINDING_APP =       "profile.app.bindingApp";
    public static final String LOG_KEY_APP_UNBINDING_APP =     "profile.app.unbindingApp";

    protected AccessTokenRepository tokenRepository;

    @Required
    public void setTokenRepository(AccessTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        AccessToken token = getAccessToken(request);
        Application application = getApplication(token);

        logger.debug(LOG_KEY_APP_BINDING_APP, application, Thread.currentThread().getName());

        Application.setCurrent(application);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        logger.debug(LOG_KEY_APP_UNBINDING_APP, Application.getCurrent(), Thread.currentThread().getName());

        Application.clear();
    }

    protected Application getApplication(AccessToken token) throws I10nProfileException {
        Date now = new Date();

        if (token.getExpiresOn() == null || now.before(token.getExpiresOn())) {
            return new Application(token.getApplication(), token.getTenantPermissions());
        } else {
            throw new ExpiredAccessTokenException(token.getApplication(), token.getExpiresOn());
        }
    }

    protected AccessToken getAccessToken(HttpServletRequest request) throws I10nProfileException {
        String tokenId = request.getParameter(RestConstants.PARAM_ACCESS_TOKEN_ID);

        if (StringUtils.isNotEmpty(tokenId)) {
            AccessToken token;
            try {
                token = tokenRepository.findById(tokenId);
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_GET_ACCESS_TOKEN_ERROR, e, tokenId);
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
