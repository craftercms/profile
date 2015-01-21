package org.craftercms.security.authentication;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.exception.rememberme.RememberMeException;

/**
 * Manages remember me functionality.
 *
 * @author avasquez
 */
public interface RememberMeManager {

    /**
     * Attempts auto login if a remember me cookie is present in the current request
     *
     * @param context the request context
     *
     * @return the authentication if auto login was successful
     */
    Authentication autoLogin(RequestContext context) throws RememberMeException;

    /**
     * Enables remember me for the current authenticated profile, generally by adding a remember me cookie.
     *
     * @param authentication    the authentication object
     * @param context           the request context
     */
    void enableRememberMe(Authentication authentication, RequestContext context) throws RememberMeException;

    /**
     * Disabled remember me for the current authenticated profile, generally by removing remember me cookie.
     *
     * @param context           the request context
     */
    void disableRememberMe(RequestContext context) throws RememberMeException;

}
