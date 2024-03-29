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

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

final class BatchJsonRpcExecutor implements JsonRpcExecutor {

    private static final Logger LOG = Logger.getLogger(BatchJsonRpcExecutor.class.getName());
    private static final JsonProvider JSONP = JsonProvider.provider();
    private final JsonRpcExecutionContext<? extends JsonRpcMethod> context;
    private final JsonArray json;

    public BatchJsonRpcExecutor(final JsonRpcExecutionContext<? extends JsonRpcMethod> context, final JsonArray json) {
        this.context = Objects.requireNonNull(context);
        this.json = JSONP.createArrayBuilder(json).build();
    }

    @Override
    public JsonRpcResponse execute() {
        LOG.entering(BatchJsonRpcExecutor.class.getName(), "execute");
        final Collection<JsonRpcExecutor> calls = calls();
        final JsonRpcResponse result;
        if (calls.stream().allMatch(JsonRpcExecutor::isNotification)) {
            result = new NotificationResponse();
        } else {
            result = new BatchJsonRpcReponse(calls);
        }
        LOG.exiting(BatchJsonRpcExecutor.class.getName(), "execute", result);
        return result;
    }

    private Collection<JsonRpcExecutor> calls() {
        LOG.entering(BatchJsonRpcExecutor.class.getName(), "calls");
        final Collection<JsonRpcExecutor> result = json.stream().map(this::asExecutor).toList();
        LOG.exiting(BatchJsonRpcExecutor.class.getName(), "calls", result);
        return result;
    }

    private JsonRpcExecutor asExecutor(final JsonValue value) {
        LOG.entering(BatchJsonRpcExecutor.class.getName(), "asExecutor");
        final JsonRpcExecutor result;
        if (value.getValueType() == JsonValue.ValueType.OBJECT) {
            result = new MethodJsonRpcExecutor(context, value.asJsonObject());
        } else {
            result = ErrorJsonRpcExecutor.invalidRequest();
        }
        LOG.exiting(BatchJsonRpcExecutor.class.getName(), "asExecutor", result);
        return result;
    }

    private static class BatchJsonRpcReponse implements JsonRpcResponse {

        private final Collection<JsonRpcExecutor> calls;

        public BatchJsonRpcReponse(final Collection<JsonRpcExecutor> calls) {
            this.calls = List.copyOf(calls);
        }

        @Override
        public void writeTo(final OutputStream out) {
            try (JsonGenerator generator = JSONP.createGenerator(out)) {
                writeTo(generator);
            }
        }

        @Override
        public void writeTo(final JsonGenerator generator) {
            generator.writeStartArray();
            calls.stream().map(JsonRpcExecutor::execute).forEach(response -> response.writeTo(generator));
            generator.writeEnd();
        }
    }
}
