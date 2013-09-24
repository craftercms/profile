package org.craftercms.security.authentication.impl;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.CreateProfileFailureHandler;
import org.craftercms.security.authentication.CreateProfileSuccessHandler;
import org.craftercms.security.exception.CrafterSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CreateProfileSuccessHandlerImpl implements CreateProfileSuccessHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateProfileSuccessHandlerImpl.class);

    protected String defaultTargetUrl;

	/* (non-Javadoc)
	 * @see org.craftercms.security.authentication.CreateProfileFailureHandler#onResetPasswordFailure(java.lang.Exception, org.craftercms.security.api.RequestContext, java.lang.String)
	 */
	@Override
	public void onCreateProfileSuccess(UserProfile profile, RequestContext context) throws CrafterSecurityException, IOException {
		HttpSession session = context.getRequest().getSession();
        session.setAttribute(SecurityConstants.PROFILE_FORGOT_PASSWORD, profile);

        String redirectUrl = context.getRequest().getContextPath() + defaultTargetUrl;

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to URL: " + redirectUrl);
        }

        context.getResponse().sendRedirect(redirectUrl);

	}
	
	/**
     * The target URL to redirect
     */
    @Required
    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }

}
