package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Throw when account had too much failed attempts.
 */
public class ProfileLockedException extends I10nProfileException {

    public static final String KEY = "profile.auth.lockedAccount";

    public ProfileLockedException() {
        super(KEY);
    }
}
