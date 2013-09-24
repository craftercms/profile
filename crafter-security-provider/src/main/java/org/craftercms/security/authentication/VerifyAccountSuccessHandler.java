package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.CrafterSecurityException;

public interface VerifyAccountSuccessHandler {
	/**
     * Handles the request after a successful verify account request.
     *
     * @param profile the user profile
     * @param context the request context
     */
    void onVerifyAccountSuccess(UserProfile profile, RequestContext context) throws CrafterSecurityException,
        IOException;
}
