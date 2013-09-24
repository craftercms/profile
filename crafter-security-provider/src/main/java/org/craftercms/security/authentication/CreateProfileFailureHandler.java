package org.craftercms.security.authentication;

import java.io.IOException;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.exception.CrafterSecurityException;

public interface CreateProfileFailureHandler {
	
	/**
     * Handles the create profile request failed
     *
     * @param e       the exception that caused the create profile  to fail.
     * @param 		  context the request context
     */
    void onCreateProfileFailure(Exception e, RequestContext context) throws CrafterSecurityException,
        IOException;

}
