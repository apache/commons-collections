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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Test the ArrayListIterator class.
 *
 */
public class ArrayListIteratorTest<E> extends ArrayIteratorTest<E> {

    public ArrayListIteratorTest(final String testName) {
        super(testName);
    }

    @Override
    public ArrayListIterator<E> makeEmptyIterator() {
        return new ArrayListIterator<>(new Object[0]);
    }

    @Override
    public ArrayListIterator<E> makeObject() {
        return new ArrayListIterator<>(testArray);
    }

    public ArrayListIterator<E> makeArrayListIterator(final Object array) {
        return new ArrayListIterator<>(array);
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    /**
     * Test the basic ListIterator functionality - going backwards using
     * {@code previous()}.
     */
    public void testListIterator() {
        final ListIterator<E> iter = makeObject();

        // TestArrayIterator#testIterator() has already tested the iterator forward,
        //  now we need to test it in reverse

        // fast-forward the iterator to the end...
        while (iter.hasNext()) {
            iter.next();
        }

        for (int x = testArray.length - 1; x >= 0; x--) {
            final Object testValue = testArray[x];
            final Object iterValue = iter.previous();

            assertEquals(testValue, iterValue);
        }

        assertTrue(!iter.hasPrevious());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.previous();
        });
        assertNull(exception.getMessage());

    }

    /**
     * Tests the {@link java.util.ListIterator#set} operation.
     */
    @SuppressWarnings("unchecked")
    public void testListIteratorSet() {
        final String[] testData = new String[] { "a", "b", "c" };

        final String[] result = new String[] { "0", "1", "2" };

        ListIterator<E> iter = makeArrayListIterator(testData);
        int x = 0;

        while (iter.hasNext()) {
            iter.next();
            iter.set((E) Integer.toString(x));
            x++;
        }

        assertTrue(Arrays.equals(testData, result));

        // a call to set() before a call to next() or previous() should throw an IllegalStateException
        final ListIterator<E> iter1 = makeArrayListIterator(testArray);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            iter1.set((E) "should fail");
        });
        assertTrue(exception.getMessage().contains("must call next() or previous() before a call to set()"));
    }

}
