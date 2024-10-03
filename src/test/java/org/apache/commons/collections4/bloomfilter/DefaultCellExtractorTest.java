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

public class DefaultCellExtractorTest extends AbstractCellExtractorTest {

    /** Make forEachIndex unordered and contain duplicates. */
    private final int[] indices = {1, 2, 3, 5};
    private final int[] values = {1, 4, 9, 25};

    @Override
    protected CellExtractor createEmptyExtractor() {
        return consumer -> true;
    }

    @Override
    protected CellExtractor createExtractor() {
        return consumer -> {
            for (int i = 0; i < indices.length; i++) {
                if (!consumer.test(indices[i], values[i])) {
                    return false;
                }
            }
            return true;
        };
    }

    @Override
    protected int[] getExpectedIndices() {
        return indices;
    }

    @Override
    protected int[] getExpectedValues() {
        return values;
    }

    @Override
    protected int getForEachIndexBehaviour() {
        // The default method has the same behavior as the forEachCount() method.
        return 0;
    }
}
