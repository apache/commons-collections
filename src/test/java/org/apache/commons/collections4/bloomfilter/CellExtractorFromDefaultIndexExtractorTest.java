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

public class CellExtractorFromDefaultIndexExtractorTest extends AbstractCellExtractorTest {

    int[] data = {0, 63, 1, 64, 128, 1, 127};
    int[] indices = {0, 1, 63, 64, 127, 128};
    int[] values = {1, 2, 1, 1, 1, 1 };

    @Override
    protected CellExtractor createEmptyExtractor() {
        return CellExtractor.from(IndexExtractor.fromIndexArray());
    }

    @Override
    protected CellExtractor createExtractor() {
        return CellExtractor.from(IndexExtractor.fromIndexArray(data));
    }

    @Override
    protected int[] getExpectedIndices() {
        return indices;
    }

    @Override
    protected int[] getExpectedValues() {
        return values;
    }

}
