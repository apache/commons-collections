/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/FastArrayList.java,v 1.2 2001/04/21 12:19:57 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2001/04/21 12:19:57 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * <p>A customized implementation of <code>java.util.ArrayList</code> designed
 * to operate in a multithreaded environment where the large majority of
 * method calls are read-only, instead of structural changes.  When operating
 * in "fast" mode, read calls are non-synchronized and write calls perform the
 * following steps:</p>
 * <ul>
 * <li>Clone the existing collection
 * <li>Perform the modification on the clone
 * <li>Replace the existing collection with the (modified) clone
 * </ul>
 * <p>When first created, objects of this class default to "slow" mode, where
 * all accesses of any type are synchronized but no cloning takes place.  This
 * is appropriate for initially populating the collection, followed by a switch
 * to "fast" mode (by calling <code>setFast(true)</code>) after initialization
 * is complete.</p>
 *
 * <p><strong>NOTE</strong>: If you are creating and accessing an
 * <code>ArrayList</code> only within a single thread, you should use
 * <code>java.util.ArrayList</code> directly (with no synchronization), for
 * maximum performance.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2001/04/21 12:19:57 $
 */

public class FastArrayList extends ArrayList {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a an empty list.
     */
    public FastArrayList() {

        super();
        this.list = new ArrayList();

    }


    /**
     * Construct an empty list with the specified capacity.
     *
     * @param capacity The initial capacity of the empty list
     */
    public FastArrayList(int capacity) {

        super();
        this.list = new ArrayList(capacity);

    }


    /**
     * Construct a list containing the elements of the specified collection,
     * in the order they are returned by the collection's iterator.
     *
     * @param collection The collection whose elements initialize the contents
     *  of this list
     */
    public FastArrayList(Collection collection) {

        super();
        this.list = new ArrayList(collection);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The underlying list we are managing.
     */
    protected ArrayList list = null;


    // ------------------------------------------------------------- Properties


    /**
     * Are we operating in "fast" mode?
     */
    protected boolean fast = false;

    public boolean getFast() {
        return (this.fast);
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Appends the specified element to the end of this list.
     *
     * @param element The element to be appended
     */
    public boolean add(Object element) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.add(element);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.add(element));
            }
        }

    }


    /**
     * Insert the specified element at the specified position in this list,
     * and shift all remaining elements up one position.
     *
     * @param index Index at which to insert this element
     * @param element The element to be inserted
     *
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public void add(int index, Object element) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                temp.add(index, element);
                list = temp;
            }
        } else {
            synchronized (list) {
                list.add(index, element);
            }
        }

    }


    /**
     * Append all of the elements in the specified Collection to the end
     * of this list, in the order that they are returned by the specified
     * Collection's Iterator.
     *
     * @param collection The collection to be appended
     */
    public boolean addAll(Collection collection) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.addAll(collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.addAll(collection));
            }
        }

    }


    /**
     * Insert all of the elements in the specified Collection at the specified
     * position in this list, and shift any previous elements upwards as
     * needed.
     *
     * @param index Index at which insertion takes place
     * @param collection The collection to be added
     *
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public boolean addAll(int index, Collection collection) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.addAll(index, collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.addAll(index, collection));
            }
        }

    }


    /**
     * Remove all of the elements from this list.  The list will be empty
     * after this call returns.
     *
     * @exception UnsupportedOperationException if <code>clear()</code>
     *  is not supported by this list
     */
    public void clear() {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                temp.clear();
                list = temp;
            }
        } else {
            synchronized (list) {
                list.clear();
            }
        }

    }


    /**
     * Return a shallow copy of this <code>FastArrayList</code> instance.
     * The elements themselves are not copied.
     */
    public Object clone() {

        FastArrayList results = null;
        if (fast) {
            results = new FastArrayList(list);
        } else {
            synchronized (list) {
                results = new FastArrayList(list);
            }
        }
        results.setFast(getFast());
        return (results);

    }


    /**
     * Return <code>true</code> if this list contains the specified element.
     *
     * @param element The element to test for
     */
    public boolean contains(Object element) {

        if (fast) {
            return (list.contains(element));
        } else {
            synchronized (list) {
                return (list.contains(element));
            }
        }

    }


    /**
     * Return <code>true</code> if this list contains all of the elements
     * in the specified Collection.
     *
     * @param collection Collection whose elements are to be checked
     */
    public boolean containsAll(Collection collection) {

        if (fast) {
            return (list.containsAll(collection));
        } else {
            synchronized (list) {
                return (list.containsAll(collection));
            }
        }

    }


    /**
     * Increase the capacity of this <code>ArrayList</code> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param capacity The new minimum capacity
     */
    public void ensureCapacity(int capacity) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                temp.ensureCapacity(capacity);
                list = temp;
            }
        } else {
            synchronized (list) {
                list.ensureCapacity(capacity);
            }
        }

    }


    /**
     * Compare the specified object with this list for equality.  This
     * implementation uses exactly the code that is used to define the
     * list equals function in the documentation for the
     * <code>List.equals</code> method.
     *
     * @param o Object to be compared to this list
     */
    public boolean equals(Object o) {

        // Simple tests that require no synchronization
        if (o == this)
            return (true);
        else if (!(o instanceof List))
            return (false);
        List lo = (List) o;

        // Compare the sets of elements for equality
        if (fast) {
            ListIterator li1 = list.listIterator();
            ListIterator li2 = lo.listIterator();
            while (li1.hasNext() && li2.hasNext()) {
                Object o1 = li1.next();
                Object o2 = li2.next();
                if (!(o1 == null ? o2 == null : o1.equals(o2)))
                    return (false);
            }
            return (!(li1.hasNext() || li2.hasNext()));
        } else {
            synchronized (list) {
                ListIterator li1 = list.listIterator();
                ListIterator li2 = lo.listIterator();
                while (li1.hasNext() && li2.hasNext()) {
                    Object o1 = li1.next();
                    Object o2 = li2.next();
                    if (!(o1 == null ? o2 == null : o1.equals(o2)))
                        return (false);
                }
                return (!(li1.hasNext() || li2.hasNext()));
            }
        }

    }


    /**
     * Return the element at the specified position in the list.
     *
     * @param index The index of the element to return
     *
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public Object get(int index) {

        if (fast) {
            return (list.get(index));
        } else {
            synchronized (list) {
                return (list.get(index));
            }
        }

    }


    /**
     * Return the hash code value for this list.  This implementation uses
     * exactly the code that is used to define the list hash function in the
     * documentation for the <code>List.hashCode</code> method.
     */
    public int hashCode() {

        if (fast) {
            int hashCode = 1;
            Iterator i = list.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
            }
            return (hashCode);
        } else {
            synchronized (list) {
                int hashCode = 1;
                Iterator i = list.iterator();
                while (i.hasNext()) {
                    Object o = i.next();
                    hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
                }
                return (hashCode);
            }
        }

    }


    /**
     * Search for the first occurrence of the given argument, testing
     * for equality using the <code>equals()</code> method, and return
     * the corresponding index, or -1 if the object is not found.
     *
     * @param element The element to search for
     */
    public int indexOf(Object element) {

        if (fast) {
            return (list.indexOf(element));
        } else {
            synchronized (list) {
                return (list.indexOf(element));
            }
        }

    }


    /**
     * Test if this list has no elements.
     */
    public boolean isEmpty() {

        if (fast) {
            return (list.isEmpty());
        } else {
            synchronized (list) {
                return (list.isEmpty());
            }
        }

    }


    /**
     * Return an iterator over the elements in this list in proper sequence.
     * <br><br>
     * <strong>IMPLEMENTATION NOTE</strong> - If the list is operating in fast
     * mode, an Iterator is returned, and a structural modification to the
     * list is made, then the Iterator will continue over the previous contents
     * of the list (at the time that the Iterator was created), rather than
     * failing due to concurrent modifications.
     */
    public Iterator iterator() {

        if (fast) {
            return (list.iterator());
        } else {
            synchronized (list) {
                return (list.iterator());
            }
        }

    }


    /**
     * Search for the last occurrence of the given argument, testing
     * for equality using the <code>equals()</code> method, and return
     * the corresponding index, or -1 if the object is not found.
     *
     * @param element The element to search for
     */
    public int lastIndexOf(Object element) {

        if (fast) {
            return (list.lastIndexOf(element));
        } else {
            synchronized (list) {
                return (list.lastIndexOf(element));
            }
        }

    }


    /**
     * Return an iterator of the elements of this list, in proper sequence.
     * See the implementation note on <code>iterator()</code>.
     */
    public ListIterator listIterator() {

        if (fast) {
            return (list.listIterator());
        } else {
            synchronized (list) {
                return (list.listIterator());
            }
        }

    }


    /**
     * Return an iterator of the elements of this list, in proper sequence,
     * starting at the specified position.
     * See the implementation note on <code>iterator()</code>.
     *
     * @param index The starting position of the iterator to return
     *
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public ListIterator listIterator(int index) {

        if (fast) {
            return (list.listIterator(index));
        } else {
            synchronized (list) {
                return (list.listIterator(index));
            }
        }

    }


    /**
     * Remove the element at the specified position in the list, and shift
     * any subsequent elements down one position.
     *
     * @param index Index of the element to be removed
     *
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public Object remove(int index) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                Object result = temp.remove(index);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.remove(index));
            }
        }

    }


    /**
     * Remove the first occurrence of the specified element from the list,
     * and shift any subsequent elements down one position.
     *
     * @param element Element to be removed
     */
    public boolean remove(Object element) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.remove(element);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.remove(element));
            }
        }

    }


    /**
     * Remove from this collection all of its elements that are contained
     * in the specified collection.
     *
     * @param collection Collection containing elements to be removed
     *
     * @exception UnsupportedOperationException if this optional operation
     *  is not supported by this list
     */
    public boolean removeAll(Collection collection) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.removeAll(collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.removeAll(collection));
            }
        }

    }


    /**
     * Remove from this collection all of its elements except those that are
     * contained in the specified collection.
     *
     * @param collection Collection containing elements to be retained
     *
     * @exception UnsupportedOperationException if this optional operation
     *  is not supported by this list
     */
    public boolean retainAll(Collection collection) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.retainAll(collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.retainAll(collection));
            }
        }

    }


    /**
     * Replace the element at the specified position in this list with
     * the specified element.  Returns the previous object at that position.
     * <br><br>
     * <strong>IMPLEMENTATION NOTE</strong> - This operation is specifically
     * documented to not be a structural change, so it is safe to be performed
     * without cloning.
     *
     * @param index Index of the element to replace
     * @param element The new element to be stored
     *
     * @exception IndexOutOfBoundsException if the index is out of range
     */
    public Object set(int index, Object element) {

        if (fast) {
            return (list.set(index, element));
        } else {
            synchronized (list) {
                return (list.set(index, element));
            }
        }

    }


    /**
     * Return the number of elements in this list.
     */
    public int size() {

        if (fast) {
            return (list.size());
        } else {
            synchronized (list) {
                return (list.size());
            }
        }

    }


    /**
     * Return a view of the portion of this list between fromIndex
     * (inclusive) and toIndex (exclusive).  The returned list is backed
     * by this list, so non-structural changes in the returned list are
     * reflected in this list.  The returned list supports
     * all of the optional list operations supported by this list.
     *
     * @param fromIndex The starting index of the sublist view
     * @param toIndex The index after the end of the sublist view
     *
     * @exception IndexOutOfBoundsException if an index is out of range
     */
    public List subList(int fromIndex, int toIndex) {

        if (fast) {
            return (list.subList(fromIndex, toIndex));
        } else {
            synchronized (list) {
                return (list.subList(fromIndex, toIndex));
            }
        }

    }


    /**
     * Return an array containing all of the elements in this list in the
     * correct order.
     */
    public Object[] toArray() {

        if (fast) {
            return (list.toArray());
        } else {
            synchronized (list) {
                return (list.toArray());
            }
        }

    }


    /**
     * Return an array containing all of the elements in this list in the
     * correct order.  The runtime type of the returned array is that of
     * the specified array.  If the list fits in the specified array, it is
     * returned therein.  Otherwise, a new array is allocated with the
     * runtime type of the specified array, and the size of this list.
     *
     * @param array Array defining the element type of the returned list
     *
     * @exception ArrayStoreException if the runtime type of <code>array</code>
     *  is not a supertype of the runtime type of every element in this list
     */
    public Object[] toArray(Object array[]) {

        if (fast) {
            return (list.toArray(array));
        } else {
            synchronized (list) {
                return (list.toArray(array));
            }
        }

    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("FastArrayList[");
        sb.append(list.toString());
        sb.append("]");
        return (sb.toString());

    }


    /**
     * Trim the capacity of this <code>ArrayList</code> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <code>ArrayList</code> instance.
     */
    public void trimToSize() {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                temp.trimToSize();
                list = temp;
            }
        } else {
            synchronized (list) {
                list.trimToSize();
            }
        }

    }


}
