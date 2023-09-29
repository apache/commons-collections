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

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.map.AbstractIterableMapTest;
import org.apache.commons.collections4.map.Flat3MapTest;

import java.util.Map;

/**
 * Abstract class for testing MapIterator to simplify nesting inside AbstractMapTest types
 */
public abstract class AbstractMapIteratorNestedTest<K, V> extends AbstractMapIteratorTest<K, V> {
    protected AbstractMapIteratorNestedTest() {
        super("AbstractMapIteratorNestedTest");
    }

    protected abstract AbstractIterableMapTest<K, V> getEnclosing();

    @Override
    public MapIterator<K, V> makeEmptyIterator() {
        getEnclosing().resetEmpty();
        return getMap().mapIterator();
    }

    @Override
    public MapIterator<K, V> makeObject() {
        getEnclosing().resetFull();
        return getMap().mapIterator();
    }

    @Override
    public IterableMap<K, V> getMap() {
        // assumes makeFullMapIterator() called first
        return getEnclosing().getMap();
    }

    @Override
    public Map<K, V> getConfirmedMap() {
        // assumes makeFullMapIterator() called first
        return getEnclosing().getConfirmed();
    }

    @Override
    public V[] addSetValues() {
        return getEnclosing().getNewSampleValues();
    }

    @Override
    public boolean supportsRemove() {
        return getEnclosing().isRemoveSupported();
    }

    @Override
    public boolean supportsSetValue() {
        return getEnclosing().isSetValueSupported();
    }

    @Override
    public boolean isGetStructuralModify() {
        return getEnclosing().isGetStructuralModify();
    }

    @Override
    public void verify() {
        super.verify();
        getEnclosing().verify();
    }
}
