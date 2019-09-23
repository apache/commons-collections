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

import java.util.Objects;

/**
 * The definition of a filter configuration.  
 * A simple Bloom filter configuration implementation that derives the values
 * from the number of items and the probability of collision.
 * 
 * <p>
 * This interface defines the values for the filter configuration and is used to
 * convert a ProtoBloomFilter into a BloomFilter.
 * </p>
 * 
 * <p>
 * This class contains the values for the filter configuration and is used to
 * convert a ProtoBloomFilter into a BloomFilter.
 * </p>
 *
 * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter
 *      calculator</a>
 *
 * @since 4.5
 * 
 */
public final class FilterConfiguration {

    /**
     * The natural logarithm of 2.
     */
    private static final double LOG_OF_2 = Math.log(2.0);

    /**
     * 1 / 2^log(2) =  ~ −0.090619058
     */ 
    private static final double DENOMINATOR = Math.log(1.0 / (Math.pow(2.0, LOG_OF_2)));
    /**
     *  number of items in the filter
     */
    private final int numberOfItems;
    /** 
     * probability of false positives.
     */
    private final double probability;
    /**
     *  number of bits in the filter;
     */
    private final int numberOfBits;
    /**
     *  number of hash functions
     */
    private final int numberOfHashFunctions;

    /**
     * The hash code for this filter.
     */
    private final int hashCode;

    /**
     * Create a filter configuration with the specified number of items and
     * probability.
     *
     * @param numberOfItems Number of items to be placed in the filter.
     * @param probability   The probability of duplicates. Must be in the range (0.0,1.0).
     */
    public FilterConfiguration(final int numberOfItems, final double probability) {
        if (numberOfItems < 1) {
            throw new IllegalArgumentException("Number of Items must be greater than 0");
        }
        if (probability <= 0.0) {
            throw new IllegalArgumentException("Probability must be greater than 0.0");
        }
        if (probability >= 1.0) {
            throw new IllegalArgumentException("Probability must be less than 1.0");
        }
        this.numberOfItems = numberOfItems;
        this.probability = probability;
        final Double dm = Math.ceil((numberOfItems * Math.log(probability)) / DENOMINATOR);
        if (dm > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Resulting filter has more than " + Integer.MAX_VALUE + " bits");
        }
        this.numberOfBits = dm.intValue();
        final Long lk = Math.round((LOG_OF_2 * numberOfBits) / numberOfItems);
        /*
         * normally we would check that lk is <- Integer.MAX_VALUE but since
         * numberOfBits is at most Integer.MAX_VALUE the numerator of lk is log(2) *
         * Integer.MAX_VALUE = 646456992.9449 the value of lk can not be above
         * Integer.MAX_VALUE.
         */
        numberOfHashFunctions = lk.intValue();
        hashCode = Objects.hash(numberOfBits, numberOfHashFunctions, numberOfItems, probability);
    }

    /**
     * Get the number of items that are expected in the filter. AKA: {@code n }
     *
     * @return the number of items.
     */
   
    public int getNumberOfItems() {
        return numberOfItems;
    }
    
    /**
     * The probability of a false positive (collision). AKA: {@code p}
     *
     * @return the probability of a false positive.
     */
    public double getProbability() {
        return probability;
    }

    /**
     * The number of bits in the Bloom filter. AKA: {@code m }
     *
     * @return the number of bits in the Bloom filter.
     */
    public int getNumberOfBits() {
        return numberOfBits;
    }

    /**
     * The number of hash functions used to construct the filter. AKA: {@code k }
     *
     * @return the number of hash functions used to construct the filter.
     */
    public int getNumberOfHashFunctions() {
        return numberOfHashFunctions;
    }

    /**
     * The number of bytes in the Bloom filter.
     *
     * @return the number of bytes in the Bloom filter.
     */
    public int getNumberOfBytes() {
        return Double.valueOf(Math.ceil(numberOfBits / 8.0)).intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FilterConfiguration) {
            FilterConfiguration other = (FilterConfiguration) o;
            return other.getNumberOfBits() == getNumberOfBits()
                    && other.getNumberOfHashFunctions() == getNumberOfHashFunctions()
                    && other.getNumberOfItems() == getNumberOfItems() && other.getProbability() == getProbability();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    
}
