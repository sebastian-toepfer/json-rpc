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

import io.github.sebastiantoepfer.ddd.common.Media;
import io.github.sebastiantoepfer.ddd.common.Printable;
import io.github.sebastiantoepfer.ddd.media.json.JsonObjectPrintable;
import io.github.sebastiantoepfer.ddd.printables.core.CompositePrintable;
import io.github.sebastiantoepfer.ddd.printables.core.NamedStringPrintable;
import jakarta.json.JsonObject;

/**
 * not a real json schema
 */
public final class JsonSchemaObject implements Printable {

    private final CompositePrintable values;

    public JsonSchemaObject(final JsonObject json) {
        this(new CompositePrintable().withPrintable(new JsonObjectPrintable(json)));
    }

    public JsonSchemaObject() {
        this(new CompositePrintable());
    }

    private JsonSchemaObject(final CompositePrintable values) {
        this.values = values;
    }

    public JsonSchemaObject withType(final String type) {
        return new JsonSchemaObject(values.withPrintable(new NamedStringPrintable("type", type)));
    }

    @Override
    public <T extends Media<T>> T printOn(final T media) {
        return values.printOn(media);
    }
}
