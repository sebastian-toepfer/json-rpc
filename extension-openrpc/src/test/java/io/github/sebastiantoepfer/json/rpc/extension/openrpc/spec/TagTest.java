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
package io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import io.github.sebastiantoepfer.ddd.media.core.HashMapMedia;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ExternalDocs;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.Tag;
import java.net.URI;
import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void should_print_summary() {
        assertThat(
            new Tag("with_summary").withSummary("A short summary of the tag.").printOn(new HashMapMedia()),
            allOf(hasEntry("name", "with_summary"), hasEntry("summary", "A short summary of the tag."))
        );
    }

    @Test
    void should_print_description() {
        assertThat(
            new Tag("with_description")
                .withDescription("A verbose explanation for the tag.")
                .printOn(new HashMapMedia()),
            allOf(hasEntry("name", "with_description"), hasEntry("description", "A verbose explanation for the tag."))
        );
    }

    @Test
    void should_print_externalDocs() throws Exception {
        assertThat(
            new Tag("with_externalDocs")
                .withExternalDocs(new ExternalDocs(URI.create("http://localhost/rpc").toURL()))
                .printOn(new HashMapMedia()),
            allOf(hasEntry("name", "with_externalDocs"), hasEntry(is("externalDocs"), is(not(nullValue()))))
        );
    }
}
