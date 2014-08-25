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

import java.util.Calendar;
import java.util.Date;

import org.craftercms.commons.crypto.CipherUtils;
import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.logging.Logged;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.exception.ActionDeniedException;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.TenantAction;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.I10nProfileException;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.BadCredentialsException;
import org.craftercms.profile.exceptions.DisabledProfileException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.permissions.Application;
import org.craftercms.profile.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
@Logged
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final I10nLogger logger = new I10nLogger(AuthenticationServiceImpl.class,
            "crafter.profile.messages.logging");

    public static final String LOG_KEY_AUTHENTICATION_SUCCESSFUL = "profile.auth.authenticationSuccessful";
    public static final String LOG_KEY_TICKET_CREATED = "profile.auth.ticketCreated";
    public static final String LOG_KEY_TICKET_REQUESTED = "profile.auth.ticketRequested";
    public static final String LOG_KEY_TICKET_EXPIRED = "profile.auth.ticketExpired";
    public static final String LOG_KEY_TICKET_INVALIDATED = "profile.auth.tickedInvalidated";

    public static final String ERROR_KEY_CREATE_TICKET_ERROR = "profile.auth.createTicketError";
    public static final String ERROR_KEY_GET_TICKET_ERROR = "profile.auth.getTicketError";
    public static final String ERROR_KEY_UPDATE_TICKET_ERROR = "profile.auth.updateTicketError";
    public static final String ERROR_KEY_DELETE_TICKET_ERROR = "profile.auth.deleteTicketError";

    protected PermissionEvaluator<Application, String> permissionEvaluator;
    protected TicketRepository ticketRepository;
    protected ProfileService profileService;
    protected int ticketMaxAge;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator<Application, String> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Required
    public void setTicketRepository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setTicketMaxAge(int ticketMaxAge) {
        this.ticketMaxAge = ticketMaxAge;
    }

    @Override
    public Ticket authenticate(String tenantName, String username, String password) throws ProfileException {
        checkIfManageTicketsIsAllowed(tenantName);

        Profile profile = profileService.getProfileByUsername(tenantName, username, ProfileConstants.NO_ATTRIBUTE);

        if (profile == null) {
            // Invalid username
            throw new BadCredentialsException();
        }
        if (!profile.isEnabled()) {
            throw new DisabledProfileException(profile.getId().toString(), tenantName);
        }
        if (!CipherUtils.matchPassword(profile.getPassword(), password)) {
            // Invalid password
            throw new BadCredentialsException();
        }

        try {
            Ticket ticket = new Ticket();
            ticket.setTenant(tenantName);
            ticket.setProfileId(profile.getId().toString());
            ticket.setLastRequestTime(new Date());

            ticketRepository.insert(ticket);

            logger.debug(LOG_KEY_AUTHENTICATION_SUCCESSFUL, profile.getId(), ticket);

            return ticket;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TICKET_ERROR, profile.getId());
        }
    }

    @Override
    public Ticket createTicket(String profileId) throws ProfileException {
        Profile profile = profileService.getProfile(profileId, ProfileConstants.NO_ATTRIBUTE);
        if (profile != null) {
            String tenantName = profile.getTenant();

            checkIfManageTicketsIsAllowed(tenantName);

            if (!profile.isEnabled()) {
                throw new DisabledProfileException(profile.getId().toString(), tenantName);
            }

            try {
                Ticket ticket = new Ticket();
                ticket.setTenant(tenantName);
                ticket.setProfileId(profile.getId().toString());
                ticket.setLastRequestTime(new Date());

                ticketRepository.insert(ticket);

                logger.debug(LOG_KEY_TICKET_CREATED, profile.getId(), ticket);

                return ticket;
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_CREATE_TICKET_ERROR, profile.getId());
            }
        } else {
            throw new NoSuchProfileException(profileId);
        }
    }

    @Override
    public Ticket getTicket(String ticketId) throws ProfileException {
        Ticket ticket;
        try {
            ticket = ticketRepository.findById(ticketId);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TICKET_ERROR, e, ticketId);
        }

        if (ticket != null) {
            checkIfManageTicketsIsAllowed(ticket.getTenant());

            Calendar expirationTime = Calendar.getInstance();
            expirationTime.setTime(ticket.getLastRequestTime());
            expirationTime.add(Calendar.SECOND, ticketMaxAge);

            if (Calendar.getInstance().before(expirationTime)) {
                ticket.setLastRequestTime(new Date());

                try {
                    ticketRepository.save(ticket);
                } catch (MongoDataException e) {
                    throw new I10nProfileException(ERROR_KEY_UPDATE_TICKET_ERROR, ticketId);
                }

                logger.debug(LOG_KEY_TICKET_REQUESTED, ticket.getId());

                return ticket;
            } else {
                try {
                    ticketRepository.removeById(ticketId);
                } catch (MongoDataException e) {
                    throw new I10nProfileException(ERROR_KEY_DELETE_TICKET_ERROR, ticketId);
                }

                logger.debug(LOG_KEY_TICKET_EXPIRED, ticket.getId());
            }
        }

        return null;
    }

    @Override
    public void invalidateTicket(String ticketId) throws ProfileException {
        try {
            Ticket ticket = ticketRepository.findById(ticketId);
            if (ticket != null) {
                checkIfManageTicketsIsAllowed(ticket.getTenant());

                ticketRepository.removeById(ticketId);

                logger.debug(LOG_KEY_TICKET_INVALIDATED, ticket.getId());
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_TICKET_ERROR, ticketId);
        }
    }

    protected void checkIfManageTicketsIsAllowed(String tenantName) {
        if (!permissionEvaluator.isAllowed(tenantName, TenantAction.MANAGE_TICKETS.toString())) {
            throw new ActionDeniedException(TenantAction.MANAGE_TICKETS.toString(), "tenant \"" + tenantName + "\"");
        }
    }

}
