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
package org.apache.commons.collections4.bloomfilter.hasher.filter;

import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * An IndexTracker implementation that uses an array of integers to track whether or not a
 * number has been seen.  Suitable for Shapes that have few hash functions.
 * @since 4.5
 */
public class ArrayTracker implements IndexTracker {
    private int[] seenAry;
    private int idx;

    /**
     * Constructs the tracker based on the shape.
     * @param shape the shape to build the tracker for.
     */
    public ArrayTracker(Shape shape) {
        seenAry = new int[shape.getNumberOfHashFunctions()];
        idx = 0;
    }

    @Override
    public boolean seen(int number) {
        for (int i = 0; i < idx; i++) {
            if (seenAry[i] == number) {
                return true;
            }
        }
        seenAry[idx++] = number;
        return false;
    }
}
