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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PrimitiveIterator.OfInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToLongBiFunction;

import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.DynamicHasher;
import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class HasherBloomFilterTest extends BloomFilterTest {


    @Override
    protected HasherBloomFilter createFilter(Hasher hasher, BloomFilter.Shape shape) {
        return new HasherBloomFilter( hasher, shape );
    }

    @Override
    protected BloomFilter createEmptyFilter(BloomFilter.Shape shape) {
        return new HasherBloomFilter( shape );
    }

    @Test
    public void constructorTest_NonStatic() throws NoSuchAlgorithmException {
        BloomFilter.Shape shape = new BloomFilter.Shape( new MD5Cyclic(), 3, 72, 17 );
        DynamicHasher hasher = new DynamicHasher.Builder( new MD5Cyclic() ).with( "Hello").build();
        HasherBloomFilter filter = createFilter( hasher, shape );
        long[] lb = filter.getBits();
        assertEquals( 2, lb.length );
        assertEquals( 0x6203101001888c44L, lb[0]);
        assertEquals( 0x60L, lb[1]);
    }


}
