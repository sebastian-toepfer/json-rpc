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
package io.github.sebastiantoepfer.json.rpc.boundary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcExecutionContext;
import io.github.sebastiantoepfer.json.rpc.runtime.DefaultJsonRpcRuntime;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcRuntime;
import jakarta.json.Json;
import jakarta.json.JsonStructure;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

class RpcResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(RpcResource.class)
            .register(
                new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(new DefaultJsonRpcRuntime(new DefaultJsonRpcExecutionContext())).to(JsonRpcRuntime.class);
                    }
                }
            );
    }

    @Test
    void should_return_result_of_json_rpc_runtime() {
        assertThat(
            target("/rpc")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .buildPost(
                    Entity.json(
                        Json
                            .createObjectBuilder()
                            .add("jsonrpc", "2.0")
                            .add("method", "sum")
                            .add("parmas", Json.createArrayBuilder().add(1).add(2).add(4))
                            .add("id", "89")
                            .build()
                    )
                )
                .invoke()
                .readEntity(JsonStructure.class),
            is(
                Json
                    .createObjectBuilder()
                    .add("jsonrpc", "2.0")
                    .add("error", Json.createObjectBuilder().add("code", -32601).add("message", "Method not found"))
                    .add("id", "89")
                    .build()
            )
        );
    }
}
