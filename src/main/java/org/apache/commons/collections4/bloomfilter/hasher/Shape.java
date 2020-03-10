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

/**
 * The definition of a Bloom filter shape.
 *
 * <p> This class contains the values for the filter configuration and is used to
 * convert a Hasher into a BloomFilter as well as verify that two Bloom filters are
 * compatible. (i.e. can be compared or merged)</p>
 *
 * <h2>Interrelatedness of values</h2>
 *
 * <dl> <dt>Number of Items (AKA: {@code n})</dt>
 * <dd>{@code n = ceil(m / (-k / log(1 - exp(log(p) / k))))}</dd> <dt>Probability of
 * Collision (AKA: {@code p})</dt> <dd>{@code p = pow(1 - exp(-k / (m / n)), k)}</dd> <dt>Number
 * of Bits (AKA: {@code m})</dt>
 * <dd>{@code m = ceil((n * log(p)) / log(1 / pow(2, log(2))))}</dd> <dt>Number of
 * Functions (AKA: {@code k})</dt> <dd>{@code k = round((m / n) * log(2))}</dd> </dl>
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
     * The natural logarithm of 2. Used in several calculations. Approximately 0.693147180559945.
     */
    private static final double LOG_OF_2 = Math.log(2.0);

    /**
     * log(1 / 2^log(2)). Used in calculating the number of bits. Approximately -0.480453013918201.
     *
     * <p>log(1 / 2^log(2)) = log(1) - log(2^log(2)) = -log(2) * log(2)
     */
    private static final double DENOMINATOR = -LOG_OF_2 * LOG_OF_2;

    /**
     * Number of items in the filter. AKA: {@code n}.
     */
    private final int numberOfItems;

    /**
     * Number of bits in the filter. AKA: {@code m}.
     */
    private final int numberOfBits;

    /**
     * Number of hash functions. AKA: {@code k}.
     */
    private final int numberOfHashFunctions;

    /**
     * The hash code for this filter.
     */
    private final int hashCode;

    /**
     * The identity of the hasher function.
     */
    private final HashFunctionIdentity hashFunctionIdentity;

    /**
     * Constructs a filter configuration with a desired false-positive probability ({@code p}) and the
     * specified number of bits ({@code m}) and hash functions ({@code k}).
     *
     * <p>The number of items ({@code n}) to be stored in the filter is computed.
     * <pre>n = ceil(m / (-k / log(1 - exp(log(p) / k))))</pre>
     *
     * <p>The actual probability will be approximately equal to the
     * desired probability but will be dependent upon the calculated Bloom filter capacity
     * (number of items). An exception is raised if this is greater than or equal to 1 (i.e. the
     * shape is invalid for use as a Bloom filter).
     *
     * @param hashFunctionIdentity The identity of the hash function this shape uses
     * @param probability The desired false-positive probability in the range {@code (0, 1)}
     * @param numberOfBits The number of bits in the filter
     * @param numberOfHashFunctions The number of hash functions in the filter
     * @throws NullPointerException if the hash function identity is null
     * @throws IllegalArgumentException if the desired probability is not in the range {@code (0, 1)}
     * @throws IllegalArgumentException if the number of bits is not above 8
     * @throws IllegalArgumentException if the number of hash functions is not strictly positive
     * @throws IllegalArgumentException if the calculated probability is not below 1
     * @see #getProbability()
     */
    public Shape(final HashFunctionIdentity hashFunctionIdentity, final double probability, final int numberOfBits,
        final int numberOfHashFunctions) {
        this.hashFunctionIdentity = Objects.requireNonNull(hashFunctionIdentity, "hashFunctionIdentity");
        checkProbability(probability);
        this.numberOfBits = checkNumberOfBits(numberOfBits);
        this.numberOfHashFunctions = checkNumberOfHashFunctions(numberOfHashFunctions);

        // Number of items (n):
        // n = ceil(m / (-k / log(1 - exp(log(p) / k))))
        final double n = Math.ceil(numberOfBits /
            (-numberOfHashFunctions / Math.log(1 - Math.exp(Math.log(probability) / numberOfHashFunctions))));

        // log of probability is always < 0
        // number of hash functions is >= 1
        // e^x where x < 0 = [0,1)
        // log 1-e^x = [log1, log0) = <0 with an effective lower limit of -53
        // numberOfBits/ (-numberOfHashFunctions / [-53,0) ) >0
        // ceil( >0 ) >= 1
        // so we can not produce a negative value thus we don't check for it.
        //
        // similarly we can not produce a number greater than numberOfBits so we
        // do not have to check for Integer.MAX_VALUE either.
        this.numberOfItems = (int) n;
        // check that probability is within range
        checkCalculatedProbability(getProbability());
        this.hashCode = generateHashCode();
    }

    /**
     * Constructs a filter configuration with the specified number of items ({@code n}) and
     * desired false-positive probability ({@code p}).
     *
     * <p>The number of bits ({@code m}) for the filter is computed.
     * <pre>m = ceil(n * log(p) / log(1 / 2^log(2)))</pre>
     *
     * <p>The optimal number of hash functions ({@code k}) is computed.
     * <pre>k = round((m / n) * log(2))</pre>
     *
     * <p>The actual probability will be approximately equal to the
     * desired probability but will be dependent upon the calculated number of bits and hash
     * functions. An exception is raised if this is greater than or equal to 1 (i.e. the
     * shape is invalid for use as a Bloom filter).
     *
     * @param hashFunctionIdentity The identity of the hash function this shape uses
     * @param numberOfItems Number of items to be placed in the filter
     * @param probability The desired false-positive probability in the range {@code (0, 1)}
     * @throws NullPointerException if the hash function identity is null
     * @throws IllegalArgumentException if the number of items is not strictly positive
     * @throws IllegalArgumentException if the desired probability is not in the range {@code (0, 1)}
     * @throws IllegalArgumentException if the calculated probability is not below 1
     * @see #getProbability()
     */
    public Shape(final HashFunctionIdentity hashFunctionIdentity, final int numberOfItems, final double probability) {
        this.hashFunctionIdentity = Objects.requireNonNull(hashFunctionIdentity, "hashFunctionIdentity");
        this.numberOfItems = checkNumberOfItems(numberOfItems);
        checkProbability(probability);

        // Number of bits (m)
        final double m = Math.ceil(numberOfItems * Math.log(probability) / DENOMINATOR);
        if (m > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Resulting filter has more than " + Integer.MAX_VALUE + " bits: " + m);
        }
        this.numberOfBits = (int) m;

        this.numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
        // check that probability is within range
        checkCalculatedProbability(getProbability());
        this.hashCode = generateHashCode();
    }

    /**
     * Constructs a filter configuration with the specified number of items ({@code n}) and
     * bits ({@code m}).
     *
     * <p>The optimal number of hash functions ({@code k}) is computed.
     * <pre>k = round((m / n) * log(2))</pre>
     *
     * <p>The false-positive probability is computed using the number of items, bits and hash
     * functions. An exception is raised if this is greater than or equal to 1 (i.e. the
     * shape is invalid for use as a Bloom filter).
     *
     * @param hashFunctionIdentity The identity of the hash function this shape uses
     * @param numberOfItems Number of items to be placed in the filter
     * @param numberOfBits The number of bits in the filter
     * @throws NullPointerException if the hash function identity is null
     * @throws IllegalArgumentException if the number of items is not strictly positive
     * @throws IllegalArgumentException if the number of bits is not above 8
     * @throws IllegalArgumentException if the calculated number of hash function is below 1
     * @throws IllegalArgumentException if the calculated probability is not below 1
     * @see #getProbability()
     */
    public Shape(final HashFunctionIdentity hashFunctionIdentity, final int numberOfItems, final int numberOfBits) {
        this.hashFunctionIdentity = Objects.requireNonNull(hashFunctionIdentity, "hashFunctionIdentity");
        this.numberOfItems = checkNumberOfItems(numberOfItems);
        this.numberOfBits = checkNumberOfBits(numberOfBits);
        this.numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
        // check that probability is within range
        checkCalculatedProbability(getProbability());
        this.hashCode = generateHashCode();
    }

    /**
     * Constructs a filter configuration with the specified number of items, bits
     * and hash functions.
     *
     * <p>The false-positive probability is computed using the number of items, bits and hash
     * functions. An exception is raised if this is greater than or equal to 1 (i.e. the
     * shape is invalid for use as a Bloom filter).
     *
     * @param hashFunctionIdentity The identity of the hash function this shape uses
     * @param numberOfItems Number of items to be placed in the filter
     * @param numberOfBits The number of bits in the filter.
     * @param numberOfHashFunctions The number of hash functions in the filter
     * @throws NullPointerException if the hash function identity is null
     * @throws IllegalArgumentException if the number of items is not strictly positive
     * @throws IllegalArgumentException if the number of bits is not above 8
     * @throws IllegalArgumentException if the number of hash functions is not strictly positive
     * @throws IllegalArgumentException if the calculated probability is not below 1
     * @see #getProbability()
     */
    public Shape(final HashFunctionIdentity hashFunctionIdentity, final int numberOfItems, final int numberOfBits,
        final int numberOfHashFunctions) {
        this.hashFunctionIdentity = Objects.requireNonNull(hashFunctionIdentity, "hashFunctionIdentity");
        this.numberOfItems = checkNumberOfItems(numberOfItems);
        this.numberOfBits = checkNumberOfBits(numberOfBits);
        this.numberOfHashFunctions = checkNumberOfHashFunctions(numberOfHashFunctions);
        // check that probability is within range
        checkCalculatedProbability(getProbability());
        this.hashCode = generateHashCode();
    }

    /**
     * Check number of items is strictly positive.
     *
     * @param numberOfItems the number of items
     * @return the number of items
     * @throws IllegalArgumentException if the number of items is not strictly positive
     */
    private static int checkNumberOfItems(final int numberOfItems) {
        if (numberOfItems < 1) {
            throw new IllegalArgumentException("Number of items must be greater than 0: " + numberOfItems);
        }
        return numberOfItems;
    }

    /**
     * Check number of bits is above 8.
     *
     * @param numberOfBits the number of bits
     * @return the number of bits
     * @throws IllegalArgumentException if the number of bits is not above 8
     */
    private static int checkNumberOfBits(final int numberOfBits) {
        if (numberOfBits < 8) {
            throw new IllegalArgumentException("Number of bits must be greater than or equal to 8: " + numberOfBits);
        }
        return numberOfBits;
    }

    /**
     * Check number of hash functions is strictly positive
     *
     * @param numberOfHashFunctions the number of hash functions
     * @return the number of hash functions
     * @throws IllegalArgumentException if the number of hash functions is not strictly positive
     */
    private static int checkNumberOfHashFunctions(final int numberOfHashFunctions) {
        if (numberOfHashFunctions < 1) {
            throw new IllegalArgumentException("Number of hash functions must be greater than 0: " + numberOfHashFunctions);
        }
        return numberOfHashFunctions;
    }

    /**
     * Check the probability is in the range 0.0, exclusive, to 1.0, exclusive.
     *
     * @param probability the probability
     * @throws IllegalArgumentException if the probability is not in the range {@code (0, 1)}
     */
    private static void checkProbability(final double probability) {
        // Using the negation of within the desired range will catch NaN
        if (!(probability > 0.0 && probability < 1.0)) {
            throw new IllegalArgumentException("Probability must be greater than 0 and less than 1: " + probability);
        }
    }

    /**
     * Check the calculated probability is below 1.0.
     *
     * <p>This function is used to verify that the dynamically calculated probability for the
     * Shape is in the valid range 0 to 1 exclusive. This need only be performed once upon
     * construction.
     *
     * @param probability the probability
     * @throws IllegalArgumentException if the calculated probability is not below 1
     */
    private static void checkCalculatedProbability(final double probability) {
        // We do not need to check for p < = since we only allow positive values for
        // parameters and the closest we can come to exp(-kn/m) == 1 is
        // exp(-1/Integer.MAX_INT) approx 0.9999999995343387 so Math.pow( x, y ) will
        // always be 0<x<1 and y>0
        if (probability >= 1.0) {
            throw new IllegalArgumentException(
                String.format("Calculated probability is greater than or equal to 1: " + probability));
        }
    }

    /**
     * Calculates the number of hash functions given numberOfItems and numberofBits.
     * This is a method so that the calculation is consistent across all constructors.
     *
     * @param numberOfItems the number of items in the filter.
     * @param numberOfBits the number of bits in the filter.
     * @return the optimal number of hash functions.
     * @throws IllegalArgumentException if the calculated number of hash function is below 1
     */
    private static int calculateNumberOfHashFunctions(final int numberOfItems, final int numberOfBits) {
        // k = round((m / n) * log(2)) We change order so that we use real math rather
        // than integer math.
        final long k = Math.round(LOG_OF_2 * numberOfBits / numberOfItems);
        if (k < 1) {
            throw new IllegalArgumentException(
                String.format("Filter too small: Calculated number of hash functions (%s) was less than 1", k));
        }
        // Normally we would check that numberofHashFunctions <= Integer.MAX_VALUE but
        // since numberOfBits is at most Integer.MAX_VALUE the numerator of
        // numberofHashFunctions is log(2) * Integer.MAX_VALUE = 646456992.9449 the
        // value of k can not be above Integer.MAX_VALUE.
        return (int) k;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Shape) {
            final Shape other = (Shape) o;
            return numberOfBits == other.numberOfBits &&
                   numberOfHashFunctions == other.numberOfHashFunctions &&
                   HashFunctionValidator.areEqual(hashFunctionIdentity,
                                                  other.hashFunctionIdentity);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private int generateHashCode() {
        return Objects.hash(numberOfBits, numberOfHashFunctions, HashFunctionValidator.hash(hashFunctionIdentity));
    }

    /**
     * Gets the HashFunctionIdentity of the hash function this shape uses.
     * @return the HashFunctionIdentity of the hash function this shape uses.
     */
    public HashFunctionIdentity getHashFunctionIdentity() {
        return hashFunctionIdentity;
    }

    /**
     * Gets the number of bits in the Bloom filter. AKA: {@code m}.
     *
     * @return the number of bits in the Bloom filter.
     */
    public int getNumberOfBits() {
        return numberOfBits;
    }

    /**
     * Gets the number of bytes in the Bloom filter.
     *
     * @return the number of bytes in the Bloom filter.
     */
    public int getNumberOfBytes() {
        return Double.valueOf(Math.ceil(numberOfBits / (double) Byte.SIZE)).intValue();
    }

    /**
     * Gets the number of hash functions used to construct the filter. AKA: {@code k}.
     *
     * @return the number of hash functions used to construct the filter.
     */
    public int getNumberOfHashFunctions() {
        return numberOfHashFunctions;
    }

    /**
     * Gets the number of items that are expected in the filter. AKA: {@code n}.
     *
     * @return the number of items.
     */
    public int getNumberOfItems() {
        return numberOfItems;
    }

    /**
     * Calculates the probability of false positives ({@code p}) given
     * numberOfItems ({@code n}), numberOfBits ({@code m}) and numberOfHashFunctions ({@code k}).
     * <pre>p = pow(1 - exp(-k / (m / n)), k)</pre>
     *
     * <p>This is the probability that a Bloom filter will return true for the presence of an item
     * when it does not contain the item.
     *
     * @return the probability of collision.
     */
    public double getProbability() {
        return Math.pow(1.0 - Math.exp(-1.0 * numberOfHashFunctions * numberOfItems / numberOfBits),
            numberOfHashFunctions);
    }

    @Override
    public String toString() {
        return String.format("Shape[ %s n=%s m=%s k=%s ]",
            HashFunctionIdentity.asCommonString(hashFunctionIdentity),
            numberOfItems, numberOfBits, numberOfHashFunctions);
    }
}
