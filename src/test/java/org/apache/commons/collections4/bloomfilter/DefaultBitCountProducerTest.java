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

public class DefaultBitCountProducerTest extends AbstractBitCountProducerTest {

    /** Make forEachIndex unordered and contain duplicates. */
    private final int[] values = {10, 1, 10, 1};

    @Override
    protected int[] getExpectedIndices() {
        return values;
    }

    @Override
    protected BitCountProducer createProducer() {
        return consumer -> {
            for (final int i : values) {
                if (!consumer.test(i, 1)) {
                    return false;
                }
            }
            return true;
        };
    }

    @Override
    protected BitCountProducer createEmptyProducer() {
        return consumer -> true;
    }

    @Override
    protected int getAsIndexArrayBehaviour() {
        // The default method streams a BitSet so is distinct and ordered.
        return ORDERED | DISTINCT;
    }

    @Override
    protected int getForEachIndexBehaviour() {
        // The default method has the same behavior as the forEachCount() method.
        return 0;
    }

    @Override
    protected int getForEachCountBehaviour() {
        // The implemented method returns unordered duplicates.
        return 0;
    }
}
