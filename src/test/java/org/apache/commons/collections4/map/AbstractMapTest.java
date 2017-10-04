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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.apache.commons.collections4.set.AbstractSetTest;

/**
 * Abstract test class for {@link java.util.Map} methods and contracts.
 * <p>
 * The forces at work here are similar to those in {@link AbstractCollectionTest}.
 * If your class implements the full Map interface, including optional
 * operations, simply extend this class, and implement the
 * {@link #makeObject()} method.
 * <p>
 * On the other hand, if your map implementation is weird, you may have to
 * override one or more of the other protected methods.  They're described
 * below.
 * <p>
 * <b>Entry Population Methods</b>
 * <p>
 * Override these methods if your map requires special entries:
 *
 * <ul>
 * <li>{@link #getSampleKeys()}
 * <li>{@link #getSampleValues()}
 * <li>{@link #getNewSampleValues()}
 * <li>{@link #getOtherKeys()}
 * <li>{@link #getOtherValues()}
 * </ul>
 *
 * <b>Supported Operation Methods</b>
 * <p>
 * Override these methods if your map doesn't support certain operations:
 *
 * <ul>
 * <li> {@link #isPutAddSupported()}
 * <li> {@link #isPutChangeSupported()}
 * <li> {@link #isSetValueSupported()}
 * <li> {@link #isRemoveSupported()}
 * <li> {@link #isGetStructuralModify()}
 * <li> {@link #isAllowDuplicateValues()}
 * <li> {@link #isAllowNullKey()}
 * <li> {@link #isAllowNullValue()}
 * </ul>
 *
 * <b>Fixture Methods</b>
 * <p>
 * For tests on modification operations (puts and removes), fixtures are used
 * to verify that that operation results in correct state for the map and its
 * collection views.  Basically, the modification is performed against your
 * map implementation, and an identical modification is performed against
 * a <I>confirmed</I> map implementation.  A confirmed map implementation is
 * something like <Code>java.util.HashMap</Code>, which is known to conform
 * exactly to the {@link Map} contract.  After the modification takes place
 * on both your map implementation and the confirmed map implementation, the
 * two maps are compared to see if their state is identical.  The comparison
 * also compares the collection views to make sure they're still the same.<P>
 *
 * The upshot of all that is that <I>any</I> test that modifies the map in
 * <I>any</I> way will verify that <I>all</I> of the map's state is still
 * correct, including the state of its collection views.  So for instance
 * if a key is removed by the map's key set's iterator, then the entry set
 * is checked to make sure the key/value pair no longer appears.<P>
 *
 * The {@link #map} field holds an instance of your collection implementation.
 * The {@link #entrySet}, {@link #keySet} and {@link #values} fields hold
 * that map's collection views.  And the {@link #confirmed} field holds
 * an instance of the confirmed collection implementation.  The
 * {@link #resetEmpty()} and {@link #resetFull()} methods set these fields to
 * empty or full maps, so that tests can proceed from a known state.<P>
 *
 * After a modification operation to both {@link #map} and {@link #confirmed},
 * the {@link #verify()} method is invoked to compare the results.  The
 * {@link #verify} method calls separate methods to verify the map and its three
 * collection views ({@link #verifyMap}, {@link #verifyEntrySet},
 * {@link #verifyKeySet}, and {@link #verifyValues}).  You may want to override
 * one of the verification methods to perform additional verifications.  For
 * instance, TestDoubleOrderedMap would want override its
 * {@link #verifyValues()} method to verify that the values are unique and in
 * ascending order.<P>
 *
 * <b>Other Notes</b>
 * <p>
 * If your {@link Map} fails one of these tests by design, you may still use
 * this base set of cases.  Simply override the test case (method) your map
 * fails and/or the methods that define the assumptions used by the test
 * cases.  For example, if your map does not allow duplicate values, override
 * {@link #isAllowDuplicateValues()} and have it return <code>false</code>
 *
 */
public abstract class AbstractMapTest<K, V> extends AbstractObjectTest {

    /**
     * JDK1.2 has bugs in null handling of Maps, especially HashMap.Entry.toString
     * This avoids nulls for JDK1.2
     */
    private static final boolean JDK12;
    static {
        final String str = System.getProperty("java.version");
        JDK12 = str.startsWith("1.2");
    }

    // These instance variables are initialized with the reset method.
    // Tests for map methods that alter the map (put, putAll, remove)
    // first call reset() to create the map and its views; then perform
    // the modification on the map; perform the same modification on the
    // confirmed; and then call verify() to ensure that the map is equal
    // to the confirmed, that the already-constructed collection views
    // are still equal to the confirmed's collection views.

    /** Map created by reset(). */
    protected Map<K, V> map;

    /** Entry set of map created by reset(). */
    protected Set<Map.Entry<K, V>> entrySet;

    /** Key set of map created by reset(). */
    protected Set<K> keySet;

    /** Values collection of map created by reset(). */
    protected Collection<V> values;

    /** HashMap created by reset(). */
    protected Map<K, V> confirmed;

    /**
     * JUnit constructor.
     *
     * @param testName  the test name
     */
    public AbstractMapTest(final String testName) {
        super(testName);
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * support the <code>put</code> and <code>putAll</code> operations
     * adding new mappings.
     * <p>
     * Default implementation returns true.
     * Override if your collection class does not support put adding.
     */
    public boolean isPutAddSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * support the <code>put</code> and <code>putAll</code> operations
     * changing existing mappings.
     * <p>
     * Default implementation returns true.
     * Override if your collection class does not support put changing.
     */
    public boolean isPutChangeSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * support the <code>setValue</code> operation on entrySet entries.
     * <p>
     * Default implementation returns isPutChangeSupported().
     * Override if your collection class does not support setValue but does
     * support put changing.
     */
    public boolean isSetValueSupported() {
        return isPutChangeSupported();
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * support the <code>remove</code> and <code>clear</code> operations.
     * <p>
     * Default implementation returns true.
     * Override if your collection class does not support removal operations.
     */
    public boolean isRemoveSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * can cause structural modification on a get(). The example is LRUMap.
     * <p>
     * Default implementation returns false.
     * Override if your map class structurally modifies on get.
     */
    public boolean isGetStructuralModify() {
        return false;
    }

    /**
     * Returns whether the sub map views of SortedMap are serializable.
     * If the class being tested is based around a TreeMap then you should
     * override and return false as TreeMap has a bug in deserialization.
     *
     * @return false
     */
    public boolean isSubMapViewsSerializable() {
        return true;
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * supports null keys.
     * <p>
     * Default implementation returns true.
     * Override if your collection class does not support null keys.
     */
    public boolean isAllowNullKey() {
        return true;
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * supports null values.
     * <p>
     * Default implementation returns true.
     * Override if your collection class does not support null values.
     */
    public boolean isAllowNullValue() {
        return true;
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * supports duplicate values.
     * <p>
     * Default implementation returns true.
     * Override if your collection class does not support duplicate values.
     */
    public boolean isAllowDuplicateValues() {
        return true;
    }

    /**
     * Returns true if the maps produced by
     * {@link #makeObject()} and {@link #makeFullMap()}
     * provide fail-fast behavior on their various iterators.
     * <p>
     * Default implementation returns true.
     * Override if your collection class does not support fast failure.
     */
    public boolean isFailFastExpected() {
        return true;
    }

    public boolean areEqualElementsDistinguishable() {
        return false;
    }

    /**
     *  Returns the set of keys in the mappings used to test the map.  This
     *  method must return an array with the same length as {@link
     *  #getSampleValues()} and all array elements must be different. The
     *  default implementation constructs a set of String keys, and includes a
     *  single null key if {@link #isAllowNullKey()} returns <code>true</code>.
     */
    @SuppressWarnings("unchecked")
    public K[] getSampleKeys() {
        final Object[] result = new Object[] {
            "blah", "foo", "bar", "baz", "tmp", "gosh", "golly", "gee",
            "hello", "goodbye", "we'll", "see", "you", "all", "again",
            "key",
            "key2",
            isAllowNullKey() && !JDK12 ? null : "nonnullkey"
        };
        return (K[]) result;
    }

    @SuppressWarnings("unchecked")
    public K[] getOtherKeys() {
        return (K[]) getOtherNonNullStringElements();
    }

    @SuppressWarnings("unchecked")
    public V[] getOtherValues() {
        return (V[]) getOtherNonNullStringElements();
    }

    @SuppressWarnings("unchecked")
    protected <E> List<E> getAsList(final Object[] o) {
        final ArrayList<E> result = new ArrayList<>();
        for (final Object element : o) {
            result.add((E) element);
        }
        return result;
    }

    /**
     * Returns a list of string elements suitable for return by
     * {@link #getOtherKeys()} or {@link #getOtherValues}.
     *
     * <p>Override getOtherElements to return the results of this method if your
     * collection does not support heterogenous elements or the null element.
     * </p>
     */
    public Object[] getOtherNonNullStringElements() {
        return new Object[] {
            "For","then","despite",/* of */"space","I","would","be","brought",
            "From","limits","far","remote","where","thou","dost","stay"
        };
    }

    /**
     * Returns the set of values in the mappings used to test the map.  This
     * method must return an array with the same length as
     * {@link #getSampleKeys()}.  The default implementation constructs a set of
     * String values and includes a single null value if
     * {@link #isAllowNullValue()} returns <code>true</code>, and includes
     * two values that are the same if {@link #isAllowDuplicateValues()} returns
     * <code>true</code>.
     */
    @SuppressWarnings("unchecked")
    public V[] getSampleValues() {
        final Object[] result = new Object[] {
            "blahv", "foov", "barv", "bazv", "tmpv", "goshv", "gollyv", "geev",
            "hellov", "goodbyev", "we'llv", "seev", "youv", "allv", "againv",
            isAllowNullValue() && !JDK12 ? null : "nonnullvalue",
            "value",
            isAllowDuplicateValues() ? "value" : "value2",
        };
        return (V[]) result;
    }

    /**
     * Returns a the set of values that can be used to replace the values
     * returned from {@link #getSampleValues()}.  This method must return an
     * array with the same length as {@link #getSampleValues()}.  The values
     * returned from this method should not be the same as those returned from
     * {@link #getSampleValues()}.  The default implementation constructs a
     * set of String values and includes a single null value if
     * {@link #isAllowNullValue()} returns <code>true</code>, and includes two values
     * that are the same if {@link #isAllowDuplicateValues()} returns
     * <code>true</code>.
     */
    @SuppressWarnings("unchecked")
    public V[] getNewSampleValues() {
        final Object[] result = new Object[] {
            isAllowNullValue() && !JDK12 && isAllowDuplicateValues() ? null : "newnonnullvalue",
            "newvalue",
            isAllowDuplicateValues() ? "newvalue" : "newvalue2",
            "newblahv", "newfoov", "newbarv", "newbazv", "newtmpv", "newgoshv",
            "newgollyv", "newgeev", "newhellov", "newgoodbyev", "newwe'llv",
            "newseev", "newyouv", "newallv", "newagainv",
        };
        return (V[]) result;
    }

    /**
     *  Helper method to add all the mappings described by
     * {@link #getSampleKeys()} and {@link #getSampleValues()}.
     */
    public void addSampleMappings(final Map<? super K, ? super V> m) {

        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            try {
                m.put(keys[i], values[i]);
            } catch (final NullPointerException exception) {
                assertTrue("NullPointerException only allowed to be thrown " +
                           "if either the key or value is null.",
                           keys[i] == null || values[i] == null);

                assertTrue("NullPointerException on null key, but " +
                           "isAllowNullKey is not overridden to return false.",
                           keys[i] == null || !isAllowNullKey());

                assertTrue("NullPointerException on null value, but " +
                           "isAllowNullValue is not overridden to return false.",
                           values[i] == null || !isAllowNullValue());

                assertTrue("Unknown reason for NullPointer.", false);
            }
        }
        assertEquals("size must reflect number of mappings added.",
                     keys.length, m.size());
    }

    //-----------------------------------------------------------------------
    /**
     * Return a new, empty {@link Map} to be used for testing.
     *
     * @return the map to be tested
     */
    @Override
    public abstract Map<K,V> makeObject();

    /**
     * Return a new, populated map.  The mappings in the map should match the
     * keys and values returned from {@link #getSampleKeys()} and
     * {@link #getSampleValues()}.  The default implementation uses makeEmptyMap()
     * and calls {@link #addSampleMappings} to add all the mappings to the
     * map.
     *
     * @return the map to be tested
     */
    public Map<K, V> makeFullMap() {
        final Map<K, V> m = makeObject();
        addSampleMappings(m);
        return m;
    }

    /**
     * Override to return a map other than HashMap as the confirmed map.
     *
     * @return a map that is known to be valid
     */
    public Map<K, V> makeConfirmedMap() {
        return new HashMap<>();
    }

    /**
     * Creates a new Map Entry that is independent of the first and the map.
     */
    public static <K, V> Map.Entry<K, V> cloneMapEntry(final Map.Entry<K, V> entry) {
        final HashMap<K, V> map = new HashMap<>();
        map.put(entry.getKey(), entry.getValue());
        return map.entrySet().iterator().next();
    }

    /**
     * Gets the compatability version, needed for package access.
     */
    @Override
    public String getCompatibilityVersion() {
        return super.getCompatibilityVersion();
    }

    //-----------------------------------------------------------------------
    /**
     * Test to ensure the test setup is working properly.  This method checks
     * to ensure that the getSampleKeys and getSampleValues methods are
     * returning results that look appropriate.  That is, they both return a
     * non-null array of equal length.  The keys array must not have any
     * duplicate values, and may only contain a (single) null key if
     * isNullKeySupported() returns true.  The values array must only have a null
     * value if useNullValue() is true and may only have duplicate values if
     * isAllowDuplicateValues() returns true.
     */
    public void testSampleMappings() {
        final Object[] keys = getSampleKeys();
        final Object[] values = getSampleValues();
        final Object[] newValues = getNewSampleValues();

        assertNotNull("failure in test: Must have keys returned from " +
                 "getSampleKeys.", keys);

        assertNotNull("failure in test: Must have values returned from " +
                 "getSampleValues.", values);

        // verify keys and values have equivalent lengths (in case getSampleX are
        // overridden)
        assertEquals("failure in test: not the same number of sample " +
                   "keys and values.",  keys.length, values.length);

        assertEquals("failure in test: not the same number of values and new values.",
                   values.length, newValues.length);

        // verify there aren't duplicate keys, and check values
        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                assertTrue("failure in test: duplicate null keys.",
                        keys[i] != null || keys[j] != null);
                assertTrue(
                        "failure in test: duplicate non-null key.",
                        keys[i] == null || keys[j] == null || !keys[i].equals(keys[j]) && !keys[j]
                                .equals(keys[i]));
            }
            assertTrue("failure in test: found null key, but isNullKeySupported " + "is false.",
                    keys[i] != null || isAllowNullKey());
            assertTrue(
                    "failure in test: found null value, but isNullValueSupported " + "is false.",
                    values[i] != null || isAllowNullValue());
            assertTrue("failure in test: found null new value, but isNullValueSupported "
                    + "is false.", newValues[i] != null || isAllowNullValue());
            assertTrue("failure in test: values should not be the same as new value",
                    values[i] != newValues[i]
                            && (values[i] == null || !values[i].equals(newValues[i])));
        }
    }

    // tests begin here.  Each test adds a little bit of tested functionality.
    // Many methods assume previous methods passed.  That is, they do not
    // exhaustively recheck things that have already been checked in a previous
    // test methods.

    /**
     * Test to ensure that makeEmptyMap and makeFull returns a new non-null
     * map with each invocation.
     */
    public void testMakeMap() {
        final Map<K, V> em = makeObject();
        assertTrue("failure in test: makeEmptyMap must return a non-null map.",
                   em != null);

        final Map<K, V> em2 = makeObject();
        assertTrue("failure in test: makeEmptyMap must return a non-null map.",
                   em != null);

        assertTrue("failure in test: makeEmptyMap must return a new map " +
                   "with each invocation.", em != em2);

        final Map<K, V> fm = makeFullMap();
        assertTrue("failure in test: makeFullMap must return a non-null map.",
                   fm != null);

        final Map<K, V> fm2 = makeFullMap();
        assertTrue("failure in test: makeFullMap must return a non-null map.",
                   fm != null);

        assertTrue("failure in test: makeFullMap must return a new map " +
                   "with each invocation.", fm != fm2);
    }

    /**
     * Tests Map.isEmpty()
     */
    public void testMapIsEmpty() {
        resetEmpty();
        assertEquals("Map.isEmpty() should return true with an empty map",
                     true, getMap().isEmpty());
        verify();

        resetFull();
        assertEquals("Map.isEmpty() should return false with a non-empty map",
                     false, getMap().isEmpty());
        verify();
    }

    /**
     * Tests Map.size()
     */
    public void testMapSize() {
        resetEmpty();
        assertEquals("Map.size() should be 0 with an empty map",
                     0, getMap().size());
        verify();

        resetFull();
        assertEquals("Map.size() should equal the number of entries " +
                     "in the map", getSampleKeys().length, getMap().size());
        verify();
    }

    /**
     * Tests {@link Map#clear()}.  If the map {@link #isRemoveSupported()}
     * can add and remove elements}, then {@link Map#size()} and
     * {@link Map#isEmpty()} are used to ensure that map has no elements after
     * a call to clear.  If the map does not support adding and removing
     * elements, this method checks to ensure clear throws an
     * UnsupportedOperationException.
     */
    public void testMapClear() {
        if (!isRemoveSupported()) {
            try {
                resetFull();
                getMap().clear();
                fail("Expected UnsupportedOperationException on clear");
            } catch (final UnsupportedOperationException ex) {}
            return;
        }

        resetEmpty();
        getMap().clear();
        getConfirmed().clear();
        verify();

        resetFull();
        getMap().clear();
        getConfirmed().clear();
        verify();
    }

    /**
     * Tests Map.containsKey(Object) by verifying it returns false for all
     * sample keys on a map created using an empty map and returns true for
     * all sample keys returned on a full map.
     */
    public void testMapContainsKey() {
        final Object[] keys = getSampleKeys();

        resetEmpty();
        for (Object key : keys) {
            assertTrue("Map must not contain key when map is empty",
                    !getMap().containsKey(key));
        }
        verify();

        resetFull();
        for (Object key : keys) {
            assertTrue("Map must contain key for a mapping in the map. " +
                       "Missing: " + key, getMap().containsKey(key));
        }
        verify();
    }

    /**
     * Tests Map.containsValue(Object) by verifying it returns false for all
     * sample values on an empty map and returns true for all sample values on
     * a full map.
     */
    public void testMapContainsValue() {
        final Object[] values = getSampleValues();

        resetEmpty();
        for(int i = 0; i < values.length; i++) {
            assertTrue("Empty map must not contain value",
                       !getMap().containsValue(values[i]));
        }
        verify();

        resetFull();
        for (final Object value : values) {
            assertTrue("Map must contain value for a mapping in the map.",
                    getMap().containsValue(value));
        }
        verify();
    }


    /**
     * Tests Map.equals(Object)
     */
    public void testMapEquals() {
        resetEmpty();
        assertTrue("Empty maps unequal.", getMap().equals(confirmed));
        verify();

        resetFull();
        assertTrue("Full maps unequal.", getMap().equals(confirmed));
        verify();

        resetFull();
        // modify the HashMap created from the full map and make sure this
        // change results in map.equals() to return false.
        final Iterator<K> iter = confirmed.keySet().iterator();
        iter.next();
        iter.remove();
        assertTrue("Different maps equal.", !getMap().equals(confirmed));

        resetFull();
        assertTrue("equals(null) returned true.", !getMap().equals(null));
        assertTrue("equals(new Object()) returned true.",
                   !getMap().equals(new Object()));
        verify();
    }

    /**
     * Tests Map.get(Object)
     */
    public void testMapGet() {
        resetEmpty();

        final Object[] keys = getSampleKeys();
        final Object[] values = getSampleValues();

        for (final Object key : keys) {
            assertTrue("Empty map.get() should return null.",
                    getMap().get(key) == null);
        }
        verify();

        resetFull();
        for (int i = 0; i < keys.length; i++) {
            assertEquals("Full map.get() should return value from mapping.",
                         values[i], getMap().get(keys[i]));
        }
    }

    /**
     * Tests Map.hashCode()
     */
    public void testMapHashCode() {
        resetEmpty();
        assertTrue("Empty maps have different hashCodes.",
                getMap().hashCode() == confirmed.hashCode());

        resetFull();
        assertTrue("Equal maps have different hashCodes.",
                getMap().hashCode() == confirmed.hashCode());
    }

    /**
     * Tests Map.toString().  Since the format of the string returned by the
     * toString() method is not defined in the Map interface, there is no
     * common way to test the results of the toString() method.  Therefore,
     * it is encouraged that Map implementations override this test with one
     * that checks the format matches any format defined in its API.  This
     * default implementation just verifies that the toString() method does
     * not return null.
     */
    public void testMapToString() {
        resetEmpty();
        assertTrue("Empty map toString() should not return null",
                getMap().toString() != null);
        verify();

        resetFull();
        assertTrue("Empty map toString() should not return null",
                getMap().toString() != null);
        verify();
    }

    /**
     * Compare the current serialized form of the Map
     * against the canonical version in SVN.
     */
    public void testEmptyMapCompatibility() throws Exception {
        /**
         * Create canonical objects with this code
        Map map = makeEmptyMap();
        if (!(map instanceof Serializable)) return;

        writeExternalFormToDisk((Serializable) map, getCanonicalEmptyCollectionName(map));
        */

        // test to make sure the canonical form has been preserved
        final Map<K, V> map = makeObject();
        if (map instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            @SuppressWarnings("unchecked")
            final Map<K, V> map2 = (Map<K, V>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
            assertEquals("Map is empty", 0, map2.size());
        }
    }

    /**
     * Compare the current serialized form of the Map
     * against the canonical version in SVN.
     */
    public void testFullMapCompatibility() throws Exception {
        /**
         * Create canonical objects with this code
        Map map = makeFullMap();
        if (!(map instanceof Serializable)) return;

        writeExternalFormToDisk((Serializable) map, getCanonicalFullCollectionName(map));
        */

        // test to make sure the canonical form has been preserved
        final Map<K, V> map = makeFullMap();
        if (map instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            @SuppressWarnings("unchecked")
            final Map<K, V> map2 = (Map<K, V>) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
            assertEquals("Map is the right size", getSampleKeys().length, map2.size());
        }
    }

    /**
     * Tests Map.put(Object, Object)
     */
    public void testMapPut() {
        resetEmpty();
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        final V[] newValues = getNewSampleValues();

        if (isPutAddSupported()) {
            for (int i = 0; i < keys.length; i++) {
                final Object o = getMap().put(keys[i], values[i]);
                getConfirmed().put(keys[i], values[i]);
                verify();
                assertTrue("First map.put should return null", o == null);
                assertTrue("Map should contain key after put",
                        getMap().containsKey(keys[i]));
                assertTrue("Map should contain value after put",
                        getMap().containsValue(values[i]));
            }
            if (isPutChangeSupported()) {
                for (int i = 0; i < keys.length; i++) {
                    final Object o = getMap().put(keys[i], newValues[i]);
                    getConfirmed().put(keys[i], newValues[i]);
                    verify();
                    assertEquals("Map.put should return previous value when changed", values[i], o);
                    assertTrue("Map should still contain key after put when changed",
                            getMap().containsKey(keys[i]));
                    assertTrue("Map should contain new value after put when changed",
                            getMap().containsValue(newValues[i]));

                    // if duplicates are allowed, we're not guaranteed that the value
                    // no longer exists, so don't try checking that.
                    if (!isAllowDuplicateValues()) {
                        assertTrue("Map should not contain old value after put when changed",
                                !getMap().containsValue(values[i]));
                    }
                }
            } else {
                try {
                    // two possible exception here, either valid
                    getMap().put(keys[0], newValues[0]);
                    fail("Expected IllegalArgumentException or UnsupportedOperationException on put (change)");
                } catch (final IllegalArgumentException ex) {
                } catch (final UnsupportedOperationException ex) {}
            }

        } else if (isPutChangeSupported()) {
            resetEmpty();
            try {
                getMap().put(keys[0], values[0]);
                fail("Expected UnsupportedOperationException or IllegalArgumentException on put (add) when fixed size");
            } catch (final IllegalArgumentException ex) {
            } catch (final UnsupportedOperationException ex) {
            }

            resetFull();
            int i = 0;
            for (final Iterator<K> it = getMap().keySet().iterator(); it.hasNext() && i < newValues.length; i++) {
                final K  key = it.next();
                final V o = getMap().put(key, newValues[i]);
                final V value = getConfirmed().put(key, newValues[i]);
                verify();
                assertEquals("Map.put should return previous value when changed", value, o);
                assertTrue("Map should still contain key after put when changed", getMap()
                        .containsKey(key));
                assertTrue("Map should contain new value after put when changed", getMap()
                        .containsValue(newValues[i]));

                // if duplicates are allowed, we're not guaranteed that the value
                // no longer exists, so don't try checking that.
                if (!isAllowDuplicateValues()) {
                    assertTrue("Map should not contain old value after put when changed",
                        !getMap().containsValue(values[i]));
                }
            }
        } else {
            try {
                getMap().put(keys[0], values[0]);
                fail("Expected UnsupportedOperationException on put (add)");
            } catch (final UnsupportedOperationException ex) {}
        }
    }

    /**
     * Tests Map.put(null, value)
     */
    public void testMapPutNullKey() {
        resetFull();
        final V[] values = getSampleValues();

        if (isPutAddSupported()) {
            if (isAllowNullKey()) {
                getMap().put(null, values[0]);
            } else {
                try {
                    getMap().put(null, values[0]);
                    fail("put(null, value) should throw NPE/IAE");
                } catch (final NullPointerException ex) {
                } catch (final IllegalArgumentException ex) {}
            }
        }
    }

    /**
     * Tests Map.put(null, value)
     */
    public void testMapPutNullValue() {
        resetFull();
        final K[] keys = getSampleKeys();

        if (isPutAddSupported()) {
            if (isAllowNullValue()) {
                getMap().put(keys[0], null);
            } else {
                try {
                    getMap().put(keys[0], null);
                    fail("put(key, null) should throw NPE/IAE");
                } catch (final NullPointerException ex) {
                } catch (final IllegalArgumentException ex) {}
            }
        }
    }

    /**
     * Tests Map.putAll(map)
     */
    public void testMapPutAll() {
        if (!isPutAddSupported()) {
            if (!isPutChangeSupported()) {
                final Map<K, V> temp = makeFullMap();
                resetEmpty();
                try {
                    getMap().putAll(temp);
                    fail("Expected UnsupportedOperationException on putAll");
                } catch (final UnsupportedOperationException ex) {}
            }
            return;
        }

        // check putAll OK adding empty map to empty map
        resetEmpty();
        assertEquals(0, getMap().size());
        getMap().putAll(new HashMap<K, V>());
        assertEquals(0, getMap().size());

        // check putAll OK adding empty map to non-empty map
        resetFull();
        final int size = getMap().size();
        getMap().putAll(new HashMap<K, V>());
        assertEquals(size, getMap().size());

        // check putAll OK adding non-empty map to empty map
        resetEmpty();
        Map<K, V> m2 = makeFullMap();
        getMap().putAll(m2);
        getConfirmed().putAll(m2);
        verify();

        // check putAll OK adding non-empty JDK map to empty map
        resetEmpty();
        m2 = makeConfirmedMap();
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        for(int i = 0; i < keys.length; i++) {
            m2.put(keys[i], values[i]);
        }
        getMap().putAll(m2);
        getConfirmed().putAll(m2);
        verify();

        // check putAll OK adding non-empty JDK map to non-empty map
        resetEmpty();
        m2 = makeConfirmedMap();
        getMap().put(keys[0], values[0]);
        getConfirmed().put(keys[0], values[0]);
        verify();
        for(int i = 1; i < keys.length; i++) {
            m2.put(keys[i], values[i]);
        }
        getMap().putAll(m2);
        getConfirmed().putAll(m2);
        verify();
    }

    /**
     * Tests Map.remove(Object)
     */
    public void testMapRemove() {
        if (!isRemoveSupported()) {
            try {
                resetFull();
                getMap().remove(getMap().keySet().iterator().next());
                fail("Expected UnsupportedOperationException on remove");
            } catch (final UnsupportedOperationException ex) {}
            return;
        }

        resetEmpty();

        final Object[] keys = getSampleKeys();
        final Object[] values = getSampleValues();
        for (final Object key : keys) {
            final Object o = getMap().remove(key);
            assertTrue("First map.remove should return null", o == null);
        }
        verify();

        resetFull();

        for (int i = 0; i < keys.length; i++) {
            final Object o = getMap().remove(keys[i]);
            getConfirmed().remove(keys[i]);
            verify();

            assertEquals("map.remove with valid key should return value",
                         values[i], o);
        }

        final Object[] other = getOtherKeys();

        resetFull();
        final int size = getMap().size();
        for (final Object element : other) {
            final Object o = getMap().remove(element);
            assertNull("map.remove for nonexistent key should return null", o);
            assertEquals("map.remove for nonexistent key should not " +
                         "shrink map", size, getMap().size());
        }
        verify();
    }

    //-----------------------------------------------------------------------
    /**
     * Tests that the {@link Map#values} collection is backed by
     * the underlying map for clear().
     */
    public void testValuesClearChangesMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // clear values, reflected in map
        resetFull();
        Collection<V> values = getMap().values();
        assertTrue(getMap().size() > 0);
        assertTrue(values.size() > 0);
        values.clear();
        assertTrue(getMap().size() == 0);
        assertTrue(values.size() == 0);

        // clear map, reflected in values
        resetFull();
        values = getMap().values();
        assertTrue(getMap().size() > 0);
        assertTrue(values.size() > 0);
        getMap().clear();
        assertTrue(getMap().size() == 0);
        assertTrue(values.size() == 0);
    }

    /**
     * Tests that the {@link Map#keySet} collection is backed by
     * the underlying map for clear().
     */
    public void testKeySetClearChangesMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // clear values, reflected in map
        resetFull();
        Set<K> keySet = getMap().keySet();
        assertTrue(getMap().size() > 0);
        assertTrue(keySet.size() > 0);
        keySet.clear();
        assertTrue(getMap().size() == 0);
        assertTrue(keySet.size() == 0);

        // clear map, reflected in values
        resetFull();
        keySet = getMap().keySet();
        assertTrue(getMap().size() > 0);
        assertTrue(keySet.size() > 0);
        getMap().clear();
        assertTrue(getMap().size() == 0);
        assertTrue(keySet.size() == 0);
    }

    /**
     * Tests that the {@link Map#entrySet()} collection is backed by
     * the underlying map for clear().
     */
    public void testEntrySetClearChangesMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // clear values, reflected in map
        resetFull();
        Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        assertTrue(getMap().size() > 0);
        assertTrue(entrySet.size() > 0);
        entrySet.clear();
        assertTrue(getMap().size() == 0);
        assertTrue(entrySet.size() == 0);

        // clear map, reflected in values
        resetFull();
        entrySet = getMap().entrySet();
        assertTrue(getMap().size() > 0);
        assertTrue(entrySet.size() > 0);
        getMap().clear();
        assertTrue(getMap().size() == 0);
        assertTrue(entrySet.size() == 0);
    }

    //-----------------------------------------------------------------------
    public void testEntrySetContains1() {
        resetFull();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        assertEquals(true, entrySet.contains(entry));
    }

    public void testEntrySetContains2() {
        resetFull();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final Map.Entry<K, V> test = cloneMapEntry(entry);
        assertEquals(true, entrySet.contains(test));
    }

    @SuppressWarnings("unchecked")
    public void testEntrySetContains3() {
        resetFull();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final HashMap<K, V> temp = new HashMap<>();
        temp.put(entry.getKey(), (V) "A VERY DIFFERENT VALUE");
        final Map.Entry<K, V> test = temp.entrySet().iterator().next();
        assertEquals(false, entrySet.contains(test));
    }

    public void testEntrySetRemove1() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final int size = getMap().size();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final K key = entry.getKey();

        assertEquals(true, entrySet.remove(entry));
        assertEquals(false, getMap().containsKey(key));
        assertEquals(size - 1, getMap().size());
    }

    public void testEntrySetRemove2() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final int size = getMap().size();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final K key = entry.getKey();
        final Map.Entry<K, V> test = cloneMapEntry(entry);

        assertEquals(true, entrySet.remove(test));
        assertEquals(false, getMap().containsKey(key));
        assertEquals(size - 1, getMap().size());
    }

    @SuppressWarnings("unchecked")
    public void testEntrySetRemove3() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final int size = getMap().size();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final K key = entry.getKey();
        final HashMap<K, V> temp = new HashMap<>();
        temp.put(entry.getKey(), (V) "A VERY DIFFERENT VALUE");
        final Map.Entry<K, V> test = temp.entrySet().iterator().next();

        assertEquals(false, entrySet.remove(test));
        assertEquals(true, getMap().containsKey(key));
        assertEquals(size, getMap().size());
    }

    //-----------------------------------------------------------------------
    /**
     * Tests that the {@link Map#values} collection is backed by
     * the underlying map by removing from the values collection
     * and testing if the value was removed from the map.
     * <p>
     * We should really test the "vice versa" case--that values removed
     * from the map are removed from the values collection--also,
     * but that's a more difficult test to construct (lacking a
     * "removeValue" method.)
     * </p>
     * <p>
     * See bug <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=9573">
     * 9573</a>.
     * </p>
     */
    public void testValuesRemoveChangesMap() {
        resetFull();
        final V[] sampleValues = getSampleValues();
        final Collection<V> values = getMap().values();
        for (int i = 0; i < sampleValues.length; i++) {
            if (map.containsValue(sampleValues[i])) {
                int j = 0;  // loop counter prevents infinite loops when remove is broken
                while (values.contains(sampleValues[i]) && j < 10000) {
                    try {
                        values.remove(sampleValues[i]);
                    } catch (final UnsupportedOperationException e) {
                        // if values.remove is unsupported, just skip this test
                        return;
                    }
                    j++;
                }
                assertTrue("values().remove(obj) is broken", j < 10000);
                assertTrue(
                    "Value should have been removed from the underlying map.",
                    !getMap().containsValue(sampleValues[i]));
            }
        }
    }

    /**
     * Tests values.removeAll.
     */
    public void testValuesRemoveAll() {
        resetFull();
        final Collection<V> values = getMap().values();
        final List<V> sampleValuesAsList = Arrays.asList(getSampleValues());
        if (!values.equals(sampleValuesAsList)) {
            return;
        }
        try {
            assertFalse(values.removeAll(Collections.<V> emptySet()));
        } catch (final UnsupportedOperationException e) {
            // if values.removeAll is unsupported, just skip this test
            return;
        }
        assertEquals(sampleValuesAsList.size(), getMap().size());
        try {
            assertTrue(values.removeAll(sampleValuesAsList));
        } catch (final UnsupportedOperationException e) {
            // if values.removeAll is unsupported, just skip this test
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Test values.retainAll.
     */
    public void testValuesRetainAll() {
        resetFull();
        final Collection<V> values = getMap().values();
        final List<V> sampleValuesAsList = Arrays.asList(getSampleValues());
        if (!values.equals(sampleValuesAsList)) {
            return;
        }
        try {
            assertFalse(values.retainAll(sampleValuesAsList));
        } catch (final UnsupportedOperationException e) {
            // if values.retainAll is unsupported, just skip this test
            return;
        }
        assertEquals(sampleValuesAsList.size(), getMap().size());
        try {
            assertTrue(values.retainAll(Collections.<V> emptySet()));
        } catch (final UnsupportedOperationException e) {
            // if values.retainAll is unsupported, just skip this test
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Verifies that values.iterator.remove changes the underlying map.
     */
    @SuppressWarnings("boxing") // OK in test code
    public void testValuesIteratorRemoveChangesMap() {
        resetFull();
        final List<V> sampleValuesAsList = Arrays.asList(getSampleValues());
        final Map<V, Integer> cardinality = CollectionUtils.getCardinalityMap(sampleValuesAsList);
        final Collection<V> values = getMap().values();
        for (final Iterator<V> iter = values.iterator(); iter.hasNext();) {
            final V value = iter.next();
            Integer count = cardinality.get(value);
            if (count == null) {
                return;
            }
            try {
                iter.remove();
                cardinality.put(value, --count);
            } catch (final UnsupportedOperationException e) {
                // if values.iterator.remove is unsupported, just skip this test
                return;
            }
            final boolean expected = count > 0;
            final StringBuilder msg = new StringBuilder("Value should ");
            msg.append(expected ? "yet " : "no longer ");
            msg.append("be present in the underlying map");
            assertEquals(msg.toString(), expected, getMap().containsValue(value));
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Tests that the {@link Map#keySet} set is backed by
     * the underlying map by removing from the keySet set
     * and testing if the key was removed from the map.
     */
    public void testKeySetRemoveChangesMap() {
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final Set<K> keys = getMap().keySet();
        for (int i = 0; i < sampleKeys.length; i++) {
            try {
                keys.remove(sampleKeys[i]);
            } catch (final UnsupportedOperationException e) {
                // if key.remove is unsupported, just skip this test
                return;
            }
            assertTrue(
                "Key should have been removed from the underlying map.",
                !getMap().containsKey(sampleKeys[i]));
        }
    }

    /**
     * Test keySet.removeAll.
     */
    public void testKeySetRemoveAll() {
        resetFull();
        final Set<K> keys = getMap().keySet();
        final List<K> sampleKeysAsList = Arrays.asList(getSampleKeys());
        if (!keys.equals(sampleKeysAsList)) {
            return;
        }
        try {
            assertFalse(keys.removeAll(Collections.<K> emptySet()));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertEquals(sampleKeysAsList, keys);
        try {
            assertTrue(keys.removeAll(sampleKeysAsList));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Test keySet.retainAll.
     */
    public void testKeySetRetainAll() {
        resetFull();
        final Set<K> keys = getMap().keySet();
        final List<K> sampleKeysAsList = Arrays.asList(getSampleKeys());
        if (!keys.equals(sampleKeysAsList)) {
            return;
        }
        try {
            assertFalse(keys.retainAll(sampleKeysAsList));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertEquals(sampleKeysAsList, keys);
        try {
            assertTrue(keys.retainAll(Collections.<K> emptySet()));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Verify that keySet.iterator.remove changes the underlying map.
     */
    public void testKeySetIteratorRemoveChangesMap() {
        resetFull();
        for (final Iterator<K> iter = getMap().keySet().iterator(); iter.hasNext();) {
            final K key = iter.next();
            try {
                iter.remove();
            } catch (final UnsupportedOperationException e) {
                return;
            }
            assertFalse(getMap().containsKey(key));
        }
    }

    /**
     * Tests that the {@link Map#entrySet} set is backed by
     * the underlying map by removing from the entrySet set
     * and testing if the entry was removed from the map.
     */
    public void testEntrySetRemoveChangesMap() {
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        for (int i = 0; i < sampleKeys.length; i++) {
            try {
                entrySet.remove(new DefaultMapEntry<>(sampleKeys[i], sampleValues[i]));
            } catch (final UnsupportedOperationException e) {
                // if entrySet removal is unsupported, just skip this test
                return;
            }
            assertTrue(
                    "Entry should have been removed from the underlying map.",
                    !getMap().containsKey(sampleKeys[i]));
        }
    }

    /**
     * Test entrySet.removeAll.
     */
    public void testEntrySetRemoveAll() {
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        //verify map looks as expected:
        for (int i = 0; i < sampleKeys.length; i++) {
            if (!getMap().containsKey(sampleKeys[i])) {
                return;
            }
            final V value = sampleValues[i];
            final V test = getMap().get(sampleKeys[i]);
            if (value == test || value != null && value.equals(test)) {
                continue;
            }
            return;
        }
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final HashSet<Map.Entry<K, V>> comparisonSet = new HashSet<>(entrySet);
        try {
            assertFalse(entrySet.removeAll(Collections.<Map.Entry<K, V>> emptySet()));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertEquals(sampleKeys.length, getMap().size());
        try {
            assertTrue(entrySet.removeAll(comparisonSet));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Test entrySet.retainAll.
     */
    public void testEntrySetRetainAll() {
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        //verify map looks as expected:
        for (int i = 0; i < sampleKeys.length; i++) {
            if (!getMap().containsKey(sampleKeys[i])) {
                return;
            }
            final V value = sampleValues[i];
            final V test = getMap().get(sampleKeys[i]);
            if (value == test || value != null && value.equals(test)) {
                continue;
            }
            return;
        }
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final HashSet<Map.Entry<K, V>> comparisonSet = new HashSet<>(entrySet);
        try {
            assertFalse(entrySet.retainAll(comparisonSet));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertEquals(sampleKeys.length, getMap().size());
        try {
            assertTrue(entrySet.retainAll(Collections.<Map.Entry<K, V>> emptySet()));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Verify that entrySet.iterator.remove changes the underlying map.
     */
    public void testEntrySetIteratorRemoveChangesMap() {
        resetFull();
        for (final Iterator<Map.Entry<K, V>> iter = getMap().entrySet().iterator(); iter.hasNext();) {
            final K key = iter.next().getKey();
            try {
                iter.remove();
            } catch (final UnsupportedOperationException e) {
                return;
            }
            assertFalse(getMap().containsKey(key));
        }
    }

    /**
     * Utility methods to create an array of Map.Entry objects
     * out of the given key and value arrays.<P>
     *
     * @param keys    the array of keys
     * @param values  the array of values
     * @return an array of Map.Entry of those keys to those values
     */
    @SuppressWarnings("unchecked")
    private Map.Entry<K, V>[] makeEntryArray(final K[] keys, final V[] values) {
        final Map.Entry<K, V>[] result = new Map.Entry[keys.length];
        for (int i = 0; i < keys.length; i++) {
            final Map<K, V> map = makeConfirmedMap();
            map.put(keys[i], values[i]);
            result[i] = map.entrySet().iterator().next();
        }
        return result;
    }

    /**
     * Bulk test {@link Map#entrySet()}.  This method runs through all of
     * the tests in {@link AbstractSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the map and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the map's entry set
     */
    public BulkTest bulkTestMapEntrySet() {
        return new TestMapEntrySet();
    }

    public class TestMapEntrySet extends AbstractSetTest<Map.Entry<K, V>> {
        public TestMapEntrySet() {
            super("MapEntrySet");
        }

        // Have to implement manually; entrySet doesn't support addAll
        /**
         * {@inheritDoc}
         */
        @Override
        public Entry<K, V>[] getFullElements() {
            return getFullNonNullElements();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map.Entry<K, V>[] getFullNonNullElements() {
            final K[] k = getSampleKeys();
            final V[] v = getSampleValues();
            return makeEntryArray(k, v);
        }

        // Have to implement manually; entrySet doesn't support addAll
        @Override
        public Map.Entry<K, V>[] getOtherElements() {
            final K[] k = getOtherKeys();
            final V[] v = getOtherValues();
            return makeEntryArray(k, v);
        }

        @Override
        public Set<Map.Entry<K, V>> makeObject() {
            return AbstractMapTest.this.makeObject().entrySet();
        }

        @Override
        public Set<Map.Entry<K, V>> makeFullCollection() {
            return makeFullMap().entrySet();
        }

        @Override
        public boolean isAddSupported() {
            // Collection views don't support add operations.
            return false;
        }

        @Override
        public boolean isRemoveSupported() {
            // Entry set should only support remove if map does
            return AbstractMapTest.this.isRemoveSupported();
        }

        public boolean isGetStructuralModify() {
            return AbstractMapTest.this.isGetStructuralModify();
        }

        @Override
        public boolean areEqualElementsDistinguishable() {
            return AbstractMapTest.this.areEqualElementsDistinguishable();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public void resetFull() {
            AbstractMapTest.this.resetFull();
            setCollection(AbstractMapTest.this.getMap().entrySet());
            TestMapEntrySet.this.setConfirmed(AbstractMapTest.this.getConfirmed().entrySet());
        }

        @Override
        public void resetEmpty() {
            AbstractMapTest.this.resetEmpty();
            setCollection(AbstractMapTest.this.getMap().entrySet());
            TestMapEntrySet.this.setConfirmed(AbstractMapTest.this.getConfirmed().entrySet());
        }

        public void testMapEntrySetIteratorEntry() {
            resetFull();
            final Iterator<Map.Entry<K, V>> it = getCollection().iterator();
            int count = 0;
            while (it.hasNext()) {
                final Map.Entry<K, V> entry = it.next();
                assertEquals(true, AbstractMapTest.this.getMap().containsKey(entry.getKey()));
                assertEquals(true, AbstractMapTest.this.getMap().containsValue(entry.getValue()));
                if (!isGetStructuralModify()) {
                    assertEquals(AbstractMapTest.this.getMap().get(entry.getKey()), entry.getValue());
                }
                count++;
            }
            assertEquals(getCollection().size(), count);
        }

        public void testMapEntrySetIteratorEntrySetValue() {
            final K key1 = getSampleKeys()[0];
            final K key2 = getSampleKeys().length == 1 ? getSampleKeys()[0] : getSampleKeys()[1];
            final V newValue1 = getNewSampleValues()[0];
            final V newValue2 = getNewSampleValues().length ==1 ? getNewSampleValues()[0] : getNewSampleValues()[1];

            resetFull();
            // explicitly get entries as sample values/keys are connected for some maps
            // such as BeanMap
            Iterator<Map.Entry<K, V>> it = TestMapEntrySet.this.getCollection().iterator();
            final Map.Entry<K, V> entry1 = getEntry(it, key1);
            it = TestMapEntrySet.this.getCollection().iterator();
            final Map.Entry<K, V> entry2 = getEntry(it, key2);
            Iterator<Map.Entry<K, V>> itConfirmed = TestMapEntrySet.this.getConfirmed().iterator();
            final Map.Entry<K, V> entryConfirmed1 = getEntry(itConfirmed, key1);
            itConfirmed = TestMapEntrySet.this.getConfirmed().iterator();
            final Map.Entry<K, V> entryConfirmed2 = getEntry(itConfirmed, key2);
            verify();

            if (!isSetValueSupported()) {
                try {
                    entry1.setValue(newValue1);
                } catch (final UnsupportedOperationException ex) {
                }
                return;
            }

            entry1.setValue(newValue1);
            entryConfirmed1.setValue(newValue1);
            assertEquals(newValue1, entry1.getValue());
            assertEquals(true, AbstractMapTest.this.getMap().containsKey(entry1.getKey()));
            assertEquals(true, AbstractMapTest.this.getMap().containsValue(newValue1));
            assertEquals(newValue1, AbstractMapTest.this.getMap().get(entry1.getKey()));
            verify();

            entry1.setValue(newValue1);
            entryConfirmed1.setValue(newValue1);
            assertEquals(newValue1, entry1.getValue());
            assertEquals(true, AbstractMapTest.this.getMap().containsKey(entry1.getKey()));
            assertEquals(true, AbstractMapTest.this.getMap().containsValue(newValue1));
            assertEquals(newValue1, AbstractMapTest.this.getMap().get(entry1.getKey()));
            verify();

            entry2.setValue(newValue2);
            entryConfirmed2.setValue(newValue2);
            assertEquals(newValue2, entry2.getValue());
            assertEquals(true, AbstractMapTest.this.getMap().containsKey(entry2.getKey()));
            assertEquals(true, AbstractMapTest.this.getMap().containsValue(newValue2));
            assertEquals(newValue2, AbstractMapTest.this.getMap().get(entry2.getKey()));
            verify();
        }

        public Map.Entry<K, V> getEntry(final Iterator<Map.Entry<K, V>> itConfirmed, final K key) {
            Map.Entry<K, V> entry = null;
            while (itConfirmed.hasNext()) {
                final Map.Entry<K, V> temp = itConfirmed.next();
                if (temp.getKey() == null) {
                    if (key == null) {
                        entry = temp;
                        break;
                    }
                } else if (temp.getKey().equals(key)) {
                    entry = temp;
                    break;
                }
            }
            assertNotNull("No matching entry in map for key '" + key + "'", entry);
            return entry;
        }

        public void testMapEntrySetRemoveNonMapEntry() {
            if (!isRemoveSupported()) {
                return;
            }
            resetFull();
            assertEquals(false, getCollection().remove(null));
            assertEquals(false, getCollection().remove(new Object()));
        }

        @Override
        public void verify() {
            super.verify();
            AbstractMapTest.this.verify();
        }
    }


    /**
     * Bulk test {@link Map#keySet()}.  This method runs through all of
     * the tests in {@link AbstractSetTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the map and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the map's key set
     */
    public BulkTest bulkTestMapKeySet() {
        return new TestMapKeySet();
    }

    public class TestMapKeySet extends AbstractSetTest<K> {
        public TestMapKeySet() {
            super("");
        }

        @Override
        public K[] getFullElements() {
            return getSampleKeys();
        }

        @Override
        public K[] getOtherElements() {
            return getOtherKeys();
        }

        @Override
        public Set<K> makeObject() {
            return AbstractMapTest.this.makeObject().keySet();
        }

        @Override
        public Set<K> makeFullCollection() {
            return AbstractMapTest.this.makeFullMap().keySet();
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public void resetEmpty() {
            AbstractMapTest.this.resetEmpty();
            setCollection(AbstractMapTest.this.getMap().keySet());
            TestMapKeySet.this.setConfirmed(AbstractMapTest.this.getConfirmed().keySet());
        }

        @Override
        public void resetFull() {
            AbstractMapTest.this.resetFull();
            setCollection(AbstractMapTest.this.getMap().keySet());
            TestMapKeySet.this.setConfirmed(AbstractMapTest.this.getConfirmed().keySet());
        }

        @Override
        public void verify() {
            super.verify();
            AbstractMapTest.this.verify();
        }
    }

    /**
     * Bulk test {@link Map#values()}.  This method runs through all of
     * the tests in {@link AbstractCollectionTest}.
     * After modification operations, {@link #verify()} is invoked to ensure
     * that the map and the other collection views are still valid.
     *
     * @return a {@link AbstractCollectionTest} instance for testing the map's
     *    values collection
     */
    public BulkTest bulkTestMapValues() {
        return new TestMapValues();
    }

    public class TestMapValues extends AbstractCollectionTest<V> {
        public TestMapValues() {
            super("");
        }

        @Override
        public V[] getFullElements() {
            return getSampleValues();
        }

        @Override
        public V[] getOtherElements() {
            return getOtherValues();
        }

        @Override
        public Collection<V> makeObject() {
            return AbstractMapTest.this.makeObject().values();
        }

        @Override
        public Collection<V> makeFullCollection() {
            return AbstractMapTest.this.makeFullMap().values();
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMapTest.this.isAllowNullKey();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isRemoveSupported() {
            return AbstractMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public boolean areEqualElementsDistinguishable() {
            // equal values are associated with different keys, so they are
            // distinguishable.
            return true;
        }

        @Override
        public Collection<V> makeConfirmedCollection() {
            // never gets called, reset methods are overridden
            return null;
        }

        @Override
        public Collection<V> makeConfirmedFullCollection() {
            // never gets called, reset methods are overridden
            return null;
        }

        @Override
        public void resetFull() {
            AbstractMapTest.this.resetFull();
            setCollection(map.values());
            TestMapValues.this.setConfirmed(AbstractMapTest.this.getConfirmed().values());
        }

        @Override
        public void resetEmpty() {
            AbstractMapTest.this.resetEmpty();
            setCollection(map.values());
            TestMapValues.this.setConfirmed(AbstractMapTest.this.getConfirmed().values());
        }

        @Override
        public void verify() {
            super.verify();
            AbstractMapTest.this.verify();
        }

        // TODO: should test that a remove on the values collection view
        // removes the proper mapping and not just any mapping that may have
        // the value equal to the value returned from the values iterator.
    }


    /**
     * Resets the {@link #map}, {@link #entrySet}, {@link #keySet},
     * {@link #values} and {@link #confirmed} fields to empty.
     */
    public void resetEmpty() {
        this.map = makeObject();
        views();
        this.confirmed = makeConfirmedMap();
    }

    /**
     * Resets the {@link #map}, {@link #entrySet}, {@link #keySet},
     * {@link #values} and {@link #confirmed} fields to full.
     */
    public void resetFull() {
        this.map = makeFullMap();
        views();
        this.confirmed = makeConfirmedMap();
        final K[] k = getSampleKeys();
        final V[] v = getSampleValues();
        for (int i = 0; i < k.length; i++) {
            confirmed.put(k[i], v[i]);
        }
    }

    /**
     * Resets the collection view fields.
     */
    private void views() {
        this.keySet = getMap().keySet();
        // see verifyValues: retrieve the values collection only when verifying them
        // this.values = getMap().values();
        this.entrySet = getMap().entrySet();
    }

    /**
     * Verifies that {@link #map} is still equal to {@link #confirmed}.
     * This method checks that the map is equal to the HashMap,
     * <I>and</I> that the map's collection views are still equal to
     * the HashMap's collection views.  An <Code>equals</Code> test
     * is done on the maps and their collection views; their size and
     * <Code>isEmpty</Code> results are compared; their hashCodes are
     * compared; and <Code>containsAll</Code> tests are run on the
     * collection views.
     */
    public void verify() {
        verifyMap();
        verifyEntrySet();
        verifyKeySet();
        verifyValues();
    }

    public void verifyMap() {
        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals("Map should be same size as HashMap", size, getMap().size());
        assertEquals("Map should be empty if HashMap is", empty, getMap().isEmpty());
        assertEquals("hashCodes should be the same", getConfirmed().hashCode(), getMap().hashCode());
        // this fails for LRUMap because confirmed.equals() somehow modifies
        // map, causing concurrent modification exceptions.
        // assertEquals("Map should still equal HashMap", confirmed, map);
        // this works though and performs the same verification:
        assertTrue("Map should still equal HashMap", getMap().equals(getConfirmed()));
        // TODO: this should really be reexamined to figure out why LRU map
        // behaves like it does (the equals shouldn't modify since all accesses
        // by the confirmed collection should be through an iterator, thus not
        // causing LRUMap to change).
        // When comparing two maps, the entries in the map have to be accessed
        // (usually with the get(Object) method). In the case of the LRUMap, this
        // also alters the modCount as moveToMRU is called for the retrieved entry.
        // Imho, the modCount should not be increased for a read-only operation.
    }

    public void verifyEntrySet() {
        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals("entrySet should be same size as HashMap's" +
                     "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet(),
                     size, entrySet.size());
        assertEquals("entrySet should be empty if HashMap is" +
                     "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet(),
                     empty, entrySet.isEmpty());
        assertTrue("entrySet should contain all HashMap's elements" +
                   "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet(),
                   entrySet.containsAll(getConfirmed().entrySet()));
        assertEquals("entrySet hashCodes should be the same" +
                     "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet(),
                     getConfirmed().entrySet().hashCode(), entrySet.hashCode());
        assertEquals("Map's entry set should still equal HashMap's",
                     getConfirmed().entrySet(), entrySet);
    }

    public void verifyKeySet() {
        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals("keySet should be same size as HashMap's" +
                     "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet(),
                     size, keySet.size());
        assertEquals("keySet should be empty if HashMap is" +
                     "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet(),
                     empty, keySet.isEmpty());
        assertTrue("keySet should contain all HashMap's elements" +
                   "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet(),
                   keySet.containsAll(getConfirmed().keySet()));
        assertEquals("keySet hashCodes should be the same" +
                     "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet(),
                     getConfirmed().keySet().hashCode(), keySet.hashCode());
        assertEquals("Map's key set should still equal HashMap's",
                getConfirmed().keySet(), keySet);
    }

    public void verifyValues() {
        final List<V> known = new ArrayList<>(getConfirmed().values());

        // bug in IBM JDK: IBM J9 VM build 2.4, JRE 1.6.0 IBM J9 2.4 Linux x86-32 jvmxi3260sr12-20121024_126067
        // a call to values() on an empty map retrieved via TreeMap#headMap or tailMap
        // will render the values view unusable: resulting in NullPointerExceptions or missing values
        // it will also not recover, as the value view is cached internally
        values = getMap().values();

        final List<V> test = new ArrayList<>(values);

        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals("values should be same size as HashMap's" +
                     "\nTest: " + test + "\nReal: " + known,
                     size, values.size());
        assertEquals("values should be empty if HashMap is" +
                     "\nTest: " + test + "\nReal: " + known,
                     empty, values.isEmpty());
        assertTrue("values should contain all HashMap's elements" +
                   "\nTest: " + test + "\nReal: " + known,
                    test.containsAll(known));
        assertTrue("values should contain all HashMap's elements" +
                   "\nTest: " + test + "\nReal: " + known,
                   known.containsAll(test));
        // originally coded to use a HashBag, but now separate jar so...
        for (final V v : known) {
            final boolean removed = test.remove(v);
            assertTrue("Map's values should still equal HashMap's", removed);
        }
        assertTrue("Map's values should still equal HashMap's", test.isEmpty());
    }

    /**
     * Erases any leftover instance variables by setting them to null.
     */
    @Override
    public void tearDown() throws Exception {
        map = null;
        keySet = null;
        entrySet = null;
        values = null;
        confirmed = null;
    }

    /**
     * Get the map.
     * @return Map<K,V>
     */
    public Map<K, V> getMap() {
        return map;
    }

    /**
     * Get the confirmed.
     * @return Map<K,V>
     */
    public Map<K, V> getConfirmed() {
        return confirmed;
    }
}
