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
package org.apache.commons.collections4;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Measures traversal of flat and nested concatenation views.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@Warmup(iterations = 3, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ChainedIterableBenchmark {

    /**
     * A flat concatenation or the number of nested views above a leaf of 10,000 values.
     */
    @Param({ "flat", "1", "8", "128", "4096" })
    public String shape;

    private Iterable<Integer> chain;

    /**
     * Builds the iterable before measuring traversal.
     */
    @Setup
    public void setUp() {
        if ("flat".equals(shape)) {
            @SuppressWarnings("unchecked")
            final Iterable<Integer>[] sources = (Iterable<Integer>[]) new Iterable<?>[8];
            for (int i = 0; i < sources.length; i++) {
                sources[i] = Collections.singletonList(i);
            }
            chain = IterableUtils.chainedIterable(sources);
        } else {
            chain = Collections.nCopies(10_000, 1);
            final int depth = Integer.parseInt(shape);
            for (int i = 0; i < depth; i++) {
                chain = IterableUtils.chainedIterable(chain, Collections.emptyList());
            }
        }
    }

    /**
     * Traverses the values and returns their sum.
     *
     * @return the sum of the concatenated values
     */
    @Benchmark
    public int traverse() {
        int sum = 0;
        for (final int value : chain) {
            sum += value;
        }
        return sum;
    }
}
