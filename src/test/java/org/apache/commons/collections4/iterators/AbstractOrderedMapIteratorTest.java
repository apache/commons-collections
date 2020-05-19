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
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections4.OrderedMapIterator;

/**
 * Abstract class for testing the OrderedMapIterator interface.
 * <p>
 * This class provides a framework for testing an implementation of MapIterator.
 * Concrete subclasses must provide the list iterator to be tested.
 * They must also specify certain details of how the list iterator operates by
 * overriding the supportsXxx() methods if necessary.
 *
 * @since 3.0
 */
public abstract class AbstractOrderedMapIteratorTest<K, V> extends AbstractMapIteratorTest<K, V> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractOrderedMapIteratorTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public abstract OrderedMapIterator<K, V> makeEmptyIterator();

    @Override
    public abstract OrderedMapIterator<K, V> makeObject();

    //-----------------------------------------------------------------------
    /**
     * Test that the empty list iterator contract is correct.
     */
    @Override
    public void testEmptyMapIterator() {
        if (!supportsEmptyIterator()) {
            return;
        }

        super.testEmptyMapIterator();

        final OrderedMapIterator<K, V> it = makeEmptyIterator();
        assertFalse(it.hasPrevious());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            it.previous();
        });
        if (null != exception.getMessage()) {
            assertTrue(exception.getMessage().contains("Iterator contains no elements"));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the full list iterator contract is correct.
     */
    @Override
    public void testFullMapIterator() {
        if (!supportsFullIterator()) {
            return;
        }

        super.testFullMapIterator();

        final OrderedMapIterator<K, V> it = makeObject();
        final Map<K, V> map = getMap();

        assertTrue(it.hasNext());
        assertFalse(it.hasPrevious());
        final Set<K> set = new HashSet<>();
        while (it.hasNext()) {
            // getKey
            final K key = it.next();
            assertSame("it.next() should equals getKey()", key, it.getKey());
            assertTrue(map.containsKey(key));
            assertTrue(set.add(key));

            // getValue
            final V value = it.getValue();
            if (!isGetStructuralModify()) {
                assertSame("Value must be mapped to key", map.get(key), value);
            }
            assertTrue(map.containsValue(value));

            assertTrue(it.hasPrevious());

            verify();
        }
        while (it.hasPrevious()) {
            // getKey
            final Object key = it.previous();
            assertSame("it.previous() should equals getKey()", key, it.getKey());
            assertTrue(map.containsKey(key));
            assertTrue(set.remove(key));

            // getValue
            final Object value = it.getValue();
            if (!isGetStructuralModify()) {
                assertSame("Value must be mapped to key", map.get(key), value);
            }
            assertTrue(map.containsValue(value));

            assertTrue(it.hasNext());

            verify();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Test that the iterator order matches the keySet order.
     */
    public void testMapIteratorOrder() {
        if (!supportsFullIterator()) {
            return;
        }

        final OrderedMapIterator<K, V> it = makeObject();
        final Map<K, V> map = getMap();

        assertEquals("keySet() not consistent", new ArrayList<>(map.keySet()), new ArrayList<>(map.keySet()));

        final Iterator<K> it2 = map.keySet().iterator();
        assertTrue(it.hasNext());
        assertTrue(it2.hasNext());
        final List<K> list = new ArrayList<>();
        while (it.hasNext()) {
            final K key = it.next();
            assertEquals(it2.next(), key);
            list.add(key);
        }
        assertEquals(map.size(), list.size());
        while (it.hasPrevious()) {
            final K key = it.previous();
            assertEquals(list.get(list.size() - 1), key);
            list.remove(list.size() - 1);
        }
        assertEquals(0, list.size());
    }

}
