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

import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.sample.core.Notify;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.List;

public final class JsonRpcAdapter {

    private final Notify notify;

    public JsonRpcAdapter(final Notify notify) {
        this.notify = notify;
    }

    public JsonRpcRuntime createJsonRpcRuntime() {
        return new DefaultJsonRpcRuntime(
            new JsonRpcExecutionContext()
                .withMethod(sum())
                .withMethod(notifyHello())
                .withMethod(subtract())
                .withMethod(getData())
                .withMethod(notifySum())
        );
    }

    private static JsonRpcMethod sum() {
        return new JsonRpcMethod("sum", List.of("first", "second", "third")) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                return Json.createValue(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.sum(
                        params.getInt("first"),
                        params.getInt("second"),
                        params.getInt("third")
                    )
                );
            }
        };
    }

    private JsonRpcMethod notifyHello() {
        return new JsonRpcMethod("notify_hello", List.of("value")) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                notify.hello(params.getInt("value"));
                return JsonValue.NULL;
            }
        };
    }

    private static JsonRpcMethod subtract() {
        return new JsonRpcMethod("subtract", List.of("minuend", "subtrahend")) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                return Json.createValue(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.subtract(
                        params.getInt("minuend"),
                        params.getInt("subtrahend")
                    )
                );
            }
        };
    }

    private JsonRpcMethod getData() {
        return new JsonRpcMethod("get_data", List.of()) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                return Json.createArrayBuilder().add(notify.name()).add(notify.currentValue()).build();
            }
        };
    }

    private JsonRpcMethod notifySum() {
        return new JsonRpcMethod("notify_sum", List.of("first", "second", "third")) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                notify.hello(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.sum(
                        params.getInt("first"),
                        params.getInt("second"),
                        params.getInt("third")
                    )
                );
                return JsonValue.NULL;
            }
        };
    }
}
