/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.list;


import org.apache.commons.collections4.list.IndexedLinkedList.BasicIterator;
import org.apache.commons.collections4.list.IndexedLinkedList.DescendingIterator;
import org.apache.commons.collections4.list.IndexedLinkedList.EnhancedIterator;
import org.apache.commons.collections4.list.IndexedLinkedList.EnhancedSubList;
import org.apache.commons.collections4.list.IndexedLinkedList.Finger;
import org.apache.commons.collections4.list.IndexedLinkedList.Node;
import static org.apache.commons.collections4.list.IndexedLinkedList.checkIndex;

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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//import org.junit.Before;
//import org.junit.Test;

public class IndexedLinkedListTest {

    private final IndexedLinkedList<Integer> list = new IndexedLinkedList<>();
    private final List<Integer> referenceList = new ArrayList<>();
    
    @BeforeEach
    public void setUp() {
        list.clear();
        referenceList.clear();
    }
    
    @Test
    public void debugAddAtIndex() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        
        list.add(0, 0);
        referenceList.add(0, 0);
        
        for (int i = 1; i < 100; ++i) {
            int index = random.nextInt(list.size());
            list.add(index, i);
            list.checkInvarant();
            referenceList.add(index, i);
            assertEquals(referenceList, list);
        }
        
    }
    
    @Test
    public void debugTryPushFingersToRight1() {
        list.addAll(getIntegerList(17));
        list.fingerList.setFingerIndices(2, 3, 4, 6, 7);
        list.remove(2);
        list.checkInvarant();
    }
    
    @Test
    public void onEmptyFingerListNonNullHead() {
        list.head = new Node<>(3);
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onEmptyFingerListNonNullTail() {
        list.tail = new Node<>(3);
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onEmptyFingerListNonEmptyList() {
        list.addAll(getIntegerList(13));
        list.fingerList.size = 0;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onNegativeFirstFingerNodeIndex() {
        list.addAll(getIntegerList(13));
        list.fingerList.getFinger(0).index = -1;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onTooMissingSecondFinger() {
        list.addAll(getIntegerList(5));
        list.fingerList.setFingerIndices(0, 1, 2);
        list.fingerList.fingerArray[1] = null;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test 
    public void onTooMissingFirstFinger() {
        list.addAll(getIntegerList(5));
        list.fingerList.setFingerIndices(0, 1, 2);
        list.fingerList.fingerArray[0] = null;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onInvalidListsize() {
        list.addAll(getIntegerList(5));
        list.size = 6;
        list.fingerList.getFinger(3).index = 6;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test 
    public void onBadRecommendation() {
        list.addAll(getIntegerList(5));
        list.size = 4;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onNullSentinel() {
        list.addAll(getIntegerList(16));
        list.fingerList.setFinger(4, null);
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onFingerListMismatch() {
        list.addAll(getIntegerList(16));
        list.fingerList.size = 5;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onWrongFingerListSize() {
        list.addAll(getIntegerList(16));
        list.fingerList.size = 5;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onJunkFingers() {
        list.addAll(getIntegerList(16));
        list.fingerList.fingerArray[6] = new Finger<>(new Node<>(666), -13);
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void onNonContractedFingerArray() {
        list.addAll(getIntegerList(16));
        list.fingerList.fingerArray = 
                Arrays.copyOf(list.fingerList.fingerArray, 100);
        
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void getEntropy() {
        list.addAll(getIntegerList(25));
        list.fingerList.setFingerIndices(0, 5, 10, 15, 20);
        assertEquals(1.0, list.getEntropy(), 0.001);
    }
    
    @Test
    public void randomizeFingers() {
        list.addAll(getIntegerList(10));
        list.randomizeFingers();
        list.randomizeFingers(new Random(3L));
        list.randomizeFingers(4L);
    }
    
    @Test
    public void strongEquals() {
        list.addAll(getIntegerList(10));
        IndexedLinkedList<Integer> other = new IndexedLinkedList<>();
        
        for (int i = 0; i < list.size(); ++i) {
            assertNotEquals(other, list);
            assertFalse(list.strongEquals(other));
            other.add(i);
        }
        
        assertEquals(other, list);
        assertFalse(list.strongEquals(other));
        
        list .fingerList.setFingerIndices(2, 4, 7, 8);
        other.fingerList.setFingerIndices(2, 4, 7, 8);
        
        assertTrue(list.strongEquals(other));
    }
    
    @Test
    public void deoptimizeOnEmptyList() {
        list.deoptimize();
        assertTrue(list.fingerList.isEmpty());
        assertEquals(0, list.fingerList.getFinger(0).index);
        assertNull(list.fingerList.getFinger(0).node);
    }
    
    @Test
    public void deoptimizeOnOneFinger() {
        list.add(10);
        assertEquals(1, list.fingerList.size());
        assertEquals(Integer.valueOf(10),
                     list.fingerList.getFinger(0).node.item);
        
        assertEquals(0, list.fingerList.getFinger(0).index);
        
        assertNull(list.fingerList.getFinger(1).node);
        assertEquals(1, list.fingerList.getFinger(1).index);
    }
    
    @Test
    public void deoptimize2Fingers() {
        list.addAll(getIntegerList(4));
        list.deoptimize();
        
        IndexedLinkedList<Integer> other = new IndexedLinkedList<>(list);
        
        other.fingerList.setFingerIndices(0, 3);
        
        assertEquals(other.fingerList, list.fingerList);
    }
    
    @Test
    public void deoptimize3Fingers() {
        list.addAll(getIntegerList(8));
        list.deoptimize();
        
        IndexedLinkedList<Integer> other = new IndexedLinkedList<>(list);
        
        other.fingerList.setFingerIndices(0, 6, 7);
        
        assertEquals(other.fingerList, list.fingerList);
    }
    
    @Test
    public void deoptimize4Fingers() {
        list.addAll(getIntegerList(11));
        list.deoptimize();
        
        IndexedLinkedList<Integer> other = new IndexedLinkedList<>(list);
        
        other.fingerList.setFingerIndices(0, 1, 9, 10);
        
        assertEquals(other.fingerList, list.fingerList);
    }
    
    @Test 
    public void removeIntAtZeroIndex() {
        referenceList.addAll(Arrays.asList(1, 2, 3, 4, 5));
        list.addAll(referenceList);
        int iteration = 0;
        
        while (!referenceList.isEmpty()) {
            referenceList.remove(0);
            list.remove(0);
            list.checkInvarant();
            assertEquals(referenceList, list);
        }
        
        list.checkInvarant();
        assertEquals(referenceList, list);
    }
    
    @Test
    public void tryGetFingerListContract() {
        list.addAll(getIntegerList(10));
        assertEquals(4, list.fingerList.size());
        list.remove(5);
        assertEquals(3, list.fingerList.size());
    }
    
    @Test
    public void deepCopy() {
        list.addAll(getIntegerList(26));
        list.randomizeFingers(11L);
        final IndexedLinkedList<Integer> copy = list.deepCopy();
        assertTrue(listsEqual(list, copy));
        assertEquals(list.fingerList, copy.fingerList);
    }
    
    @Test 
    public void removeIntAtLastIndex() {
        referenceList.addAll(Arrays.asList(1, 2, 3, 4, 5));
        list.addAll(referenceList);
        
        while (!referenceList.isEmpty()) {
            referenceList.remove(referenceList.size() - 1);
            list.remove(list.size() - 1);
            list.checkInvarant();
            assertEquals(referenceList, list);
        }
        
        list.checkInvarant();
        assertEquals(referenceList, list);
    }
    
    @Test 
    public void singleElementListIterator() {
        ListIterator<Integer> referenceListIterator = 
                referenceList.listIterator();
        
        ListIterator<Integer> myListIterator = list.listIterator();
        
        assertEquals(referenceListIterator.previousIndex(), 
                     myListIterator.previousIndex());
        
        assertEquals(referenceListIterator.nextIndex(), 
                     myListIterator.nextIndex());
        
        referenceListIterator.add(1);
        myListIterator.add(1);
        
        assertEquals(referenceList, list);
        assertEquals(referenceListIterator.previousIndex(),
                     myListIterator.previousIndex());
        
        assertEquals(referenceListIterator.nextIndex(),
                     myListIterator.nextIndex());
        
        assertEquals(referenceListIterator.previous(), 
                     myListIterator.previous());
        
        try {
            referenceListIterator.previous();
            fail();
        } catch (Exception ex) {
            
        }
        
        try {
            myListIterator.previous();
            fail();
        } catch (Exception ex) {
            
        }
        
        assertEquals(referenceListIterator.next(), myListIterator.next());
        assertEquals(referenceListIterator.previous(),
                     myListIterator.previous());
        
        assertEquals(referenceListIterator.next(), myListIterator.next());
        
        try {
            referenceListIterator.next();
            fail();
        } catch (Exception ex) {
            
        }
        
        try {
            myListIterator.next();
            fail();
        } catch (Exception ex) {
            
        }
        
        assertEquals(referenceListIterator.previous(),
                     myListIterator.previous());
        
        referenceListIterator.remove();
        myListIterator.remove();
        
        assertEquals(referenceList, list);
        
        list.checkInvarant();
    }
    
    @Test 
    public void twoElementsListIterator() {
        ListIterator<Integer> referenceListIterator = 
                referenceList.listIterator();
        
        ListIterator<Integer> myListIterator = list.listIterator();
        
        referenceListIterator.add(1);
        referenceListIterator.add(2);
        
        myListIterator.add(1);
        myListIterator.add(2);
        
        try {
            myListIterator.next();
            fail();
        } catch (Exception ex) {
            
        }
        
        try {
            referenceListIterator.next();
            fail();
        } catch (Exception ex) {
            
        }
        
        assertEquals(referenceListIterator.nextIndex(), 
                     myListIterator.nextIndex());
        
        assertEquals(referenceListIterator.previousIndex(), 
                     myListIterator.previousIndex());
        
        assertEquals(referenceListIterator.hasNext(), myListIterator.hasNext());
        assertEquals(referenceListIterator.hasPrevious(),
                     myListIterator.hasPrevious());
        
        assertEquals(referenceListIterator.previous(), 
                     myListIterator.previous());
        
        assertEquals(referenceListIterator.hasNext(), myListIterator.hasNext());
        assertEquals(referenceListIterator.hasPrevious(),
                     myListIterator.hasPrevious());
        
        assertEquals(referenceListIterator.previous(), 
                     myListIterator.previous());
        
        assertEquals(referenceListIterator.hasNext(), myListIterator.hasNext());
        assertEquals(referenceListIterator.hasPrevious(),
                     myListIterator.hasPrevious());
        
        try {
            myListIterator.previous();
            fail();
        } catch (Exception ex) {
            
        }
        
        try {
            referenceListIterator.previous();
            fail();
        } catch (Exception ex) {
            
        }
        
        assertEquals(referenceList, list);
        
        list.clear();
        referenceList.clear();
        
        myListIterator = list.listIterator();
        referenceListIterator = referenceList.listIterator();
        
        myListIterator.add(2);
        referenceListIterator.add(2);
        
        assertEquals(referenceList, list);
        
        assertEquals(referenceListIterator.nextIndex(), 
                     myListIterator.nextIndex());
        
        assertEquals(referenceListIterator.previousIndex(), 
                     myListIterator.previousIndex());
        
        assertEquals(referenceListIterator.previous(), 
                     myListIterator.previous());
        
        referenceListIterator.add(1);
        myListIterator.add(1);
        
        assertEquals(referenceList, list);
        
        assertEquals(referenceListIterator.nextIndex(), 
                     myListIterator.nextIndex());
        
        assertEquals(referenceListIterator.previousIndex(), 
                     myListIterator.previousIndex());
        
        assertEquals(referenceListIterator.next(), myListIterator.next());
        
        assertEquals(referenceListIterator.hasNext(), 
                     myListIterator.hasNext());
        
        assertEquals(referenceListIterator.hasPrevious(), 
                     myListIterator.hasPrevious());
        
        assertEquals(referenceListIterator.previous(),
                     myListIterator.previous());
        
        assertEquals(referenceListIterator.hasNext(), 
                     myListIterator.hasNext());
        
        assertEquals(referenceListIterator.hasPrevious(), 
                     myListIterator.hasPrevious());
        
        assertEquals(referenceListIterator.previous(),
                     myListIterator.previous());
        
        assertEquals(referenceListIterator.hasNext(), 
                     myListIterator.hasNext());
        
        assertEquals(referenceListIterator.hasPrevious(), 
                     myListIterator.hasPrevious());
        
        list.checkInvarant();
    }
    
    @Test
    public void breakInvariant1() {
        list.add(11);
        list.add(12);
        list.add(13);
        
        list.fingerList.fingerArray[0].index = 1;
        list.fingerList.fingerArray[1].index = 0;
        
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test 
    public void breakInvariant2() {
        list.add(11);
        list.add(12);
        list.add(13);
        
        list.fingerList.fingerArray[list.fingerList.size()].index = 
                list.fingerList.size() + 10;
        
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void breakInvariant3() {
        list.add(11);
        list.add(12);
        list.add(13);
        
        list.fingerList.fingerArray[list.fingerList.size()].node = 
                new Node<>(null);
        
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void breakInvariant4() {
        list.add(11);
        list.add(12);
        list.add(13);
        list.size = 2;
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void breakInvariant5() {
        list.add(11);
        list.add(12);
        list.add(13);
        
        list.fingerList.fingerArray[2] = new Finger<>(new Node<>(100), 2);
        list.fingerList.fingerArray[3] = new Finger<>(null, 3);
        list.fingerList.size = 3;
        
        assertThrows(IllegalStateException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void nodeToString() {
        Node<Integer> node = new Node<>(12);
        assertEquals("<item = 12>", node.toString());
    }
    
    @Test 
    public void debugAdjustOnRemoveFirst() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        list.fingerList.fingerArray[0].index = 0;
        list.fingerList.fingerArray[1].index = 1;
        list.fingerList.fingerArray[2].index = 3;
        list.fingerList.fingerArray[3].index = 5;
        
        list.fingerList.fingerArray[0].node = list.head;
        list.fingerList.fingerArray[1].node = list.head.next;
        list.fingerList.fingerArray[2].node = list.head.next.next.next;
        
        list.removeFirst();
        list.checkInvarant();
    }
    
    @Test 
    public void addFirstLarge() {
        List<Integer> l = getIntegerList(1000);
        
        for (int i = 0; i < l.size(); i++) {
            list.addFirst(l.get(i));
            list.checkInvarant();
        }
        
        Collections.reverse(l);
        assertTrue(listsEqual(list, l));
        list.checkInvarant();
    }
    
    @Test 
    public void addAllAtIndexLarge() {
        Random random = new Random(1003L);
        referenceList.clear();
        
        for (int i = 0; i < 100; ++i) {
            int index = random.nextInt(list.size() + 1);
            List<Integer> coll = getIntegerList(random.nextInt(100));
            list.addAll(index, coll);
            list.checkInvarant();
            referenceList.addAll(index, coll);
        }
        
        list.checkInvarant();
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
        assertFalse(list.contains(1));
        assertFalse(list.contains(2));
        assertFalse(list.contains(3));
        
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        
        list.checkInvarant();
        list.addAll(Arrays.asList(1, 2, 3));
        
        assertEquals(3, list.size());
        assertFalse(list.isEmpty());
        
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        
        list.checkInvarant();
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
        
        list.checkInvarant();
        iterator.next();
        iterator.remove();
        list.checkInvarant();
        
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
        list.checkInvarant();
        list.get(10);
        list.subList(5, 100).clear();
        list.checkInvarant();
        assertEquals(Arrays.asList(0, 1, 2, 3, 4), list);
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
                new Finger<>(list.tail.prev.prev.prev, 12);
        
        list.fingerList.fingerArray[1] = new Finger<>(list.tail.prev.prev, 13);
        list.fingerList.fingerArray[2] = new Finger<>(list.tail.prev, 14);
        list.fingerList.fingerArray[3] = new Finger<>(list.tail, 15);
        
        list.checkInvarant();
        list.remove(12);
        list.checkInvarant();
        
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
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(4), iter.next());
        iter.remove();
        list.checkInvarant();
        
        assertEquals(3, list.size());
        
        assertEquals(Integer.valueOf(3), iter.next());
        iter.remove();
        list.checkInvarant();
        
        assertEquals(2, list.size());
        
        assertEquals(Integer.valueOf(2), iter.next());
        assertEquals(Integer.valueOf(1), iter.next());
        
        iter.remove();
        list.checkInvarant();
        
        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(2), list.get(0));
    }
    
    @Test
    public void elementThrowsOnEmptyList() {
        list.element();
        assertThrows(NoSuchElementException.class, () -> list.checkInvarant());
    }
    
    @Test 
    public void removeRangeBug() {
        for (int i = 0; i < 40_000; i++) {
            list.add(Integer.MIN_VALUE);
        }
        
        list.subList(0, 500).clear();
        list.checkInvarant();
        list.subList(20411, 20911).clear();
        list.checkInvarant();
        assertEquals(39000, list.size());
    }
    
    @Test 
    public void removeRangeBug2() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        list.subList(0, 7).clear();
        list.checkInvarant();
        assertEquals(Arrays.asList(7, 8, 9, 10), list);
        list.subList(0, 2).clear();
        list.checkInvarant();
        assertEquals(Arrays.asList(9, 10), list);
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
        list.checkInvarant();
        
        assertFalse(list.equals(otherList));
        
        assertFalse(list.equals(null));
        assertTrue(list.equals(list));
        
        Set<Integer> set = new HashSet<>(list);
        
        assertFalse(list.equals(set));
        
        list.clear();
        list.checkInvarant();
        list.addAll(Arrays.asList(0, 1, 2, 3));
        list.checkInvarant();
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
                return 0;
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
        list.checkInvarant();
        List<Integer> sublist = list.subList(49, 51);
        assertEquals(2, sublist.size());
        sublist.clear();
        list.checkInvarant();
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
        list.checkInvarant();
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
    public void bruteforceCollectionSetAll() {
        for (int sz = 0; sz < 10; ++sz) {
            list.clear();
            list.checkInvarant();
            referenceList.clear();

            List<Integer> tmpList = getIntegerList(sz);

            list.addAll(tmpList);
            list.checkInvarant();
            referenceList.addAll(tmpList);
            
            assertEquals(referenceList, list);
        }
    }
    
    @Test
    public void bruteforceCollectionAppend() {
        for (int sz = 0; sz < 10; ++sz) {
            for (int sz2 = 0; sz2 < 10; ++sz2) {
                list.clear();
                list.addAll(getIntegerList(sz));
                list.checkInvarant();
                
                referenceList.clear();
                referenceList.addAll(list);
                
                List<Integer> tmpList = getIntegerList(sz2);
                
                list.addAll(tmpList);
                list.checkInvarant();
                
                referenceList.addAll(tmpList);
                
                assertEquals(referenceList, list);
            }
        }
    }
    
    @Test
    public void bruteforceCollectionPrepend() {
        for (int sz = 0; sz < 10; ++sz) {
            for (int sz2 = 0; sz2 < 10; ++sz2) {
                list.clear();
                list.addAll(getIntegerList(sz));
                list.checkInvarant();
                
                referenceList.clear();
                referenceList.addAll(list);
                
                List<Integer> tmpList = getIntegerList(sz2);
                
                list.addAll(0, tmpList);
                list.checkInvarant();
                
                referenceList.addAll(0, tmpList);
                
                assertEquals(referenceList, list);
            }
        }
    }
    
    @Test
    public void bruteforceCollectionInsert() {
        for (int sz = 0; sz < 10; ++sz) {
            for (int sz2 = 0; sz2 < 10; ++sz2) {
                for (int index = 0; index <= sz; ++index) {
                    list.clear();
                    list.addAll(getIntegerList(sz));
                    list.checkInvarant();

                    referenceList.clear();
                    referenceList.addAll(list);
                    
                    List<Integer> tmpList = getIntegerList(sz2);

                    list.addAll(index, tmpList);
                    list.checkInvarant();
                    
                    referenceList.addAll(index, tmpList);
                    
                    assertEquals(referenceList, list);
                }
            }
        }
    }
    
    @Test  
    public void debugClear2() {
        list.addAll(getIntegerList(10));
        list.subList(0, 4).clear();
        list.checkInvarant();
        referenceList.clear();
        referenceList.addAll(Arrays.asList(4, 5, 6, 7, 8, 9));
        assertEquals(referenceList, list);
        
        list.clear();
        list.addAll(getIntegerList(10));
        list.subList(6, 10).clear();
        list.checkInvarant();
        referenceList.clear();
        referenceList.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        assertEquals(referenceList, list);
    }
    
    @Test 
    public void subListClear2Fingers3Nodes_1() {
        list.addAll(Arrays.asList(0, 1, 2));
        list.subList(0, 1).clear();
        list.checkInvarant();
        assertEquals(Arrays.asList(1, 2), list);
    }
    
    @Test 
    public void sublistClear6() {
        list.addAll(getIntegerList(1000));
        list.subList(70, 1000).clear();
        list.checkInvarant();
    }
    
    @Test 
    public void removeRangeNodes() {
        list.addAll(getIntegerList(20));
        list.subList(5, 10).clear();
        
        assertEquals(15, list.size());
        list.checkInvarant();
    }
    
    @Test
    public void bruteForceSubListClearFromTo() {
        List<Integer> data = getIntegerList(100);
        int iteration = 0;
        
        for (int fromIndex = 0; fromIndex <= 100; fromIndex++) {
            for (int toIndex = fromIndex; toIndex <= 100; toIndex++) {
                iteration++;
                
                list.clear();
                list.addAll(data);
                referenceList.clear();
                referenceList.addAll(data);
                
                list.subList(fromIndex, toIndex).clear();
                list.checkInvarant();
                    
                referenceList.subList(fromIndex, toIndex).clear();
                assertEquals(referenceList, list);
            }
        }
    }
    
    @Test
    public void removeRangeSmall1() {
        list.addAll(getIntegerList(9));
        list.fingerList.setFingerIndices(0, 1, 7);
        list.checkInvarant();
        referenceList.addAll(list);
        
        list.subList(4, 7).clear();
        list.checkInvarant();
        referenceList.subList(4, 7).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeSmall2() {
        list.addAll(getIntegerList(9));
        list.fingerList.setFingerIndices(4, 6, 7);
        list.checkInvarant();
        referenceList.addAll(list);
        
        list.subList(0, 3).clear();
        list.checkInvarant();
        referenceList.subList(0, 3).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeSmall3() {
        list.addAll(getIntegerList(9));
        list.fingerList.setFingerIndices(1, 2, 4);
        list.checkInvarant();
        referenceList.addAll(list);
        
        list.subList(6, 9).clear();
        list.checkInvarant();
        referenceList.subList(6, 9).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeSmall4() {
        list.addAll(getIntegerList(9));
        list.fingerList.setFingerIndices(2, 4, 7);
        list.checkInvarant();
        referenceList.addAll(list);
        
        list.subList(3, 6).clear();
        list.checkInvarant();
        referenceList.subList(3, 6).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
        public void removeRangeMedium1() {
        list.addAll(getIntegerList(100));
        list.fingerList.setFingerIndices(1, 10, 11, 12, 13, 40, 50, 90, 92, 93);
        list.checkInvarant();
        referenceList.addAll(list);
        
        list.subList(11, 13).clear();
        list.checkInvarant();
        referenceList.subList(11, 13).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeMedium2() {
        list.addAll(getIntegerList(100));
        list.fingerList.setFingerIndices(5, 15, 25, 35, 45, 55, 60, 61, 62, 63);
        list.checkInvarant();
        referenceList.addAll(list);
        
        list.subList(61, 63).clear();
        list.checkInvarant();
        referenceList.subList(61, 63).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangePrefix1() {
        list.addAll(getIntegerList(100));
        list.fingerList.setFingerIndices(0, 1, 2, 3, 10, 20, 30, 40, 50, 70);
        list.checkInvarant();
        
        referenceList.addAll(list);
        
        list.subList(0, 3).clear();
        list.checkInvarant();
        
        referenceList.subList(0, 3).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeSuffix1() {
        list.addAll(getIntegerList(100));
        list.fingerList.setFingerIndices(1, 10, 20, 25, 30, 31, 95, 96, 97, 98);
        list.checkInvarant();
        
        referenceList.addAll(list);
        
        list.subList(97, 100).clear();
        list.checkInvarant();
        
        referenceList.subList(97, 100).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeEqualFingersCoveringFingers() {
        list.addAll(getIntegerList(25));
        list.fingerList.setFingerIndices(1, 10, 11, 21, 23);
        list.checkInvarant();
        
        referenceList.addAll(list);
        
        list.subList(5, 21).clear();
        list.checkInvarant();
        
        referenceList.subList(5, 21).clear();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeCase4() {
        list.addAll(getIntegerList(100));
        referenceList.addAll(list);
        referenceList.subList(10, 80).clear();
        
        list.fingerList.setFingerIndices(1, 3, 5, 50, 51, 52, 95, 96, 98, 99);
        list.checkInvarant();
        
        list.subList(10, 80).clear();
        list.checkInvarant();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void removeRangeCase5() {
        list.addAll(getIntegerList(100));
        referenceList.addAll(list);
        referenceList.subList(6, 90).clear(); // rangelen = 84. 4 fingers, 6
                                              // off away.
        list.fingerList.setFingerIndices(
                10, 20, 30, 40, 50, 51, 52, 60, 93, 94);
        
        list.checkInvarant();
        
        list.subList(6, 90).clear();
        list.checkInvarant();
        
        assertEquals(referenceList, list);
    }
    
    @Test
    public void bruteForceSubListClearFromToWithRandomization666() {
        Random random = new Random(666L);
        
        List<Integer> data = getIntegerList(100);
        int iteration = 0;
        
        for (int fromIndex = 0; fromIndex <= 100; fromIndex++) {
            for (int toIndex = fromIndex; toIndex <= 100; toIndex++) {
                iteration++;
                
                list.clear();
                list.addAll(data);
                list.randomizeFingers(random);
                
                referenceList.clear();
                referenceList.addAll(data);
                
                list.subList(fromIndex, toIndex).clear();
                referenceList.subList(fromIndex, toIndex).clear();
                
                assertEquals(referenceList, list);
                list.checkInvarant();
            }
        }
    }
    
    @Test
    public void bruteForceSubListClearFromToWithRandomization13() {
        Random random = new Random(13L);
        
        List<Integer> data = getIntegerList(100);
        int iteration = 0;
        
        for (int fromIndex = 0; fromIndex <= 100; fromIndex++) {
            for (int toIndex = fromIndex; toIndex <= 100; toIndex++) {
                iteration++;
                
                list.clear();
                list.addAll(data);
                list.randomizeFingers(random);
                
                referenceList.clear();
                referenceList.addAll(data);
                
                list.subList(fromIndex, toIndex).clear();
                referenceList.subList(fromIndex, toIndex).clear();
                
                assertEquals(referenceList, list);
                list.checkInvarant();
            }
        }
    }
    
    @Test 
    public void bruteForceSublistClearOnSmallLists() {
        long seed = 1662121251795L;
        Random random = new Random(seed);
        
        for (int i = 0; i < 1000; ++i) {
            int size = 1 + random.nextInt(400);
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
    public void debugRemoveRange() {
        list.addAll(getIntegerList(15));
        list.subList(6, 11).clear();
        list.checkInvarant();
    }
    
    @Test 
    public void debugSmallSublistClear1() {
        list.addAll(getIntegerList(14));
        list.subList(3, 7).clear();
        list.checkInvarant();
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
        list.checkInvarant();
        subList1.remove(Integer.valueOf(0));
        list.checkInvarant();
        
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
    public void distributeFingersOnSmallerList() {
        list.addAll(getIntegerList(20));
        final Random random = new Random(13);
        Collections.shuffle(list, random);
        list.randomizeFingers(random);
        
        list.distributeFingers(5, list.size() - 3);
        list.checkInvarant();
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
            list.checkInvarant();
            list.subList(fromIndex, toIndex).sort(cmp);
            list.checkInvarant();
            referenceList.subList(fromIndex, toIndex).sort(cmp);
            
            assertEquals(referenceList, list);
        }
    }
    
    @Test 
    public void sortSubListOfSubList() {
        list.addAll(Arrays.asList(4, 1, 0, 2, 6, 8, 4, 1, 3));
        List<Integer> referenceList = new ArrayList<>(list);
        Comparator<Integer> cmp = Integer::compare;
        list.checkInvarant();
        list.subList(1, 7).subList(1, 4).sort(cmp);
        list.checkInvarant();
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
        list.checkInvarant();
        // list = <4, 8, 9, 2, 3, 0>
        assertEquals(Arrays.asList(4, 8, 9, 2, 3, 0), list);
        
        list.removeAll(Arrays.asList(-2, 8, 0));
        list.checkInvarant();
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
    public void noNextItemEnhancedSubList() {
        list.addAll(Arrays.asList(1, 2, 3, 4));
        List<Integer> subList = list.subList(0, 3);
        ListIterator<Integer> iter = subList.listIterator(3);
        iter.next();
        assertThrows(NoSuchElementException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void noPreviousItemEnhancedSubList() {
        list.addAll(Arrays.asList(1, 2, 3, 4));
        List<Integer> subList = list.subList(0, 3);
        ListIterator<Integer> iter = subList.listIterator(0);
        iter.previous();
        assertThrows(NoSuchElementException.class, () -> list.checkInvarant());
    }
    
    @Test
    public void removeIfOnNothing() {
        Predicate<Integer> predicate = (i) -> false; 
        list.addAll(Arrays.asList(1, 2, 4, 8));
        assertFalse(list.subList(0, 3).removeIf(predicate));
    }
    
    @Test
    public void checkInsertionIndexOnNegative() {
        list.addAll(Arrays.asList(1, 3, 5));
        EnhancedSubList sl = (EnhancedSubList) list.subList(0, 3);
        
        assertThrows(IndexOutOfBoundsException.class,
                     () -> sl.checkInsertionIndex(-1));
    }
    
    @Test
    public void checkInsertionIndexOnTooLarge() {
        list.addAll(Arrays.asList(1, 3, 5));
        EnhancedSubList sl = (EnhancedSubList) list.subList(0, 3);
        assertThrows(IndexOutOfBoundsException.class,
                     () -> sl.checkInsertionIndex(-1));
    }
    
    @Test
    public void returnsFalseIfDidNotRemoveObjects() {
        list.add(1);
        list.add(2);
        EnhancedSubList subList = (EnhancedSubList) list.subList(0, 2);
        
        assertFalse(subList.removeAll(Arrays.asList(4, 3)));
    }
    
    @Test
    public void retainAll() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        list.checkInvarant();
        list.retainAll(Arrays.asList(2, 3, 5, 7));
        list.checkInvarant();
        
        assertEquals(Arrays.asList(2, 3, 5), list);
        
        list.checkInvarant();
        list.retainAll(Arrays.asList(3));
        list.checkInvarant();
        
        assertEquals(Arrays.asList(3), list);
        
        list.checkInvarant();
        list.retainAll(Arrays.asList(0));
        list.checkInvarant();
        
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void toArrayGeneric() {
        list.addAll(Arrays.asList(3, 1, 2, 5, 4));
        
        Integer[] array = new Integer[7];
        
        array[5] = 10;
        array[6] = 11;
        
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
    
    @Test
    public void listIteratorSetAddThrows() {
        list.addAll(getIntegerList(10));
        ListIterator<Integer> listIterator = list.listIterator(3);
        
        list.checkInvarant();
        listIterator.add(100);
        list.checkInvarant();
        listIterator.set(-100);
        list.checkInvarant();
        
        assertThrows(IllegalStateException.class,
                     () -> list.checkInvarant());
    
    }
    
    @Test
    public void subListIteratorSetAddThrows() {
        list.addAll(getIntegerList(10));
        ListIterator<Integer> listIterator = list.subList(4, 9).listIterator(3);
        
        listIterator.add(100);
    
        assertThrows(IllegalStateException.class,
                     () -> listIterator.add(100));
    }
    
    @Test
    public void listIteratorRemoveWithouttNextPreviousThrows() {
        list.addAll(getIntegerList(5));
        ListIterator<Integer> iter = list.listIterator(1);
        assertThrows(IndexOutOfBoundsException.class,
                     () -> iter.remove());
    }
    
    @Test
    public void subListIteratorRemoveWithouttNextPreviousThrows() {
        list.addAll(getIntegerList(8));
        List<Integer> subList = list.subList(1, 6);
        ListIterator<Integer> iter = subList.listIterator(5);
        assertThrows(IndexOutOfBoundsException.class,
                     () -> iter.remove());
    }
    
    @Test
    public void listIteratorSetAdd() {
        list.addAll(getIntegerList(5));
        ListIterator<Integer> listIterator = list.listIterator(2);
        // list = <0, 1, 2, 3, 4>
        // iter:       |
        
        listIterator.add(100);
        list.checkInvarant();
        // list = <0, 1, 100, 2, 3, 4>
        assertEquals(Integer.valueOf(2), listIterator.next());
        listIterator.set(-100);
        // list = <0, 1, 100, -100, 3, 4>
        list.checkInvarant();
        
        assertEquals(Arrays.asList(0, 1, 100, -100, 3, 4), list);
        // list = <0, 1, 100, -100, 3, 4>
                     
        listIterator = list.listIterator(4);
        // list = <0, 1, 100, -100, 3, 4>
        // iter:                   |
        listIterator.add(1000);
        list.checkInvarant();
        // list = <0, 1, 100, -100, 1000, 3, 4>
        assertEquals(Arrays.asList(0, 1, 100, -100, 1000, 3, 4), list);
        
        assertEquals(Integer.valueOf(1000), listIterator.previous());
        listIterator.set(-1000);
        list.checkInvarant();
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
        list.checkInvarant();
        assertEquals(Arrays.asList(1, 2, 100, 3, 4, 5), subList);
        // subList = <1, 2, 100, 3, 4, 5>
        
        assertEquals(Integer.valueOf(3), listIterator.next());
        listIterator.set(-100);
        list.checkInvarant();
        // subList = <1, 2, 100, -100, 4, 5>
        // iter:                      |
        
        assertEquals(Arrays.asList(1, 2, 100, -100, 4, 5), subList);
        assertEquals(Integer.valueOf(4), listIterator.next()); 
        assertEquals(Integer.valueOf(5), listIterator.next()); 
        
        list.checkInvarant();
        listIterator.add(1000);
        list.checkInvarant();

        // list = <1, 2, 100, -100, 4, 5, 1000>
        assertEquals(Arrays.asList(1, 2, 100, -100, 4, 5, 1000), subList);
        
        assertEquals(Integer.valueOf(1000), listIterator.previous());
        list.checkInvarant();
        listIterator.set(-1000);
        list.checkInvarant();
        // list = <1, 2, 100, -100, 4, 5, -1000>
        
        assertEquals(Arrays.asList(1, 2, 100, -100, 4, 5, -1000), subList);
    }
    
    @Test
    public void listIteratorThrowsOnSetAfterRemove() {
        list.addAll(getIntegerList(8));
        ListIterator<Integer> iterator = list.listIterator(2);
        iterator.previous();
        list.checkInvarant();
        iterator.remove();
        list.checkInvarant();
        assertThrows(IllegalStateException.class,
                     () -> iterator.set(1000));
    }
    
    @Test
    public void subListIteratorThrowsOnSetAfterRemove() {
        list.addAll(getIntegerList(8));
        List<Integer> subList = list.subList(1, 7);
        
        ListIterator<Integer> iterator = subList.listIterator(2);
        iterator.previous();
        iterator.remove();
        assertThrows(IllegalStateException.class,
                     () -> iterator.set(1000));
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
        list.checkInvarant();
        list.addAll(Arrays.asList(-1, -2));
        list.checkInvarant();
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
        
        map.put(subList1, 200);
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
        list.checkInvarant();
        subList.add(1);
        list.checkInvarant();
        assertFalse(subList.isEmpty());
        list.checkInvarant();
        subList.remove(0);
        list.checkInvarant();
        assertTrue(subList.isEmpty());
    }
    
    @Test
    public void subListIterator() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        List<Integer> subList = list.subList(1, 5); // <2, 3, 4, 5>
        Iterator<Integer> iterator = subList.iterator();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        
        list.checkInvarant();
        iterator.remove();
        list.checkInvarant();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(4), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(5), iterator.next());
        
        list.checkInvarant();
        iterator.remove();
        list.checkInvarant();
        
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
        
        list.checkInvarant();
        iterator.remove(); // subList = <3, 4, 6, 7, 8>
        list.checkInvarant();
        
        assertEquals(Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9), list);
        assertEquals(Arrays.asList(3, 4, 6, 7, 8), subList);
        
        assertEquals(Integer.valueOf(4), iterator.previous());
        
        list.checkInvarant();
        iterator.remove(); // subList = <3, 6, 7, 8>
        list.checkInvarant();// [1, 2, 3, 6, 7, 8, 9]
        
        assertEquals(Integer.valueOf(6), iterator.next());
        assertEquals(Integer.valueOf(7), iterator.next());
        assertEquals(Integer.valueOf(8), iterator.next());
        
        assertFalse(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
    }
    
    @Test
    public void debugRemoveAtFigner() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9));
        
    }
    
    @Test
    public void subListRemoveObject() {
        list.addAll(Arrays.asList(3, 1, 2, 4, null, 5, null, 8, 1));
        List<Integer> subList = list.subList(2, 8);
        // subList = <2, 4, null, 5, null, 8>
        
        list.checkInvarant();
        subList.remove(null);
        list.checkInvarant();
        
        assertEquals(Arrays.asList(2, 4, 5, null, 8), subList);
        assertEquals(Arrays.asList(3, 1, 2, 4, 5, null, 8, 1), list);
        
        list.checkInvarant();
        subList.remove(Integer.valueOf(5));
        list.checkInvarant();
        
        assertEquals(Arrays.asList(2, 4, null, 8), subList);
        assertEquals(Arrays.asList(3, 1, 2, 4, null, 8, 1), list);
        
        list.checkInvarant();
        subList.remove(null);
        list.checkInvarant();
        
        assertEquals(Arrays.asList(2, 4, 8), subList);
        assertEquals(Arrays.asList(3, 1, 2, 4, 8, 1), list);
    }
    
    @Test
    public void subListRemoveInt() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        List<Integer> subList = list.subList(3, 5);
        
        assertEquals(Arrays.asList(4, 5), subList);
        
        list.checkInvarant();
        subList.remove(1);
        list.checkInvarant();
        
        assertEquals(Arrays.asList(4), subList);
        assertEquals(Arrays.asList(1, 2, 3, 4, 6, 7), list);
        
        list.checkInvarant();
        subList.remove(0);
        list.checkInvarant();
        
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
        
        list.checkInvarant();
        
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
        list.checkInvarant();
        
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
        
        list.checkInvarant();
        list.clear();
        list.checkInvarant();
        
        list.addAll(Arrays.asList(1, 3, 2, 1, 2, 3, 3, 1, 2, 0, 0));
        subList = list.subList(1, 6);
        // subList = <3, 2, 1, 2, 3>
        
        list.checkInvarant();
        subList.retainAll(Arrays.asList(3, 4, 5));
        list.checkInvarant();
        // subList = <3, 3>
        assertEquals(Arrays.asList(3, 3), subList);
        assertEquals(Arrays.asList(1, 3, 3, 3, 1, 2, 0, 0), list);
    }
    
    @Test
    public void subListSet() {
        list.addAll(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> subList = list.subList(1, 4);
        // subList = <2, 3, 4>
        list.checkInvarant();
        subList.set(0, 10);
        list.checkInvarant();
        subList.set(2, 11);
        list.checkInvarant();
        
        assertEquals(Arrays.asList(10, 3, 11), subList);
        assertEquals(Arrays.asList(1, 10, 3, 11, 5), list);
    }
    
    @Test
    public void subListSort() {
        Random random = new Random(1001L);
        
        for (int i = 0; i < 100; ++i) {
            int value = random.nextInt(100) % 75;
            list.add(value);
        }
        
        Collections.shuffle(list);
        List<Integer> referenceList = new ArrayList<>(list);
        
        list.subList(10, 80).sort(Integer::compare);
        list.checkInvarant();
        
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
            
            list.checkInvarant();
            list.subList(fromIndex, toIndex).clear();
            list.checkInvarant();
            referenceList.subList(fromIndex, toIndex).clear();
            assertEquals(referenceList, list);
        }
    }
    
    @Test
    public void listEqualsThrowsOnBadIterator() {
        DummyList dummyList = new DummyList();
        list.addAll(Arrays.asList(0, 0));
        
        assertThrows(IllegalStateException.class,
                     () -> listsEqual(list, dummyList));
    }
    
    @Test
    public void offer() {
        assertTrue(list.equals(Arrays.asList()));
        
        list.checkInvarant();
        list.offer(1);
        list.checkInvarant();
        
        assertTrue(list.equals(Arrays.asList(1)));
        
        list.offer(2);
        list.checkInvarant();
        
        assertTrue(list.equals(Arrays.asList(1, 2)));
    }
    
    @Test
    public void offerFirst() {
        assertTrue(list.equals(Arrays.asList()));
        
        list.checkInvarant();
        list.offerFirst(1);
        list.checkInvarant();
        
        assertTrue(list.equals(Arrays.asList(1)));
        
        list.offerFirst(2);
        list.checkInvarant();
        
        assertTrue(list.equals(Arrays.asList(2, 1)));
    }
    
    @Test
    public void offerLast() {
        assertTrue(list.equals(Arrays.asList()));
        
        list.checkInvarant();
        list.offerLast(1);
        list.checkInvarant();
        
        assertTrue(list.equals(Arrays.asList(1)));
        
        list.offerLast(2);
        list.checkInvarant();
        
        assertTrue(list.equals(Arrays.asList(1, 2)));
    }
    
    @Test
    public void peek() {
        assertNull(list.peek());
        
        list.addLast(0);
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(0), list.peek());
        
        list.addLast(1);
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(0), list.peek());
    
        list.addFirst(-1);
        list.checkInvarant();

        assertEquals(Integer.valueOf(-1), list.peek());
    }
    
    @Test
    public void sortEmpty() {
        list.sort((c1,c2) -> { return -1; });
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void sublistRangeCheck1() {
        list.add(1);
        list.subListRangeCheck(-1, 2, 5);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void sublistRangeCheck2() {
        list.add(1);
        list.subListRangeCheck(0, 3, 2);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void sublistRangeCheck3() {
        list.add(1);
        list.subListRangeCheck(1, 0, 1);
    }
    
    @Test
    public void emptyBatchRemove() {
        list.batchRemove(new ArrayList<>(), true, 0, 1);
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void checkForComodification() {
        list.modCount = 123;
        list.checkForComodification(122);
    }
    
    @Test
    public void distributeFingers() {
        list.addAll(getIntegerList(10));
        list.distributeFingers(2, 2);
        list.checkInvarant();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void iteratorNextThrowsOnNoElements() {
        Iterator<Integer> iterator = 
                new IndexedLinkedList<>(Arrays.asList(2, 3)).iterator();
        
        try {
            iterator.next();
            iterator.next();
        } catch (NoSuchElementException ex) {
            fail("Should not throw here.");
        }
        
        iterator.next();
    }
    @Test(expected = IllegalStateException.class)
    public void iteratorNextThrowsOnRemove() {
        Iterator<Integer> iterator = 
                new IndexedLinkedList<>(Arrays.asList(2, 3)).iterator();
        
        try {
            iterator.next();
            iterator.next();
            iterator.remove();
        } catch (IllegalStateException ex) {
            fail("Should not throw here.");
        }
        
        iterator.remove();
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void checkIteratorComodification() {
        BasicIterator iterator = (BasicIterator)
                new IndexedLinkedList<>(Arrays.asList(2, 3)).iterator();
        
        iterator.expectedModCount = 1000;
        iterator.checkForComodification();
    }
    
    @Test
    public void removeEntryRange() {
        list.addAll(Arrays.asList(1, 2, 3));
        list.removeRange(0, 3);
    }
    
    @Test
    public void addAllEmpty() {
        list.addAll(0, new ArrayList<>());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void listIteratorNext() {
        ListIterator<Integer> iterator = 
                new IndexedLinkedList<>(Arrays.asList(1,2,3)).listIterator(1);
        
        try {
            iterator.next();
            iterator.next();
        } catch (NoSuchElementException ex) {
            fail("Should not throw here.");
        }
        
        iterator.next();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void listIteratorPrev() {
        ListIterator<Integer> iterator = 
                new IndexedLinkedList<>(Arrays.asList(1,2,3)).listIterator(1);
        
        try {
            iterator.previous();
        } catch (NoSuchElementException ex) {
            fail("Should not throw here.");
        }
        
        iterator.previous();    
    }
    
    @Test
    public void forRemainingOnEmptyList() {
        new IndexedLinkedList<>().iterator().forEachRemaining((a) -> {});
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void checkFromToOnNegativeFromIndex() {
        list.checkFromTo(-1, 2);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void checkFromToOnTooLargeToIndex() {
        list.add(1);
        list.add(3);
        
        list.checkFromTo(0, 3);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void checkFromToOnBackwardIndices() {
        list.add(1);
        list.add(3);
        
        list.checkFromTo(2, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void checkIndexOnNegativeSize() {
        checkIndex(0, -1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOnNegativeIndex() {
        checkIndex(-1, 1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOnTooLargeIndex() {
        checkIndex(2, 2);
    }
    
    @Test
    public void equalsRangeShortArgList() {
        list.addAll(Arrays.asList(1, 2, 3, 4));
        referenceList.addAll(Arrays.asList(1, 2, 3));
        assertFalse(list.equals(referenceList));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void noHasNext() {
        list.iterator().next();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void noHasNextDescending() {
        list.descendingIterator().next();
    }
    
    @Test(expected = IllegalStateException.class)
    public void throwsOnDoubleRemove() {
        list.add(10);
        Iterator<Integer> it = list.iterator();
        it.next();
        it.remove();
        it.remove();
    }
    
    @Test(expected = IllegalStateException.class)
    public void throwsOnDoubleRemoveDescending() {
        list.add(10);
        Iterator<Integer> it = list.descendingIterator();
        it.next();
        it.remove();
        it.remove();
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void throwOnConcurrency() {
        list.addAll(Arrays.asList(1, 2, 3));
        Iterator<Integer> it = list.iterator();
        
        it.next();
        list.add(13);
        it.next();
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void throwOnConcurrencyDescending() {
        list.addAll(Arrays.asList(1, 2, 3));
        Iterator<Integer> it = list.descendingIterator();
        
        it.next();
        list.add(13);
        it.next();
    }
    
    @Test
    public void equalsHaltsOnShorterArgList() {
        list.addAll(Arrays.asList(1, 2, 3));
        referenceList.addAll(Arrays.asList(1, 2));
        assertFalse(list.equals(referenceList));
    }
    
    @Test
    public void peekFirst() {
        assertNull(list.peekFirst());
        
        list.checkInvarant();
        list.addLast(0);
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(0), list.peekFirst());
        
        list.checkInvarant();
        list.addFirst(1);
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(1), list.peekFirst());
    
        list.addFirst(-1);

        assertEquals(Integer.valueOf(-1), list.peekFirst());
    }
    
    @Test
    public void peekLast() {
        assertNull(list.peekLast());
        
        list.addLast(0);
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(0), list.peekLast());
        
        list.checkInvarant();
        list.addLast(1);
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(1), list.peekLast());
    
        list.checkInvarant();
        list.addLast(2);
        list.checkInvarant();

        assertEquals(Integer.valueOf(2), list.peekLast());
    }
    
    @Test
    public void poll() {
        assertNull(list.poll());
        
        list.addAll(Arrays.asList(1, 2, 3));
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(1), list.poll());
        list.checkInvarant();
        assertEquals(Integer.valueOf(2), list.poll());
        list.checkInvarant();
        assertEquals(Integer.valueOf(3), list.poll());
        list.checkInvarant();
    }
    
    @Test
    public void pollFirst() {
        assertNull(list.pollFirst());
        
        list.addAll(Arrays.asList(1, 2, 3));
        
        list.checkInvarant();
        assertEquals(Integer.valueOf(1), list.pollFirst());
        list.checkInvarant();
        assertEquals(Integer.valueOf(2), list.pollFirst());
        list.checkInvarant();
        assertEquals(Integer.valueOf(3), list.pollFirst());
        list.checkInvarant();
    }
    
    @Test
    public void pollLast() {
        assertNull(list.pollLast());
        
        list.addAll(Arrays.asList(1, 2, 3));
        
        list.checkInvarant();
        assertEquals(Integer.valueOf(3), list.pollLast());
        list.checkInvarant();
        assertEquals(Integer.valueOf(2), list.pollLast());
        list.checkInvarant();
        assertEquals(Integer.valueOf(1), list.pollLast());
        list.checkInvarant();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void removeFirstThrowsOnEmptyList() {
        list.removeFirst();
    }
    
    @Test
    public void pop() {
        list.addAll(Arrays.asList(1, 2, 3));
        
        list.checkInvarant();
        assertEquals(Integer.valueOf(1), list.pop());
        list.checkInvarant();
        assertEquals(Integer.valueOf(2), list.pop());
        list.checkInvarant();
        assertEquals(Integer.valueOf(3), list.pop());
        list.checkInvarant();
    }
    
    @Test
    public void push() {
        list.push(1);
        list.checkInvarant();
        list.push(2);
        list.checkInvarant();
        list.push(3);
        list.checkInvarant();
        
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
                return 3;
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
    
    @Test
    public void badThisIterator() {
        list.addAll(Arrays.asList(3, 2));
        BadList badList = new BadList();
        badList.addAll(Arrays.asList(3, 3));
        Assert.assertNotEquals(badList, list);
    }
    
    @Test
    public void removeFirstOccurrenceOfNull() {
        list.addAll(Arrays.asList(1, 2, null, 4, null, 6));
        
        assertTrue(list.removeFirstOccurrence(null));
        list.checkInvarant();
        // Remove the last null value:
        list.set(3, 10);
        
        assertFalse(list.removeFirstOccurrence(null));
        list.checkInvarant();
    }
    
    @Test
    public void removeLastOccurrenceOfNull() {
        list.addAll(Arrays.asList(1, 2, null, 4, null, 6));
        
        assertTrue(list.removeLastOccurrence(null));
        list.checkInvarant();
        
        // Remove the last null value:
        list.set(2, 10);
        
        assertFalse(list.removeLastOccurrence(null));
        list.checkInvarant();
    }
    
    @Test
    public void appendAll() {
        list.addAll(Arrays.asList(0, 1, 2));
        
        List<Integer> arrayList = new ArrayList<>();
        
        for (int i = 3; i < 20_000; i++) {
            arrayList.add(i);
        }
        
        list.addAll(arrayList);
        list.checkInvarant();
        
        for (int i = 0; i < 20_000; i++) {
            assertEquals(Integer.valueOf(i), list.get(i));
        }
    }
    
    @Test
    public void smallListRemoveFirstFinger() {
        list.add(0);
        list.checkInvarant();
        list.add(1);
        list.checkInvarant();
        list.remove(0);
        list.checkInvarant();
    }
    
    @Test
    public void smallListRemoveSecondFinger() {
        list.checkInvarant();
        list.add(0);
        list.checkInvarant();
        list.add(1);
        list.checkInvarant();
        list.remove(1);
        list.checkInvarant();
    }
    
    @Test
    public void prependAll() {
        List<Integer> l = new ArrayList<>();
        
        for (int i = 0; i < 10_000; i++) {
            l.add(i);
        }
        
        list.addAll(l);
        list.checkInvarant();
        
        l = new ArrayList<>();
        
        for (int i = 10_000; i < 20_000; i++) {
            l.add(i);
        }
        
        list.addAll(0, l);
        list.checkInvarant();
        
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
        list.add(0);
        
        list.add(2, 1);
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
        
        list.checkInvarant();
        list.removeFirst();
        list.checkInvarant();
        
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
        list.checkInvarant();
        iter.remove();
        list.checkInvarant();
        iter.next();
        iter.remove();
        list.checkInvarant();
        
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
        list.checkInvarant();
        iter.remove();
        list.checkInvarant();
        iter.next();
        iter.next();
    }
    
    @Test(expected = IllegalStateException.class) 
    public void enhancedIteratorThrowsOnSetAfterRemove() {
        list.addAll(Arrays.asList(1, 2, 3, 4));
        
        ListIterator<Integer> iter = list.listIterator(1);
        
        iter.next();
        list.checkInvarant();
        iter.remove();
        list.checkInvarant();
        iter.set(10);
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void basicIteratorForEachRemainingThrowsOnConcurrentModification() {
        list.addAll(getIntegerList(1_000_000));
        
        BasicIterator iter = (BasicIterator) list.iterator();
        iter.expectedModCount = -1000;
        
        iter.forEachRemaining((e) -> {});
    }
    
    @Test
    public void addEmptyCollection() {
        list.subList(0, 0).addAll(0, Collections.emptyList());
    }
    
    @Test
    public void onEmptyCollectionSort() {
        list.subList(0, 0).sort(null);
    }
    
    @Test
    public void sublistRemoveNullNotMatched() {
        list.addAll(Arrays.asList(1, 2, 3));
        assertFalse(list.subList(0, 3).remove(null));
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void basicDescendingIteratorForEachRemainingThrowsOnConcurrentModification() {
        list.addAll(getIntegerList(1_000_000));
        
        DescendingIterator iter = (DescendingIterator) list.descendingIterator();
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
        
        list.checkInvarant();
        list.removeLast();
        list.checkInvarant();
        
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
        list.checkInvarant();

        assertEquals(1, list.size());
        assertFalse(list.isEmpty());

        assertEquals(Integer.valueOf(1), list.get(0));

        list.add(2);
        list.checkInvarant();

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
        list.checkInvarant();

        assertEquals(1, list.size());
        assertFalse(list.isEmpty());

        assertEquals(Integer.valueOf(1), list.get(0));

        list.addFirst(2);
        list.checkInvarant();

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
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(1), list.get(0));

        list.add(0, 2);
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));

        list.add(2, 10);
        list.checkInvarant();

        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(10), list.get(2));

        list.add(2, 100);
        list.checkInvarant();

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
        list.checkInvarant();

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
        list.checkInvarant();
        list.addAll(0, Arrays.asList(0, 1)); // prependAll
        list.checkInvarant();
        list.addAll(4, Arrays.asList(6, 7)); // appendAll
        list.checkInvarant();
        list.addAll(4, Arrays.asList(4, 5)); // insertAll
        list.checkInvarant();

        for (int i = 0; i < list.size(); i++) {
            assertEquals(Integer.valueOf(i), list.get(i));
        }
    }

    @Test
    public void removeInt() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));

        // [0, 1, 2, 3, 4]
        assertEquals(Integer.valueOf(0), list.remove(0));
        list.checkInvarant();
        // [1, 2, 3, 4]
        assertEquals(Integer.valueOf(4), list.remove(3));
        list.checkInvarant();
        // [1, 2, 3]
        assertEquals(Integer.valueOf(2), list.remove(1));
        list.checkInvarant();
        // [1, 3]
        assertEquals(Integer.valueOf(1), list.remove(0));
        list.checkInvarant();
        // [3]
        assertEquals(Integer.valueOf(3), list.remove(0));
        list.checkInvarant();
        // []
    }

    @Test
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
        list.checkInvarant();
        referenceList.remove(0);
        
        assertTrue(listsEqual(list, referenceList));
        
        list.removeFirst();
        list.checkInvarant();
        referenceList.remove(0);
        
        assertTrue(listsEqual(list, referenceList));
        
        list.removeLast();
        list.checkInvarant();
        referenceList.remove(referenceList.size() - 1);
        
        assertTrue(listsEqual(list, referenceList));
        
        list.removeLast();
        list.checkInvarant();
        referenceList.remove(referenceList.size() - 1);
        
        assertTrue(listsEqual(list, referenceList));
    }
    
    @Test(expected = ConcurrentModificationException.class)
    public void subListThrowsOnConcurrentModification() {
        List<Integer> l =
                new IndexedLinkedList<>(
                        Arrays.asList(1, 2, 3, 4));
        
        List<Integer> subList1 = l.subList(1, 4); // <2, 3, 4>
        List<Integer> subList2 = subList1.subList(0, 2); // <2, 3>
        
        subList1.add(1, 10);
        subList2.add(1, 11); // Must throw here.
    }
    
    @Test
    public void removeFirstLastOccurrence() {
        IndexedLinkedList<Integer> l = new IndexedLinkedList<>();
        
        list.addAll(Arrays.asList(1, 2, 3, 1, 2, 3)); // <1, 2, 3, 1, 2, 3>
        list.checkInvarant();
        l.addAll(list);
        
        list.removeFirstOccurrence(2); // <1, 3, 1, 2, 3>
        list.checkInvarant();
        l.removeFirstOccurrence(2);
        
        assertTrue(listsEqual(list, l));
        
        list.removeLastOccurrence(3); // <1, 3, 1, 2>
        list.checkInvarant();
        l.removeLastOccurrence(3);
        
        assertTrue(listsEqual(list, l));
    }

    @Test 
    public void bruteForceAddCollectionAtIndex() {
        Random random = new Random(100L);

        list.addAll(getIntegerList());

        referenceList.clear();
        referenceList.addAll(list);
        
        for (int op = 0; op < 100; op++) {
            int index = random.nextInt(list.size());
            Collection<Integer> coll = getIntegerList(random.nextInt(40));
            referenceList.addAll(index, coll);
            list.addAll(index, coll);
            list.checkInvarant();

            assertTrue(listsEqual(list, referenceList));
        }
    }

    @Test
    public void removeAtIndex() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        
        // [0, 1, 2, 3, 4]
        assertEquals(Integer.valueOf(2), list.remove(2));
        list.checkInvarant();
        // [0, 1, 3, 4]
        assertEquals(Integer.valueOf(0), list.remove(0));
        list.checkInvarant();
        // [1, 3, 4]
        assertEquals(Integer.valueOf(4), list.remove(2));
        list.checkInvarant();
        // [1, 3]
        assertEquals(Integer.valueOf(3), list.remove(1));
        list.checkInvarant();
        // [1]
        assertEquals(Integer.valueOf(1), list.remove(0));
        list.checkInvarant();
        // []
    }

    @Test
    public void removeObject() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));

        assertFalse(list.remove(Integer.valueOf(10)));
        list.checkInvarant();
        assertFalse(list.remove(null));
        list.checkInvarant();

        list.add(3, null);
        list.checkInvarant();

        assertTrue(list.remove(null));
        list.checkInvarant();

        assertTrue(list.remove(Integer.valueOf(4)));
        list.checkInvarant();
        assertTrue(list.remove(Integer.valueOf(0)));
        list.checkInvarant();
        assertTrue(list.remove(Integer.valueOf(2)));
        list.checkInvarant();
        assertFalse(list.remove(Integer.valueOf(2)));
    }

    @Test
    public void spreadOnSet() {
        list.addAll(getIntegerList(100));
        list.checkInvarant();
    }
    
    @Test
    public void spreadOnAppend() {
        list.addAll(getIntegerList(10));
        list.addAll(getIntegerList(90));
        list.checkInvarant();
    }
    
    @Test
    public void spreadOnPrepend() {
        list.addAll(getIntegerList(10));
        list.addAll(0, getIntegerList(90));
        list.checkInvarant();
    }
    
    @Test
    public void spreadOnInsert() {
        list.addAll(getIntegerList(20));
        list.addAll(10, getIntegerList(80));
        list.checkInvarant();
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
        list.checkInvarant();

        assertEquals(4, list.size());

        iter = list.iterator();
        iter.next();
        iter.remove();
        list.checkInvarant();

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
        referenceList.clear();
        referenceList.addAll(list);
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
        
        list.checkInvarant();
        list.remove(4);    
        list.checkInvarant();
        list.remove(0);    
        list.checkInvarant();
        list.remove(2);    
        list.checkInvarant();
        list.remove(0);    
        list.checkInvarant();
        list.remove(0);    
        list.checkInvarant();
    }
    
    @Test
    public void removeAtIndex1() {
        list.addAll(getIntegerList(10));
        // TODO: remove 'getIntegerList()'!
        referenceList.clear();
        referenceList.addAll(list);
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
    public void enhancedIteratorAdditionToHead() {
        List<Integer> referenceList = new ArrayList<>();
        list.clear();
        
        ListIterator<Integer> referenceListIterator = 
                referenceList.listIterator();
        
        ListIterator<Integer> myListIterator = list.listIterator();
        
        referenceListIterator.add(1);
        list.checkInvarant();
        myListIterator.add(1);
        list.checkInvarant();
        
        assertEquals(referenceList, list);
        
        referenceListIterator.add(3);
        list.checkInvarant();
        myListIterator.add(3);
        list.checkInvarant();
        
        assertEquals(referenceList, list);
        
        assertTrue(referenceListIterator.hasPrevious());
        assertTrue(myListIterator.hasPrevious());
        
        assertEquals(Integer.valueOf(3), referenceListIterator.previous());
        assertEquals(Integer.valueOf(3), myListIterator.previous());
        
        referenceListIterator.add(2);
        list.checkInvarant();
        myListIterator.add(2);
        list.checkInvarant();
        
        assertEquals(referenceList, list);
        
        assertEquals(Integer.valueOf(2), referenceListIterator.previous());
        assertEquals(Integer.valueOf(2), myListIterator.previous());
        
        assertEquals(Integer.valueOf(1), referenceListIterator.previous());
        assertEquals(Integer.valueOf(1), myListIterator.previous());
        
        list.checkInvarant();
        myListIterator.add(0);
        list.checkInvarant();
        referenceListIterator.add(0);
        
        assertEquals(referenceList, list);
        
        assertEquals(Integer.valueOf(1), referenceListIterator.next());
        assertEquals(Integer.valueOf(1), myListIterator.next());
    }

    @Test
    public void enhancedIteratorAddition() {
        list.addAll(Arrays.asList(1, 2, 3));
        ListIterator<Integer> iter = list.listIterator();

        iter.add(0);
        list.checkInvarant();

        while (iter.hasNext()) {
            iter.next();
        }

        iter.add(4);
        list.checkInvarant();
        iter = list.listIterator();

        for (int i = 0; i < list.size(); i++) {
            assertEquals(Integer.valueOf(i), iter.next());
        }

        iter = list.listIterator(2);
        iter.add(10);
        list.checkInvarant();

        assertEquals(Integer.valueOf(10), list.get(2));
    }
    
    @Test
    public void debugBasicIterator1() {
        list.addAll(getIntegerList(10));
        list.fingerList.setFingerIndices(0, 1, 5, 7);
        Iterator<Integer> iterator = list.iterator();
        
        int count = 0;
        
        while (iterator.hasNext()) {
            
            iterator.next();
            list.checkInvarant();
            iterator.remove();
            list.checkInvarant();
        }
    }

    // TODO: DEBUG ME!
    @Test
    public void debugBasicIterator2() {
        // Elements 1, 2, 3, 4 will be removed.
        list.addAll(Arrays.asList(0, 1, 2, 3, 4));
        Iterator<Integer> iterator = list.iterator();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(0), iterator.next()); // Omit 0.
        list.checkInvarant();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next()); // Omit 1.
        list.checkInvarant();
        
        // Remove 1:
        iterator.remove();
        list.checkInvarant();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        list.checkInvarant();
        
        // Remove 2:
        iterator.remove();
        list.checkInvarant();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());
        list.checkInvarant();
        
        // Remove 3:
        iterator.remove(); 
        list.checkInvarant();
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(4), iterator.next());
        list.checkInvarant();
        
        // Remove 4:
        iterator.remove();
        list.checkInvarant();
        
        assertFalse(iterator.hasNext());
        list.checkInvarant();
    }
    
    @Test
    public void debugBasicIterator3() {
        list.addAll(getIntegerList(12));
        list.fingerList.setFingerIndices(1, 2, 4, 5);
        Iterator<Integer> iterator = list.iterator();
        
        int count = 0;
        
        while (iterator.hasNext()) {
//            Sysem.out.println("cnt = " + ++count);
            
            iterator.next();
            list.checkInvarant();
            
            if (count % 2 == 0) {
                iterator.remove();
                list.checkInvarant();
            }
        }
    }
    
    @Test
    public void findFailingIterator() {
        list.addAll(getIntegerList(3850));
        Iterator<Integer> iterator = list.iterator();
        int counter = 0;

        while (iterator.hasNext()) {
            Integer actualInteger = iterator.next();
            assertEquals(Integer.valueOf(counter), actualInteger);
            list.checkInvarant();
            
            // Remove every 10th element:
            if (counter % 10 == 0) {
                iterator.remove();
                list.checkInvarant();
            }

            counter++;
        }
        
        list.checkInvarant();
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
                    list.checkInvarant();
                    iter.remove();
                    list.checkInvarant();
                } catch (IllegalStateException ex) {
                    throw new Exception(ex);
                }

                list.checkInvarant();
                arrayListIter.remove();
                list.checkInvarant();
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
        list.checkInvarant();
        referenceList.addAll(list);

        Integer probe = list.get(1);

        list.remove(probe);
        list.checkInvarant();
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
                list.checkInvarant();
                iterator1.remove();
                list.checkInvarant();
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

        list.checkInvarant();
        iterator.add(100);
        list.checkInvarant();

        assertEquals(Integer.valueOf(0), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(100), list.get(2));
        assertEquals(Integer.valueOf(2), list.get(3));
        assertEquals(Integer.valueOf(3), list.get(4));
    }

    @Test
    public void bruteForceIteratorTest() {
        list.addAll(getIntegerList(100));
        referenceList.clear();
        referenceList.addAll(list);
        
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
                Integer integer = random.nextInt(100);
                list.checkInvarant();
                iterator1.add(integer);
                list.checkInvarant();
                iterator2.add(integer);
                assertTrue(listsEqual(list, referenceList));
            } else if (choice == 2) {
                list.checkInvarant();
                iterator1.remove();
                list.checkInvarant();
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

        Spliterator<Integer> spliterator3 = spliterator2.trySplit();

        assertEquals(4997, spliterator1.getExactSizeIfKnown());

        assertTrue(spliterator3.tryAdvance(
                i -> assertEquals(list.get(3), Integer.valueOf(3))));

        assertTrue(spliterator3.tryAdvance(
                i -> assertEquals(list.get(4), Integer.valueOf(4))));

        assertTrue(spliterator3.tryAdvance(
                i -> assertEquals(list.get(5), Integer.valueOf(5))));

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
        Collections.<Integer>shuffle(list, new Random(13));

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
        list.checkInvarant();
        list.remove(1);
        list.checkInvarant();
        
        assertEquals(list.size(), 2);
        assertEquals(Integer.valueOf(0), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
    }
    
    @Test
    public void bruteForceRemoveAt1() {
        Random random = new Random(400L);
        
        list.addAll(getIntegerList(1000));
        referenceList.clear();
        referenceList.addAll(list);
        
        Integer probe = 3;
        
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
        list.checkInvarant();
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
        referenceList.clear();
        referenceList.addAll(list);
        
        final int[] indices = { 7, 7, 4, 1, 2, 1, 3, 1, 1, 0 };
        
        for (int i = 0; i < indices.length; i++) {
            final int index = indices[i];
            list.remove(index);
            list.checkInvarant();
            referenceList.remove(index);
            assertEquals(referenceList, list);
        }
    }
    
    @Test
    public void bugRemoveAt() {
        list.addAll(getIntegerList(10));
        
        assertEquals(Integer.valueOf(5), list.remove(5));
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(3), list.remove(3));
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(2), list.remove(2));
        list.checkInvarant();
        
        assertEquals(Integer.valueOf(1), list.remove(1));
        list.checkInvarant();
        
        // list = [0, 4, 5, 7, 8, 8]
        assertEquals(Integer.valueOf(8), list.remove(4));    
        list.checkInvarant();
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

    @Test
    public void bruteForceRemoveFirstOccurrence() {
        final Random random = new Random(13L);
        
        for (int i = 0; i < 100; i++) {
            list.add(random.nextInt(30));
        }
        
        final LinkedList<Integer> referenceList = new LinkedList<>(list);
        
        while (!list.isEmpty()) {
            final int target = random.nextInt(40);
            
            assertEquals(referenceList, list);
            
            list.removeFirstOccurrence(target);
            list.checkInvarant();
            
            referenceList.removeFirstOccurrence(target);
            
            assertEquals(referenceList, list);
        }
    }

    @Test
    public void bruteForceRemoveLastOccurrence() {
        final Random random = new Random(13L);
        
        for (int i = 0; i < 100; i++) {
            list.add(random.nextInt(30));
        }
        
        final LinkedList<Integer> referenceList = new LinkedList<>(list);
        
        while (!list.isEmpty()) {
            final int target = random.nextInt(40);
            
            assertEquals(referenceList, list);
            
            list.removeLastOccurrence(target);
            list.checkInvarant();
            
            referenceList.removeLastOccurrence(target);
            
            assertEquals(referenceList, list);
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
    
    @Test
    public void copy() {
        
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        
        Random random = new Random(10L);
        list.randomizeFingers(random);
        
        for (int i = 0; i < 1000; i++) {
            assertEquals(Integer.valueOf(i), list.get(i));   
        }
    }
    
    @Test
    public void getNodeSequentially() {
        list.addAll(getIntegerList(5));
        assertEquals(Integer.valueOf(3), 
                     list.getNodeSequentially(3).item);
    }
    
    @Test
    public void stressTestGetNodeSequentially() {
        list.addAll(getIntegerList(100));
        
        for (int i = 0; i < list.size(); ++i) {
            assertEquals(list.get(i),
                         list.getNodeSequentially(i).item);
        }
    }
    
    @Test
    public void getPrefixNode() {
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        
        Random random = new Random(4L);
        
        list.randomizeFingers(random);
        
        for (int index = 0; index < 11; index++) {
            int datum = list.get(index);
            
            assertEquals(index, datum);
        }
        
        int datum = list.get(10);
        
        assertEquals(10, datum);
    } 
    
    @Test
    public void debugFingerGetNode() {
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        
        Integer datum = list.get(9);
        
        assertEquals(Integer.valueOf(9), datum);
    }
    
    @Test
    public void listIteratorRemove() {
        list.addAll(getIntegerList(100));
        referenceList.addAll(list);
        
        final Random random = new Random(13L);
        final int initialIndex = random.nextInt(list.size() + 1);
        
        final ListIterator<Integer> listIterator = 
                list.listIterator(initialIndex);
        
        final ListIterator<Integer> referenceListIterator = 
                referenceList.listIterator(initialIndex);
        
        boolean previousMove = false;
        
        while (!list.isEmpty()) {
            
            final int coin = random.nextInt(3);
            
            switch (coin) {
                case 0:
                    if (listIterator.hasPrevious()) {
                        listIterator.previous();
                        referenceListIterator.previous();
                        previousMove = true;
                    }
                    
                    break;
                    
                case 1:
                    if (listIterator.hasNext()) {
                        listIterator.next();
                        referenceListIterator.next();
                        previousMove = true;
                    }
                    
                    break;
                    
                case 2:
                    if (previousMove) {
                        previousMove = false;
                        list.checkInvarant();
                        listIterator.remove();
                        list.checkInvarant();
                        referenceListIterator.remove();
                        assertEquals(referenceList, list);
                    }
                    
                    break;
            }
        }
    }
    /*
    void loadFingerCoverageCounters(int fromFingerIndex,
                                    int toFingerIndex,
                                    int fromIndex,
                                    int toIndex,
                                    int fingersToRemove)*/
    @Test
    public void loadFingerCoverageCounters1() {
        list.size = 10;
        list.fingerList.size = 4;
        list.loadFingerCoverageCounters(2, 4, 3, 6, 1);
        assertEquals(0, list.numberOfCoveringFingersToPrefix);
        assertEquals(1, list.numberOfCoveringFingersToSuffix);
    }
    
    @Test
    public void loadFingerCoverageCounters2() {
        list.size = 100;
        list.fingerList.size = 10;
        list.loadFingerCoverageCounters(0, 9, 1, 99, 8);
        assertEquals(1, list.numberOfCoveringFingersToPrefix);
        assertEquals(0, list.numberOfCoveringFingersToSuffix);
    }
    
    @Test
    public void loadFingerCoverageCounters3() {
        list.size = 22;
        list.fingerList.size = 5;
        list.loadFingerCoverageCounters(0, 5, 13, 18, 0);
        assertEquals(3, list.numberOfCoveringFingersToPrefix);
        assertEquals(2, list.numberOfCoveringFingersToSuffix);
    }
    
    @Test
    public void makeRoomAtPrefix1() {
        list.clear();
        
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        
        list.fingerList.setFingerIndices(2, 4, 6, 7);
        
        list.fingerList.makeRoomAtPrefix(4, 1, 2);
    }
    
    @Test 
    public void bruteForceDeleteRangeAllIndexCombinations() {
        IndexListGenerator ilg = new IndexListGenerator(11, 4);
        List<Integer> sourceList = getIntegerList(11);
        List<Integer> referenceList = new ArrayList<>();
        
        int iteration = 0;
        int combinations = 0;
        
        do {
            int[] indices = ilg.getIndices();
            
            combinations++;
            
            for (int fromIndex = 0; fromIndex <= 10; fromIndex++) {
                for (int toIndex = fromIndex; toIndex <= 10; toIndex++) {
                    
                    iteration++;
                    
                    list.clear();
                    list.addAll(sourceList);
                    list.fingerList.setFingerIndices(indices);
                    
                    referenceList.clear();
                    referenceList.addAll(sourceList);
                    
                    list.subList(fromIndex, toIndex).clear();
                    referenceList.subList(fromIndex, toIndex).clear();
                    
                    assertEquals(referenceList, list);
                    
                    list.checkInvarant();
                }
            }
            
        } while (ilg.inc());
    }
    
    @Test
    public void removeByIndex1() {
        list.addAll(getIntegerList(10));
        referenceList.addAll(list);
       
        list.remove(4);
        referenceList.remove(4);
        
        assertEquals(referenceList, list);
        list.checkInvarant();
    }
    
    @Test
    public void removeByIndex2() {
        list.addAll(getIntegerList(9));
        referenceList.addAll(list);
        list.fingerList.setFingerIndices(2, 5, 8);
        list.remove(5);
        referenceList.remove(5);
        
        assertEquals(referenceList, list);
        list.checkInvarant();
    }
    
    @Test
    public void removeByIndex3() {
        list.addAll(getIntegerList(9));
        referenceList.addAll(list);
        list.fingerList.setFingerIndices(2, 5, 8);
        list.remove(6);
        referenceList.remove(6);
        
        assertEquals(referenceList, list);
        list.checkInvarant();
    }
}

class IndexListGenerator {
    
    private final int listSize;
    private final int[] indices;
    
    IndexListGenerator(final int listSize,
                       final int numberOfIndices) {
        this.listSize = listSize;
        this.indices = new int[numberOfIndices];
        
        preload();
    }
    
    boolean inc() {
        if (indices[indices.length - 1] < listSize - 1) {
            indices[indices.length - 1]++;
            return true;
        }
        
        for (int i = indices.length - 2; i >= 0; i--) {
            int a = indices[i];
            int b = indices[i + 1];
            
            if (a < b - 1) {
                indices[i]++;
                
                for (int j = i + 1; j < indices.length; j++) {
                    indices[j] = indices[j - 1] + 1;
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    int[] getIndices() {
        return indices;
    }
    
    private void preload() {
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
    }
}
