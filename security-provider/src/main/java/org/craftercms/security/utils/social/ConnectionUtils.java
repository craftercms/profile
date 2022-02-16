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

package org.craftercms.security.utils.social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.CryptoException;
import org.craftercms.commons.crypto.TextEncryptor;
import org.craftercms.profile.api.Profile;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;

/**
 * Utility methods related with connections with providers.
 *
 * @author avasquez
 */
public class ConnectionUtils {

    public static final String CONNECTIONS_ATTRIBUTE_NAME = "connections";
    public static final String FIRST_NAME_ATTRIBUTE_NAME = "firstName";
    public static final String LAST_NAME_ATTRIBUTE_NAME = "lastName";
    public static final String DISPLAY_NAME_ATTRIBUTE_NAME = "displayName";
    public static final String AVATAR_LINK_ATTRIBUTE_NAME = "avatarLink";

    /**
     * Creates a new map from the specified {@link ConnectionData}. Used when
     * connection data needs to be stored in a profile.
     *
     * @param connectionData    the connection data to convert
     * @param encryptor         the encryptor used to encrypt the accessToken, secret and refreshToken (optional)
     *
     * @return the connection data as a map
     */
    public static Map<String, Object> connectionDataToMap(ConnectionData connectionData,
                                                          TextEncryptor encryptor) throws CryptoException {
        Map<String, Object> map = new HashMap<>();
        map.put("providerUserId", connectionData.getProviderUserId());
        map.put("displayName", connectionData.getDisplayName());
        map.put("profileUrl", connectionData.getProfileUrl());
        map.put("imageUrl", connectionData.getImageUrl());
        map.put("accessToken", encrypt(connectionData.getAccessToken(), encryptor));
        map.put("secret", encrypt(connectionData.getSecret(), encryptor));
        map.put("refreshToken", encrypt(connectionData.getRefreshToken(), encryptor));
        map.put("expireTime", connectionData.getExpireTime());

        return map;
    }

    /**
     * Creates a new instance of {@link ConnectionData} from the specified map.
     * Used when connection data needs to be retrieved from a profile.
     *
     * @param providerId    the provider ID of the connection (which is not stored in the map)
     * @param map           the map to convert
     * @param encryptor     the encryptor used to decrypt the accessToken, secret and refreshToken (optional)
     *
     * @return the map as {@link ConnectionData}
     */
    public static ConnectionData mapToConnectionData(String providerId, Map<String, Object> map,
                                                     TextEncryptor encryptor) throws CryptoException {
        String providerUserId = (String) map.get("providerUserId");
        String displayName = (String) map.get("displayName");
        String profileUrl = (String) map.get("profileUrl");
        String imageUrl = (String) map.get("imageUrl");
        String accessToken = decrypt((String)map.get("accessToken"), encryptor);
        String secret = decrypt((String)map.get("secret"), encryptor);
        String refreshToken = decrypt((String)map.get("refreshToken"), encryptor);
        Long expireTime = (Long) map.get("expireTime");

        return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl, accessToken, secret,
            refreshToken, expireTime);
    }

    /**
     * Adds the specified {@link ConnectionData} to the profile. If a connection
     * data with the same user ID already exists, it will be replaced with the new data.
     *
     * @param profile           the profile
     * @param connectionData    the connection data to add
     * @param encryptor         the encryptor used to encrypt the accessToken, secret and refreshToken
     */
    public static void addConnectionData(Profile profile, ConnectionData connectionData,
                                         TextEncryptor encryptor) throws CryptoException {
        Map<String, List<Map<String, Object>>> allConnections = profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);
        List<Map<String, Object>> connectionsForProvider = null;

        if (allConnections == null) {
            allConnections = new HashMap<>();

            profile.setAttribute(CONNECTIONS_ATTRIBUTE_NAME, allConnections);
        } else {
            connectionsForProvider = allConnections.get(connectionData.getProviderId());
        }

        if (connectionsForProvider == null) {
            connectionsForProvider = new ArrayList<>();

            allConnections.put(connectionData.getProviderId(), connectionsForProvider);
        }

        Map<String, Object> currentConnectionDataMap = null;

        for (Map<String, Object> connectionDataMap : connectionsForProvider) {
            if (connectionData.getProviderUserId().equals(connectionDataMap.get("providerUserId"))) {
                currentConnectionDataMap = connectionDataMap;
                break;
            }
        }

        if (currentConnectionDataMap != null) {
            currentConnectionDataMap.putAll(connectionDataToMap(connectionData, encryptor));
        } else {
            connectionsForProvider.add(connectionDataToMap(connectionData, encryptor));
        }
    }

    /**
     * Returns the list of {@link ConnectionData} associated to the provider ID of
     * the specified profile
     *
     * @param profile       the profile that contains the connection data in its attributes
     * @param providerId    the provider ID of the connection
     * @param encryptor     the encryptor used to decrypt the accessToken, secret and refreshToken
     *
     * @return the list of connection data for the provider, or empty if no connection data was found
     */
    public static List<ConnectionData> getConnectionData(Profile profile, String providerId,
                                                         TextEncryptor encryptor) throws CryptoException {
        Map<String, List<Map<String, Object>>> allConnections = profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);

        if (MapUtils.isNotEmpty(allConnections)) {
            List<Map<String, Object>> connectionsForProvider = allConnections.get(providerId);

            if (CollectionUtils.isNotEmpty(connectionsForProvider)) {
                List<ConnectionData> connectionDataList = new ArrayList<>(connectionsForProvider.size());

                for (Map<String, Object> connectionDataMap : connectionsForProvider) {
                    connectionDataList.add(mapToConnectionData(providerId, connectionDataMap, encryptor));
                }

                return connectionDataList;
            }
        }

        return Collections.emptyList();
    }

    /**
     * Remove all {@link ConnectionData} associated to the specified provider ID.
     *
     * @param profile       the profile where to remove the data from
     * @param providerId    the provider ID of the connection
     */
    public static void removeConnectionData(Profile profile, String providerId) {
        Map<String, List<Map<String, Object>>> allConnections = profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);
        if (MapUtils.isNotEmpty(allConnections)) {
            allConnections.remove(providerId);
        }
    }

    /**
     * Remove the {@link ConnectionData} associated to the provider ID and user ID.
     *
     * @param providerId        the provider ID of the connection
     * @param providerUserId    the provider user ID
     * @param profile           the profile where to remove the data from
     */
    public static void removeConnectionData(String providerId, String providerUserId, Profile profile) {
        Map<String, List<Map<String, Object>>> allConnections = profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);

        if (MapUtils.isNotEmpty(allConnections)) {
            List<Map<String, Object>> connectionsForProvider = allConnections.get(providerId);

            if (CollectionUtils.isNotEmpty(connectionsForProvider)) {
                for (Iterator<Map<String, Object>> iter = connectionsForProvider.iterator(); iter.hasNext();) {
                    Map<String, Object> connectionDataMap = iter.next();

                    if (providerUserId.equals(connectionDataMap.get("providerUserId"))) {
                        iter.remove();
                    }
                }
            }
        }
    }

    /**
     * Adds the info from the provider profile to the specified profile.
     *
     * @param profile           the target profile
     * @param providerProfile   the provider profile where to get the info
     */
    public static void addProviderProfileInfo(Profile profile, UserProfile providerProfile) {
        String email = providerProfile.getEmail();
        if (StringUtils.isEmpty(email)) {
            throw new IllegalStateException("No email included in provider profile");
        }

        String username = providerProfile.getUsername();
        if (StringUtils.isEmpty(username)) {
            username = email;
        }

        String firstName = providerProfile.getFirstName();
        String lastName = providerProfile.getLastName();

        profile.setUsername(username);
        profile.setEmail(email);
        profile.setAttribute(FIRST_NAME_ATTRIBUTE_NAME, firstName);
        profile.setAttribute(LAST_NAME_ATTRIBUTE_NAME, lastName);
    }

    /**
     * Creates a profile from the specified connection.
     *
     * @param connection the connection where to retrieve the profile info from
     *
     * @return the created profile
     */
    public static Profile createProfile(Connection<?> connection) {
        Profile profile = new Profile();
        UserProfile providerProfile = connection.fetchUserProfile();

        String email = providerProfile.getEmail();
        if (StringUtils.isEmpty(email)) {
            throw new IllegalStateException("No email included in provider profile");
        }

        String username = providerProfile.getUsername();
        if (StringUtils.isEmpty(username)) {
            username = email;
        }

        String firstName = providerProfile.getFirstName();
        String lastName = providerProfile.getLastName();
        String displayName;

        if (StringUtils.isNotEmpty(connection.getDisplayName())) {
            displayName = connection.getDisplayName();
        } else {
            displayName = firstName + " " + lastName;
        }

        profile.setUsername(username);
        profile.setEmail(email);
        profile.setAttribute(FIRST_NAME_ATTRIBUTE_NAME, firstName);
        profile.setAttribute(LAST_NAME_ATTRIBUTE_NAME, lastName);
        profile.setAttribute(DISPLAY_NAME_ATTRIBUTE_NAME, displayName);

        if (StringUtils.isNotEmpty(connection.getImageUrl())) {
            profile.setAttribute(AVATAR_LINK_ATTRIBUTE_NAME, connection.getImageUrl());
        }

        return profile;
    }

    private static String encrypt(String clear, TextEncryptor encryptor) throws CryptoException {
        return encryptor != null && StringUtils.isNotEmpty(clear) ? encryptor.encrypt(clear) : clear;
    }

    private static String decrypt(String encrypted, TextEncryptor encryptor) throws CryptoException {
        return encryptor != null && StringUtils.isNotEmpty(encrypted) ? encryptor.decrypt(encrypted) : encrypted;
    }

}
