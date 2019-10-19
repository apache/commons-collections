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
import java.util.Objects;

/**
 * The definition of a filter configuration. A simple Bloom filter configuration
 * implementation that derives the values from the number of items and the probability of
 * collision.
 *
 * <p> This interface defines the values for the filter configuration and is used to
 * convert a ProtoBloomFilter into a BloomFilter. </p>
 *
 * <p> This class contains the values for the filter configuration and is used to convert
 * a ProtoBloomFilter into a BloomFilter. </p>
 *
 * <h2>Interrelatedness of values</h2>
 *
 *  <dl>
 *  <dt>Number of Items (AKA: {@code n})</dt>
 *  <dd>{@code n = ceil(m / (-k / log(1 - exp(log(p) / k))))}</dd>
 *  <dt>Probability of Collision (AKA: {@code p})</dt>
 *  <dd>{@code p =  (1 - exp(-kn/m))^k}</dd>
 *  <dt>Number of Bits (AKA: {@code m})</dt>
 *  <dd>{@code m = ceil((n * log(p)) / log(1 / pow(2, log(2))))}</dd>
 *  <dt>Number of Functions (AKA: {@code k})</dt>
 *  <dd>{@code k = round((m / n) * log(2))}</dd>
 *  </dl>

 * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter calculator</a>
 * @see <a href="https://en.wikipedia.org/wiki/Bloom_filter">Bloom filter [Wikipedia]</a>
 *
 * @since 4.5
 */
public final class BloomFilterConfiguration {

    /**
     * The natural logarithm of 2. Used in several calculations. approx 0.693147180
     */
    private static final double LOG_OF_2 = Math.log(2.0);

    /**
     * 1 / 2^log(2) approx −0.090619058. Used in calculating the number of bits.
     */
    private static final double DENOMINATOR = Math.log(1.0 / (Math.pow(2.0, LOG_OF_2)));
    /**
     * number of items in the filter. (AKA: {@code n})
     */
    private final int numberOfItems;
    /**
     * probability of false positives. (AKA: {@code p})
     */
    private final double probability;
    /**
     * number of bits in the filter.  (AKA: {@code m})
     */
    private final int numberOfBits;
    /**
     * number of hash functions. (AKA: {@code k})
     */
    private final int numberOfHashFunctions;

    /**
     * The hash code for this filter.
     */
    private final int hashCode;

    /**
     * Create a filter configuration with the specified number of items and probability.
     * <p>
     * The actual probability will be approximately equal to the desired probability but will
     * be dependent upon the caluclated bloom filter size and function count.
     * </p>
     * @param numberOfItems Number of items to be placed in the filter.
     * @param probability The desired probability of duplicates. Must be in the range (0.0,1.0).
     */
    public BloomFilterConfiguration(final int numberOfItems, final double probability) {
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
        /*
         * number of bits is called "m" in most mathematical statement describing bloom
         * filters so we use it here.
         */
        final double m = Math.ceil(numberOfItems * Math.log(probability) / DENOMINATOR);
        if (m > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Resulting filter has more than " + Integer.MAX_VALUE + " bits");
        }
        this.numberOfBits = (int)m;
        numberOfHashFunctions = calculateNumberOfHashFunctions( numberOfItems, numberOfBits );
        this.probability = calculateProbability( numberOfItems, numberOfBits, numberOfHashFunctions );
        hashCode = Objects.hash(numberOfBits, numberOfHashFunctions, numberOfItems, probability);
    }

    /**
     * Create a filter configuration with the specified number of items and probability.
     *
     * @param numberOfItems Number of items to be placed in the filter.
     * @param numberOfBits The number of bits in the filter.
     */
    public BloomFilterConfiguration(final int numberOfItems, final int numberOfBits) {
        if (numberOfItems < 1) {
            throw new IllegalArgumentException("Number of Items must be greater than 0");
        }
        if (numberOfBits < 8) {
            throw new IllegalArgumentException("Number of Bits must be greater than or equal to 8");
        }

        this.numberOfItems = numberOfItems;
        this.numberOfBits = numberOfBits;
        this.numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
        this.probability = calculateProbability( numberOfItems, numberOfBits, numberOfHashFunctions );
        hashCode = Objects.hash(numberOfBits, numberOfHashFunctions, numberOfItems, probability);
    }

    /**
     * Create a filter configuration with the specified number of items and probability.
     *
     * @param numberOfItems Number of items to be placed in the filter.
     * @param numberOfBits The number of bits in the filter.
     * @param numberOfHashFunctions The number of hash functions in the filter.
     */
    public BloomFilterConfiguration(final int numberOfItems, final int numberOfBits, final int numberOfHashFunctions) {
        if (numberOfItems < 1) {
            throw new IllegalArgumentException("Number of Items must be greater than 0");
        }
        if (numberOfBits < 8) {
            throw new IllegalArgumentException("Number of Bits must be greater than or equal to 8");
        }
        if (numberOfHashFunctions < 1)
        {
            throw new IllegalArgumentException("Number of Hash Functions must be greater than or equal to 8");
        }

        this.numberOfItems = numberOfItems;
        this.numberOfBits = numberOfBits;
        this.numberOfHashFunctions = numberOfHashFunctions;
        this.probability = calculateProbability( numberOfItems, numberOfBits, numberOfHashFunctions );
        hashCode = Objects.hash(numberOfBits, numberOfHashFunctions, numberOfItems, probability);
    }

    /**
     * Create a filter configuration with the specified number of items and probability.
     *
     * @param probability The probability of duplicates. Must be in the range (0.0,1.0).
     * @param numberOfBits The number of bits in the filter.
     * @param numberOfHashFunctions The number of hash functions in the filter.
     */
    public BloomFilterConfiguration(final double probability, final int numberOfBits, final int numberOfHashFunctions) {
        if (probability <= 0.0) {
            throw new IllegalArgumentException("Probability must be greater than 0.0");
        }
        if (probability >= 1.0) {
            throw new IllegalArgumentException("Probability must be less than 1.0");
        }
        if (numberOfBits < 8) {
            throw new IllegalArgumentException("Number of bits must be greater than or equal to 8");
        }
        if (numberOfHashFunctions < 1) {
            throw new IllegalArgumentException("Number of hash functions must be greater than or equal to 8");
        }

        this.numberOfBits = numberOfBits;
        this.numberOfHashFunctions = numberOfHashFunctions;


        // n = ceil(m / (-k / log(1 - exp(log(p) / k))))
        double n = Math.ceil(numberOfBits /
            (-numberOfHashFunctions / Math.log(1 - Math.exp(Math.log(probability) / numberOfHashFunctions))));

        // log of probability is always < 0
        // number of hash functions is >= 1
        // e^x where x < 0 = [0,1)
        // log 1-e^x = [log1, log0) = <0 with an effective lower limit of -53
        // numberOfBits/ (-numberOfHashFunctions / [-53,0) ) >0
        // ceil( >0 ) >= 1
        // so we can not produce a negative value thus we don't chack for it.
        //
        // similarly we can not produce a number greater than numberOfBits so we
        // do not have to check for Integer.MAX_VALUE either.
        this.numberOfItems = (int) n;
        this.probability = calculateProbability( numberOfItems, numberOfBits, numberOfHashFunctions );
        hashCode = Objects.hash(numberOfBits, numberOfHashFunctions, numberOfItems, probability);
    }


    /**
     * Calculates the number of hash functions given numberOfItems and  numberofBits.
     * This is a method so that the calculation is consistent across all constructors.
     *
     * @param numberOfItems the number of items in the filter.
     * @param numberOfBits the number of bits in the filter.
     * @return the optimal number of hash functions.
     */
    private final int calculateNumberOfHashFunctions(int numberOfItems, int numberOfBits)
    {
        /*
         * k = round((m / n) * log(2)) We change order so that we use real math rather
         * than integer math.
         */
        long k = Math.round(LOG_OF_2 * numberOfBits / numberOfItems);
        if (k < 1) {
            throw new IllegalArgumentException(
                String.format("Filter to small: Calculated number of hash functions (%s) was less than 1", k));
        }
        /*
         * normally we would check that numberofHashFunctions <= Integer.MAX_VALUE but since numberOfBits
         * is at most Integer.MAX_VALUE the numerator of numberofHashFunctions is log(2) * Integer.MAX_VALUE
         * = 646456992.9449 the value of k can not be above Integer.MAX_VALUE.
         */
        return (int) k;
    }

    /**
     * Calculates the probability given numberOfItems, numberofBits and numberOfHashFunctions.
     * This is a method so that the calculation is consistent across all constructors.
     *
     * @param numberOfItems the number of items in the filter.
     * @param numberOfBits the number of bits in the filter.
     * @param numberOfHashFunctions the number of hash functions used to create the filter.
     * @return the probability of collision.
     */
    private final double calculateProbability(int numberOfItems, int numberOfBits, int numberOfHashFunctions) {
        // (1 - exp(-kn/m))^k
        double p = Math.pow(1.0 - Math.exp(-1.0 * numberOfHashFunctions * numberOfItems / numberOfBits),
            numberOfHashFunctions);
        /*
         * We do not need to check for p < = since we only allow positive values for parameters
         * and the closest we can come to exp(-kn/m) == 1 is exp(-1/Integer.MAX_INT) approx 0.9999999995343387
         * so Math.pow( x, y ) will always be 0<x<1 and y>0
         */
        if (p >= 1.0) {
            throw new IllegalArgumentException(
                String.format("Calculated probability (%s) is greater than or equal to 1.0", p));
        }
        return p;
    }

    /**
     * Gets the number of items that are expected in the filter. AKA: {@code n}
     *
     * @return the number of items.
     */

    public int getNumberOfItems() {
        return numberOfItems;
    }

    /**
     * Gets the probability of a false positive (collision). AKA: {@code p}
     *
     * @return the probability of a false positive.
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Gets the number of bits in the Bloom filter. AKA: {@code m}
     *
     * @return the number of bits in the Bloom filter.
     */
    public int getNumberOfBits() {
        return numberOfBits;
    }

    /**
     * Gets the number of hash functions used to construct the filter. AKA: {@code k}
     *
     * @return the number of hash functions used to construct the filter.
     */
    public int getNumberOfHashFunctions() {
        return numberOfHashFunctions;
    }

    /**
     * Gets the number of bytes in the Bloom filter.
     *
     * @return the number of bytes in the Bloom filter.
     */
    public int getNumberOfBytes() {
        return Double.valueOf(Math.ceil(numberOfBits / 8.0)).intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BloomFilterConfiguration) {
            BloomFilterConfiguration other = (BloomFilterConfiguration) o;
            return other.getNumberOfBits() == getNumberOfBits() &&
                other.getNumberOfHashFunctions() == getNumberOfHashFunctions() &&
                other.getNumberOfItems() == getNumberOfItems() && other.getProbability() == getProbability();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Estimates the number of items in a Bloom filter based on this configuration and the
     * number of bits that are enabled.
     *
     * @param filter the filter to check.
     * @return and estimate of the number of items that were placed in the Bloom filter.
     */
    public long estimateSize(AbstractBloomFilter filter) {
        double estimate = -(getNumberOfBits() * 1.0 / getNumberOfHashFunctions()) *
            Math.log(1.0 - (filter.getHammingWeight() * 1.0 / getNumberOfBits()));
        return Math.round( estimate );
    }

    /**
     * Estimates the number of items in the union of the sets fronted by two Bloom
     * filters.
     *
     * @param filter1 the first Bloom filter.
     * @param filter2 the second Bloom filter.
     * @return an estimate of the size of the union between the two filters.
     */
    public long estimateUnionSize(AbstractBloomFilter filter1, AbstractBloomFilter filter2) {
        BitSet union = filter1.getBitSet();
        union.or(filter2.getBitSet());

        double estimate = -(getNumberOfBits() * 1.0 / getNumberOfHashFunctions()) *
            Math.log(1 - union.cardinality() * 1.0 / getNumberOfBits());
        return Math.round( estimate );
    }

    /**
     * Estimates the number of items in the intersection of the sets fronted by two Bloom
     * filters.
     *
     * @param filter1 the first Bloom filter.
     * @param filter2 the second Bloom filter.
     * @return an estimate of the size of the intersection between the two filters.
     */
    public long estimateIntersectionSize(AbstractBloomFilter filter1, AbstractBloomFilter filter2) {
        // do subtraction early to avoid Long overflow.
        return estimateSize(filter1) - estimateUnionSize(filter1, filter2) + estimateSize(filter2);
    }

    /**
     * Determines if the bloom filter is "full".
     * Full is definded as haveing no unset bits.
     * @param filter the filter to check.
     * @return true if the filter is full.
     */
    public boolean isFull(AbstractBloomFilter filter) {
        return filter.getHammingWeight() == numberOfBits;
    }
}
