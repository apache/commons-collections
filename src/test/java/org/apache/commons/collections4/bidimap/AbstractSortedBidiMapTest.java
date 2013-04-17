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

/**
 * Abstract test class for {@link SortedBidiMap} methods and contracts.
 *
 * @version $Id$
 */
public abstract class AbstractSortedBidiMapTest<K extends Comparable<K>, V extends Comparable<V>> extends AbstractOrderedBidiMapTest<K, V> {

    protected List<K> sortedKeys;
    protected List<V> sortedValues = new ArrayList<V>();
    protected SortedSet<V> sortedNewValues = new TreeSet<V>();

    public AbstractSortedBidiMapTest(final String testName) {
        super(testName);
        sortedKeys = getAsList(getSampleKeys());
        Collections.sort(sortedKeys);
        sortedKeys = Collections.unmodifiableList(sortedKeys);

        final Map<K, V> map = new TreeMap<K, V>();
        addSampleMappings(map);

        sortedValues.addAll(map.values());
        sortedValues = Collections.unmodifiableList(sortedValues);

        sortedNewValues.addAll(this.<V> getAsList(getNewSampleValues()));
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

    //-----------------------------------------------------------------------
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
        return new TreeMap<K, V>();
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, head.containsKey(first));
        assertEquals(true, sm.containsValue(firstValue));
        assertEquals(true, head.containsValue(firstValue));
        assertEquals(true, sm.containsKey(second));
        assertEquals(false, head.containsKey(second));
        assertEquals(true, sm.containsValue(secondValue));
        assertEquals(false, head.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiClearByHeadMap() {
        if (isRemoveSupported() == false) {
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

        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(false, sub.containsValue(firstValue));

        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, sub.containsKey(second));
        assertEquals(false, sub.containsValue(secondValue));

        assertEquals(true, sm.containsKey(toKey));
        assertEquals(true, sm.containsValue(toKeyValue));
        assertEquals(true, sm.inverseBidiMap().containsKey(toKeyValue));
        assertEquals(true, sm.inverseBidiMap().containsValue(toKey));
        assertEquals(false, sub.containsKey(toKey));
        assertEquals(false, sub.containsValue(toKeyValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByHeadMap() {
        if (isRemoveSupported() == false) {
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));

        final V firstValue = sub.remove(first);
        assertEquals(1, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(false, sub.containsValue(firstValue));

        final V secondValue = sub.remove(second);
        assertEquals(0, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, sub.containsKey(second));
        assertEquals(false, sub.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByHeadMapEntrySet() {
        if (isRemoveSupported() == false) {
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, set.contains(firstEntry));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));
        assertEquals(true, set.contains(secondEntry));

        set.remove(firstEntry);
        assertEquals(1, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(firstEntry.getKey()));
        assertEquals(false, sm.containsValue(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertEquals(false, sub.containsKey(firstEntry.getKey()));
        assertEquals(false, sub.containsValue(firstEntry.getValue()));
        assertEquals(false, set.contains(firstEntry));

        set.remove(secondEntry);
        assertEquals(0, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(secondEntry.getKey()));
        assertEquals(false, sm.containsValue(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertEquals(false, sub.containsKey(secondEntry.getKey()));
        assertEquals(false, sub.containsValue(secondEntry.getValue()));
        assertEquals(false, set.contains(secondEntry));
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(true, sm.containsValue(firstValue));
        assertEquals(false, sub.containsValue(firstValue));
        assertEquals(true, sm.containsKey(fromKey));
        assertEquals(true, sub.containsKey(fromKey));
        assertEquals(true, sm.containsValue(fromKeyValue));
        assertEquals(true, sub.containsValue(fromKeyValue));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));
        assertEquals(true, sm.containsValue(secondValue));
        assertEquals(true, sub.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiClearByTailMap() {
        if (isRemoveSupported() == false) {
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

        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sm.containsValue(firstValue));
        assertEquals(true, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(true, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(false, sub.containsValue(firstValue));

        assertEquals(false, sm.containsKey(fromKey));
        assertEquals(false, sm.containsValue(fromKeyValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(fromKeyValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(fromKey));
        assertEquals(false, sub.containsKey(fromKey));
        assertEquals(false, sub.containsValue(fromKeyValue));

        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, sub.containsKey(second));
        assertEquals(false, sub.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByTailMap() {
        if (isRemoveSupported() == false) {
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));

        final Object firstValue = sub.remove(first);
        assertEquals(size - 3, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(false, sub.containsValue(firstValue));

        final Object secondValue = sub.remove(second);
        assertEquals(size - 4, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, sub.containsKey(second));
        assertEquals(false, sub.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveByTailMapEntrySet() {
        if (isRemoveSupported() == false) {
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, set.contains(firstEntry));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));
        assertEquals(true, set.contains(secondEntry));

        set.remove(firstEntry);
        assertEquals(size - 3, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(firstEntry.getKey()));
        assertEquals(false, sm.containsValue(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertEquals(false, sub.containsKey(firstEntry.getKey()));
        assertEquals(false, sub.containsValue(firstEntry.getValue()));
        assertEquals(false, set.contains(firstEntry));

        set.remove(secondEntry);
        assertEquals(size - 4, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(secondEntry.getKey()));
        assertEquals(false, sm.containsValue(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertEquals(false, sub.containsKey(secondEntry.getKey()));
        assertEquals(false, sub.containsValue(secondEntry.getValue()));
        assertEquals(false, set.contains(secondEntry));
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(true, sm.containsValue(firstValue));
        assertEquals(false, sub.containsValue(firstValue));
        assertEquals(true, sm.containsKey(fromKey));
        assertEquals(true, sub.containsKey(fromKey));
        assertEquals(true, sm.containsValue(fromKeyValue));
        assertEquals(true, sub.containsValue(fromKeyValue));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));
        assertEquals(true, sm.containsValue(secondValue));
        assertEquals(true, sub.containsValue(secondValue));
        assertEquals(true, sm.containsKey(third));
        assertEquals(false, sub.containsKey(third));
        assertEquals(true, sm.containsValue(thirdValue));
        assertEquals(false, sub.containsValue(thirdValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiClearBySubMap() {
        if (isRemoveSupported() == false) {
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

        assertEquals(false, sm.containsKey(fromKey));
        assertEquals(false, sm.containsValue(fromKeyValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(fromKeyValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(fromKey));
        assertEquals(false, sub.containsKey(fromKey));
        assertEquals(false, sub.containsValue(fromKeyValue));

        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(false, sub.containsValue(firstValue));

        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, sub.containsKey(second));
        assertEquals(false, sub.containsValue(secondValue));

        assertEquals(true, sm.containsKey(toKey));
        assertEquals(true, sm.containsValue(toKeyValue));
        assertEquals(true, sm.inverseBidiMap().containsKey(toKeyValue));
        assertEquals(true, sm.inverseBidiMap().containsValue(toKey));
        assertEquals(false, sub.containsKey(toKey));
        assertEquals(false, sub.containsValue(toKeyValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveBySubMap() {
        if (isRemoveSupported() == false) {
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));

        final V firstValue = sub.remove(first);
        assertEquals(2, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(first));
        assertEquals(false, sm.containsValue(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(first));
        assertEquals(false, sub.containsKey(first));
        assertEquals(false, sub.containsValue(firstValue));

        final V secondValue = sub.remove(second);
        assertEquals(1, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(second));
        assertEquals(false, sm.containsValue(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondValue));
        assertEquals(false, sm.inverseBidiMap().containsValue(second));
        assertEquals(false, sub.containsKey(second));
        assertEquals(false, sub.containsValue(secondValue));
    }

    //-----------------------------------------------------------------------
    public void testBidiRemoveBySubMapEntrySet() {
        if (isRemoveSupported() == false) {
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
        assertEquals(true, sm.containsKey(first));
        assertEquals(true, sub.containsKey(first));
        assertEquals(true, set.contains(firstEntry));
        assertEquals(true, sm.containsKey(second));
        assertEquals(true, sub.containsKey(second));
        assertEquals(true, set.contains(secondEntry));

        set.remove(firstEntry);
        assertEquals(2, sub.size());
        assertEquals(size - 1, sm.size());
        assertEquals(size - 1, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(firstEntry.getKey()));
        assertEquals(false, sm.containsValue(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(firstEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(firstEntry.getKey()));
        assertEquals(false, sub.containsKey(firstEntry.getKey()));
        assertEquals(false, sub.containsValue(firstEntry.getValue()));
        assertEquals(false, set.contains(firstEntry));

        set.remove(secondEntry);
        assertEquals(1, sub.size());
        assertEquals(size - 2, sm.size());
        assertEquals(size - 2, sm.inverseBidiMap().size());
        assertEquals(false, sm.containsKey(secondEntry.getKey()));
        assertEquals(false, sm.containsValue(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsKey(secondEntry.getValue()));
        assertEquals(false, sm.inverseBidiMap().containsValue(secondEntry.getKey()));
        assertEquals(false, sub.containsKey(secondEntry.getKey()));
        assertEquals(false, sub.containsValue(secondEntry.getValue()));
        assertEquals(false, set.contains(secondEntry));
    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestHeadMap() {
        return new AbstractSortedMapTest.TestHeadMap<K, V>(this);
    }

    public BulkTest bulkTestTailMap() {
        return new AbstractSortedMapTest.TestTailMap<K, V>(this);
    }

    public BulkTest bulkTestSubMap() {
        return new AbstractSortedMapTest.TestSubMap<K, V>(this);
    }

}
