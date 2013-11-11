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

import java.util.HashMap;

import junit.framework.Test;
import org.apache.commons.collections4.BoundedMap;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.OrderedMap;

/**
 * JUnit tests.
 *
 * @version $Id$
 */
public class SingletonMapTest<K, V> extends AbstractOrderedMapTest<K, V> {

    private static final Integer ONE = Integer.valueOf(1);
    private static final Integer TWO = Integer.valueOf(2);
    private static final String TEN = "10";

    public SingletonMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(SingletonMapTest.class);
    }

    //-----------------------------------------------------------------------
    @Override
    public OrderedMap<K, V> makeObject() {
        // need an empty singleton map, but thats not possible
        // use a ridiculous fake instead to make the tests pass
        return UnmodifiableOrderedMap.unmodifiableOrderedMap(ListOrderedMap.listOrderedMap(new HashMap<K, V>()));
    }

    @Override
    public String[] ignoredTests() {
        // the ridiculous map above still doesn't pass these tests
        // but its not relevant, so we ignore them
        return new String[] {
            "SingletonMapTest.bulkTestMapIterator.testEmptyMapIterator",
            "SingletonMapTest.bulkTestOrderedMapIterator.testEmptyMapIterator",
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public SingletonMap<K, V> makeFullMap() {
        return new SingletonMap<K, V>((K) ONE, (V) TWO);
    }

    @Override
    public boolean isPutAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public K[] getSampleKeys() {
        return (K[]) new Object[] { ONE };
    }

    @Override
    @SuppressWarnings("unchecked")
    public V[] getSampleValues() {
        return (V[]) new Object[] { TWO };
    }

    @Override
    @SuppressWarnings("unchecked")
    public V[] getNewSampleValues() {
        return (V[]) new Object[] { TEN };
    }

    //-----------------------------------------------------------------------
    public void testClone() {
        final SingletonMap<K, V> map = makeFullMap();
        assertEquals(1, map.size());
        final SingletonMap<K, V> cloned = map.clone();
        assertEquals(1, cloned.size());
        assertEquals(true, cloned.containsKey(ONE));
        assertEquals(true, cloned.containsValue(TWO));
    }

    public void testKeyValue() {
        final SingletonMap<K, V> map = makeFullMap();
        assertEquals(1, map.size());
        assertEquals(ONE, map.getKey());
        assertEquals(TWO, map.getValue());
        assertTrue(map instanceof KeyValue);
    }

    public void testBoundedMap() {
        final SingletonMap<K, V> map = makeFullMap();
        assertEquals(1, map.size());
        assertEquals(true, map.isFull());
        assertEquals(1, map.maxSize());
        assertTrue(map instanceof BoundedMap);
    }

    //-----------------------------------------------------------------------
//    public BulkTest bulkTestMapIterator() {
//        return new TestFlatMapIterator();
//    }
//
//    public class TestFlatMapIterator extends AbstractTestOrderedMapIterator {
//        public TestFlatMapIterator() {
//            super("TestFlatMapIterator");
//        }
//
//        public Object[] addSetValues() {
//            return TestSingletonMap.this.getNewSampleValues();
//        }
//
//        public boolean supportsRemove() {
//            return TestSingletonMap.this.isRemoveSupported();
//        }
//
//        public boolean supportsSetValue() {
//            return TestSingletonMap.this.isSetValueSupported();
//        }
//
//        public MapIterator makeEmptyMapIterator() {
//            resetEmpty();
//            return ((Flat3Map) TestSingletonMap.this.map).mapIterator();
//        }
//
//        public MapIterator makeFullMapIterator() {
//            resetFull();
//            return ((Flat3Map) TestSingletonMap.this.map).mapIterator();
//        }
//
//        public Map getMap() {
//            // assumes makeFullMapIterator() called first
//            return TestSingletonMap.this.map;
//        }
//
//        public Map getConfirmedMap() {
//            // assumes makeFullMapIterator() called first
//            return TestSingletonMap.this.confirmed;
//        }
//
//        public void verify() {
//            super.verify();
//            TestSingletonMap.this.verify();
//        }
//    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/SingletonMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/SingletonMap.fullCollection.version4.obj");
//    }

}
