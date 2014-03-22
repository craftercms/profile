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
package org.craftercms.profile.v2.http.filters;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.craftercms.profile.v2.exceptions.ExpiredAccessTokenException;
import org.craftercms.profile.v2.exceptions.InvalidAccessTokenIdException;
import org.craftercms.profile.v2.exceptions.MissingRequiredParameterException;
import org.craftercms.profile.v2.permissions.Application;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Filter that checks that in every call the access token ID is specified. If no access token ID is specified, a 401
 * is returned to the caller. If a token ID is found, and the token's expiration date hasn't been reached, a new
 * {@link org.craftercms.profile.v2.permissions.Application} is created and bound to the current thread.
 *
 * @author avasquez
 */
public class AccessTokenCheckingFilter extends GenericFilterBean {

    private static final I10nLogger logger = new I10nLogger(AccessTokenCheckingFilter.class,
            "crafter.profile.messages.logging");

    public static final String ACCESS_TOKEN_ID_PARAM = "accessTokenId";

    private static final String LOG_KEY_ACCESS_TOKEN_FOUND =    "profile.accessToken.accessTokenFound";
    private static final String LOG_KEY_APP_BINDING_APP =       "profile.app.bindingApp";
    private static final String LOG_KEY_APP_UNBINDING_APP =     "profile.app.unbindingApp";

    protected AccessTokenRepository tokenRepository;
    protected TenantService tenantService;

    @Required
    public void setTokenRepository(AccessTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (Application.getCurrent() == null) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            try {
                AccessToken token = getAccessToken(httpRequest);
                Application application = getApplication(token);
                String threadName = Thread.currentThread().getName();

                logger.debug(LOG_KEY_APP_BINDING_APP, application, threadName);

                Application.setCurrent(application);

                try {
                    chain.doFilter(request, response);
                } finally {
                    logger.debug(LOG_KEY_APP_UNBINDING_APP, application, threadName);

                    Application.clear();
                }
            } catch (MissingRequiredParameterException e) {
                handleAccessTokenParamNotFound(e, httpRequest, httpResponse);
            } catch (InvalidAccessTokenIdException e) {
                handleInvalidAccessTokenId(e, httpRequest, httpResponse);
            } catch (ExpiredAccessTokenException e) {
                handleExpiredAccessToken(e, httpRequest, httpResponse);
            } catch (MongoDataException e) {
                handleGeneralError(e, httpRequest, httpResponse);
            }
        }
    }

    protected Application getApplication(AccessToken token) throws ExpiredAccessTokenException {
        Date now = new Date();

        if (token.getExpiresOn() == null || now.before(token.getExpiresOn())) {
            return new Application(token.getApplication(), token.getTenantPermissions());
        } else {
            throw new ExpiredAccessTokenException(token.getApplication(), token.getExpiresOn());
        }
    }

    protected AccessToken getAccessToken(HttpServletRequest request) throws MissingRequiredParameterException,
            InvalidAccessTokenIdException, MongoDataException {
        String tokenId = request.getParameter(ACCESS_TOKEN_ID_PARAM);

        if (StringUtils.isNotEmpty(tokenId)) {
            AccessToken token = tokenRepository.findById(tokenId);
            if (token != null) {
                logger.debug(LOG_KEY_ACCESS_TOKEN_FOUND, tokenId, token);

                return token;
            } else {
                throw new InvalidAccessTokenIdException(tokenId);
            }
        } else {
            throw new MissingRequiredParameterException(ACCESS_TOKEN_ID_PARAM);
        }
    }

    protected void handleAccessTokenParamNotFound(MissingRequiredParameterException e, HttpServletRequest request,
                                                  HttpServletResponse response) throws IOException {
        logger.error(e.getLocalizedMessage(), e);

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    protected void handleInvalidAccessTokenId(InvalidAccessTokenIdException e, HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        logger.error(e.getLocalizedMessage(), e);

        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    protected void handleExpiredAccessToken(ExpiredAccessTokenException e, HttpServletRequest request,
                                            HttpServletResponse response) throws IOException {
        logger.error(e.getLocalizedMessage(), e);

        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }

    protected void handleGeneralError(Exception e, HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        logger.error(e.getLocalizedMessage(), e);

        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
