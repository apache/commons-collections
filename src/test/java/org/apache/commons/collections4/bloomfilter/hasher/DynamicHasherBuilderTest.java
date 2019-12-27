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

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.PrimitiveIterator.OfInt;

import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.Before;
import org.junit.Test;

public class DynamicHasherBuilderTest {

    DynamicHasher.Builder builder;
    BloomFilter.Shape shape = new BloomFilter.Shape( new MD5Cyclic(), 1, Integer.MAX_VALUE, 1 );

    @Before
    public void setup() throws NoSuchAlgorithmException
    {
        builder = new DynamicHasher.Builder( new MD5Cyclic());
    }

    //private static final Hash HELLO_HASH = new Hash(3871253994707141660L, -6917270852172884668L);

    @Test
    public void buildTest_byte() {
        DynamicHasher hasher = builder.with((byte) 0x1).build();

        int expected = 1483089307;

        OfInt iter = hasher.getBits(shape);

        assertTrue(iter.hasNext());
        assertEquals( expected, iter.nextInt() );
        assertFalse( iter.hasNext());
    }

    @Test
    public void buildTest_byteArray() {
        DynamicHasher hasher = builder.with("Hello".getBytes()).build();
        int expected = 1519797563;

        OfInt iter = hasher.getBits(shape);

        assertTrue(iter.hasNext());
        assertEquals( expected, iter.nextInt() );
        assertFalse( iter.hasNext());

    }

    @Test
    public void buildTest_String() {
        DynamicHasher hasher = builder.with("Hello").build();
        int expected = 1519797563;

        OfInt iter = hasher.getBits(shape);

        assertTrue(iter.hasNext());
        assertEquals( expected, iter.nextInt() );
        assertFalse( iter.hasNext());
    }

    @Test
    public void buildTest_Empty() {
        DynamicHasher hasher = builder.build();

        OfInt iter = hasher.getBits(shape);

        assertFalse(iter.hasNext());
    }
}
