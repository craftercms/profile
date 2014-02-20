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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.craftercms.commons.crypto.CipherUtils;
import org.craftercms.commons.crypto.SimpleCipher;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.v2.exceptions.ExpiredAccessTokenException;
import org.craftercms.profile.v2.exceptions.InvalidAccessTokenParamException;
import org.craftercms.profile.v2.exceptions.MissingRequiredParameterException;
import org.craftercms.profile.v2.exceptions.NoSuchTenantException;
import org.craftercms.profile.v2.permissions.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.GenericFilterBean;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Date;

/**
 * Filter that checks that in every call the {@link org.craftercms.profile.api.AccessToken} is specified as a
 * parameter. If no access token is specified, a 401 is returned to the caller. If a token is found, a new
 * {@link org.craftercms.profile.v2.permissions.Application} is created and set as the context of the current
 * thread.
 *
 * @author avasquez
 */
public class AccessTokenCheckingFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenCheckingFilter.class);

    public static final String ACCESS_TOKEN_PARAM = "accessToken";
    public static final String ACCESS_TOKEN_PARAM_SEP =   "|";

    protected ObjectMapper jsonMapper;
    protected Key paramEncryptionKey;
    protected TenantService tenantService;

    public AccessTokenCheckingFilter() {
        jsonMapper = new ObjectMapper();
    }

    @Required
    public void setParamEncryptionKey(String paramEncryptionKey) {
        byte[] keyBytes = Base64.decodeBase64(paramEncryptionKey);

        this.paramEncryptionKey = new SecretKeySpec(keyBytes, CipherUtils.AES_CIPHER_ALGORITHM);
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

                Application.setCurrent(application);

                try {
                    chain.doFilter(request, response);
                } finally {
                    Application.clear();
                }
            } catch (MissingRequiredParameterException e) {
                handleAccessTokenParamNotFound(e, httpRequest, httpResponse);
            } catch (InvalidAccessTokenParamException e) {
                handleInvalidAccessTokenParam(e, httpRequest, httpResponse);
            } catch (ExpiredAccessTokenException e) {
                handleExpiredAccessToken(e, httpRequest, httpResponse);
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
            InvalidAccessTokenParamException {
        String encryptedParam = getEncryptedAccessTokenParam(request);
        String[] encryptedTokenAndIv = StringUtils.split(encryptedParam, ACCESS_TOKEN_PARAM_SEP);

        if (encryptedTokenAndIv.length != 2) {
            throw new InvalidAccessTokenParamException("Expected format is ENCRYPTED_TOKEN" + ACCESS_TOKEN_PARAM_SEP + "IV");
        }

        SimpleCipher cipher = new SimpleCipher();
        cipher.setKey(paramEncryptionKey);
        cipher.setBase64Iv(encryptedTokenAndIv[1]);

        String serializedToken;
        try {
            serializedToken = cipher.decryptBase64ToString(encryptedTokenAndIv[0]);
        } catch (GeneralSecurityException e) {
            throw new InvalidAccessTokenParamException("Unable to decrypt token: " + e.getMessage(), e);
        }

        AccessToken accessToken;
        try {
            accessToken = jsonMapper.readValue(serializedToken, AccessToken.class);
        } catch (IOException e) {
            throw new InvalidAccessTokenParamException("Unable to deserialize token: " + e.getMessage(), e);
        }

        return accessToken;
    }

    protected String getEncryptedAccessTokenParam(HttpServletRequest request) throws MissingRequiredParameterException {
        String encryptedParam = request.getParameter(ACCESS_TOKEN_PARAM);
        if (encryptedParam != null) {
            return encryptedParam;
        } else {
            throw new MissingRequiredParameterException(ACCESS_TOKEN_PARAM);
        }
    }

    protected void handleAccessTokenParamNotFound(MissingRequiredParameterException e, HttpServletRequest request,
                                                  HttpServletResponse response) throws IOException {
        logger.error(e.getMessage(), e);

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    protected void handleInvalidAccessTokenParam(InvalidAccessTokenParamException e, HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {
        logger.error(e.getMessage(), e);

        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    protected void handleExpiredAccessToken(ExpiredAccessTokenException e, HttpServletRequest request,
                                            HttpServletResponse response) throws IOException {
        logger.error(e.getMessage(), e);

        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }

    protected void handleNoSuchTenant(NoSuchTenantException e, HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        logger.error(e.getMessage(), e);

        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

}
