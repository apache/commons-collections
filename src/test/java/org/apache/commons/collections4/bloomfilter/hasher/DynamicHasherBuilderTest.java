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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;

import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link DynamicHasher.Builder} tests.
 */
public class DynamicHasherBuilderTest {

    private DynamicHasher.Builder builder;
    private HashFunction hf = new MD5Cyclic();
    private final Shape shape = new Shape(hf, 1, 345, 1);
    private String testString = HasherBuilderTest.getExtendedString();

    /**
     * Tests that hashing a byte array works as expected.
     */
    @Test
    public void buildTest_byteArray() {
        final byte[] bytes = testString.getBytes();
        final DynamicHasher hasher = builder.with(bytes).build();
        final int expected = (int) Math.floorMod(hf.apply(bytes, 0), shape.getNumberOfBits());

        final OfInt iter = hasher.iterator(shape);

        assertTrue(iter.hasNext());
        assertEquals(expected, iter.nextInt());
        assertFalse(iter.hasNext());
    }

    /**
     * Tests that an empty hasher works as expected.
     */
    @Test
    public void buildTest_Empty() {
        final DynamicHasher hasher = builder.build();

        final OfInt iter = hasher.iterator(shape);

        assertFalse(iter.hasNext());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.nextInt();
        });
        assertNull(exception.getMessage());
    }

    /**
     * Tests that hashing a string works as expected.
     */
    @Test
    public void buildTest_String() {
        final byte[] bytes = testString.getBytes(StandardCharsets.UTF_8);
        final DynamicHasher hasher = builder.with(testString, StandardCharsets.UTF_8).build();
        final int expected = (int) Math.floorMod(hf.apply(bytes, 0), shape.getNumberOfBits());

        final OfInt iter = hasher.iterator(shape);

        assertTrue(iter.hasNext());
        assertEquals(expected, iter.nextInt());
        assertFalse(iter.hasNext());
    }

    /**
     * Tests that hashing a string works as expected.
     */
    @Test
    public void buildTest_UnencodedString() {
        final byte[] bytes = testString.getBytes(StandardCharsets.UTF_16LE);
        final DynamicHasher hasher = builder.withUnencoded(testString).build();
        final int expected = (int) Math.floorMod(hf.apply(bytes, 0), shape.getNumberOfBits());

        final OfInt iter = hasher.iterator(shape);

        assertTrue(iter.hasNext());
        assertEquals(expected, iter.nextInt());
        assertFalse(iter.hasNext());
    }

    /**
     * Tests that build resets the builder.
     */
    @Test
    public void buildResetTest() {
        builder.with(new byte[] {123});
        final OfInt iter = builder.build().iterator(shape);

        assertTrue(iter.hasNext());
        iter.next();
        assertFalse(iter.hasNext());

        // Nothing added since last build so it should be an empty hasher
        final OfInt iter2 = builder.build().iterator(shape);
        assertFalse(iter2.hasNext());
    }

    /**
     * Sets up the builder for testing.
     */
    @Before
    public void setup() {
        builder = new DynamicHasher.Builder(hf);
    }
}
