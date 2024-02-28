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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import io.github.sebastiantoepfer.ddd.common.Printable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject.MethodObjectParamStructure;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethodFunction;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

final class MethodMedia implements BaseMethodMedia<MethodMedia> {

    private final String methodName;
    private final List<Printable> params;
    private final MethodObjectParamStructure paramStructure;

    public MethodMedia() {
        this(null, List.of(), MethodObjectParamStructure.either);
    }

    private MethodMedia(
        final String methodname,
        final List<Printable> params,
        final MethodObjectParamStructure paramStucture
    ) {
        this.methodName = methodname;
        this.params = List.copyOf(params);
        this.paramStructure = paramStucture;
    }

    @Override
    public MethodMedia withValue(final String name, final String value) {
        final MethodMedia result;
        if (Objects.equals(name, "name")) {
            result = new MethodMedia(value, params, paramStructure);
        } else if (Objects.equals(name, "paramStructure")) {
            result = Arrays.stream(MethodObjectParamStructure.values())
                .filter(s -> Objects.equals(s.toString(), value))
                .map(s -> new MethodMedia(methodName, params, s))
                .findFirst()
                .orElse(this);
        } else {
            result = this;
        }
        return result;
    }

    @Override
    public MethodMedia withValue(final String name, final Collection<?> values) {
        final MethodMedia result;
        if (Objects.equals(name, "params")) {
            result = values
                .stream()
                .filter(Printable.class::isInstance)
                .map(Printable.class::cast)
                .collect(
                    collectingAndThen(toList(), newParams -> new MethodMedia(methodName, newParams, paramStructure))
                );
        } else {
            result = this;
        }
        return result;
    }

    JsonRpcMethod createMethodWith(final JsonRpcMethodFunction function) {
        Objects.requireNonNull(methodName, "can not determine methodname!");
        Objects.requireNonNull(function, "function must be provided!");
        final MethodParameters methodParams = params
            .stream()
            .map(p -> p.printOn(new MethodParamMedia()))
            .collect(collectingAndThen(toList(), MethodParameters::new));
        return new ProtectedJsonRpcMethod(methodName, function, paramStructure, methodParams);
    }
}
