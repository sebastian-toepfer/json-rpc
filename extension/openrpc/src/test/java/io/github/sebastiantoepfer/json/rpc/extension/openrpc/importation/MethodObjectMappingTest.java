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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.ddd.media.core.HashMapMedia;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

class MethodObjectMappingTest {

    @Test
    void should_create_with_name() {
        assertThat(
            new MethodObjectMapping(
                Json.createObjectBuilder().add("name", "name").add("params", JsonValue.EMPTY_JSON_ARRAY).build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("name", "name")
        );
    }

    @Test
    void should_create_with_description() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
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
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("summary", "summary")
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("summary", "summary")
        );
    }

    @Test
    void should_create_with_deprecated() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("deprecated", true)
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("deprecated", true)
        );
    }

    @Test
    void should_create_with_non_deprecated() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("deprecated", false)
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("deprecated", false)
        );
    }

    @Test
    void should_create_with_servers() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add(
                        "servers",
                        Json.createArrayBuilder().add(Json.createObjectBuilder().add("url", "file:///test"))
                    )
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("servers"), contains(hasEntry("url", "file:/test")))
        );
    }

    @Test
    void should_create_with_tags() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("tags", Json.createArrayBuilder().add(Json.createObjectBuilder().add("$ref", "tag")))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("tags"), contains(hasEntry("$ref", "tag")))
        );
    }

    @Test
    void should_create_with_paramStructure() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("paramStructure", "either")
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry(is("paramStructure"), is("either"))
        );
    }

    @Test
    void should_create_with_result() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("result", Json.createObjectBuilder().add("$ref", "test"))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("result"), hasEntry("$ref", "test"))
        );
    }

    @Test
    void should_create_with_errors() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("errors", Json.createArrayBuilder().add(Json.createObjectBuilder().add("$ref", "test")))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("errors"), contains(hasEntry("$ref", "test")))
        );
    }

    @Test
    void should_create_with_links() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("links", Json.createArrayBuilder().add(Json.createObjectBuilder().add("$ref", "test")))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("links"), contains(hasEntry("$ref", "test")))
        );
    }

    @Test
    void should_create_with_examples() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("examples", Json.createArrayBuilder().add(Json.createObjectBuilder().add("$ref", "test")))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("examples"), contains(hasEntry("$ref", "test")))
        );
    }

    @Test
    void should_create_with_externalDocs() {
        assertThat(
            new MethodObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("name", "name")
                    .add("params", JsonValue.EMPTY_JSON_ARRAY)
                    .add("externalDocs", Json.createObjectBuilder().add("url", "http://www.qwant.de"))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("externalDocs"), hasEntry("url", "http://www.qwant.de"))
        );
    }
}
