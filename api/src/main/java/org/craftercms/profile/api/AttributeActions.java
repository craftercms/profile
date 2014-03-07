package org.craftercms.profile.api;

/**
 * Actions that can be executed on attributes by applications.
 *
 * @author avasquez
*/
public class AttributeActions {

    public static final String READ =   "read";
    public static final String WRITE =  "write";

    public static final String[] ALL_ACTIONS = { READ, WRITE };

    private AttributeActions() {
    }

}
