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
package org.apache.commons.collections4.bloomfilter.hasher;

import java.util.Objects;

import org.apache.commons.collections4.bloomfilter.BloomFilter;

/**
 * The definition of a Bloom filter shape.
 *
 * <p> This class contains the values for the filter configuration and is used to
 * convert a Hasher into a BloomFilter as well as verify that two Bloom filters are
 * compatible. (i.e. can be compared or merged)</p>
 *
 * <h2>Interrelatedness of values</h2>
 *
 * <dl> <dt>Number of Items ({@code n})</dt>
 * <dd>{@code n = ceil(m / (-k / ln(1 - exp(ln(p) / k))))}</dd> <dt>Probability of
 * False Positives ({@code p})</dt> <dd>{@code p = pow(1 - exp(-k / (m / n)), k)}</dd> <dt>Number
 * of Bits ({@code m})</dt>
 * <dd>{@code m = ceil((n * ln(p)) / ln(1 / pow(2, ln(2))))}</dd> <dt>Number of
 * Functions ({@code k})</dt> <dd>{@code k = round((m / n) * ln(2))}</dd> </dl>
 *
 * <h2>Comparisons</h2> <p> For purposes of equality checking and hashCode
 * calculations a {@code Shape} is defined by the hashing function identity, the number of
 * bits ({@code m}), and the number of functions ({@code k}). </p>
 *
 * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter calculator</a>
 * @see <a href="https://en.wikipedia.org/wiki/Bloom_filter">Bloom filter
 * [Wikipedia]</a>
 * @since 4.5
 */
public final class Shape {

    /**
     * Number of hash functions to create a filter ({@code k}).
     */
    private final int numberOfHashFunctions;

    /**
     * Number of bits in the filter ({@code m}).
     */
    private final int numberOfBits;




    /**
     * Constructs a filter configuration with the specified number of items ({@code n}) and
     * bits ({@code m}).
     *
     * <p>The optimal number of hash functions ({@code k}) is computed.
     * <pre>k = round((m / n) * ln(2))</pre>
     *
     * <p>The false-positive probability is computed using the number of items, bits and hash
     * functions. An exception is raised if this is greater than or equal to 1 (i.e. the
     * shape is invalid for use as a Bloom filter).
     *
     * @param numberOfHashFunctions Number of hash functions to use for each item placed in the filter.
     * @param numberOfBits The number of bits in the filter
     * @throws IllegalArgumentException if {@code numberOfHashFunctions < 1} or {@code numberOfBits < 1}
     */
    public Shape(final int numberOfHashFunctions, final int numberOfBits) {
        this.numberOfBits = checkNumberOfBits(numberOfBits);
        this.numberOfHashFunctions = checkNumberOfHashFunctions(numberOfHashFunctions);
    }

    /**
     * Check number of items is strictly positive.
     *
     * @param numberOfItems the number of items
     * @return the number of items
     * @throws IllegalArgumentException if the number of items is {@code < 1}
     */
    private static void checkNumberOfItems(final int numberOfItems) {
        if (numberOfItems < 1) {
            throw new IllegalArgumentException("Number of items must be greater than 0: " + numberOfItems);
        }
    }

    /**
     * Check number of bits is strictly positive.
     *
     * @param numberOfBits the number of bits
     * @return the number of bits
     * @throws IllegalArgumentException if the number of bits is {@code < 1}
     */
    private static int checkNumberOfBits(final int numberOfBits) {
        if (numberOfBits < 1) {
            throw new IllegalArgumentException("Number of bits must be greater than 0: " + numberOfBits);
        }
        return numberOfBits;
    }

    /**
     * Check number of hash functions is strictly positive
     *
     * @param numberOfHashFunctions the number of hash functions
     * @return the number of hash functions
     * @throws IllegalArgumentException if the number of hash functions is {@code < 1}
     */
    private static int checkNumberOfHashFunctions(final int numberOfHashFunctions) {
        if (numberOfHashFunctions < 1) {
            throw new IllegalArgumentException("Number of hash functions must be greater than 0: " + numberOfHashFunctions);
        }
        return numberOfHashFunctions;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Shape) {
            final Shape other = (Shape) o;
            return numberOfBits == other.numberOfBits &&
                   numberOfHashFunctions == other.numberOfHashFunctions;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfBits, numberOfHashFunctions);
    }

    /**
     * Gets the number of bits in the Bloom filter.
     * This is also known as {@code m}.
     *
     * @return the number of bits in the Bloom filter ({@code m}).
     */
    public int getNumberOfBits() {
        return numberOfBits;
    }


    /**
     * Gets the number of hash functions used to construct the filter.
     * This is also known as {@code k}.
     *
     * @return the number of hash functions used to construct the filter ({@code k}).
     */
    public int getNumberOfHashFunctions() {
        return numberOfHashFunctions;
    }


    /**
     * Calculates the probability of false positives ({@code p}) given
     * numberOfItems ({@code n}), numberOfBits ({@code m}) and numberOfHashFunctions ({@code k}).
     * <pre>p = pow(1 - exp(-k / (m / n)), k)</pre>
     *
     * <p>This is the probability that a Bloom filter will return true for the presence of an item
     * when it does not contain the item.
     *
     * <p>The probability assumes that the Bloom filter is filled with the expected number of
     * items. If the filter contains fewer items then the actual probability will be lower.
     * Thus this returns the worst-case false positive probability for a filter that has not
     * exceeded its expected number of items.
     *
     * @param numberOfItems the number of items hashed into the Bloom filter.
     * @return the probability of false positives.
     * @see #getNumberOfItems()
     */
    public double getProbability(int numberOfItems) {
        checkNumberOfItems( numberOfItems );
        return Math.pow(1.0 - Math.exp(-1.0 * numberOfHashFunctions * numberOfItems / numberOfBits),
            numberOfHashFunctions);
    }

    @Override
    public String toString() {
        return String.format("Shape[ m=%s k=%s ]",
             numberOfBits, numberOfHashFunctions);
    }

    public double estimate_n( int hammingValue ) {
        double c = hammingValue;
        double m = numberOfBits;
        double k = numberOfHashFunctions;
        return  -(m / k) * Math.log(1.0 - (c / m));
    }
}
