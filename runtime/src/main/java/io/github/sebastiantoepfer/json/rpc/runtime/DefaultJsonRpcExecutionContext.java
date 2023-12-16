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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class DefaultJsonRpcExecutionContext implements JsonRpcExecutionContext<DefaultJsonRpcMethod> {

    private final Collection<DefaultJsonRpcMethod> methods;

    public DefaultJsonRpcExecutionContext() {
        this(Set.of());
    }

    public DefaultJsonRpcExecutionContext(final Collection<DefaultJsonRpcMethod> methods) {
        this.methods = Set.copyOf(methods);
    }

    @Override
    public JsonRpcExecutionContext withMethod(final DefaultJsonRpcMethod method) {
        final Set<DefaultJsonRpcMethod> newMethods = new HashSet<>(this.methods);
        newMethods.add(method);
        return new DefaultJsonRpcExecutionContext(newMethods);
    }

    @Override
    public Stream<DefaultJsonRpcMethod> methods() {
        return methods.stream();
    }
}
