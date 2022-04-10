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

import static org.apache.commons.collections4.AbstractObjectTestUtils.getCanonicalEmptyCollectionName;
import static org.apache.commons.collections4.AbstractObjectTestUtils.getCanonicalFullCollectionName;
import static org.apache.commons.collections4.AbstractObjectTestUtils.readExternalFormFromBytes;
import static org.apache.commons.collections4.AbstractObjectTestUtils.serializeDeserialize;
import static org.apache.commons.collections4.AbstractObjectTestUtils.writeExternalFormToBytes;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

/**
 * Abstract test class for {@link java.lang.Object} methods and contracts.
 * <p>
 * To use, simply extend this class, and implement
 * the {@link #makeObject()} method.
 * <p>
 * If your {@link Object} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Object} fails.
 */
public abstract class AbstractObjectTest extends BulkTest implements AbstractObjectTestInterface {

    /** Current major release for Collections */
    public static final int COLLECTIONS_MAJOR_VERSION = 4;

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractObjectTest(final String testName) {
        super(testName);
    }

    /**
     * Implement this method to return the object to test.
     *
     * @return the object to test
     */
    public abstract Object makeObject();

    /**
     * Override this method if a subclass is testing an object
     * that cannot serialize an "empty" Collection.
     * (e.g. Comparators have no contents)
     *
     * @return true
     */
    public boolean supportsEmptyCollections() {
        return true;
    }

    /**
     * Override this method if a subclass is testing an object
     * that cannot serialize a "full" Collection.
     * (e.g. Comparators have no contents)
     *
     * @return true
     */
    public boolean supportsFullCollections() {
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
     * Returns true to indicate that the collection supports equals() comparisons.
     * This implementation returns true;
     */
    public boolean isEqualsCheckable() {
        return true;
    }

    @Test
    public void testObjectEqualsSelf() {
        final Object obj = makeObject();
        assertEquals("A Object should equal itself", obj, obj);
    }

    @Test
    public void testEqualsNull() {
        final Object obj = makeObject();
        assertFalse(obj.equals(null)); // make sure this doesn't throw NPE either
    }

    @Test
    public void testObjectHashCodeEqualsSelfHashCode() {
        final Object obj = makeObject();
        assertEquals("hashCode should be repeatable", obj.hashCode(), obj.hashCode());
    }

    @Test
    public void testObjectHashCodeEqualsContract() {
        final Object obj1 = makeObject();
        if (obj1.equals(obj1)) {
            assertEquals(
                "[1] When two objects are equal, their hashCodes should be also.",
                obj1.hashCode(), obj1.hashCode());
        }
        final Object obj2 = makeObject();
        if (obj1.equals(obj2)) {
            assertEquals(
                "[2] When two objects are equal, their hashCodes should be also.",
                obj1.hashCode(), obj2.hashCode());
            assertEquals("When obj1.equals(obj2) is true, then obj2.equals(obj1) should also be true", obj2, obj1);
        }
    }

    @Test
    public void testSerializeDeserializeThenCompare() throws Exception {
        final Object obj = makeObject();
        if (obj instanceof Serializable && isTestSerialization()) {
            final Object dest = serializeDeserialize(obj);
            if (isEqualsCheckable()) {
                assertEquals("obj != deserialize(serialize(obj))", obj, dest);
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
            final byte[] objekt = writeExternalFormToBytes((Serializable) o);
            readExternalFormFromBytes(objekt);
        }
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
                final String name = getCanonicalEmptyCollectionName(object, this);
                assertTrue(
                    "Canonical empty collection (" + name + ") is not in SCM",
                    new File(name).exists());
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
                final String name = getCanonicalFullCollectionName(object, this);
                assertTrue(
                    "Canonical full collection (" + name + ") is not in SCM",
                    new File(name).exists());
            }
        }
    }

    // protected implementation
    /**
     * Get the version of Collections that this object tries to
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
    @Override
    public String getCompatibilityVersion() {
        return "4";
    }

    protected boolean skipSerializedCanonicalTests() {
        return Boolean.getBoolean("org.apache.commons.collections:with-clover");
    }

}
