package org.craftercms.profile.social.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.craftercms.profile.api.Profile;
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

    /**
     * Creates a new map from the specified {@link org.springframework.social.connect.ConnectionData}. Used when
     * connection data needs to be stored in a profile.
     *
     * @param connectionData the connection data to convert
     *
     * @return the connection data as a map
     */
    public static Map<String, Object> connectionDataToMap(ConnectionData connectionData) {
        Map<String, Object> map = new HashMap<>();
        map.put("providerUserId", connectionData.getProviderUserId());
        map.put("displayName", connectionData.getDisplayName());
        map.put("profileUrl", connectionData.getProfileUrl());
        map.put("imageUrl", connectionData.getImageUrl());
        map.put("accessToken", connectionData.getAccessToken());
        map.put("secret", connectionData.getSecret());
        map.put("refreshToken", connectionData.getRefreshToken());
        map.put("expireTime", connectionData.getExpireTime());

        return map;
    }

    /**
     * Creates a new instance of {@link org.springframework.social.connect.ConnectionData} from the specified map.
     * Used when connection data needs to be retrieved from a profile.
     *
     * @param providerId    the provider ID of the connection (which is not stored in the map)
     * @param map           the map to convert
     *
     * @return the map as {@link org.springframework.social.connect.ConnectionData}
     */
    public static ConnectionData mapToConnectionData(String providerId, Map<String, Object> map) {
        String providerUserId = (String) map.get("providerUserId");
        String displayName = (String) map.get("displayName");
        String profileUrl = (String) map.get("profileUrl");
        String imageUrl = (String) map.get("imageUrl");
        String accessToken = (String) map.get("accessToken");
        String secret = (String) map.get("secret");
        String refreshToken = (String) map.get("refreshToken");
        Long expireTime = (Long) map.get("expireTime");

        return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl,
            accessToken, secret,refreshToken, expireTime);
    }

    /**
     * Adds the specified {@link org.springframework.social.connect.ConnectionData} to the profile. If a connection
     * data with the same user ID already exists, it will be replaced with the new data.
     *
     * @param profile           the profile
     * @param connectionData    the connection data to add
     */
    public static void addConnectionData(Profile profile, ConnectionData connectionData) {
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
            currentConnectionDataMap.putAll(connectionDataToMap(connectionData));
        } else {
            connectionsForProvider.add(connectionDataToMap(connectionData));
        }
    }

    /**
     * Returns the list of {@link org.springframework.social.connect.ConnectionData} associated to the provider ID of
     * the specified profile
     *
     * @param profile       the profile that contains the connection data in its attributes
     * @param providerId    the provider ID of the connection
     *
     * @return the list of connection data for the provider, or empty if no connection data was found
     */
    public static List<ConnectionData> getConnectionData(Profile profile, String providerId) {
        Map<String, List<Map<String, Object>>> allConnections = profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);

        if (MapUtils.isNotEmpty(allConnections)) {
            List<Map<String, Object>> connectionsForProvider = allConnections.get(providerId);

            if (CollectionUtils.isNotEmpty(connectionsForProvider)) {
                List<ConnectionData> connectionDataList = new ArrayList<>(connectionsForProvider.size());

                for (Map<String, Object> connectionDataMap : connectionsForProvider) {
                    connectionDataList.add(mapToConnectionData(providerId, connectionDataMap));
                }

                return connectionDataList;
            }
        }

        return Collections.emptyList();
    }

    /**
     * Remove all {@link org.springframework.social.connect.ConnectionData} associated to the specified provider ID.
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
     * Remove the {@link org.springframework.social.connect.ConnectionData} associated to the provider ID and user ID.
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
        profile.setUsername(providerProfile.getUsername());
        profile.setEmail(providerProfile.getEmail());
        profile.setAttribute(FIRST_NAME_ATTRIBUTE_NAME, providerProfile.getFirstName());
        profile.setAttribute(LAST_NAME_ATTRIBUTE_NAME, providerProfile.getLastName());
    }

}
