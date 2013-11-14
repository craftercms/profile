package org.craftercms.security.authentication;

public class BaseHandler {
	
	protected boolean isRedirectRequired;
	
	public BaseHandler() {
		this.isRedirectRequired = true;
	}

	public boolean isRedirectRequired() {
		return isRedirectRequired;
	}

	public void setRedirectRequired(boolean isRedirectRequired) {
		this.isRedirectRequired = isRedirectRequired;
	}

}
