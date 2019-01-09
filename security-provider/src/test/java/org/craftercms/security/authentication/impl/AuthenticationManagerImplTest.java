/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
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
package org.craftercms.security.authentication.impl;

import java.util.Date;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.AuthenticationCache;
import org.craftercms.security.exception.BadCredentialsException;
import org.craftercms.security.exception.DisabledUserException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.security.authentication.impl.AuthenticationManagerImpl}.
 *
 * @author avasquez
 */
public class AuthenticationManagerImplTest {

    private static final String TENANT1 = "default";
    private static final String TENANT2 = "mysite";
    private static final String USERNAME1 = "avasquez";
    private static final String USERNAME2 = "avasquez2";
    private static final String DISABLED_USERNAME = "jdoe";
    private static final String PASSWORD1 = "1234";
    private static final String PASSWORD2 = "4321";
    private static final ObjectId PROFILE_ID1 = new ObjectId();
    private static final ObjectId PROFILE_ID2 = new ObjectId();
    private static final String TICKET_ID1 = UUID.randomUUID().toString();
    private static final String TICKET_ID2 = UUID.randomUUID().toString();
    private static final String INVALID_TICKET_ID = UUID.randomUUID().toString();

    private AuthenticationManagerImpl authenticationManager;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ProfileService profileService;
    @Mock
    private AuthenticationCache authenticationCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(authenticationService.authenticate(TENANT1, USERNAME1, PASSWORD1)).thenReturn(getTicket1());
        when(authenticationService.authenticate(TENANT2, USERNAME2, PASSWORD2)).thenReturn(getTicket2());
        doThrow(new ProfileRestServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, ""))
            .when(authenticationService).authenticate(TENANT1, USERNAME2, PASSWORD2);
        doThrow(new ProfileRestServiceException(HttpStatus.FORBIDDEN, ErrorCode.DISABLED_PROFILE, ""))
            .when(authenticationService).authenticate(TENANT1, DISABLED_USERNAME, PASSWORD1);

        when(profileService.getProfile(PROFILE_ID1.toString(), new String[0])).thenReturn(getProfile1());
        when(profileService.getProfile(PROFILE_ID2.toString(), new String[0])).thenReturn(getProfile2());
        when(profileService.getProfileByTicket(TICKET_ID1, new String[0])).thenReturn(getProfile1());
        doThrow(new ProfileRestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NO_SUCH_TICKET, ""))
            .when(profileService).getProfileByTicket(INVALID_TICKET_ID, new String[0]);

        when(authenticationCache.getAuthentication(TICKET_ID1)).thenReturn(getAuthentication1());

        authenticationManager = new AuthenticationManagerImpl();
        authenticationManager.setAuthenticationService(authenticationService);
        authenticationManager.setProfileService(profileService);
        authenticationManager.setAuthenticationCache(authenticationCache);
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        Authentication authentication = authenticationManager.authenticateUser(TENANT1, USERNAME1, PASSWORD1);
        Authentication expected = getAuthentication1();

        assertNotNull(authentication);
        assertEquals(expected.getTicket(), authentication.getTicket());
        assertEquals(expected.getProfile().getId(), authentication.getProfile().getId());

        verify(authenticationService).authenticate(TENANT1, USERNAME1, PASSWORD1);
        verify(profileService).getProfile(PROFILE_ID1.toString(), new String[0]);
    }

    @Test
    public void testAuthenticateUserWithMultipleTenants() throws Exception {
        Authentication authentication = authenticationManager.authenticateUser(new String[] {TENANT1, TENANT2},
                                                                               USERNAME2, PASSWORD2);
        Authentication expected = getAuthentication2();

        assertNotNull(authentication);
        assertEquals(expected.getTicket(), authentication.getTicket());
        assertEquals(expected.getProfile().getId(), authentication.getProfile().getId());

        verify(authenticationService).authenticate(TENANT1, USERNAME2, PASSWORD2);
        verify(authenticationService).authenticate(TENANT2, USERNAME2, PASSWORD2);
        verify(profileService).getProfile(PROFILE_ID2.toString(), new String[0]);
    }

    @Test(expected = DisabledUserException.class)
    public void testAuthenticateDisabledUser() throws Exception {
        authenticationManager.authenticateUser(TENANT1, DISABLED_USERNAME, PASSWORD1);
    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthenticateUserBadCredentials() throws Exception {
        authenticationManager.authenticateUser(TENANT1, USERNAME2, PASSWORD2);
    }

    @Test
    public void testGetAuthentication() throws Exception {
        Authentication authentication = authenticationManager.getAuthentication(TICKET_ID1, false);
        Authentication expected = getAuthentication1();

        assertNotNull(authentication);
        assertEquals(expected.getTicket(), authentication.getTicket());
        assertEquals(expected.getProfile().getId(), authentication.getProfile().getId());

        verify(authenticationCache).getAuthentication(TICKET_ID1);

        authentication = authenticationManager.getAuthentication(TICKET_ID1, true);

        assertNotNull(authentication);
        assertEquals(expected.getTicket(), authentication.getTicket());
        assertEquals(expected.getProfile().getId(), authentication.getProfile().getId());

        verify(authenticationCache).putAuthentication(authentication);
    }

    @Test
    public void testGetAuthenticationInvalidTicket() throws Exception {
        Authentication auth = authenticationManager.getAuthentication(INVALID_TICKET_ID, false);

        assertNull(auth);
    }

    @Test
    public void testInvalidateAuthentication() throws Exception {
        Authentication auth = getAuthentication1();

        authenticationManager.invalidateAuthentication(auth);

        verify(authenticationCache).removeAuthentication(auth.getTicket());
        verify(authenticationService).invalidateTicket(auth.getTicket());
    }

    private Ticket getTicket1() {
        Ticket ticket = new Ticket();
        ticket.setId(TICKET_ID1);
        ticket.setTenant(TENANT1);
        ticket.setProfileId(PROFILE_ID1.toString());
        ticket.setLastRequestTime(new Date());

        return ticket;
    }

    private Ticket getTicket2() {
        Ticket ticket = new Ticket();
        ticket.setId(TICKET_ID2);
        ticket.setTenant(TENANT2);
        ticket.setProfileId(PROFILE_ID2.toString());
        ticket.setLastRequestTime(new Date());

        return ticket;
    }

    private Profile getProfile1() {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID1);

        return profile;
    }

    private Profile getProfile2() {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID2);

        return profile;
    }

    private Authentication getAuthentication1() {
        return new DefaultAuthentication(TICKET_ID1, getProfile1());
    }

    private Authentication getAuthentication2() {
        return new DefaultAuthentication(TICKET_ID2, getProfile2());
    }

}
