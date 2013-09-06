/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.profile.security;

import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.util.StringUtils;

public class PersistentParameterTokenRememberMeServices extends PersistentTokenBasedRememberMeServices {
    private PersistentTokenRepository tokenRepository = new InMemoryTokenRepositoryImpl();

    public PersistentParameterTokenRememberMeServices(String key, UserDetailsService userDetailsService,
                                                      PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
        // Do nothing
        logger.debug("cancelCookie");

        String tokenStr = extractRememberMeCookie(request);

        if (tokenStr != null) {
            String[] cookieTokens = decodeCookie(tokenStr);

            if (cookieTokens.length >= 2) {
                final String presentedSeries = cookieTokens[0];
                final String presentedToken = cookieTokens[1];

                PersistentRememberMeToken token = tokenRepository.getTokenForSeries(presentedSeries);

                if (token != null && presentedToken.equals(token.getTokenValue())) {
                    tokenRepository.removeUserTokens(token.getUsername());
                }
            }
        }

        request.removeAttribute(getCookieName());
    }

    @Override
    protected String extractRememberMeCookie(HttpServletRequest request) {
        logger.debug(String.format("extractRememberMeCookie: %s = %s", getCookieName(),
            request.getParameter(getCookieName())));
        String token = request.getParameter(getCookieName());
        return (token == null || token.length() == 0)? null: token;
    }

    @Override
    protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {

        request.setAttribute(getCookieName(), encodeCookie(tokens));

        logger.debug(String.format("setCookie('%s' maxAge='%d' encodedCookie='%s', request, response)",
            StringUtils.arrayToCommaDelimitedString(tokens), maxAge, encodeCookie(tokens)));
    }

    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                                 HttpServletResponse response) {

        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, " +
                "but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        PersistentTenantRememberMeToken token = (PersistentTenantRememberMeToken)tokenRepository.getTokenForSeries
            (presentedSeries);

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        if (!presentedToken.equals(token.getTokenValue())) {
            // Token doesn't match series value. Delete all logins for this user
            // and throw an exception to warn them.
            tokenRepository.removeUserTokens(token.getUsername());

            throw new CookieTheftException(messages.getMessage("PersistentTokenBasedRememberMeServices.cookieStolen",
                "Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
        }

        if (token.getDate().getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }

        // Token also matches, so login is valid. Update the token value,
        // keeping the *same* series number.
        if (logger.isDebugEnabled()) {
            logger.debug("Refreshing persistent login token for user '" + token.getUsername() + "', " +
                "series '" + token.getSeries() + "'");
        }

        try {
            tokenRepository.updateToken(token.getSeries(), token.getTokenValue(), new Date());
            setCookie(new String[] {token.getSeries(), token.getTokenValue()}, getTokenValiditySeconds(), request,
                response);
        } catch (DataAccessException e) {
            logger.error("Failed to update token: ", e);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
        }

        return getUserDetailsService().loadUserByUsername(token.getTenantName() == null || token.getTenantName()
            .equals("")? token.getUsername(): token.getUsername() + "@" + token.getTenantName());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            cancelCookie(request, response);
        }
    }


    /**
     * Creates a new persistent login token with a new series number, stores the data in the
     * persistent token repository and adds the corresponding cookie to the response.
     */
    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
                                  Authentication successfulAuthentication) {
        String username = successfulAuthentication.getName();
        String tenantName = request.getParameter("tenantName");
        username = username.trim();

        logger.debug("Creating new persistent login for user " + username);

        PersistentTenantRememberMeToken persistentToken = new PersistentTenantRememberMeToken(username,
            generateSeriesData(), generateTokenData(), new Date(), tenantName);
        try {
            tokenRepository.createNewToken(persistentToken);

            setCookie(new String[] {persistentToken.getSeries(), persistentToken.getTokenValue()}, getTokenValiditySeconds(), request, response);
        } catch (DataAccessException e) {
            logger.error("Failed to save persistent token ", e);
        }
    }

}
