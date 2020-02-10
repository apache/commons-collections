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

import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
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
     * the count of entries. Each enabled bit is a key with the count for that bit
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
        final Set<Integer> idxs = new HashSet<>();
        hasher.getBits(shape).forEachRemaining((IntConsumer) idxs::add);
        idxs.stream().forEach(idx -> counts.put(idx, 1));
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

    /**
     * Constructs a counting Bloom filter with the provided counts and shape
     *
     * @param counts A map of data counts.
     * @param shape  The shape of the resulting filter.
     */
    public CountingBloomFilter(final Map<Integer,Integer> counts, final Shape shape) {
        this(shape);
        counts.entrySet().stream().forEach( e -> {
            if (e.getKey() >= shape.getNumberOfBits())
            {
                throw new IllegalArgumentException( "dataMap has an item with an index larger than "+
                    (shape.getNumberOfBits()-1) );
            }
            else if (e.getKey() < 0)
            {
                throw new IllegalArgumentException( "dataMap has an item with an index less than 0" );
            }
            if (e.getValue() < 0) {
                throw new IllegalArgumentException( "dataMap has an item with an value less than 0" );
            } else if (e.getValue() > 0)
            {
                this.counts.put( e.getKey(), e.getValue() );
            }});
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
    public String toString() {
        final StringBuilder sb = new StringBuilder("{ ");
        for (final Map.Entry<Integer, Integer> e : counts.entrySet()) {
            sb.append(String.format("(%s,%s) ", e.getKey(), e.getValue()));
        }
        return sb.append("}").toString();
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
     */
    @Override
    public void merge(final BloomFilter other) {
        verifyShape(other);
        if (other instanceof CountingBloomFilter)
        {
            merge(((CountingBloomFilter)other).counts.keySet().iterator());
        } else {
            merge(BitSet.valueOf(other.getBits()).stream().iterator());
        }
    }

    @Override
    public void merge(final Hasher hasher) {
        verifyHasher( hasher );
        merge( hasher.getBits(getShape()) );
    }

    /**
     * Merge an iterator of set bits into this filter.
     * @param iter the iterator of bits to set.
     */
    private void merge(final Iterator<Integer> iter) {
        iter.forEachRemaining(idx -> {
            final Integer val = counts.get(idx);
            if (val == null) {
                counts.put(idx, 1 );
            } else if (val == Integer.MAX_VALUE) {
                throw new IllegalStateException( "Overflow on index "+idx);
            } else {
                counts.put( idx,  val+1 );
            }
        });
    }

    /**
     * Decrement the counts for the bits that are on in the other BloomFilter from this
     * one.
     *
     * <p>
     * For each bit that is turned on in the other filter the count is decremented by 1.
     * </p>
     *
     * @param other the other filter.
     */
    public void remove(final BloomFilter other) {
        verifyShape(other);
        if (other instanceof CountingBloomFilter)
        {
            remove(((CountingBloomFilter)other).counts.keySet().stream());
        } else {
            remove(BitSet.valueOf(other.getBits()).stream().boxed());
        }
    }

    /**
     * Decrement the counts for the bits that are on in the hasher from this
     * Bloom filter.
     *
     * <p>
     * For each bit that is turned on in the other filter the count is decremented by 1.
     * </p>
     *
     * @param hasher the hasher to generate bits.
     */
    public void remove(final Hasher hasher) {
        verifyHasher( hasher );
        final Set<Integer> lst = new HashSet<>();
        hasher.getBits(getShape()).forEachRemaining( (Consumer<Integer>)lst::add );
        remove(lst.stream());
    }

    /**
     * Decrements the counts for the bits specified in the Integer stream.
     *
     * @param idxStream The stream of bit counts to decrement.
     */
    private void remove(final Stream<Integer> idxStream) {
        idxStream.forEach(idx -> {
            final Integer val = counts.get(idx);
            if (val != null) {
                if (val - 1 == 0) {
                    counts.remove(idx);
                } else {
                    counts.put(idx, val - 1);
                }
            }
            if (val == null || val == 0) {
                throw new IllegalStateException( "Underflow on index "+idx);
            } else if (val - 1 == 0) {
                counts.remove(idx);
            } else {
                counts.put(idx, val - 1);
            }
        });
    }

    @Override
    public long[] getBits() {
        final BitSet bs = new BitSet();
        counts.keySet().stream().forEach(bs::set);
        return bs.toLongArray();
    }

    @Override
    public StaticHasher getHasher() {
        return new StaticHasher(counts.keySet().iterator(), getShape());
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
    public int cardinality() {
        return counts.size();
    }

    @Override
    public int andCardinality(final BloomFilter other) {
        if (other instanceof CountingBloomFilter) {
            final Set<Integer> result = new HashSet<>( counts.keySet());
            result.retainAll( ((CountingBloomFilter)other).counts.keySet() );
            return result.size();
        }
        return super.andCardinality(other);
    }
}
