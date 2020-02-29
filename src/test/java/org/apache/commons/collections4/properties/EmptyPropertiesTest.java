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
import org.junit.Assert;
import org.junit.Test;

public class EmptyPropertiesTest {

    @Test
    public void testClear() {
        PropertiesFactory.EMPTY_PROPERTIES.clear();
        Assert.assertEquals(0, PropertiesFactory.EMPTY_PROPERTIES.size());
    }

    @Test
    public void testClone() {
        // TODO Better test?
        PropertiesFactory.EMPTY_PROPERTIES.clone();
        Assert.assertEquals(0, PropertiesFactory.EMPTY_PROPERTIES.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCompute() {
        PropertiesFactory.EMPTY_PROPERTIES.compute("key", (k, v) -> "foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testComputeIfAbsent() {
        PropertiesFactory.EMPTY_PROPERTIES.computeIfAbsent("key", k -> "foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testComputeIfPresent() {
        PropertiesFactory.EMPTY_PROPERTIES.computeIfPresent("key", (k, v) -> "foo");
    }

    @Test
    public void testContains() {
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.contains("foo"));
    }

    @Test
    public void testContainsKey() {
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.containsKey("foo"));
    }

    @Test
    public void testContainsValue() {
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.containsValue("foo"));
    }

    @Test
    public void testElements() {
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.elements().hasMoreElements());
    }

    @Test
    public void testEntrySet() {
        Assert.assertTrue(PropertiesFactory.EMPTY_PROPERTIES.entrySet().isEmpty());
    }

    @Test
    public void testEquals() {
        Assert.assertTrue(PropertiesFactory.EMPTY_PROPERTIES.equals(PropertiesFactory.EMPTY_PROPERTIES));
        Assert.assertTrue(PropertiesFactory.EMPTY_PROPERTIES.equals(new Properties()));
        Assert.assertTrue(new Properties().equals(PropertiesFactory.EMPTY_PROPERTIES));
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.equals(null));
        final Properties p = new Properties();
        p.put("Key", "Value");
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.equals(p));
        Assert.assertFalse(p.equals(PropertiesFactory.EMPTY_PROPERTIES));
    }

    public void testForEach() {
        PropertiesFactory.EMPTY_PROPERTIES.forEach((k, v) -> Assert.fail());
    }

    @Test
    public void testGet() {
        Assert.assertNull(PropertiesFactory.EMPTY_PROPERTIES.get("foo"));
    }

    @Test
    public void testGetOrDefault() {
        Assert.assertEquals("bar", PropertiesFactory.EMPTY_PROPERTIES.getOrDefault("foo", "bar"));
    }

    @Test
    public void testGetProperty() {
        Assert.assertNull(PropertiesFactory.EMPTY_PROPERTIES.getProperty("foo"));
    }

    @Test
    public void testGetPropertyDefault() {
        Assert.assertEquals("bar", PropertiesFactory.EMPTY_PROPERTIES.getProperty("foo", "bar"));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(PropertiesFactory.EMPTY_PROPERTIES.hashCode(),
            PropertiesFactory.EMPTY_PROPERTIES.hashCode());
        // Should be equals?
        // Assert.assertEquals(PropertiesFactory.EMPTY_PROPERTIES.hashCode(), new Properties().hashCode());
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(PropertiesFactory.EMPTY_PROPERTIES.isEmpty());
    }

    @Test
    public void testKeys() {
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.keys().hasMoreElements());
    }

    @Test
    public void testKeySet() {
        Assert.assertTrue(PropertiesFactory.EMPTY_PROPERTIES.keySet().isEmpty());
    }

    @Test
    public void testListToPrintStream() {
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.list(new PrintStream(actual));
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().list(new PrintStream(expected));
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().list(new PrintStream(expected));
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testListToPrintWriter() {
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.list(new PrintWriter(actual));
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().list(new PrintWriter(expected));
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().list(new PrintWriter(expected));
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoadFromXML() throws IOException {
        PropertiesFactory.EMPTY_PROPERTIES.loadFromXML(new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoadInputStream() throws IOException {
        PropertiesFactory.EMPTY_PROPERTIES.load(new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoadReader() throws IOException {
        try (NullReader reader = new NullReader(0)) {
            PropertiesFactory.EMPTY_PROPERTIES.load(reader);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMerge() {
        PropertiesFactory.EMPTY_PROPERTIES.merge("key", "value", (k, v) -> "foo");
    }

    @Test
    public void testPropertyName() {
        Assert.assertFalse(PropertiesFactory.EMPTY_PROPERTIES.propertyNames().hasMoreElements());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPut() {
        PropertiesFactory.EMPTY_PROPERTIES.put("Key", "Value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutAll() {
        PropertiesFactory.EMPTY_PROPERTIES.putAll(new HashMap<>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutIfAbsent() {
        PropertiesFactory.EMPTY_PROPERTIES.putIfAbsent("Key", "Value");
    }

    @Test
    public void testRehash() {
        // Can't really test without extending and casting to a currently private class
        // PropertiesFactory.EMPTY_PROPERTIES.rehash();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        PropertiesFactory.EMPTY_PROPERTIES.remove("key", "value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveKey() {
        PropertiesFactory.EMPTY_PROPERTIES.remove("key");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReplace() {
        PropertiesFactory.EMPTY_PROPERTIES.replace("key", "value1");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReplaceAll() {
        PropertiesFactory.EMPTY_PROPERTIES.replaceAll((k, v) -> "value1");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReplaceOldValue() {
        PropertiesFactory.EMPTY_PROPERTIES.replace("key", "value1", "value2");
    }

    @Test
    public void testSave() {
        final String comments = "Hello world!";
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        PropertiesFactory.EMPTY_PROPERTIES.save(new PrintStream(actual), comments);
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        PropertiesFactory.INSTANCE.createProperties().save(new PrintStream(expected), comments);
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().save(new PrintStream(expected), comments);
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetProperty() {
        PropertiesFactory.EMPTY_PROPERTIES.setProperty("Key", "Value");
    }

    @Test
    public void testSize() {
        Assert.assertEquals(0, PropertiesFactory.EMPTY_PROPERTIES.size());
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
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().store(new PrintStream(expected), comments);
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
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
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().store(new PrintWriter(expected), comments);
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
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
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().storeToXML(new PrintStream(expected), comments);
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
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
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        expected.reset();
        new Properties().storeToXML(new PrintStream(expected), comments, encoding);
        Assert.assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    public void testStringPropertyName() {
        Assert.assertTrue(PropertiesFactory.EMPTY_PROPERTIES.stringPropertyNames().isEmpty());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(new Properties().toString(), PropertiesFactory.EMPTY_PROPERTIES.toString());
    }

    @Test
    public void testValues() {
        Assert.assertTrue(PropertiesFactory.EMPTY_PROPERTIES.values().isEmpty());
    }
}
