package org.craftercms.profile.testing;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class IntegrationTestingBase {
	
	protected static final String WEB_APP_URL = "/crafter-profile-admin-console";

	private WebDriver mDriver = null;
	private boolean mAutoQuitDriver = true;

	protected static Properties sConfig;
	protected static DesiredCapabilities sCaps;

	protected static String baseUrl;

	private static final String CONFIG_FILE = "test.properties";

	@BeforeClass
	public static void configure() throws IOException {
		// Read config file
		sConfig = new Properties();
		
		sConfig.load(IntegrationTestingBase.class.getClassLoader().getResourceAsStream(
				CONFIG_FILE));

		// Prepare capabilities
		sCaps = new DesiredCapabilities();
		sCaps.setJavascriptEnabled(true);
		sCaps.setCapability("takesScreenshot", false);

		baseUrl = sConfig.getProperty("craftercms.test.base.url");
	}

	@Before
	public void prepareDriver() throws Exception {
		mDriver = new FirefoxDriver();
	}

	protected WebDriver getDriver() {
		return mDriver;
	}

	protected void disableAutoQuitDriver() {
		mAutoQuitDriver = false;
	}

	protected void enableAutoQuitDriver() {
		mAutoQuitDriver = true;
	}

	protected boolean isAutoQuitDriverEnabled() {
		return mAutoQuitDriver;
	}

	@After
	public void quitDriver() {
		if (mAutoQuitDriver && mDriver != null) {
			mDriver.quit();
			mDriver = null;
		}
	}
	
	protected void loginAsAdmin(WebDriver driver) {
		WebElement inputUsername = driver.findElement(By.id("username")); 
		WebElement inputPass = driver.findElement(By.id("password")); 
		WebElement loginButton = driver.findElement(By.id("login")); 
		inputUsername.sendKeys("admin");
		inputPass.sendKeys("admin");
		loginButton.click();
	}

}
