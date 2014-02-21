package org.craftercms.profile.services;

import java.text.ParseException;

import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.exceptions.ProfileException;

/**
 * Manage all the services related to profile password
 *
 * @author Alvaro Gonzalez
 */
public interface PasswordService {
    /**
     * Updates the password with the new value sent by argument.
     *
     * @param password Clear text new password value
     * @param token    Security token sent to the email account of the profile account. This token contain username,
     *                 tenantName and Date that was generated
     * @return the profile instance that the password was reset
     * @throws CipherException        If an error occurred during the decryption process.
     * @throws NoSuchProfileException If the profile was not found in the database
     * @throws ParseException         If an error occurred when the date of the token is parsed
     * @throws ExpiryDateException    If the token has already expired
     */
    Profile resetPassword(String password, String token) throws CipherException, NoSuchProfileException,
        ParseException, ExpiryDateException, ProfileException;

    /**
     * This service will send an email to the user profile owner so that the password could be changed.
     *
     * @param changePasswordUrl The url that will be added to the email so that the user will use it to change the
     *                          password
     * @param username          that forgot the password.
     * @param tenantName        current tenant name
     * @return the profile instance that the password was forgotten
     * @throws CipherException        If an error ocurred during the encryption process.
     * @throws MailException          If an error ocurred during the emailing process
     * @throws NoSuchProfileException If there is not a username registered with the value sent as argument.
     */
    Profile forgotPassword(String changePasswordUrl, String username, String tenantName) throws CipherException,
        MailException, NoSuchProfileException, ProfileException;

}
