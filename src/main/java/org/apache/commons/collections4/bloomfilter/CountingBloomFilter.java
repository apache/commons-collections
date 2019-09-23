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

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A counting Bloom filter.
 *
 * <p>
 * This Bloom filter maintains a count of the number of times a bit has been
 * turned on. This allows for removal of Bloom filters from the filter.
 * </p>
 * <p>
 * Instances are immutable.
 * </p>
 *
 * @since 4.5
 *
 */
public class CountingBloomFilter extends StandardBloomFilter {

    /**
     * An empty Counting Bloom Filter.
     */
    public static final BloomFilter EMPTY = new CountingBloomFilter(new BitSet(0), Collections.emptyMap());

    /**
     *  the count of entries.  Each enabled bit is a key with the 
     *  count for that bit being the value.
     */
    private final TreeMap<Integer, Integer> counts;

    /**
     * Constructor.
     *
     * @param protoFilter the protoFilter to build this Bloom filter from.
     * @param config      the Filter configuration to use to build the Bloom filter.
     */
    public CountingBloomFilter(ProtoBloomFilter protoFilter, FilterConfiguration config) {
        super(protoFilter, config);
        int[] intArry = new int[config.getNumberOfBits()];

        counts = new TreeMap<Integer, Integer>();
        protoFilter.getHashes().map(hash -> hash.getBits(config)).flatMapToInt(x -> Arrays.stream(x))
                .forEach(x -> intArry[x]++);

        for (int i = 0; i < config.getNumberOfBits(); i++) {
            if (intArry[i] != 0) {
                counts.put(i, intArry[i]);
            }
        }
    }

    /**
     * Constructor.
     *
     * A copy of the bitSet parameter is made so that the Bloom filter is isolated
     * from any further changes in the bitSet.
     *
     * @param bitSet The bit set that was built by the config.
     * @param counts the Map of set bits to counts for that bit.
     */
    CountingBloomFilter(BitSet bits, Map<Integer, Integer> counts) {
        super(bits);
        this.counts = new TreeMap<Integer, Integer>(counts);
    }

    /**
     * Get the count for each enabled  bit.
     * @return an immutable map of enabled bits (key) to counts for that bit (value).
     */
    public Map<Integer,Integer> getCounts() {
        return Collections.unmodifiableMap( counts );
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{ ");
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            sb.append(String.format("(%s,%s) ", e.getKey(), e.getValue()));
        }
        return sb.append("}").toString();
    }

    @Override
    public int hashCode() {
        // here to keep PMD happy
        return super.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = super.equals(other);
        if (result && other instanceof CountingBloomFilter) {
            return this.counts.equals(((CountingBloomFilter) other).counts);
        }
        return result;
    }

    /**
     * Merge this Bloom filter with the other creating a new filter. The counts for
     * bits that are on in the other filter are incremented.
     * <p>
     * For each bit that is turned on in the other filter; if the other filter is
     * also a CountingBloomFilter the count is added to this filter, otherwise the
     * count is incremented by one.
     * </p>
     *
     * @param other the other filter.
     * @return a new filter.
     */
    @Override
    public CountingBloomFilter merge(BloomFilter other) {
        BitSet next = other.getBitSet();
        next.or(getBitSet());
        Map<Integer, Integer> newSet = new HashMap<Integer, Integer>(counts);
        // calculate the counts.
        other.getBitSet().stream().forEach(key -> {
            int otherCount = (other instanceof CountingBloomFilter) ? ((CountingBloomFilter) other).counts.get(key) : 1;

            Integer count = newSet.get(key);
            if (count == null) {
                newSet.put(key, otherCount);
            } else {
                if (otherCount > Integer.MAX_VALUE - count) {
                    throw new IllegalStateException("More than " + Integer.MAX_VALUE + " filters added");
                }

                newSet.put(key, count + otherCount);
            }
        });
        return new CountingBloomFilter(next, newSet);
    }

    /**
     * Decrement the counts for the bits that are on in the other BloomFilter from
     * this one.
     *
     * <p>
     * For each bit that is turned on in the other filter; if the other filter is
     * also a CountingBloomFilter the count is subtracted from this filter,
     * otherwise the count is decremented by 1.
     * </p>
     *
     * @param other the other filter.
     * @return a new filter.
     */
    public CountingBloomFilter remove(BloomFilter other) {
        BitSet next = (BitSet) this.getBitSet();
        TreeMap<Integer, Integer> newSet = new TreeMap<Integer, Integer>(counts);

        other.getBitSet().stream().forEach(key -> {
            int otherCount = (other instanceof CountingBloomFilter) ? ((CountingBloomFilter) other).counts.get(key) : 1;
            Integer count = newSet.get(key);
            if (count != null) {
                int c = count - otherCount;
                if (c == 0) {
                    next.clear(key);
                    newSet.remove(key);
                } else {
                    newSet.put(key, c);
                }
            }
        });
        return new CountingBloomFilter(next, newSet);
    }
}
