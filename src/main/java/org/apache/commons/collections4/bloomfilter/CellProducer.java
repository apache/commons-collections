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

import java.util.function.IntPredicate;

/**
 * Some Bloom filter implementations use a count rather than a bit flag.  The term {@code Cell} is used to
 * refer to these counts.  This class is the equivalent of the index producer except that it produces a cell
 * value associated with each index.
 *
 * <p>Note that a CellProducer may return duplicate indices and may be unordered.
 *
 * <p>Implementations must guarantee that:
 *
 * <ul>
 * <li>The mapping of index to cells is the combined sum of cells at each index.
 * <li>For every unique value produced by the IndexProducer there will be at least one matching
 * index and cell produced by the CellProducer.
 * <li>The CellProducer will not generate indices that are not output by the IndexProducer.
 * </ul>
 *
 * <p>Note that implementations that do not output duplicate indices for CellProducer and
 * do for IndexProducer, or vice versa, are consistent if the distinct indices from each are
 * the same.
 *
 * <p>For example the mapping [(1,2),(2,3),(3,1)] can be output with many combinations including:
 * <pre>
 * [(1,2),(2,3),(3,1)]
 * [(1,1),(1,1),(2,1),(2,1),(2,1),(3,1)]
 * [(1,1),(3,1),(1,1),(2,1),(2,1),(2,1)]
 * [(3,1),(1,1),(2,2),(1,1),(2,1)]
 * ...
 * </pre>
 *
 * @since 4.5
 */
@FunctionalInterface
public interface CellProducer extends IndexProducer {

    /**
     * Performs the given action for each {@code <index, cell>} pair where the cell is non-zero.
     * Any exceptions thrown by the action are relayed to the caller. The consumer is applied to each
     * index-cell pair, if the consumer returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further pairs are processed.
     *
     * Duplicate indices are not required to be aggregated. Duplicates may be output by the producer as
     * noted in the class javadoc.
     *
     * @param consumer the action to be performed for each non-zero cell.
     * @return {@code true} if all cells return true from consumer, {@code false} otherwise.
     * @throws NullPointerException if the specified consumer is null
     */
    boolean forEachCell(CellConsumer consumer);

    /**
     * The default implementation returns indices with ordering and uniqueness of {@code forEachCell()}.
     */
    @Override
    default boolean forEachIndex(final IntPredicate predicate) {
        return forEachCell((i, v) -> predicate.test(i));
    }

    /**
     * Creates a CellProducer from an IndexProducer. The resulting
     * producer will return every index from the IndexProducer with a cell value of 1.
     *
     * <p>Note that the CellProducer does not remove duplicates. Any use of the
     * CellProducer to create an aggregate mapping of index to counts, such as a
     * CountingBloomFilter, should use the same CellProducer in both add and
     * subtract operations to maintain consistency.
     * </p>
     * @param idx An index producer.
     * @return A CellProducer with the same indices as the IndexProducer.
     */
    static CellProducer from(final IndexProducer idx) {
        return new CellProducer() {
            @Override
            public boolean forEachCell(final CellConsumer consumer) {
                return idx.forEachIndex(i -> consumer.test(i, 1));
            }

            @Override
            public int[] asIndexArray() {
                return idx.asIndexArray();
            }

            @Override
            public boolean forEachIndex(final IntPredicate predicate) {
                return idx.forEachIndex(predicate);
            }
        };
    }

    /**
     * Represents an operation that accepts an {@code <index, cell>} pair representing
     * the cell a bit index. Returns {@code true}
     * if processing should continue, {@code false} otherwise.
     *
     * <p>Note: This is a functional interface as a specialization of
     * {@link java.util.function.BiPredicate} for {@code int}.</p>
     */
    @FunctionalInterface
    interface CellConsumer {
        /**
         * Performs an operation on the given {@code <index, count>} pair.
         *
         * @param index the bit index.
         * @param cell the cell value at the specified bit index.
         * @return {@code true} if processing should continue, {@code false} if processing should stop.
         */
        boolean test(int index, int cell);
    }
}
