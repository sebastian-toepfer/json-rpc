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

import io.github.sebastiantoepfer.json.rpc.runtime.BaseJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OpenRpcServiceDiscoveryJsonRpcExecutionContextTest {

    @Test
    void should_return_only_specification_of_discovery() {
        assertThat(
            executeDiscover(new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new Info("test app", "1.0.0"))),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add(
                        "result",
                        Json
                            .createObjectBuilder()
                            .add("info", Json.createObjectBuilder().add("title", "test app").add("version", "1.0.0"))
                            .add(
                                "methods",
                                Json
                                    .createArrayBuilder()
                                    .add(
                                        Json
                                            .createObjectBuilder()
                                            .add("name", "rpc.discover")
                                            .add(
                                                "description",
                                                "Returns an OpenRPC schema as a description of this service"
                                            )
                                            .add("params", JsonValue.EMPTY_JSON_ARRAY)
                                            .add(
                                                "result",
                                                Json
                                                    .createObjectBuilder()
                                                    .add("name", "OpenRPC Schema")
                                                    .add(
                                                        "schema",
                                                        Json
                                                            .createObjectBuilder()
                                                            .add(
                                                                "$ref",
                                                                "https://raw.githubusercontent.com/open-rpc/meta-schema/master/schema.json"
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .add("openrpc", "2.0.0")
                    )
                    .add("id", "1")
                    .build()
            )
        );
    }

    @Test
    void should_return_all_methods_in_discover() {
        assertThat(
            Json
                .createPointer("/result/methods")
                .getValue(
                    executeDiscover(
                        new OpenRpcServiceDiscoveryJsonRpcExecutionContext(new Info("test app", "1.0.0"))
                            .withMethod(
                                new Method(
                                    new BaseJsonRpcMethod("list_pets", List.of("limit")) {
                                        @Override
                                        protected JsonValue execute(final JsonObject params)
                                            throws JsonRpcExecutionExecption {
                                            return Json.createArrayBuilder().add("bunnies").add("cats").build();
                                        }
                                    }
                                )
                                    .withTags(new Tag[] { new Tag("pets") })
                                    .withSummary("List all pets")
                                    .withParams(
                                        Map.of(
                                            "limit",
                                            new ContentDescriptor("limit", new Schema().withType("integer"))
                                                .withDescription("How many items to return at one time (max 100)")
                                                .withRequired(false)
                                        )
                                    )
                                    .withResultDescription(
                                        new ContentDescriptor("pets", new Reference("#/components/schemas/Pets"))
                                            .withDescription("A paged array of pets")
                                    )
                            )
                    )
                ),
            is(
                Json
                    .createArrayBuilder()
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("name", "rpc.discover")
                            .add("description", "Returns an OpenRPC schema as a description of this service")
                            .add("params", JsonValue.EMPTY_JSON_ARRAY)
                            .add(
                                "result",
                                Json
                                    .createObjectBuilder()
                                    .add("name", "OpenRPC Schema")
                                    .add(
                                        "schema",
                                        Json
                                            .createObjectBuilder()
                                            .add(
                                                "$ref",
                                                "https://raw.githubusercontent.com/open-rpc/meta-schema/master/schema.json"
                                            )
                                    )
                            )
                    )
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("name", "list_pets")
                            .add("summary", "List all pets")
                            .add("tags", Json.createArrayBuilder().add(Json.createObjectBuilder().add("name", "pets")))
                            .add(
                                "params",
                                Json
                                    .createArrayBuilder()
                                    .add(
                                        Json
                                            .createObjectBuilder()
                                            .add("name", "limit")
                                            .add("description", "How many items to return at one time (max 100)")
                                            .add("required", false)
                                            .add("schema", Json.createObjectBuilder().add("type", "integer"))
                                    )
                            )
                            .add(
                                "result",
                                Json
                                    .createObjectBuilder()
                                    .add("name", "pets")
                                    .add("description", "A paged array of pets")
                                    .add("schema", Json.createObjectBuilder().add("$ref", "#/components/schemas/Pets"))
                            )
                    )
                    .build()
            )
        );
    }

    private JsonObject executeDiscover(final OpenRpcServiceDiscoveryJsonRpcExecutionContext ctx) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new DefaultJsonRpcRuntime(ctx)
            .createExecutorFor(
                Json
                    .createObjectBuilder()
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