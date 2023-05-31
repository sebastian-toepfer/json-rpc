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

import static java.util.Arrays.asList;

import io.github.sebastiantoepfer.ddd.common.Media;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.CompositePrintable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.NamedArrayPrintable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.NamedPrintable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.printable.NamedStringPrintable;
import java.util.List;
import java.util.Objects;

public final class Method extends BaseOpenRpc {

    public Method(final String name, final List<ContentDescriptor> params) {
        this(
            new CompositePrintable()
                .withPrintable(new NamedStringPrintable("name", Objects.requireNonNull(name)))
                .withPrintable(new NamedArrayPrintable("params", params))
        );
    }

    private Method(final CompositePrintable values) {
        super(values);
    }

    @Override
    public <T extends Media<T>> T printOn(final T media) {
        return super.printOn(media);
    }

    public Method withTags(final Tag... tags) {
        return new Method(values().withPrintable(new NamedArrayPrintable("tags", asList(tags))));
    }

    public Method withTags(final Reference... tags) {
        return new Method(values().withPrintable(new NamedArrayPrintable("tags", asList(tags))));
    }

    public Method withSummary(final String summary) {
        return new Method(values().withPrintable(new NamedStringPrintable("summary", summary)));
    }

    public Method withDescription(final String description) {
        return new Method(values().withPrintable(new NamedStringPrintable("description", description)));
    }

    public Method withResultDescription(final ContentDescriptor resultDescription) {
        return new Method(values().withPrintable(new NamedPrintable("result", resultDescription)));
    }

    public Method withExternalDocs(final ExternalDocs externalDocs) {
        return new Method(values().withPrintable(new NamedPrintable("externalDocs", externalDocs)));
    }
}
