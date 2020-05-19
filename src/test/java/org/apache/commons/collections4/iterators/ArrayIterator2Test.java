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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests the ArrayIterator with primitive type arrays.
 *
 */
public class ArrayIterator2Test<E> extends AbstractIteratorTest<E> {

    protected int[] testArray = { 2, 4, 6, 8 };

    public ArrayIterator2Test(final String testName) {
        super(testName);
    }

    @Override
    public ArrayIterator<E> makeEmptyIterator() {
        return new ArrayIterator<>(new int[0]);
    }

    @Override
    public ArrayIterator<E> makeObject() {
        return new ArrayIterator<>(testArray);
    }

    public ArrayIterator<E> makeArrayIterator(final Object array) {
        return new ArrayIterator<>(array);
    }

    public ArrayIterator<E> makeArrayIterator(final Object array, final int index) {
        return new ArrayIterator<>(array, index);
    }

    public ArrayIterator<E> makeArrayIterator(final Object array, final int start, final int end) {
        return new ArrayIterator<>(array, start, end);
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    public void testIterator() {
        final Iterator<E> iter = makeObject();
        for (final int element : testArray) {
            final Integer testValue = Integer.valueOf(element);
            final Number iterValue = (Number) iter.next();

            assertEquals(testValue, iterValue);
        }

        assertTrue(!iter.hasNext());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.next();
        });
        assertNull(exception.getMessage());
    }

    public void testIndexedArray() {
        Iterator<E> iter = makeArrayIterator(testArray, 2);
        int count = 0;
        while (iter.hasNext()) {
            ++count;
            iter.next();
        }

        assertEquals(count, testArray.length - 2);

        iter = makeArrayIterator(testArray, 1, testArray.length - 1);
        count = 0;
        while (iter.hasNext()) {
            ++count;
            iter.next();
        }

        assertEquals(count, testArray.length - 2);

        Exception exception = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            makeArrayIterator(testArray, -1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts before the start of the array."));

        exception = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            makeArrayIterator(testArray, testArray.length + 1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that starts beyond the end of the array"));


        exception = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            makeArrayIterator(testArray, 0, -1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that ends before the start of the array."));

        exception = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            makeArrayIterator(testArray, 0, testArray.length + 1);
        });
        assertTrue(exception.getMessage().contains("Attempt to make an ArrayIterator that"));

        makeArrayIterator(testArray, 1, 1);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            makeArrayIterator(testArray, testArray.length - 1, testArray.length - 2);
        });
        assertTrue(exception.getMessage().contains("End index must not be less than start index."));
    }
}
