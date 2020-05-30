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
import org.junit.jupiter.api.Test;

/**
 * Tests the MD5 cyclic hash function.
 */
public class MD5CyclicTest extends AbstractHashFunctionTest {

    /**
     * Test that the apply function returns the proper values.
     */
    @Test
    public void applyTest() {
        final MD5Cyclic md5 = new MD5Cyclic();
        final long l1 = 0x8b1a9953c4611296L;
        final long l2 = 0xa827abf8c47804d7L;
        final byte[] buffer = "Hello".getBytes();

        long l = md5.apply(buffer, 0);
        assertEquals(l1, l);
        l = md5.apply(buffer, 1);
        assertEquals(l1 + l2, l);
        l = md5.apply(buffer, 2);
        assertEquals(l1 + l2 + l2, l);
    }

    @Override
    protected HashFunction createHashFunction() {
        return new MD5Cyclic();
    }
}
