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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link OrderedProperties}.
 */
class OrderedPropertiesTest {

    private void assertAscendingOrder(final OrderedProperties orderedProperties) {
        final int first = 1;
        final int last = 11;
        final Enumeration<Object> enumObjects = orderedProperties.keys();
        for (int i = first; i <= last; i++) {
            assertEquals("key" + i, enumObjects.nextElement());
        }
        final Iterator<Object> iterSet = orderedProperties.keySet().iterator();
        for (int i = first; i <= last; i++) {
            assertEquals("key" + i, iterSet.next());
        }
        final Iterator<Entry<Object, Object>> iterEntrySet = orderedProperties.entrySet().iterator();
        for (int i = first; i <= last; i++) {
            final Entry<Object, Object> next = iterEntrySet.next();
            assertEquals("key" + i, next.getKey());
            assertEquals("value" + i, next.getValue());
        }
        final Enumeration<?> propertyNames = orderedProperties.propertyNames();
        for (int i = first; i <= last; i++) {
            assertEquals("key" + i, propertyNames.nextElement());
        }
    }

    private OrderedProperties assertDescendingOrder(final OrderedProperties orderedProperties) {
        final int first = 11;
        final int last = 1;
        final Enumeration<Object> enumObjects = orderedProperties.keys();
        for (int i = first; i <= last; i--) {
            assertEquals("key" + i, enumObjects.nextElement());
        }
        final Iterator<Object> iterSet = orderedProperties.keySet().iterator();
        for (int i = first; i <= last; i--) {
            assertEquals("key" + i, iterSet.next());
        }
        final Iterator<Entry<Object, Object>> iterEntrySet = orderedProperties.entrySet().iterator();
        for (int i = first; i <= last; i--) {
            final Entry<Object, Object> next = iterEntrySet.next();
            assertEquals("key" + i, next.getKey());
            assertEquals("value" + i, next.getValue());
        }
        final Enumeration<?> propertyNames = orderedProperties.propertyNames();
        for (int i = first; i <= last; i--) {
            assertEquals("key" + i, propertyNames.nextElement());
        }
        return orderedProperties;
    }

    private OrderedProperties loadOrderedKeysReverse() throws FileNotFoundException, IOException {
        final OrderedProperties orderedProperties = new OrderedProperties();
        try (FileReader reader = new FileReader("src/test/resources/org/apache/commons/collections4/properties/test-reverse.properties")) {
            orderedProperties.load(reader);
        }
        return assertDescendingOrder(orderedProperties);
    }

    @Test
    void testCompute() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            final AtomicInteger aInt = new AtomicInteger(i);
            orderedProperties.compute("key" + i, (k, v) -> "value" + aInt.get());
        }
        assertAscendingOrder(orderedProperties);
        orderedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            final AtomicInteger aInt = new AtomicInteger(i);
            orderedProperties.compute("key" + i, (k, v) -> "value" + aInt.get());
        }
        assertDescendingOrder(orderedProperties);
    }

    @Test
    void testComputeIfAbsent() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            final AtomicInteger aInt = new AtomicInteger(i);
            orderedProperties.computeIfAbsent("key" + i, k -> "value" + aInt.get());
        }
        assertAscendingOrder(orderedProperties);
        orderedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            final AtomicInteger aInt = new AtomicInteger(i);
            orderedProperties.computeIfAbsent("key" + i, k -> "value" + aInt.get());
        }
        assertDescendingOrder(orderedProperties);
    }

    @Test
    void testEntrySet() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            orderedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Iterator<Map.Entry<Object, Object>> entries = orderedProperties.entrySet().iterator();
        for (char ch = first; ch <= last; ch++) {
            final Map.Entry<Object, Object> entry = entries.next();
            assertEquals(String.valueOf(ch), entry.getKey());
            assertEquals("Value" + ch, entry.getValue());
        }
    }

    @Test
    void testForEach() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            orderedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final AtomicInteger aCh = new AtomicInteger(first);
        orderedProperties.forEach((k, v) -> {
            final char ch = (char) aCh.getAndDecrement();
            assertEquals(String.valueOf(ch), k);
            assertEquals("Value" + ch, v);
        });
    }

    @Test
    void testKeys() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            orderedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        final Enumeration<Object> keys = orderedProperties.keys();
        for (char ch = first; ch <= last; ch++) {
            assertEquals(String.valueOf(ch), keys.nextElement());
        }
    }

    @Test
    void testLoadOrderedKeys() throws IOException {
        final OrderedProperties orderedProperties = new OrderedProperties();
        try (FileReader reader = new FileReader("src/test/resources/org/apache/commons/collections4/properties/test.properties")) {
            orderedProperties.load(reader);
        }
        assertAscendingOrder(orderedProperties);
    }

    @Test
    void testLoadOrderedKeysReverse() throws IOException {
        loadOrderedKeysReverse();
    }

    @Test
    void testMerge() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            orderedProperties.merge("key" + i, "value" + i, (k, v) -> v);
        }
        assertAscendingOrder(orderedProperties);
        orderedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            orderedProperties.merge("key" + i, "value" + i, (k, v) -> v);
        }
        assertDescendingOrder(orderedProperties);
    }

    @Test
    void testPut() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            orderedProperties.put("key" + i, "value" + i);
        }
        assertAscendingOrder(orderedProperties);
        orderedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            orderedProperties.put("key" + i, "value" + i);
        }
        assertDescendingOrder(orderedProperties);
    }

    @Test
    void testPutAll() {
        final OrderedProperties sourceProperties = new OrderedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            sourceProperties.put("key" + i, "value" + i);
        }
        final OrderedProperties orderedProperties = new OrderedProperties();
        orderedProperties.putAll(sourceProperties);
        assertAscendingOrder(orderedProperties);
        orderedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            orderedProperties.put("key" + i, "value" + i);
        }
        assertDescendingOrder(orderedProperties);
    }

    @Test
    void testPutIfAbsent() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        int first = 1;
        int last = 11;
        for (int i = first; i <= last; i++) {
            orderedProperties.putIfAbsent("key" + i, "value" + i);
        }
        assertAscendingOrder(orderedProperties);
        orderedProperties.clear();
        first = 11;
        last = 1;
        for (int i = first; i >= last; i--) {
            orderedProperties.putIfAbsent("key" + i, "value" + i);
        }
        assertDescendingOrder(orderedProperties);
    }

    @Test
    void testRemoveKey() throws FileNotFoundException, IOException {
        final OrderedProperties props = loadOrderedKeysReverse();
        final String k = "key1";
        props.remove(k);
        assertFalse(props.contains(k));
        assertFalse(props.containsKey(k));
        assertFalse(Collections.list(props.keys()).contains(k));
        assertFalse(Collections.list(props.propertyNames()).contains(k));
    }

    @Test
    void testRemoveKeyValue() throws FileNotFoundException, IOException {
        final OrderedProperties props = loadOrderedKeysReverse();
        final String k = "key1";
        props.remove(k, "value1");
        assertFalse(props.contains(k));
        assertFalse(props.containsKey(k));
        assertFalse(Collections.list(props.keys()).contains(k));
        assertFalse(Collections.list(props.propertyNames()).contains(k));
    }

    @Test
    void testToString() {
        final OrderedProperties orderedProperties = new OrderedProperties();
        final char first = 'Z';
        final char last = 'A';
        for (char ch = first; ch >= last; ch--) {
            orderedProperties.put(String.valueOf(ch), "Value" + ch);
        }
        assertEquals(
                "{Z=ValueZ, Y=ValueY, X=ValueX, W=ValueW, V=ValueV, U=ValueU, T=ValueT, S=ValueS, R=ValueR, Q=ValueQ, P=ValueP, O=ValueO, N=ValueN, M=ValueM, L=ValueL, K=ValueK, J=ValueJ, I=ValueI, H=ValueH, G=ValueG, F=ValueF, E=ValueE, D=ValueD, C=ValueC, B=ValueB, A=ValueA}",
                orderedProperties.toString());
    }
}
