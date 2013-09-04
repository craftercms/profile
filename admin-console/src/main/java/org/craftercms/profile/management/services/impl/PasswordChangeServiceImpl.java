package org.craftercms.profile.management.services.impl;

import org.apache.log4j.Logger;
import org.craftercms.profile.exceptions.AppAuthenticationException;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.management.services.PasswordChangeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Change password service
 *
 * @author Alvaro Gonzalez
 */
@Service
public class PasswordChangeServiceImpl implements PasswordChangeService {

    private static final Logger log = Logger.getLogger(PasswordChangeServiceImpl.class);

    private String changePasswordUrl;

    /*
     * (non-Javadoc)
     * @see org.craftercms.profile.management.services.PasswordChangeService#forgotPassword(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void forgotPassword(String tenantName, String username) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            ProfileServiceManager.getProfileClient().forgotPassword(ProfileServiceManager.getAppToken(),
                changePasswordUrl, tenantName, username);
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            ProfileServiceManager.getProfileClient().forgotPassword(ProfileServiceManager.getAppToken(),
                changePasswordUrl, tenantName, username);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.craftercms.profile.management.services.PasswordChangeService#changePassword(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void changePassword(String token, String newPassword) throws AppAuthenticationFailedException {
        if (!ProfileServiceManager.isAppTokenInit()) {
            ProfileServiceManager.setAppToken();
        }
        try {
            ProfileServiceManager.getProfileClient().changePassword(ProfileServiceManager.getAppToken(), token,
                newPassword);
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            ProfileServiceManager.getProfileClient().changePassword(ProfileServiceManager.getAppToken(), token,
                newPassword);
        }

    }

    /**
     * Url to the form which capture the new password
     *
     * @param url valid url to the form that will capture the new password
     */
    @Value("${crafter.profile.app.change.password.url}")
    public void setCrafterProfileAppTenantName(String url) {
        this.changePasswordUrl = url;
    }

}
