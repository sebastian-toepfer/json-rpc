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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc.importation;

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ExternalDocumentationObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

final class MethodObjectMapping implements ModelObjectMapping<MethodObject> {

    private final JsonObject methodObject;
    private final JsonObjectModelMapping<MethodObject> mapping;

    MethodObjectMapping(final JsonObject methodObject) {
        this.methodObject = Objects.requireNonNull(methodObject);
        this.mapping =
            new JsonObjectModelMapping<>(
                v ->
                    new MethodObject(
                        v.getString("name"),
                        v
                            .getJsonArray("params")
                            .stream()
                            .map(JsonValue::asJsonObject)
                            .map(ContentDescriptorOrReferenceObjectMapping::new)
                            .map(ContentDescriptorOrReferenceObjectMapping::asModelObject)
                            .toList()
                    ),
                List.of(
                    new OptionalField<>(
                        "description",
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withDescription(v)
                    ),
                    new OptionalField<>(
                        "summary",
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withSummary(v)
                    ),
                    new OptionalField<>(
                        "servers",
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(ServerObjectMapping::new)
                                .map(ServerObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withServers(v)
                    ),
                    new OptionalField<>(
                        "tags",
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(TagOrReferenceObjectMapping::new)
                                .map(TagOrReferenceObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withTags(v)
                    ),
                    new OptionalField<>(
                        "paramStructure",
                        v -> MethodObject.MethodObjectParamStructure.valueOf(JsonString.class.cast(v).getString()),
                        (v, o) -> o.withParamStructure(v)
                    ),
                    new OptionalField<>(
                        "result",
                        v -> new MethodObjectResultObjectMapping(v.asJsonObject()).asModelObject(),
                        (v, o) -> o.withResult(v)
                    ),
                    new OptionalField<>(
                        "errors",
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(ErrorOrReferenceObjectMapping::new)
                                .map(ErrorOrReferenceObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withErrors(v)
                    ),
                    new OptionalField<>(
                        "links",
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(LinkOrReferenceObjectMapping::new)
                                .map(LinkOrReferenceObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withLinks(v)
                    ),
                    new OptionalField<>(
                        "examples",
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(ExamplePairingOrReferenceObjectMapping::new)
                                .map(ExamplePairingOrReferenceObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withExamples(v)
                    ),
                    new OptionalField<>("deprecated", v -> v == JsonValue.TRUE, (v, o) -> o.withDeprecated(v)),
                    new OptionalField<>(
                        "externalDocs",
                        v -> {
                            try {
                                return new ExternalDocumentationObject(
                                    URI.create(v.asJsonObject().getString("url")).toURL()
                                );
                            } catch (MalformedURLException e) {
                                throw new IllegalArgumentException(e);
                            }
                        },
                        (v, o) -> o.withExternalDocs(v)
                    )
                )
            );
    }

    @Override
    public MethodObject asModelObject() {
        return mapping.asModelObject(methodObject);
    }
}
