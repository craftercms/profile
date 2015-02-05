package org.craftercms.security.exception.rememberme;

/**
 * Thrown when a remember me cookie has been possibly stolen and is being used.
 *
 * @author avasquez
 */
public class CookieTheftException extends RememberMeException {

    public CookieTheftException(String s) {
        super(s);
    }

    public CookieTheftException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
