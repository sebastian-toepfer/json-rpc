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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ContentDescriptorOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.JsonSchemaOrReference;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.MethodObject;
import io.github.sebastiantoepfer.json.rpc.extension.openrpc.spec.ReferenceObject;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethodFunction;
import io.github.sebastiantoepfer.jsonschema.JsonSchemas;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class DescribableJsonRpcMethodTest {

    @Test
    void should_delegate_has_names() {
        final JsonRpcMethod jsonRpcMethod = new DescribableJsonRpcMethod(
            new MethodObject("list_pets", List.of()),
            params -> {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        );
        //pitest :(
        assertThat(jsonRpcMethod.hasName("get_pet"), is(false));
        assertThat(jsonRpcMethod.hasName("list_pets"), is(true));
    }

    @Test
    void should_call_method_with_parameters() throws Exception {
        final JsonArray parameters = Json.createArrayBuilder().add(2).build();
        final JsonRpcMethodFunction function = params -> params;
        assertThat(
            new DescribableJsonRpcMethod(
                new MethodObject(
                    "list_pets",
                    List.of(
                        new ContentDescriptorOrReference.Object(
                            new ContentDescriptorObject(
                                "limit",
                                new JsonSchemaOrReference.Object(JsonSchemas.load(JsonValue.EMPTY_JSON_OBJECT))
                            )
                        )
                    )
                ),
                function
            )
                .execute(parameters),
            is(new DefaultJsonRpcMethod("list_pets", List.of("limit"), function).execute(parameters))
        );
    }

    @Test
    void should_not_creatable_with_reference_parameters() {
        final MethodObject desciption = new MethodObject(
            "list_pets",
            List.of(new ContentDescriptorOrReference.Reference(new ReferenceObject("test")))
        );

        assertThrows(IllegalArgumentException.class, () -> new DescribableJsonRpcMethod(desciption, a -> a));
    }

    @Test
    void should_not_callable_by_position_when_using_byName_paramstucture() {
        final DescribableJsonRpcMethod m = new DescribableJsonRpcMethod(
            new MethodObject(
                "list_pets",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject(
                            "limit",
                            new JsonSchemaOrReference.Object(JsonSchemas.load(JsonValue.EMPTY_JSON_OBJECT))
                        )
                    )
                )
            )
                .withParamStructure(MethodObject.MethodObjectParamStructure.byname),
            a -> a
        );
        final JsonValue parameters = Json.createArrayBuilder().add("a").build();

        assertThrows(JsonRpcExecutionExecption.class, () -> m.execute(parameters));
    }

    @Test
    void should_not_callable_by_name_when_using_byposition_paramstucture() {
        final DescribableJsonRpcMethod m = new DescribableJsonRpcMethod(
            new MethodObject(
                "list_pets",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject(
                            "limit",
                            new JsonSchemaOrReference.Object(JsonSchemas.load(JsonValue.EMPTY_JSON_OBJECT))
                        )
                    )
                )
            )
                .withParamStructure(MethodObject.MethodObjectParamStructure.byposition),
            a -> a
        );
        final JsonValue parameters = Json.createObjectBuilder().add("list_pets", 12).build();

        assertThrows(JsonRpcExecutionExecption.class, () -> m.execute(parameters));
    }

    @Test
    void should_not_callable_without_required_parameter() {
        final DescribableJsonRpcMethod m = new DescribableJsonRpcMethod(
            new MethodObject(
                "list_pets",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject(
                            "limit",
                            new JsonSchemaOrReference.Object(
                                JsonSchemas.load(Json.createObjectBuilder().add("type", "integer").build())
                            )
                        )
                            .withRequired(true)
                    )
                )
            ),
            a -> a
        );

        assertThrows(JsonRpcExecutionExecption.class, () -> m.execute(null));
    }

    @Test
    void should_not_callable_with_parameter_doesnt_match_his_schema() {
        final DescribableJsonRpcMethod m = new DescribableJsonRpcMethod(
            new MethodObject(
                "list_pets",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject(
                            "limit",
                            new JsonSchemaOrReference.Object(
                                JsonSchemas.load(Json.createObjectBuilder().add("type", "integer").build())
                            )
                        )
                    )
                )
            ),
            a -> a
        );

        final JsonValue params = Json.createArrayBuilder().add(1.1).build();
        assertThrows(JsonRpcExecutionExecption.class, () -> m.execute(params));
    }

    @Test
    void should_callable_without_optional_parameters() throws Exception {
        final JsonRpcMethod method = new DescribableJsonRpcMethod(
            new MethodObject(
                "echo",
                List.of(
                    new ContentDescriptorOrReference.Object(
                        new ContentDescriptorObject(
                            "value",
                            new JsonSchemaOrReference.Object(JsonSchemas.load(JsonValue.EMPTY_JSON_OBJECT))
                        )
                    )
                )
            ),
            o -> o.get("value")
        );

        assertThat(method.execute(null), is(nullValue()));
        assertThat(method.execute(JsonValue.NULL), is(nullValue()));
    }
}
