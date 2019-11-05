/*
 * Copyright 2016 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;


/**
 *
 * @author Radek Smogura
 */
public class CopyOnWriteListTest  {
    List<Integer> original;
    List<Integer> expectedList = new ArrayList<Integer>();
    CopyOnWriteList<Integer> assertedList;

    ListIterator<Integer> expected;
    ListIterator<Integer> asserted;

    protected void assertOriginalList() {
        assertEquals(3, original.size());
        assertEquals(1, original.get(0).intValue());
        assertEquals(2, original.get(1).intValue());
        assertEquals(3, original.get(2).intValue());
    }

    protected void openIterators() {
        expected = expectedList.listIterator();
        asserted = assertedList.listIterator();
    }

    protected void checkIterators(ListIterator<Integer> expected, ListIterator<Integer> asserted) {
        while (expected.hasNext()) {
            assertEquals(expected.hasNext(), asserted.hasNext());
            assertEquals(expected.nextIndex(), asserted.nextIndex());

            assertEquals(expected.previousIndex(), asserted.previousIndex());
            assertEquals(expected.hasPrevious(), asserted.hasPrevious());

            assertEquals(expected.next(), asserted.next());
        }

        //And back
        while (expected.hasPrevious()) {
            assertEquals(expected.hasNext(), asserted.hasNext());
            assertEquals(expected.nextIndex(), asserted.nextIndex());

            assertEquals(expected.previousIndex(), asserted.previousIndex());
            assertEquals(expected.hasPrevious(), asserted.hasPrevious());

            assertEquals(expected.previous(), asserted.previous());
        }
    }

    protected void check() {
        // Checks used iterators
        checkIterators(expected, asserted);

        //And check listIteratorsWithIndex()
        for (int i=0; i < expectedList.size(); i++) {
            checkIterators(expectedList.listIterator(i), assertedList.listIterator(i));
        }

        for (int i=0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), assertedList.get(i));
        }
    }

    protected <P, T> void doOnTasks(P param, Task<P, T> task, T... targets) {
        for (T t : targets) {
            task.execute(param, t);
        }
    }

    @Before
    public void setup() {
        original = new ArrayList<Integer>();

        original.add(1);
        original.add(2);
        original.add(3);

        expectedList = new ArrayList<Integer>(original);
        assertedList = new CopyOnWriteList<Integer>(original);
    }

    @Test
    public void testNoCopy() {
        for (Integer i : assertedList) {
            i.hashCode();
        }
        assertSame(original, assertedList.decorated());
    }

    /// Plain operations
    @Test
    public void testAdd() {
        Task<Integer, List<Integer>> t = new AddElementTask();
        doOnTasks(1, t, expectedList, assertedList);
        openIterators();
        check();
        assertOriginalList();
        assertNotSame(original, assertedList.decorated());
        assertEquals(original.size() + 1, assertedList.size());
    }

    @Test
    public void testInsertIndexed() {
        Task<Integer, List<Integer>> t = new InsertElementTask();
        for (int i=0; i < original.size(); i++) {
            setup();
            doOnTasks(i, t, expectedList, assertedList);
            openIterators();
            check();
            assertOriginalList();
            assertNotSame(original, assertedList.decorated());
            assertEquals(original.size() + 1, assertedList.size());
        }
    }

    @Test
    public void testAddAll() {
        Task<Integer, List<Integer>> t = new AddAllElementTask();
        doOnTasks(1, t, expectedList, assertedList);
        openIterators();
        check();
        assertOriginalList();
        assertNotSame(original, assertedList.decorated());
        assertEquals(original.size() + 2, assertedList.size());
    }

    @Test
    public void testInsertAllIndexed() {
        Task<Integer, List<Integer>> t = new InsertAllElementTask();
        for (int i=0; i < original.size(); i++) {
            setup();
            doOnTasks(i, t, expectedList, assertedList);
            openIterators();
            check();
            assertOriginalList();
            assertNotSame(original, assertedList.decorated());
            assertEquals(original.size() + 2, assertedList.size());
        }
    }

    @Test
    public void testRemoveIndexed() {
        Task<Integer, List<Integer>> t = new RemoveIndexedElementTask();
        for (int i=0; i < original.size(); i++) {
            setup();
            doOnTasks(i, t, expectedList, assertedList);
            openIterators();
            check();
            assertOriginalList();
            assertNotSame(original, assertedList.decorated());
            assertEquals(original.size() - 1, assertedList.size());
        }
    }

    @Test
    public void testRemoveObj() {
        Task<Integer, List<Integer>> t = new RemoveObjElementTask();
        for (int i=0; i < original.size(); i++) {
            setup();
            doOnTasks(i+1, t, expectedList, assertedList);
            openIterators();
            check();
            assertOriginalList();
            assertNotSame(original, assertedList.decorated());
            assertEquals(original.size() - 1, assertedList.size());
        }
    }

    @Test
    public void testRemoveAll() {
        Task<Integer, List<Integer>> t = new RemoveAllElementTask();
        for (int i=0; i < original.size(); i++) {
            setup();
            doOnTasks(i+1, t, expectedList, assertedList);
            openIterators();
            check();
            assertOriginalList();
            assertNotSame(original, assertedList.decorated());
        }
    }

    @Test
    public void testRetainAll() {
        Task<Integer, List<Integer>> t = new RetainAllElementTask();
        for (int i=0; i < original.size(); i++) {
            setup();
            doOnTasks(i+1, t, expectedList, assertedList);
            openIterators();
            check();
            assertOriginalList();
            assertNotSame(original, assertedList.decorated());
        }
    }

    @Test
    public void testSet() {
        Task<Integer, List<Integer>> t = new SetElementTask();
        doOnTasks(1, t, expectedList, assertedList);
        openIterators();
        check();
        assertOriginalList();
        assertNotSame(original, assertedList.decorated());
        assertEquals(original.size(), assertedList.size());
    }

    @Test
    public void testSubList() {
        List subList = assertedList.subList(0,assertedList.size());
        assertEquals(subList,assertedList);
        subList = assertedList.subList(0,0);
        assertEquals(0, subList.size());
        int i = 3;
        if(i > assertedList.size())
        {
            i = assertedList.size();
        }
        subList = assertedList.subList(0,i);
        assertEquals(i, subList.size());
    }

    /**
     * Test sorting a copy on write list.
     */
    @Test
    public void testListSort() {
        final Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        };

       assertedList.sort(comparator);

    }

    /// Iterators
    @Test
    public void testIteratorRemoveIdxElement() {
        for (int i=0; i < original.size(); i++) {
            setup();
            openIterators();
            Task<Integer, ListIterator<Integer>> t = new RemoveIteratorElementTask();
            doOnTasks(i + 1, t, expected, asserted);
            check();
            assertOriginalList();
            assertEquals(original.size() - 1, assertedList.size());
        }
    }

    @Test
    public void testIteratorSetElement() {
        for (int i=0; i < original.size(); i++) {
            setup();
            openIterators();
            Task<Integer, ListIterator<Integer>> t = new SetIteratorElementTask();
            doOnTasks(i + 1, t, expected, asserted);
            check();
            assertOriginalList();
        }
    }

    @Test
    public void testIteratorAddElement() {
        for (int i=0; i < original.size(); i++) {
            setup();
            openIterators();
            Task<Integer, ListIterator<Integer>> t = new AddIteratorElementTask();
            doOnTasks(i + 1, t, expected, asserted);
            check();
            assertOriginalList();
            assertEquals(original.size() + 1, assertedList.size());
        }
    }

    //// Misc tests
    @Test
    public void testConcurrentModification() {
        Iterator<Integer> i = assertedList.iterator();
        assertedList.remove(2);
        try {
            i.next();
            fail("Expected ConcurrentModificationException");
        }catch(ConcurrentModificationException ignored) {

        }
    }
    @Test
    public void testClear() {
        Task<Integer, List<Integer>> t = new ClearElementsTask();
        doOnTasks(0, t, expectedList, assertedList);
        openIterators();
        check();
        assertOriginalList();
        assertEquals(0, assertedList.size());
    }

    private class AddElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.add(10);
        }
    };

    private class InsertElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.add(param, 11);
        }
    };

    private class AddAllElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.addAll(Arrays.asList(14, 15));
        }
    };

    private class InsertAllElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.addAll(param, Arrays.asList(14, 15));
        }
    };

    private class RemoveIndexedElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.remove(param.intValue());
        }
    };

    private class RemoveObjElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.remove(param);
        }
    };

    private class RemoveAllElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.removeAll(Arrays.asList(param, param + 1));
        }
    };

    private class RetainAllElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.retainAll(Arrays.asList(param, param + 1));
        }
    };

    private class SetElementTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> integerList) {
            integerList.set(param,10);
        }
    };

    private class SetIteratorElementTask implements Task<Integer, ListIterator<Integer>> {
        @Override
        public void execute(Integer param, ListIterator<Integer> i) {
            for (int j=0; j < param; j++) i.next();
            i.set(10);
        }
    };
    private class RemoveIteratorElementTask implements Task<Integer, ListIterator<Integer>> {
        @Override
        public void execute(Integer param, ListIterator<Integer> i) {
            for (int j=0; j < param; j++) i.next();
            i.remove();
        }
    };
    private class AddIteratorElementTask implements Task<Integer, ListIterator<Integer>> {
        @Override
        public void execute(Integer param, ListIterator<Integer> i) {
            for (int j=0; j < param; j++) i.next();
            i.add(11);
        }
    };
    private class ClearElementsTask implements Task<Integer, List<Integer>> {
        @Override
        public void execute(Integer param, List<Integer> i) {
            i.clear();
        }
    };

    private interface Task<P, T> {
        void execute(P param, T i);
    }
}