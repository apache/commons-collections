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
package org.apache.commons.collections4.bloomfilter.hasher.function;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.commons.collections4.bloomfilter.hasher.function.MD5Cyclic;
import org.junit.Test;

/**
 * Tests the MD5 cyclic hash function.
 */
public class MD5CyclicTest {

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

    /**
     * Test that the signature is properly generated.
     */
    @Test
    public void signatureTest() {
        final MD5Cyclic md5 = new MD5Cyclic();
        final String arg = String.format("%s-%s-%s", md5.getName().toUpperCase(Locale.ROOT), md5.getSignedness(),
            md5.getProcessType());
        final long expected = md5.apply(arg.getBytes(StandardCharsets.UTF_8), 0);
        assertEquals(expected, md5.getSignature());
        assertEquals("Apache Commons Collections", md5.getProvider());
    }
}
