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
 * Layered Bloom filters are described in Zhiwang, Cen; Jungang, Xu; Jian, Sun (2010), "A multi-layer Bloom filter for duplicated URL detection", Proc. 3rd
 * International Conference on Advanced Computer Theory and Engineering (ICACTE 2010), vol. 1, pp. V1-586-V1-591, doi:10.1109/ICACTE.2010.5578947, ISBN
 * 978-1-4244-6539-2, S2CID 3108985
 * <p>
 * In short, Layered Bloom filter contains several bloom filters arranged in layers.
 * </p>
 * <ul>
 * <li>When membership in the filter is checked each layer in turn is checked and if a match is found {@code true} is returned.</li>
 * <li>When merging each bloom filter is merged into the newest filter in the list of layers.</li>
 * <li>When questions of cardinality are asked the cardinality of the union of the enclosed Bloom filters is used.</li>
 * </ul>
 * <p>
 * The net result is that the layered Bloom filter can be populated with more items than the Shape would indicate and yet still return a false positive rate in
 * line with the Shape and not the over population.
 * </p>
 * <p>
 * This implementation uses a LayerManager to handle the manipulation of the layers.
 * </p>
 * <ul>
 * <li>Level 0 is the oldest layer and the highest level is the newest.</li>
 * <li>There is always at least one enclosed filter.</li>
 * <li>The newest filter is the {@code target} into which merges are performed.
 * <li>Whenever the target is retrieved, or a {@code merge} operation is performed the code checks if any older layers should be removed, and if so removes
 * them. It also checks it a new layer should be added, and if so adds it and sets the {@code target} before the operation.</li>
 * </ul>
 *
 * @param <T> The type of Bloom Filter that is used for the layers.
 * @since 4.5.0-M2
 */
public class LayeredBloomFilter<T extends BloomFilter<T>> implements BloomFilter<LayeredBloomFilter<T>>, BloomFilterExtractor {

    /**
     * A class used to locate matching filters across all the layers.
     */
    private class Finder implements Predicate<BloomFilter> {
        int[] result = new int[layerManager.getDepth()];
        int bfIdx;
        int resultIdx;
        BloomFilter<?> bf;

        Finder(final BloomFilter<?> bf) {
            this.bf = bf;
        }

        int[] getResult() {
            return Arrays.copyOf(result, resultIdx);
        }

        @Override
        public boolean test(final BloomFilter x) {
            if (x.contains(bf)) {
                result[resultIdx++] = bfIdx;
            }
            bfIdx++;
            return true;
        }
    }

    private final Shape shape;

    private final LayerManager<T> layerManager;

    /**
     * Constructs a new instance.
     *
     * @param shape        the Shape of the enclosed Bloom filters
     * @param layerManager the LayerManager to manage the layers.
     */
    public LayeredBloomFilter(final Shape shape, final LayerManager<T> layerManager) {
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

    /**
     * Forces the execution of the cleanup Consumer that was provided when the associated LayerManager was built.
     *
     * @see LayerManager.Builder#setCleanup(java.util.function.Consumer)
     */
    public void cleanup() {
        layerManager.cleanup();
    }

    @Override
    public final void clear() {
        layerManager.clear();
    }

    @Override
    public boolean contains(final BitMapExtractor bitMapExtractor) {
        return contains(createFilter(bitMapExtractor));
    }

    /**
     * Returns {@code true} if this any layer contained by this filter contains the specified filter.
     * <p>
     * If the {@code other} is a BloomFilterExtractor each filter within the {@code other} is checked to see if it exits within this filter.
     * </p>
     *
     * @param other the other Bloom filter
     * @return {@code true} if this filter contains the other filter.
     */
    @Override
    public boolean contains(final BloomFilter other) {
        return other instanceof BloomFilterExtractor ? contains((BloomFilterExtractor) other) : !processBloomFilters(x -> !x.contains(other));
    }

    /**
     * Returns {@code true} if each filter within the {@code bloomFilterExtractor} exits within this filter.
     *
     * @param bloomFilterExtractor the BloomFilterExtractor that provides the filters to check for.
     * @return {@code true} if this filter contains all of the filters contained in the {@code bloomFilterExtractor}.
     */
    public boolean contains(final BloomFilterExtractor bloomFilterExtractor) {
        final boolean[] result = { true };
        // return false when we have found a match to short circuit checks
        return bloomFilterExtractor.processBloomFilters(x -> {
            result[0] &= contains(x);
            return result[0];
        });
    }

    @Override
    public boolean contains(final Hasher hasher) {
        return contains(createFilter(hasher));
    }

    @Override
    public boolean contains(final IndexExtractor indexExtractor) {
        return contains(createFilter(indexExtractor));
    }

    /**
     * Creates a new instance of this {@link LayeredBloomFilter} with the same properties as the current one.
     *
     * @return a copy of this {@link LayeredBloomFilter}.
     */
    @Override
    public LayeredBloomFilter<T> copy() {
        return new LayeredBloomFilter<>(shape, layerManager.copy());
    }

    /**
     * Creates a Bloom filter from a BitMapExtractor.
     *
     * @param bitMapExtractor the BitMapExtractor to create the filter from.
     * @return the BloomFilter.
     */
    private SimpleBloomFilter createFilter(final BitMapExtractor bitMapExtractor) {
        final SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(bitMapExtractor);
        return bf;
    }

    /**
     * Creates a Bloom filter from a Hasher.
     *
     * @param hasher the hasher to create the filter from.
     * @return the BloomFilter.
     */
    private SimpleBloomFilter createFilter(final Hasher hasher) {
        final SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(hasher);
        return bf;
    }

    /**
     * Creates a Bloom filter from an IndexExtractor.
     *
     * @param indexExtractor the IndexExtractor to create the filter from.
     * @return the BloomFilter.
     */
    private SimpleBloomFilter createFilter(final IndexExtractor indexExtractor) {
        final SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(indexExtractor);
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
     * Finds the layers in which the BitMapExtractor is found.
     *
     * @param bitMapExtractor the BitMapExtractor to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(final BitMapExtractor bitMapExtractor) {
        final SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(bitMapExtractor);
        return find(bf);
    }

    /**
     * Finds the layers in which the Bloom filter is found.
     *
     * @param bf the Bloom filter to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(final BloomFilter bf) {
        final Finder finder = new Finder(bf);
        processBloomFilters(finder);
        return finder.getResult();
    }

    /**
     * Finds the layers in which the Hasher is found.
     *
     * @param hasher the Hasher to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(final Hasher hasher) {
        final SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(hasher);
        return find(bf);
    }

    /**
     * Finds the layers in which the IndexExtractor is found.
     *
     * @param indexExtractor the Index extractor to search for.
     * @return an array of layer indices in which the Bloom filter is found.
     */
    public int[] find(final IndexExtractor indexExtractor) {
        final SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        bf.merge(indexExtractor);
        return find(bf);
    }

    /**
     * Create a standard (non-layered) Bloom filter by merging all of the layers. If the filter is empty this method will return an empty Bloom filter.
     *
     * @return the merged bloom filter.
     */
    @Override
    public SimpleBloomFilter flatten() {
        final SimpleBloomFilter bf = new SimpleBloomFilter(shape);
        processBloomFilters(bf::merge);
        return bf;
    }

    /**
     * Gets the Bloom filter at the specified depth
     *
     * @param depth the depth of the filter to return.
     * @return the Bloom filter at the specified depth.
     * @throws NoSuchElementException if depth is not in the range [0,getDepth())
     */
    public T get(final int depth) {
        return layerManager.get(depth);
    }

    /**
     * Gets the depth of the deepest layer. The minimum value returned by this method is 1.
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
        return processBloomFilters(BloomFilter::isEmpty);
    }

    @Override
    public boolean merge(final BitMapExtractor bitMapExtractor) {
        return layerManager.getTarget().merge(bitMapExtractor);
    }

    @Override
    public boolean merge(final BloomFilter bf) {
        return layerManager.getTarget().merge(bf);
    }

    @Override
    public boolean merge(final IndexExtractor indexExtractor) {
        return layerManager.getTarget().merge(indexExtractor);
    }

    /**
     * Forces and advance to the next layer. This method will clean-up the current layers and generate a new filter layer. In most cases is it unnecessary to
     * call this method directly.
     *
     * @see LayerManager.Builder#setCleanup(java.util.function.Consumer)
     * @see LayerManager.Builder#setExtendCheck(Predicate)
     */
    public void next() {
        layerManager.next();
    }

    @Override
    public boolean processBitMaps(final LongPredicate predicate) {
        return flatten().processBitMaps(predicate);
    }

    /**
     * Processes the Bloom filters in depth order with the most recent filters first. Each filter is passed to the predicate in turn. The function exits on the
     * first {@code false} returned by the predicate.
     *
     * @param bloomFilterPredicate the predicate to execute.
     * @return {@code true} if all filters passed the predicate, {@code false} otherwise.
     */
    @Override
    public final boolean processBloomFilters(final Predicate<BloomFilter> bloomFilterPredicate) {
        return layerManager.processBloomFilters(bloomFilterPredicate);
    }

    @Override
    public boolean processIndices(final IntPredicate predicate) {
        return processBloomFilters(bf -> bf.processIndices(predicate));
    }

}
