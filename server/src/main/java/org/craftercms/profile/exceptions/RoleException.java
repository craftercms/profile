package org.craftercms.profile.exceptions;

/**
 * Thrown when a Role Operation error happened.
 *
 * @author Carlos Ortiz.
 */
public class RoleException extends AbstractProfileException {

    private static final long serialVersionUID = -2495205339397178280L;

    public RoleException() {
    }

    public RoleException(final String message) {
        super(message);
    }

    public RoleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RoleException(final Throwable cause) {
        super(cause);
    }

    public RoleException(final String message, final Throwable cause, final boolean enableSuppression,
                         final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
