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

import io.github.sebastiantoepfer.ddd.common.Printable;
import io.github.sebastiantoepfer.ddd.media.json.JsonObjectMedia;
import jakarta.json.JsonValue;
import java.util.Objects;
import java.util.Optional;

final class MethodParamMedia implements BaseMethodMedia<MethodParamMedia> {

    private final String parameterName;
    private final Printable schema;
    private final boolean required;

    MethodParamMedia() {
        this(null, null, false);
    }

    private MethodParamMedia(final String name, final Printable schema, final boolean required) {
        this.parameterName = name;
        this.schema = schema;
        this.required = required;
    }

    @Override
    public MethodParamMedia withValue(final String name, final String value) {
        final MethodParamMedia result;
        if (Objects.equals("name", name)) {
            result = new MethodParamMedia(value, schema, required);
        } else {
            result = this;
        }
        return result;
    }

    @Override
    public MethodParamMedia withValue(final String name, final Printable value) {
        final MethodParamMedia result;
        if (Objects.equals("schema", name)) {
            result = new MethodParamMedia(parameterName, value, required);
        } else {
            result = this;
        }
        return result;
    }

    @Override
    public MethodParamMedia withValue(final String name, final boolean value) {
        final MethodParamMedia result;
        if (Objects.equals("required", name)) {
            result = new MethodParamMedia(parameterName, schema, value);
        } else {
            result = this;
        }
        return result;
    }

    boolean isRequired() {
        return required;
    }

    JsonValue schema() {
        return schema.printOn(new JsonObjectMedia());
    }

    Optional<String> name() {
        return Optional.ofNullable(parameterName);
    }
}
