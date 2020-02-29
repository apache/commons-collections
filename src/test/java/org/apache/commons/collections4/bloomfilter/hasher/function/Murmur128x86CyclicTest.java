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

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.Test;

/**
 * Test that the Murmur3 128 x86 hash function works correctly.
 */
public class Murmur128x86CyclicTest {

    /**
     * Test that the apply function returns the proper values.
     */
    @Test
    public void applyTest() {
        final Murmur128x86Cyclic murmur = new Murmur128x86Cyclic();

        final long l1 = 0xe7eb60dabb386407L;
        final long l2 = 0xc3ca49f691f73056L;
        final byte[] buffer = "Now is the time for all good men to come to the aid of their country"
            .getBytes(StandardCharsets.UTF_8);

        long l = murmur.apply(buffer, 0);
        assertEquals(l1, l);
        l = murmur.apply(buffer, 1);
        assertEquals(l1 + l2, l);
        l = murmur.apply(buffer, 2);
        assertEquals(l1 + l2 + l2, l);
    }

    /**
     * Test that the signature is properly generated.
     */
    @Test
    public void signatureTest() {
        final Murmur128x86Cyclic murmur = new Murmur128x86Cyclic();
        final String arg = String.format("%s-%s-%s", murmur.getName().toUpperCase(Locale.ROOT), murmur.getSignedness(),
            murmur.getProcessType());
        final long expected = murmur.apply(arg.getBytes(StandardCharsets.UTF_8), 0);
        assertEquals(expected, murmur.getSignature());
        assertEquals("Apache Commons Collections", murmur.getProvider());
    }
}
