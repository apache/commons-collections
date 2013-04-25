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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;

import junit.framework.TestCase;

/**
 * Unit tests for {@link org.apache.commons.collections4.keyvalue.MultiKey}.
 *
 * @version $Id$
 */
public class MultiKeyTest extends TestCase {

    Integer ONE = new Integer(1);
    Integer TWO = new Integer(2);
    Integer THREE = new Integer(3);
    Integer FOUR = new Integer(4);
    Integer FIVE = new Integer(5);

    public MultiKeyTest(final String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //-----------------------------------------------------------------------
    public void testConstructors() throws Exception {
        MultiKey<Integer> mk = null;
        mk = new MultiKey<Integer>(ONE, TWO);
        assertTrue(Arrays.equals(new Object[] { ONE, TWO }, mk.getKeys()));

        mk = new MultiKey<Integer>(ONE, TWO, THREE);
        assertTrue(Arrays.equals(new Object[] { ONE, TWO, THREE }, mk.getKeys()));

        mk = new MultiKey<Integer>(ONE, TWO, THREE, FOUR);
        assertTrue(Arrays.equals(new Object[] { ONE, TWO, THREE, FOUR }, mk.getKeys()));

        mk = new MultiKey<Integer>(ONE, TWO, THREE, FOUR, FIVE);
        assertTrue(Arrays.equals(new Object[] { ONE, TWO, THREE, FOUR, FIVE }, mk.getKeys()));

        mk = new MultiKey<Integer>(new Integer[] { THREE, FOUR, ONE, TWO }, false);
        assertTrue(Arrays.equals(new Object[] { THREE, FOUR, ONE, TWO }, mk.getKeys()));
    }

    public void testConstructorsByArray() throws Exception {
        MultiKey<Integer> mk = null;
        Integer[] keys = new Integer[] { THREE, FOUR, ONE, TWO };
        mk = new MultiKey<Integer>(keys);
        assertTrue(Arrays.equals(new Object[] { THREE, FOUR, ONE, TWO }, mk.getKeys()));
        keys[3] = FIVE;  // no effect
        assertTrue(Arrays.equals(new Object[] { THREE, FOUR, ONE, TWO }, mk.getKeys()));

        keys = new Integer[] {};
        mk = new MultiKey<Integer>(keys);
        assertTrue(Arrays.equals(new Object[] {}, mk.getKeys()));

        keys = new Integer[] { THREE, FOUR, ONE, TWO };
        mk = new MultiKey<Integer>(keys, true);
        assertTrue(Arrays.equals(new Object[] { THREE, FOUR, ONE, TWO }, mk.getKeys()));
        keys[3] = FIVE;  // no effect
        assertTrue(Arrays.equals(new Object[] { THREE, FOUR, ONE, TWO }, mk.getKeys()));

        keys = new Integer[] { THREE, FOUR, ONE, TWO };
        mk = new MultiKey<Integer>(keys, false);
        assertTrue(Arrays.equals(new Object[] { THREE, FOUR, ONE, TWO }, mk.getKeys()));
        // change key - don't do this!
        // the hashcode of the MultiKey is now broken
        keys[3] = FIVE;
        assertTrue(Arrays.equals(new Object[] { THREE, FOUR, ONE, FIVE }, mk.getKeys()));
    }

    public void testConstructorsByArrayNull() throws Exception {
        final Integer[] keys = null;
        try {
            new MultiKey<Integer>(keys);
            fail();
        } catch (final IllegalArgumentException ex) {}
        try {
            new MultiKey<Integer>(keys, true);
            fail();
        } catch (final IllegalArgumentException ex) {}
        try {
            new MultiKey<Integer>(keys, false);
            fail();
        } catch (final IllegalArgumentException ex) {}
    }

    public void testSize() {
        assertEquals(2, new MultiKey<Integer>(ONE, TWO).size());
        assertEquals(2, new MultiKey<Object>(null, null).size());
        assertEquals(3, new MultiKey<Integer>(ONE, TWO, THREE).size());
        assertEquals(3, new MultiKey<Object>(null, null, null).size());
        assertEquals(4, new MultiKey<Integer>(ONE, TWO, THREE, FOUR).size());
        assertEquals(4, new MultiKey<Object>(null, null, null, null).size());
        assertEquals(5, new MultiKey<Integer>(ONE, TWO, THREE, FOUR, FIVE).size());
        assertEquals(5, new MultiKey<Object>(null, null, null, null, null).size());

        assertEquals(0, new MultiKey<Object>(new Object[] {}).size());
        assertEquals(1, new MultiKey<Integer>(new Integer[] { ONE }).size());
        assertEquals(2, new MultiKey<Integer>(new Integer[] { ONE, TWO }).size());
        assertEquals(7, new MultiKey<Integer>(new Integer[] { ONE, TWO, ONE, TWO, ONE, TWO, ONE }).size());
    }

    public void testGetIndexed() {
        final MultiKey<Integer> mk = new MultiKey<Integer>(ONE, TWO);
        assertSame(ONE, mk.getKey(0));
        assertSame(TWO, mk.getKey(1));
        try {
            mk.getKey(-1);
            fail();
        } catch (final IndexOutOfBoundsException ex) {}
        try {
            mk.getKey(2);
            fail();
        } catch (final IndexOutOfBoundsException ex) {}
    }

    public void testGetKeysSimpleConstructor() {
        final MultiKey<Integer> mk = new MultiKey<Integer>(ONE, TWO);
        final Object[] array = mk.getKeys();
        assertSame(ONE, array[0]);
        assertSame(TWO, array[1]);
        assertEquals(2, array.length);
    }

    public void testGetKeysArrayConstructorCloned() {
        final Integer[] keys = new Integer[] { ONE, TWO };
        final MultiKey<Integer> mk = new MultiKey<Integer>(keys, true);
        final Object[] array = mk.getKeys();
        assertTrue(array != keys);
        assertTrue(Arrays.equals(array, keys));
        assertSame(ONE, array[0]);
        assertSame(TWO, array[1]);
        assertEquals(2, array.length);
    }

    public void testGetKeysArrayConstructorNonCloned() {
        final Integer[] keys = new Integer[] { ONE, TWO };
        final MultiKey<Integer> mk = new MultiKey<Integer>(keys, false);
        final Object[] array = mk.getKeys();
        assertTrue(array != keys);  // still not equal
        assertTrue(Arrays.equals(array, keys));
        assertSame(ONE, array[0]);
        assertSame(TWO, array[1]);
        assertEquals(2, array.length);
    }

    public void testHashCode() {
        final MultiKey<Integer> mk1 = new MultiKey<Integer>(ONE, TWO);
        final MultiKey<Integer> mk2 = new MultiKey<Integer>(ONE, TWO);
        final MultiKey<Object> mk3 = new MultiKey<Object>(ONE, "TWO");

        assertTrue(mk1.hashCode() == mk1.hashCode());
        assertTrue(mk1.hashCode() == mk2.hashCode());
        assertTrue(mk1.hashCode() != mk3.hashCode());

        final int total = (0 ^ ONE.hashCode()) ^ TWO.hashCode();
        assertEquals(total, mk1.hashCode());
    }

    public void testEquals() {
        final MultiKey<Integer> mk1 = new MultiKey<Integer>(ONE, TWO);
        final MultiKey<Integer> mk2 = new MultiKey<Integer>(ONE, TWO);
        final MultiKey<Object> mk3 = new MultiKey<Object>(ONE, "TWO");

        assertEquals(mk1, mk1);
        assertEquals(mk1, mk2);
        assertTrue(mk1.equals(mk3) == false);
        assertTrue(mk1.equals("") == false);
        assertTrue(mk1.equals(null) == false);
    }

    static class SystemHashCodeSimulatingKey implements Serializable {

        private static final long serialVersionUID = -1736147315703444603L;
        private final String name;
        private int hashCode = 1;

        public SystemHashCodeSimulatingKey(final String name)
        {
            this.name = name;
        }

        @Override
        public boolean equals(final Object obj)
        {
            return obj instanceof SystemHashCodeSimulatingKey 
                && name.equals(((SystemHashCodeSimulatingKey)obj).name);
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

        private Object readResolve() {
            hashCode=2; // simulate different hashCode after deserialization in another process
            return this;
        }
    }
    
    public void testEqualsAfterSerialization() throws IOException, ClassNotFoundException
    {
        SystemHashCodeSimulatingKey sysKey = new SystemHashCodeSimulatingKey("test");
        final MultiKey<?> mk = new MultiKey<Object>(ONE, sysKey);
        final Map<MultiKey<?>, Integer> map = new HashMap<MultiKey<?>, Integer>();
        map.put(mk, TWO);

        // serialize
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(sysKey);
        out.writeObject(map);
        out.close();

        // deserialize
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream in = new ObjectInputStream(bais);
        sysKey = (SystemHashCodeSimulatingKey)in.readObject(); // simulate deserialization in another process
        final Map<?, ?> map2 = (Map<?, ?>) in.readObject();
        in.close();

        assertEquals(2, sysKey.hashCode()); // different hashCode now

        final MultiKey<?> mk2 = new MultiKey<Object>(ONE, sysKey);
        assertEquals(TWO, map2.get(mk2));        
    }
}
