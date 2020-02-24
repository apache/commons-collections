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

import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

/**
 * A counting Bloom filter.
 * This Bloom filter maintains a count of the number of times a bit has been
 * turned on. This allows for removal of Bloom filters from the filter.
 * <p>
 * This implementation uses a map to track enabled bit counts
 * </p>
 *
 * @since 4.5
 */
public class CountingBloomFilter extends AbstractBloomFilter {

    /**
     * The count of entries. Each enabled bit is a key with the count for that bit
     * being the value.  Entries with a value of zero are removed.
     */
    private final TreeMap<Integer, Integer> counts;

    /**
     * Constructs a counting Bloom filter from a hasher and a shape.
     *
     * @param hasher The hasher to build the filter from.
     * @param shape  The shape of the resulting filter.
     */
    public CountingBloomFilter(final Hasher hasher, final Shape shape) {
        super(shape);
        verifyHasher(hasher);
        counts = new TreeMap<>();
        // Eliminate duplicates and only set each bit once.
        hasher.getBits(shape).forEachRemaining((Consumer<Integer>) idx -> {
            if (!counts.containsKey(idx)) {
                counts.put(idx, 1);
            }
        });
    }

    /**
     * Constructs a counting Bloom filter with the provided counts and shape
     *
     * @param counts A map of data counts.
     * @param shape  The shape of the resulting filter.
     */
    public CountingBloomFilter(final Map<Integer, Integer> counts, final Shape shape) {
        this(shape);
        counts.entrySet().stream().forEach(e -> {
            if (e.getKey() >= shape.getNumberOfBits()) {
                throw new IllegalArgumentException(
                    "dataMap has an item with an index larger than " + (shape.getNumberOfBits() - 1));
            } else if (e.getKey() < 0) {
                throw new IllegalArgumentException("dataMap has an item with an index less than 0");
            }
            if (e.getValue() < 0) {
                throw new IllegalArgumentException("dataMap has an item with an value less than 0");
            } else if (e.getValue() > 0) {
                this.counts.put(e.getKey(), e.getValue());
            }
        });
    }

    /**
     * Constructs an empty Counting filter with the specified shape.
     *
     * @param shape  The shape of the resulting filter.
     */
    public CountingBloomFilter(final Shape shape) {
        super(shape);
        this.counts = new TreeMap<>();
    }

    @Override
    public int andCardinality(final BloomFilter other) {
        if (other instanceof CountingBloomFilter) {
            final Set<Integer> result = new HashSet<>(counts.keySet());
            result.retainAll(((CountingBloomFilter) other).counts.keySet());
            return result.size();
        }
        return super.andCardinality(other);
    }

    @Override
    public int cardinality() {
        return counts.size();
    }

    @Override
    public boolean contains(final Hasher hasher) {
        verifyHasher(hasher);
        final OfInt iter = hasher.getBits(getShape());
        while (iter.hasNext()) {
            if (counts.get(iter.nextInt()) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long[] getBits() {
        final BitSet bs = new BitSet();
        counts.keySet().stream().forEach(bs::set);
        return bs.toLongArray();
    }

    /**
     * Gets the count for each enabled bit.
     *
     * @return an immutable map of enabled bits (key) to counts for that bit
     *         (value).
     */
    public Stream<Map.Entry<Integer, Integer>> getCounts() {
        return counts.entrySet().stream()
            .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
    }

    @Override
    public StaticHasher getHasher() {
        return new StaticHasher(counts.keySet().iterator(), getShape());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: If the other filter is also a CountingBloomFilter the counts are not used.
     * </p>
     *
     * @throws IllegalStateException If the counts overflow {@link Integer#MAX_VALUE}
     */
    @Override
    public void merge(final BloomFilter other) {
        verifyShape(other);
        if (other instanceof CountingBloomFilter) {
            // Only use the keys and not the counts
            ((CountingBloomFilter) other).counts.keySet().forEach(this::addIndex);
        } else {
            BitSet.valueOf(other.getBits()).stream().forEach(this::addIndex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException If the counts overflow {@link Integer#MAX_VALUE}
     */
    @Override
    public void merge(final Hasher hasher) {
        verifyHasher(hasher);
        toStream(hasher).forEach(this::addIndex);
    }

    /**
     * Increments the count for the bit index.
     *
     * @param idx the index.
     * @throws IllegalStateException If the counts overflow {@link Integer#MAX_VALUE}
     */
    private void addIndex(int idx) {
        final Integer val = counts.get(idx);
        if (val == null) {
            counts.put(idx, 1);
        } else if (val == Integer.MAX_VALUE) {
            throw new IllegalStateException("Overflow on index " + idx);
        } else {
            counts.put(idx, val + 1);
        }
    }

    /**
     * Removes the other Bloom filter from this one.
     * <p>
     * For each bit that is turned on in the other filter the count is decremented by 1.
     * </p>
     *
     * @param other the other filter.
     * @throws IllegalStateException If the count for a decremented bit was already at zero
     */
    public void remove(final BloomFilter other) {
        verifyShape(other);
        if (other instanceof CountingBloomFilter) {
            // Only use the keys and not the counts
            ((CountingBloomFilter) other).counts.keySet().forEach(this::subtractIndex);
        } else {
            BitSet.valueOf(other.getBits()).stream().forEach(this::subtractIndex);
        }
    }

    /**
     * Decrements the counts for the bits that are on in the hasher from this
     * Bloom filter.
     * <p>
     * For each bit that is identified by the hasher the count is decremented by 1.
     * Duplicate bits are ignored.
     * </p>
     *
     * @param hasher the hasher to generate bits.
     * @throws IllegalStateException If the count for a decremented bit was already at zero
     */
    public void remove(final Hasher hasher) {
        verifyHasher(hasher);
        toStream(hasher).forEach(this::subtractIndex);
    }

    /**
     * Decrements the count for the bit index.
     *
     * @param idx the index.
     * @throws IllegalStateException If the count for a decremented bit was already at zero
     */
    private void subtractIndex(int idx) {
        final Integer val = counts.get(idx);
        if (val != null) {
            if (val == 1) {
                counts.remove(idx);
            } else {
                counts.put(idx, val - 1);
            }
        } else {
            throw new IllegalStateException("Underflow on index " + idx);
        }
    }

    /**
     * Convert the hasher to a stream. Duplicates indices are removed.
     *
     * @param hasher the hasher
     * @return the stream
     */
    private Stream<Integer> toStream(Hasher hasher) {
        final Set<Integer> lst = new HashSet<>();
        hasher.getBits(getShape()).forEachRemaining((Consumer<Integer>) lst::add);
        return lst.stream();
    }

    @Override
    public String toString() {
        if (counts.isEmpty()) {
            return "{}";
        }
        // Allow 12 digits per entry: "(x,y) "
        // This will handle up to 6-digit indices x each with a count y of 2-digits
        // without resizing the buffer. The +1 is for the leading '{'.
        final StringBuilder sb = new StringBuilder(counts.size() * 12 + 1);
        sb.append('{');
        for (final Map.Entry<Integer, Integer> e : counts.entrySet()) {
            sb.append('(').append(e.getKey()).append(',')
                          .append(e.getValue()).append(") ");
        }
        // Replace the final space with a close bracket
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }
}
