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

import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import java.io.OutputStream;
import java.util.logging.Logger;

final class ErrorJsonRpcExecutor implements JsonRpcExecutor {

    private static final Logger LOG = Logger.getLogger(ErrorJsonRpcExecutor.class.getName());
    private static final JsonProvider JSONP = JsonProvider.provider();

    static JsonRpcExecutor parseError() {
        return new ErrorJsonRpcExecutor(ErrorResponse.parseError());
    }

    static JsonRpcExecutor invalidRequest() {
        return new ErrorJsonRpcExecutor(ErrorResponse.invalidRequest());
    }

    private final ErrorResponse response;

    private ErrorJsonRpcExecutor(final ErrorResponse response) {
        this.response = response;
    }

    @Override
    public JsonRpcResponse execute() {
        LOG.entering(ErrorJsonRpcExecutor.class.getName(), "execute");
        LOG.exiting(ErrorJsonRpcExecutor.class.getName(), "execute");
        return response;
    }

    public static class ErrorResponse implements JsonRpcResponse {

        private static final Logger LOG = Logger.getLogger(ErrorResponse.class.getName());

        private static ErrorResponse parseError() {
            return new ErrorResponse(-32700, "Parse error");
        }

        private static ErrorResponse invalidRequest() {
            return new ErrorResponse(-32600, "Invalid Request");
        }

        static JsonRpcResponse nonExistingMethod(final JsonValue id) {
            return new ErrorResponse(-32601, "Method not found", id);
        }

        static JsonRpcResponse customError(final JsonValue id, final JsonRpcExecutionExecption ex) {
            return new ErrorResponse(ex.code(), ex.getMessage(), id);
        }

        private final int code;
        private final String message;
        private final JsonValue id;

        private ErrorResponse(final int code, final String message) {
            this(code, message, JsonValue.NULL);
        }

        private ErrorResponse(final int code, final String message, final JsonValue id) {
            this.code = code;
            this.message = message;
            this.id = id;
        }

        @Override
        public void writeTo(final OutputStream out) {
            LOG.entering(ErrorResponse.class.getName(), "writeTo");
            try (final JsonGenerator generator = JSONP.createGenerator(out)) {
                writeTo(generator);
            }
            LOG.exiting(ErrorResponse.class.getName(), "writeTo");
        }

        @Override
        public void writeTo(final JsonGenerator generator) {
            LOG.entering(ErrorResponse.class.getName(), "writeTo");
            generator
                .writeStartObject()
                .write("jsonrpc", "2.0")
                .writeStartObject("error")
                .write("code", code)
                .write("message", message)
                .writeEnd()
                .write("id", id)
                .writeEnd();
            LOG.exiting(ErrorResponse.class.getName(), "writeTo");
        }
    }
}
