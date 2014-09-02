package org.craftercms.profile.social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.craftercms.commons.crypto.CryptoException;
import org.craftercms.commons.crypto.TextEncryptor;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.social.exceptions.ProfileConnectionRepositoryException;
import org.craftercms.profile.social.utils.TenantResolver;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.craftercms.profile.social.utils.ConnectionUtils.*;

/**
 * Implementation of {@link org.springframework.social.connect.ConnectionRepository} that uses a profile to persist
 * the connection information. If the profile is null while adding a connection, a new profile is created from the
 * connection information.
 *
 * @author avasquez
 */
public class ProfileConnectionRepository implements ConnectionRepository {

    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected Profile profile;
    protected ProfileService profileService;
    protected TenantResolver tenantResolver;
    protected TextEncryptor encryptor;

    public ProfileConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator, Profile profile,
                                       ProfileService profileService, TextEncryptor encryptor) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.profile = profile;
        this.profileService = profileService;
        this.encryptor = encryptor;
    }

    public ProfileConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator,
                                       ProfileService profileService, TenantResolver tenantResolver,
                                       TextEncryptor encryptor) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.profileService = profileService;
        this.tenantResolver = tenantResolver;
        this.encryptor = encryptor;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public MultiValueMap<String, Connection<?>> findAllConnections() {
        if (profile == null) {
            return new LinkedMultiValueMap<>();
        }

        MultiValueMap<String, Connection<?>> allConnections = new LinkedMultiValueMap<>();
        Map<String, List<Map<String, Object>>> allConnectionData = getConnectionsAttribute(profile);

        if (MapUtils.isNotEmpty(allConnectionData)) {
            for (Map.Entry<String, List<Map<String, Object>>> entry : allConnectionData.entrySet()) {
                String providerId = entry.getKey();

                for (Map<String, Object> connectionDataMap : entry.getValue()) {
                    ConnectionData connectionData;
                    try {
                        connectionData = mapToConnectionData(providerId, connectionDataMap, encryptor);
                    } catch (CryptoException e) {
                        throw new ProfileConnectionRepositoryException("Decryption error", e);
                    }

                    Connection<?> connection = createConnection(connectionData);

                    allConnections.add(providerId, connection);
                }
            }
        }

        return allConnections;
    }

    @Override
    public List<Connection<?>> findConnections(String providerId) {
        if (profile == null) {
            return Collections.emptyList();
        }

        List<Connection<?>> connections = new ArrayList<>();
        List<ConnectionData> connectionDataList;

        try {
            connectionDataList = getConnectionData(profile, providerId, encryptor);
        } catch (CryptoException e) {
            throw new ProfileConnectionRepositoryException("Decryption error", e);
        }

        if (CollectionUtils.isNotEmpty(connectionDataList)) {
            for (ConnectionData connectionData : connectionDataList) {
                connections.add(createConnection(connectionData));
            }
        }

        return connections;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType));

        return (List<Connection<A>>) connections;
    }

    @Override
    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
        if (profile == null) {
            return new LinkedMultiValueMap<>();
        }

        MultiValueMap<String, Connection<?>> connectionsForUserIds = new LinkedMultiValueMap<>();

        for (Map.Entry<String, List<String>> entry : providerUserIds.entrySet()) {
            String providerId = entry.getKey();
            List<ConnectionData> connectionDataList;

            try {
                connectionDataList = getConnectionData(profile, providerId, encryptor);
            } catch (CryptoException e) {
                throw new ProfileConnectionRepositoryException("Decryption error", e);
            }

            for (String providerUserId : entry.getValue()) {
                Connection<?> connection = findConnection(connectionDataList, providerUserId);
                connectionsForUserIds.add(providerId, connection);
            }
        }

        return connectionsForUserIds;
    }

    @Override
    public Connection<?> getConnection(ConnectionKey connectionKey) {
        if (profile == null) {
            throw new NoSuchConnectionException(connectionKey);
        }

        String providerId = connectionKey.getProviderId();
        List<ConnectionData> connectionDataList;

        try {
            connectionDataList = getConnectionData(profile, providerId, encryptor);
        } catch (CryptoException e) {
            throw new ProfileConnectionRepositoryException("Decryption error", e);
        }

        Connection<?> connection = findConnection(connectionDataList, connectionKey.getProviderUserId());
        if (connection != null) {
            return connection;
        } else {
            throw new NoSuchConnectionException(connectionKey);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        return (Connection<A>) getConnection(new ConnectionKey(getProviderId(apiType), providerUserId));
    }

    @Override
    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        Connection<A> connection = findPrimaryConnection(apiType);
        if (connection != null) {
            return connection;
        } else {
            throw new NotConnectedException(getProviderId(apiType));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        if (profile == null) {
            return null;
        }

        String providerId = getProviderId(apiType);
        List<Connection<?>> connections = findConnections(providerId);

        if (CollectionUtils.isNotEmpty(connections)){
            return (Connection<A>) connections.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void addConnection(Connection<?> connection) {
        if (profile == null) {
            profile = getProfileFromConnection(connection);

            Profile existingProfile = findProfileByUsername(profile.getTenant(), profile.getUsername());
            if (existingProfile != null) {
                profile = existingProfile;

                try {
                    addConnectionData(profile, connection.createData(), encryptor);
                } catch (CryptoException e) {
                    throw new ProfileConnectionRepositoryException("Encryption error", e);
                }

                updateProfile();
            } else {
                createProfile();
            }
        } else {
            try {
                addConnectionData(profile, connection.createData(), encryptor);
            } catch (CryptoException e) {
                throw new ProfileConnectionRepositoryException("Encryption error", e);
            }

            updateProfile();
        }
    }

    @Override
    public void updateConnection(Connection<?> connection) {
        if (profile == null) {
            throw new IllegalStateException("Missing profile in connection repository");
        }

        try {
            addConnectionData(profile, connection.createData(), encryptor);
        } catch (CryptoException e) {
            throw new ProfileConnectionRepositoryException("Encryption error", e);
        }

        updateProfile();
    }

    @Override
    public void removeConnections(String providerId) {
        if (profile != null) {
            removeConnectionData(profile, providerId);
            updateProfile();
        }
    }

    @Override
    public void removeConnection(ConnectionKey connectionKey) {
        if (profile != null) {
            String providerId = connectionKey.getProviderId();
            String providerUserId = connectionKey.getProviderUserId();

            removeConnectionData(providerId, providerUserId, profile);
            updateProfile();
        }
    }
    
    protected Connection<?> createConnection(ConnectionData data) {
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(data.getProviderId());

        return connectionFactory.createConnection(data);
    }

    protected Connection<?> findConnection(List<ConnectionData> connectionDataList, String providerUserId) {
        if (CollectionUtils.isNotEmpty(connectionDataList)) {
            for (ConnectionData connectionData : connectionDataList) {
                if (connectionData.getProviderUserId().equals(providerUserId)) {
                    return createConnection(connectionData);
                }
            }
        }

        return null;
    }

    protected Map<String, List<Map<String, Object>>> getConnectionsAttribute(Profile profile) {
        return profile.getAttribute(CONNECTIONS_ATTRIBUTE_NAME);
    }

    protected <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }

    protected void createProfile() {
        String tenant = profile.getTenant();
        boolean enabled = profile.isEnabled();
        String username = profile.getUsername();
        String email = profile.getEmail();
        Map<String, Object> attributes = profile.getAttributes();

        try {
            profile = profileService.createProfile(tenant, username, null, email, enabled, null, attributes, null);
        } catch (ProfileException e) {
            throw new ProfileConnectionRepositoryException("Unable to create profile for user '" + username + "'", e);
        }
    }

    protected Profile getProfileFromConnection(Connection<?> connection) {
        Profile profile = new Profile();
        profile.setTenant(tenantResolver.getCurrentTenant());
        profile.setEnabled(true);

        addProviderProfileInfo(profile, connection.fetchUserProfile());

        try {
            addConnectionData(profile, connection.createData(), encryptor);
        } catch (CryptoException e) {
            throw new ProfileConnectionRepositoryException("Encryption error", e);
        }

        return profile;
    }

    protected void updateProfile() {
        try {
            profile = profileService.updateAttributes(profile.getId().toString(), profile.getAttributes());
        } catch (ProfileException e) {
            throw new ProfileConnectionRepositoryException("Unable to update profile of user '" + profile
                .getUsername() + "'", e);
        }
    }

    protected Profile findProfileByUsername(String tenant, String username) {
        try {
            return profileService.getProfileByUsername(tenant, username);
        } catch (ProfileException e) {
            throw new ProfileConnectionRepositoryException("Unable to find profile for user '" + username +
                " of tenant '" + tenant + "'", e);
        }
    }

}
