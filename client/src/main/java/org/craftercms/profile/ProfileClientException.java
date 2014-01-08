package org.craftercms.profile;

/**
 * Thrown when Error communicating with Profile server.
 */
public class ProfileClientException extends Exception {

    public ProfileClientException(final String message) {
        super(message);
    }

    public ProfileClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProfileClientException(final Throwable cause) {
        super(cause);
    }
}
