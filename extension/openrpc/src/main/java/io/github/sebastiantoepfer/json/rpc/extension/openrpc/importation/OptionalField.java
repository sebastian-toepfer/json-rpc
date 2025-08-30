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

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

final class OptionalField<T, R> {

    private final String name;
    private final Function<JsonValue, T> mapping;
    private final BiFunction<T, R, R> setter;

    public OptionalField(final String name, final Function<JsonValue, T> mapping, final BiFunction<T, R, R> setter) {
        this.name = name;
        this.mapping = mapping;
        this.setter = setter;
    }

    public R update(final R value, final JsonObject json) {
        return Optional.ofNullable(json.get(name))
            .map(mapping)
            .map(v -> setter.apply(v, value))
            .orElse(value);
    }
}
