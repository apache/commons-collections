/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class SortedPropertiesTest {

    private SortedProperties assertAscendingOrder(final SortedProperties sortedProperties) {
        final String[] keys = { "1", "10", "11", "2", "3", "4", "5", "6", "7", "8", "9" };
        final Enumeration<Object> enumObjects = sortedProperties.keys();
        for (int i = 0; i < keys.length; i++) {
            assertEquals("key" + keys[i], enumObjects.nextElement());
        }
        final Iterator<Object> iterSet = sortedProperties.keySet().iterator();
        for (int i = 0; i < keys.length; i++) {
            assertEquals("key" + keys[i], iterSet.next());
        }
        final Iterator<Entry<Object, Object>> iterEntrySet = sortedProperties.entrySet().iterator();
        for (int i = 0; i < keys.length; i++) {
            final Entry<Object, Object> next = iterEntrySet.next();
            assertEquals("key" + keys[i], next.getKey());
            assertEquals("value" + keys[i], next.getValue());
        }
        final Enumeration<?> propertyNames = sortedProperties.propertyNames();
        for (int i = 0; i < keys.length; i++) {
            assertEquals("key" + keys[i], propertyNames.nextElement());
        }
        final Set<String> propertyNameSet = sortedProperties.stringPropertyNames();
        final AtomicInteger i = new AtomicInteger(0);
        propertyNameSet.forEach(e -> assertEquals("key" + keys[i.getAndIncrement()], e));
        return sortedProperties;
    }

    private SortedProperties loadOrderedKeysReverse() throws FileNotFoundException, IOException {
        final SortedProperties sortedProperties = new SortedProperties();
        try (FileReader reader = new FileReader("src/test/resources/org/apache/commons/collections4/properties/test-reverse.properties")) {
            sortedProperties.load(reader);
        }
        return assertAscendingOrder(sortedProperties);
    }

    @Test
    void testCompute() {
        final SortedProperties sortedProperties = new SortedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            final AtomicInteger aInt = new AtomicInteger(i);
            sortedProperties.compute("key" + i, (k, v) -> "value" + aInt.get());
        }
        assertAscendingOrder(sortedProperties);
        sortedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            final AtomicInteger aInt = new AtomicInteger(i);
            sortedProperties.compute("key" + i, (k, v) -> "value" + aInt.get());
        }
        assertAscendingOrder(sortedProperties);
    }

    @Test
    void testComputeIfAbsent() {
        final SortedProperties sortedProperties = new SortedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            final AtomicInteger aInt = new AtomicInteger(i);
            sortedProperties.computeIfAbsent("key" + i, k -> "value" + aInt.get());
        }
        assertAscendingOrder(sortedProperties);
        sortedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            final AtomicInteger aInt = new AtomicInteger(i);
            sortedProperties.computeIfAbsent("key" + i, k -> "value" + aInt.get());
        }
        assertAscendingOrder(sortedProperties);
    }

    @Test
    void testEntrySet() {
        final SortedProperties sortedProperties = new SortedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Iterator<Map.Entry<Object, Object>> entries = sortedProperties.entrySet().iterator();
        for (char ch = first; ch <= last; ch++) {
            final Map.Entry<Object, Object> entry = entries.next();
            assertEquals(String.valueOf(ch), entry.getKey());
            assertEquals("Value" + ch, entry.getValue());
        }
    }

    @Test
    void testEntrySet2() {
        final SortedProperties sortedProperties = new SortedProperties();
        for (char ch = 'Z'; ch >= 'A'; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Iterator<Map.Entry<Object, Object>> entries = sortedProperties.entrySet().iterator();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            final Map.Entry<Object, Object> entry = entries.next();
            assertEquals(String.valueOf(ch), entry.getKey());
            assertEquals("Value" + ch, entry.getValue());
        }
    }

    @Test
    void testForEach() {
        final SortedProperties sortedProperties = new SortedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final AtomicInteger aCh = new AtomicInteger(last);
        sortedProperties.forEach((k, v) -> {
            final char ch = (char) aCh.getAndIncrement();
            assertEquals(String.valueOf(ch), k);
            assertEquals("Value" + ch, v);
        });
    }

    @Test
    void testKeys() {
        final SortedProperties sortedProperties = new SortedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Enumeration<Object> keys = sortedProperties.keys();
        for (char ch = first; ch <= last; ch++) {
            assertEquals(String.valueOf(ch), keys.nextElement());
        }
    }

    @Test
    void testKeys2() {
        final SortedProperties sortedProperties = new SortedProperties();
        for (char ch = 'Z'; ch >= 'A'; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Enumeration<Object> keys = sortedProperties.keys();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            assertEquals(String.valueOf(ch), keys.nextElement());
        }
    }

    @Test
    void testLoadOrderedKeys() throws IOException {
        final SortedProperties sortedProperties = new SortedProperties();
        try (FileReader reader = new FileReader("src/test/resources/org/apache/commons/collections4/properties/test.properties")) {
            sortedProperties.load(reader);
        }
        assertAscendingOrder(sortedProperties);
    }

    @Test
    void testLoadOrderedKeysReverse() throws IOException {
        loadOrderedKeysReverse();
    }

    @Test
    void testMerge() {
        final SortedProperties sortedProperties = new SortedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            sortedProperties.merge("key" + i, "value" + i, (k, v) -> v);
        }
        assertAscendingOrder(sortedProperties);
        sortedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            sortedProperties.merge("key" + i, "value" + i, (k, v) -> v);
        }
        assertAscendingOrder(sortedProperties);
    }

    @Test
    void testPut() {
        final SortedProperties sortedProperties = new SortedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            sortedProperties.put("key" + i, "value" + i);
        }
        assertAscendingOrder(sortedProperties);
        sortedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            sortedProperties.put("key" + i, "value" + i);
        }
        assertAscendingOrder(sortedProperties);
    }

    @Test
    void testPutAll() {
        final SortedProperties sourceProperties = new SortedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            sourceProperties.put("key" + i, "value" + i);
        }
        final SortedProperties sortedProperties = new SortedProperties();
        sortedProperties.putAll(sourceProperties);
        assertAscendingOrder(sortedProperties);
        sortedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            sortedProperties.put("key" + i, "value" + i);
        }
        assertAscendingOrder(sortedProperties);
    }

    @Test
    void testPutIfAbsent() {
        final SortedProperties sortedProperties = new SortedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            sortedProperties.putIfAbsent("key" + i, "value" + i);
        }
        assertAscendingOrder(sortedProperties);
        sortedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            sortedProperties.putIfAbsent("key" + i, "value" + i);
        }
        assertAscendingOrder(sortedProperties);
    }

    @Test
    void testRemoveKey() throws FileNotFoundException, IOException {
        final SortedProperties props = loadOrderedKeysReverse();
        final String k = "key1";
        props.remove(k);
        assertFalse(props.contains(k));
        assertFalse(props.containsKey(k));
        assertFalse(Collections.list(props.keys()).contains(k));
        assertFalse(Collections.list(props.propertyNames()).contains(k));
    }

    @Test
    void testRemoveKeyValue() throws FileNotFoundException, IOException {
        final SortedProperties props = loadOrderedKeysReverse();
        final String k = "key1";
        props.remove(k, "value1");
        assertFalse(props.contains(k));
        assertFalse(props.containsKey(k));
        assertFalse(Collections.list(props.keys()).contains(k));
        assertFalse(Collections.list(props.propertyNames()).contains(k));
    }

    @Test
    void testStringPropertyName() {
        final SortedProperties sortedProperties = new SortedProperties();
        assertTrue(sortedProperties.stringPropertyNames().isEmpty());
    }

    @Test
    void testToString() {
        final SortedProperties sortedProperties = new SortedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            sortedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        assertEquals(
                "{A=ValueA, B=ValueB, C=ValueC, D=ValueD, E=ValueE, F=ValueF, G=ValueG, H=ValueH, I=ValueI, J=ValueJ, K=ValueK, L=ValueL, M=ValueM, N=ValueN, O=ValueO, P=ValueP, Q=ValueQ, R=ValueR, S=ValueS, T=ValueT, U=ValueU, V=ValueV, W=ValueW, X=ValueX, Y=ValueY, Z=ValueZ}",
                sortedProperties.toString());
    }

}
