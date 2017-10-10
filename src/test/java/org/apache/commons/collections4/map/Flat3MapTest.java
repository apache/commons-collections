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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.AbstractMapIteratorTest;

/**
 * JUnit tests.
 *
 */
public class Flat3MapTest<K, V> extends AbstractIterableMapTest<K, V> {

    private static final Integer ONE = Integer.valueOf(1);
    private static final Integer TWO = Integer.valueOf(2);
    private static final Integer THREE = Integer.valueOf(3);
    private static final String TEN = "10";
    private static final String TWENTY = "20";
    private static final String THIRTY = "30";

    public Flat3MapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(Flat3MapTest.class);
    }

    @Override
    public Flat3Map<K, V> makeObject() {
        return new Flat3Map<>();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testEquals1() {
        final Flat3Map<K, V> map1 = makeObject();
        map1.put((K) "a", (V) "testA");
        map1.put((K) "b", (V) "testB");
        final Flat3Map<K, V> map2 = makeObject();
        map2.put((K) "a", (V) "testB");
        map2.put((K) "b", (V) "testA");
        assertEquals(false, map1.equals(map2));
    }

    @SuppressWarnings("unchecked")
    public void testEquals2() {
        final Flat3Map<K, V> map1 = makeObject();
        map1.put((K) "a", (V) "testA");
        map1.put((K) "b", (V) "testB");
        final Flat3Map<K, V> map2 = makeObject();
        map2.put((K) "a", (V) "testB");
        map2.put((K) "c", (V) "testA");
        assertEquals(false, map1.equals(map2));
    }

    @SuppressWarnings("unchecked")
    public void testClone2() {
        final Flat3Map<K, V> map = makeObject();
        assertEquals(0, map.size());
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        assertEquals(2, map.size());
        assertEquals(true, map.containsKey(ONE));
        assertEquals(true, map.containsKey(TWO));
        assertSame(TEN, map.get(ONE));
        assertSame(TWENTY, map.get(TWO));

        // clone works (size = 2)
        final Flat3Map<K, V> cloned = map.clone();
        assertEquals(2, cloned.size());
        assertEquals(true, cloned.containsKey(ONE));
        assertEquals(true, cloned.containsKey(TWO));
        assertSame(TEN, cloned.get(ONE));
        assertSame(TWENTY, cloned.get(TWO));

        // change original doesn't change clone
        map.put((K) TEN, (V) ONE);
        map.put((K) TWENTY, (V) TWO);
        assertEquals(4, map.size());
        assertEquals(2, cloned.size());
        assertEquals(true, cloned.containsKey(ONE));
        assertEquals(true, cloned.containsKey(TWO));
        assertSame(TEN, cloned.get(ONE));
        assertSame(TWENTY, cloned.get(TWO));
    }

    @SuppressWarnings("unchecked")
    public void testClone4() {
        final Flat3Map<K, V> map = makeObject();
        assertEquals(0, map.size());
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) TEN, (V) ONE);
        map.put((K) TWENTY, (V) TWO);

        // clone works (size = 4)
        final Flat3Map<K, V> cloned = map.clone();
        assertEquals(4, map.size());
        assertEquals(4, cloned.size());
        assertEquals(true, cloned.containsKey(ONE));
        assertEquals(true, cloned.containsKey(TWO));
        assertEquals(true, cloned.containsKey(TEN));
        assertEquals(true, cloned.containsKey(TWENTY));
        assertSame(TEN, cloned.get(ONE));
        assertSame(TWENTY, cloned.get(TWO));
        assertSame(ONE, cloned.get(TEN));
        assertSame(TWO, cloned.get(TWENTY));

        // change original doesn't change clone
        map.clear();
        assertEquals(0, map.size());
        assertEquals(4, cloned.size());
        assertEquals(true, cloned.containsKey(ONE));
        assertEquals(true, cloned.containsKey(TWO));
        assertEquals(true, cloned.containsKey(TEN));
        assertEquals(true, cloned.containsKey(TWENTY));
        assertSame(TEN, cloned.get(ONE));
        assertSame(TWENTY, cloned.get(TWO));
        assertSame(ONE, cloned.get(TEN));
        assertSame(TWO, cloned.get(TWENTY));
    }

    public void testSerialisation0() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(map);
        final byte[] bytes = bout.toByteArray();
        out.close();
        final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        final ObjectInputStream in = new ObjectInputStream(bin);
        final Flat3Map<?, ?> ser = (Flat3Map<?, ?>) in.readObject();
        in.close();
        assertEquals(0, map.size());
        assertEquals(0, ser.size());
    }

    @SuppressWarnings("unchecked")
    public void testSerialisation2() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(map);
        final byte[] bytes = bout.toByteArray();
        out.close();
        final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        final ObjectInputStream in = new ObjectInputStream(bin);
        final Flat3Map<?, ?> ser = (Flat3Map<?, ?>) in.readObject();
        in.close();
        assertEquals(2, map.size());
        assertEquals(2, ser.size());
        assertEquals(true, ser.containsKey(ONE));
        assertEquals(true, ser.containsKey(TWO));
        assertEquals(TEN, ser.get(ONE));
        assertEquals(TWENTY, ser.get(TWO));
    }

    @SuppressWarnings("unchecked")
    public void testSerialisation4() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) TEN, (V) ONE);
        map.put((K) TWENTY, (V) TWO);

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(map);
        final byte[] bytes = bout.toByteArray();
        out.close();
        final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        final ObjectInputStream in = new ObjectInputStream(bin);
        final Flat3Map<?, ?> ser = (Flat3Map<?, ?>) in.readObject();
        in.close();
        assertEquals(4, map.size());
        assertEquals(4, ser.size());
        assertEquals(true, ser.containsKey(ONE));
        assertEquals(true, ser.containsKey(TWO));
        assertEquals(true, ser.containsKey(TEN));
        assertEquals(true, ser.containsKey(TWENTY));
        assertEquals(TEN, ser.get(ONE));
        assertEquals(TWENTY, ser.get(TWO));
        assertEquals(ONE, ser.get(TEN));
        assertEquals(TWO, ser.get(TWENTY));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testEntryIteratorSetValue1() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) THREE, (V) THIRTY);

        final Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        final Map.Entry<K, V> entry = it.next();
        entry.setValue((V) "NewValue");
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(ONE));
        assertEquals(true, map.containsKey(TWO));
        assertEquals(true, map.containsKey(THREE));
        assertEquals("NewValue", map.get(ONE));
        assertEquals(TWENTY, map.get(TWO));
        assertEquals(THIRTY, map.get(THREE));
    }

    @SuppressWarnings("unchecked")
    public void testEntryIteratorSetValue2() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) THREE, (V) THIRTY);

        final Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        it.next();
        final Map.Entry<K, V> entry = it.next();
        entry.setValue((V) "NewValue");
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(ONE));
        assertEquals(true, map.containsKey(TWO));
        assertEquals(true, map.containsKey(THREE));
        assertEquals(TEN, map.get(ONE));
        assertEquals("NewValue", map.get(TWO));
        assertEquals(THIRTY, map.get(THREE));
    }

    @SuppressWarnings("unchecked")
    public void testEntryIteratorSetValue3() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) THREE, (V) THIRTY);

        final Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        it.next();
        it.next();
        final Map.Entry<K, V> entry = it.next();
        entry.setValue((V) "NewValue");
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(ONE));
        assertEquals(true, map.containsKey(TWO));
        assertEquals(true, map.containsKey(THREE));
        assertEquals(TEN, map.get(ONE));
        assertEquals(TWENTY, map.get(TWO));
        assertEquals("NewValue", map.get(THREE));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testMapIteratorSetValue1() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) THREE, (V) THIRTY);

        final MapIterator<K, V> it = map.mapIterator();
        it.next();
        it.setValue((V) "NewValue");
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(ONE));
        assertEquals(true, map.containsKey(TWO));
        assertEquals(true, map.containsKey(THREE));
        assertEquals("NewValue", map.get(ONE));
        assertEquals(TWENTY, map.get(TWO));
        assertEquals(THIRTY, map.get(THREE));
    }

    @SuppressWarnings("unchecked")
    public void testMapIteratorSetValue2() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) THREE, (V) THIRTY);

        final MapIterator<K, V> it = map.mapIterator();
        it.next();
        it.next();
        it.setValue((V) "NewValue");
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(ONE));
        assertEquals(true, map.containsKey(TWO));
        assertEquals(true, map.containsKey(THREE));
        assertEquals(TEN, map.get(ONE));
        assertEquals("NewValue", map.get(TWO));
        assertEquals(THIRTY, map.get(THREE));
    }

    @SuppressWarnings("unchecked")
    public void testMapIteratorSetValue3() throws Exception {
        final Flat3Map<K, V> map = makeObject();
        map.put((K) ONE, (V) TEN);
        map.put((K) TWO, (V) TWENTY);
        map.put((K) THREE, (V) THIRTY);

        final MapIterator<K, V> it = map.mapIterator();
        it.next();
        it.next();
        it.next();
        it.setValue((V) "NewValue");
        assertEquals(3, map.size());
        assertEquals(true, map.containsKey(ONE));
        assertEquals(true, map.containsKey(TWO));
        assertEquals(true, map.containsKey(THREE));
        assertEquals(TEN, map.get(ONE));
        assertEquals(TWENTY, map.get(TWO));
        assertEquals("NewValue", map.get(THREE));
    }

    //-----------------------------------------------------------------------
    @Override
    public BulkTest bulkTestMapIterator() {
        return new TestFlatMapIterator();
    }

    public class TestFlatMapIterator extends AbstractMapIteratorTest<K, V> {
        public TestFlatMapIterator() {
            super("TestFlatMapIterator");
        }

        @Override
        public V[] addSetValues() {
            return Flat3MapTest.this.getNewSampleValues();
        }

        @Override
        public boolean supportsRemove() {
            return Flat3MapTest.this.isRemoveSupported();
        }

        @Override
        public boolean supportsSetValue() {
            return Flat3MapTest.this.isSetValueSupported();
        }

        @Override
        public MapIterator<K, V> makeEmptyIterator() {
            resetEmpty();
            return Flat3MapTest.this.getMap().mapIterator();
        }

        @Override
        public MapIterator<K, V> makeObject() {
            resetFull();
            return Flat3MapTest.this.getMap().mapIterator();
        }

        @Override
        public IterableMap<K, V> getMap() {
            // assumes makeFullMapIterator() called first
            return Flat3MapTest.this.getMap();
        }

        @Override
        public Map<K, V> getConfirmedMap() {
            // assumes makeFullMapIterator() called first
            return Flat3MapTest.this.getConfirmed();
        }

        @Override
        public void verify() {
            super.verify();
            Flat3MapTest.this.verify();
        }
    }

    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/Flat3Map.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/Flat3Map.fullCollection.version4.obj");
//    }

    public void testCollections261() {
        final Flat3Map<Integer, Integer> m = new Flat3Map<>();
        m.put( Integer.valueOf(1), Integer.valueOf(1) );
        m.put( Integer.valueOf(0), Integer.valueOf(0) );
        assertEquals( Integer.valueOf(1), m.remove( Integer.valueOf(1) ) ); 
        assertEquals( Integer.valueOf(0), m.remove( Integer.valueOf(0) ) ); 

        m.put( Integer.valueOf(2), Integer.valueOf(2) );
        m.put( Integer.valueOf(1), Integer.valueOf(1) );
        m.put( Integer.valueOf(0), Integer.valueOf(0) );
        assertEquals( Integer.valueOf(2), m.remove( Integer.valueOf(2) ) ); 
        assertEquals( Integer.valueOf(1), m.remove( Integer.valueOf(1) ) ); 
        assertEquals( Integer.valueOf(0), m.remove( Integer.valueOf(0) ) ); 
    }

    public void testToString() {
        final Flat3Map<Integer, Integer> m = new Flat3Map<>();
        final String string0 = m.toString();
        assertNotNull(string0);
        m.put( Integer.valueOf(1), Integer.valueOf(1) );
        final String string1 = m.toString();
        assertNotNull(string1);
        assertNotSame(string0, string1);
        m.put( Integer.valueOf(0), Integer.valueOf(0) );
        final String string2 = m.toString();
        assertNotNull(string2);
        assertNotSame(string0, string2);
        assertNotSame(string1, string2);
        m.put( Integer.valueOf(2), Integer.valueOf(2) );
        final String string3 = m.toString();
        assertNotNull(string3);
        assertNotSame(string0, string3);
        assertNotSame(string1, string3);
        assertNotSame(string2, string3);
    }
}
