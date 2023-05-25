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
package io.github.sebastiantoepfer.json.rpc.runtime;

import static io.github.sebastiantoepfer.json.rpc.runtime.test.JsonRpcMethods.subtract;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.json.rpc.runtime.test.JsonRpcExecutorJsonAdapter;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BatchJsonRpcExecutorTest {

    private JsonRpcExecutionContext context;

    @BeforeEach
    void createExecutionContext() {
        context = new DefaultJsonRpcExecutionContext().withMethod(subtract());
    }

    @Test
    void should_return_error_reponse_for_invalid_but_not_empty_array() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(new BatchJsonRpcExecutor(context, Json.createArrayBuilder().add(1).build()))
                .execute(),
            is(
                Json
                    .createArrayBuilder()
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("jsonrpc", "2.0")
                            .add(
                                "error",
                                Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request")
                            )
                            .addNull("id")
                    )
                    .build()
            )
        );
    }

    @Test
    void should_return_error_reponse_for_invalid_array() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new BatchJsonRpcExecutor(context, Json.createArrayBuilder().add(1).add(2).add(3).build())
            )
                .execute(),
            is(
                Json
                    .createArrayBuilder()
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("jsonrpc", "2.0")
                            .add(
                                "error",
                                Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request")
                            )
                            .addNull("id")
                    )
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("jsonrpc", "2.0")
                            .add(
                                "error",
                                Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request")
                            )
                            .addNull("id")
                    )
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("jsonrpc", "2.0")
                            .add(
                                "error",
                                Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request")
                            )
                            .addNull("id")
                    )
                    .build()
            )
        );
    }

    @Test
    void should_return_batch_result() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new BatchJsonRpcExecutor(
                    context,
                    Json
                        .createArrayBuilder()
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 23).add("minuend", 42))
                                .add("id", 1)
                        )
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 1).add("minuend", 3))
                                .add("id", 2)
                        )
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 1).add("minuend", 3))
                        )
                        .build()
                )
            )
                .execute(),
            is(
                Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 19).add("id", 1))
                    .add(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 2).add("id", 2))
                    .build()
            )
        );
    }

    @Test
    void should_return_nothing_if_all_methods_are_notifications() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new BatchJsonRpcExecutor(
                    context,
                    Json
                        .createArrayBuilder()
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 23).add("minuend", 42))
                        )
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 1).add("minuend", 3))
                        )
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 1).add("minuend", 3))
                        )
                        .build()
                )
            )
                .execute(),
            is(JsonValue.NULL)
        );
    }

    @Test
    void should_return_batch_result_and_errors() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new BatchJsonRpcExecutor(
                    context,
                    Json
                        .createArrayBuilder()
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 23).add("minuend", 42))
                                .add("id", 1)
                        )
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", "value")
                                .add("id", 2)
                        )
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 1).add("minuend", 3))
                                .add("id", 3)
                        )
                        .add(
                            Json
                                .createObjectBuilder()
                                .add("jsonrpc", "2.0")
                                .add("method", "subtract")
                                .add("params", Json.createObjectBuilder().add("subtrahend", 1).add("minuend", 3))
                                .add("id", 4)
                        )
                        .build()
                )
            )
                .execute(),
            is(
                Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 19).add("id", 1))
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("jsonrpc", "2.0")
                            .add(
                                "error",
                                Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request")
                            )
                            .addNull("id")
                    )
                    .add(
                        Json
                            .createObjectBuilder()
                            .add("jsonrpc", "2.0")
                            .add(
                                "error",
                                Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request")
                            )
                            .addNull("id")
                    )
                    .add(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 2).add("id", 4))
                    .build()
            )
        );
    }
}
