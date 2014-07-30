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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.craftercms.commons.mail.Email;
import org.craftercms.commons.mail.EmailFactory;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.exceptions.ExpiredVerificationTokenException;
import org.craftercms.profile.exceptions.NoSuchVerificationTokenException;
import org.craftercms.profile.repositories.VerificationTokenRepository;
import org.craftercms.profile.services.VerificationService;
import org.craftercms.profile.services.VerificationSuccessCallback;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

    private static final String FROM = "noreply@craftersoftware.com";
    private static final String SUBJECT = "Verification Email";
    private static final String TEMPLATE_NAME = "verification-email.ftl";
    private static final int TOKEN_MAX_AGE = 86400;

    private static final ObjectId PROFILE_ID = new ObjectId();
    private static final String PROFILE_EMAIL = "johndoe@gmail.com";

    private static final String[] TO = {PROFILE_EMAIL};

    private static final ObjectId NORMAL_TOKEN_ID = new ObjectId();
    private static final ObjectId EXPIRED_TOKEN_ID = new ObjectId();

    private static final String VERIFICATION_BASE_URL = "http://localhost:8080/verifyProfile";
    private static final Map<String, String> VERIFICATION_TEMPLATE_ARGS = Collections.singletonMap(
            VerificationServiceImpl.VERIFICATION_LINK_TEMPLATE_ARG, VERIFICATION_BASE_URL + "?" +
                    VerificationService.TOKEN_ID_PARAM + "=" + NORMAL_TOKEN_ID);

    private VerificationServiceImpl verificationService;
    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private EmailFactory emailFactory;
    @Mock
    private Email email;
    @Mock
    private VerificationSuccessCallback callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                VerificationToken token = (VerificationToken) invocation.getArguments()[0];
                token.setId(NORMAL_TOKEN_ID);

                return token;
            }

        }).when(tokenRepository).insert(any(VerificationToken.class));
        when(tokenRepository.findById(NORMAL_TOKEN_ID.toString())).thenReturn(getNormalToken());
        when(tokenRepository.findById(EXPIRED_TOKEN_ID.toString())).thenReturn(getExpiredToken());

        when(emailFactory.getEmail(FROM, TO, null, null, SUBJECT, TEMPLATE_NAME, VERIFICATION_TEMPLATE_ARGS, true))
                .thenReturn(email);

        verificationService = new VerificationServiceImpl();
        verificationService.setTokenRepository(tokenRepository);
        verificationService.setEmailFactory(emailFactory);
        verificationService.setFrom(FROM);
        verificationService.setSubject(SUBJECT);
        verificationService.setTemplateName(TEMPLATE_NAME);
        verificationService.setTokenMaxAge(TOKEN_MAX_AGE);
    }

    @Test
    public void testSendEmail() throws Exception {
        Profile profile = new Profile();

        profile.setId(PROFILE_ID);
        profile.setEmail(PROFILE_EMAIL);

        verificationService.sendEmail(profile, VERIFICATION_BASE_URL);

        verify(tokenRepository).insert(any(VerificationToken.class));
        verify(emailFactory).getEmail(FROM, TO, null, null, SUBJECT, TEMPLATE_NAME, VERIFICATION_TEMPLATE_ARGS, true);
        verify(email).send();
    }

    @Test
    public void testVerifyToken() throws Exception {
        verificationService.verifyToken(NORMAL_TOKEN_ID.toString(), callback);

        verify(tokenRepository).findById(NORMAL_TOKEN_ID.toString());
        verify(callback).doOnSuccess(getNormalToken());
    }

    @Test(expected = NoSuchVerificationTokenException.class)
    public void testVerifyInvalidToken() throws Exception {
        verificationService.verifyToken("1", callback);
    }

    @Test(expected = ExpiredVerificationTokenException.class)
    public void testVerifyExpiredToken() throws Exception {
        verificationService.verifyToken(EXPIRED_TOKEN_ID.toString(), callback);
    }

    private VerificationToken getNormalToken() {
        VerificationToken token = new VerificationToken();
        token.setId(NORMAL_TOKEN_ID);
        token.setProfileId(PROFILE_ID.toString());
        token.setTimestamp(new Date());

        return token;
    }

    private VerificationToken getExpiredToken() {
        VerificationToken token = new VerificationToken();
        token.setId(EXPIRED_TOKEN_ID);
        token.setProfileId(PROFILE_ID.toString());
        token.setTimestamp(new Date(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(TOKEN_MAX_AGE)));

        return token;
    }

}
