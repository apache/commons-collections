/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.ResettableIterator;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests.
 */
public class LRUMapTest<K, V> extends AbstractOrderedMapTest<K, V> {

    public LRUMapTest() {
        super(LRUMapTest.class.getSimpleName());
    }

    public static junit.framework.Test suite() {
        return BulkTest.makeSuite(LRUMapTest.class);
    }

    @Override
    public LRUMap<K, V> makeObject() {
        return new LRUMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LRUMap<K, V> makeFullMap() {
        return (LRUMap<K, V>) super.makeFullMap();
    }

    @Override
    public boolean isGetStructuralModify() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LRUMap<K, V> getMap() {
        return (LRUMap<K, V>) super.getMap();
    }

    @Test
    public void testCtors() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new LRUMap<K, V>(0),
                        "maxSize must be positive"),
                () -> assertThrows(IllegalArgumentException.class, () -> new LRUMap<K, V>(-1, 12, 0.75f, false),
                        "maxSize must be positive"),
                () -> assertThrows(IllegalArgumentException.class, () -> new LRUMap<K, V>(10, -1),
                        "initialSize must not be negative"),
                () -> assertThrows(IllegalArgumentException.class, () -> new LRUMap<K, V>(10, 12),
                        "initialSize must not be larger than maxSize"),
                () -> assertThrows(IllegalArgumentException.class, () -> new LRUMap<K, V>(10, -1, 0.75f, false),
                        "initialSize must not be negative"),
                () -> assertThrows(IllegalArgumentException.class, () -> new LRUMap<K, V>(10, 12, 0.75f, false),
                        "initialSize must not be larger than maxSize")
        );
    }

    @Test
    public void testLRU() {
        if (!isPutAddSupported() || !isPutChangeSupported()) {
            return;
        }
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        Iterator<K> kit;
        Iterator<V> vit;

        final LRUMap<K, V> map = new LRUMap<>(2);
        assertEquals(0, map.size());
        assertFalse(map.isFull());
        assertEquals(2, map.maxSize());

        map.put(keys[0], values[0]);
        assertEquals(1, map.size());
        assertFalse(map.isFull());
        assertEquals(2, map.maxSize());

        map.put(keys[1], values[1]);
        assertEquals(2, map.size());
        assertTrue(map.isFull());
        assertEquals(2, map.maxSize());
        kit = map.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = map.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[1], vit.next());

        map.put(keys[2], values[2]);
        assertEquals(2, map.size());
        assertTrue(map.isFull());
        assertEquals(2, map.maxSize());
        kit = map.keySet().iterator();
        assertSame(keys[1], kit.next());
        assertSame(keys[2], kit.next());
        vit = map.values().iterator();
        assertSame(values[1], vit.next());
        assertSame(values[2], vit.next());

        map.put(keys[2], values[0]);
        assertEquals(2, map.size());
        assertTrue(map.isFull());
        assertEquals(2, map.maxSize());
        kit = map.keySet().iterator();
        assertSame(keys[1], kit.next());
        assertSame(keys[2], kit.next());
        vit = map.values().iterator();
        assertSame(values[1], vit.next());
        assertSame(values[0], vit.next());

        map.put(keys[1], values[3]);
        assertEquals(2, map.size());
        assertTrue(map.isFull());
        assertEquals(2, map.maxSize());
        kit = map.keySet().iterator();
        assertSame(keys[2], kit.next());
        assertSame(keys[1], kit.next());
        vit = map.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[3], vit.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReset() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        ((ResettableIterator<K>) ordered.mapIterator()).reset();

        resetFull();
        ordered = getMap();
        final List<K> list = new ArrayList<>(ordered.keySet());
        final ResettableIterator<K> it = (ResettableIterator<K>) ordered.mapIterator();
        assertSame(list.get(0), it.next());
        assertSame(list.get(1), it.next());
        it.reset();
        assertSame(list.get(0), it.next());
    }

    @Test
    public void testAccessOrder() {
        if (!isPutAddSupported() || !isPutChangeSupported()) {
            return;
        }
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        Iterator<K> kit;
        Iterator<V> vit;

        resetEmpty();
        map.put(keys[0], values[0]);
        map.put(keys[1], values[1]);
        kit = map.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = map.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[1], vit.next());

        // no change to order
        map.put(keys[1], values[1]);
        kit = map.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = map.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[1], vit.next());

        // no change to order
        map.put(keys[1], values[2]);
        kit = map.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = map.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[2], vit.next());

        // change to order
        map.put(keys[0], values[3]);
        kit = map.keySet().iterator();
        assertSame(keys[1], kit.next());
        assertSame(keys[0], kit.next());
        vit = map.values().iterator();
        assertSame(values[2], vit.next());
        assertSame(values[3], vit.next());

        // change to order
        map.get(keys[1]);
        kit = map.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = map.values().iterator();
        assertSame(values[3], vit.next());
        assertSame(values[2], vit.next());

        // change to order
        map.get(keys[0]);
        kit = map.keySet().iterator();
        assertSame(keys[1], kit.next());
        assertSame(keys[0], kit.next());
        vit = map.values().iterator();
        assertSame(values[2], vit.next());
        assertSame(values[3], vit.next());

        // no change to order
        map.get(keys[0]);
        kit = map.keySet().iterator();
        assertSame(keys[1], kit.next());
        assertSame(keys[0], kit.next());
        vit = map.values().iterator();
        assertSame(values[2], vit.next());
        assertSame(values[3], vit.next());
    }

    @Test
    public void testAccessOrder2() {
        if (!isPutAddSupported() || !isPutChangeSupported()) {
            return;
        }
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        Iterator<K> kit;
        Iterator<V> vit;

        resetEmpty();
        final LRUMap<K, V> lruMap = (LRUMap<K, V>) map;

        lruMap.put(keys[0], values[0]);
        lruMap.put(keys[1], values[1]);
        kit = lruMap.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = lruMap.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[1], vit.next());

        // no change to order
        lruMap.put(keys[1], values[1]);
        kit = lruMap.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = lruMap.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[1], vit.next());

        // no change to order
        lruMap.get(keys[1], false);
        kit = lruMap.keySet().iterator();
        assertSame(keys[0], kit.next());
        assertSame(keys[1], kit.next());
        vit = lruMap.values().iterator();
        assertSame(values[0], vit.next());
        assertSame(values[1], vit.next());

        // change to order
        lruMap.get(keys[0], true);
        kit = lruMap.keySet().iterator();
        assertSame(keys[1], kit.next());
        assertSame(keys[0], kit.next());
        vit = lruMap.values().iterator();
        assertSame(values[1], vit.next());
        assertSame(values[0], vit.next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClone() {
        final LRUMap<K, V> map = new LRUMap<>(10);
        map.put((K) "1", (V) "1");
        final Map<K, V> cloned = map.clone();
        assertEquals(map.size(), cloned.size());
        assertSame(map.get("1"), cloned.get("1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveLRU() {
        final MockLRUMapSubclass<K, String> map = new MockLRUMapSubclass<>(2);
        assertNull(map.entry);
        map.put((K) "A", "a");
        assertNull(map.entry);
        map.put((K) "B", "b");
        assertNull(map.entry);
        map.put((K) "C", "c");  // removes oldest, which is A=a
        assertNotNull(map.entry);
        assertEquals("A", map.key);
        assertEquals("a", map.value);
        assertEquals("C", map.entry.getKey());  // entry is reused
        assertEquals("c", map.entry.getValue());  // entry is reused
        assertFalse(map.containsKey("A"));
        assertTrue(map.containsKey("B"));
        assertTrue(map.containsKey("C"));
    }

    static class MockLRUMapSubclass<K, V> extends LRUMap<K, V> {

        /**
         * Generated serial version ID.
         */
        private static final long serialVersionUID = -2126883654452042477L;
        LinkEntry<K, V> entry;
        K key;
        V value;

        MockLRUMapSubclass(final int size) {
            super(size);
        }

        @Override
        protected boolean removeLRU(final LinkEntry<K, V> entry) {
            this.entry = entry;
            this.key = entry.getKey();
            this.value = entry.getValue();
            return true;
        }

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveLRUBlocksRemove() {
        final MockLRUMapSubclassBlocksRemove<K, V> map = new MockLRUMapSubclassBlocksRemove<>(2, false);
        assertEquals(0, map.size());
        map.put((K) "A", (V) "a");
        assertEquals(1, map.size());
        map.put((K) "B", (V) "b");
        assertEquals(2, map.size());
        map.put((K) "C", (V) "c");  // should remove oldest, which is A=a, but this is blocked
        assertEquals(3, map.size());
        assertEquals(2, map.maxSize());
        assertTrue(map.containsKey("A"));
        assertTrue(map.containsKey("B"));
        assertTrue(map.containsKey("C"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveLRUBlocksRemoveScan() {
        final MockLRUMapSubclassBlocksRemove<K, V> map = new MockLRUMapSubclassBlocksRemove<>(2, true);
        assertEquals(0, map.size());
        map.put((K) "A", (V) "a");
        assertEquals(1, map.size());
        map.put((K) "B", (V) "b");
        assertEquals(2, map.size());
        map.put((K) "C", (V) "c");  // should remove oldest, which is A=a, but this is blocked
        assertEquals(3, map.size());
        assertEquals(2, map.maxSize());
        assertTrue(map.containsKey("A"));
        assertTrue(map.containsKey("B"));
        assertTrue(map.containsKey("C"));
    }

    static class MockLRUMapSubclassBlocksRemove<K, V> extends LRUMap<K, V> {

        /**
         * Generated serial version ID.
         */
        private static final long serialVersionUID = 6278917461128992945L;

        MockLRUMapSubclassBlocksRemove(final int size, final boolean scanUntilRemove) {
            super(size, scanUntilRemove);
        }

        @Override
        protected boolean removeLRU(final LinkEntry<K, V> entry) {
            return false;
        }

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemoveLRUFirstBlocksRemove() {
        final MockLRUMapSubclassFirstBlocksRemove<K, V> map = new MockLRUMapSubclassFirstBlocksRemove<>(2);
        assertEquals(0, map.size());
        map.put((K) "A", (V) "a");
        assertEquals(1, map.size());
        map.put((K) "B", (V) "b");
        assertEquals(2, map.size());
        map.put((K) "C", (V) "c");  // should remove oldest, which is A=a  but this is blocked - so advance to B=b
        assertEquals(2, map.size());
        assertEquals(2, map.maxSize());
        assertTrue(map.containsKey("A"));
        assertFalse(map.containsKey("B"));
        assertTrue(map.containsKey("C"));
    }

    static class MockLRUMapSubclassFirstBlocksRemove<K, V> extends LRUMap<K, V> {

        /**
         * Generated serial version ID.
         */
        private static final long serialVersionUID = -6939790801702973428L;

        MockLRUMapSubclassFirstBlocksRemove(final int size) {
            super(size, true);
        }

        @Override
        protected boolean removeLRU(final LinkEntry<K, V> entry) {
            if ("a".equals(entry.getValue())) {
                return false;
            }
            return true;
        }

    }

    static class SingleHashCode {

        private final String code;

        SingleHashCode(final String code) {
            this.code = code;
        }

        @Override
        public int hashCode() {
            // always return the same hashcode
            // that way, it will end up in the same bucket
            return 12;
        }

        @Override
        public String toString() {
            return "SingleHashCode:" + code;
        }

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInternalState_Buckets() {
        if (!isPutAddSupported() || !isPutChangeSupported()) {
            return;
        }
        final SingleHashCode one = new SingleHashCode("1");
        final SingleHashCode two = new SingleHashCode("2");
        final SingleHashCode three = new SingleHashCode("3");
        final SingleHashCode four = new SingleHashCode("4");
        final SingleHashCode five = new SingleHashCode("5");
        final SingleHashCode six = new SingleHashCode("6");

        final LRUMap<K, V> map = new LRUMap<>(3, 1.0f);
        final int hashIndex = map.hashIndex(map.hash(one), 4);
        map.put((K) one, (V) "A");
        map.put((K) two, (V) "B");
        map.put((K) three, (V) "C");

        assertEquals(4, map.data.length);
        assertEquals(3, map.size);
        assertNull(map.header.next);
        assertEquals(one, map.header.after.key);  // LRU
        assertEquals(two, map.header.after.after.key);
        assertEquals(three, map.header.after.after.after.key);  // MRU
        assertEquals(three, map.data[hashIndex].key);
        assertEquals(two, map.data[hashIndex].next.key);
        assertEquals(one, map.data[hashIndex].next.next.key);

        map.put((K) four, (V) "D");  // reuses last in next list

        assertEquals(4, map.data.length);
        assertEquals(3, map.size);
        assertNull(map.header.next);
        assertEquals(two, map.header.after.key);  // LRU
        assertEquals(three, map.header.after.after.key);
        assertEquals(four, map.header.after.after.after.key);  // MRU
        assertEquals(four, map.data[hashIndex].key);
        assertEquals(three, map.data[hashIndex].next.key);
        assertEquals(two, map.data[hashIndex].next.next.key);

        map.get(three);

        assertEquals(4, map.data.length);
        assertEquals(3, map.size);
        assertNull(map.header.next);
        assertEquals(two, map.header.after.key);  // LRU
        assertEquals(four, map.header.after.after.key);
        assertEquals(three, map.header.after.after.after.key);  // MRU
        assertEquals(four, map.data[hashIndex].key);
        assertEquals(three, map.data[hashIndex].next.key);
        assertEquals(two, map.data[hashIndex].next.next.key);

        map.put((K) five, (V) "E");  // reuses last in next list

        assertEquals(4, map.data.length);
        assertEquals(3, map.size);
        assertNull(map.header.next);
        assertEquals(four, map.header.after.key);  // LRU
        assertEquals(three, map.header.after.after.key);
        assertEquals(five, map.header.after.after.after.key);  // MRU
        assertEquals(five, map.data[hashIndex].key);
        assertEquals(four, map.data[hashIndex].next.key);
        assertEquals(three, map.data[hashIndex].next.next.key);

        map.get(three);
        map.get(five);

        assertEquals(4, map.data.length);
        assertEquals(3, map.size);
        assertNull(map.header.next);
        assertEquals(four, map.header.after.key);  // LRU
        assertEquals(three, map.header.after.after.key);
        assertEquals(five, map.header.after.after.after.key);  // MRU
        assertEquals(five, map.data[hashIndex].key);
        assertEquals(four, map.data[hashIndex].next.key);
        assertEquals(three, map.data[hashIndex].next.next.key);

        map.put((K) six, (V) "F");  // reuses middle in next list

        assertEquals(4, map.data.length);
        assertEquals(3, map.size);
        assertNull(map.header.next);
        assertEquals(three, map.header.after.key);  // LRU
        assertEquals(five, map.header.after.after.key);
        assertEquals(six, map.header.after.after.after.key);  // MRU
        assertEquals(six, map.data[hashIndex].key);
        assertEquals(five, map.data[hashIndex].next.key);
        assertEquals(three, map.data[hashIndex].next.next.key);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInternalState_getEntry_int() {
        if (!isPutAddSupported() || !isPutChangeSupported()) {
            return;
        }
        final SingleHashCode one = new SingleHashCode("1");
        final SingleHashCode two = new SingleHashCode("2");
        final SingleHashCode three = new SingleHashCode("3");

        final LRUMap<K, V> map = new LRUMap<>(3, 1.0f);
        map.put((K) one, (V) "A");
        map.put((K) two, (V) "B");
        map.put((K) three, (V) "C");

        assertEquals(one, map.getEntry(0).key);
        assertEquals(two, map.getEntry(1).key);
        assertEquals(three, map.getEntry(2).key);
        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> map.getEntry(-1)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> map.getEntry(3))
        );
    }

    @Test
    public void testSynchronizedRemoveFromMapIterator() throws InterruptedException {

        final LRUMap<Object, Thread> map = new LRUMap<>(10000);

        final Map<Throwable, String> exceptions = new HashMap<>();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[50];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(tg, "JUnit Thread " + i) {

                @Override
                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        final Thread thread = Thread.currentThread();
                        while (i < 1000  && !interrupted()) {
                            synchronized (map) {
                                map.put(thread.getName() + "[" + ++i + "]", thread);
                            }
                        }
                        synchronized (map) {
                            for (final MapIterator<Object, Thread> iter = map.mapIterator(); iter.hasNext();) {
                                iter.next();
                                if (iter.getValue() == this) {
                                    iter.remove();
                                }
                            }
                        }
                    } catch (final InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    }
                    if (i > 0) {
                        synchronized (counter) {
                            counter[0]++;
                        }
                    }
                }

            };
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.start();
                thread.wait();
            }
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.notifyAll();
            }
        }

        Thread.sleep(1000);

        for (final Thread thread : threads) {
            thread.interrupt();
        }
        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.join();
            }
        }

        assertEquals("Exceptions have been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have put at least 1 element into the map, but only "
                + counter[0] + " did succeed", counter[0] >= threads.length);
    }

    @Test
    public void testSynchronizedRemoveFromEntrySet() throws InterruptedException {

        final Map<Object, Thread> map = new LRUMap<>(10000);

        final Map<Throwable, String> exceptions = new HashMap<>();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[50];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(tg, "JUnit Thread " + i) {

                @Override
                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        final Thread thread = Thread.currentThread();
                        while (i < 1000  && !interrupted()) {
                            synchronized (map) {
                                map.put(thread.getName() + "[" + ++i + "]", thread);
                            }
                        }
                        synchronized (map) {
                            map.entrySet().removeIf(entry -> entry.getValue() == this);
                        }
                    } catch (final InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    }
                    if (i > 0) {
                        synchronized (counter) {
                            counter[0]++;
                        }
                    }
                }

            };
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.start();
                thread.wait();
            }
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.notifyAll();
            }
        }

        Thread.sleep(1000);

        for (final Thread thread : threads) {
            thread.interrupt();
        }
        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.join();
            }
        }

        assertEquals("Exceptions have been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have put at least 1 element into the map, but only "
                + counter[0] + " did succeed", counter[0] >= threads.length);
    }

    @Test
    public void testSynchronizedRemoveFromKeySet() throws InterruptedException {

        final Map<Object, Thread> map = new LRUMap<>(10000);

        final Map<Throwable, String> exceptions = new HashMap<>();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[50];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(tg, "JUnit Thread " + i) {

                @Override
                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        final Thread thread = Thread.currentThread();
                        while (i < 1000  && !interrupted()) {
                            synchronized (map) {
                                map.put(thread.getName() + "[" + ++i + "]", thread);
                            }
                        }
                        synchronized (map) {
                            for (final Iterator<Object> iter = map.keySet().iterator(); iter.hasNext();) {
                                final String name = (String) iter.next();
                                if (name.substring(0, name.indexOf('[')).equals(getName())) {
                                    iter.remove();
                                }
                            }
                        }
                    } catch (final InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    }
                    if (i > 0) {
                        synchronized (counter) {
                            counter[0]++;
                        }
                    }
                }

            };
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.start();
                thread.wait();
            }
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.notifyAll();
            }
        }

        Thread.sleep(1000);

        for (final Thread thread : threads) {
            thread.interrupt();
        }
        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.join();
            }
        }

        assertEquals("Exceptions have been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have put at least 1 element into the map, but only "
                + counter[0] + " did succeed", counter[0] >= threads.length);
    }

    @Test
    public void testSynchronizedRemoveFromValues() throws InterruptedException {

        final Map<Object, Thread> map = new LRUMap<>(10000);

        final Map<Throwable, String> exceptions = new HashMap<>();
        final ThreadGroup tg = new ThreadGroup(getName()) {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                exceptions.put(e, t.getName());
                super.uncaughtException(t, e);
            }
        };

        final int[] counter = new int[1];
        counter[0] = 0;
        final Thread[] threads = new Thread[50];
        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(tg, "JUnit Thread " + i) {

                @Override
                public void run() {
                    int i = 0;
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        final Thread thread = Thread.currentThread();
                        while (i < 1000  && !interrupted()) {
                            synchronized (map) {
                                map.put(thread.getName() + "[" + ++i + "]", thread);
                            }
                        }
                        synchronized (map) {
                            map.values().removeIf(thread1 -> thread1 == this);
                        }
                    } catch (final InterruptedException e) {
                        fail("Unexpected InterruptedException");
                    }
                    if (i > 0) {
                        synchronized (counter) {
                            counter[0]++;
                        }
                    }
                }

            };
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.start();
                thread.wait();
            }
        }

        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.notifyAll();
            }
        }

        Thread.sleep(1000);

        for (final Thread thread : threads) {
            thread.interrupt();
        }
        for (final Thread thread : threads) {
            synchronized (thread) {
                thread.join();
            }
        }

        assertEquals("Exceptions have been thrown: " + exceptions, 0, exceptions.size());
        assertTrue("Each thread should have put at least 1 element into the map, but only "
                + counter[0] + " did succeed", counter[0] >= threads.length);
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/LRUMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) map, "src/test/resources/data/test/LRUMap.fullCollection.version4.obj");
//    }

}
