package org.craftercms.profile.testing.unit;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.services.MailService;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.impl.PasswordServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordChangeServiceTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private PasswordServiceImpl passwordChangeServiceImpl;

    private Profile current;

    private static final String DUMMY_TOKEN = "lF5wdKR5hy6nbVKsYRI8l7Xfl";
    private static final String REAL_TOKEN = "fRT9U4eQUmeO7iys/ZIf4cAK302ZbS8MC594C554hughangpbkpsJp3nz7rZmVcs";

    @Before
    public void startup() {
        current = getProfile();
        when(profileService.getProfileByUserName("tester", "test")).thenReturn(current);
        when(profileService.getProfileByUserNameWithAllAttributes("tester", "test")).thenReturn(current);
    }


    @Test
    public void testForgotPassword() throws CipherException, MailException, NoSuchProfileException {
        passwordChangeServiceImpl.setProfileCipherKey("lF5wdKR5hy6nbVKsYRI8l7XflveFGRAFIX1cGzu1FAI=");
        passwordChangeServiceImpl.forgotPassword("url", "tester", "test");

        Mockito.verify(mailService).sendMailTLS(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<Map<String, Object>>any(), Mockito.<String>any(), Mockito.<String>any());
    }

    @Test(expected = ExpiryDateException.class)
    public void testChangePassword_expirydate() throws CipherException, NoSuchProfileException, ParseException,
        ExpiryDateException {
        when(profileService.getProfileByUserName("admin", "craftercms")).thenReturn(current);
        when(profileService.getProfileByUserNameWithAllAttributes("admin", "craftercms")).thenReturn(current);
        passwordChangeServiceImpl.setProfileCipherKey("lF5wdKR5hy6nbVKsYRI8l7XflveFGRAFIX1cGzu1FAI=");
        passwordChangeServiceImpl.changePassword("newpass", REAL_TOKEN);
    }

    @Test(expected = NoSuchProfileException.class)
    public void testChangePassword_invalid_user() throws CipherException, NoSuchProfileException, ParseException,
        ExpiryDateException {
        passwordChangeServiceImpl.setProfileCipherKey("lF5wdKR5hy6nbVKsYRI8l7XflveFGRAFIX1cGzu1FAI=");
        passwordChangeServiceImpl.changePassword("newpass", REAL_TOKEN);
    }

    @Test(expected = CipherException.class)
    public void testChangePassword_dummy_token() throws CipherException, NoSuchProfileException, ParseException,
        ExpiryDateException {
        passwordChangeServiceImpl.setProfileCipherKey("lF5wdKR5hy6nbVKsYRI8l7XflveFGRAFIX1cGzu1FAI=");
        passwordChangeServiceImpl.changePassword("newpass", DUMMY_TOKEN);
    }

    private Profile getProfile() {
        Profile profile = new Profile();
        Map<String, Serializable> attributes = new HashMap<String, Serializable>();
        attributes.put("first-name", "test");
        attributes.put("last-name", "test");
        List<String> roles = new ArrayList<String>();
        roles.add("SOCIAL_USER");
        profile.setUserName("test");
        PasswordEncoder encoder = new Md5PasswordEncoder();
        String hashedPassword = encoder.encodePassword("test", null);
        profile.setPassword(hashedPassword);
        profile.setActive(true);
        profile.setTenantName("test");
        profile.setCreated(new Date());
        profile.setModified(new Date());
        profile.setAttributes(attributes);
        profile.setRoles(roles);
        return profile;
    }


}
