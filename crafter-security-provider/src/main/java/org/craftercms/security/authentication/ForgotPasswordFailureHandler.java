package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.exception.CrafterSecurityException;

public interface ForgotPasswordFailureHandler {

    /**
     * Handles the forgot password request failed
     *
     * @param e       the exception that caused the forgot password  to fail.
     * @param context the request context
     */
    void onForgotPasswordFailure(Exception e, RequestContext context) throws CrafterSecurityException, IOException;
}
