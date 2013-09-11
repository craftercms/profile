package org.craftercms.profile.loader.services;

import org.craftercms.profile.domain.Role;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.domain.Profile;

/**
 * Created with IntelliJ IDEA.
 * User: sokeeffe
 * Date: 9/4/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProfileWrapperService {

    /**
     * Creates & generates profile
     */
    Profile createProfile(String tenant) throws AppAuthenticationFailedException;

    Tenant createTenant(String tenant) throws AppAuthenticationFailedException;

    Role createRole(String roleName) throws AppAuthenticationFailedException;
}
