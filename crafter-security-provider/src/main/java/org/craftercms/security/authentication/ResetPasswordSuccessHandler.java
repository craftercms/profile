package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.CrafterSecurityException;

public interface ResetPasswordSuccessHandler {
	/**
     * Handles the request after a successful reset password request.
     *
     * @param profile the user profile
     * @param context the request context
     */
    void onResetPasswordSuccess(UserProfile profile, RequestContext context) throws CrafterSecurityException,
        IOException;
}
