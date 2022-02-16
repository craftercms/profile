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
package org.craftercms.profile.services.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.logging.Logged;
import org.craftercms.commons.mail.EmailException;
import org.craftercms.commons.mail.EmailFactory;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.TenantAction;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
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

    private static final I10nLogger logger = new I10nLogger(VerificationServiceImpl.class,
                                                            "crafter.profile.messages.logging");

    public static final String VERIFICATION_LINK_TEMPLATE_ARG = "verificationLink";

    public static final String LOG_KEY_TOKEN_CREATED = "profile.verification.tokenCreated";
    public static final String LOG_KEY_EMAIL_SENT = "profile.verification.emailSent";
    public static final String LOG_KEY_TOKEN_DELETED = "profile.verification.tokenDeleted";
    public static final String ERROR_KEY_CREATE_TOKEN_ERROR = "profile.verification.createTokenError";
    public static final String ERROR_KEY_GET_TOKEN_ERROR = "profile.verification.getTokenError";
    public static final String ERROR_KEY_DELETE_TOKEN_ERROR = "profile.verification.deleteTokenError";
    public static final String ERROR_KEY_EMAIL_ERROR = "profile.verification.emailError";

    protected PermissionEvaluator<AccessToken, String> permissionEvaluator;
    protected VerificationTokenRepository tokenRepository;
    protected EmailFactory emailFactory;
    protected int tokenMaxAge;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator<AccessToken, String> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

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
    public VerificationToken createToken(Profile profile) throws ProfileException {
        String tenant = profile.getTenant();
        String profileId = profile.getId().toString();

        VerificationToken token = new VerificationToken();
        token.setId(UUID.randomUUID().toString());
        token.setTenant(tenant);
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
    public VerificationToken getToken(String tokenId) throws ProfileException {
        try {
            VerificationToken token = tokenRepository.findByStringId(tokenId);
            if (token != null) {
                checkIfManageProfilesIsAllowed(token.getTenant());
            }

            return token;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TOKEN_ERROR, tokenId);
        }
    }

    @Override
    public void deleteToken(String tokenId) throws ProfileException {
        VerificationToken token = getToken(tokenId);
        if (token != null) {
            try {
                tokenRepository.removeByStringId(tokenId);
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_DELETE_TOKEN_ERROR, tokenId);
            }

            logger.debug(LOG_KEY_TOKEN_DELETED, tokenId);
        }
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

    protected void checkIfManageProfilesIsAllowed(String tenantName) {
        if (!permissionEvaluator.isAllowed(tenantName, TenantAction.MANAGE_PROFILES.toString())) {
            throw new ActionDeniedException(TenantAction.MANAGE_PROFILES.toString(), "tenant \"" + tenantName + "\"");
        }
    }

}
