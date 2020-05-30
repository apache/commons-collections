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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;

import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link DynamicHasher}.
 */
public class DynamicHasherTest {
    private DynamicHasher.Builder builder;
    private Shape shape;

    private final HashFunctionIdentity testFunction = new HashFunctionIdentity() {

        @Override
        public String getName() {
            return "Test Function";
        }

        @Override
        public ProcessType getProcessType() {
            return ProcessType.CYCLIC;
        }

        @Override
        public String getProvider() {
            return "Apache Commons Collection Tests";
        }

        @Override
        public long getSignature() {
            return 0;
        }

        @Override
        public Signedness getSignedness() {
            return Signedness.SIGNED;
        }
    };

    /**
     * Sets up the DynamicHasher.
     */
    @BeforeEach
    public void setup() {
        builder = new DynamicHasher.Builder(new MD5Cyclic());
        shape = new Shape(new MD5Cyclic(), 3, 72, 17);
    }

    /**
     * Tests that the expected bits are returned from hashing.
     */
    @Test
    public void testGetBits() {

        final int[] expected = {6, 69, 44, 19, 10, 57, 48, 23, 70, 61, 36, 11, 2, 49, 24, 15, 62};

        final Hasher hasher = builder.with("Hello", StandardCharsets.UTF_8).build();

        final OfInt iter = hasher.iterator(shape);

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
        final int[] expected = {6, 69, 44, 19, 10, 57, 48, 23, 70, 61, 36, 11, 2, 49, 24, 15, 62, 1, 63, 53, 43, 17, 7, 69,
            59, 49, 39, 13, 3, 65, 55, 45, 35, 25};

        final Hasher hasher = builder.with("Hello", StandardCharsets.UTF_8).with("World", StandardCharsets.UTF_8).build();

        final OfInt iter = hasher.iterator(shape);

        for (final int element : expected) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.nextInt());
        }
        assertFalse(iter.hasNext());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            iter.next();
        });
        assertNull(exception.getMessage());
    }

    /**
     * Tests that retrieving bits for the wrong shape throws an exception.
     */
    @Test
    public void testGetBits_WrongShape() {

        final Hasher hasher = builder.with("Hello", StandardCharsets.UTF_8).build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            hasher.iterator(new Shape(testFunction, 3, 72, 17));
        });
        assertNotNull(exception.getMessage());
    }
}
