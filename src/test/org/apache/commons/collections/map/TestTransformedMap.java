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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.collections.collection.TestTransformedCollection;

/**
 * Extension of {@link AbstractTestMap} for exercising the {@link TransformedMap}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public class TestTransformedMap<K, V> extends AbstractTestIterableMap<K, V> {

    public TestTransformedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransformedMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTransformedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //-----------------------------------------------------------------------
    @Override
    public IterableMap<K, V> makeObject() {
        return TransformedMap.decorate(new HashMap<K, V>(), TransformerUtils.<K> nopTransformer(),
                TransformerUtils.<V> nopTransformer());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testTransformedMap() {
        Object[] els = new Object[] { "1", "3", "5", "7", "2", "4", "6" };

        Map<K, V> map = TransformedMap
                .decorate(
                        new HashMap<K, V>(),
                        (Transformer<? super K, ? extends K>) TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER,
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

        map = TransformedMap.decorate(new HashMap(), null, TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
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

        Set<Map.Entry<K, V>> entrySet = map.entrySet();
        Map.Entry<K, V>[] array = entrySet.toArray(new Map.Entry[0]);
        array[0].setValue((V) "66");
        assertEquals(new Integer(66), array[0].getValue());
        assertEquals(new Integer(66), map.get(array[0].getKey()));

        Map.Entry entry = entrySet.iterator().next();
        entry.setValue("88");
        assertEquals(new Integer(88), entry.getValue());
        assertEquals(new Integer(88), map.get(entry.getKey()));
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testFactory_Decorate() {
        Map<K, V> base = new HashMap<K, V>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        Map<K, V> trans = TransformedMap
                .decorate(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(3, trans.size());
        assertEquals("1", trans.get("A"));
        assertEquals("2", trans.get("B"));
        assertEquals("3", trans.get("C"));
        trans.put((K) "D", (V) "4");
        assertEquals(new Integer(4), trans.get("D"));
    }

    @SuppressWarnings("unchecked")
    public void testFactory_decorateTransform() {
        Map<K, V> base = new HashMap<K, V>();
        base.put((K) "A", (V) "1");
        base.put((K) "B", (V) "2");
        base.put((K) "C", (V) "3");

        Map<K, V> trans = TransformedMap
                .decorateTransform(
                        base,
                        null,
                        (Transformer<? super V, ? extends V>) TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
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
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/TransformedMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/TransformedMap.fullCollection.version3.1.obj");
//    }
}
