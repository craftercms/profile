package org.craftercms.profile.testing.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.exceptions.InvalidEmailException;
import org.craftercms.profile.repositories.ProfileRepository;
import org.craftercms.profile.repositories.TicketRepository;
import org.craftercms.profile.services.EmailValidatorService;
import org.craftercms.profile.services.impl.ProfileServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    EmailValidatorService emailValidatorService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private Profile current;
    private Ticket ticket;

    private List<String> attributesNames;
    private List<String> ids;
    private List<Profile> testerList;

    private static final String VALID_ID = "111111111111111111111111";

    @Before
    public void startup() {
        //ps = new ProfileServiceImpl();
        current = getProfile();
        ticket = getTicket();
        testerList = new ArrayList<Profile>();
        testerList.add(current);
        ids = new ArrayList<String>();
        ids.add("ok");
        attributesNames = new ArrayList<String>();
        attributesNames.add("first-name");
        attributesNames.add("last-name");
        //when(profileRepository.save(current)).thenReturn(current);

        when(profileRepository.getProfile("ok")).thenReturn(current);
        when(profileRepository.getProfile("ok", attributesNames)).thenReturn(current);

        when(profileRepository.save(Mockito.<Profile>any())).thenReturn(new Profile());
        when(emailValidatorService.validateEmail(Mockito.<String>any())).thenReturn(true);


        when(profileRepository.getAllAttributes("ok")).thenReturn(current.getAttributes());
        when(profileRepository.getAttribute("ok", "first-name")).thenReturn(current.getAttributes());
        when(profileRepository.getAttributes("ok", attributesNames)).thenReturn(current.getAttributes());
        when(profileRepository.getProfileByUserName(ticket.getUsername(), ticket.getTenantName(),
            null)).thenReturn(current);
        when(profileRepository.getProfileByUserName(ticket.getUsername(), ticket.getTenantName(),
            attributesNames)).thenReturn(current);
        when(profileRepository.getProfileByUserNameWithAllAttributes("test", "test")).thenReturn(current);
        when(profileRepository.findByRolesAndTenantName("tester", "test")).thenReturn(testerList);
        when(profileRepository.getProfilesCount("test")).thenReturn(1l);
        when(profileRepository.getProfilesWithAttributes(attributesNames)).thenReturn(testerList);
        when(profileRepository.findOne(new ObjectId(VALID_ID))).thenReturn(current);
        //(profileRepository.deleteAllAttributes("ok")).thenReturn(current);

        when(ticketRepository.getByTicket("ticketStr")).thenReturn(ticket);

    }

    @Test
    public void testGetProfile() {
        Profile p = profileService.getProfile("ok");
        assertNotNull(p);

    }

    @Test
    public void testGetProfileAttributes() {
        Profile p = profileService.getProfile("ok", attributesNames);
        assertNotNull(p);

    }

    @Test
    public void testGetAllAttributes() {
        Map<String, Serializable> attrs = profileService.getAllAttributes("ok");
        assertNotNull(attrs);

    }

    @Test
    public void testGetAttribute() {
        Map<String, Serializable> attrs = profileService.getAttribute("ok", "first-name");
        assertNotNull(attrs);

    }

    @Test
    public void testGetAttributes() {
        Map<String, Serializable> attrs = profileService.getAttributes("ok", attributesNames);
        assertNotNull(attrs);

    }

    @Test
    public void testGetProfileByTicket() {
        Profile p = profileService.getProfileByTicket("ticketStr");
        assertNotNull(p);

    }

    @Test
    public void testGetProfileByTicketAttributes() {
        Profile p = profileService.getProfileByTicket("ticketStr", attributesNames);
        assertNotNull(p);

    }

    @Test
    public void testGetProfileByTicketWithAllAttributes() {
        Profile p = profileService.getProfileByTicketWithAllAttributes("ticketStr");
        assertNotNull(p);

    }

    @Test
    public void testGetProfilesByRoleName() {
        List<Profile> p = profileService.getProfilesByRoleName("tester", "test");
        assertNotNull(p);
        assertTrue(p.size() > 0);

    }

    @Test
    public void testGetProfilesCount() {
        long n = profileService.getProfilesCount("test");
        assertTrue(n > 0);

    }

    @Test
    public void testProfilesWithAttributes() {
        List<Profile> p = profileService.getProfilesWithAttributes(ids);
        assertNotNull(p);
    }

    @Test
    public void testGetProfileWithAllAttributes() {
        Profile p = profileService.getProfileWithAllAttributes(VALID_ID);
        assertNotNull(p);
    }

    @Test
    public void testCreateProfile() {
        Profile p = null;
        try {
            p = profileService.createProfile(current.getUserName(), "test", true, "test", "test@test.com",
                current.getAttributes(), current.getRoles(), null);
        } catch (InvalidEmailException e) {
            fail(e.getMessage());
        }
        assertNotNull(p);

    }

    @Test
    public void testDeleteAllAttributes() {
        profileService.deleteAllAttributes("ok");
        Mockito.verify(profileRepository).deleteAllAttributes(Mockito.eq("ok"));
    }

    @Test
    public void testDeleteAttributes() {
        profileService.deleteAttributes("ok", attributesNames);
        Mockito.verify(profileRepository).deleteAttributes(Mockito.eq("ok"), Mockito.<List<String>>any());
    }

    @Test
    public void testActiveProfile() {
        profileService.activeProfile(VALID_ID, true);
        Mockito.verify(profileRepository).save(Mockito.<Profile>any());
    }

    @Test
    public void testDeleteProfiles() {
        when(profileRepository.getProfilesByTenantName("test")).thenReturn(testerList);
        profileService.deleteProfiles("test");
        Mockito.verify(profileRepository).delete(Mockito.eq(testerList));
    }

    @Test
    public void testSetAttributes() {
        //when(profileRepository.getProfilesByTenantName("test")).thenReturn(testerList);
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("address", "CR");
        map.put("phone-number", "911");
        profileService.setAttributes("ok", map);
        Mockito.verify(profileRepository).setAttributes(Mockito.eq("ok"), Mockito.eq(map));
    }

    @Test
    public void testUpdateProfile() {
        //when(profileRepository.getProfilesByTenantName("test")).thenReturn(testerList);
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("address", "CR");
        map.put("phone-number", "911");
        Profile p = profileService.updateProfile(VALID_ID, "test", "test", false, "test", "test@test.com", map,
            current.getRoles());
        assertNotNull(p);
        //Mockito.verify(profileRepository).setAttributes(Mockito.eq("ok"),Mockito.eq(map));
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

    private Ticket getTicket() {
        Ticket ticket = new Ticket();
        ticket.setDate(new Date());
        ticket.setSeries("series");
        ticket.setTenantName("test");
        ticket.setTokenValue("token");
        ticket.setUsername("test");

        return ticket;
    }

}
