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
package io.github.sebastiantoepfer.json.rpc.benchmark;

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.DescribeableJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.JsonSchemaObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.OpenRpcServiceDiscoveryJsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcRuntime;
import jakarta.json.spi.JsonProvider;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 7, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
public class OpenRpcJsonRpcRuntimeBenchmarks {

    @State(Scope.Benchmark)
    public static class OpenRPCSingleMethodExecutionPlan {

        final JsonRpcRuntime runtime;
        final String methodRequest;

        public OpenRPCSingleMethodExecutionPlan() {
            final JsonProvider provider = JsonProvider.provider();
            runtime =
                new DefaultJsonRpcRuntime(
                    new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new InfoObject("Benchmark App", "1.0.0"))
                        .withMethod(
                            new DescribeableJsonRpcMethod(
                                new MethodObject(
                                    "subtract",
                                    List.of(
                                        new ContentDescriptorOrReference.Object(
                                            new ContentDescriptorObject(
                                                "minuend",
                                                new JsonSchemaOrReference.Object(
                                                    new JsonSchemaObject().withType("integer")
                                                )
                                            )
                                        ),
                                        new ContentDescriptorOrReference.Object(
                                            new ContentDescriptorObject(
                                                "subtrahend",
                                                new JsonSchemaOrReference.Object(
                                                    new JsonSchemaObject().withType("integer")
                                                )
                                            )
                                        )
                                    )
                                ),
                                params -> provider.createValue(params.getInt("minuend") - params.getInt("subtrahend"))
                            )
                        )
                );
            methodRequest =
                provider
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("method", "subtract")
                    .add("params", provider.createArrayBuilder().add(42).add(23))
                    .build()
                    .toString();
        }
    }

    @Benchmark
    public void runJsonRpcMethod(final OpenRPCSingleMethodExecutionPlan plan) {
        plan.runtime.createExecutorFor(plan.methodRequest).execute().writeTo(new ByteArrayOutputStream());
    }
}
