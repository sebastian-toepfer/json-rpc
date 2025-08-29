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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc;

import io.github.sebastiantoepfer.ddd.media.json.JsonObjectMedia;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.OpenrpcDocument;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ReferenceObject;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DiscoveryJsonRpcMethod implements JsonRpcMethod {

    private static final Logger LOG = Logger.getLogger(DiscoveryJsonRpcMethod.class.getName());
    private static final JsonSchemaOrReference.Reference OPENRPC_SCHEMA;

    private static Properties loadSchemaProperties() {
        final Properties result = new Properties();
        try {
            result.load(
                OpenRpcServiceDiscoveryJsonRpcExecutionContext.class.getClassLoader().getResourceAsStream(
                    "schema_url.properties"
                )
            );
        } catch (IOException ignore) {
            LOG.log(Level.FINE, null, ignore);
        }
        return result;
    }

    static {
        OPENRPC_SCHEMA = new JsonSchemaOrReference.Reference(
            new ReferenceObject(
                loadSchemaProperties().getProperty(
                    "schema_url",
                    "https://github.com/open-rpc/meta-schema/releases/download/1.14.6/open-rpc-meta-schema.json"
                )
            )
        );
    }

    private final InfoObject info;
    private final List<DescribableJsonRpcMethod> methods;
    private final String methodName;

    public DiscoveryJsonRpcMethod(final InfoObject info, final List<DescribableJsonRpcMethod> methods) {
        this.methodName = "rpc.discover";
        this.info = Objects.requireNonNull(info);
        this.methods = List.copyOf(methods);
    }

    InfoObject asInfo() {
        return info;
    }

    @Override
    public boolean hasName(final String name) {
        return Objects.equals(methodName, name);
    }

    @Override
    public String name() {
        return methodName;
    }

    @Override
    public JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
        return Stream.concat(
            Stream.of(
                new MethodOrReference.Object(
                    new MethodObject(methodName, List.of())
                        .withDescription("Returns an OpenRPC schema as a description of this service")
                        .withResult(
                            new ContentDescriptorOrReference.Object(
                                new ContentDescriptorObject("OpenRPC Schema", OPENRPC_SCHEMA)
                            )
                        )
                )
            ),
            methods
                .stream()
                .map(DescribableJsonRpcMethod::asMethodObject)
                .map(MethodOrReference.Object::new)
                .map(MethodOrReference.class::cast)
        )
            .collect(Collectors.collectingAndThen(Collectors.toList(), this::asOpenRpcDoucment))
            .printOn(new JsonObjectMedia());
    }

    private OpenrpcDocument asOpenRpcDoucment(final List<MethodOrReference> methodDescriptions) {
        return new OpenrpcDocument(OpenrpcDocument.Openrpc.Openrpc_132, info, methodDescriptions);
    }
}
