package org.craftercms.profile.testing.integration;

import java.util.List;

import org.craftercms.profile.testing.IntegrationTestingBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class ITProfileAdminTest extends IntegrationTestingBase {

    @Test
    public void testLogin() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile List"));
    }

    @Test
    public void testNewProfile() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile List"));
        try {
            selectItem(driver, "input[id='newtestuser']");
            WebElement deleteLink = driver.findElement(By.id("Delete"));
            deleteLink.click();
            assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile "
                + "List"));
        } catch (NoSuchElementException e) {
        }
        createNewProfile(driver, "newtestuser");
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile List"));
    }

    @Test
    public void testUpdateProfile() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile List"));
        try {
            WebElement linkUpdateUser = driver.findElement(By.cssSelector("a[id='newtestuser']"));
            linkUpdateUser.click();
        } catch (Exception e) {
            createNewProfile(driver, "newtestuser");
            WebElement linkUpdateUser = driver.findElement(By.cssSelector("a[id='newtestuser']"));
            linkUpdateUser.click();
        }
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Update " +
            "Profile"));
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement updatePass = driver.findElement(By.id("password"));
        WebElement updateConffirmPass = driver.findElement(By.id("confirmPassword"));
        updatePass.clear();
        updateConffirmPass.clear();
        updatePass.sendKeys("updatetestuser");
        updateConffirmPass.sendKeys("updatetestuser");
        emailInput.clear();
        emailInput.sendKeys("testingmail@mail.com");
        WebElement role = driver.findElement(By.id("roles"));
        List<WebElement> options = role.findElements(By.tagName("option"));
        if (options != null) {
            for (WebElement e : options) {
                if (!e.isSelected() && !e.getText().equalsIgnoreCase("SUPERADMIN")) {
                    e.click();
                    break;
                }
            }
        }
        WebElement createButton = driver.findElement(By.id("Update"));
        createButton.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile List"));
    }

    @Test
    public void testUpdateToInactiveProfile() {
        WebDriver driver = getDriver();
        driver.get(baseUrl + WEB_APP_URL + "/login");
        loginAsAdmin(driver);
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile List"));
        try {
            WebElement linkUpdateUser = driver.findElement(By.cssSelector("a[id='newtestuser']"));
            linkUpdateUser.click();
        } catch (Exception e) {
            createNewProfile(driver, "newtestuser");
            WebElement linkUpdateUser = driver.findElement(By.cssSelector("a[id='newtestuser']"));
            linkUpdateUser.click();
        }
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - Update " +
            "Profile"));
        WebElement activeInput = driver.findElement(By.id("active"));
        activeInput.sendKeys("testingmail@mail.com");

        WebElement updateButton = driver.findElement(By.id("Update"));
        updateButton.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console Profile List"));
        WebElement tdUpdateUser = driver.findElement(By.cssSelector("td[id='newtestuserStatus']"));

    }

    private void createNewProfile(WebDriver driver, String profileUserName) {
        WebElement newLink = driver.findElement(By.id("New"));
        newLink.click();
        assertEquals("Title error, Wrong page", true, driver.getTitle().contains("Crafter Admin Console - New " +
            "Profile"));
        WebElement newUsername = driver.findElement(By.id("username"));
        WebElement email = driver.findElement(By.id("emailAccount"));

        WebElement newPass = driver.findElement(By.id("passwordAccount"));
        WebElement newConffirmPass = driver.findElement(By.id("confirmPassword"));
        WebElement role = driver.findElement(By.id("roles"));
        List<WebElement> options = role.findElements(By.tagName("option"));
        if (options != null) {
            for (WebElement e : options) {
                if (!e.isSelected() && !e.getText().equalsIgnoreCase("SUPERADMIN")) {
                    e.click();
                    break;
                }
            }
        }
        email.sendKeys(profileUserName + "@test.com");
        newUsername.sendKeys(profileUserName);
        newPass.sendKeys(profileUserName);
        newConffirmPass.sendKeys(profileUserName);

        WebElement createButton = driver.findElement(By.name("Create"));
        createButton.click();
    }

    private void selectItem(WebDriver driver, String cssSelectorItem) {
        WebElement linkUser = driver.findElement(By.cssSelector(cssSelectorItem));
        linkUser.click();
    }

}
