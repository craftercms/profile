package org.craftercms.profile.exceptions;

/**
 * Thrown when a GroupRole operation fails.
 *
 * @author Carlos Ortiz.
 */
public class GroupRoleException extends ProfileException {

    public GroupRoleException() {
    }

    public GroupRoleException(final String message) {
        super(message);
    }

    public GroupRoleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GroupRoleException(final Throwable cause) {
        super(cause);
    }

    public GroupRoleException(final String message, final Throwable cause, final boolean enableSuppression,
                              final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
