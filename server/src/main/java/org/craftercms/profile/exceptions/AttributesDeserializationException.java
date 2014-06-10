package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when the attributes sent as JSON in a param can't be correctly deserialized.
 *
 * @author avasquez
 */
public class AttributesDeserializationException extends I10nProfileException {

    public static final String KEY = "profile.attribute.deserializationError";

    public AttributesDeserializationException(Throwable cause) {
        super(KEY, cause);
    }

}
