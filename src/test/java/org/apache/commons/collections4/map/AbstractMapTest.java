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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.collection.AbstractCollectionTest;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.apache.commons.collections4.set.AbstractSetTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link java.util.Map}.
 * <p>
 * The forces at work here are similar to those in {@link AbstractCollectionTest}. If your class implements the full Map interface, including optional
 * operations, simply extend this class, and implement the {@link #makeObject()} method.
 * </p>
 * <p>
 * On the other hand, if your map implementation is weird, you may have to override one or more of the other protected methods. They're described below.
 * </p>
 * <p>
 * <strong>Entry Population Methods</strong>
 * </p>
 * <p>
 * Override these methods if your map requires special entries:
 * </p>
 *
 * <ul>
 * <li>{@link #getSampleKeys()}
 * <li>{@link #getSampleValues()}
 * <li>{@link #getNewSampleValues()}
 * <li>{@link #getOtherKeys()}
 * <li>{@link #getOtherValues()}
 * </ul>
 *
 * <strong>Indicate Map Behaviour</strong>
 * <p>
 * Override these if your map makes specific behavior guarantees:
 * </p>
 * <ul>
 * <li>{@link #getIterationBehaviour()}</li>
 * </ul>
 *
 * <strong>Supported Operation Methods</strong>
 * <p>
 * Override these methods if your map doesn't support certain operations:
 * </p>
 *
 * <ul>
 * <li>{@link #isPutAddSupported()}
 * <li>{@link #isPutChangeSupported()}
 * <li>{@link #isSetValueSupported()}
 * <li>{@link #isRemoveSupported()}
 * <li>{@link #isGetStructuralModify()}
 * <li>{@link #isAllowDuplicateValues()}
 * <li>{@link #isAllowNullKey()}
 * <li>{@link #isAllowNullValue()}
 * </ul>
 *
 * <strong>Fixture Methods</strong>
 * <p>
 * For tests on modification operations (puts and removes), fixtures are used to verify that that operation results in correct state for the map and its
 * collection views. Basically, the modification is performed against your map implementation, and an identical modification is performed against a
 * <em>confirmed</em> map implementation. A confirmed map implementation is something like <Code>java.util.HashMap</Code>, which is known to conform exactly to
 * the {@link Map} contract. After the modification takes place on both your map implementation and the confirmed map implementation, the two maps are compared
 * to see if their state is identical. The comparison also compares the collection views to make sure they're still the same.
 * </p>
 * <p>
 * The upshot of all that is that <em>any</em> test that modifies the map in <em>any</em> way will verify that <em>all</em> of the map's state is still correct,
 * including the state of its collection views. So for instance if a key is removed by the map's key set's iterator, then the entry set is checked to make sure
 * the key/value pair no longer appears.
 * </p>
 * <p>
 * The {@link #map} field holds an instance of your collection implementation. The {@link #entrySet}, {@link #keySet} and {@link #values} fields hold that map's
 * collection views. And the {@link #confirmed} field holds an instance of the confirmed collection implementation. The {@link #resetEmpty()} and
 * {@link #resetFull()} methods set these fields to empty or full maps, so that tests can proceed from a known state.
 * </p>
 * <p>
 * After a modification operation to both {@link #map} and {@link #confirmed}, the {@link #verify()} method is invoked to compare the results. The
 * {@link #verify} method calls separate methods to verify the map and its three collection views ({@link #verifyMap}, {@link #verifyEntrySet},
 * {@link #verifyKeySet}, and {@link #verifyValues}). You may want to override one of the verification methods to perform additional verifications. For
 * instance, TestDoubleOrderedMap would want override its {@link #verifyValues()} method to verify that the values are unique and in ascending order.
 * </p>
 *
 * <strong>Other Notes</strong>
 * <p>
 * If your {@link Map} fails one of these tests by design, you may still use this base set of cases. Simply override the test case (method) your map fails
 * and/or the methods that define the assumptions used by the test cases. For example, if your map does not allow duplicate values, override
 * {@link #isAllowDuplicateValues()} and have it return {@code false}
 * </p>
 *
 * @param <M> the Map type.
 * @param <K> the key type.
 * @param <V> the value type.
 */
public abstract class AbstractMapTest<M extends Map<K, V>, K, V> extends AbstractObjectTest {

    public class TestMapEntrySet extends AbstractSetTest<Map.Entry<K, V>> {

        @Override
        public boolean areEqualElementsDistinguishable() {
            return AbstractMapTest.this.areEqualElementsDistinguishable();
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
            assertNotNull(entry, "No matching entry in map for key '" + key + "'");
            return entry;
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

        @Override
        protected int getIterationBehaviour() {
            return AbstractMapTest.this.getIterationBehaviour();
        }

        // Have to implement manually; entrySet doesn't support addAll
        @Override
        public Map.Entry<K, V>[] getOtherElements() {
            final K[] k = getOtherKeys();
            final V[] v = getOtherValues();
            return makeEntryArray(k, v);
        }

        @Override
        public boolean isAddSupported() {
            // Collection views don't support add operations.
            return false;
        }

        public boolean isGetStructuralModify() {
            return AbstractMapTest.this.isGetStructuralModify();
        }

        @Override
        public boolean isRemoveSupported() {
            // Entry set should only support remove if map does
            return AbstractMapTest.this.isRemoveSupported();
        }

        @Override
        public boolean isTestSerialization() {
            return false;
        }

        @Override
        public Set<Map.Entry<K, V>> makeFullCollection() {
            return makeFullMap().entrySet();
        }

        @Override
        public Set<Map.Entry<K, V>> makeObject() {
            return AbstractMapTest.this.makeObject().entrySet();
        }

        @Override
        public void resetEmpty() {
            AbstractMapTest.this.resetEmpty();
            setCollection(AbstractMapTest.this.getMap().entrySet());
            TestMapEntrySet.this.setConfirmed(AbstractMapTest.this.getConfirmed().entrySet());
        }

        @Override
        public void resetFull() {
            AbstractMapTest.this.resetFull();
            setCollection(AbstractMapTest.this.getMap().entrySet());
            TestMapEntrySet.this.setConfirmed(AbstractMapTest.this.getConfirmed().entrySet());
        }

        @Test
        public void testMapEntrySetIteratorEntry() {
            resetFull();
            int count = 0;
            for (final Entry<K, V> entry : getCollection()) {
                assertTrue(AbstractMapTest.this.getMap().containsKey(entry.getKey()));
                assertTrue(AbstractMapTest.this.getMap().containsValue(entry.getValue()));
                if (!isGetStructuralModify()) {
                    assertEquals(AbstractMapTest.this.getMap().get(entry.getKey()), entry.getValue());
                }
                count++;
            }
            assertEquals(getCollection().size(), count);
        }

        @Test
        public void testMapEntrySetIteratorEntrySetValue() {
            final K key1 = getSampleKeys()[0];
            final K key2 = getSampleKeys().length == 1 ? getSampleKeys()[0] : getSampleKeys()[1];
            final V newValue1 = getNewSampleValues()[0];
            final V newValue2 = getNewSampleValues().length == 1 ? getNewSampleValues()[0] : getNewSampleValues()[1];

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
                assertThrows(UnsupportedOperationException.class, () -> entry1.setValue(newValue1));
                return;
            }

            entry1.setValue(newValue1);
            entryConfirmed1.setValue(newValue1);
            assertEquals(newValue1, entry1.getValue());
            assertTrue(AbstractMapTest.this.getMap().containsKey(entry1.getKey()));
            assertTrue(AbstractMapTest.this.getMap().containsValue(newValue1));
            assertEquals(newValue1, AbstractMapTest.this.getMap().get(entry1.getKey()));
            verify();

            entry1.setValue(newValue1);
            entryConfirmed1.setValue(newValue1);
            assertEquals(newValue1, entry1.getValue());
            assertTrue(AbstractMapTest.this.getMap().containsKey(entry1.getKey()));
            assertTrue(AbstractMapTest.this.getMap().containsValue(newValue1));
            assertEquals(newValue1, AbstractMapTest.this.getMap().get(entry1.getKey()));
            verify();

            entry2.setValue(newValue2);
            entryConfirmed2.setValue(newValue2);
            assertEquals(newValue2, entry2.getValue());
            assertTrue(AbstractMapTest.this.getMap().containsKey(entry2.getKey()));
            assertTrue(AbstractMapTest.this.getMap().containsValue(newValue2));
            assertEquals(newValue2, AbstractMapTest.this.getMap().get(entry2.getKey()));
            verify();
        }

        @Test
        public void testMapEntrySetRemoveNonMapEntry() {
            if (!isRemoveSupported()) {
                return;
            }
            resetFull();
            assertFalse(getCollection().remove(null));
            assertFalse(getCollection().remove(new Object()));
        }

        @Override
        public void verify() {
            super.verify();
            AbstractMapTest.this.verify();
        }
    }

    public class TestMapKeySet extends AbstractSetTest<K> {

        @Override
        public K[] getFullElements() {
            return getSampleKeys();
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractMapTest.this.getIterationBehaviour();
        }

        @Override
        public K[] getOtherElements() {
            return getOtherKeys();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMapTest.this.isAllowNullKey();
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
        public Set<K> makeFullCollection() {
            return AbstractMapTest.this.makeFullMap().keySet();
        }

        @Override
        public Set<K> makeObject() {
            return AbstractMapTest.this.makeObject().keySet();
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

    // These instance variables are initialized with the reset method.
    // Tests for map methods that alter the map (put, putAll, remove)
    // first call reset() to create the map and its views; then perform
    // the modification on the map; perform the same modification on the
    // confirmed; and then call verify() to ensure that the map is equal
    // to the confirmed, that the already-constructed collection views
    // are still equal to the confirmed's collection views.

    public class TestMapValues extends AbstractCollectionTest<V> {

        @Override
        public boolean areEqualElementsDistinguishable() {
            // equal values are associated with different keys, so they are
            // distinguishable.
            return true;
        }

        @Override
        public V[] getFullElements() {
            return getSampleValues();
        }

        @Override
        protected int getIterationBehaviour() {
            return AbstractMapTest.this.getIterationBehaviour();
        }

        @Override
        public V[] getOtherElements() {
            return getOtherValues();
        }

        @Override
        public boolean isAddSupported() {
            return false;
        }

        @Override
        public boolean isNullSupported() {
            return AbstractMapTest.this.isAllowNullKey();
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
        public Collection<V> makeFullCollection() {
            return AbstractMapTest.this.makeFullMap().values();
        }

        @Override
        public Collection<V> makeObject() {
            return AbstractMapTest.this.makeObject().values();
        }

        @Override
        public void resetEmpty() {
            AbstractMapTest.this.resetEmpty();
            setCollection(map.values());
            TestMapValues.this.setConfirmed(AbstractMapTest.this.getConfirmed().values());
        }

        @Override
        public void resetFull() {
            AbstractMapTest.this.resetFull();
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
     * Creates a new Map Entry that is independent of the first and the map.
     *
     * @param <K> the key type.
     * @param <V> the value type.
     */
    public static <K, V> Map.Entry<K, V> cloneMapEntry(final Map.Entry<K, V> entry) {
        final HashMap<K, V> map = new HashMap<>();
        map.put(entry.getKey(), entry.getValue());
        return map.entrySet().iterator().next();
    }

    /** Map created by reset(). */
    protected M map;

    /** Entry set of map created by reset(). */
    protected Set<Map.Entry<K, V>> entrySet;

    /** Key set of map created by reset(). */
    protected Set<K> keySet;

    /** Values collection of map created by reset(). */
    protected Collection<V> values;

    /** HashMap created by reset(). */
    protected Map<K, V> confirmed;

    /**
     * Helper method to add all the mappings described by {@link #getSampleKeys()} and {@link #getSampleValues()}.
     */
    public void addSampleMappings(final Map<? super K, ? super V> m) {

        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            try {
                m.put(keys[i], values[i]);
            } catch (final NullPointerException exception) {
                assertTrue(keys[i] == null || values[i] == null, "NullPointerException only allowed to be thrown " + "if either the key or value is null.");

                assertTrue(keys[i] == null || !isAllowNullKey(),
                        "NullPointerException on null key, but " + "isAllowNullKey is not overridden to return false.");

                assertTrue(values[i] == null || !isAllowNullValue(),
                        "NullPointerException on null value, but " + "isAllowNullValue is not overridden to return false.");

                fail("Unknown reason for NullPointer.");
            }
        }
        assertEquals(keys.length, m.size(), "size must reflect number of mappings added.");
    }

    public boolean areEqualElementsDistinguishable() {
        return false;
    }

    /**
     * Bulk test {@link Map#entrySet()}. This method runs through all of the tests in {@link AbstractSetTest}. After modification operations, {@link #verify()}
     * is invoked to ensure that the map and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the map's entry set
     */
    public BulkTest bulkTestMapEntrySet() {
        return new TestMapEntrySet();
    }

    /**
     * Bulk test {@link Map#keySet()}. This method runs through all of the tests in {@link AbstractSetTest}. After modification operations, {@link #verify()} is
     * invoked to ensure that the map and the other collection views are still valid.
     *
     * @return a {@link AbstractSetTest} instance for testing the map's key set
     */
    public BulkTest bulkTestMapKeySet() {
        return new TestMapKeySet();
    }

    /**
     * Bulk test {@link Map#values()}. This method runs through all of the tests in {@link AbstractCollectionTest}. After modification operations,
     * {@link #verify()} is invoked to ensure that the map and the other collection views are still valid.
     *
     * @return a {@link AbstractCollectionTest} instance for testing the map's values collection
     */
    public BulkTest bulkTestMapValues() {
        return new TestMapValues();
    }

    /**
     * Subclasses can override for special cases, like Apache Commons BeanUtils.
     *
     * @param key             See @{link {@link Map#computeIfAbsent(Object, Function)}.
     * @param mappingFunction See @{link {@link Map#computeIfAbsent(Object, Function)}.
     * @return See @{link {@link Map#computeIfAbsent(Object, Function)}.
     */
    protected V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        return getMap().computeIfAbsent(key, mappingFunction);
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
     * Gets the compatibility version, needed for package access.
     */
    @Override
    public String getCompatibilityVersion() {
        return super.getCompatibilityVersion();
    }

    /**
     * Gets the confirmed.
     *
     * @return Map<K, V>
     */
    public Map<K, V> getConfirmed() {
        return confirmed;
    }

    /**
     * Return a flag specifying the iteration behavior of the collection. This is used to change the assertions used by specific tests. The default
     * implementation returns 0 which indicates ordered iteration behavior.
     *
     * @return the iteration behavior
     * @see AbstractCollectionTest#UNORDERED
     */
    protected int getIterationBehaviour() {
        return 0;
    }

    /**
     * Gets the map.
     *
     * @return Map<K, V>
     */
    public M getMap() {
        return map;
    }

    /**
     * Returns a set of values that can be used to replace the values returned from {@link #getSampleValues()}. This method must return an array with the same
     * length as {@link #getSampleValues()}. The values returned from this method should not be the same as those returned from {@link #getSampleValues()}. The
     * default implementation constructs a set of String values and includes a single null value if {@link #isAllowNullValue()} returns {@code true}, and
     * includes two values that are the same if {@link #isAllowDuplicateValues()} returns {@code true}.
     */
    @SuppressWarnings("unchecked")
    public V[] getNewSampleValues() {
        final Object[] result = { isAllowNullValue() && isAllowDuplicateValues() ? null : "newnonnullvalue", "newvalue",
                isAllowDuplicateValues() ? "newvalue" : "newvalue2", "newblahv", "newfoov", "newbarv", "newbazv", "newtmpv", "newgoshv", "newgollyv", "newgeev",
                "newhellov", "newgoodbyev", "newwe'llv", "newseev", "newyouv", "newallv", "newagainv" };
        return (V[]) result;
    }

    @SuppressWarnings("unchecked")
    public K[] getOtherKeys() {
        return (K[]) getOtherNonNullStringElements();
    }

    /**
     * Returns a list of string elements suitable for return by {@link #getOtherKeys()} or {@link #getOtherValues}.
     *
     * <p>
     * Override getOtherElements to return the results of this method if your collection does not support heterogeneous elements or the null element.
     * </p>
     */
    public Object[] getOtherNonNullStringElements() {
        return new Object[] { "For", "then", "despite", /* of */"space", "I", "would", "be", "brought", "From", "limits", "far", "remote", "where", "thou",
                "dost", "stay" };
    }

    @SuppressWarnings("unchecked")
    public V[] getOtherValues() {
        return (V[]) getOtherNonNullStringElements();
    }

    /**
     * Returns the set of keys in the mappings used to test the map. This method must return an array with the same length as {@link #getSampleValues()} and all
     * array elements must be different. The default implementation constructs a set of String keys, and includes a single null key if {@link #isAllowNullKey()}
     * returns {@code true}.
     */
    @SuppressWarnings("unchecked")
    public K[] getSampleKeys() {
        final Object[] result = { "blah", "foo", "bar", "baz", "tmp", "gosh", "golly", "gee", "hello", "goodbye", "we'll", "see", "you", "all", "again", "key",
                "key2", isAllowNullKey() ? null : "nonnullkey" };
        return (K[]) result;
    }

    /**
     * Returns the set of values in the mappings used to test the map. This method must return an array with the same length as {@link #getSampleKeys()}. The
     * default implementation constructs a set of String values and includes a single null value if {@link #isAllowNullValue()} returns {@code true}, and
     * includes two values that are the same if {@link #isAllowDuplicateValues()} returns {@code true}.
     */
    @SuppressWarnings("unchecked")
    public V[] getSampleValues() {
        final Object[] result = { "blahv", "foov", "barv", "bazv", "tmpv", "goshv", "gollyv", "geev", "hellov", "goodbyev", "we'llv", "seev", "youv", "allv",
                "againv", isAllowNullValue() ? null : "nonnullvalue", "value", isAllowDuplicateValues() ? "value" : "value2", };
        return (V[]) result;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} supports duplicate values.
     * <p>
     * Default implementation returns true. Override if your collection class does not support duplicate values.
     * </p>
     *
     * @return true by default.
     */
    public boolean isAllowDuplicateValues() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} supports null keys.
     * <p>
     * Default implementation returns true. Override if your collection class does not support null keys.
     * </p>
     *
     * @return true by default.
     */
    public boolean isAllowNullKey() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} supports null values.
     * <p>
     * Default implementation returns true. Override if your collection class does not support null values.
     * </p>
     */
    public boolean isAllowNullValue() {
        return isAllowNullValueGet() && isAllowNullValuePut();
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} supports null values.
     * <p>
     * Default implementation returns true. Override if your collection class does not support null values.
     * </p>
     *
     * @return true by default.
     */
    public boolean isAllowNullValueGet() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} supports null values.
     * <p>
     * Default implementation returns true. Override if your collection class does not support null values.
     * </p>
     *
     * @return true by default.
     */
    public boolean isAllowNullValuePut() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} provide fail-fast behavior on their various iterators.
     * <p>
     * Default implementation returns true. Override if your collection class does not support fast failure.
     * </p>
     *
     * @return true by default.
     */
    public boolean isFailFastExpected() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} can cause structural modification on a get(). The example is
     * LRUMap.
     * <p>
     * Default implementation returns false. Override if your map class structurally modifies on get.
     * </p>
     *
     * @return false by default.
     */
    public boolean isGetStructuralModify() {
        return false;
    }

    // tests begin here. Each test adds a little bit of tested functionality.
    // Many methods assume previous methods passed. That is, they do not
    // exhaustively recheck things that have already been checked in a previous
    // test methods.

    protected boolean isLazyMapTest() {
        return false;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} support the {@link Map#put(Object, Object)} and
     * {@link Map#putAll(Map)} operations adding new mappings.
     * <p>
     * Default implementation returns true. Override if your collection class does not support put adding.
     * </p>
     *
     * @return true by default.
     */
    public boolean isPutAddSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} support the {@link Map#put(Object, Object)} and
     * {@link Map#putAll(Map)} operations changing existing mappings.
     * <p>
     * Default implementation returns true. Override if your collection class does not support put changing.
     * </p>
     *
     * @return true by default.
     */
    public boolean isPutChangeSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} support the {@code remove} and {@link Map#clear()} operations.
     * <p>
     * Default implementation returns true. Override if your collection class does not support removal operations.
     * </p>
     *
     * @return true by default.
     */
    public boolean isRemoveSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} support the {@link Map#replaceAll(java.util.function.BiFunction)}
     * operations.
     * <p>
     * Default implementation returns true. Override if your collection class does not support replaceAll operations.
     * </p>
     *
     * @return true by default.
     */
    public boolean isReplaceAllSupported() {
        return true;
    }

    /**
     * Returns true if the maps produced by {@link #makeObject()} and {@link #makeFullMap()} support the {@code setValue} operation on entrySet entries.
     * <p>
     * Default implementation returns isPutChangeSupported(). Override if your collection class does not support setValue but does support put changing.
     * </p>
     */
    public boolean isSetValueSupported() {
        return isPutChangeSupported();
    }

    /**
     * Returns whether the sub map views of SortedMap are serializable. If the class being tested is based around a TreeMap then you should override and return
     * false as TreeMap has a bug in deserialization.
     *
     * @return true by default.
     */
    public boolean isSubMapViewsSerializable() {
        return true;
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
     * Utility methods to create an array of Map.Entry objects out of the given key and value arrays.
     *
     * @param keys   the array of keys
     * @param values the array of values
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
     * Return a new, populated map. The mappings in the map should match the keys and values returned from {@link #getSampleKeys()} and
     * {@link #getSampleValues()}. The default implementation uses makeEmptyMap() and calls {@link #addSampleMappings} to add all the mappings to the map.
     *
     * @return the map to be tested
     */
    public M makeFullMap() {
        final M m = makeObject();
        addSampleMappings(m);
        return m;
    }

    /**
     * Return a new, empty {@link Map} to be used for testing.
     *
     * @return the map to be tested
     */
    @Override
    public abstract M makeObject();

    /**
     * Subclasses can override for special cases, like Apache Commons BeanUtils.
     *
     * @param key   See @{link {@link Map#putIfAbsent(Object, Object)}.
     * @param value See @{link {@link Map#putIfAbsent(Object, Object)}.
     * @return See @{link {@link Map#putIfAbsent(Object, Object)}.
     */
    protected V putIfAbsent(final K key, final V value) {
        return getMap().putIfAbsent(key, value);
    }

    /**
     * Resets the {@link #map}, {@link #entrySet}, {@link #keySet}, {@link #values} and {@link #confirmed} fields to empty.
     */
    public void resetEmpty() {
        map = makeObject();
        views();
        confirmed = makeConfirmedMap();
    }

    /**
     * Resets the {@link #map}, {@link #entrySet}, {@link #keySet}, {@link #values} and {@link #confirmed} fields to full.
     */
    public void resetFull() {
        map = makeFullMap();
        views();
        confirmed = makeConfirmedMap();
        final K[] k = getSampleKeys();
        final V[] v = getSampleValues();
        for (int i = 0; i < k.length; i++) {
            confirmed.put(k[i], v[i]);
        }
    }

    /**
     * Erases any leftover instance variables by setting them to null.
     */
    @AfterEach
    public void tearDown() throws Exception {
        map = null;
        keySet = null;
        entrySet = null;
        values = null;
        confirmed = null;
    }

    /**
     * Compare the current serialized form of the Map against the canonical version in SCM.
     */
    @Test
    public void testEmptyMapCompatibility() throws Exception {
        /*
         * Create canonical objects with this code Map map = makeEmptyMap(); if (!(map instanceof Serializable)) return;
         *
         * writeExternalFormToDisk((Serializable) map, getCanonicalEmptyCollectionName(map));
         */

        // test to make sure the canonical form has been preserved
        final Map<K, V> map = makeObject();
        if (map instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            @SuppressWarnings("unchecked")
            final Map<K, V> map2 = (Map<K, V>) readExternalFormFromDisk(getCanonicalEmptyCollectionName(map));
            assertEquals(0, map2.size(), "Map is empty");
        }
    }

    /**
     * Tests that the {@link Map#entrySet()} collection is backed by the underlying map for clear().
     */
    @Test
    public void testEntrySetClearChangesMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // clear values, reflected in map
        resetFull();
        Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        assertFalse(getMap().isEmpty());
        assertFalse(entrySet.isEmpty());
        entrySet.clear();
        assertTrue(getMap().isEmpty());
        assertTrue(entrySet.isEmpty());

        // clear map, reflected in values
        resetFull();
        entrySet = getMap().entrySet();
        assertFalse(getMap().isEmpty());
        assertFalse(entrySet.isEmpty());
        getMap().clear();
        assertTrue(getMap().isEmpty());
        assertTrue(entrySet.isEmpty());
    }

    @Test
    public void testEntrySetContains1() {
        resetFull();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        assertTrue(entrySet.contains(entry));
    }

    @Test
    public void testEntrySetContains2() {
        resetFull();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final Map.Entry<K, V> test = cloneMapEntry(entry);
        assertTrue(entrySet.contains(test));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEntrySetContains3() {
        resetFull();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final HashMap<K, V> temp = new HashMap<>();
        temp.put(entry.getKey(), (V) "A VERY DIFFERENT VALUE");
        final Map.Entry<K, V> test = temp.entrySet().iterator().next();
        assertFalse(entrySet.contains(test));
    }

    /**
     * Verify that entrySet.iterator.remove changes the underlying map.
     */
    @Test
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

    @Test
    public void testEntrySetRemove1() {
        if (!isRemoveSupported()) {
            return;
        }
        resetFull();
        final int size = getMap().size();
        final Set<Map.Entry<K, V>> entrySet = getMap().entrySet();
        final Map.Entry<K, V> entry = entrySet.iterator().next();
        final K key = entry.getKey();

        assertTrue(entrySet.remove(entry));
        assertFalse(getMap().containsKey(key));
        assertEquals(size - 1, getMap().size());
    }

    @Test
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

        assertTrue(entrySet.remove(test));
        assertFalse(getMap().containsKey(key));
        assertEquals(size - 1, getMap().size());
    }

    @Test
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

        assertFalse(entrySet.remove(test));
        assertTrue(getMap().containsKey(key));
        assertEquals(size, getMap().size());
    }

    /**
     * Test entrySet.removeAll.
     */
    @Test
    public void testEntrySetRemoveAll() {
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        // verify map looks as expected:
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
            assertFalse(entrySet.removeAll(Collections.<Map.Entry<K, V>>emptySet()));
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
     * Tests that the {@link Map#entrySet} set is backed by the underlying map by removing from the entrySet set and testing if the entry was removed from the
     * map.
     */
    @Test
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
            assertFalse(getMap().containsKey(sampleKeys[i]), "Entry should have been removed from the underlying map.");
        }
    }

    /**
     * Test entrySet.retainAll.
     */
    @Test
    public void testEntrySetRetainAll() {
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        // verify map looks as expected:
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
            assertTrue(entrySet.retainAll(Collections.<Map.Entry<K, V>>emptySet()));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Tests {@link Map#forEach(java.util.function.BiConsumer)}.
     */
    @Test
    public void testForEach() {
        resetFull();
        final AtomicInteger i = new AtomicInteger();
        getMap().forEach((k, v) -> {
            assertTrue(getMap().containsKey(k));
            assertTrue(getMap().containsValue(v));
            i.incrementAndGet();
        });
        assertEquals(i.get(), getMap().size());
    }

    /**
     * Compare the current serialized form of the Map against the canonical version in SCM.
     */
    @Test
    public void testFullMapCompatibility() throws Exception {
        /*
         * Create canonical objects with this code Map map = makeFullMap(); if (!(map instanceof Serializable)) return;
         *
         * writeExternalFormToDisk((Serializable) map, getCanonicalFullCollectionName(map));
         */

        // test to make sure the canonical form has been preserved
        final Map<K, V> map = makeFullMap();
        if (map instanceof Serializable && !skipSerializedCanonicalTests() && isTestSerialization()) {
            @SuppressWarnings("unchecked")
            final Map<K, V> map2 = (Map<K, V>) readExternalFormFromDisk(getCanonicalFullCollectionName(map));
            assertEquals(getSampleKeys().length, map2.size(), "Map is the right size");
        }
    }

    /**
     * Tests that the {@link Map#keySet} collection is backed by the underlying map for clear().
     */
    @Test
    public void testKeySetClearChangesMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // clear values, reflected in map
        resetFull();
        Set<K> keySet = getMap().keySet();
        assertFalse(getMap().isEmpty());
        assertFalse(keySet.isEmpty());
        keySet.clear();
        assertTrue(getMap().isEmpty());
        assertTrue(keySet.isEmpty());

        // clear map, reflected in values
        resetFull();
        keySet = getMap().keySet();
        assertFalse(getMap().isEmpty());
        assertFalse(keySet.isEmpty());
        getMap().clear();
        assertTrue(getMap().isEmpty());
        assertTrue(keySet.isEmpty());
    }

    /**
     * Verify that keySet.iterator.remove changes the underlying map.
     */
    @Test
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
     * Test keySet.removeAll.
     */
    @Test
    public void testKeySetRemoveAll() {
        resetFull();
        final Set<K> keys = getMap().keySet();
        final List<K> sampleKeysAsList = Arrays.asList(getSampleKeys());
        if (!keys.equals(sampleKeysAsList)) {
            return;
        }
        try {
            assertFalse(keys.removeAll(Collections.<K>emptySet()));
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
     * Tests that the {@link Map#keySet} set is backed by the underlying map by removing from the keySet set and testing if the key was removed from the map.
     */
    @Test
    public void testKeySetRemoveChangesMap() {
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final Set<K> keys = getMap().keySet();
        for (final K sampleKey : sampleKeys) {
            try {
                keys.remove(sampleKey);
            } catch (final UnsupportedOperationException e) {
                // if key.remove is unsupported, just skip this test
                return;
            }
            assertFalse(getMap().containsKey(sampleKey), "Key should have been removed from the underlying map.");
        }
    }

    /**
     * Test keySet.retainAll.
     */
    @Test
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
            assertTrue(keys.retainAll(Collections.<K>emptySet()));
        } catch (final UnsupportedOperationException e) {
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Test to ensure that makeEmptyMap and makeFull returns a new non-null map with each invocation.
     */
    @Test
    public void testMakeMap() {
        final Map<K, V> em = makeObject();
        assertNotNull(em, "failure in test: makeEmptyMap must return a non-null map.");

        final Map<K, V> em2 = makeObject();
        assertNotNull(em, "failure in test: makeEmptyMap must return a non-null map.");

        assertNotSame(em, em2, "failure in test: makeEmptyMap must return a new map " + "with each invocation.");

        final Map<K, V> fm = makeFullMap();
        assertNotNull(fm, "failure in test: makeFullMap must return a non-null map.");

        final Map<K, V> fm2 = makeFullMap();
        assertNotNull(fm2, "failure in test: makeFullMap must return a non-null map.");

        assertNotSame(fm, fm2, "failure in test: makeFullMap must return a new map " + "with each invocation.");
    }

    /**
     * Tests {@link Map#clear()}. If the map {@link #isRemoveSupported() can add and remove elements}, then {@link Map#size()} and {@link Map#isEmpty()} are
     * used to ensure that map has no elements after a call to clear. If the map does not support adding and removing elements, this method checks to ensure
     * clear throws an UnsupportedOperationException.
     */
    @Test
    public void testMapClear() {
        if (!isRemoveSupported()) {
            resetFull();
            assertThrows(UnsupportedOperationException.class, () -> getMap().clear(), "Expected UnsupportedOperationException on clear");
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
     * Tests {@link Map#computeIfAbsent(Object, java.util.function.Function)}.
     *
     * @see Map#putIfAbsent(Object, Object)
     * @see #testMapPutIfAbsent()
     */
    @Test
    public void testMapComputeIfAbsent() {
        resetEmpty();
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        final V[] newValues = getNewSampleValues();
        if (isPutAddSupported()) {
            for (final AtomicInteger inc = new AtomicInteger(); inc.get() < keys.length; inc.incrementAndGet()) {
                final int i = inc.get();
                final K key = keys[i];
                final V value = values[i];
                final boolean expectKey = key != null && value != null || key == null && !getMap().containsKey(key);
                final Map<K, V> oldMap = new HashMap<>(getMap());
                final Object currValue = computeIfAbsent(key, k -> value);
                // map is updated if new value is not null
                getConfirmed().computeIfAbsent(key, k -> value);
                if (!isLazyMapTest()) {
                    // TODO LazyMap tests do not like this check
                    verify();
                }
                final Supplier<String> messageSupplier = () -> String.format("[%,d] map.computeIfAbsent key '%s', value '%s', old %s", inc.get(), key, value,
                        oldMap);
                assertEquals(value, currValue, messageSupplier);
                if (isLazyMapTest()) {
                    // TODO Is there a better way to write generic tests?
                    assertTrue(getMap().containsKey(key), messageSupplier);
                    assertTrue(getMap().containsValue(value), messageSupplier);
                } else {
                    assertEquals(expectKey, getMap().containsKey(key), messageSupplier);
                    assertEquals(expectKey, getMap().containsValue(value), messageSupplier);
                }
            }
            if (isPutChangeSupported()) {
                // Piggyback on isPutChangeSupported() for computeIfAbsent with a caveat for null values.
                for (final AtomicInteger inc = new AtomicInteger(); inc.get() < keys.length; inc.incrementAndGet()) {
                    final int i = inc.get();
                    final K key = keys[i];
                    final V value = newValues[i];
                    final boolean valueAlreadyPresent = getMap().containsValue(value);
                    final V prevValue = getMap().get(key);
                    final Map<K, V> oldMap = new HashMap<>(getMap());
                    final Object computedValue = computeIfAbsent(key, k -> value);
                    getConfirmed().computeIfAbsent(key, k -> value);
                    if (!isLazyMapTest()) {
                        // TODO LazyMap tests do not like this check
                        verify();
                    }
                    final V arrValue = values[i];
                    final Supplier<String> messageSupplier = () -> String.format("[%,d] map.computeIfAbsent key '%s', value '%s', old %s", inc.get(), key,
                            value, oldMap);
                    if (valueAlreadyPresent || key == null) {
                        assertNotEquals(value, computedValue, messageSupplier);
                    } else if (prevValue != null && value != null) {
                        assertEquals(prevValue, computedValue, messageSupplier);
                    } else if (prevValue == null) {
                        assertEquals(value, computedValue, messageSupplier);
                    } else if (value == null) {
                        assertEquals(prevValue, computedValue, messageSupplier);
                    }
                    if (prevValue == null) {
                        assertEquals(value, getMap().get(key), messageSupplier);
                    } else {
                        assertEquals(computedValue, getMap().get(key), messageSupplier);
                    }
                    assertTrue(getMap().containsKey(key), messageSupplier);
                    if (valueAlreadyPresent && value != null) {
                        // TODO The test fixture already contain a null value, so we condition this assertion
                        assertFalse(getMap().containsValue(value), messageSupplier);
                    }
                    // if duplicates are allowed, we're not guaranteed that the value
                    // no longer exists, so don't try checking that.
                    if (!isAllowDuplicateValues() && valueAlreadyPresent && value != null) {
                        assertFalse(getMap().containsValue(arrValue),
                                String.format(
                                        "Map should not contain old value after computeIfAbsent when changed: [%,d] key '%s', prevValue '%s', newValue '%s'", i,
                                        key, prevValue, value));
                    }
                }
            } else {
                try {
                    // two possible exception here, either valid
                    computeIfAbsent(keys[0], k -> newValues[0]);
                    fail("Expected IllegalArgumentException or UnsupportedOperationException on putIfAbsent (change)");
                } catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                    // ignore
                }
            }
        } else if (isPutChangeSupported()) {
            resetEmpty();
            try {
                computeIfAbsent(keys[0], k -> values[0]);
                fail("Expected UnsupportedOperationException or IllegalArgumentException on putIfAbsent (add) when fixed size");
            } catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                // ignore
            }
            resetFull();
            int i = 0;
            for (final Iterator<K> it = getMap().keySet().iterator(); it.hasNext() && i < newValues.length; i++) {
                final K key = it.next();
                final V newValue = newValues[i];
                final boolean newValueAlready = getMap().containsValue(newValue);
                final V prevValue = getMap().get(key);
                final V oldValue = getMap().putIfAbsent(key, newValue);
                final V value = getConfirmed().putIfAbsent(key, newValue);
                verify();
                assertEquals(value, oldValue, "Map.putIfAbsent should return previous value when changed");
                assertEquals(prevValue, oldValue, "Map.putIfAbsent should return previous value when changed");
                if (prevValue == null) {
                    assertEquals(newValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                } else {
                    assertEquals(oldValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                }
                assertTrue(getMap().containsKey(key), "Map should still contain key after putIfAbsent when changed");
                if (newValueAlready && newValue != null) {
                    // TODO The test fixture already contain a null value, so we condition this assertion
                    assertFalse(getMap().containsValue(newValue),
                            String.format("[%,d] Map at '%s' shouldn't contain new value '%s' after putIfAbsent when changed", i, key, newValue));
                }
                // if duplicates are allowed, we're not guaranteed that the value
                // no longer exists, so don't try checking that.
                if (!isAllowDuplicateValues()) {
                    assertFalse(getMap().containsValue(values[i]), "Map should not contain old value after putIfAbsent when changed");
                }
            }
        } else {
            assertThrows(UnsupportedOperationException.class, () -> getMap().putIfAbsent(keys[0], values[0]),
                    "Expected UnsupportedOperationException on put (add)");
        }
    }

    /**
     * Tests {@link Map#computeIfPresent(Object, java.util.function.BiFunction)}.
     *
     * @see Map#computeIfPresent(Object, java.util.function.BiFunction)
     * @see #testMapComputeIfPresent()
     */
    @Test
    public void testMapComputeIfPresent() {
        resetEmpty();
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        final V[] newValues = getNewSampleValues();
        if (isPutAddSupported()) {
            for (final AtomicInteger inc = new AtomicInteger(); inc.get() < keys.length; inc.incrementAndGet()) {
                final int i = inc.get();
                final K key = keys[i];
                final V value = values[i];
                final boolean expectKey = getMap().containsKey(key);
                final Map<K, V> oldMap = new HashMap<>(getMap());
                final Object computedValue = getMap().computeIfPresent(key, (k, v) -> value);
                // map is updated if new or old value is not null
                getConfirmed().computeIfPresent(key, (k, v) -> value);
                if (!isLazyMapTest()) {
                    // TODO LazyMap tests do not like this check
                    verify();
                }
                final Supplier<String> messageSupplier = () -> String.format("[%,d] map.computeIfPresent key '%s', value '%s', old %s", inc.get(), key, value,
                        oldMap);
                assertNull(computedValue, messageSupplier);
                if (isLazyMapTest()) {
                    // TODO Is there a better way to write abstract tests?
                    assertTrue(getMap().containsKey(key), messageSupplier);
                    assertEquals(value == null, getMap().containsValue(value), messageSupplier);
                } else {
                    assertEquals(expectKey, getMap().containsKey(key), messageSupplier);
                    assertEquals(expectKey, getMap().containsValue(value), messageSupplier);
                }
                // again, same result
                getConfirmed().computeIfPresent(key, (k, v) -> value);
                assertNull(computedValue, messageSupplier);
            }
            if (isPutChangeSupported()) {
                // Piggyback on isPutChangeSupported() for computeIfAbsent with a caveat for null values.
                for (final AtomicInteger inc = new AtomicInteger(); inc.get() < keys.length; inc.incrementAndGet()) {
                    final int i = inc.get();
                    final K key = keys[i];
                    final V value = values[i];
                    final V newValue = newValues[i];
                    final boolean valueAlreadyPresent = getMap().containsValue(newValue);
                    final V prevValue = getMap().get(key);
                    final Map<K, V> oldMap = new HashMap<>(getMap());
                    final Supplier<String> messageSupplier0 = () -> String.format("[%,d] map.computeIfPresent key '%s', value '%s', old %s", inc.get(), key,
                            value, oldMap);
                    final Supplier<String> messageSupplier1 = () -> String.format("[%,d] map.computeIfPresent key '%s', newValue '%s', old %s", inc.get(), key,
                            newValue, oldMap);
                    // compute same
                    oldMap.clear();
                    oldMap.putAll(getMap());
                    getMap().put(key, value);
                    getConfirmed().put(key, value);
                    V computedValue = getMap().computeIfPresent(key, (k, v) -> value);
                    getConfirmed().computeIfPresent(key, (k, v) -> value);
                    if (value != null) {
                        assertNotNull(computedValue, messageSupplier0);
                    } else {
                        assertNull(computedValue, messageSupplier0);
                    }
                    // compute new (mod null)
                    oldMap.clear();
                    oldMap.putAll(getMap());
                    final boolean keyPresent = getMap().containsKey(key);
                    computedValue = getMap().computeIfPresent(key, (k, v) -> newValue);
                    getConfirmed().computeIfPresent(key, (k, v) -> newValue);
                    if (!isLazyMapTest()) {
                        // TODO LazyMap tests do not like this check
                        verify();
                    }
                    if (keyPresent && value != null) {
                        assertEquals(newValue, computedValue, messageSupplier1);
                    } else {
                        assertNull(computedValue, messageSupplier1);
                    }
                    assertEquals(newValue != null, getMap().containsKey(key), messageSupplier1);
                    // if duplicates are allowed, we're not guaranteed that the value
                    // no longer exists, so don't try checking that.
                    if (!isAllowDuplicateValues() && valueAlreadyPresent && newValue != null) {
                        assertFalse(getMap().containsValue(value),
                                String.format(
                                        "Map should not contain old value after computeIfPresent when changed: [%,d] key '%s', prevValue '%s', newValue '%s'",
                                        i, key, prevValue, newValue));
                    }
                }
            } else {
                try {
                    // two possible exception here, either valid
                    getMap().computeIfPresent(keys[0], (k, v) -> newValues[0]);
                    fail("Expected IllegalArgumentException or UnsupportedOperationException on putIfAbsent (change)");
                } catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                    // ignore
                }
            }
        } else if (isPutChangeSupported()) {
            // compute if present is a put.
            resetEmpty();
            getMap().computeIfPresent(keys[0], (k, v) -> values[0]);
            if (isPutAddSupported()) {
                resetFull();
                int i = 0;
                for (final Iterator<K> it = getMap().keySet().iterator(); it.hasNext() && i < newValues.length; i++) {
                    final K key = it.next();
                    final V newValue = newValues[i];
                    final boolean newValueAlready = getMap().containsValue(newValue);
                    final V prevValue = getMap().get(key);
                    final V oldValue = getMap().computeIfPresent(key, (k, v) -> newValue);
                    final V value = getConfirmed().computeIfPresent(key, (k, v) -> newValue);
                    verify();
                    assertEquals(value, oldValue, "Map.putIfAbsent should return previous value when changed");
                    assertEquals(prevValue, oldValue, "Map.putIfAbsent should return previous value when changed");
                    if (prevValue == null) {
                        assertEquals(newValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                    } else {
                        assertEquals(oldValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                    }
                    assertTrue(getMap().containsKey(key), "Map should still contain key after putIfAbsent when changed");
                    if (newValueAlready && newValue != null) {
                        // TODO The test fixture already contain a null value, so we condition this assertion
                        assertFalse(getMap().containsValue(newValue),
                                String.format("[%,d] Map at '%s' shouldn't contain new value '%s' after putIfAbsent when changed", i, key, newValue));
                    }
                    // if duplicates are allowed, we're not guaranteed that the value
                    // no longer exists, so don't try checking that.
                    if (!isAllowDuplicateValues()) {
                        assertFalse(getMap().containsValue(values[i]), "Map should not contain old value after putIfAbsent when changed");
                    }
                }
            }
        } else if (getMap().containsKey(keys[0])) {
            assertThrows(UnsupportedOperationException.class, () -> getMap().computeIfPresent(keys[0], (k, v) -> values[0]),
                    "Expected UnsupportedOperationException on put (add)");
        } else {
            // doesn't throw
            getMap().computeIfPresent(keys[0], (k, v) -> values[0]);
        }
    }

    @Test
    public void testMapComputeIfPresentOnEmpty() {
        resetEmpty();
        assertTrue(getMap().isEmpty());
        final K[] otherKeys = getOtherKeys();
        final V[] otherValues = getOtherValues();
        final K key = otherKeys[0];
        assertFalse(getMap().containsKey(key));
        assertNull(getMap().computeIfPresent(key, (k, v) -> otherValues[0]));
        assertEquals(isLazyMapTest(), getMap().containsKey(key));
        assertEquals(!isLazyMapTest(), getMap().isEmpty());
    }

    /**
     * Tests Map.containsKey(Object) by verifying it returns false for all sample keys on a map created using an empty map and returns true for all sample keys
     * returned on a full map.
     */
    @Test
    public void testMapContainsKey() {
        final Object[] keys = getSampleKeys();

        resetEmpty();
        for (final Object key : keys) {
            assertFalse(getMap().containsKey(key), "Map must not contain key when map is empty");
        }
        verify();

        resetFull();
        for (final Object key : keys) {
            assertTrue(getMap().containsKey(key), "Map must contain key for a mapping in the map. " + "Missing: " + key);
        }
        verify();
    }

    /**
     * Tests Map.containsValue(Object) by verifying it returns false for all sample values on an empty map and returns true for all sample values on a full map.
     */
    @Test
    public void testMapContainsValue() {
        final Object[] values = getSampleValues();
        resetEmpty();
        if (isAllowNullValueGet()) {
            assertFalse(getMap().containsValue(null));
        } else {
            assertThrows(NullPointerException.class, () -> getMap().containsValue(null));
        }
        for (final Object value : values) {
            assertFalse(getMap().containsValue(value), "Empty map must not contain value");
        }
        verify();

        resetFull();
        for (final Object value : values) {
            assertTrue(getMap().containsValue(value), "Map must contain value for a mapping in the map.");
        }
        verify();
    }

    /**
     * Tests Map.equals(Object)
     */
    @Test
    public void testMapEquals() {
        resetEmpty();
        assertEquals(getMap(), confirmed, "Empty maps unequal.");
        verify();

        resetFull();
        assertEquals(getMap(), confirmed, "Full maps unequal.");
        verify();

        resetFull();
        // modify the HashMap created from the full map and make sure this
        // change results in map.equals() to return false.
        final Iterator<K> iter = confirmed.keySet().iterator();
        iter.next();
        iter.remove();
        assertFalse(getMap().equals(confirmed), "Different maps equal.");

        resetFull();
        assertFalse(getMap().equals(null), "equals(null) returned true.");
        assertFalse(getMap().equals(new Object()), "equals(new Object()) returned true.");
        verify();
    }

    /**
     * Tests Map.get(Object)
     */
    @Test
    public void testMapGet() {
        resetEmpty();

        final Object[] keys = getSampleKeys();
        final Object[] values = getSampleValues();

        for (final Object key : keys) {
            assertNull(getMap().get(key), "Empty map.get() should return null.");
        }
        verify();

        resetFull();
        for (int i = 0; i < keys.length; i++) {
            assertEquals(values[i], getMap().get(keys[i]), "Full map.get() should return value from mapping.");
        }
    }

    /**
     * Tests {@link Map#getOrDefault(Object, Object)}.
     */
    @Test
    public void testMapGetOrDefault() {
        resetEmpty();
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        for (final K key : keys) {
            assertNull(getMap().getOrDefault(key, null));
        }
        final K[] otherKeys = getOtherKeys();
        final V[] otherValues = getOtherValues();
        for (int i = 0; i < otherKeys.length; i++) {
            final K otherKey = otherKeys[i];
            assertNull(getMap().getOrDefault(otherKey, null));
            final V otherValue = otherValues[i];
            if (getMap().containsKey(otherKey)) {
                assertEquals(getMap().get(otherKey), getMap().getOrDefault(otherKey, otherValue));
            } else {
                assertEquals(otherValue, getMap().getOrDefault(otherKey, otherValue));
            }
        }
        if (!isLazyMapTest()) {
            // TODO LazyMap tests do not like this check
            verify();
        }
        resetFull();
        for (int i = 0; i < keys.length; i++) {
            assertEquals(values[i], getMap().getOrDefault(keys[i], values[i]));
        }
    }

    /**
     * Tests Map.hashCode()
     */
    @Test
    public void testMapHashCode() {
        resetEmpty();
        assertEquals(getMap().hashCode(), confirmed.hashCode(), "Empty maps have different hashCodes.");

        resetFull();
        assertEquals(getMap().hashCode(), confirmed.hashCode(), "Equal maps have different hashCodes.");
    }

    /**
     * Tests Map.isEmpty()
     */
    @Test
    public void testMapIsEmpty() {
        resetEmpty();
        assertTrue(getMap().isEmpty(), "Map.isEmpty() should return true with an empty map");
        verify();

        resetFull();
        assertFalse(getMap().isEmpty(), "Map.isEmpty() should return false with a non-empty map");
        verify();
    }

    /**
     * Tests Map.put(Object, Object)
     */
    @Test
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
                assertNull(o, "First map.put should return null");
                assertTrue(getMap().containsKey(keys[i]), "Map should contain key after put");
                assertTrue(getMap().containsValue(values[i]), "Map should contain value after put");
            }
            if (isPutChangeSupported()) {
                for (int i = 0; i < keys.length; i++) {
                    final Object o = getMap().put(keys[i], newValues[i]);
                    getConfirmed().put(keys[i], newValues[i]);
                    verify();
                    assertEquals(values[i], o, "Map.put should return previous value when changed");
                    assertTrue(getMap().containsKey(keys[i]), "Map should still contain key after put when changed");
                    assertTrue(getMap().containsValue(newValues[i]), "Map should contain new value after put when changed");

                    // if duplicates are allowed, we're not guaranteed that the value
                    // no longer exists, so don't try checking that.
                    if (!isAllowDuplicateValues()) {
                        assertFalse(getMap().containsValue(values[i]), "Map should not contain old value after put when changed");
                    }
                }
            } else {
                try {
                    // two possible exception here, either valid
                    getMap().put(keys[0], newValues[0]);
                    fail("Expected IllegalArgumentException or UnsupportedOperationException on put (change)");
                } catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                    // ignore
                }
            }

        } else if (isPutChangeSupported()) {
            resetEmpty();
            try {
                getMap().put(keys[0], values[0]);
                fail("Expected UnsupportedOperationException or IllegalArgumentException on put (add) when fixed size");
            } catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                // ignore
            }

            resetFull();
            int i = 0;
            for (final Iterator<K> it = getMap().keySet().iterator(); it.hasNext() && i < newValues.length; i++) {
                final K key = it.next();
                final V o = getMap().put(key, newValues[i]);
                final V value = getConfirmed().put(key, newValues[i]);
                verify();
                assertEquals(value, o, "Map.put should return previous value when changed");
                assertTrue(getMap().containsKey(key), "Map should still contain key after put when changed");
                assertTrue(getMap().containsValue(newValues[i]), "Map should contain new value after put when changed");

                // if duplicates are allowed, we're not guaranteed that the value
                // no longer exists, so don't try checking that.
                if (!isAllowDuplicateValues()) {
                    assertFalse(getMap().containsValue(values[i]), "Map should not contain old value after put when changed");
                }
            }
        } else {
            assertThrows(UnsupportedOperationException.class, () -> getMap().put(keys[0], values[0]), "Expected UnsupportedOperationException on put (add)");
        }
    }

    /**
     * Tests Map.putAll(map)
     */
    @Test
    public void testMapPutAll() {
        if (!isPutAddSupported()) {
            if (!isPutChangeSupported()) {
                final Map<K, V> temp = makeFullMap();
                resetEmpty();
                assertThrows(UnsupportedOperationException.class, () -> getMap().putAll(temp), "Expected UnsupportedOperationException on putAll");
            }
            return;
        }

        // check putAll OK adding empty map to empty map
        resetEmpty();
        assertEquals(0, getMap().size());
        getMap().putAll(new HashMap<>());
        assertEquals(0, getMap().size());

        // check putAll OK adding empty map to non-empty map
        resetFull();
        final int size = getMap().size();
        getMap().putAll(new HashMap<>());
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
        for (int i = 0; i < keys.length; i++) {
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
        for (int i = 1; i < keys.length; i++) {
            m2.put(keys[i], values[i]);
        }
        getMap().putAll(m2);
        getConfirmed().putAll(m2);
        verify();
    }

    /**
     * Tests {@link Map#putIfAbsent(Object, Object)}.
     *
     * @see Map#computeIfAbsent(Object, java.util.function.Function)
     */
    @Test
    public void testMapPutIfAbsent() {
        resetEmpty();
        final K[] keys = getSampleKeys();
        final V[] values = getSampleValues();
        final V[] newValues = getNewSampleValues();
        if (isPutAddSupported()) {
            for (int i = 0; i < keys.length; i++) {
                final K key = keys[i];
                final V value = values[i];
                final Object o = putIfAbsent(key, value);
                getConfirmed().putIfAbsent(key, value);
                verify();
                assertNull(o, "First map.putIfAbsent should return null");
                assertTrue(getMap().containsKey(key), "Map should contain key after putIfAbsent");
                assertTrue(getMap().containsValue(value), "Map should contain value after putIfAbsent");
            }
            if (isPutChangeSupported()) {
                // Piggyback on isPutChangeSupported() for putIfAbsent with a caveat for null values.
                for (int i = 0; i < keys.length; i++) {
                    final K key = keys[i];
                    final V newValue = newValues[i];
                    final boolean newValueAlready = getMap().containsValue(newValue);
                    final V prevValue = getMap().get(key);
                    final Object oldValue = putIfAbsent(key, newValue);
                    getConfirmed().putIfAbsent(key, newValue);
                    verify();
                    final V arrValue = values[i];
                    assertEquals(arrValue, oldValue, "Map.putIfAbsent should return previous value when changed");
                    assertEquals(prevValue, oldValue, "Map.putIfAbsent should return previous value when changed");
                    if (prevValue == null) {
                        assertEquals(newValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                    } else {
                        assertEquals(oldValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                    }
                    assertTrue(getMap().containsKey(key), "Map should still contain key after putIfAbsent when changed");
                    if (newValueAlready && newValue != null) {
                        // TODO The test fixture already contain a null value, so we condition this assertion
                        assertFalse(getMap().containsValue(newValue),
                                String.format("[%,d] Map at '%s' shouldn't contain new value '%s' after putIfAbsent when changed", i, key, newValue));
                    }
                    // if duplicates are allowed, we're not guaranteed that the value
                    // no longer exists, so don't try checking that.
                    if (!isAllowDuplicateValues() && newValueAlready && newValue != null) {
                        assertFalse(getMap().containsValue(arrValue),
                                String.format("Map should not contain old value after putIfAbsent when changed: [%,d] key '%s', prevValue '%s', newValue '%s'",
                                        i, key, prevValue, newValue));
                    }
                }
            } else {
                try {
                    // two possible exception here, either valid
                    putIfAbsent(keys[0], newValues[0]);
                    fail("Expected IllegalArgumentException or UnsupportedOperationException on putIfAbsent (change)");
                } catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                    // ignore
                }
            }
        } else if (isPutChangeSupported()) {
            resetEmpty();
            final K key0 = keys[0];
            final V value0 = values[0];
            if (getMap().containsKey(key0)) {
                // don't throw
                assertEquals(getMap().get(key0), putIfAbsent(key0, value0));
            } else if (isPutAddSupported()) {
                putIfAbsent(key0, value0);
            } else {
                // throw (fixed size for example)
                try {
                    putIfAbsent(key0, value0);
                    fail("Expected UnsupportedOperationException or IllegalArgumentException on putIfAbsent (add) when fixed size");
                } catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                    // ignore
                }
            }
            resetFull();
            int i = 0;
            for (final Iterator<K> it = getMap().keySet().iterator(); it.hasNext() && i < newValues.length; i++) {
                final K key = it.next();
                final V newValue = newValues[i];
                final boolean newValueAlready = getMap().containsValue(newValue);
                final V prevValue = getMap().get(key);
                final V oldValue = putIfAbsent(key, newValue);
                final V value = getConfirmed().putIfAbsent(key, newValue);
                verify();
                assertEquals(value, oldValue, "Map.putIfAbsent should return previous value when changed");
                assertEquals(prevValue, oldValue, "Map.putIfAbsent should return previous value when changed");
                if (prevValue == null) {
                    assertEquals(newValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                } else {
                    assertEquals(oldValue, getMap().get(key), String.format("[%,d] key '%s', prevValue '%s', newValue '%s'", i, key, prevValue, newValue));
                }
                assertTrue(getMap().containsKey(key), "Map should still contain key after putIfAbsent when changed");
                if (newValueAlready && newValue != null) {
                    // TODO The test fixture already contain a null value, so we condition this assertion
                    assertFalse(getMap().containsValue(newValue),
                            String.format("[%,d] Map at '%s' shouldn't contain new value '%s' after putIfAbsent when changed", i, key, newValue));
                }
                // if duplicates are allowed, we're not guaranteed that the value
                // no longer exists, so don't try checking that.
                if (!isAllowDuplicateValues()) {
                    assertFalse(getMap().containsValue(values[i]), "Map should not contain old value after putIfAbsent when changed");
                }
            }
        } else {
            assertThrows(UnsupportedOperationException.class, () -> putIfAbsent(keys[0], values[0]), "Expected UnsupportedOperationException on put (add)");
        }
    }

    /**
     * Tests Map.put(null, value)
     */
    @Test
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
                } catch (final NullPointerException | IllegalArgumentException ex) {
                }
            }
        }
    }

    /**
     * Tests Map.put(null, value)
     */
    @Test
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
                } catch (final NullPointerException | IllegalArgumentException ex) {
                }
            }
        }
    }

    /**
     * Tests Map.remove(Object)
     */
    @Test
    public void testMapRemove() {
        if (!isRemoveSupported()) {
            resetFull();
            assertThrows(UnsupportedOperationException.class, () -> getMap().remove(getMap().keySet().iterator().next()),
                    "Expected UnsupportedOperationException on remove");
            return;
        }

        resetEmpty();

        final Object[] keys = getSampleKeys();
        final Object[] values = getSampleValues();
        for (final Object key : keys) {
            final Object o = getMap().remove(key);
            assertNull(o, "First map.remove should return null");
        }
        verify();

        resetFull();

        for (int i = 0; i < keys.length; i++) {
            final Object o = getMap().remove(keys[i]);
            getConfirmed().remove(keys[i]);
            verify();

            assertEquals(values[i], o, "map.remove with valid key should return value");
        }

        final Object[] other = getOtherKeys();

        resetFull();
        final int size = getMap().size();
        for (final Object element : other) {
            final Object o = getMap().remove(element);
            assertNull(o, "map.remove for nonexistent key should return null");
            assertEquals(size, getMap().size(), "map.remove for nonexistent key should not " + "shrink map");
        }
        verify();
    }

    /**
     * Tests Map.size()
     */
    @Test
    public void testMapSize() {
        resetEmpty();
        assertEquals(0, getMap().size(), "Map.size() should be 0 with an empty map");
        verify();

        resetFull();
        assertEquals(getSampleKeys().length, getMap().size(), "Map.size() should equal the number of entries " + "in the map");
        verify();
    }

    /**
     * Tests Map.toString(). Since the format of the string returned by the toString() method is not defined in the Map interface, there is no common way to
     * test the results of the toString() method. Therefore, it is encouraged that Map implementations override this test with one that checks the format
     * matches any format defined in its API. This default implementation just verifies that the toString() method does not return null.
     */
    @Test
    public void testMapToString() {
        resetEmpty();
        assertNotNull(getMap().toString(), "Empty map toString() should not return null");
        verify();

        resetFull();
        assertNotNull(getMap().toString(), "Empty map toString() should not return null");
        verify();
    }

    /**
     * Tests {@link Map#remove(Object, Object)}.
     */
    @Test
    public void testRemoveKeyValue() {
        assumeTrue(isRemoveSupported());
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        assertFalse(getMap().isEmpty());
        for (int i = 0; i < sampleKeys.length; i++) {
            assertTrue(getMap().remove(sampleKeys[i], sampleValues[i]));
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Tests {@link Map#replaceAll(java.util.function.BiFunction)}.
     */
    @Test
    public void testReplaceAll() {
        assumeTrue(isSetValueSupported() && isReplaceAllSupported());
        resetFull();
        final V[] newValues = getNewSampleValues();
        assertFalse(getMap().isEmpty());
        getMap().replaceAll((k, v) -> v);
        assertEquals(makeFullMap(), getMap());
        getMap().replaceAll((k, v) -> v);
        final AtomicInteger i = new AtomicInteger();
        final Map<K, V> newMap = new HashMap<>();
        getMap().replaceAll((k, v) -> {
            final V v2 = newValues[i.getAndIncrement()];
            newMap.put(k, v2);
            return v2;
        });
        assertEquals(newMap, getMap());
    }

    /**
     * Tests {@link Map#replace(Object, Object)}.
     */
    @Test
    public void testReplaceKeyValue() {
        assumeTrue(isRemoveSupported());
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        final V[] newValues = getNewSampleValues();
        assertFalse(getMap().isEmpty());
        for (final AtomicInteger inc = new AtomicInteger(); inc.get() < sampleKeys.length; inc.incrementAndGet()) {
            final int i = inc.get();
            final V value = sampleValues[i];
            final K sampleKey = sampleKeys[i];
            final Supplier<String> messageSupplier = () -> String.format("[%,d] key '%s'; %s", inc.get(), sampleKey, getMap());
            assertEquals(value, getMap().replace(sampleKey, value), messageSupplier);
            assertEquals(value, getMap().replace(sampleKey, value), messageSupplier);
            final V newValue = newValues[i];
            assertEquals(value, getMap().replace(sampleKey, newValue), messageSupplier);
            V oldValue = getMap().get(sampleKey);
            assertEquals(oldValue, getMap().replace(sampleKey, newValue), messageSupplier);
            oldValue = getMap().get(sampleKey);
            assertEquals(oldValue, getMap().replace(sampleKey, value), messageSupplier);
            if (isAllowNullValue()) {
                oldValue = getMap().get(sampleKey);
                assertEquals(oldValue, getMap().replace(sampleKey, null), messageSupplier);
                assertNull(getMap().get(sampleKey), messageSupplier);
                assertNull(getMap().replace(sampleKey, null), messageSupplier);
                assertNull(getMap().replace(sampleKey, value), messageSupplier);
            }
        }
    }

    /**
     * Tests {@link Map#replace(Object, Object, Object)}.
     */
    @Test
    public void testReplaceKeyValueValue() {
        assumeTrue(isRemoveSupported());
        resetFull();
        final K[] sampleKeys = getSampleKeys();
        final V[] sampleValues = getSampleValues();
        final V[] newValues = getNewSampleValues();
        assertFalse(getMap().isEmpty());
        for (final AtomicInteger inc = new AtomicInteger(); inc.get() < sampleKeys.length; inc.incrementAndGet()) {
            final int i = inc.get();
            final V value = sampleValues[i];
            final K key = sampleKeys[i];
            final Supplier<String> messageSupplier = () -> String.format("[%,d] key '%s', value '%s'; %s", inc.get(), key, value, getMap());
            boolean containsKey = getMap().containsKey(key);
            assertEquals(containsKey, getMap().replace(key, value, value), messageSupplier);
            containsKey = getMap().containsKey(key);
            assertEquals(containsKey, getMap().replace(key, value, value), messageSupplier);
            final V newValue = newValues[i];
            containsKey = getMap().containsKey(key);
            assertEquals(containsKey, getMap().replace(key, value, newValue), messageSupplier);
            V oldValue = getMap().get(key);
            containsKey = getMap().containsKey(key);
            assertEquals(containsKey, getMap().replace(key, oldValue, newValue), messageSupplier);
            oldValue = getMap().get(key);
            containsKey = getMap().containsKey(key);
            assertEquals(containsKey, getMap().replace(key, oldValue, value), messageSupplier);
            if (isAllowNullValue()) {
                oldValue = getMap().get(key);
                containsKey = getMap().containsKey(key);
                assertEquals(containsKey, getMap().replace(key, oldValue, null), messageSupplier);
                assertNull(getMap().get(key), messageSupplier);
                containsKey = getMap().containsKey(key);
                assertEquals(containsKey, getMap().replace(key, null, null), messageSupplier);
                containsKey = getMap().containsKey(key);
                assertEquals(containsKey, getMap().replace(key, null, value), messageSupplier);
            }
        }
    }

    /**
     * Test to ensure the test setup is working properly. This method checks to ensure that the getSampleKeys and getSampleValues methods are returning results
     * that look appropriate. That is, they both return a non-null array of equal length. The keys array must not have any duplicate values, and may only
     * contain a (single) null key if isNullKeySupported() returns true. The values array must only have a null value if useNullValue() is true and may only
     * have duplicate values if isAllowDuplicateValues() returns true.
     */
    @Test
    public void testSampleMappings() {
        final Object[] keys = getSampleKeys();
        final Object[] values = getSampleValues();
        final Object[] newValues = getNewSampleValues();

        assertNotNull(keys, "failure in test: Must have keys returned from " + "getSampleKeys.");

        assertNotNull(values, "failure in test: Must have values returned from " + "getSampleValues.");

        // verify keys and values have equivalent lengths (in case getSampleX are
        // overridden)
        assertEquals(keys.length, values.length, "failure in test: not the same number of sample " + "keys and values.");

        assertEquals(values.length, newValues.length, "failure in test: not the same number of values and new values.");

        // verify there aren't duplicate keys, and check values
        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                assertTrue(keys[i] != null || keys[j] != null, "failure in test: duplicate null keys.");
                assertTrue(keys[i] == null || keys[j] == null || !keys[i].equals(keys[j]) && !keys[j].equals(keys[i]),
                        "failure in test: duplicate non-null key.");
            }
            assertTrue(keys[i] != null || isAllowNullKey(), "failure in test: found null key, but isNullKeySupported " + "is false.");
            assertTrue(values[i] != null || isAllowNullValue(), "failure in test: found null value, but isNullValueSupported " + "is false.");
            assertTrue(newValues[i] != null || isAllowNullValue(), "failure in test: found null new value, but isNullValueSupported " + "is false.");
            assertTrue(values[i] != newValues[i] && (values[i] == null || !values[i].equals(newValues[i])),
                    "failure in test: values should not be the same as new value");
        }
    }

    /**
     * Tests that the {@link Map#bitMaps} collection is backed by the underlying map for clear().
     */
    @Test
    public void testValuesClearChangesMap() {
        if (!isRemoveSupported()) {
            return;
        }

        // clear values, reflected in map
        resetFull();
        Collection<V> values = getMap().values();
        assertFalse(getMap().isEmpty());
        assertFalse(values.isEmpty());
        values.clear();
        assertTrue(getMap().isEmpty());
        assertTrue(values.isEmpty());

        // clear map, reflected in values
        resetFull();
        values = getMap().values();
        assertFalse(getMap().isEmpty());
        assertFalse(values.isEmpty());
        getMap().clear();
        assertTrue(getMap().isEmpty());
        assertTrue(values.isEmpty());
    }

    /**
     * Verifies that values.iterator.remove changes the underlying map.
     */
    @Test
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
            assertEquals(expected, getMap().containsValue(value), msg.toString());
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Tests values.removeAll.
     */
    @Test
    public void testValuesRemoveAll() {
        resetFull();
        final Collection<V> values = getMap().values();
        final List<V> sampleValuesAsList = Arrays.asList(getSampleValues());
        if (!values.equals(sampleValuesAsList)) {
            return;
        }
        try {
            assertFalse(values.removeAll(Collections.<V>emptySet()));
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
     * Tests that the {@link Map#bitMaps} collection is backed by the underlying map by removing from the values collection and testing if the value was removed
     * from the map.
     * <p>
     * We should really test the "vice versa" case--that values removed from the map are removed from the values collection--also, but that's a more difficult
     * test to construct (lacking a "removeValue" method.)
     * </p>
     * <p>
     * See bug <a href="https://issues.apache.org/jira/browse/COLLECTIONS-92"> COLLECTIONS-92</a>.
     * </p>
     */
    @Test
    public void testValuesRemoveChangesMap() {
        resetFull();
        final V[] sampleValues = getSampleValues();
        final Collection<V> values = getMap().values();
        for (final V sampleValue : sampleValues) {
            if (map.containsValue(sampleValue)) {
                int j = 0; // loop counter prevents infinite loops when remove is broken
                while (values.contains(sampleValue) && j < 10000) {
                    try {
                        values.remove(sampleValue);
                    } catch (final UnsupportedOperationException e) {
                        // if values.remove is unsupported, just skip this test
                        return;
                    }
                    j++;
                }
                assertTrue(j < 10000, "values().remove(obj) is broken");
                assertFalse(getMap().containsValue(sampleValue), "Value should have been removed from the underlying map.");
            }
        }
    }

    /**
     * Test values.retainAll.
     */
    @Test
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
            assertTrue(values.retainAll(Collections.<V>emptySet()));
        } catch (final UnsupportedOperationException e) {
            // if values.retainAll is unsupported, just skip this test
            return;
        }
        assertTrue(getMap().isEmpty());
    }

    /**
     * Verifies that {@link #map} is still equal to {@link #confirmed}. This method checks that the map is equal to the HashMap, <em>and</em> that the map's
     * collection views are still equal to the HashMap's collection views. An <Code>equals</Code> test is done on the maps and their collection views; their
     * size and <Code>isEmpty</Code> results are compared; their hashCodes are compared; and <Code>containsAll</Code> tests are run on the collection views.
     */
    public void verify() {
        verifyMap();
        verifyEntrySet();
        verifyKeySet();
        verifyValues();
    }

    public void verifyEntrySet() {
        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals(size, entrySet.size(), "entrySet should be same size as HashMap's" + "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet());
        assertEquals(empty, entrySet.isEmpty(), "entrySet should be empty if HashMap is" + "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet());
        assertTrue(entrySet.containsAll(getConfirmed().entrySet()),
                "entrySet should contain all HashMap's elements" + "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet());
        assertEquals(getConfirmed().entrySet().hashCode(), entrySet.hashCode(),
                "entrySet hashCodes should be the same" + "\nTest: " + entrySet + "\nReal: " + getConfirmed().entrySet());
        assertEquals(getConfirmed().entrySet(), entrySet, "Map's entry set should still equal HashMap's");
    }

    public void verifyKeySet() {
        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals(size, keySet.size(), "keySet should be same size as HashMap's" + "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet());
        assertEquals(empty, keySet.isEmpty(), "keySet should be empty if HashMap is" + "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet());
        assertTrue(keySet.containsAll(getConfirmed().keySet()),
                "keySet should contain all HashMap's elements" + "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet());
        assertEquals(getConfirmed().keySet().hashCode(), keySet.hashCode(),
                "keySet hashCodes should be the same" + "\nTest: " + keySet + "\nReal: " + getConfirmed().keySet());
        assertEquals(getConfirmed().keySet(), keySet, "Map's key set should still equal HashMap's");
    }

    public void verifyMap() {
        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals(size, getMap().size(), "Map should be same size as HashMap");
        assertEquals(empty, getMap().isEmpty(), "Map should be empty if HashMap is");
        assertEquals(getConfirmed().hashCode(), getMap().hashCode(), "hashCodes should be the same");
        // changing the order of the assertion below fails for LRUMap because confirmed is
        // another collection (e.g. treemap) and confirmed.equals() creates a normal iterator (not
        // #mapIterator()), which modifies the parent expected modCount of the map object, causing
        // concurrent modification exceptions.
        // Because of this we have assertEquals(map, confirmed), and not the other way around.
        assertEquals(map, confirmed, "Map should still equal HashMap");
        assertEquals(getMap(), getConfirmed(), "Map should still equal HashMap");
    }

    public void verifyValues() {
        final List<V> known = new ArrayList<>(getConfirmed().values());

        values = getMap().values();

        final List<V> test = new ArrayList<>(values);

        final int size = getConfirmed().size();
        final boolean empty = getConfirmed().isEmpty();
        assertEquals(size, values.size(), "values should be same size as HashMap's" + "\nTest: " + test + "\nReal: " + known);
        assertEquals(empty, values.isEmpty(), "values should be empty if HashMap is" + "\nTest: " + test + "\nReal: " + known);
        assertTrue(test.containsAll(known), "values should contain all HashMap's elements" + "\nTest: " + test + "\nReal: " + known);
        assertTrue(known.containsAll(test), "values should contain all HashMap's elements" + "\nTest: " + test + "\nReal: " + known);
        // originally coded to use a HashBag, but now separate jar so...
        for (final V v : known) {
            final boolean removed = test.remove(v);
            assertTrue(removed, "Map's values should still equal HashMap's");
        }
        assertTrue(test.isEmpty(), "Map's values should still equal HashMap's");
    }

    /**
     * Resets the collection view fields.
     */
    private void views() {
        keySet = getMap().keySet();
        // see verifyValues: retrieve the values collection only when verifying them
        // this.values = getMap().values();
        entrySet = getMap().entrySet();
    }

}
