/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/list/AbstractLinkedList.java,v 1.1 2003/12/11 00:18:06 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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
package org.apache.commons.collections.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.OrderedIterator;

/**
 * An abstract implementation of a linked list which provides numerous points for
 * subclasses to override.
 * <p>
 * Overridable methods are provided to change the storage node, and to change how
 * entries are added to and removed from the map. Hopefully, all you need for
 * unusual subclasses is here.
 * <p>
 * This class currently extends AbstractList, but do not rely on that. It may change.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/12/11 00:18:06 $
 *
 * @author Rich Dougherty
 * @author Phil Steitz
 * @author Stephen Colebourne
 */
public abstract class AbstractLinkedList extends AbstractList implements List {

    /*
     * Implementation notes:
     * - a standard circular doubly-linked list
     * - a marker node is stored to mark the start and the end of the list
     * - node creation and removal always occurs through createNode() and
     *   removeNode().
     * - a modification count is kept, with the same semantics as
     * {@link java.util.LinkedList}.
     * - respects {@link AbstractList#modCount}
     * - only extends AbstractList for subList() - TODO
     */

    /**
     * A {@link Node} which indicates the start and end of the list and does not
     * hold a value. The value of <code>next</code> is the first item in the
     * list. The value of of <code>previous</code> is the last item in the list.
     */
    protected transient Node header;
    /** The size of the list */
    protected transient int size;
//    /** Modification count for iterators */
//    protected transient int modCount;

    /**
     * Constructor that does nothing intended for deserialization.
     * <p>
     * If this constructor is used by a serializable subclass then the init()
     * method must be called.
     */
    protected AbstractLinkedList() {
        super();
    }

    /**
     * Constructs a list copying data from the specified collection.
     * 
     * @param coll  the collection to copy
     */
    protected AbstractLinkedList(Collection coll) {
        super();
        init();
        addAll(coll);
    }

    /**
     * The equivalent of a default constructor, broken out so it can be called
     * by any constructor and by <code>readObject</code>.
     * Subclasses which override this method should make sure they call super,
     * so the list is initialised properly.
     */
    protected void init() {
        header = createHeaderNode();
    }

    //-----------------------------------------------------------------------
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return (size() == 0);
    }

    public Object get(int index) {
        Node node = getNode(index, false);
        return node.value;
    }

    //-----------------------------------------------------------------------
    public Iterator iterator() {
        return listIterator();
    }

    public ListIterator listIterator() {
        return new LinkedListIterator();
    }

    public ListIterator listIterator(int startIndex) {
        return new LinkedListIterator(startIndex);
    }

    //-----------------------------------------------------------------------
    public int indexOf(Object value) {
        int i = 0;
        for (Node node = header.next; node != header; node = node.next) {
            if (isEqualValue(node.value, value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public int lastIndexOf(Object value) {
        int i = size - 1;
        for (Node node = header.previous; node != header; node = node.previous) {
            if (isEqualValue(node.value, value)) {
                return i;
            }
            i--;
        }
        return -1;
    }

    public boolean contains(Object value) {
        return indexOf(value) != -1;
    }

    public boolean containsAll(Collection coll) {
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            if (contains(it.next()) == false) {
                return false;
            }
        }
        return true;
    }
    
    //-----------------------------------------------------------------------
    public boolean add(Object value) {
        addLast(value);
        return true;
    }
    
    public void add(int index, Object value) {
        Node node = getNode(index, true);
        addNodeBefore(node, value);
    }
    
    public boolean addAll(Collection coll) {
        return addAll(size, coll);
    }

    public boolean addAll(int index, Collection coll) {
        Node node = getNode(index, true);
        for (Iterator itr = coll.iterator(); itr.hasNext();) {
            Object value = itr.next();
            addNodeBefore(node, value);
        }
        return true;
    }

    //-----------------------------------------------------------------------
    public Object remove(int index) {
        Node node = getNode(index, false);
        Object oldValue = node.value;
        removeNode(node);
        return oldValue;
    }

    public boolean remove(Object value) {
        for (Node node = header.next; node != header; node = node.next) {
            if (isEqualValue(node.value, value)) {
                removeNode(node);
                return true;
            }
        }
        return false;
    }

    public boolean removeAll(Collection coll) {
        boolean modified = false;
        Iterator it = iterator();
        while (it.hasNext()) {
            if (coll.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    //-----------------------------------------------------------------------
    public boolean retainAll(Collection coll) {
        boolean modified = false;
        Iterator it = iterator();
        while (it.hasNext()) {
            if (coll.contains(it.next()) == false) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    public Object set(int index, Object value) {
        Node node = getNode(index, false);
        Object oldValue = node.value;
        node.value = value;
        return oldValue;
    }

    public void clear() {
        removeAllNodes();
    }
    
    //-----------------------------------------------------------------------
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
        Node node = header.next;
        for (int i = 0; i < size; i++) {
            array[i] = node.value;
            node = node.next;
        }
        // Set the value after the last value to null
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    //-----------------------------------------------------------------------
    public Object getFirst() {
        Node node = header.next;
        if (node == header) {
            throw new NoSuchElementException();
        }
        return node.value;
    }

    public Object getLast() {
        Node node = header.previous;
        if (node == header) {
            throw new NoSuchElementException();
        }
        return node.value;
    }

    public void addFirst(Object o) {
        addNodeAfter(header, o);
    }

    public void addLast(Object o) {
        addNodeBefore(header, o);
    }

    public Object removeFirst() {
        Node node = header.next;
        if (node == header) {
            throw new NoSuchElementException();
        }
        Object oldValue = node.value;
        removeNode(node);
        return oldValue;
    }

    public Object removeLast() {
        Node node = header.previous;
        if (node == header) {
            throw new NoSuchElementException();
        }
        Object oldValue = node.value;
        removeNode(node);
        return oldValue;
    }

    //-----------------------------------------------------------------------
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof List == false) {
            return false;
        }
        List other = (List) obj;
        if (other.size() != size()) {
            return false;
        }
        ListIterator it1 = listIterator();
        ListIterator it2 = other.listIterator();
        while (it1.hasNext() && it2.hasNext()) {
            Object o1 = it1.next();
            Object o2 = it2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }
        return !(it1.hasNext() || it2.hasNext());
    }

    public int hashCode() {
        int hashCode = 1;
        Iterator it = iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        StringBuffer buf = new StringBuffer(16 * size());
        buf.append("[");

        Iterator it = iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object value = it.next();
            buf.append(value == this ? "(this Collection)" : value);
            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append("]");
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two values for equals.
     * This implementation uses the equals method.
     * Subclasses can override this to match differently.
     * 
     * @param value1  the first value to compare, may be null
     * @param value2  the second value to compare, may be null
     * @return true if equal
     */
    protected boolean isEqualValue(Object value1, Object value2) {
        return (value1 == value2 || (value1 == null ? false : value1.equals(value2)));
    }
    
    /**
     * Creates a new node with previous, next and element all set to null.
     * This implementation creates a new empty Node.
     * Subclasses can override this to create a different class.
     * 
     * @return  newly created node
     */
    protected Node createHeaderNode() {
        return new Node();
    }

    /**
     * Creates a new node with the specified properties.
     * This implementation creates a new Node with data.
     * Subclasses can override this to create a different class.
     * 
     * @param previous  node to precede the new node
     * @param next  node to follow the new node
     * @param value  value of the new node
     */
    protected Node createNode(Node previous, Node next, Object value) {
        return new Node(previous, next, value);
    }

    /**
     * Creates a new node with the specified object as its 
     * <code>value</code> and inserts it before <code>node</code>.
     *
     * @param node  node to insert before
     * @param value  value of the newly added node
     * @throws NullPointerException if <code>node</code> is null
     */
    protected void addNodeBefore(Node node, Object value) {
        Node newNode = createNode(node.previous, node, value);
        node.previous.next = newNode;
        node.previous = newNode;
        size++;
        modCount++;
    }

    /**
     * Creates a new node with the specified object as its 
     * <code>value</code> and inserts it after <code>node</code>.
     * 
     * @param node  node to insert after
     * @param value  value of the newly added node
     * @throws NullPointerException if <code>node</code> is null
     */
    protected void addNodeAfter(Node node, Object value) {
        Node newNode = createNode(node, node.next, value);
        node.next.previous = newNode;
        node.next = newNode;
        size++;
        modCount++;
    }

    /**
     * Removes the specified node from the list.
     *
     * @param node  the node to remove
     * @throws NullPointerException if <code>node</code> is null
     */
    protected void removeNode(Node node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        size--;
        modCount++;
    }

    /**
     * Removes all nodes by resetting the circular list marker.
     */
    protected void removeAllNodes() {
        header.next = header;
        header.previous = header;
        size = 0;
        modCount++;
    }

    /**
     * Gets the node at a particular index.
     * 
     * @param index  the index, starting from 0
     * @param endMarkerAllowed  whether or not the end marker can be returned if
     * startIndex is set to the list's size
     * @throws IndexOutOfBoundsException if the index is less than 0; equal to
     * the size of the list and endMakerAllowed is false; or greater than the
     * size of the list
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
            node = header.next;
            for (int currentIndex = 0; currentIndex < index; currentIndex++) {
                node = node.next;
            }
        } else {
            // Search backwards
            node = header;
            for (int currentIndex = size; currentIndex > index; currentIndex--) {
                node = node.previous;
            }
        }
        return node;
    }

    //-----------------------------------------------------------------------
    /**
     * Serializes the data held in this object to the stream specified.
     * <p>
     * The first serializable subclass must call this method from
     * <code>writeObject</code>.
     */
    protected void doWriteObject(ObjectOutputStream outputStream) throws IOException {
        // Write the size so we know how many nodes to read back
        outputStream.writeInt(size());
        for (Iterator itr = iterator(); itr.hasNext();) {
            outputStream.writeObject(itr.next());
        }
    }

    /**
     * Deserializes the data held in this object to the stream specified.
     * <p>
     * The first serializable subclass must call this method from
     * <code>readObject</code>.
     */
    protected void doReadObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        init();
        int size = inputStream.readInt();
        for (int i = 0; i < size; i++) {
            add(inputStream.readObject());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A node within the linked list.
     * 
     * @author Rich Dougherty
     * @author Stephen Colebourne
     */
    protected static class Node {

        /** A pointer to the node before this node */
        public Node previous;
        /** A pointer to the node after this node */
        public Node next;
        /** The object contained within this node */
        public Object value;

        /**
         * Constructs a new header node.
         */
        public Node() {
            super();
            previous = this;
            next = this;
        }

        /**
         * Constructs a new node.
         * 
         * @param previous  the previous node in the list
         * @param next  the next node in the list
         * @param value  the value to store
         */
        public Node(Node previous, Node next, Object value) {
            super();
            this.previous = previous;
            this.next = next;
            this.value = value;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A list iterator over the linked list.
     * 
     * @author Rich Dougherty
     */
    protected class LinkedListIterator implements ListIterator, OrderedIterator {

        /**
         * The node that will be returned by {@link #next()}. If this is equal
         * to {@link #marker} then there are no more values to return.
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
         * Create a ListIterator for a list, starting at the first value in
         * the list.
         */
        public LinkedListIterator() throws IndexOutOfBoundsException {
            this(0);
        }

        /**
         * Create a ListIterator for a list.
         * 
         * @param startIndex The index to start at.
         */
        public LinkedListIterator(int startIndex) throws IndexOutOfBoundsException {
            super();
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
         * @throws IllegalStateException If {@link #next()} or
         * {@link #previous()} haven't been called, or if the node has been removed
         * with {@link #remove()} or a new node added with {@link #add(Object)}.
         */
        protected Node getLastNodeReturned() throws IllegalStateException {
            if (lastNodeReturned == null) {
                throw new IllegalStateException();
            }
            return lastNodeReturned;
        }

        public boolean hasNext() {
            return nextNode != header;
        }

        public Object next() {
            checkModCount();
            if (!hasNext()) {
                throw new NoSuchElementException("No element at index " +
                        nextIndex + ".");
            }
            Object value = nextNode.value;
            lastNodeReturned = nextNode;
            nextNode = nextNode.next;
            nextIndex++;
            return value;
        }

        public boolean hasPrevious() {
            return nextNode.previous != header;
        }

        public Object previous() {
            checkModCount();
            if (!hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            nextNode = nextNode.previous;
            Object value = nextNode.value;
            lastNodeReturned = nextNode;
            nextIndex--;
            return value;
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
            getLastNodeReturned().value = o;
        }

        public void add(Object o) {
            checkModCount();
            addNodeBefore(nextNode, o);
            lastNodeReturned = null;
            nextIndex++;
            expectedModCount++;
        }

    }

}
