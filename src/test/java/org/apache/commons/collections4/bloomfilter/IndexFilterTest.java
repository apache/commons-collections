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

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentityImpl;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.Signedness;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

/**
 * Tests for the {@link IndexFilters}.
 */
public class IndexFilterTest {

    /**
     * The shape of the dummy Bloom filter.
     * This is used as an argument to a Hasher that just returns fixed indexes
     * so the parameters do not matter.
     */
    private final Shape shape = new Shape(new HashFunctionIdentityImpl(
        "Apache Commons Collections", "Dummy", Signedness.SIGNED, ProcessType.CYCLIC, 0L),
        50, 3000, 4);

    @Test
    public void testApplyThrowsWithNullArguments() {
        final FixedIndexesTestHasher hasher = new FixedIndexesTestHasher(shape, 1, 2, 3);
        final Shape shape = this.shape;
        final ArrayList<Integer> actual = new ArrayList<>();
        final IntConsumer consumer = actual::add;

        try {
            IndexFilters.distinctIndexes(null, shape, consumer);
            Assert.fail("null hasher");
        } catch (final NullPointerException expected) {
            // Ignore
        }

        try {
            IndexFilters.distinctIndexes(hasher, null, consumer);
            Assert.fail("null shape");
        } catch (final NullPointerException expected) {
            // Ignore
        }

        try {
            IndexFilters.distinctIndexes(hasher, shape, null);
            Assert.fail("null consumer");
        } catch (final NullPointerException expected) {
            // Ignore
        }

        // All OK together
        IndexFilters.distinctIndexes(hasher, shape, consumer);
    }

    @Test
    public void testApply() {
        assertFilter(1, 4, 6, 7, 9);
    }

    @Test
    public void testApplyWithDuplicates() {
        assertFilter(1, 4, 4, 6, 7, 7, 7, 7, 7, 9);
    }

    private void assertFilter(final int... indexes) {
        final FixedIndexesTestHasher hasher = new FixedIndexesTestHasher(shape, indexes);
        final Set<Integer> expected = Arrays.stream(indexes).boxed().collect(Collectors.toSet());
        final ArrayList<Integer> actual = new ArrayList<>();

        IndexFilters.distinctIndexes(hasher, shape, actual::add);

        Assert.assertEquals(expected.size(), actual.size());
        // Check the array has all the values.
        // We do not currently check the order of indexes from the
        // hasher.iterator() function.
        for (final Integer index : actual) {
            Assert.assertTrue(expected.contains(index));
        }
    }
}
