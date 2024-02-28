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

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ReferenceObject;
import jakarta.json.JsonObject;
import java.util.Objects;

final class ContentDescriptorOrReferenceObjectMapping implements ModelObjectMapping<ContentDescriptorOrReference> {

    private final JsonObject contentDesc;

    public ContentDescriptorOrReferenceObjectMapping(final JsonObject contentDesc) {
        this.contentDesc = Objects.requireNonNull(contentDesc);
    }

    @Override
    public ContentDescriptorOrReference asModelObject() {
        final ContentDescriptorOrReference result;
        if (contentDesc.containsKey("$ref")) {
            result = new ContentDescriptorOrReference.Reference(new ReferenceObject(contentDesc.getString("$ref")));
        } else {
            result = new ContentDescriptorOrReference.Object(
                new ContentDescriptorObjectMapping(contentDesc).asModelObject()
            );
        }
        return result;
    }
}
