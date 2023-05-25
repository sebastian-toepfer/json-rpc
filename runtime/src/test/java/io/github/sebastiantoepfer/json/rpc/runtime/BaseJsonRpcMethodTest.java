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
package io.github.sebastiantoepfer.json.rpc.runtime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.ddd.media.core.HashMapMedia;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

class BaseJsonRpcMethodTest {

    @Test
    void equalsContract() {
        EqualsVerifier.simple().forClass(BaseJsonRpcMethod.class).withIgnoredFields("parameterNames").verify();
    }

    @Test
    void should_be_callable_with_null_as_params() throws Exception {
        assertThat(
            new BaseJsonRpcMethod("test", List.of()) {
                @Override
                protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                    return Json.createValue("hello");
                }
            }
                .execute((JsonValue) null),
            is(Json.createValue("hello"))
        );
    }

    @Test
    void should_be_callable_without_params() throws Exception {
        assertThat(
            new BaseJsonRpcMethod("test", List.of()) {
                @Override
                protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                    return Json.createValue("hello");
                }
            }
                .execute(JsonValue.NULL),
            is(Json.createValue("hello"))
        );
    }

    @Test
    void should_print_name_and_parametersnames() {
        assertThat(
            new BaseJsonRpcMethod("test", List.of()) {
                @Override
                protected JsonValue execute(final JsonObject params) throws JsonRpcExecutionExecption {
                    return Json.createValue("hello");
                }
            }
                .printOn(new HashMapMedia()),
            allOf(hasEntry(is("name"), (Matcher) is("test")), hasEntry(is("params"), empty()))
        );
    }
}
