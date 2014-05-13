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
package org.craftercms.security.authentication.impl;

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

import java.util.Date;

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

    private static final String TENANT =                "default";
    private static final String USERNAME =              "avasquez";
    private static final String DISABLED_USERNAME =     "jdoe";
    private static final String PASSWORD =              "1234";
    private static final String INVALID_PASSWORD =      "4321";
    private static final ObjectId PROFILE_ID =          new ObjectId();
    private static final ObjectId TICKET_ID =           new ObjectId();
    private static final ObjectId INVALID_TICKET_ID =   new ObjectId();

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

        when(authenticationService.authenticate(TENANT, USERNAME, PASSWORD)).thenReturn(getDefaultTicket());
        doThrow(new ProfileRestServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, ""))
                .when(authenticationService).authenticate(TENANT, USERNAME, INVALID_PASSWORD);
        doThrow(new ProfileRestServiceException(HttpStatus.FORBIDDEN, ErrorCode.DISABLED_PROFILE, ""))
                .when(authenticationService).authenticate(TENANT, DISABLED_USERNAME, PASSWORD);

        when(profileService.getProfile(PROFILE_ID.toString(), new String[0])).thenReturn(getDefaultProfile());
        when(profileService.getProfileByTicket(TICKET_ID.toString(), new String[0])).thenReturn(getDefaultProfile());
        doThrow(new ProfileRestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NO_SUCH_TICKET, ""))
                .when(profileService).getProfileByTicket(INVALID_TICKET_ID.toString(), new String[0]);

        when(authenticationCache.getAuthentication(TICKET_ID.toString())).thenReturn(getDefaultAuthentication());

        authenticationManager = new AuthenticationManagerImpl();
        authenticationManager.setAuthenticationService(authenticationService);
        authenticationManager.setProfileService(profileService);
        authenticationManager.setAuthenticationCache(authenticationCache);
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        Authentication authentication = authenticationManager.authenticateUser(TENANT, USERNAME, PASSWORD);
        Authentication expected = getDefaultAuthentication();

        assertNotNull(authentication);
        assertEquals(expected.getTicket(), authentication.getTicket());
        assertEquals(expected.getProfile().getId(), authentication.getProfile().getId());

        verify(authenticationService).authenticate(TENANT, USERNAME, PASSWORD);
        verify(profileService).getProfile(PROFILE_ID.toString(), new String[0]);
    }

    @Test(expected = DisabledUserException.class)
    public void testAuthenticateDisabledUser() throws Exception {
        authenticationManager.authenticateUser(TENANT, DISABLED_USERNAME, PASSWORD);
    }

    @Test(expected = BadCredentialsException.class)
    public void testAuthenticateUserBadCredentials() throws Exception {
        authenticationManager.authenticateUser(TENANT, USERNAME, INVALID_PASSWORD);
    }

    @Test
    public void testGetAuthentication() throws Exception {
        Authentication authentication = authenticationManager.getAuthentication(TICKET_ID.toString(), false);
        Authentication expected = getDefaultAuthentication();

        assertNotNull(authentication);
        assertEquals(expected.getTicket(), authentication.getTicket());
        assertEquals(expected.getProfile().getId(), authentication.getProfile().getId());

        verify(authenticationCache).getAuthentication(TICKET_ID.toString());

        authentication = authenticationManager.getAuthentication(TICKET_ID.toString(), true);

        assertNotNull(authentication);
        assertEquals(expected.getTicket(), authentication.getTicket());
        assertEquals(expected.getProfile().getId(), authentication.getProfile().getId());

        verify(authenticationCache).putAuthentication(authentication);
    }

    @Test
    public void testGetAuthenticationInvalidTicket() throws Exception {
        Authentication auth = authenticationManager.getAuthentication(INVALID_TICKET_ID.toString(), false);

        assertNull(auth);
    }

    @Test
    public void testInvalidateAuthentication() throws Exception {
        Authentication auth = getDefaultAuthentication();

        authenticationManager.invalidateAuthentication(auth);

        verify(authenticationCache).removeAuthentication(auth.getTicket());
        verify(authenticationService).invalidateTicket(auth.getTicket());
    }

    private Ticket getDefaultTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(TICKET_ID);
        ticket.setTenant(TENANT);
        ticket.setProfileId(PROFILE_ID.toString());
        ticket.setLastRequestTime(new Date());

        return ticket;
    }

    private Profile getDefaultProfile() {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID);

        return profile;
    }

    private Authentication getDefaultAuthentication() {
        return new DefaultAuthentication(TICKET_ID.toString(), getDefaultProfile());
    }

}
