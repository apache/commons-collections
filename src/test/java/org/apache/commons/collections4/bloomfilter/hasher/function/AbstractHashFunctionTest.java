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
package org.apache.commons.collections4.bloomfilter.hasher.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.junit.jupiter.api.Test;

/**
 * Tests the signature of a hash function.
 */
public abstract class AbstractHashFunctionTest {

    /**
     * Test that the signature is properly generated.
     */
    @Test
    public void signatureTest() {
        final HashFunction hf = createHashFunction();
        final long expected = hf.apply(HashFunctionIdentity.prepareSignatureBuffer(hf), 0);
        assertEquals(expected, hf.getSignature());
        // Should be repeatable
        final long expected2 = hf.apply(HashFunctionIdentity.prepareSignatureBuffer(hf), 0);
        assertEquals(expected, expected2);
        assertEquals("Apache Commons Collections", hf.getProvider());
    }

    /**
     * Creates the hash function.
     *
     * @return the hash function
     */
    protected abstract HashFunction createHashFunction();
}
