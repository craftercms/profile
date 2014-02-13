package org.craftercms.profile.exceptions;

/**
 * Thrown when there is a Tenant Operation Error
 */
public class TenantException extends AbstractProfileException {

    private static final long serialVersionUID = 2482344813938364874L;

    public TenantException() {
    }

    public TenantException(final String message) {
        super(message);
    }

    public TenantException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TenantException(final Throwable cause) {
        super(cause);
    }

    public TenantException(final String message, final Throwable cause, final boolean enableSuppression,
                           final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
