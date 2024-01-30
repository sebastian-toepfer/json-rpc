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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

import io.github.sebastiantoepfer.ddd.media.core.HashMapMedia;
import jakarta.json.Json;
import org.junit.jupiter.api.Test;

class ContentDescriptorObjectMappingTest {

    @Test
    void should_create_with_description() {
        assertThat(
            new ContentDescriptorObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("schema", Json.createObjectBuilder().add("$ref", "http://localhost"))
                    .add("description", "description")
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("description", "description")
        );
    }

    @Test
    void should_create_with_summary() {
        assertThat(
            new ContentDescriptorObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("schema", Json.createObjectBuilder().add("$ref", "http://localhost"))
                    .add("summary", "summary")
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("summary", "summary")
        );
    }

    @Test
    void should_create_with_required() {
        assertThat(
            new ContentDescriptorObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("schema", Json.createObjectBuilder().add("$ref", "http://localhost"))
                    .add("required", true)
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("required", true)
        );
    }

    @Test
    void should_create_with_not_required() {
        assertThat(
            new ContentDescriptorObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("schema", Json.createObjectBuilder().add("$ref", "http://localhost"))
                    .add("required", false)
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("required", false)
        );
    }

    @Test
    void should_create_with_deprecated() {
        assertThat(
            new ContentDescriptorObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("schema", Json.createObjectBuilder().add("$ref", "http://localhost"))
                    .add("deprecated", true)
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("deprecated", true)
        );
    }

    @Test
    void should_create_with_not_deprecated() {
        assertThat(
            new ContentDescriptorObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("schema", Json.createObjectBuilder().add("$ref", "http://localhost"))
                    .add("deprecated", false)
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("deprecated", false)
        );
    }
}
