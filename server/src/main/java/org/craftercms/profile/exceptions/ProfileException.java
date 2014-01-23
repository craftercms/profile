package org.craftercms.profile.exceptions;

/**
 * Created by Carlos on 1/23/14.
 */
public abstract class ProfileException extends Exception {
    protected ProfileException() {
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
