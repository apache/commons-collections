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
package org.apache.commons.collections4.bidimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.map.AbstractSortedMapTest;
import org.junit.jupiter.api.Test;

/**
 * Abstract test class for {@link SortedBidiMap} methods and contracts.
 *
 */
public abstract class AbstractSortedBidiMapTest<K extends Comparable<K>, V extends Comparable<V>> extends AbstractOrderedBidiMapTest<K, V> {

    protected List<K> sortedKeys;
    protected List<V> sortedValues = new ArrayList<>();
    protected SortedSet<V> sortedNewValues = new TreeSet<>();

    public AbstractSortedBidiMapTest(final String testName) {
        super(testName);
        sortedKeys = getAsList(getSampleKeys());
        sortedKeys.sort(null);
        sortedKeys = Collections.unmodifiableList(sortedKeys);

        final Map<K, V> map = new TreeMap<>();
        addSampleMappings(map);

        sortedValues.addAll(map.values());
        sortedValues = Collections.unmodifiableList(sortedValues);

        sortedNewValues.addAll(getAsList(getNewSampleValues()));
    }

//    public AbstractTestSortedBidiMap() {
//        super();
//        sortedKeys.addAll(Arrays.asList(getSampleValues()));
//        Collections.sort(sortedKeys);
//        sortedKeys = Collections.unmodifiableList(sortedKeys);
//
//        Map map = new TreeMap();
//        for (int i = 0; i < getSampleKeys().length; i++) {
//            map.put(getSampleValues()[i], getSampleKeys()[i]);
//        }
//        sortedValues.addAll(map.values());
//        sortedValues = Collections.unmodifiableList(sortedValues);
//
//        sortedNewValues.addAll(Arrays.asList(getNewSampleValues()));
//    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValue() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract SortedBidiMap<K, V> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedBidiMap<K, V> makeFullMap() {
        return (SortedBidiMap<K, V>) super.makeFullMap();
    }

    @Override
    public SortedMap<K, V> makeConfirmedMap() {
        return new TreeMap<>();
    }

    @Test
    public void testBidiHeadMapContains() {
        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        final K first = it.next();
        final K toKey = it.next();
        final K second = it.next();
        final V firstValue = sm.get(first);
        final V secondValue = sm.get(second);

        final SortedMap<K, V> head = sm.headMap(toKey);
        assertEquals(1, head.size());
        assertTrue(sm.containsKey(first));
        assertTrue(head.containsKey(first));
        assertTrue(sm.containsValue(firstValue));
        assertTrue(head.containsValue(firstValue));
        assertTrue(sm.containsKey(second));
        assertFalse(head.containsKey(second));
        assertTrue(sm.containsValue(secondValue));
        assertFalse(head.containsValue(secondValue));
    }

    @Test
    public void testBidiClearByHeadMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        final K first = it.next();
        final K second = it.next();
        final K toKey = it.next();

        final V firstValue = sm.get(first);
        final V secondValue = sm.get(second);
        final V toKeyValue = sm.get(toKey);

        final SortedMap<K, V> sub = sm.headMap(toKey);
        final int size = sm.size();
        assertEquals(2, sub.size());
        sub.clear();
        assertEquals(0, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());

        assertFalse(sm.containsKey(first));
        assertFalse(sm.containsValue(firstValue));
        assertFalse(sm.inverseBidiMap().containsKey(firstValue));
        assertFalse(sm.inverseBidiMap().containsValue(first));
        assertFalse(sub.containsKey(first));
        assertFalse(sub.containsValue(firstValue));

        assertFalse(sm.containsKey(second));
        assertFalse(sm.containsValue(secondValue));
        assertFalse(sm.inverseBidiMap().containsKey(secondValue));
        assertFalse(sm.inverseBidiMap().containsValue(second));
        assertFalse(sub.containsKey(second));
        assertFalse(sub.containsValue(secondValue));

        assertTrue(sm.containsKey(toKey));
        assertTrue(sm.containsValue(toKeyValue));
        assertTrue(sm.inverseBidiMap().containsKey(toKeyValue));
        assertTrue(sm.inverseBidiMap().containsValue(toKey));
        assertFalse(sub.containsKey(toKey));
        assertFalse(sub.containsValue(toKeyValue));
    }

    @Test
    public void testBidiRemoveByHeadMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        final K first = it.next();
        final K second = it.next();
        final K toKey = it.next();

        final int size = sm.size();
        final SortedMap<K, V> sub = sm.headMap(toKey);
        assertEquals(2, sub.size());
        assertTrue(sm.containsKey(first));
        assertTrue(sub.containsKey(first));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));

        final V firstValue = sub.remove(first);
        assertEquals(1, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(first));
        assertFalse(sm.containsValue(firstValue));
        assertFalse(sm.inverseBidiMap().containsKey(firstValue));
        assertFalse(sm.inverseBidiMap().containsValue(first));
        assertFalse(sub.containsKey(first));
        assertFalse(sub.containsValue(firstValue));

        final V secondValue = sub.remove(second);
        assertEquals(0, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(second));
        assertFalse(sm.containsValue(secondValue));
        assertFalse(sm.inverseBidiMap().containsKey(secondValue));
        assertFalse(sm.inverseBidiMap().containsValue(second));
        assertFalse(sub.containsKey(second));
        assertFalse(sub.containsValue(secondValue));
    }

    @Test
    public void testBidiRemoveByHeadMapEntrySet() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        final K first = it.next();
        final K second = it.next();
        final K toKey = it.next();

        final int size = sm.size();
        final SortedMap<K, V> sub = sm.headMap(toKey);
        final Set<Map.Entry<K, V>> set = sub.entrySet();
        assertEquals(2, sub.size());
        assertEquals(2, set.size());

        final Iterator<Map.Entry<K, V>> it2 = set.iterator();
        final Map.Entry<K, V> firstEntry = cloneMapEntry(it2.next());
        final Map.Entry<K, V> secondEntry = cloneMapEntry(it2.next());
        assertTrue(sm.containsKey(first));
        assertTrue(sub.containsKey(first));
        assertTrue(set.contains(firstEntry));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));
        assertTrue(set.contains(secondEntry));

        set.remove(firstEntry);
        assertEquals(1, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(firstEntry.getKey()));
        assertFalse(sm.containsValue(firstEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertFalse(sub.containsKey(firstEntry.getKey()));
        assertFalse(sub.containsValue(firstEntry.getValue()));
        assertFalse(set.contains(firstEntry));

        set.remove(secondEntry);
        assertEquals(0, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(secondEntry.getKey()));
        assertFalse(sm.containsValue(secondEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertFalse(sub.containsKey(secondEntry.getKey()));
        assertFalse(sub.containsValue(secondEntry.getValue()));
        assertFalse(set.contains(secondEntry));
    }

    @Test
    public void testBidiTailMapContains() {
        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        final K first = it.next();
        final K fromKey = it.next();
        final K second = it.next();
        final V firstValue = sm.get(first);
        final V fromKeyValue = sm.get(fromKey);
        final V secondValue = sm.get(second);

        final SortedMap<K, V> sub = sm.tailMap(fromKey);
        assertEquals(sm.size() - 1, sub.size());
        assertTrue(sm.containsKey(first));
        assertFalse(sub.containsKey(first));
        assertTrue(sm.containsValue(firstValue));
        assertFalse(sub.containsValue(firstValue));
        assertTrue(sm.containsKey(fromKey));
        assertTrue(sub.containsKey(fromKey));
        assertTrue(sm.containsValue(fromKeyValue));
        assertTrue(sub.containsValue(fromKeyValue));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));
        assertTrue(sm.containsValue(secondValue));
        assertTrue(sub.containsValue(secondValue));
    }

    @Test
    public void testBidiClearByTailMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        it.next();
        it.next();
        final K first = it.next();
        final K fromKey = it.next();
        final K second = it.next();

        final V firstValue = sm.get(first);
        final V fromKeyValue = sm.get(fromKey);
        final V secondValue = sm.get(second);

        final SortedMap<K, V> sub = sm.tailMap(fromKey);
        final int size = sm.size();
        assertEquals(size - 3, sub.size());
        sub.clear();
        assertEquals(0, sub.size());
        assertEquals(3, sm.size());
        assertEquals(3, sm.inverseBidiMap().size());

        assertTrue(sm.containsKey(first));
        assertTrue(sm.containsValue(firstValue));
        assertTrue(sm.inverseBidiMap().containsKey(firstValue));
        assertTrue(sm.inverseBidiMap().containsValue(first));
        assertFalse(sub.containsKey(first));
        assertFalse(sub.containsValue(firstValue));

        assertFalse(sm.containsKey(fromKey));
        assertFalse(sm.containsValue(fromKeyValue));
        assertFalse(sm.inverseBidiMap().containsKey(fromKeyValue));
        assertFalse(sm.inverseBidiMap().containsValue(fromKey));
        assertFalse(sub.containsKey(fromKey));
        assertFalse(sub.containsValue(fromKeyValue));

        assertFalse(sm.containsKey(second));
        assertFalse(sm.containsValue(secondValue));
        assertFalse(sm.inverseBidiMap().containsKey(secondValue));
        assertFalse(sm.inverseBidiMap().containsValue(second));
        assertFalse(sub.containsKey(second));
        assertFalse(sub.containsValue(secondValue));
    }

    @Test
    public void testBidiRemoveByTailMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        it.next();
        it.next();
        final K fromKey = it.next();
        final K first = it.next();
        final K second = it.next();

        final int size = sm.size();
        final SortedMap<K, V> sub = sm.tailMap(fromKey);
        assertTrue(sm.containsKey(first));
        assertTrue(sub.containsKey(first));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));

        final Object firstValue = sub.remove(first);
        assertEquals(size - 3, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(first));
        assertFalse(sm.containsValue(firstValue));
        assertFalse(sm.inverseBidiMap().containsKey(firstValue));
        assertFalse(sm.inverseBidiMap().containsValue(first));
        assertFalse(sub.containsKey(first));
        assertFalse(sub.containsValue(firstValue));

        final Object secondValue = sub.remove(second);
        assertEquals(size - 4, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(second));
        assertFalse(sm.containsValue(secondValue));
        assertFalse(sm.inverseBidiMap().containsKey(secondValue));
        assertFalse(sm.inverseBidiMap().containsValue(second));
        assertFalse(sub.containsKey(second));
        assertFalse(sub.containsValue(secondValue));
    }

    @Test
    public void testBidiRemoveByTailMapEntrySet() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        it.next();
        it.next();
        final K fromKey = it.next();
        final K first = it.next();
        final K second = it.next();

        final int size = sm.size();
        final SortedMap<K, V> sub = sm.tailMap(fromKey);
        final Set<Map.Entry<K, V>> set = sub.entrySet();
        final Iterator<Map.Entry<K, V>> it2 = set.iterator();
        it2.next();
        final Map.Entry<K, V> firstEntry = cloneMapEntry(it2.next());
        final Map.Entry<K, V> secondEntry = cloneMapEntry(it2.next());
        assertTrue(sm.containsKey(first));
        assertTrue(sub.containsKey(first));
        assertTrue(set.contains(firstEntry));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));
        assertTrue(set.contains(secondEntry));

        set.remove(firstEntry);
        assertEquals(size - 3, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(firstEntry.getKey()));
        assertFalse(sm.containsValue(firstEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertFalse(sub.containsKey(firstEntry.getKey()));
        assertFalse(sub.containsValue(firstEntry.getValue()));
        assertFalse(set.contains(firstEntry));

        set.remove(secondEntry);
        assertEquals(size - 4, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(secondEntry.getKey()));
        assertFalse(sm.containsValue(secondEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertFalse(sub.containsKey(secondEntry.getKey()));
        assertFalse(sub.containsValue(secondEntry.getValue()));
        assertFalse(set.contains(secondEntry));
    }

    @Test
    public void testBidiSubMapContains() {
        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        final K first = it.next();
        final K fromKey = it.next();
        final K second = it.next();
        final K toKey = it.next();
        final K third = it.next();
        final V firstValue = sm.get(first);
        final V fromKeyValue = sm.get(fromKey);
        final V secondValue = sm.get(second);
        final V thirdValue = sm.get(third);

        final SortedMap<K, V> sub = sm.subMap(fromKey, toKey);
        assertEquals(2, sub.size());
        assertTrue(sm.containsKey(first));
        assertFalse(sub.containsKey(first));
        assertTrue(sm.containsValue(firstValue));
        assertFalse(sub.containsValue(firstValue));
        assertTrue(sm.containsKey(fromKey));
        assertTrue(sub.containsKey(fromKey));
        assertTrue(sm.containsValue(fromKeyValue));
        assertTrue(sub.containsValue(fromKeyValue));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));
        assertTrue(sm.containsValue(secondValue));
        assertTrue(sub.containsValue(secondValue));
        assertTrue(sm.containsKey(third));
        assertFalse(sub.containsKey(third));
        assertTrue(sm.containsValue(thirdValue));
        assertFalse(sub.containsValue(thirdValue));
    }

    @Test
    public void testBidiClearBySubMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        it.next();
        final K fromKey = it.next();
        final K first = it.next();
        final K second = it.next();
        final K toKey = it.next();

        final V fromKeyValue = sm.get(fromKey);
        final V firstValue = sm.get(first);
        final V secondValue = sm.get(second);
        final V toKeyValue = sm.get(toKey);

        final SortedMap<K, V> sub = sm.subMap(fromKey, toKey);
        final int size = sm.size();
        assertEquals(3, sub.size());
        sub.clear();
        assertEquals(0, sub.size());
        assertEquals(size - 3, sm.size());
        assertEquals(size - 3, sm.inverseBidiMap().size());

        assertFalse(sm.containsKey(fromKey));
        assertFalse(sm.containsValue(fromKeyValue));
        assertFalse(sm.inverseBidiMap().containsKey(fromKeyValue));
        assertFalse(sm.inverseBidiMap().containsValue(fromKey));
        assertFalse(sub.containsKey(fromKey));
        assertFalse(sub.containsValue(fromKeyValue));

        assertFalse(sm.containsKey(first));
        assertFalse(sm.containsValue(firstValue));
        assertFalse(sm.inverseBidiMap().containsKey(firstValue));
        assertFalse(sm.inverseBidiMap().containsValue(first));
        assertFalse(sub.containsKey(first));
        assertFalse(sub.containsValue(firstValue));

        assertFalse(sm.containsKey(second));
        assertFalse(sm.containsValue(secondValue));
        assertFalse(sm.inverseBidiMap().containsKey(secondValue));
        assertFalse(sm.inverseBidiMap().containsValue(second));
        assertFalse(sub.containsKey(second));
        assertFalse(sub.containsValue(secondValue));

        assertTrue(sm.containsKey(toKey));
        assertTrue(sm.containsValue(toKeyValue));
        assertTrue(sm.inverseBidiMap().containsKey(toKeyValue));
        assertTrue(sm.inverseBidiMap().containsValue(toKey));
        assertFalse(sub.containsKey(toKey));
        assertFalse(sub.containsValue(toKeyValue));
    }

    @Test
    public void testBidiRemoveBySubMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        it.next();
        it.next();
        final K fromKey = it.next();
        final K first = it.next();
        final K second = it.next();
        final K toKey = it.next();

        final int size = sm.size();
        final SortedMap<K, V> sub = sm.subMap(fromKey, toKey);
        assertTrue(sm.containsKey(first));
        assertTrue(sub.containsKey(first));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));

        final V firstValue = sub.remove(first);
        assertEquals(2, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(first));
        assertFalse(sm.containsValue(firstValue));
        assertFalse(sm.inverseBidiMap().containsKey(firstValue));
        assertFalse(sm.inverseBidiMap().containsValue(first));
        assertFalse(sub.containsKey(first));
        assertFalse(sub.containsValue(firstValue));

        final V secondValue = sub.remove(second);
        assertEquals(1, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(second));
        assertFalse(sm.containsValue(secondValue));
        assertFalse(sm.inverseBidiMap().containsKey(secondValue));
        assertFalse(sm.inverseBidiMap().containsValue(second));
        assertFalse(sub.containsKey(second));
        assertFalse(sub.containsValue(secondValue));
    }

    @Test
    public void testBidiRemoveBySubMapEntrySet() {
        if (!isRemoveSupported()) {
            return;
        }

        // extra test as other tests get complex
        final SortedBidiMap<K, V> sm = makeFullMap();
        final Iterator<K> it = sm.keySet().iterator();
        it.next();
        it.next();
        final K fromKey = it.next();
        final K first = it.next();
        final K second = it.next();
        final K toKey = it.next();

        final int size = sm.size();
        final SortedMap<K, V> sub = sm.subMap(fromKey, toKey);
        final Set<Map.Entry<K, V>> set = sub.entrySet();
        assertEquals(3, set.size());
        final Iterator<Map.Entry<K, V>> it2 = set.iterator();
        it2.next();
        final Map.Entry<K, V> firstEntry = cloneMapEntry(it2.next());
        final Map.Entry<K, V> secondEntry = cloneMapEntry(it2.next());
        assertTrue(sm.containsKey(first));
        assertTrue(sub.containsKey(first));
        assertTrue(set.contains(firstEntry));
        assertTrue(sm.containsKey(second));
        assertTrue(sub.containsKey(second));
        assertTrue(set.contains(secondEntry));

        set.remove(firstEntry);
        assertEquals(2, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(firstEntry.getKey()));
        assertFalse(sm.containsValue(firstEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertFalse(sub.containsKey(firstEntry.getKey()));
        assertFalse(sub.containsValue(firstEntry.getValue()));
        assertFalse(set.contains(firstEntry));

        set.remove(secondEntry);
        assertEquals(1, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertFalse(sm.containsKey(secondEntry.getKey()));
        assertFalse(sm.containsValue(secondEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertFalse(sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertFalse(sub.containsKey(secondEntry.getKey()));
        assertFalse(sub.containsValue(secondEntry.getValue()));
        assertFalse(set.contains(secondEntry));
    }

    public BulkTest bulkTestHeadMap() {
        return new AbstractSortedMapTest.TestHeadMap<>(this);
    }

    public BulkTest bulkTestTailMap() {
        return new AbstractSortedMapTest.TestTailMap<>(this);
    }

    public BulkTest bulkTestSubMap() {
        return new AbstractSortedMapTest.TestSubMap<>(this);
    }

}
