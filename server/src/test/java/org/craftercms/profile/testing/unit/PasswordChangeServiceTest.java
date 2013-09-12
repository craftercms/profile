package org.craftercms.profile.testing.unit;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.exceptions.CipherException;
import org.craftercms.profile.exceptions.ExpiryDateException;
import org.craftercms.profile.exceptions.MailException;
import org.craftercms.profile.exceptions.NoSuchProfileException;
import org.craftercms.profile.security.util.crypto.CipherPasswordChangeToken;
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

    private static final String CONFIG_FILE = "test.properties";
    protected static Properties sConfig;

    @Mock
    private ProfileService profileService;

    @Mock
    private MailService mailService;

    @Mock
    private CipherPasswordChangeToken cipherPasswordChangeToken;

    @InjectMocks
    private PasswordServiceImpl passwordChangeServiceImpl;

    private static final String VALID_ID = "111111111111111111111111";

    private Profile current;

    private static final String DUMMY_TOKEN = "lF5wdKR5hy6nbVKsYRI8l7Xfl";
    private static final String REAL_TOKEN =
        "NbFqdgFOvpHm5g5/N9bl9AfbA4vP2Zuq7EVKa/SVya0q/zO7pH86/2MuX0CL60QT|cl96lrLVg4bA38AJS4uq8cA==";

    @Before
    public void startup() throws Exception {
        sConfig = new Properties();
        sConfig.load(PasswordChangeServiceTest.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
        String passwordChangeKeyFile = sConfig.getProperty("craftercms.test.password.key.file");
        current = getProfile();
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, -120);
        String date = DateFormat.getDateTimeInstance().format(expiryDate.getTime());
        String token = "test" + "|" + "test" + "|" + date;
        cipherPasswordChangeToken.setEncryptionKeyFile(new File(passwordChangeKeyFile));
        when(profileService.getProfileByUserName("test", "test")).thenReturn(current);
        when(profileService.getProfileByUserNameWithAllAttributes("test", "test")).thenReturn(current);
        when(profileService.updateProfile(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<Boolean>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<Map<String,
            Serializable>>any(), Mockito.<List<String>>any())).thenReturn(current);
        when(cipherPasswordChangeToken.decrypt(Mockito.<String>any())).thenReturn(token);
        when(cipherPasswordChangeToken.encrypt(Mockito.<String>any())).thenReturn
            ("NbFqdgFOvpHm5g5/N9bl9AfbA4vP2Zuq7EVKa/SVya0q/zO7pH86/2MuX0CL60QT|cl96lrLVg4bA38AJS4uq8cA==");

    }

    @Test
    public void testForgotPassword() throws CipherException, MailException, NoSuchProfileException {
        //passwordChangeServiceImpl.setProfileCipherKey("lF5wdKR5hy6nbVKsYRI8l7XflveFGRAFIX1cGzu1FAI=");
        passwordChangeServiceImpl.forgotPassword("url", "test", "test");

        Mockito.verify(mailService).sendMailTLS(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
            Mockito.<Map<String, Object>>any(), Mockito.<String>any(), Mockito.<String>any());
    }

    @Test(expected = ExpiryDateException.class)
    public void testResetPassword_expirydate() throws CipherException, NoSuchProfileException, ParseException,
        ExpiryDateException {
        when(profileService.getProfileByUserName("admin", "craftercms")).thenReturn(current);
        when(profileService.getProfileByUserNameWithAllAttributes("admin", "craftercms")).thenReturn(current);

        passwordChangeServiceImpl.resetPassword("newpass", REAL_TOKEN);
    }

    @Test(expected = NoSuchProfileException.class)
    public void testResetPassword_invalid_user() throws CipherException, NoSuchProfileException, ParseException,
        ExpiryDateException {
        Calendar expiryDate = Calendar.getInstance();

        String date = DateFormat.getDateTimeInstance().format(expiryDate.getTime());
        String token = "test1" + "|" + "test1" + "|" + date;
        when(cipherPasswordChangeToken.decrypt(Mockito.<String>any())).thenReturn(token);

        passwordChangeServiceImpl.resetPassword("newpass", REAL_TOKEN);
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
        profile.setEmail("test@test.com");
        profile.setTenantName("test");
        profile.setCreated(new Date());
        profile.setModified(new Date());
        profile.setAttributes(attributes);
        profile.setRoles(roles);
        profile.setId(new ObjectId(VALID_ID));
        return profile;
    }


}
