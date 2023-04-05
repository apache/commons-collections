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

import java.util.Objects;
import java.util.function.IntPredicate;

/**
 * A Testing Hasher that returns the array values % shape.getNumberOfBits().
 *
 * <p>To be used for testing only.</p>
 */
final class ArrayHasher implements Hasher {
    private final int[] values;

    ArrayHasher(final int... values) {
        this.values = values;
    }

    @Override
    public IndexProducer indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        return new Producer(shape);
    }

    @Override
    public IndexProducer uniqueIndices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        return new Producer(shape);
    }

    private class Producer implements IndexProducer {
        Shape shape;

        Producer(final Shape shape) {
            this.shape = shape;
        }

        @Override
        public boolean forEachIndex(final IntPredicate consumer) {
            Objects.requireNonNull(consumer, "consumer");

            int pos = 0;
            for (int i = 0; i < shape.getNumberOfHashFunctions(); i++) {
                final int result = values[pos++] % shape.getNumberOfBits();
                pos = pos % values.length;
                if (!consumer.test(result)) {
                    return false;
                }
            }
            return true;
        }
    }
}
