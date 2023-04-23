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
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public abstract class JsonRpcMethod {

    private static final Logger LOG = Logger.getLogger(JsonRpcMethod.class.getName());
    private final String name;
    private final List<String> parameterNames;

    protected JsonRpcMethod(final String name, final List<String> parameterNames) {
        this.name = Objects.requireNonNull(name);
        this.parameterNames = List.copyOf(parameterNames);
    }

    public final boolean hasName(final String name) {
        return this.name.equals(name);
    }

    public final JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
        LOG.entering(JsonRpcMethod.class.getName(), "execute", params);
        final JsonValue result;
        try {
            if (params.getValueType() == JsonValue.ValueType.ARRAY) {
                result = execute(createNamedParameters(params.asJsonArray()));
            } else {
                result = execute(params.asJsonObject());
            }
        } catch (RuntimeException ex) {
            final JsonRpcExecutionExecption thrown = new JsonRpcExecutionExecption(-1, ex);
            LOG.throwing(JsonRpcMethod.class.getName(), "execute", thrown);
            throw thrown;
        }
        LOG.exiting(JsonRpcMethod.class.getName(), "execute", result);
        return result;
    }

    private JsonObject createNamedParameters(final JsonArray params) {
        LOG.entering(JsonRpcMethod.class.getName(), "createNamedParameters", params);
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (int i = 0; i < params.size(); i++) {
            builder = builder.add(parameterNames.get(i), params.get(i));
        }
        final JsonObject result = builder.build();
        LOG.exiting(JsonRpcMethod.class.getName(), "createNamedParameters", result);
        return result;
    }

    protected abstract JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JsonRpcMethod other = (JsonRpcMethod) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "JsonRpcMethod{" + "name=" + name + ", parameterNames=" + parameterNames + '}';
    }
}
