/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/DefaultMapBag.java,v 1.12 2003/12/03 11:37:44 scolebourne Exp $
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
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * A skeletal implementation of the {@link Bag}
 * interface to minimize the effort required for target implementations.
 * Subclasses need only to call <code>setMap(Map)</code> in their constructor 
 * (or invoke the {@link #DefaultMapBag(java.util.Map) Map-constructor})
 * specifying a map instance that will be used to store the contents of 
 * the bag.
 * <p>
 * The map will be used to map bag elements to a number; the number represents
 * the number of occurrences of that element in the bag.
 *
 * @deprecated Moved to bag subpackage as AbstractMapBag. Due to be removed in v4.0.
 * @since Commons Collections 2.0
 * @version $Revision: 1.12 $ $Date: 2003/12/03 11:37:44 $
 * 
 * @author Chuck Burdick
 * @author Michael A. Smith
 * @author Stephen Colebourne
 * @author Janek Bogucki
 */
public abstract class DefaultMapBag implements Bag {
    private Map _map = null;
    private int _total = 0;
    private int _mods = 0;

    /**
     * No-argument constructor.  
     * Subclasses should invoke <code>setMap(Map)</code> in
     * their constructors.
     */
    public DefaultMapBag() {
    }

    /**
     * Constructor that assigns the specified Map as the backing store.
     * The map must be empty.
     * 
     * @param map  the map to assign
     */
    protected DefaultMapBag(Map map) {
        setMap(map);
    }

    /**
     * Adds a new element to the bag by incrementing its count in the 
     * underlying map.
     *
     * @param object  the object to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    public boolean add(Object object) {
        return add(object, 1);
    }

    /**
     * Adds a new element to the bag by incrementing its count in the map.
     *
     * @param object  the object to search for
     * @param nCopies  the number of copies to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    public boolean add(Object object, int nCopies) {
        _mods++;
        if (nCopies > 0) {
            int count = (nCopies + getCount(object));
            _map.put(object, new Integer(count));
            _total += nCopies;
            return (count == nCopies);
        } else {
            return false;
        }
    }

    /**
     * Invokes {@link #add(Object)} for each element in the given collection.
     *
     * @param coll  the collection to add
     * @return <code>true</code> if this call changed the bag
     */
    public boolean addAll(Collection coll) {
        boolean changed = false;
        Iterator i = coll.iterator();
        while (i.hasNext()) {
            boolean added = add(i.next());
            changed = changed || added;
        }
        return changed;
    }

    /**
     * Clears the bag by clearing the underlying map.
     */
    public void clear() {
        _mods++;
        _map.clear();
        _total = 0;
    }

    /**
     * Determines if the bag contains the given element by checking if the
     * underlying map contains the element as a key.
     *
     * @param object  the object to search for
     * @return true if the bag contains the given element
     */
    public boolean contains(Object object) {
        return _map.containsKey(object);
    }

    /**
     * Determines if the bag contains the given elements.
     * 
     * @param coll  the collection to check against
     * @return <code>true</code> if the Bag contains all the collection
     */
    public boolean containsAll(Collection coll) {
        return containsAll(new HashBag(coll));
    }

    /**
     * Returns <code>true</code> if the bag contains all elements in
     * the given collection, respecting cardinality.
     * 
     * @param other  the bag to check against
     * @return <code>true</code> if the Bag contains all the collection
     */
    public boolean containsAll(Bag other) {
        boolean result = true;
        Iterator i = other.uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            boolean contains = getCount(current) >= ((Bag) other).getCount(current);
            result = result && contains;
        }
        return result;
    }

    /**
     * Returns true if the given object is not null, has the precise type 
     * of this bag, and contains the same number of occurrences of all the
     * same elements.
     *
     * @param object  the object to test for equality
     * @return true if that object equals this bag
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object != null &&
                object.getClass().equals(this.getClass()) &&
                ((DefaultMapBag) object)._map.equals(this._map));
    }

    /**
     * Returns the hash code of the underlying map.
     *
     * @return the hash code of the underlying map
     */
    public int hashCode() {
        return _map.hashCode();
    }

    /**
     * Returns true if the underlying map is empty.
     *
     * @return true if there are no elements in this bag
     */
    public boolean isEmpty() {
        return _map.isEmpty();
    }

    public Iterator iterator() {
        return new BagIterator(this, extractList().iterator());
    }

    static class BagIterator implements Iterator {
        private DefaultMapBag _parent = null;
        private Iterator _support = null;
        private Object _current = null;
        private int _mods = 0;

        public BagIterator(DefaultMapBag parent, Iterator support) {
            _parent = parent;
            _support = support;
            _current = null;
            _mods = parent.modCount();
        }

        public boolean hasNext() {
            return _support.hasNext();
        }

        public Object next() {
            if (_parent.modCount() != _mods) {
                throw new ConcurrentModificationException();
            }
            _current = _support.next();
            return _current;
        }

        public void remove() {
            if (_parent.modCount() != _mods) {
                throw new ConcurrentModificationException();
            }
            _support.remove();
            _parent.remove(_current, 1);
            _mods++;
        }
    }

    public boolean remove(Object object) {
        return remove(object, getCount(object));
    }

    public boolean remove(Object object, int nCopies) {
        _mods++;
        boolean result = false;
        int count = getCount(object);
        if (nCopies <= 0) {
            result = false;
        } else if (count > nCopies) {
            _map.put(object, new Integer(count - nCopies));
            result = true;
            _total -= nCopies;
        } else { // count > 0 && count <= i  
            // need to remove all
            result = (_map.remove(object) != null);
            _total -= count;
        }
        return result;
    }

    public boolean removeAll(Collection coll) {
        boolean result = false;
        if (coll != null) {
            Iterator i = coll.iterator();
            while (i.hasNext()) {
                boolean changed = remove(i.next(), 1);
                result = result || changed;
            }
        }
        return result;
    }

    /**
     * Remove any members of the bag that are not in the given
     * bag, respecting cardinality.
     *
     * @param coll  the collection to retain
     * @return true if this call changed the collection
     */
    public boolean retainAll(Collection coll) {
        return retainAll(new HashBag(coll));
    }

    /**
     * Remove any members of the bag that are not in the given
     * bag, respecting cardinality.
     * @see #retainAll(Collection)
     * 
     * @param other  the bag to retain
     * @return <code>true</code> if this call changed the collection
     */
    public boolean retainAll(Bag other) {
        boolean result = false;
        Bag excess = new HashBag();
        Iterator i = uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            int myCount = getCount(current);
            int otherCount = other.getCount(current);
            if (1 <= otherCount && otherCount <= myCount) {
                excess.add(current, myCount - otherCount);
            } else {
                excess.add(current, myCount);
            }
        }
        if (!excess.isEmpty()) {
            result = removeAll(excess);
        }
        return result;
    }

    /**
     * Returns an array of all of this bag's elements.
     *
     * @return an array of all of this bag's elements
     */
    public Object[] toArray() {
        return extractList().toArray();
    }

    /**
     * Returns an array of all of this bag's elements.
     *
     * @param array  the array to populate
     * @return an array of all of this bag's elements
     */
    public Object[] toArray(Object[] array) {
        return extractList().toArray(array);
    }

    /**
     * Returns the number of occurrence of the given element in this bag
     * by looking up its count in the underlying map.
     *
     * @param object  the object to search for
     * @return the number of occurrences of the object, zero if not found
     */
    public int getCount(Object object) {
        int result = 0;
        Integer count = MapUtils.getInteger(_map, object);
        if (count != null) {
            result = count.intValue();
        }
        return result;
    }

    /**
     * Returns an unmodifiable view of the underlying map's key set.
     *
     * @return the set of unique elements in this bag
     */
    public Set uniqueSet() {
        return UnmodifiableSet.decorate(_map.keySet());
    }

    /**
     * Returns the number of elements in this bag.
     *
     * @return the number of elements in this bag
     */
    public int size() {
        return _total;
    }

    /**
     * Actually walks the bag to make sure the count is correct and
     * resets the running total
     * 
     * @return the current total size
     */
    protected int calcTotalSize() {
        _total = extractList().size();
        return _total;
    }

    /**
     * Utility method for implementations to set the map that backs
     * this bag. Not intended for interactive use outside of
     * subclasses.
     */
    protected void setMap(Map map) {
        if (map == null || map.isEmpty() == false) {
            throw new IllegalArgumentException("The map must be non-null and empty");
        }
        _map = map;
    }

    /**
     * Utility method for implementations to access the map that backs
     * this bag. Not intended for interactive use outside of
     * subclasses.
     */
    protected Map getMap() {
        return _map;
    }

    /**
     * Create a list for use in iteration, etc.
     */
    private List extractList() {
        List result = new ArrayList();
        Iterator i = uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            for (int index = getCount(current); index > 0; index--) {
                result.add(current);
            }
        }
        return result;
    }

    /**
     * Return number of modifications for iterator.
     * 
     * @return the modification count
     */
    private int modCount() {
        return _mods;
    }

    /**
     * Implement a toString() method suitable for debugging.
     * 
     * @return a debugging toString
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        Iterator i = uniqueSet().iterator();
        while (i.hasNext()) {
            Object current = i.next();
            int count = getCount(current);
            buf.append(count);
            buf.append(":");
            buf.append(current);
            if (i.hasNext()) {
                buf.append(",");
            }
        }
        buf.append("]");
        return buf.toString();
    }
    
}
