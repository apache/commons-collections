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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 * Filter configuration class.
 * <p>
 * This class contains the values for the filter configuration.
 * </p><p>
 * 
 * </p> 
 *
 * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter
 *      calculator</a>
 *
 */
public class FilterConfig implements Serializable {
	public static final int STORED_SIZE = 2 * Integer.BYTES;
	private static final long serialVersionUID = 8857015449149940190L;
	private static final double LOG_OF_2 = Math.log(2.0);
	private static final double DENOMINATOR = Math.log(1.0 / (Math.pow(2.0, LOG_OF_2)));
	// number of items in the filter
	private final int numberOfItems;
	// probability of false positives defined as 1 in x;
	private final int probability;
	// number of bits in the filter;
	private final int numberOfBits;
	// number of hash functions
	private final int numberOfHashFunctions;

	public static void write(FilterConfig config, DataOutput out) throws IOException {
		out.writeInt(config.getNumberOfItems());
		out.writeInt(config.getProbability());
	}

	public static FilterConfig read(DataInput ois) throws IOException {
		int nItems = ois.readInt();
		int nProb = ois.readInt();
		return new FilterConfig(nItems, nProb);
	}

	/**
	 * A main method to generate and output the results of different constructor
	 * arguments.
	 * 
	 * Arguments:
	 * <ol>
	 * <li>The number of items to put in the bloom filter</li>
	 * <li>The probability of a collision expressed as X in 1/X</li>
	 * </ol>
	 * 
	 * Outputs the statistics of the filter configuration.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		FilterConfig fc = new FilterConfig(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		System.out.println(String.format("items: %s bits: %s bytes: %s functions: %s p: 1/%s (%s)",
				fc.getNumberOfItems(), fc.getNumberOfBits(), fc.getNumberOfBytes(), fc.getNumberOfHashFunctions(),
				fc.getProbability(), (1.0 / fc.getProbability())));
	}

	/**
	 * Create a filter configuration with the specified number of bits and
	 * probability.
	 * 
	 * @param numberOfItems Number of items to be placed in the filter.
	 * @param probability   The probability of duplicates expressed as 1 in x.
	 */
	public FilterConfig(final int numberOfItems, final int probability) {
		this.numberOfItems = numberOfItems;
		this.probability = probability;
		final double dp = 1.0 / probability;
		final Double dm = Math.ceil((numberOfItems * Math.log(dp)) / DENOMINATOR);
		if (dm > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Resulting filter has more than " + Integer.MAX_VALUE + " bits");
		}
		this.numberOfBits = dm.intValue();
		final Long lk = Math.round((LOG_OF_2 * numberOfBits) / numberOfItems);
		if (lk > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting filter has more than " + Integer.MAX_VALUE + " hash functions");
		}
		numberOfHashFunctions = lk.intValue();
	}

	/**
	 * Get the number of items that are expected in the filter. AKA: n
	 * 
	 * @return the number of items.
	 */
	public int getNumberOfItems() {
		return numberOfItems;
	}

	/**
	 * The probability of a false positive (collision) expressed as 1/x. AKA: 1/p
	 * 
	 * @return the x in 1/x.
	 */
	public int getProbability() {
		return probability;
	}

	/**
	 * The number of bits in the bloom filter. AKA: m
	 * 
	 * @return the number of bits in the bloom filter.
	 */
	public int getNumberOfBits() {
		return numberOfBits;
	}

	/**
	 * The number of hash functions used to construct the filter. AKA: k
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

}