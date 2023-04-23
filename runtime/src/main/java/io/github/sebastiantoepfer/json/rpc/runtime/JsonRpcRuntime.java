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

import io.github.sebastiantoepfer.json.rpc.runtime.json.JsonObjectString;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParsingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JsonRpcRuntime {

    private static final Logger LOG = Logger.getLogger(JsonRpcRuntime.class.getName());
    private final JsonRpcExecutionContext context;

    public JsonRpcRuntime(final JsonRpcExecutionContext context) {
        this.context = context;
    }

    public JsonRpcExecutor createExecutorFor(final String json) {
        LOG.entering(JsonRpcRuntime.class.getName(), "createExecutorFor", json);
        JsonRpcExecutor result;
        try {
            result = createExecutorFor(new JsonObjectString(json));
        } catch (JsonParsingException ex) {
            LOG.log(Level.INFO, "Invalid JSON received.", ex);
            result = ErrorJsonRpcExecutor.parseError();
        }
        LOG.exiting(JsonRpcRuntime.class.getName(), "createExecutorFor", result);
        return result;
    }

    private JsonRpcExecutor createExecutorFor(final JsonObjectString json) {
        LOG.entering(JsonRpcRuntime.class.getName(), "createExecutorFor", json);
        JsonRpcExecutor result = createExecutorFor(json.asJsonStructure());
        LOG.exiting(JsonRpcRuntime.class.getName(), "createExecutorFor", result);
        return result;
    }

    private JsonRpcExecutor createExecutorFor(final JsonStructure json) {
        LOG.entering(JsonRpcRuntime.class.getName(), "createExecutorFor", json);
        final JsonRpcExecutor result;
        if (json.getValueType() == JsonValue.ValueType.ARRAY) {
            result = createBatchJsonRpcExecutor(json.asJsonArray());
        } else {
            result = createMethodJsonJsonRpcExecutor(json.asJsonObject());
        }
        LOG.exiting(JsonRpcRuntime.class.getName(), "createExecutorFor", result);
        return result;
    }

    private JsonRpcExecutor createBatchJsonRpcExecutor(final JsonArray array) {
        LOG.entering(JsonRpcRuntime.class.getName(), "createBatchJsonRpcExecutor", array);
        final JsonRpcExecutor result;
        if (array.isEmpty()) {
            result = ErrorJsonRpcExecutor.invalidRequest();
        } else {
            result = new BatchJsonRpcExecutor(context, array);
        }
        LOG.exiting(JsonRpcRuntime.class.getName(), "createBatchJsonRpcExecutor", result);
        return result;
    }

    private JsonRpcExecutor createMethodJsonJsonRpcExecutor(final JsonObject json) {
        LOG.entering(JsonRpcRuntime.class.getName(), "createMethodJsonJsonRpcExecutor", json);
        final JsonRpcExecutor result;
        if (new JsonRpcMethodValidation(json).isValid()) {
            result = new SingleMethodJsonRpcExecutor(context, json);
        } else {
            result = ErrorJsonRpcExecutor.invalidRequest();
        }
        LOG.exiting(JsonRpcRuntime.class.getName(), "createMethodJsonJsonRpcExecutor", result);
        return result;
    }
}
