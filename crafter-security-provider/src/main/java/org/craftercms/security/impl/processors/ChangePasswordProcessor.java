package org.craftercms.security.impl.processors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Manag
 * @author Alvaro Gonzalez
 *
 */
public class ChangePasswordProcessor implements RequestSecurityProcessor {
	
	public static final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    public static final String DEFAULT_FORGOT_PASSWORD_URL =      "/crafter-security-change-password";
    public static final String DEFAULT_FORGOT_PASSWORD_METHOD =   "POST";
    public static final String DEFAULT_PASSWORD_PARAM = "newPassword";
    public static final String DEFAULT_TOKEN_PARAM = "token";
    
    protected String changePasswordUrl;
    protected String changePasswordMethod;
    protected String newPasswordParameter;
    protected String tokenParameter;
    protected String forgotPassUrlParameter;
    
    protected AuthenticationService authenticationService;
    
    public ChangePasswordProcessor() {
    	this.changePasswordUrl = DEFAULT_FORGOT_PASSWORD_URL;
    	this.changePasswordMethod = DEFAULT_FORGOT_PASSWORD_METHOD;
    	this.newPasswordParameter = DEFAULT_PASSWORD_PARAM;
    	this.tokenParameter = DEFAULT_TOKEN_PARAM;
    }

	public String getChangePasswordUrl() {
		return changePasswordUrl;
	}

	public void setChangePasswordUrl(String changePasswordUrl) {
		this.changePasswordUrl = changePasswordUrl;
	}

	public String getChangePasswordMethod() {
		return changePasswordMethod;
	}

	public void setChangePasswordMethod(String forgotMethod) {
		this.changePasswordMethod = forgotMethod;
	}

	public String getNewPasswordParameter() {
		return newPasswordParameter;
	}

	public void setNewPasswordParameter(String newPasswordParameter) {
		this.newPasswordParameter = newPasswordParameter;
	}

	public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isChangePasswordRequest(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing login request");
            }

            if (StringUtils.isEmpty(context.getTenantName())) {
                throw new IllegalArgumentException("Request context doesn't contain a tenant name");
            }

            String password = getPassword(request);
            String token = getToken(request);
            
            if (password == null) {
            	password = "";
            }
            if (token == null) {
            	token = "";
            }
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("ChangePassword request for token " + token);
                }

                authenticationService.changePassword(password, token);
                
            } catch (AuthenticationException e) {
                //onLoginFailure(e, context);
            	logger.error(e.getMessage());
            }
        } else {
            processorChain.processRequest(context);
        }
    }
	
	/**
     * Returns the value of the password parameter from the request.
     */
    protected String getPassword(HttpServletRequest request) {
        return request.getParameter(newPasswordParameter);
    }
    /**
     * Returns the value of the token parameter from the request.
     */
    protected String getToken(HttpServletRequest request) {
    	return request.getParameter(tokenParameter);
    }
    
	
	protected boolean isChangePasswordRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + changePasswordUrl) && request.getMethod().equals(changePasswordMethod);
    }

	/**
     * Sets the {@link AuthenticationService}, to perform the login against.
     */
    @Required
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

}
