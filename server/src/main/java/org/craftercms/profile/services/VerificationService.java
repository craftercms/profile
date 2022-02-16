/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.services;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.ProfileException;

/**
 * Service used to verify a particular activity with the profile owner (like a recently created profile or a reset
 * password request).
 *
 * @author avasquez
 */
public interface VerificationService {

    /**
     * Creates a new verification token. The token can be later transmitted to the client through email, for
     * example.
     *
     * @param profile the profile to create the token for
     */
    VerificationToken createToken(Profile profile) throws ProfileException;

    /**
     * Creates a verification token and sends the user an email with the token for verification.
     *
     * @param token             the verification token to send
     * @param profile           the profile of the user
     * @param verificationUrl   the URL the user should click to verify the new profile
     * @param from              the from address
     * @param subject           the subject of the email
     * @param templateName      the template name of the email
     *
     */
    void sendEmail(VerificationToken token, Profile profile, String verificationUrl, String from, String subject,
                   String templateName) throws ProfileException;

    /**
     * Returns the token that corresponds to the specified ID
     *
     * @param tokenId   the token ID, sent in the verification email
     *
     * @return the verification token object associated to the ID
     */
    VerificationToken getToken(String tokenId) throws ProfileException;

    /**
     * Deletes the token corresponding the specified ID.
     *
     * @param tokenId the ID of the token to delete
     */
    void deleteToken(String tokenId) throws ProfileException;

}
