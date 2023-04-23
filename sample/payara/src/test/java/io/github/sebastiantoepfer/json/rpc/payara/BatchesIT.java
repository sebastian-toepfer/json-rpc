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
package io.github.sebastiantoepfer.json.rpc.payara;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class BatchesIT {

    /**
     * --> [ </br>
     * {"jsonrpc": "2.0", "method": "sum", "params": [1,2,4], "id": "1"},</br>
     * {"jsonrpc": "2.0", "method": "notify_hello", "params": [7]},</br>
     * {"jsonrpc": "2.0", "method": "subtract", "params": [42,23], "id": "2"},</br>
     * {"foo": "boo"},</br>
     * {"jsonrpc": "2.0", "method": "foo.get", "params": {"name": "myself"}, "id": "5"},</br>
     * {"jsonrpc": "2.0", "method": "get_data", "id": "9"} </br>
     * ]</br>
     * <-- [</br> {"jsonrpc": "2.0", "result": 7, "id": "1"},</br>
     * {"jsonrpc": "2.0", "result": 19, "id": "2"},</br>
     * {"jsonrpc": "2.0", "error": {"code": -32600, "message": "Invalid Request"}, "id": null},</br>
     * {"jsonrpc": "2.0", "error": {"code": -32601, "message": "Method not found"}, "id": "5"},</br>
     * {"jsonrpc": "2.0", "result": ["hello", 5], "id": "9"}</br>
     * ]</br>
     */
    @Test
    void rpcCallBatch() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(
                "[ \n" +
                "{\"jsonrpc\": \"2.0\", \"method\": \"sum\", \"params\": [1,2,4], \"id\": \"1\"},\n" +
                "{\"jsonrpc\": \"2.0\", \"method\": \"notify_hello\", \"params\": [7]},\n" +
                "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42,23], \"id\": \"2\"},\n" +
                "{\"foo\": \"boo\"},\n" +
                "{\"jsonrpc\": \"2.0\", \"method\": \"foo.get\", \"params\": {\"name\": \"myself\"}, \"id\": \"5\"},\n" +
                "{\"jsonrpc\": \"2.0\", \"method\": \"get_data\", \"id\": \"9\"} \n" +
                "]"
            )
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body("[0].jsonrpc", is("2.0"))
            .body("[0].result", is(7))
            .body("[0].id", is("1"))
            .body("[1].jsonrpc", is("2.0"))
            .body("[1].result", is(19))
            .body("[1].id", is("2"))
            .body("[2].jsonrpc", is("2.0"))
            .body("[2].error.code", is(-32600))
            .body("[2].error.message", is("Invalid Request"))
            .body("[2].id", is(nullValue()))
            .body("[3].jsonrpc", is("2.0"))
            .body("[3].error.code", is(-32601))
            .body("[3].error.message", is("Method not found"))
            .body("[3].id", is("5"))
            .body("[4].jsonrpc", is("2.0"))
            .body("[4].result[0]", is("hello"))
            .body("[4].result[1]", is(7)) //mutale object, in same request :)
            .body("[4].id", is("9"));
    }
}
