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

import org.apache.commons.collections4.OrderedMap;

import java.util.Map;

/**
 * Abstract class for testing the MapIterator interface to simplify nesting inside AbstractMapTest types
 */
public abstract class AbstractOrderedMapNestedTest<K, V> extends AbstractOrderedMapTest<K, V> {
    protected AbstractOrderedMapNestedTest() {
        super("AbstractOrderedMapNestedTest");
    }

    protected abstract AbstractMapTest<K, V> getEnclosing();

    @Override
    public OrderedMap<K, V> makeObject() {
        return (OrderedMap<K, V>) getEnclosing().makeObject();
    }

    @Override
    public OrderedMap<K, V> makeFullMap() {
        return (OrderedMap<K, V>) getEnclosing().makeFullMap();
    }

    @Override
    public Map<K, V> makeConfirmedMap() {
        return getEnclosing().makeConfirmedMap();
    }

    @Override
    public boolean supportsEmptyCollections() {
        return getEnclosing().supportsEmptyCollections();
    }

    @Override
    public boolean supportsFullCollections() {
        return getEnclosing().supportsFullCollections();
    }

    @Override
    public boolean isTestSerialization() {
        return getEnclosing().isTestSerialization();
    }

    @Override
    public boolean isEqualsCheckable() {
        return getEnclosing().isEqualsCheckable();
    }

    @Override
    public boolean isPutAddSupported() {
        return getEnclosing().isPutAddSupported();
    }

    @Override
    public boolean isPutChangeSupported() {
        return getEnclosing().isPutChangeSupported();
    }

    @Override
    public boolean isSetValueSupported() {
        return getEnclosing().isSetValueSupported();
    }

    @Override
    public boolean isRemoveSupported() {
        return getEnclosing().isRemoveSupported();
    }

    @Override
    public boolean isGetStructuralModify() {
        return getEnclosing().isGetStructuralModify();
    }

    @Override
    public boolean isSubMapViewsSerializable() {
        return getEnclosing().isSubMapViewsSerializable();
    }

    @Override
    public boolean isAllowNullValue() {
        return getEnclosing().isAllowNullValue();
    }

    @Override
    public boolean isAllowNullKey() {
        return getEnclosing().isAllowNullKey();
    }

    @Override
    public boolean isAllowDuplicateValues() {
        return getEnclosing().isAllowDuplicateValues();
    }

    @Override
    public boolean isFailFastExpected() {
        return getEnclosing().isFailFastExpected();
    }

    @Override
    public boolean areEqualElementsDistinguishable() {
        return getEnclosing().areEqualElementsDistinguishable();
    }

    @Override
    public K[] getSampleKeys() {
        return getEnclosing().getSampleKeys();
    }

    @Override
    public K[] getOtherKeys() {
        return getEnclosing().getOtherKeys();
    }

    @Override
    public V[] getOtherValues() {
        return getEnclosing().getOtherValues();
    }

    @Override
    public Object[] getOtherNonNullStringElements() {
        return getEnclosing().getOtherNonNullStringElements();
    }

    @Override
    public V[] getSampleValues() {
        return getEnclosing().getSampleValues();
    }

    @Override
    public V[] getNewSampleValues() {
        return getEnclosing().getNewSampleValues();
    }

    @Override
    public String getCompatibilityVersion() {
        return getEnclosing().getCompatibilityVersion();
    }

    @Override
    protected int getIterationBehaviour() {
        return getEnclosing().getIterationBehaviour();
    }
}
