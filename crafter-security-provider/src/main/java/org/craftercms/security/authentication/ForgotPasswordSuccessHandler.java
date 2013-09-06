package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.CrafterSecurityException;

public interface ForgotPasswordSuccessHandler {

	/**
     * Handles the request after a successful forgot password request.
     *
     * @param profile the user profile
     * @param context the request context
     */

    void onForgotPasswordSuccess(UserProfile profile, RequestContext context) throws CrafterSecurityException,
        IOException;

}
