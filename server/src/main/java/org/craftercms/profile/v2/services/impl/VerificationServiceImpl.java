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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.craftercms.commons.crypto.CryptoException;
import org.craftercms.commons.crypto.SimpleCipher;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mail.Email;
import org.craftercms.commons.mail.EmailException;
import org.craftercms.commons.mail.EmailFactory;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.v2.exceptions.ExpiredVerificationTokenException;
import org.craftercms.profile.v2.exceptions.I10nProfileException;
import org.craftercms.profile.v2.exceptions.InvalidVerificationTokenException;
import org.craftercms.profile.v2.repositories.ProfileRepository;
import org.craftercms.profile.v2.services.VerificationService;
import org.springframework.beans.factory.annotation.Required;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link org.craftercms.profile.v2.services.VerificationService}.
 *
 * @author avasquez
 */
public class VerificationServiceImpl implements VerificationService {

    private static final I10nLogger logger = new I10nLogger(VerificationServiceImpl.class,
            "crafter.profile.messages.logging");

    public static final String TOKEN_SEP =      "|";
    public static final String TOKEN_PARAM =    "token";

    public static final String VERIFICATION_LINK_TEMPLATE_ARG = "verificationLink";

    public static final String LOG_KEY_TOKEN_SERIALIZED =      "profile.verification.tokenSerialized";
    public static final String LOG_KEY_VER_URL_CREATED =       "profile.verification.verificationUrlCreated";
    public static final String LOG_KEY_EMAIL_SENT =            "profile.verification.emailSent";
    public static final String LOG_KEY_TOKEN_DESERIALIZED =    "profile.verification.tokenDeserialized";
    public static final String LOG_KEY_PROFILE_VERIFIED =      "profile.verification.profileVerified";

    public static final String ERROR_KEY_SERIALIZATION_ERROR =     "profile.verification.serializationError";
    public static final String ERROR_KEY_DESERIALIZATION_ERROR =   "profile.verification.deserializationError";
    public static final String ERROR_KEY_EMAIL_ERROR =             "profile.verification.emailError";
    public static final String ERROR_KEY_PROFILE_UPDATE_ERROR =    "profile.verification.profileUpdateError";

    protected ProfileRepository profileRepository;
    protected EmailFactory emailFactory;
    protected String verificationEmailFrom;
    protected String verificationEmailSubject;
    protected String verificationEmailTemplateName;
    protected ObjectMapper tokenSerializer;
    protected SecretKey tokenEncryptionKey;
    protected long expireTokensAfter;

    public VerificationServiceImpl() {
        tokenSerializer = new ObjectMapper();
    }

    @Required
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Required
    public void setEmailFactory(EmailFactory emailFactory) {
        this.emailFactory = emailFactory;
    }

    @Required
    public void setVerificationEmailFrom(String verificationEmailFrom) {
        this.verificationEmailFrom = verificationEmailFrom;
    }

    @Required
    public void setVerificationEmailSubject(String verificationEmailSubject) {
        this.verificationEmailSubject = verificationEmailSubject;
    }

    @Required
    public void setVerificationEmailTemplateName(String verificationEmailTemplateName) {
        this.verificationEmailTemplateName = verificationEmailTemplateName;
    }

    @Required
    public void setTokenEncryptionKey(SecretKey tokenEncryptionKey) {
        this.tokenEncryptionKey = tokenEncryptionKey;
    }

    @Required
    public void setExpireTokensAfterMins(long expireTokensAfterMins) {
        this.expireTokensAfter = TimeUnit.MINUTES.toMillis(expireTokensAfterMins);
    }

    @Override
    public void sendVerificationEmail(Profile profile, String verificationBaseUrl) throws ProfileException {
        VerificationToken token = new VerificationToken(profile.getId().toString(), getTokenExpirationDate());
        String serializedToken = serializeToken(token);

        logger.debug(LOG_KEY_TOKEN_SERIALIZED, token, serializedToken);

        try {
            serializedToken = URLEncoder.encode(serializedToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should NEVER happen, UTF-8 is a valid encoding
            throw new RuntimeException(e);
        }

        String verificationUrl = createVerificationUrl(verificationBaseUrl, serializedToken);

        logger.debug(LOG_KEY_VER_URL_CREATED, profile.getId(), verificationUrl);

        Map<String, String> templateArgs = Collections.singletonMap(VERIFICATION_LINK_TEMPLATE_ARG, verificationUrl);

        try {
            Email email = emailFactory.getEmail(verificationEmailFrom, new String[] {profile.getEmail()}, null, null,
                    verificationEmailSubject, verificationEmailTemplateName, templateArgs, true);
            email.send();

            logger.debug(LOG_KEY_EMAIL_SENT, profile.getId(), profile.getEmail());
        } catch (EmailException e) {
            throw new I10nProfileException(ERROR_KEY_EMAIL_ERROR, e, profile.getEmail());
        }
    }

    @Override
    public void verifyToken(String serializedToken) throws ProfileException  {
        VerificationToken token = deserializeToken(serializedToken);
        Date now = new Date();

        logger.debug(LOG_KEY_TOKEN_DESERIALIZED, serializedToken, token);

        if (now.before(token.getExpirationDate())) {
            try {
                Profile profile = profileRepository.findById(token.getUserId());
                profile.setEnabled(true);
                profile.setVerified(true);

                profileRepository.save(profile);

                logger.debug(LOG_KEY_PROFILE_VERIFIED, profile.getId());
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_PROFILE_UPDATE_ERROR, e);
            }
        } else {
            throw new ExpiredVerificationTokenException(token.getExpirationDate());
        }
    }

    protected Date getTokenExpirationDate() {
        return new Date(System.currentTimeMillis() + expireTokensAfter);
    }

    protected String createVerificationUrl(String verificationBaseUrl, String serializedToken) {
        StringBuilder verificationUrl = new StringBuilder(verificationBaseUrl);

        if (verificationBaseUrl.contains("?")) {
            verificationUrl.append("&");
        } else {
            verificationUrl.append("?");
        }

        verificationUrl.append(TOKEN_PARAM).append("=").append(serializedToken);

        return verificationUrl.toString();
    }

    protected String serializeToken(VerificationToken token) throws ProfileException {
        try {
            SimpleCipher cipher = new SimpleCipher();
            cipher.setKey(tokenEncryptionKey);

            String unencryptedToken = tokenSerializer.writeValueAsString(token);
            String encryptedToken = cipher.encryptBase64(unencryptedToken);

            return encryptedToken + TOKEN_SEP + cipher.getBase64Iv();
        } catch (JsonProcessingException | CryptoException e) {
            throw new I10nProfileException(ERROR_KEY_SERIALIZATION_ERROR, e);
        }
    }

    protected VerificationToken deserializeToken(String serializedToken) throws ProfileException {
        int indexOfSep = serializedToken.indexOf(TOKEN_SEP);

        if (indexOfSep < 0 || indexOfSep >= (serializedToken.length() - 1)) {
            throw new InvalidVerificationTokenException();
        }

        String encryptedToken = serializedToken.substring(0, indexOfSep);
        String iv = serializedToken.substring(indexOfSep + 1);
        SimpleCipher cipher = new SimpleCipher();

        cipher.setKey(tokenEncryptionKey);
        cipher.setBase64Iv(iv);

        try {
            String unencryptedToken = cipher.decryptBase64(encryptedToken);
            VerificationToken token = tokenSerializer.readValue(unencryptedToken, VerificationToken.class);

            return token;
        } catch (CryptoException | IOException e) {
            throw new I10nProfileException(ERROR_KEY_DESERIALIZATION_ERROR, e);
        }
    }

}
