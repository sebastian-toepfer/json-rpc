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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec;

import io.github.sebastiantoepfer.ddd.media.json.JsonObjectPrintable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.CompositePrintable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.NamedStringPrintable;
import jakarta.json.JsonObject;

/**
 * not a real json schema
 */
public final class Schema extends BaseOpenRpc {

    public Schema(final JsonObject json) {
        this(new CompositePrintable().withPrintable(new JsonObjectPrintable(json)));
    }

    public Schema() {
        this(new CompositePrintable());
    }

    private Schema(final CompositePrintable values) {
        super(values);
    }

    public Schema withType(final String type) {
        return new Schema(values().withPrintable(new NamedStringPrintable("type", type)));
    }
}
