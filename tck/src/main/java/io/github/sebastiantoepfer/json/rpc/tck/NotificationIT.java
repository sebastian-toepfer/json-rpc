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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class NotificationIT {

    /**
     * --> {"jsonrpc": "2.0", "method": "notify_hello", "params": [7]},
     */
    @Test
    void notification() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"jsonrpc\": \"2.0\", \"method\": \"notify_hello\", \"params\": [7]}")
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body(Matchers.hasToString(""));
    }

    /**
     * --> [</br>
     * {"jsonrpc": "2.0", "method": "notify_sum", "params": [1,2,4]},</br>
     * {"jsonrpc": "2.0", "method": "notify_hello", "params": [7]}</br>
     * ]</br>
     * <-- //Nothing is returned for all notification batches
     */
    @Test
    void allNotification() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(
                "[\n" +
                "{\"jsonrpc\": \"2.0\", \"method\": \"notify_sum\", \"params\": [1,2,4]},\n" +
                "{\"jsonrpc\": \"2.0\", \"method\": \"notify_hello\", \"params\": [7]}\n" +
                "]"
            )
            .when()
            .post("/rpc")
            .then()
            .statusCode(HTTP_OK)
            .body(Matchers.hasToString(""));
    }
}
