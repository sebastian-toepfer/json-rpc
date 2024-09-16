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

import io.github.sebastiantoepfer.ddd.common.Printable;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContactObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ErrorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ErrorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ExamplePairingObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ExamplePairingOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ExternalDocumentationObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.LicenseObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.LinkObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.LinkOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ReferenceObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ServerObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagOrReference;
import io.github.sebastiantoepfer.jsonschema.JsonSchema;
import io.github.sebastiantoepfer.jsonschema.JsonSchemas;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class JsonMappings implements Mappings<JsonObject> {

    private final String referencePropertyName;

    public JsonMappings() {
        this.referencePropertyName = "$ref";
    }

    @Override
    public ModelObjectMapping<InfoObject> info(final JsonObject object) {
        return new JsonObjectModelMapping<>(
            object,
            v -> new InfoObject(v.getString("title"), v.getString("version")),
            List.of(
                new JsonObjectPropertyModificationRule<>(
                    "description",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withDescription(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "termsOfService",
                    new DefaultPropertyModificationRule<>(
                        v -> {
                            try {
                                return URI.create(JsonString.class.cast(v).getString()).toURL();
                            } catch (MalformedURLException e) {
                                return null;
                            }
                        },
                        (v, o) -> o.withTermsOfService(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "contact",
                    new DefaultPropertyModificationRule<>(
                        v -> contact(v.asJsonObject()).asModelObject(),
                        (v, o) -> o.withContact(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "license",
                    new DefaultPropertyModificationRule<>(
                        v -> licence(v.asJsonObject()).asModelObject(),
                        (v, o) -> o.withLicense(v)
                    )
                )
            )
        );
    }

    @Override
    public ModelObjectMapping<ContactObject> contact(final JsonObject object) {
        return new JsonObjectModelMapping<>(
            object,
            v -> new ContactObject(),
            List.of(
                new JsonObjectPropertyModificationRule<>(
                    "name",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withName(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "url",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withUrl(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "email",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withEmail(v)
                    )
                )
            )
        );
    }

    @Override
    public ModelObjectMapping<LicenseObject> licence(final JsonObject object) {
        return new JsonObjectModelMapping<>(
            object,
            v -> new LicenseObject(),
            List.of(
                new JsonObjectPropertyModificationRule<>(
                    "name",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withName(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "url",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withUrl(v)
                    )
                )
            )
        );
    }

    @Override
    public ModelObjectMapping<ServerObject> server(final JsonObject object) {
        return new JsonObjectModelMapping<>(
            object,
            v -> {
                try {
                    return new ServerObject(URI.create(v.getString("url")).toURL());
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException();
                }
            },
            List.of()
        );
    }

    @Override
    public ModelObjectMapping<MethodObject> method(final JsonObject object) {
        return new JsonObjectModelMapping<>(
            object,
            v ->
                new MethodObject(
                    v.getString("name"),
                    v
                        .getJsonArray("params")
                        .stream()
                        .map(JsonValue::asJsonObject)
                        .map(this::contentDescriptorOrReference)
                        .map(ModelObjectMapping::asModelObject)
                        .toList()
                ),
            List.of(
                new JsonObjectPropertyModificationRule<>(
                    "description",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withDescription(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "summary",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withSummary(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "servers",
                    new DefaultPropertyModificationRule<>(
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(this::server)
                                .map(ModelObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withServers(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "tags",
                    new DefaultPropertyModificationRule<>(
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(this::tagOrReference)
                                .map(ModelObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withTags(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "paramStructure",
                    new DefaultPropertyModificationRule<>(
                        v -> MethodObject.MethodObjectParamStructure.valueOf(JsonString.class.cast(v).getString()),
                        (v, o) -> o.withParamStructure(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "result",
                    new DefaultPropertyModificationRule<>(
                        v -> contentDescriptorOrReference(v.asJsonObject()).asModelObject(),
                        (v, o) -> o.withResult(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "errors",
                    new DefaultPropertyModificationRule<>(
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(this::errorOrReference)
                                .map(ModelObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withErrors(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "links",
                    new DefaultPropertyModificationRule<>(
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(this::linkOrReference)
                                .map(ModelObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withLinks(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "examples",
                    new DefaultPropertyModificationRule<>(
                        v ->
                            v
                                .asJsonArray()
                                .stream()
                                .map(JsonValue::asJsonObject)
                                .map(this::examplePairingOrReference)
                                .map(ModelObjectMapping::asModelObject)
                                .toList(),
                        (v, o) -> o.withExamples(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "deprecated",
                    new DefaultPropertyModificationRule<>(v -> v == JsonValue.TRUE, (v, o) -> o.withDeprecated(v))
                ),
                new JsonObjectPropertyModificationRule<>(
                    "externalDocs",
                    new DefaultPropertyModificationRule<>(
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
            )
        );
    }

    @Override
    public ModelObjectMapping<LinkOrReference> linkOrReference(final JsonObject object) {
        return new OrReferenceMapping<>(
            object,
            LinkOrReference.Reference::new,
            LinkOrReference.Object::new,
            this::link
        );
    }

    @Override
    public ModelObjectMapping<LinkObject> link(final JsonObject object) {
        return () -> new LinkObject();
    }

    @Override
    public ModelObjectMapping<ExamplePairingOrReference> examplePairingOrReference(final JsonObject object) {
        return new OrReferenceMapping<>(
            object,
            ExamplePairingOrReference.Reference::new,
            ExamplePairingOrReference.Object::new,
            this::examplePairing
        );
    }

    @Override
    public ModelObjectMapping<ExamplePairingObject> examplePairing(final JsonObject object) {
        return () -> new ExamplePairingObject(object.getString("name"), List.of());
    }

    @Override
    public ModelObjectMapping<ErrorOrReference> errorOrReference(final JsonObject object) {
        return new OrReferenceMapping<>(
            object,
            ErrorOrReference.Reference::new,
            ErrorOrReference.Object::new,
            this::error
        );
    }

    @Override
    public ModelObjectMapping<ErrorObject> error(final JsonObject object) {
        return () -> new ErrorObject(object.getInt("code"), object.getString("message"));
    }

    @Override
    public ModelObjectMapping<ContentDescriptorOrReference> contentDescriptorOrReference(final JsonObject object) {
        return new OrReferenceMapping<>(
            object,
            ContentDescriptorOrReference.Reference::new,
            ContentDescriptorOrReference.Object::new,
            this::contentDescriptor
        );
    }

    @Override
    public ModelObjectMapping<ContentDescriptorObject> contentDescriptor(final JsonObject object) {
        return new JsonObjectModelMapping<>(
            object,
            v ->
                new ContentDescriptorObject(
                    v.getString("name"),
                    jsonSchemaOrReference(v.getJsonObject("schema")).asModelObject()
                ),
            List.of(
                new JsonObjectPropertyModificationRule<>(
                    "description",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withDescription(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "summary",
                    new DefaultPropertyModificationRule<>(
                        v -> JsonString.class.cast(v).getString(),
                        (v, o) -> o.withSummary(v)
                    )
                ),
                new JsonObjectPropertyModificationRule<>(
                    "required",
                    new DefaultPropertyModificationRule<>(v -> v == JsonValue.TRUE, (v, o) -> o.withRequired(v))
                ),
                new JsonObjectPropertyModificationRule<>(
                    "deprecated",
                    new DefaultPropertyModificationRule<>(v -> v == JsonValue.TRUE, (v, o) -> o.withDeprecated(v))
                )
            )
        );
    }

    @Override
    public ModelObjectMapping<JsonSchemaOrReference> jsonSchemaOrReference(final JsonObject object) {
        return new OrReferenceMapping<>(
            object,
            JsonSchemaOrReference.Reference::new,
            JsonSchemaOrReference.Object::new,
            this::jsonSchema
        );
    }

    @Override
    public ModelObjectMapping<JsonSchema> jsonSchema(final JsonObject object) {
        return () -> JsonSchemas.load(object);
    }

    @Override
    public ModelObjectMapping<TagOrReference> tagOrReference(final JsonObject object) {
        return new OrReferenceMapping<>(object, TagOrReference.Reference::new, TagOrReference.Object::new, this::tag);
    }

    @Override
    public ModelObjectMapping<TagObject> tag(final JsonObject json) {
        return () -> new TagObject("");
    }

    private ModelObjectMapping<ReferenceObject> reference(final JsonObject object) {
        return () -> new ReferenceObject(object.getString(referencePropertyName));
    }

    private class OrReferenceMapping<U extends ModelObjectMapping<O>, O extends Printable, T extends Printable>
        implements ModelObjectMapping<T> {

        private final JsonObject object;
        private final Function<ReferenceObject, T> referenceCreator;
        private final Function<O, T> objectCreator;
        private final Function<JsonObject, ModelObjectMapping<O>> mappingCreator;

        public OrReferenceMapping(
            final JsonObject object,
            final Function<ReferenceObject, T> referenceCreator,
            final Function<O, T> objectCreator,
            final Function<JsonObject, ModelObjectMapping<O>> mappingCreator
        ) {
            this.object = Objects.requireNonNull(object);
            this.referenceCreator = Objects.requireNonNull(referenceCreator);
            this.objectCreator = Objects.requireNonNull(objectCreator);
            this.mappingCreator = Objects.requireNonNull(mappingCreator);
        }

        @Override
        public T asModelObject() {
            final T result;
            if (object.containsKey(referencePropertyName)) {
                result = referenceCreator.apply(reference(object).asModelObject());
            } else {
                result = objectCreator.apply(mappingCreator.apply(object).asModelObject());
            }
            return result;
        }
    }
}
