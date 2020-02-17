/*
    * Licensed to the Apache Software Foundation (ASF) under one
    * or more contributor license agreements.  See the NOTICE file
    * distributed with this work for additional information
    * regarding copyright ownership.  The ASF licenses this file
    * to you under the Apache License, Version 2.0 (the
    * "License"); you may not use this file except in compliance
    * with the License.  You may obtain a copy of the License at
    *
    *     http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
    */
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.Signedness;
import org.junit.Test;

/**
 * Tests of the {@link HashFunctionIdentity#COMMON_COMPARATOR}.
 */
public class CommonComparatorTest {

    private static void assertAfter(final HashFunctionIdentity identity1, final HashFunctionIdentity identity2) {
        assertTrue(0 < HashFunctionIdentity.COMMON_COMPARATOR.compare(identity1, identity2));
    }

    private static void assertBefore(final HashFunctionIdentity identity1, final HashFunctionIdentity identity2) {
        assertTrue(0 > HashFunctionIdentity.COMMON_COMPARATOR.compare(identity1, identity2));
    }

    /**
     * Tests the name ordering is not affected by case.
     */
    @Test
    public void nameOrderTestDifferentCapitalization() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "IMPL1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);

        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl1, impl2));
    }

    /**
     * Tests the name ordering.
     */
    @Test
    public void nameOrderTestDifferentNames() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "impl2", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);

        assertBefore(impl1, impl2);
        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl1, impl1));
        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl2, impl2));
        assertAfter(impl2, impl1);
    }

    /**
     * Tests that the process type ordering in correct.
     */
    @Test
    public void processTypeOrder() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.ITERATIVE, 300L);

        assertBefore(impl1, impl2);
        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl1, impl1));
        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl2, impl2));
        assertAfter(impl2, impl1);
    }

    /**
     * Tests that a change in producer does not change the order.
     */
    @Test
    public void producerDoesNotChangeOrder() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite2", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);

        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl1, impl2));
    }

    /**
     * Tests that signedness ordering is correct.
     */
    @Test
    public void signednessOrder() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.UNSIGNED,
            ProcessType.CYCLIC, 300L);

        assertBefore(impl1, impl2);
        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl1, impl1));
        assertEquals(0, HashFunctionIdentity.COMMON_COMPARATOR.compare(impl2, impl2));
        assertAfter(impl2, impl1);
    }

    /**
     * Tests that the ordering is correct when applied ot a collection.
     */
    @Test
    public void testSortOrder() {
        // in this test the signature is the position in the final collection for the ID
        final TreeSet<HashFunctionIdentity> result = new TreeSet<>(
            HashFunctionIdentity.COMMON_COMPARATOR);
        final List<HashFunctionIdentity> collection = new ArrayList<>();

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED, ProcessType.CYCLIC, 0));

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED, ProcessType.ITERATIVE, 1));

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.UNSIGNED, ProcessType.CYCLIC, 2));

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.UNSIGNED, ProcessType.ITERATIVE, 3));

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl2", Signedness.SIGNED, ProcessType.CYCLIC, 4));

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl2", Signedness.SIGNED, ProcessType.ITERATIVE, 5));

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl2", Signedness.UNSIGNED, ProcessType.CYCLIC, 6));

        collection
            .add(new HashFunctionIdentityImpl("Testing Suite", "impl2", Signedness.UNSIGNED, ProcessType.ITERATIVE, 7));

        Collections.shuffle(collection);

        result.addAll(collection);
        long idx = 0;
        for (final HashFunctionIdentity id : result) {
            assertEquals("Unexpected order for " + HashFunctionIdentity.asCommonString(id), idx++, id.getSignature());
        }
    }
}
