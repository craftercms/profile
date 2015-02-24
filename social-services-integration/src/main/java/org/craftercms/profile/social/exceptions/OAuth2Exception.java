package org.craftercms.profile.social.exceptions;

/**
 * Exception used when a OAuth2 provider returns an error on a login attempt. See http://tools.ietf
 * .org/html/rfc6749#section-4.1.2.1 for details on the error params returned.
 *
 * @author avasquez
 */
public class OAuth2Exception extends SocialMediaIntegrationException {

    private String error;
    private String errorDescription;
    private String errorUri;

    public OAuth2Exception(final String error, final String errorDescription, final String errorUri) {
        super("[" + error + "] " + errorDescription);

        this.error = error;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorUri() {
        return errorUri;
    }

}
