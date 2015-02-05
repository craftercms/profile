package org.craftercms.security.exception.rememberme;

import org.craftercms.security.exception.AuthenticationException;

/**
 * Root exception for remember me related errors.
 *
 * @author avasquez
 */
public class RememberMeException extends AuthenticationException {

    public RememberMeException(String s) {
        super(s);
    }

    public RememberMeException(String s, Throwable throwable) {
        super(s, throwable);
    }
    
}
