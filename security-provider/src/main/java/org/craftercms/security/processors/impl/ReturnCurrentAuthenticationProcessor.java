package org.craftercms.security.processors.impl;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.commons.http.HttpUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.commons.rest.HttpMessageConvertingResponseWriter;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

/**
 * {@link org.craftercms.security.processors.RequestSecurityProcessor} that returns the current authentication to the
 * client.
 *
 * @author avasquez
 */
public class ReturnCurrentAuthenticationProcessor implements RequestSecurityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ReturnCurrentAuthenticationProcessor.class);

    public static final String DEFAULT_SERVICE_URL = "/crafter-security-current-auth";
    public static final String DEFAULT_SERVICE_METHOD = "GET";

    private String serviceUrl;
    private String serviceMethod;
    private HttpMessageConvertingResponseWriter responseWriter;

    public ReturnCurrentAuthenticationProcessor() {
        serviceUrl = DEFAULT_SERVICE_URL;
        serviceMethod = DEFAULT_SERVICE_METHOD;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public void setResponseWriter(HttpMessageConvertingResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    @Override
    public void processRequest(RequestContext context, RequestSecurityProcessorChain processorChain) throws Exception {
        HttpServletRequest request = context.getRequest();

        if (isServiceRequest(request)) {
            sendAuthentication(SecurityUtils.getAuthentication(request), context);
        } else {
            processorChain.processRequest(context);
        }
    }

    protected boolean isServiceRequest(HttpServletRequest request) {
        return HttpUtils.getRequestUriWithoutContextPath(request).equals(serviceUrl) && request.getMethod().equals(
            serviceMethod);
    }

    protected <T> void sendAuthentication(Authentication auth, RequestContext context) throws IOException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        if (auth != null) {
            response.setStatus(HttpServletResponse.SC_OK);

            try {
                responseWriter.writeWithMessageConverters(auth, request, response);
            } catch (HttpMediaTypeNotAcceptableException e) {
                logger.error(e.getMessage(), e);

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

}
