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
package org.craftercms.profile.services;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.craftercms.profile.api.PersistentLogin;
import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.craftercms.profile.services.impl.SingleAccessTokenIdResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration tests for {@link org.craftercms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:crafter/profile/extension/client-context.xml")
public class AuthenticationServiceIT {

    private static final String DEFAULT_TENANT_NAME = "default";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String INVALID_USERNAME = "nouser";
    private static final String INVALID_PASSWORD = "nopassword";
    private static final String DISABLED_USER_USERNAME = "jdoe";
    private static final String DISABLED_USER_PASSWORD = "1234";
    private static final String INVALID_PROFILE_ID = ObjectId.get().toString();
    private static final String INVALID_PERSISTENT_LOGIN_ID = ObjectId.get().toString();

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private SingleAccessTokenIdResolver accessTokenIdResolver;

    @Test
    public void testAuthenticate() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);

        assertNotNull(ticket);
        assertNotNull(ticket.getId());
        assertNotNull(ticket.getProfileId());
        assertEquals(DEFAULT_TENANT_NAME, ticket.getTenant());
        assertNotNull(ticket.getLastRequestTime());

        authenticationService.invalidateTicket(ticket.getId());
    }

    @Test
    public void testCreateTicket() throws Exception {
        String profileId = profileService.getProfileByUsername(DEFAULT_TENANT_NAME, ADMIN_USERNAME).getId().toString();
        Ticket ticket = authenticationService.createTicket(profileId);

        assertNotNull(ticket);
        assertNotNull(ticket.getId());
        assertEquals(profileId, ticket.getProfileId());
        assertEquals(DEFAULT_TENANT_NAME, ticket.getTenant());
        assertNotNull(ticket.getLastRequestTime());

        authenticationService.invalidateTicket(ticket.getId());
    }

    @Test
    public void testAuthenticateWithInvalidUsername() throws Exception {
        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, INVALID_USERNAME, ADMIN_PASSWORD);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals(ErrorCode.BAD_CREDENTIALS, e.getErrorCode());
        }
    }

    @Test
    public void testAuthenticateWithInvalidPassword() throws Exception {
        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, INVALID_PASSWORD);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals(ErrorCode.BAD_CREDENTIALS, e.getErrorCode());
        }
    }

    @Test
    public void testAuthenticateWithDisabledProfile() throws Exception {
        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, DISABLED_USER_USERNAME, DISABLED_USER_PASSWORD);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.DISABLED_PROFILE, e.getErrorCode());
        }
    }

    @Test
    public void testGetTicket() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);

        assertNotNull(ticket);

        Date lastRequestTime = ticket.getLastRequestTime();

        ticket = authenticationService.getTicket(ticket.getId());

        assertNotNull(ticket);
        assertTrue(ticket.getLastRequestTime().after(lastRequestTime));

        authenticationService.invalidateTicket(ticket.getId());
    }

    @Test
    public void testGetExpiredTicket() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);

        assertNotNull(ticket);

        Thread.sleep(TimeUnit.SECONDS.toMillis(4));

        ticket = authenticationService.getTicket(ticket.getId());

        assertNull(ticket);
    }

    @Test
    public void testInvalidateTicket() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);

        assertNotNull(ticket);

        authenticationService.invalidateTicket(ticket.getId());

        ticket = authenticationService.getTicket(ticket.getId());

        assertNull(ticket);
    }

    @Test
    public void testCreatePersistentLogin() throws Exception {
        String profileId = profileService.getProfileByUsername(DEFAULT_TENANT_NAME, ADMIN_USERNAME).getId().toString();
        PersistentLogin login = authenticationService.createPersistentLogin(profileId);

        assertNotNull(login);
        assertNotNull(login.getId());
        assertEquals(profileId, login.getProfileId());
        assertEquals(DEFAULT_TENANT_NAME, login.getTenant());
        assertNotNull(login.getToken());
        assertNotNull(login.getTimestamp());

        authenticationService.invalidateTicket(login.getId());
    }

    @Test
    public void testCreatePersistentLoginWithInvalidProfile() throws Exception {
        try {
            authenticationService.createPersistentLogin(INVALID_PROFILE_ID);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals(ErrorCode.NO_SUCH_PROFILE, e.getErrorCode());
        }
    }

    @Test
    public void testCreatePersistentLoginWithDisabledProfile() throws Exception {
        String profileId = profileService.getProfileByUsername(DEFAULT_TENANT_NAME,
                                                               DISABLED_USER_USERNAME).getId().toString();

        try {
            authenticationService.createPersistentLogin(profileId);
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.DISABLED_PROFILE, e.getErrorCode());
        }
    }

    @Test
    public void testGetPersistentLogin() throws Exception {
        String profileId = profileService.getProfileByUsername(DEFAULT_TENANT_NAME, ADMIN_USERNAME).getId().toString();
        PersistentLogin expectedLogin = authenticationService.createPersistentLogin(profileId);

        assertNotNull(expectedLogin);

        PersistentLogin login = authenticationService.getPersistentLogin(expectedLogin.getId());

        assertNotNull(login);
        assertEquals(expectedLogin.getId(), login.getId());
        assertEquals(expectedLogin.getProfileId(), login.getProfileId());
        assertEquals(expectedLogin.getTenant(), login.getTenant());
        assertEquals(expectedLogin.getToken(), login.getToken());
        assertEquals(expectedLogin.getTimestamp(), login.getTimestamp());

        authenticationService.invalidateTicket(expectedLogin.getId());
    }

    @Test
    public void testGetExpiredPersistentLogin() throws Exception {
        String profileId = profileService.getProfileByUsername(DEFAULT_TENANT_NAME, ADMIN_USERNAME).getId().toString();
        PersistentLogin login = authenticationService.createPersistentLogin(profileId);

        assertNotNull(login);

        Thread.sleep(TimeUnit.SECONDS.toMillis(4));

        login = authenticationService.getPersistentLogin(login.getId());

        assertNull(login);
    }

    @Test
    public void testRefreshPersistentLoginToken() throws Exception {
        String profileId = profileService.getProfileByUsername(DEFAULT_TENANT_NAME, ADMIN_USERNAME).getId().toString();
        PersistentLogin login = authenticationService.createPersistentLogin(profileId);

        assertNotNull(login);

        PersistentLogin refreshedLogin = authenticationService.refreshPersistentLoginToken(login.getId());

        assertNotNull(refreshedLogin);
        assertEquals(login.getId(), refreshedLogin.getId());
        assertEquals(login.getProfileId(), refreshedLogin.getProfileId());
        assertEquals(login.getTenant(), refreshedLogin.getTenant());
        assertNotEquals(login.getToken(), refreshedLogin.getToken());
        assertEquals(login.getTimestamp(), refreshedLogin.getTimestamp());

        authenticationService.invalidateTicket(login.getId());
    }

    @Test
    public void testRefreshPersistentLoginTokenWithInvalidLoginId() throws Exception {
        try {
            authenticationService.refreshPersistentLoginToken(INVALID_PERSISTENT_LOGIN_ID);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals(ErrorCode.NO_SUCH_PERSISTENT_LOGIN, e.getErrorCode());
        }
    }

    @Test
    public void testDeletePersistentLogin() throws Exception {
        String profileId = profileService.getProfileByUsername(DEFAULT_TENANT_NAME, ADMIN_USERNAME).getId().toString();
        PersistentLogin login = authenticationService.createPersistentLogin(profileId);

        assertNotNull(login);

        authenticationService.deletePersistentLogin(login.getId());

        login = authenticationService.getPersistentLogin(login.getId());

        assertNull(login);
    }

}
