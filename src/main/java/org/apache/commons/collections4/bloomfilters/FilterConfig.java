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

import java.io.Serializable;

/**
 * Filter configuration class.
 * <p>
 * This class contains the values for the filter configuration and is used
 * to convert a ProtoBloomFilter into a BloomFilter.
 * </p>
 *
 * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter
 *      calculator</a>
 *
 * @since 4.5
 */
public final class FilterConfig implements Serializable {
	public static final int STORED_SIZE = 2 * Integer.BYTES;
	private static final long serialVersionUID = 8857015449149940190L;
	private static final double LOG_OF_2 = Math.log(2.0);
	
	// ~ −0.090619058
	private static final double DENOMINATOR = Math.log(1.0 / (Math.pow(2.0, LOG_OF_2)));
	// number of items in the filter
	private final int numberOfItems;
	// probability of false positives defined as 1 in x;
	private final int probability;
	// number of bits in the filter;
	private final int numberOfBits;
	// number of hash functions
	private final int numberOfHashFunctions;


	/**
	 * Create a filter configuration with the specified number of items and
	 * probability.
	 * 
	 * @param numberOfItems Number of items to be placed in the filter.
	 * @param probability   The probability of duplicates expressed as 1 in x.
	 */
	public FilterConfig(final int numberOfItems, final int probability) {
		if (numberOfItems < 1) {
			throw new IllegalArgumentException("Number of Items must be greater than 0");
		}
		if (probability < 1) {
			throw new IllegalArgumentException("Probability must be greater than 0");
		}
		this.numberOfItems = numberOfItems;
		this.probability = probability;
		final double dp = 1.0 / probability;
		final Double dm = Math.ceil((numberOfItems * Math.log(dp)) / DENOMINATOR);
		if (dm > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Resulting filter has more than " + Integer.MAX_VALUE + " bits");
		}
		this.numberOfBits = dm.intValue();
		final Long lk = Math.round((LOG_OF_2 * numberOfBits) / numberOfItems);
		/*
		 * normally we would check that lk is <- Integer.MAX_VALUE but since
		 * numberOfBits is at most Integer.MAX_VALUE the numerator of lk is
		 * log(2) * Integer.MAX_VALUE = 646456992.9449 the value of lk can not be 
		 * above Integer.MAX_VALUE.
		 */
		numberOfHashFunctions = lk.intValue();
	}

	/**
	 * Get the number of items that are expected in the filter. AKA: <code>n</code>
	 * 
	 * @return the number of items.
	 */
	public int getNumberOfItems() {
		return numberOfItems;
	}

	/**
	 * The probability of a false positive (collision) expressed as <code>1/x</code>. AKA: <code>1/p</code>
	 * 
	 * @return the x in 1/x.
	 */
	public int getProbability() {
		return probability;
	}

	/**
	 * The number of bits in the bloom filter. AKA: <code>m</code>
	 * 
	 * @return the number of bits in the bloom filter.
	 */
	public int getNumberOfBits() {
		return numberOfBits;
	}

	/**
	 * The number of hash functions used to construct the filter. AKA: <code>k</code>
	 * 
	 * @return the number of hash functions used to construct the filter.
	 */
	public int getNumberOfHashFunctions() {
		return numberOfHashFunctions;
	}

	/**
	 * The number of bytes in the bloom filter.
	 * 
	 * @return the number of bytes in the bloom filter.
	 */
	public int getNumberOfBytes() {
		return Double.valueOf(Math.ceil(numberOfBits / 8.0)).intValue();
	}
	
	private Object writeReplace() {
		return new ConfigSerProxy(this);
	}
	/**
	 * A Serialization proxy for a FilterConfig.
	 *
	 */
	private static class ConfigSerProxy implements Serializable {

		private static final long serialVersionUID = -7616329495238942105L;
		private final int numberOfItems;
		private final int probability;

		ConfigSerProxy(FilterConfig filterConfig) {
			numberOfItems = filterConfig.numberOfItems;
			probability = filterConfig.probability;
		}

		private Object readResolve() {
			return new FilterConfig(numberOfItems, probability);
		}
	}

}