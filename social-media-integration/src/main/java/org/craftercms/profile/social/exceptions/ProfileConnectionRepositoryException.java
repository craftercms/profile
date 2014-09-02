package org.craftercms.profile.social.exceptions;

import org.springframework.social.connect.ConnectionRepositoryException;

/**
 * Thrown when an error occurs in {@link org.craftercms.profile.social.ProfileConnectionRepository} or {@link org
 * .craftercms.profile.social.ProfileUsersConnectionRepository}
 *
 * @author avasquez
 */
public class ProfileConnectionRepositoryException extends ConnectionRepositoryException {

    public ProfileConnectionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
