package org.craftercms.profile.testing.integration;

import org.craftercms.profile.testing.BaseTest;
import org.craftercms.profile.impl.domain.Attribute;
import org.craftercms.profile.impl.domain.Schema;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class ITSchemaRestControllerTest extends BaseTest {
	
	@Test
	public void testSetAttributeforSchema() throws AppAuthenticationFailedException {
		initAppToken();
		Attribute a = new Attribute();
		a.setLabel("test");
		a.setName("test-attr");
		a.setOrder(900);
		a.setRequired(true);
		a.setType("Text");
		profileRestClientImpl.setAttributeForSchema(appToken, "craftercms", a);
		Schema schema = profileRestClientImpl.getSchema(appToken, "craftercms");
		boolean found = false;
		for (Attribute c: schema.getAttributes()) {
			if (c.getName().equalsIgnoreCase("test-attr")) {
				found = true;
				break;
			}
		}
		
		assertTrue(found);
	}
	
	@Test
	public void testDeleteAttributeforSchema() throws AppAuthenticationFailedException {
		initAppToken();
		Attribute a = new Attribute();
		a.setLabel("test");
		a.setName("test-attr1");
		a.setOrder(900);
		a.setRequired(true);
		a.setType("Text");
		profileRestClientImpl.setAttributeForSchema(appToken, "craftercms", a);
		Schema schema = profileRestClientImpl.getSchema(appToken, "craftercms");
		boolean found = false;
		for (Attribute c: schema.getAttributes()) {
			if (c.getName().equalsIgnoreCase("test-attr1")) {
				found = true;
				break;
			}
		}
		
		assertTrue(found);
		
		profileRestClientImpl.deleteAttributeForSchema(appToken, "craftercms", "test-attr1");
		schema = profileRestClientImpl.getSchema(appToken, "craftercms");
		found = false;
		for (Attribute c: schema.getAttributes()) {
			if (c.getName().equalsIgnoreCase("test-attr1")) {
				found = true;
				break;
			}
		}
		
		assertTrue(!found);
		
	}

}
