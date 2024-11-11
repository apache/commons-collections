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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.AbstractMapIteratorTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IterableMap}.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public abstract class AbstractIterableMapTest<K, V> extends AbstractMapTest<IterableMap<K, V>, K, V> {

    public class InnerTestMapIterator extends AbstractMapIteratorTest<K, V> {

        @Override
        public V[] addSetValues() {
            return getNewSampleValues();
        }

        @Override
        public Map<K, V> getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return getConfirmed();
        }

        @Override
        public Map<K, V> getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractIterableMapTest.this.getMap();
        }

        @Override
        public boolean isGetStructuralModify() {
            return AbstractIterableMapTest.this.isGetStructuralModify();
        }

        @Override
        public MapIterator<K, V> makeEmptyIterator() {
            resetEmpty();
            return AbstractIterableMapTest.this.getMap().mapIterator();
        }

        @Override
        public MapIterator<K, V> makeObject() {
            resetFull();
            return AbstractIterableMapTest.this.getMap().mapIterator();
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
            AbstractIterableMapTest.this.verify();
        }
    }

    public BulkTest bulkTestMapIterator() {
        return new InnerTestMapIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IterableMap<K, V> getMap() {
        return (IterableMap<K, V>) super.getMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IterableMap<K, V> makeFullMap() {
        return (IterableMap<K, V>) super.makeFullMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract IterableMap<K, V> makeObject();

    @Test
    public void testFailFastEntrySet() {
        if (!isRemoveSupported()) {
            return;
        }
        if (!isFailFastExpected()) {
            return;
        }
        resetFull();
        Iterator<Map.Entry<K, V>> it = getMap().entrySet().iterator();
        final Map.Entry<K, V> val = it.next();
        getMap().remove(val.getKey());
        final Iterator<Map.Entry<K, V>> finalIt0 = it;
        assertThrows(ConcurrentModificationException.class, () -> finalIt0.next());

        resetFull();
        it = getMap().entrySet().iterator();
        it.next();
        getMap().clear();
        final Iterator<Map.Entry<K, V>> finalIt1 = it;
        assertThrows(ConcurrentModificationException.class, () -> finalIt1.next());
    }

    @Test
    public void testFailFastKeySet() {
        if (!isRemoveSupported()) {
            return;
        }
        if (!isFailFastExpected()) {
            return;
        }
        resetFull();
        Iterator<K> it = getMap().keySet().iterator();
        final K val = it.next();
        getMap().remove(val);
        final Iterator<K> finalIt0 = it;
        assertThrows(ConcurrentModificationException.class, () -> finalIt0.next());

        resetFull();
        it = getMap().keySet().iterator();
        it.next();
        getMap().clear();
        final Iterator<K> finalIt1 = it;
        assertThrows(ConcurrentModificationException.class, () -> finalIt1.next());
    }

//  public void testCreate() throws Exception {
//      resetEmpty();
//      writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/HashedMap.emptyCollection.version3.obj");
//      resetFull();
//      writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/HashedMap.fullCollection.version3.obj");
//  }

    @Test
    public void testFailFastValues() {
        if (!isRemoveSupported()) {
            return;
        }
        if (!isFailFastExpected()) {
            return;
        }
        resetFull();
        Iterator<V> it = getMap().values().iterator();
        it.next();
        getMap().remove(getMap().keySet().iterator().next());
        final Iterator<V> finalIt0 = it;
        assertThrows(ConcurrentModificationException.class, () -> finalIt0.next());

        resetFull();
        it = getMap().values().iterator();
        it.next();
        getMap().clear();
        final Iterator<V> finalIt1 = it;
        assertThrows(ConcurrentModificationException.class, () -> finalIt1.next());
    }

}
