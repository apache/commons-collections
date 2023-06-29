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

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * A bloom filter comprising multiple Bloom filters. Each enclosed filter is a
 * "level". Level 0 is the oldest filter and the highest level is the newest.
 * This class utilizes a LayerManager to handle the manipulation of the layers.
 * There is always at least one enclosed filter. The newest filter is the target
 * into which merges are performed. The {@code contains} operation checks each
 * layer in turn and will return {@code true} when the first match is found.
 */
public class LayeredBloomFilter implements BloomFilter {
    private final Shape shape;
    private LayerManager layerManager;

    /**
     * Creates a fixed size layered bloom filter that adds new filters to the list,
     * but never merges them. List will never exceed maxDepth. As additional filters
     * are added earlier filters are removed.
     */
    public static LayeredBloomFilter fixed(final Shape shape, int maxDepth) {
        return new LayeredBloomFilter(shape, new LayerManager(LayerManager.FilterSupplier.simple(shape),
                LayerManager.ExtendCheck.ADVANCE_ON_POPULATED, LayerManager.Cleanup.onMaxSize(maxDepth)));
    }

    /**
     * Constructor.
     *
     * @param shape        the Shape of the enclosed Bloom filters
     * @param layerManager the LayerManager to manage the layers.
     */
    public LayeredBloomFilter(Shape shape, LayerManager layerManager) {
        this.shape = shape;
        this.layerManager = layerManager;
    }

    @Override
    public LayeredBloomFilter copy() {
        return new LayeredBloomFilter(shape, layerManager.copy());
    }

    /**
     * Gets the depth of the deepest layer.
     *
     * @return the depth of the deepest layer.
     */
    public final int getDepth() {
        return layerManager.getDepth();
    }

    /**
     * Gets the Bloom filter at the specified depth
     *
     * @param depth the depth of the filter to return.
     * @return the Bloom filter at the specified depth.
     * @throws NoSuchElementException if depth is not in the range [0,getDepth())
     */
    public BloomFilter get(int depth) {
        return layerManager.get(depth);
    }

    @Override
    public int cardinality() {
        return SetOperations.cardinality(this);
    }

    @Override
    public final void clear() {
        layerManager.clear();
    }

    /**
     * Clears the Bloom filter (removes all set bits) at the specified level.
     *
     * @param level the level to clear.
     */
    public final void clear(int level) {
        layerManager.get(level).clear();
    }

    /**
     * Get the Bloom filter that is currently being merged into.
     *
     * @return the current Bloom filter.
     */
    public final BloomFilter target() {
        return layerManager.target();
    }

    /**
     * Processes the Bloom filters in depth order with the most recent filters
     * first. Each filter is passed to the predicate in turn. The function exits on
     * the first {@code false} returned by the predicate.
     *
     * @param bloomFilterPredicate the predicate to execute.
     * @returns {@code true} if all filters passed the predicate, {@code false}
     *          otherwise.
     */
    public final boolean forEachBloomFilter(Predicate<BloomFilter> bloomFilterPredicate) {
        return layerManager.forEachBloomFilter(bloomFilterPredicate);
    }

    /**
     * Create a standard (non-layered) Bloom filter by merging all of the layers.
     *
     * @return the merged bloom filter.
     */
    public BloomFilter flatten() {
        BloomFilter bf = new SimpleBloomFilter(shape);
        forEachBloomFilter(bf::merge);
        return bf;
    }

    /**
     * Finds the layers in which the Hasher is found.
     *
     * @param hasher the Hasher to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(final Hasher hasher) {
        SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(hasher);
        return find(bf);
    }

    /**
     * Finds the layers in which the IndexProducer is found.
     *
     * @param indexProducer the Index producer to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(final IndexProducer indexProducer) {
        SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(indexProducer);
        return find(bf);
    }

    /**
     * Finds the layers in which the BitMapProducer is found.
     *
     * @param bitMapProducer the BitMapProducer to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(final BitMapProducer bitMapProducer) {
        SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(bitMapProducer);
        return find(bf);
    }

    /**
     * Finds the layers in which the Bloom filter is found.
     *
     * @param bf the Bloom filter to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(BloomFilter bf) {
        Finder finder = new Finder(bf);
        forEachBloomFilter(finder);
        return finder.getResult();
    }

    /**
     * Returns {@code true} if this any layer contained by this filter contains the
     * specified filter.
     *
     * @param other the other Bloom filter
     * @return {@code true} if this filter contains the other filter.
     */
    @Override
    public boolean contains(final BloomFilter other) {
        if (other instanceof LayeredBloomFilter) {
            boolean[] result = { true };
            // return false when we have found a match.
            ((LayeredBloomFilter) other).forEachBloomFilter(x -> {
                result[0] &= contains(x);
                return result[0];
            });
            return result[0];
        }
        return !forEachBloomFilter(x -> !x.contains(other));
    }

    /**
     * Creates a Bloom filter from a Hasher.
     *
     * @param hasher the hasher to create the filter from.
     * @return the BloomFilter.
     */
    private BloomFilter createFilter(final Hasher hasher) {
        SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(hasher);
        return bf;
    }

    /**
     * Creates a Bloom filter from an IndexProducer.
     *
     * @param indexProducer the IndexProducer to create the filter from.
     * @return the BloomFilter.
     */
    private BloomFilter createFilter(final IndexProducer indexProducer) {
        SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(indexProducer);
        return bf;
    }

    /**
     * Creates a Bloom filter from a BitMapProducer.
     *
     * @param bitMapProducer the BitMapProducer to create the filter from.
     * @return the BloomFilter.
     */
    private BloomFilter createFilter(final BitMapProducer bitMapProducer) {
        SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(bitMapProducer);
        return bf;
    }

    @Override
    public int characteristics() {
        return 0;
    }

    @Override
    public final Shape getShape() {
        return shape;
    }

    /**
     * Returns {@code true} if this filter contains the bits specified in the
     * hasher.
     *
     * <p>
     * Specifically this returns {@code true} if this filter is enabled for all bit
     * indexes identified by the {@code hasher}. Using the bit map representations
     * this is effectively {@code (this AND hasher) == hasher}.
     * </p>
     *
     * @param hasher the hasher to provide the indexes
     * @return true if this filter is enabled for all bits specified by the hasher
     */
    @Override
    public boolean contains(final Hasher hasher) {
        return contains(createFilter(hasher));
    }

    @Override
    public boolean contains(final BitMapProducer bitMapProducer) {
        return contains(createFilter(bitMapProducer));
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        return contains(createFilter(indexProducer));
    }

    @Override
    public boolean merge(BloomFilter bf) {
        return target().merge(bf);
    }

    @Override
    public boolean merge(IndexProducer indexProducer) {
        return target().merge(indexProducer);
    }

    @Override
    public boolean merge(BitMapProducer bitMapProducer) {
        return target().merge(bitMapProducer);
    }

    @Override
    public boolean forEachIndex(IntPredicate predicate) {
        return forEachBloomFilter(bf -> bf.forEachIndex(predicate));
    }

    @Override
    public boolean forEachBitMap(LongPredicate predicate) {
        BloomFilter merged = new SimpleBloomFilter(shape);
        if (forEachBloomFilter(merged::merge) && !merged.forEachBitMap(predicate)) {
            return false;
        }
        return true;
    }

    @Override
    public int estimateN() {
        BloomFilter result = new SimpleBloomFilter(shape);
        forEachBloomFilter(result::merge);
        return result.estimateN();
    }

    @Override
    public int estimateUnion(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        final BloomFilter cpy = this.flatten();
        cpy.merge(other);
        return cpy.estimateN();
    }

    @Override
    public int estimateIntersection(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        long eThis = estimateN();
        long eOther = other.estimateN();
        if (eThis == Integer.MAX_VALUE && eOther == Integer.MAX_VALUE) {
            // if both are infinite the union is infinite and we return Integer.MAX_VALUE
            return Integer.MAX_VALUE;
        }
        long estimate;
        // if one is infinite the intersection is the other.
        if (eThis == Integer.MAX_VALUE) {
            estimate = eOther;
        } else if (eOther == Integer.MAX_VALUE) {
            estimate = eThis;
        } else {
            long eUnion = estimateUnion(other);
            if (eUnion == Integer.MAX_VALUE) {
                throw new IllegalArgumentException("The estimated N for the union of the filters is infinite");
            }
            // maximum estimate value using integer values is: 46144189292 thus
            // eThis + eOther can not overflow the long value.
            estimate = eThis + eOther - eUnion;
            estimate = estimate < 0 ? 0 : estimate;
        }
        return estimate > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) estimate;
    }

    /**
     * A class used to locate matching filters across all the layers.
     */
    private class Finder implements Predicate<BloomFilter> {
        int[] result = new int[layerManager.getDepth()];
        int bfIdx = 0;
        int resultIdx = 0;
        BloomFilter bf;

        Finder(BloomFilter bf) {
            this.bf = bf;
        }

        @Override
        public boolean test(BloomFilter x) {
            if (x.contains(bf)) {
                result[resultIdx++] = bfIdx;
            }
            bfIdx++;
            return true;
        }

        int[] getResult() {
            return Arrays.copyOf(result, resultIdx);
        }
    }
}
