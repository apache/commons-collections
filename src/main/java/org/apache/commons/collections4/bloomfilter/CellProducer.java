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

import java.util.TreeMap;
import java.util.function.IntPredicate;


/**
 * Some Bloom filter implementations use a count rather than a bit flag. The term {@code Cell} is used to
 * refer to these counts and their associated index.  This class is the equivalent of the index producer except
 * that it produces cells.
 *
 * <p>Note that a CellProducer must not return duplicate indices and must be ordered.</p>
 *
 * <p>Implementations must guarantee that:</p>
 *
 * <ul>
 * <li>The IndexProducer implementation returns unique ordered indices.</li>
 * <li>The cells are produced in IndexProducer order.</li>
 * <li>For every value produced by the IndexProducer there will be only one matching
 * cell produced by the CellProducer.</li>
 * <li>The CellProducer will not generate cells with indices that are not output by the IndexProducer.</li>
 * <li>The IndexProducer will not generate indices that have a zero count for the cell.</li>
 * </ul>
 *
 * @since 4.5
 */
@FunctionalInterface
public interface CellProducer extends IndexProducer {

    /**
     * Represents an operation that accepts an {@code <index, count>} pair.
     * Returns {@code true} if processing should continue, {@code false} otherwise.
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
         * @param count the cell value at the specified bit index.
         * @return {@code true} if processing should continue, {@code false} if processing should stop.
         */
        boolean test(int index, int count);
    }

    /**
     * Creates a CellProducer from an IndexProducer.
     *
     * <p>Note the following properties:
     * <ul>
     * <li>Each index returned from the IndexProducer is assumed to have a cell value of 1.</li>
     * <li>The CellProducer aggregates duplicate indices from the IndexProducer.</li>
     * </ul>
     *
     * <p>A CellProducer that outputs the mapping [(1,2),(2,3),(3,1)] can be created from many combinations
     * of indices including:
     * <pre>
     * [1, 1, 2, 2, 2, 3]
     * [1, 3, 1, 2, 2, 2]
     * [3, 2, 1, 2, 1, 2]
     * ...
     * </pre>
     *
     * @param producer An index producer.
     * @return A CellProducer with the same indices as the IndexProducer.
     */
    static CellProducer from(final IndexProducer producer) {
        return new CellProducer() {
            /**
             * Class to track cell values in the TreeMap.
             */
            final class CounterCell implements Comparable<CounterCell> {
                final int idx;
                int count;

                CounterCell(final int idx, final int count) {
                    this.idx = idx;
                    this.count = count;
                }

                @Override
                public int compareTo(final CounterCell other) {
                    return Integer.compare(idx, other.idx);
                }
            }

            TreeMap<CounterCell, CounterCell> counterCells = new TreeMap<>();

            @Override
            public int[] asIndexArray() {
                populate();
                return counterCells.keySet().stream().mapToInt(c -> c.idx).toArray();
            }

            @Override
            public boolean forEachCell(final CellConsumer consumer) {
                populate();
                for (final CounterCell cell : counterCells.values()) {
                    if (!consumer.test(cell.idx, cell.count)) {
                        return false;
                    }
                }
                return true;
            }

            private void populate() {
                if (counterCells.isEmpty()) {
                    producer.forEachIndex( idx -> {
                        final CounterCell cell = new CounterCell(idx, 1);
                        final CounterCell counter = counterCells.get(cell);
                        if (counter == null) {
                            counterCells.put(cell, cell);
                        } else {
                            counter.count++;
                        }
                        return true;
                    });
                }
            }
        };
    }

    /**
     * Performs the given action for each {@code cell}  where the cell count is non-zero.
     *
     * <p>Some Bloom filter implementations use a count rather than a bit flag.  The term {@code Cell} is used to
     * refer to these counts.</p>
     *
     * <p>Any exceptions thrown by the action are relayed to the caller. The consumer is applied to each
     * cell. If the consumer returns {@code false} the execution is stopped, {@code false}
     * is returned, and no further pairs are processed.</p>
     *
     * @param consumer the action to be performed for each non-zero cell.
     * @return {@code true} if all cells return true from consumer, {@code false} otherwise.
     * @throws NullPointerException if the specified consumer is null
     */
    boolean forEachCell(CellConsumer consumer);

    /**
     * The default implementation returns distinct and ordered indices for all cells with a non-zero count.
     */
    @Override
    default boolean forEachIndex(final IntPredicate predicate) {
        return forEachCell((i, v) -> predicate.test(i));
    }

    @Override
    default IndexProducer uniqueIndices() {
        return this;
    }
}

