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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.LongPredicate;

/**
 * A LongConsumer that builds an Array of BitMaps as produced by a BitMapProducer.
 *
 */
public class ArrayBuilder implements LongPredicate {
    private long[] result;
    private int idx = 0;
    private int bucketCount = 0;

    /**
     * Constructor that creates an empty ArrayBuilder.
     * @param shape The shape used to generate the BitMaps.
     */
    public ArrayBuilder(Shape shape) {
        this(shape, null);
    }

    /**
     * Constructor that creates an array builder with an initial value.
     * @param shape The shape used to generate the BitMaps.
     * @param initialValue an array of BitMap values to initialize the builder with.  May be {@code null}.
     * @throws IllegalArgumentException is the length of initialValue is greater than the number of
     * bitmaps as specified by the number of bits in the Shape.
     */
    public ArrayBuilder(Shape shape, long[] initialValue) {
        Objects.requireNonNull(shape, "shape");
        result = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        if (initialValue != null) {
            if (initialValue.length > result.length) {
                throw new IllegalArgumentException(
                        String.format("initialValue length (%s) is longer than shape length (%s)",
                                initialValue.length, result.length));
            }
            bucketCount = initialValue.length;
            System.arraycopy(initialValue, 0, result, 0, initialValue.length);
        }
    }

    @Override
    public boolean test(long bitmap) {
        result[idx++] |= bitmap;
        bucketCount = bucketCount >= idx ? bucketCount : idx;
        return true;
    }

    /**
     * Returns the array.
     * @return the Array of BitMaps.
     */
    public long[] getArray() {
        return Arrays.copyOf(result, bucketCount);
    }
}