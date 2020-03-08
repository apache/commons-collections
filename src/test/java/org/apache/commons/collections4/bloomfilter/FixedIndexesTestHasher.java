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

import org.apache.commons.collections4.bloomfilter.hasher.HashFunctionIdentity;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;

import java.util.Arrays;
import java.util.PrimitiveIterator.OfInt;

/**
 * A Hasher implementation to return fixed indexes. Duplicates are allowed.
 * The shape is ignored when generating the indexes.
 *
 * <p><strong>This is not a real hasher and is used for testing only</strong>.
 */
class FixedIndexesTestHasher implements Hasher {
    /** The shape. */
    private Shape shape;
    /** The indexes. */
    private int[] indexes;

    /**
     * Create an instance.
     *
     * @param shape the shape
     * @param indexes the indexes
     */
    FixedIndexesTestHasher(Shape shape, int... indexes) {
        this.shape = shape;
        this.indexes = indexes;
    }

    @Override
    public OfInt getBits(Shape shape) {
        if (!this.shape.equals(shape)) {
            throw new IllegalArgumentException(
                String.format("shape (%s) does not match internal shape (%s)", shape, this.shape));
        }
        return Arrays.stream(indexes).iterator();
    }

    @Override
    public HashFunctionIdentity getHashFunctionIdentity() {
        return shape.getHashFunctionIdentity();
    }

    @Override
    public boolean isEmpty() {
        return indexes.length == 0;
    }
}
