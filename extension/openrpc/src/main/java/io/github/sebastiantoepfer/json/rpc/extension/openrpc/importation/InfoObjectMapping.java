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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

final class InfoObjectMapping implements ModelObjectMapping<InfoObject> {

    private final JsonObject json;
    private final JsonObjectModelMapping<InfoObject> mapping;

    InfoObjectMapping(final JsonObject json) {
        this.json = json;
        this.mapping = new JsonObjectModelMapping<>(
            v -> new InfoObject(v.getString("title"), v.getString("version")),
            List.of(
                new OptionalField<>(
                    "description",
                    v -> JsonString.class.cast(v).getString(),
                    (v, o) -> o.withDescription(v)
                ),
                new OptionalField<>(
                    "termsOfService",
                    v -> {
                        try {
                            return URI.create(JsonString.class.cast(v).getString()).toURL();
                        } catch (MalformedURLException e) {
                            return null;
                        }
                    },
                    (v, o) -> o.withTermsOfService(v)
                ),
                new OptionalField<>(
                    "contact",
                    v -> new ContactObjectMapping(v.asJsonObject()).asModelObject(),
                    (v, o) -> o.withContact(v)
                ),
                new OptionalField<>(
                    "license",
                    v -> new LicenseObjectMapping(v.asJsonObject()).asModelObject(),
                    (v, o) -> o.withLicense(v)
                )
            )
        );
    }

    @Override
    public InfoObject asModelObject() {
        return mapping.asModelObject(json);
    }
}
