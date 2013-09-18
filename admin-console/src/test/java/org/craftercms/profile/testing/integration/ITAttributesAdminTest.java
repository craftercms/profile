package org.craftercms.profile.testing.integration;

import java.util.Set;

import org.craftercms.profile.testing.IntegrationTestingBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class ITAttributesAdminTest extends IntegrationTestingBase {

    @Test
    public void testAddAttributes() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        WebElement tenantsLink = driver.findElement(By.id("GetTenants"));
        tenantsLink.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Tenant List"));
        WebElement linkUpdateTenant = driver.findElement(By.cssSelector("a[id='craftercms']"));
        linkUpdateTenant.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Update " +
            "Tenant"));
        WebElement attributesLink = driver.findElement(By.id("ManageAttributes"));
        attributesLink.click();
        // Switch to edit window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);

            if (driver.getCurrentUrl().contains("getprops")) {
                break;
            }
        }
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
        deleteAttribute("a[id='phone']", driver);
        WebElement newAttributeLink = driver.findElement(By.id("New"));
        newAttributeLink.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - New Attribute"));
        WebElement name = driver.findElement(By.id("name"));
        name.sendKeys("phone");
        WebElement label = driver.findElement(By.id("label"));
        label.sendKeys("Phone");
        WebElement constraint = driver.findElement(By.id("constraint"));
        constraint.sendKeys("constraint");
        WebElement create = driver.findElement(By.id("CreateProp"));
        create.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
    }

    @Test
    public void testUpdateAttributes() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        WebElement tenantsLink = driver.findElement(By.id("GetTenants"));
        tenantsLink.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Tenant List"));
        WebElement linkUpdateTenant = driver.findElement(By.cssSelector("a[id='craftercms']"));
        linkUpdateTenant.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Update Tenant"));
        WebElement attributesLink = driver.findElement(By.id("ManageAttributes"));
        attributesLink.click();
        // Switch to edit window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
            if (driver.getCurrentUrl().contains("getprops")) {
                break;
            }
        }
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
        WebElement newAttributeLink = driver.findElement(By.id("New"));
        newAttributeLink.click();
        waitForElement(driver, 500, "newattribute");
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - New Attribute"));
        WebElement name = driver.findElement(By.id("name"));
        name.sendKeys("updateattribute1");
        WebElement label = driver.findElement(By.id("label"));
        label.sendKeys("updateattribute1");

        WebElement create = driver.findElement(By.id("CreateProp"));
        create.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
        WebElement linkUpdateAttr = driver.findElement(By.cssSelector("a[id='updateattribute1']"));
        linkUpdateAttr.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Update Attribute"));
        label = driver.findElement(By.id("label"));
        label.sendKeys("updateattribute2");
        create = driver.findElement(By.id("UpdateProp"));
        create.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
    }

    @Test
    public void testDeleteAttributes() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        WebElement tenantsLink = driver.findElement(By.id("GetTenants"));
        tenantsLink.click();
        waitForElement(driver, 100, "tenantlist");
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Tenant List"));
        WebElement linkUpdateTenant = driver.findElement(By.cssSelector("a[id='craftercms']"));
        linkUpdateTenant.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Update Tenant"));
        WebElement attributesLink = driver.findElement(By.id("ManageAttributes"));
        attributesLink.click();
        // Switch to edit window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
            if (driver.getCurrentUrl().contains("getprops")) {
                break;
            }
        }
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
        WebElement newAttributeLink = driver.findElement(By.id("New"));
        newAttributeLink.click();
        waitForElement(driver, 100, "newattribute");
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - New Attribute"));
        WebElement name = driver.findElement(By.id("name"));
        name.sendKeys("testDeleteAttributes");
        WebElement label = driver.findElement(By.id("label"));
        label.sendKeys("testDeleteAttributes");
        WebElement create = driver.findElement(By.id("CreateProp"));
        create.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
        deleteAttribute("a[id='testDeleteAttributes']", driver);
    }

    private void deleteAttribute(String selector, WebDriver driver) {
        try {
            WebElement deleteLink = driver.findElement(By.cssSelector("a[id='Delete']"));
            WebElement linkDeleteAttr = driver.findElement(By.cssSelector(selector));
            linkDeleteAttr.click();

            deleteLink.click();
            assertEquals("Title error, Wrong page, Expected Profile List ", true, driver.getTitle().contains("Crafter Admin Console - Attribute List"));
        } catch (Exception e) {
            System.out.println(" deleteAttribute 5 " + e.getMessage());
        }
    }

}
