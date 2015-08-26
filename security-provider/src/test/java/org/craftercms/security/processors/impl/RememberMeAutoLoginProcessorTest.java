package org.craftercms.security.processors.impl;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.RememberMeManager;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.craftercms.security.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link org.craftercms.security.processors.impl.RememberMeAutoLoginProcessor}.
 *
 * @author avasquez
 */
public class RememberMeAutoLoginProcessorTest {

    private RememberMeAutoLoginProcessor processor;
    @Mock
    private RememberMeManager rememberMeManager;
    @Mock
    private Authentication authentication;
    @Mock
    private RequestSecurityProcessorChain chain;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(rememberMeManager.autoLogin(any(RequestContext.class))).thenReturn(authentication);

        processor = new RememberMeAutoLoginProcessor();
        processor.setRememberMeManager(rememberMeManager);
    }

    @Test
    public void testProcessRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        processor.processRequest(context, chain);

        assertNotNull(SecurityUtils.getAuthentication(request));
        assertEquals(authentication, SecurityUtils.getAuthentication(request));

        verify(rememberMeManager).autoLogin(context);
        verify(chain).processRequest(context);
    }

    @Test
    public void testProcessRequestWithPreviousAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContext context = new RequestContext(request, response, null);

        SecurityUtils.setAuthentication(request, authentication);

        processor.processRequest(context, chain);

        verify(rememberMeManager, never()).autoLogin(context);
        verify(chain).processRequest(context);
    }

}
