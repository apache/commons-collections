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
package org.apache.commons.collections4.bloomfilter.hasher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.ProcessType;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity.Signedness;
import org.junit.jupiter.api.Test;

/**
 * Tests of the {@link HashFunctionValidator}.
 */
public class HashFunctionValidatorTest {

    /**
     * Tests that name is used in the equality check.
     */
    @Test
    public void testName() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "impl2", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);

        assertTrue(HashFunctionValidator.areEqual(impl1, impl1));
        assertTrue(HashFunctionValidator.areEqual(impl2, impl2));
        assertFalse(HashFunctionValidator.areEqual(impl1, impl2));
        assertFalse(HashFunctionValidator.areEqual(impl2, impl1));
    }

    /**
     * Tests that name is not affected by case.
     */
    @Test
    public void testNameIsCaseInsensitive() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "IMPL1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);

        assertTrue(HashFunctionValidator.areEqual(impl1, impl2));
    }

    /**
     * Tests that process type is used in the equality check.
     */
    @Test
    public void testProcessType() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.ITERATIVE, 300L);

        assertTrue(HashFunctionValidator.areEqual(impl1, impl1));
        assertTrue(HashFunctionValidator.areEqual(impl2, impl2));
        assertFalse(HashFunctionValidator.areEqual(impl1, impl2));
        assertFalse(HashFunctionValidator.areEqual(impl2, impl1));
    }

    /**
     * Tests that provider is <strong>not</strong> used in the equality check.
     */
    @Test
    public void testProviderIsNotUsedInEqualityCheck() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite2", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);

        assertTrue(HashFunctionValidator.areEqual(impl1, impl1));
        assertTrue(HashFunctionValidator.areEqual(impl2, impl2));
        assertTrue(HashFunctionValidator.areEqual(impl1, impl2));
        assertTrue(HashFunctionValidator.areEqual(impl2, impl1));
    }

    /**
     * Tests that signedness is used in the equality check.
     */
    @Test
    public void testSignedness() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.UNSIGNED,
            ProcessType.CYCLIC, 300L);

        assertTrue(HashFunctionValidator.areEqual(impl1, impl1));
        assertTrue(HashFunctionValidator.areEqual(impl2, impl2));
        assertFalse(HashFunctionValidator.areEqual(impl1, impl2));
        assertFalse(HashFunctionValidator.areEqual(impl2, impl1));
    }

    /**
     * Test the check method throws when the two hash functions are not equal.
     */
    @Test
    public void testCheckThrows() {
        final HashFunctionIdentityImpl impl1 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.SIGNED,
            ProcessType.CYCLIC, 300L);
        final HashFunctionIdentityImpl impl2 = new HashFunctionIdentityImpl("Testing Suite", "impl1", Signedness.UNSIGNED,
            ProcessType.CYCLIC, 300L);
        assertThrows(IllegalArgumentException.class, () -> HashFunctionValidator.checkAreEqual(impl1, impl2));
    }
}
