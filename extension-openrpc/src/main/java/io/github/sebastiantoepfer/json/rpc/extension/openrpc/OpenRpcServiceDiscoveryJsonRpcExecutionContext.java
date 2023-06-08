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
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObjectResult;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.OpenrpcDocument;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ReferenceObject;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class OpenRpcServiceDiscoveryJsonRpcExecutionContext
    extends JsonRpcExecutionContext<DescribeableJsonRpcMethod> {

    private static final Logger LOG = Logger.getLogger(OpenRpcServiceDiscoveryJsonRpcExecutionContext.class.getName());
    private static final JsonSchemaOrReference.Reference OPENRPC_SCHEMA;

    static {
        OPENRPC_SCHEMA =
            new JsonSchemaOrReference.Reference(
                new ReferenceObject(
                    loadSchemaProperties()
                        .getProperty(
                            "schema_url",
                            "https://github.com/open-rpc/meta-schema/releases/download/1.14.5/open-rpc-meta-schema.json"
                        )
                )
            );
    }

    private static Properties loadSchemaProperties() {
        final Properties result = new Properties();
        try {
            result.load(
                OpenRpcServiceDiscoveryJsonRpcExecutionContext.class.getClassLoader()
                    .getResourceAsStream("schema_url.properties")
            );
        } catch (IOException ignore) {
            LOG.log(Level.FINE, null, ignore);
        }
        return result;
    }

    private final InfoObject info;
    private final JsonSchemaOrReference openRpcSchema;
    private final List<DescribeableJsonRpcMethod> methods;

    public OpenRpcServiceDiscoveryJsonRpcExecutionContext(final InfoObject info) {
        this(info, OPENRPC_SCHEMA, List.of());
    }

    private OpenRpcServiceDiscoveryJsonRpcExecutionContext(
        final InfoObject info,
        final JsonSchemaOrReference openRpcSchema,
        final List<DescribeableJsonRpcMethod> methods
    ) {
        this.info = Objects.requireNonNull(info, "info must be non null!");
        this.openRpcSchema = Objects.requireNonNull(openRpcSchema, "schema mst be non null");
        this.methods = List.copyOf(methods);
    }

    @Override
    public OpenRpcServiceDiscoveryJsonRpcExecutionContext withMethod(final DescribeableJsonRpcMethod method) {
        final List<DescribeableJsonRpcMethod> newMethods = new ArrayList<>(methods);
        newMethods.add(method);
        return new OpenRpcServiceDiscoveryJsonRpcExecutionContext(info, openRpcSchema, newMethods);
    }

    @Override
    protected Stream<DescribeableJsonRpcMethod> methods() {
        return Stream.concat(Stream.of(createDiscoverMethod()), methods.stream());
    }

    private DescribeableJsonRpcMethod createDiscoverMethod() {
        return new DescribeableJsonRpcMethod(
            new MethodObject("rpc.discover", List.of())
                .withDescription("Returns an OpenRPC schema as a description of this service")
                .withResult(
                    new MethodObjectResult.Object(new ContentDescriptorObject("OpenRPC Schema", openRpcSchema))
                ),
            params ->
                new OpenrpcDocument(
                    OpenrpcDocument.Openrpc.Openrpc_130,
                    info,
                    methods()
                        .map(DescribeableJsonRpcMethod::description)
                        .map(MethodOrReference.Object::new)
                        .map(MethodOrReference.class::cast)
                        .toList()
                )
                    .printOn(new JsonObjectMedia())
        );
    }
}
