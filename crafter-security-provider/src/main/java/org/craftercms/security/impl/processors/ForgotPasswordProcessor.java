package org.craftercms.security.impl.processors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.exception.AuthenticationException;
import org.craftercms.security.exception.AuthenticationSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ForgotPasswordProcessor implements RequestSecurityProcessor {
	
	public static final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    public static final String DEFAULT_FORGOT_PASSWORD_URL =      "/crafter-security-forgot-password";
    public static final String DEFAULT_FORGOT_PASSWORD_METHOD =   "POST";
    public static final String DEFAULT_USERNAME_PARAM = "username";
    public static final String DEFAULT_TENANT_PARAM = "tenantName";
    public static final String DEFAULT_FORGOT_PASSOWRD_URL_PARAM = "changePasswordUrl";

    protected String forgotPasswordUrl;
    protected String forgotPasswordMethod;
    protected String usernameParameter;
    protected String tenantNameParameter;
    protected String forgotPassUrlParameter;
    
    protected AuthenticationService authenticationService;
    
    public ForgotPasswordProcessor() {
    	this.forgotPasswordUrl = DEFAULT_FORGOT_PASSWORD_URL;
    	this.forgotPasswordMethod = DEFAULT_FORGOT_PASSWORD_METHOD;
    	this.usernameParameter = DEFAULT_USERNAME_PARAM;
    	this.tenantNameParameter = DEFAULT_TENANT_PARAM;
    	this.forgotPassUrlParameter = DEFAULT_FORGOT_PASSOWRD_URL_PARAM;
    }

	public String getForgotPasswordUrl() {
		return forgotPasswordUrl;
	}

	public void setForgotPasswordUrl(String forgotPasswordUrl) {
		this.forgotPasswordUrl = forgotPasswordUrl;
	}

	public String getForgotPasswordMethod() {
		return forgotPasswordMethod;
	}

	public void setForgotPasswordMethod(String forgotMethod) {
		this.forgotPasswordMethod = forgotMethod;
	}

	public String getUsernameParameter() {
		return usernameParameter;
	}

	public void setUsernameParameter(String usernameParameter) {
		this.usernameParameter = usernameParameter;
	}

	public String getTenantParameter() {
		return tenantNameParameter;
	}

	public void setTenantParameter(String tenantParameter) {
		this.tenantNameParameter = tenantParameter;
	}

	public String getForgotPassUrlParameter() {
		return forgotPassUrlParameter;
	}

	public void setForgotPassUrlParameter(String forgotPassUrlParameter) {
		this.forgotPassUrlParameter = forgotPassUrlParameter;
	}
	
	public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isForgotPasswordRequest(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing login request");
            }

            if (StringUtils.isEmpty(context.getTenantName())) {
                throw new IllegalArgumentException("Request context doesn't contain a tenant name");
            }

            String username = getUsername(request);
            String tenant = getTenant(request);
            String changePassworUrl = getChangePasswordUrl(request);

            if (username == null) {
                username = "";
            }
            if (tenant == null) {
            	tenant = "";
            }
            if (changePassworUrl == null) {
            	changePassworUrl = "";
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Authenticating user '" + username + "' for tenant '" + context.getTenantName() + "'");
                }

                authenticationService.forgotPassword(changePassworUrl, username, tenant);
                
            } catch (AuthenticationException e) {
                //onLoginFailure(e, context);
            	logger.error(e.getMessage());
            }
        } else {
            processorChain.processRequest(context);
        }
    }
	
	/**
     * Returns the value of the username parameter from the request.
     */
    protected String getUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }
    /**
     * Returns the value of the tenant parameter from the request.
     */
    protected String getTenant(HttpServletRequest request) {
    	return request.getParameter(tenantNameParameter);
    }
    /**
     * Returns the value of the forgotPassUrlParameter parameter from the request.
     */
    protected String getChangePasswordUrl(HttpServletRequest request) {
    	return request.getParameter(this.forgotPassUrlParameter);
    }
	
	protected boolean isForgotPasswordRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + forgotPasswordUrl) && request.getMethod().equals(forgotPasswordMethod);
    }
	
	/**
     * Sets the {@link AuthenticationService}, to perform the login against.
     */
    @Required
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

}
