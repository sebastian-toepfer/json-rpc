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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import io.github.sebastiantoepfer.ddd.common.Media;
import io.github.sebastiantoepfer.ddd.common.Printable;
import io.github.sebastiantoepfer.ddd.media.core.decorator.MediaDecorator;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import jakarta.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class Method extends BaseOpenRpc implements JsonRpcMethod {

    private final JsonRpcMethod method;
    private final Map<String, ContentDescriptor> params;

    Method(final JsonRpcMethod method) {
        this(method, new CompositePrintable(), Map.of());
    }

    private Method(
        final JsonRpcMethod method,
        final CompositePrintable values,
        final Map<String, ContentDescriptor> params
    ) {
        super(values);
        this.method = method;
        this.params = Map.copyOf(params);
    }

    @Override
    public boolean hasName(final String name) {
        return method.hasName(name);
    }

    @Override
    public JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
        return method.execute(params);
    }

    @Override
    public <T extends Media<T>> T printOn(final T media) {
        return super.printOn(method.printOn(new MethodParamterMediaDecorator<>(media)).decoratedMedia());
    }

    public Method withTags(final Tag... tags) {
        return new Method(method, values().withPrintable(new NamedArrayPrintable("tags", asList(tags))), params);
    }

    public Method withTags(final Reference... tags) {
        return new Method(method, values().withPrintable(new NamedArrayPrintable("tags", asList(tags))), params);
    }

    public Method withSummary(final String summary) {
        return new Method(method, values().withPrintable(new NamedStringPrintable("summary", summary)), params);
    }

    public Method withDescription(final String description) {
        return new Method(method, values().withPrintable(new NamedStringPrintable("description", description)), params);
    }

    public Method withResultDescription(final ContentDescriptor resultDescription) {
        return new Method(method, values().withPrintable(new NamedPrintable("result", resultDescription)), params);
    }

    public Method withExternalDocs(final ExternalDocs externalDocs) {
        return new Method(method, values().withPrintable(new NamedPrintable("externalDocs", externalDocs)), params);
    }

    public Method withParams(final Map<String, ContentDescriptor> params) {
        return new Method(method, values(), params);
    }

    private class MethodParamterMediaDecorator<T extends Media<T>>
        extends MediaDecorator<T, MethodParamterMediaDecorator<T>> {

        public MethodParamterMediaDecorator(final T decoratedMedia) {
            super(decoratedMedia);
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String name, final String value) {
            return new MethodParamterMediaDecorator<>(decoratedMedia().withValue(name, value));
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final long l) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final double d) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final BigDecimal bd) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final BigInteger bi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final boolean bln) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final Printable prntbl) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String name, final Collection<?> value) {
            final MethodParamterMediaDecorator<T> result;
            if (Objects.equals(name, "params")) {
                result =
                    value
                        .stream()
                        .map(String::valueOf)
                        .map(paraname -> params.getOrDefault(paraname, new ContentDescriptor(paraname, new Schema())))
                        .collect(
                            collectingAndThen(
                                toList(),
                                l -> new MethodParamterMediaDecorator<>(decoratedMedia().withValue(name, l))
                            )
                        );
            } else {
                result = new MethodParamterMediaDecorator<>(decoratedMedia().withValue(name, value));
            }
            return result;
        }

        @Override
        public MethodParamterMediaDecorator<T> withValue(final String string, final MethodParamterMediaDecorator<T> t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
