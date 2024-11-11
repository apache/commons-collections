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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.comparators.NullComparator;
import org.apache.commons.collections4.iterators.AbstractOrderedMapIteratorTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link OrderedMap}.
 *
 * @param <K> the type of the keys in the maps tested.
 * @param <V> the type of the values in the maps tested.
 */
public abstract class AbstractOrderedMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public class InnerTestOrderedMapIterator extends AbstractOrderedMapIteratorTest<K, V> {

        @Override
        public Map<K, V> getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return getConfirmed();
        }

        @Override
        public OrderedMap<K, V> getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractOrderedMapTest.this.getMap();
        }

        @Override
        public boolean isGetStructuralModify() {
            return AbstractOrderedMapTest.this.isGetStructuralModify();
        }

        @Override
        public OrderedMapIterator<K, V> makeEmptyIterator() {
            resetEmpty();
            return AbstractOrderedMapTest.this.getMap().mapIterator();
        }

        @Override
        public OrderedMapIterator<K, V> makeObject() {
            resetFull();
            return AbstractOrderedMapTest.this.getMap().mapIterator();
        }

        @Override
        public boolean supportsRemove() {
            return isRemoveSupported();
        }

        @Override
        public boolean supportsSetValue() {
            return isSetValueSupported();
        }

        @Override
        public void verify() {
            super.verify();
            AbstractOrderedMapTest.this.verify();
        }
    }

    public BulkTest bulkTestOrderedMapIterator() {
        return new InnerTestOrderedMapIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedMap<K, V> getMap() {
        return (OrderedMap<K, V>) super.getMap();
    }

    /**
     * The only confirmed collection we have that is ordered is the sorted one. Thus, sort the keys.
     */
    @Override
    @SuppressWarnings("unchecked")
    public K[] getSampleKeys() {
        final List<K> list = new ArrayList<>(Arrays.asList(super.getSampleKeys()));
        list.sort(new NullComparator<>());
        return (K[]) list.toArray();
    }

    /**
     * OrderedMap uses TreeMap as its known comparison.
     *
     * @return a map that is known to be valid
     */
    @Override
    public Map<K, V> makeConfirmedMap() {
        return new TreeMap<>(new NullComparator<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedMap<K, V> makeFullMap() {
        return (OrderedMap<K, V>) super.makeFullMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract OrderedMap<K, V> makeObject();

    @Test
    public void testFirstKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        final OrderedMap<K, V> finalOrdered = ordered;
        assertThrows(NoSuchElementException.class, () -> finalOrdered.firstKey());

        resetFull();
        ordered = getMap();
        final K confirmedFirst = confirmed.keySet().iterator().next();
        assertEquals(confirmedFirst, ordered.firstKey());
    }

    @Test
    public void testLastKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        final OrderedMap<K, V> finalOrdered = ordered;
        assertThrows(NoSuchElementException.class, () -> finalOrdered.lastKey());

        resetFull();
        ordered = getMap();
        K confirmedLast = null;
        for (final Iterator<K> it = confirmed.keySet().iterator(); it.hasNext();) {
            confirmedLast = it.next();
        }
        assertEquals(confirmedLast, ordered.lastKey());
    }

    @Test
    public void testNextKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        assertNull(ordered.nextKey(getOtherKeys()[0]));
        if (!isAllowNullKey()) {
            try {
                assertNull(ordered.nextKey(null)); // this is allowed too
            } catch (final NullPointerException ex) {
            }
        } else {
            assertNull(ordered.nextKey(null));
        }

        resetFull();
        ordered = getMap();
        final Iterator<K> it = confirmed.keySet().iterator();
        K confirmedLast = it.next();
        while (it.hasNext()) {
            final K confirmedObject = it.next();
            assertEquals(confirmedObject, ordered.nextKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertNull(ordered.nextKey(confirmedLast));

        if (!isAllowNullKey()) {
            final OrderedMap<K, V> finalOrdered = ordered;
            assertThrows(NullPointerException.class, () -> finalOrdered.nextKey(null));
        } else {
            assertNull(ordered.nextKey(null));
        }
    }

    @Test
    public void testPreviousKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        assertNull(ordered.previousKey(getOtherKeys()[0]));
        if (!isAllowNullKey()) {
            try {
                assertNull(ordered.previousKey(null)); // this is allowed too
            } catch (final NullPointerException ex) {
            }
        } else {
            assertNull(ordered.previousKey(null));
        }

        resetFull();
        ordered = getMap();
        final List<K> list = new ArrayList<>(confirmed.keySet());
        Collections.reverse(list);
        final Iterator<K> it = list.iterator();
        K confirmedLast = it.next();
        while (it.hasNext()) {
            final K confirmedObject = it.next();
            assertEquals(confirmedObject, ordered.previousKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertNull(ordered.previousKey(confirmedLast));

        if (!isAllowNullKey()) {
            final OrderedMap<K, V> finalOrdered = ordered;
            assertThrows(NullPointerException.class, () -> finalOrdered.previousKey(null));
        } else if (!isAllowNullKey()) {
            assertNull(ordered.previousKey(null));
        }
    }

}
