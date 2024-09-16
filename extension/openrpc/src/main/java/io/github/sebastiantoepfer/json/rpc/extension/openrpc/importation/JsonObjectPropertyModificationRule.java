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
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

final class JsonObjectPropertyModificationRule<T extends JsonValue, R>
    implements PropertyModificationRule<JsonObject, R> {

    private final String jsonPropertyName;
    private final Function<JsonValue, Optional<T>> jsonValueMapping;
    private final PropertyModificationRule<T, R> setter;

    public JsonObjectPropertyModificationRule(
        final String jsonPropertyName,
        final PropertyModificationRule<T, R> setter
    ) {
        this(jsonPropertyName, j -> Optional.of((T) j), setter);
    }

    public JsonObjectPropertyModificationRule(
        final String jsonPropertyName,
        final Function<JsonValue, Optional<T>> jsonValueMapping,
        final PropertyModificationRule<T, R> setter
    ) {
        this.jsonPropertyName = jsonPropertyName;
        this.jsonValueMapping = jsonValueMapping;
        this.setter = setter;
    }

    @Override
    public R apply(final R value, final JsonObject propertyValueSource) {
        return propertyValueSource
            .entrySet()
            .stream()
            .filter(e -> e.getKey().equals(jsonPropertyName))
            .map(Map.Entry::getValue)
            .findFirst()
            .flatMap(jsonValueMapping)
            .map(v -> setter.apply(value, v))
            .orElse(value);
    }
}
