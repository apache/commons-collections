/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/CommonsLinkedList.java,v 1.3 2003/01/10 20:21:22 rwaldhoff Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An implementation of {@link List} which duplicates the behaviour
 * of {@link LinkedList}, but which provides a more open interface for
 * subclasses to extend.
 * 
 * @since Commons Collections 2.2
 * @author <a href="mailto:rich@rd.gen.nz">Rich Dougherty</a>
 * @version $Revision: 1.3 $ $Date: 2003/01/10 20:21:22 $
 */
class CommonsLinkedList extends LinkedList
        implements List, Serializable {

    /*
     * Implementation notes:
     * - a standard circular doubly-linked list
     * - a marker node is stored to mark the start and the end of the list
     * - node creation and removal always occurs through createNode() and
     *   removeNode().
     * - a modification count is kept, with the same semantics as
     * {@link java.util.LinkedList}.
     * - respects {@link AbstractList#modCount}
     */

    /**
     * A node within the {@link CommonsLinkedList}.
     * 
     * @author <a href="mailto:rich@nil.co.nz">Rich Dougherty</a>
     */
    protected static class Node {

        /**
         * A pointer to the node before this node.
         */
        public Node previous;

        /**
         * A pointer to the node after this node.
         */
        public Node next;

        /**
         * The object contained within this node.
         */
        public Object element;

        public Node() {
        }

        public Node(Node previous, Node next, Object element) {
            this.previous = previous;
            this.next = next;
            this.element = element;
        }

        /**
         * Checks if a value is equal to this node's element.
         *
         * @return <code>true</code> iff the elements are both <code>null</code>
         *         or {@link Object#equals equal}.
         */
        public boolean elementEquals(Object otherElement) {
            if (element == null) {
                return otherElement == null;
            } else {
                return element.equals(otherElement);
            }
        }
    }

    /**
     * A {@link java.util.ListIterator} {@link CommonsLinkedList}. 
     * 
     * @author <a href="mailto:rich@nil.co.nz">Rich Dougherty</a>
     */
    protected class ListIteratorImpl implements ListIterator {

        /**
         * The node that will be returned by {@link #next()}. If this is equal
         * to {@link #marker} then there are no more elements to return.
         */
        protected Node nextNode;

        /**
         * The index of {@link #nextNode}.
         */
        protected int nextIndex;

        /**
         * The last node that was returned by {@link #next()} or {@link
         * #previous()}. Set to <code>null</code> if {@link #next()} or {@link
         * #previous()} haven't been called, or if the node has been removed
         * with {@link #remove()} or a new node added with {@link #add(Object)}.
         * Should be accesed through {@link #getLastNodeReturned()} to enforce
         * this behaviour.
         */
        protected Node lastNodeReturned;

        /**
         * The modification count that the list is expected to have. If the list
         * doesn't have this count, then a
         * {@link java.util.ConcurrentModificationException} may be thrown by
         * the operations.
         */
        protected int expectedModCount;

        /**
         * Create a ListIterator for a list, starting at the first element in
         * the list.
         */
        public ListIteratorImpl() throws IndexOutOfBoundsException {
            this(0);
        }

        /**
         * Create a ListIterator for a list.
         * 
         * @param startIndex The index to start at.
         */
        public ListIteratorImpl(int startIndex)
                throws IndexOutOfBoundsException {
            expectedModCount = modCount;
            nextNode = getNode(startIndex, true);
            nextIndex = startIndex;
        }

        /**
         * Checks the modification count of the list is the value that this
         * object expects.
         * 
         * @throws ConcurrentModificationException If the list's modification
         * count isn't the value that was expected.
         */
        protected void checkModCount()
            throws ConcurrentModificationException {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        /**
         * Gets the last node returned.
         * 
         * @throws IllegalStateException If {@link #next()} or {@link
         * #previous()} haven't been called, or if the node has been removed
         * with {@link #remove()} or a new node added with {@link #add(Object)}.
         */
        protected Node getLastNodeReturned() throws IllegalStateException {
            if (lastNodeReturned == null) {
                throw new IllegalStateException();
            }
            return lastNodeReturned;
        }

        public boolean hasNext() {
            return nextNode != marker;
        }

        public Object next() {
            checkModCount();
            if (!hasNext()) {
                throw new NoSuchElementException("No element at index " +
                        nextIndex + ".");
            }
            Object element = nextNode.element;
            lastNodeReturned = nextNode;
            nextNode = nextNode.next;
            nextIndex++;
            return element;
        }

        public boolean hasPrevious() {
            return nextNode.previous != marker;
        }

        public Object previous() {
            checkModCount();
            if (!hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            nextNode = nextNode.previous;
            Object element = nextNode.element;
            lastNodeReturned = nextNode;
            nextIndex--;
            return element;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkModCount();
            removeNode(getLastNodeReturned());
            lastNodeReturned = null;
            nextIndex--;
            expectedModCount++;
        }

        public void set(Object o) {
            checkModCount();
            getLastNodeReturned().element = o;
        }

        public void add(Object o) {
            checkModCount();
            addNodeBefore(nextNode, o);
            lastNodeReturned = null;
            nextIndex++;
            expectedModCount++;
        }

    }

    private static final long serialVersionUID = 1L;

    /**
     * A {@link Node} which indicates the start and end of the list and does not
     * hold a value. The value of <code>next</code> is the first item in the
     * list. The value of of <code>previous</code> is the last item in the list.
     */
    protected transient Node marker;

    /**
     * The size of the list.
     */
    protected transient int size;

    public CommonsLinkedList() {
        initializeEmptyList();
    }

    public CommonsLinkedList(Collection c) {
        initializeEmptyList();
        addAll(c);
    }

    /**
     * The equivalent of a default constructor, broken out so it can be called
     * by any constructor and by <code>readObject</code>.
     * Subclasses which override this method should make sure they call it so
     * the list is initialised properly.
     */
    protected void initializeEmptyList() {
        marker = createNode();
        marker.next = marker;
        marker.previous = marker;
    }

    // Operations on nodes

    protected Node createNode() {
        return new Node();
    }

    protected Node createNode(Node next, Node previous, Object element) {
        return new Node(next, previous, element);
    }

    private void addNodeBefore(Node node, Object o) {
        Node newNode = createNode(node.previous, node, o);
        node.previous.next = newNode;
        node.previous = newNode;
        size++;
        modCount++;
    }

    protected void addNodeAfter(Node node, Object o) {
        Node newNode = createNode(node, node.next, o);
        node.next.previous = newNode;
        node.next = newNode;
        size++;
        modCount++;
    }

    protected void removeNode(Node node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        size--;
        modCount++;
    }

    protected void removeAllNodes() {
        marker.next = marker;
        marker.previous = marker;
        size = 0;
        modCount++;
    }

    /**
     * Gets the node at a particular index.
     * 
     * @param index The index, starting from 0.
     * @param endMarkerAllowd Whether or not the end marker can be returned if
     * startIndex is set to the list's size.
     * @throws IndexOutOfBoundsException If the index is less than 0; equal to
     * the size of the list and endMakerAllowed is false; or greater than the
     * size of the list.
     */
    protected Node getNode(int index, boolean endMarkerAllowed) throws IndexOutOfBoundsException {
        // Check the index is within the bounds
        if (index < 0) {
            throw new IndexOutOfBoundsException("Couldn't get the node: " +
                    "index (" + index + ") less than zero.");
        }
        if (!endMarkerAllowed && index == size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: " +
                    "index (" + index + ") is the size of the list.");
        }
        if (index > size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: " +
                    "index (" + index + ") greater than the size of the " +
                    "list (" + size + ").");
        }
        // Search the list and get the node
        Node node;
        if (index < (size / 2)) {
            // Search forwards
            node = marker.next;
            for (int currentIndex = 0; currentIndex < index; currentIndex++) {
                node = node.next;
            }
        } else {
            // Search backwards
            node = marker;
            for (int currentIndex = size; currentIndex > index; currentIndex--) {
                node = node.previous;
            }
        }
        return node;
    }

    // List implementation required by AbstractSequentialList

    public ListIterator listIterator() {
        return new ListIteratorImpl();
    }

    public ListIterator listIterator(int startIndex) {
        return new ListIteratorImpl(startIndex);
    }

    public int size() {
        return size;
    }

    // List implementation not required by AbstractSequentialList, but provided
    // for efficiency or to override LinkedList's implementation.
    
    public void clear() {
        removeAllNodes();
    }
    
    public boolean add(Object o) {
        addLast(o);
        return true;
    }
    
    public void add(int index, Object element) {
        Node node = getNode(index, true);
        addNodeBefore(node, element);
    }
    
    public boolean addAll(Collection c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection c) {
        Node node = getNode(index, true);
        for (Iterator itr = c.iterator(); itr.hasNext();) {
            Object element = itr.next();
            addNodeBefore(node, element);
        }
        return true;
    }

    public Object get(int index) {
        Node node = getNode(index, false);
        return node.element;
    }

    public Object set(int index, Object element) {
        Node node = getNode(index, false);
        Object oldElement = node.element;
        node.element = element;
        return oldElement;
    }

    public Object remove(int index) {
        Node node = getNode(index, false);
        Object oldElement = node.element;
        removeNode(node);
        return oldElement;
    }

    public boolean remove(Object element) {
        for (Node node = marker.next; node != marker; node = node.next) {
            if (node.elementEquals(element)) {
                removeNode(node);
                return true;
            }
        }
        return false;
    }

    public int indexOf(Object element) {
        int i = 0;
        for (Node node = marker.next; node != marker; node = node.next) {
            if (node.elementEquals(element)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public int lastIndexOf(Object element) {
        int i = size - 1;
        for (Node node = marker.previous; node != marker; node = node.previous) {
            if (node.elementEquals(element)) {
                return i;
            }
            i--;
        }
        return -1;
    }

    public boolean contains(Object element) {
        return indexOf(element) != -1;
    }

    public Object[] toArray() {
        return toArray(new Object[size]);
    }

    public Object[] toArray(Object[] array) {
        // Extend the array if needed
        if (array.length < size) {
            Class componentType = array.getClass().getComponentType();
            array = (Object[]) Array.newInstance(componentType, size);
        }
        // Copy the values into the array
        Node node = marker.next;
        for (int i = 0; i < size; i++) {
            array[i] = node.element;
            node = node.next;
        }
        // Set the value after the last element to null
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }
    // Extra methods compatible with java.util.LinkedList.

    public Object getFirst() {
        Node node = marker.next;
        if (node == marker) {
            throw new NoSuchElementException();
        }
        return node.element;
    }

    public Object getLast() {
        Node node = marker.previous;
        if (node == marker) {
            throw new NoSuchElementException();
        }
        return node.element;
    }

    public void addFirst(Object o) {
        addNodeAfter(marker, o);
    }

    public void addLast(Object o) {
        addNodeBefore(marker, o);
    }

    public Object removeFirst() {
        Node node = marker.next;
        if (node == marker) {
            throw new NoSuchElementException();
        }
        Object oldElement = node.element;
        removeNode(node);
        return oldElement;
    }

    public Object removeLast() {
        Node node = marker.previous;
        if (node == marker) {
            throw new NoSuchElementException();
        }
        Object oldElement = node.element;
        removeNode(node);
        return oldElement;
    }

    // Serialization methods

    private void writeObject(ObjectOutputStream outputStream)
        throws IOException, ClassNotFoundException {
        outputStream.defaultWriteObject();
        // Write the size so we know how many nodes to read back
        outputStream.writeInt(size());
        for (Iterator itr = iterator(); itr.hasNext();) {
            outputStream.writeObject(itr.next());
        }
    }

    private void readObject(ObjectInputStream inputStream)
        throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        initializeEmptyList();
        int size = inputStream.readInt();
        for (int i = 0; i < size; i++) {
            add(inputStream.readObject());
        }
    }

}
