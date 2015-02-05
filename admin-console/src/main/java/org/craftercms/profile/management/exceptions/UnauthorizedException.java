package org.craftercms.profile.management.exceptions;

/**
 * {@link java.lang.RuntimeException} thrown when the current user is not authorized to do a certain action or
 * request a resource.
 *
 * @author avasquez
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

}
