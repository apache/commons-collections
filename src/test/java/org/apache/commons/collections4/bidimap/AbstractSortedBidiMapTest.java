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
package org.apache.commons.collections4.bidimap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.map.AbstractSortedMapTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link SortedBidiMap}.
 */
public abstract class AbstractSortedBidiMapTest<K extends Comparable<K>, V extends Comparable<V>> extends AbstractOrderedBidiMapTest<K, V> {

    /**
     * Every way of removing all the mappings of a sub map through its collection views.
     */
    static Stream<Arguments> getViewClears() {
        return Stream.of(
                arguments("keySet().clear", (Consumer<Map<?, ?>>) map -> map.keySet().clear()),
                arguments("keySet().retainAll", (Consumer<Map<?, ?>>) map -> map.keySet().retainAll(Collections.emptySet())),
                arguments("values().clear", (Consumer<Map<?, ?>>) map -> map.values().clear()),
                arguments("values().retainAll", (Consumer<Map<?, ?>>) map -> map.values().retainAll(Collections.emptySet())),
                arguments("entrySet().clear", (Consumer<Map<?, ?>>) map -> map.entrySet().clear()),
                arguments("entrySet().retainAll", (Consumer<Map<?, ?>>) map -> map.entrySet().retainAll(Collections.emptySet())));
    }

    protected List<K> sortedKeys;
    protected List<V> sortedValues = new ArrayList<>();
    protected SortedSet<V> sortedNewValues = new TreeSet<>();

    public AbstractSortedBidiMapTest() {
        sortedKeys = getAsList(getSampleKeys());
        Collections.sort(sortedKeys);
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

    public BulkTest bulkTestHeadMap() {
        return new AbstractSortedMapTest.TestHeadMap<K, V>((AbstractBidiMapTest) this);
    }

    public BulkTest bulkTestSubMap() {
        return new AbstractSortedMapTest.TestSubMap<>((AbstractBidiMapTest) this);
    }

    public BulkTest bulkTestTailMap() {
        return new AbstractSortedMapTest.TestTailMap<>((AbstractBidiMapTest) this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedBidiMap<K, V> getMap() {
        return (SortedBidiMap<K, V>) super.getMap();
    }

    @Override
    public boolean isAllowNullKey() {
        return false;
    }

    @Override
    public boolean isAllowNullValueGet() {
        return false;
    }

    @Override
    public boolean isAllowNullValuePut() {
        return false;
    }

    @Override
    public SortedMap<K, V> makeConfirmedMap() {
        return new TreeMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedBidiMap<K, V> makeFullMap() {
        return (SortedBidiMap<K, V>) super.makeFullMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract SortedBidiMap<K, V> makeObject();

    @Test
    void testBidiClearByHeadMap() {
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("getViewClears")
    void testBidiClearByHeadMapView(final String description, final Consumer<Map<?, ?>> clear) {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final K toKey = new ArrayList<>(getMap().keySet()).get(2);
        // the mappings out of the range of the sub map must be kept in both directions
        clear.accept(getMap().headMap(toKey));
        clear.accept(((SortedMap<K, V>) getConfirmed()).headMap(toKey));
        verify();
    }

    @Test
    void testBidiClearBySubMap() {
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
    void testBidiClearByTailMap() {
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
    void testBidiHeadMapContains() {
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
    void testBidiPutByHeadMapKeyOutOfRange() {
        if (!isPutAddSupported()) {
            return;
        }
        resetFull();
        final List<K> keys = new ArrayList<>(getMap().keySet());
        final K toKey = keys.get(2);
        final V firstValue = getMap().get(keys.get(0));
        final SortedMap<K, V> sub = getMap().headMap(toKey);
        // the rejected put must not remove the mapping of the sub map which holds the value
        assertThrows(IllegalArgumentException.class, () -> sub.put(toKey, firstValue));
        verify();
    }

    @Test
    void testBidiPutByTailMapValueOutOfRange() {
        if (!isPutChangeSupported()) {
            return;
        }
        resetFull();
        final List<K> keys = new ArrayList<>(getMap().keySet());
        final K first = keys.get(0);
        final K second = keys.get(1);
        final K fromKey = keys.get(2);
        final K nestedFromKey = keys.get(3);
        final V firstValue = getMap().get(first);
        final V secondValue = getMap().get(second);
        // as with a put in the full map, the key out of the range of the sub map loses the value
        final SortedMap<K, V> sub = getMap().tailMap(fromKey);
        assertEquals(getConfirmed().get(fromKey), sub.put(fromKey, firstValue));
        getConfirmed().remove(first);
        getConfirmed().put(fromKey, firstValue);
        verify();
        // and so it does through a sub map of the sub map
        assertEquals(getConfirmed().get(nestedFromKey), sub.tailMap(nestedFromKey).put(nestedFromKey, secondValue));
        getConfirmed().remove(second);
        getConfirmed().put(nestedFromKey, secondValue);
        verify();
    }

    @Test
    void testBidiRemoveByHeadMap() {
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
    void testBidiRemoveByHeadMapEntrySet() {
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
    void testBidiRemoveByHeadMapValuesOutOfRange() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final K toKey = new ArrayList<>(getMap().keySet()).get(2);
        final V toKeyValue = getMap().get(toKey);
        final Collection<V> values = getMap().headMap(toKey).values();
        // the value is mapped to a key out of the range of the sub map
        assertFalse(values.contains(toKeyValue));
        assertFalse(values.remove(toKeyValue));
        verify();
    }

    @Test
    void testBidiRemoveBySubMap() {
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
    void testBidiRemoveBySubMapEntrySet() {
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

    @Test
    void testBidiRemoveByTailMap() {
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
    void testBidiRemoveByTailMapEntrySet() {
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
    void testBidiSubMapContains() {
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
    void testBidiTailMapContains() {
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

}
