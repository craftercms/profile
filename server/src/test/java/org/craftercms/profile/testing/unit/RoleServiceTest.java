package org.craftercms.profile.testing.unit;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.profile.domain.Attribute;
import org.craftercms.profile.domain.Role;
import org.craftercms.profile.domain.Schema;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.profile.repositories.RoleRepository;
import org.craftercms.profile.repositories.TenantRepository;
import org.craftercms.profile.services.impl.RoleServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role current;
    private List<Role> roles;

    @Before
    public void startup() {
        current = new Role();
        current.setRoleName("TESTER");
        roles = new ArrayList<Role>();
        roles.add(current);
        when(roleRepository.findByRoleName("TESTER")).thenReturn(current);
        when(roleRepository.findAll()).thenReturn(roles);
        when(roleRepository.save(Mockito.<Role>any())).thenReturn(new Role());
    }

    @Test
    public void testGetRole() {
        Role r = roleService.getRole("TESTER");
        assertNotNull(r);
    }

    @Test
    public void testGetAllRole() {
        List<Role> l = roleService.getAllRoles();
        assertNotNull(l);
        assertTrue(l.size() > 0);

    }

    @Test
    public void testCreateRole() {
        Role r = roleService.createRole("TESTER", null);
        assertNotNull(r);
    }

    @Test
    public void testDeleteRole() {
        List<Tenant> lt = new ArrayList<Tenant>();
        when(tenantRepository.getTenants(new String[] {"TESTER"})).thenReturn(lt);
        roleService.deleteRole("TESTER", null);
        Mockito.verify(roleRepository).delete(Mockito.<Role>any());
    }

    @Test
    public void testDeleteAllRole() {
        roleService.deleteAllRoles();
        Mockito.verify(roleRepository).delete(Mockito.<Role>any());
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
