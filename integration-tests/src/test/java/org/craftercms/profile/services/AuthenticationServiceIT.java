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
package org.craftercms.profile.services;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.craftercms.profile.api.Ticket;
import org.craftercms.profile.api.exceptions.ErrorCode;
import org.craftercms.profile.api.services.AuthenticationService;
import org.craftercms.profile.exceptions.ProfileRestServiceException;
import org.craftercms.profile.services.impl.SingleAccessTokenIdResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
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

    private static final String INVALID_ACCESS_TOKEN_ID = "ab785de0-c327-11e3-9c1a-0800200c9a66";
    private static final String EXPIRED_ACCESS_TOKEN_ID = "9161fb80-c329-11e3-9c1a-0800200c9a66";
    private static final String UNALLOWED_ACCESS_TOKEN_ID = "f9929b40-c358-11e3-9c1a-0800200c9a66";

    private static final String DEFAULT_TENANT_NAME = "default";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String INVALID_USERNAME = "nouser";
    private static final String INVALID_PASSWORD = "nopassword";
    private static final String DISABLED_USER_USERNAME = "jdoe";
    private static final String DISABLED_USER_PASSWORD = "1234";

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SingleAccessTokenIdResolver accessTokenIdResolver;

    @Test
    @DirtiesContext
    public void testMissingAccessTokenIdParamError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(null);

        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals(ErrorCode.MISSING_ACCESS_TOKEN_ID_PARAM, e.getErrorCode());
        }
    }

    @Test
    @DirtiesContext
    public void testNoSuchAccessTokenIdError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(INVALID_ACCESS_TOKEN_ID);

        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.NO_SUCH_ACCESS_TOKEN_ID, e.getErrorCode());
        }
    }

    @Test
    @DirtiesContext
    public void testExpiredAccessTokenError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(EXPIRED_ACCESS_TOKEN_ID);

        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.EXPIRED_ACCESS_TOKEN, e.getErrorCode());
        }
    }

    @Test
    @DirtiesContext
    public void testUnallowedAccessTokenError() throws Exception {
        accessTokenIdResolver.setAccessTokenId(UNALLOWED_ACCESS_TOKEN_ID);

        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);
            fail("Exception " + ProfileRestServiceException.class.getName() + " expected");
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
            assertEquals(ErrorCode.ACTION_DENIED, e.getErrorCode());
        }
    }

    @Test
    public void testAuthenticate() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);

        assertNotNull(ticket);
        assertNotNull(ticket.getId());
        assertNotNull(ticket.getProfileId());
        assertEquals(DEFAULT_TENANT_NAME, ticket.getTenant());
        assertNotNull(ticket.getLastRequestTime());

        authenticationService.invalidateTicket(ticket.getId().toString());
    }

    @Test
    public void testAuthenticateWithInvalidUsername() throws Exception {
        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, INVALID_USERNAME, ADMIN_PASSWORD);
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals(ErrorCode.BAD_CREDENTIALS, e.getErrorCode());
        }
    }

    @Test
    public void testAuthenticateWithInvalidPassword() throws Exception {
        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, INVALID_PASSWORD);
        } catch (ProfileRestServiceException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals(ErrorCode.BAD_CREDENTIALS, e.getErrorCode());
        }
    }

    @Test
    public void testAuthenticateWithDisabledProfile() throws Exception {
        try {
            authenticationService.authenticate(DEFAULT_TENANT_NAME, DISABLED_USER_USERNAME, DISABLED_USER_PASSWORD);
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

        ticket = authenticationService.getTicket(ticket.getId().toString());

        assertNotNull(ticket);
        assertTrue(ticket.getLastRequestTime().after(lastRequestTime));

        authenticationService.invalidateTicket(ticket.getId().toString());
    }

    @Test
    public void testGetExpiredTicket() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);

        assertNotNull(ticket);

        Thread.sleep(TimeUnit.SECONDS.toMillis(4));

        ticket = authenticationService.getTicket(ticket.getId().toString());

        assertNull(ticket);
    }

    @Test
    public void testInvalidateTicket() throws Exception {
        Ticket ticket = authenticationService.authenticate(DEFAULT_TENANT_NAME, ADMIN_USERNAME, ADMIN_PASSWORD);

        assertNotNull(ticket);

        authenticationService.invalidateTicket(ticket.getId().toString());

        ticket = authenticationService.getTicket(ticket.getId().toString());

        assertNull(ticket);
    }

}
