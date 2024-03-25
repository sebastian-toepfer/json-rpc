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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ReferenceObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagOrReference;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcRuntime;
import io.github.sebastiantoepfer.jsonschema.JsonSchemas;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpenRpcServiceDiscoveryJsonRpcExecutionContextTest {

    @Test
    void should_return_only_specification_of_discovery() {
        assertThat(
            executeDiscover(new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new InfoObject("test app", "1.0.0"))),
            is(
                Json.createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add(
                        "result",
                        Json.createObjectBuilder()
                            .add("info", Json.createObjectBuilder().add("title", "test app").add("version", "1.0.0"))
                            .add(
                                "methods",
                                Json.createArrayBuilder()
                                    .add(
                                        Json.createObjectBuilder()
                                            .add("name", "rpc.discover")
                                            .add(
                                                "description",
                                                "Returns an OpenRPC schema as a description of this service"
                                            )
                                            .add("params", JsonValue.EMPTY_JSON_ARRAY)
                                            .add(
                                                "result",
                                                Json.createObjectBuilder()
                                                    .add("name", "OpenRPC Schema")
                                                    .add(
                                                        "schema",
                                                        Json.createObjectBuilder()
                                                            .add("$ref", System.getProperty("openrpc.schema.url"))
                                                    )
                                            )
                                    )
                            )
                            .add("openrpc", "1.3.2")
                    )
                    .add("id", "1")
                    .build()
            )
        );
    }

    @Test
    void should_return_all_methods_in_discover() {
        assertThat(
            Json.createPointer("/result/methods").getValue(
                executeDiscover(
                    new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new InfoObject("test app", "1.0.0")).withMethod(
                        new DescribableJsonRpcMethod(
                            new MethodObject(
                                "list_pets",
                                List.of(
                                    new ContentDescriptorOrReference.Object(
                                        new ContentDescriptorObject(
                                            "limit",
                                            new JsonSchemaOrReference.Object(
                                                JsonSchemas.load(
                                                    Json.createObjectBuilder().add("type", "integer").build()
                                                )
                                            )
                                        )
                                            .withDescription("How many items to return at one time (max 100)")
                                            .withRequired(false)
                                    )
                                )
                            )
                                .withSummary("List all pets")
                                .withTags(List.of(new TagOrReference.Object(new TagObject("pets"))))
                                .withResult(
                                    new ContentDescriptorOrReference.Object(
                                        new ContentDescriptorObject(
                                            "pets",
                                            new JsonSchemaOrReference.Reference(
                                                new ReferenceObject("#/components/schemas/Pets")
                                            )
                                        ).withDescription("A paged array of pets")
                                    )
                                ),
                            params -> Json.createArrayBuilder().add("bunnies").add("cats").build()
                        )
                    )
                )
            ),
            is(
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add("name", "rpc.discover")
                            .add("description", "Returns an OpenRPC schema as a description of this service")
                            .add("params", JsonValue.EMPTY_JSON_ARRAY)
                            .add(
                                "result",
                                Json.createObjectBuilder()
                                    .add("name", "OpenRPC Schema")
                                    .add(
                                        "schema",
                                        Json.createObjectBuilder().add("$ref", System.getProperty("openrpc.schema.url"))
                                    )
                            )
                    )
                    .add(
                        Json.createObjectBuilder()
                            .add("name", "list_pets")
                            .add("summary", "List all pets")
                            .add("tags", Json.createArrayBuilder().add(Json.createObjectBuilder().add("name", "pets")))
                            .add(
                                "params",
                                Json.createArrayBuilder()
                                    .add(
                                        Json.createObjectBuilder()
                                            .add("name", "limit")
                                            .add("description", "How many items to return at one time (max 100)")
                                            .add("required", false)
                                            .add("schema", Json.createObjectBuilder().add("type", "integer"))
                                    )
                            )
                            .add(
                                "result",
                                Json.createObjectBuilder()
                                    .add("name", "pets")
                                    .add("description", "A paged array of pets")
                                    .add("schema", Json.createObjectBuilder().add("$ref", "#/components/schemas/Pets"))
                            )
                    )
                    .build()
            )
        );
    }

    @Test
    void should_excute_other_than_discovery_method() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new DefaultJsonRpcRuntime(
            new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new InfoObject("test app", "1.0.0")).withMethod(
                new DescribableJsonRpcMethod(
                    new MethodObject(
                        "list_pets",
                        List.of(
                            new ContentDescriptorOrReference.Object(
                                new ContentDescriptorObject(
                                    "limit",
                                    new JsonSchemaOrReference.Object(
                                        JsonSchemas.load(Json.createObjectBuilder().add("type", "integer").build())
                                    )
                                )
                                    .withDescription("How many items to return at one time (max 100)")
                                    .withRequired(false)
                            )
                        )
                    )
                        .withSummary("List all pets")
                        .withTags(List.of(new TagOrReference.Object(new TagObject("pets"))))
                        .withResult(
                            new ContentDescriptorOrReference.Object(
                                new ContentDescriptorObject(
                                    "pets",
                                    new JsonSchemaOrReference.Reference(
                                        new ReferenceObject("#/components/schemas/Pets")
                                    )
                                ).withDescription("A paged array of pets")
                            )
                        ),
                    params -> Json.createArrayBuilder().add("bunnies").add("cats").build()
                )
            )
        )
            .createExecutorFor(
                Json.createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("method", "list_pets")
                    .add("id", "1")
                    .build()
                    .toString()
            )
            .execute()
            .writeTo(baos);
        assertThat(
            Json.createReader(new ByteArrayInputStream(baos.toByteArray())).readObject().get("result"),
            is(Json.createArrayBuilder().add("bunnies").add("cats").build())
        );
    }

    private JsonObject executeDiscover(final OpenRpcServiceDiscoveryJsonRpcExecutionContext ctx) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new DefaultJsonRpcRuntime(ctx)
            .createExecutorFor(
                Json.createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("method", "rpc.discover")
                    .add("id", "1")
                    .build()
                    .toString()
            )
            .execute()
            .writeTo(baos);
        return Json.createReader(new ByteArrayInputStream(baos.toByteArray())).readObject();
    }
}
