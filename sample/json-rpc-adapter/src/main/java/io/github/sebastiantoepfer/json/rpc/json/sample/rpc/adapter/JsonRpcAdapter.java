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

import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.sample.core.Notify;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import java.util.List;

public final class JsonRpcAdapter {

    private final Notify notify;

    public JsonRpcAdapter(final Notify notify) {
        this.notify = notify;
    }

    public JsonRpcRuntime createJsonRpcRuntime() {
        return new DefaultJsonRpcRuntime(
            new DefaultJsonRpcExecutionContext()
                .withMethod(sum())
                .withMethod(notifyHello())
                .withMethod(subtract())
                .withMethod(getData())
                .withMethod(notifySum())
        );
    }

    private static DefaultJsonRpcMethod sum() {
        return new DefaultJsonRpcMethod(
            "sum",
            List.of("first", "second", "third"),
            params ->
                Json.createValue(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.sum(
                        params.getInt("first"),
                        params.getInt("second"),
                        params.getInt("third")
                    )
                )
        );
    }

    private DefaultJsonRpcMethod notifyHello() {
        return new DefaultJsonRpcMethod(
            "notify_hello",
            List.of("value"),
            params -> {
                notify.hello(params.getInt("value"));
                return JsonValue.NULL;
            }
        );
    }

    private static DefaultJsonRpcMethod subtract() {
        return new DefaultJsonRpcMethod(
            "subtract",
            List.of("minuend", "subtrahend"),
            params ->
                Json.createValue(
                    io.github.sebastiantoepfer.json.rpc.sample.core.Math.subtract(
                        params.getInt("minuend"),
                        params.getInt("subtrahend")
                    )
                )
        );
    }

    private DefaultJsonRpcMethod getData() {
        return new DefaultJsonRpcMethod(
            "get_data",
            List.of(),
            params -> Json.createArrayBuilder().add(notify.name()).add(notify.currentValue()).build()
        );
    }

    private DefaultJsonRpcMethod notifySum() {
        return new DefaultJsonRpcMethod(
            "notify_sum",
            List.of("first", "second", "third"),
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
