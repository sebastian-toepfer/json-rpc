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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.CompositePrintable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.NamedPrintable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.NamedStringPrintable;
import java.util.Objects;

public final class Tag extends BaseOpenRpc {

    public Tag(final String name) {
        this(new CompositePrintable().withPrintable(new NamedStringPrintable("name", Objects.requireNonNull(name))));
    }

    private Tag(final CompositePrintable values) {
        super(values);
    }

    public Tag withSummary(final String summary) {
        return new Tag(values().withPrintable(new NamedStringPrintable("summary", summary)));
    }

    public Tag withDescription(final String description) {
        return new Tag(values().withPrintable(new NamedStringPrintable("description", description)));
    }

    public Tag withExternalDocs(final ExternalDocs externalDocs) {
        return new Tag(values().withPrintable(new NamedPrintable("externalDocs", externalDocs)));
    }
}
