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
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;
import org.apache.commons.io.input.NullReader;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

public class EmptyPropertiesTest {

    /**
     * Returns the first line from multi-lined string separated by a line separator character
     *
     * @param x the multi-lined String
     * @return the first line from x
     */
    private String getFirstLine(final String x) {
        return x.split("\\R", 2)[0];
    }

    private PrintStream newPrintStream(final ByteArrayOutputStream baos) throws UnsupportedEncodingException {
        return new PrintStream(baos, true, StandardCharsets.UTF_8.name());
    }

    private String removeLine2(final ByteArrayOutputStream baos) {
        return removeLine2(toString(baos));
    }

    private String removeLine2(final String x) {
        final String[] s = x.split("\\R", 2);
        return s[0] + System.lineSeparator() + (s.length > 2 ? s[2] : "");
    }

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
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.compute("key", (k, v) -> "foo"));
    }

    @Test
    public void testComputeIfAbsent() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.computeIfAbsent("key", k -> "foo"));
    }

    @Test
    public void testComputeIfPresent() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.computeIfPresent("key", (k, v) -> "foo"));
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
        assertEquals(PropertiesFactory.EMPTY_PROPERTIES, PropertiesFactory.EMPTY_PROPERTIES);
        assertEquals(PropertiesFactory.EMPTY_PROPERTIES, new Properties());
        assertEquals(new Properties(), PropertiesFactory.EMPTY_PROPERTIES);
        assertNotEquals(null, PropertiesFactory.EMPTY_PROPERTIES);
        final Properties p = new Properties();
        p.put("Key", "Value");
        assertNotEquals(PropertiesFactory.EMPTY_PROPERTIES, p);
        assertNotEquals(p, PropertiesFactory.EMPTY_PROPERTIES);
    }

    @Test
    public void testForEach() {
        PropertiesFactory.EMPTY_PROPERTIES.forEach((k, v) -> fail());
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
        assertEquals(PropertiesFactory.EMPTY_PROPERTIES.hashCode(), PropertiesFactory.EMPTY_PROPERTIES.hashCode());
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
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.isEmpty());
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
        assertThrows(UnsupportedOperationException.class,
            () -> PropertiesFactory.EMPTY_PROPERTIES.loadFromXML(new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY)));
    }

    @Test
    public void testLoadInputStream() throws IOException {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.load(new ByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY)));
    }

    @Test
    public void testLoadReader() throws IOException {
        try (NullReader reader = new NullReader(0)) {
            assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.load(reader));
        }
    }

    @Test
    public void testMerge() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.merge("key", "value", (k, v) -> "foo"));
    }

    @Test
    public void testPropertyName() {
        assertFalse(PropertiesFactory.EMPTY_PROPERTIES.propertyNames().hasMoreElements());
    }

    @Test
    public void testPut() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.put("Key", "Value"));
    }

    @Test
    public void testPutAll() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.putAll(new HashMap<>()));
    }

    @Test
    public void testPutIfAbsent() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.putIfAbsent("Key", "Value"));
    }

    @Test
    public void testRehash() {
        // Can't really test without extending and casting to a currently private class
        // PropertiesFactory.EMPTY_PROPERTIES.rehash();
    }

    @Test
    public void testRemove() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.remove("key", "value"));
    }

    @Test
    public void testRemoveKey() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.remove("key"));
    }

    @Test
    public void testReplace() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.replace("key", "value1"));
    }

    @Test
    public void testReplaceAll() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.replaceAll((k, v) -> "value1"));
    }

    @Test
    public void testReplaceOldValue() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.replace("key", "value1", "value2"));
    }

    @Test
    public void testSave() throws IOException {
        final String comments = "Hello world!";
        try (ByteArrayOutputStream actual = new ByteArrayOutputStream(); ByteArrayOutputStream expected = new ByteArrayOutputStream()) {
            // actual
            PropertiesFactory.EMPTY_PROPERTIES.store(actual, comments);
            // expected
            PropertiesFactory.INSTANCE.createProperties().store(expected, comments);

            // Properties.store stores the specified comment appended with current time stamp in the next line
            final String expectedComment = getFirstLine(expected.toString("UTF-8"));
            final String actualComment = getFirstLine(actual.toString("UTF-8"));
            assertEquals(expectedComment, actualComment,
                () -> String.format("Expected String '%s' with length '%s'", expectedComment, expectedComment.length()));
            expected.reset();
            try (PrintStream out = new PrintStream(expected)) {
                new Properties().store(out, comments);
            }
            final String[] expectedLines = expected.toString(StandardCharsets.UTF_8.displayName()).split("\\n");
            final String[] actualLines = actual.toString(StandardCharsets.UTF_8.displayName()).split("\\n");
            assertEquals(expectedLines.length, actualLines.length);
            // The assertion below checks that the comment is the same in both files
            assertEquals(expectedLines[0], actualLines[0]);
            // N.B.: We must not expect expectedLines[1] and actualLines[1] to have the same value as
            // it contains the timestamp of when the data was written to the stream, which makes
            // this test brittle, causing intermitent failures, see COLLECTIONS-812
        }
    }

    @Test
    public void testSetProperty() {
        assertThrows(UnsupportedOperationException.class, () -> PropertiesFactory.EMPTY_PROPERTIES.setProperty("Key", "Value"));
    }

    @Test
    public void testSize() {
        assertEquals(0, PropertiesFactory.EMPTY_PROPERTIES.size());
    }

    @Test
    public void testStoreToOutputStream() throws IOException {
        // Note: The second line is always a comment with a timestamp.
        final String comments = "Hello world!";
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(actual)) {
            PropertiesFactory.EMPTY_PROPERTIES.store(ps, comments);
        }
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(expected)) {
            PropertiesFactory.INSTANCE.createProperties().store(ps, comments);
        }
        assertEquals(removeLine2(expected), removeLine2(actual));
        expected.reset();
        try (PrintStream ps = newPrintStream(expected)) {
            new Properties().store(ps, comments);
        }
        assertEquals(removeLine2(expected), removeLine2(actual), () -> removeLine2(actual));
    }

    @Test
    public void testStoreToPrintWriter() throws IOException {
        // Note: The second line is always a comment with a timestamp.
        final String comments = "Hello world!";
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(actual)) {
            PropertiesFactory.EMPTY_PROPERTIES.store(ps, comments);
        }
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(expected)) {
            PropertiesFactory.INSTANCE.createProperties().store(ps, comments);
        }
        assertEquals(removeLine2(expected), removeLine2(actual));
        expected.reset();
        try (PrintStream ps = newPrintStream(expected)) {
            new Properties().store(ps, comments);
        }
        assertEquals(removeLine2(expected), removeLine2(actual));
    }

    @Test
    public void testStoreToXMLOutputStream() throws IOException {
        // Note: The second line is always a comment with a timestamp.
        final String comments = "Hello world!";
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(actual)) {
            PropertiesFactory.EMPTY_PROPERTIES.storeToXML(ps, comments);
        }
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(expected)) {
            PropertiesFactory.INSTANCE.createProperties().storeToXML(ps, comments);
        }
        assertEquals(toString(expected), toString(actual));
        expected.reset();
        try (PrintStream ps = new PrintStream(expected)) {
            new Properties().storeToXML(ps, comments);
        }
        assertEquals(removeLine2(expected), removeLine2(actual));
    }

    @Test
    public void testStoreToXMLOutputStreamWithEncoding() throws IOException {
        // Note: The second line is always a comment with a timestamp.
        final String comments = "Hello world!";
        final String encoding = StandardCharsets.UTF_8.name();
        // actual
        final ByteArrayOutputStream actual = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(actual)) {
            PropertiesFactory.EMPTY_PROPERTIES.storeToXML(ps, comments, encoding);
        }
        // expected
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        try (PrintStream ps = newPrintStream(expected)) {
            PropertiesFactory.INSTANCE.createProperties().storeToXML(ps, comments, encoding);
        }
        assertEquals(removeLine2(expected), removeLine2(actual));
        expected.reset();
        try (PrintStream ps = newPrintStream(expected)) {
            new Properties().storeToXML(ps, comments, encoding);
        }
        assertEquals(removeLine2(expected), removeLine2(actual));
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
        assertTrue(PropertiesFactory.EMPTY_PROPERTIES.isEmpty());
    }

    private String toString(final ByteArrayOutputStream expected) {
        return new String(expected.toByteArray(), StandardCharsets.UTF_8);
    }
}
