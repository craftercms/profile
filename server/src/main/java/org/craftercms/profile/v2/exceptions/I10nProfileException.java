package org.craftercms.profile.v2.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.i10n.I10nUtils;
import org.craftercms.profile.api.exceptions.ProfileException;

import java.util.ResourceBundle;

/**
 * Localized version of {@link org.craftercms.profile.api.exceptions.ProfileException}. Follows the same strategy
 * as {@link org.craftercms.commons.i10n.I10nException}.
 *
 * @author avasquez
 */
public class I10nProfileException extends ProfileException {

    public static final String ERROR_BUNDLE_NAME = "crafter.profile.messages.errors";

    protected Object[] args;

    public I10nProfileException() {
    }

    public I10nProfileException(String key, Object... args) {
        super(key);

        this.args = args;
    }

    public I10nProfileException(String key, Throwable cause, Object... args) {
        super(key, cause);

        this.args = args;
    }

    public I10nProfileException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        String key = getMessage();
        if (StringUtils.isNotEmpty(key)) {
            return I10nUtils.getLocalizedMessage(getResourceBundle(), key, args);
        } else {
            return null;
        }
    }

    protected ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(ERROR_BUNDLE_NAME);
    }

}
