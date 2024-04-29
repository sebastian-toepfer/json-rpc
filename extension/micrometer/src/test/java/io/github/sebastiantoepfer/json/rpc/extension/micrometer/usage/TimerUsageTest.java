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
package io.github.sebastiantoepfer.json.rpc.extension.micrometer.usage;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Test to learn how a timer works and ensure that the knowledge is still valid.
 */
class TimerUsageTest {

    @Test
    void should_messure_the_calltime() throws Exception {
        final MeterRegistry registry = new SimpleMeterRegistry();

        registry.timer("test").recordCallable(() -> sleep(1));
        registry.timer("test").recordCallable(() -> sleep(2));
        registry.timer("test").recordCallable(() -> sleep(3));
        registry.timer("test").recordCallable(() -> sleep(6));
        registry.timer("test").recordCallable(() -> sleep(10));

        assertThat(registry.timer("test").count(), is(5L));
        //we do not want to check whether the calculation was performed correctly,
        //but only whether we get a more or less valid-looking result
        assertThat(
            registry.timer("test").mean(TimeUnit.MILLISECONDS),
            both(greaterThanOrEqualTo(4.0)).and(lessThan(8.0))
        );
        assertThat(
            registry.timer("test").max(TimeUnit.MILLISECONDS),
            both(greaterThanOrEqualTo(10.0)).and(lessThan(11.0))
        );
    }

    private static int sleep(final int time) {
        try {
            Thread.sleep(time);
            return time;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
