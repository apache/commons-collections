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

/**
 * Provides functions to assist in IndexExtractor creation and manipulation.
 *
 * @see IndexExtractor
 */
final class IndexUtils {

    /**
     * The maximum array size for the methods in this class: {@value}.
     */
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Ensure the array can add an element at the specified index.
     *
     * @param array the array to check.
     * @param index the index to add at.
     * @return the array or a newly allocated copy of the array.
     */
    static int[] ensureCapacityForAdd(final int[] array, final int index) {
        if (index >= array.length) {
            return Arrays.copyOf(array, (int) Math.min(MAX_ARRAY_SIZE, Math.max(array.length * 2L, index + 1)));
        }
        return array;
    }

    /**
     *  Don't instantiate.
     */
    private IndexUtils() {
        // empty
    }
}
