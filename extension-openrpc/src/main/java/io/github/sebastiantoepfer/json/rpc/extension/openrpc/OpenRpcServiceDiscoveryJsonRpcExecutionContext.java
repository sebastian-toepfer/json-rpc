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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc;

import io.github.sebastiantoepfer.ddd.media.json.JsonObjectMedia;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptor;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.Info;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.Method;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.Reference;
import io.github.sebastiantoepfer.json.rpc.runtime.BaseJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class OpenRpcServiceDiscoveryJsonRpcExecutionContext
    extends JsonRpcExecutionContext<DescribeableJsonRpcMethod> {

    private final Info info;
    private final List<DescribeableJsonRpcMethod> methods;

    public OpenRpcServiceDiscoveryJsonRpcExecutionContext(final Info info) {
        this(info, List.of());
    }

    private OpenRpcServiceDiscoveryJsonRpcExecutionContext(final Info info, final List methods) {
        this.info = Objects.requireNonNull(info, "info must be non null!");
        this.methods = List.copyOf(methods);
    }

    @Override
    public OpenRpcServiceDiscoveryJsonRpcExecutionContext withMethod(final DescribeableJsonRpcMethod method) {
        final List<DescribeableJsonRpcMethod> newMethods = new ArrayList<>(methods);
        newMethods.add(method);
        return new OpenRpcServiceDiscoveryJsonRpcExecutionContext(info, newMethods);
    }

    @Override
    protected Stream<DescribeableJsonRpcMethod> methods() {
        return Stream.concat(Stream.of(asMethod()), methods.stream());
    }

    public DescribeableJsonRpcMethod asMethod() {
        return new DescribeableJsonRpcMethod(
            new BaseJsonRpcMethod("rpc.discover", List.of()) {
                @Override
                protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                    return new JsonObjectMedia()
                        .withValue("info", info)
                        .withValue("methods", methods().toList())
                        .withValue("openrpc", "2.0.0");
                }
            },
            new Method("rpc.discover", List.of())
                .withDescription("Returns an OpenRPC schema as a description of this service")
                .withResultDescription(
                    new ContentDescriptor(
                        "OpenRPC Schema",
                        new Reference("https://raw.githubusercontent.com/open-rpc/meta-schema/master/schema.json")
                    )
                )
        );
    }
}
