package org.craftercms.profile.api;

/**
 * Objects tha implement this interface can be restricted through the ACL attached to them or the ACL of their
 * parents (depending of the ACL propagation).
 *
 * @author avasquez
 */
public interface SecuredObject {

    SecuredObject getParent();

    AccessControlList getAcl();

    void setAcl(AccessControlList acl);

}
