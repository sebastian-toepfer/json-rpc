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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc.importation;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class DefaultPropertyModificationRule<T, U, R> implements PropertyModificationRule<T, R> {

    private final Function<T, U> mapping;
    private final BiFunction<U, R, R> writer;

    public DefaultPropertyModificationRule(final Function<T, U> mapping, final BiFunction<U, R, R> writer) {
        this.mapping = Objects.requireNonNull(mapping);
        this.writer = Objects.requireNonNull(writer);
    }

    @Override
    public R apply(final R value, final T propertyValueSource) {
        return mapping.andThen(v -> writer.apply(v, value)).apply(propertyValueSource);
    }
}
