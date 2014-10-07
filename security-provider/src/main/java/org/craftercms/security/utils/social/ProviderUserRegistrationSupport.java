package org.craftercms.security.utils.social;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.crypto.TextEncryptor;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.social.utils.ConnectionUtils;
import org.springframework.social.connect.Connection;

/**
 * Helper class that eases registration for user profiles obtained from providers (Facebook, Twitter, etc.)
 */
public class ProviderUserRegistrationSupport {

    public static final String SESSION_ATTRIBUTE = "providerUserRegistrationSupport";

    private Profile profile;
    private ProfileService profileService;

    public static ProviderUserRegistrationSupport fromConnection(Connection<?> connection, TextEncryptor encryptor,
                                                                 ProfileService profileService) {
        Profile profile = ConnectionUtils.getProfileFromConnection(connection, encryptor);

        return new ProviderUserRegistrationSupport(profile, profileService);
    }

    public ProviderUserRegistrationSupport(Profile profile, ProfileService profileService) {
        this.profile = profile;
        this.profileService = profileService;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile finishRegistration() throws ProfileException {
        return finishRegistration(null);
    }

    public Profile finishRegistration(String verificationUrl) throws ProfileException {
        String tenant = profile.getTenant();
        String username = profile.getPassword();
        String password = profile.getPassword();
        String email = profile.getEmail();
        boolean enabled = profile.isEnabled();
        Set<String> roles = profile.getRoles();
        Map<String, Object> attributes = profile.getAttributes();

        if (StringUtils.isEmpty(username)) {
            username = email;
        }

        profile = profileService.createProfile(tenant, username, password, email, enabled,
            roles, attributes, verificationUrl);

        return profile;
    }

}
