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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc;

import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.jsonschema.JsonSchemas;
import io.github.sebastiantoepfer.jsonschema.Validator;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.IntStream;

final class MethodParameters {

    private static final JsonProvider JSONP = JsonProvider.provider();
    private final List<String> parameters;
    private final Validator validator;

    public MethodParameters(final List<MethodParamMedia> parameters) {
        this.parameters = parameters
            .stream()
            .map(MethodParamMedia::name)
            .map(n -> n.orElseThrow(() -> new IllegalArgumentException("parameter with references not supported yet!")))
            .toList();
        validator = JsonSchemas.load(
            JSONP.createObjectBuilder()
                .add(
                    "properties",
                    parameters
                        .stream()
                        .map(p -> Map.entry(p.name().orElse(""), p.schema()))
                        .collect(
                            Collector.of(
                                JSONP::createObjectBuilder,
                                (JsonObjectBuilder b, Map.Entry<String, JsonValue> v) ->
                                    b.add(v.getKey(), v.getValue()),
                                JsonObjectBuilder::addAll
                            )
                        )
                )
                .add(
                    "required",
                    parameters
                        .stream()
                        .filter(MethodParamMedia::isRequired)
                        .map(MethodParamMedia::name)
                        .flatMap(Optional::stream)
                        .map(JSONP::createValue)
                        .collect(
                            Collector.of(JSONP::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::addAll)
                        )
                )
                .build()
        ).validator();
    }

    JsonObject createValidParameterObjectFrom(final JsonValue parameters) throws JsonRpcExecutionExecption {
        final JsonObject result = createAsObject(parameters);
        if (schemaValidator().isValid(result)) {
            return result;
        } else {
            throw new JsonRpcExecutionExecption(-1, "parameter are not valid!");
        }
    }

    private JsonObject createAsObject(final JsonValue currentParameters) {
        final JsonObject result;
        if (currentParameters == null || currentParameters == JsonValue.NULL) {
            result = JsonValue.EMPTY_JSON_OBJECT;
        } else if (currentParameters.getValueType() == JsonValue.ValueType.OBJECT) {
            result = currentParameters.asJsonObject();
        } else {
            result = createAsObject(currentParameters.asJsonArray());
        }
        return result;
    }

    private JsonObject createAsObject(final JsonArray asJsonArray) {
        return IntStream.range(0, Math.min(asJsonArray.size(), parameters.size()))
            .boxed()
            .map(i -> Map.entry(parameters.get(i), asJsonArray.get(i)))
            .collect(
                Collector.of(
                    JSONP::createObjectBuilder,
                    (JsonObjectBuilder b, Map.Entry<String, JsonValue> v) -> b.add(v.getKey(), v.getValue()),
                    JsonObjectBuilder::addAll,
                    JsonObjectBuilder::build
                )
            );
    }

    private Validator schemaValidator() {
        return validator;
    }
}
