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
import io.github.sebastiantoepfer.ddd.media.core.HashMapMedia;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.Method;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethodFunction;
import jakarta.json.JsonValue;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DescribeableJsonRpcMethod implements JsonRpcMethod, Printable {

    private final JsonRpcMethod method;
    private final Method description;

    public DescribeableJsonRpcMethod(final Method description, final JsonRpcMethodFunction function) {
        this.description = Objects.requireNonNull(description);
        final Map<String, Object> desc = description.printOn(new HashMapMedia());
        this.method =
            new DefaultJsonRpcMethod(
                String.valueOf(desc.get("name")),
                ((List<Map<String, Object>>) desc.getOrDefault("params", Map.of())).stream()
                    .map(m -> m.get("name"))
                    .map(String::valueOf)
                    .toList(),
                function
            );
    }

    @Override
    public boolean hasName(final String name) {
        return method.hasName(name);
    }

    @Override
    public JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
        return method.execute(params);
    }

    @Override
    public <T extends Media<T>> T printOn(final T media) {
        return description.printOn(media);
    }
}
