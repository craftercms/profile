package org.craftercms.profile.testing;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.impl.ProfileRestClientImpl;


public class BaseTest {
	
	protected static Properties sConfig;
    protected static String baseUrl;
	protected static ProfileRestClientImpl profileRestClientImpl;
	protected static String appToken = null;
	private static final String CONFIG_FILE = "test.properties";
	private static String appPassword;
	private static String appUsername;
	protected static String tenantName;
	

	@BeforeClass
	public static void configure() throws IOException {
		
		sConfig = new Properties();
		
		sConfig.load(BaseTest.class.getClassLoader().getResourceAsStream(
				CONFIG_FILE));

		baseUrl = sConfig.getProperty("craftercms.test.base.url");
		appUsername = sConfig.getProperty("craftercms.test.profile.appUsername");
		appPassword = sConfig.getProperty("craftercms.test.profile.appPassword");
		tenantName = sConfig.getProperty("craftercms.test.profile.tenantName");
		profileRestClientImpl = new ProfileRestClientImpl();
		if (sConfig.getProperty("craftercms.test.profile.port") != null) {
			profileRestClientImpl.setPort(Integer.parseInt(sConfig.getProperty("craftercms.test.profile.port")));
		}
		if (sConfig.getProperty("craftercms.test.profile.scheme") != null) {
			profileRestClientImpl.setScheme(sConfig.getProperty("craftercms.test.profile.scheme"));
		}
		if (sConfig.getProperty("craftercms.test.profile.host") != null) {
			profileRestClientImpl.setHost(sConfig.getProperty("craftercms.test.profile.host"));
		}
		if (sConfig.getProperty("craftercms.test.profile.profileAppPath") != null) {
			profileRestClientImpl.setProfileAppPath(sConfig.getProperty("craftercms.test.profile.profileAppPath"));
		}
		
		
	}

	

	@Before
	public void prepareTest() throws Exception {
		
	}

	@After
	public void quitTest() {
		
	}
	
	protected void initAppToken() throws AppAuthenticationFailedException {
		if (appToken == null) {
			appToken = profileRestClientImpl.getAppToken(appUsername, appPassword);
		}
	}

}