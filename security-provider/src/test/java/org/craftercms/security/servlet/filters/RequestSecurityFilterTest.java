/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.servlet.filters;

import java.util.Arrays;
import javax.servlet.FilterChain;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.processors.RequestSecurityProcessor;
import org.craftercms.security.processors.RequestSecurityProcessorChain;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link org.craftercms.security.servlet.filters.RequestSecurityFilter}.
 *
 * @author avasquez
 */
public class RequestSecurityFilterTest {

    private RequestSecurityFilter filter;
    @Mock
    private RequestSecurityProcessor processor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RequestContext context = (RequestContext) invocation.getArguments()[0];
                RequestSecurityProcessorChain chain = (RequestSecurityProcessorChain) invocation.getArguments()[1];

                chain.processRequest(context);

                return null;
            }

        }).when(processor).processRequest(any(RequestContext.class), any(RequestSecurityProcessorChain.class));

        filter = new RequestSecurityFilter();
        filter.setSecurityEnabled(true);
        filter.setSecurityProcessors(Arrays.asList(processor));
        filter.setUrlsToInclude("/static-assets/paywall/**");
        filter.setUrlsToExclude("/static-assets/**");
    }

    @Test
    public void testFilter() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/home");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(processor).processRequest(any(RequestContext.class), any(RequestSecurityProcessorChain.class));
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testFilterIncludeUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/static-assets/paywall/image.jpg");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(processor).processRequest(any(RequestContext.class), any(RequestSecurityProcessorChain.class));
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testFilterExcludeUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/static-assets/image.jpg");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(processor, never()).processRequest(any(RequestContext.class), any(RequestSecurityProcessorChain.class));
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testFilterSecurityDisabled() throws Exception {
        filter.setSecurityEnabled(false);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/home");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(processor, never()).processRequest(any(RequestContext.class), any(RequestSecurityProcessorChain.class));
        verify(chain).doFilter(request, response);
    }

}
