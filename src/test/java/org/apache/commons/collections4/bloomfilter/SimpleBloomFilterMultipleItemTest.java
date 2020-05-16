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

import java.util.function.Function;

import org.apache.commons.collections4.bloomfilter.SimpleBloomFilter.SimpleBuilder;
import org.apache.commons.collections4.bloomfilter.SimpleBloomFilterTest.TypedObject;
import org.apache.commons.collections4.bloomfilter.hasher.DynamicHasher;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.function.Murmur128x64Cyclic;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link SimpleBloomFilter} when T represents a single item in
 * the filter.
 */
public class SimpleBloomFilterMultipleItemTest extends AbstractBloomFilterTest {

    public SimpleBloomFilterMultipleItemTest() {
        shape = new Shape(new Murmur128x64Cyclic(), 3, 72, 17);
    }

    protected Function<TypedObject, SimpleBuilder> getFunc() {
        return new Function<TypedObject, SimpleBuilder>() {

            @Override
            public SimpleBuilder apply(TypedObject t) {
                return (SimpleBuilder) new SimpleBuilder().withUnencoded(t.hello).withUnencoded(t.world);

            }
        };
    }

    @Override
    protected SimpleBloomFilter<TypedObject> createEmptyFilter(final Shape shape) {
        return new SimpleBloomFilter<TypedObject>(shape, getFunc());
    }

    @Override
    protected SimpleBloomFilter<TypedObject> createFilter(final Hasher hasher, final Shape shape) {
        return new SimpleBloomFilter<TypedObject>(hasher, shape, getFunc());
    }

    /**
     * Tests that adding {@code TypedObject} adds as multiple items.
     */
    @Test
    public void mergeTTest() {
        SimpleBloomFilter<TypedObject> bloomFilter = createEmptyFilter(shape);
        bloomFilter.merge(new TypedObject());

        Hasher hasher = new DynamicHasher.Builder(new Murmur128x64Cyclic()).withUnencoded("hello")
                .withUnencoded("world").build();

        BloomFilter other = new BitSetBloomFilter(hasher, shape);
        Assert.assertArrayEquals(other.getBits(), bloomFilter.getBits());

    }

    /**
     * Tests that contains {@code TypedObject} works as expected.
     */
    @Test
    public void containsTTest() {
        SimpleBloomFilter<TypedObject> bloomFilter = createEmptyFilter(shape);
        bloomFilter.merge(new TypedObject());

        TypedObject t = new TypedObject();

        Assert.assertTrue(bloomFilter.contains(t));

        t.hello = "hola";
        Assert.assertFalse(bloomFilter.contains(t));
    }

    /**
     * Simple class for use in testing testing {@code merge( T )}.
     *
     */
    public class TypedObject {
        String hello = "hello";
        String world = "world";
    }
}
