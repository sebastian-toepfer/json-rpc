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
package io.github.sebastiantoepfer.json.rpc.runtime.validation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class JsonPropertyValidation implements Rule {

    private final JsonPointer pointer;
    private final Collection<Rule> rules;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public JsonPropertyValidation(final JsonPointer pointer, final Rule rule, final Rule... rules) {
        this.pointer = pointer;
        this.rules = new ArrayList<>();
        this.rules.add(rule);
        this.rules.addAll(Arrays.asList(rules));
    }

    @Override
    public boolean isValid(final JsonValue value) {
        return value.getValueType() == JsonValue.ValueType.OBJECT && isValid(value.asJsonObject());
    }

    public boolean isValid(final JsonObject json) {
        final JsonValue value;
        if (pointer.containsValue(json)) {
            value = pointer.getValue(json);
        } else {
            value = JsonValue.NULL;
        }
        return isValueValid(value);
    }

    private boolean isValueValid(final JsonValue value) {
        return rules.stream().allMatch(rule -> rule.isValid(value));
    }
}
