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
package org.craftercms.profile.utils;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.jackson.CustomSerializationObjectMapper;
import org.craftercms.commons.jackson.ObjectIdDeserializer;
import org.craftercms.commons.jackson.ObjectIdSerializer;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.api.TenantActions;
import org.craftercms.profile.api.TenantPermission;
import org.craftercms.profile.repositories.AccessTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author avasquez
 */
public class AccessTokenManagerCliTest {

    private AccessTokenRepository tokenRepository;
    private CustomSerializationObjectMapper objectMapper;
    private List<AccessToken> tokens;

    public AccessTokenManagerCliTest() {
        tokens = new ArrayList<>();
    }

    @Before
    public void setUp() throws Exception {
        createTestAccessTokenRepository();
        createTestObjectMapper();

        tokens.clear();
    }

    @Test
    public void testAdd() throws Exception {
        StringWriter inputWriter = new StringWriter();
        PrintWriter printInputWriter = new PrintWriter(inputWriter);

        printInputWriter.println("crafterengine");
        printInputWriter.println("03/05/2014");
        printInputWriter.println("corporate");
        printInputWriter.println(StringUtils.join(TenantActions.ALL_ACTIONS, ','));
        printInputWriter.println("n");

        BufferedReader stdIn = new BufferedReader(new StringReader(inputWriter.toString()));
        PrintWriter stdOut = new PrintWriter(System.out);

        AccessTokenManagerCli cli = new AccessTokenManagerCli(stdIn, stdOut, tokenRepository, objectMapper);
        cli.run("-add");

        assertNotNull(tokens);
        assertEquals(1, tokens.size());

        AccessToken expectedToken = createTestToken();

        assertEquals(expectedToken.getApplication(), tokens.get(0).getApplication());
        assertEquals(expectedToken.getExpiresOn(), tokens.get(0).getExpiresOn());
        assertEquals(expectedToken.getTenantPermissions(), tokens.get(0).getTenantPermissions());
    }

    @Test
    public void testRemove() throws Exception {
        String id = "507c7f79bcf86cd7994f6c0e";

        tokens.add(createTestToken());

        BufferedReader stdIn = new BufferedReader(new StringReader(""));
        PrintWriter stdOut = new PrintWriter(System.out);

        AccessTokenManagerCli cli = new AccessTokenManagerCli(stdIn, stdOut, tokenRepository, objectMapper);
        cli.run("-remove", id);

        assertNotNull(tokens);
        assertEquals(0, tokens.size());
    }

    @Test
    public void testList() throws Exception {
        tokens.add(createTestToken());

        StringWriter outputWriter = new StringWriter();

        BufferedReader stdIn = new BufferedReader(new StringReader(""));
        PrintWriter stdOut = new PrintWriter(outputWriter);

        AccessTokenManagerCli cli = new AccessTokenManagerCli(stdIn, stdOut, tokenRepository, objectMapper);
        cli.run("-list");

        assertEquals("[{\"application\":\"crafterengine\",\"tenantPermissions\":[{\"allowedActions\":" +
                        "[\"authenticate\",\"update\",\"count\",\"manageProfiles\",\"delete\",\"read\",\"readAll\"," +
                        "\"create\"],\"tenant\":\"corporate\"}],\"expiresOn\":\"2014-03-05T06:00:00.000+0000\"," +
                        "\"id\":\"507c7f79bcf86cd7994f6c0e\"}]",
                outputWriter.toString().trim());
    }

    private void createTestAccessTokenRepository() throws MongoDataException {
        tokenRepository = mock(AccessTokenRepository.class);

        when(tokenRepository.findAll()).thenReturn(tokens);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();

                tokens.add((AccessToken) args[0]);

                return null;
            }

        }).when(tokenRepository).save(any(AccessToken.class));

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();

                for (int i = 0; i < tokens.size(); i++) {
                    if (tokens.get(i).getId().equals(args[0])) {
                        tokens.remove(i);
                        break;
                    }
                }

                return null;
            }

        }).when(tokenRepository).removeById(any(String.class));
    }

    private void createTestObjectMapper() {
        objectMapper = new CustomSerializationObjectMapper();
        objectMapper.setSerializers(Arrays.<JsonSerializer>asList(new ObjectIdSerializer()));
        objectMapper.setDeserializers(Collections.<Class, JsonDeserializer>singletonMap(ObjectId.class,
                new ObjectIdDeserializer()));
        objectMapper.init();
    }

    private AccessToken createTestToken() throws ParseException {
        TenantPermission permission = new TenantPermission("corporate");
        permission.allow(TenantActions.ALL_ACTIONS);

        AccessToken token = new AccessToken();
        token.setId(new ObjectId("507c7f79bcf86cd7994f6c0e"));
        token.setApplication("crafterengine");
        token.setExpiresOn(new SimpleDateFormat("MM/dd/yyyy").parse("03/05/2014"));
        token.setTenantPermissions(Arrays.asList(permission));

        return token;
    }

}
