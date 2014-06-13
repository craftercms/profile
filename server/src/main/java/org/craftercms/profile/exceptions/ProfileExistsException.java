package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a create profile operation fails because a profile with the same tenant and username already exists.
 *
 * @author avasquez
 */
public class ProfileExistsException extends I10nProfileException {

    public static final String KEY = "profile.profile.profileExists";

    public ProfileExistsException(String tenantName, String username) {
        super(KEY, username, tenantName);
    }

}
