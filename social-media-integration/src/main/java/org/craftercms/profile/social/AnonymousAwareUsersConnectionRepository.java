package org.craftercms.profile.social;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * Extension of {@link org.springframework.social.connect.UsersConnectionRepository} that allows to create
 * {@link org.springframework.social.connect.ConnectionRepository}s for anonymous users.
 *
 * @author avasquez
 */
public interface AnonymousAwareUsersConnectionRepository extends UsersConnectionRepository {

    /**
     * Creates a {@link org.springframework.social.connect.ConnectionRepository} for an anonymous user.
     */
    ConnectionRepository createConnectionRepository();

}
