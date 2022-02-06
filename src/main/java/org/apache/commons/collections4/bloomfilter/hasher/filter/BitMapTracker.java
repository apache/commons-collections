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
package org.apache.commons.collections4.bloomfilter.hasher.filter;

import org.apache.commons.collections4.bloomfilter.BitMap;
import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * An IndexTracker implementation that uses an array of bit maps to track whether or not a
 * number has been seen.
 * @since 4.5
 */
public class BitMapTracker implements IndexTracker {
    private long[] bits;

    /**
     * Constructs a bit map based tracker for the specified shape.
     * @param shape The shape that is being generated.
     */
    public BitMapTracker(Shape shape) {
        bits = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
    }

    @Override
    public boolean seen(int number) {
        boolean retval = BitMap.contains(bits, number);
        BitMap.set(bits, number);
        return retval;
    }
}