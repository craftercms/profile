package org.craftercms.profile.social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.TextEncryptor;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.social.exceptions.SocialMediaIntegrationException;
import org.craftercms.profile.social.utils.ConnectionUtils;
import org.craftercms.profile.social.utils.TenantsResolver;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * Implementation of {@link org.springframework.social.connect.UsersConnectionRepository} that uses Crafter Profile
 * for connection persistence.
 *
 * @author avasquez
 */
public class ProfileUsersConnectionRepository implements UsersConnectionRepository {

    public static final String FIND_PROFILES_BY_PROVIDER_ID_AND_USER_ID_QUERY = "{attributes." + ConnectionUtils
        .CONNECTIONS_ATTRIBUTE_NAME + ".%s.providerUserId: {$in: [%s]}}";

    protected ProfileService profileService;
    protected TenantsResolver tenantsResolver;
    protected ConnectionFactoryLocator connectionFactoryLocator;
    protected TextEncryptor encryptor;

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setTenantsResolver(TenantsResolver tenantsResolver) {
        this.tenantsResolver = tenantsResolver;
    }

    @Required
    public void setConnectionFactoryLocator(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    @Required
    public void setEncryptor(TextEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        String providerId = connection.getKey().getProviderId();
        String providerUserId = connection.getKey().getProviderUserId();
        String query = String.format(FIND_PROFILES_BY_PROVIDER_ID_AND_USER_ID_QUERY, providerId, providerUserId);
        List<Profile> profiles = findProfilesByQuery(query);

        if (CollectionUtils.isNotEmpty(profiles)) {
            List<String> ids = new ArrayList<>(profiles.size());

            for (Profile profile : profiles) {
                ids.add(profile.getId().toString());
            }

            return ids;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        String providerUserIdsStr = StringUtils.join(providerUserIds, ", ");
        String query = String.format(FIND_PROFILES_BY_PROVIDER_ID_AND_USER_ID_QUERY, providerId, providerUserIdsStr);
        List<Profile> profiles = findProfilesByQuery(query);

        if (CollectionUtils.isNotEmpty(profiles)) {
            Set<String> ids = new LinkedHashSet<>(profiles.size());

            for (Profile profile : profiles) {
                ids.add(profile.getId().toString());
            }

            return ids;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        Profile profile = findProfile(userId);
        if (profile != null) {
            return new ProfileConnectionRepository(connectionFactoryLocator, profile, profileService, encryptor);
        } else {
            return null;
        }
    }

    public ConnectionRepository createConnectionRepository(Profile profile) {
        return new ProfileConnectionRepository(connectionFactoryLocator, profile, profileService, encryptor);
    }

    protected List<Profile> findProfilesByQuery(String query) {
        String[] tenants = tenantsResolver.getTenants();
        List<Profile> result = new ArrayList<>();

        for (String tenant : tenants) {
            List<Profile> profiles = findProfilesByQuery(tenant, query);
            if (CollectionUtils.isNotEmpty(profiles)) {
                result.addAll(profiles);
            }
        }

        return result;
    }

    protected List<Profile> findProfilesByQuery(String tenant, String query) {
        try {
            return profileService.getProfilesByQuery(tenant, query, null, null, null, null,
                                                     ProfileConstants.NO_ATTRIBUTE);
        } catch (ProfileException e) {
            throw new SocialMediaIntegrationException("Unable to find profiles of tenant '" + tenant + "' by " +
                                                      "query " + query, e);
        }
    }

    protected Profile findProfile(String profileId) {
        try {
            return profileService.getProfile(profileId);
        } catch (ProfileException e) {
            throw new SocialMediaIntegrationException("Unable to find profile '" + profileId + "'", e);
        }
    }

}
