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

import java.util.List;
import java.util.UUID;

import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.DuplicateKeyException;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AccessTokenService;
import org.craftercms.profile.exceptions.AccessTokenExistsException;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.craftercms.profile.utils.AccessTokenUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link AccessTokenService}.
 *
 * @author avasquez
 */
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final I10nLogger logger = new I10nLogger(AccessTokenServiceImpl.class,
                                                            "crafter.profile.messages.logging");

    public static final String LOG_KEY_ACCESS_TOKEN_CREATED = "profile.accessToken.accessTokenCreated";
    public static final String LOG_KEY_ACCESS_TOKEN_DELETED = "profile.accessToken.accessTokenDeleted";

    public static final String ERROR_KEY_CREATE_ACCESS_TOKEN_ERROR = "profile.accessToken.createAccessTokenError";
    public static final String ERROR_KEY_GET_ACCESS_TOKEN_ERROR = "profile.accessToken.getAccessTokenError";
    public static final String ERROR_KEY_GET_ALL_ACCESS_TOKENS_ERROR = "profile.accessToken.getAllAccessTokensError";
    public static final String ERROR_KEY_DELETE_ACCESS_TOKEN_ERROR = "profile.accessToken.deleteAccessTokenError";

    protected AccessTokenRepository accessTokenRepository;
    protected byte[] hashSalt;

    @Required
    public void setAccessTokenRepository(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    public AccessToken createToken(AccessToken token) throws ProfileException {
        checkIfTokenActionIsAllowed(null, Action.CREATE_TOKEN);

        if (token.getId() == null) {
            token.setId(UUID.randomUUID().toString());
        }

        try {
            accessTokenRepository.insert(token);
        } catch (DuplicateKeyException e) {
            throw new AccessTokenExistsException(token.getId());
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_ACCESS_TOKEN_ERROR, e, token);
        }

        logger.debug(LOG_KEY_ACCESS_TOKEN_CREATED, token);

        return token;
    }

    @Override
    public AccessToken getToken(String id) throws ProfileException {
        checkIfTokenActionIsAllowed(id, Action.READ_TOKEN);

        try {
            return accessTokenRepository.findByStringId(id);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_ACCESS_TOKEN_ERROR, e, id);
        }
    }

    @Override
    public List<AccessToken> getAllTokens() throws ProfileException {
        checkIfTokenActionIsAllowed(null, Action.READ_TOKEN);

        try {
            return IterableUtils.toList(accessTokenRepository.findAll());
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_ALL_ACCESS_TOKENS_ERROR, e);
        }
    }

    @Override
    public void deleteToken(String id) throws ProfileException {
        checkIfTokenActionIsAllowed(id, Action.DELETE_TOKEN);

        try {
            accessTokenRepository.removeByStringId(id);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_ACCESS_TOKEN_ERROR, e, id);
        }

        logger.debug(LOG_KEY_ACCESS_TOKEN_DELETED, id);
    }

    protected void checkIfTokenActionIsAllowed(String id, Action action) {
        if (!AccessTokenUtils.getCurrentToken().isMaster()) {
            if (id != null) {
                throw new ActionDeniedException(action.toString(), id);
            } else {
                throw new ActionDeniedException(action.toString());
            }
        }
    }

    private enum Action {
        CREATE_TOKEN,
        READ_TOKEN,
        DELETE_TOKEN
    }

}
