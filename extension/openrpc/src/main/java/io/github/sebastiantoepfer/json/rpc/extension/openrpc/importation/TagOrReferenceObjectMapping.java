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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ReferenceObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagOrReference;
import jakarta.json.JsonObject;

class TagOrReferenceObjectMapping implements ModelObjectMapping<TagOrReference> {

    private final JsonObject json;

    public TagOrReferenceObjectMapping(final JsonObject json) {
        this.json = json;
    }

    @Override
    public TagOrReference asModelObject() {
        final TagOrReference result;
        if (json.containsKey("$ref")) {
            result = new TagOrReference.Reference(new ReferenceObject(json.getString("$ref")));
        } else {
            result = new TagOrReference.Object(new TagObject(""));
        }
        return result;
    }
}
