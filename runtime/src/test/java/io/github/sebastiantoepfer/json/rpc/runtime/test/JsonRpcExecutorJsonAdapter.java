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
package io.github.sebastiantoepfer.json.rpc.runtime.test;

import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutor;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonRpcExecutorJsonAdapter {

    private static final Logger LOG = Logger.getLogger(JsonRpcExecutorJsonAdapter.class.getName());
    private final JsonRpcExecutor executor;

    public JsonRpcExecutorJsonAdapter(final JsonRpcExecutor executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    public JsonValue execute() {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writeResultTo(baos);
            return convertToJson(baos);
        } catch (IOException ex) {
            LOG.log(Level.WARNING, null, ex);
            return JsonValue.NULL;
        }
    }

    private void writeResultTo(final ByteArrayOutputStream baos) {
        executor.execute().writeTo(baos);
    }

    private JsonValue convertToJson(final ByteArrayOutputStream baos) {
        try (final JsonReader reader = Json.createReader(new ByteArrayInputStream(baos.toByteArray()))) {
            return reader.read();
        } catch (Exception e) {
            return JsonValue.NULL;
        }
    }
}
