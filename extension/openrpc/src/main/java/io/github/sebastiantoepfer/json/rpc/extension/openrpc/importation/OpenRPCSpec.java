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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc.importation;

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.DescribableJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.OpenRpcServiceDiscoveryJsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethodFunction;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import java.io.InputStream;
import java.util.Objects;

public final class OpenRPCSpec {

    private static final JsonProvider JSONP = JsonProvider.provider();

    public static OpenRPCSpec load(final InputStream is) {
        try (final JsonReader reader = JSONP.createReader(is)) {
            return new OpenRPCSpec(reader.readObject());
        }
    }

    private final JsonObject spec;
    private final OpenRpcServiceDiscoveryJsonRpcExecutionContext context;

    private OpenRPCSpec(final JsonObject spec) {
        this(
            spec,
            new OpenRpcServiceDiscoveryJsonRpcExecutionContext(
                new JsonInfoObject(spec.getJsonObject("info")).asInfoObject()
            )
        );
    }

    private OpenRPCSpec(final JsonObject spec, final OpenRpcServiceDiscoveryJsonRpcExecutionContext context) {
        this.spec = Objects.requireNonNull(spec);
        this.context = Objects.requireNonNull(context);
    }

    public MethodMapping map(final JsonRpcMethodFunction function) {
        return new MethodMapping(function);
    }

    public JsonRpcExecutionContext<DescribableJsonRpcMethod> asContext() {
        return context;
    }

    public final class MethodMapping {

        private final JsonRpcMethodFunction function;

        private MethodMapping(final JsonRpcMethodFunction function) {
            this.function = Objects.requireNonNull(function);
        }

        OpenRPCSpec toName(final String methodName) {
            return spec
                .getJsonArray("methods")
                .stream()
                .map(JsonValue::asJsonObject)
                .filter(obj -> obj.getString("name").equals(methodName))
                .map(obj -> new JsonMethodObject(obj))
                .map(JsonMethodObject::asMethodObject)
                .findFirst()
                .map(method -> new DescribableJsonRpcMethod(method, function))
                .map(context::withMethod)
                .map(ctx -> new OpenRPCSpec(spec, ctx))
                .orElseThrow(() ->
                    new IllegalArgumentException(String.format("method \"%s\"not found in spec!", methodName))
                );
        }
    }
}
