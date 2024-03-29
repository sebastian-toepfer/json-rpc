/*
 * The MIT License
 *
 * Copyright 2024 sebastian.
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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethodFunction;
import jakarta.json.JsonValue;
import java.util.Objects;

public final class DescribableJsonRpcMethod implements JsonRpcMethod {

    private final JsonRpcMethod method;
    private final MethodObject description;

    public DescribableJsonRpcMethod(final MethodObject description, final JsonRpcMethodFunction function) {
        this.description = Objects.requireNonNull(description);
        this.method = description.printOn(new MethodMedia()).createMethodWith(function);
    }

    @Override
    public boolean hasName(final String name) {
        return method.hasName(name);
    }

    @Override
    public String name() {
        return method.name();
    }

    @Override
    public JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
        return method.execute(params);
    }

    MethodObject asMethodObject() {
        return description;
    }
}
