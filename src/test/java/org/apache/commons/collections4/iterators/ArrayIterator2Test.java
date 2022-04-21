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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Tests the ArrayIterator with primitive type arrays.
 */
public class ArrayIterator2Test<E> extends AbstractIteratorTest<E> {

    protected int[] testArray = { 2, 4, 6, 8 };

    public ArrayIterator2Test() {
        super(ArrayIterator2Test.class.getSimpleName());
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

    @Test
    public void testIterator() {
        final Iterator<E> iter = makeObject();
        for (final int element : testArray) {
            final Integer testValue = Integer.valueOf(element);
            final Number iterValue = (Number) iter.next();

            assertEquals("Iteration value is correct", testValue, iterValue);
        }

        assertFalse("Iterator should now be empty", iter.hasNext());

        try {
            iter.next();
        } catch (final Exception e) {
            assertEquals("NoSuchElementException must be thrown", e.getClass(), new NoSuchElementException().getClass());
        }
    }

    @Test
    public void testIndexedArray() {
        Iterator<E> iter = makeArrayIterator(testArray, 2);
        int count = 0;
        while (iter.hasNext()) {
            ++count;
            iter.next();
        }

        assertEquals("the count should be right using ArrayIterator(Object,2) ", count, testArray.length - 2);

        iter = makeArrayIterator(testArray, 1, testArray.length - 1);
        count = 0;
        while (iter.hasNext()) {
            ++count;
            iter.next();
        }

        assertEquals(
            "the count should be right using ArrayIterator(Object,1," + (testArray.length - 1) + ") ",
            count,
            testArray.length - 2);
        assertAll(
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> makeArrayIterator(testArray, -1),
                        "new ArrayIterator(Object,-1) should throw an ArrayIndexOutOfBoundsException"),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> makeArrayIterator(testArray, testArray.length + 1),
                        "new ArrayIterator(Object,length+1) should throw an ArrayIndexOutOfBoundsException"),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> makeArrayIterator(testArray, 0, -1),
                        "new ArrayIterator(Object,0,-1) should throw an ArrayIndexOutOfBoundsException"),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> makeArrayIterator(testArray, 0, testArray.length + 1),
                        "new ArrayIterator(Object,0,length+1) should throw an ArrayIndexOutOfBoundsException"),
                () -> assertThrows(IllegalArgumentException.class, () -> makeArrayIterator(testArray, testArray.length - 1, testArray.length - 2),
                        "new ArrayIterator(Object,length-2,length-1) should throw an IllegalArgumentException")
        );

        try {
            iter = makeArrayIterator(testArray, 1, 1);
            // expected not to fail
        } catch (final IllegalArgumentException iae) {
            // MODIFIED: an iterator over a zero-length section of array
            //  should be perfectly legal behavior
            fail("new ArrayIterator(Object,1,1) should NOT throw an IllegalArgumentException");
        }
    }

}
