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

import org.apache.commons.collections4.list.IndexedLinkedList.Finger;
import org.apache.commons.collections4.list.IndexedLinkedList.FingerList;
import org.apache.commons.collections4.list.IndexedLinkedList.Node;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The test for finger list of a 
 * {@link org.apache.commons.collections4.list.IndexedLinkedList}.
 */
public class FingerListTest {

    private final IndexedLinkedList<Integer> list = new IndexedLinkedList<>();
    private final FingerList<Integer> fl = list.fingerList;
    
    @BeforeEach
    public void setUp() {
        fl.clear();
    }
    
    @Test
    public void enlargeFingerArrayWithEmptyRangeNonExpansion() {
        list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        list.fingerList.setFingerIndices(0, 1, 2, 3);
        list.fingerList.enlargeFingerArrayWithEmptyRange(8, 1, 2, 2);
        
        assertEquals(new Finger<>(new Node<>(0), 0), 
                     list.fingerList.fingerArray[0]);
        
        assertEquals(new Finger<>(new Node<>(1), 3), 
                     list.fingerList.fingerArray[1]);
        
        assertEquals(new Finger<>(new Node<>(2), 4), 
                     list.fingerList.fingerArray[2]);
        
        assertEquals(new Finger<>(new Node<>(1), 3), 
                     list.fingerList.fingerArray[3]);
        
        assertEquals(new Finger<>(new Node<>(2), 4), 
                     list.fingerList.fingerArray[4]);
        
        assertEquals(new Finger<>(new Node<>(3), 5), 
                     list.fingerList.fingerArray[5]);
        
        assertEquals(new Finger<>(null, 14),
                     list.fingerList.fingerArray[6]);
    }
    
    @Test
    public void toStringImpl() {
        list.addAll(Arrays.asList(9, 10));
        
        assertEquals("[FingerList (size = 3) | " + 
                     "[index = 0, item = 9], "   +  
                     "[index = 1, item = 10], "  +
                     "[index = 2, item = null]]", 
                     list.fingerList.toString());
    }

    @Test
    public void appendGetFinger() {
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(0)), 0));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(1)), 1));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(3)), 3));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(6)), 6));
        fl.fingerArray[4].index = 8;
        fl.fingerArray[4].node = new Node<>(Integer.valueOf(1000));
        
        Finger<Integer> finger = fl.getFinger(fl.getClosestFingerIndex(0));
        assertEquals(0, finger.index);
        assertEquals(Integer.valueOf(0), finger.node.item);
        
        finger = fl.getFinger(fl.getClosestFingerIndex(1));
        assertEquals(1, finger.index);
        assertEquals(Integer.valueOf(1), finger.node.item);
        
        finger = fl.getFinger(fl.getClosestFingerIndex(2));
        assertEquals(3, finger.index);
        assertEquals(Integer.valueOf(3), finger.node.item);
        
        finger = fl.getFinger(fl.getClosestFingerIndex(3));
        assertEquals(3, finger.index);
        assertEquals(Integer.valueOf(3), finger.node.item);
        
        finger = fl.getFinger(fl.getClosestFingerIndex(4));
        assertEquals(3, finger.index);
        assertEquals(Integer.valueOf(3), finger.node.item);
        
        finger = fl.getFinger(fl.getClosestFingerIndex(5));
        assertEquals(6, finger.index);
        assertEquals(Integer.valueOf(6), finger.node.item);
        
        finger = fl.getFinger(fl.getClosestFingerIndex(6));
        assertEquals(6, finger.index);
        assertEquals(Integer.valueOf(6), finger.node.item);
    }
    
    @Test
    public void insertFingerAtFront() {
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(0)), 0));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(1)), 1));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(3)), 3));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(6)), 6));
        
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(null), 0);
        
        fl.insertFingerAndShiftOnceToRight(insertionFinger);
        
        Finger<Integer> finger = fl.getFinger(fl.getClosestFingerIndex(0));
        assertEquals(insertionFinger.index, finger.index);
        
        assertEquals(5, fl.size());
    }
    
    @Test
    public void insertFingerAtTail() {
        fl.list.size = 1;
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(2)), 0));
        fl.list.size = 2;
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(4)), 1));
        fl.list.size = 3;
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(5)), 2));
        
        // Add end of finger list sentinel:
        fl.fingerArray[3] = new Finger<>(new Node<>(null), 10);
        
        fl.list.size = 4;
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(13), 1);
        
        fl.insertFingerAndShiftOnceToRight(insertionFinger);

        assertEquals(4, fl.size());
    }
    
    @Test
    public void insertFingerInBetween1() {
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(2)), 2));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(4)), 4));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(5)), 5));
        
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(null), 4);
        
        fl.insertFingerAndShiftOnceToRight(insertionFinger);
        
        assertEquals(insertionFinger, fl.getFinger(1));
    }
    
    @Test
    public void insertFingerInBetween2() {
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(2)), 2));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(4)), 4));
        fl.appendFingerImpl(new Finger<>(new Node<>(Integer.valueOf(5)), 5));
        
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(null), 3);
        
        fl.insertFingerAndShiftOnceToRight(insertionFinger);
        
        assertEquals(insertionFinger, fl.getFinger(1));
    }
    
    @Test
    public void makeRoomAtPrefix1Old() {
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        
        list.fingerList.setFingerIndices(6, 7, 8, 9);
        
        list.fingerList.makeRoomAtPrefix(5, 0, 3);
        list.fingerList.pushCoveredFingersToPrefix(6, 0, 3);
        
        Finger<Integer> finger0 = list.fingerList.fingerArray[0];
        Finger<Integer> finger1 = list.fingerList.fingerArray[1];
        Finger<Integer> finger2 = list.fingerList.fingerArray[2];
        
        assertEquals(3, finger0.index);
        assertEquals(4, finger1.index);
        assertEquals(5, finger2.index);
    }
    
    @Test
    public void makeRoomAtPrefix1() {
        
        loadList(10);
        
        list.fingerList.setFingerIndices(1, 3, 4, 6);
        list.fingerList.makeRoomAtPrefix(4, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(0, 1, 4, 6);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtPrefix2() {
        
        loadList(10);
        
        list.fingerList.setFingerIndices(1, 3, 5, 8);
        list.fingerList.makeRoomAtPrefix(4, 2, 1);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(1, 2, 5, 8);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtPrefix3() {
        
        loadList(10);
        
        list.fingerList.setFingerIndices(1, 4, 6, 9);
        list.fingerList.makeRoomAtPrefix(5, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(1, 2, 6, 9);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtPrefix4() {
        
        loadList(10);
        
        list.fingerList.setFingerIndices(0, 1, 7, 8);
        list.fingerList.makeRoomAtPrefix(4, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(0, 1, 7, 8);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtPrefix5() {
        
        loadList(10);
        
        list.fingerList.setFingerIndices(0, 2, 6, 8);
        list.fingerList.makeRoomAtPrefix(5, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(0, 2, 6, 8);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtPrefix6() {
        
        loadList(101);
        
        list.fingerList.setFingerIndices(
                1, 3, 6, 10, 15, 29, 54, 57, 69, 72, 100
        );
        
        list.fingerList.makeRoomAtPrefix(12, 4, 5);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(
                1, 3, 4, 5, 15, 29, 54, 57, 69, 72, 100
        );
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtSuffix1() {
        loadList(10);
        
        list.fingerList.setFingerIndices(3, 4, 8, 9);
        list.fingerList.makeRoomAtSuffix(6, 2, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(3, 4, 8, 9);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtSuffix2() {
        loadList(10);
        
        list.fingerList.setFingerIndices(3, 4, 7, 9);
        list.fingerList.makeRoomAtSuffix(6, 2, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(3, 4, 8, 9);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtSuffix3() {
        loadList(10);
        
        list.fingerList.setFingerIndices(3, 4, 7, 8);
        list.fingerList.makeRoomAtSuffix(6, 2, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(3, 4, 8, 9);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void makeRoomAtSuffix4() {
        loadList(10);
        
        list.fingerList.setFingerIndices(3, 4, 7, 8);
        list.fingerList.makeRoomAtSuffix(6, 2, 0, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(3, 4, 7, 8);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void arrangePrefix1() {
        loadList(10);
        
        list.fingerList.setFingerIndices(1, 3, 5, 8);
        list.fingerList.arrangePrefix(4, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(0, 1, 2, 3);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void arrangePrefix2() {
        loadList(10);
        
        list.fingerList.setFingerIndices(5, 6, 8, 9);
        list.fingerList.arrangePrefix(5, 0, 4);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(1, 2, 3, 4);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void add() {
        list.clear();
        
        for (int i = 0; i < 100; ++i) {
            list.add(i);
            list.checkInvarant();
        }
    }
    
    @Test
    public void arrangePrefix3() {
        
        loadList(101);
        
        list.fingerList.setFingerIndices(
                1, 3, 6, 10, 15, 29, 54, 57, 69, 72, 100
        );
        
        list.fingerList.arrangePrefix(12, 4, 5);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(
                1, 3, 4, 5, 6, 7, 8, 9, 10, 72, 100
        );
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void pushCoveredFingersToSuffix1() {
        
        loadList(10);
        
        list.fingerList.setFingerIndices(1, 3, 4, 5);
        list.fingerList.pushCoveredFingersToSuffix(6, 0, 3);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(1, 6, 7, 8);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void pushCoveredFingersToSuffix2() {
       
        loadList(10);
        
        list.fingerList.setFingerIndices(1, 3, 6, 9);
        list.fingerList.pushCoveredFingersToSuffix(4, 2, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(4, 5, 6, 9);
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void arrange1() {
        loadList(100);
        
        list.fingerList.setFingerIndices(5, 14, 15, 25, 26, 39, 49, 85, 86, 99);
        list.fingerList.arrangePrefix(17, 3, 2);
        list.fingerList.arrangeSuffix(83, 7, 3, 2);
        
        IndexedLinkedList<Integer> expectedList = new IndexedLinkedList<>(list);
        expectedList.fingerList.setFingerIndices(
                5, 6, 7, 8, 9, 83, 84, 85, 86, 99
        );
        
        assertTrue(list.strongEquals(expectedList));
    }
    
    @Test
    public void removeFingersOnDeleteRange1() {
        loadList(100);
        
        list.fingerList.removeFingersOnDeleteRange(3, 4, 20);
        FingerList<Integer> expectedFingerList = new FingerList<>(null);
        
        expectedFingerList.size = 6;
        
        expectedFingerList.fingerArray[0] = new Finger<>(new Node<>(0),  0  );
        expectedFingerList.fingerArray[1] = new Finger<>(new Node<>(1),  1  );
        expectedFingerList.fingerArray[2] = new Finger<>(new Node<>(4),  4  );
        expectedFingerList.fingerArray[3] = new Finger<>(new Node<>(49), 29 );
        expectedFingerList.fingerArray[4] = new Finger<>(new Node<>(64), 44 );
        expectedFingerList.fingerArray[5] = new Finger<>(new Node<>(81), 61 );
        expectedFingerList.fingerArray[6] = new Finger<>(new Node<>(null), 80);
            
        assertEquals(expectedFingerList, list.fingerList);
    }
    
    @Test
    public void equals() {
        IndexedLinkedList<Integer> list1 = new IndexedLinkedList<>();
        IndexedLinkedList<Integer> list2 = new IndexedLinkedList<>();
        FingerList<Integer> fingerList1 = new FingerList<>(list1);
        FingerList<Integer> fingerList2 = new FingerList<>(list2);
        
        assertTrue(fingerList1.equals(fingerList1));
        assertFalse(fingerList1.equals(null));
        
        fingerList2.size = 1;
        
        assertFalse(fingerList1.equals(fingerList2));
        assertFalse(fingerList1.equals(new Object()));
        
        fingerList1.size = 0;
        fingerList1.appendFingerImpl(new Finger<>(new Node(1), 0));
        fingerList1.appendFingerImpl(new Finger<>(new Node(2), 1));
        
        fingerList2.size = 0;
        fingerList2.appendFingerImpl(new Finger<>(new Node(1), 0));
        
        assertFalse(fingerList1.equals(fingerList2));
        
        fingerList2.appendFingerImpl(new Finger<>(new Node(2), 1));
        
        assertTrue(fingerList1.equals(fingerList2));
        
        fingerList2.fingerArray[1] = null;
        
        assertFalse(fingerList1.equals(fingerList2));
        assertFalse(fingerList2.equals(fingerList1));
    }
    
    private void loadList(int size) {
        for (int i = 0; i < size; i++) {
            list.add(i);
            list.checkInvarant();
        }
    }
}
