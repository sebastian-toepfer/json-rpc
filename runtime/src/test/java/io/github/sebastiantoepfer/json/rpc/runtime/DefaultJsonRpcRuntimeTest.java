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

import io.github.sebastiantoepfer.json.rpc.runtime.test.JsonRpcRuntimeExecutor;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultJsonRpcRuntimeTest {

    private JsonRpcRuntimeExecutor jsonRpcRuntimeExecutor;

    @BeforeEach
    void createExecutor() {
        this.jsonRpcRuntimeExecutor =
            new JsonRpcRuntimeExecutor(new DefaultJsonRpcRuntime(new JsonRpcExecutionContext().withMethod(subtract())));
    }

    @Test
    void should_return_error_response_for_invalid_json_input() {
        assertThat(
            executeJsonRequest("{\"jsonrpc\": \"2.0\", \"method\": \"foobar, \"params\": \"bar\", \"baz]"),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32700).add("message", "Parse error"))
                    .addNull("id")
                    .build()
            )
        );
    }

    @Test
    void should_return_error_response_for_invalid_request_object() {
        assertThat(
            executeJsonRequest("{\"jsonrpc\": \"2.0\", \"method\": 1, \"params\": \"bar\"}"),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request"))
                    .addNull("id")
                    .build()
            )
        );
    }

    @Test
    void should_return_error_response_for_invalid_batch_json_input() {
        assertThat(
            executeJsonRequest(
                "[\n" +
                "  {\"jsonrpc\": \"2.0\", \"method\": \"sum\", \"params\": [1,2,4], \"id\": \"1\"},\n" +
                "  {\"jsonrpc\": \"2.0\", \"method\"\n" +
                "]"
            ),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32700).add("message", "Parse error"))
                    .addNull("id")
                    .build()
            )
        );
    }

    @Test
    void should_return_error_response_for_empty_json_array() {
        assertThat(
            executeJsonRequest("[]"),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request"))
                    .addNull("id")
                    .build()
            )
        );
    }

    @Test
    void should_return_non_null_for_valid_jsonobject() {
        assertThat(
            executeJsonRequest("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}"),
            is(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 19).add("id", 1).build())
        );
    }

    @Test
    void should_return_non_null_for_valid_jsonarray() {
        assertThat(
            executeJsonRequest("[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}]"),
            is(
                Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder().add("jsonrpc", "2.0").add("result", 19).add("id", 1))
                    .build()
            )
        );
    }

    @Test
    void should_return_invalid_request_if_method_is_mission() {
        assertThat(
            executeJsonRequest("{\"jsonrpc\": \"2.0\", \"params\": [42, 23], \"id\": 1}"),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request"))
                    .addNull("id")
                    .build()
            )
        );
    }

    @Test
    void should_work_also_with_inputstream_as_input() {
        assertThat(
            executeJsonRequest(
                new ByteArrayInputStream("{\"jsonrpc\": \"2.0\", \"params\": [42, 23], \"id\": 1}".getBytes())
            ),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32600).add("message", "Invalid Request"))
                    .addNull("id")
                    .build()
            )
        );
    }

    private JsonValue executeJsonRequest(final String json) {
        return jsonRpcRuntimeExecutor.execute(json);
    }

    private JsonValue executeJsonRequest(final InputStream json) {
        return jsonRpcRuntimeExecutor.execute(json);
    }
}
