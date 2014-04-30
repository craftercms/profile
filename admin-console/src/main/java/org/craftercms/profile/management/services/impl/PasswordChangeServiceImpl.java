package org.craftercms.profile.management.services.impl;


import org.craftercms.profile.client.exceptions.AppAuthenticationException;
import org.craftercms.profile.client.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.management.services.PasswordChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Change password service
 *
 * @author Alvaro Gonzalez
 */
@Service
public class PasswordChangeServiceImpl implements PasswordChangeService {

    private static final Logger log = LoggerFactory.getLogger(PasswordChangeServiceImpl.class);

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
            ProfileServiceManager.getProfileClient().resetPassword(ProfileServiceManager.getAppToken(), token,
                newPassword);
        } catch (AppAuthenticationException e) {
            try {
                ProfileServiceManager.setAppToken();
            } catch (AppAuthenticationFailedException e1) {
                log.error("could not get an AppToken", e);
            }
            ProfileServiceManager.getProfileClient().resetPassword(ProfileServiceManager.getAppToken(), token,
                newPassword);
        }

    }

    /**
     * Url to the form which capture the new password
     *
     * @param url valid url to the form that will capture the new password
     */
    @Value("${crafter.profile.app.reset.password.url}")
    public void setCrafterProfileChangePasswordUrl(String url) {
        this.changePasswordUrl = url;
    }

    @Override
    public String getCrafterProfileChangePasswordUrl() {
        return this.changePasswordUrl;
    }

}
