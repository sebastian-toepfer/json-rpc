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

import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class ObservableJsonRpcExecutionContext<T extends JsonRpcMethod> implements JsonRpcExecutionContext<T> {

    private final JsonRpcExecutionContext<T> context;
    private final JsonRpcMethodMetrics metrics;

    public ObservableJsonRpcExecutionContext(
        final MeterRegistry meterRegistry,
        final JsonRpcExecutionContext<T> context,
        final JsonRpcMethodMetric... metrics
    ) {
        this(meterRegistry, context, "", metrics);
    }

    public ObservableJsonRpcExecutionContext(
        final MeterRegistry meterRegistry,
        final JsonRpcExecutionContext<T> context,
        final String prefix,
        final JsonRpcMethodMetric... metrics
    ) {
        this(meterRegistry, context, Arrays.asList(metrics), prefix);
    }

    public ObservableJsonRpcExecutionContext(
        final MeterRegistry meterRegistry,
        final JsonRpcExecutionContext<T> context,
        final List<JsonRpcMethodMetric> metrics,
        final String prefix
    ) {
        this(context, new JsonRpcMethodMetrics(meterRegistry, metrics, prefix));
    }

    private ObservableJsonRpcExecutionContext(
        final JsonRpcExecutionContext<T> context,
        final JsonRpcMethodMetrics metrics
    ) {
        this.context = Objects.requireNonNull(context);
        this.metrics = Objects.requireNonNull(metrics);
    }

    @Override
    public JsonRpcExecutionContext<T> withMethod(final T method) {
        return new ObservableJsonRpcExecutionContext<>(context.withMethod(method), metrics);
    }

    @Override
    public Stream<JsonRpcMethod> methods() {
        return context.methods().map(metrics::observe);
    }
}
