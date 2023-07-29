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

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@ConditionalOnClass({ WebEndpointsSupplier.class, WebEndpointProperties.class })
final class ActuatorEndpoints implements MvcUrlPrefixes {

    private static final Logger LOG = Logger.getLogger(ActuatorEndpoints.class.getName());
    private final WebEndpointProperties webEndpointProperties;
    private final WebEndpointsSupplier webEndpointsSupplier;

    public ActuatorEndpoints(
        final WebEndpointProperties webEndpointProperties,
        final WebEndpointsSupplier webEndpointsSupplier
    ) {
        this.webEndpointProperties = webEndpointProperties;
        this.webEndpointsSupplier = webEndpointsSupplier;
    }

    @Override
    public Set<String> prefixes() {
        LOG.entering(ActuatorEndpoints.class.getName(), "mvcActuatorPrefixes");
        final Set<String> result = webEndpointsSupplier
            .getEndpoints()
            .stream()
            .map(PathMappedEndpoint::getRootPath)
            .map(webEndpointProperties.getBasePath().concat("/")::concat)
            .collect(Collectors.toSet());
        LOG.exiting(ActuatorEndpoints.class.getName(), "mvcActuatorPrefixes", result);
        return result;
    }
}
