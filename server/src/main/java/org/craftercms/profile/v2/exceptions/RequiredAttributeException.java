package org.craftercms.profile.v2.exceptions;

/**
 * Thrown when a required attribute is null or empty.
 *
 * @author avasquez
 */
public class RequiredAttributeException extends AttributeFilterException {

    public static final String MESSAGE_FORMAT = "Attribute '%s' must not be null or empty";

    public RequiredAttributeException(String attributeName) {
        super(String.format(MESSAGE_FORMAT, attributeName));
    }

}
