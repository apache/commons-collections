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

import java.security.NoSuchAlgorithmException;
import java.util.PrimitiveIterator.OfInt;

import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.Before;
import org.junit.Test;

/**
 * DynamicHasher Builder tests.
 *
 */
public class DynamicHasherBuilderTest {

    private DynamicHasher.Builder builder;
    private final Shape shape = new Shape( new MD5Cyclic(), 1, Integer.MAX_VALUE, 1 );

    /**
     * Sets up the builder for testing.
     * @throws NoSuchAlgorithmException if MD5 is not available.
     */
    @Before
    public void setup() throws NoSuchAlgorithmException
    {
        builder = new DynamicHasher.Builder( new MD5Cyclic());
    }

    /**
     * Tests that hashing a byte works as expected.
     */
    @Test
    public void buildTest_byte() {
        final DynamicHasher hasher = builder.with((byte) 0x1).build();

        final int expected = 1483089307;

        final OfInt iter = hasher.getBits(shape);

        assertTrue(iter.hasNext());
        assertEquals( expected, iter.nextInt() );
        assertFalse( iter.hasNext());
    }

    /**
     * Tests that hashing a byte array works as expected.
     */
    @Test
    public void buildTest_byteArray() {
        final DynamicHasher hasher = builder.with("Hello".getBytes()).build();
        final int expected = 1519797563;

        final OfInt iter = hasher.getBits(shape);

        assertTrue(iter.hasNext());
        assertEquals( expected, iter.nextInt() );
        assertFalse( iter.hasNext());

    }

    /**
     * Tests that hashing a string works as expected.
     */
    @Test
    public void buildTest_String() {
        final DynamicHasher hasher = builder.with("Hello").build();
        final int expected = 1519797563;

        final OfInt iter = hasher.getBits(shape);

        assertTrue(iter.hasNext());
        assertEquals( expected, iter.nextInt() );
        assertFalse( iter.hasNext());
    }

    /**
     * Tests that an empty hasher works as expected.
     */
    @Test
    public void buildTest_Empty() {
        final DynamicHasher hasher = builder.build();

        final OfInt iter = hasher.getBits(shape);

        assertFalse(iter.hasNext());
    }
}
