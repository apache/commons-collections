/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/NodeCachingLinkedList.java,v 1.1 2002/11/18 23:58:17 scolebourne Exp $
 * $Revision: 1.1 $
 * $Date: 2002/11/18 23:58:17 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
/**
 * <code>NodeCachingLinkedList</code> is a linked list implementation that 
 * provides better performance than java.util.LinkedList.
 * <p>
 * This class differs from java.util.LinkedList in that internal Node 
 * objects used to hold the elements are not necessarily thrown away when an
 * entry is removed from the list. Instead, they are cached, which allows this
 * implementation to give better performance than java.util.LinkedList with a
 * small space penalty.
 * <p>
 * <b>Note that this implementation is not synchronized.</b> If multiple
 * threads access a list concurrently, and at least one of the threads
 * modifies the list structurally, it <i>must</i> be synchronized
 * externally.
 * <p>
 * The iterators returned by the this class's <code>iterator</code> and
 * <code>listIterator</code> methods are <i>fail-fast</i>: if the list is
 * structurally modified at any time after the iterator is created, in any way
 * except through the Iterator's own <code>remove</code> or <code>add</code> methods,
 * the iterator will throw a <code>ConcurrentModificationException</code>.  Thus,
 * in the face of concurrent modification, the iterator fails quickly and
 * cleanly, rather than risking arbitrary, non-deterministic behavior at an
 * undetermined time in the future.
 * <p>
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <code>ConcurrentModificationException</code> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * @author Jeff Varszegi
 */
public final class NodeCachingLinkedList 
        extends LinkedList 
        implements List, Cloneable, Serializable {
            
    private static final int MINIMUM_MAXIMUM_CACHE_SIZE = 100;
    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 1000000;

    private Node cacheHeader = new Node(null, null, null);
    private int cacheCount = 0;

    private int maximumCacheSize = DEFAULT_MAXIMUM_CACHE_SIZE;

    private Node header = new Node(null, null, null);
    private int size = 0;
    
    /**
     * Constructs an empty list.
     */
    public NodeCachingLinkedList() {
        header.next = header.previous = header;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param coll the collection whose elements are to be placed into this list.
     * @throws NullPointerException if the specified collection is null.
     */
    public NodeCachingLinkedList(Collection coll) {
        this();
        addAll(coll);
    }
    
    /**
     * Sets the maximum number of elements that may be held in the internal
     * reusable node cache.
     * 
     * @return the maximum cache size
     */
    public int getMaximumCacheSize() {
        return maximumCacheSize;
    }

    /**
     * Sets the maximum number of elements that may be held in the internal
     * reusable node cache.
     * 
     * @param maximumCacheSize the maximum cache size to set
     */
    public void setMaximumCacheSize(int maximumCacheSize) {
        this.maximumCacheSize = maximumCacheSize;
    }
    
    /**
     * Returns the first element in this list.
     * 
     * @return the first object in the list
     * @throws NoSuchElementException if this list is empty
     */
    public Object getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        else {
            return header.next.element;
        }
    }

    /**
     * Returns the last element in this list.
     * 
     * @return the last object in the list
     * @throws NoSuchElementException if this list is empty
     */
    public Object getLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return header.previous.element;
    }

    /**
     * Removes and returns the first element from this list.
     *
     * @return the first element from this list, now removed
     * @throws NoSuchElementException if this list is empty
     */
    public Object removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException(); 
        }
        Node firstNode = header.next;
        Object first = firstNode.element;
        
        firstNode.next.previous = header;
        header.next = firstNode.next;
        size--;
        modCount++;

        if (cacheCount < maximumCacheSize) {
            
            firstNode.element = null;
            firstNode.next = cacheHeader.next;
            cacheHeader.next = firstNode;
            cacheCount++;
        }
                
        return first;
    }

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list, now removed
     * @throws NoSuchElementException if this list is empty
     */
    public Object removeLast() {
        if (size == 0) {
            throw new NoSuchElementException(); 

        }
        Node lastNode = header.previous;
        Object last = lastNode.element;

        lastNode.previous.next = header;
        header.previous = lastNode.previous;
        size--;
        modCount++;

        if (cacheCount < maximumCacheSize) {
            lastNode.element = null;
            lastNode.previous = null;
            lastNode.next = cacheHeader.next;
            cacheHeader.next = lastNode;
            cacheCount++;
        }
        
        return last;
    }
    
    /**
     * Inserts the given element at the beginning of this list.
     * 
     * @param obj the element to be inserted at the beginning of this list
     */
    public void addFirst(Object obj) {
        Node newNode;
          
        if (cacheCount > 0) {
            newNode = cacheHeader.next;
            cacheHeader.next = newNode.next;
            newNode.element = obj;
            newNode.next = header.next;
            newNode.previous = header;
            cacheCount--;
        }
        else {
            newNode = new Node(obj, header.next, header);
        }
        
        newNode.previous.next = newNode;
        newNode.next.previous = newNode;
        size++;
        modCount++;
    }
    
    /**
     * Appends the given element to the end of this list.  (Identical in
     * function to the <code>add</code> method; included only for consistency.)
     * 
     * @param obj the element to be inserted at the end of this list
     */
    public void addLast(Object obj) {
        Node newNode;

        if (cacheCount > 0) {
            newNode = cacheHeader.next;
            cacheHeader.next = newNode.next;
            newNode.element = obj;
            newNode.next = header.next;
            newNode.previous = header;
            cacheCount--;
        }
        else {
            newNode = new Node(obj, header, header.previous);
        }


  
        newNode.previous.next = newNode;
        newNode.next.previous = newNode;
        size++;
        modCount++;
    }

    /**
     * Returns <code>true</code> if this list contains the specified element.
     * More formally, returns <code>true</code> if and only if this list contains
     * at least one element <code>e</code> such that <code>(o==null ? e==null
     * : o.equals(e))</code>.
     *
     * @param obj element whose presence in this list is to be tested
     * @return <code>true</code> if this list contains the specified element
     */
    public boolean contains(Object obj) {
        return indexOf(obj) != -1;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param obj element to be appended to this list.
     * @return <code>true</code> (as per the general contract of
     * <code>Collection.add</code>)
     */
    public boolean add(Object obj) {
        addLast(obj);
        return true;
    }

    /**
     * Removes the first occurrence of the specified element in this list.  If
     * the list does not contain the element, it is unchanged.  More formally,
     * removes the element with the lowest index <code>i</code> such that
     * <code>(o==null ? get(i)==null : o.equals(get(i)))</code> (if such an
     * element exists).
     *
     * @param obj element to be removed from this list, if present
     * @return <code>true</code> if the list contained the specified element
     */
    public boolean remove(Object obj) {
        if (obj == null) {
            for (Node e = header.next; e != header; e = e.next) {
                if (e.element == null) {
                    remove(e);
                    return true;
                }
            }
        }
        else {
            for (Node e = header.next; e != header; e = e.next) {
                if (obj.equals(e.element)) {
                    remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator.  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in
     * progress.  (This implies that the behavior of this call is undefined if
     * the specified Collection is this list, and this list is nonempty.)
     *
     * @param coll the elements to be inserted into this list
     * @return <code>true</code> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(Collection coll) {
        return addAll(size, coll);
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert first element
     *          from the specified collection
     * @param coll elements to be inserted into this list
     * @return <code>true</code> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException if the specified index is out of
     *            range (<code>index &lt; 0 || index &gt; size()</code>)
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(int index, Collection coll) {
        int numNew = coll.size();
        if (numNew == 0)
            return false;
        modCount++;

        Node successor = (index == size ? header : entry(index));
        Node predecessor = successor.previous;
        Iterator it = coll.iterator();
        for (int i = 0; i < numNew; i++) {
            Node e; 
            if (cacheCount > 0) {
                e = cacheHeader.next;
                cacheHeader.next = e.next;
                e.element = it.next();
                e.next = successor;
                e.previous = predecessor;
            }
            else {
                e = new Node(it.next(), successor, predecessor);
            }
            
            predecessor.next = e;
            predecessor = e;
        }
        successor.previous = predecessor;

        size += numNew;
        return true;
    }

    /**
     * Removes all of the elements from this list.
     */
    public void clear() {
        modCount++;
        header.next = header.previous = header;
        size = 0;
    }

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of element to return
     * @return the element at the specified position in this list
     * 
     * @throws IndexOutOfBoundsException if the specified index is is out of
     * range (<code>index &lt; 0 || index &gt;= size()</code>)
     */
    public Object get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node e = header;
        if (index < (size >> 1)) {
            for (int i = 0; i <= index; i++) {
                e = e.next;
            }
        }
        else {
            for (int i = size; i > index; i--) {
                e = e.previous;
            }
        }
        return e.element;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index index of element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is out of
     *        range (<code>index &lt; 0 || index &gt;= size()</code>)
     */
    public Object set(int index, Object element) {
        Node e = entry(index);
        Object oldVal = e.element;
        e.element = element;
        return oldVal;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * 
     * @throws IndexOutOfBoundsException if the specified index is out of
     *        range (<code>index &lt; 0 || index &gt; size()</code>)
     */
    public void add(int index, Object element) {
        Node e = (index == size ? header : entry(index));
        Node newNode; 
        if (cacheCount > 0) {
            newNode = cacheHeader.next;
            cacheHeader.next = newNode.next;
            newNode.element = element;
            newNode.next = e;
            newNode.previous = e.previous;
        }
        else {
            newNode = new Node(element, e, e.previous);
        }
        
        newNode.previous.next = newNode;
        newNode.next.previous = newNode;
        size++;
        modCount++;
    }

    /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to removed
     * @return the element previously at the specified position
     * 
     * @throws IndexOutOfBoundsException if the specified index is out of
     *        range (<code>index &lt; 0 || index &gt;= size()</code>)
     */
    public Object remove(int index) {
        Node e = entry(index);
        Object data = e.element;
        remove(e);
        return data;
    }

    /**
     * Return the indexed entry.
     */
    private Node entry(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node e = header;
        if (index < (size >> 1)) {
            for (int i = 0; i <= index; i++) {
                e = e.next;
            }
        }
        else {
            for (int i = size; i > index; i--) {
                e = e.previous;
            }
        }
        return e;
    }

    // Search Operations

    /**
     * Returns the index in this list of the first occurrence of the
     * specified element, or -1 if the List does not contain this
     * element.  More formally, returns the lowest index i such that
     * <code>(o==null ? get(i)==null : o.equals(get(i)))</code>, or -1 if
     * there is no such index.
     *
     * @param obj element to search for
     * @return the index in this list of the first occurrence of the
     *         specified element, or -1 if the list does not contain this
     *         element
     */
    public int indexOf(Object obj) {
        int index = 0;
        if (obj == null) {
            for (Node e = header.next; e != header; e = e.next) {
                if (e.element == null)
                    return index;
                index++;
            }
        }
        else {
            for (Node e = header.next; e != header; e = e.next) {
                if (obj.equals(e.element))
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * Returns the index in this list of the last occurrence of the
     * specified element, or -1 if the list does not contain this
     * element.  More formally, returns the highest index i such that
     * <code>(o==null ? get(i)==null : o.equals(get(i)))</code>, or -1 if
     * there is no such index.
     *
     * @param obj element to search for
     * @return the index in this list of the last occurrence of the
     *         specified element, or -1 if the list does not contain this
     *         element
     */
    public int lastIndexOf(Object obj) {
        int index = size;
        if (obj == null) {
            for (Node e = header.previous; e != header; e = e.previous) {
                index--;
                if (e.element == null)
                    return index;
            }
        }
        else {
            for (Node e = header.previous; e != header; e = e.previous) {
                index--;
                if (obj.equals(e.element))
                    return index;
            }
        }
        return -1;
    }

    /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * Obeys the general contract of <code>List.listIterator(int)</code>.<p>
     * <p>
     * The list-iterator is <i>fail-fast</i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own <code>remove</code> or <code>add</code>
     * methods, the list-iterator will throw a
     * <code>ConcurrentModificationException</code>.  Thus, in the face of
     * concurrent modification, the iterator fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future.
     *
     * @param index index of first element to be returned from the
     *          list-iterator (by a call to <code>next</code>)
     * @return a ListIterator of the elements in this list (in proper
     *         sequence), starting at the specified position in the list
     * @throws    IndexOutOfBoundsException if index is out of range
     *        (<code>index &lt; 0 || index &gt; size()</code>)
     * @see List#listIterator(int)
     */
    public ListIterator listIterator(int index) {
        return new ListItr(index);
    }

    private final class ListItr implements ListIterator {
        private Node lastReturned = header;
        private Node next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            if (index < (size >> 1)) {
                next = header.next;
                for (nextIndex = 0; nextIndex < index; nextIndex++)
                    next = next.next;
            }
            else {
                next = header;
                for (nextIndex = size; nextIndex > index; nextIndex--)
                    next = next.previous;
            }
        }

        public boolean hasNext() {
            return nextIndex != size;
        }

        public Object next() {
            checkForComodification();
            if (nextIndex == size)
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.element;
        }

        public boolean hasPrevious() {
            return nextIndex != 0;
        }

        public Object previous() {
            if (nextIndex == 0)
                throw new NoSuchElementException();

            lastReturned = next = next.previous;
            nextIndex--;
            checkForComodification();
            return lastReturned.element;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            try {
                NodeCachingLinkedList.this.remove(lastReturned);
            }
            catch (NoSuchElementException e) {
                throw new IllegalStateException();
            }
            if (next == lastReturned)
                next = lastReturned.next;
            else
                nextIndex--;
            lastReturned = header;
            expectedModCount++;
        }

        public void set(Object o) {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.element = o;
        }

        public void add(Object o) {
            checkForComodification();
            lastReturned = header;

            Node newNode; 
            if (cacheCount > 0) {
                newNode = cacheHeader.next;
                cacheHeader.next = newNode.next;
                newNode.element = o;
                newNode.next = next;
                newNode.previous = next.previous;
            }
            else {
                newNode = new Node(o, next, next.previous);
            }
            
            newNode.previous.next = newNode;
            newNode.next.previous = newNode;
            size++;
            modCount++;

            nextIndex++;
            expectedModCount++;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private final static class Node implements Serializable {
        Object element;
        Node next;
        Node previous;

        Node(Object element, Node next, Node previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }


    private Node addsBefore(Object o, Node e) {
        Node newNode; 
        if (cacheCount > 0) {
            newNode = cacheHeader.next;
            cacheHeader.next = newNode.next;
            newNode.element = o;
            newNode.next = e;
            newNode.previous = e.previous;
        }
        else {
            newNode = new Node(o, e, e.previous);
        }
        
        newNode.previous.next = newNode;
        newNode.next.previous = newNode;
        size++;
        modCount++;
        return newNode;
    }


    private void remove(Node e) {
        if (e == header) {
            throw new NoSuchElementException();
        }
        e.previous.next = e.next;
        e.next.previous = e.previous;
        size--;
        modCount++;
        
        if (cacheCount < maximumCacheSize) {
            e.element = null;
            e.previous = null;
            e.next = cacheHeader.next;
            cacheHeader.next = e;
            cacheCount++;
        }
    }

    /**
     * Returns a shallow copy of this <code>NodeCachingLinkedList</code>. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this <code>NodeCachingLinkedList</code> instance
     */
    public Object clone() {
        NodeCachingLinkedList clone = new NodeCachingLinkedList();
        clone.setMaximumCacheSize(maximumCacheSize);
        
        // Initialize clone with our elements
        for (Node e = header.next; e != header; e = e.next) {
            clone.add(e.element);
        }

        return clone;
    }

    /**
     * Returns an array containing all of the elements in this list
     * in the correct order.
     *
     * @return an array containing all of the elements in this list
     *         in the correct order
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node e = header.next; e != header; e = e.next)
            result[i++] = e.element;
        return result;
    }

    /**
     * Returns an array containing all of the elements in this list in
     * the correct order; the runtime type of the returned array is that of
     * the specified array.  If the list fits in the specified array, it
     * is returned therein.  Otherwise, a new array is allocated with the
     * runtime type of the specified array and the size of this list.<p>
     * <p>
     * If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list),
     * the element in the array immediately following the end of the
     * collection is set to null.  This is useful in determining the length
     * of the list <i>only</i> if the caller knows that the list
     * does not contain any null elements.
     *
     * @param a the array into which the elements of the list are to
     *      be stored, if it is big enough; otherwise, a new array of the
     *      same runtime type is allocated for this purpose
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of a is not a
     *         supertype of the runtime type of every element in this list
     * @throws NullPointerException if the specified array is null
     */
    public Object[] toArray(Object a[]) {
        if (a.length < size)
            a = (Object[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        for (Node e = header.next; e != header; e = e.next)
            a[i++] = e.element;

        if (a.length > size)
            a[size] = null;

        return a;
    }
    
}
