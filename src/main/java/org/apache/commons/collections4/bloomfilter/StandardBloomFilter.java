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
 * A Bloom filter.
 *
 * Instances are immutable.
 *
 * @since 4.5
 */
public class StandardBloomFilter implements BloomFilter {
    /**
     * An empty BloomFilter
     */
    public static final StandardBloomFilter EMPTY = new StandardBloomFilter(new BitSet(0));
    
    /**
     * The maximum log depth at which the log calculation makes no
     * difference to the result.
     */
    private final static int MAX_LOG_DEPTH = 25;

    /**
     * The BitSet that represents the Bloom filter.
     */
    private final BitSet bitSet;

    /**
     * the base 2 log of the Bloom filter considered as an integer.
     */
    private final double logValue;

    /**
     * Constructor.
     *
     * @param protoFilter the protoFilter to build this Bloom filter from.
     * @param config      the Filter configuration to use to build the Bloom filter.
     */
    public StandardBloomFilter(ProtoBloomFilter protoFilter, BloomFilterConfiguration config) {
        this.bitSet = new BitSet(config.getNumberOfBits());
        protoFilter.getUniqueHashes().forEach(hash -> hash.populate(bitSet, config));
        this.logValue = getApproximateLog(Integer.min(bitSet.length(), MAX_LOG_DEPTH));;
    }

    /**
     * Constructor.
     *
     * A copy of the bitSet parameter is made so that the Bloom filter is isolated
     * from any further changes in the bitSet.
     *
     * @param bitSet The bit set that was built by the config.
     */
    protected StandardBloomFilter(BitSet bitSet) {
        this.bitSet = (BitSet) bitSet.clone();
        this.logValue = getApproximateLog(Integer.min(bitSet.length(), MAX_LOG_DEPTH));;
    }

    @Override
    public boolean inverseMatch(final BloomFilter other) {
        return other.match(this);
    }

    @Override
    public final boolean match(final BloomFilter other) {
        BitSet temp = other.getBitSet();
        temp.and(bitSet);
        return temp.equals(bitSet);
    }

    @Override
    public final int distance(final BloomFilter other) {
        BitSet temp = other.getBitSet();
        temp.xor(bitSet);
        return temp.cardinality();
    }

    @Override
    public final int getHammingWeight() {
        return bitSet.cardinality();
    }

    @Override
    public final double getLog() {
        return logValue;
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

    @Override
    public StandardBloomFilter merge(BloomFilter other) {
        BitSet next = other.getBitSet();
        next.or(bitSet);
        return new StandardBloomFilter(next);
    }

    @Override
    public final BitSet getBitSet() {
        return (BitSet) bitSet.clone();
    }
}
