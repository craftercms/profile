package org.craftercms.profile.testing.integration;

import java.util.List;

import org.craftercms.profile.testing.IntegrationTestingBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class ITTenantAdminTest extends IntegrationTestingBase {

    @Test
    public void testNewTenant() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        WebElement newLink = driver.findElement(By.id("GetTenants"));
        newLink.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Tenant List"));
        createNewTenant(driver, "newTenantName", "localhost");
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Tenant List"));
    }

    @Test
    public void testUpdateTenant() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/gettenants");
        loginAsAdmin(driver);
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Tenant List"));
        WebElement linkUpdateTenant = driver.findElement(By.cssSelector("a[id='craftercms']"));
        linkUpdateTenant.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Update " +
            "Tenant"));
        WebElement role = driver.findElement(By.id("roles"));
        List<WebElement> options = role.findElements(By.tagName("option"));
        if (options != null) {
            for (WebElement e : options) {
                if (!e.isSelected() && !e.getText().equalsIgnoreCase("SUPERADMIN")) {
                    e.click();
                }
            }
        }
        WebElement updateButton = driver.findElement(By.id("UpdateTenant"));
        updateButton.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Tenant List"));
    }

    private void createNewTenant(WebDriver driver, String newTenantName, String domainName) {
        WebElement newLink = driver.findElement(By.id("NewTenant"));
        newLink.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - New Tenant"));
        WebElement newTenant = driver.findElement(By.id("tenantName"));
        //WebElement domain = driver.findElement(By.cssSelector("input[id='field']"));
        WebElement domain = driver.findElement(By.id("domains"));
        WebElement role = driver.findElement(By.id("roles"));
        List<WebElement> options = role.findElements(By.tagName("option"));
        if (options != null) {
            for (WebElement e : options) {
                if (!e.isSelected() && !e.getText().equalsIgnoreCase("SUPERADMIN")) {
                    e.click();
                }
            }
        }
        newTenant.sendKeys("newTenantName");
        domain.sendKeys(domainName);
        WebElement createButton = driver.findElement(By.id("CreateTenant"));
        createButton.click();
    }
}
