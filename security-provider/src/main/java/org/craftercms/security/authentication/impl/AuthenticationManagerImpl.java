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
package org.craftercms.security.authentication.impl;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationCache;
import org.craftercms.security.authentication.AuthenticationManager;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.craftercms.security.exception.BadCredentialsException;
import org.craftercms.security.exception.DisabledUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.security.authentication.AuthenticationManager}.
 *
 * @author avasquez
 */
public class AuthenticationManagerImpl implements AuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationManagerImpl.class);

    protected AuthenticationService authenticationService;
    protected ProfileService profileService;
    protected AuthenticationCache authenticationCache;

    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setAuthenticationCache(AuthenticationCache authenticationCache) {
        this.authenticationCache = authenticationCache;
    }

    @Override
    public Authentication authenticateUser(String tenant, String username, String password) {
        try {
            Ticket ticket = authenticationService.authenticate(tenant, username, password);
            Profile profile = profileService.getProfile(ticket.getProfileId());

            if (profile == null) {
                throw new AuthenticationSystemException("No profile found for ID '" + ticket.getProfileId() + "'");
            }

            String ticketId = ticket.getId();
            DefaultAuthentication auth = new DefaultAuthentication(ticketId, profile);

            authenticationCache.putAuthentication(auth);

            logger.debug("Authentication successful for user '{}' (ticket ID = '{}')", ticket.getProfileId(), ticketId);

            return auth;
        } catch (ProfileRestServiceException e) {
            switch (e.getErrorCode()) {
                case DISABLED_PROFILE:
                    throw new DisabledUserException("User is disabled", e);
                case BAD_CREDENTIALS:
                    throw new BadCredentialsException("Invalid username and/or password", e);
                default:
                    throw new AuthenticationSystemException("An unexpected error occurred while authenticating", e);
            }
        } catch (ProfileException e) {
            throw new AuthenticationSystemException("An unexpected error occurred while authenticating", e);
        }
    }

    @Override
    public Authentication authenticateUser(String[] tenants, String username,
                                           String password) throws AuthenticationException {
        for (String tenant : tenants) {
            try {
                return authenticateUser(tenant, username, password);
            } catch (BadCredentialsException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Authentication attempt for user '" + username + "' with tenant '" + tenant +
                                 "' failed. Trying with next tenant...", e);
                }
            }
        }

        throw new BadCredentialsException("Invalid username and/or password");
    }

    @Override
    public Authentication authenticateUser(Profile profile) throws AuthenticationException {
        return authenticateUser(profile, false);
    }

    @Override
    public Authentication authenticateUser(Profile profile, boolean remembered) throws AuthenticationException {
        try {
            Ticket ticket = authenticationService.createTicket(profile.getId().toString());
            String ticketId = ticket.getId();
            DefaultAuthentication auth = new DefaultAuthentication(ticketId, profile, remembered);

            authenticationCache.putAuthentication(auth);

            logger.debug("Authentication successful for user '{}' (ticket ID = '{}')", ticket.getProfileId(), ticketId);

            return auth;
        } catch (ProfileRestServiceException e) {
            if (e.getErrorCode() == ErrorCode.DISABLED_PROFILE) {
                throw new DisabledUserException("User is disabled", e);
            } else {
                throw new AuthenticationSystemException("An unexpected error occurred while authenticating", e);
            }
        } catch (ProfileException e) {
            throw new AuthenticationSystemException("An unexpected error occurred while authenticating", e);
        }
    }

    @Override
    public Authentication getAuthentication(String ticket, boolean reloadProfile) throws AuthenticationException {
        Authentication auth = null;

        if (!reloadProfile) {
            auth = authenticationCache.getAuthentication(ticket);
        }

        if (auth == null) {
            if (reloadProfile) {
                logger.debug("Profile reload forced for ticket '{}'", ticket);
            } else {
                logger.debug("Ticket '{}' found in request but there's no cached authentication for it", ticket);
            }

            Profile profile = loadProfile(ticket);
            if (profile != null) {
                auth = new DefaultAuthentication(ticket, profile);

                authenticationCache.putAuthentication(auth);
            } else {
                return null;
            }
        }

        return auth;
    }

    @Override
    public void invalidateAuthentication(Authentication authentication) {
        try {
            authenticationCache.removeAuthentication(authentication.getTicket());

            authenticationService.invalidateTicket(authentication.getTicket());

            logger.debug("Ticket '{}' successfully invalidated");
        } catch (ProfileException e) {
            throw new AuthenticationSystemException("An unexpected error occurred while attempting to invalidate " +
                    "ticket '" + authentication.getTicket() + "'", e);
        }
    }

    protected Profile loadProfile(String ticketId) throws AuthenticationException {
        try {
            Profile profile = profileService.getProfileByTicket(ticketId);
            if (profile != null) {
                logger.debug("Profile '{}' retrieved for ticket '{}'", profile.getId(), ticketId);

                return profile;
            } else {
                throw new AuthenticationSystemException("No profile found for ticket '" + ticketId + "'");
            }
        } catch (ProfileRestServiceException e) {
            if (e.getErrorCode() == ErrorCode.NO_SUCH_TICKET) {
                logger.debug("Ticket '{}' is invalid", ticketId);

                return null;
            } else {
                throw new AuthenticationSystemException("An unexpected error occurred while attempting to retrieve " +
                        "profile for ticket '" + ticketId + "'", e);
            }
        } catch (ProfileException e) {
            throw new AuthenticationSystemException("An unexpected error occurred while attempting to retrieve " +
                    "profile for ticket '" + ticketId + "'", e);
        }
    }

}
