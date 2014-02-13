package org.craftercms.profile.exceptions;

/**
 *
 */
public class ProfileException extends AbstractProfileException {


    protected ProfileException() {
        super();
    }

    public ProfileException(final String message) {
        super(message);
    }

    public ProfileException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProfileException(final Throwable cause) {
        super(cause);
    }

    public ProfileException(final String message, final Throwable cause, final boolean enableSuppression,
                            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
