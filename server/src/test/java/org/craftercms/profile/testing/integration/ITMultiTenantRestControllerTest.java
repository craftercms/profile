package org.craftercms.profile.testing.integration;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.testing.BaseTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ITMultiTenantRestControllerTest extends BaseTest {

    @Test
    public void testCreateTenant() throws AppAuthenticationFailedException {
        initAppToken();
        List<String> roles = new ArrayList<String>();
        roles.add("ADMIN");
        roles.add("TESTER");
        List<String> domains = new ArrayList<String>();
        domains.add("test.com");
        Tenant tenant = profileRestClientImpl.createTenant(appToken, "testtenant", roles, domains, false, false);
        assertNotNull(tenant);
        assertTrue(tenant.getTenantName().equals("testtenant"));
        profileRestClientImpl.deleteTenant(appToken, "testtenant");

    }

    @Test
    public void testUpdateTenant() throws AppAuthenticationFailedException {
        initAppToken();
        List<String> roles = new ArrayList<String>();
        roles.add("ADMIN");
        List<String> domains = new ArrayList<String>();
        domains.add("test.com");
        Tenant tenant = profileRestClientImpl.createTenant(appToken, "testtenant", roles, domains, false, false);
        assertNotNull(tenant);
        assertTrue(tenant.getTenantName().equals("testtenant"));
        roles = new ArrayList<String>();
        roles.add("USER");
        domains = new ArrayList<String>();
        domains.add("dev.test.com");
        Tenant t = profileRestClientImpl.updateTenant(appToken, tenant.getId(), "testtenant", roles, domains, false);
        boolean found = false;
        for (String d : t.getDomains()) {
            if (d.equalsIgnoreCase("dev.test.com")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        profileRestClientImpl.deleteTenant(appToken, "testtenant");

    }

    @Test
    public void testDeleteTenant() throws AppAuthenticationFailedException {
        initAppToken();
        List<String> roles = new ArrayList<String>();
        roles.add("ADMIN");
        List<String> domains = new ArrayList<String>();
        domains.add("test.com");
        Tenant tenant = profileRestClientImpl.createTenant(appToken, "testtenant", roles, domains, false, false);
        assertNotNull(tenant);
        assertTrue(tenant.getTenantName().equals("testtenant"));

        profileRestClientImpl.deleteTenant(appToken, "testtenant");
        List<Tenant> tenants = profileRestClientImpl.getAllTenants(appToken);
        boolean found = false;
        for (Tenant t : tenants) {
            if (t.getTenantName().equalsIgnoreCase("testtenant")) {
                found = true;
                break;
            }
        }
        assertEquals(false, found);
    }

    @Test
    public void testGetTenantByName() throws AppAuthenticationFailedException {
        initAppToken();

        Tenant tenant = profileRestClientImpl.getTenantByName(appToken, "craftercms");
        assertNotNull(tenant);
        assertTrue(tenant.getTenantName().equals("craftercms"));


    }

    @Test
    public void testGetTenantById() throws AppAuthenticationFailedException {
        initAppToken();
        List<String> roles = new ArrayList<String>();
        roles.add("ADMIN");
        List<String> domains = new ArrayList<String>();
        domains.add("test.com");
        Tenant tenant = profileRestClientImpl.createTenant(appToken, "testtenant", roles, domains, false, false);
        assertNotNull(tenant);
        Tenant tenantFound = profileRestClientImpl.getTenantById(appToken, tenant.getId());
        assertNotNull(tenantFound);
        assertTrue(tenant.getId().equals(tenantFound.getId()));
        profileRestClientImpl.deleteTenant(appToken, "testtenant");

    }

    @Test
    public void testGetTenantByTicket() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
        initAppToken();
        String ticket = profileRestClientImpl.getTicket(appToken, "admin", "admin", "craftercms");
        assertNotNull(ticket);
        Tenant tenant = profileRestClientImpl.getTenantByTicket(appToken, ticket);
        assertTrue(tenant.getTenantName().equals("craftercms"));
    }

    @Test
    public void testExistTenant() throws AppAuthenticationFailedException {
        initAppToken();
        List<String> roles = new ArrayList<String>();
        roles.add("ADMIN");
        roles.add("TESTER");
        List<String> domains = new ArrayList<String>();
        domains.add("test.com");
        Tenant tenant = profileRestClientImpl.createTenant(appToken, "testtenant", roles, domains, false, false);
        assertNotNull(tenant);
        assertTrue(tenant.getTenantName().equals("testtenant"));
        assertTrue(profileRestClientImpl.exitsTenant(appToken, "testtenant"));
        profileRestClientImpl.deleteTenant(appToken, "testtenant");

    }

    @Test
    public void testGetTenantCount() throws AppAuthenticationFailedException {
        initAppToken();

        assertTrue(profileRestClientImpl.getTenantCount(appToken) > 0);
    }

    @Test
    public void testGetTenantRange() throws AppAuthenticationFailedException {
        initAppToken();

        assertTrue(profileRestClientImpl.getTenantRange(appToken, "tenantName", "ASC", 0, 9).size() > 0);
    }

    @Test
    public void testGetAllTenant() throws AppAuthenticationFailedException {
        initAppToken();

        assertTrue(profileRestClientImpl.getAllTenants(appToken).size() > 0);
    }

}
