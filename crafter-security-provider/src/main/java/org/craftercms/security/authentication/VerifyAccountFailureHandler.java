package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.exception.CrafterSecurityException;

public interface VerifyAccountFailureHandler {
    /**
     * Handles the verify account request failed
     *
     * @param e       the exception that caused the verify account  to fail.
     * @param context the request context
     */
    void onVerifyAccountFailure(Exception e, RequestContext context, String token) throws CrafterSecurityException,
        IOException;
}
