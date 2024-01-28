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
package io.github.sebastiantoepfer.json.rpc.extension.micrometer;

import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcExecutionExecption;
import io.github.sebastiantoepfer.json.rpc.runtime.JsonRpcMethod;
import io.micrometer.core.instrument.Timer;
import jakarta.json.JsonValue;
import java.util.Objects;

final class TimedJsonRpcMethod implements JsonRpcMethod {

    private final Timer timer;
    private final JsonRpcMethod methodToObserve;

    public TimedJsonRpcMethod(final Timer timer, final JsonRpcMethod methodToObserve) {
        this.timer = Objects.requireNonNull(timer);
        this.methodToObserve = Objects.requireNonNull(methodToObserve);
    }

    @Override
    public boolean hasName(final String name) {
        return methodToObserve.hasName(name);
    }

    @Override
    public String name() {
        return methodToObserve.name();
    }

    @Override
    public JsonValue execute(final JsonValue params) throws JsonRpcExecutionExecption {
        try {
            return timer.recordCallable(() -> methodToObserve.execute(params));
        } catch (JsonRpcExecutionExecption e) {
            throw e;
        } catch (Exception ex) {
            throw new CanNotTakeMetric(ex);
        }
    }
}
