package org.apache.commons.collections4.bloomfilters;

import java.util.BitSet;

/**
 * The interface for all BloomFilter implementations.
 * Instances of BloomFilters should be immutable.
 */
public interface BloomFilter {

    /**
     * Return true if other &amp; this == other
     *
     * This is the inverse of the match method.
     *
     * {@code X.match(Y)} is the same as {@code Y.inverseMatch(X) }
     *
     * @param other the other bloom filter to match.
     * @return true if they match.
     */
    boolean inverseMatch(BloomFilter other);

    /**
     * Return true if {@code this & other == this }.
     *
     * This is the standard bloom filter match.
     *
     * @param other the other bloom filter to match.
     * @return true if they match.
     */
    boolean match(BloomFilter other);

    /**
     * Calculate the hamming distance from this bloom filter to the other. The
     * hamming distance is defined as this xor other and is the number of bits that
     * have to be flipped to convert one filter to the other.
     *
     * @param other The other bloom filter to calculate the distance to.
     * @return the distance.
     */
    int distance(BloomFilter other);

    /**
     * Get the hamming weight for this filter.
     *
     * This is the number of bits that are on in the filter.
     *
     * @return The hamming weight.
     */
    int getHammingWeight();

    /**
     * The the log(2) of this bloom filter. This is the base 2 logarithm of this
     * bloom filter if the bits in this filter were considers the bits in an
     * unsigned integer.
     *
     * @return the base 2 logarithm
     */
    double getLog();

    /**
     * Merge this bloom filter with the other creating a new filter.
     *
     * @param other the other filter.
     * @return a new filter.
     * @throws IllegalArgumentException if other can not be merged.
     */
    BloomFilter merge(BloomFilter other);

    /**
     * Return the a copy of the bitset in in the filter.
     *
     * @return the bit set representation.
     */
    BitSet getBitSet();

}