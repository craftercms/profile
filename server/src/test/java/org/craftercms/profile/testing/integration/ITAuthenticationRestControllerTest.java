package org.craftercms.profile.testing.integration;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.testing.BaseTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ITAuthenticationRestControllerTest extends BaseTest {

    @Test
    public void testGetAppToken() throws AppAuthenticationFailedException {
        String token = profileRestClientImpl.getAppToken("crafterengine", "crafterengine");
        assertNotNull(token);

    }

    @Test
    public void testGetAppTokenFail() throws AppAuthenticationFailedException {
        String token = profileRestClientImpl.getAppToken("notvaliduserapp", "notvalidpasswordapp");
        assertTrue(token == null);
    }

    @Test
    public void testGetTicket() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "admin", "admin", "craftercms");
        assertNotNull(ticket);
    }

    @Test
    public void testGetTicketFail() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "adminfake", "adminfake", "craftercms");

        assertTrue(ticket == null);
    }

    @Test
    public void testIsValidTicket() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "admin", "admin", "craftercms");
        assertNotNull(ticket);
        assertTrue(profileRestClientImpl.isTicketValid(appToken, ticket));
    }

    @Test
    public void testInvalidTicket() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "admin", "admin", "craftercms");
        assertNotNull(ticket);
        assertTrue(profileRestClientImpl.isTicketValid(appToken, ticket));
        profileRestClientImpl.invalidateTicket(appToken, ticket);
        assertTrue(!profileRestClientImpl.isTicketValid(appToken, ticket));
    }

}
