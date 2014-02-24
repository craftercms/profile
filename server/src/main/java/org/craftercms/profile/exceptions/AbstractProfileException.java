package org.craftercms.profile.exceptions;

/**
 * Created by Carlos on 1/23/14.
 */
public abstract class AbstractProfileException extends Exception {
    protected AbstractProfileException() {
    }

    public AbstractProfileException(final String message) {
        super(message);
    }

    public AbstractProfileException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AbstractProfileException(final Throwable cause) {
        super(cause);
    }

    public AbstractProfileException(final String message, final Throwable cause, final boolean enableSuppression,
                                    final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
