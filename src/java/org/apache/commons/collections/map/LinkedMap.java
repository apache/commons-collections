/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/map/LinkedMap.java,v 1.1 2003/12/03 19:04:41 scolebourne Exp $
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
package org.apache.commons.collections.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.ResettableIterator;

/**
 * A <code>Map</code> implementation that maintains the order of the entries.
 * The order maintained is by insertion.
 * <p>
 * This implementation improves on the JDK1.4 LinkedHashMap by adding the 
 * {@link org.apache.commons.collections.iterators.MapIterator MapIterator}
 * functionality, additional convenience methods and allowing
 * bidirectional iteration. It also implements <code>OrderedMap</code>.
 * <p>
 * The <code>orderedMapIterator()</code> method provides direct access to a
 * bidirectional iterator. The iterators from the other views can also be cast
 * to <code>OrderedIterator</code> if required.
 * <p>
 * All the available iterators can be reset back to the start by casting to
 * <code>ResettableIterator</code> and calling <code>reset()</code>.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/12/03 19:04:41 $
 *
 * @author java util LinkedHashMap
 * @author Stephen Colebourne
 */
public class LinkedMap extends HashedMap implements OrderedMap {
    
    /** Serialisation version */
    static final long serialVersionUID = -1954063410665686469L;
    
    /** Header in the linked list */
    private transient LinkedEntry header;

    /**
     * Constructs a new empty map with default size and load factor.
     */
    public LinkedMap() {
        super();
    }

    /**
     * Constructs a new, empty map with the specified initial capacity. 
     *
     * @param initialCapacity  the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than one
     */
    public LinkedMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor. 
     *
     * @param initialCapacity  the initial capacity
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the initial capacity is less than one
     * @throws IllegalArgumentException if the load factor is less than one
     */
    public LinkedMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor copying elements from another map.
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     */
    public LinkedMap(Map map) {
        super(map);
    }

    /**
     * Initialise this subclass during construction.
     */
    protected void init() {
        header = new LinkedEntry(null, -1, null, null);
        header.before = header.after = header;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the map contains the specified value.
     * 
     * @param value  the value to search for
     * @return true if the map contains the value
     */
    public boolean containsValue(Object value) {
        // override uses faster iterator
        if (value == null) {
            for (LinkedEntry entry = header.after; entry != header; entry = entry.after) {
                if (entry.getValue() == null) {
                    return true;
                }
            }
        } else {
            for (LinkedEntry entry = header.after; entry != header; entry = entry.after) {
                if (isEqualValue(value, entry.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Clears the map, resetting the size to zero and nullifying references
     * to avoid garbage collection issues.
     */
    public void clear() {
        // override to reset the linked list
        super.clear();
        header.before = header.after = header;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the first key in the map, which is the most recently inserted.
     * 
     * @return the most recently inserted key
     */
    public Object firstKey() {
        if (size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return header.after.getKey();
    }

    /**
     * Gets the last key in the map, which is the first inserted.
     * 
     * @return the eldest key
     */
    public Object lastKey() {
        if (size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return header.before.getKey();
    }

    /**
     * Gets the next key in sequence.
     * 
     * @param key  the key to get after
     * @return the next key
     */
    public Object nextKey(Object key) {
        LinkedEntry entry = (LinkedEntry) getEntry(key);
        return (entry == null || entry.after == header ? null : entry.after.getKey());
    }

    /**
     * Gets the previous key in sequence.
     * 
     * @param key  the key to get before
     * @return the previous key
     */
    public Object previousKey(Object key) {
        LinkedEntry entry = (LinkedEntry) getEntry(key);
        return (entry == null || entry.before == header ? null : entry.before.getKey());
    }

    //-----------------------------------------------------------------------    
    /**
     * Creates an entry to store the data.
     * This implementation creates a LinkEntry instance in the linked list.
     * 
     * @param next  the next entry in sequence
     * @param hashCode  the hash code to use
     * @param key  the key to store
     * @param value  the value to store
     * @return the newly created entry
     */
    protected HashEntry createEntry(HashEntry next, int hashCode, Object key, Object value) {
        LinkedEntry entry = new LinkedEntry(next, hashCode, key, value);
        entry.after  = header;
        entry.before = header.before;
        header.before.after = entry;
        header.before = entry;
        return entry;
    }
    
    /**
     * Kills an entry ready for the garbage collector.
     * This implementation manages the linked list and prepares the
     * LinkEntry for garbage collection.
     * 
     * @param entry  the entry to destroy
     * @return the value from the entry
     */
    protected Object destroyEntry(HashEntry entry) {
        LinkedEntry link = (LinkedEntry) entry;
        link.before.after = link.after;
        link.after.before = link.before;
        link.next = null;
        link.after = null;
        link.before = null;
        return entry.value;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator over the map.
     * Changes made to the iterator affect this map.
     * <p>
     * A MapIterator returns the keys in the map. It also provides convenient
     * methods to get the key and value, and set the value.
     * It avoids the need to create an entrySet/keySet/values object.
     * It also avoids creating the Mep Entry object.
     * 
     * @return the map iterator
     */
    public MapIterator mapIterator() {
        if (size == 0) {
            return IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR;
        }
        return new LinkedMapIterator(this);
    }

    /**
     * Gets a bidirectional iterator over the map.
     * Changes made to the iterator affect this map.
     * <p>
     * A MapIterator returns the keys in the map. It also provides convenient
     * methods to get the key and value, and set the value.
     * It avoids the need to create an entrySet/keySet/values object.
     * It also avoids creating the Mep Entry object.
     * 
     * @return the map iterator
     */
    public OrderedMapIterator orderedMapIterator() {
        if (size == 0) {
            return IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR;
        }
        return new LinkedMapIterator(this);
    }

    /**
     * MapIterator
     */
    static class LinkedMapIterator extends LinkedIterator implements OrderedMapIterator {
        
        LinkedMapIterator(LinkedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }

        public Object previous() {
            return super.previousEntry().getKey();
        }

        public Object getKey() {
            HashEntry current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            return current.getKey();
        }

        public Object getValue() {
            HashEntry current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            return current.getValue();
        }

        public Object setValue(Object value) {
            HashEntry current = currentEntry();
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            return current.setValue(value);
        }
    }
    
    //-----------------------------------------------------------------------    
    /**
     * Creates an entry set iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the entrySet iterator
     */
    protected Iterator createEntrySetIterator() {
        if (size() == 0) {
            return IteratorUtils.EMPTY_ORDERED_ITERATOR;
        }
        return new EntrySetIterator(this);
    }

    /**
     * EntrySetIterator and MapEntry
     */
    static class EntrySetIterator extends LinkedIterator {
        
        EntrySetIterator(LinkedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry();
        }

        public Object previous() {
            return super.previousEntry();
        }
    }

    //-----------------------------------------------------------------------    
    /**
     * Creates a key set iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the keySet iterator
     */
    protected Iterator createKeySetIterator() {
        if (size() == 0) {
            return IteratorUtils.EMPTY_ORDERED_ITERATOR;
        }
        return new KeySetIterator(this);
    }

    /**
     * KeySetIterator
     */
    static class KeySetIterator extends EntrySetIterator {
        
        KeySetIterator(LinkedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry().getKey();
        }

        public Object previous() {
            return super.previousEntry().getKey();
        }
    }
    
    //-----------------------------------------------------------------------    
    /**
     * Creates a values iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the values iterator
     */
    protected Iterator createValuesIterator() {
        if (size() == 0) {
            return IteratorUtils.EMPTY_ORDERED_ITERATOR;
        }
        return new ValuesIterator(this);
    }

    /**
     * ValuesIterator
     */
    static class ValuesIterator extends LinkedIterator {
        
        ValuesIterator(LinkedMap map) {
            super(map);
        }

        public Object next() {
            return super.nextEntry().getValue();
        }

        public Object previous() {
            return super.previousEntry().getValue();
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * LinkEntry
     */
    protected static class LinkedEntry extends HashEntry {
        
        LinkedEntry before;
        LinkedEntry after;
        
        protected LinkedEntry(HashEntry next, int hashCode, Object key, Object value) {
            super(next, hashCode, key, value);
        }
    }
    
    /**
     * Base Iterator
     */
    protected static abstract class LinkedIterator
            implements OrderedIterator, ResettableIterator {
                
        private final LinkedMap map;
        private LinkedEntry current;
        private LinkedEntry next;
        private int expectedModCount;
        
        protected LinkedIterator(LinkedMap map) {
            super();
            this.map = map;
            this.next = map.header.after;
            this.expectedModCount = map.modCount;
        }

        public boolean hasNext() {
            return (next != map.header);
        }

        public boolean hasPrevious() {
            return (next.before != map.header);
        }

        protected LinkedEntry nextEntry() {
            if (map.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (next == map.header)  {
                throw new NoSuchElementException("No more elements in the iteration");
            }
            current = next;
            next = next.after;
            return current;
        }

        protected LinkedEntry previousEntry() {
            if (map.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            LinkedEntry previous = next.before;
            if (previous == map.header)  {
                throw new NoSuchElementException("No more elements in the iteration");
            }
            next = previous;
            current = previous;
            return current;
        }
        
        protected LinkedEntry currentEntry() {
            return current;
        }
        
        public void remove() {
            if (current == null) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            if (map.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            map.remove(current.getKey());
            current = null;
            expectedModCount = map.modCount;
        }
        
        public void reset() {
            current = null;
            next = map.header.after;
        }

        public String toString() {
            if (current != null) {
                return "Iterator[" + current.getKey() + "=" + current.getValue() + "]";
            } else {
                return "Iterator[]";
            }
        }
    }
    
}
