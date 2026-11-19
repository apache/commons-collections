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

import com.github.coderodde.util.IndexedLinkedList.BasicIterator;
import com.github.coderodde.util.IndexedLinkedList.EnhancedIterator;
import com.github.coderodde.util.IndexedLinkedList.Finger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class IndexedLinkedListTest {

    private final IndexedLinkedList<Integer> list = new IndexedLinkedList<>();
    
    @Before
    public void setUp() {
        list.clear();
    }
    
    @Test
    public void addFirstLarge() {
        List<Integer> l = getIntegerList(1000);
        
        for (int i = 0; i < l.size(); i++) {
            list.addFirst(l.get(i));
        }
        
        Collections.reverse(l);
        assertTrue(listsEqual(list, l));
    }
    
    @Test
    public void addAllAtIndexLarge() {
        Random random = new Random(1003L);
        List<Integer> referenceList = new ArrayList<>();
        
        for (int i = 0; i < 100; ++i) {
            int index = random.nextInt(list.size() + 1);
            List<Integer> coll = getIntegerList(random.nextInt(100));
            list.addAll(index, coll);
            referenceList.addAll(index, coll);
        }
        
        assertTrue(listsEqual(list, referenceList));
    }
    
    @Test
    public void constructAdd() {
        List<String> l = new IndexedLinkedList<>(Arrays.asList("a", "b", "c"));
        
        assertEquals(3, l.size());
        
        assertEquals("a", l.get(0));
        assertEquals("b", l.get(1));
        assertEquals("c", l.get(2));
    }

    @Test
    public void contains() {
        assertFalse(list.contains(Integer.valueOf(1)));
        assertFalse(list.contains(Integer.valueOf(2)));
        assertFalse(list.contains(Integer.valueOf(3)));
        
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        
        list.addAll(Arrays.asList(1, 2, 3));
        
        assertEquals(3, list.size());
        assertFalse(list.isEmpty());
        
        assertTrue(list.contains(Integer.valueOf(1)));
        assertTrue(list.contains(Integer.valueOf(2)));
        assertTrue(list.contains(Integer.valueOf(3)));
    }
    
    @Test
    public void descendingIterator() {
        list.addAll(Arrays.asList(1, 2, 3));
        Iterator<Integer> iterator = list.descendingIterator();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void descendingIteratorRemove1() {
        list.addAll(Arrays.asList(1, 2, 3));
        Iterator<Integer> iterator = list.descendingIterator();
        
        iterator.next();
        iterator.remove();
        
        assertEquals(2, list.size());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
        
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void descendingIteratorForEachRemaining() {
        list.addAll(Arrays.asList(1, 2, 3, -1, -2, -3));
        Iterator<Integer> iterator = list.descendingIterator();
        
        assertEquals(Integer.valueOf(-3), iterator.next());
        assertEquals(Integer.valueOf(-2), iterator.next());
        assertEquals(Integer.valueOf(-1), iterator.next());
        
        class LocalConsumer implements Consumer<Integer> {

            final List<Integer> list = new ArrayList<>();
            
            @Override
            public void accept(Integer i) {
                list.add(i);
            }
        }
        
        LocalConsumer consumer = new LocalConsumer();
        iterator.forEachRemaining(consumer);
        
        assertEquals(Integer.valueOf(3), consumer.list.get(0));
        assertEquals(Integer.valueOf(2), consumer.list.get(1));
        assertEquals(Integer.valueOf(1), consumer.list.get(2));
    }
    
    @Test
    public void subListClearOnEmptyPrefix() {
        list.addAll(getIntegerList(100));
        list.get(10);
        list.subList(5, 100).clear();
        list.checkInvarant();
    }
    
    @Test
    public void removeFirstUntilEmpty() {
        list.addAll(getIntegerList(10));
        
        while (!list.isEmpty()) {
            list.removeFirst();
            list.checkInvarant();
        }
        
        list.checkInvarant();
    }
    
    @Test
    public void moveFingerOutOfRemovalLocation() {
        list.addAll(getIntegerList(16));
        list.fingerList.fingerArray[0] =
                new Finger<>(list.last.prev.prev.prev, 12);
        
        list.fingerList.fingerArray[1] = new Finger<>(list.last.prev.prev, 13);
        list.fingerList.fingerArray[2] = new Finger<>(list.last.prev, 14);
        list.fingerList.fingerArray[3] = new Finger<>(list.last, 15);
        
        list.remove(12);
        
        Finger<Integer> finger = list.fingerList.fingerArray[0];
        
        assertEquals(Integer.valueOf(11), finger.node.item);
        assertEquals(11, finger.index);
    }
    
    @Test
    public void removeIf() {
        list.addAll(getIntegerList(10));
        list.removeIf((i) -> {
            return i % 2 == 1;
        });
        
        list.checkInvarant();
        assertEquals(Arrays.asList(0, 2, 4, 6, 8), list);
    }
    
    @Test
    public void descendingIteratorRemove2() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5));
        
        Iterator<Integer> iter = list.descendingIterator();
        
        iter.next();
        iter.remove();
        
        assertEquals(Integer.valueOf(4), iter.next());
        iter.remove();
        
        assertEquals(3, list.size());
        
        assertEquals(Integer.valueOf(3), iter.next());
        iter.remove();
        
        assertEquals(2, list.size());
        
        assertEquals(Integer.valueOf(2), iter.next());
        assertEquals(Integer.valueOf(1), iter.next());
        
        iter.remove();
        
        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(2), list.get(0));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void elementThrowsOnEmptyList() {
        list.element();
    }

    @Test
    public void element() {
        list.add(1);
        list.add(2);
        
        assertEquals(Integer.valueOf(1), list.element());
        
        list.remove();
        
        assertEquals(Integer.valueOf(2), list.element());
    }
    
    @Test
    public void listEquals() {
        list.addAll(Arrays.asList(1, 2, 3, 4));
        List<Integer> otherList = Arrays.asList(1, 2, 3, 4);
        
        assertTrue(list.equals(otherList));
        
        list.remove(Integer.valueOf(3));
        
        assertFalse(list.equals(otherList));
        
        assertFalse(list.equals(null));
        assertTrue(list.equals(list));
        
        Set<Integer> set = new HashSet<>(list);
        
        assertFalse(list.equals(set));
        
        list.clear();
        list.addAll(Arrays.asList(0, 1, 2, 3));
        otherList = Arrays.asList(0, 1, 4, 3);
        
        assertFalse(list.equals(otherList));
    }
    
    class DummyList extends ArrayList<Integer> {
        private final class DummyIterator implements Iterator<Integer> {

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                return Integer.valueOf(0);
            }
        }
        
        public Iterator<Integer> iterator()  {
            return new DummyIterator();
        }
        
        public int size() {
            return 2;
        }
    }
    
    @Test
    public void sublistClear1() {
        list.addAll(getIntegerList(100));
        List<Integer> sublist = list.subList(49, 51);
        assertEquals(2, sublist.size());
        sublist.clear();
        assertEquals(98, list.size());
        assertEquals(0, sublist.size());
    }
    
    @Test
    public void sublistClear2() {
        int fromIndex = 10;
        int toIndex = 990;
        
        List<Integer> referenceList = getIntegerList(1000);
        list.addAll(referenceList);
        referenceList.subList(fromIndex, toIndex).clear();
        list.subList(fromIndex, toIndex).clear();
        list.checkInvarant();
        assertEquals(referenceList, list);
    }
    
    private void checkSubList(int size, int fromIndex, int toIndex) {
        List<Integer> referenceList = getIntegerList(size);
        list.addAll(referenceList);
        referenceList.subList(fromIndex, toIndex).clear();
        list.subList(fromIndex, toIndex).clear();
        list.checkInvarant();
        assertEquals(referenceList, list);
    }
    
    @Test
    public void sublistClear3() {
        int size = 1_000_000;
        int fromIndex = 10;
        int toIndex = 999_990;
        checkSubList(size, fromIndex, toIndex);
    }
    
    @Test
    public void sublistClear4() {
        int size = 1_000;
        int fromIndex = 10;
        int toIndex = 500;
        checkSubList(size, fromIndex, toIndex);
    }
    
    @Test
    public void sublistClear5() {
        int size = 100;
        int fromIndex = 10;
        int toIndex = 90;
        checkSubList(size, fromIndex, toIndex);
    }
    
    @Test
    public void sublistClearLeftOfSmall() {
        list.add(1);
        list.add(2);
        
        list.subList(0, 1).clear();
        
        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(2), list.get(0));
        list.checkInvarant();
    }
    
    @Test
    public void sublistClearRightOfSmall() {
        list.add(1);
        list.add(2);
        
        list.subList(1, 2).clear();
        list.checkInvarant();
        
        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
    }
    
    @Test
    public void sublistClearRightOfSmall2() {
        List<Integer> referenceList = new ArrayList<>(getIntegerList(20));
        list.addAll(referenceList);
        
        list.subList(0, 5).clear();
        list.checkInvarant();
        referenceList.subList(0, 5).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void debugClear1() {
        list.addAll(getIntegerList(12));
        list.subList(4, 9).clear();
        list.checkInvarant();
        assertEquals(Arrays.asList(0, 1, 2, 3, 9, 10, 11), list);
    }
    
    @Test
    public void debugClear2() {
        list.addAll(getIntegerList(10));
        list.subList(0, 4).clear();
        list.checkInvarant();
        assertEquals(Arrays.asList(4, 5, 6, 7, 8, 9), list);
    }
    
    @Test
    public void subListClear2Fingers3Nodes_1() {
        list.addAll(Arrays.asList(1, 2, 3));
        list.subList(0, 1).clear();
        list.checkInvarant();
        assertEquals(Arrays.asList(2, 3), list);
    }
    
    @Test
    public void sublistClear6() {
        list.addAll(getIntegerList(1000));
        list.subList(70, 1000).clear();
    }
    
    @Test
    public void bruteForceSublistClearOnSmallLists() {
        Random random = new Random(26L);
        
        for (int i = 0; i < 200; ++i) {
            int size = 1 + random.nextInt(15);
            List<Integer> referenceList = new ArrayList<>(getIntegerList(size));
            list.clear();
            list.addAll(referenceList); 
            
            int fromIndex = random.nextInt(size);
            int toIndex = Math.min(size, fromIndex + random.nextInt(size));
            
            list.subList(fromIndex, toIndex).clear();
            referenceList.subList(fromIndex, toIndex).clear();
            
            list.checkInvarant();
            assertEquals(referenceList, list);
        }
    }
    
    @Test
    public void optmize() {
        list.addAll(getIntegerList(100));
        Random random = new Random(100L);
        
        for (int i = 0; i < 50; ++i) {
            list.get(random.nextInt(list.size()));
        }
        
        list.checkInvarant();
        list.optimize();
        list.checkInvarant();
    }
    
    @Test
    public void removeFromRange() {
        list.addAll(getIntegerList(10));
        List<Integer> referenceList = new ArrayList<>(list);
    
        List<Integer> subList1 = list.subList(1, 9);
        List<Integer> subList2 = referenceList.subList(1, 9);
        
        assertEquals(subList2, subList1);
        
        // Remove from ArrayList:
        subList2.remove(Integer.valueOf(0));
        
        // Remove from IndexedLinkedList:
        subList1.remove(Integer.valueOf(0));
        
        assertEquals(subList2, subList1);
        
        assertEquals(8, subList1.size());
        assertEquals(10, list.size());
        
        subList1.remove(Integer.valueOf(5));
        subList2.remove(Integer.valueOf(5));
        
        assertEquals(subList2, subList1);
        
        assertEquals(7, subList1.size());
        assertEquals(9, list.size());
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void sort() {
        Random random = new Random(1L);
        
        for (int i = 0; i < 100; ++i) {
            list.add(random.nextInt(70));
        }
        
        List<Integer> referenceList = new ArrayList<>(list);
        
        Comparator<Integer> comp = (i1, i2) -> {
            return Integer.compare(i1, i2);
        };
        
        list.checkInvarant();
        list.sort(comp);
        list.checkInvarant();
        
        referenceList.sort(comp);
        assertEquals(referenceList, list);
    }
    
    @Test
    public void sortSubLists() {
        Random random = new Random(12L);
        
        for (int i = 0; i < 10; ++i) {
            list.clear();
            list.addAll(getIntegerList(500));
            Collections.shuffle(list, random);
            List<Integer> referenceList = new ArrayList<>(list);
            
            int f = random.nextInt(list.size() + 1);
            int t = random.nextInt(list.size() - 1);
            
            int fromIndex = Math.min(f, t);
            int toIndex = Math.max(f, t);
            
            Comparator<Integer> cmp = Integer::compare;
            
            list.subList(fromIndex, toIndex).sort(cmp);
            referenceList.subList(fromIndex, toIndex).sort(cmp);
            
            assertEquals(referenceList, list);
        }
    }
    
    @Test
    public void sortSubListOfSubList() {
        list.addAll(Arrays.asList(4, 1, 0, 2, 6, 8, 4, 1, 3));
        List<Integer> referenceList = new ArrayList<>(list);
        Comparator<Integer> cmp = Integer::compare;
        list.subList(1, 7).subList(1, 4).sort(cmp);
        referenceList.subList(1, 7).subList(1, 4).sort(cmp);
        assertEquals(referenceList, list);
    }
    
    @Test
    public void subListAdd() {
        list.addAll(Arrays.asList(3, 2, 1, 4, 5));
        list.subList(1, 4).add(Integer.valueOf(1000));
        list.checkInvarant();
        assertEquals(Arrays.asList(3, 2, 1, 4, 1000, 5), list);
    }
    
    @Test
    public void subListAddInt() {
        list.addAll(Arrays.asList(3, 2, 1, 4, 5));
        list.subList(1, 4).add(1, Integer.valueOf(1000));
        list.checkInvarant();
        assertEquals(Arrays.asList(3, 2, 1000, 1, 4, 5), list);
    }
    
    @Test
    public void subListAddAll() {
        list.addAll(Arrays.asList(3, 2, 1, 4, 5));
        list.subList(1, 4).addAll(Arrays.asList(10, 11));
        list.checkInvarant();
        assertEquals(Arrays.asList(3, 2, 1, 4, 10, 11, 5), list);
    }
    
    @Test
    public void subListAddAllInt() {
        list.addAll(Arrays.asList(3, 2, 1, 4, 5));
        list.subList(1, 4).addAll(0, Arrays.asList(10, 11));
        list.checkInvarant();
        assertEquals(Arrays.asList(3, 10, 11, 2, 1, 4, 5), list);
    }
    
    @Test
    public void subListContains() {
        list.addAll(Arrays.asList(3, 2, 1, 4, 5, 8, 7));
        
        assertTrue(list.subList(1, 5).contains(2));
        assertTrue(list.subList(1, 5).contains(1));
        assertTrue(list.subList(1, 5).contains(4));
        assertTrue(list.subList(1, 5).contains(5));
        
        assertFalse(list.subList(1, 5).contains(3));
        assertFalse(list.subList(1, 5).contains(8));
        assertFalse(list.subList(1, 5).contains(7));
    }
    
    @Test
    public void subListContainsAll() {
        list.addAll(Arrays.asList(3, 2, 1, 4, 5, 8, 7));
        
        assertTrue(list.subList(1, 5).containsAll(Arrays.asList(2, 4, 1, 5)));
        
        assertFalse(list.subList(1, 5)
                .containsAll(Arrays.asList(2, 4, 1, 5, 3)));
        
        assertFalse(list.subList(1, 5)
                .containsAll(Arrays.asList(2, 4, 1, 5, 8)));
        
        assertFalse(list.subList(1, 5)
                .containsAll(Arrays.asList(2, 4, 1, 5, 7)));
    }
    
    @Test
    public void containsAll() {
        list.addAll(Arrays.asList(4, 1, 8, 7, 5, 6));
        
        assertFalse(list.containsAll(Arrays.asList(8, 7, 3)));
        assertFalse(list.containsAll(Arrays.asList(1, 4, 3)));
        assertFalse(list.containsAll(Arrays.asList(-1)));
        
        list.addAll(Arrays.asList(4, 1, 8, 7, 5, 6));
        
        assertTrue(list.containsAll(Arrays.asList(8, 7)));
        assertTrue(list.containsAll(Arrays.asList()));
        assertTrue(list.containsAll(Arrays.asList(8, 1, 4, 7, 6, 5)));
    }
    
    @Test
    public void hashCode2() {
        List<Integer> referenceList = new ArrayList<>();
        
        list.add(null);
        referenceList.add(null);
        assertEquals(referenceList.hashCode(), list.hashCode());
        
        list.add(1);
        referenceList.add(1);
        assertEquals(referenceList.hashCode(), list.hashCode());
        
        list.add(5);
        referenceList.add(5);
        assertEquals(referenceList.hashCode(), list.hashCode());
        
        list.add(7);
        referenceList.add(7);
        assertEquals(referenceList.hashCode(), list.hashCode());
        
        list.add(null);
        referenceList.add(null);
        assertEquals(referenceList.hashCode(), list.hashCode());
    }
    
    @Test
    public void removeAll() {
        list.addAll(Arrays.asList(4, 1, 8, 9, 5, 1, -1, 5, 2, 3, 0));
        list.removeAll(Arrays.asList(1, -1, 5));
        // list = <4, 8, 9, 2, 3, 0>
        assertEquals(Arrays.asList(4, 8, 9, 2, 3, 0), list);
        
        list.removeAll(Arrays.asList(-2, 8, 0));
        // list = <4, 9, 2, 3>
        assertEquals(Arrays.asList(4, 9, 2, 3), list);
    }
    
    @Test
    public void replaceAll() {
        list.addAll(Arrays.asList(3, 2, 1));
        list.replaceAll((i) -> {
            return i * 3 + 1;
        });
        
        assertEquals(Arrays.asList(10, 7, 4), list);
    }
    
    @Test
    public void retainAll() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        
        list.retainAll(Arrays.asList(2, 3, 5, 7));
        
        assertEquals(Arrays.asList(2, 3, 5), list);
        
        list.retainAll(Arrays.asList(3));
        
        assertEquals(Arrays.asList(3), list);
        
        list.retainAll(Arrays.asList(0));
        
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void toArrayGeneric() {
        list.addAll(Arrays.asList(3, 1, 2, 5, 4));
        
        Integer[] array = new Integer[7];
        
        array[5] = Integer.valueOf(10);
        array[6] = Integer.valueOf(11);
        
        Integer[] cloneArray = list.toArray(array);
        
        assertTrue(cloneArray == array);
        assertNull(cloneArray[5]);
        assertEquals(Integer.valueOf(11), cloneArray[6]);
        
        array = new Integer[3];
        
        cloneArray = list.toArray(array);
        
        assertFalse(cloneArray == array);
        
        assertEquals(5, cloneArray.length);
        
        for (int i = 0; i < cloneArray.length; ++i) {
            assertEquals(list.get(i), cloneArray[i]);
        }
    }
    
    @Test
    public void toString2() {
        list.addAll(Arrays.asList(1, 11, 111));
        assertEquals("[1, 11, 111]", list.toString());
    }
    
    @Test
    public void subListToString() {
        list.addAll(Arrays.asList(0, 2, 22, 222, 0));
        assertEquals("[2, 22, 222]", list.subList(1, 4).toString());
    }
    
    @Test(expected = IllegalStateException.class)
    public void listIteratorSetAddThrows() {
        list.addAll(getIntegerList(10));
        ListIterator<Integer> listIterator = list.listIterator(3);
        
        listIterator.add(100);
        listIterator.set(-100);
    }
    
    @Test(expected = IllegalStateException.class)
    public void subListIteratorSetAddThrows() {
        list.addAll(getIntegerList(10));
        ListIterator<Integer> listIterator = list.subList(4, 9).listIterator(3);
        
        listIterator.add(100);
        listIterator.set(-100);
    }
    
    @Test(expected = IllegalStateException.class)
    public void listIteratorRemoveWithouttNextPreviousThrows() {
        list.addAll(getIntegerList(5));
        ListIterator<Integer> iter = list.listIterator(1);
        iter.remove();
    }
    
    @Test(expected = IllegalStateException.class)
    public void subListIteratorRemoveWithouttNextPreviousThrows() {
        list.addAll(getIntegerList(8));
        List<Integer> subList = list.subList(1, 6);
        ListIterator<Integer> iter = subList.listIterator(5);
        iter.remove();
    }
    
    @Test
    public void listIteratorSetAdd() {
        list.addAll(getIntegerList(5));
        ListIterator<Integer> listIterator = list.listIterator(2);
        // list = <0, 1, 2, 3, 4>
        // iter:       |
        
        listIterator.add(100);
        // list = <0, 1, 100, 2, 3, 4>
        assertEquals(Integer.valueOf(2), listIterator.next());
        listIterator.set(-100);
        // list = <0, 1, 100, -100, 3, 4>
        
        assertEquals(Arrays.asList(0, 1, 100, -100, 3, 4), list);
        // list = <0, 1, 100, -100, 3, 4>
                     
        listIterator = list.listIterator(4);
        // list = <0, 1, 100, -100, 3, 4>
        // iter:                   |
        listIterator.add(1000);
        // list = <0, 1, 100, -100, 1000, 3, 4>
        assertEquals(Arrays.asList(0, 1, 100, -100, 1000, 3, 4), list);
        
        assertEquals(Integer.valueOf(1000), listIterator.previous());
        listIterator.set(-1000);
        // list = <0, 1, 100, -1000, 1000, 3, 4>
        
        assertEquals(
                Arrays.asList(0, 1, 100, -100, -1000, 3, 4), 
                list);
    }
    
    @Test
    public void subListIteratorSetAdd() {
        list.addAll(getIntegerList(8));
        List<Integer> subList = list.subList(1, 6);
        ListIterator<Integer> listIterator = subList.listIterator(2);
        // subList = <1, 2, 3, 4, 5>
        // iter:           |
        
        listIterator.add(100);
        assertEquals(Arrays.asList(1, 2, 100, 3, 4, 5), subList);
        // subList = <1, 2, 100, 3, 4, 5>
        
        assertEquals(Integer.valueOf(3), listIterator.next());
        listIterator.set(-100);
        // subList = <1, 2, 100, -100, 4, 5>
        // iter:                      |
        
        assertEquals(Arrays.asList(1, 2, 100, -100, 4, 5), subList);
        assertEquals(Integer.valueOf(4), listIterator.next()); 
        assertEquals(Integer.valueOf(5), listIterator.next()); 
        
        listIterator.add(1000);

        // list = <1, 2, 100, -100, 4, 5, 1000>
        assertEquals(Arrays.asList(1, 2, 100, -100, 4, 5, 1000), subList);
        
        assertEquals(Integer.valueOf(1000), listIterator.previous());
        listIterator.set(-1000);
        // list = <1, 2, 100, -100, 4, 5, -1000>
        
        assertEquals(Arrays.asList(1, 2, 100, -100, 4, 5, -1000), subList);
    }
    
    @Test(expected = IllegalStateException.class)
    public void listIteratorThrowsOnSetAfterRemove() {
        list.addAll(getIntegerList(8));
        ListIterator<Integer> iterator = list.listIterator(2);
        iterator.previous();
        iterator.remove();
        iterator.set(1000);
    }
    
    @Test(expected = IllegalStateException.class)
    public void subListIteratorThrowsOnSetAfterRemove() {
        list.addAll(getIntegerList(8));
        List<Integer> subList = list.subList(1, 7);
        
        ListIterator<Integer> iterator = subList.listIterator(2);
        iterator.previous();
        iterator.remove();
        iterator.set(1000);
    }
    
    @Test
    public void debugChainResolve() {
        list.addAll(getIntegerList(9_999));
        list.checkInvarant();
        list.subList(5, list.size() - 5).clear();
        list.checkInvarant();
    }
    
    @Test
    public void debugChainResolve2() {
        list.addAll(getIntegerList(9_999));
        list.checkInvarant();
        list.subList(5, list.size()).clear();
        list.checkInvarant();
    }
    
    @Test
    public void debugChainResolve3() {
        list.addAll(getIntegerList(9_999));
        list.checkInvarant();
        list.subList(0, list.size() - 5).clear();
        list.checkInvarant();
    }
    
    @Test
    public void addFingersAfterAppendAll() {
        list.addAll(getIntegerList(9_990));
        assertEquals(100, list.getFingerListSize());
        list.addAll(Arrays.asList(-1, -2));
        assertEquals(100, list.getFingerListSize());
    }
    
    @Test
    public void canUseListAsMapKey() {
        List<Integer> l = new ArrayList<>(Arrays.asList(1, 5, 3));
        list.addAll(l);
        
        Map<List<Integer>, Integer> map = new HashMap<>();
        
        map.put(l, 100);
        
        assertEquals(Integer.valueOf(100), map.get(list));
        
        l = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        list.clear();
        list.addAll(l);
        
        List<Integer> subList1 = l.subList(1, 4);
        List<Integer> subList2 = list.subList(1, 4);
        
        map.put(subList1, Integer.valueOf(200));
        assertEquals(Integer.valueOf(200), map.get(subList2));
    }
    
    @Test
    public void spliteratorOverSubList() {
        list.addAll(getIntegerList(10));
        List<Integer> subList = list.subList(1, 9);
        
        Spliterator<Integer> spliterator = subList.spliterator();
        
        class MyConsumer implements Consumer<Integer> {
            final List<Integer> data = new ArrayList<>();

            @Override
            public void accept(Integer i) {
                data.add(i);
            }
        } 
        
        MyConsumer myConsumer = new MyConsumer();
        
        assertEquals(8L, spliterator.getExactSizeIfKnown());
        assertEquals(8L, spliterator.estimateSize());
        
        spliterator.tryAdvance(myConsumer);
        assertEquals(1, myConsumer.data.size());
        
        spliterator.tryAdvance(myConsumer);
        assertEquals(2, myConsumer.data.size());
        
        spliterator.tryAdvance(myConsumer);
        assertEquals(3, myConsumer.data.size());
        
        spliterator.tryAdvance(myConsumer);
        assertEquals(4, myConsumer.data.size());
        
        assertEquals(Arrays.asList(1, 2, 3, 4), myConsumer.data);
        
        spliterator.forEachRemaining(myConsumer);
        assertEquals(8, myConsumer.data.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), myConsumer.data);
    }
    
    @Test
    public void subListForEach() {
        list.addAll(Arrays.asList(4, 2, 1, 3, 1, 2, 5, 8));
        
        class MyConsumer implements Consumer<Integer> {

            final List<Integer> data = new ArrayList<>();
            
            @Override
            public void accept(Integer t) {
                data.add(t);
            }
        }
        
        MyConsumer myConsumer = new MyConsumer();
        list.subList(1, 6).forEach(myConsumer);
        
        assertEquals(Integer.valueOf(2), myConsumer.data.get(0));
        assertEquals(Integer.valueOf(1), myConsumer.data.get(1));
        assertEquals(Integer.valueOf(3), myConsumer.data.get(2));
        assertEquals(Integer.valueOf(1), myConsumer.data.get(3));
        assertEquals(Integer.valueOf(2), myConsumer.data.get(4));
    }
    
    @Test
    public void subListGet() {
        list.addAll(Arrays.asList(4, 2, 8, 0, 9));
        List<Integer> subList = list.subList(1, 4);
        assertEquals(Integer.valueOf(2), subList.get(0));
        assertEquals(Integer.valueOf(8), subList.get(1));
        assertEquals(Integer.valueOf(0), subList.get(2));
    }
    
    @Test
    public void subListIndexOf() {
        list.addAll(Arrays.asList(5, 1, 9, 10, 2, 3, 7, 6));
        List<Integer> subList = list.subList(2, 6); // <9, 10, 2, 3>
        
        assertEquals(-1, subList.indexOf(5));
        assertEquals(-1, subList.indexOf(1));
        assertEquals(-1, subList.indexOf(7));
        assertEquals(-1, subList.indexOf(6));
        
        assertEquals(0, subList.indexOf(9));
        assertEquals(1, subList.indexOf(10));
        assertEquals(2, subList.indexOf(2));
        assertEquals(3, subList.indexOf(3));
    }
    
    @Test
    public void subListIsEmpty() {
        List<Integer> subList = list.subList(0, 0);
        assertTrue(subList.isEmpty());
        subList.add(1);
        assertFalse(subList.isEmpty());
        subList.remove(0);
        assertTrue(subList.isEmpty());
    }
    
    @Test
    public void subListIterator() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        List<Integer> subList = list.subList(1, 5); // <2, 3, 4, 5>
        Iterator<Integer> iterator = subList.iterator();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        
        iterator.remove();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(4), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(5), iterator.next());
        
        iterator.remove();
        
        assertFalse(iterator.hasNext());
        
        assertEquals(Arrays.asList(3, 4), subList);
        assertEquals(Arrays.asList(1, 3, 4, 6), list);
    }
    
    @Test
    public void subListLastIndex() {
        list.addAll(Arrays.asList(1, 2, 3, 2, 3, 2, 1));
        List<Integer> subList = list.subList(2, 7); // <3, 2, 3, 2, 1>
        
        assertEquals(4, subList.lastIndexOf(1));
        assertEquals(3, subList.lastIndexOf(2));
        assertEquals(2, subList.lastIndexOf(3));
        assertEquals(-1, subList.lastIndexOf(10));
    }
    
    @Test
    public void subListListIterator() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        List<Integer> subList = list.subList(2, 8); // <3, 4, 5, 6, 7, 8>
        
        assertEquals(Arrays.asList(3, 4, 5, 6, 7, 8), subList);
        
        ListIterator<Integer> iterator = subList.listIterator(2);
        
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        
        assertEquals(Integer.valueOf(5), iterator.next());
        
        iterator.remove(); // subList = <3, 4, 6, 7, 8>
        
        assertEquals(Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9), list);
        assertEquals(Arrays.asList(3, 4, 6, 7, 8), subList);
        
        assertEquals(Integer.valueOf(4), iterator.previous());
        
        iterator.remove(); // subList = <3, 6, 7, 8>
        
        assertEquals(Integer.valueOf(6), iterator.next());
        assertEquals(Integer.valueOf(7), iterator.next());
        assertEquals(Integer.valueOf(8), iterator.next());
        
        assertFalse(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
    }
    
    @Test
    public void subListRemoveObject() {
        list.addAll(Arrays.asList(3, 1, 2, 4, null, 5, null, 8, 1));
        List<Integer> subList = list.subList(2, 8);
        // subList = <2, 4, null, 5, null, 8>
        
        subList.remove(null);
        
        assertEquals(Arrays.asList(2, 4, 5, null, 8), subList);
        assertEquals(Arrays.asList(3, 1, 2, 4, 5, null, 8, 1), list);
        
        subList.remove(Integer.valueOf(5));
        
        assertEquals(Arrays.asList(2, 4, null, 8), subList);
        assertEquals(Arrays.asList(3, 1, 2, 4, null, 8, 1), list);
        
        subList.remove(null);
        
        assertEquals(Arrays.asList(2, 4, 8), subList);
        assertEquals(Arrays.asList(3, 1, 2, 4, 8, 1), list);
    }
    
    @Test
    public void subListRemoveInt() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        List<Integer> subList = list.subList(3, 5);
        
        assertEquals(Arrays.asList(4, 5), subList);
        
        subList.remove(1);
        
        assertEquals(Arrays.asList(4), subList);
        assertEquals(Arrays.asList(1, 2, 3, 4, 6, 7), list);
        
        subList.remove(0);
        
        assertEquals(Arrays.asList(), subList);
        assertEquals(Arrays.asList(1, 2, 3, 6, 7), list);
    }
    
    @Test
    public void subListRemoveAll() {
        list.addAll(Arrays.asList(4, 1, 2, 9, 8, 7, 5, 2, 8, 10, 11));
        List<Integer> subList = list.subList(1, 9);
        // subList = <1, 2, 9, 8, 7, 5, 2, 8>
        
        subList.removeAll(Arrays.asList(1));
        // subList = <2, 9, 8, 7, 5, 2, 8>
        list.checkInvarant();
        
        assertEquals(Arrays.asList(2, 9, 8, 7, 5, 2, 8), subList);
        
        subList.removeAll(Arrays.asList(2));
        // subList = <9, 8, 7, 5, 8>
        list.checkInvarant();
        
        assertEquals(Arrays.asList(9, 8, 7, 5, 8), subList);
        
        subList.removeAll(Arrays.asList(8, 7));
        // subList = <9, 5>
        list.checkInvarant();
        
        assertEquals(Arrays.asList(9, 5), subList);
        
        subList.removeAll(Arrays.asList(9, 5)); // subList = <>
        list.checkInvarant();
        
        assertEquals(Arrays.asList(), subList);
        assertTrue(subList.isEmpty());
        assertFalse(list.isEmpty());
    }
    
    @Test
    public void subListRemoveIf() {
        list.addAll(Arrays.asList(1, 5, 2, 3, 4, 8, 9, 10, 4));
        List<Integer> subList = list.subList(2, 7);
        // subList = <2, 3, 4, 8, 9>
        
        subList.removeIf((i) -> {
            return i % 2 == 1; // Remove odd integers.
        });
        
        // subList = <2, 4, 8>
        assertEquals(Arrays.asList(2, 4, 8), subList);
        assertEquals(Arrays.asList(1, 5, 2, 4, 8, 10, 4), list);
    }
    
    @Test
    public void subListReplaceAll() {
        list.addAll(Arrays.asList(4, 4, 5, 1, 8, 2, 9, 0, 1, 3));
        List<Integer> subList = list.subList(2, 8);
        // subList = <5, 1, 8, 2, 9, 0>
        subList.replaceAll((i) -> { return i * 2; });
        
        assertEquals(Arrays.asList(10, 2, 16, 4, 18, 0), subList);
        assertEquals(Arrays.asList(4, 4, 10, 2, 16, 4, 18, 0, 1, 3), list);
    }
    
    @Test
    public void subListRetainAll() {
        list.addAll(Arrays.asList(3, 10, 8, 2, 5, 4, 1, 0, 7, 4));
        List<Integer> subList = list.subList(2, 8);
        // subList = <8, 2, 5, 4, 1, 0>
        subList.retainAll(Arrays.asList(4, 2, 5, 11));
        list.checkInvarant();
        
        // subList = <2, 5, 4>
        assertEquals(Arrays.asList(2, 5, 4), subList);
        assertEquals(Arrays.asList(3, 10, 2, 5, 4, 7, 4), list);
        
        list.clear();
        
        list.addAll(Arrays.asList(1, 3, 2, 1, 2, 3, 3, 1, 2, 0, 0));
        subList = list.subList(1, 6);
        // subList = <3, 2, 1, 2, 3>
        
        subList.retainAll(Arrays.asList(3, 4, 5));
        // subList = <3, 3>
        assertEquals(Arrays.asList(3, 3), subList);
        assertEquals(Arrays.asList(1, 3, 3, 3, 1, 2, 0, 0), list);
    }
    
    @Test
    public void subListSet() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> subList = list.subList(1, 4);
        // subList = <2, 3, 4>
        subList.set(0, 10);
        subList.set(2, 11);
        
        assertEquals(Arrays.asList(10, 3, 11), subList);
        assertEquals(Arrays.asList(1, 10, 3, 11, 5), list);
    }
    
    @Test
    public void subListSort() {
        Random random = new Random();
        
        for (int i = 0; i < 100; ++i) {
            int value = random.nextInt(100) % 75;
            list.add(value);
        }
        
        Collections.shuffle(list);
        List<Integer> referenceList = new ArrayList<>(list);
        
        list.subList(10, 80).sort(Integer::compare);
        referenceList.subList(10, 80).sort(Integer::compare);
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void subListToArray() {
        list.addAll(getIntegerList(15));
        List<Integer> subList = list.subList(5, 10);
        
        Object[] array = subList.toArray();
        
        for (int i = 0; i < subList.size(); ++i) {
            Integer listInteger = list.get(i + 5);
            Integer arrayInteger = (Integer) array[i];
            assertEquals(listInteger, arrayInteger);
        }
    }
    
    @Test
    public void subListToArrayGenerator() {
        list.addAll(getIntegerList(20));
        List<Integer> subList = list.subList(10, 16);
        Integer[] array = subList.toArray(Integer[]::new);
        
        assertEquals(Integer.valueOf(10), array[0]);
        assertEquals(Integer.valueOf(11), array[1]);
        assertEquals(Integer.valueOf(12), array[2]);
        assertEquals(Integer.valueOf(13), array[3]);
        assertEquals(Integer.valueOf(14), array[4]);
        assertEquals(Integer.valueOf(15), array[5]);
    }
    
    @Test
    public void listToArrayGenerator() {
        list.addAll(Arrays.asList(4, 1, 3, 2, 5));
        Integer[] array = list.toArray(Integer[]::new);
        
        assertEquals(5, array.length);
        assertEquals(Integer.valueOf(4), array[0]);
        assertEquals(Integer.valueOf(1), array[1]);
        assertEquals(Integer.valueOf(3), array[2]);
        assertEquals(Integer.valueOf(2), array[3]);
        assertEquals(Integer.valueOf(5), array[4]);
    }
    
    @Test
    public void subListToArrayGeneric() {
        list.addAll(getIntegerList(15));
        List<Integer> subList = list.subList(5, 10);
        
        Integer[] array = new Integer[5];
        Integer[] resultArray = subList.toArray(array);
        
        assertTrue(array == resultArray);
        
        array = new Integer[7];
        resultArray = subList.toArray(array);
        
        assertTrue(array == resultArray);
        
        assertNull(resultArray[5]);
        
        array = new Integer[3];
        resultArray = subList.toArray(array);
        
        assertFalse(array == resultArray);
        assertEquals(5, resultArray.length);
    }
    
    @Test
    public void clone2() {
        list.addAll(Arrays.asList(4, 1, 3, 2));
        assertEquals(Arrays.asList(4, 1, 3, 2), list.clone());
    }
    
    @Test
    public void subListClone() {
        list.addAll(Arrays.asList(4, 1, 8, 9, 5, 6, 7, 0, 1));
        List<Integer> subList1 = list.subList(1, list.size() - 1);
        
        IndexedLinkedList<Integer>.EnhancedSubList subList2 = 
                (IndexedLinkedList<Integer>.EnhancedSubList) 
                subList1.subList(1, subList1.size() - 1);
        
        List<Integer> clone = (List<Integer>) subList2.clone();
        assertEquals(Arrays.asList(8, 9, 5, 6, 7), clone);
    }
    
    @Test
    public void debugContractFingerArrayIfNeeded() {
        list.addAll(getIntegerList(15_877)); // 128 finger spots occupied.
        assertEquals(127, list.getFingerListSize());
        list.subList(49, list.size()).clear(); // 8 finger spots occupied.
        assertEquals(7, list.getFingerListSize());
    }
    
    @Test
    public void bruteForceSublistClearOnLargeLists() {
        Random random = new Random(26L);
        
        for (int i = 0; i < 30; ++i) {
            int size = 1 + random.nextInt(5_000);
            List<Integer> referenceList = new ArrayList<>(getIntegerList(size));
            list.clear();
            list.addAll(referenceList); 
            
            int f = random.nextInt(size);
            int t = random.nextInt(size);
            int fromIndex = Math.min(f, t);
            int toIndex = Math.max(f, t);
            
            list.subList(fromIndex, toIndex).clear();
            referenceList.subList(fromIndex, toIndex).clear();
            
            list.checkInvarant();
            assertEquals(referenceList, list);
        }
    }
    
//    @Test(expected = IllegalStateException.class) 
    public void listEqualsThrowsOnBadIterator() {
        DummyList dummyList = new DummyList();
        list.addAll(Arrays.asList(0, 0));
        list.equals(dummyList);
    }
    
    @Test
    public void offer() {
        assertTrue(list.equals(Arrays.asList()));
        
        list.offer(1);
        
        assertTrue(list.equals(Arrays.asList(1)));
        
        list.offer(2);
        
        assertTrue(list.equals(Arrays.asList(1, 2)));
    }
    
    @Test
    public void offerFirst() {
        assertTrue(list.equals(Arrays.asList()));
        
        list.offerFirst(1);
        
        assertTrue(list.equals(Arrays.asList(1)));
        
        list.offerFirst(2);
        
        assertTrue(list.equals(Arrays.asList(2, 1)));
    }
    
    @Test
    public void offerLast() {
        assertTrue(list.equals(Arrays.asList()));
        
        list.offerLast(1);
        
        assertTrue(list.equals(Arrays.asList(1)));
        
        list.offerLast(2);
        
        assertTrue(list.equals(Arrays.asList(1, 2)));
    }
    
    @Test
    public void peek() {
        assertNull(list.peek());
        
        list.addLast(0);
        
        assertEquals(Integer.valueOf(0), list.peek());
        
        list.addLast(1);
        
        assertEquals(Integer.valueOf(0), list.peek());
    
        list.addFirst(Integer.valueOf(-1));

        assertEquals(Integer.valueOf(-1), list.peek());
    }
    
    @Test
    public void peekFirst() {
        assertNull(list.peek());
        
        list.addLast(0);
        
        assertEquals(Integer.valueOf(0), list.peekFirst());
        
        list.addFirst(1);
        
        assertEquals(Integer.valueOf(1), list.peekFirst());
    
        list.addFirst(Integer.valueOf(-1));

        assertEquals(Integer.valueOf(-1), list.peekFirst());
    }
    
    @Test
    public void peekLast() {
        assertNull(list.peek());
        
        list.addLast(0);
        
        assertEquals(Integer.valueOf(0), list.peekLast());
        
        list.addLast(1);
        
        assertEquals(Integer.valueOf(1), list.peekLast());
    
        list.addLast(2);

        assertEquals(Integer.valueOf(2), list.peekLast());
    }
    
    @Test
    public void poll() {
        assertNull(list.poll());
        
        list.addAll(Arrays.asList(1, 2, 3));
        
        assertEquals(Integer.valueOf(1), list.poll());
        assertEquals(Integer.valueOf(2), list.poll());
        assertEquals(Integer.valueOf(3), list.poll());
    }
    
    @Test
    public void pollFirst() {
        assertNull(list.pollFirst());
        
        list.addAll(Arrays.asList(1, 2, 3));
        
        assertEquals(Integer.valueOf(1), list.pollFirst());
        assertEquals(Integer.valueOf(2), list.pollFirst());
        assertEquals(Integer.valueOf(3), list.pollFirst());
    }
    
    @Test
    public void pollLast() {
        assertNull(list.pollLast());
        
        list.addAll(Arrays.asList(1, 2, 3));
        
        assertEquals(Integer.valueOf(3), list.pollLast());
        assertEquals(Integer.valueOf(2), list.pollLast());
        assertEquals(Integer.valueOf(1), list.pollLast());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void removeFirstThrowsOnEmptyList() {
        list.removeFirst();
    }
    
    @Test
    public void pop() {
        list.addAll(Arrays.asList(1, 2, 3));
        
        assertEquals(Integer.valueOf(1), list.pop());
        assertEquals(Integer.valueOf(2), list.pop());
        assertEquals(Integer.valueOf(3), list.pop());
    }
    
    @Test
    public void push() {
        list.push(1);
        list.push(2);
        list.push(3);
        
        assertTrue(list.equals(Arrays.asList(3, 2, 1)));
    }
    
    class BadList extends IndexedLinkedList<Integer> {
        
        class BadListIterator implements Iterator<Integer> {

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                return Integer.valueOf(3);
            }
        }
        
        @Override
        public Iterator<Integer> iterator() {
            return new BadListIterator();
        };
        
        public int size() {
            return 2;
        }
    }
    
    // TODO: deal with the equals(Object)!
//    @Test(expected = IllegalStateException.class) 
//    public void badThisIterator() {
//        List<Integer> arrayList = Arrays.asList(3, 3);
//        BadList badList = new BadList();
//        badList.addAll(Arrays.asList(3, 3));
//        badList.equals(arrayList);
//    }
    
    @Test
    public void removeFirstOccurrenceOfNull() {
        list.addAll(Arrays.asList(1, 2, null, 4, null, 6));
        
        assertTrue(list.removeFirstOccurrence(null));
        
        // Remove the last null value:
        list.set(3, 10);
        
        assertFalse(list.removeFirstOccurrence(null));
    }
    
    @Test
    public void removeLastOccurrenceOfNull() {
        list.addAll(Arrays.asList(1, 2, null, 4, null, 6));
        
        assertTrue(list.removeLastOccurrence(null));
        
        // Remove the last null value:
        list.set(2, 10);
        
        assertFalse(list.removeLastOccurrence(null));
    }
    
    @Test
    public void appendAll() {
        list.addAll(Arrays.asList(0, 1, 2));
        
        List<Integer> arrayList = new ArrayList<>();
        
        for (int i = 3; i < 20_000; i++) {
            arrayList.add(i);
        }
        
        list.addAll(arrayList);
        
        for (int i = 0; i < 20_000; i++) {
            assertEquals(Integer.valueOf(i), list.get(i));
        }
    }
    
    @Test
    public void smallListRemoveFirstFinger() {
        list.add(0);
        list.add(1);
        list.remove(0);
    }
    
    @Test
    public void smallListRemoveSecondFinger() {
        list.add(0);
        list.add(1);
        list.remove(1);
    }
    
    @Test
    public void prependAll() {
        List<Integer> l = new ArrayList<>();
        
        for (int i = 0; i < 10_000; i++) {
            l.add(i);
        }
        
        list.addAll(l);
        
        l = new ArrayList<>();
        
        for (int i = 10_000; i < 20_000; i++) {
            l.add(i);
        }
        
        list.addAll(0, l);
        
        int index = 0;
        
        for (int i = 10_000; i < 20_000; i++) {
            assertEquals(Integer.valueOf(i), list.get(index++));
        }
        
        for (int i = 0; i < 10_000; i++) {
            assertEquals(Integer.valueOf(i), list.get(index++));
        }
    }
    
    @Test
    public void insertAll() {
        for (int i = 0; i < 20_000; i++) {
            list.add(i);
        }
        
        List<Integer> arrayList = new ArrayList<>(10_000);
        
        for (int i = 20_000; i < 30_000; i++) {
            arrayList.add(i);
        }
        
        list.addAll(10_000, arrayList);
        
        int index = 0;
        
        for (int i = 0; i < 10_000; i++) {
            assertEquals(Integer.valueOf(i), list.get(index++));
        }
        
        for (int i = 20_000; i < 30_000; i++) {
            assertEquals(Integer.valueOf(i), list.get(index++));
        }
        
        for (int i = 10_000; i < 20_000; i++) {
            assertEquals(Integer.valueOf(i), list.get(index++));    
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkPositionIndexThrowsOnNegativeIndex() {
        list.add(-1, Integer.valueOf(0));
    }
    
    @Test(expected = IndexOutOfBoundsException.class) 
    public void checkPositionIndxThrowsOnTooLargeIndex() {
        list.add(Integer.valueOf(0));
        
        list.add(2, Integer.valueOf(1));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void removeLastThrowsOnEmptyList() {
        list.removeLast();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void getFirstThrowsOnEmptyList() {
        list.getFirst();
    }
    
    @Test
    public void getFirst() {
        list.addAll(Arrays.asList(10, 20));
        assertEquals(Integer.valueOf(10), list.getFirst());
        
        list.removeFirst();
        
        assertEquals(Integer.valueOf(20), list.getFirst());
    }
      
    @Test(expected = NoSuchElementException.class)
    public void basicIteratorNextThrowsOnNoNext() {
        list.add(1);
        
        Iterator<Integer> iter = list.iterator();
        
        try {
            iter.next();
        } catch (Exception ex) {
            fail("Should not get here.");
        }
        
        iter.next();
    }
    
    @Test(expected = IllegalStateException.class)
    public void basicIteratorThrowsOnDoubleRemove() {
        list.add(1);
        
        Iterator<Integer> iter = list.iterator();
        
        try {
            iter.next();
            iter.remove();
        } catch (Exception ex) {
            fail("Should not get here.");
        }
        
        iter.remove();
    }
    
    @Test
    public void basicIteratorRemove1() {
        list.add(1);
        list.add(2);
        
        Iterator<Integer> iter = list.iterator();
        
        iter.next();
        iter.remove();
        iter.next();
        iter.remove();
        
        assertEquals(0, list.size());
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void basicIteratorRemove2() {
        list.add(1);
        list.add(2);
        list.add(3);
        
        Iterator<Integer> iter = list.iterator();
        
        iter.next();
        iter.remove();
        iter.next();
        iter.next();
    }
    
    @Test(expected = IllegalStateException.class) 
    public void enhancedIteratorThrowsOnSetAfterRemove() {
        list.addAll(Arrays.asList(1, 2, 3, 4));
        
        ListIterator<Integer> iter = list.listIterator(1);
        
        iter.next();
        iter.remove();
        iter.set(10);
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void basicIteratorForEachRemainingThrowsOnConcurrentModification() {
        list.addAll(getIntegerList(1_000_000));
        
        BasicIterator iter =(BasicIterator) list.iterator();
        iter.expectedModCount = -1000;
        
        iter.forEachRemaining((e) -> {});
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void 
        enhancedIteratorForEachRemainingThrowsOnConcurrentModification() {
            
        list.addAll(getIntegerList(1_000_000));
        
        EnhancedIterator iter = (EnhancedIterator) list.listIterator();
        iter.expectedModCount = -1;
        
        iter.forEachRemaining((e) -> {});
    }
        
    @Test(expected = ConcurrentModificationException.class)
    public void spliteratorThrowsOnConcurrentModification() {
        list.addAll(getIntegerList(50_000));
        
        Spliterator<Integer> spliterator = list.spliterator();
        list.add(50_000);
        
        spliterator.tryAdvance((e) -> {});
    }
    
    @Test
    public void spliteratorTrySplitReturnsNullOnEmptyList() {
        Spliterator<Integer> spliterator = list.spliterator();
        
        assertNull(spliterator.trySplit());
    }
    
    @Test
    public void spliteratorTrySplitReturnsNullOnTooSmallList() {
        list.addAll(
                getIntegerList(
                        (int)
                        (IndexedLinkedList
                                .LinkedListSpliterator
                                .MINIMUM_BATCH_SIZE - 1L)));
        
        Spliterator<Integer> spliterator = list.spliterator();
        
        assertNull(spliterator.trySplit());
    }
    
    @Test
    public void spliteratorHasCharasteristics() {
        Spliterator<Integer> spliterator = list.spliterator();
        
        assertTrue(spliterator.hasCharacteristics(Spliterator.ORDERED));
        assertTrue(spliterator.hasCharacteristics(Spliterator.SIZED));
        assertTrue(spliterator.hasCharacteristics(Spliterator.SUBSIZED));
        
        assertFalse(spliterator.hasCharacteristics(Spliterator.CONCURRENT));
        assertFalse(spliterator.hasCharacteristics(Spliterator.DISTINCT));
        assertFalse(spliterator.hasCharacteristics(Spliterator.IMMUTABLE));
        assertFalse(spliterator.hasCharacteristics(Spliterator.NONNULL));
        assertFalse(spliterator.hasCharacteristics(Spliterator.SORTED));
    }
    
    @Test
    public void enhancedListIteratorForEachRemaining() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        
        List<Integer> storageList = new ArrayList<>(3);
        ListIterator<Integer> iter = list.listIterator(1);
        
        iter.next();
        
        iter.forEachRemaining((e) -> {
            storageList.add(e);
        });
        
        storageList.equals(Arrays.asList(2, 3, 4));
    }
    
    @Test(expected = NullPointerException.class)
    public void 
        spliteratorTryAdvanceThrowsNullPointerExceptionOnNullConsumer() {
        list.spliterator().tryAdvance(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void 
        spliteratorForEachRemainingThrowsNullPointerExceptionOnNullConsumer() {
        list.spliterator().forEachRemaining(null);
    }
        
    @Test(expected = ConcurrentModificationException.class)
    public void 
    spliteratorThrowsConcurrentModificationExceptionOnConcurrentModification() {
        list.addAll(Arrays.asList(1, 2, 3));
        
        Spliterator<Integer> spliterator = list.spliterator();
        
        list.add(4);
        spliterator.forEachRemaining((e) -> {});
        list.forEach((e) -> {});
    }
    
    @Test(expected = NoSuchElementException.class) 
    public void enhancedIteratorNextThrowsOnNoNext() {
        list.addAll(getIntegerList(20));
        
        ListIterator<Integer> iter = list.listIterator(19);
        
        try {
            iter.next();
        } catch (Exception ex) {
            fail("Should not get here.");
        }
        
        iter.next();
    }
    
    @Test(expected = NoSuchElementException.class) 
    public void enhancedIteratorPrevioiusThrowsOnNoPrevious() {
        list.addAll(getIntegerList(20));
        
        ListIterator<Integer> iter = list.listIterator(1);
        
        try {
            iter.previous();
        } catch (Exception ex) {
            fail("Should not get here.");
        }
        
        iter.previous();
    }
    
    @Test(expected = IllegalStateException.class)
    public void enhancedIteratorThrowsOnDoubleRemove() {
        list.add(1);
        
        ListIterator<Integer> iter = list.listIterator();
        
        try {
            iter.next();
            iter.remove();
        } catch (Exception ex) {
            fail("Should not get here.");
        }
        
        iter.remove();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void getLastThrowsOnEmptyList() {
        list.getLast();
    }
    
    @Test
    public void getLast() {
        list.addAll(Arrays.asList(10, 20));
        assertEquals(Integer.valueOf(20), list.getLast());
        
        list.removeLast();
        
        assertEquals(Integer.valueOf(10), list.getLast());
    }
    
    @Test
    public void indexOfNull() {
        list.addAll(Arrays.asList(1, 2, null, 3, null, 4));
        
        assertEquals(2, list.indexOf(null));
        
        list.set(2, 5);
        list.set(4, 10);
        
        assertEquals(-1, list.indexOf(null));
    }
    
    @Test
    public void lastIndexOfNull() {
        list.addAll(Arrays.asList(1, 2, null, 3, null, 4));
        
        assertEquals(4, list.lastIndexOf(null));
        
        list.set(2, 5);
        list.set(4, 10);
        
        assertEquals(-1, list.lastIndexOf(null));
    }
    
    @Test
    public void add() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        list.add(1);

        assertEquals(1, list.size());
        assertFalse(list.isEmpty());

        assertEquals(Integer.valueOf(1), list.get(0));

        list.add(2);

        assertEquals(2, list.size());
        assertFalse(list.isEmpty());

        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
    }

    @Test
    public void addFirst() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        list.addFirst(1);

        assertEquals(1, list.size());
        assertFalse(list.isEmpty());

        assertEquals(Integer.valueOf(1), list.get(0));

        list.addFirst(2);

        assertEquals(2, list.size());
        assertFalse(list.isEmpty());

        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsOnAccessingEmptyList() {
        list.get(0);
    }

    @Test(expected = IndexOutOfBoundsException.class) 
    public void throwsOnNegativeIndexInEmptyList() {
        list.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class) 
    public void throwsOnNegativeIndexInNonEmptyList() {
        list.addFirst(10);
        list.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class) 
    public void throwsOnTooLargeIndex() {
        list.addFirst(10);
        list.addLast(20);
        list.get(2);
    }

    @Test
    public void addIndexAndElement() {
        list.add(0, 1);
        assertEquals(Integer.valueOf(1), list.get(0));

        list.add(0, 2);
        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));

        list.add(2, 10);

        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(10), list.get(2));

        list.add(2, 100);

        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(100), list.get(2));
        assertEquals(Integer.valueOf(10), list.get(3));
    }

    @Test
    public void addCollectionOneElementToEmptyList() {
        List<Integer> c = new ArrayList<>();
        c.add(100);

        list.addAll(c);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(100), list.get(0));
    }

    @Test
    public void addCollectionThreeElementsToEmptyList() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        List<Integer> c = Arrays.asList(1, 2, 3);

        list.addAll(c);
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());

        for (int i = 0; i < list.size(); i++) {
            assertEquals(Integer.valueOf(i + 1), list.get(i));
        }
    }

    @Test
    public void addCollectionAtIndex() {
        list.addAll(0, Arrays.asList(2, 3)); // setAll
        list.addAll(0, Arrays.asList(0, 1)); // prependAll
        list.addAll(4, Arrays.asList(6, 7)); // appendAll
        list.addAll(4, Arrays.asList(4, 5)); // insertAll

        for (int i = 0; i < list.size(); i++) {
            assertEquals(Integer.valueOf(i), list.get(i));
        }
    }

    @Test
    public void removeInt() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));

        // [0, 1, 2, 3, 4]
        assertEquals(Integer.valueOf(0), list.remove(0));
        // [1, 2, 3, 4]
        assertEquals(Integer.valueOf(4), list.remove(3));
        // [1, 2, 3]
        assertEquals(Integer.valueOf(2), list.remove(1));
        // [1, 3]
        assertEquals(Integer.valueOf(1), list.remove(0));
        // [3]
        assertEquals(Integer.valueOf(3), list.remove(0));
        // []
    }

    @Test // shadowed
    public void basicIteratorUsage() {
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        Iterator<Integer> iterator = list.iterator();

        for (int i = 0; i < 1000; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(i), iterator.next());
        }

        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void removeFirstLast() {
        list.addAll(getIntegerList(5));
        
        List<Integer> referenceList = new ArrayList<>(list);
        
        list.removeFirst();
        referenceList.remove(0);
        
        assertTrue(listsEqual(list, referenceList));
        
        list.removeFirst();
        referenceList.remove(0);
        
        assertTrue(listsEqual(list, referenceList));
        
        list.removeLast();
        referenceList.remove(referenceList.size() - 1);
        
        assertTrue(listsEqual(list, referenceList));
        
        list.removeLast();
        referenceList.remove(referenceList.size() - 1);
        
        assertTrue(listsEqual(list, referenceList));
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void subListThrowsOnConcurrentModification() {
        List<Integer> l =
                new IndexedLinkedList<Integer>(
                        Arrays.asList(1, 2, 3, 4));
        
        List<Integer> subList1 = l.subList(1, 4); // <2, 3, 4>
        List<Integer> subList2 = subList1.subList(0, 2); // <2, 3>
        
        subList1.add(1, Integer.valueOf(10));
        subList2.add(1, Integer.valueOf(11)); // Must throw here.
    }
    
    @Test
    public void removeFirstLastOccurrence() {
        IndexedLinkedList<Integer> l = new IndexedLinkedList<>();
        
        list.addAll(Arrays.asList(1, 2, 3, 1, 2, 3));
        l.addAll(list);
        
        list.removeFirstOccurrence(2);
        l.removeFirstOccurrence(2);
        
        assertTrue(listsEqual(list, l));
        
        list.removeLastOccurrence(3);
        l.removeLastOccurrence(3);
        
        assertTrue(listsEqual(list, l));
    }

    @Test 
    public void bruteForceAddCollectionAtIndex() {
        Random random = new Random(100L);

        list.addAll(getIntegerList());

        LinkedList<Integer> referenceList = new LinkedList<>(list);

        for (int op = 0; op < 100; op++) {
            int index = random.nextInt(list.size());
            Collection<Integer> coll = getIntegerList(random.nextInt(40));
            referenceList.addAll(index, coll);
            list.addAll(index, coll);

            if (!listsEqual(list, referenceList)) {
                fail("Lists not equal!");
            }
        }
    }

    @Test
    public void removeAtIndex() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        
        // [0, 1, 2, 3, 4]
        assertEquals(Integer.valueOf(2), list.remove(2));
        // [0, 1, 3, 4]
        assertEquals(Integer.valueOf(0), list.remove(0));
        // [1, 3, 4]
        assertEquals(Integer.valueOf(4), list.remove(2));
        // [1, 3]
        assertEquals(Integer.valueOf(3), list.remove(1));
        // [1]
        assertEquals(Integer.valueOf(1), list.remove(0));
        // []
    }

    @Test
    public void removeObject() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));

        assertFalse(list.remove(Integer.valueOf(10)));
        assertFalse(list.remove(null));

        list.add(3, null);

        assertTrue(list.remove(null));

        assertTrue(list.remove(Integer.valueOf(4)));
        assertTrue(list.remove(Integer.valueOf(0)));
        assertTrue(list.remove(Integer.valueOf(2)));
        assertFalse(list.remove(Integer.valueOf(2)));
    }

    @Test
    public void basicIteratorTraversal() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));

        Iterator<Integer> iter = list.iterator();

        for (int i = 0; i < list.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i), iter.next());
        }

        iter = list.iterator();

        class MyConsumer implements Consumer<Integer> {

            int total;

            @Override
            public void accept(Integer t) {
                total += t;
            }
        }

        MyConsumer myConsumer = new MyConsumer();

        list.iterator().forEachRemaining(myConsumer);
        assertEquals(10, myConsumer.total);
    }

    @Test
    public void basicIteratorRemoval() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        Iterator<Integer> iter = list.iterator();

        iter.next();
        iter.next();
        iter.remove();

        assertEquals(4, list.size());

        iter = list.iterator();
        iter.next();
        iter.remove();

        assertEquals(3, list.size());

        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(3), list.get(1));
        assertEquals(Integer.valueOf(4), list.get(2));
    }

    @Test
    public void enhancedIteratorTraversal() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        ListIterator<Integer> iter = list.listIterator();

        assertFalse(iter.hasPrevious());

        for (int i = 0; i < list.size(); i++) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(i), iter.next());
        }

        assertFalse(iter.hasNext());

        for (int i = 4; i >= 0; i--) {
            assertTrue(iter.hasPrevious());
            assertEquals(Integer.valueOf(i), iter.previous());
        }

        iter = list.listIterator(2);

        assertEquals(Integer.valueOf(2), iter.next());
        assertEquals(Integer.valueOf(2), iter.previous());

        iter = list.listIterator(3);

        assertEquals(Integer.valueOf(3), iter.next());
        assertEquals(Integer.valueOf(4), iter.next());

        assertFalse(iter.hasNext());
        assertTrue(iter.hasPrevious());
    }
    
    @Test
    public void removeAt() {
        list.addAll(getIntegerList(10));
        List<Integer> referenceList = new ArrayList<>(list);
        Random random = new Random(100L);
        
        while (!referenceList.isEmpty()) {
            int removalIndex = random.nextInt(list.size());
            Integer referenceInteger = referenceList.remove(removalIndex);
            Integer listInteger = list.remove(removalIndex);
            assertEquals(referenceInteger, listInteger);
            assertEquals(referenceList, list);
        }
    }
    
    // Used to find a failing removal sequence:
    @Test
    public void removeAtFindFailing() {
        long seed = 101L;
        Random random = new Random(seed);
        int iteration = 0;
        while (true) {
            iteration++;
            
            list.clear();
            list.addAll(getIntegerList(10));
            
            List<Integer> indices = new ArrayList<>();
            
            if (iteration == 100) {
                return;
            }
            
            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                indices.add(index);
                
                try {
                    list.remove(index);
                } catch (NullPointerException ex) {
                    // Should not get here. Ever.
                    fail("Failing indices: " +  indices);
                    return;
                }catch (AssertionError ae) {
                    return;
                }
            }
        }
    }
    
    @Test
    public void bugTinyRemoveInt() {
        list.addAll(getIntegerList(5));
        
        list.remove(4);    
        list.remove(0);    
        list.remove(2);    
        list.remove(0);    
        list.remove(0);    
    }
    
    @Test
    public void removeAtIndex1() {
        list.addAll(getIntegerList(10));
        // TODO: remove 'getIntegerList()'!
        List<Integer> referenceList = new ArrayList<>(getIntegerList(10));
        int[] indices = { 9, 3, 3, 3, 1, 0 };
        
        for (int i = 0; i < indices.length; i++) {
            assertEquals(referenceList, list);
            
            int index = indices[i];
            list.remove(index);
            referenceList.remove((int) index);
        }
        
        assertEquals(referenceList, list);
    }

    @Test
    public void enhancedIteratorAddition() {
        list.addAll(Arrays.asList(1, 2, 3));
        ListIterator<Integer> iter = list.listIterator();

        iter.add(0);

        while (iter.hasNext()) {
            iter.next();
        }

        iter.add(4);
        iter = list.listIterator();

        for (int i = 0; i < list.size(); i++) {
            assertEquals(Integer.valueOf(i), iter.next());
        }

        iter = list.listIterator(2);
        iter.add(10);

        assertEquals(Integer.valueOf(10), list.get(2));
    }

    @Test
    public void findFailingIterator() {
        list.addAll(getIntegerList(3850));
        Iterator<Integer> iterator = list.iterator();
        int counter = 0;

        while (iterator.hasNext()) {
            assertEquals(Integer.valueOf(counter), iterator.next());
            
            // Remove every 10th element:
            if (counter % 10 == 0) {
                iterator.remove();
            }

            counter++;
        }
    }

    @Test
    public void bruteForceIteratorRemove() throws Exception {
        list.addAll(getIntegerList(1000));
 
        int counter = 1;
        List<Integer> arrayList = new ArrayList<>(list);
        Iterator<Integer> iter = list.iterator();
        Iterator<Integer> arrayListIter = arrayList.iterator();
        int totalIterations = 0;

        while (iter.hasNext()) {
            iter.next();
            arrayListIter.next();
            
            if (counter % 10 == 0) {

                try {
                    iter.remove();
                } catch (IllegalStateException ex) {
                    throw new Exception(ex);
                }

                arrayListIter.remove();
                counter = 1;
            } else {
                counter++;
            }

            if (!listsEqual(list, arrayList)) {
                throw new IllegalStateException(
                        "totalIterations = " + totalIterations);
            }

            totalIterations++;
        }
    }

    @Test
    public void findFailingRemoveObject() {
        LinkedList<Integer> referenceList = new LinkedList<>();

        list.addAll(getIntegerList(10));
        referenceList.addAll(list);

        Integer probe = list.get(1);

        list.remove(probe);
        referenceList.remove(probe);

        Iterator<Integer> iterator1 = list.iterator();
        Iterator<Integer> iterator2 = referenceList.iterator();

        Random random = new Random(100L);

        while (!list.isEmpty()) {
            if (!iterator1.hasNext()) {

                if (iterator2.hasNext()) {
                    throw new IllegalStateException();
                }

                iterator1 = list.iterator();
                iterator2 = referenceList.iterator();
                continue;
            }

            iterator1.next();
            iterator2.next();

            if (random.nextBoolean()) {
                iterator1.remove();
                iterator2.remove();
                assertTrue(listsEqual(list, referenceList));
            }
        }

        assertTrue(listsEqual(list, referenceList));
    }

    @Test
    public void iteratorAdd() {
        list.addAll(getIntegerList(4));

        ListIterator<Integer> iterator = list.listIterator(1);

        assertEquals(1, iterator.nextIndex());
        assertEquals(0, iterator.previousIndex());

        iterator.next();

        assertEquals(2, iterator.nextIndex());
        assertEquals(1, iterator.previousIndex());

        iterator.add(Integer.valueOf(100));

        assertEquals(Integer.valueOf(0), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(100), list.get(2));
        assertEquals(Integer.valueOf(2), list.get(3));
        assertEquals(Integer.valueOf(3), list.get(4));
    }

    @Test
    public void bruteForceIteratorTest() {
        list.addAll(getIntegerList(100));
        List<Integer> referenceList = new LinkedList<>(list);

        ListIterator<Integer> iterator1 = list.listIterator(2);
        ListIterator<Integer> iterator2 = referenceList.listIterator(2);
        Random random = new Random(300L);

        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                fail("Iterator mismatch on hasNext().");
            }

            iterator1.next();
            iterator2.next();

            int choice = random.nextInt(10);

            if (choice < 2) {
                Integer integer = Integer.valueOf(random.nextInt(100));
                iterator1.add(integer);
                iterator2.add(integer);
                assertTrue(listsEqual(list, referenceList));
            } else if (choice == 2) {
                iterator1.remove();
                iterator2.remove();
                assertTrue(listsEqual(list, referenceList));
            } else if (choice < 6) {
                if (iterator1.hasPrevious()) {
                    iterator1.previous();
                }

                if (iterator2.hasPrevious()) {
                    iterator2.previous();
                }
            } else {
                if (iterator1.hasNext()) {
                    iterator1.next();
                }

                if (iterator2.hasNext()) {
                    iterator2.next();
                }
            }
        }

        if (iterator2.hasNext()) {
            fail("Java List iterator has more to offer.");
        }
    }

    @Test
    public void indexOf() {
        list.add(1);
        list.add(2);
        list.add(3);

        list.add(3);
        list.add(2);
        list.add(1);

        assertEquals(0, list.indexOf(1));
        assertEquals(1, list.indexOf(2));
        assertEquals(2, list.indexOf(3));

        assertEquals(3, list.lastIndexOf(3));
        assertEquals(4, list.lastIndexOf(2));
        assertEquals(5, list.lastIndexOf(1));
    }

    class MyIntegerConsumer implements Consumer<Integer> {

        List<Integer> ints = new ArrayList<>();

        @Override
        public void accept(Integer t) {
            ints.add(t);
        }
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void basicSpliteratorUsage() {
        list.addAll(getIntegerList(10_000));

        Spliterator<Integer> spliterator1 = list.spliterator();
        Spliterator<Integer> spliterator2 = spliterator1.trySplit();

        //// spliterator 2 : spliterator 1

        assertEquals(5000, spliterator1.getExactSizeIfKnown());
        assertEquals(5000, spliterator2.getExactSizeIfKnown());


        assertTrue(spliterator2.tryAdvance(
                i -> assertEquals(list.get(0), Integer.valueOf(0))));

        assertTrue(spliterator2.tryAdvance(
                i -> assertEquals(list.get(1), Integer.valueOf(1))));

        assertTrue(spliterator2.tryAdvance(
                i -> assertEquals(list.get(2), Integer.valueOf(2))));



        assertTrue(spliterator1.tryAdvance(
                i -> assertEquals(list.get(5000), Integer.valueOf(5000))));

        assertTrue(spliterator1.tryAdvance(
                i -> assertEquals(list.get(5001), Integer.valueOf(5001))));

        assertTrue(spliterator1.tryAdvance(
                i -> assertEquals(list.get(5002), Integer.valueOf(5002))));

        //// spliterator 3 : spliterator 2 : splitereator 1

        Spliterator<Integer> spliterator3 = spliterator2.trySplit();

        assertEquals(4997, spliterator1.getExactSizeIfKnown());

        assertTrue(spliterator3.tryAdvance(
                i -> assertEquals(list.get(3), Integer.valueOf(3))));

        assertTrue(spliterator3.tryAdvance(
                i -> assertEquals(list.get(4), Integer.valueOf(4))));

        assertTrue(spliterator3.tryAdvance(
                i -> assertEquals(list.get(5), Integer.valueOf(5))));

        //// 

        MyIntegerConsumer consumer = new MyIntegerConsumer();

        while (spliterator1.tryAdvance(consumer));

        for (int i = 0; i < consumer.ints.size(); i++) {
            Integer actualInteger = consumer.ints.get(i);
            Integer expectedInteger = 5003 + i;
            assertEquals(expectedInteger, actualInteger);
        }
    }

    @Test
    public void spliteratorForEachRemaining() {
        list.addAll(getIntegerList(10_000));
        Spliterator<Integer> split = list.spliterator();
        MyIntegerConsumer consumer = new MyIntegerConsumer();

        split.forEachRemaining(consumer);

        for (int i = 0; i < 10_000; i++) {
            assertEquals(Integer.valueOf(i), consumer.ints.get(i));
        }
    }

    @Test
    public void spliteratorForEachRemainingTwoSpliterators() {
        list.addAll(getIntegerList(10_000));
        Spliterator<Integer> splitRight = list.spliterator();
        Spliterator<Integer> splitLeft = splitRight.trySplit();

        MyIntegerConsumer consumerRight = new MyIntegerConsumer();
        MyIntegerConsumer consumerLeft = new MyIntegerConsumer();

        splitRight.forEachRemaining(consumerRight);
        splitLeft.forEachRemaining(consumerLeft);

        for (int i = 0; i < 5_000; i++) {
            assertEquals(Integer.valueOf(i), consumerLeft.ints.get(i));
        }

        for (int i = 5_000; i < 10_000; i++) {
            assertEquals(Integer.valueOf(i), consumerRight.ints.get(i - 5_000));
        }
    }

    @Test
    public void spliteratorForEachRemainingWithAdvance() {
        list.addAll(getIntegerList(10_000));
        Spliterator<Integer> rightSpliterator = list.spliterator();

        assertTrue(
                rightSpliterator.tryAdvance(
                        i -> assertEquals(Integer.valueOf(0), i)));

        Spliterator<Integer> leftSpliterator = rightSpliterator.trySplit();

        assertEquals(4_999, rightSpliterator.getExactSizeIfKnown());
        assertEquals(5_000, leftSpliterator.getExactSizeIfKnown());

        // Check two leftmost elements of the left spliterator:
        assertTrue(leftSpliterator.tryAdvance(
                i -> assertEquals(Integer.valueOf(1), i)));

        assertTrue(leftSpliterator.tryAdvance(
                i -> assertEquals(Integer.valueOf(2), i)));

        // Check two leftmost elements of the right splliterator:
        assertTrue(rightSpliterator.tryAdvance(
                i -> assertEquals(Integer.valueOf(5_000), i)));

        assertTrue(rightSpliterator.tryAdvance(
                i -> assertEquals(Integer.valueOf(5_001), i)));
    }

    @Test
    public void spliterator() {
        list.addAll(getIntegerList(6_000));
        Spliterator split = list.spliterator();

        assertEquals(6_000L, split.getExactSizeIfKnown());
        assertEquals(6_000L, split.estimateSize());

        assertTrue(split.tryAdvance((i) -> assertEquals(list.get((int) i), i)));
        assertTrue(split.tryAdvance((i) -> assertEquals(list.get((int) i), i)));

        assertEquals(5998, split.getExactSizeIfKnown());

        // 5998 elements left / 2 = 2999 per spliterator:
        Spliterator leftSpliterator = split.trySplit();

        assertNotNull(leftSpliterator);
        assertEquals(2999, split.getExactSizeIfKnown());
        assertEquals(2999, leftSpliterator.getExactSizeIfKnown());

        //// leftSpliterator = [1, 2999]

        for (int i = 2; i < 3000; i++) {
            Integer integer = list.get(i);
            assertTrue(
                    leftSpliterator.tryAdvance(
                            (j) -> assertEquals(integer, j)));
        }

        //// split = [3001, 5999]

        assertTrue(split.tryAdvance(i -> assertEquals(2999, i)));
        assertTrue(split.tryAdvance(i -> assertEquals(3000, i)));
        assertTrue(split.tryAdvance(i -> assertEquals(3001, i)));

        while (split.getExactSizeIfKnown() > 0) {
            split.tryAdvance(i -> {});
        }

        assertFalse(split.tryAdvance(i -> {}));
    }

    @Test
    public void bruteforceSpliterator() {
        list.addAll(getIntegerList(1_000_000));
        Collections.<Integer>shuffle(list);

        List<Integer> newList = 
               list.parallelStream()
                   .map(i -> 2 * i)
                   .collect(Collectors.toList());

        assertEquals(newList.size(), list.size());

        for (int i = 0; i < list.size(); i++) {
            Integer integer1 = 2 * list.get(i);
            Integer integer2 = newList.get(i);
            assertEquals(integer1, integer2);
        }
    }

    private static final String SERIALIZATION_FILE_NAME = "LinkedList.ser";

    @Test
    public void serialization() {
        list.add(10);
        list.add(13);
        list.add(12);

        try {
            File file = new File(SERIALIZATION_FILE_NAME);

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(list);
            oos.flush();
            oos.close();

            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            IndexedLinkedList<Integer> ll = 
                    (IndexedLinkedList<Integer>) ois.readObject();

            ois.close();
            boolean equal = listsEqual(list, ll);
            assertTrue(equal);

            if (!file.delete()) {
                file.deleteOnExit();
            }

        } catch (IOException | ClassNotFoundException ex) {
            fail(ex.getMessage());
        }   
    }

    @Test
    public void bruteforceSerialization() {
        for (int i = 0; i < 20; i++) {
            list.addAll(getIntegerList(i));

            try {
                File file = new File(SERIALIZATION_FILE_NAME);

                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(list);
                oos.flush();
                oos.close();

                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);

                IndexedLinkedList<Integer> ll =
                        (IndexedLinkedList<Integer>) ois.readObject();

                ois.close();
                boolean equal = listsEqual(list, ll);
                assertTrue(equal);

                if (!file.delete()) {
                    file.deleteOnExit();
                }

            } catch (IOException | ClassNotFoundException ex) {
                fail(ex.getMessage());
            }   

            list.clear();
        }
    }
    
    @Test
    public void bugCheckInvariantAfterRemoval() {
        for (int i = 0; i < 4; i++) {
            list.add(i);
        }
        
        list.remove(Integer.valueOf(3));
        list.remove(1);
        assertEquals(list.size(), 2);
        assertEquals(Integer.valueOf(0), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
    }
    
    @Test
    public void bruteForceRemoveAt1() {
        Random random = new Random(400L);
        
        list.addAll(getIntegerList(1000));
        List<Integer> referenceList = new ArrayList<>(list);
        
        Integer probe = Integer.valueOf(3);
        
        list.remove(probe);
        referenceList.remove(probe);
        
        int iters = 0;
        
        while (!list.isEmpty()) {
            iters++;
            int index = random.nextInt(list.size());
            list.remove(index);
            referenceList.remove(index);
            
            listsEqual(list, referenceList);
        } 
    }
    
    @Test
    public void contractAdaptsToMinimumCapacity() {
        list.addAll(getIntegerList(1000_000));
        list.subList(10, 1000_000 - 10).clear();
        list.checkInvarant();
        assertEquals(20, list.size());
    }
    
    @Test
    public void bruteForceRemoveAt2() {
        long seed = 1630487847317L;
        Random random = new Random(seed);
        
        for (int i = 0; i < 100; i++) {
            list.addAll(getIntegerList(10));
            List<Integer> indices = new ArrayList<>(list.size());
            
            while (!list.isEmpty()) {
                int index = random.nextInt(list.size());
                indices.add(index);
                
                try {
                    list.remove(index);
                } catch (AssertionError ae) {
                    System.out.println(
                            "Message: " + ae.getMessage() + ", indices: " + 
                                    indices.toString());
                    return;
                }
            }
            
            indices.clear();
        }
    }
    
    @Test
    public void bugRemoveAt2() {
        list.addAll(getIntegerList(10));
        final int[] indices = { 7, 7, 4, 1, 2, 1, 3, 1, 1, 0 };
        
        for (int i = 0; i < indices.length; i++) {
            final int index = indices[i];
            list.remove(index);
        }
    }
    
    @Test
    public void bugRemoveAt() {
        list.addAll(getIntegerList(10));
        
        assertEquals(Integer.valueOf(5), list.remove(5));
        
        assertEquals(Integer.valueOf(3), list.remove(3));
        
        assertEquals(Integer.valueOf(2), list.remove(2));
        
        assertEquals(Integer.valueOf(1), list.remove(1));
        
        // list = [0, 4, 5, 7, 8, 8]
        assertEquals(Integer.valueOf(8), list.remove(4));    
    }
    
    @Test
    public void bugRemoveFirst() {
        list.addAll(getIntegerList(5));
        
        assertEquals(5, list.size());
        
        for (int i = 0; i < 2; i++) {
            list.removeFirst();
        }
        
        Random random = new Random(500L);
        List<Integer> referenceList = new ArrayList<>(list);
        
        while (!list.isEmpty()) {
            int index = random.nextInt(list.size());
            list.remove(index);
            referenceList.remove(index);
            assertTrue(listsEqual(list, referenceList));
        }
    }
    
    @Test
    public void bugRemoveLast() {
        list.addAll(getIntegerList(10));
        
        assertEquals(10, list.size());
        
        for (int i = 0; i < 5; i++) {
            list.removeLast();
        }
        
        Random random = new Random(600L);
        List<Integer> referenceList = new ArrayList<>(list);
        
        while (!list.isEmpty()) {
            int index = random.nextInt(list.size());
            list.remove(index);
            referenceList.remove(index);
            assertTrue(listsEqual(list, referenceList));
        }
    }

    private static List<Integer> getIntegerList(int length) {
        List<Integer> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            list.add(i);
        }

        return list;
    }   

    private static List<Integer> getIntegerList() {
        return getIntegerList(100);
    }
    
    private static boolean listsEqual(IndexedLinkedList<Integer> list1,
                                      List<Integer> list2) {

        if (list1.size() != list2.size()) {
            return false;
        }

        Iterator<Integer> iter1 = list1.iterator();
        Iterator<Integer> iter2 = list2.iterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            Integer int1 = iter1.next();
            Integer int2 = iter2.next();

            if (!int1.equals(int2)) {
                return false;
            }
        }

        if (iter1.hasNext() || iter2.hasNext()) {
            throw new IllegalStateException();
        }

        return true;
    }   
}
