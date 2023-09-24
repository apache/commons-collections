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

import org.apache.commons.collections4.IterableSortedMap;
import org.apache.commons.collections4.OrderedMap;
import org.junit.jupiter.api.Nested;

import java.util.Map;
import java.util.SortedMap;

/**
 * Abstract test class to apply tests for both {@link SortedMap} and {@link OrderedMap} contracts.
 */
public abstract class AbstractIterableSortedMapTest<K, V> extends AbstractSortedMapTest<K, V> {
    protected AbstractIterableSortedMapTest(final String testName) {
        super(testName);
    }

    @Override
    public abstract IterableSortedMap<K, V> makeObject();

    @Override
    public IterableSortedMap<K, V> makeFullMap() {
        return (IterableSortedMap<K, V>) super.makeFullMap();
    }

    @Nested
    public class TestAsOrderedMap extends AbstractOrderedMapTest<K, V> {
        public TestAsOrderedMap() {
            super("TestAsOrderedMap");
        }

        @Override
        public OrderedMap<K, V> makeObject() {
            return AbstractIterableSortedMapTest.this.makeObject();
        }

        @Override
        public OrderedMap<K, V> makeFullMap() {
            return AbstractIterableSortedMapTest.this.makeFullMap();
        }

        @Override
        public Map<K, V> makeConfirmedMap() {
            return AbstractIterableSortedMapTest.this.makeConfirmedMap();
        }

        @Override
        public boolean supportsEmptyCollections() {
            return AbstractIterableSortedMapTest.this.supportsEmptyCollections();
        }

        @Override
        public boolean supportsFullCollections() {
            return AbstractIterableSortedMapTest.this.supportsFullCollections();
        }

        @Override
        public boolean isTestSerialization() {
            return AbstractIterableSortedMapTest.this.isTestSerialization();
        }

        @Override
        public boolean isEqualsCheckable() {
            return AbstractIterableSortedMapTest.this.isEqualsCheckable();
        }

        @Override
        public boolean isPutAddSupported() {
            return AbstractIterableSortedMapTest.this.isPutAddSupported();
        }

        @Override
        public boolean isPutChangeSupported() {
            return AbstractIterableSortedMapTest.this.isPutChangeSupported();
        }

        @Override
        public boolean isSetValueSupported() {
            return AbstractIterableSortedMapTest.this.isSetValueSupported();
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractIterableSortedMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isGetStructuralModify() {
            return AbstractIterableSortedMapTest.this.isGetStructuralModify();
        }

        @Override
        public boolean isSubMapViewsSerializable() {
            return AbstractIterableSortedMapTest.this.isSubMapViewsSerializable();
        }

        @Override
        public boolean isAllowNullValue() {
            return AbstractIterableSortedMapTest.this.isAllowNullValue();
        }

        @Override
        public boolean isAllowDuplicateValues() {
            return AbstractIterableSortedMapTest.this.isAllowDuplicateValues();
        }

        @Override
        public boolean isFailFastExpected() {
            return AbstractIterableSortedMapTest.this.isFailFastExpected();
        }

        @Override
        public boolean areEqualElementsDistinguishable() {
            return AbstractIterableSortedMapTest.this.areEqualElementsDistinguishable();
        }

        @Override
        public K[] getSampleKeys() {
            return AbstractIterableSortedMapTest.this.getSampleKeys();
        }

        @Override
        public K[] getOtherKeys() {
            return AbstractIterableSortedMapTest.this.getOtherKeys();
        }

        @Override
        public V[] getOtherValues() {
            return AbstractIterableSortedMapTest.this.getOtherValues();
        }

        @Override
        public Object[] getOtherNonNullStringElements() {
            return AbstractIterableSortedMapTest.this.getOtherNonNullStringElements();
        }

        @Override
        public V[] getSampleValues() {
            return AbstractIterableSortedMapTest.this.getSampleValues();
        }

        @Override
        public V[] getNewSampleValues() {
            return AbstractIterableSortedMapTest.this.getNewSampleValues();
        }

        @Override
        public void addSampleMappings(Map<? super K, ? super V> m) {
            AbstractIterableSortedMapTest.this.addSampleMappings(m);
        }

        @Override
        public String getCompatibilityVersion() {
            return AbstractIterableSortedMapTest.this.getCompatibilityVersion();
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractIterableSortedMapTest.this.getIterationBehaviour();
        }

        @Override
        public boolean isAllowNullKey() {
            return AbstractIterableSortedMapTest.this.isAllowNullKey();
        }
    }
}
