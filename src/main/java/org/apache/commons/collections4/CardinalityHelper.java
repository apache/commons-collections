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

package org.apache.commons.collections4;


import java.util.Map;

/**
 * Helper class to easily access cardinality properties of two collections.
 *
 * @param <O> the element type
 */

public class CardinalityHelper<O> {
    /**
     * Contains the cardinality for each object in collection A.
     */
    final Map<O, Integer> cardinalityA;

    /**
     * Contains the cardinality for each object in collection B.
     */
    final Map<O, Integer> cardinalityB;

    /**
     * Create a new CardinalityHelper for two collections.
     *
     * @param a the first collection
     * @param b the second collection
     */
    CardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        cardinalityA = CollectionUtils.<O>getCardinalityMap(a);
        cardinalityB = CollectionUtils.<O>getCardinalityMap(b);
    }

    /**
     * Returns the frequency of this object in collection A.
     *
     * @param obj the object
     * @return the frequency of the object in collection A
     */
    public int freqA(final Object obj) {
        return getFreq(obj, cardinalityA);
    }

    /**
     * Returns the frequency of this object in collection B.
     *
     * @param obj the object
     * @return the frequency of the object in collection B
     */
    public int freqB(final Object obj) {
        return getFreq(obj, cardinalityB);
    }

    private int getFreq(final Object obj, final Map<?, Integer> freqMap) {
        final Integer count = freqMap.get(obj);
        if (count != null) {
            return count.intValue();
        }
        return 0;
    }

    /**
     * Returns the maximum frequency of an object.
     *
     * @param obj the object
     * @return the maximum frequency of the object
     */
    public final int max(final Object obj) {
        return Math.max(freqA(obj), freqB(obj));
    }

    /**
     * Returns the minimum frequency of an object.
     *
     * @param obj the object
     * @return the minimum frequency of the object
     */
    public final int min(final Object obj) {
        return Math.min(freqA(obj), freqB(obj));
    }
}
