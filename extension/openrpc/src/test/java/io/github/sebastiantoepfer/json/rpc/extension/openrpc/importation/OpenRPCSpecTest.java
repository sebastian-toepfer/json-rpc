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

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.json.Json;
import org.junit.jupiter.api.Test;

class OpenRPCSpecTest {

    @Test
    void should_create_context_from_inputstream() {
        assertThat(
            OpenRPCSpec.load(OpenRPCSpecTest.class.getClassLoader().getResourceAsStream("petstore-openrpc.json"))
                .map(in -> Json.createValue("list_pets"))
                .toName("list_pets")
                .map(in -> Json.createValue("create_pet"))
                .toName("create_pet")
                /* ref not supported yet!
                    .map(in -> Json.createValue("get_pet"))
                    .toName("get_pet")
                */
                .asContext()
                .findMethodWithName("create_pet"),
            isPresent()
        );
    }

    @Test
    void should_throw_illegal_argument_exception_if_an_invalid_method_name_should_be_used() {
        final OpenRPCSpec.MethodMapping map = OpenRPCSpec.load(
            OpenRPCSpecTest.class.getClassLoader().getResourceAsStream("petstore-openrpc.json")
        ).map(in -> Json.createValue("list_pets"));

        assertThat(
            assertThrows(IllegalArgumentException.class, () -> map.toName("subtract")).getMessage(),
            is("method \"subtract\"not found in spec!")
        );
    }
}
