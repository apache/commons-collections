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
package org.apache.commons.collections4.bloomfilter;

import java.util.function.LongPredicate;

/**
 * A long predicate that applies the test func to each member of the {@code ary} in sequence for each call to {@code test()}.
 * if the {@code ary} is exhausted, the subsequent calls to {@code test} are executed with a zero value.
 * If the calls to {@code test} do not exhaust the {@code ary} the {@code processRemaining} method can be called to
 * execute the @{code test} with a zero value for each remaining {@code idx} value.
 */
class CountingLongPredicate implements LongPredicate {

    private int idx;
    private final long[] ary;
    private final LongBiPredicate func;

    /**
     * Constructs an instance that will compare the elements in {@code ary} with the elements returned by {@code func}.
     * function is called as {@code func.test( idxValue, otherValue )}. If there are more {@code otherValue} values than
     * {@code idxValues} then {@code func} is called as {@code func.test( 0, otherValue )}.
     *
     * @param ary The array of long values to compare.
     * @param func The function to apply to the pairs of long values.
     */
    CountingLongPredicate(final long[] ary, final LongBiPredicate func) {
        this.ary = ary;
        this.func = func;
    }

    /**
     * Call the long-long consuming bi-predicate for each remaining unpaired long in
     * the input array. This method should be invoked after the predicate has been
     * passed to {@link BitMapExtractor#processBitMaps(LongPredicate)} to consume any
     * unpaired bitmaps. The second argument to the bi-predicate will be zero.
     *
     * @return true if all calls to the predicate were successful
     */
    boolean processRemaining() {
        // uses local references for optimization benefit.
        int i = idx;
        final long[] a = ary;
        final int limit = a.length;
        while (i != limit && func.test(a[i], 0)) {
            i++;
        }
        return i == limit;
    }

    @Override
    public boolean test(final long other) {
        return func.test(idx == ary.length ? 0 : ary[idx++], other);
    }
}
