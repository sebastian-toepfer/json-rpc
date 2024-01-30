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
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.ddd.media.core.HashMapMedia;
import jakarta.json.Json;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

class InfoObjectMappingTest {

    @Test
    void should_create_with_description() {
        assertThat(
            new InfoObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("title", "title")
                    .add("version", "1.0.0")
                    .add("description", "description")
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("description", "description")
        );
    }

    @Test
    void should_create_with_termsOfService() throws Exception {
        assertThat(
            new InfoObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("title", "title")
                    .add("version", "1.0.0")
                    .add("termsOfService", "http://localhost")
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            hasEntry("termsOfService", "http://localhost")
        );
    }

    @Test
    void should_create_with_contact() throws Exception {
        assertThat(
            new InfoObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("title", "title")
                    .add("version", "1.0.0")
                    .add("contact", Json.createObjectBuilder().add("name", "jane"))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("contact"), hasEntry("name", "jane"))
        );
    }

    @Test
    void should_create_with_license() throws Exception {
        assertThat(
            new InfoObjectMapping(
                Json
                    .createObjectBuilder()
                    .add("title", "title")
                    .add("version", "1.0.0")
                    .add("license", Json.createObjectBuilder().add("name", "MIT"))
                    .build()
            )
                .asModelObject()
                .printOn(new HashMapMedia()),
            (Matcher) hasEntry(is("license"), hasEntry("name", "MIT"))
        );
    }
}
