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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

public class LayeredBloomFilterTest extends AbstractBloomFilterTest<LayeredBloomFilter>
{

    public static LayeredBloomFilter createLayeredFilter(Shape shape, int maxDepth, Predicate<LayerManager> extendCheck)
    {
        return new LayeredBloomFilter(shape, new LayerManager(
                LayerManager.FilterSupplier.simple(shape),
                extendCheck,
                LayerManager.Cleanup.onMaxSize(maxDepth)));
    }

    @Override
    protected LayeredBloomFilter createEmptyFilter(Shape shape)
    {
        return createLayeredFilter(shape, 10, LayerManager.ExtendCheck.ADVANCE_ON_POPULATED);
    }

    protected BloomFilter makeFilter(int... values)
    {
        return makeFilter(IndexProducer.fromIndexArray(values));
    }

    protected BloomFilter makeFilter(IndexProducer p)
    {
        BloomFilter bf = new SparseBloomFilter(getTestShape());
        bf.merge(p);
        return bf;
    }

    protected BloomFilter makeFilter(Hasher h)
    {
        BloomFilter bf = new SparseBloomFilter(getTestShape());
        bf.merge(h);
        return bf;
    }

    @Test
    public void testMultipleFilters()
    {
        LayeredBloomFilter filter = createLayeredFilter(getTestShape(), 10,
                LayerManager.ExtendCheck.ADVANCE_ON_POPULATED);
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
    public void testFind()
    {
        LayeredBloomFilter filter = createLayeredFilter(getTestShape(), 10,
                LayerManager.ExtendCheck.ADVANCE_ON_POPULATED);
        filter.merge(TestingHashers.FROM1);
        filter.merge(TestingHashers.FROM11);
        filter.merge(new IncrementingHasher(11, 2));
        filter.merge(TestingHashers.populateFromHashersFrom1AndFrom11(new SimpleBloomFilter(getTestShape())));
        int[] result = filter.find(TestingHashers.FROM1);
        assertEquals(2, result.length);
        assertEquals(0, result[0]);
        assertEquals(3, result[1]);
        result = filter.find(TestingHashers.FROM11);
        assertEquals(2, result.length);
        assertEquals(1, result[0]);
        assertEquals(3, result[1]);
    }

    /**
     * Tests that the estimated union calculations are correct.
     */
    @Test
    public final void testEstimateUnionCrossTypes()
    {
        final BloomFilter bf = createFilter(getTestShape(), TestingHashers.FROM1);
        final BloomFilter bf2 = new DefaultBloomFilterTest.SparseDefaultBloomFilter(getTestShape());
        bf2.merge(TestingHashers.FROM11);

        assertEquals(2, bf.estimateUnion(bf2));
        assertEquals(2, bf2.estimateUnion(bf));
    }

    // ***** TESTS THAT CHECK LAYERED PROCESSING ******

    // ***example of instrumentation ***
    private static List<String> dbgInstrument = new ArrayList<>();
    // instrumentation to record timestamps in dbgInstrument list
    private Predicate<BloomFilter> dbg = (bf) -> {
        TimestampedBloomFilter tbf = (TimestampedBloomFilter) bf;
        long ts = System.currentTimeMillis();
        dbgInstrument.add(String.format("T:%s (Elapsed:%s)- EstN:%s (Card:%s)\n", tbf.timestamp, ts - tbf.timestamp, tbf.estimateN(),
                tbf.cardinality()));
        return true;
    };
    // *** end of instrumentation ***

    /**
     * Creates a LayeredBloomFilter that retains enclosed filters for {@code duration} and limits the contents of each
     * enclosed filter to a time {@code quanta}. This filter uses the timestamped Bloom filter internally.
     *
     * @param shape
     *            The shape of the Bloom filters.
     * @param duration
     *            The length of time to keep filters in the list.
     * @param dUnit
     *            The unit of time to apply to duration.
     * @param quanta
     *            The quantization factor for each filter. Individual filters will span at most this much time.
     * @param qUnit
     *            the unit of time to apply to quanta.
     * @return LayeredBloomFilter with the above properties.
     */
    static LayeredBloomFilter createTimedLayeredFilter(Shape shape, long duration, TimeUnit dUnit, long quanta,
            TimeUnit qUnit)
    {
        return new LayeredBloomFilter(shape, new LayerManager(
                () -> new TimestampedBloomFilter(new SimpleBloomFilter(shape)),
                new AdvanceOnFullOrTimeQuanta(shape, quanta, qUnit),
                new CleanByTime(duration, dUnit)));
    }

    /**
     * A Predicate that advances after a quantum of time or when the target bloom filter is full.
     */
    static class AdvanceOnFullOrTimeQuanta implements Predicate<LayerManager>
    {
        double maxN;
        long quanta;

        AdvanceOnFullOrTimeQuanta(Shape shape, long quanta, TimeUnit unit)
        {
            maxN = shape.estimateMaxN();
            this.quanta = unit.toMillis(quanta);
        }

        @Override
        public boolean test(LayerManager lm)
        {
            int depth = lm.getDepth();
            if (depth == 0)
            {
                return false;
            }
            // can not use getTarget() as it causes recursion.
            TimestampedBloomFilter bf = (TimestampedBloomFilter) lm.get(depth - 1);
            return bf.timestamp + quanta < System.currentTimeMillis()
                    || bf.getShape().estimateN(bf.cardinality()) >= maxN;
        }
    }

    /**
     * A Consumer that cleans the list based on how long each filters has been in the list.
     *
     */
    static class CleanByTime implements Consumer<LinkedList<BloomFilter>>
    {
        long elapsedTime;

        CleanByTime(long duration, TimeUnit unit)
        {
            elapsedTime = unit.toMillis(duration);
        }

        @Override
        public void accept(LinkedList<BloomFilter> t)
        {
            long min = System.currentTimeMillis() - elapsedTime;
            while (!t.isEmpty() && ((TimestampedBloomFilter) t.getFirst()).getTimestamp() < min)
            {
                TimestampedBloomFilter bf = (TimestampedBloomFilter) t.getFirst();
                dbgInstrument
                        .add(String.format("Removing old entry: T:%s (Aged: %s) \n", bf.getTimestamp(),
                                (min - bf.getTimestamp())));
                t.removeFirst();
            }
        }
    }

    /**
     * A Bloomfilter implementation that tracks the creation time.
     */
    static class TimestampedBloomFilter extends WrappedBloomFilter
    {
        final long timestamp;

        public TimestampedBloomFilter(BloomFilter bf)
        {
            super(bf);
            this.timestamp = System.currentTimeMillis();
        }

        public long getTimestamp()
        {
            return timestamp;
        }
    }

    @Test
    public void testExpiration() throws InterruptedException
    {
        // this test uses the instrumentation noted above to track changes for debugging purposes.

        // list of timestamps that are expected to be expired.
        List<Long> lst = new ArrayList<>();
        Shape shape = Shape.fromNM(4, 64);

        LayeredBloomFilter underTest = createTimedLayeredFilter(shape, 4, TimeUnit.SECONDS, 1, TimeUnit.SECONDS);

        for (int i = 0; i < 10; i++)
        {
            underTest.merge(TestingHashers.randomHasher());
        }
        underTest.forEachBloomFilter(dbg.and(x -> lst.add(((TimestampedBloomFilter) x).timestamp)));
        assertTrue(underTest.getDepth() > 1);

        Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        for (int i = 0; i < 10; i++)
        {
            underTest.merge(TestingHashers.randomHasher());
        }
        dbgInstrument.add("=== AFTER 2 seconds ====\n");
        underTest.forEachBloomFilter(dbg);

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        for (int i = 0; i < 10; i++)
        {
            underTest.merge(TestingHashers.randomHasher());
        }
        dbgInstrument.add("=== AFTER 3 seconds ====\n");
        underTest.forEachBloomFilter(dbg);

        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        underTest.merge(TestingHashers.randomHasher());
        dbgInstrument.add("=== AFTER 4 seconds ====\n");
        assertTrue(underTest.forEachBloomFilter(dbg.and(x -> !lst.contains(((TimestampedBloomFilter) x).timestamp))),
                "Found filter that should have been deleted");
    }
}
