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
import java.util.function.LongPredicate;

/**
 * An object that produces indices of a Bloom filter.
 *
 * @since 4.5
 */
public interface IndexProducer {

    /**
     * Each index is passed to the predicate.  The predicate is applied to each
     * index value, if the predicate returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further indices are processed.
     *
     * <p>Any exceptions thrown by the action are relayed to the caller.</p>
     *
     * <p>Indices ordering is not guaranteed</p>
     *
     * @param predicate the action to be performed for each non-zero bit index.
     * @returns {@code true} if all indexes return true from consumer, {@code false} otherwise.
     * @throws NullPointerException if the specified action is null
     */
    boolean forEachIndex(IntPredicate predicate);

    /**
     * Creates an IndexProducer from a @{code BitMapProducer}.
     * @param producer the @{code BitMapProducer}
     * @return a new @{code IndexProducer}.
     */
    static IndexProducer fromBitMapProducer(BitMapProducer producer) {
        Objects.requireNonNull(producer, "producer");
        return new IndexProducer() {
            @Override
            public boolean forEachIndex(IntPredicate consumer) {
                LongPredicate longPredicate = new LongPredicate() {
                    int wordIdx = 0;

                    @Override
                    public boolean test(long word) {
                        int i = wordIdx;
                        while (word != 0) {
                            if ((word & 1) == 1) {
                                if (!consumer.test(i)) {
                                    return false;
                                }
                            }
                            word >>>= 1;
                            i++;
                        }
                        wordIdx += 64;
                        return true;
                    }
                };
                return producer.forEachBitMap(longPredicate::test);
            }

        };
    }
}
