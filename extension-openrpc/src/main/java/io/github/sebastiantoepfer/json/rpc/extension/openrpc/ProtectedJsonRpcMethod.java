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
import io.github.sebastiantoepfer.json.rpc.runtime.validation.AnyOf;
import io.github.sebastiantoepfer.json.rpc.runtime.validation.OfType;
import io.github.sebastiantoepfer.json.rpc.runtime.validation.Rule;
import jakarta.json.JsonValue;
import java.util.Objects;

final class ProtectedJsonRpcMethod implements JsonRpcMethod {

    private final String name;
    private final JsonRpcMethodFunction function;
    private final MethodObject.MethodObjectParamStructure paramStructure;
    private final MethodParameters parameters;

    ProtectedJsonRpcMethod(
        final String name,
        final JsonRpcMethodFunction function,
        final MethodObject.MethodObjectParamStructure paramStructure,
        final MethodParameters parameters
    ) {
        this.name = Objects.requireNonNull(name);
        this.function = Objects.requireNonNull(function);
        this.paramStructure = Objects.requireNonNull(paramStructure);
        this.parameters = Objects.requireNonNull(parameters);
    }

    @Override
    public boolean hasName(final String name) {
        return Objects.equals(this.name, name);
    }

    @Override
    public JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
        if (params != null && !parametersRule().isValid(params)) {
            throw new JsonRpcExecutionExecption(-1, "method must be called with " + paramStructure + "!");
        }
        return function.apply(parameters.createValidParameterObjectFrom(params));
    }

    private Rule parametersRule() {
        return switch (paramStructure) {
            case byname -> new OfType(JsonValue.ValueType.OBJECT);
            case byposition -> new OfType(JsonValue.ValueType.ARRAY);
            default -> new AnyOf(
                new OfType(JsonValue.ValueType.OBJECT),
                new OfType(JsonValue.ValueType.ARRAY),
                new OfType(JsonValue.ValueType.NULL)
            );
        };
    }
}
