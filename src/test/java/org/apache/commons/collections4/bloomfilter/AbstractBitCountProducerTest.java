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
package org.apache.commons.collections4.bloomfilter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.bloomfilter.BitCountProducer.BitCountConsumer;
import org.junit.jupiter.api.Test;

public abstract class AbstractBitCountProducerTest extends AbstractIndexProducerTest {
    /**
     * A testing BitCountConsumer that always returns true.
     */
    private static final BitCountConsumer TRUE_CONSUMER = (i, j) -> true;
    /**
     * A testing BitCountConsumer that always returns false.
     */
    private static final BitCountConsumer FALSE_CONSUMER = (i, j) -> false;

    /**
     * Creates a producer with some data.
     * @return a producer with some data
     */
    @Override
    protected abstract BitCountProducer createProducer();

    /**
     * Creates an producer without data.
     * @return a producer that has no data.
     */
    @Override
    protected abstract BitCountProducer createEmptyProducer();


    @Test
    public final void testForEachCountResults() {

        assertFalse(createProducer().forEachCount(FALSE_CONSUMER), "non-empty should be false");
        assertTrue(createProducer().forEachCount(TRUE_CONSUMER), "non-empty should be true");
        assertTrue(createEmptyProducer().forEachCount(FALSE_CONSUMER), "empty should be true");
        assertTrue(createEmptyProducer().forEachCount(TRUE_CONSUMER), "empty should be true");

    }

    protected abstract int[][] getExpectedBitCount();

    @Test
    public void testForEachCount() {
        BitCountProducer bcp = createEmptyProducer();
        int[] count = { 0 };
        bcp.forEachCount( (i, j) -> {
            count[0]++;
            return true;
        });
        assertEquals( 0, count[0] );

        bcp = createProducer();
        count[0] = 0;
        int[][] expected = getExpectedBitCount();
        int[][] result = new int[expected.length][2];
        bcp.forEachCount((i, j) -> {
            result[count[0]][0]=i;
            result[count[0]++][1]=j;
            return true;
        });
        assertEquals( expected.length, count[0]);
        assertArrayEquals( expected, result );
    }
}
