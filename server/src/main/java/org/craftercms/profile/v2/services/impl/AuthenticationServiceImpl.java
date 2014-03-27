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

import org.craftercms.commons.crypto.SimpleDigest;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.commons.security.permissions.annotations.SecuredObject;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.TenantActions;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.v2.exceptions.BadCredentialsException;
import org.craftercms.profile.v2.exceptions.DisabledProfileException;
import org.craftercms.profile.v2.exceptions.I10nProfileException;
import org.craftercms.profile.v2.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Required;

import java.util.Calendar;
import java.util.Date;

/**
 * Default implementation of {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
@HasPermission(type = TenantPermission.class, action = TenantActions.AUTHENTICATE)
public class AuthenticationServiceImpl implements AuthenticationService {

    public static final String ERROR_KEY_AUTHENTICATE_ERROR =       "profile.auth.authenticateError";
    public static final String ERROR_KEY_GET_TICKET_ERROR =         "profile.auth.getTicketError";
    public static final String ERROR_KEY_INVALIDATE_TICKET_ERROR =  "profile.auth.invalidateTicket";

    protected TicketRepository ticketRepository;
    protected ProfileService profileService;
    protected int ticketMaxAge;

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
    public Ticket authenticate(@SecuredObject String tenant, String username, String password) throws ProfileException {
        try {
            Profile profile = profileService.getProfileByUsername(tenant, username);

            if (profile == null) {
                // Invalid username
                throw new BadCredentialsException();
            }
            if (!profile.isEnabled()) {
                throw new DisabledProfileException(profile.getId().toString(), tenant);
            }
            if (!comparePasswords(profile.getPassword(), password)) {
                // Invalid password
                throw new BadCredentialsException();
            }

            Ticket ticket = new Ticket();
            ticket.setUserId(profile.getId().toString());
            ticket.setTimestamp(new Date());

            ticketRepository.save(ticket);

            return ticket;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_AUTHENTICATE_ERROR, username, tenant);
        }
    }

    @Override
    public Ticket getTicket(String ticketId) throws ProfileException {
        try {
            Ticket ticket = ticketRepository.findById(ticketId);
            if (ticket != null) {
                Calendar expirationTime = Calendar.getInstance();
                expirationTime.setTime(ticket.getTimestamp());
                expirationTime.add(Calendar.SECOND, ticketMaxAge);

                return Calendar.getInstance().before(expirationTime) ? ticket : null;
            } else {
                return null;
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TICKET_ERROR, ticketId);
        }
    }

    @Override
    public void invalidateTicket(@SecuredObject String tenant, String ticket) throws ProfileException {
        try {
            ticketRepository.removeById(ticket);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_INVALIDATE_TICKET_ERROR, ticket);
        }
    }

    protected boolean comparePasswords(String storedPswd, String providedPswd) {
        int idxOfSep = storedPswd.indexOf(ProfileServiceImpl.PASSWORD_SEP);
        String storedHash = storedPswd.substring(0, idxOfSep);
        String salt = storedPswd.substring(idxOfSep + 1);
        SimpleDigest digest = new SimpleDigest();

        digest.setBase64Salt(salt);

        return storedHash.equals(digest.digestBase64(providedPswd));
    }

}
