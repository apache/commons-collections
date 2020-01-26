package org.apache.commons.collections4.list;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class IndexedTreeListRandomizedTest {

    private Random random;
    private Set<Long> elementsSet;
    private List<Long> elementsList;
    private List<Long> removedList;

    private IndexedTreeList<Long> testList;

    private int seed;
    private int iterations;

    public IndexedTreeListRandomizedTest(int seed, int iterations) {
        this.seed = seed;
        this.iterations = iterations;
    }

    @Before
    public void setUp() throws Exception {
        random = new Random(seed);
        elementsSet = new HashSet<>();
        elementsList = new ArrayList<>();
        removedList = new ArrayList<>();
        testList = new IndexedTreeList<>();
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
            testList.add(addRandom());
            assertReference();
        }
    }

    @Test
    public void addToHead() throws Exception {
        for (int i = 0; i < iterations; i++) {
            testList.add(0, addRandom(0));
            assertReference();
        }
    }

    @Test
    public void addToMiddle() throws Exception {
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size() + 1);
            testList.add(index, addRandom(index));
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
            testList.set(index, newValue);
            assertReference();
        }
    }

    @Test
    public void setExistingValue() throws Exception {
        init();
        final List<Integer> indexes = IntStream.range(0, iterations)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());
        Collections.shuffle(indexes, random);
        for (int i = 0; i < indexes.size() - 1; i += 2) {
            int indexFrom = indexes.get(i);
            int indexTo = indexes.get(i + 1);
            final Long value = elementsList.get(indexFrom);
            elementsSet.remove(elementsList.get(indexTo));
            elementsList.set(indexTo, value);
            testList.set(indexTo, value);
            assertReference();
        }
    }

    @Test
    public void removeByIndex() throws Exception {
        init();
        while (!testList.isEmpty()) {
            int index = removeRandomIndex();
            testList.remove(index);
            assertReference();
        }
    }

    @Test
    public void removeByValue() throws Exception {
        init();
        while (!testList.isEmpty()) {
            Long value = removeRandomValue();
            assertTrue(testList.contains(value));
            testList.remove(value);
            assertFalse(testList.contains(value));
            assertReference();
        }
    }

    @Test
    public void removeFirstLast() throws Exception {
        init();
        while (!testList.isEmpty()) {
            removeByIndex(0);
            testList.remove(0);
            if (testList.size() > 0) {
                removeByIndex(testList.size() - 1);
                testList.remove(testList.size() - 1);
            }
            assertReference();
        }
    }

    @Test
    public void removeMiddle() throws Exception {
        init();
        while (!testList.isEmpty()) {
            removeByIndex(testList.size() / 2);
            testList.remove(testList.size() / 2);
            assertReference();
        }
    }

    @Test
    public void contains() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            assertTrue(testList.contains(getRandomExisting()));
            assertFalse(testList.contains(getRandomNotExisting()));
        }
        assertReference();
    }

    @Test
    public void get() throws Exception {
        init();
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size());
            Long value = elementsList.get(index);
            assertEquals(value, testList.get(index));
        }
        assertReference();
    }

    @Test
    public void indexOf() throws Exception {
        init();
        int initialSize = elementsList.size();
        for (int i = 0; i < initialSize; i++) {
            elementsList.add(elementsList.get(i));
            testList.add(elementsList.get(i));
        }
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(initialSize);
            Long value = elementsList.get(index);
            assertEquals(index, testList.indexOf(value));
        }
        assertReference();
    }

    @Test
    public void lastIndexOf() throws Exception {
        init();
        int initialSize = elementsList.size();
        for (int i = 0; i < initialSize; i++) {
            elementsList.add(elementsList.get(i));
            testList.add(elementsList.get(i));
        }
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(initialSize);
            Long value = elementsList.get(index);
            assertEquals(index + initialSize, testList.lastIndexOf(value));
        }
        assertReference();
    }

    @Test
    public void indexes() throws Exception {
        init();
        int initialSize = elementsList.size();
        for (int i = 0; i < initialSize; i++) {
            elementsList.add(elementsList.get(i));
            testList.add(elementsList.get(i));
        }
        for (int i = 0; i < initialSize; i++) {
            elementsList.add(elementsList.get(i));
            testList.add(i, elementsList.get(i));
        }
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(initialSize);
            Long value = elementsList.get(index);
            assertArrayEquals(new int[] {index, index + initialSize, index + initialSize * 2}, testList.indexes(value));
            assertEquals(3, testList.count(value));
        }
        assertReference();
    }

    @Test
    public void indexesWithRemove() throws Exception {
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size() + 1);
            testList.add(index, addRandom(index));

            index = random.nextInt(elementsList.size() + 1);
            Long existingValue = getRandomExisting();
            testList.add(index, existingValue);
            elementsList.add(index, existingValue);

            index = random.nextInt(elementsList.size());
            existingValue = getRandomExisting();
            testList.set(index, existingValue);
            elementsList.set(index, existingValue);

            index = random.nextInt(elementsList.size());
            testList.remove(index);
            elementsList.remove(index);
        }

        assertEquals(elementsList, testList);

        Map<Long, List<Integer>> indexes = new HashMap<>();
        for (int i = 0; i < elementsList.size(); i++) {
            indexes.computeIfAbsent(elementsList.get(i), k -> new ArrayList()).add(i);
        }

        assertEquals(indexes.keySet(), testList.nodeMap.keySet());

        for (Entry<Long, List<Integer>> entry : indexes.entrySet()) {
            assertArrayEquals(entry.getValue().stream().mapToInt(i->i).toArray(),
                    testList.indexes(entry.getKey()));
            assertEquals(entry.getValue().size(), testList.count(entry.getKey()));
        }

        assertArrayEquals(elementsList.toArray(), testList.toArray());
        assertArrayEquals(elementsList.toArray(new Long[0]), testList.toArray(new Long[0]));
    }

    @Test
    public void addRandomExisting() throws Exception {
        for (int i = 0; i < iterations; i++) {
            testList.add(addRandom());
            Long randomExisting = getRandomExisting();
            testList.add(randomExisting);
            elementsList.add(randomExisting);
            assertReference();
        }

        assertArrayEquals(elementsList.toArray(), testList.toArray());
        assertArrayEquals(elementsList.toArray(new Long[0]), testList.toArray(new Long[0]));
    }

    @Test
    public void addSameExisting() throws Exception {
        for (int i = 0; i < iterations; i++) {
            testList.add(addRandom());
            testList.add(elementsList.get(0));
            elementsList.add(elementsList.get(0));
            assertReference();
        }

        assertArrayEquals(elementsList.toArray(), testList.toArray());
        assertArrayEquals(elementsList.toArray(new Long[0]), testList.toArray(new Long[0]));
    }

    @Test
    public void addContainsIndexOfRemove() throws Exception {
        init();
        while (!testList.isEmpty()) {
            // add
            int index = random.nextInt(elementsList.size() + 1);
            testList.add(index, addRandom(index));

            // contains
            assertTrue(testList.contains(getRandomExisting()));
            assertFalse(testList.contains(getRandomNotExisting()));

            // indexOf
            index = random.nextInt(elementsList.size());
            Long value = elementsList.get(index);
            assertEquals(value, testList.get(index));
            assertEquals(index, testList.indexOf(value));

            // remove
            final Long valueToRemove = removeRandomValue();
            assertTrue(testList.remove(valueToRemove));
            final int indexToRemove = removeRandomIndex();
            final Long removedValue = testList.get(indexToRemove);
            assertEquals(removedValue, testList.remove(indexToRemove));

            // check add nulls
            try {
                testList.add(null);
                fail("No exception on adding null");
            } catch (NullPointerException e) {}
            try {
                testList.add(random.nextInt(elementsList.size() + 1), null);
                fail("No exception on adding null");
            } catch (NullPointerException e) {}
            // check not existing indexOf
            assertEquals(-1, testList.indexOf(random.nextLong()));
            assertEquals(-1, testList.lastIndexOf(random.nextLong()));
            assertEquals(0, testList.indexes(random.nextLong()).length);
            assertEquals(0, testList.count(random.nextLong()));
            assertFalse(testList.remove(random.nextLong()));

            assertReference();
        }
    }

    @Test
    public void clear() {
        init();
        testList.clear();
        elementsList.clear();
        elementsSet.clear();
        assertReference();
    }

    @Test
    public void constructorWithCollection() {
        init();
        testList = new IndexedTreeList<>(elementsList);
        assertReference();
    }

    @Test
    public void constructorWithTreeMap() {
        init();
        testList = new IndexedTreeList<>(elementsList, new TreeMap());
        assertReference();
    }

    private void init() {
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(elementsList.size() + 1);
            testList.add(index, addRandom(index)); // can be optimized
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
        assertEquals(elementsList, testList);
        assertEquals(elementsSet, testList.uniqueValues());
        testList.assertConsistent();
    }
}