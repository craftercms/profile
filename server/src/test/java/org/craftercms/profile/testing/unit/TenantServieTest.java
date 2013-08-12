package org.craftercms.profile.testing.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.services.ProfileService;
import org.craftercms.profile.services.RoleService;
import org.craftercms.profile.services.impl.MultiTenantServiceImpl;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class TenantServieTest {
	
	@Mock
	private TenantRepository tenantRepository;
	@Mock
	ProfileService profileService;
	@Mock
	private RoleService roleService;
	
	private List<Tenant> tenants;
	private Tenant current;
	
	private static final String VALID_ID = "111111111111111111111111";
	
	@InjectMocks 
	private MultiTenantServiceImpl multiTenantService;
	
	private Profile currentProfile;
	
	@Before
	public void startup() {
		current = getTenant();
		current.setId(new ObjectId(VALID_ID));
		tenants = new ArrayList<Tenant>();
		tenants.add(current);
		currentProfile = getProfile();
		when(tenantRepository.findAll()).thenReturn(tenants);
		when(tenantRepository.findTenantById(new ObjectId(VALID_ID))).thenReturn(current);
		when(tenantRepository.getTenantByName("test")).thenReturn(current);
		when(tenantRepository.getTenantRange("tenantName", "DESC", 1, 10)).thenReturn(tenants);
		when(tenantRepository.getTenants(new String[]{"tester"})).thenReturn(tenants);
		when(tenantRepository.count()).thenReturn(1l);
		when(tenantRepository.save(Mockito.<Tenant>any())).thenReturn(new Tenant());
		when(profileService.getProfileByTicket("ticket")).thenReturn(currentProfile);
	}
	
	@Test
    public void testGetAllTenants(){
		List<Tenant> tl = multiTenantService.getAllTenants();
    	assertNotNull(tl);
    	assertTrue(tl.size() > 0);

    }
	@Test
	public void testGetTenantById(){
		Tenant t = multiTenantService.getTenantById(VALID_ID);
		assertNotNull(t);
	}
	@Test
	public void testGetTenantByName(){
		Tenant t = multiTenantService.getTenantByName("test");
		assertNotNull(t);
	}
	@Test
	public void testgetTenantByTicket(){
		Tenant t = multiTenantService.getTenantByTicket("ticket");
		assertNotNull(t);
	}
	@Test
    public void testGetTenantRange(){
		List<Tenant> tl = multiTenantService.getTenantRange("tenantName","DESC", 1, 10);
    	assertNotNull(tl);
    	assertTrue(tl.size() > 0);

    }
	@Test
	public void testGetTenantByRoleName(){
		List<Tenant> tl = multiTenantService.getTenantsByRoleName("tester");
		assertNotNull(tl);
		assertTrue(tl.size() > 0);
		
	}
	@Test
	public void testGetTenantCount(){
		assertTrue(multiTenantService.getTenantsCount() > 0);
	}
	@Test
	public void testExist(){
		assertTrue(multiTenantService.exists("test"));
	}
	
	@Test
	public void testCreateTenant(){
		List<String> roles = new ArrayList<String>();
		roles.add("test");
		List<String> domains = new ArrayList<String>();
		domains.add("localhost");
		Tenant t = multiTenantService.createTenant("test", false, roles, domains, null);
		assertNotNull(t);
	}
	@Test
	public void testUpdateTenant(){
		List<String> roles = new ArrayList<String>();
		roles.add("test");
		List<String> domains = new ArrayList<String>();
		domains.add("localhost");
		Tenant t = multiTenantService.updateTenant(VALID_ID,"test", roles, domains);
		assertNotNull(t);
	}
	@Test
	public void testDeleteTenant(){
		
		multiTenantService.deleteTenant("test");
		Mockito.verify(tenantRepository).delete(Mockito.<ObjectId>any());
	}
	
	private Tenant getTenant() {
		Tenant t = new Tenant();
		List<String> domains = new ArrayList<String>();
		domains.add("localhost");
		t.setDomains(domains);
		List<String> roles = new ArrayList<String>();
		roles.add("test");
		t.setRoles(roles);
		t.setTenantName("test");
		Schema schema = new Schema();
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute a = new Attribute();
		a.setLabel("Name");
		a.setName("name");
		a.setOrder(1);
		a.setRequired(false);
		attributes.add(a);
		schema.setAttributes(attributes);
		t.setSchema(schema);
		return t;
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
