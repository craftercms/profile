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

import java.util.Date;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.craftercms.commons.crypto.CryptoUtils;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.BadCredentialsException;
import org.craftercms.profile.exceptions.DisabledProfileException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.repositories.PersistentLoginRepository;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TicketRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.profile.services.impl.AuthenticationServiceImpl}.
 *
 * @author avasquez
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceImplTest {

    private static final String TENANT_NAME = "tenant1";

    private static final ObjectId PROFILE1_ID = new ObjectId();
    private static final ObjectId PROFILE2_ID = new ObjectId();
    private static final String USERNAME1 = "user1";
    private static final String USERNAME2 = "user2";
    private static final String PASSWORD = "12345";

    private static final String TICKET_ID = UUID.randomUUID().toString();

    private static final String PERSISTENT_LOGIN_ID = UUID.randomUUID().toString();
    private static final String PERSISTENT_LOGIN_TOKEN = UUID.randomUUID().toString();

    private AuthenticationServiceImpl authenticationService;
    @Mock
    private PermissionEvaluator<AccessToken, String> permissionEvaluator;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PersistentLoginRepository persistentLoginRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private ProfileRepository profileRepository;

    @Before
    public void setUp() throws Exception {
        when(permissionEvaluator.isAllowed(anyString(), anyString())).thenReturn(true);

        when(ticketRepository.findByStringId(TICKET_ID)).thenReturn(getTicket());

        when(persistentLoginRepository.findByStringId(PERSISTENT_LOGIN_ID)).thenReturn(getPersistentLogin());

        when(profileService.getProfileByUsername(TENANT_NAME, USERNAME1, ProfileConstants.NO_ATTRIBUTE))
            .thenReturn(getProfile1());
        when(profileService.getProfileByUsername(TENANT_NAME, USERNAME2, ProfileConstants.NO_ATTRIBUTE))
            .thenReturn(getProfile2());
        when(profileService.getProfile(PROFILE1_ID.toString(), ProfileConstants.NO_ATTRIBUTE))
            .thenReturn(getProfile1());
        when(profileService.getProfile(PROFILE2_ID.toString(), ProfileConstants.NO_ATTRIBUTE))
            .thenReturn(getProfile2());


        authenticationService = new AuthenticationServiceImpl();
        authenticationService.setPermissionEvaluator(permissionEvaluator);
        authenticationService.setTicketRepository(ticketRepository);
        authenticationService.setPersistentLoginRepository(persistentLoginRepository);
        authenticationService.setProfileService(profileService);
        authenticationService.setFailedLoginAttemptsBeforeDelay(2);
        authenticationService.setLockTime(5);
        authenticationService.setFailedLoginAttemptsBeforeLock(8);
    }

    @Test
    public void testAuthenticate() throws Exception {
        Ticket ticket = authenticationService.authenticate(TENANT_NAME, USERNAME1, PASSWORD);

        assertNotNull(ticket);
        assertEquals(PROFILE1_ID.toString(), ticket.getProfileId());
        assertNotNull(ticket.getLastRequestTime());

        verify(profileService).getProfileByUsername(TENANT_NAME, USERNAME1, ProfileConstants.NO_ATTRIBUTE);
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
    public void testCreateTicket() throws Exception {
        Ticket ticket = authenticationService.createTicket(PROFILE1_ID.toString());

        assertNotNull(ticket);
        assertEquals(PROFILE1_ID.toString(), ticket.getProfileId());
        assertNotNull(ticket.getLastRequestTime());

        verify(profileService).getProfile(PROFILE1_ID.toString(), ProfileConstants.NO_ATTRIBUTE);
        verify(ticketRepository).insert(ticket);
    }

    @Test(expected = NoSuchProfileException.class)
    public void testCreateTicketWithInvalidProfile() throws Exception {
        authenticationService.createTicket("1234");
        fail("Expected " + NoSuchProfileException.class.getName() + " exception");
    }

    @Test
    public void testGetTicket() throws Exception {
        Ticket ticket = authenticationService.getTicket(TICKET_ID);

        assertNotNull(ticket);
        assertEquals(TICKET_ID, ticket.getId());
        assertEquals(PROFILE1_ID.toString(), ticket.getProfileId());
        assertNotNull(ticket.getLastRequestTime());

        verify(ticketRepository).findByStringId(TICKET_ID);
        verify(ticketRepository).save(ticket);
    }

    @Test
    public void testInvalidateTicket() throws Exception {
        authenticationService.invalidateTicket(TICKET_ID);

        verify(ticketRepository).removeByStringId(TICKET_ID);
    }

    @Test
    public void testCreatePersistentLogin() throws Exception {
        PersistentLogin login = authenticationService.createPersistentLogin(PROFILE1_ID.toString());

        assertNotNull(login);
        assertNotNull(login.getId());
        assertEquals(TENANT_NAME, login.getTenant());
        assertEquals(PROFILE1_ID.toString(), login.getProfileId());
        assertNotNull(login.getToken());
        assertNotNull(login.getTimestamp());

        verify(profileService).getProfile(PROFILE1_ID.toString(), ProfileConstants.NO_ATTRIBUTE);
        verify(persistentLoginRepository).insert(login);
    }

    @Test(expected = NoSuchProfileException.class)
    public void testCreatePersistentLoginWithInvalidProfile() throws Exception {
        authenticationService.createPersistentLogin("1234");
    }

    @Test(expected = DisabledProfileException.class)
    public void testCreatePersistentLoginWithDisabledProfile() throws Exception {
        authenticationService.createPersistentLogin(PROFILE2_ID.toString());
    }

    @Test
    public void testGetPersistentLogin() throws Exception {
        PersistentLogin login = authenticationService.getPersistentLogin(PERSISTENT_LOGIN_ID);

        assertNotNull(login);
        assertEquals(PERSISTENT_LOGIN_ID, login.getId());
        assertEquals(TENANT_NAME, login.getTenant());
        assertEquals(PROFILE1_ID.toString(), login.getProfileId());
        assertEquals(PERSISTENT_LOGIN_TOKEN, login.getToken());
        assertNotNull(login.getTimestamp());

        verify(persistentLoginRepository).findByStringId(PERSISTENT_LOGIN_ID);
    }

    private Profile getProfile1() {
        Profile profile = new Profile();
        profile.setId(PROFILE1_ID);
        profile.setUsername(USERNAME1);
        profile.setPassword(CryptoUtils.hashPassword(PASSWORD));
        profile.setEnabled(true);
        profile.setTenant(TENANT_NAME);

        return profile;
    }

    private Profile getProfile2() {
        Profile profile = new Profile();
        profile.setId(PROFILE2_ID);
        profile.setUsername(USERNAME2);
        profile.setPassword(CryptoUtils.hashPassword(PASSWORD));
        profile.setEnabled(false);
        profile.setTenant(TENANT_NAME);

        return profile;
    }

    private Ticket getTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(TICKET_ID);
        ticket.setProfileId(PROFILE1_ID.toString());
        ticket.setLastRequestTime(new Date());

        return ticket;
    }

    private PersistentLogin getPersistentLogin() {
        PersistentLogin login = new PersistentLogin();
        login.setId(PERSISTENT_LOGIN_ID);
        login.setTenant(TENANT_NAME);
        login.setProfileId(PROFILE1_ID.toString());
        login.setToken(PERSISTENT_LOGIN_TOKEN);
        login.setTimestamp(new Date());

        return login;
    }

}
