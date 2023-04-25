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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParsingException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DefaultJsonRpcRuntime implements JsonRpcRuntime {

    private static final Logger LOG = Logger.getLogger(DefaultJsonRpcRuntime.class.getName());
    private final JsonRpcExecutionContext context;

    public DefaultJsonRpcRuntime(final JsonRpcExecutionContext context) {
        this.context = context;
    }

    @Override
    public JsonRpcExecutor createExecutorFor(final Reader json) {
        LOG.entering(DefaultJsonRpcRuntime.class.getName(), "createExecutorFor", json);
        JsonRpcExecutor result;
        try {
            result = createExecutorFor(Json.createParser(json));
        } catch (JsonParsingException ex) {
            LOG.log(Level.INFO, "Invalid JSON received.", ex);
            result = ErrorJsonRpcExecutor.parseError();
        }
        LOG.exiting(DefaultJsonRpcRuntime.class.getName(), "createExecutorFor", result);
        return result;
    }

    private JsonRpcExecutor createExecutorFor(final JsonParser parser) {
        LOG.entering(DefaultJsonRpcRuntime.class.getName(), "createExecutorFor", parser);
        final JsonParser.Event event = parser.next();
        final JsonRpcExecutor result =
            switch (event) {
                case START_ARRAY -> createBatchJsonRpcExecutor(parser.getArray());
                case START_OBJECT -> createMethodJsonJsonRpcExecutor(parser.getObject());
                default -> ErrorJsonRpcExecutor.invalidRequest();
            };
        LOG.exiting(DefaultJsonRpcRuntime.class.getName(), "createExecutorFor", result);
        return result;
    }

    private JsonRpcExecutor createBatchJsonRpcExecutor(final JsonArray array) {
        LOG.entering(DefaultJsonRpcRuntime.class.getName(), "createBatchJsonRpcExecutor", array);
        final JsonRpcExecutor result;
        if (array.isEmpty()) {
            result = ErrorJsonRpcExecutor.invalidRequest();
        } else {
            result = new BatchJsonRpcExecutor(context, array);
        }
        LOG.exiting(DefaultJsonRpcRuntime.class.getName(), "createBatchJsonRpcExecutor", result);
        return result;
    }

    private JsonRpcExecutor createMethodJsonJsonRpcExecutor(final JsonObject json) {
        LOG.entering(DefaultJsonRpcRuntime.class.getName(), "createMethodJsonJsonRpcExecutor", json);
        final JsonRpcExecutor result = new MethodJsonRpcExecutor(context, json);
        LOG.exiting(DefaultJsonRpcRuntime.class.getName(), "createMethodJsonJsonRpcExecutor", result);
        return result;
    }
}
