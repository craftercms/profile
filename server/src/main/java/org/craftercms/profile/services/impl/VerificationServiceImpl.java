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
package org.craftercms.profile.services.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.logging.Logged;
import org.craftercms.commons.mail.EmailException;
import org.craftercms.commons.mail.EmailFactory;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.exceptions.NoSuchVerificationTokenException;
import org.craftercms.profile.repositories.VerificationTokenRepository;
import org.craftercms.profile.services.VerificationService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Async;

/**
 * Default implementation of {@link org.craftercms.profile.services.VerificationService}.
 *
 * @author avasquez
 */
@Logged
public class VerificationServiceImpl implements VerificationService {

    public static final String VERIFICATION_LINK_TEMPLATE_ARG = "verificationLink";

    public static final String LOG_KEY_TOKEN_CREATED = "profile.verification.tokenCreated";
    public static final String LOG_KEY_EMAIL_SENT = "profile.verification.emailSent";
    public static final String LOG_KEY_TOKEN_VERIFIED = "profile.verification.tokenVerified";
    public static final String LOG_KEY_TOKEN_DELETED = "profile.verification.tokenDeleted";
    public static final String ERROR_KEY_CREATE_TOKEN_ERROR = "profile.verification.createTokenError";
    public static final String ERROR_KEY_GET_TOKEN_ERROR = "profile.verification.getTokenError";
    public static final String ERROR_KEY_DELETE_TOKEN_ERROR = "profile.verification.deleteTokenError";
    public static final String ERROR_KEY_EMAIL_ERROR = "profile.verification.emailError";

    private static final I10nLogger logger = new I10nLogger(VerificationServiceImpl.class,
        "crafter.profile.messages.logging");

    protected VerificationTokenRepository tokenRepository;
    protected EmailFactory emailFactory;
    protected int tokenMaxAge;

    @Required
    public void setTokenRepository(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Required
    public void setEmailFactory(EmailFactory emailFactory) {
        this.emailFactory = emailFactory;
    }

    @Required
    public void setTokenMaxAge(int tokenMaxAge) {
        this.tokenMaxAge = tokenMaxAge;
    }

    @Override
    public VerificationToken createToken(String profileId) throws ProfileException {
        VerificationToken token = new VerificationToken();
        token.setProfileId(profileId);
        token.setTimestamp(new Date());

        try {
            tokenRepository.insert(token);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TOKEN_ERROR, profileId);
        }

        logger.debug(LOG_KEY_TOKEN_CREATED, profileId, token);

        return token;
    }

    @Override
    @Async
    public void sendEmail(VerificationToken token, Profile profile, String verificationBaseUrl, String from,
                          String subject, String templateName) throws ProfileException {
        String verificationUrl = createVerificationUrl(verificationBaseUrl, token.getId().toString());

        Map<String, String> templateArgs = Collections.singletonMap(VERIFICATION_LINK_TEMPLATE_ARG, verificationUrl);
        String[] to = new String[] {profile.getEmail()};

        try {
            emailFactory.getEmail(from, to, null, null, subject, templateName, templateArgs, true).send();

            logger.debug(LOG_KEY_EMAIL_SENT, profile.getId(), profile.getEmail());
        } catch (EmailException e) {
            throw new I10nProfileException(ERROR_KEY_EMAIL_ERROR, e, profile.getEmail());
        }
    }

    @Override
    public VerificationToken verifyToken(String tokenId) throws ProfileException {
        VerificationToken token;
        try {
            token = tokenRepository.findById(tokenId);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TOKEN_ERROR, tokenId);
        }

        if (token == null) {
            throw new NoSuchVerificationTokenException(tokenId);
        }

        return token;
    }

    @Override
    public void deleteToken(String tokenId) throws ProfileException {
        try {
            tokenRepository.removeById(tokenId);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_TOKEN_ERROR, tokenId);
        }

        logger.debug(LOG_KEY_TOKEN_DELETED, tokenId);
    }

    protected String createVerificationUrl(String verificationBaseUrl, String tokenId) {
        StringBuilder verificationUrl = new StringBuilder(verificationBaseUrl);

        if (verificationBaseUrl.contains("?")) {
            verificationUrl.append("&");
        } else {
            verificationUrl.append("?");
        }

        verificationUrl.append(ProfileConstants.PARAM_TOKEN_ID).append("=").append(tokenId);

        return verificationUrl.toString();
    }

}
