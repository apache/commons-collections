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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.iterators.AbstractTestMapIterator;

/**
 * Abstract test class for {@link IterableMap} methods and contracts.
 *
 * @version $Revision$
 *
 * @author Stephen Colebourne
 */
public abstract class AbstractTestIterableMap<K, V> extends AbstractTestMap<K, V> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test name
     */
    public AbstractTestIterableMap(String testName) {
        super(testName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract IterableMap<K, V> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public IterableMap<K, V> makeFullMap() {
        return (IterableMap<K, V>) super.makeFullMap();
    }

    //-----------------------------------------------------------------------
    public void testFailFastEntrySet() {
        if (isRemoveSupported() == false) return;
        if (isFailFastExpected() == false) return;
        resetFull();
        Iterator<Map.Entry<K, V>> it = getMap().entrySet().iterator();
        Map.Entry<K, V> val = it.next();
        getMap().remove(val.getKey());
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}

        resetFull();
        it = getMap().entrySet().iterator();
        it.next();
        getMap().clear();
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
    }

    public void testFailFastKeySet() {
        if (isRemoveSupported() == false) return;
        if (isFailFastExpected() == false) return;
        resetFull();
        Iterator<K> it = getMap().keySet().iterator();
        K val = it.next();
        getMap().remove(val);
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}

        resetFull();
        it = getMap().keySet().iterator();
        it.next();
        getMap().clear();
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
    }

    public void testFailFastValues() {
        if (isRemoveSupported() == false) return;
        if (isFailFastExpected() == false) return;
        resetFull();
        Iterator<V> it = getMap().values().iterator();
        it.next();
        getMap().remove(getMap().keySet().iterator().next());
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}

        resetFull();
        it = getMap().values().iterator();
        it.next();
        getMap().clear();
        try {
            it.next();
            fail();
        } catch (ConcurrentModificationException ex) {}
    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestMapIterator() {
        return new InnerTestMapIterator();
    }

    public class InnerTestMapIterator extends AbstractTestMapIterator<K, V> {
        public InnerTestMapIterator() {
            super("InnerTestMapIterator");
        }

        @Override
        public V[] addSetValues() {
            return AbstractTestIterableMap.this.getNewSampleValues();
        }

        @Override
        public boolean supportsRemove() {
            return AbstractTestIterableMap.this.isRemoveSupported();
        }

        @Override
        public boolean isGetStructuralModify() {
            return AbstractTestIterableMap.this.isGetStructuralModify();
        }

        @Override
        public boolean supportsSetValue() {
            return AbstractTestIterableMap.this.isSetValueSupported();
        }

        @Override
        public MapIterator<K, V> makeEmptyIterator() {
            resetEmpty();
            return AbstractTestIterableMap.this.getMap().mapIterator();
        }

        @Override
        public MapIterator<K, V> makeObject() {
            resetFull();
            return AbstractTestIterableMap.this.getMap().mapIterator();
        }

        @Override
        public Map<K, V> getMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestIterableMap.this.getMap();
        }

        @Override
        public Map<K, V> getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return AbstractTestIterableMap.this.getConfirmed();
        }

        @Override
        public void verify() {
            super.verify();
            AbstractTestIterableMap.this.verify();
        }
    }

//  public void testCreate() throws Exception {
//      resetEmpty();
//      writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/HashedMap.emptyCollection.version3.obj");
//      resetFull();
//      writeExternalFormToDisk((Serializable) map, "D:/dev/collections/data/test/HashedMap.fullCollection.version3.obj");
//  }

    /**
     * {@inheritDoc}
     */
    @Override
    public IterableMap<K, V> getMap() {
        return (IterableMap<K, V>) super.getMap();
    }
}
