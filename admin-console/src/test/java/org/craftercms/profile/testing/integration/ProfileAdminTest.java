package org.craftercms.profile.testing.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.craftercms.profile.testing.IntegrationTestingBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProfileAdminTest extends IntegrationTestingBase {
	
	@Test
	public void testLogin() {
		System.out.println("----->>>> testLogin");
		WebDriver driver = getDriver();
		System.out.println("----->>>> testLogin 1");
		driver.get(baseUrl + WEB_APP_URL + "/login");
		System.out.println("----->>>> testLogin 2");
		loginAsAdmin(driver);
		System.out.println("----->>>> testLogin 3");
		System.out.println("----->>>> " +driver.getTitle());
		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));

	}

	
	
//	@Test
//	public void testNewProfile() {
//		WebDriver driver = getDriver();
//
//		driver.get(baseUrl + WEB_APP_URL + "/login");
//
//		loginAsAdmin(driver);
//		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));
//		
//		try {
//			selectItem(driver, "input[id='newtestuser']");
//			
//			WebElement deleteLink = driver.findElement(By.id("Delete"));
//			deleteLink.click();
//			assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));
//		
//		} catch (NoSuchElementException e) {
//	    } 
//		
//		createNewProfile(driver, "newtestuser");
//		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));
//	}
//
//	@Test
//	public void testUpdateProfile() {
//		WebDriver driver = getDriver();
//		driver.get(baseUrl + WEB_APP_URL + "/login");
//		
//		loginAsAdmin(driver);
//		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));
//		
//		try {
//			WebElement linkUpdateUser = driver.findElement(By.cssSelector("a[id='newtestuser']"));
//			linkUpdateUser.click();
//		} catch(Exception e) {
//			createNewProfile(driver, "newtestuser");
//			WebElement linkUpdateUser = driver.findElement(By.cssSelector("a[id='newtestuser']"));
//			linkUpdateUser.click();
//		}
//		
//		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console - Update Profile"));
//		WebElement updatePass = driver.findElement(By.id("password")); 
//		WebElement updateConffirmPass = driver.findElement(By.id("confirmPassword"));
//		updatePass.sendKeys("updatetestuser");
//		updateConffirmPass.sendKeys("updatetestuser");
//		WebElement role = driver.findElement(By.id("roles"));
//		List<WebElement> options = role.findElements(By.tagName("option"));
//		if (options != null) {
//			for (WebElement e: options) {
//				if (!e.isSelected() && !e.getText().equalsIgnoreCase("SUPERADMIN")) {
//					e.click();
//					break;
//				}
//			}
//		}
//		WebElement createButton = driver.findElement(By.id("Update"));
//		createButton.click();
//		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));
//	}
//	
//	@Test
//	public void testDeleteProfile() {
//		WebDriver driver = getDriver();
//		driver.get(baseUrl + WEB_APP_URL + "/login");
//
//		loginAsAdmin(driver);
//
//		createNewProfile(driver, "deletetestuser");
//		assertEquals("Title error, Wrong page, Expected Profile List " ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));
//		
//		WebElement linkDeleteUser = driver.findElement(By.id("deletetestuser"));
//		linkDeleteUser.click();
//		WebElement deleteLink = driver.findElement(By.id("Delete"));
//		deleteLink.click();
//		assertEquals("Title error, Wrong page, Expected Profile List " ,true, driver.getTitle().contains("Crafter Admin Console Profile List"));
//
//	}
	
	private void createNewProfile(WebDriver driver, String profileUserName) {
		WebElement newLink = driver.findElement(By.id("New"));
		newLink.click();
		System.out.println("----->>>> testNewProfile 3 checking new form " + driver.getTitle());
		assertEquals("Title error, Wrong page" ,true, driver.getTitle().contains("Crafter Admin Console - New Profile"));
		WebElement newUsername = driver.findElement(By.id("username")); 
		WebElement newPass = driver.findElement(By.id("password")); 
		WebElement newConffirmPass = driver.findElement(By.id("confirmPassword"));
		WebElement role = driver.findElement(By.id("roles"));
		List<WebElement> options = role.findElements(By.tagName("option"));
		if (options != null) {
			for (WebElement e: options) {
				if (!e.isSelected() && !e.getText().equalsIgnoreCase("SUPERADMIN")) {
					e.click();
					break;
				}
			}
		}
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
