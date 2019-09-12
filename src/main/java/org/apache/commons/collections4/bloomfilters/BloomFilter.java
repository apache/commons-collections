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
 * This is an immutable instance.
 *
 */
public class BloomFilter {

	// the bitset we are using
	private final BitSet bitSet;

	// the hamming value once we have calculated it.
	private transient Integer hamming;

	// the base 2 log of the bloom filter considered as an integer.
	private transient Double logValue;

	/**
	 * Constructor.
	 * 
	 * A copy of the bitSet parameter is made so that the bloomfilter
	 * is isolated from any further changes in the bitSet.
	 * 
	 * @param bitSet The bit set that was built by the config.
	 */
	public BloomFilter(BitSet bitSet) {
		this.bitSet = (BitSet)bitSet.clone();
		this.hamming = null;
		this.logValue = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param config The FilterConfig to define this BloomFilter.
	 */
	public BloomFilter(FilterConfig config) {
		this(new BitSet(config.getNumberOfBits()));
	}

	/**
	 * Return true if other & this == other
	 * 
	 * @param other the other bloom filter to match.
	 * @return true if they match.
	 */
	public boolean inverseMatch(final BloomFilter other) {
		return other.match(this);
	}

	/**
	 * Return true if this & other == this
	 * 
	 * @param other the other bloom filter to match.
	 * @return true if they match.
	 */
	public final boolean match(final BloomFilter other) {
		BitSet temp = BitSet.valueOf(this.bitSet.toByteArray());
		temp.and(other.bitSet);
		return temp.equals(this.bitSet);
	}

	/**
	 * Calculate the hamming distance from this bloom filter to the other. The
	 * hamming distance is defined as this xor other and is the number of bits that
	 * have to be flipped to convert one filter to the other.
	 * 
	 * @param other The other bloom filter to calculate the distance to.
	 * @return the distance.
	 */
	public final int distance(final BloomFilter other) {
		BitSet temp = BitSet.valueOf(this.bitSet.toByteArray());
		temp.xor(other.bitSet);
		return temp.cardinality();
	}

	/**
	 * Get the hamming weight for this filter.
	 * 
	 * This is the number of bits that are on in the filter.
	 * 
	 * @return The hamming weight.
	 */
	public final int getHammingWeight() {
		if (hamming == null) {
			hamming = bitSet.cardinality();
		}
		return hamming;
	}

	/**
	 * The the log(2) of this bloom filter. This is the base 2 logarithm of this
	 * bloom filter if thie bits in this filter were considers the bits in an
	 * unsigned integer.
	 * 
	 * @return the base 2 logarithm
	 */
	public final double getLog() {
		if (logValue == null) {
			logValue = getApproximateLog(bitSet.size());
		}
		return logValue;
	}

	/**
	 * Get the approximate log for this filter. If the bloom filter is considered as
	 * an unsigned number what is the approximate base 2 log of that value. The
	 * depth argument indicates how many extra bits are to be considered in the log
	 * calculation. At least one bit must be considered. If there are no bits on
	 * then the log value is 0.
	 * 
	 * @see AbstractBloomFilter.getApproximateLog()
	 * @param depth the number of bits to consider.
	 * @return the approximate log.
	 */
	private final double getApproximateLog(int depth) {
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
		// now we move backwards from the highest bit until the requested
		// is achieved.
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
	private final int[] getApproximateLogExponents(int depth) {
		int[] exp = new int[depth + 1];

		exp[0] = bitSet.length() - 1;
		if (exp[0] < 0) {
			return exp;
		}

		for (int i = 1; i < depth; i++) {
			exp[i] = bitSet.previousSetBit(exp[i - 1] - 1);
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
	public boolean equals(Object other) {
		return (other instanceof BloomFilter) ? this.bitSet.equals(((BloomFilter) other).bitSet) : false;
	}

	/**
	 * Merge this bloom filter with the other creating a new filter.
	 * 
	 * @param other the other filter.
	 * @return a new filter.
	 */
	public BloomFilter merge(BloomFilter other) {
		BitSet next = (BitSet) this.bitSet.clone();
		next.or(other.bitSet);
		return new BloomFilter(next);		
	}

	/**
	 * Return the filter as a bitset.
	 * 
	 * @return the bit set representation.
	 */
	public BitSet asBitSet() {
		return bitSet;
	}
}
