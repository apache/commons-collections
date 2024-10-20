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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExtendedIteratorTest {
    /**
     * Collection of {@link Integer}s
     */
    private List<Integer> collectionA;

    @BeforeEach
    public void setUp() {
        collectionA = new ArrayList<>();
        collectionA.add(1);
        collectionA.add(2);
        collectionA.add(3);
        collectionA.add(4);
        collectionA.add(5);
        collectionA.add(6);
    }

    @Test
    public void testCreateNoRemove() {
        Iterator<Integer> iter = ExtendedIterator.createNoRemove(collectionA.iterator());
        assertThrows(UnsupportedOperationException.class, iter::remove);
    }

    @Test
    public void testCreateWithStream() {
        Iterator<Integer> iter = ExtendedIterator.create(collectionA.stream());
        assertThrows(UnsupportedOperationException.class, iter::remove);
        List<Integer> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(collectionA, actual);
    }

    @Test
    public void testFlatten() {
        Iterator<Iterator<Integer>> iteratorIterator = Arrays.asList(
                Arrays.asList(1, 2, 3).iterator(),
                Arrays.asList(4, 5, 6).iterator()
        ).iterator();
        Iterator<Integer>  iter = ExtendedIterator.flatten(iteratorIterator);
        List<Integer> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(collectionA, actual);
    }

    @Test
    public void testEmptyIterator() {
        assertFalse(ExtendedIterator.emptyIterator().hasNext());
    }

    @Test
    public void testCreate() {
        Iterator<Integer> iter = ExtendedIterator.create(collectionA.iterator());
        List<Integer> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(collectionA, actual);
    }

    @Test
    public void testRemove() {
        Iterator<Integer> iter = ExtendedIterator.create(collectionA.iterator());
        Integer i = iter.next();
        iter.remove();
        assertFalse(collectionA.contains(i));
        List<Integer> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(collectionA, actual);
    }

    @Test
    public void testRemoveNext() {
        ExtendedIterator<Integer> iter = ExtendedIterator.create(collectionA.iterator());
        Integer i = iter.removeNext();
        assertFalse(collectionA.contains(i));
        List<Integer> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(collectionA, actual);
    }

    @Test
    public void testAndThen() {
        Iterator<Integer> iter1 = Arrays.asList(1, 2, 3).iterator();
        Iterator<Integer> iter2 = Arrays.asList(4, 5, 6).iterator();

        ExtendedIterator<Integer> underTest = ExtendedIterator.create(iter1).andThen(iter2);
        List<Integer> actual = new ArrayList<>();
        underTest.forEachRemaining(actual::add);
        assertEquals(collectionA, actual);
    }

    @Test
    public void testFilter() {
        List<Integer> expected = Arrays.asList(2, 4, 6);
        Predicate<Integer> predicate = i -> i % 2 == 0;
        ExtendedIterator<Integer> underTest = ExtendedIterator.create(collectionA.iterator()).filter(predicate);
        List<Integer> actual = new ArrayList<>();
        underTest.forEachRemaining(actual::add);
        assertEquals(expected, actual);
    }

    @Test
    public void testMap() {
        List<Double> expected = Arrays.asList(0.5, 1., 1.5, 2.0, 2.5, 3.0);
        Function<Integer, Double> function = i -> i / 2.0;
        ExtendedIterator<Double> underTest = ExtendedIterator.create(collectionA.iterator()).map(function);
        List<Double> actual = new ArrayList<>();
        underTest.forEachRemaining(actual::add);
        assertEquals(expected, actual);
    }

    @Test
    public void testAddTo() {
        List<Integer> expected = new ArrayList<>(collectionA);
        expected.addAll(collectionA);
        List<Integer> actual = ExtendedIterator.create(collectionA.iterator()).addTo(new ArrayList<>(collectionA));
        assertEquals(expected, actual);
    }
}
