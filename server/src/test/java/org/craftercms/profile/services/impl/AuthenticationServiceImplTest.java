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

import org.bson.types.ObjectId;
import org.craftercms.commons.crypto.CipherUtils;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.BadCredentialsException;
import org.craftercms.profile.exceptions.DisabledProfileException;
import org.craftercms.profile.permissions.Application;
import org.craftercms.profile.repositories.TicketRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link org.craftercms.profile.services.impl.AuthenticationServiceImpl}.
 *
 * @author avasquez
 */
public class AuthenticationServiceImplTest {

    private static final String TENANT_NAME = "tenant1";

    private static final ObjectId PROFILE_ID =  new ObjectId();
    private static final String USERNAME1 =     "user1";
    private static final String USERNAME2 =     "user2";
    private static final String PASSWORD =      "12345";

    private static final ObjectId NORMAL_TICKET_ID =    new ObjectId();
    private static final ObjectId EXPIRED_TICKET_ID =   new ObjectId();
    private static final int TICKET_MAX_AGE =           900;

    private AuthenticationServiceImpl authenticationService;
    @Mock
    private PermissionEvaluator<Application, String> permissionEvaluator;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ProfileService profileService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(permissionEvaluator.isAllowed(anyString(), anyString())).thenReturn(true);

        when(ticketRepository.findById(NORMAL_TICKET_ID.toString())).thenReturn(getNormalTicket());
        when(ticketRepository.findById(EXPIRED_TICKET_ID.toString())).thenReturn(getExpiredTicket());

        when(profileService.getProfileByUsername(TENANT_NAME, USERNAME1)).thenReturn(getProfile1());
        when(profileService.getProfileByUsername(TENANT_NAME, USERNAME2)).thenReturn(getProfile2());

        authenticationService = new AuthenticationServiceImpl();
        authenticationService.setPermissionEvaluator(permissionEvaluator);
        authenticationService.setTicketRepository(ticketRepository);
        authenticationService.setProfileService(profileService);
        authenticationService.setTicketMaxAge(TICKET_MAX_AGE);
    }

    @Test
    public void testAuthenticate() throws Exception {
        Ticket ticket = authenticationService.authenticate(TENANT_NAME, USERNAME1, PASSWORD);

        assertNotNull(ticket);
        assertEquals(PROFILE_ID, ticket.getProfileId());
        assertNotNull(ticket.getLastRequestTime());

        verify(profileService).getProfileByUsername(TENANT_NAME, USERNAME1);
        verify(ticketRepository).insert(ticket);
    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthenticateInvalidUsername() throws Exception {
        authenticationService.authenticate(TENANT_NAME, "user3", PASSWORD);
        fail("Expected " + BadCredentialsException.class.getName() + " exception");
    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthenticateInvalidPassword() throws Exception {
        authenticationService.authenticate(TENANT_NAME, USERNAME1, "54321");
        fail("Expected " + BadCredentialsException.class.getName() + " exception");
    }

    @Test(expected = DisabledProfileException.class)
    public void testAuthenticateDisabledProfile() throws Exception {
        authenticationService.authenticate(TENANT_NAME, USERNAME2, PASSWORD);
        fail("Expected " + DisabledProfileException.class.getName() + " exception");
    }

    @Test
    public void testGetTicket() throws Exception {
        Ticket ticket = authenticationService.getTicket(NORMAL_TICKET_ID.toString());

        assertNotNull(ticket);
        assertEquals(NORMAL_TICKET_ID, ticket.getId());
        assertEquals(PROFILE_ID, ticket.getProfileId());
        assertNotNull(ticket.getLastRequestTime());

        verify(ticketRepository).findById(NORMAL_TICKET_ID.toString());
        verify(ticketRepository).save(ticket);
    }

    @Test
    public void testGetExpiredTicket() throws Exception {
        Ticket ticket = authenticationService.getTicket(EXPIRED_TICKET_ID.toString());

        assertNull(ticket);

        verify(ticketRepository).findById(EXPIRED_TICKET_ID.toString());
        verify(ticketRepository).removeById(EXPIRED_TICKET_ID.toString());
    }

    @Test
    public void testInvalidateTicket() throws Exception {
        authenticationService.invalidateTicket(NORMAL_TICKET_ID.toString());

        verify(ticketRepository).removeById(NORMAL_TICKET_ID.toString());
    }

    private Profile getProfile1() {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID);
        profile.setUsername(USERNAME1);
        profile.setPassword(CipherUtils.hashPassword(PASSWORD));
        profile.setEnabled(true);

        return profile;
    }

    private Profile getProfile2() {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID);
        profile.setUsername(USERNAME2);
        profile.setPassword(CipherUtils.hashPassword(PASSWORD));
        profile.setEnabled(false);

        return profile;
    }

    private Ticket getNormalTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(NORMAL_TICKET_ID);
        ticket.setProfileId(PROFILE_ID.toString());
        ticket.setLastRequestTime(new Date());

        return ticket;
    }

    private Ticket getExpiredTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(EXPIRED_TICKET_ID);
        ticket.setProfileId(PROFILE_ID.toString());
        ticket.setLastRequestTime(new Date(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(TICKET_MAX_AGE)));

        return ticket;
    }

}
