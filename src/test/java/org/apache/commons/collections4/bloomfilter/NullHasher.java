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
 * A Hasher that returns no values.
 *
 * <p>To be used for testing only.</p>
 */
final class NullHasher implements Hasher {

    /**
     * The instance of the Null Hasher.
     */
    static final NullHasher INSTANCE = new NullHasher();

    private static final IndexExtractor INDEX_EXTRACTOR = new IndexExtractor() {
        @Override
        public int[] asIndexArray() {
            return new int[0];
        }

        @Override
        public boolean processIndices(final IntPredicate consumer) {
            Objects.requireNonNull(consumer, "consumer");
            return true;
        }
    };

    private NullHasher() {
        // No instances
    }

    @Override
    public IndexExtractor indices(final Shape shape) {
        Objects.requireNonNull(shape, "shape");
        return INDEX_EXTRACTOR;
    }
}
