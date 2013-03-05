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
package org.apache.commons.collections.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.iterators.AbstractOrderedMapIteratorTest;

/**
 * Abstract test class for {@link OrderedMap} methods and contracts.
 *
 * @version $Id$
 */
public abstract class AbstractOrderedMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test name
     */
    public AbstractOrderedMapTest(final String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract OrderedMap<K, V> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedMap<K, V> makeFullMap() {
        return (OrderedMap<K, V>) super.makeFullMap();
    }

    //-----------------------------------------------------------------------
    /**
     * OrderedMap uses TreeMap as its known comparison.
     *
     * @return a map that is known to be valid
     */
    @Override
    public Map<K, V> makeConfirmedMap() {
        return new TreeMap<K, V>(new NullComparator<K>());
    }

    /**
     * The only confirmed collection we have that is ordered is the sorted one.
     * Thus, sort the keys.
     */
    @Override
    @SuppressWarnings("unchecked")
    public K[] getSampleKeys() {
        final List<K> list = new ArrayList<K>(Arrays.asList(super.getSampleKeys()));
        Collections.sort(list, new NullComparator<K>());
        return (K[]) list.toArray();
    }

    //-----------------------------------------------------------------------
    public void testFirstKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        try {
            ordered.firstKey();
            fail();
        } catch (final NoSuchElementException ex) {}

        resetFull();
        ordered = getMap();
        final K confirmedFirst = confirmed.keySet().iterator().next();
        assertEquals(confirmedFirst, ordered.firstKey());
    }

    public void testLastKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        try {
            ordered.lastKey();
            fail();
        } catch (final NoSuchElementException ex) {}

        resetFull();
        ordered = getMap();
        K confirmedLast = null;
        for (final Iterator<K> it = confirmed.keySet().iterator(); it.hasNext();) {
            confirmedLast = it.next();
        }
        assertEquals(confirmedLast, ordered.lastKey());
    }

    //-----------------------------------------------------------------------
    public void testNextKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        assertEquals(null, ordered.nextKey(getOtherKeys()[0]));
        if (!isAllowNullKey()) {
            try {
                assertEquals(null, ordered.nextKey(null)); // this is allowed too
            } catch (final NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.nextKey(null));
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
        assertEquals(null, ordered.nextKey(confirmedLast));

        if (isAllowNullKey() == false) {
            try {
                ordered.nextKey(null);
                fail();
            } catch (final NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.nextKey(null));
        }
    }

    public void testPreviousKey() {
        resetEmpty();
        OrderedMap<K, V> ordered = getMap();
        assertEquals(null, ordered.previousKey(getOtherKeys()[0]));
        if (isAllowNullKey() == false) {
            try {
                assertEquals(null, ordered.previousKey(null)); // this is allowed too
            } catch (final NullPointerException ex) {}
        } else {
            assertEquals(null, ordered.previousKey(null));
        }

        resetFull();
        ordered = getMap();
        final List<K> list = new ArrayList<K>(confirmed.keySet());
        Collections.reverse(list);
        final Iterator<K> it = list.iterator();
        K confirmedLast = it.next();
        while (it.hasNext()) {
            final K confirmedObject = it.next();
            assertEquals(confirmedObject, ordered.previousKey(confirmedLast));
            confirmedLast = confirmedObject;
        }
        assertEquals(null, ordered.previousKey(confirmedLast));

        if (isAllowNullKey() == false) {
            try {
                ordered.previousKey(null);
                fail();
            } catch (final NullPointerException ex) {}
        } else {
            if (isAllowNullKey() == false) {
                assertEquals(null, ordered.previousKey(null));
            }
        }
    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestOrderedMapIterator() {
        return new InnerTestOrderedMapIterator();
    }

    public class InnerTestOrderedMapIterator extends AbstractOrderedMapIteratorTest<K, V> {
        public InnerTestOrderedMapIterator() {
            super("InnerTestOrderedMapIterator");
        }

        @Override
        public boolean supportsRemove() {
            return AbstractOrderedMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isGetStructuralModify() {
            return AbstractOrderedMapTest.this.isGetStructuralModify();
        }

        @Override
        public boolean supportsSetValue() {
            return AbstractOrderedMapTest.this.isSetValueSupported();
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
        public OrderedMap<K, V> getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractOrderedMapTest.this.getMap();
        }

        @Override
        public Map<K, V> getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return AbstractOrderedMapTest.this.getConfirmed();
        }

        @Override
        public void verify() {
            super.verify();
            AbstractOrderedMapTest.this.verify();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderedMap<K, V> getMap() {
        return (OrderedMap<K, V>) super.getMap();
    }
}
