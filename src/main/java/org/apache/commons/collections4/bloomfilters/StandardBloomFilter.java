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
package org.apache.commons.collections4.bloomfilters;

import java.util.BitSet;

/**
 * A bloom filter.
 *
 * Instances are immutable.
 *
 * @since 4.5
 *
 */
public class StandardBloomFilter implements BloomFilter {

    /*
     * The maximum log depth is the depth at which the log calculation makes no
     * difference to the double result.
     */
    private final static int MAX_LOG_DEPTH = 25;

    /**
     * The BitSet that represents the bloom filter.
     */
    protected final BitSet bitSet;

    // the hamming value once we have calculated it.
    private transient Integer hamming;

    // the base 2 log of the bloom filter considered as an integer.
    private transient Double logValue;

    /**
     * An empty BloomFilter
     */
    public static final StandardBloomFilter EMPTY = new StandardBloomFilter( new BitSet(0));

    /**
     * Constructor.
     *
     * @param protoFilter the protoFilter to build this bloom filter from.
     * @param config      the Filter configuration to use to build the bloom filter.
     */
    public StandardBloomFilter(ProtoBloomFilter protoFilter, FilterConfig config) {
        this.bitSet = new BitSet(config.getNumberOfBits());
        protoFilter.getUniqueHashes().forEach(hash -> hash.populate(bitSet, config));
        this.hamming = null;
        this.logValue = null;
    }

    /**
     * Constructor.
     *
     * A copy of the bitSet parameter is made so that the bloom filter is isolated
     * from any further changes in the bitSet.
     *
     * @param bitSet The bit set that was built by the config.
     */
    protected StandardBloomFilter(BitSet bitSet) {
        this.bitSet = (BitSet) bitSet.clone();
        this.hamming = null;
        this.logValue = null;
    }

    /**
     * Return true if other &amp; this == other
     *
     * This is the inverse of the match method.
     *
     * {@code X.match(Y)} is the same as {@code Y.inverseMatch(X) }
     *
     * @param other the other bloom filter to match.
     * @return true if they match.
     */
    @Override
    public boolean inverseMatch(final BloomFilter other) {
        return other.match(this);
    }

    /**
     * Return true if {@code this & other == this }.
     *
     * This is the standard bloom filter match.
     *
     * @param other the other bloom filter to match.
     * @return true if they match.
     */
    @Override
    public final boolean match(final BloomFilter other) {        
        BitSet temp = other.getBitSet();
        temp.and(bitSet);
        return temp.equals(bitSet);
    }

    /**
     * Calculate the hamming distance from this bloom filter to the other. The
     * hamming distance is defined as this xor other and is the number of bits that
     * have to be flipped to convert one filter to the other.
     *
     * @param other The other bloom filter to calculate the distance to.
     * @return the distance.
     */
    @Override
    public final int distance(final BloomFilter other) {
        BitSet temp = other.getBitSet();
        temp.xor(bitSet);
        return temp.cardinality();
    }

    /**
     * Get the hamming weight for this filter.
     *
     * This is the number of bits that are on in the filter.
     *
     * @return The hamming weight.
     */
    @Override
    public final int getHammingWeight() {
        if (hamming == null) {
            hamming = bitSet.cardinality();
        }
        return hamming;
    }

    /**
     * The the log(2) of this bloom filter. This is the base 2 logarithm of this
     * bloom filter if the bits in this filter were considers the bits in an
     * unsigned integer.
     *
     * @return the base 2 logarithm
     */
    @Override
    public final double getLog() {
        if (logValue == null) {
            logValue = getApproximateLog(Integer.min(bitSet.length(), MAX_LOG_DEPTH));
        }
        return logValue;
    }

    /**
     * Get the approximate log for this filter. If the bloom filter is considered as
     * an unsigned number what is the approximate base 2 log of that value. The
     * depth argument indicates how many extra bits are to be considered in the log
     * calculation. At least one bit must be considered. If there are no bits on
     * then the log value is 0.

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
     * The mantissa of the log in in position position 0. The remainder are
     * characteristic powers.
     *
     * @param depth
     * @return An array of depth integers that are the exponents.
     */
    private int[] getApproximateLogExponents(int depth) {
        if (depth < 1) {
            return new int[] { -1 };
        }
        int[] exp = new int[depth];

        exp[0] = bitSet.length() - 1;
        if (exp[0] < 0) {
            return exp;
        }

        for (int i = 1; i < depth; i++) {
            exp[i] = bitSet.previousSetBit(exp[i - 1] - 1);
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

    @Override
    public String toString() {
        return bitSet.toString();
    }

    @Override
    public int hashCode() {
        return bitSet.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StandardBloomFilter && this.bitSet.equals(((StandardBloomFilter) other).bitSet);
    }

    /**
     * Merge this bloom filter with the other creating a new filter.
     *
     * @param other the other filter.
     * @return a new filter.
     */
    @Override
    public StandardBloomFilter merge(BloomFilter other) {
        BitSet next = other.getBitSet();
        next.or(bitSet);
        return new StandardBloomFilter(next);
    }

    /**
     * Return the a copy of the bitset in in the filter.
     *
     * @return the bit set representation.
     */
    @Override
    public final BitSet getBitSet() {
        return (BitSet) bitSet.clone();
    }
}
