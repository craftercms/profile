/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.security.social.impl;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.CryptoException;
import org.craftercms.commons.crypto.TextEncryptor;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.OAuth2Exception;
import org.craftercms.security.social.ProviderLoginSupport;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.security.utils.social.ConnectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Default implementation of {@link ProviderLoginSupport}. On {@link #complete(String, String, HttpServletRequest)}, if the
 * user data of the provider connection corresponds to an existing Crafter Profile user, the profile connection data
 * will be updated. If a profile doesn't exist, a new one with the connection data will be created. In both cases, the
 * user is automatically authenticated with Crafter Profile.
 *
 * @author avasquez
 */
public class ProviderLoginSupportImpl implements ProviderLoginSupport {

    public static final String PARAM_OAUTH_TOKEN = "oauth_token";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_ERROR_DESCRIPTION = "error_description";
    public static final String PARAM_ERROR_URI = "error_uri";

    protected ConnectSupport connectSupport;
    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected ProfileService profileService;
    protected AuthenticationManager authenticationManager;
    protected TextEncryptor textEncryptor;

    public ProviderLoginSupportImpl() {
        connectSupport = new ConnectSupport();
    }

    public void setConnectSupport(ConnectSupport connectSupport) {
        this.connectSupport = connectSupport;
    }

    @Required
    public void setConnectionFactoryLocator(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Required
    public void setTextEncryptor(TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }

    @Override
    public String start(String tenant, String providerId, HttpServletRequest request) throws AuthenticationException {
        return start(tenant, providerId, request, null, null);
    }
    @Override
    public String start(String tenant, String providerId, HttpServletRequest request,
                        MultiValueMap<String, String> additionalUrlParams) throws AuthenticationException {
        return start(tenant, providerId, request, additionalUrlParams, null);
    }

    @Override
    public String start(String tenant, String providerId, HttpServletRequest request,
                        MultiValueMap<String, String> additionalUrlParams, ConnectSupport connectSupport)
        throws AuthenticationException {
        if (connectSupport == null) {
            connectSupport = this.connectSupport;
        }

        ConnectionFactory<?> connectionFactory = getConnectionFactory(providerId);
        ServletWebRequest webRequest = new ServletWebRequest(request);

        return connectSupport.buildOAuthUrl(connectionFactory, webRequest, additionalUrlParams);
    }

    @Override
    public Authentication complete(String tenant, String providerId,
                                   HttpServletRequest request) throws AuthenticationException {
        return complete(tenant, providerId, request, null, null, null);
    }

    @Override
    public Authentication complete(String tenant, String providerId, HttpServletRequest request,
                                   Set<String> newUserRoles, Map<String, Object> newUserAttributes)
        throws AuthenticationException {
        return complete(tenant, providerId, request, newUserRoles, newUserAttributes, null);
    }

    @Override
    public Authentication complete(String tenant, String providerId, HttpServletRequest request,
                                   Set<String> newUserRoles, Map<String, Object> newUserAttributes,
                                   ConnectSupport connectSupport) throws AuthenticationException {
        if (connectSupport == null) {
            connectSupport = this.connectSupport;
        }

        Connection<?> connection = completeConnection(connectSupport, providerId, request);
        if (connection != null) {
            Profile userData = ConnectionUtils.createProfile(connection);
            Profile profile = getProfile(tenant, userData);

            if (profile == null) {
                if (CollectionUtils.isNotEmpty(newUserRoles)) {
                    userData.getRoles().addAll(newUserRoles);
                }
                if (MapUtils.isNotEmpty(newUserAttributes)) {
                    userData.getAttributes().putAll(newUserAttributes);
                }

                profile = createProfile(tenant, connection, userData);
            } else {
                profile = updateProfileConnectionData(tenant, connection, profile);
            }

            Authentication auth = authenticationManager.authenticateUser(profile);
            SecurityUtils.setAuthentication(request, auth);

            return auth;
        } else {
            return null;
        }
    }

    protected Connection<?> completeConnection(ConnectSupport connectSupport, String providerId,
                                               HttpServletRequest request) throws OAuth2Exception {
        if (StringUtils.isNotEmpty(request.getParameter(PARAM_OAUTH_TOKEN))) {
            OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>)getConnectionFactory(providerId);
            ServletWebRequest webRequest = new ServletWebRequest(request);

            return connectSupport.completeConnection(connectionFactory, webRequest);
        } else if (StringUtils.isNotEmpty(request.getParameter(PARAM_CODE))) {
            OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>)getConnectionFactory(providerId);
            ServletWebRequest webRequest = new ServletWebRequest(request);

            return connectSupport.completeConnection(connectionFactory, webRequest);
        } else if (StringUtils.isNotEmpty(request.getParameter(PARAM_ERROR))) {
            String error = request.getParameter(PARAM_ERROR);
            String errorDescription = request.getParameter(PARAM_ERROR_DESCRIPTION);
            String errorUri = request.getParameter(PARAM_ERROR_URI);

            throw new OAuth2Exception(error, errorDescription, errorUri);
        } else {
            return null;
        }
    }

    protected ConnectionFactory<?> getConnectionFactory(String providerId) {
        return connectionFactoryLocator.getConnectionFactory(providerId);
    }

    protected Profile getProfile(String tenant, Profile userData) {
        try {
            return profileService.getProfileByUsername(tenant, userData.getUsername());
        } catch (ProfileException e) {
            throw new AuthenticationException("Unable to retrieve current profile for user '" +
                                              userData.getUsername() + "' of tenant '" + tenant + "'", e);
        }
    }

    protected Profile createProfile(String tenant, Connection<?> connection, Profile userData) {
        try {
            ConnectionUtils.addConnectionData(userData, connection.createData(), textEncryptor);

            return profileService.createProfile(tenant, userData.getUsername(), null, userData.getEmail(), true,
                                                userData.getRoles(), userData.getAttributes(), null);
        } catch (CryptoException | ProfileException e) {
            throw new AuthenticationException("Unable to create profile of user '" + userData.getUsername() +
                                              "' in tenant '" + tenant + "'", e);
        }
    }

    protected Profile updateProfileConnectionData(String tenant, Connection<?> connection, Profile profile) {
        try {
            ConnectionUtils.addConnectionData(profile, connection.createData(), textEncryptor);

            return profileService.updateAttributes(profile.getId().toString(), profile.getAttributes());
        } catch (CryptoException | ProfileException e) {
            throw new AuthenticationException("Unable to update connection data of user '" + profile.getUsername() +
                                              "' of tenant '" + tenant + "'", e);
        }
    }

}
