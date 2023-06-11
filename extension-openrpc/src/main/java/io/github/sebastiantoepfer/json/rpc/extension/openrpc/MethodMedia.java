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
import io.github.sebastiantoepfer.ddd.media.core.BaseMedia;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject.MethodObjectParamStructure;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcMethod;
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
            result =
                Arrays
                    .stream(MethodObjectParamStructure.values())
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
            result =
                values
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
        return new DefaultJsonRpcMethod(
            Objects.requireNonNull(methodName, "can not determine methodname!"),
            params.stream().map(p -> p.printOn(new MethodParamMedia())).map(MethodParamMedia::name).toList(),
            Objects.requireNonNull(function, "function must be provided!")
        );
    }

    private class MethodParamMedia implements BaseMethodMedia<MethodParamMedia> {

        private final String name;

        MethodParamMedia() {
            this(null);
        }

        private MethodParamMedia(final String name) {
            this.name = name;
        }

        @Override
        public MethodParamMedia withValue(final String name, final String value) {
            final MethodParamMedia result;
            if (Objects.equals("name", name)) {
                result = new MethodParamMedia(value);
            } else {
                result = this;
            }
            return result;
        }

        public String name() {
            if (name == null && paramStructure != MethodObjectParamStructure.byname) {
                throw new IllegalArgumentException(
                    String.format(
                        "create a method which has parameter descibe by a reference and %s is not supported yet!",
                        paramStructure
                    )
                );
            } else if (name == null) {
                return "";
            }
            return name;
        }
    }
}

//how to fake for pitest :)
interface BaseMethodMedia<T extends BaseMethodMedia<T>> extends BaseMedia<T> {
    @Override
    default T withValue(final String name, final int value) {
        return (T) this;
    }

    @Override
    default T withValue(final String name, final long value) {
        return (T) this;
    }

    @Override
    default T withValue(final String name, final double value) {
        return (T) this;
    }

    @Override
    default T withValue(final String name, final boolean value) {
        return (T) this;
    }
}
