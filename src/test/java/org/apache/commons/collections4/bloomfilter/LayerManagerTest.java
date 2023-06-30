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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

public class LayerManagerTest {

    private Shape shape = Shape.fromKM(17, 72);

    private LayerManager.Builder testBuilder() {
        return LayerManager.builder().withSuplier(() -> new SimpleBloomFilter(shape));
    }

    @Test
    public void testAdvanceOnPopulated() {
        Predicate<LayerManager> underTest = LayerManager.ExtendCheck.ADVANCE_ON_POPULATED;
        LayerManager layerManager = testBuilder().build();
        assertFalse(underTest.test(layerManager));
        layerManager.target().merge(TestingHashers.FROM1);
        assertTrue(underTest.test(layerManager));
    }

    @Test
    public void testNeverAdvance() {
        Predicate<LayerManager> underTest = LayerManager.ExtendCheck.NEVER_ADVANCE;
        LayerManager layerManager = testBuilder().build();
        assertFalse(underTest.test(layerManager));
        layerManager.target().merge(TestingHashers.FROM1);
        assertFalse(underTest.test(layerManager));
    }

    @Test
    public void testAdvanceOnCount() {
        Predicate<LayerManager> underTest = LayerManager.ExtendCheck.advanceOnCount(4);
        LayerManager layerManager = testBuilder().build();
        for (int i = 0; i < 3; i++) {
            assertFalse(underTest.test(layerManager), "at " + i);
            layerManager.target().merge(TestingHashers.FROM1);
        }
        assertTrue(underTest.test(layerManager));
    }

    @Test
    public void testAdvanceOnCalculatedFull() {
        Double maxN = shape.estimateMaxN();
        Predicate<LayerManager> underTest = LayerManager.ExtendCheck.advanceOnCalculatedFull(shape);
        LayerManager layerManager = testBuilder().build();
        while (layerManager.target().getShape().estimateN(layerManager.target().cardinality()) < maxN) {
            assertFalse(underTest.test(layerManager));
            layerManager.target().merge(TestingHashers.randomHasher());
        }
        assertTrue(underTest.test(layerManager));
    }

    @Test
    public void testAdvanceOnSaturationWithDouble() {
        Double maxN = shape.estimateMaxN();
        Predicate<LayerManager> underTest = LayerManager.ExtendCheck.advanceOnSaturation(maxN);
        LayerManager layerManager = testBuilder().build();
        while (layerManager.target().getShape().estimateN(layerManager.target().cardinality()) < maxN) {
            assertFalse(underTest.test(layerManager));
            layerManager.target().merge(TestingHashers.randomHasher());
        }
        assertTrue(underTest.test(layerManager));
    }

    @Test
    public void testAdvanceOnSaturationWithInt() {
        int maxN = (int) Math.floor(shape.estimateMaxN());
        Predicate<LayerManager> underTest = LayerManager.ExtendCheck.advanceOnSaturation(maxN);
        LayerManager layerManager = testBuilder().build();
        while (layerManager.target().estimateN() < maxN) {
            assertFalse(underTest.test(layerManager));
            layerManager.target().merge(TestingHashers.randomHasher());
        }
        assertTrue(underTest.test(layerManager));
    }

    @Test
    public void testOnMaxSize() {
        Consumer<LinkedList<BloomFilter>> underTest = LayerManager.Cleanup.onMaxSize(5);
        LinkedList<BloomFilter> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            assertEquals(i, list.size());
            list.add(new SimpleBloomFilter(shape));
            underTest.accept(list);
        }
        assertEquals(5, list.size());
        list.add(new SimpleBloomFilter(shape));
        underTest.accept(list);

        assertEquals(5, list.size());
        list.add(new SimpleBloomFilter(shape));
        underTest.accept(list);

        assertEquals(5, list.size());
        list.add(new SimpleBloomFilter(shape));
        underTest.accept(list);
    }

    @Test
    public void testCopy() {
        LayerManager underTest = LayerManager.builder().withSuplier(() -> new SimpleBloomFilter(shape)).build();
        underTest.target().merge(TestingHashers.randomHasher());
        underTest.next();
        underTest.target().merge(TestingHashers.randomHasher());
        underTest.next();
        underTest.target().merge(TestingHashers.randomHasher());
        assertEquals(3, underTest.getDepth());

        LayerManager copy = underTest.copy();
        assertFalse(underTest == copy);
        assertFalse(underTest.equals(copy));

        assertEquals(underTest.getDepth(), copy.getDepth());
        assertTrue(
                underTest.forEachBloomFilterPair(copy, (x, y) -> Arrays.equals(x.asBitMapArray(), y.asBitMapArray())));
    }

    @Test
    public void testClear() {
        LayerManager underTest = LayerManager.builder().withSuplier(() -> new SimpleBloomFilter(shape)).build();
        underTest.target().merge(TestingHashers.randomHasher());
        underTest.next();
        underTest.target().merge(TestingHashers.randomHasher());
        underTest.next();
        underTest.target().merge(TestingHashers.randomHasher());
        assertEquals(3, underTest.getDepth());
        underTest.clear();
        assertEquals(1, underTest.getDepth());
        assertEquals(0, underTest.target().cardinality());
    }

    @Test
    public void testNext() {
        LayerManager underTest = LayerManager.builder().withSuplier(() -> new SimpleBloomFilter(shape)).build();
        assertEquals(1, underTest.getDepth());
        underTest.target().merge(TestingHashers.randomHasher());
        assertEquals(1, underTest.getDepth());
        underTest.next();
        assertEquals(2, underTest.getDepth());
    }

    @Test
    public void testGetDepth() {
        LayerManager underTest = LayerManager.builder().withSuplier(() -> new SimpleBloomFilter(shape)).build();
        assertEquals(1, underTest.getDepth());
        underTest.target().merge(TestingHashers.randomHasher());
        assertEquals(1, underTest.getDepth());
        underTest.next();
        assertEquals(2, underTest.getDepth());
    }

    @Test
    public void testTarget() {
        boolean[] extendCheckCalled = { false };
        boolean[] cleanupCalled = { false };
        int[] supplierCount = { 0 };
        LayerManager underTest = LayerManager.builder().withSuplier(() -> {
            supplierCount[0]++;
            return new SimpleBloomFilter(shape);
        }).withExtendCheck(lm -> {
            extendCheckCalled[0] = true;
            return true;
        }).withCleanup(ll -> {
            cleanupCalled[0] = true;
        }).build();
        assertFalse(extendCheckCalled[0]);
        assertFalse(cleanupCalled[0]);
        assertEquals(1, supplierCount[0]);
        underTest.target();
        assertTrue(extendCheckCalled[0]);
        assertTrue(cleanupCalled[0]);
        assertEquals(2, supplierCount[0]);
    }

    @Test
    public void testForEachBloomFilter() {
        LayerManager underTest = LayerManager.builder().withSuplier(() -> new SimpleBloomFilter(shape))
                .withExtendCheck(LayerManager.ExtendCheck.ADVANCE_ON_POPULATED).build();

        List<BloomFilter> lst = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BloomFilter bf = new SimpleBloomFilter(shape);
            bf.merge(TestingHashers.randomHasher());
            lst.add(bf);
            underTest.target().merge(bf);
        }
        List<BloomFilter> lst2 = new ArrayList<>();
        underTest.forEachBloomFilter(lst2::add);
        assertEquals(10, lst.size());
        assertEquals(10, lst2.size());
        for (int i = 0; i < lst.size(); i++) {
            assertArrayEquals(lst.get(i).asBitMapArray(), lst2.get(i).asBitMapArray());
        }
    }

}
