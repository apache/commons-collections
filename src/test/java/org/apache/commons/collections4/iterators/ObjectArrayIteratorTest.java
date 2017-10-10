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
package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests the ObjectArrayIterator.
 *
 */
public class ObjectArrayIteratorTest<E> extends AbstractIteratorTest<E> {

    protected String[] testArray = { "One", "Two", "Three" };

    public ObjectArrayIteratorTest(final String testName) {
        super(testName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayIterator<E> makeEmptyIterator() {
        return new ObjectArrayIterator<>((E[]) new Object[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayIterator<E> makeObject() {
        return new ObjectArrayIterator<>((E[]) testArray);
    }

    @SuppressWarnings("unchecked")
    public ObjectArrayIterator<E> makeArrayIterator() {
        return new ObjectArrayIterator<>();
    }

    public ObjectArrayIterator<E> makeArrayIterator(final E[] array) {
        return new ObjectArrayIterator<>(array);
    }

    public ObjectArrayIterator<E> makeArrayIterator(final E[] array, final int index) {
        return new ObjectArrayIterator<>(array, index);
    }

    public ObjectArrayIterator<E> makeArrayIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayIterator<>(array, start, end);
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    public void testIterator() {
        final Iterator<E> iter = makeObject();
        for (final String testValue : testArray) {
            final E iterValue = iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertTrue("Iterator should now be empty", !iter.hasNext());

        try {
            iter.next();
        } catch (final Exception e) {
            assertTrue(
                "NoSuchElementException must be thrown",
                e.getClass().equals(new NoSuchElementException().getClass()));
        }
    }

    public void testNullArray() {
        try {
            makeArrayIterator(null);

            fail("Constructor should throw a NullPointerException when constructed with a null array");
        } catch (final NullPointerException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testReset() {
        final ObjectArrayIterator<E> it = makeArrayIterator((E[]) testArray);
        it.next();
        it.reset();
        assertEquals("One", it.next());
    }

}
