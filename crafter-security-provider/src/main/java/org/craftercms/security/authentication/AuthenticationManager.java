package org.craftercms.security.authentication;

import org.craftercms.security.exception.AuthenticationException;

/**
 * Manages authentication.
 *
 * @author avasquez
 */
public interface AuthenticationManager {

    /**
     * Authenticates a user.
     *
     * @param tenant    the tenant's name the user profile belongs to
     * @param username  the user's username
     * @param password  the user's password
     *
     * @return the authentication object, which contains the token and the user's profile
     */
    Authentication authenticateUser(String tenant, String username, String password) throws AuthenticationException;

    /**
     * Returns the authentication associated to the given ticket ID
     *
     * @param ticket        the authentication ticket
     * @param reloadProfile if the cached profile should be reloaded
     *
     * @return the authentication object associated to the ticket ID, or null if no authentication was found
     * for the ticket ID (anonymous user)
     */
    Authentication getAuthentication(String ticket, boolean reloadProfile) throws AuthenticationException;

    /**
     * Invalidates the given authentication.
     *
     * @param authentication the authentication to invalidate
     */
    void invalidateAuthentication(Authentication authentication) throws AuthenticationException;

}
