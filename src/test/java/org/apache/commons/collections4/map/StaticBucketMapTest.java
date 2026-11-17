/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests.
 * {@link StaticBucketMap}.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class StaticBucketMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    /**
     * Starts {@code access} on another thread and reports whether it blocks on a monitor instead of running to completion.
     */
    private static boolean blocksWhileRunning(final Runnable access) {
        final Thread thread = new Thread(access);
        thread.setDaemon(true);
        thread.start();
        Thread.State state = thread.getState();
        while (state != Thread.State.BLOCKED && state != Thread.State.TERMINATED) {
            Thread.yield();
            state = thread.getState();
        }
        return state == Thread.State.BLOCKED;
    }

    private static Arguments compoundOperation(final String name, final BiConsumer<Map<String, String>, Runnable> operation) {
        return Arguments.of(name, operation);
    }

    static Stream<Arguments> compoundOperations() {
        return Stream.of(
                compoundOperation("compute", (map, probe) -> map.compute("present", (k, v) -> {
                    probe.run();
                    return v;
                })),
                compoundOperation("computeIfAbsent", (map, probe) -> map.computeIfAbsent("absent", k -> {
                    probe.run();
                    return k;
                })),
                compoundOperation("computeIfPresent", (map, probe) -> map.computeIfPresent("present", (k, v) -> {
                    probe.run();
                    return v;
                })),
                compoundOperation("merge", (map, probe) -> map.merge("present", "value", (a, b) -> {
                    probe.run();
                    return a;
                })));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFailFastExpected() {
        return false;
    }

    @Override
    public StaticBucketMap<K, V> makeObject() {
        return new StaticBucketMap<>(30);
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_containsKey_nullMatchesIncorrectly() {
        final StaticBucketMap<K, V> map = new StaticBucketMap<>(17);
        map.put(null, (V) "A");
        assertTrue(map.containsKey(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            final String str = String.valueOf((char) i);
            assertFalse(map.containsKey(str), "String: " + str);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void test_containsValue_nullMatchesIncorrectly() {
        final StaticBucketMap<K, V> map = new StaticBucketMap<>(17);
        map.put((K) "A", null);
        assertTrue(map.containsValue(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            final String str = String.valueOf((char) i);
            assertFalse(map.containsValue(str), "String: " + str);
        }
    }

    /**
     * A compound operation must hold the bucket lock while it runs its function, otherwise a concurrent update to the same key is lost. Since
     * {@code size()} visits every bucket, another thread calling it in the meantime is expected to block on the monitor rather than complete.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("compoundOperations")
    void testCompoundOperationHoldsBucketLock(final String name, final BiConsumer<Map<String, String>, Runnable> operation) {
        final StaticBucketMap<String, String> map = new StaticBucketMap<>();
        map.put("present", "value");
        final AtomicBoolean blocked = new AtomicBoolean();
        operation.accept(map, () -> blocked.set(blocksWhileRunning(map::size)));
        assertTrue(blocked.get(), () -> name + " ran its function without holding the bucket lock");
    }

    // Bugzilla 37567
    @Test
    @SuppressWarnings("unchecked")
    void test_get_nullMatchesIncorrectly() {
        final StaticBucketMap<K, V> map = new StaticBucketMap<>(17);
        map.put(null, (V) "A");
        assertEquals("A", map.get(null));
        // loop so we find a string that is in the same bucket as the null
        for (int i = 'A'; i <= 'Z'; i++) {
            final String str = String.valueOf((char) i);
            assertNull(map.get(str), "String: " + str);
        }
    }

}
