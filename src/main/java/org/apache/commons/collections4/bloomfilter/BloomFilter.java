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
import java.util.PrimitiveIterator.OfInt;

import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * An abstract Bloom filter providing default implementations for most Bloom filter
 * functions. Specific implementations are encouraged to override the methods that can be
 * more efficiently implemented.
 *
 */
public abstract class BloomFilter {

    /**
     * The shape used by this BloomFilter
     */
    private Shape shape;

    /**
     * Gets an array of little-endian long values representing the on bits of this filter.
     * bits 0-63 are in the first long.
     *
     * @return the LongBuffer representation of this filter.
     */
    public abstract long[] getBits();

    /**
     * Creates a StaticHasher that contains the indexes of the bits that are on in this
     * filter.
     *
     * @return a StaticHasher for that produces this Bloom filter.
     */
    public abstract StaticHasher getHasher();

    /**
     * Construct a Bloom filter with the specified shape.
     *
     * @param shape The shape.
     */
    protected BloomFilter(Shape shape) {
        this.shape = shape;
    }

    /**
     * Verify the other Bloom filter has the same shape as this Bloom filter.
     *
     * @param other the other filter to check.
     * @throws IllegalArgumentException if the shapes are not the same.
     */
    protected final void verifyShape(BloomFilter other) {
        verifyShape(other.getShape());
    }

    /**
     * Verify the specified shape has the same shape as this Bloom filter.
     *
     * @param shape the other shape to check.
     * @throws IllegalArgumentException if the shapes are not the same.
     */
    protected final void verifyShape(Shape shape) {
        if (!this.shape.equals(shape)) {
            throw new IllegalArgumentException(String.format("Shape %s is not the same as %s", shape, this.shape));
        }
    }

    /**
     * Verifies that the hasher has the same name as the shape.
     *
     * @param hasher the Hasher to check
     */
    protected final void verifyHasher(Hasher hasher) {
        if (!shape.getHashFunctionName().equals(hasher.getName())) {
            throw new IllegalArgumentException(
                String.format("Hasher (%s) is not the hasher for shape (%s)", hasher.getName(), shape.toString()));
        }
    }

    /**
     * Gets the shape of this filter.
     *
     * @return The shape of this filter.
     */
    public final Shape getShape() {
        return shape;
    }

    /**
     * Merge the other Bloom filter into this one.
     *
     * @param other the other Bloom filter.
     */
    abstract public void merge(BloomFilter other);

    /**
     * Merge the decomposed Bloom filter defined by the shape and hasher into this Bloom
     * filter. The Shape must match the shape of this filter. The hasher provides bit
     * indexes to enable.
     *
     * @param shape the decomposed Bloom filter.
     * @param hasher the hasher to provide the indexes.
     * @throws IllegalArgumentException if the shape argument does not match the shape of
     * this filter, or if the hasher is not the specified one
     */
    abstract public void merge(Shape shape, Hasher hasher);

    /**
     * Gets the cardinality of this Bloom filter.
     *
     * @return the cardinality (number of enabled bits) in this filter.
     */
    public int cardinality() {
        return BitSet.valueOf(getBits()).cardinality();
    }

    /**
     * Performs a logical "AND" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code ( this AND other )}.
     */
    public int andCardinality(BloomFilter other) {
        verifyShape(other);
        long[] mine = getBits();
        long[] theirs = other.getBits();
        long[] remainder = null;
        long[] result = null;
        if (mine.length > theirs.length) {
            result = new long[mine.length];
            remainder = mine;
        } else {
            result = new long[theirs.length];
            remainder = theirs;

        }
        int limit = Integer.min(mine.length, theirs.length);
        for (int i = 0; i < limit; i++) {
            result[i] = mine[i] & theirs[i];
        }
        if (limit<result.length)
        {
            System.arraycopy(remainder, limit, result, limit, result.length);
        }
        return BitSet.valueOf(result).cardinality();
    }

    /**
     * Performs a logical "OR" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code ( this OR other )}.
     */
    public int orCardinality(BloomFilter other) {
        verifyShape(other);
        long[] mine = getBits();
        long[] theirs = other.getBits();
        long[] remainder = null;
        long[] result = null;
        if (mine.length > theirs.length) {
            result = new long[mine.length];
            remainder = mine;
        } else {
            result = new long[theirs.length];
            remainder = theirs;

        }
        int limit = Integer.min(mine.length, theirs.length);
        for (int i = 0; i < limit; i++) {
            result[i] = mine[i] | theirs[i];
        }
        if (limit<result.length)
        {
            System.arraycopy(remainder, limit, result, limit, result.length);
        }
        return BitSet.valueOf(result).cardinality();
    }

    /**
     * Performs a logical "XOR" with the other Bloom filter and returns the cardinality of
     * the result.
     *
     * @param other the other Bloom filter.
     * @return the cardinality of the result of {@code( this XOR other )}
     */
    public int xorCardinality(BloomFilter other) {
        verifyShape(other);
        long[] mine = getBits();
        long[] theirs = other.getBits();
        long[] remainder = null;
        long[] result = null;
        if (mine.length > theirs.length) {
            result = new long[mine.length];
            remainder = mine;
        } else {
            result = new long[theirs.length];
            remainder = theirs;

        }
        int limit = Integer.min(mine.length, theirs.length);
        for (int i = 0; i < limit; i++) {
            result[i] = mine[i] ^ theirs[i];
        }
        if (limit<result.length)
        {
            System.arraycopy(remainder, limit, result, limit, result.length);
        }
        return BitSet.valueOf(result).cardinality();
    }

    /**
     * Performs a contains check. Effectively this AND other == other.
     *
     * @param other the Other Bloom filter.
     * @return true if this filter matches the other.
     */
    public boolean contains(BloomFilter other) {
        verifyShape(other);
        return other.cardinality() == andCardinality(other);
    }

    /**
     * Performs a contains check against a decomposed Bloom filter. The shape must match
     * the shape of this filter. The hasher provides bit indexes to check for. Effectively
     * decomposed AND this == decomposed.
     *
     * @param shape the Shape of the decomposed Bloom filter.
     * @param hasher The hasher containing the bits to check.
     * @return true if this filter contains the other.
     * @throws IllegalArgumentException if the shape argument does not match the shape of
     * this filter, or if the hasher is not the specified one
     */
    public boolean contains(Shape shape, Hasher hasher) {
        verifyShape(shape);
        if (!shape.getHashFunctionName().equals(hasher.getName())) {
            throw new IllegalArgumentException(
                String.format("Hasher (%s) is not the hasher for shape (%s)", hasher.getName(), shape.toString()));
        }
        long[] buff = getBits();

        OfInt iter = hasher.getBits(shape);
        while (iter.hasNext()) {
            int idx = iter.nextInt();
            int buffIdx = idx / Long.SIZE;
            int pwr = Math.floorMod(idx, Long.SIZE);
            long buffOffset = (long) Math.pow(2, pwr);
            if ((buff[buffIdx] & buffOffset) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the Hamming value of this Bloom filter.
     *
     * @return the hamming value.
     */
    public int hammingValue() {
        return cardinality();
    }

    /**
     * Gets the Hamming distance to the other Bloom filter.
     *
     * @param other the Other bloom filter.
     * @return the Hamming distance.
     */
    public int hammingDistance(BloomFilter other) {
        verifyShape(other);
        return xorCardinality(other);
    }

    /**
     * Gets the Jaccard similarity wih the other Bloom filter.
     *
     * @param other the Other bloom filter.
     * @return the Jaccard similarity.
     */
    public double jaccardSimilarity(BloomFilter other) {
        verifyShape(other);
        int orCard = orCardinality(other);
        if (orCard == 0) {
            return 0;
        }
        return hammingDistance(other) / (double) orCard;
    }

    /**
     * Gets the jaccard distance to the other Bloom filter.
     *
     * @param other the Other Bloom filter.
     * @return the jaccard distance.
     */
    public final double jaccardDistance(BloomFilter other) {
        return 1.0 - jaccardSimilarity(other);
    }

    /**
     * Gets the Cosine similarity wih the other Bloom filter.
     *
     * @param other the Other bloom filter.
     * @return the Cosine similarity.
     */
    public double cosineSimilarity(BloomFilter other) {
        verifyShape(other);
        return andCardinality(other) / (Math.sqrt(cardinality()) * Math.sqrt(other.cardinality()));
    }

    /**
     * Gets the jaccard distance to the other Bloom filter.
     *
     * @param other the Other Bloom filter.
     * @return the jaccard distance.
     */
    public final double cosineDistance(BloomFilter other) {
        return 1.0 - cosineSimilarity(other);
    }

    /**
     * Estimates the number of items in the Bloom filter based on the shape and the number
     * of bits that are enabled.
     *
     * @return and estimate of the number of items that were placed in the Bloom filter.
     */
    public final long estimateSize() {
        double estimate = -(getShape().getNumberOfBits() *
            Math.log(1.0 - hammingValue() * 1.0 / getShape().getNumberOfBits())) /
            getShape().getNumberOfHashFunctions();
        return Math.round(estimate);
    }

    /**
     * Estimates the number of items in the union of the sets of items that created the
     * bloom filters.
     *
     * @param other the other Bloom filter.
     * @return an estimate of the size of the union between the two filters.
     */
    public final long estimateUnionSize(BloomFilter other) {
        verifyShape(other);
        double estimate = -(getShape().getNumberOfBits() *
            Math.log(1.0 - orCardinality(other) * 1.0 / getShape().getNumberOfBits())) /
            getShape().getNumberOfHashFunctions();
        return Math.round(estimate);
    }

    /**
     * Estimates the number of items in the intersection of the sets of items that created
     * the bloom filters.
     *
     * @param other the other Bloom filter.
     * @return an estimate of the size of the intersection between the two filters.
     */
    public final long estimateIntersectionSize(BloomFilter other) {
        verifyShape(other);
        // do subtraction early to avoid Long overflow.
        return estimateSize() - estimateUnionSize(other) + other.estimateSize();
    }

    /**
     * Determines if the bloom filter is "full". Full is definded as haveing no unset
     * bits.
     *
     * @return true if the filter is full.
     */
    public final boolean isFull() {
        return hammingValue() == getShape().getNumberOfBits();
    }

    /**
     * The definition of a filter configuration. A simple Bloom filter configuration
     * implementation that derives the values from the number of items and the probability
     * of collision.
     *
     * <p> This interface defines the values for the filter configuration and is used to
     * convert a ProtoBloomFilter into a BloomFilter. </p>
     *
     * <p> This class contains the values for the filter configuration and is used to
     * convert a ProtoBloomFilter into a BloomFilter. </p>
     *
     * <h2>Interrelatedness of values</h2>
     *
     * <dl> <dt>Number of Items (AKA: {@code n})</dt>
     * <dd>{@code n = ceil(m / (-k / log(1 - exp(log(p) / k))))}</dd> <dt>Probability of
     * Collision (AKA: {@code p})</dt> <dd>{@code p =  (1 - exp(-kn/m))^k}</dd> <dt>Number
     * of Bits (AKA: {@code m})</dt>
     * <dd>{@code m = ceil((n * log(p)) / log(1 / pow(2, log(2))))}</dd> <dt>Number of
     * Functions (AKA: {@code k})</dt> <dd>{@code k = round((m / n) * log(2))}</dd> </dl>
     *
     * <h2>Comparisons</h2> <p> For purposes of equality checking and hashCode
     * calculations a {@code Shape} is defined by the hashing function name, the number of
     * bits ({@code m}), and the number of functions ({@code k}). </p>
     *
     * @see <a href="http://hur.st/bloomfilter?n=3&p=1.0E-5">Bloom Filter calculator</a>
     * @see <a href="https://en.wikipedia.org/wiki/Bloom_filter">Bloom filter
     * [Wikipedia]</a>
     */
    public static class Shape {

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
         * number of bits in the filter. (AKA: {@code m})
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
         * The name of the hasher function.
         */
        private final String hashFunctionName;

        /**
         * Create a filter configuration with the specified number of items and
         * probability. <p> The actual probability will be approximately equal to the
         * desired probability but will be dependent upon the caluclated bloom filter size
         * and function count. </p>
         *
         * @param hashFunctionName The name of the hash function this shape uses.
         * @param numberOfItems Number of items to be placed in the filter.
         * @param probability The desired probability of duplicates. Must be in the range
         * (0.0,1.0).
         */
        public Shape(String hashFunctionName, final int numberOfItems, final double probability) {
            if (hashFunctionName == null) {
                throw new IllegalArgumentException("Hash function name may not be null");
            }
            if (numberOfItems < 1) {
                throw new IllegalArgumentException("Number of Items must be greater than 0");
            }
            if (probability <= 0.0) {
                throw new IllegalArgumentException("Probability must be greater than 0.0");
            }
            if (probability >= 1.0) {
                throw new IllegalArgumentException("Probability must be less than 1.0");
            }
            this.hashFunctionName = hashFunctionName;
            this.numberOfItems = numberOfItems;
            /*
             * number of bits is called "m" in most mathematical statement describing
             * bloom filters so we use it here.
             */
            final double m = Math.ceil(numberOfItems * Math.log(probability) / DENOMINATOR);
            if (m > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Resulting filter has more than " + Integer.MAX_VALUE + " bits");
            }
            this.numberOfBits = (int) m;
            numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
            hashCode = generateHashCode();
            // check that probability is within range
            getProbability();

        }

        /**
         * Create a filter configuration with the specified number of items and
         * probability.
         *
         * @param hashFunctionName The name of the hash function this shape uses.
         * @param numberOfItems Number of items to be placed in the filter.
         * @param numberOfBits The number of bits in the filter.
         */
        public Shape(final String hashFunctionName, final int numberOfItems, final int numberOfBits) {
            if (hashFunctionName == null) {
                throw new IllegalArgumentException("Hash function name may not be null");
            }
            if (numberOfItems < 1) {
                throw new IllegalArgumentException("Number of Items must be greater than 0");
            }
            if (numberOfBits < 8) {
                throw new IllegalArgumentException("Number of Bits must be greater than or equal to 8");
            }
            this.hashFunctionName = hashFunctionName;
            this.numberOfItems = numberOfItems;
            this.numberOfBits = numberOfBits;
            this.numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
            hashCode = generateHashCode();
            // check that probability is within range
            getProbability();

        }

        /**
         * Create a filter configuration with the specified number of items and
         * probability.
         *
         * @param hashFunctionName The name of the hash function this shape uses.
         * @param numberOfItems Number of items to be placed in the filter.
         * @param numberOfBits The number of bits in the filter.
         * @param numberOfHashFunctions The number of hash functions in the filter.
         */
        public Shape(final String hashFunctionName, final int numberOfItems, final int numberOfBits,
            final int numberOfHashFunctions) {
            if (hashFunctionName == null) {
                throw new IllegalArgumentException("Hash function name may not be null");
            }
            if (numberOfItems < 1) {
                throw new IllegalArgumentException("Number of Items must be greater than 0");
            }
            if (numberOfBits < 8) {
                throw new IllegalArgumentException("Number of Bits must be greater than or equal to 8");
            }
            if (numberOfHashFunctions < 1) {
                throw new IllegalArgumentException("Number of Hash Functions must be greater than or equal to 8");
            }
            this.hashFunctionName = hashFunctionName;
            this.numberOfItems = numberOfItems;
            this.numberOfBits = numberOfBits;
            this.numberOfHashFunctions = numberOfHashFunctions;
            hashCode = generateHashCode();
            // check that probability is within range
            getProbability();

        }

        /**
         * Create a filter configuration with the specified number of items and
         * probability.
         *
         * @param hashFunctionName The name of the hash function this shape uses.
         * @param probability The probability of duplicates. Must be in the range
         * (0.0,1.0).
         * @param numberOfBits The number of bits in the filter.
         * @param numberOfHashFunctions The number of hash functions in the filter.
         */
        public Shape(final String hashFunctionName, final double probability, final int numberOfBits,
            final int numberOfHashFunctions) {
            if (hashFunctionName == null) {
                throw new IllegalArgumentException("Hash function name may not be null");
            }
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
            this.hashFunctionName = hashFunctionName;
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
            hashCode = generateHashCode();
            // check that probability is within range
            getProbability();
        }

        private int generateHashCode() {
            return Objects.hash(hashFunctionName, numberOfBits, numberOfHashFunctions);
        }

        @Override
        public String toString() {
            return String.format("Shape[ %s n=%s m=%s k=%s ]", hashFunctionName, numberOfItems, numberOfBits,
                numberOfHashFunctions);
        }

        /**
         * Calculates the number of hash functions given numberOfItems and numberofBits.
         * This is a method so that the calculation is consistent across all constructors.
         *
         * @param numberOfItems the number of items in the filter.
         * @param numberOfBits the number of bits in the filter.
         * @return the optimal number of hash functions.
         */
        private int calculateNumberOfHashFunctions(int numberOfItems, int numberOfBits) {
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
             * normally we would check that numberofHashFunctions <= Integer.MAX_VALUE but
             * since numberOfBits is at most Integer.MAX_VALUE the numerator of
             * numberofHashFunctions is log(2) * Integer.MAX_VALUE = 646456992.9449 the
             * value of k can not be above Integer.MAX_VALUE.
             */
            return (int) k;
        }

        /**
         * Calculates the probability of false positives (AKA: {@code p} given
         * numberOfItems, numberofBits and numberOfHashFunctions. This is a method so that
         * the calculation is consistent across all constructors.
         *
         * @return the probability of collision.
         */
        public final double getProbability() {
            // (1 - exp(-kn/m))^k
            double p = Math.pow(1.0 - Math.exp(-1.0 * numberOfHashFunctions * numberOfItems / numberOfBits),
                numberOfHashFunctions);
            /*
             * We do not need to check for p < = since we only allow positive values for
             * parameters and the closest we can come to exp(-kn/m) == 1 is
             * exp(-1/Integer.MAX_INT) approx 0.9999999995343387 so Math.pow( x, y ) will
             * always be 0<x<1 and y>0
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
            if (o instanceof Shape) {
                Shape other = (Shape) o;
                return other.getHashFunctionName().equals(getHashFunctionName()) &&
                    other.getNumberOfBits() == getNumberOfBits() &&
                    other.getNumberOfHashFunctions() == getNumberOfHashFunctions();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        /**
         * Gets the name of the hash function that this Shape is using.
         * @return the name of the hash function.
         */
        public String getHashFunctionName() {
            return hashFunctionName;
        }
    }

}
