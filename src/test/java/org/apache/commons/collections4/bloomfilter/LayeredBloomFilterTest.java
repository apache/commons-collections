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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.collections4.bloomfilter.LayerManager.Cleanup;
import org.apache.commons.collections4.bloomfilter.LayerManager.ExtendCheck;
import org.junit.jupiter.api.Test;

public class LayeredBloomFilterTest extends AbstractBloomFilterTest<LayeredBloomFilter<?>> {

    /**
     * A Predicate that advances after a quantum of time.
     */
    static class AdvanceOnTimeQuanta<T extends BloomFilter<T>> implements Predicate<LayerManager<TimestampedBloomFilter<T>>> {
        Duration quanta;

        AdvanceOnTimeQuanta(final Duration quanta) {
            this.quanta = quanta;
        }

        @Override
        public boolean test(final LayerManager<TimestampedBloomFilter<T>> layerManager) {
            // cannot use getTarget() as it causes recursion.
            return layerManager.last().getTimestamp().plus(quanta).isBefore(Instant.now());
        }
    }

    /**
     * A Consumer that cleans the list based on how long each filters has been in
     * the list.
     */
    static class CleanByTime<T extends TimestampedBloomFilter> implements Consumer<List<T>> {
        Duration elapsedTime;

        CleanByTime(final Duration elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        @Override
        public void accept(final List<T> t) {
            final Instant min = Instant.now().minus(elapsedTime);
            final Iterator<T> iter = t.iterator();
            while (iter.hasNext()) {
                final TimestampedBloomFilter bf = iter.next();
                if (bf.getTimestamp().isAfter(min) || bf.getTimestamp().equals(min)) {
                    return;
                }
                dbgInstrument.add(String.format("Removing old entry: T:%s (Aged: %s) \n", bf.getTimestamp(), Duration.between(bf.getTimestamp(), min)));
                iter.remove();
            }
        }
    }

    static class NumberedBloomFilter extends WrappedBloomFilter<NumberedBloomFilter, SimpleBloomFilter> {

        int value;
        int sequence;

        NumberedBloomFilter(final Shape shape, final int value, final int sequence) {
            super(new SimpleBloomFilter(shape));
            this.value = value;
            this.sequence = sequence;
        }

        @Override
        public NumberedBloomFilter copy() {
            return new NumberedBloomFilter(getShape(), value, sequence);
        }
    }

    /**
     * A Bloom filter implementation that tracks the creation time.
     */
    public static class TimestampedBloomFilter<T extends BloomFilter<T>> extends WrappedBloomFilter<TimestampedBloomFilter<T>, T> {

        private final Instant timestamp;

        TimestampedBloomFilter(final T bf) {
            this(bf, Instant.now());
        }

        TimestampedBloomFilter(final T bf, final Instant timestamp) {
            super(bf);
            this.timestamp = timestamp;
        }

        @Override
        public TimestampedBloomFilter<T> copy() {
            return new TimestampedBloomFilter<>(getWrapped().copy(), timestamp);
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }

    // ***example of instrumentation ***
    private static final List<String> dbgInstrument = new ArrayList<>();

    /**
     * Creates a LayeredBloomFilter that retains enclosed filters for
     * {@code duration} and limits the contents of each enclosed filter to a time
     * {@code quanta}. This filter uses the timestamped Bloom filter internally.
     *
     * @param shape    The shape of the Bloom filters.
     * @param duration The length of time to keep filters in the list.
     * @param quanta   The quantization factor for each filter. Individual filters
     *                 will span at most this much time.
     * @return LayeredBloomFilter with the above properties.
     */
    static LayeredBloomFilter<TimestampedBloomFilter<SimpleBloomFilter>> createTimedLayeredFilter(final Shape shape, final Duration duration, final Duration quanta) {
        final LayerManager.Builder<TimestampedBloomFilter<SimpleBloomFilter>> builder = LayerManager.builder();
        final Consumer<Deque<TimestampedBloomFilter<SimpleBloomFilter>>> cleanup = Cleanup.removeEmptyTarget().andThen(new CleanByTime(duration));
        final LayerManager<TimestampedBloomFilter<SimpleBloomFilter>> layerManager = builder
                .setSupplier(() -> new TimestampedBloomFilter<>(new SimpleBloomFilter(shape)))
                .setCleanup(cleanup)
                .setExtendCheck(new AdvanceOnTimeQuanta(quanta)
                        .or(LayerManager.ExtendCheck.advanceOnSaturation(shape.estimateMaxN())))
                .get();
        return new LayeredBloomFilter<>(shape, layerManager);
    }

    /**
     * Creates a fixed size layered bloom filter that adds new filters to the list,
     * but never merges them. List will never exceed maxDepth. As additional filters
     * are added earlier filters are removed.  Uses SimpleBloomFilters.
     *
     * @param shape    The shape for the enclosed Bloom filters.
     * @param maxDepth The maximum depth of layers.
     * @return An empty layered Bloom filter of the specified shape and depth.
     */
    public static LayeredBloomFilter<SimpleBloomFilter> fixed(final Shape shape, final int maxDepth) {
        return fixed(shape, maxDepth, () -> new SimpleBloomFilter(shape));
    }

    /**
     * Creates a fixed size layered bloom filter that adds new filters to the list,
     * but never merges them. List will never exceed maxDepth. As additional filters
     * are added earlier filters are removed.
     *
     * @param shape    The shape for the enclosed Bloom filters.
     * @param maxDepth The maximum depth of layers.
     * @param supplier A supplier of the Bloom filters to create layers with.
     * @return An empty layered Bloom filter of the specified shape and depth.
     */
    public static <T extends BloomFilter<T>> LayeredBloomFilter<T> fixed(final Shape shape, final int maxDepth, final Supplier<T> supplier) {
        final LayerManager.Builder<T> builder = LayerManager.builder();
        builder.setExtendCheck(LayerManager.ExtendCheck.advanceOnPopulated())
                .setCleanup(LayerManager.Cleanup.onMaxSize(maxDepth)).setSupplier(supplier);
        return new LayeredBloomFilter<>(shape, builder.get());
    }

    // instrumentation to record timestamps in dbgInstrument list
    private final Predicate<BloomFilter> dbg = bf -> {
        final TimestampedBloomFilter tbf = (TimestampedBloomFilter) bf;
        final Instant ts = Instant.now();
        dbgInstrument.add(String.format("T:%s (Elapsed:%s)- EstN:%s (Card:%s)\n", tbf.timestamp, Duration.between(tbf.timestamp, ts),
                tbf.estimateN(), tbf.cardinality()));
        return true;
    };
    // *** end of instrumentation ***

    @Override
    protected LayeredBloomFilter<SimpleBloomFilter> createEmptyFilter(final Shape shape) {
        return LayeredBloomFilterTest.fixed(shape, 10);
    }

    protected BloomFilter makeFilter(final Hasher h) {
        final BloomFilter bf = new SparseBloomFilter(getTestShape());
        bf.merge(h);
        return bf;
    }

    protected BloomFilter makeFilter(final IndexExtractor p) {
        final BloomFilter bf = new SparseBloomFilter(getTestShape());
        bf.merge(p);
        return bf;
    }

    protected BloomFilter makeFilter(final int... values) {
        return makeFilter(IndexExtractor.fromIndexArray(values));
    }

    private LayeredBloomFilter<SimpleBloomFilter> setupFindTest() {
        final LayeredBloomFilter<SimpleBloomFilter> filter = LayeredBloomFilterTest.fixed(getTestShape(), 10);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        filter.merge(new IncrementingHasher(11, 2));
        filter.merge(TestingHashers.populateFromHashersFrom1AndFrom11(new SimpleBloomFilter(getTestShape())));
        return filter;
    }

    @Override
    @Test
    public void testCardinalityAndIsEmpty() {
        final LayerManager<SimpleBloomFilter> layerManager = LayerManager.<SimpleBloomFilter>builder().setExtendCheck(ExtendCheck.neverAdvance())
                .setSupplier(() -> new SimpleBloomFilter(getTestShape())).get();
        testCardinalityAndIsEmpty(new LayeredBloomFilter<>(getTestShape(), layerManager));
    }

    // ***** TESTS THAT CHECK LAYERED PROCESSING ******

    @Test
    public void testCleanup() {
        final int[] sequence = {1};
        final LayerManager<NumberedBloomFilter> layerManager = LayerManager.<NumberedBloomFilter>builder()
                .setSupplier(() -> new NumberedBloomFilter(getTestShape(), 3, sequence[0]++))
                .setExtendCheck(ExtendCheck.neverAdvance())
                .setCleanup(ll -> ll.removeIf(f -> (f.value-- == 0))).get();
        final LayeredBloomFilter<NumberedBloomFilter> underTest = new LayeredBloomFilter<>(getTestShape(), layerManager);
        assertEquals(1, underTest.getDepth());
        underTest.merge(TestingHashers.randomHasher());
        underTest.cleanup(); // first count == 2
        assertEquals(1, underTest.getDepth());
        underTest.next(); // first count == 1
        assertEquals(2, underTest.getDepth());
        underTest.merge(TestingHashers.randomHasher());
        underTest.cleanup(); // first count == 0
        NumberedBloomFilter f = underTest.get(0);
        assertEquals(1, f.sequence);

        assertEquals(2, underTest.getDepth());
        underTest.cleanup(); // should be removed ; second is now 1st with value 1
        assertEquals(1, underTest.getDepth());
        f = underTest.get(0);
        assertEquals(2, f.sequence);

        underTest.cleanup(); // first count == 0
        underTest.cleanup(); // should be removed.  But there is always at least one
        assertEquals(1, underTest.getDepth());
        f = underTest.get(0);
        assertEquals(3, f.sequence);  // it is a new one.
    }
    /**
     * Tests that the estimated union calculations are correct.
     */
    @Test
    public final void testEstimateUnionCrossTypes() {
        final BloomFilter bf = createFilter(getTestShape(), TestingHashers.FROM1);
        final BloomFilter bf2 = new DefaultBloomFilterTest.SparseDefaultBloomFilter(getTestShape());
        bf2.merge(TestingHashers.FROM11);

        assertEquals(2, bf.estimateUnion(bf2));
        assertEquals(2, bf2.estimateUnion(bf));
    }

    @Test
    public void testExpiration() throws InterruptedException {
        // this test uses the instrumentation noted above to track changes for debugging
        // purposes.

        // list of timestamps that are expected to be expired.
        final List<Instant> lst = new ArrayList<>();
        final Shape shape = Shape.fromNM(4, 64);

        // create a filter that removes filters that are 4 seconds old
        // and quantises time to 1 second intervals.
        final LayeredBloomFilter<TimestampedBloomFilter<SimpleBloomFilter>> underTest = createTimedLayeredFilter(shape, Duration.ofMillis(600),
                Duration.ofMillis(150));

        for (int i = 0; i < 10; i++) {
            underTest.merge(TestingHashers.randomHasher());
        }
        underTest.processBloomFilters(dbg.and(x -> lst.add(((TimestampedBloomFilter) x).timestamp)));
        assertTrue(underTest.getDepth() > 1);

        Thread.sleep(300);
        for (int i = 0; i < 10; i++) {
            underTest.merge(TestingHashers.randomHasher());
        }
        dbgInstrument.add("=== AFTER 300 milliseconds ====\n");
        underTest.processBloomFilters(dbg);

        Thread.sleep(150);
        for (int i = 0; i < 10; i++) {
            underTest.merge(TestingHashers.randomHasher());
        }
        dbgInstrument.add("=== AFTER 450 milliseconds ====\n");
        underTest.processBloomFilters(dbg);

        // sleep 200 milliseconds to ensure we cross the 600 millisecond boundary
        Thread.sleep(200);
        underTest.merge(TestingHashers.randomHasher());
        dbgInstrument.add("=== AFTER 600 milliseconds ====\n");
        assertTrue(underTest.processBloomFilters(dbg.and(x -> !lst.contains(((TimestampedBloomFilter) x).timestamp))),
                "Found filter that should have been deleted: " + dbgInstrument.get(dbgInstrument.size() - 1));
    }

    @Test
    public void testFindBitMapExtractor() {
        final LayeredBloomFilter<SimpleBloomFilter> filter = setupFindTest();

        IndexExtractor indexExtractor = TestingHashers.FROM1.indices(getTestShape());
        BitMapExtractor bitMapExtractor = BitMapExtractor.fromIndexExtractor(indexExtractor, getTestShape().getNumberOfBits());

        int[] expected = {0, 3};
        int[] result = filter.find(bitMapExtractor);
        assertArrayEquals(expected, result);

        expected = new int[]{1, 3};
        indexExtractor = TestingHashers.FROM11.indices(getTestShape());
        bitMapExtractor = BitMapExtractor.fromIndexExtractor(indexExtractor, getTestShape().getNumberOfBits());
        result = filter.find(bitMapExtractor);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testFindBloomFilter() {
        final LayeredBloomFilter<SimpleBloomFilter> filter = setupFindTest();
        int[] expected = {0, 3};
        int[] result = filter.find(TestingHashers.FROM1);
        assertArrayEquals(expected, result);
        expected = new int[] {1, 3};
        result = filter.find(TestingHashers.FROM11);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testFindIndexExtractor() {
        IndexExtractor indexExtractor = TestingHashers.FROM1.indices(getTestShape());
        final LayeredBloomFilter<SimpleBloomFilter> filter = setupFindTest();

        int[] expected = {0, 3};
        int[] result = filter.find(indexExtractor);
        assertArrayEquals(expected, result);

        expected = new int[] {1, 3};
        indexExtractor = TestingHashers.FROM11.indices(getTestShape());
        result = filter.find(indexExtractor);
        assertArrayEquals(expected, result);
    }

    @Test
    public final void testGetLayer() {
        final BloomFilter bf = new SimpleBloomFilter(getTestShape());
        bf.merge(TestingHashers.FROM11);
        final LayeredBloomFilter<SimpleBloomFilter> filter = LayeredBloomFilterTest.fixed(getTestShape(), 10);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        filter.merge(new IncrementingHasher(11, 2));
        filter.merge(TestingHashers.populateFromHashersFrom1AndFrom11(new SimpleBloomFilter(getTestShape())));
        assertArrayEquals(bf.asBitMapArray(), filter.get(1).asBitMapArray());
    }

    @Test
    public void testMultipleFilters() {
        final LayeredBloomFilter<SimpleBloomFilter> filter = LayeredBloomFilterTest.fixed(getTestShape(), 10);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        assertEquals(2, filter.getDepth());
        assertTrue(filter.contains(makeFilter(TestingHashers.FROM1)));
        assertTrue(filter.contains(makeFilter(TestingHashers.FROM11)));
        final BloomFilter t1 = makeFilter(6, 7, 17, 18, 19);
        assertFalse(filter.contains(t1));
        assertFalse(filter.copy().contains(t1));
        assertTrue(filter.flatten().contains(t1));
    }

    @Test
    public final void testNext() {
        final LayerManager<SimpleBloomFilter> layerManager = LayerManager.<SimpleBloomFilter>builder().setSupplier(() -> new SimpleBloomFilter(getTestShape())).get();
        final LayeredBloomFilter<SimpleBloomFilter> filter = new LayeredBloomFilter<>(getTestShape(), layerManager);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        assertEquals(1, filter.getDepth());
        filter.next();
        filter.merge(new IncrementingHasher(11, 2));
        assertEquals(2, filter.getDepth());
        assertTrue(filter.get(0).contains(TestingHashers.FROM1));
        assertTrue(filter.get(0).contains(TestingHashers.FROM11));
        assertFalse(filter.get(0).contains(new IncrementingHasher(11, 2)));
        assertFalse(filter.get(1).contains(TestingHashers.FROM1));
        assertFalse(filter.get(1).contains(TestingHashers.FROM11));
        assertTrue(filter.get(1).contains(new IncrementingHasher(11, 2)));
    }
}
