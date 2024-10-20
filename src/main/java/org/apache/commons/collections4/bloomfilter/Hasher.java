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

/**
 * A Hasher creates {@link IndexExtractor}s based on the hash implementation and the provided {@link Shape}.
 *
 * @since 4.5.0-M1
 */
public interface Hasher {

    /**
     * Creates an IndexExtractor for this hasher based on the Shape.
     *
     * <p>
     * The {@code IndexExtractor} will create indices within the range defined by the number of bits in the shape. The total number of indices will respect the
     * number of hash functions per item defined by the shape. However the count of indices may not be a multiple of the number of hash functions if the
     * implementation has removed duplicates.
     * </p>
     *
     * <p>
     * This IndexExtractor must be deterministic in that it must return the same indices for the same Shape.
     * </p>
     *
     * <p>
     * No guarantee is made as to order of indices.
     * </p>
     * <p>
     * Duplicates indices for a single item may be produced.
     * </p>
     *
     * @param shape the shape of the desired Bloom filter.
     * @return the iterator of integers
     */
    IndexExtractor indices(Shape shape);
}
