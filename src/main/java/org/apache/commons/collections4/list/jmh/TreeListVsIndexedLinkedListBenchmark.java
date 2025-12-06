/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.list.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.list.ExtendedTreeList;
import org.apache.commons.collections4.list.IndexedLinkedList;

/**
 * JMH benchmark comparing IndexedLinkedList vs ExtendedTreeList.
 *
 * Benchmarks:
 *  - prepend (add at beginning)
 *  - append (add at end)
 *  - insert in the middle
 *  - prepend collection
 *  - append collection
 *  - insert collection in the middle
 *  - random access
 *  - pop front
 *  - pop back
 *  - pop at random location
 *  - remove range via subList(a, b).clear()
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class TreeListVsIndexedLinkedListBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {

        /**
         * Which implementation to benchmark.
         * JMH will run every @Benchmark method once for each value.
         */
        @Param({"IndexedLinkedList", "ExtendedTreeList"})
        public String impl;

        /**
         * Initial size of the list before performing the operation under test.
         */
        @Param({"1000", "10000", "100000"})
        public int size;

        /**
         * Size of the batch collection used in addAll benchmarks.
         */
        @Param({"10", "100"})
        public int batchSize;

        /**
         * The list under test for this invocation.
         * This is rebuilt before each benchmark invocation.
         */
        public List<Integer> list;

        /**
         * Collection used for prepend/append/insert collection benchmarks.
         */
        public Collection<Integer> batch;

        /**
         * Random used for random index selection.
         */
        public Random random;

        /**
         * Range for subList(a, b).clear()
         */
        public int fromIndex;
        public int toIndex;

        @Setup(Level.Trial)
        public void setupTrial() {
            random = new Random(42);

            batch = new ArrayList<>(batchSize);
            for (int i = 0; i < batchSize; i++) {
                batch.add(-i - 1); // arbitrary distinct values
            }

            // Fixed middle range for remove-range benchmark
            fromIndex = Math.max(0, size / 4);
            toIndex   = Math.max(fromIndex + 1, size / 2);
        }

        @Setup(Level.Invocation)
        public void setupInvocation() {
            list = createList();

            // Fill list with deterministic contents [0, 1, 2, ..., size-1]
            for (int i = 0; i < size; i++) {
                list.add(i);
            }
        }

        private List<Integer> createList() {
            switch (impl) {
                case "IndexedLinkedList":
                    return new IndexedLinkedList<>();
                case "ExtendedTreeList":
                    return new ExtendedTreeList<>();
                default:
                    throw new IllegalStateException("Unknown impl: " + impl);
            }
        }
    }

    // ------------------------------------------------------------------
    // Single-element operations
    // ------------------------------------------------------------------

    /** add at beginning (prepend) */
    @Benchmark
    public void prepend(BenchmarkState state) {
        state.list.add(0, -1);
    }

    /** add at end (append) */
    @Benchmark
    public void append(BenchmarkState state) {
        state.list.add(-1);
    }

    /** insert in the middle */
    @Benchmark
    public void insertMiddle(BenchmarkState state) {
        int mid = state.list.size() / 2;
        state.list.add(mid, -1);
    }

    // ------------------------------------------------------------------
    // Collection operations
    // ------------------------------------------------------------------

    /** prepend collection */
    @Benchmark
    public void prependCollection(BenchmarkState state) {
        state.list.addAll(0, state.batch);
    }

    /** append collection */
    @Benchmark
    public void appendCollection(BenchmarkState state) {
        state.list.addAll(state.batch);
    }

    /** insert collection in the middle */
    @Benchmark
    public void insertCollectionMiddle(BenchmarkState state) {
        int mid = state.list.size() / 2;
        state.list.addAll(mid, state.batch);
    }

    // ------------------------------------------------------------------
    // Random access and removals
    // ------------------------------------------------------------------

    /** access random element */
    @Benchmark
    public void randomAccess(BenchmarkState state, Blackhole bh) {
        int idx = state.random.nextInt(state.list.size());
        Integer value = state.list.get(idx);
        bh.consume(value);
    }

    /** pop front */
    @Benchmark
    public void popFront(BenchmarkState state, Blackhole bh) {
        Integer value = state.list.remove(0);
        bh.consume(value);
    }

    /** pop back */
    @Benchmark
    public void popBack(BenchmarkState state, Blackhole bh) {
        int lastIndex = state.list.size() - 1;
        Integer value = state.list.remove(lastIndex);
        bh.consume(value);
    }

    /** pop at random location */
    @Benchmark
    public void popRandom(BenchmarkState state, Blackhole bh) {
        int idx = state.random.nextInt(state.list.size());
        Integer value = state.list.remove(idx);
        bh.consume(value);
    }

    /** remove range via subList(a, b).clear() */
    @Benchmark
    public void removeRangeSubListClear(BenchmarkState state, Blackhole bh) {
        state.list.subList(state.fromIndex, state.toIndex).clear();
        // consume size so JIT can't completely ignore the mutation
        bh.consume(state.list.size());
    }

    // ------------------------------------------------------------------
    // Main method for running from IDE (optional)
    // ------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(TreeListVsIndexedLinkedListBenchmark
                            .class.getSimpleName())
                .detectJvmArgs()      // lets JMH reuse your IDE JVM args
                .build();

        new Runner(opt).run();
    }
}
