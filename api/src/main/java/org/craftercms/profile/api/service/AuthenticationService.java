package org.craftercms.profile.api.service;

/**
 * Service for handling authentication for users.
 *
 * @author avasquez
 */
public interface AuthenticationService {

    /**
     * Authenticates the user, and returns a ticket identifying the authentication.
     *
     * @param tenant    the user's tenant's name
     * @param username  the username
     * @param password  the password
     *
     * @return the ticket
     */
    String authenticate(String tenant, String username, String password);

    /**
     * Returns if the ticket is still valid, basically that it corresponds to an authenticated user and that it
     * hasn't expired
     *
     * @param ticket the ticket to validate
     *
     * @return true if the ticket corresponds to an authenticated user and that it hasn't expired
     */
    boolean isTicketValid(String ticket);

    /**
     * Invalidates the ticket.
     *
     * @param ticket    the ticket to invalidate
     */
    void invalidateTicket(String ticket);

}
