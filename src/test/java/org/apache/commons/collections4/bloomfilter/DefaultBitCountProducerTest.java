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


import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;

public class DefaultBitCountProducerTest extends AbstractBitCountProducerTest {

    /** Make forEachIndex unordered and contain duplicates. */
    private int[] values = {10, 1, 10, 1};

    @Override
    protected int[] getExpectedIndices() {
        return values;
    }

    @Override
    protected BitCountProducer createProducer() {
        return new BitCountProducer() {
            @Override
            public boolean forEachIndex(IntPredicate predicate) {
                Objects.requireNonNull(predicate);
                for (int i : values) {
                    if (!predicate.test(i)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean forEachCount(BitCountConsumer consumer) {
                int[] vals = values.clone();
                Arrays.sort(vals);
                for (int i : vals) {
                    if (!consumer.test(i, 1)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return new BitCountProducer() {
            @Override
            public boolean forEachIndex(IntPredicate predicate) {
                Objects.requireNonNull(predicate);
                return true;
            }
            @Override
            public boolean forEachCount(BitCountConsumer consumer) {
                return true;
            }
        };
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // The default method streams a BitSet so is distinct and ordered.
        // However the forEachIndex may not be distinct and ordered and
        // the test cannot distinguish the two cases.
        return DISTINCT | ORDERED;
    }

    @Override
    protected int getForEachIndexBehaviour() {
        return 0;
    }

    @Override
    protected int getForEachCountBehaviour() {
        return ORDERED;
    }
}
