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
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfInt;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.Signedness;
import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link CachingHasher}.
 */
public class CachingHasherTest {
    private CachingHasher.Builder builder;
    private Shape shape;

    private final HashFunctionIdentity testFunction =  new HashFunctionIdentityImpl("Apache Commons Collection Testing",
            "Test Function", Signedness.SIGNED, ProcessType.CYCLIC, 0 );

    /**
     * Sets up the CachingHasher.
     */
    @Before
    public void setup() {
        builder = new CachingHasher.Builder(new MD5Cyclic());
        shape = new Shape(new MD5Cyclic(), 3, 72, 17);
    }

    /**
     * Tests that the expected bits are returned from hashing.
     */
    @Test
    public void testGetBits() {
        final int[] expected = { 6, 69, 44, 19, 10, 57, 48, 23, 70, 61, 36, 11, 2, 49, 24, 15, 62 };

        final Hasher hasher = builder.with("Hello").build();

        final OfInt iter = hasher.getBits(shape);

        for (final int element : expected) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.nextInt());
        }
        assertFalse(iter.hasNext());
    }

    /**
     * Tests that bits from multiple hashes are returned correctly.
     */
    @Test
    public void testGetBits_MultipleHashes() {
        final int[] expected = { 6, 69, 44, 19, 10, 57, 48, 23, 70, 61, 36, 11, 2, 49, 24, 15, 62, 1, 63, 53, 43, 17, 7,
            69, 59, 49, 39, 13, 3, 65, 55, 45, 35, 25 };

        final Hasher hasher = builder.with("Hello").with("World").build();

        final OfInt iter = hasher.getBits(shape);

        for (final int element : expected) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.nextInt());
        }
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thown NoSuchElementException");
        } catch (final NoSuchElementException ignore) {
            // do nothing
        }
    }

    /**
     * Tests that retrieving bits for the wrong shape throws an exception.
     */
    @Test
    public void testGetBits_WongShape() {
        final Hasher hasher = builder.with("Hello").build();

        try {
            hasher.getBits(new Shape(testFunction, 3, 72, 17));
            fail("Should have thown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            // do nothing
        }
    }

    /**
     * Tests if isEmpty() reports correctly and the iterator returns no values.
     */
    @Test
    public void testIsEmpty() {
        CachingHasher hasher = builder.build();
        assertTrue(hasher.isEmpty());
        final OfInt iter = hasher.getBits(shape);
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thown NoSuchElementException");
        } catch (final NoSuchElementException expected) {
            // do nothing
        }

        assertFalse(builder.with("Hello").build().isEmpty());
    }

    @Test
    public void testNonCyclicHashFunction() {
        HashFunctionIdentity hfi = new HashFunctionIdentityImpl("Apache Commons Collection Testing", "Testing-NonCyclic-Hash",
                Signedness.SIGNED, ProcessType.ITERATIVE, 0 );

        try {
            new CachingHasher(hfi, new long[][] { { 1, 4 }, { 3, 6 } });
            fail("Should have thrown exception");
        } catch (IllegalArgumentException expected) {
            // do nothing.
        }

    }

    @Test
    public void testGetBuffers() {
        final CachingHasher hasher = builder.with("Hello").build();

        List<long[]> buffers = hasher.getBuffers();
        HashFunctionIdentity id = hasher.getHashFunctionIdentity();

        CachingHasher hasher2 = new CachingHasher( id, buffers );

        PrimitiveIterator.OfInt iter1 = hasher.getBits(shape);
        PrimitiveIterator.OfInt iter2 = hasher2.getBits(shape);

        while (iter1.hasNext()) {
            assertTrue( "Too few values in second hasher", iter2.hasNext());
            assertEquals( "Wrong value", iter1.next(), iter2.next() );
        }
        assertFalse( "Second hasher has too many values", iter2.hasNext());
    }

    @Test
    public void testConstructorWithLongArray() {
        final CachingHasher hasher = builder.with("Hello").build();

        List<long[]> buffers = hasher.getBuffers();
        assertEquals( 1, buffers.size() );

        long[][] ary = new long[1][];
        ary[0] = buffers.get(0);

        HashFunctionIdentity id = hasher.getHashFunctionIdentity();


        CachingHasher hasher2 = new CachingHasher( id, ary );

        PrimitiveIterator.OfInt iter1 = hasher.getBits(shape);
        PrimitiveIterator.OfInt iter2 = hasher2.getBits(shape);

        while (iter1.hasNext()) {
            assertTrue( "Too few values in second hasher", iter2.hasNext());
            assertEquals( "Wrong value", iter1.next(), iter2.next() );
        }
        assertFalse( "Second hasher has too many values", iter2.hasNext());
    }
}
