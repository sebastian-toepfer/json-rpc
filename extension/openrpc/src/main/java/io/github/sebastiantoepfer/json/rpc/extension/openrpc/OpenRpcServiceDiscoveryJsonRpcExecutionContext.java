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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class OpenRpcServiceDiscoveryJsonRpcExecutionContext
    implements JsonRpcExecutionContext<DescribableJsonRpcMethod> {

    private final DiscoveryJsonRpcMethod discovery;
    private final List<DescribableJsonRpcMethod> methods;

    public OpenRpcServiceDiscoveryJsonRpcExecutionContext(final InfoObject info) {
        this(info, List.of());
    }

    private OpenRpcServiceDiscoveryJsonRpcExecutionContext(
        final InfoObject info,
        final List<DescribableJsonRpcMethod> methods
    ) {
        this.methods = List.copyOf(methods);
        this.discovery = new DiscoveryJsonRpcMethod(info, methods);
    }

    @Override
    public OpenRpcServiceDiscoveryJsonRpcExecutionContext withMethod(final DescribableJsonRpcMethod method) {
        final List<DescribableJsonRpcMethod> newMethods = new ArrayList<>(methods);
        newMethods.add(method);
        return new OpenRpcServiceDiscoveryJsonRpcExecutionContext(discovery.asInfo(), newMethods);
    }

    @Override
    public Stream<JsonRpcMethod> methods() {
        return Stream.concat(Stream.of(discovery), methods.stream());
    }
}
