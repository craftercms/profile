package org.craftercms.profile.exceptions;

import java.util.Date;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Exception thrown when a request is rejected because of a problem with the access token (not provided, expired or not found).
 *
 * @author avasquez
 */
public class AccessDeniedException extends I10nProfileException {

    private static final String KEY_MISSING_ACCESS_TOKEN_ID = "profile.accessToken.missingAccessTokenId";
    private static final String KEY_EXPIRED_ACCESS_TOKEN = "profile.accessToken.expiredAccessToken";
    private static final String KEY_NO_SUCH_ACCESS_TOKEN = "profile.accessToken.noSuchAccessToken";

    private AccessDeniedException(String key, Object... args) {
        super(key, args);
    }

    public static class MissingAccessToken extends AccessDeniedException {

        public MissingAccessToken() {
            super(KEY_MISSING_ACCESS_TOKEN_ID);
        }
    }

    public static class ExpiredAccessToken extends AccessDeniedException {

        public ExpiredAccessToken(String id, String application, Date expiredOn) {
            super(KEY_EXPIRED_ACCESS_TOKEN, id, application, expiredOn);
        }
    }

    public static class NoSuchAccessToken extends AccessDeniedException {

        public NoSuchAccessToken(String id) {
            super(KEY_NO_SUCH_ACCESS_TOKEN, id);
        }
    }

}
