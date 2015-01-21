package org.craftercms.security.exception.rememberme;

/**
 * Thrown when a remember me cookie provided has an invalid format.
 * 
 * @author avasquez
 */
public class InvalidCookieException extends RememberMeException {

    public InvalidCookieException(String s) {
        super(s);
    }

    public InvalidCookieException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
