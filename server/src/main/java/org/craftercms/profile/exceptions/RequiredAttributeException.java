package org.craftercms.profile.exceptions;

/**
 * Thrown when a required attribute is null or empty.
 *
 * @author avasquez
 */
public class RequiredAttributeException extends AttributeValidationException {

    public static final String MESSAGE_FORMAT = "Attribute '%s' must not be null or empty";

    public RequiredAttributeException(String attributeName) {
        super(String.format(MESSAGE_FORMAT, attributeName));
    }

}
