/*
 * The MIT License
 *
 * Copyright 2024 sebastian.
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
package io.github.sebastiantoepfer.json.rpc.extension.micrometer;

import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.json.JsonValue;
import java.util.List;
import java.util.Objects;

final class JsonRpcMethodMetrics {

    private final MeterRegistry registry;
    private final List<JsonRpcMethodMetric> metrics;
    private final String prefix;

    public JsonRpcMethodMetrics(
        final MeterRegistry registry,
        final List<JsonRpcMethodMetric> metrics,
        final String prefix
    ) {
        this.registry = Objects.requireNonNull(registry);
        this.metrics = List.copyOf(metrics);
        this.prefix = Objects.requireNonNull(prefix);
    }

    JsonRpcMethod observe(final JsonRpcMethod method) {
        return metrics.stream().reduce((JsonRpcMethod) new PrefixedJsonRpcMethod(method), this::wrap, (l, r) -> null);
    }

    private JsonRpcMethod wrap(final JsonRpcMethod method, final JsonRpcMethodMetric metric) {
        return metric.observe(registry, method);
    }

    private final class PrefixedJsonRpcMethod implements JsonRpcMethod {

        private final JsonRpcMethod method;

        public PrefixedJsonRpcMethod(final JsonRpcMethod method) {
            this.method = Objects.requireNonNull(method);
        }

        @Override
        public boolean hasName(final String name) {
            return method.hasName(name);
        }

        @Override
        public String name() {
            final String result;
            if (prefix.isBlank()) {
                result = method.name();
            } else {
                result = String.format("%s.%s", prefix, method.name());
            }
            return result;
        }

        @Override
        public JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
            return method.execute(params);
        }
    }
}
