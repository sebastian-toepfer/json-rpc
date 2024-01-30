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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.util.List;
import java.util.Objects;

class ContentDescriptorObjectMapping implements ModelObjectMapping<ContentDescriptorObject> {

    private final JsonObject json;
    private final JsonObjectModelMapping<ContentDescriptorObject> mapping;

    public ContentDescriptorObjectMapping(final JsonObject json) {
        this.json = Objects.requireNonNull(json);
        this.mapping =
            new JsonObjectModelMapping<>(
                v ->
                    new ContentDescriptorObject(
                        v.getString("name"),
                        new JsonSchemaOrReferenceObjectMapping(v.getJsonObject("schema")).asModelObject()
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
                    new OptionalField<>("required", v -> v == JsonValue.TRUE, (v, o) -> o.withRequired(v)),
                    new OptionalField<>("deprecated", v -> v == JsonValue.TRUE, (v, o) -> o.withDeprecated(v))
                )
            );
    }

    @Override
    public ContentDescriptorObject asModelObject() {
        return mapping.asModelObject(json);
    }
}
