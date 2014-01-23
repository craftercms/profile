package org.craftercms.profile.exceptions;

/**
 * Exception Thrown where is not possible to execute a Ticket related operation.
 * <p>Since ticket operations are backbone of the application execution of any flow should be suspended
 * if there is a problem with the ticket (persistence of it).</p>
 */
public class TicketException extends ProfileException {

    private static final long serialVersionUID = -8791745852807299843L;

    public TicketException(final String message, final Exception cause) {
        super(message, cause);
    }

    public TicketException(final String message) {
        super(message);
    }
}
