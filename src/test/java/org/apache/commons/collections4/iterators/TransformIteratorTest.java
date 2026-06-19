/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TransformIterator}.
 */
class TransformIteratorTest {

    @Test
    void testConstructorInvariant() {
        assertThrows(NullPointerException.class, () -> new TransformIterator<>(null));
        assertThrows(NullPointerException.class, () -> new TransformIterator<>(null, null));
    }

    /**
     * Tests <a href="https://issues.apache.org/jira/browse/COLLECTIONS-890">COLLECTIONS-890</a>.
     */
    @Test
    void testNullTransformerActsAsPassThrough() {
        final TransformIterator<Integer, Integer> iterator = new TransformIterator<>(Arrays.asList(1, 2).iterator(), null);
        assertEquals(Integer.valueOf(1), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
    }

    @Test
    void testSetNullTransformerActsAsPassThrough() {
        final TransformIterator<Integer, Integer> iterator = new TransformIterator<>(Arrays.asList(1, 2).iterator(), null);
        iterator.setTransformer(null);
        assertEquals(Integer.valueOf(1), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
    }
}
