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
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ErrorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ErrorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ExamplePairingObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ExamplePairingOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.InfoObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.LicenseObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.LinkObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.LinkOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ServerObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.TagOrReference;
import io.github.sebastiantoepfer.jsonschema.JsonSchema;

public interface Mappings<T> {

    ModelObjectMapping<ContactObject> contact(T source);

    ModelObjectMapping<ContentDescriptorObject> contentDescriptor(T source);

    ModelObjectMapping<ContentDescriptorOrReference> contentDescriptorOrReference(T source);

    ModelObjectMapping<ErrorObject> error(T source);

    ModelObjectMapping<ErrorOrReference> errorOrReference(T source);

    ModelObjectMapping<ExamplePairingObject> examplePairing(T source);

    ModelObjectMapping<ExamplePairingOrReference> examplePairingOrReference(T source);

    ModelObjectMapping<InfoObject> info(T source);

    ModelObjectMapping<JsonSchema> jsonSchema(T source);

    ModelObjectMapping<JsonSchemaOrReference> jsonSchemaOrReference(T source);

    ModelObjectMapping<LicenseObject> licence(T source);

    ModelObjectMapping<LinkObject> link(T source);

    ModelObjectMapping<LinkOrReference> linkOrReference(T source);

    ModelObjectMapping<MethodObject> method(T source);

    ModelObjectMapping<ServerObject> server(T source);

    ModelObjectMapping<TagObject> tag(T source);

    ModelObjectMapping<TagOrReference> tagOrReference(T source);

}
