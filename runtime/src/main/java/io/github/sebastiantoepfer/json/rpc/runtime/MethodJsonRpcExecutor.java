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

import io.github.sebastiantoepfer.common.condition4j.core.AllOf;
import io.github.sebastiantoepfer.common.condition4j.core.AnyOf;
import io.github.sebastiantoepfer.common.condition4j.json.JsonPropertyWhichFulfilThe;
import io.github.sebastiantoepfer.common.condition4j.json.JsonValueOfType;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

final class MethodJsonRpcExecutor implements JsonRpcExecutor {

    private static final Logger LOG = Logger.getLogger(MethodJsonRpcExecutor.class.getName());
    private static final JsonProvider JSONP = JsonProvider.provider();
    private final JsonRpcExecutor delegate;

    public MethodJsonRpcExecutor(
        final JsonRpcExecutionContext<? extends JsonRpcMethod> context,
        final JsonObject json
    ) {
        if (
            new AllOf<>(
                new JsonPropertyWhichFulfilThe(
                    JSONP.createPointer("/method"),
                    new JsonValueOfType(JsonValue.ValueType.STRING)
                ),
                new JsonPropertyWhichFulfilThe(
                    JSONP.createPointer("/params"),
                    new AnyOf<>(
                        new JsonValueOfType(JsonValue.ValueType.OBJECT),
                        new JsonValueOfType(JsonValue.ValueType.ARRAY),
                        new JsonValueOfType(JsonValue.ValueType.NULL)
                    )
                )
            ).isFulfilledBy(json)
        ) {
            delegate = new SingleMethodJsonRpcExecutor(context, json);
        } else {
            delegate = ErrorJsonRpcExecutor.invalidRequest();
        }
    }

    @Override
    public JsonRpcResponse execute() {
        LOG.entering(MethodJsonRpcExecutor.class.getName(), "execute");
        final JsonRpcResponse result = delegate.execute();
        LOG.exiting(MethodJsonRpcExecutor.class.getName(), "execute", result);
        return result;
    }

    @Override
    public boolean isNotification() {
        LOG.entering(MethodJsonRpcExecutor.class.getName(), "isNotification");
        final boolean result = delegate.isNotification();
        LOG.entering(MethodJsonRpcExecutor.class.getName(), "isNotification", result);
        return result;
    }

    private static final class SingleMethodJsonRpcExecutor implements JsonRpcExecutor {

        private static final Logger LOG = Logger.getLogger(SingleMethodJsonRpcExecutor.class.getName());
        private final JsonRpcExecutionContext<? extends JsonRpcMethod> context;
        private final JsonObject json;

        public SingleMethodJsonRpcExecutor(
            final JsonRpcExecutionContext<? extends JsonRpcMethod> context,
            final JsonObject json
        ) {
            this.context = context;
            this.json = json;
        }

        @Override
        public JsonRpcResponse execute() {
            LOG.entering(SingleMethodJsonRpcExecutor.class.getName(), "execute");
            final JsonRpcResponse result = context
                .findMethodWithName(methodName())
                .map(WrappedJsonRpcMethod::new)
                .map(this::execute)
                .map(SingleMethodJsonRpcResponse::new)
                .map(JsonRpcResponse.class::cast)
                .orElseGet(() -> ErrorJsonRpcExecutor.ErrorResponse.nonExistingMethod(id()));
            LOG.exiting(SingleMethodJsonRpcExecutor.class.getName(), "execute", result);
            return result;
        }

        private JsonRpcResponse execute(final JsonRpcMethod method) {
            LOG.entering(SingleMethodJsonRpcExecutor.class.getName(), "execute", method);
            JsonRpcResponse result;
            try {
                result = new MethodResultJsonRpcResponse((method.execute(params())));
            } catch (JsonRpcExecutionExecption ex) {
                LOG.log(Level.FINE, "Catch execption", ex);
                result = ErrorJsonRpcExecutor.ErrorResponse.customError(id(), ex);
            }
            LOG.exiting(SingleMethodJsonRpcExecutor.class.getName(), "execute", result);
            return result;
        }

        @Override
        public boolean isNotification() {
            LOG.entering(SingleMethodJsonRpcExecutor.class.getName(), "isNotification");
            final boolean result = id() == null || id().getValueType() == JsonValue.ValueType.NULL;
            LOG.exiting(SingleMethodJsonRpcExecutor.class.getName(), "isNotification", result);
            return result;
        }

        @Override
        public String toString() {
            return "SingleMethodJsonRpcExecutor{methodName = " + methodName() + ", id = " + id() + '}';
        }

        private String methodName() {
            LOG.entering(SingleMethodJsonRpcExecutor.class.getName(), "methodName");
            final String result = json.getString("method");
            LOG.exiting(SingleMethodJsonRpcExecutor.class.getName(), "methodName", result);
            return result;
        }

        private JsonValue id() {
            LOG.entering(SingleMethodJsonRpcExecutor.class.getName(), "id");
            final JsonValue result = json.get("id");
            LOG.exiting(SingleMethodJsonRpcExecutor.class.getName(), "id", result);
            return result;
        }

        private JsonValue params() {
            LOG.entering(SingleMethodJsonRpcExecutor.class.getName(), "params");
            final JsonValue result = json.get("params");
            LOG.exiting(SingleMethodJsonRpcExecutor.class.getName(), "params", result);
            return result;
        }

        private class MethodResultJsonRpcResponse implements JsonRpcResponse {

            private final JsonValue methodExecutionResult;

            public MethodResultJsonRpcResponse(final JsonValue methodExecutionResult) {
                this.methodExecutionResult = Objects.requireNonNull(methodExecutionResult);
            }

            @Override
            public void writeTo(final OutputStream out) {
                try (final JsonGenerator generator = JSONP.createGenerator(out)) {
                    writeTo(generator);
                }
            }

            @Override
            public void writeTo(final JsonGenerator generator) {
                generator
                    .writeStartObject()
                    .write("jsonrpc", json.get("jsonrpc"))
                    .write("result", methodExecutionResult)
                    .write("id", id())
                    .writeEnd();
            }
        }

        private class SingleMethodJsonRpcResponse implements JsonRpcResponse {

            private final JsonRpcResponse response;

            public SingleMethodJsonRpcResponse(final JsonRpcResponse response) {
                this.response = Objects.requireNonNull(response);
            }

            @Override
            public void writeTo(final OutputStream out) {
                if (!isNotification()) {
                    response.writeTo(out);
                }
            }

            @Override
            public void writeTo(final JsonGenerator generator) {
                if (!isNotification()) {
                    response.writeTo(generator);
                }
            }
        }
    }
}
