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

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ DispatcherServlet.class })
@ConditionalOnBean({ MvcUrlPrefixes.class })
@Component
@Order(HIGHEST_PRECEDENCE)
final class SpringMvcRequestFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(SpringMvcRequestFilter.class.getName());
    private final Set<String> springMvcPrefixes;
    private ServletContext servletContext;

    public SpringMvcRequestFilter(final MvcUrlPrefixes mvcUrlPrefixes) {
        this.springMvcPrefixes = mvcUrlPrefixes.prefixes();
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        LOG.entering(SpringMvcRequestFilter.class.getName(), "doFilter");
        if (isMvcRequest(request)) {
            delegateToSpringMvc(request, response);
        }
        chain.doFilter(request, response);
        LOG.exiting(SpringMvcRequestFilter.class.getName(), "doFilter");
    }

    private boolean isMvcRequest(final ServletRequest request) {
        LOG.entering(SpringMvcRequestFilter.class.getName(), "isMvcRequest");
        final boolean result =
            request.getDispatcherType() != DispatcherType.FORWARD &&
            request instanceof HttpServletRequest httpRequest &&
            springMvcPrefixes.stream().anyMatch(httpRequest.getRequestURI()::startsWith);
        LOG.exiting(SpringMvcRequestFilter.class.getName(), "isMvcRequest", result);
        return result;
    }

    private void delegateToSpringMvc(final ServletRequest request, final ServletResponse response)
        throws IOException, ServletException {
        LOG.entering(SpringMvcRequestFilter.class.getName(), "delegateToSpringMvc");
        servletContext.getNamedDispatcher("dispatcherServlet").forward(request, response);
        LOG.exiting(SpringMvcRequestFilter.class.getName(), "delegateToSpringMvc");
    }
}
