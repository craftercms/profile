package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.CrafterSecurityException;

public interface CreateProfileSuccessHandler {
	
	/**
     * Handles the request after a successful create profile request.
     *
     * @param profile the user profile
     * @param context the request context
     */
    void onCreateProfileSuccess(UserProfile profile, RequestContext context) throws CrafterSecurityException,
        IOException;

}
