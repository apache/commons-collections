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
import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.collections4.bloomfilter.hasher.function.ObjectsHashIterative;
import org.junit.Test;

/**
 * Tests that the Objects hash works correctly..
 *
 */
public class ObjectsHashIterativeTest {

    /**
     * Test that the apply function returns the proper values.
     */
    @Test
    public void applyTest() {
        final ObjectsHashIterative obj = new ObjectsHashIterative();

        final byte[] buffer = "Now is the time for all good men to come to the aid of their country"
            .getBytes(StandardCharsets.UTF_8);

        long l = obj.apply(buffer, 0);
        long prev = 0;
        assertEquals(Arrays.deepHashCode(new Object[] {prev, buffer}), l);
        prev += l;
        l = obj.apply(buffer, 1);
        assertEquals(Arrays.deepHashCode(new Object[] {prev, buffer}), l);
        prev += l;
        l = obj.apply(buffer, 2);
        assertEquals(Arrays.deepHashCode(new Object[] {prev, buffer}), l);
    }

    /**
     * Test that the signature is properly generated.
     */
    @Test
    public void signatureTest() {
        final ObjectsHashIterative obj = new ObjectsHashIterative();
        final String arg = String.format("%s-%s-%s", obj.getName().toUpperCase(Locale.ROOT), obj.getSignedness(),
            obj.getProcessType());
        final long expected = obj.apply(arg.getBytes(StandardCharsets.UTF_8), 0);
        final long expected2 = obj.apply(arg.getBytes(StandardCharsets.UTF_8), 0);
        assertEquals(expected, expected2);
        assertEquals(expected, obj.getSignature());
    }
}
