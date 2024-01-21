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

package io.github.sebastiantoepfer.json.rpc.json.sample.rpc.adapter;

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.DescribableJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.OpenRpcServiceDiscoveryJsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.sample.core.Notify;
import io.github.sebastiantoepfer.jsonschema.JsonSchema;
import io.github.sebastiantoepfer.jsonschema.JsonSchemas;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import java.util.List;

public final class JsonRpcAdapter {

    private static final JsonProvider JSONP = JsonProvider.provider();
    private static final JsonSchema INT_TYPE_SCHEMA = JsonSchemas.load(
        JSONP.createObjectBuilder().add("type", "integer").build()
    );

    private final Notify notify;

    public JsonRpcAdapter(final Notify notify) {
        this.notify = notify;
    }

    public JsonRpcRuntime createJsonRpcRuntime() {
        return new DefaultJsonRpcRuntime(
            new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new InfoObject("Tck App", "1.0.0"))
                .withMethod(sum())
                .withMethod(notifyHello())
                .withMethod(subtract())
                .withMethod(getData())
                .withMethod(notifySum())
        );
    }

    private static DescribableJsonRpcMethod sum() {
        return new DescribableJsonRpcMethod(
            new MethodObject(
                "sum",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("first", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    ),
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("second", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    ),
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("third", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    )
                )
            ),
            params ->
                JSONP.createValue(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.sum(
                        params.getInt("first"),
                        params.getInt("second"),
                        params.getInt("third")
                    )
                )
        );
    }

    private DescribableJsonRpcMethod notifyHello() {
        return new DescribableJsonRpcMethod(
            new MethodObject(
                "notify_hello",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("value", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    )
                )
            ),
            params -> {
                notify.hello(params.getInt("value"));
                return JsonValue.NULL;
            }
        );
    }

    private static DescribableJsonRpcMethod subtract() {
        return new DescribableJsonRpcMethod(
            new MethodObject(
                "subtract",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("minuend", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    ),
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("subtrahend", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    )
                )
            ),
            params ->
                JSONP.createValue(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.subtract(
                        params.getInt("minuend"),
                        params.getInt("subtrahend")
                    )
                )
        );
    }

    private DescribableJsonRpcMethod getData() {
        return new DescribableJsonRpcMethod(
            new MethodObject("get_data", List.of()),
            params -> JSONP.createArrayBuilder().add(notify.name()).add(notify.currentValue()).build()
        );
    }

    private DescribableJsonRpcMethod notifySum() {
        return new DescribableJsonRpcMethod(
            new MethodObject(
                "notify_sum",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("first", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    ),
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("second", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    ),
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject("third", new JsonSchemaOrReference.Object(INT_TYPE_SCHEMA))
                            .withRequired(true)
                    )
                )
            ),
            params -> {
                notify.hello(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.sum(
                        params.getInt("first"),
                        params.getInt("second"),
                        params.getInt("third")
                    )
                );
                return JsonValue.NULL;
            }
        );
    }
}
