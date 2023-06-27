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

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import org.apache.commons.collections4.bloomfilter.AbstractBloomFilterTest;
import org.apache.commons.collections4.bloomfilter.BitMapProducer;
import org.apache.commons.collections4.bloomfilter.SparseBloomFilter;
import org.apache.commons.collections4.bloomfilter.SimpleBloomFilter;
import org.apache.commons.collections4.bloomfilter.TestingHashers;
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.DefaultBloomFilterTest;
import org.apache.commons.collections4.bloomfilter.Hasher;
import org.apache.commons.collections4.bloomfilter.IncrementingHasher;
import org.apache.commons.collections4.bloomfilter.IndexProducer;
import org.apache.commons.collections4.bloomfilter.LongBiPredicate;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.junit.jupiter.api.Test;

public class LayeredBloomFilterTest2 {

    public LayeredBloomFilter createTimedLayeredFilter(Shape shape, long duration, TimeUnit dUnit, long quanta, TimeUnit qUnit) {
        return new LayeredBloomFilter( shape, new LayerManager(
                () -> new TimestampedBloomFilter( new SimpleBloomFilter(shape) ),
                new AdvanceOnFull(shape, quanta, qUnit),
                new CleanByTime( duration, dUnit)
                ) );
    }
    
    private static class AdvanceOnFull implements Predicate<LayerManager> {
        double maxN;
        long quanta;
        AdvanceOnFull(Shape shape, long quanta, TimeUnit unit) {
            maxN = shape.estimateMaxN();
            this.quanta = unit.toMillis(quanta);
        }

        @Override
        public boolean test(LayerManager lm) {
            int depth = lm.getDepth();
            if (depth == 0) {
                return false;
            }
            TimestampedBloomFilter bf = (TimestampedBloomFilter) lm.get(depth-1);
            return bf.timestamp+quanta < System.currentTimeMillis() || bf.getShape().estimateN(bf.cardinality()) >= maxN;
        }
    }
    
    private static class CleanByTime implements Consumer<LinkedList<BloomFilter>> {
        long elapsedTime;
        
        CleanByTime(long duration, TimeUnit unit) {
            elapsedTime = unit.toMillis(duration);
        }

        @Override
        public void accept(LinkedList<BloomFilter> t) {
            long min = System.currentTimeMillis() - elapsedTime;
            while (!t.isEmpty() && ((TimestampedBloomFilter)t.getFirst()).getTimestamp()< min) {
                System.out.println( "Removing old entry: "+(min-((TimestampedBloomFilter)t.getFirst()).getTimestamp()));
                t.removeFirst();
            }
        }
    }
    
    /**
     * Resets the timestamp on every merge.
     *
     */
    private static class TimestampedBloomFilter implements BloomFilter {
        final long timestamp;
        final BloomFilter bf;
        
        public TimestampedBloomFilter(BloomFilter bf) {
            this.timestamp = System.currentTimeMillis();
            this.bf = bf;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean forEachIndex(IntPredicate predicate) {
            return bf.forEachIndex(predicate);
        }

        public BloomFilter copy() {
            return bf.copy();
        }

        public boolean forEachBitMap(LongPredicate predicate) {
            return bf.forEachBitMap(predicate);
        }

        public int characteristics() {
            return bf.characteristics();
        }

        public Shape getShape() {
            return bf.getShape();
        }

        public void clear() {
            bf.clear();
        }

        public boolean contains(BloomFilter other) {
            return bf.contains(other);
        }

        public boolean forEachBitMapPair(BitMapProducer other, LongBiPredicate func) {
            return bf.forEachBitMapPair(other, func);
        }

        public boolean contains(Hasher hasher) {
            return bf.contains(hasher);
        }

        public long[] asBitMapArray() {
            return bf.asBitMapArray();
        }

        public int[] asIndexArray() {
            return bf.asIndexArray();
        }

        public boolean contains(IndexProducer indexProducer) {
            return bf.contains(indexProducer);
        }

        public boolean contains(BitMapProducer bitMapProducer) {
            return bf.contains(bitMapProducer);
        }

        public boolean merge(BloomFilter other) {
            return bf.merge(other);
        }

        public boolean merge(Hasher hasher) {
            return bf.merge(hasher);
        }

        public boolean merge(IndexProducer indexProducer) {
            return bf.merge(indexProducer);
        }

        public boolean merge(BitMapProducer bitMapProducer) {
            return bf.merge(bitMapProducer);
        }

        public boolean isFull() {
            return bf.isFull();
        }

        public int cardinality() {
            return bf.cardinality();
        }

        public int estimateN() {
            return bf.estimateN();
        }

        public int estimateUnion(BloomFilter other) {
            return bf.estimateUnion(other);
        }

        public int estimateIntersection(BloomFilter other) {
            return bf.estimateIntersection(other);
        }
        
    }
    

    @Test
    public void testExpiration() throws InterruptedException {
        Predicate<BloomFilter> dbg = (bf) -> { TimestampedBloomFilter tbf = (TimestampedBloomFilter)bf;
        long ts = System.currentTimeMillis();
        System.out.format( "%s (%s)- %s (%s)\n", tbf.timestamp, ts-tbf.timestamp, tbf.estimateN(), tbf.cardinality());
        return true;
    };
    
        List<Long> lst = new ArrayList<Long>();
        Shape shape = Shape.fromNM(4, 64);
        
        LayeredBloomFilter underTest = createTimedLayeredFilter(shape, 4, TimeUnit.SECONDS, 1, TimeUnit.SECONDS );
        
        for (int i=0;i<10;i++) {
            underTest.merge( TestingHashers.randomHasher());
            underTest.forEachBloomFilter(dbg );
        }
        System.out.println( "=== AFTER ===");
        underTest.forEachBloomFilter(dbg.and( x -> lst.add(((TimestampedBloomFilter)x).timestamp)));
        assertTrue( underTest.getDepth() > 1);
        
        Thread.sleep( TimeUnit.SECONDS.toMillis(2));
        for (int i=0;i<10;i++) {
            underTest.merge( TestingHashers.randomHasher());
        }
        System.out.println( "=== AFTER 2 ===");
        underTest.forEachBloomFilter(dbg );
        
        
        Thread.sleep( TimeUnit.SECONDS.toMillis(1));
        for (int i=0;i<10;i++) {
            underTest.merge( TestingHashers.randomHasher());
        }
        System.out.println( "=== AFTER 3 ===");
        underTest.forEachBloomFilter(dbg);
        
        
        Thread.sleep( TimeUnit.SECONDS.toMillis(1));
        underTest.merge( TestingHashers.randomHasher());
        System.out.println( "=== AFTER 4 ===");
        underTest.forEachBloomFilter(dbg);
        
        assertTrue( underTest.forEachBloomFilter( x -> !lst.contains(((TimestampedBloomFilter)x).timestamp)),
                "Found filter that should have been deleted");
        
    }
}
