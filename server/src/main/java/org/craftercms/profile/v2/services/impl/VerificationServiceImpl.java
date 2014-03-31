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
package org.craftercms.profile.v2.services.impl;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mail.EmailException;
import org.craftercms.commons.mail.EmailFactory;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.v2.exceptions.ExpiredVerificationTokenException;
import org.craftercms.profile.v2.exceptions.I10nProfileException;
import org.craftercms.profile.v2.exceptions.NoSuchVerificationTokenException;
import org.craftercms.profile.v2.repositories.VerificationTokenRepository;
import org.craftercms.profile.v2.services.VerificationService;
import org.craftercms.profile.v2.services.VerificationSuccessCallback;
import org.springframework.beans.factory.annotation.Required;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Default implementation of {@link org.craftercms.profile.v2.services.VerificationService}.
 *
 * @author avasquez
 */
public class VerificationServiceImpl implements VerificationService {

    private static final I10nLogger logger = new I10nLogger(VerificationServiceImpl.class,
            "crafter.profile.messages.logging");

    public static final String TOKEN_PARAM =    "token";

    public static final String VERIFICATION_LINK_TEMPLATE_ARG = "verificationLink";

    public static final String LOG_KEY_VER_URL_CREATED =    "profile.verification.verificationUrlCreated";
    public static final String LOG_KEY_EMAIL_SENT =         "profile.verification.emailSent";

    public static final String ERROR_KEY_CREATE_TOKEN_ERROR =   "profile.verification.createTokenError";
    public static final String ERROR_KEY_GET_TOKEN_ERROR =      "profile.verification.getTokenError";
    public static final String ERROR_KEY_EMAIL_ERROR =          "profile.verification.emailError";

    protected VerificationTokenRepository tokenRepository;
    protected EmailFactory emailFactory;
    protected String from;
    protected String subject;
    protected String templateName;
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
    public void setFrom(String from) {
        this.from = from;
    }

    @Required
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Required
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Required
    public void setTokenMaxAge(int tokenMaxAge) {
        this.tokenMaxAge = tokenMaxAge;
    }

    @Override
    public void sendEmail(Profile profile, String verificationBaseUrl) throws ProfileException {
        VerificationToken token = new VerificationToken(profile.getId().toString(), new Date());

        try {
            tokenRepository.save(token);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TOKEN_ERROR, profile.getId());
        }

        String verificationUrl = createVerificationUrl(verificationBaseUrl, token.getId().toString());

        logger.debug(LOG_KEY_VER_URL_CREATED, profile.getId(), verificationUrl);

        Map<String, String> templateArgs = Collections.singletonMap(VERIFICATION_LINK_TEMPLATE_ARG, verificationUrl);
        String[] to = new String[] { profile.getEmail() };

        try {
            emailFactory.getEmail(from, to, null, null, subject, templateName, templateArgs, true).send();

            logger.debug(LOG_KEY_EMAIL_SENT, profile.getId(), profile.getEmail());
        } catch (EmailException e) {
            throw new I10nProfileException(ERROR_KEY_EMAIL_ERROR, e, profile.getEmail());
        }
    }

    @Override
    public Profile verifyToken(String tokenId, VerificationSuccessCallback callback) throws ProfileException  {
        VerificationToken token;
        try {
            token = tokenRepository.findById(tokenId);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TOKEN_ERROR, tokenId);
        }

        if (token == null) {
            throw new NoSuchVerificationTokenException(tokenId);
        }

        Calendar expirationTime = Calendar.getInstance();
        expirationTime.setTime(token.getTimestamp());
        expirationTime.add(Calendar.SECOND, tokenMaxAge);

        if (Calendar.getInstance().before(token.getTimestamp())) {
            return callback.doOnSuccess(token);
        } else {
            throw new ExpiredVerificationTokenException(expirationTime.getTime());
        }
    }

    protected String createVerificationUrl(String verificationBaseUrl, String tokenId) {
        StringBuilder verificationUrl = new StringBuilder(verificationBaseUrl);

        if (verificationBaseUrl.contains("?")) {
            verificationUrl.append("&");
        } else {
            verificationUrl.append("?");
        }

        verificationUrl.append(TOKEN_PARAM).append("=").append(tokenId);

        return verificationUrl.toString();
    }


}
