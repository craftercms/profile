package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.exception.CrafterSecurityException;

public interface ResetPasswordFailureHandler {
    /**
     * Handles the reset password request failed
     *
     * @param e       the exception that caused the reset password  to fail.
     * @param context the request context
     */
    void onResetPasswordFailure(Exception e, RequestContext context, String token) throws CrafterSecurityException,
        IOException;
}
