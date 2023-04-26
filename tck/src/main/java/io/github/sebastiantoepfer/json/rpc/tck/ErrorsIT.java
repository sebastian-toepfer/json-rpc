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
package io.github.sebastiantoepfer.json.rpc.tck;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class ErrorsIT {

    /**
     * --> {"jsonrpc": "2.0", "method": "foobar", "id": "1"} </br>
     * <-- {"jsonrpc": "2.0", "error": {"code": -32601, "message": "Method not found"}, "id": "1"}
     */
    @Test
    void nonExistingMethod() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"jsonrpc\": \"2.0\", \"method\": \"foobar\", \"id\": \"1\"}")
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("jsonrpc", is("2.0"))
            .body("error.code", is(-32601))
            .body("error.message", is("Method not found"))
            .body("id", is("1"));
    }

    /**
     * --> {"jsonrpc": "2.0", "method": "foobar, "params": "bar", "baz] </br>
     * <-- {"jsonrpc": "2.0", "error": {"code": -32700, "message": "Parse error"}, "id": null}
     */
    @Test
    void invalidJson() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"jsonrpc\": \"2.0\", \"method\": \"foobar, \"params\": \"bar\", \"baz]")
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("jsonrpc", is("2.0"))
            .body("error.code", is(-32700))
            .body("error.message", is("Parse error"))
            .body("id", is(nullValue()));
    }

    /**
     * --> {"jsonrpc": "2.0", "method": 1, "params": "bar"} </br>
     * <-- {"jsonrpc": "2.0", "error": {"code": -32600, "message": "Invalid Request"}, "id": null}
     */
    @Test
    void invalidRequestObject() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"jsonrpc\": \"2.0\", \"method\": 1, \"params\": \"bar\"}")
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("jsonrpc", is("2.0"))
            .body("error.code", is(-32600))
            .body("error.message", is("Invalid Request"))
            .body("id", is(nullValue()));
    }
}
