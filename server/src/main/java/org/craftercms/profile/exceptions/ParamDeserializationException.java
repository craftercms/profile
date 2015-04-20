package org.craftercms.profile.exceptions;

import org.craftercms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when the attributes sent as JSON in a param can't be correctly deserialized.
 *
 * @author avasquez
 */
public class ParamDeserializationException extends I10nProfileException {

    public static final String KEY = "profile.attribute.deserializationError";

    public ParamDeserializationException(Throwable cause) {
        super(KEY, cause);
    }

}
