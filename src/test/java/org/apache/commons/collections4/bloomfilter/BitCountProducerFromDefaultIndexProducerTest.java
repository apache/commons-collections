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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class BitCountProducerFromDefaultIndexProducerTest extends AbstractBitCountProducerTest {

    int[] data = {0, 63, 1, 1, 64, 127, 128};
    int[] expected = {0, 1, 63, 64, 127, 128};

    @Override
    protected BitCountProducer createProducer() {
        return BitCountProducer.from(IndexProducer.fromIndexArray(data));
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return BitCountProducer.from(IndexProducer.fromIndexArray(new int[0]));
    }

    @Override
    protected int getBehaviour() {
        // The default method streams a BitSet so is distinct and ordered.
        return AS_ARRAY_DISTINCT | AS_ARRAY_ORDERED;
    }

    @Override
    protected int[][] getExpectedBitCount() {
        return new int[][]{{0,1},{63,1},{1,1},{1,1},{64,1},{127,1},{128,1}};
    }

    @Override
    protected int[] getExpectedIndex() {
        return expected;
    }

    @Override
    protected int[] getExpectedForEach() {
        return data;
    }

}
