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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.collections4.bloomfilter.LayerManager.Cleanup;
import org.apache.commons.collections4.bloomfilter.LayerManager.ExtendCheck;
import org.junit.jupiter.api.Test;

public class LayeredBloomFilterTest extends AbstractBloomFilterTest<LayeredBloomFilter> {

    /**
     * A Predicate that advances after a quantum of time.
     */
    static class AdvanceOnTimeQuanta implements Predicate<LayerManager> {
        long quanta;

        AdvanceOnTimeQuanta(long quanta, TimeUnit unit) {
            this.quanta = unit.toMillis(quanta);
        }

        @Override
        public boolean test(LayerManager lm) {
            // can not use getTarget() as it causes recursion.
            TimestampedBloomFilter bf = (TimestampedBloomFilter) lm.get(lm.getDepth() - 1);
            return bf.timestamp + quanta < System.currentTimeMillis();
        }
    }

    /**
     * A Consumer that cleans the list based on how long each filters has been in
     * the list.
     */
    static class CleanByTime implements Consumer<LinkedList<BloomFilter>> {
        long elapsedTime;

        CleanByTime(long duration, TimeUnit unit) {
            elapsedTime = unit.toMillis(duration);
        }

        @Override
        public void accept(LinkedList<BloomFilter> t) {
            long min = System.currentTimeMillis() - elapsedTime;
            while (!t.isEmpty() && ((TimestampedBloomFilter) t.getFirst()).getTimestamp() < min) {
                TimestampedBloomFilter bf = (TimestampedBloomFilter) t.getFirst();
                dbgInstrument.add(String.format("Removing old entry: T:%s (Aged: %s) \n", bf.getTimestamp(),
                        (min - bf.getTimestamp())));
                t.removeFirst();
            }
        }
    }

    /**
     * A Bloomfilter implementation that tracks the creation time.
     */
    static class TimestampedBloomFilter extends WrappedBloomFilter {
        final long timestamp;

        TimestampedBloomFilter(BloomFilter bf) {
            super(bf);
            this.timestamp = System.currentTimeMillis();
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    // ***example of instrumentation ***
    private static List<String> dbgInstrument = new ArrayList<>();

    /**
     * Creates a LayeredBloomFilter that retains enclosed filters for
     * {@code duration} and limits the contents of each enclosed filter to a time
     * {@code quanta}. This filter uses the timestamped Bloom filter internally.
     *
     * @param shape    The shape of the Bloom filters.
     * @param duration The length of time to keep filters in the list.
     * @param dUnit    The unit of time to apply to duration.
     * @param quanta   The quantization factor for each filter. Individual filters
     *                 will span at most this much time.
     * @param qUnit    the unit of time to apply to quanta.
     * @return LayeredBloomFilter with the above properties.
     */
    static LayeredBloomFilter createTimedLayeredFilter(Shape shape, long duration, TimeUnit dUnit, long quanta,
            TimeUnit qUnit) {
        LayerManager layerManager = LayerManager.builder()
                .setSupplier(() -> new TimestampedBloomFilter(new SimpleBloomFilter(shape)))
                .setCleanup(Cleanup.removeEmptyTarget().andThen(new CleanByTime(duration, dUnit)))
                .setExtendCheck(new AdvanceOnTimeQuanta(quanta, qUnit)
                        .or(LayerManager.ExtendCheck.advanceOnSaturation(shape.estimateMaxN())))
                .build();
        return new LayeredBloomFilter(shape, layerManager);
    }

    // instrumentation to record timestamps in dbgInstrument list
    private Predicate<BloomFilter> dbg = (bf) -> {
        TimestampedBloomFilter tbf = (TimestampedBloomFilter) bf;
        long ts = System.currentTimeMillis();
        dbgInstrument.add(String.format("T:%s (Elapsed:%s)- EstN:%s (Card:%s)\n", tbf.timestamp, ts - tbf.timestamp,
                tbf.estimateN(), tbf.cardinality()));
        return true;
    };
    // *** end of instrumentation ***

    @Override
    protected LayeredBloomFilter createEmptyFilter(Shape shape) {
        return LayeredBloomFilter.fixed(shape, 10);
    }

    protected BloomFilter makeFilter(Hasher h) {
        BloomFilter bf = new SparseBloomFilter(getTestShape());
        bf.merge(h);
        return bf;
    }

    protected BloomFilter makeFilter(IndexProducer p) {
        BloomFilter bf = new SparseBloomFilter(getTestShape());
        bf.merge(p);
        return bf;
    }

    protected BloomFilter makeFilter(int... values) {
        return makeFilter(IndexProducer.fromIndexArray(values));
    }

    private LayeredBloomFilter setupFindTest() {
        LayeredBloomFilter filter = LayeredBloomFilter.fixed(getTestShape(), 10);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        filter.merge(new IncrementingHasher(11, 2));
        filter.merge(TestingHashers.populateFromHashersFrom1AndFrom11(new SimpleBloomFilter(getTestShape())));
        return filter;
    }

    @Override
    @Test
    public void testCardinalityAndIsEmpty() {
        LayerManager layerManager = LayerManager.builder().setExtendCheck(ExtendCheck.neverAdvance())
                .setSupplier(() -> new SimpleBloomFilter(getTestShape())).build();
        testCardinalityAndIsEmpty(new LayeredBloomFilter(getTestShape(), layerManager));
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

    // ***** TESTS THAT CHECK LAYERED PROCESSING ******

    @Test
    public void testExpiration() throws InterruptedException {
        // this test uses the instrumentation noted above to track changes for debugging
        // purposes.

        // list of timestamps that are expected to be expired.
        List<Long> lst = new ArrayList<>();
        Shape shape = Shape.fromNM(4, 64);

        // create a filter that removes filters that are 4 seconds old
        // and quantises time to 1 second intervals.
        LayeredBloomFilter underTest = createTimedLayeredFilter(shape, 600, TimeUnit.MILLISECONDS, 150,
                TimeUnit.MILLISECONDS);

        for (int i = 0; i < 10; i++) {
            underTest.merge(TestingHashers.randomHasher());
        }
        underTest.forEachBloomFilter(dbg.and(x -> lst.add(((TimestampedBloomFilter) x).timestamp)));
        assertTrue(underTest.getDepth() > 1);

        Thread.sleep(300);
        for (int i = 0; i < 10; i++) {
            underTest.merge(TestingHashers.randomHasher());
        }
        dbgInstrument.add("=== AFTER 300 milliseconds ====\n");
        underTest.forEachBloomFilter(dbg);

        Thread.sleep(150);
        for (int i = 0; i < 10; i++) {
            underTest.merge(TestingHashers.randomHasher());
        }
        dbgInstrument.add("=== AFTER 450 milliseconds ====\n");
        underTest.forEachBloomFilter(dbg);

        // sleep 200 milliseconds to ensure we cross the 600 millisecond boundary
        Thread.sleep(200);
        underTest.merge(TestingHashers.randomHasher());
        dbgInstrument.add("=== AFTER 600 milliseconds ====\n");
        assertTrue(underTest.forEachBloomFilter(dbg.and(x -> !lst.contains(((TimestampedBloomFilter) x).timestamp))),
                "Found filter that should have been deleted: " + dbgInstrument.get(dbgInstrument.size() - 1));
    }
    @Test
    public void testFindBitMapProducer() {
        LayeredBloomFilter filter = setupFindTest();

        IndexProducer idxProducer = TestingHashers.FROM1.indices(getTestShape());
        BitMapProducer producer = BitMapProducer.fromIndexProducer(idxProducer, getTestShape().getNumberOfBits());

        int[] expected = {0, 3};
        int[] result = filter.find(producer);
        assertArrayEquals(expected, result);

        expected = new int[]{1, 3};
        idxProducer = TestingHashers.FROM11.indices(getTestShape());
        producer = BitMapProducer.fromIndexProducer(idxProducer, getTestShape().getNumberOfBits());
        result = filter.find(producer);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testFindBloomFilter() {
        LayeredBloomFilter filter = setupFindTest();
        int[] expected = {0, 3};
        int[] result = filter.find(TestingHashers.FROM1);
        assertArrayEquals(expected, result);
        expected = new int[] {1, 3};
        result = filter.find(TestingHashers.FROM11);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testFindIndexProducer() {
        IndexProducer producer = TestingHashers.FROM1.indices(getTestShape());
        LayeredBloomFilter filter = setupFindTest();

        int[] expected = {0, 3};
        int[] result = filter.find(producer);
        assertArrayEquals(expected, result);

        expected = new int[] {1, 3};
        producer = TestingHashers.FROM11.indices(getTestShape());
        result = filter.find(producer);
        assertArrayEquals(expected, result);
    }

    @Test
    public final void testGetLayer() {
        BloomFilter bf = new SimpleBloomFilter(getTestShape());
        bf.merge(TestingHashers.FROM11);
        LayeredBloomFilter filter = LayeredBloomFilter.fixed(getTestShape(), 10);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        filter.merge(new IncrementingHasher(11, 2));
        filter.merge(TestingHashers.populateFromHashersFrom1AndFrom11(new SimpleBloomFilter(getTestShape())));
        assertArrayEquals(bf.asBitMapArray(), filter.get(1).asBitMapArray());
    }

    @Test
    public void testMultipleFilters() {
        LayeredBloomFilter filter = LayeredBloomFilter.fixed(getTestShape(), 10);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        assertEquals(2, filter.getDepth());
        assertTrue(filter.contains(makeFilter(TestingHashers.FROM1)));
        assertTrue(filter.contains(makeFilter(TestingHashers.FROM11)));
        BloomFilter t1 = makeFilter(6, 7, 17, 18, 19);
        assertFalse(filter.contains(t1));
        assertFalse(filter.copy().contains(t1));
        assertTrue(filter.flatten().contains(t1));
    }

    @Test
    public final void testNext() {
        LayerManager layerManager = LayerManager.builder().setSupplier(() -> new SimpleBloomFilter(getTestShape()))
                .build();

        LayeredBloomFilter filter = new LayeredBloomFilter(getTestShape(), layerManager);
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
