/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator.OfInt;

import org.junit.Test;

/**
 * Tests the static hasher.
 */
public class StaticHasherTest {

    private HashFunctionIdentity testFunction = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test Function";
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }

        @Override
        public ProcessType getProcessType() {
            return ProcessType.CYCLIC;
        }

        @Override
        public long getSignature() {
            return 0;
        }
    };

    private HashFunctionIdentity testFunctionX = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test FunctionX";
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }

        @Override
        public ProcessType getProcessType() {
            return ProcessType.CYCLIC;
        }

        @Override
        public long getSignature() {
            return 0;
        }
    };

    private Shape shape = new Shape(testFunction, 3, 72, 17);

    /**
     * Tests that getBits returns the proper values.
     */
    @Test
    public void testGetBits() {
        List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        StaticHasher hasher = new StaticHasher(lst.iterator(), shape);
        assertEquals(17, hasher.size());
        OfInt iter = hasher.getBits(shape);
        for (int i = 0; i < 17; i++) {
            assertTrue(iter.hasNext());
            assertEquals(i, iter.nextInt());
        }
        assertFalse(iter.hasNext());

    }

    /**
     * Tests that gitBits does not return duplicates and orders the indices.
     */
    @Test
    public void testGetBits_DuplicateValues() {
        int[] input = {6, 69, 44, 19, 10, 57, 48, 23, 70, 61, 36, 11, 2, 49, 24, 15, 62, 1, 63, 53, 43, 17, 7, 69, 59,
            49, 39, 13, 3, 65, 55, 45, 35, 25};
        int[] expected = {1, 2, 3, 6, 7, 10, 11, 13, 15, 17, 19, 23, 24, 25, 35, 36, 39, 43, 44, 45, 48, 49, 53, 55, 57,
            59, 61, 62, 63, 65, 69, 70};

        StaticHasher hasher = new StaticHasher(Arrays.stream(input).iterator(), shape);

        OfInt iter = hasher.getBits(shape);
        for (int i = 0; i < expected.length; i++) {
            assertTrue(iter.hasNext());
            assertEquals(expected[i], iter.nextInt());
        }
        assertFalse(iter.hasNext());
    }

    /**
     * Tests that gitBits is called with the wrong shape an exeption is thrown.
     */
    @Test
    public void testGetBits_WrongShape() {
        List<Integer> lst = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        StaticHasher hasher = new StaticHasher(lst.iterator(), shape);

        try {
            hasher.getBits(new Shape(testFunctionX, 3, 72, 17));
            fail("Should have thown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing
        }

    }

    /**
     * Test that the iterator based constructor works correctly and removes duplicates.
     */
    @Test
    public void testConstructor_Iterator() {

        int[] values = {1, 3, 5, 7, 9, 3, 5, 1};
        Iterator<Integer> iter = Arrays.stream(values).iterator();
        StaticHasher hasher = new StaticHasher(iter, shape);

        assertEquals(5, hasher.size());
        assertEquals(shape, hasher.getShape());
        assertEquals(0, HashFunctionIdentity.DEEP_COMPARATOR.compare(testFunction, hasher.getHashFunctionIdentity()));

        iter = hasher.getBits(shape);
        int idx = 0;
        while (iter.hasNext()) {
            assertEquals("Error at idx " + idx, Integer.valueOf(values[idx]), iter.next());
            idx++;
        }
        assertEquals(5, idx);
    }

    /**
     * Tests that if the iterator passed to the constructor contains a value greater than
     * or equal to Shape.numerOfBits() an exception is thrown.
     */
    @Test
    public void testConstructor_Iterator_ValueTooBig() {

        int[] values = {shape.getNumberOfBits(), 3, 5, 7, 9, 3, 5, 1};
        Iterator<Integer> iter = Arrays.stream(values).iterator();
        try {
            new StaticHasher(iter, shape);
            fail("Should have thown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing
        }
    }

    /**
     * Tests that if the iterator passed to the constructor contains a value less than 0
     * (zero) an exception is thrown.
     */
    @Test
    public void testConstructor_Iterator_ValueTooSmall() {

        int[] values = {-1, 3, 5, 7, 9, 3, 5, 1};
        Iterator<Integer> iter = Arrays.stream(values).iterator();
        try {
            new StaticHasher(iter, shape);
            fail("Should have thown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing
        }
    }

    /**
     * Compare 2 static hashers to verify they have the same bits enabled.
     *
     * @param hasher1 the first static hasher.
     * @param hasher2 the second static hasher.
     */
    private void assertSameBits(StaticHasher hasher1, StaticHasher hasher2) {
        OfInt iter1 = hasher1.getBits(shape);
        OfInt iter2 = hasher2.getBits(shape);

        while (iter1.hasNext()) {
            assertTrue("Not enough data in second hasher", iter2.hasNext());
            assertEquals(iter1.nextInt(), iter2.nextInt());
        }
        assertFalse("Too much data in second hasher", iter2.hasNext());
    }

    /**
     * Tests that the constructor that accepts a static hasher properly builds the hasher.
     */
    @Test
    public void testConstructor_StaticHasher() {
        int[] values = {1, 3, 5, 7, 9, 3, 5, 1};
        Iterator<Integer> iter = Arrays.stream(values).iterator();
        StaticHasher hasher = new StaticHasher(iter, shape);

        StaticHasher hasher2 = new StaticHasher(hasher, shape);
        assertEquals(shape, hasher2.getShape());
        assertSameBits(hasher, hasher2);

    }

    /**
     * Tests that calling the constructor with a hasher and the wrong shape throws an
     * IllegalArgumentException.
     */
    @Test
    public void testConstructor_StaticHasher_WrongShape() {
        int[] values = {1, 3, 5, 7, 9, 3, 5, 1};
        Iterator<Integer> iter = Arrays.stream(values).iterator();
        StaticHasher hasher = new StaticHasher(iter, new Shape(testFunctionX, 3, 72, 17));

        try {
            new StaticHasher(hasher, shape);
            fail("Should have thown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing
        }
    }

    /**
     * Tests that passing a hasher other than a StaticHahser to the constructor works as
     * expected.
     */
    @Test
    public void testConstructor_Hasher() {
        int[] expected = {1, 3, 5, 7, 9};

        Hasher testHasher = new Hasher() {

            @Override
            public HashFunctionIdentity getHashFunctionIdentity() {
                return testFunction;
            }

            @Override
            public OfInt getBits(Shape shape) {
                int[] values = {1, 3, 5, 7, 9, 3, 5, 1};
                return Arrays.stream(values).iterator();
            }
        };

        StaticHasher hasher = new StaticHasher(testHasher, shape);
        OfInt iter = hasher.getBits(shape);
        for (int i = 0; i < expected.length; i++) {
            assertTrue(iter.hasNext());
            assertEquals(expected[i], iter.nextInt());
        }
        assertFalse(iter.hasNext());
    }

    /**
     * Tests that passing a hasher other than a StaticHahser and the wrong Shape to the
     * constructor throws an IllegalArgumentException.
     */
    @Test
    public void testConstructor_Hasher_WrongShape() {
        Hasher testHasher = new Hasher() {

            @Override
            public HashFunctionIdentity getHashFunctionIdentity() {
                return testFunctionX;
            }

            @Override
            public OfInt getBits(Shape shape) {
                int[] values = {1, 3, 5, 7, 9, 3, 5, 1};
                return Arrays.stream(values).iterator();
            }
        };

        try {
            new StaticHasher(testHasher, shape);
            fail("Should have thown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing
        }
    }
}
