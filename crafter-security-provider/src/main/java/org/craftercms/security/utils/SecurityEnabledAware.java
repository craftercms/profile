package org.craftercms.security.utils;

/**
 * Interface implemented by objects that need to be notified if security is enabled or not.
 *
 * @author Alfonso Vásquez
 */
public interface SecurityEnabledAware {

    void setSecurityEnabled(boolean securityEnabled);

}
