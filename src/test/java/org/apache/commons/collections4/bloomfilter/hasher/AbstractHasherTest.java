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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.bloomfilter.AbstractIndexProducerTest;
import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.Test;

public abstract class AbstractHasherTest extends AbstractIndexProducerTest {

    protected abstract Hasher createHasher();

    protected abstract Hasher createEmptyHasher();

    /**
     * The shape of the Hashers filters for testing.
     * <ul>
     *  <li>Hash functions (k) = 17
     *  <li>Number of bits (m) = 72
     * </ul>
     * @return the testing shape.
     */
    protected final Shape getTestShape() {
        return Shape.fromKM(17, 72);
    }

    @Override
    protected IndexProducer createProducer() {
        return createHasher().indices(getTestShape());
    }

    @Override
    protected IndexProducer createEmptyProducer() {
        return createEmptyHasher().indices(getTestShape());
    }

    @Test
    public void testSize() {
        assertEquals(1, createHasher().size());
        assertEquals(0, createEmptyHasher().size());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(createHasher().isEmpty());
        assertTrue(createEmptyHasher().isEmpty());
    }
}
