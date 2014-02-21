package org.craftercms.security.impl.processors;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.search.SearchException;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.security.api.AuthenticationService;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.RequestSecurityProcessor;
import org.craftercms.security.api.RequestSecurityProcessorChain;
import org.craftercms.security.api.UserProfile;
import org.craftercms.security.authentication.CreateProfileFailureHandler;
import org.craftercms.security.authentication.CreateProfileSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CreateProfileProcessor implements RequestSecurityProcessor {

    public static final Logger logger = LoggerFactory.getLogger(ForgotPasswordProcessor.class);

    public static final String DEFAULT_CREATE_PROFILE_URL = "/crafter-create-profile";
    public static final String DEFAULT_CREATE_PROFILE_METHOD = "POST";
    public static final String DEFAULT_USERNAME_PARAM = "username";
    public static final String DEFAULT_PASSWORD_PARAM = "password";
    public static final String DEFAULT_CONFIRM_PASSWORD_PARAM = "confirmPassword";
    public static final String DEFAULT_ACTIVE_PARAM = "active";
    public static final String DEFAULT_TENANTNAME_PARAM = "tenantName";
    public static final String DEFAULT_EMAIL_PARAM = "email";
    public static final String DEFAULT_ROLES_PARAM = "roles";
    public static final String DEFAULT_VERIFICATION_ACOUNT_URL_PARAM = "verificationAccountUrl";

    private String createProfileUrl;
    private String createProfileMethod;

    private String usernameParameter;
    private String passwordParameter;
    private String confirmPasswordParameter;
    private String activeParameter;
    private String tenantNameParameter;
    private String rolesParameter;
    private String emailParameter;
    private String verificationAccountUrlParameter;

    protected AuthenticationService authenticationService;
    protected CreateProfileSuccessHandler createProfileSuccessHandler;
    protected CreateProfileFailureHandler createProfileFailureHandler;

    public CreateProfileProcessor() {
        createProfileUrl = DEFAULT_CREATE_PROFILE_URL;
        createProfileMethod = DEFAULT_CREATE_PROFILE_METHOD;
        usernameParameter = DEFAULT_USERNAME_PARAM;
        passwordParameter = DEFAULT_PASSWORD_PARAM;
        confirmPasswordParameter = DEFAULT_CONFIRM_PASSWORD_PARAM;
        rolesParameter = DEFAULT_ROLES_PARAM;
        activeParameter = DEFAULT_ACTIVE_PARAM;
        tenantNameParameter = DEFAULT_TENANTNAME_PARAM;
        emailParameter = DEFAULT_EMAIL_PARAM;
        verificationAccountUrlParameter = DEFAULT_VERIFICATION_ACOUNT_URL_PARAM;
    }

    public String getCreateProfileUrl() {
        return createProfileUrl;
    }

    public void setCreateProfileUrl(String createProfileUrl) {
        this.createProfileUrl = createProfileUrl;
    }

    public String getCreateProfileMethod() {
        return createProfileMethod;
    }

    public void setCreateProfileMethod(String createProfileMethod) {
        this.createProfileMethod = createProfileMethod;
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    public String getActiveParameter() {
        return activeParameter;
    }

    public void setActiveParameter(String activeParameter) {
        this.activeParameter = activeParameter;
    }

    public String getTenantNameParameter() {
        return tenantNameParameter;
    }

    public void setTenantNameParameter(String tenantNameParameter) {
        this.tenantNameParameter = tenantNameParameter;
    }

    public String getEmailParameter() {
        return emailParameter;
    }

    public void setEmailParameter(String emailParameter) {
        this.emailParameter = emailParameter;
    }

    public String getVerificationAccountUrlParameter() {
        return verificationAccountUrlParameter;
    }

    public void setVerificationAccountUrlParameter(String verificationAccountUrlParameter) {
        this.verificationAccountUrlParameter = verificationAccountUrlParameter;
    }

    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isCreateProfileRequest(request)) {

            try {
                Map<String, Serializable> data = createMapFromQueryParam(context);

                UserProfile profile = this.authenticationService.createProfile(data);

                createProfileSuccessHandler.onCreateProfileSuccess(profile, context);
            } catch (Exception e) {
                logger.error(e.getMessage());
                createProfileFailureHandler.onCreateProfileFailure(e, context);
            }

        } else {
            processorChain.processRequest(context);
        }
    }

    private Map<String, Serializable> createMapFromQueryParam(RequestContext context) {
        Map<String, Serializable> queryParams = new HashMap<String, Serializable>();
        String username = getUsername(context.getRequest());
        if (StringUtils.isEmpty(username)) {
            throw new SecurityException("Request  doesn't contain a username");
        }
        queryParams.put(ProfileConstants.USER_NAME, username);
        String password = getPassword(context.getRequest());
        if (StringUtils.isEmpty(password)) {
            throw new SecurityException("Request  doesn't contain a password value");
        }

        String confirmPassword = getConfirmPassword(context.getRequest());
        if (StringUtils.isEmpty(confirmPassword)) {
            throw new SecurityException("Request  doesn't contain a confirm password value");
        }
        if (!password.equals(confirmPassword)) {
            throw new SecurityException("Password and confirm password do not match");
        }
        queryParams.put(ProfileConstants.PASSWORD, password);

        queryParams.put(ProfileConstants.ACTIVE, getActive(context.getRequest()));
        String tenantName = context.getTenantName();
        if (StringUtils.isEmpty(tenantName)) {
            throw new SecurityException("Request  doesn't contain a tenantName");
            //tenantName = "craftercms";
        }
        queryParams.put(ProfileConstants.TENANT_NAME, tenantName);
        String email = getEmail(context.getRequest());
        if (StringUtils.isEmpty(email)) {
            throw new SearchException("Request  doesn't contain a email");
        }
        queryParams.put(ProfileConstants.EMAIL, email);
        String verificationAccountUrl = getVerificationAccountUrl(context.getRequest(), context);
        if (StringUtils.isEmpty(verificationAccountUrl)) {
            throw new SearchException("Request  doesn't contain a verificationAccountUrl");
        }
        queryParams.put(ProfileConstants.VERIFICATION_ACCOUNT_URL, verificationAccountUrl);

        addRoles(context.getRequest(), queryParams);

        return queryParams;
    }

    private void addRoles(HttpServletRequest request, Map<String, Serializable> data) {
        ArrayList<String> rolesList = new ArrayList<String>();
        String[] roles;

        Map params = request.getParameterMap();

        roles = (String[])params.get(this.rolesParameter);

        if (roles != null) {
            rolesList = (ArrayList<String>)Arrays.asList(roles);
        }
        data.put(this.rolesParameter, rolesList);
    }

    private String getVerificationAccountUrl(HttpServletRequest request, RequestContext context) {
        //return request.getParameter(verificationAccountUrlParameter);
        String uriVerification = request.getParameter(verificationAccountUrlParameter);
        String url = uriVerification;
        try {
            if (!isAbsolute(uriVerification)) {
                url = createUrl(context, uriVerification);
            }
        } catch (URISyntaxException e) {
            logger.warn("Verification URI Syntax Exception");
        }

        return url;
    }

    private String getEmail(HttpServletRequest request) {
        return request.getParameter(emailParameter);
    }

    private String getActive(HttpServletRequest request) {
        String activeValue = request.getParameter(activeParameter);
        String active = "false";
        if (activeValue == null || activeValue.equals("")) {
            activeValue = "off";
        }
        if (activeValue != null && activeValue.equalsIgnoreCase("on")) {
            active = "true";
        }
        return active;
    }

    private String getPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    private String getConfirmPassword(HttpServletRequest request) {
        return request.getParameter(confirmPasswordParameter);
    }

    private String getUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    protected boolean isCreateProfileRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(request.getContextPath() + createProfileUrl) && request.getMethod()
            .equals(createProfileMethod);
    }

    /**
     * Sets the {@link AuthenticationService}, to perform the login against.
     */
    @Required
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public CreateProfileSuccessHandler getCreateProfileSuccessHandler() {
        return createProfileSuccessHandler;
    }

    public void setCreateProfileSuccessHandler(CreateProfileSuccessHandler createProfileSuccessHandler) {
        this.createProfileSuccessHandler = createProfileSuccessHandler;
    }

    public CreateProfileFailureHandler getCreateProfileFailureHandler() {
        return createProfileFailureHandler;
    }

    public void setCreateProfileFailureHandler(CreateProfileFailureHandler createProfileFailureHandler) {
        this.createProfileFailureHandler = createProfileFailureHandler;
    }

    public String getConfirmPasswordParameter() {
        return confirmPasswordParameter;
    }

    public void setConfirmPasswordParameter(String confirmPasswordParameter) {
        this.confirmPasswordParameter = confirmPasswordParameter;
    }

    private String createUrl(RequestContext context, String uriVerification) {
        String url = uriVerification;
        try {
            int index = context.getRequest().getRequestURL().indexOf(context.getRequest().getRequestURI());
            if (index >= 0) {
                String baseUri = context.getRequest().getRequestURL().substring(0, index);
                if (baseUri.endsWith("/") && uriVerification.startsWith("/")) {
                    url = baseUri + uriVerification.substring(1);
                } else if (baseUri.endsWith("/")) {
                    url = baseUri + uriVerification;
                } else if (uriVerification.startsWith("/")) {
                    url = baseUri + uriVerification;
                } else {
                    url = baseUri + "/" + uriVerification;
                }
            }
        } catch (Exception e) {
            this.logger.error("Error generating the verification url: " + e.getMessage());
        }
        return url;
    }

    private boolean isAbsolute(String uri) throws URISyntaxException {
        URI u = new URI(uri);
        boolean result = false;
        if (u.isAbsolute()) {
            return true;
        }
        return result;
    }

}
