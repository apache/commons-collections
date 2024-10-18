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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * A predicate that applies the test {@code func} to each member of the {@code ary} in
 * sequence for each call to {@code test()}. if the {@code ary} is exhausted,
 * the subsequent calls to {@code test} are executed with a {@code null} value.
 * If the calls to {@code test} do not exhaust the {@code ary} the {@code
 * processRemaining} method can be called to execute the @{code test} with a
 * {@code null} value for each remaining {@code idx} value.
 *
 * @param <T> the type of object being compared.
 */
class CountingPredicate<T> implements Predicate<T> {
    private int idx;
    private final T[] ary;
    private final BiPredicate<T, T> func;

    /**
     * Constructs an instance that will compare the elements in {@code ary} with the
     * elements returned by {@code func}. function is called as {@code func.test(
     * idxValue, otherValue )}. If there are more {@code otherValue} values than
     * {@code idxValues} then {@code func} is called as {@code func.test(null, otherValue)}.
     *
     * @param ary  The array of long values to compare.
     * @param func The function to apply to the pairs of long values.
     */
    CountingPredicate(final T[] ary, final BiPredicate<T, T> func) {
        this.ary = ary;
        this.func = func;
    }

    /**
     * Call {@code BiPredicate<T, T>} for each remaining unpaired {@code <T>} in the
     * input array. This method should be invoked after the predicate has been
     * passed to a {@code Extractor.forEach<T>(BiPredicate<T, T>)} to consume any
     * unpaired {@code <T>}s. The second argument to the BiPredicate will be {@code null}.
     *
     * @return true if all calls to the predicate were successful
     */
    boolean processRemaining() {
        // uses local references for optimization benefit.
        int i = idx;
        final T[] a = ary;
        final int limit = a.length;
        while (i != limit && func.test(a[i], null)) {
            i++;
        }
        return i == limit;
    }

    @Override
    public boolean test(final T other) {
        return func.test(idx == ary.length ? null : ary[idx++], other);
    }
}
