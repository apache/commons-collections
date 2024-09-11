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
package org.apache.commons.collections4;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ArrayUtilsTest {

    @Test
    public void testContains() {
        final Object[] array = { "0", "1", "2", "3", null, "0" };
        assertFalse(ArrayUtils.contains(null, null));
        assertFalse(ArrayUtils.contains(null, "1"));
        assertTrue(ArrayUtils.contains(array, "0"));
        assertTrue(ArrayUtils.contains(array, "1"));
        assertTrue(ArrayUtils.contains(array, "2"));
        assertTrue(ArrayUtils.contains(array, "3"));
        assertTrue(ArrayUtils.contains(array, null));
        assertFalse(ArrayUtils.contains(array, "notInArray"));
    }

    @Test
    public void testContains_LANG_1261() {
        class LANG1261ParentObject {
            @Override
            public boolean equals(final Object o) {
                return true;
            }
        }
        class LANG1261ChildObject extends LANG1261ParentObject {
        }
        final Object[] array = new LANG1261ChildObject[] { new LANG1261ChildObject() };
        assertTrue(ArrayUtils.contains(array, new LANG1261ParentObject()));
    }

    @Test
    public void testIndexOf() {
        final Object[] array = { "0", "1", "2", "3", null, "0" };
        assertEquals(-1, ArrayUtils.indexOf(null, null));
        assertEquals(-1, ArrayUtils.indexOf(null, "0"));
        assertEquals(-1, ArrayUtils.indexOf(new Object[0], "0"));
        assertEquals(0, ArrayUtils.indexOf(array, "0"));
        assertEquals(1, ArrayUtils.indexOf(array, "1"));
        assertEquals(2, ArrayUtils.indexOf(array, "2"));
        assertEquals(3, ArrayUtils.indexOf(array, "3"));
        assertEquals(4, ArrayUtils.indexOf(array, null));
        assertEquals(-1, ArrayUtils.indexOf(array, "notInArray"));
    }

    // Test for reverse functionality
    @Test
    public void testReverse() {
        // Normal case: reverse a non-empty array
        Integer[] array = { 1, 2, 3, 4 };
        Integer[] reversed = ArrayUtils.reverse(array);
        assertArrayEquals(new Integer[] { 4, 3, 2, 1 }, reversed);

        // Ensure the original array remains unchanged
        assertArrayEquals(new Integer[] { 1, 2, 3, 4 }, array);

        // Edge case: empty array
        Integer[] emptyArray = {};
        Integer[] reversedEmptyArray = ArrayUtils.reverse(emptyArray);
        assertArrayEquals(new Integer[] {}, reversedEmptyArray);

        // Edge case: single element array
        Integer[] singleElementArray = { 1 };
        Integer[] reversedSingleElementArray = ArrayUtils.reverse(singleElementArray);
        assertArrayEquals(new Integer[] { 1 }, reversedSingleElementArray);

        // Null case: should return null
        assertNull(ArrayUtils.reverse(null));
    }

    // Test for reverse with null array
    @Test
    public void testReverseNullArray() {
        // Test reverse with null input
        assertNull(ArrayUtils.reverse(null));
    }

    // Test for reverseInPlace functionality
    @Test
    public void testReverseInPlace() {
        // Normal case: reverse a non-empty array in place
        Integer[] array = { 1, 2, 3, 4 };
        ArrayUtils.reverseInPlace(array);
        assertArrayEquals(new Integer[] { 4, 3, 2, 1 }, array);

        // Edge case: empty array, reversing in place should do nothing
        Integer[] emptyArray = {};
        ArrayUtils.reverseInPlace(emptyArray);
        assertArrayEquals(new Integer[] {}, emptyArray);

        // Edge case: single element array, reversing should do nothing
        Integer[] singleElementArray = { 1 };
        ArrayUtils.reverseInPlace(singleElementArray);
        assertArrayEquals(new Integer[] { 1 }, singleElementArray);

        // Null case: should not throw an exception, should just return
        ArrayUtils.reverseInPlace(null); // No exception should be thrown
    }

    // Test for reverseInPlace with null array
    @Test
    public void testReverseInPlaceNullArray() {
        // Test reverseInPlace with null input
        // Should not throw any exceptions, just return silently
        ArrayUtils.reverseInPlace(null); // No exception should be thrown
    }

    // Test for reverse with arrays of strings
    @Test
    public void testReverseStrings() {
        String[] array = { "A", "B", "C" };
        String[] reversed = ArrayUtils.reverse(array);
        assertArrayEquals(new String[] { "C", "B", "A" }, reversed);
        assertArrayEquals(new String[] { "A", "B", "C" }, array); // ensure original array is unchanged
    }

    // Test for reverseInPlace with arrays of strings
    @Test
    public void testReverseInPlaceStrings() {
        String[] array = { "A", "B", "C" };
        ArrayUtils.reverseInPlace(array);
        assertArrayEquals(new String[] { "C", "B", "A" }, array);
    }

    // Test for reverse with array of null elements
    @Test
    public void testReverseWithNullElements() {
        Integer[] array = { 1, null, 2 };
        Integer[] reversed = ArrayUtils.reverse(array);
        assertArrayEquals(new Integer[] { 2, null, 1 }, reversed);
    }

    // Test for reverseInPlace with array of null elements
    @Test
    public void testReverseInPlaceWithNullElements() {
        Integer[] array = { 1, null, 2 };
        ArrayUtils.reverseInPlace(array);
        assertArrayEquals(new Integer[] { 2, null, 1 }, array);
    }
}
