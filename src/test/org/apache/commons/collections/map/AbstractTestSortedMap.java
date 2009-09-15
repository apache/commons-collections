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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.BulkTest;

/**
 * Abstract test class for {@link java.util.SortedMap} methods and contracts.
 *
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public abstract class AbstractTestSortedMap<K, V> extends AbstractTestMap<K, V> {

    /**
     * JUnit constructor.
     *
     * @param testName  the test name
     */
    public AbstractTestSortedMap(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Can't sort null keys.
     *
     * @return false
     */
    public boolean isAllowNullKey() {
        return false;
    }

    /**
     * SortedMap uses TreeMap as its known comparison.
     *
     * @return a map that is known to be valid
     */
    public SortedMap<K, V> makeConfirmedMap() {
        return new TreeMap<K, V>();
    }

    //-----------------------------------------------------------------------
    public void testComparator() {
//        SortedMap<K, V> sm = makeFullMap();
        // no tests I can think of
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract SortedMap<K, V> makeObject();

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<K, V> makeFullMap() {
        return (SortedMap<K, V>) super.makeFullMap();
    }

    public void testFirstKey() {
        SortedMap<K, V> sm = makeFullMap();
        assertSame(sm.keySet().iterator().next(), sm.firstKey());
    }

    public void testLastKey() {
        SortedMap<K, V> sm = makeFullMap();
        K obj = null;
        for (Iterator<K> it = sm.keySet().iterator(); it.hasNext();) {
            obj = it.next();
        }
        assertSame(obj, sm.lastKey());
    }

    //-----------------------------------------------------------------------
    public BulkTest bulkTestHeadMap() {
        return new TestHeadMap<K, V>(this);
    }

    public BulkTest bulkTestTailMap() {
        return new TestTailMap<K, V>(this);
    }

    public BulkTest bulkTestSubMap() {
        return new TestSubMap<K, V>(this);
    }

    public static abstract class TestViewMap <K, V> extends AbstractTestSortedMap<K, V> {
        protected final AbstractTestMap<K, V> main;
        protected final List<K> subSortedKeys = new ArrayList<K>();
        protected final List<V> subSortedValues = new ArrayList<V>();
        protected final List<V> subSortedNewValues = new ArrayList<V>();

        public TestViewMap(String name, AbstractTestMap<K, V> main) {
            super(name);
            this.main = main;
        }
        public void resetEmpty() {
            // needed to init verify correctly
            main.resetEmpty();
            super.resetEmpty();
        }
        public void resetFull() {
            // needed to init verify correctly
            main.resetFull();
            super.resetFull();
        }
        public void verify() {
            // cross verify changes on view with changes on main map
            super.verify();
            main.verify();
        }
        public BulkTest bulkTestHeadMap() {
            return null;  // block infinite recursion
        }
        public BulkTest bulkTestTailMap() {
            return null;  // block infinite recursion
        }
        public BulkTest bulkTestSubMap() {
            return null;  // block infinite recursion
        }

        @SuppressWarnings("unchecked")
        public K[] getSampleKeys() {
            return (K[]) subSortedKeys.toArray();
        }
        @SuppressWarnings("unchecked")
        public V[] getSampleValues() {
            return (V[]) subSortedValues.toArray();
        }
        @SuppressWarnings("unchecked")
        public V[] getNewSampleValues() {
            return (V[]) subSortedNewValues.toArray();
        }

        public boolean isAllowNullKey() {
            return main.isAllowNullKey();
        }
        public boolean isAllowNullValue() {
            return main.isAllowNullValue();
        }
        public boolean isPutAddSupported() {
            return main.isPutAddSupported();
        }
        public boolean isPutChangeSupported() {
            return main.isPutChangeSupported();
        }
        public boolean isRemoveSupported() {
            return main.isRemoveSupported();
        }
        public boolean isTestSerialization() {
            return false;
        }
//        public void testSimpleSerialization() throws Exception {
//            if (main.isSubMapViewsSerializable() == false) return;
//            super.testSimpleSerialization();
//        }
//        public void testSerializeDeserializeThenCompare() throws Exception {
//            if (main.isSubMapViewsSerializable() == false) return;
//            super.testSerializeDeserializeThenCompare();
//        }
//        public void testEmptyMapCompatibility() throws Exception {
//            if (main.isSubMapViewsSerializable() == false) return;
//            super.testEmptyMapCompatibility();
//        }
//        public void testFullMapCompatibility() throws Exception {
//            if (main.isSubMapViewsSerializable() == false) return;
//            super.testFullMapCompatibility();
//        }
    }

    public static class TestHeadMap<K, V> extends TestViewMap<K, V> {
        static final int SUBSIZE = 6;
        final K toKey;

        public TestHeadMap(AbstractTestMap<K, V> main) {
            super("SortedMap.HeadMap", main);
            Map<K, V> sm = main.makeFullMap();
            for (Iterator<Map.Entry<K, V>> it = sm.entrySet().iterator(); it.hasNext();) {
                Map.Entry<K, V> entry = it.next();
                this.subSortedKeys.add(entry.getKey());
                this.subSortedValues.add(entry.getValue());
            }
            this.toKey = this.subSortedKeys.get(SUBSIZE);
            this.subSortedKeys.subList(SUBSIZE, this.subSortedKeys.size()).clear();
            this.subSortedValues.subList(SUBSIZE, this.subSortedValues.size()).clear();
            this.subSortedNewValues.addAll(Arrays.asList(main.getNewSampleValues()).subList(0, SUBSIZE));
        }
        public SortedMap<K, V> makeObject() {
            // done this way so toKey is correctly set in the returned map
            return ((SortedMap<K, V>) main.makeObject()).headMap(toKey);
        }
        public SortedMap<K, V> makeFullMap() {
            return ((SortedMap<K, V>) main.makeFullMap()).headMap(toKey);
        }
        public void testHeadMapOutOfRange() {
            if (isPutAddSupported() == false) return;
            resetEmpty();
            try {
                getMap().put(toKey, subSortedValues.get(0));
                fail();
            } catch (IllegalArgumentException ex) {}
            verify();
        }
        public String getCompatibilityVersion() {
            return main.getCompatibilityVersion() + ".HeadMapView";
        }

//        public void testCreate() throws Exception {
//            Map map = makeEmptyMap();
//            writeExternalFormToDisk(
//                (java.io.Serializable) map,
//                "D:/dev/collections/data/test/FixedSizeSortedMap.emptyCollection.version3.1.HeadMapView.obj");
//            map = makeFullMap();
//            writeExternalFormToDisk(
//                (java.io.Serializable) map,
//                "D:/dev/collections/data/test/FixedSizeSortedMap.fullCollection.version3.1.HeadMapView.obj");
//        }
    }

    public static class TestTailMap <K, V> extends TestViewMap<K, V> {
        static final int SUBSIZE = 6;
        final K fromKey;
        final K invalidKey;

        public TestTailMap(AbstractTestMap<K, V> main) {
            super("SortedMap.TailMap", main);
            Map<K, V> sm = main.makeFullMap();
            for (Iterator<Map.Entry<K, V>> it = sm.entrySet().iterator(); it.hasNext();) {
                Map.Entry<K, V> entry = it.next();
                this.subSortedKeys.add(entry.getKey());
                this.subSortedValues.add(entry.getValue());
            }
            this.fromKey = this.subSortedKeys.get(this.subSortedKeys.size() - SUBSIZE);
            this.invalidKey = this.subSortedKeys.get(this.subSortedKeys.size() - SUBSIZE - 1);
            this.subSortedKeys.subList(0, this.subSortedKeys.size() - SUBSIZE).clear();
            this.subSortedValues.subList(0, this.subSortedValues.size() - SUBSIZE).clear();
            this.subSortedNewValues.addAll(Arrays.asList(main.getNewSampleValues()).subList(0, SUBSIZE));
        }
        public SortedMap<K, V> makeObject() {
            // done this way so toKey is correctly set in the returned map
            return ((SortedMap<K, V>) main.makeObject()).tailMap(fromKey);
        }
        public SortedMap<K, V> makeFullMap() {
            return ((SortedMap<K, V>) main.makeFullMap()).tailMap(fromKey);
        }
        public void testTailMapOutOfRange() {
            if (isPutAddSupported() == false) return;
            resetEmpty();
            try {
                getMap().put(invalidKey, subSortedValues.get(0));
                fail();
            } catch (IllegalArgumentException ex) {}
            verify();
        }
        public String getCompatibilityVersion() {
            return main.getCompatibilityVersion() + ".TailMapView";
        }

//        public void testCreate() throws Exception {
//            Map map = makeEmptyMap();
//            writeExternalFormToDisk(
//                (java.io.Serializable) map,
//                "D:/dev/collections/data/test/FixedSizeSortedMap.emptyCollection.version3.1.TailMapView.obj");
//            map = makeFullMap();
//            writeExternalFormToDisk(
//                (java.io.Serializable) map,
//                "D:/dev/collections/data/test/FixedSizeSortedMap.fullCollection.version3.1.TailMapView.obj");
//        }
    }

    public static class TestSubMap<K, V> extends TestViewMap<K, V> {
        static final int SUBSIZE = 3;
        final K fromKey;
        final K toKey;

        public TestSubMap(AbstractTestMap<K, V> main) {
            super("SortedMap.SubMap", main);
            Map<K, V> sm = main.makeFullMap();
            for (Iterator<Map.Entry<K, V>> it = sm.entrySet().iterator(); it.hasNext();) {
                Map.Entry<K, V> entry = it.next();
                this.subSortedKeys.add(entry.getKey());
                this.subSortedValues.add(entry.getValue());
            }
            this.fromKey = this.subSortedKeys.get(SUBSIZE);
            this.toKey = this.subSortedKeys.get(this.subSortedKeys.size() - SUBSIZE);

            this.subSortedKeys.subList(0, SUBSIZE).clear();
            this.subSortedKeys.subList(this.subSortedKeys.size() - SUBSIZE, this.subSortedKeys.size()).clear();

            this.subSortedValues.subList(0, SUBSIZE).clear();
            this.subSortedValues.subList(this.subSortedValues.size() - SUBSIZE, this.subSortedValues.size()).clear();

            this.subSortedNewValues.addAll(Arrays.asList(main.getNewSampleValues()).subList(
                SUBSIZE, this.main.getNewSampleValues().length - SUBSIZE));
        }

        public SortedMap<K, V> makeObject() {
            // done this way so toKey is correctly set in the returned map
            return ((SortedMap<K, V>) main.makeObject()).subMap(fromKey, toKey);
        }
        public SortedMap<K, V> makeFullMap() {
            return ((SortedMap<K, V>) main.makeFullMap()).subMap(fromKey, toKey);
        }
        public void testSubMapOutOfRange() {
            if (isPutAddSupported() == false) return;
            resetEmpty();
            try {
                getMap().put(toKey, subSortedValues.get(0));
                fail();
            } catch (IllegalArgumentException ex) {}
            verify();
        }
        public String getCompatibilityVersion() {
            return main.getCompatibilityVersion() + ".SubMapView";
        }

//        public void testCreate() throws Exception {
//            Map map = makeEmptyMap();
//            writeExternalFormToDisk(
//                (java.io.Serializable) map,
//                "D:/dev/collections/data/test/TransformedSortedMap.emptyCollection.version3.1.SubMapView.obj");
//            map = makeFullMap();
//            writeExternalFormToDisk(
//                (java.io.Serializable) map,
//                "D:/dev/collections/data/test/TransformedSortedMap.fullCollection.version3.1.SubMapView.obj");
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<K, V> getMap() {
        return (SortedMap<K, V>) super.getMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<K, V> getConfirmed() {
        return (SortedMap<K, V>) super.getConfirmed();
    }
}
