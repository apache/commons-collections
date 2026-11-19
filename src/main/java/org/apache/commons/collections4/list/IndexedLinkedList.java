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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * <p>
 * This class implements the indexed, heuristic doubly-linked list data 
 * structure that runs all the single-element operations in expected
 * \(\mathcal{O}(\sqrt{n})\) time. Under the hood, the actual elements are
 * stored in a doubly-linked list. However, we also maintain a list of so-called 
 * <i>"fingers"</i> stored in a random access array. Each finger {@code F} 
 * contains two data fields:
 * <ul>
 *  <li>{@code F.node} - the actual element node,</li>
 *  <li>{@code F.index} - the appearance index of {@code F.node} in the actual 
 *       list.</li>
 * </ul>
 * 
 * <p>
 * 
 * For the list of size \(n\), we maintain 
 * \(\bigg \lceil \sqrt{n} \bigg \rceil + 1\) fingers. The rightmost finger in 
 * the finger list is a <i>special end-of-list sentinel</i>. It always has 
 * {@code F.node = null} and {@code F.index = } \(n\). The fingers are sorted by 
 * their indices. That arrangement allows simpler and faster code in the method 
 * that accesses a finger via element index; see 
 * {@link FingerList#getFingerIndexImpl(int)}. Since number of fingers is 
 * \(\sqrt{n}\), and assuming that the fingers are evenly distributed, each 
 * finger "covers" \(n / \sqrt{n} = \sqrt{n}\) elements. In order to access an 
 * element in the actual list, we first consult the finger list for the index 
 * {@code i} of the finger {@code fingerArray[i]} that is closest to the index 
 * of the target element. This runs in 
 * 
 * \[ 
 * \mathcal{O}(\log \sqrt{n}) = \mathcal{O}(\log n^{1/2}) = \mathcal{O}(\frac{1}{2} \log n) = \mathcal{O}(\log n).
 * \]
 * 
 * The rest is to <i>"rewind"</i> the closest finger to point to the target 
 * element (which requires \(\mathcal{O}(\sqrt{n})\) on evenly distributed 
 * finger lists).
 */
public class IndexedLinkedList<E> implements Deque<E>, 
                                             List<E>, 
                                             Cloneable, 
                                             java.io.Serializable {
    /**
     * This inner class implements the finger list data structure for managing
     * list fingers.
     * 
     * @param <E> the list node item type.
     */
    class FingerList<E> {

        // This is also the minimum capacity.
        private static final int INITIAL_CAPACITY = 8;

        // The actual list storage array:
        Finger<E>[] fingerArray = new Finger[INITIAL_CAPACITY];

        // The number of fingers stored in the list. This field does not count
        // the end-of-list sentinel finger 'F' for which 'F.index = size'.
        private int size;

        // Constructs the empty finger list consisting only of end-of-list 
        // sentinel finger.
        private FingerList() {
            fingerArray[0] = new Finger<>(null, 0);
        }
        
        @Override
        public String toString() {
            return "[FingerList, size = " + size + "]";
        }
        
        // Appends the input finger to the tail of the finger list:
        void appendFinger(Finger<E> finger) {
            size++;
            enlargeFingerArrayIfNeeded(size + 1);
            fingerArray[size] = fingerArray[size - 1];
            fingerArray[size - 1] = finger;
            fingerArray[size].index = IndexedLinkedList.this.size;
        }

        // Not 'private' since is used in the unit tests.
        void clear() {
            Arrays.fill(fingerArray, 0, size, null);
            fingerArray = new Finger[INITIAL_CAPACITY];
            fingerArray[0] = new Finger<>(null, 0);
            size = 0;
        }
        
        Finger<E> get(int index) {
            return fingerArray[index];
        }
        
        // Returns the index of the finger that is closest to the 
        // 'elementIndex'th list element.
        int getFingerIndex(int elementIndex) {
            return normalize(getFingerIndexImpl(elementIndex), elementIndex);
        }

        int size() {
            return size;
        }
        
        private void adjustOnRemoveFirst() {
            int lastPrefixIndex = Integer.MAX_VALUE;
            
            for (int i = 0; i < size; ++i) {
                Finger<E> finger = fingerArray[i];
                
                if (finger.index != i) {
                    lastPrefixIndex = i;
                    break;
                } else {
                    finger.node = finger.node.next;
                }
            }
            
            shiftFingerIndicesToLeftOnceUntil(lastPrefixIndex, size - 1);
        }
        
        // We can save some space while keeping the finger array operations 
        // amortized O(1). The 'nextSize' defines the requested finger array 
        // size not counting the end-of-finger-list sentinel finger:
        private void contractFingerArrayIfNeeded(int nextSize) {
            // Can we contract at least once?
            if ((nextSize + 1) * 4 < fingerArray.length 
                    && fingerArray.length > 2 * INITIAL_CAPACITY) {
                int nextCapacity = fingerArray.length / 4;
  
                // Good, we can. But can we keep on splitting in half the 
                // capacity any further?
                while (nextCapacity >= 2 * (nextSize + 1)
                        && nextCapacity > INITIAL_CAPACITY) {
                    // Yes, we can do it as well.
                    nextCapacity /= 2;
                }
                
                fingerArray = Arrays.copyOf(fingerArray, nextCapacity);
            }
        }

        // Makes sure that the next finger fits in this finger stack:
        private void enlargeFingerArrayIfNeeded(int requestedSize) {
            // If the finger array is full, double the capacity:
            if (requestedSize > fingerArray.length) {
                int nextCapacity = 2 * fingerArray.length;
                
                while (nextCapacity < size + 1) {
                    // If 'requestedSize' is too large, we may need to keep on
                    // doubling the next capacity until it's large enought to 
                    // accommodate 'requestedSiz
                    nextCapacity *= 2;
                }
                
                fingerArray = Arrays.copyOf(fingerArray, nextCapacity);
            }
        }

        // Returns the finger index 'i', such that 'fingerArray[i].index' is no
        // less than 'i', and is closest to 'i'. This algorithm is translated
        // from https://en.cppreference.com/w/cpp/algorithm/lower_bound 
        private int getFingerIndexImpl(int elementIndex) {
            int count = size + 1; // + 1 for the end sentinel.
            int it;
            int idx = 0;

            while (count > 0) {
                it = idx;
                int step = count / 2;
                it += step;

                if (fingerArray[it].index < elementIndex) {
                    idx = ++it;
                    count -= step + 1;
                } else {
                    count = step;
                }
            }
            
            return idx;
        }
        
        // Inserts the input finger into the finger list such that the entire
        // finger list is sorted by indices:
        private void insertFingerAndShiftOnceToRight(Finger<E> finger) {
            enlargeFingerArrayIfNeeded(size + 2);
            int beforeFingerIndex = getFingerIndex(finger.index);
            System.arraycopy(
                    fingerArray, 
                    beforeFingerIndex, 
                    fingerArray, 
                    beforeFingerIndex + 1, 
                    size + 1 - beforeFingerIndex);
            
            // Shift fingerArray[beforeFingerIndex ... size] one position to the
            // right (towards larger index values:
            shiftFingerIndicesToRightOnce(beforeFingerIndex + 1);

            fingerArray[beforeFingerIndex] = finger;
            fingerArray[++size].index = IndexedLinkedList.this.size;
        }
        
        // Make sure we can insert 'roomSize' fingers starting from 
        // 'fingerIndex', shifting all the fingers starting from 'fingerIndex'
        // 'numberOfNodes' to the right:
        private void makeRoomAtIndex(int fingerIndex, int roomSize, int numberOfNodes) {
            shiftFingerIndicesToRight(fingerIndex, numberOfNodes);
            size += roomSize;
            enlargeFingerArrayIfNeeded(size + 1); // +1 for the end of list
                                                  // sentinel.
            System.arraycopy(fingerArray, 
                             fingerIndex, 
                             fingerArray, 
                             fingerIndex + roomSize,
                             size - roomSize - fingerIndex + 1);
        }
        
        // Moves 'numberOfFingers' fingers to the prefix ending in 'fromIndex':
        private void moveFingersToPrefix(int fromIndex, int numberOfFingers) {
            if (numberOfFingers == 0) {
                // Here, nothing to move:
                return;
            }
            
            int fromFingerIndex = getFingerIndex(fromIndex);
            
            if (fromFingerIndex == 0) {
                // Here, the prefix is empty:
                moveFingersToPrefixOnEmptyPrefix(
                        fromIndex, 
                        numberOfFingers);
                
                return;
            }
            
            int i;
            int targetIndex = -1;
            Finger<E> targetFinger = null;
            
            // Find the rightmost finger index after which we can put
            // 'numberOfFingers' fingers:
            for (i = fromFingerIndex - 1; i >= 0; --i) {
                Finger<E> finger = fingerArray[i];
                
                if (finger.index + numberOfFingers - 1 + i < fromIndex) {
                    targetFinger = finger;
                    targetIndex = i;
                    break;
                }
            }
            
            // Pack the rest of the prefix fingers:
            for (int j = targetIndex + 1; j < numberOfFingers; ++j) {
                Finger<E> predecessorFinger = fingerArray[j - 1];
                Finger<E> currentFinger = fingerArray[j];
                currentFinger.index = predecessorFinger.index + 1;
                currentFinger.node = predecessorFinger.node.next;
            }
        }
        
        // Move 'numberOfFingers' fingers to the empty prefix:
        private void moveFingersToPrefixOnEmptyPrefix(int fromIndex,
                                                      int numberOfFingers) {
            Finger<E> firstFinger = fingerArray[0];
            int toMove = firstFinger.index - fromIndex + numberOfFingers;

            for (int i = 0; i < toMove; ++i) {
                firstFinger.node = firstFinger.node.prev;
            }

            firstFinger.index -= toMove;

            for (int i = 1; i < numberOfFingers; ++i) {
                Finger<E> previousFinger = fingerArray[i - 1];
                Finger<E> currentFinger = fingerArray[i];
                currentFinger.node = previousFinger.node.next;
                currentFinger.index = previousFinger.index + 1;
            }
        }
        
        // Moves 'numberOfFingers' fingers to the suffix starting in 
        // 'toIndex':
        private void moveFingersToSuffix(int toIndex, int numberOfFingers) {
            if (numberOfFingers == 0) {
                // Here, nothing to move:
                return;
            }
            
            int toFingerIndex = getFingerIndexImpl(toIndex);
            
            if (toFingerIndex == fingerList.size) {
                // Here, the suffix is empty:
                moveFingersToSuffixOnEmptySuffix(toIndex, numberOfFingers);
                return;
            }
            
            int i;
            Finger<E> targetFinger = null;
            
            // Find the leftmost finger index before which we can put
            // 'numberOfFingers' fingers:
            for (i = toFingerIndex; i < size; ++i) {
                Finger<E> finger = fingerArray[i];
                
                if (finger.index - numberOfFingers + 1 >= toIndex) {
                    targetFinger = finger;
                    break;
                }
            }
            
            if (targetFinger == null) {
                // Here, all the 'numberOfFingers' do not fit. Make some room:
                Finger<E> f = fingerArray[size - 1];
                int toMove = toIndex + numberOfFingers - 1 - f.index;
                
                for (int j = 0; j < toMove; ++j) {
                    f.node = f.node.next;
                }
                
                f.index += toMove;
                i = size - 1;
            }

            int stopIndex = numberOfFingers - (size - i);
            
            // Pack the rest of the suffix fingers:
            for (int j = i - 1, k = 0; k < stopIndex; ++k, --j) {
                Finger<E> predecessorFinger = fingerArray[j];
                Finger<E> currentFinger = fingerArray[j + 1];
                predecessorFinger.index = currentFinger.index - 1;
                predecessorFinger.node = currentFinger.node.prev;
            }
        }

        // Move 'numberOfFingers' fingers to the empty suffix:
        private void moveFingersToSuffixOnEmptySuffix(int toIndex,
                                                      int numberOfFingers) {
            int toMove = toIndex 
                       - fingerArray[size - 1].index 
                       + numberOfFingers - 1;

            Finger<E> finger = fingerArray[size - 1];

            for (int i = 0; i < toMove; ++i) {
                finger.node = finger.node.next;
            }   

            finger.index += toMove;

            for (int i = 1; i < numberOfFingers; ++i) {
                Finger<E> predecessorFinger = fingerArray[size - i - 1];
                Finger<E> currentFinger = fingerArray[size - i];
                predecessorFinger.index = currentFinger.index - 1;
                predecessorFinger.node = currentFinger.node.prev;
            }
        }

        // Returns the 'i'th node of this linked list. The closest finger is 
        // updated to point to the returned node:
        private Node<E> node(int index) {
            Finger finger = fingerArray[getFingerIndex(index)];
            int steps = finger.index - index;

            if (steps > 0) {
                finger.rewindLeft(steps);
            } else {
                finger.rewindRight(-steps);
            }

            return finger.node;
        }
        
        // Makes sure that the returned finger index 'i' points to the closest
        // finger in the finger array:
        private int normalize(int fingerIndex, int elementIndex) {
            if (fingerIndex == 0) {
                // Since we cannot point to '-1'th finger, return 0:
                return 0;
            }
            
            if (fingerIndex == size) {
                // Don't go outside of 'size - 1*:
                return size - 1;
            }
            
            Finger finger1 = fingerArray[fingerIndex - 1];
            Finger finger2 = fingerArray[fingerIndex];
            
            int distance1 = elementIndex - finger1.index;
            int distance2 = finger2.index - elementIndex;
            
            // Return the closest finger index:
            return distance1 < distance2 ? fingerIndex - 1 : fingerIndex;
        }
        
        // Removes the last finger residing right before the end-of-finger-list
        // sentinel finger:
        private void removeFinger() {
            contractFingerArrayIfNeeded(--size);
            fingerArray[size] = fingerArray[size + 1];
            fingerArray[size + 1] = null;
            fingerArray[size].index = IndexedLinkedList.this.size;
        }
        
        // Removes the finger range [startFingerIndex, endFingerIndex).
        private void removeRange(int prefixSize, 
                                 int suffixSize,
                                 int nodesToRemove) {
            int fingersToRemove = size - suffixSize - prefixSize;
            
            shiftFingerIndicesToLeft(size - suffixSize, nodesToRemove);
            
            System.arraycopy(fingerArray, 
                             size - suffixSize, 
                             fingerArray, 
                             prefixSize,
                             suffixSize + 1);
            
            size -= fingersToRemove;
            contractFingerArrayIfNeeded(size);
            
            Arrays.fill(fingerArray,
                        size + 1,
                        Math.min(fingerArray.length, 
                                 size + 1 + fingersToRemove),
                        null);
        }
        
        private void setFinger(int index, Finger<E> finger) {
            fingerArray[index] = finger;
        }

        // Moves all the fingers in range [startFingerIndex, size] 
        // 'shiftLength' positions to the left (towards smaller indices):
        private void shiftFingerIndicesToLeft(int startFingerIndex,      
                                              int shiftLength) {
            for (int i = startFingerIndex; i <= size; ++i) {
                fingerArray[i].index -= shiftLength;
            }
        }
        
        // Moves all the fingers in range [startFingerIndex, endFingerIndex] one 
        // position to the left (towards smaller indices):
        private void shiftFingerIndicesToLeftOnceUntil(int startFingerIndex,
                                                       int endFingerIndex) {
            for (int i = startFingerIndex; i <= endFingerIndex; ++i) {
                fingerArray[i].index--;
            }
        }
        
        // Moves all the fingers in range [startFingerIndex, size] 
        // 'shiftLength' positions to the right (towards larger indices):
        private void shiftFingerIndicesToRight(int startIndex,      
                                               int shiftLength) {
            for (int i = startIndex; i <= size; ++i) {
                fingerArray[i].index += shiftLength;
            }
        }
        
        // Moves all the fingers in range [startFingerIndex, size] one  
        // position to the right (towards larger indices):
        private void shiftFingerIndicesToRightOnce(int startIndex) {
            shiftFingerIndicesToRight(startIndex, 1);
        }
    }
    
    static final class Node<E> {
    
        E item;
        Node<E> prev;
        Node<E> next;

        Node(E item) {
            this.item = item;
        }

        @Override
        public String toString() {
            return "[Node; item = " + item + "]";
        }
    }
    
    static final class Finger<E> {
 
        Node<E> node;
        int index; // Index at which 'node' is located.
        int updateIndex;

        Finger(Node<E> node, int index) {
            this.node = node;
            this.index = index;
        }

        @Override
        public String toString() {
            return "[Finger; index = " + index + 
                    ", item = " + ((node == null) ? "null" : node.item) + 
                    "]";
        }

        // Moves this finger 'steps' position to the left
        void rewindLeft(int steps) {
            for (int i = 0; i < steps; i++) {
                node = node.prev;
            }

            index -= steps;
        }

        // Moves this finger 'steps' position to the right
        void rewindRight(int steps) {
            for (int i = 0; i < steps; i++) {
                node = node.next;
            }

            index += steps;
        }
    }
    
    @java.io.Serial
    private static final long serialVersionUID = 54170828611556733L;
    
    /**
     * The cached number of elements in this list.
     */
    private int size;
    
    /**
     * The modification counter. Used to detect state changes.
     */
    private transient int modCount;
    transient Node<E> first;
    transient Node<E> last;
    
    // Without 'private' since it is accessed in unit tests.
    transient FingerList<E> fingerList = new FingerList<>();
    
    /**
     * Constructs an empty list.
     */
    public IndexedLinkedList() {
        
    }
    
    /**
     * Constructs a new list and copies the data in {@code c} to it. Runs in
     * \(\mathcal{O}(m + \sqrt{m})\) time, where \(m = |c|\).
     * 
     * @param c the collection to copy. 
     */
    public IndexedLinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
    
    /**
     * Appends the specified element to the end of this list. Runs in amortized 
     * constant time.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list.
     * @return {@code true} (as specified by {@link Collection#add}).
     */
    @Override
    public boolean add(E e) {
        linkLast(e);
        return true;
    }
    
    /**
     * Inserts the specified element at the specified position in this list.
     * The affected finger indices will be incremented by one. A finger 
     * {@code F} is <i>affected</i>, if {@code F.index >= index}. Runs in
     * \(\mathcal{O}(\sqrt{n})\) time.
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        
        if (index == size) {
            linkLast(element);
        } else {
            linkBefore(element, node(index), index);
        }
    }
    
    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order they are returned by the specified collection's
     * iterator.  The behavior of this operation is undefined if the specified 
     * collection is modified while the operation is in progress. (Note that 
     * this will occur if the specified collection is this list, and it's
     * nonempty.) Runs in \(\mathcal{O}(m + \sqrt{m + n})\), where \(m = |c|\)
     * and \(n\) is the size of this list.
     *
     * @param c collection containing elements to be added to this list.
     * @return {@code true} if this list changed as a result of the call.
     * @throws NullPointerException if the specified collection is null.
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }
    
    /**
     * Inserts all of the elements in the specified collection into this list, 
     * starting at the specified position. For each finger {@code F} with 
     * {@code F.index >= index} will increment {@code F.index} by 1. Runs in 
     * \(\mathcal{O}(m + \sqrt{m + n})\), where \(m = |c|\) and \(n\) is the 
     * size of this list.
     *
     * @param index index at which to insert the first element from the
     *              specified collection.
     * @param c collection containing elements to be added to this list.
     * @return {@code true} if this list changed as a result of the call.
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);
        
        if (c.isEmpty()) {
            return false;
        }

        if (size == 0) {
            setAll(c);
        } else if (index == 0) {
            prependAll(c);
        } else if (index == size) {
            appendAll(c);
        } else {
            insertAll(c, node(index), index);
        }
        
        return true;
    }
    
    /**
     * Adds the element {@code e} before the head of this list. Runs in Runs in 
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @param e the element to add.
     */
    @Override
    public void addFirst(E e) {
        linkFirst(e);
    }
    
    /**
     * Adds the element {@code e} after the tail of this list. Runs in constant
     * time.
     * 
     * @param e the element to add.
     */
    public void addLast(E e) {
        linkLast(e);
    }
    
    /**
     * Checks the data structure invariant. Throws 
     * {@link java.lang.IllegalStateException} on invalid invariant. The 
     * invariant is valid if:
     * <ol>
     * <li>All the fingers in the finger list are sorted by indices.</li>
     * <li>There is no duplicate indices.</li>
     * <li>The index of the leftmost finger is no less than zero.</li>
     * <li>There must be an end-of-list sentinel finger {@code F}, such that 
     * {@code F.index = } <i>size of linked list</i> and {@code F.node} is 
     * {@code null}.
     * </li>
     * <li>Each finger {@code F} points to the {@code i}th linked list node,
     * where {@code i = F.index}.</li>
     * </ol>
     * Runs always in linear time.
     */
    public void checkInvarant() {
        for (int i = 0; i < fingerList.size() - 1; ++i) {
            Finger<E> left = fingerList.get(i);
            Finger<E> right = fingerList.get(i + 1);
            
            if (left.index >= right.index) {
                throw new IllegalStateException(
                        "FingerList failed: fingerList[" 
                                + i
                                + "].index = " 
                                + left.index 
                                + " >= " 
                                + right.index 
                                + " = fingerList[" 
                                + (i + 1) 
                                + "]");
            }
        }
        
        Finger<E> sentinelFinger = fingerList.get(fingerList.size());
        
        if (sentinelFinger.index != this.size || sentinelFinger.node != null) {
            throw new IllegalStateException(
                    "Invalid end-of-list sentinel: " + sentinelFinger);
        }
        
        Finger<E> finger = fingerList.get(0);
        Node<E> node = first;
        int fingerCount = 0;
        int tentativeSize = 0;
        
        while (node != null) {
            tentativeSize++;
            
            if (finger.node == node) {
                finger = fingerList.get(++fingerCount);
            }
            
            node = node.next;
        }
        
        if (size != tentativeSize) {
            throw new IllegalStateException(
                    "Number of nodes mismatch: size = " 
                            + size 
                            + ", tentativeSize = " 
                            + tentativeSize);
        }
        
        if (fingerList.size() != fingerCount) {
            throw new IllegalStateException(
                    "Number of fingers mismatch: fingerList.size() = " 
                            + fingerList.size() 
                            + ", fingerCount = " 
                            + fingerCount);
        }
    }
    
    /**
     * Completely clears this list.
     */
    @Override
    public void clear() {
        fingerList.clear();
        size = 0;
        
        // Help GC:
        for (Node<E> node = first; node != null;) {
            node.prev = null;
            node.item = null;
            Node<E> next = node.next;
            node.next = null;
            node = next;
        }

        first = last = null;
        modCount++;
    }
    
    /**
     * Returns the clone list with same content as this list.
     * 
     * @return the clone list.
     */
    @Override
    public Object clone() {
        return new IndexedLinkedList<>(this);
    }
    
    /**
     * Returns {@code true} only if {@code o} is present in this list. Runs in
     * worst-case linear time.
     * 
     * @param o the query object.
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    
    /**
     * Returns {@code true} only if this list contains all the elements 
     * mentioned in the {@code c}. Runs in Runs in \(\mathcal{O}(mn)\) time, 
     * where \(m = |c|\).
     * 
     * @param c the query object collection.
     * @return {@code true} only if  this list contains all the elements in
     *         {@code c}, or {@code false} otherwise.
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Returns the descending iterator.
     * 
     * @return the descending iterator pointing to the tail of this list.
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }
    
    /**
     * Returns the first element of this list. Runs in constant time.
     * 
     * @return the first element of this list.
     * @throws NoSuchElementException if this list is empty.
     */
    @Override
    public E element() {
        return getFirst();
    }
    
    /**
     * Returns {@code true} only if {@code o} is an instance of 
     * {@link java.util.List} and has the sane contents as this list. Runs in
     * worst-case linear time.
     * 
     * @return {@code true} only if {@code o} is a list with the same contents
     *         as this list.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof List)) {
            return false;
        }
        
        int expectedModCount = modCount;
        
        List<?> otherList = (List<?>) o;
        boolean equal = equalsRange(otherList, 0, size);
        
        checkForComodification(expectedModCount);
        return equal;
    }
    
    /**
     * Returns {@code index}th element. Runs in worst-case Runs in worst-case
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @return {@code index}th element.
     * @throws IndexOutOfBoundsException if the index is out of range
     *         {@code 0, 1, ..., size - 1}, or if this list is empty.
     */
    @Override
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }
    
    /**
     * Returns the first element of this list. Runs in constant time.
     * 
     * @return the first element of this list.
     * @throws NoSuchElementException if this list is empty.
     */
    @Override
    public E getFirst() {
        if (first == null) {
            throw new NoSuchElementException(
                    "Getting the head element from an empty list.");
        }
        
        return first.item;
    }
    
    /**
     * Returns the last element of this list. Runs in constant time.
     * 
     * @return the last element of this list.
     * @throws NoSuchElementException if this list is empty.
     */
    @Override
    public E getLast() {
        if (last == null) {
            throw new NoSuchElementException(
                    "Getting the tail element from an empty list.");
        }
        
        return last.item;
    }
    
    /**
     * Returns the hash code of this list. Runs in linear time.
     * 
     * @return the hash code of this list.
     */
    @Override
    public int hashCode() {
        int expectedModCount = modCount;
        int hash = hashCodeRange(0, size);
        checkForComodification(expectedModCount);
        return hash;
    }
    
    /**
     * Returns the index of the leftmost {@code obj}, or {@code -1} if 
     * {@code obj} does not appear in this list. Runs in worst-case linear time.
     * 
     * @return the index of the leftmost {@code obj}, or {@code -1} if 
     *         {@code obj} does not appear in this list.
     * 
     * @see IndexedLinkedList#lastIndexOf(java.lang.Object) 
     */
    @Override
    public int indexOf(Object obj) {
        return indexOfRange(obj, 0, size);
    }
    
    /**
     * Returns {@code true} only if this list is empty.
     * 
     * @return {@code true} only if this list is empty.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the iterator over this list.
     * 
     * @return the iterator over this list.
     */
    @Override
    public Iterator<E> iterator() {
        return new BasicIterator();
    }

    /**
     * Returns the index of the rightmost {@code obj}, or {@code -1} if 
     * {@code obj} does not appear in this list. Runs in worst-case linear time.
     * 
     * @return the index of the rightmost {@code obj}, or {@code -1} if
     *         {@code obj} does not appear in this list.
     * 
     * @see IndexedLinkedList#lastIndexOf(java.lang.Object) 
     */
    @Override
    public int lastIndexOf(Object obj) {
        return lastIndexOfRange(obj, 0, size);
    }
    
    /**
     * Returns the list iterator pointing to the head element of this list.
     * 
     * @return the list iterator.
     * @see java.util.ListIterator
     */
    @Override
    public ListIterator<E> listIterator() {
        return new EnhancedIterator(0);
    }
    
    /**
     * Returns the list iterator pointing between {@code list[index - 1]} and
     * {@code list[index]}.
     * 
     * @param index the gap index. The value of zero will point before the head 
     *              element.
     * 
     * @return the list iterator pointing to the {@code index}th gap.
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return new EnhancedIterator(index);
    }
    
    /**
     * Adds {@code e} after the tail element of this list. Runs in constant 
     * time.
     * 
     * @param e the element to add.
     * @return always {@code true}.
     */
    @Override
    public boolean offer(E e) {
        return add(e);
    }

    /**
     * Adds {@code e} before the head element of this list. Runs in 
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @param e the element to add.
     * @return always {@code true}.
     */
    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Adds {@code e} after the tail element of this list. Runs in constant 
     * time.
     * 
     * @param e the element to add.
     * @return always {@code true}.
     */
    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }
    
    /**
     * Moves all the fingers such that they are evenly distributed. Runs in 
     * linear time.
     */
    public void optimize() {
        distributeAllFingers();
    }
    
    /**
     * Takes a look at the first element in this list.
     * 
     * @return the head element or {@code null} if this list is empty.
     */
    @Override
    public E peek() {
        return first == null ? null : first.item;
    }

    /**
     * Takes a look at the first element in this list.
     * 
     * @return the head element or {@code null} if this list is empty.
     */
    @Override
    public E peekFirst() {
        return first == null ? null : first.item;
    }

    /**
     * Takes a look at the last element in this list.
     * 
     * @return the tail element or {@code null} if this list is empty.
     */
    @Override
    public E peekLast() {
        return last == null ? null : last.item;
    }
    
    /**
     * If this list is empty, does nothing else but return {@code null}. 
     * Otherwise, removes the first element and returns it. Runs in 
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @return the first element (which was removed due to the call to this 
     *         method), or {@code null} if the list is empty.
     */
    @Override
    public E poll() {
        return first == null ? null : removeFirst();
    }

    /**
     * If this list is empty, does nothing else but return {@code null}. 
     * Otherwise, removes the first element and returns it. Runs in 
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @return the first element (which was removed due to the call to this 
     *         method), or {@code null} if the list is empty.
     */
    @Override
    public E pollFirst() {
        return first == null ? null : removeFirst();
    }

    /**
     * If this list is empty, does nothing else but return {@code null}. 
     * Otherwise, removes the last element and returns it. Runs in constant 
     * time.
     * 
     * @return the last element (which was removed due to the call to this 
     *         method), or {@code null} if the list is empty.
     */
    @Override
    public E pollLast() {
        return last == null ? null : removeLast();
    }
    
    /**
     * Removes the first element and returns it.
     * Runs in \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @return the first element.
     * @throws NoSuchElementException if the list is empty.
     */
    @Override
    public E pop() {
        return removeFirst();
    }

    /**
     * Adds {@code e} before the head of this list. Runs in 
     * \(\mathcal{O}(\sqrt{n})\) time.
     */
    @Override
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Removes and returns the first element. Runs in 
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @return the head element of this list.
     * @throws NoSuchElementException if this list is empty.
     */
    @Override
    public E remove() {
        return removeFirst();
    }

    /**
     * Removes the leftmost occurrence of {@code o} in this list. Runs in worst-
     * case Runs in \(\mathcal{O}(n + \sqrt{n})\) time. \(\mathcal{O}(n)\) for
     * iterating the list and \(\mathcal{O}(\sqrt{n})\) time for fixing the 
     * fingers.
     * 
     * @return {@code true} only if {@code o} was located in this list and, 
     *         thus, removed.
     */
    @Override
    public boolean remove(Object o) {
        int index = 0;

        for (Node<E> x = first; x != null; x = x.next, index++) {
            if (Objects.equals(o, x.item)) {
                removeObjectImpl(x, index);
                return true;
            }
        }

        return false;
    }
    
    /**
     * Removes the element residing at the given index. Runs in worst-case
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @param index the index of the element to remove.
     * @return the removed element. (The one that resided at the index 
     *         {@code index}.)
    */
    @Override
    public E remove(int index) {
        checkElementIndex(index);
        
        int closestFingerIndex = fingerList.getFingerIndex(index);
        Finger<E> closestFinger = fingerList.get(closestFingerIndex);
        
        E returnValue;
        Node<E> nodeToRemove;
        
        if (closestFinger.index == index) {
            nodeToRemove = closestFinger.node;
            moveFingerOutOfRemovalLocation(closestFinger, 
                                           closestFingerIndex);    
        } else {
            // Keep the fingers at their original position.
            // Find the target node:
            int steps = closestFinger.index - index;
            
            nodeToRemove =
                    traverseLinkedListBackwards(
                            closestFinger,
                            steps);
            
            for (int i = closestFingerIndex + 1;
                    i <= fingerList.size(); 
                    i++) {
                fingerList.get(i).index--;
            }
            
            if (steps > 0) {
                fingerList.get(closestFingerIndex).index--;
            }
        }
        
        returnValue = nodeToRemove.item;
        unlink(nodeToRemove);
        decreaseSize();

        if (mustRemoveFinger()) {
            removeFinger();
        }

        return returnValue;
    }
    
    /**
     * Removes from this list all the elements mentioned in {@code c}. Runs in
     * \(\mathcal{O}(n\sqrt{n} + fn)\) time, where \(\mathcal{O}(f)\) is the 
     * time of checking for element inclusion in {@code c}.
     * 
     * @param c the collection holding all the elements to remove.
     * @return {@code true} only if at least one element in {@code c} was 
     *         located and removed from this list.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, true, 0, size);
    }

    /**
     * Removes the first element from this list. Runs in 
     * \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @return the first element.
     */
    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "removeFirst from an empty LinkedList");
        }
        
        E returnValue = first.item;
        decreaseSize();
        
        first = first.next;
        
        if (first == null) {
            last = null;
        } else {
            first.prev = null;
        }
        
        fingerList.adjustOnRemoveFirst();
        
        if (mustRemoveFinger()) {
            removeFinger();
        }

        fingerList.get(fingerList.size()).index = size;
        return returnValue;
    }
    
    /**
     * Removes the leftmost occurrence of {@code o}. Runs in worst-case 
     * \(\mathcal{O}(n)\) time.
     * 
     * @return {@code true} only if {@code o} was present in the list and was 
     *         successfully removed.
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {
        int index = 0;
        
        for (Node<E> x = first; x != null; x = x.next, index++) {
            if (Objects.equals(o, x.item)) {
                removeObjectImpl(x, index);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Removes from this list all the elements that satisfy the given input
     * predicate. Runs in \(\mathcal{O}(n\sqrt{n})\) time.
     * 
     * @param filter the filtering predicate.
     * @return {@code true} only if at least one element was removed.
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return removeIf(filter, 0, size);
    }
    
    /**
     * Removes and returns the last element of this list. Runs in constant time.
     * 
     * @return the removed head element.
     * @throws NoSuchElementException if this list is empty.
     */
    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("removeLast on empty LinkedList");
        }
        
        E returnValue = last.item;
        decreaseSize();
        
        last = last.prev;
        
        if (last == null) {
            first = null;
        } else {
            last.next = null;
        }
        
        if (mustRemoveFinger()) {
            removeFinger();
        }
        
        return returnValue;
    }

    /**
     * Removes the rightmost occurrence of {@code o}. Runs in 
     * \(\mathcal{O}(n)\) time.
     * 
     * @param o the object to remove.
     * @return {@code true} only if an element was actually removed.
     */
    @Override
    public boolean removeLastOccurrence(Object o) {
        int index = size - 1;

        for (Node<E> x = last; x != null; x = x.prev, index--) {
            if (Objects.equals(o, x.item)) {
                removeObjectImpl(x, index);
                return true;
            }
        }

        return false;
    }

    /**
     * Replaces all the elements in this list by applying the given input 
     * operator to each of the elements. Runs in linear time.
     * 
     * @param operator the operator mapping one element to another. 
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        replaceAllRange(operator, 0, size);
        modCount++;
    }
    
    /**
     * Remove all the elements that <strong>do not</strong> appear in 
     * {@code c}. Runs in worst-case \(\mathcal{O}(nf + n\sqrt{n})\) time, where
     * the inclusion check is run in \(\mathcal{O}(f)\) time.
     * 
     * @param c the collection of elements to retain.
     * @return {@code true} only if at least one element was removed.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, false, 0, size);
    }
    
    /**
     * Sets the element at index {@code index} to {@code element} and returns
     * the old element. Runs in worst-case \(\mathcal{O}(\sqrt{n})\) time.
     * 
     * @param index   the target index.
     * @param element the element to set.
     * @return the previous element at the given index.
     */
    @Override
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> node = node(index);
        E oldElement = node.item;
        node.item = element;
        return oldElement;
    }
    
    /**
     * Returns the number of elements in this list.
     * 
     * @return the size of this list.
     */
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Sorts stably this list into non-descending order. Runs in 
     * \(\mathcal{O}(n \log n)\).
     * 
     * @param c the element comparator.
     */
    @Override
    public void sort(Comparator<? super E> c) {
        if (size == 0) {
            return;
        }
        
        Object[] array = toArray();
        Arrays.sort((E[]) array, c);
        
        Node<E> node = first;
        
        // Rearrange the items over the linked list nodes:
        for (int i = 0; i < array.length; ++i, node = node.next) {
            E item = (E) array[i];
            node.item = item;
        }
        
        distributeAllFingers();
        modCount++;
    }
    
    /**
     * Returns the spliterator over this list.
     */
    @Override
    public Spliterator<E> spliterator() {
        return new LinkedListSpliterator<>(this, first, size, 0, modCount);
    }
    
    /**
     * Returns a sublist view 
     * {@code list[fromIndex, fromIndex + 1, ..., toIndex - 1}.
     * 
     * @param fromIndex the smallest index, inclusive.
     * @param toIndex the largest index, exclusive.
     * @return the sublist view.
     */
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new EnhancedSubList(this, fromIndex, toIndex);
    }
    
    /**
     * Returns the {@link Object} array containing all the elements in this 
     * list, in the same order as they appear in the list.
     * 
     * @return the list contents in an {@link Object} array.
     */
    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int index = 0;
        
        for (Node<E> node = first; node != null; node = node.next) {
            arr[index++] = node.item;
        }
        
        return arr;
    }
    
    /**
     * Generates the array containing all the elements in this list.
     * 
     * @param <T>       the array component type.
     * @param generator the generator function.
     * @return the list contents in an array with component type of {@code T}.
     */
    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return toArray(generator.apply(size));
    }
    
    /**
     * If {@code a} is sufficiently large, returns the same array holding all
     * the contents of this list. Also, if {@code a} is larger than the input
     * array, sets {@code a[s] = null}, where {@code s} is the size of this
     * list. However, if {@code a} is smaller than this list, allocates a new
     * array of the same length, populates it with the list contents and returns
     * it.
     * 
     * @param <T> the element type.
     * @param a the input array.
     * @return an array holding the contents of this list.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }
        
        int index = 0;
        
        for (Node<E> node = first; node != null; node = node.next) {
            a[index++] = (T) node.item;
        }
        
        if (a.length > size) {
            a[size] = null;
        }
        
        return a;
    }
    
    /**
     * Returns the string representation of this list, listing all the elements.
     * 
     * @return the string representation of this list.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        
        boolean firstIteration = true;
        
        for (E element : this) {
            if (firstIteration) {
                firstIteration = false;
            } else {
                stringBuilder.append(", ");
            }
            
            stringBuilder.append(element);
        }
        
        return stringBuilder.append("]").toString();
    }

    int getFingerListSize() {
        return fingerList.size();
    }
    
    // Computes the recommended number of fingers for 'size' elements.
    private static int getRecommendedNumberOfFingers(int size) {
        return (int) Math.ceil(Math.sqrt(size));
    }
    
    private static void subListRangeCheck(int fromIndex, 
                                          int toIndex, 
                                          int size) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }

        if (toIndex > size){
            throw new IndexOutOfBoundsException(
                    "toIndex(" + toIndex + ") > size(" + size + ")");
        }

        if (fromIndex > toIndex)
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") > toIndex(" 
                            + toIndex + ")");
    }
    
    // Adds fingers after appending a collection to this list.
    private void addFingersAfterAppendAll(
            Node<E> first,
            int firstIndex,
            int collectionSize) {
        int numberOfNewFingers = 
                getRecommendedNumberOfFingers() - fingerList.size();

        if (numberOfNewFingers == 0) {
            fingerList.get(fingerList.size()).index += collectionSize;
            return;
        }

        int distanceBetweenFingers = collectionSize / numberOfNewFingers;
        int nodesToSkip = distanceBetweenFingers / 2;
        int index = firstIndex + nodesToSkip;
        Node<E> node = first;

        for (int i = 0; i < nodesToSkip; i++) {
            node = node.next;
        }
        
        int fingerIndex = fingerList.size();
        
        fingerList.makeRoomAtIndex(fingerIndex, 
                                   numberOfNewFingers, 
                                   collectionSize);

        fingerList.setFinger(fingerIndex++, new Finger<>(node, index));

        for (int i = 1; i < numberOfNewFingers; i++) {
            index += distanceBetweenFingers;

            for  (int j = 0; j < distanceBetweenFingers; j++) {
                node = node.next;
            }

            fingerList.setFinger(fingerIndex++, new Finger<>(node, index));
        }
    }
    
    // Adds fingers after inserting a collection in this list.
    private void addFingersAfterInsertAll(Node<E> headNodeOfInsertedRange,
                                          int indexOfInsertedRangeHead,
                                          int collectionSize) {
        int numberOfNewFingers =
                getRecommendedNumberOfFingers() - fingerList.size();

        if (numberOfNewFingers == 0) {
            int fingerIndex = 
                    fingerList.getFingerIndexImpl(indexOfInsertedRangeHead);
            
            fingerList.shiftFingerIndicesToRight(fingerIndex, collectionSize);
            return;
        }

        int distanceBetweenFingers = collectionSize / numberOfNewFingers;
        int startOffset = distanceBetweenFingers / 2;
        int index = indexOfInsertedRangeHead + startOffset;
        Node<E> node = headNodeOfInsertedRange;
        
        for (int i = 0; i < startOffset; i++) {
           node = node.next;
        }

        int startFingerIndex =
                fingerList.getFingerIndexImpl(indexOfInsertedRangeHead);
        
        fingerList.makeRoomAtIndex(startFingerIndex, 
                                   numberOfNewFingers, 
                                   collectionSize);
        
        fingerList.setFinger(startFingerIndex, new Finger<>(node, index));

        for (int i = 1; i < numberOfNewFingers; i++) {
            index += distanceBetweenFingers;

            for (int j = 0; j < distanceBetweenFingers; j++) {
                node = node.next;
            }

            fingerList.setFinger(startFingerIndex + i, 
                                 new Finger<>(node, index));
        }
    }
    
    // Adds fingers after prepending a collection to this list.
    private void addFingersAfterPrependAll(Node<E> first, int collectionSize) {
        int numberOfNewFingers =
                getRecommendedNumberOfFingers() - fingerList.size();

        if (numberOfNewFingers == 0) {
            fingerList.shiftFingerIndicesToRight(0, collectionSize);
            return;
        }
        
        fingerList.makeRoomAtIndex(0, numberOfNewFingers, collectionSize);

        int distance = collectionSize / numberOfNewFingers;
        int startIndex = distance / 2;
        int index = startIndex;
        Node<E> node = first;

        for (int i = 0; i < startIndex; i++) {
            node = node.next;
        }
        
        int fingerIndex = 0;
        
        fingerList.setFinger(fingerIndex++, new Finger<>(node, index));

        for (int i = 1; i < numberOfNewFingers; i++) {
            index += distance;

            for (int j = 0; j < distance; j++) {
                node = node.next;
            }

            fingerList.setFinger(fingerIndex++, new Finger<>(node, index)); 
        }
    }
    
    // Adds fingers after setting a collection as a list.
    private void addFingersAfterSetAll(int collectionSize) {
        int numberOfNewFingers = getRecommendedNumberOfFingers();
        int distance = size / numberOfNewFingers;
        int startIndex = distance / 2;
        int index = startIndex;
        fingerList.makeRoomAtIndex(0,
                                   numberOfNewFingers, 
                                   collectionSize);
        
        Node<E> node = first;

        for (int i = 0; i < startIndex; i++) {
            node = node.next;
        }
        
        fingerList.setFinger(0, new Finger<>(node, startIndex));

        for (int i = 1; i < numberOfNewFingers; i++) {
            index += distance;

            for (int j = 0; j < distance; j++) {
                node = node.next;
            }

            fingerList.setFinger(i, new Finger<>(node, index));
        }
    }
    
    // Appends the input collection to the tail of this list.
    private void appendAll(Collection<? extends E> c) {
        Node<E> prev = last;
        Node<E> oldLast = last;

        for (E item : c) {
            Node<E> newNode = new Node<>(item);
            newNode.item = item;
            newNode.prev = prev;
            prev.next = newNode;
            prev = newNode;
        }

        last = prev;
        int sz = c.size();
        size += sz;
        modCount++;
        addFingersAfterAppendAll(oldLast.next, size - sz, sz);
    }
    
    // Adds the finger to the tail of the finger list and before the end-of-
    // finger-list sentinel.
    private void appendFinger(Node<E> node, int index) {
        Finger<E> finger = new Finger<>(node, index);
        fingerList.appendFinger(finger);
    }
    
    /**
     * This class implements a basic iterator over this list.
     */
    public final class BasicIterator implements Iterator<E> {
        
        private Node<E> lastReturned;
        private Node<E> next = first;
        private int nextIndex;
        int expectedModCount = IndexedLinkedList.this.modCount;

        /**
         * Constructs the basic iterator pointing to the first element.
         */
        BasicIterator() {
            
        }
        
        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public E next() {
            checkForComodification();
            
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            
            checkForComodification();
            
            int removalIndex = nextIndex - 1;
            removeObjectImpl(lastReturned, removalIndex);
            nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                next = next.next;
                nextIndex++;
            }
            
            checkForComodification();
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    // Implements the batch remove. If 'complement' is true, this operation 
    // removes all the elements appearing in 'c'. Otherwise, it will retain all
    // the elements present in 'c':
    private boolean batchRemove(Collection<?> c,
                                boolean complement,
                                int from,  
                                int end) {
        Objects.requireNonNull(c);
        
        if (c.isEmpty()) {
            return false;
        }
        
        boolean modified = false;
        
        int numberOfNodesToIterate = end - from;
        int i = 0;
        int nodeIndex = from;
        
        for (Node<E> node = node(from); i < numberOfNodesToIterate; ++i) {
            Node<E> nextNode = node.next;
            
            if (c.contains(node.item) == complement) {
                modified = true;
                removeObjectImpl(node, nodeIndex);
            } else {
                nodeIndex++;
            }
            
            node = nextNode;
        }
        
        return modified;
    }
    
    // Checks the element index. In the case of non-empty list, valid indices 
    // are '{ 0, 1, ..., size - 1 }'.
    private void checkElementIndex(int index) {
        if (!isElementIndex(index)) {
            throw new IndexOutOfBoundsException(getOutOfBoundsMessage(index));
        }
    }
    
    private void checkForComodification(int expectedModCount) {
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    // Checks that the input index is a valid position index for add operation 
    // or iterator position. In other words, checks that {@code index} is in the 
    // set '{ 0, 1, ..., size}'.
    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index)) {
            throw new IndexOutOfBoundsException(getOutOfBoundsMessage(index));
        }
    }
    
    private void decreaseSize() {
        size--;
        modCount++;
    }   
    
    // Distributes the fingers over the element list [fromIndex, toIndex):
    private void distributeFingers(int fromIndex, int toIndex) {
        int rangeLength = toIndex - fromIndex;
        
        if (rangeLength == 0) {
            return;
        }
        
        int fingerPrefixLength = fingerList.getFingerIndexImpl(fromIndex);
        int fingerSuffixLength = fingerList.size() 
                               - fingerList.getFingerIndexImpl(toIndex);
        
        int numberOfRangeFingers = fingerList.size()
                                 - fingerPrefixLength 
                                 - fingerSuffixLength;
        
        int numberOfFingersPerFinger = rangeLength / numberOfRangeFingers;
        int startOffset = numberOfFingersPerFinger / 2;
        int index = fromIndex + startOffset;
        
        Node<E> node = node(fromIndex);
        
        for (int i = 0; i < startOffset; ++i) {
            node = node.next;
        }
        
        for (int i = 0; i < numberOfRangeFingers - 1; ++i) {
            Finger<E> finger = fingerList.get(i);
            finger.node = node;
            finger.index = index;
            
            for (int j = 0; j < numberOfFingersPerFinger; ++j) {
                node = node.next;
            }
            
            index += numberOfFingersPerFinger;
        }
        
        // Since we cannot advance node to the right, we need to deal with the
        // last (non-sentinel) finger manually:
        Finger<E> lastFinger = fingerList.get(fingerList.size() - 1);
        lastFinger.node = node;
        lastFinger.index = index;
    }
    
    // Distributes evenly all the figners over this list:
    private void distributeAllFingers() {
        distributeFingers(0, size);
    }
    
    // Implements the descending list iterator over this list.
    private final class DescendingIterator implements Iterator<E> {

        private Node<E> lastReturned;
        private Node<E> nextToIterate = last;
        private int nextIndex = IndexedLinkedList.this.size - 1;
        int expectedModCount = IndexedLinkedList.this.modCount;
        
        @Override
        public boolean hasNext() {
            return nextIndex > -1;
        }
        
        @Override
        public E next() {
            checkForComodification();
            
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            lastReturned = nextToIterate;
            nextToIterate = nextToIterate.prev;
            nextIndex--;
            return lastReturned.item;
        }
        
        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            
            checkForComodification();
            
            removeObjectImpl(lastReturned, nextIndex + 1);
            lastReturned = null;
            expectedModCount++;
        }
        
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            
            while (modCount == expectedModCount && hasNext()) {
                action.accept(nextToIterate.item);
                nextToIterate = nextToIterate.prev;
                nextIndex--;
            }
            
            checkForComodification();
        }
        
        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    // Implements the enhanced list iterator over this list.
    final class EnhancedIterator implements ListIterator<E> {

        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        
        // Package-private for the sake of unit testing:
        int expectedModCount = modCount;
        
        EnhancedIterator(int index) {
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }
        
        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }
        
        @Override
        public E next() {
            checkForComdification();
            
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public E previous() {
            checkForComdification();
            
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            
            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            checkForComdification();
            
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            
            Node<E> lastNext = lastReturned.next;
            int removalIndex = nextIndex - 1;
            removeObjectImpl(lastReturned, removalIndex);
            
            if (next == lastReturned) {
                next = lastNext;
            } else {
                nextIndex = removalIndex;
            }
            
            lastReturned = null;
            expectedModCount++;
        }

        @Override
        public void set(E e) {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            
            checkForComdification();
            lastReturned.item = e;
        }

        @Override
        public void add(E e) {
            checkForComdification();
            
            lastReturned = null;
            
            if (next == null) {
                linkLast(e);
            } else {
                linkBefore(e, next, nextIndex);
            }
            
            nextIndex++;
            expectedModCount++;
        }
        
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                next = next.next;
                nextIndex++;
            }
            
            checkForComdification();
        }
        
        private void checkForComdification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    private boolean equalsRange(List<?> other, int from, int to) {
        Iterator<?> otherIterator = other.iterator();
        
        for (Node<E> node = node(from); from < to; from++, node = node.next) {
            if (!otherIterator.hasNext() || 
                    !Objects.equals(node.item, otherIterator.next())) {
                return false;
            }
        }
        
        return true;
    }
    
    // Constructs an IndexOutOfBoundsException detail message.
    private String getOutOfBoundsMessage(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    // Computes the recommended number of fingers.
    private int getRecommendedNumberOfFingers() {
        return (int) Math.ceil(Math.sqrt(size));
    }
    
    // Computes the hash code for the range [from, to):
    private int hashCodeRange(int from, int to) {
        int hashCode = 1;
        
        Node<E> node = node(from);
        
        while (from++ < to) {
            // Same arithmetics as in ArrayList.
            hashCode =
                    31 * hashCode + 
                    (node.item == null ? 0 : node.item.hashCode());
            
            node = node.next;
        }
        
        return hashCode;
    }
    
    // Increases the size of the list and its modification count.
    private void increaseSize() {
        ++size;
        ++modCount;
    }
    
    private int indexOfRange(Object o, int start, int end) {
        int index = start;
        
        if (o == null) {
            for (Node<E> node = node(start);
                    index < end; 
                    index++, node = node.next) {
                if (node.item == null) {
                    return index;
                }
            }
        } else {
            for (Node<E> node = node(start);
                    index < end;
                    index++, node = node.next) {
                if (o.equals(node.item)) {
                    return index;
                }
            }
        }
        
        return -1;
    }
    
    // Inserts the input collection right before the node 'succ'.
    private void insertAll(Collection<? extends E> c,
                           Node<E> succ,
                           int succIndex) {
        
        Node<E> pred = succ.prev;
        Node<E> prev = pred;

        for (E item : c) {
            Node<E> newNode = new Node<>(item);
            newNode.prev = prev;
            prev.next = newNode;
            prev = newNode;
        }

        prev.next = succ;
        succ.prev = prev;

        int sz = c.size();
        modCount++;
        size += sz;
        
        // Add fingers:
        addFingersAfterInsertAll(pred.next, 
                                 succIndex,
                                 sz);
    }

    // Tells if the argument is the index of an existing element.
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    // Tells if the argument is the index of a valid position for an iterator or 
    // an add operation.
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }
    
    // Returns the last appearance index of 'obj'.
    private int lastIndexOfRange(Object o, int start, int end) {
        int index = end - 1;
        
        if (o == null) {
            for (Node<E> node = node(index);
                    index >= start; 
                    index--, node = node.prev) {
                if (node.item == null) {
                    return index;
                }
            }
        } else {
            for (Node<E> node = node(index);
                    index >= start;
                    index--, node = node.prev) {
                if (o.equals(node.item)) {
                    return index;
                }
            }
        }
        
        return -1;
    }
    
    // Links the input element right before the node 'succ'.
    private void linkBefore(E e, Node<E> succ, int index) {
        Node<E> pred = succ.prev;
        Node<E> newNode = new Node<>(e);
        newNode.next = succ;
        succ.prev = newNode;

        if (pred == null) {
            first = newNode;
        } else {
            pred.next = newNode;
            newNode.prev = pred;
        }

        size++;
        modCount++;

        if (mustAddFinger()) {
            fingerList.insertFingerAndShiftOnceToRight(
                    new Finger<>(newNode, index));
        } else {
            int fingerIndex = fingerList.getFingerIndex(index);
            fingerList.shiftFingerIndicesToRightOnce(fingerIndex);
        }
    }
    
    // Prepends the input element to the head of this list.
    private void linkFirst(E e) {
        Node<E> f = first;
        Node<E> newNode = new Node<>(e);
        newNode.next = f;
        first = newNode;

        if (f == null) {
            last = newNode;
        } else {
            f.prev = newNode;
        }

        increaseSize();

        if (mustAddFinger()) {
            fingerList.insertFingerAndShiftOnceToRight(
                    new Finger<>(newNode, 0));
        } else {
            fingerList.shiftFingerIndicesToRightOnce(0);
        }
    }
    
    // Appends the input element to the tail of this list.
    private void linkLast(E e) {
        Node<E> l = last;
        Node<E> newNode = new Node<>(e);
        newNode.prev = l;
        last = newNode;
        
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        
        increaseSize();
        
        if (mustAddFinger()) {
            appendFinger(newNode, size - 1);
        } else {
            fingerList.get(fingerList.size()).index++;
        }
    }
    
    // Sets a finger that does not point to the element to remove. We need this
    // in order to make sure that after removal, all the fingers point to valid
    // nodes.
    void moveFingerOutOfRemovalLocation(Finger<E> finger, int fingerIndex) {
        if (fingerList.size() == size()) {
            // Here, fingerList.size() is 1 or 2 and the size of the list is the
            // same:
            if (fingerList.size() == 1) {
                // The only finger will be removed in 'remove(int)'. Return:
                return;
            }
            
            // Once here, 'fingerList.size() == 2'!
            switch (fingerIndex) {
                case 0:
                    // Shift 2nd and the sentinal fingers one position to the
                    // left:
                    fingerList.setFinger(0, fingerList.get(1));
                    fingerList.get(0).index = 0;
                    fingerList.setFinger(1, fingerList.get(2));
                    fingerList.get(1).index = 1;
                    fingerList.setFinger(2, null);
                    fingerList.size = 1;
                    break;
                    
                case 1:
                    // Just remove the (last) finger:
                    fingerList.removeFinger();
                    fingerList.get(1).index = 1;
                    break;
            }
            
            return;
        }
        
        // Try push the fingers to the right:
        for (int f = fingerIndex; f < fingerList.size(); ++f) {
            Finger<E> fingerLeft  = fingerList.get(f);
            Finger<E> fingerRight = fingerList.get(f + 1);

            if (fingerLeft.index + 1 < fingerRight.index) {
                for (int i = f; i >= fingerIndex; --i) {
                    Finger<E> fngr = fingerList.get(i);
                    fngr.node = fngr.node.next;
                }

                for (int j = f + 1; j <= fingerList.size(); ++j) {
                    fingerList.get(j).index--;
                }

                return;
            }
        }
        
        // Could not push the fingers to the right. Push to the left. Since the
        // number of fingers here is smaller than the list size, there must be
        // a spot to move to some fingers:
        for (int f = fingerIndex; f > 0; --f) {
            Finger<E> fingerLeft  = fingerList.get(f - 1);
            Finger<E> fingerRight = fingerList.get(f);
            
            if (fingerLeft.index + 1 < fingerRight.index) {
                for (int i = fingerIndex; i > 0; --i) {
                    Finger<E> fngr = fingerList.get(i);
                    fngr.node = fngr.node.prev;
                    fngr.index--;
                }
                
                for (int i = fingerIndex + 1; i <= fingerList.size(); ++i) {
                    fingerList.get(i).index--;
                }
                
                return;
            }
        }
        
        // Once here, the only free spots are at the very beginning of the
        // finger list:
        for (int i = 0; i < fingerList.size(); ++i) {
            Finger<E> fngr = fingerList.get(i);
            fngr.index--;
            fngr.node = fngr.node.prev;
        }
        
        // The end-of-finger-list node has no Finger<E>.node defined. Take it 
        // outside of the above loop and decrement its index manually:cd 
        fingerList.get(fingerList.size()).index--;
    }
    
    // Returns true only if this list requires more fingers.
    private boolean mustAddFinger() {
        // Here, fingerStack.size() == getRecommendedFingerCount(), or,
        // fingerStack.size() == getRecommendedFingerCount() - 1
        return fingerList.size() != getRecommendedNumberOfFingers();
    }
    
    // Returns true only if this list requires less fingers.
    private boolean mustRemoveFinger() {
        // Here, fingerStack.size() == getRecommendedFingerCount(), or,
        // fingerStack.size() == getRecommendedFingerCount() + 1
        return fingerList.size() != getRecommendedNumberOfFingers();
    }
    
    // Returns the node at index 'elementIndex'.
    private Node<E> node(int elementIndex) {
         return fingerList.node(elementIndex);
    }
    
    // Prepends the input collection to the head of this list.
    private void prependAll(Collection<? extends E> c) {
        Iterator<? extends E> iterator = c.iterator();
        Node<E> oldFirst = first;
        first = new Node<>(iterator.next());

        Node<E> prevNode = first;

        for (int i = 1, sz = c.size(); i < sz; i++) {
            Node<E> newNode = new Node<>(iterator.next());
            newNode.prev = prevNode;
            prevNode.next = newNode;
            prevNode = newNode;
        }

        prevNode.next = oldFirst;
        oldFirst.prev = prevNode;

        int sz = c.size();
        modCount++;
        size += sz;

        // Now, add the missing fingers:
        addFingersAfterPrependAll(first, sz);
    }
    
    /**
     * Reconstitutes this {@code LinkedList} instance from a stream (that is, 
     * deserializes it).
     * 
     * @param s the object input stream.
     * 
     * @serialData first, the size of the list is read. Then all the node items
     *             are read and stored in the deserialization order, that is the
     *             same order as in serialization.
     * 
     * @throws java.io.IOException if I/O fails.
     * @throws ClassNotFoundException if the class is not found.
     */
    @java.io.Serial
    private void readObject(java.io.ObjectInputStream s) 
            throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        int size = s.readInt();
        this.size = size;
        this.fingerList = new FingerList<>();

        switch (size) {
            case 0:
                return;
                
            case 1:
                Node<E> newNode = new Node<>((E) s.readObject());
                first = last = newNode;
                fingerList.appendFinger(new Finger<>(newNode, 0));
                return;
        }
        
        Node<E> rightmostNode = new Node<>((E) s.readObject());
        first = rightmostNode;
        
        int numberOfRequestedFingers = getRecommendedNumberOfFingers(size);
        int distance = size / numberOfRequestedFingers;
        int startOffset = distance / 2;
        
        // Read in all elements in the proper order.
        for (int i = 1; i < size; i++) {
            Node<E> node = new Node<>((E) s.readObject());
            
            if ((i - startOffset) % distance == 0) {
                fingerList.appendFinger(new Finger<>(node, i));
            }
            
            rightmostNode.next = node;
            node.prev = rightmostNode;
            rightmostNode = node;
        }
        
        last = rightmostNode;
    }
    
    private void removeFinger() {
        fingerList.removeFinger();
    }
    
    // Removes all the items that satisfy the given predicate.
    private boolean removeIf(Predicate<? super E> filter,
                             int fromIndex, 
                             int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        
        boolean modified = false;
        int numberOfNodesToIterate = toIndex - fromIndex;
        int i = 0;
        int nodeIndex = fromIndex;
        
        for (Node<E> node = node(fromIndex); i < numberOfNodesToIterate; ++i) {
            Node<E> nextNode = node.next;
            
            if (filter.test(node.item)) {
                modified = true;
                removeObjectImpl(node, nodeIndex);
            } else {
                nodeIndex++;
            }
            
            node = nextNode;
        }
        
        return modified;
    }
    
    // Implements the node removal. 
    private void removeObjectImpl(Node<E> node, int index) {
        int closestFingerIndex = fingerList.getFingerIndex(index);
        Finger<E> closestFinger = fingerList.get(closestFingerIndex);
        
        if (closestFinger.index == index) {
            // Make sure no finger is pointing to 'node':
            moveFingerOutOfRemovalLocation(closestFinger, closestFingerIndex);
        } else {
            for (int i = closestFingerIndex + 1;
                    i <= fingerList.size();
                    i++) {
                fingerList.get(i).index--;
            }
            
            int steps = closestFinger.index - index;
            
            if (steps > 0) {
                fingerList.get(closestFingerIndex).index--;
            }
        }
        
        unlink(node);
        decreaseSize();
        
        if (mustRemoveFinger()) {
            removeFinger();
        }
    }
    
    // Removes the finger range 'fingereList[fromIndex], ..., 
    // fingerList[toIndex - 1]'.
    private void removeRange(int fromIndex, int toIndex) {
        int removalSize = toIndex - fromIndex;
        
        if (removalSize == 0) {
            return;
        }
        
        if (removalSize == size) {
            clear();
            return;
        }
        
        Node<E> firstNodeToRemove = node(fromIndex);
        
        int nextFingerCount = getRecommendedNumberOfFingers(size - removalSize);
        int prefixSize = fromIndex;
        int suffixSize = size - toIndex;
        int prefixFingersSize = fingerList.getFingerIndexImpl(fromIndex);
        int suffixFingersSize = fingerList.size - 
                                fingerList.getFingerIndexImpl(toIndex);
        
        int prefixFreeSpotCount = prefixSize - prefixFingersSize;
        int suffixFreeSpotCount = suffixSize - suffixFingersSize;

        if (prefixFreeSpotCount == 0) {
            if (suffixFreeSpotCount == 0) {
                removeRangeNoPrefixNoSuffix(firstNodeToRemove,
                                            fromIndex, 
                                            removalSize);
            } else {
                int numberOfFingersToMove = nextFingerCount - prefixFingersSize;
                
                // Once here, prefixFreeSpotCount = 0 and 
                // suffixFreeSpotCount > 0. In other words, we are moving to 
                // suffix.
                fingerList.moveFingersToSuffix(toIndex, numberOfFingersToMove);
                fingerList.removeRange(0, suffixFingersSize, removalSize);
                removeRangeNodes(firstNodeToRemove, removalSize);
            }
        } else {
            if (suffixFreeSpotCount == 0) {
                int numberOfFingersToMove = 
                        Math.min(
                                nextFingerCount - prefixFingersSize, 
                                prefixFreeSpotCount);
                
                // Once here, suffixFreeSpotCount = 0 and 
                // prefixFreeSpotCount > 0. In other words, we are moving a to
                // prefix:
                fingerList.moveFingersToPrefix(
                        fromIndex,
                        numberOfFingersToMove);
                
                fingerList.removeRange(numberOfFingersToMove, 0, removalSize);
                removeRangeNodes(firstNodeToRemove, removalSize);
            } else {
                int prefixSuffixFreeSpotCount = prefixFreeSpotCount 
                                              + suffixFreeSpotCount;

                float prefixLoadFactor = ((float)(prefixFreeSpotCount)) /
                                         ((float)(prefixSuffixFreeSpotCount));

                int numberOfFingersOnLeft = 
                        (int)(prefixLoadFactor * nextFingerCount);

                int numberOfFingersOnRight = 
                        nextFingerCount - numberOfFingersOnLeft;

                fingerList.moveFingersToPrefix(fromIndex, 
                                               numberOfFingersOnLeft);
                
                fingerList.moveFingersToSuffix(toIndex, numberOfFingersOnRight);

                fingerList.removeRange(numberOfFingersOnLeft,
                                       numberOfFingersOnRight, 
                                       removalSize);
                
                removeRangeNodes(firstNodeToRemove, removalSize);
            }
        }
        
        modCount++;
        size -= removalSize;
    }
    
    // Removes the unnecessary fingers when the prefix and suffix are empty.
    void removeRangeNoPrefixNoSuffix(Node<E> node,
                                     int fromIndex, 
                                     int removalSize) {
        int nextListSize = IndexedLinkedList.this.size - removalSize;
        int nextFingerListSize =
                getRecommendedNumberOfFingers(nextListSize);

        int fingersToRemove = fingerList.size() - nextFingerListSize;
        int firstFingerIndex = fingerList.getFingerIndexImpl(fromIndex);
        int fingerCount = 0;
        
        Finger<E> finger1 = fingerList.get(firstFingerIndex);
        Finger<E> finger2 = fingerList.get(firstFingerIndex + 1);
        Node<E> prefixLastNode = node.prev;
        Node<E> nextNode = node;
        
        for (int i = 0; i < removalSize - 1; ++i) {
            Finger<E> f = fingerList.get(firstFingerIndex + fingerCount);
            
            if (finger1 == f) {
                if (fingersToRemove != 0) {
                    fingersToRemove--;
                    fingerCount++;
                    finger1 = finger2;
                    finger2 = fingerList.get(firstFingerIndex + fingerCount);
                }
            }
            
            nextNode = node.next;
            node.next = null;
            node.prev = null;
            node.item = null;
            node = nextNode;
        }

        Node<E> suffixFirstNode = nextNode.next;
        nextNode.next = null;
        nextNode.prev = null;
        nextNode.item = null;
        
        if (fingersToRemove != 0) {
            // Count the last finger:
            fingerCount++;
        }

        if (prefixLastNode != null) {
            prefixLastNode.next = null;
            last = prefixLastNode;
        } else {
            suffixFirstNode.prev = null;
            first = suffixFirstNode;
        }
        
        fingerList.removeRange(
                firstFingerIndex, 
                fingerList.size() - firstFingerIndex - fingerCount, 
                removalSize);
    }
    
    // Unlinks the 'numberOfNodesToRemove' consecutive nodes starting from 
    // 'node'.
    private void removeRangeNodes(Node<E> node, int numberOfNodesToRemove) {
        Node<E> prefixLastNode = node.prev;
        Node<E> nextNode = node;
        
        for (int i = 0; i < numberOfNodesToRemove - 1; ++i) {
            nextNode = node.next;
            node.next = null;
            node.prev = null;
            node.item = null;
            node = nextNode;
        }
        
        Node<E> suffixFirstNode = nextNode.next;
        nextNode.next = null;
        nextNode.prev = null;
        nextNode.item = null;
        
        if (prefixLastNode != null) {
            if (suffixFirstNode == null) {
                prefixLastNode.next = null;
                last = prefixLastNode;
            } else {
                prefixLastNode.next = suffixFirstNode;
                suffixFirstNode.prev = prefixLastNode;
            }
        } else {
            suffixFirstNode.prev = null;
            first = suffixFirstNode;
        }
    }
    
    // Replaces all the elements from range [i, end):
    private void replaceAllRange(UnaryOperator<E> operator, int i, int end) {
        Objects.requireNonNull(operator);
        int expectedModCount = modCount;
        Node<E> node = node(i);
        
        while (modCount == expectedModCount && i < end) {
            node.item = operator.apply(node.item);
            node = node.next;
            i++;
        }
        
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
    
    // Sets the input collection as a list.
    private void setAll(Collection<? extends E> c) {
        Iterator<? extends E> iterator = c.iterator();

        first = new Node<>(iterator.next());
        Node<E> prevNode = first;

        for (int i = 1, sz = c.size(); i < sz; i++) {
            Node<E> newNode = new Node<>(iterator.next());
            prevNode.next = newNode;
            newNode.prev = prevNode;
            prevNode = newNode;
        }

        last = prevNode;
        size = c.size();
        modCount++;

        addFingersAfterSetAll(c.size());
    }
    
    // If steps > 0, rewind to the left. Otherwise, rewind to the right.
    private Node<E> traverseLinkedListBackwards(Finger<E> finger, int steps) {
        Node<E> node = finger.node;
        
        if (steps > 0) {
            for (int i = 0; i < steps; i++) {
                node = node.prev;
            }
        } else {
            steps = -steps;
            
            for (int i = 0; i < steps; i++) {
                node = node.next;
            }
        }
        
        return node;
    }
    
    // Unlinks the input node from the actual doubly-linked list.
    private void unlink(Node<E> x) {
        Node<E> next = x.next;
        Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
    }
    
    /**
     * Saves the state of this {@code LinkedList} instance to a stream (that is, 
     * serializes it).
     * 
     * @param s the object output stream.
     *
     * @serialData The size of the list (the number of elements it
     *             contains) is emitted (int), followed by all of its
     *             elements (each an Object) in the proper order.
     * 
     * @throws java.io.IOException if the I/O fails.
     */
    @java.io.Serial
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next) {
            s.writeObject(x.item);
        }
    }
    
    static final class LinkedListSpliterator<E> implements Spliterator<E> {
        
        static final long MINIMUM_BATCH_SIZE = 1 << 10; // 1024 items
        
        private final IndexedLinkedList<E> list;
        private Node<E> node;
        private long lengthOfSpliterator;
        private long numberOfProcessedElements;
        private long offsetOfSpliterator;
        private final int expectedModCount;
        
        private LinkedListSpliterator(IndexedLinkedList<E> list,
                                      Node<E> node,
                                      long lengthOfSpliterator,
                                      long offsetOfSpliterator,
                                      int expectedModCount) {
            this.list = list;
            this.node = node;
            this.lengthOfSpliterator = lengthOfSpliterator;
            this.offsetOfSpliterator = offsetOfSpliterator;
            this.expectedModCount = expectedModCount;
        }

        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            
            if (numberOfProcessedElements == lengthOfSpliterator) {
                return false;
            }
            
            numberOfProcessedElements++;
            E item = node.item;
            action.accept(item);
            node = node.next;
            
            if (list.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
                
            return true;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            
            for (long i = numberOfProcessedElements; 
                 i < lengthOfSpliterator; 
                 i++) {
                E item = node.item;
                action.accept(item);
                node = node.next;
            }
            
            numberOfProcessedElements = lengthOfSpliterator;
            
            if (list.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        @Override
        public Spliterator<E> trySplit() {
            long sizeLeft = estimateSize();
            
            if (sizeLeft == 0) {
                return null;
            }
                
            long thisSpliteratorNewLength = sizeLeft / 2L;
            
            if (thisSpliteratorNewLength < MINIMUM_BATCH_SIZE) {
                return null;
            }
            
            long newSpliteratorLength = sizeLeft - thisSpliteratorNewLength;
            long newSpliteratorOffset = this.offsetOfSpliterator;
            
            this.offsetOfSpliterator += newSpliteratorLength;
            this.lengthOfSpliterator -= newSpliteratorLength;
            
            Node<E> newSpliteratorNode = this.node;
            
            this.node = list.node((int) this.offsetOfSpliterator);
            
            return new LinkedListSpliterator<>(list,
                                               newSpliteratorNode,
                                               newSpliteratorLength, // length
                                               newSpliteratorOffset, // offset
                                               expectedModCount);
        }

        @Override
        public long estimateSize() {
            return (long)(lengthOfSpliterator - numberOfProcessedElements);
        }

        @Override
        public long getExactSizeIfKnown() {
            return estimateSize();
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | 
                   Spliterator.SUBSIZED |
                   Spliterator.SIZED;
        }
        
        @Override
        public boolean hasCharacteristics(int characteristics) {
            switch (characteristics) {
                case Spliterator.ORDERED:
                case Spliterator.SIZED:
                case Spliterator.SUBSIZED:
                    return true;
                    
                default:
                    return false;
            }
        }
    }
    
    class EnhancedSubList implements List<E>, Cloneable {
        
        private final IndexedLinkedList<E> root;
        private final EnhancedSubList parent;
        private final int offset;
        private int size;
        private int modCount;
        
        public EnhancedSubList(IndexedLinkedList<E> root, 
                               int fromIndex, 
                               int toIndex) {
            
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }
        
        private EnhancedSubList(EnhancedSubList parent, 
                                int fromIndex, 
                                int toIndex) {
            
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        @Override
        public boolean add(E e) {
            checkInsertionIndex(size);
            checkForComodification();
            root.add(offset + size, e);
            updateSizeAndModCount(1);
            return true;
        }
        
        @Override
        public void add(int index, E element) {
            checkInsertionIndex(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }
        
        @Override
        public boolean addAll(Collection<? extends E> c) {
            return addAll(this.size, c);
        }
        
        @Override
        public boolean addAll(int index, Collection<? extends E> collection) {
            checkInsertionIndex(index);
            int collectionSize = collection.size();
            
            if (collectionSize == 0) {
                return false;
            }
            
            checkForComodification();
            root.addAll(offset + index, collection);
            updateSizeAndModCount(collectionSize);
            return true;
        }
        
        @Override
        public void clear() {
            checkForComodification();
            root.removeRange(offset, offset + size);
            updateSizeAndModCount(-size);
        }
        
        @Override
        public Object clone() {
            List<E> list = new IndexedLinkedList<>();
            
            for (E element : this) {
                list.add(element);
            }
            
            return list;
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                if (!contains(o)) {
                    return false;
                }
            }
            
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return root.equalsRange((List<?>) o, offset, offset + size);
        }
        
        @Override
        public void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            int expectedModCount = modCount;
            int iterated = 0;
            
            for (Node<E> node = node(offset); 
                    modCount == expectedModCount && iterated < size; 
                    node = node.next, ++iterated) {
                
                action.accept(node.item);
            }
            
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        @Override
        public E get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.get(offset + index);
        }
        
        @Override
        public int hashCode() {
            return root.hashCodeRange(offset, offset + size);
        }

        @Override
        public int indexOf(Object o) {
            int index = root.indexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }
        
        @Override
        public boolean isEmpty() {
            return size == 0;
        }
        
        @Override
        public Iterator<E> iterator() {
            return listIterator();
        }

        @Override
        public int lastIndexOf(Object o) {
            int index = root.lastIndexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }
        
        @Override
        public ListIterator<E> listIterator(int index) {
            checkForComodification();
            checkInsertionIndex(index);
            
            return new ListIterator<E>() {
                private final ListIterator<E> i = 
                        root.listIterator(offset + index);
                
                public boolean hasNext() {
                    return nextIndex() < size;
                }
                
                public E next() {
                    if (hasNext()) {
                        return i.next();
                    } 
                    
                    throw new NoSuchElementException();
                }
                
                public boolean hasPrevious() {
                    return previousIndex() >= 0;
                }
                
                public E previous() {
                    if (hasPrevious()) {
                        return i.previous();
                    }
                    
                    throw new NoSuchElementException();
                }
                
                public int nextIndex() {
                    return i.nextIndex() - offset;
                }
                
                public int previousIndex() {
                    return i.previousIndex() - offset;
                }
                
                public void remove() {
                    i.remove();
                    updateSizeAndModCount(-1);
                }
                
                public void set(E e) {
                    i.set(e);
                }
                
                public void add(E e) {
                    i.add(e);
                    updateSizeAndModCount(1);
                }
            };
        }
        
        @Override
        public boolean remove(Object o) {
            ListIterator<E> iterator = listIterator();
            
            if (o == null) {
                while (iterator.hasNext()) {
                    if (iterator.next() == null) {
                        iterator.remove();
                        return true;
                    }
                }
            } else {
                while (iterator.hasNext()) {
                    if (o.equals(iterator.next())) {
                        iterator.remove();
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        @Override
        public E remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return batchRemove(c, true);
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.removeIf(filter, offset, offset + size);
            
            if (modified) {
                updateSizeAndModCount(root.size - oldSize);
            }
            
            return modified;
        }
        
        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            root.replaceAllRange(operator, offset, offset + size);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return batchRemove(c, false);
        }
        
        @Override
        public E set(int index, E element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.set(offset + index, element);
        }
        
        @Override
        public int size() {
            checkForComodification();
            return size;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public void sort(Comparator<? super E> c) {
            if (size == 0) {
                return;
            }
            
            int expectedModCount = modCount;
            Object[] array = toArray();
            Node<E> node = node(offset);

            Arrays.sort((E[]) array, c);

            // Rearrange the items over the linked list nodes:
            for (int i = 0; i < array.length; ++i, node = node.next) {
                E item = (E) array[i];
                node.item = item;
            }
            
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            
            distributeFingers(offset, offset + size);
            modCount++;
        }
        
        @Override
        public Spliterator<E> spliterator() {
            return new LinkedListSpliterator(root,
                                             node(offset),
                                             size,
                                             offset,
                                             modCount);
        }
        
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new EnhancedSubList(this, fromIndex, toIndex);
        }

        @Override
        public Object[] toArray() {
            checkForComodification();
            int expectedModCount = root.modCount;
            Object[] array = new Object[this.size];
            Node<E> node = node(offset);
            
            for (int i = 0; 
                    i < array.length && expectedModCount == root.modCount;
                    i++, node = node.next) {
                array[i] = node.item;
            }
            
            if (expectedModCount != root.modCount) {
                throw new ConcurrentModificationException();
            }
            
            return array;
        }

        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            return toArray(generator.apply(size));
        }

        @Override
        public <T> T[] toArray(T[] a) {
            checkForComodification();
            
            int expectedModCount = root.modCount;
            
            if (a.length < size) {
                a = (T[]) Array.newInstance(
                        a.getClass().getComponentType(),
                        size);
            }
            
            int index = 0;
            
            for (Node<E> node = node(offset);
                    expectedModCount == root.modCount && index < size; 
                    ++index, node = node.next) {
                
                a[index] = (T) node.item;
            }
            
            if (a.length > size) {
                a[size] = null;
            }
            
            return a;
        }
        
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");

            boolean firstIteration = true;

            for (E element : this) {
                if (firstIteration) {
                    firstIteration = false;
                } else {
                    stringBuilder.append(", ");
                }

                stringBuilder.append(element);
            }

            return stringBuilder.append("]").toString();
        }
    
        private boolean batchRemove(Collection<?> c, boolean complement) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.batchRemove(c,
                                                complement, 
                                                offset, 
                                                offset + size);
            
            if (modified) {
                updateSizeAndModCount(root.size - oldSize);
            }
            
            return modified;
        }
        
        private void checkForComodification() {
            if (root.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }

        private void checkInsertionIndex(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("Negative index: " + index);
            }
            
            if (index > this.size) {
                throw new IndexOutOfBoundsException(
                        "index(" + index + ") > size(" + size + ")");
            }
        }
        
        private void updateSizeAndModCount(int sizeDelta) {
            EnhancedSubList subList = this;
            
            do {
                subList.size += sizeDelta;
                subList.modCount = root.modCount;
                subList = subList.parent;
            } while (subList != null);
        }
    }
}
