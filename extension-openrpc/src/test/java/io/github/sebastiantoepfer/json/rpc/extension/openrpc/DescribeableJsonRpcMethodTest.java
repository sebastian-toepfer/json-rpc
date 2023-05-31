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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.Method;
import io.github.sebastiantoepfer.json.rpc.runtime.BaseJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class DescribeableJsonRpcMethodTest {

    @Test
    void should_not_creatable_with_different_names() {
        final JsonRpcMethod jsonRpcMethod = new BaseJsonRpcMethod("list_pets", List.of()) {
            @Override
            protected JsonValue execute(JsonObject params) throws JsonRpcExecutionExecption {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        final Method desc = new Method("get_pet", List.of());

        assertThrows(IllegalArgumentException.class, () -> new DescribeableJsonRpcMethod(jsonRpcMethod, desc));
    }

    @Test
    void should_delegate_has_names() {
        final JsonRpcMethod jsonRpcMethod = new DescribeableJsonRpcMethod(
            new BaseJsonRpcMethod("list_pets", List.of()) {
                @Override
                protected JsonValue execute(JsonObject params) throws JsonRpcExecutionExecption {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            },
            new Method("list_pets", List.of())
        );
        //pitest :(
        assertThat(jsonRpcMethod.hasName("get_pet"), is(false));
        assertThat(jsonRpcMethod.hasName("list_pets"), is(true));
    }
}
