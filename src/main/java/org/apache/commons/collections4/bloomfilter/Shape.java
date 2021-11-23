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
package org.apache.commons.collections4.bloomfilter;

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
 * <dl> <dt>Number of Items ({@code n})</dt>
 * <dd>{@code n = ceil(m / (-k / ln(1 - exp(ln(p) / k))))}</dd> <dt>Probability of
 * False Positives ({@code p})</dt> <dd>{@code p = pow(1 - exp(-k / (m / n)), k)}</dd> <dt>Number
 * of Bits ({@code m})</dt>
 * <dd>{@code m = ceil((n * ln(p)) / ln(1 / pow(2, ln(2))))}</dd> <dt>Number of
 * Functions ({@code k})</dt> <dd>{@code k = round((m / n) * ln(2))}</dd> </dl>
 *
 * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter calculator</a>
 * @see <a href="https://en.wikipedia.org/wiki/Bloom_filter">Bloom filter
 * [Wikipedia]</a>
 * @since 4.5
 */
public final class Shape implements Comparable<Shape> {

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
            throw new IllegalArgumentException(
                    "Number of hash functions must be greater than 0: " + numberOfHashFunctions);
        }
        return numberOfHashFunctions;
    }

    @Override
    public int compareTo(Shape other) {
        int i = Integer.compare(numberOfBits, other.numberOfBits);
        return i == 0 ? Integer.compare(numberOfHashFunctions, other.numberOfHashFunctions) : i;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof Shape) ? compareTo((Shape) o) == 0 : false;
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
     * when it does not contain the item.</p>
     *
     * <p>The probability assumes that the Bloom filter is filled with the expected number of
     * items. If the filter contains fewer items then the actual probability will be lower.
     * Thus, this returns the worst-case false positive probability for a filter that has not
     * exceeded its expected number of items.</p>
     *
     * @param numberOfItems the number of items hashed into the Bloom filter.
     * @return the probability of false positives.
     */
    public double getProbability(int numberOfItems) {
        if (numberOfItems < 0) {
            throw new IllegalArgumentException("Number of items must be greater than or equal to 0: " + numberOfItems);
        }
        if (numberOfItems == 0) {
            return 0;
        }
        return Math.pow(1.0 - Math.exp(-1.0 * numberOfHashFunctions * numberOfItems / numberOfBits),
                numberOfHashFunctions);
    }

    @Override
    public String toString() {
        return String.format("Shape[ m=%s k=%s ]", numberOfBits, numberOfHashFunctions);
    }

    /**
     * Estimate the number of items in a Bloom filter with this shape and the specified number of bits enabled.
     * @param hammingValue the number of enabled  bits.
     * @return An estimate of the number of items in the Bloom filter.
     */
    public double estimateN(int hammingValue) {
        double c = hammingValue;
        double m = numberOfBits;
        double k = numberOfHashFunctions;
        return -(m / k) * Math.log(1.0 - (c / m));
    }

    /**
     * The factory to assist in the creation of proper Shapes.
     *
     * In the methods of this factory the `from` names are appended with the standard variable
     * names in the order expected:
     *
     * <dl>
     * <dt>{@code N})</dt><dd>The number of items to be placed in the Bloom filter</dd>
     * <dt>{@code M})</dt><dd>The number of bits in the Bloom filter</dd>
     * <dt>{@code K})</dt><dd>The number of hash functions for each item placed in the Bloom filter</dd>
     * <dt>{@code P})</dt><dd>The probability of a collision once N items have been placed in the Bloom filter</dd>
     * </dl>
     */
    public static class Factory {

        /**
         * The natural logarithm of 2. Used in several calculations. Approximately 0.693147180559945.
         */
        private static final double LN_2 = Math.log(2.0);

        /**
         * ln(1 / 2^ln(2)). Used in calculating the number of bits. Approximately -0.480453013918201.
         *
         * <p>ln(1 / 2^ln(2)) = ln(1) - ln(2^ln(2)) = -ln(2) * ln(2)
         */
        private static final double DENOMINATOR = -LN_2 * LN_2;

        /**
         * Constructs a filter configuration with a desired false-positive probability ({@code p}) and the
         * specified number of bits ({@code m}) and hash functions ({@code k}).
         *
         * <p>The number of items ({@code n}) to be stored in the filter is computed.
         * <pre>n = ceil(m / (-k / ln(1 - exp(ln(p) / k))))</pre>
         *
         * <p>The actual probability will be approximately equal to the
         * desired probability but will be dependent upon the calculated Bloom filter capacity
         * (number of items). An exception is raised if this is greater than or equal to 1 (i.e. the
         * shape is invalid for use as a Bloom filter).
         *
         * @param probability The desired false-positive probability in the range {@code (0, 1)}
         * @param numberOfBits The number of bits in the filter
         * @param numberOfHashFunctions The number of hash functions in the filter
         * @return a valid Shape.
         * @throws IllegalArgumentException if the desired probability is not in the range {@code (0, 1)},
         * {@code numberOfBits < 1}, {@code numberOfHashFunctions < 1}, or the actual
         * probability is {@code >= 1.0}
         */
        public static Shape fromPMK(final double probability, final int numberOfBits, final int numberOfHashFunctions) {
            checkProbability(probability);
            checkNumberOfBits(numberOfBits);
            checkNumberOfHashFunctions(numberOfHashFunctions);

            // Number of items (n):
            // n = ceil(m / (-k / ln(1 - exp(ln(p) / k))))
            final double n = Math.ceil(numberOfBits
                    / (-numberOfHashFunctions / Math.log(1 - Math.exp(Math.log(probability) / numberOfHashFunctions))));

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

            Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
            // check that probability is within range
            checkCalculatedProbability(shape.getProbability((int) n));
            return shape;
        }

        /**
         * Constructs a filter configuration with the specified number of items ({@code n}) and
         * desired false-positive probability ({@code p}).
         *
         * <p>The number of bits ({@code m}) for the filter is computed.
         * <pre>m = ceil(n * ln(p) / ln(1 / 2^ln(2)))</pre>
         *
         * <p>The optimal number of hash functions ({@code k}) is computed.
         * <pre>k = round((m / n) * ln(2))</pre>
         *
         * <p>The actual probability will be approximately equal to the
         * desired probability but will be dependent upon the calculated number of bits and hash
         * functions. An exception is raised if this is greater than or equal to 1 (i.e. the
         * shape is invalid for use as a Bloom filter).
         *
         * @param numberOfItems Number of items to be placed in the filter
         * @param probability The desired false-positive probability in the range {@code (0, 1)}
         * @return a valid Shape
         * @throws IllegalArgumentException if {@code numberOfItems < 1}, if the desired probability
         * is not in the range {@code (0, 1)} or if the actual probability is {@code >= 1.0}.
         */
        public static Shape fromNP(final int numberOfItems, final double probability) {
            checkNumberOfItems(numberOfItems);
            checkProbability(probability);

            // Number of bits (m)
            final double m = Math.ceil(numberOfItems * Math.log(probability) / DENOMINATOR);
            if (m > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Resulting filter has more than " + Integer.MAX_VALUE + " bits: " + m);
            }
            int numberOfBits = (int) m;

            int numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
            Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
            // check that probability is within range
            checkCalculatedProbability(shape.getProbability(numberOfItems));
            return shape;
        }

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
         * @param numberOfItems Number of items to be placed in the filter
         * @param numberOfBits The number of bits in the filter
         * @return a valid Shape.
         * @throws IllegalArgumentException if {@code numberOfItems < 1}, {@code numberOfBits < 1},
         * the calculated number of hash function is {@code < 1}, or if the actual probability is {@code >= 1.0}
         */
        public static Shape fromNM(final int numberOfItems, final int numberOfBits) {
            checkNumberOfItems(numberOfItems);
            checkNumberOfBits(numberOfBits);
            int numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
            Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
            // check that probability is within range
            checkCalculatedProbability(shape.getProbability(numberOfItems));
            return shape;
        }

        /**
         * Constructs a filter configuration with the specified number of items, bits
         * and hash functions.
         *
         * <p>The false-positive probability is computed using the number of items, bits and hash
         * functions. An exception is raised if this is greater than or equal to 1 (i.e. the
         * shape is invalid for use as a Bloom filter).
         *
         * @param numberOfItems Number of items to be placed in the filter
         * @param numberOfBits The number of bits in the filter.
         * @param numberOfHashFunctions The number of hash functions in the filter
         * @return a valid Shape.
         * @throws IllegalArgumentException if {@code numberOfItems < 1}, {@code numberOfBits < 1},
         * {@code numberOfHashFunctions < 1}, or if the actual probability is {@code >= 1.0}.
         */
        public static Shape fromNMK(final int numberOfItems, final int numberOfBits, final int numberOfHashFunctions) {
            checkNumberOfItems(numberOfItems);
            checkNumberOfBits(numberOfBits);
            checkNumberOfHashFunctions(numberOfHashFunctions);
            // check that probability is within range
            Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
            // check that probability is within range
            checkCalculatedProbability(shape.getProbability(numberOfItems));
            return shape;
        }

        /**
         * Check number of items is strictly positive.
         *
         * @param numberOfItems the number of items
         * @return the number of items
         * @throws IllegalArgumentException if the number of items is {@code < 1}.
         */
        private static int checkNumberOfItems(final int numberOfItems) {
            if (numberOfItems < 1) {
                throw new IllegalArgumentException("Number of items must be greater than 0: " + numberOfItems);
            }
            return numberOfItems;
        }

        /**
         * Check number of bits is strictly positive.
         *
         * @param numberOfBits the number of bits
         * @return the number of bits
         * @throws IllegalArgumentException if the number of bits is {@code < 1}.
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
         * @throws IllegalArgumentException if the number of hash functions is {@code < 1}.
         */
        private static int checkNumberOfHashFunctions(final int numberOfHashFunctions) {
            if (numberOfHashFunctions < 1) {
                throw new IllegalArgumentException(
                        "Number of hash functions must be greater than 0: " + numberOfHashFunctions);
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
                throw new IllegalArgumentException(
                        "Probability must be greater than 0 and less than 1: " + probability);
            }
        }

        /**
         * Check the calculated probability is {@code < 1.0}.
         *
         * <p>This function is used to verify that the dynamically calculated probability for the
         * Shape is in the valid range 0 to 1 exclusive. This need only be performed once upon
         * construction.
         *
         * @param probability the probability
         * @throws IllegalArgumentException if the probability is {@code >= 1.0}.
         */
        private static void checkCalculatedProbability(final double probability) {
            // We do not need to check for p <= 0.0 since we only allow positive values for
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
         * @throws IllegalArgumentException if the calculated number of hash function is {@code < 1}
         */
        private static int calculateNumberOfHashFunctions(final int numberOfItems, final int numberOfBits) {
            // k = round((m / n) * ln(2)) We change order so that we use real math rather
            // than integer math.
            final long k = Math.round(LN_2 * numberOfBits / numberOfItems);
            if (k < 1) {
                throw new IllegalArgumentException(
                        String.format("Filter too small: Calculated number of hash functions (%s) was less than 1", k));
            }
            // Normally we would check that numberofHashFunctions <= Integer.MAX_VALUE but
            // since numberOfBits is at most Integer.MAX_VALUE the numerator of
            // numberofHashFunctions is ln(2) * Integer.MAX_VALUE = 646456992.9449 the
            // value of k can not be above Integer.MAX_VALUE.
            return (int) k;
        }
    }

}
