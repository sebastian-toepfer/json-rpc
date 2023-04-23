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
package io.github.sebastiantoepfer.json.rpc.runtime.test;

import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.List;

public final class JsonRpcMethods {

    public static JsonRpcMethod subtract() {
        return new JsonRpcMethod("subtract", List.of("minuend", "subtrahend")) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                return Json.createValue(params.getInt("minuend") - params.getInt("subtrahend"));
            }
        };
    }

    public static JsonRpcMethod exception() {
        return new JsonRpcMethod("exception", List.of("code", "message")) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                throw new JsonRpcExecutionExecption(params.getInt("code"), params.getString("message"));
            }
        };
    }

    public static JsonRpcMethod runtimeexception() {
        return new JsonRpcMethod("runtimeexception", List.of("code", "message")) {
            @Override
            protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                throw new NullPointerException();
            }
        };
    }
}
