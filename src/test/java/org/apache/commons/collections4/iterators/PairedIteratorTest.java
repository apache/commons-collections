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
package org.apache.commons.collections4.iterators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.iterators.PairedIterator.PairedItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;


/** Unit test suite for {@link PairedIterator}. */
public final class PairedIteratorTest
    extends AbstractIteratorTest<PairedItem<String, Integer>> {

    public PairedIteratorTest() { super(ObjectArrayIteratorTest.class.getSimpleName()); }

    private ArrayList<String> smallStringsList;
    private ArrayList<String> largeStringsList;
    private ArrayList<Integer> smallIntsList;
    private ArrayList<Integer> largeIntsList;

    private static final int SMALL_LIST_SIZE = 20;
    private static final int LARGE_LIST_SIZE = 40;


    @BeforeEach
    public void setUp() throws Exception {

        smallStringsList = new ArrayList<>();
        largeStringsList = new ArrayList<>();
        smallIntsList = new ArrayList<>();
        largeIntsList = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < SMALL_LIST_SIZE; i++) {
            smallIntsList.add(random.nextInt());
            smallStringsList.add(UUID.randomUUID().toString());
        }

        for (int i = 0; i < LARGE_LIST_SIZE; i++) {
            largeIntsList.add(random.nextInt());
            largeStringsList.add(UUID.randomUUID().toString());
        }
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Override
    public Iterator<PairedItem<String, Integer>> makeEmptyIterator() {
        return PairedIterator.of(IteratorUtils.emptyIterator(), IteratorUtils.emptyIterator());
    }

    @Override
    public Iterator<PairedItem<String, Integer>> makeObject() {
        return PairedIterator.of(smallStringsList.iterator(), smallIntsList.iterator());
    }

    @Test
    public void testLeftIteratorLargerThanRight() {
        Iterator<PairedItem<String, Integer>> zipPairIterator =
            PairedIterator.ofIterables(largeStringsList, smallIntsList);


        for (int i = 0; i < SMALL_LIST_SIZE; i++) {
            assertTrue(zipPairIterator.hasNext());
            PairedItem<String, Integer> zippedItem = zipPairIterator.next();

            assertEquals(largeStringsList.get(i), zippedItem.getLeftItem());
            assertEquals(smallIntsList.get(i), zippedItem.getRightItem());
        }

        assertFalse(zipPairIterator.hasNext());
    }

    @Test
    public void testRightIteratorLargerThanLeft() {
        Iterator<PairedItem<String, Integer>> zipPairIterator =
            PairedIterator.ofIterables(smallStringsList, largeIntsList);


        for (int i = 0; i < SMALL_LIST_SIZE; i++) {
            assertTrue(zipPairIterator.hasNext());
            PairedItem<String, Integer> zippedItem = zipPairIterator.next();

            assertEquals(smallStringsList.get(i), zippedItem.getLeftItem());
            assertEquals(largeIntsList.get(i), zippedItem.getRightItem());
        }

        assertFalse(zipPairIterator.hasNext());
    }

    @Test
    public void testEmptyLeftIterator() {
        Iterator<PairedItem<String, Integer>> zipPairIterator =
            PairedIterator.of(IteratorUtils.emptyIterator(), largeIntsList.iterator());

        assertFalse(zipPairIterator.hasNext());
    }

    @Test
    public void testEmptyRightIterator() {
        Iterator<PairedItem<String, Integer>> zipPairIterator =
            PairedIterator.of(largeStringsList.iterator(), IteratorUtils.emptyIterator());

        assertFalse(zipPairIterator.hasNext());
    }

    @Test
    public void testValidTupleString() {
        Iterator<PairedItem<String, Integer>> zipPairIterator =
            PairedIterator.ofIterables(smallStringsList, largeIntsList);


        for (int i = 0; i < SMALL_LIST_SIZE; i++) {
            assertTrue(zipPairIterator.hasNext());
            PairedItem<String, Integer> zippedItem = zipPairIterator.next();

            assertEquals(
                String.format("{%s, %s}", zippedItem.getLeftItem(), zippedItem.getRightItem()),
                zippedItem.toString());
        }

        assertFalse(zipPairIterator.hasNext());
    }
}
