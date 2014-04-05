/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.v2.services;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;

/**
 * Service used to verify a particular activity with the profile owner through email (like a recently created profile
 * or a reset password request).
 *
 * @author avasquez
 */
public interface VerificationService {

    String TOKEN_ID_PARAM =    "tokenId";

    /**
     * Sends the user an email for verification.
     *
     * @param profile           the profile of the user
     * @param verificationUrl   the URL the user should click to verify the new profile
     */
    void sendEmail(Profile profile, String verificationUrl) throws ProfileException;

    /**
     * Verify that the token received from the user is correct.
     *
     * @param tokenId   the serialized token, sent in the verification email
     * @param callback  callback used on verification success
     *
     * @return the profile associated to the token
     */
    Profile verifyToken(String tokenId, VerificationSuccessCallback callback) throws ProfileException;

}
