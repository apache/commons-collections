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

import com.github.coderodde.util.IndexedLinkedList.Finger;
import com.github.coderodde.util.IndexedLinkedList.Node;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class FingerListTest {

    private final IndexedLinkedList<Integer> list = new IndexedLinkedList<>();
    private final IndexedLinkedList<Integer>.FingerList<Integer> fl = 
            list.fingerList;
    
    @Before
    public void setUp() {
        fl.clear();
    }

    @Test
    public void appendGetFinger() {
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(0)), 0));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(1)), 1));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(3)), 3));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(6)), 6));
        fl.fingerArray[4].index = 8;
        fl.fingerArray[4].node = new Node<>(Integer.valueOf(1000));
        
        Finger<Integer> finger = fl.get(fl.getFingerIndex(0));
        assertEquals(0, finger.index);
        assertEquals(Integer.valueOf(0), finger.node.item);
        
        finger = fl.get(fl.getFingerIndex(1));
        assertEquals(1, finger.index);
        assertEquals(Integer.valueOf(1), finger.node.item);
        
        finger = fl.get(fl.getFingerIndex(2));
        assertEquals(3, finger.index);
        assertEquals(Integer.valueOf(3), finger.node.item);
        
        finger = fl.get(fl.getFingerIndex(3));
        assertEquals(3, finger.index);
        assertEquals(Integer.valueOf(3), finger.node.item);
        
        finger = fl.get(fl.getFingerIndex(4));
        assertEquals(3, finger.index);
        assertEquals(Integer.valueOf(3), finger.node.item);
        
        finger = fl.get(fl.getFingerIndex(5));
        assertEquals(6, finger.index);
        assertEquals(Integer.valueOf(6), finger.node.item);
        
        finger = fl.get(fl.getFingerIndex(6));
        assertEquals(6, finger.index);
        assertEquals(Integer.valueOf(6), finger.node.item);
    }
    
//    @Test
    public void insertFingerAtFront() {
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(0)), 0));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(1)), 1));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(3)), 3));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(6)), 6));
        
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(null), 0);
        
//        fl.insertFinger(insertionFinger);
        
        Finger<Integer> finger = fl.get(fl.getFingerIndex(0));
        assertEquals(insertionFinger.index, finger.index);
        
        assertEquals(5, fl.size());
    }
    
//    @Test
    public void insertFingerAtTail() {
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(2)), 2));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(4)), 4));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(5)), 5));
        
        // Add end of finger list sentinel:
        fl.fingerArray[3] = 
                new Finger<>(new Node<Integer>(Integer.valueOf(100)), 10);
        
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(null), 6);
        
//        fl.insertFinger(insertionFinger);

        Finger<Integer> finger = fl.get(fl.getFingerIndex(6));
        assertEquals(insertionFinger.index, finger.index);
        
        assertEquals(4, fl.size());
    }
    
//    @Test
    public void insertFingerInBetween1() {
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(2)), 2));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(4)), 4));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(5)), 5));
        
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(null), 4);
        
//        fl.insertFinger(insertionFinger);
        
        assertEquals(insertionFinger, fl.get(1));
    }
    
//    @Test
    public void insertFingerInBetween2() {
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(2)), 2));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(4)), 4));
        fl.appendFinger(new Finger<>(new Node<>(Integer.valueOf(5)), 5));
        
        Finger<Integer> insertionFinger = new Finger<>(new Node<>(null), 3);
        
//        fl.insertFinger(insertionFinger);
        
        assertEquals(insertionFinger, fl.get(1));
    }
}