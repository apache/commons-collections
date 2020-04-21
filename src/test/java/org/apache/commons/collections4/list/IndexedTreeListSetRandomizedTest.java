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
package org.apache.commons.collections4.list;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class IndexedTreeListSetRandomizedTest {

    private Random random;
    private Set<Long> elementsSet;
    private List<Long> elementsList;
    private List<Long> removedList;

    private IndexedTreeListSet<Long> testListSet;

    private int seed;
    private int iterations;

    public IndexedTreeListSetRandomizedTest(int seed, int iterations) {
        this.seed = seed;
        this.iterations = iterations;
    }

    @Before
    public void setUp() throws Exception {
        random = new Random(seed);
        elementsSet = new HashSet<>();
        elementsList = new ArrayList<>();
        removedList = new ArrayList<>();
        testListSet = new IndexedTreeListSet<>();
    }

    @Parameterized.Parameters(name = "{0} {1}")
    public static Collection parameters() {
        return Arrays.asList(new Object[][] {
                {9999, 1},
                {9999, 2},
                {9999, 3},
                {9999, 4},
                {9999, 5},
                {9999, 10},
                {9999, 100},
                {9999, 1000},
//                {9999, 10000},
        });
    }

//    @Parameterized.Parameters(name = "{0} {1}")
//    public static Collection parameters() {
//        ArrayList params = new ArrayList();
//        Random r = new Random();
//        for (int i = 0; i < 1000; i++) {
//            params.add(new Object[] {r.nextInt(), r.nextInt(1 << (r.nextInt(12))) + 1});
//        }
//        return params;
//    }

    @Test
    public void addToTail() throws Exception {
        for (int i = 0; i < iterations; i++) {
            testListSet.add(addRandom());
            assertReference();
        }
    }

    @Test
    public void addToHead() throws Exception {
        for (int i = 0; i < iterations; i++) {
            testListSet.add(0, addRandom(0));
            assertReference();
        }
    }

    @Test
    public void addToMiddle() throws Exception {
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size() + 1);
            testListSet.add(index, addRandom(index));
            assertReference();
        }
    }

    @Test
    public void setNewValue() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size());
            final Long value = elementsList.get(index);
            elementsSet.remove(value);
            final long newValue = random.nextLong();
            elementsSet.add(newValue);
            elementsList.set(index, newValue);
            final Long oldValue = testListSet.set(index, newValue);
            assertEquals(value, oldValue);
            assertReference();
        }
    }

    @Test
    public void removeElementOnSetExistingValue() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            int indexFrom = random.nextInt(elementsList.size());
            int indexTo = random.nextInt(elementsList.size());
            final Long value = elementsList.get(indexFrom);
            final Long oldValue = testListSet.set(indexTo, value);
            assertEquals(elementsList.get(indexTo), oldValue);
            if (indexFrom != indexTo) {
                elementsSet.remove(elementsList.get(indexTo));
                elementsList.set(indexTo, elementsList.get(indexFrom));
                elementsList.remove(indexFrom);
            }
            assertReference();
        }
    }

    @Test
    public void removeByIndex() throws Exception {
        init();
        while (!testListSet.isEmpty()) {
            int index = removeRandomIndex();
            testListSet.remove(index);
            assertReference();
        }
    }

    @Test
    public void removeByValue() throws Exception {
        init();
        while (!testListSet.isEmpty()) {
            Long value = removeRandomValue();
            assertTrue(testListSet.contains(value));
            testListSet.remove(value);
            assertFalse(testListSet.contains(value));
            assertReference();
        }
    }

    @Test
    public void removeFirstLast() throws Exception {
        init();
        while (!testListSet.isEmpty()) {
            removeByIndex(0);
            testListSet.remove(0);
            if (testListSet.size() > 0) {
                removeByIndex(testListSet.size() - 1);
                testListSet.remove(testListSet.size() - 1);
            }
            assertReference();
        }
    }

    @Test
    public void contains() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            assertTrue(testListSet.contains(getRandomExisting()));
            assertFalse(testListSet.contains(getRandomNotExisting()));
        }
        assertReference();
    }

    @Test
    public void get() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size());
            Long value = elementsList.get(index);
            assertEquals(value, testListSet.get(index));
        }
        assertReference();
    }

    @Test
    public void indexOf() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size());
            Long value = elementsList.get(index);
            assertEquals(index, testListSet.indexOf(value));
        }
        assertReference();
    }

    @Test
    public void lastIndexOf() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size());
            Long value = elementsList.get(index);
            assertEquals(index, testListSet.lastIndexOf(value));
        }
        assertReference();
    }

    @Test
    public void addExisting() throws Exception {
        for (int i = 0; i < iterations; i++) {
            testListSet.add(addRandom());
            testListSet.add(getRandomExisting());
            testListSet.add(random.nextInt(elementsList.size() + 1), getRandomExisting());
            assertReference();
        }
    }

    @Test
    public void addContainsIndexOfRemove() throws Exception {
        init();
        while (!testListSet.isEmpty()) {
            // add
            int index = random.nextInt(elementsList.size() + 1);
            testListSet.add(index, addRandom(index));

            // contains
            assertTrue(testListSet.contains(getRandomExisting()));
            assertFalse(testListSet.contains(getRandomNotExisting()));

            // indexOf
            index = random.nextInt(elementsList.size());
            Long value = elementsList.get(index);
            assertEquals(value, testListSet.get(index));
            assertEquals(index, testListSet.indexOf(value));

            // remove
            final Long valueToRemove = removeRandomValue();
            assertTrue(testListSet.remove(valueToRemove));
            final int indexToRemove = removeRandomIndex();
            final Long removedValue = testListSet.get(indexToRemove);
            assertEquals(removedValue, testListSet.remove(indexToRemove));

            // check add nulls
            try {
                testListSet.add(null);
                fail("No exception on adding null");
            } catch (NullPointerException e) {}
            try {
                testListSet.add(random.nextInt(elementsList.size() + 1), null);
                fail("No exception on adding null");
            } catch (NullPointerException e) {}
            // check not existing indexOf
            assertEquals(-1, testListSet.indexOf(random.nextLong()));
            assertEquals(-1, testListSet.lastIndexOf(random.nextLong()));
            assertFalse(testListSet.remove(random.nextLong()));


            assertReference();
        }
    }

    @Test
    public void toArray() {
        init();

        for (int i = 0; i < iterations; i++) {
            testListSet.add(addRandom());
            testListSet.remove(removeRandomValue());
            assertReference();
        }

        assertArrayEquals(elementsList.toArray(), testListSet.toArray());
        assertArrayEquals(elementsList.toArray(new Long[0]), testListSet.toArray(new Long[0]));
    }

    @Test
    public void clear() {
        init();
        testListSet.clear();
        elementsList.clear();
        elementsSet.clear();
        assertReference();
    }

    @Test
    public void constructorWithColection() {
        init();
        testListSet = new IndexedTreeListSet<>(elementsList);
        assertReference();
    }

    @Test
    public void constructorWithTreeMap() {
        init();
        testListSet = new IndexedTreeListSet<>(elementsList, new TreeMap());
        assertReference();
    }

    private void init() {
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size() + 1);
            testListSet.add(index, addRandom(index)); // can be optimized
        }
        assertReference();
    }

    private Long addRandom() {
        while (true) {
            Long value = random.nextLong();
            if (elementsSet.add(value)) {
                elementsList.add(value);
                return value;
            }
        }
    }

    private Long addRandom(int index) {
        while (true) {
            Long value = random.nextLong();
            if (elementsSet.add(value)) {
                elementsList.add(index, value);
                return value;
            }
        }
    }

    private Long getRandomExisting() {
        if (elementsList.isEmpty()) {
            return null;
        }
        return elementsList.get(random.nextInt(elementsList.size()));
    }

    private Long getRandomNotExisting() {
        while (true) {
            Long value = random.nextLong();
            if (!elementsSet.contains(value)) {
                return value;
            }
        }
    }

    private Long getRandomRemoved() {
        if (removedList.isEmpty()) {
            return null;
        }
        return removedList.get(random.nextInt(removedList.size()));
    }

    private Long removeRandomValue() {
        if (elementsList.isEmpty()) {
            return null;
        }
        int index = random.nextInt(elementsList.size());
        Long value = elementsList.get(index);
        elementsSet.remove(value);
        elementsList.remove(index);
        removedList.add(value);
        return value;
    }

    private int removeRandomIndex() {
        if (elementsList.isEmpty()) {
            return -1;
        }
        int index = random.nextInt(elementsList.size());
        removeByIndex(index);
        return index;
    }

    private void removeByIndex(final int index) {
        Long value = elementsList.get(index);
        elementsSet.remove(value);
        elementsList.remove(index);
        removedList.add(value);
    }

    private void assertReference() {
        assertEquals(elementsSet, testListSet);
        assertEquals(elementsList, testListSet);
        testListSet.assertConsistent();
    }
}