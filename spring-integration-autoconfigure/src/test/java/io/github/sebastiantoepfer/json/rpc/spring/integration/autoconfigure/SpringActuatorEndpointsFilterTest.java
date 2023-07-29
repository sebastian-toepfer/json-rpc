/*
 * The MIT License
 *
 * Copyright 2023 sebastian.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.sebastiantoepfer.json.rpc.spring.integration.autoconfigure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SpringActuatorEndpointsFilterTest {

    @Test
    void should_use_dispatcherServlet_for_mvc_request() throws Exception {
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final ServletContext servletContext = when(mock(ServletContext.class).getNamedDispatcher("dispatcherServlet"))
            .thenReturn(dispatcher)
            .getMock();

        final Filter filter = new SpringMvcRequestFilter(() -> Set.of("/actuator/health"));
        filter.init(when(mock(FilterConfig.class).getServletContext()).thenReturn(servletContext).getMock());

        final ServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        final ServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        verify(dispatcher).forward(request, response);
    }

    @Test
    void should_not_use_dispatcherServlet_for_non_mvc_request() throws Exception {
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final ServletContext servletContext = when(mock(ServletContext.class).getNamedDispatcher("dispatcherServlet"))
            .thenReturn(dispatcher)
            .getMock();

        final Filter filter = new SpringMvcRequestFilter(() -> Set.of("/actuator/health"));
        filter.init(when(mock(FilterConfig.class).getServletContext()).thenReturn(servletContext).getMock());

        final ServletRequest request = new MockHttpServletRequest("POST", "/rpc");
        final ServletResponse response = new MockHttpServletResponse();
        final FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(dispatcher, never()).forward(request, response);
        verify(chain).doFilter(request, response);
    }
}
