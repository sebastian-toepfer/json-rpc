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
package io.github.sebastiantoepfer.json.rpc.extension.micrometer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcMethod;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.json.Json;
import java.util.List;
import org.junit.jupiter.api.Test;

class CountedJsonRpcMethodTest {

    @Test
    void should_count_his_call() throws Exception {
        final Counter counter = new SimpleMeterRegistry().counter("list");
        new CountedJsonRpcMethod(counter, new DefaultJsonRpcMethod("test", List.of(), p -> p))
            .execute(Json.createObjectBuilder().add("name", "jane").build());
        assertThat(counter.count(), is(1.0));
    }

    @Test
    void should_call_decored_method() throws Exception {
        assertThat(
            new CountedJsonRpcMethod(
                new SimpleMeterRegistry().counter("list"),
                new DefaultJsonRpcMethod("test", List.of(), p -> p)
            )
                .execute(Json.createObjectBuilder().add("name", "jane").build()),
            is(Json.createObjectBuilder().add("name", "jane").build())
        );
    }

    @Test
    void should_know_his_name() {
        final CountedJsonRpcMethod method = new CountedJsonRpcMethod(
            new SimpleMeterRegistry().counter("list"),
            new DefaultJsonRpcMethod("test", List.of(), p -> p)
        );

        assertThat(method.hasName("test"), is(true));
        assertThat(method.hasName("list"), is(false));
        assertThat(method.name(), is("test"));
    }
}
