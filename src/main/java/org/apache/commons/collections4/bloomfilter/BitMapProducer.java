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
import java.util.function.LongConsumer;

/**
 * Produces bit map longs for a Bloom filter.
 *
 *  Each bit map is a little-endian long value representing a block of bits of this filter.
 *
 * <p>The returned array will have length {@code ceil(m / 64)} where {@code m} is the
 * number of bits in the filter and {@code ceil} is the ceiling function.
 * Bits 0-63 are in the first long. A value of 1 at a bit position indicates the bit
 * index is enabled.
 *
 * The producer may produce empty bit maps at the end of the sequence.
 *
 */
public interface BitMapProducer {

    /**
     * Performs the given action for each {@code index} that represents an enabled bit.
     * Any exceptions thrown by the action are relayed to the caller.
     *
     * @param consumer the action to be performed for each non-zero bit index.
     * @throws NullPointerException if the specified action is null
     */
    void forEachBitMap(LongConsumer consumer);

    /**
     * A LongConsumer that builds an Array of BitMaps as produced by a BitMapProducer.
     *
     */
    public class ArrayBuilder implements LongConsumer {
        private long[] result;
        private int idx=0;

        /**
         * Constructor.
         * @param shape The shape used to generate the BitMaps.
         */
        public ArrayBuilder( Shape shape ) {
            result = new long[ BitMap.numberOfBuckets( shape.getNumberOfBits() )];
        }
        @Override
        public void accept(long bitmap) {
            result[idx++] = bitmap;
        }

        /**
         * Trims the resulting array so that there are no trailing empty BitMaps
         * @return
         */
        public long[] trim() {
            return Arrays.copyOf( result, idx );
        }
    }

}
