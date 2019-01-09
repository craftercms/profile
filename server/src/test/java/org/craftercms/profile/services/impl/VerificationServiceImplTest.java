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
package org.craftercms.profile.services.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.craftercms.commons.mail.Email;
import org.craftercms.commons.mail.EmailFactory;
import org.craftercms.commons.security.permissions.PermissionEvaluator;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.ProfileConstants;
import org.craftercms.profile.api.VerificationToken;
import org.craftercms.profile.repositories.VerificationTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link org.craftercms.profile.services.impl.VerificationServiceImpl}.
 *
 * @author avasquez
 */
public class VerificationServiceImplTest {

    private static final String FROM = "noreply@example.com";
    private static final String SUBJECT = "Verification Email";
    private static final String TEMPLATE_NAME = "verification-email.ftl";
    private static final int TOKEN_MAX_AGE = 86400;

    private static final String TENANT_NAME = "default";
    private static final ObjectId PROFILE_ID = new ObjectId();
    private static final String PROFILE_EMAIL = "john.doe@example.com";

    private static final String[] TO = {PROFILE_EMAIL};

    private static final String TOKEN_ID = UUID.randomUUID().toString();

    private static final String VERIFICATION_BASE_URL = "http://localhost:8080/verifyProfile";
    private static final Map<String, String> VERIFICATION_TEMPLATE_ARGS = Collections.singletonMap(
            VerificationServiceImpl.VERIFICATION_LINK_TEMPLATE_ARG, VERIFICATION_BASE_URL + "?" +
                    ProfileConstants.PARAM_TOKEN_ID + "=" + TOKEN_ID);

    private VerificationServiceImpl verificationService;
    @Mock
    private PermissionEvaluator<AccessToken, String> permissionEvaluator;
    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private EmailFactory emailFactory;
    @Mock
    private Email email;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(permissionEvaluator.isAllowed(anyString(), anyString())).thenReturn(true);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                VerificationToken token = (VerificationToken) invocation.getArguments()[0];
                token.setId(TOKEN_ID);

                return token;
            }

        }).when(tokenRepository).insert(any(VerificationToken.class));
        when(tokenRepository.findByStringId(TOKEN_ID)).thenReturn(getToken());

        when(emailFactory.getEmail(FROM, TO, null, null, SUBJECT, TEMPLATE_NAME, VERIFICATION_TEMPLATE_ARGS, true))
            .thenReturn(email);

        verificationService = new VerificationServiceImpl();
        verificationService.setPermissionEvaluator(permissionEvaluator);
        verificationService.setTokenRepository(tokenRepository);
        verificationService.setEmailFactory(emailFactory);
        verificationService.setTokenMaxAge(TOKEN_MAX_AGE);
    }

    @Test
    public void testCreateToken() throws Exception {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID);
        profile.setTenant(TENANT_NAME);

        VerificationToken token = verificationService.createToken(profile);

        assertEquals(TOKEN_ID, token.getId());
        assertEquals(TENANT_NAME, token.getTenant());
        assertEquals(PROFILE_ID.toString(), token.getProfileId());
        assertNotNull(token.getTimestamp());

        verify(tokenRepository).insert(any(VerificationToken.class));
    }

    @Test
    public void testSendEmail() throws Exception {
        Profile profile = new Profile();
        profile.setId(PROFILE_ID);
        profile.setEmail(PROFILE_EMAIL);

        VerificationToken token = new VerificationToken();
        token.setId(TOKEN_ID);

        verificationService.sendEmail(token, profile, VERIFICATION_BASE_URL, FROM, SUBJECT, TEMPLATE_NAME);

        verify(emailFactory).getEmail(FROM, TO, null, null, SUBJECT, TEMPLATE_NAME, VERIFICATION_TEMPLATE_ARGS, true);
        verify(email).send();
    }

    @Test
    public void testGetToken() throws Exception {
        VerificationToken token = verificationService.getToken(TOKEN_ID);

        assertNotNull(token);
        assertEquals(TOKEN_ID, token.getId());
        assertEquals(PROFILE_ID.toString(), token.getProfileId());
        assertNotNull(token.getTimestamp());

        verify(tokenRepository).findByStringId(TOKEN_ID);
    }

    @Test
    public void testDeleteToken() throws Exception {
        verificationService.deleteToken(TOKEN_ID);

        verify(tokenRepository).removeByStringId(TOKEN_ID);
    }

    private VerificationToken getToken() {
        VerificationToken token = new VerificationToken();
        token.setId(TOKEN_ID);
        token.setProfileId(PROFILE_ID.toString());
        token.setTimestamp(new Date());

        return token;
    }

}
