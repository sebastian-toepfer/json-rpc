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
package io.github.sebastiantoepfer.json.rpc.runtime;

import io.github.sebastiantoepfer.json.rpc.runtime.validation.AllOf;
import io.github.sebastiantoepfer.json.rpc.runtime.validation.AnyOf;
import io.github.sebastiantoepfer.json.rpc.runtime.validation.JsonPropertyValidation;
import io.github.sebastiantoepfer.json.rpc.runtime.validation.OfType;
import io.github.sebastiantoepfer.json.rpc.runtime.validation.Rule;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

class JsonRpcMethodValidation {

    private static final JsonProvider JSONP = JsonProvider.provider();
    private static final Rule VALIDATION = new AllOf(
        new JsonPropertyValidation(JSONP.createPointer("/method"), new OfType(JsonValue.ValueType.STRING)),
        new JsonPropertyValidation(
            JSONP.createPointer("/params"),
            new AnyOf(
                new OfType(JsonValue.ValueType.OBJECT),
                new OfType(JsonValue.ValueType.ARRAY),
                new OfType(JsonValue.ValueType.NULL)
            )
        )
    );
    private final JsonObject json;

    public JsonRpcMethodValidation(final JsonObject json) {
        this.json = json;
    }

    public boolean isValid() {
        return VALIDATION.isValid(json);
    }
}
