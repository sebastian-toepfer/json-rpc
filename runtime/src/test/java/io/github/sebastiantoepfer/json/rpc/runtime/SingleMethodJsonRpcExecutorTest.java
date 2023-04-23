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

import static io.github.sebastiantoepfer.json.rpc.runtime.test.JsonRpcMethods.exception;
import static io.github.sebastiantoepfer.json.rpc.runtime.test.JsonRpcMethods.runtimeexception;
import static io.github.sebastiantoepfer.json.rpc.runtime.test.JsonRpcMethods.subtract;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.json.rpc.runtime.test.JsonRpcExecutorJsonAdapter;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SingleMethodJsonRpcExecutorTest {

    private JsonRpcExecutionContext context;

    @BeforeEach
    void createExecutionContext() {
        context =
            new JsonRpcExecutionContext().withMethod(subtract()).withMethod(exception()).withMethod(runtimeexception());
    }

    @Test
    void should_call_method_with_positional_parameters() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new SingleMethodJsonRpcExecutor(
                    context,
                    Json
                        .createObjectBuilder()
                        .add("jsonrpc", "2.0")
                        .add("method", "subtract")
                        .add("params", Json.createArrayBuilder().add(42).add(23))
                        .add("id", 5)
                        .build()
                )
            )
                .execute(),
            is(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 19).add("id", 5).build())
        );
    }

    @Test
    void should_call_method_with_named_parameters() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new SingleMethodJsonRpcExecutor(
                    context,
                    Json
                        .createObjectBuilder()
                        .add("jsonrpc", "2.0")
                        .add("method", "subtract")
                        .add("params", Json.createObjectBuilder().add("subtrahend", 23).add("minuend", 42))
                        .add("id", 5)
                        .build()
                )
            )
                .execute(),
            is(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 19).add("id", 5).build())
        );
    }

    @Test
    void should_return_nothing_for_notification() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new SingleMethodJsonRpcExecutor(
                    context,
                    Json
                        .createObjectBuilder()
                        .add("jsonrpc", "2.0")
                        .add("method", "subtract")
                        .add("params", Json.createObjectBuilder().add("subtrahend", 23).add("minuend", 42))
                        .build()
                )
            )
                .execute(),
            is(JsonValue.NULL)
        );
    }

    @Test
    void should_return_error_for_nonexistent_method() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new SingleMethodJsonRpcExecutor(
                    context,
                    Json.createObjectBuilder().add("jsonrpc", "2.0").add("method", "foobar").add("id", 5).build()
                )
            )
                .execute(),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32601).add("message", "Method not found"))
                    .add("id", 5)
                    .build()
            )
        );
    }

    @Test
    void should_return_error_for_exception() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new SingleMethodJsonRpcExecutor(
                    context,
                    Json
                        .createObjectBuilder()
                        .add("jsonrpc", "2.0")
                        .add("method", "exception")
                        .add("params", Json.createArrayBuilder().add(42).add("Error message"))
                        .add("id", 5)
                        .build()
                )
            )
                .execute(),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", 42).add("message", "Error message"))
                    .add("id", 5)
                    .build()
            )
        );
    }

    @Test
    void should_return_error_for_runtimeexception() {
        assertThat(
            new JsonRpcExecutorJsonAdapter(
                new SingleMethodJsonRpcExecutor(
                    context,
                    Json
                        .createObjectBuilder()
                        .add("jsonrpc", "2.0")
                        .add("method", "runtimeexception")
                        .add("params", Json.createArrayBuilder().add(42).add("Error message"))
                        .add("id", 5)
                        .build()
                )
            )
                .execute(),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add(
                        "error",
                        Json.createObjectBuilder().add("code", -1).add("message", "java.lang.NullPointerException")
                    )
                    .add("id", 5)
                    .build()
            )
        );
    }
}
