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

package org.apache.commons.collections4.properties;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.input.NullReader;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

public class EmptyPropertiesTest {

    @Test
    public void testClear() {
        PropertiesFactory.EMPTY_PROPERTIES.clear();
        assertEquals(0, PropertiesFactory.EMPTY_PROPERTIES.size());
    }

    @Test
    public void testClone() {
        // TODO Better test?
        PropertiesFactory.EMPTY_PROPERTIES.clone();
        assertEquals(0, PropertiesFactory.EMPTY_PROPERTIES.size());
    }

    @Test
    public void testCompute() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.compute("key", (k, v) -> "foo");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testComputeIfAbsent() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.computeIfAbsent("key", k -> "foo");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testComputeIfPresent() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.computeIfPresent("key", (k, v) -> "foo");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testContains() {
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.contains("foo"));
    }

    @Test
    public void testContainsKey() {
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.containsKey("foo"));
    }

    @Test
    public void testContainsValue() {
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.containsValue("foo"));
    }

    @Test
    public void testElements() {
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.elements().hasMoreElements());
    }

    @Test
    public void testEntrySet() {
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.entrySet().isEmpty());
    }

    @Test
    public void testEquals() {
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.equals(PropertiesFactory.EMPTY_PROPERTIES));
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.equals(new Properties()));
        assertTrue(new Properties().equals(PropertiesFactory.EMPTY_PROPERTIES));
        assertNotEquals(PropertiesFactory.EMPTY_PROPERTIES, null);
        final Properties p = new Properties();
        p.put("Key", "Value");
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.equals(p));
        assertFalse(p.equals(PropertiesFactory.EMPTY_PROPERTIES));
    }

    @Test
    public void testForEach() {
        PropertiesFactory.EMPTY_PROPERTIES.forEach((k, v) -> fail());
    }

    @Test
    public void testForEachWithNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.forEach(null);
        });
        assertNull(exception.getMessage());
    }
    @Test
    public void testGet() {
        assertNull(PropertiesFactory.EMPTY_PROPERTIES.get("foo"));
    }

    @Test
    public void testGetOrDefault() {
        assertEquals("bar", PropertiesFactory.EMPTY_PROPERTIES.getOrDefault("foo", "bar"));
    }

    @Test
    public void testGetProperty() {
        assertNull(PropertiesFactory.EMPTY_PROPERTIES.getProperty("foo"));
    }

    @Test
    public void testGetPropertyDefault() {
        assertEquals("bar", PropertiesFactory.EMPTY_PROPERTIES.getProperty("foo", "bar"));
    }

    @Test
    public void testHashCode() {
        assertEquals(PropertiesFactory.EMPTY_PROPERTIES.hashCode(),
            PropertiesFactory.EMPTY_PROPERTIES.hashCode());
        // Should be equals?
        // assertEquals(PropertiesFactory.EMPTY_PROPERTIES.hashCode(), new Properties().hashCode());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.isEmpty());
    }

    @Test
    public void testKeys() {
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.keys().hasMoreElements());
    }

    @Test
    public void testKeySet() {
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.keySet().isEmpty());
    }

    @Test
    public void testListToPrintStream() {
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.list(new PrintStream(actual));
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().list(new PrintStream(expected));
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().list(new PrintStream(expected));
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testListToPrintWriter() {
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.list(new PrintWriter(actual));
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().list(new PrintWriter(expected));
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().list(new PrintWriter(expected));
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testLoadFromXML() throws IOException {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.loadFromXML(new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY));
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testLoadInputStream() throws IOException {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.load(new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY));
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testLoadReader() throws IOException {
        try (NullReader reader = new NullReader(0)) {
            Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
                PropertiesFactory.EMPTY_PROPERTIES.load(reader);
            });
            assertNull(exception.getMessage());
        }
    }

    @Test
    public void testMerge() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.merge("key", "value", (k, v) -> "foo");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testPropertyName() {
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.propertyNames().hasMoreElements());
    }

    @Test
    public void testPut() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.put("Key", "Value");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testPutAll() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.putAll(new HashMap<>());
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testPutIfAbsent() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.putIfAbsent("Key", "Value");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testRehash() {
        // Can't really test without extending and casting to a currently private class
        //PropertiesFactory.EMPTY_PROPERTIES.rehash();
    }

    @Test
    public void testRemove() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.remove("key", "value");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testRemoveKey() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.remove("key");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testReplace() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.replace("key", "value1");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testReplaceAll() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.replaceAll((k, v) -> "value1");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testReplaceOldValue() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.replace("key", "value1", "value2");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testSave() throws IOException {
        final String comments = "Hello world!";
        // actual
        try (ByteArrayOutputStream actual = new ByteArrayOutputStream()) {
            try (PrintStream out = new PrintStream(actual)) {
                PropertiesFactory.EMPTY_PROPERTIES.save(out, comments);
            }
            // expected
            try (ByteArrayOutputStream expected = new ByteArrayOutputStream()) {
                try (PrintStream out = new PrintStream(expected)) {
                    PropertiesFactory.INSTANCE.createProperties().save(out, comments);
                }
                assertArrayEquals(expected.toByteArray(), actual.toByteArray());
                expected.reset();
                try (PrintStream out = new PrintStream(expected)) {
                    new Properties().save(out, comments);
                }
                assertArrayEquals(expected.toByteArray(), actual.toByteArray());
            }
        }
    }

    @Test
    public void testSetProperty() {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            PropertiesFactory.EMPTY_PROPERTIES.setProperty("Key", "Value");
        });
        assertNull(exception.getMessage());
    }

    @Test
    public void testSize() {
        assertEquals(0, PropertiesFactory.EMPTY_PROPERTIES.size());
    }

    @Test
    public void testStoreToOutputStream() throws IOException {
        final String comments = "Hello world!";
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.store(new PrintStream(actual), comments);
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().store(new PrintStream(expected), comments);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().store(new PrintStream(expected), comments);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testStoreToPrintWriter() throws IOException {
        final String comments = "Hello world!";
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.store(new PrintWriter(actual), comments);
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().store(new PrintWriter(expected), comments);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().store(new PrintWriter(expected), comments);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testStoreToXMLOutputStream() throws IOException {
        final String comments = "Hello world!";
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.storeToXML(new PrintStream(actual), comments);
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().storeToXML(new PrintStream(expected), comments);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().storeToXML(new PrintStream(expected), comments);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testStoreToXMLOutputStreamWithEncoding() throws IOException {
        final String comments = "Hello world!";
        final String encoding = StandardCharsets.UTF_8.name();
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.storeToXML(new PrintStream(actual), comments, encoding);
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().storeToXML(new PrintStream(expected), comments, encoding);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().storeToXML(new PrintStream(expected), comments, encoding);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testStringPropertyName() {
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.stringPropertyNames().isEmpty());
    }

    @Test
    public void testToString() {
        assertEquals(new Properties().toString(), PropertiesFactory.EMPTY_PROPERTIES.toString());
    }

    @Test
    public void testValues() {
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.values().isEmpty());
    }
}
