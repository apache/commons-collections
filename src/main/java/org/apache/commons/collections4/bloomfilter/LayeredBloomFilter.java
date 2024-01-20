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
 * Layered Bloom filters are described in Zhiwang, Cen; Jungang, Xu; Jian, Sun
 * (2010), "A multi-layer Bloom filter for duplicated URL detection", Proc. 3rd
 * International Conference on Advanced Computer Theory and Engineering (ICACTE
 * 2010), vol. 1, pp. V1-586-V1-591, doi:10.1109/ICACTE.2010.5578947, ISBN
 * 978-1-4244-6539-2, S2CID 3108985
 * <p>
 * In short, Layered Bloom filter contains several bloom filters arranged in
 * layers.
 * </p>
 * <ul>
 * <li>When membership in the filter is checked each layer in turn is checked
 * and if a match is found {@code true} is returned.</li>
 * <li>When merging each bloom filter is merged into the newest filter in the
 * list of layers.</li>
 * <li>When questions of cardinality are asked the cardinality of the union of
 * the enclosed Bloom filters is used.</li>
 * </ul>
 * <p>
 * The net result is that the layered Bloom filter can be populated with more
 * items than the Shape would indicate and yet still return a false positive
 * rate in line with the Shape and not the over population.
 * </p>
 * <p>
 * This implementation uses a LayerManager to handle the manipulation of the
 * layers.
 * </p>
 * <ul>
 * <li>Level 0 is the oldest layer and the highest level is the newest.</li>
 * <li>There is always at least one enclosed filter.</li>
 * <li>The newest filter is the {@code target} into which merges are performed.
 * <li>Whenever the target is retrieved, or a {@code merge} operation is
 * performed the code checks if any older layers should be removed, and if so
 * removes them. It also checks it a new layer should be added, and if so adds
 * it and sets the {@code target} before the operation.</li>
 * </ul>
 * @since 4.5
 */
public class LayeredBloomFilter implements BloomFilter, BloomFilterProducer {
    /**
     * A class used to locate matching filters across all the layers.
     */
    private class Finder implements Predicate<BloomFilter> {
        int[] result = new int[layerManager.getDepth()];
        int bfIdx;
        int resultIdx;
        BloomFilter bf;

        Finder(BloomFilter bf) {
            this.bf = bf;
        }

        int[] getResult() {
            return Arrays.copyOf(result, resultIdx);
        }

        @Override
        public boolean test(BloomFilter x) {
            if (x.contains(bf)) {
                result[resultIdx++] = bfIdx;
            }
            bfIdx++;
            return true;
        }
    }
    /**
     * Creates a fixed size layered bloom filter that adds new filters to the list,
     * but never merges them. List will never exceed maxDepth. As additional filters
     * are added earlier filters are removed.
     *
     * @param shape    The shape for the enclosed Bloom filters.
     * @param maxDepth The maximum depth of layers.
     * @return An empty layered Bloom filter of the specified shape and depth.
     */
    public static LayeredBloomFilter fixed(final Shape shape, int maxDepth) {
        LayerManager manager = LayerManager.builder().setExtendCheck(LayerManager.ExtendCheck.advanceOnPopulated())
                .setCleanup(LayerManager.Cleanup.onMaxSize(maxDepth)).setSupplier(() -> new SimpleBloomFilter(shape)).build();
        return new LayeredBloomFilter(shape, manager);
    }

    private final Shape shape;

    private LayerManager layerManager;

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
    public int cardinality() {
        return SetOperations.cardinality(this);
    }

    @Override
    public int characteristics() {
        return 0;
    }

    @Override
    public final void clear() {
        layerManager.clear();
    }

    @Override
    public boolean contains(final BitMapProducer bitMapProducer) {
        return contains(createFilter(bitMapProducer));
    }

    /**
     * Returns {@code true} if this any layer contained by this filter contains the
     * specified filter.
     * <p>
     * If the {@code other} is a BloomFilterProducer each filter within the
     * {@code other} is checked to see if it exits within this filter.
     * </p>
     *
     * @param other the other Bloom filter
     * @return {@code true} if this filter contains the other filter.
     */
    @Override
    public boolean contains(final BloomFilter other) {
        return other instanceof BloomFilterProducer ? contains((BloomFilterProducer) other)
                : !forEachBloomFilter(x -> !x.contains(other));
    }

    /**
     * Returns {@code true} if each filter within the {@code producer} exits within
     * this filter.
     *
     * @param producer the BloomFilterProducer that provides the filters to check
     *                 for.
     * @return {@code true} if this filter contains all of the filters contained in
     *         the {@code producer}.
     */
    public boolean contains(final BloomFilterProducer producer) {
        boolean[] result = { true };
        // return false when we have found a match to short circuit checks
        return producer.forEachBloomFilter(x -> {
            result[0] &= contains(x);
            return result[0];
        });
    }

    @Override
    public boolean contains(final Hasher hasher) {
        return contains(createFilter(hasher));
    }

    @Override
    public boolean contains(IndexProducer indexProducer) {
        return contains(createFilter(indexProducer));
    }

    @Override
    public LayeredBloomFilter copy() {
        return new LayeredBloomFilter(shape, layerManager.copy());
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

    @Override
    public int estimateN() {
        return flatten().estimateN();
    }

    @Override
    public int estimateUnion(final BloomFilter other) {
        Objects.requireNonNull(other, "other");
        final BloomFilter cpy = this.flatten();
        cpy.merge(other);
        return cpy.estimateN();
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
     * Create a standard (non-layered) Bloom filter by merging all of the layers. If
     * the filter is empty this method will return an empty Bloom filter.
     *
     * @return the merged bloom filter.
     */
    @Override
    public BloomFilter flatten() {
        BloomFilter bf = new SimpleBloomFilter(shape);
        forEachBloomFilter(bf::merge);
        return bf;
    }

    @Override
    public boolean forEachBitMap(LongPredicate predicate) {
        return flatten().forEachBitMap(predicate);
    }

    /**
     * Processes the Bloom filters in depth order with the most recent filters
     * first. Each filter is passed to the predicate in turn. The function exits on
     * the first {@code false} returned by the predicate.
     *
     * @param bloomFilterPredicate the predicate to execute.
     * @return {@code true} if all filters passed the predicate, {@code false}
     *         otherwise.
     */
    @Override
    public final boolean forEachBloomFilter(Predicate<BloomFilter> bloomFilterPredicate) {
        return layerManager.forEachBloomFilter(bloomFilterPredicate);
    }

    @Override
    public boolean forEachIndex(IntPredicate predicate) {
        return forEachBloomFilter(bf -> bf.forEachIndex(predicate));
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

    /**
     * Gets the depth of the deepest layer. The minimum value returned by this
     * method is 1.
     *
     * @return the depth of the deepest layer.
     */
    public final int getDepth() {
        return layerManager.getDepth();
    }

    @Override
    public final Shape getShape() {
        return shape;
    }

    @Override
    public boolean isEmpty() {
        return forEachBloomFilter(BloomFilter::isEmpty);
    }

    @Override
    public boolean merge(BitMapProducer bitMapProducer) {
        return layerManager.getTarget().merge(bitMapProducer);
    }

    @Override
    public boolean merge(BloomFilter bf) {
        return layerManager.getTarget().merge(bf);
    }

    @Override
    public boolean merge(IndexProducer indexProducer) {
        return layerManager.getTarget().merge(indexProducer);
    }

    /**
     * Forces and advance to the next layer. Executes the same logic as when
     * LayerManager.extendCheck returns {@code true}
     *
     * @see LayerManager
     */
    public void next() {
        layerManager.next();
    }
}
