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

import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import jakarta.json.Json;
import java.util.List;

public final class JsonRpcMethods {

    public static DefaultJsonRpcMethod subtract() {
        return new DefaultJsonRpcMethod(
            "subtract",
            List.of("minuend", "subtrahend"),
            params -> Json.createValue(params.getInt("minuend") - params.getInt("subtrahend"))
        );
    }

    public static DefaultJsonRpcMethod exception() {
        return new DefaultJsonRpcMethod("exception", List.of("code", "message"), params -> {
            throw new JsonRpcExecutionExecption(params.getInt("code"), params.getString("message"));
        });
    }

    public static DefaultJsonRpcMethod runtimeexception() {
        return new DefaultJsonRpcMethod("runtimeexception", List.of("code", "message"), params -> {
            throw new NullPointerException();
        });
    }
}
