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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.junit.Test;

/**
 * Tests for the {@link SimpleBloomFilter}.
 */
public class SimpleBloomFilterTest extends AbstractBloomFilterTest<SimpleBloomFilter> {
    @Override
    protected SimpleBloomFilter createEmptyFilter(final Shape shape) {
        return new SimpleBloomFilter(shape);
    }

    @Override
    protected SimpleBloomFilter createFilter(final Shape shape, final Hasher hasher) {
        return new SimpleBloomFilter(shape, hasher);
    }

    @Test
    public void constructorTest() {

        SimpleBloomFilter filter = new SimpleBloomFilter( shape, BitMapProducer.fromLongArray( new long[] { 500L }) );
        List<Long> lst = new ArrayList<Long>();
        filter.forEachBitMap( lst::add );
        assertEquals( 1, lst.size() );
        assertEquals( 500L, lst.get(0).intValue() );

        try {
            filter = new SimpleBloomFilter( shape,
                    BitMapProducer.fromLongArray( new long[] { 500L, 400L, 300L }) );
            fail( "Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // do nothing
        }
    }
}
