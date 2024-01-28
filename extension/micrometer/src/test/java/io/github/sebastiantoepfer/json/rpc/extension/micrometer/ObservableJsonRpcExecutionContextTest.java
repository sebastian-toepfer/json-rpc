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

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static io.github.sebastiantoepfer.json.rpc.extension.micrometer.DefaultJsonRpcMethodMetrics.CALLTIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcMethod;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class ObservableJsonRpcExecutionContextTest {

    @Test
    void should_return_observable_method_and_use_the_methodname() throws Exception {
        final MeterRegistry meters = new SimpleMeterRegistry();
        assertThat(
            new ObservableJsonRpcExecutionContext<>(meters, new DefaultJsonRpcExecutionContext(), CALLTIME)
                .withMethod(new DefaultJsonRpcMethod("test", List.of("timeout"), params -> sleep(params)))
                .findMethodWithName("test")
                .get()
                .execute(Json.createObjectBuilder().add("timeout", 5).build()),
            is(not(nullValue()))
        );

        assertThat(meters.timer("test.calltime").count(), is(1L));
    }

    @Test
    void should_return_observable_method_and_use_prefixedmethodname() throws Exception {
        final MeterRegistry meters = new SimpleMeterRegistry();
        assertThat(
            new ObservableJsonRpcExecutionContext<>(meters, new DefaultJsonRpcExecutionContext(), "rpc", CALLTIME)
                .withMethod(new DefaultJsonRpcMethod("test", List.of("timeout"), params -> sleep(params)))
                .findMethodWithName("test")
                .get()
                .execute(Json.createObjectBuilder().add("timeout", 5).build()),
            is(not(nullValue()))
        );

        assertThat(meters.timer("rpc.test.calltime").count(), is(1L));
    }

    @Test
    void should_retrun_empty_for_unknow_method() {
        final MeterRegistry meters = new SimpleMeterRegistry();
        assertThat(
            new ObservableJsonRpcExecutionContext<>(meters, new DefaultJsonRpcExecutionContext(), "rpc", CALLTIME)
                .withMethod(new DefaultJsonRpcMethod("test", List.of("timeout"), params -> sleep(params)))
                .findMethodWithName("rpc.test"),
            isEmpty()
        );
    }

    private JsonValue sleep(JsonObject params) {
        try {
            Thread.sleep(params.getInt("timeout"));
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
