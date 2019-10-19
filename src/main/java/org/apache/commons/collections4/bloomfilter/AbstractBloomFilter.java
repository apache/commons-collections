/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.collections4.bloomfilter;

import java.util.BitSet;

/**
 * The interface for all BloomFilter implementations.
 *
 * @since 4.5
 */
public abstract class AbstractBloomFilter {

    /**
     * The maximum log depth at which the log calculation makes no
     * difference to the result.
     */
    private final static int MAX_LOG_DEPTH = 25;


    /**
     * Returns true if {@code other & this == other}. <p> This is the inverse of the match
     * method. </p> {@code X.match(Y)} is the same as {@code Y.inverseMatch(X) }
     *
     * @param other the other Bloom filter to match.
     * @return true if they match.
     */
    public final boolean inverseMatches(AbstractBloomFilter other) {
        return other.matches( this );
    }

    /**
     * Returns true if {@code this & other == this}.
     *
     * This is the standard Bloom filter match.
     *
     * @param other the other Bloom filter to match.
     * @return true if they match.
     */
    public boolean matches(AbstractBloomFilter other) {
        BloomFilter mine = getBitSet().clone();
        mine.and(other.getBitSet());
        return mine.bitEquals( getBitSet() );
    }

    /**
     * Calculates the hamming distance from this Bloom filter to the other. The hamming
     * distance is defined as {@code this xor other} and is the number of bits that have
     * to be flipped to convert one filter to the other.
     *
     * @param other The other Bloom filter to calculate the distance to.
     * @return the distance.
     */
    public int distance(AbstractBloomFilter other) {
        BloomFilter mine = this.getBitSet().clone();
        mine.xor( other.getBitSet() );
        return mine.cardinality();
    }

    /**
     * Gets the hamming weight for this filter.
     *
     * This is the number of bits that are on in the filter.
     *
     * @return The hamming weight.
     */
    int getHammingWeight() {
        return getBitSet().cardinality();
    }

    /**
     * Gets the log2 (log base 2) of this Bloom filter. This is the base 2 logarithm of
     * this Bloom filter if the bits in this filter were considers the bits in an unsigned
     * integer.
     *
     * @return the base 2 logarithm
     */
    public double getLog() {
        return getApproximateLog( MAX_LOG_DEPTH );
    }

    /**
     * Gets the approximate log for this filter. If the Bloom filter is considered as
     * an unsigned number what is the approximate base 2 log of that value. The
     * depth argument indicates how many extra bits are to be considered in the log
     * calculation. At least one bit must be considered. If there are no bits on
     * then the log value is 0.
     *
     * @param depth the number of bits to consider.
     * @return the approximate log.
     */
    private double getApproximateLog(int depth) {
        if (depth == 0) {
            return 0;
        }
        int[] exp = getApproximateLogExponents(depth);
        /*
         * this approximation is calculated using a derivation of
         * http://en.wikipedia.org/wiki/Binary_logarithm#Algorithm
         */
        // the mantissa is the highest bit that is turned on.
        if (exp[0] < 0) {
            // there are no bits so return 0
            return 0;
        }
        double result = exp[0];
        /*
         * now we move backwards from the highest bit until the requested depth is
         * achieved.
         */
        double exp2;
        for (int i = 1; i < exp.length; i++) {
            if (exp[i] == -1) {
                return result;
            }
            exp2 = exp[i] - exp[0]; // should be negative
            result += Math.pow(2.0, exp2);
        }
        return result;
    }

    /**
     * Gets the mantissa and characteristic powers of the log.
     * The mantissa is in position position 0. The remainder are
     * characteristic powers.
     *
     * The depth is the depth to probe for characteristics.  The
     * effective limit is 25 as beyond that the value of the calculated
     * double does not change.
     *
     * @param depth the depth to probe.
     * @return An array of depth integers that are the exponents.
     */
    private int[] getApproximateLogExponents(int depth) {
        if (depth < 1) {
            return new int[] { -1 };
        }
        int[] exp = new int[depth];

        exp[0] = getBitSet().previousSetBit( Integer.MAX_VALUE );
        if (exp[0] < 0) {
            return exp;
        }

        for (int i = 1; i < depth; i++) {
            exp[i] = getBitSet().previousSetBit(exp[i - 1] - 1);
            /*
             * 25 bits from the start make no difference in the double calculation so we can
             * short circuit the method here.
             */
            if (exp[i] - exp[0] < -25) {
                exp[i] = -1;
            }
            if (exp[i] == -1) {
                return exp;
            }
        }
        return exp;
    }

    /**
     * Merges this Bloom filter with the other creating a new filter.
     *
     * @param other the other filter.
     * @throws IllegalArgumentException if other can not be merged.
     */
    void merge(AbstractBloomFilter other) {
        getBitSet().and( other.getBitSet() );
    }

    /**
     * Gets a copy of the bitset representation of the filter.
     *
     * @return the bit set representation.
     */
    abstract protected BloomFilter getBitSet();

}
