package org.craftercms.profile.loader.services;

import org.craftercms.profile.loader.exceptions.ProfileLoadException;

/**
 * Created with IntelliJ IDEA.
 * User: sokeeffe
 * Date: 9/4/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProfileLoaderService {

    void loadContent(int profileCount) throws ProfileLoadException;

    void setTenantCount(int tenantCount);

    void setTenantCount(String tenantCount);
}
