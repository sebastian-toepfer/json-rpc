/*
 * The MIT License
 *
 * Copyright 2023 sebastian.
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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.ddd.media.core.HashMapMedia;
import io.github.sebastiantoepfer.json.rpc.runtime.BaseJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.net.URI;
import java.util.List;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

class MethodTest {

    @Test
    void should_print_external_docs() throws Exception {
        assertThat(
            method()
                .withExternalDocs(new ExternalDocs(URI.create("http://localhost/test").toURL()))
                .printOn(new HashMapMedia()),
            hasEntry(is("externalDocs"), (Matcher) hasEntry("url", "http://localhost/test"))
        );
    }

    @Test
    void should_print_tags() throws Exception {
        assertThat(
            method().withTags(new Tag[] { new Tag("pets") }).printOn(new HashMapMedia()),
            hasEntry(is("tags"), (Matcher) contains(hasEntry("name", "pets")))
        );
    }

    @Test
    void should_print_reference_tags() throws Exception {
        assertThat(
            method().withTags(new Reference[] { new Reference("#/test") }).printOn(new HashMapMedia()),
            hasEntry(is("tags"), (Matcher) contains(hasEntry("$ref", "#/test")))
        );
    }

    @Test
    void make_pitest_happy() throws Exception {
        //or unsure ... how to find a good place to verify this functionality
        assertThat(method().hasName("list_pets"), is(true));
        assertThat(method().hasName("get_pet_by_id"), is(false));
    }

    private Method method() {
        return new Method(
            new BaseJsonRpcMethod("list_pets", List.of("limit")) {
                @Override
                protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                    return Json.createArrayBuilder().add("bunnies").add("cats").build();
                }
            }
        );
    }
}
