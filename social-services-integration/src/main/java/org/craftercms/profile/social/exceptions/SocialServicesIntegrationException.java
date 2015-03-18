package org.craftercms.profile.social.exceptions;

/**
 * General exception thrown by social services integration related code.
 *
 * @author avasquez
 */
public class SocialServicesIntegrationException extends RuntimeException {

    public SocialServicesIntegrationException(String message) {
        super(message);
    }

    public SocialServicesIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
