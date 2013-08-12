package org.craftercms.profile.testing.unit;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.profile.domain.Attribute;

import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.repositories.TenantRepository;

import org.craftercms.profile.services.impl.SchemaServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SchemaServiceTest {
	
	@Mock
	private TenantRepository tenantRepository;
	
	@InjectMocks
	private SchemaServiceImpl schemaService;
	
	private Tenant currentTenant;
	
	@Before
	public void startup() {
		currentTenant = getTenant();
		when(tenantRepository.getTenantByName("test")).thenReturn(currentTenant);
	}
	
	@Test
	public void testGetSchemaByTenantName() {
		Schema schema = schemaService.geSchemaByTenantName("test");
		assertNotNull(schema);
	}
	@Test
	public void testSetAttribute() {
		Attribute a = new Attribute();
		a.setLabel("Telephone");
		a.setName("telephone");
		a.setOrder(200);
		a.setRequired(true);
		schemaService.setAttribute("test", a);
		Mockito.verify(tenantRepository).setAttribute(Mockito.<String>any(), Mockito.<Attribute>any());
	}
	@Test
	public void testDeleteAttribute() {
		
		schemaService.deleteAttribute("test", "telephone");
		Mockito.verify(tenantRepository).deleteAttribute("test", "telephone");
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

}
