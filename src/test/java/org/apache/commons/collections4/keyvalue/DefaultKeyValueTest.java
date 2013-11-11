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
package org.apache.commons.collections4.keyvalue;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test the DefaultKeyValue class.
 *
 * @since 3.0
 * @version $Id$
 */
public class DefaultKeyValueTest<K, V> extends TestCase {

    private final String key = "name";
    private final String value = "duke";

    /**
     * JUnit constructor.
     *
     * @param testName  the test name
     */
    public DefaultKeyValueTest(final String testName) {
        super(testName);

    }

    //-----------------------------------------------------------------------
    /**
     * Make an instance of DefaultKeyValue with the default (null) key and value.
     * Subclasses should override this method to return a DefaultKeyValue
     * of the type being tested.
     */
    protected DefaultKeyValue<K, V> makeDefaultKeyValue() {
        return new DefaultKeyValue<K, V>(null, null);
    }

    /**
     * Make an instance of DefaultKeyValue with the specified key and value.
     * Subclasses should override this method to return a DefaultKeyValue
     * of the type being tested.
     */
    protected DefaultKeyValue<K, V> makeDefaultKeyValue(final K key, final V value) {
        return new DefaultKeyValue<K, V>(key, value);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void testAccessorsAndMutators() {
        final DefaultKeyValue<K, V> kv = makeDefaultKeyValue();

        kv.setKey((K) key);
        assertTrue(kv.getKey() == key);

        kv.setValue((V) value);
        assertTrue(kv.getValue() == value);

        // check that null doesn't do anything funny
        kv.setKey(null);
        assertTrue(kv.getKey() == null);

        kv.setValue(null);
        assertTrue(kv.getValue() == null);

    }

    @SuppressWarnings("unchecked")
    public void testSelfReferenceHandling() {
        // test that #setKey and #setValue do not permit
        //  the KVP to contain itself (and thus cause infinite recursion
        //  in #hashCode and #toString)

        final DefaultKeyValue<K, V> kv = makeDefaultKeyValue();

        try {
            kv.setKey((K) kv);
            fail("Should throw an IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
            // expected to happen...

            // check that the KVP's state has not changed
            assertTrue(kv.getKey() == null && kv.getValue() == null);
        }

        try {
            kv.setValue((V) kv);
            fail("Should throw an IllegalArgumentException");
        } catch (final IllegalArgumentException iae) {
            // expected to happen...

            // check that the KVP's state has not changed
            assertTrue(kv.getKey() == null && kv.getValue() == null);
        }
    }

    /**
     * Subclasses should override this method to test their own constructors.
     */
    @SuppressWarnings("unchecked")
    public void testConstructors() {
        // 1. test default constructor
        DefaultKeyValue<K, V> kv = new DefaultKeyValue<K, V>();
        assertTrue(kv.getKey() == null && kv.getValue() == null);

        // 2. test key-value constructor
        kv = new DefaultKeyValue<K, V>((K) key, (V) value);
        assertTrue(kv.getKey() == key && kv.getValue() == value);

        // 3. test copy constructor
        final DefaultKeyValue<K, V> kv2 = new DefaultKeyValue<K, V>(kv);
        assertTrue(kv2.getKey() == key && kv2.getValue() == value);

        // test that the KVPs are independent
        kv.setKey(null);
        kv.setValue(null);

        assertTrue(kv2.getKey() == key && kv2.getValue() == value);

        // 4. test Map.Entry constructor
        final Map<K, V> map = new HashMap<K, V>();
        map.put((K) key, (V) value);
        final Map.Entry<K, V> entry = map.entrySet().iterator().next();

        kv = new DefaultKeyValue<K, V>(entry);
        assertTrue(kv.getKey() == key && kv.getValue() == value);

        // test that the KVP is independent of the Map.Entry
        entry.setValue(null);
        assertTrue(kv.getValue() == value);

    }

    @SuppressWarnings("unchecked")
    public void testEqualsAndHashCode() {
        // 1. test with object data
        DefaultKeyValue<K, V> kv = makeDefaultKeyValue((K) key, (V) value);
        DefaultKeyValue<K, V> kv2 = makeDefaultKeyValue((K) key, (V) value);

        assertTrue(kv.equals(kv));
        assertTrue(kv.equals(kv2));
        assertTrue(kv.hashCode() == kv2.hashCode());

        // 2. test with nulls
        kv = makeDefaultKeyValue(null, null);
        kv2 = makeDefaultKeyValue(null, null);

        assertTrue(kv.equals(kv));
        assertTrue(kv.equals(kv2));
        assertTrue(kv.hashCode() == kv2.hashCode());
    }

    @SuppressWarnings("unchecked")
    public void testToString() {
        DefaultKeyValue<K, V> kv = makeDefaultKeyValue((K) key, (V) value);
        assertTrue(kv.toString().equals(kv.getKey() + "=" + kv.getValue()));

        // test with nulls
        kv = makeDefaultKeyValue(null, null);
        assertTrue(kv.toString().equals(kv.getKey() + "=" + kv.getValue()));
    }

    @SuppressWarnings("unchecked")
    public void testToMapEntry() {
        final DefaultKeyValue<K, V> kv = makeDefaultKeyValue((K) key, (V) value);

        final Map<K, V> map = new HashMap<K, V>();
        map.put(kv.getKey(), kv.getValue());
        final Map.Entry<K, V> entry = map.entrySet().iterator().next();

        assertTrue(entry.equals(kv.toMapEntry()));
        assertTrue(entry.hashCode() == kv.hashCode());
    }

}
