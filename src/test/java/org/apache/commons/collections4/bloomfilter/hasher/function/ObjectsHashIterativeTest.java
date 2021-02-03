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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.collections4.bloomfilter.hasher.HashFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests that the Objects hash works correctly.
 */
public class ObjectsHashIterativeTest extends AbstractHashFunctionTest {

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
        for (int i = 1; i <= 5; i++) {
            prev += l;
            l = obj.apply(buffer, i);
            assertEquals(Arrays.deepHashCode(new Object[] {prev, buffer}), l);
        }
    }

    @Override
    protected HashFunction createHashFunction() {
        return new ObjectsHashIterative();
    }
}
