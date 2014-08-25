package org.craftercms.profile.api.services;

import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;

/**
 * Service for handling authentication for users.
 *
 * @author avasquez
 */
public interface AuthenticationService {

    /**
     * Authenticates the user, and returns a ticket identifying the authentication.
     *
     * @param tenantName    the tenant's name
     * @param username      the username
     * @param password      the password
     *
     * @return the ticket
     */
    Ticket authenticate(String tenantName, String username, String password) throws ProfileException;

    /**
     * Create a new ticket for the specified profile.
     *
     * <p>
     *     <strong>Note: </strong> this method should only be used when authentication is done through other means
     *     (like when authenticating through Facebook or Twitter) different than profile.
     * </p>
     *
     * @param profileId the ID of the profile
     *
     * @return the ticket
     */
    Ticket createTicket(String profileId) throws ProfileException;

    /**
     * Returns the ticket object for the given ticket ID.
     *
     * @param ticketId      the ID of the ticket
     *
     * @return the ticket object, or null if no ticket found or ticket has expired
     */
    Ticket getTicket(String ticketId) throws ProfileException;

    /**
     * Invalidates the ticket.
     *
     * @param ticketId      the ID of the ticket to invalidate
     */
    void invalidateTicket(String ticketId) throws ProfileException;

}
