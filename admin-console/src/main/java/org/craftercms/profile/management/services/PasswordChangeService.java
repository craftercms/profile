package org.craftercms.profile.management.services;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;

public interface PasswordChangeService {

    /**
     * Updates the password with the new value sent by argument.
     *
     * @param password The new password value
     * @param token    Security token sent to the email account of the profile account. This token contain username,
     *                 tenantName and Date that was generated
     * @throws AppAuthenticationFailedException
     *          If the appToken has expired
     */
    void changePassword(String token, String password) throws AppAuthenticationFailedException;

    /**
     * This service will send an email to the user profile owner so that the password could be changed.
     *
     * @param username   that forgot the password.
     * @param tenantName current tenant name
     * @throws AppAuthenticationFailedException
     *          If the appToken has expired
     */
    void forgotPassword(String tenantName, String username) throws AppAuthenticationFailedException;

    /**
     * Gets current changePasswordUrl. This link will request the change password form
     *
     * @return url to the change password form
     */
    String getCrafterProfileChangePasswordUrl();

}
