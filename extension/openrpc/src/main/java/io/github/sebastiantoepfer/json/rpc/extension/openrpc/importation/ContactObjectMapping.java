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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContactObject;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import java.util.List;

class ContactObjectMapping implements ModelObjectMapping<ContactObject> {

    private final JsonObject json;
    private final JsonObjectModelMapping<ContactObject> mapping;

    ContactObjectMapping(final JsonObject json) {
        this.json = json;
        this.mapping = new JsonObjectModelMapping<>(
            v -> new ContactObject(),
            List.of(
                new OptionalField<>("name", v -> JsonString.class.cast(v).getString(), (v, o) -> o.withName(v)),
                new OptionalField<>("url", v -> JsonString.class.cast(v).getString(), (v, o) -> o.withUrl(v)),
                new OptionalField<>("email", v -> JsonString.class.cast(v).getString(), (v, o) -> o.withEmail(v))
            )
        );
    }

    @Override
    public ContactObject asModelObject() {
        return mapping.asModelObject(json);
    }
}
