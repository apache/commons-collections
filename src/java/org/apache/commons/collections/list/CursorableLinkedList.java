/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/list/CursorableLinkedList.java,v 1.1 2003/12/24 01:15:40 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A <code>List</code> implementation with a <code>ListIterator</code> that
 * allows concurrent modifications to the underlying list.
 * <p>
 * This implementation supports all of the optional {@link List} operations.
 * It extends <code>AbstractLinkedList</code> and thus provides the
 * stack/queue/dequeue operations available in {@link java.util.LinkedList}.
 * <p>
 * The main feature of this class is the ability to modify the list and the
 * iterator at the same time. Both the {@link #listIterator()} and {@link #cursor()}
 * methods provides access to a <code>Cursor</code> instance which extends
 * <code>ListIterator</code>. The cursor allows changes to the list concurrent
 * with changes to the iterator. Note that the {@link #iterator()} method and
 * sublists  do <b>not</b> provide this cursor behaviour.
 * <p>
 * The <code>Cursor</code> class is provided partly for backwards compatability
 * and partly because it allows the cursor to be directly closed. Closing the
 * cursor is optional because references are held via a <code>WeakReference</code>.
 * For most purposes, simply modify the iterator and list at will, and then let
 * the garbage collector to the rest.
 * <p>
 * <b>Note that this implementation is not synchronized.</b>
 *
 * @see java.util.LinkedList
 * @since Commons Collections 1.0
 * @version $Revision: 1.1 $ $Date: 2003/12/24 01:15:40 $
 * 
 * @author Rodney Waldhoff
 * @author Janek Bogucki
 * @author Simon Kitching
 * @author Stephen Colebourne
 */
public class CursorableLinkedList extends AbstractLinkedList implements Serializable {

    /** Ensure serialization compatability */
    private static final long serialVersionUID = 8836393098519411393L;

    /** A list of the cursor currently open on this list */
    protected transient List cursors = new ArrayList();

    //-----------------------------------------------------------------------
    /**
     * Constructor that creates.
     */
    public CursorableLinkedList() {
        super();
        init(); // must call init() as use super();
    }

    /**
     * Constructor that copies the specified collection
     * 
     * @param coll  the collection to copy
     */
    public CursorableLinkedList(Collection coll) {
        super(coll);
    }

    /**
     * The equivalent of a default constructor called
     * by any constructor and by <code>readObject</code>.
     */
    protected void init() {
        super.init();
        cursors = new ArrayList();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an iterator that does <b>not</b> support concurrent modification.
     * <p>
     * If the underlying list is modified while iterating using this iterator
     * a ConcurrentModificationException will occur.
     * The cursor behaviour is available via {@link #listIterator()}.
     * 
     * @return a new iterator that does <b>not</b> support concurrent modification
     */
    public Iterator iterator() {
        return super.listIterator(0);
    }

    /**
     * Returns a cursor iterator that allows changes to the underlying list in parallel.
     * <p>
     * The cursor enables iteration and list changes to occur in any order without
     * invalidating the iterator (from one thread). When elements are added to the
     * list, an event is fired to all active cursors enabling them to adjust to the
     * change in the list.
     * <p>
     * When the "current" (i.e., last returned by {@link ListIterator#next}
     * or {@link ListIterator#previous}) element of the list is removed,
     * the cursor automatically adjusts to the change (invalidating the
     * last returned value such that it cannot be removed).
     * 
     * @return a new cursor iterator
     */
    public ListIterator listIterator() {
        return cursor(0);
    }

    /**
     * Returns a cursor iterator that allows changes to the underlying list in parallel.
     * <p>
     * The cursor enables iteration and list changes to occur in any order without
     * invalidating the iterator (from one thread). When elements are added to the
     * list, an event is fired to all active cursors enabling them to adjust to the
     * change in the list.
     * <p>
     * When the "current" (i.e., last returned by {@link ListIterator#next}
     * or {@link ListIterator#previous}) element of the list is removed,
     * the cursor automatically adjusts to the change (invalidating the
     * last returned value such that it cannot be removed).
     * 
     * @param fromIndex  the index to start from
     * @return a new cursor iterator
     */
    public ListIterator listIterator(int fromIndex) {
        return cursor(fromIndex);
    }

    /**
     * Returns a {@link Cursor} for iterating through the elements of this list.
     * <p>
     * A <code>Cursor</code> is a <code>ListIterator</code> with an additional
     * <code>close()</code> method. Calling this method immediately discards the
     * references to the cursor. If it is not called, then the garbage collector
     * will still remove the reference as it is held via a <code>WeakReference</code>.
     * <p>
     * The cursor enables iteration and list changes to occur in any order without
     * invalidating the iterator (from one thread). When elements are added to the
     * list, an event is fired to all active cursors enabling them to adjust to the
     * change in the list.
     * <p>
     * When the "current" (i.e., last returned by {@link ListIterator#next}
     * or {@link ListIterator#previous}) element of the list is removed,
     * the cursor automatically adjusts to the change (invalidating the
     * last returned value such that it cannot be removed).
     * <p>
     * The {@link #listIterator()} method returns the same as this method, and can
     * be cast to a <code>Cursor</code> if the <code>close</code> method is required.
     *
     * @return a new cursor iterator
     */
    public CursorableLinkedList.Cursor cursor() {
        return cursor(0);
    }

    /**
     * Returns a {@link Cursor} for iterating through the elements of this list
     * starting from a specified index.
     * <p>
     * A <code>Cursor</code> is a <code>ListIterator</code> with an additional
     * <code>close()</code> method. Calling this method immediately discards the
     * references to the cursor. If it is not called, then the garbage collector
     * will still remove the reference as it is held via a <code>WeakReference</code>.
     * <p>
     * The cursor enables iteration and list changes to occur in any order without
     * invalidating the iterator (from one thread). When elements are added to the
     * list, an event is fired to all active cursors enabling them to adjust to the
     * change in the list.
     * <p>
     * When the "current" (i.e., last returned by {@link ListIterator#next}
     * or {@link ListIterator#previous}) element of the list is removed,
     * the cursor automatically adjusts to the change (invalidating the
     * last returned value such that it cannot be removed).
     * <p>
     * The {@link #listIterator(int)} method returns the same as this method, and can
     * be cast to a <code>Cursor</code> if the <code>close</code> method is required.
     *
     * @param fromIndex  the index to start from
     * @return a new cursor iterator
     * @throws IndexOutOfBoundsException if the index is out of range
     *      (index &lt; 0 || index &gt; size()).
     */
    public CursorableLinkedList.Cursor cursor(int fromIndex) {
        Cursor cursor = new Cursor(this, fromIndex);
        registerCursor(cursor);
        return cursor;
    }

    //-----------------------------------------------------------------------
    /**
     * Updates the node with a new value.
     * This implementation sets the value on the node.
     * Subclasses can override this to record the change.
     * 
     * @param node  node to update
     * @param value  new value of the node
     */
    protected void updateNode(Node node, Object value) {
        super.updateNode(node, value);
        broadcastNodeChanged(node);
    }

    /**
     * Inserts a new node into the list.
     *
     * @param nodeToInsert  new node to insert
     * @param insertBeforeNode  node to insert before
     * @throws NullPointerException if either node is null
     */
    protected void addNode(Node nodeToInsert, Node insertBeforeNode) {
        super.addNode(nodeToInsert, insertBeforeNode);
        broadcastNodeInserted(nodeToInsert);
    }
    
    /**
     * Removes the specified node from the list.
     *
     * @param node  the node to remove
     * @throws NullPointerException if <code>node</code> is null
     */
    protected void removeNode(Node node) {
        super.removeNode(node);
        broadcastNodeRemoved(node);
    }

    /**
     * Removes all nodes by iteration.
     */
    protected void removeAllNodes() {
        if (size() > 0) {
            // superclass implementation would break all the iterators
            Iterator it = iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Registers a cursor to be notified of changes to this list.
     * 
     * @param cursor  the cursor to register
     */
    protected void registerCursor(Cursor cursor) {
        // We take this opportunity to clean the cursors list
        // of WeakReference objects to garbage-collected cursors.
        for (Iterator it = cursors.iterator(); it.hasNext();) {
            WeakReference ref = (WeakReference) it.next();
            if (ref.get() == null) {
                it.remove();
            }
        }
        cursors.add(new WeakReference(cursor));
    }

    /**
     * Deregisters a cursor from the list to be notified of changes.
     * 
     * @param cursor  the cursor to deregister
     */
    protected void unregisterCursor(Cursor cursor) {
        for (Iterator it = cursors.iterator(); it.hasNext();) {
            WeakReference ref = (WeakReference) it.next();
            Cursor cur = (Cursor) ref.get();
            if (cur == null) {
                // some other unrelated cursor object has been 
                // garbage-collected; let's take the opportunity to
                // clean up the cursors list anyway..
                it.remove();

            } else if (cur == cursor) {
                ref.clear();
                it.remove();
                break;
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Informs all of my registered cursors that the specified
     * element was changed.
     * 
     * @param node  the node that was changed
     */
    protected void broadcastNodeChanged(Node node) {
        Iterator it = cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference) it.next();
            Cursor cursor = (Cursor) ref.get();
            if (cursor == null) {
                it.remove(); // clean up list
            } else {
                cursor.nodeChanged(node);
            }
        }
    }

    /**
     * Informs all of my registered cursors that the specified
     * element was just removed from my list.
     * 
     * @param node  the node that was changed
     */
    protected void broadcastNodeRemoved(Node node) {
        Iterator it = cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference) it.next();
            Cursor cursor = (Cursor) ref.get();
            if (cursor == null) {
                it.remove(); // clean up list
            } else {
                cursor.nodeRemoved(node);
            }
        }
    }

    /**
     * Informs all of my registered cursors that the specified
     * element was just added to my list.
     * 
     * @param node  the node that was changed
     */
    protected void broadcastNodeInserted(Node node) {
        Iterator it = cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference) it.next();
            Cursor cursor = (Cursor) ref.get();
            if (cursor == null) {
                it.remove(); // clean up list
            } else {
                cursor.nodeInserted(node);
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Serializes the data held in this object to the stream specified.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    /**
     * Deserializes the data held in this object to the stream specified.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }

    //-----------------------------------------------------------------------
    /**
     * An extended <code>ListIterator</code> that allows concurrent changes to
     * the underlying list.
     */
    public static class Cursor extends AbstractLinkedList.LinkedListIterator {
        /** Is the cursor valid (not closed) */
        boolean valid = true;
        /** Is the next index valid */
        boolean nextIndexValid = true;
        
        /**
         * Constructs a new cursor.
         * 
         * @param index  the index to start from
         */
        protected Cursor(CursorableLinkedList parent, int index) {
            super(parent, index);
            valid = true;
        }
        
        /**
         * Adds an object to the list.
         * The object added here will be the new 'previous' in the iterator.
         * 
         * @param obj  the object to add
         */
        public void add(Object obj) {
            super.add(obj);
            // add on iterator does not return the added element
            next = next.next;
        }

        /**
         * Gets the index of the next element to be returned.
         * 
         * @return the next index
         */
        public int nextIndex() {
            if (nextIndexValid == false) {
                if (next == list.header) {
                    nextIndex = list.size();
                } else {
                    int pos = 0;
                    Node temp = list.header.next;
                    while (temp != next) {
                        pos++;
                        temp = temp.next;
                    }
                    nextIndex = pos;
                }
                nextIndexValid = true;
            }
            return nextIndex;
        }

        /**
         * Handle event from the list when a node has changed.
         * 
         * @param node  the node that changed
         */
        protected void nodeChanged(Node node) {
            // do nothing
        }

        /**
         * Handle event from the list when a node has been removed.
         * 
         * @param node  the node that was removed
         */
        protected void nodeRemoved(Node node) {
            if (node == next) {
                next = node.next;
            } else if (node == current) {
                current = null;
                nextIndex--;
            } else {
                nextIndexValid = false;
            }
        }

        /**
         * Handle event from the list when a node has been added.
         * 
         * @param node  the node that was added
         */
        protected void nodeInserted(Node node) {
            if (node.previous == current) {
                next = node;
            } else if (next.previous == node) {
                next = node;
            } else {
                nextIndexValid = false;
            }
        }

        /**
         * Override superclass modCount check, and replace it with our valid flag.
         */
        protected void checkModCount() {
            if (!valid) {
                throw new ConcurrentModificationException("Cursor closed");
            }
        }

        /**
         * Mark this cursor as no longer being needed. Any resources
         * associated with this cursor are immediately released.
         * In previous versions of this class, it was mandatory to close
         * all cursor objects to avoid memory leaks. It is <i>no longer</i>
         * necessary to call this close method; an instance of this class
         * can now be treated exactly like a normal iterator.
         */
        public void close() {
            if (valid) {
                ((CursorableLinkedList) list).unregisterCursor(this);
                valid = false;
            }
        }
    }
}

//class CursorableSubList extends CursorableLinkedList implements List {
//
//    //--- constructors -----------------------------------------------
//
//    CursorableSubList(CursorableLinkedList list, int from, int to) {
//        if(0 > from || list.size() < to) {
//            throw new IndexOutOfBoundsException();
//        } else if(from > to) {
//            throw new IllegalArgumentException();
//        }
//        _list = list;
//        if(from < list.size()) {
//            _head.setNext(_list.getListableAt(from));
//            _pre = (null == _head.next()) ? null : _head.next().prev();
//        } else {
//            _pre = _list.getListableAt(from-1);
//        }
//        if(from == to) {
//            _head.setNext(null);
//            _head.setPrev(null);
//            if(to < list.size()) {
//                _post = _list.getListableAt(to);
//            } else {
//                _post = null;
//            }
//        } else {
//            _head.setPrev(_list.getListableAt(to-1));
//            _post = _head.prev().next();
//        }
//        _size = to - from;
//        _modCount = _list._modCount;
//    }
//
//    //--- public methods ------------------------------------------
//
//    public void clear() {
//        checkForComod();
//        Iterator it = iterator();
//        while(it.hasNext()) {
//            it.next();
//            it.remove();
//        }
//    }
//
//    public Iterator iterator() {
//        checkForComod();
//        return super.iterator();
//    }
//
//    public int size() {
//        checkForComod();
//        return super.size();
//    }
//
//    public boolean isEmpty() {
//        checkForComod();
//        return super.isEmpty();
//    }
//
//    public Object[] toArray() {
//        checkForComod();
//        return super.toArray();
//    }
//
//    public Object[] toArray(Object a[]) {
//        checkForComod();
//        return super.toArray(a);
//    }
//
//    public boolean contains(Object o) {
//        checkForComod();
//        return super.contains(o);
//    }
//
//    public boolean remove(Object o) {
//        checkForComod();
//        return super.remove(o);
//    }
//
//    public Object removeFirst() {
//        checkForComod();
//        return super.removeFirst();
//    }
//
//    public Object removeLast() {
//        checkForComod();
//        return super.removeLast();
//    }
//
//    public boolean addAll(Collection c) {
//        checkForComod();
//        return super.addAll(c);
//    }
//
//    public boolean add(Object o) {
//        checkForComod();
//        return super.add(o);
//    }
//
//    public boolean addFirst(Object o) {
//        checkForComod();
//        return super.addFirst(o);
//    }
//
//    public boolean addLast(Object o) {
//        checkForComod();
//        return super.addLast(o);
//    }
//
//    public boolean removeAll(Collection c) {
//        checkForComod();
//        return super.removeAll(c);
//    }
//
//    public boolean containsAll(Collection c) {
//        checkForComod();
//        return super.containsAll(c);
//    }
//
//    public boolean addAll(int index, Collection c) {
//        checkForComod();
//        return super.addAll(index,c);
//    }
//
//    public int hashCode() {
//        checkForComod();
//        return super.hashCode();
//    }
//
//    public boolean retainAll(Collection c) {
//        checkForComod();
//        return super.retainAll(c);
//    }
//
//    public Object set(int index, Object element) {
//        checkForComod();
//        return super.set(index,element);
//    }
//
//    public boolean equals(Object o) {
//        checkForComod();
//        return super.equals(o);
//    }
//
//    public Object get(int index) {
//        checkForComod();
//        return super.get(index);
//    }
//
//    public Object getFirst() {
//        checkForComod();
//        return super.getFirst();
//    }
//
//    public Object getLast() {
//        checkForComod();
//        return super.getLast();
//    }
//
//    public void add(int index, Object element) {
//        checkForComod();
//        super.add(index,element);
//    }
//
//    public ListIterator listIterator(int index) {
//        checkForComod();
//        return super.listIterator(index);
//    }
//
//    public Object remove(int index) {
//        checkForComod();
//        return super.remove(index);
//    }
//
//    public int indexOf(Object o) {
//        checkForComod();
//        return super.indexOf(o);
//    }
//
//    public int lastIndexOf(Object o) {
//        checkForComod();
//        return super.lastIndexOf(o);
//    }
//
//    public ListIterator listIterator() {
//        checkForComod();
//        return super.listIterator();
//    }
//
//    public List subList(int fromIndex, int toIndex) {
//        checkForComod();
//        return super.subList(fromIndex,toIndex);
//    }
//
//    //--- protected methods ------------------------------------------
//
//    /**
//     * Inserts a new <i>value</i> into my
//     * list, after the specified <i>before</i> element, and before the
//     * specified <i>after</i> element
//     *
//     * @return the newly created {@link CursorableLinkedList.Listable}
//     */
//    protected Listable insertListable(Listable before, Listable after, Object value) {
//        _modCount++;
//        _size++;
//        Listable elt = _list.insertListable((null == before ? _pre : before), (null == after ? _post : after),value);
//        if(null == _head.next()) {
//            _head.setNext(elt);
//            _head.setPrev(elt);
//        }
//        if(before == _head.prev()) {
//            _head.setPrev(elt);
//        }
//        if(after == _head.next()) {
//            _head.setNext(elt);
//        }
//        broadcastListableInserted(elt);
//        return elt;
//    }
//
//    /**
//     * Removes the given {@link CursorableLinkedList.Listable} from my list.
//     */
//    protected void removeListable(Listable elt) {
//        _modCount++;
//        _size--;
//        if(_head.next() == elt && _head.prev() == elt) {
//            _head.setNext(null);
//            _head.setPrev(null);
//        }
//        if(_head.next() == elt) {
//            _head.setNext(elt.next());
//        }
//        if(_head.prev() == elt) {
//            _head.setPrev(elt.prev());
//        }
//        _list.removeListable(elt);
//        broadcastListableRemoved(elt);
//    }
//
//    /**
//     * Test to see if my underlying list has been modified
//     * by some other process.  If it has, throws a
//     * {@link ConcurrentModificationException}, otherwise
//     * quietly returns.
//     *
//     * @throws ConcurrentModificationException
//     */
//    protected void checkForComod() throws ConcurrentModificationException {
//        if(_modCount != _list._modCount) {
//            throw new ConcurrentModificationException();
//        }
//    }
//
//    //--- protected attributes ---------------------------------------
//
//    /** My underlying list */
//    protected CursorableLinkedList _list = null;
//
//    /** The element in my underlying list preceding the first element in my list. */
//    protected Listable _pre = null;
//
//    /** The element in my underlying list following the last element in my list. */
//    protected Listable _post = null;
//
//}
