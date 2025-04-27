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
package org.apache.commons.collections4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Object}.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject()} method.
 * <p>
 * If your {@link Object} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Object} fails.
 */
public abstract class AbstractObjectTest extends BulkTest {

    /** Current major release for Collections */
    public static final int COLLECTIONS_MAJOR_VERSION = 4;

    protected String getCanonicalEmptyCollectionName(final Object object) {
        final StringBuilder retval = new StringBuilder();
        retval.append(TEST_DATA_PATH);
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".") + 1);
        retval.append(colName);
        retval.append(".emptyCollection.version");
        retval.append(getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    protected String getCanonicalFullCollectionName(final Object object) {
        final StringBuilder retval = new StringBuilder();
        retval.append(TEST_DATA_PATH);
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".") + 1);
        retval.append(colName);
        retval.append(".fullCollection.version");
        retval.append(getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    // protected implementation
    /**
     * Gets the version of Collections that this object tries to
     * maintain serialization compatibility with. Defaults to 4, due to
     * the package change to collections4 introduced in version 4.
     *
     * This constant makes it possible for TestMap (and other subclasses,
     * if necessary) to automatically check SCM for a versionX copy of a
     * Serialized object, so we can make sure that compatibility is maintained.
     * See, for example, TestMap.getCanonicalFullMapName(Map map).
     * Subclasses can override this variable, indicating compatibility
     * with earlier Collections versions.
     *
     * @return The version, or {@code null} if this object shouldn't be
     * tested for compatibility with previous versions.
     */
    public String getCompatibilityVersion() {
        return "4";
    }

    /**
     * Returns true to indicate that the collection supports equals() comparisons.
     * This implementation returns true;
     */
    public boolean isEqualsCheckable() {
        return true;
    }

    /**
     * Is serialization testing supported.
     * Default is true.
     */
    public boolean isTestSerialization() {
        return true;
    }

    /**
     * Implement this method to return the object to test.
     *
     * @return the object to test
     */
    public abstract Object makeObject();

    /**
     * Reads a Serialized or Externalized Object from bytes.
     * Useful for verifying serialization in memory.
     *
     * @param b byte array containing a serialized Object
     * @return Object contained in the bytes
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected Object readExternalFormFromBytes(final byte[] b) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(b);
        return readExternalFormFromStream(stream);
    }

    /**
     * Reads a Serialized or Externalized Object from disk.
     * Useful for creating compatibility tests between
     * different SCM versions of the same class
     *
     * @param path path to the serialized Object
     * @return the Object at the given path
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected Object readExternalFormFromDisk(final String path) throws IOException, ClassNotFoundException {
        try (InputStream stream = Files.newInputStream(Paths.get(path))) {
            return readExternalFormFromStream(stream);
        }
    }

    // private implementation
    private Object readExternalFormFromStream(final InputStream stream) throws IOException, ClassNotFoundException {
        final ObjectInputStream oStream = new ObjectInputStream(stream);
        return oStream.readObject();
    }

    protected Object serializeDeserialize(final Object obj) throws Exception {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(buffer);
        out.writeObject(obj);
        out.close();

        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        final Object dest = in.readObject();
        in.close();

        return dest;
    }

    protected boolean skipSerializedCanonicalTests() {
        return Boolean.getBoolean("org.apache.commons.collections:with-clover");
    }

    /**
     * Override this method if a subclass is testing an object
     * that cannot serialize an "empty" Collection.
     * (for example Comparators have no contents)
     *
     * @return true
     */
    public boolean supportsEmptyCollections() {
        return true;
    }

    /**
     * Override this method if a subclass is testing an object
     * that cannot serialize a "full" Collection.
     * (for example Comparators have no contents)
     *
     * @return true
     */
    public boolean supportsFullCollections() {
        return true;
    }

    /**
     * Tests serialization by comparing against a previously stored version in SCM.
     * If the test object is serializable, confirm that a canonical form exists.
     */
    @Test
    public void testCanonicalEmptyCollectionExists() {
        if (supportsEmptyCollections() && isTestSerialization() && !skipSerializedCanonicalTests()) {
            final Object object = makeObject();
            if (object instanceof Serializable) {
                final String name = getCanonicalEmptyCollectionName(object);
                assertTrue(
                        new File(name).exists(),
                        "Canonical empty collection (" + name + ") is not in SCM");
            }
        }
    }

    /**
     * Tests serialization by comparing against a previously stored version in SCM.
     * If the test object is serializable, confirm that a canonical form exists.
     */
    @Test
    public void testCanonicalFullCollectionExists() {
        if (supportsFullCollections() && isTestSerialization() && !skipSerializedCanonicalTests()) {
            final Object object = makeObject();
            if (object instanceof Serializable) {
                final String name = getCanonicalFullCollectionName(object);
                assertTrue(
                        new File(name).exists(),
                        "Canonical full collection (" + name + ") is not in SCM");
            }
        }
    }

    @Test
    public void testEqualsNull() {
        final Object obj = makeObject();
        assertFalse(obj.equals(null)); // make sure this doesn't throw NPE either
    }

    @Test
    public void testObjectEqualsSelf() {
        final Object obj = makeObject();
        assertEquals(obj, obj, "A Object should equal itself");
    }

    @Test
    public void testObjectHashCodeEqualsContract() {
        final Object obj1 = makeObject();
        if (obj1.equals(obj1)) {
            assertEquals(
                    obj1.hashCode(), obj1.hashCode(),
                    "[1] When two objects are equal, their hashCodes should be also.");
        }
        final Object obj2 = makeObject();
        if (obj1.equals(obj2)) {
            assertEquals(
                    obj1.hashCode(), obj2.hashCode(),
                    "[2] When two objects are equal, their hashCodes should be also.");
            assertEquals(obj2, obj1, "When obj1.equals(obj2) is true, then obj2.equals(obj1) should also be true");
        }
    }

    @Test
    public void testObjectHashCodeEqualsSelfHashCode() {
        final Object obj = makeObject();
        assertEquals(obj.hashCode(), obj.hashCode(), "hashCode should be repeatable");
    }

    @Test
    public void testSerializeDeserializeThenCompare() throws Exception {
        final Object obj = makeObject();
        if (obj instanceof Serializable && isTestSerialization()) {
            final Object dest = serializeDeserialize(obj);
            if (isEqualsCheckable()) {
                assertEquals(obj, dest, "obj != deserialize(serialize(obj))");
            }
        }
    }

    /**
     * Sanity check method, makes sure that any Serializable
     * class can be serialized and de-serialized in memory,
     * using the handy makeObject() method
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void testSimpleSerialization() throws Exception {
        final Object o = makeObject();
        if (o instanceof Serializable && isTestSerialization()) {
            final byte[] object = writeExternalFormToBytes((Serializable) o);
            readExternalFormFromBytes(object);
        }
    }

    /**
     * Converts a Serializable or Externalizable object to
     * bytes.  Useful for in-memory tests of serialization
     *
     * @param o Object to convert to bytes
     * @return serialized form of the Object
     * @throws IOException
     */
    protected byte[] writeExternalFormToBytes(final Serializable o) throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        writeExternalFormToStream(o, byteStream);
        return byteStream.toByteArray();
    }

    /**
     * Writes a Serializable or Externalizable object as
     * a file at the given path.  NOT USEFUL as part
     * of a unit test; this is just a utility method
     * for creating disk-based objects in SCM that can become
     * the basis for compatibility tests using
     * readExternalFormFromDisk(String path)
     *
     * @param o Object to serialize
     * @param path path to write the serialized Object
     * @throws IOException
     */
    protected void writeExternalFormToDisk(final Serializable o, final String path) throws IOException {
        try (FileOutputStream fileStream = new FileOutputStream(path)) {
            writeExternalFormToStream(o, fileStream);
        }
    }

    private void writeExternalFormToStream(final Serializable o, final OutputStream stream) throws IOException {
        final ObjectOutputStream oStream = new ObjectOutputStream(stream);
        oStream.writeObject(o);
    }

}
