package org.craftercms.profile.social.exceptions;

/**
 * General exception thrown by social media integration related code.
 *
 * @author avasquez
 */
public class SocialMediaIntegrationException extends RuntimeException {

    public SocialMediaIntegrationException(String message) {
        super(message);
    }

    public SocialMediaIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
