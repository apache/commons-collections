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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Tests the ObjectArrayListIterator class.
 *
 * @param <E> the type of elements tested by this iterator.
 */
public class ObjectArrayListIteratorTest<E> extends ObjectArrayIteratorTest<E> {

    public ObjectArrayListIterator<E> makeArrayListIterator(final E[] array) {
        return new ObjectArrayListIterator<>(array);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayListIterator<E> makeEmptyIterator() {
        return new ObjectArrayListIterator<>((E[]) new Object[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ObjectArrayListIterator<E> makeObject() {
        return new ObjectArrayListIterator<>((E[]) testArray);
    }

    /**
     * Test the basic ListIterator functionality - going backwards using
     * {@code previous()}.
     */
    @Test
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
            assertEquals(testValue, iterValue, "Iteration value is correct");
        }
        assertFalse(iter.hasPrevious(), "Iterator should now be empty");
        assertThrows(NoSuchElementException.class, iter::previous);
    }

    /**
     * Tests the {@link java.util.ListIterator#set} operation.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testListIteratorSet() {
        final String[] testData = { "a", "b", "c" };

        final String[] result = { "0", "1", "2" };

        ListIterator<E> iter = makeArrayListIterator((E[]) testData);
        int x = 0;

        while (iter.hasNext()) {
            iter.next();
            iter.set((E) Integer.toString(x));
            x++;
        }

        assertArrayEquals(testData, result, "The two arrays should have the same value, i.e. {0,1,2}");

        // a call to set() before a call to next() or previous() should throw an IllegalStateException
        iter = makeArrayListIterator((E[]) testArray);

        final ListIterator<E> finalIter = iter;
        assertThrows(IllegalStateException.class, () -> finalIter.set((E) "should fail"), "ListIterator#set should fail if next() or previous() have not yet been called.");
    }

}
