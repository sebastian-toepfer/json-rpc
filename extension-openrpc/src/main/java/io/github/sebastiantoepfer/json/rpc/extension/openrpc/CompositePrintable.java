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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class CompositePrintable implements Printable {

    private final List<Printable> values;

    public CompositePrintable() {
        this(List.of());
    }

    private CompositePrintable(final List<Printable> values) {
        this.values = List.copyOf(values);
    }

    public CompositePrintable withPrintable(final Printable value) {
        final List<Printable> newValues = new ArrayList<>(values);
        newValues.add(Objects.requireNonNull(value));
        return new CompositePrintable(newValues);
    }

    @Override
    public <T extends Media<T>> T printOn(final T media) {
        return values.stream().reduce(media, (m, p) -> p.printOn(m), (l, r) -> null);
    }
}
