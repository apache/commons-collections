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
package org.apache.commons.collections4.multimap;

import java.util.Collection;

import junit.framework.Test;

import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.TransformerUtils;
import org.apache.commons.collections4.collection.TransformedCollectionTest;

/**
 * Tests for TransformedMultiValuedMap
 * 
 * @since 4.1
 */
public class TransformedMultiValuedMapTest<K, V> extends AbstractMultiValuedMapTest<K, V> {

    public TransformedMultiValuedMapTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TransformedMultiValuedMapTest.class);
    }

    // -----------------------------------------------------------------------
    @Override
    public MultiValuedMap<K, V> makeObject() {
        return TransformedMultiValuedMap.transformingMap(new ArrayListValuedHashMap<K, V>(),
                TransformerUtils.<K> nopTransformer(), TransformerUtils.<V> nopTransformer());
    }

    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testKeyTransformedMap() {
        final Object[] els = new Object[] { "1", "3", "5", "7", "2", "4", "6" };

        MultiValuedMap<K, V> map = TransformedMultiValuedMap.transformingMap(
                new ArrayListValuedHashMap<K, V>(),
                (Transformer<? super K, ? extends K>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER,
                null);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put((K) els[i], (V) els[i]);
            assertEquals(i + 1, map.size());
            assertEquals(true, map.containsKey(Integer.valueOf((String) els[i])));
            assertEquals(false, map.containsKey(els[i]));
            assertEquals(true, map.containsValue(els[i]));
            assertEquals(true, map.get((K) Integer.valueOf((String) els[i])).contains(els[i]));
        }

        Collection<V> coll = map.remove(els[0]);
        assertNotNull(coll);
        assertEquals(0, coll.size());
        assertEquals(true, map.remove(Integer.valueOf((String) els[0])).contains(els[0]));
    }

    @SuppressWarnings("unchecked")
    public void testValueTransformedMap() {
        final Object[] els = new Object[] { "1", "3", "5", "7", "2", "4", "6" };

        MultiValuedMap<K, V> map = TransformedMultiValuedMap.transformingMap(
                new ArrayListValuedHashMap<K, V>(), null,
                (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put((K) els[i], (V) els[i]);
            assertEquals(i + 1, map.size());
            assertEquals(true, map.containsValue(Integer.valueOf((String) els[i])));
            assertEquals(false, map.containsValue(els[i]));
            assertEquals(true, map.containsKey(els[i]));
            assertEquals(true, map.get((K) els[i]).contains(Integer.valueOf((String) els[i])));
        }
        assertEquals(true, map.remove(els[0]).contains(Integer.valueOf((String) els[0])));
    }

    // -----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testFactory_Decorate() {
        final MultiValuedMap<K, V> base = new ArrayListValuedHashMap<>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        final MultiValuedMap<K, V> trans = TransformedMultiValuedMap
                .transformingMap(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(3, trans.size());
        assertEquals(true, trans.get((K) "A").contains("1"));
        assertEquals(true, trans.get((K) "B").contains("2"));
        assertEquals(true, trans.get((K) "C").contains("3"));
        trans.put((K) "D", (V) "4");
        assertEquals(true, trans.get((K) "D").contains(Integer.valueOf(4)));
    }

    @SuppressWarnings("unchecked")
    public void testFactory_decorateTransform() {
        final MultiValuedMap<K, V> base = new ArrayListValuedHashMap<>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        final MultiValuedMap<K, V> trans = TransformedMultiValuedMap
                .transformedMap(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(3, trans.size());
        assertEquals(true, trans.get((K) "A").contains(Integer.valueOf(1)));
        assertEquals(true, trans.get((K) "B").contains(Integer.valueOf(2)));
        assertEquals(true, trans.get((K) "C").contains(Integer.valueOf(3)));
        trans.put((K) "D", (V) "4");
        assertEquals(true, trans.get((K) "D").contains(Integer.valueOf(4)));
    }

//    public void testCreate() throws Exception {
//        writeExternalFormToDisk((java.io.Serializable) makeObject(),
//                "src/test/resources/data/test/TransformedMultiValuedMap.emptyCollection.version4.1.obj");
//        writeExternalFormToDisk((java.io.Serializable) makeFullMap(),
//                "src/test/resources/data/test/TransformedMultiValuedMap.fullCollection.version4.1.obj");
//    }

}
