package org.craftercms.profile.v2.exceptions;

/**
 * Thrown when an application doesn't have the permissions to set a new user attribute.
 *
 * @author avasquez
 */
public class AttributeWriteNotAllowedException extends AttributeProcessorException {

    public static final String MESSAGE_FORMAT = "The application is not allowed to write attribute '%s'";

    public AttributeWriteNotAllowedException(String attributeName) {
        super(String.format(MESSAGE_FORMAT, attributeName));
    }

}
