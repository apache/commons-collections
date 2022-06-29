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

import java.util.function.IntPredicate;

/**
 * An IndexTracker implementation that uses an array of integers to track whether or not a
 * number has been seen.  Suitable for Shapes that have few hash functions.
 * @since 4.5
 */
public class ArrayTracker implements IntPredicate {
    private int[] seen;
    private int populated;

    /**
     * Constructs the tracker based on the shape.
     * @param shape the shape to build the tracker for.
     */
    ArrayTracker(Shape shape) {
        seen = new int[shape.getNumberOfHashFunctions()];
    }

    @Override
    public boolean test(int number) {
        if (number < 0) {
            throw new IndexOutOfBoundsException("number may not be less than zero. " + number);
        }
        for (int i = 0; i < populated; i++) {
            if (seen[i] == number) {
                return false;
            }
        }
        seen[populated++] = number;
        return true;
    }
}
