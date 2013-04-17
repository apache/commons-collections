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
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.TransformerUtils;
import org.apache.commons.collections4.collection.TransformedCollectionTest;
import org.apache.commons.collections4.map.TransformedMap;

/**
 * Extension of {@link AbstractMapTest} for exercising the {@link TransformedMap}
 * implementation.
 *
 * @since 3.0
 * @version $Id$
 */
public class TransformedMapTest<K, V> extends AbstractIterableMapTest<K, V> {

    public TransformedMapTest(final String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    @Override
    public IterableMap<K, V> makeObject() {
        return TransformedMap.transformingMap(new HashMap<K, V>(), TransformerUtils.<K> nopTransformer(),
                TransformerUtils.<V> nopTransformer());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testTransformedMap() {
        final Object[] els = new Object[] { "1", "3", "5", "7", "2", "4", "6" };

        Map<K, V> map = TransformedMap
                .transformingMap(
                        new HashMap<K, V>(),
                        (Transformer<? super K, ? extends K>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER,
                        null);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put((K) els[i], (V) els[i]);
            assertEquals(i + 1, map.size());
            assertEquals(true, map.containsKey(new Integer((String) els[i])));
            assertEquals(false, map.containsKey(els[i]));
            assertEquals(true, map.containsValue(els[i]));
            assertEquals(els[i], map.get(new Integer((String) els[i])));
        }

        assertEquals(null, map.remove(els[0]));
        assertEquals(els[0], map.remove(new Integer((String) els[0])));

        map = TransformedMap.transformingMap(new HashMap(), null, TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put((K) els[i], (V) els[i]);
            assertEquals(i + 1, map.size());
            assertEquals(true, map.containsValue(new Integer((String) els[i])));
            assertEquals(false, map.containsValue(els[i]));
            assertEquals(true, map.containsKey(els[i]));
            assertEquals(new Integer((String) els[i]), map.get(els[i]));
        }

        assertEquals(new Integer((String) els[0]), map.remove(els[0]));

        final Set<Map.Entry<K, V>> entrySet = map.entrySet();
        final Map.Entry<K, V>[] array = entrySet.toArray(new Map.Entry[0]);
        array[0].setValue((V) "66");
        assertEquals(new Integer(66), array[0].getValue());
        assertEquals(new Integer(66), map.get(array[0].getKey()));

        final Map.Entry entry = entrySet.iterator().next();
        entry.setValue("88");
        assertEquals(new Integer(88), entry.getValue());
        assertEquals(new Integer(88), map.get(entry.getKey()));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testFactory_Decorate() {
        final Map<K, V> base = new HashMap<K, V>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        final Map<K, V> trans = TransformedMap
                .transformingMap(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(3, trans.size());
        assertEquals("1", trans.get("A"));
        assertEquals("2", trans.get("B"));
        assertEquals("3", trans.get("C"));
        trans.put((K) "D", (V) "4");
        assertEquals(new Integer(4), trans.get("D"));
    }

    @SuppressWarnings("unchecked")
    public void testFactory_decorateTransform() {
        final Map<K, V> base = new HashMap<K, V>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        final Map<K, V> trans = TransformedMap
                .transformedMap(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TransformedCollectionTest.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(3, trans.size());
        assertEquals(new Integer(1), trans.get("A"));
        assertEquals(new Integer(2), trans.get("B"));
        assertEquals(new Integer(3), trans.get("C"));
        trans.put((K) "D", (V) "4");
        assertEquals(new Integer(4), trans.get("D"));
    }

    //-----------------------------------------------------------------------
    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/TransformedMap.emptyCollection.version4.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "src/test/resources/data/test/TransformedMap.fullCollection.version4.obj");
//    }

}
