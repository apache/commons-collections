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
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests the ObjectArrayIterator.
 *
 * @version $Id$
 */
public class ObjectArrayIteratorTest<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = { "One", "Two", "Three" };

    public ObjectArrayIteratorTest(String testName) {
        super(testName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayIterator<E> makeEmptyIterator() {
        return new ObjectArrayIterator<E>((E[]) new Object[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayIterator<E> makeObject() {
        return new ObjectArrayIterator<E>((E[]) testArray);
    }

    public ObjectArrayIterator<E> makeArrayIterator() {
        return new ObjectArrayIterator<E>();
    }

    public ObjectArrayIterator<E> makeArrayIterator(E[] array) {
        return new ObjectArrayIterator<E>(array);
    }

    public ObjectArrayIterator<E> makeArrayIterator(E[] array, int index) {
        return new ObjectArrayIterator<E>(array, index);
    }

    public ObjectArrayIterator<E> makeArrayIterator(E[] array, int start, int end) {
        return new ObjectArrayIterator<E>(array, start, end);
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    public void testIterator() {
        Iterator<E> iter = makeObject();
        for (int i = 0; i < testArray.length; i++) {
            Object testValue = testArray[i];
            E iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertTrue("Iterator should now be empty", !iter.hasNext());

        try {
            iter.next();
        } catch (Exception e) {
            assertTrue(
                "NoSuchElementException must be thrown",
                e.getClass().equals((new NoSuchElementException()).getClass()));
        }
    }

    public void testNullArray() {
        try {
            makeArrayIterator(null);

            fail("Constructor should throw a NullPointerException when constructed with a null array");
        } catch (NullPointerException e) {
            // expected
        }

        ObjectArrayIterator<E> iter = makeArrayIterator();
        try {
            iter.setArray(null);

            fail("setArray(null) should throw a NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testDoubleSet() {
        ObjectArrayIterator<E> it = makeArrayIterator();
        it.setArray((E[]) new String[0]);
        try {
            it.setArray((E[]) new String[0]);
            fail();
        } catch (IllegalStateException ex) {
        }
    }

    @SuppressWarnings("unchecked")
    public void testReset() {
        ObjectArrayIterator<E> it = makeArrayIterator((E[]) testArray);
        it.next();
        it.reset();
        assertEquals("One", it.next());
    }

}
