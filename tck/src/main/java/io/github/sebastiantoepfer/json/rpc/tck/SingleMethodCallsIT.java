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

import org.junit.jupiter.api.Test;

class SingleMethodCallsIT {

    /**
     * --> {"jsonrpc": "2.0", "method": "subtract", "params": [42, 23], "id": 1} </br>
     * <-- {"jsonrpc": "2.0", "result": 19, "id": 1}
     */
    @Test
    void callWithPositionalParmeters() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}")
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("jsonrpc", is("2.0"))
            .body("result", is(19))
            .body("id", is(1));
    }

    /**
     * --> {"jsonrpc": "2.0", "method": "subtract", "params": [23, 42], "id": 2} </br>
     * <-- {"jsonrpc": "2.0", "result": -19, "id": 2}
     */
    @Test
    void callWithPositionalParmetersInReserveOrder() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [23, 42], \"id\": 2}")
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("jsonrpc", is("2.0"))
            .body("result", is(-19))
            .body("id", is(2));
    }

    /**
     * --> {"jsonrpc": "2.0", "method": "subtract", "params": {"subtrahend": 23, "minuend": 42}, "id": 3} </br>
     * <-- {"jsonrpc": "2.0", "result": 19, "id": 3}
     */
    @Test
    void callWithNamedParmeters() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(
                "{" +
                "\"jsonrpc\": \"2.0\"," +
                " \"method\": \"subtract\"," +
                " \"params\": {\"subtrahend\": 23, \"minuend\": 42}," +
                " \"id\": 3" +
                "}"
            )
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("jsonrpc", is("2.0"))
            .body("result", is(19))
            .body("id", is(3));
    }

    /**
     * --> {"jsonrpc": "2.0", "method": "subtract", "params": {"minuend": 42, "subtrahend": 23}, "id": 4}</br>
     * <-- {"jsonrpc": "2.0", "result": 19, "id": 4}
     */
    @Test
    void callWithNamedParmetersInReserveOrder() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(
                "{\"jsonrpc\": \"2.0\"," +
                " \"method\": \"subtract\"," +
                " \"params\": {\"minuend\": 42, \"subtrahend\": 23}," +
                " \"id\": 4}"
            )
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("jsonrpc", is("2.0"))
            .body("result", is(19))
            .body("id", is(4));
    }
}
