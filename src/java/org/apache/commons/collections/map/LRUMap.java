/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/map/LRUMap.java,v 1.2 2003/12/07 23:59:13 scolebourne Exp $
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A <code>Map</code> implementation with a fixed maximum size which removes
 * the least recently used entry if an entry is added when full.
 * <p>
 * The least recently used algorithm works on the get and put operations only.
 * Iteration of any kind, including setting the value by iteration, does not
 * change the order. Queries such as containsKey and containsValue or access
 * via views also do not change the order.
 * <p>
 * The map implements <code>OrderedMap</code> and entries may be queried using
 * the bidirectional <code>OrderedMapIterator</code>. The order returned is
 * most recently used to least recently used. Iterators from map views can 
 * also be cast to <code>OrderedIterator</code> if required.
 * <p>
 * All the available iterators can be reset back to the start by casting to
 * <code>ResettableIterator</code> and calling <code>reset()</code>.
 * <p>
 * NOTE: The order of the map has changed from the previous version located
 * in the main collections package. The map is now ordered most recently used
 * to least recently used.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/12/07 23:59:13 $
 *
 * @author James Strachan
 * @author Morgan Delagrange
 * @author Stephen Colebourne
 */
public class LRUMap extends AbstractLinkedMap implements Serializable, Cloneable {
    
    /** Serialisation version */
    static final long serialVersionUID = -612114643488955218L;
    /** Default maximum size */
    protected static final int DEFAULT_MAX_SIZE = 100;
    
    /** Maximum size */
    private transient int maxSize;

    /**
     * Constructs a new empty map with a maximum size of 100.
     */
    public LRUMap() {
        this(DEFAULT_MAX_SIZE, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new, empty map with the specified maximum size.
     *
     * @param maxSize  the maximum size of the map
     * @throws IllegalArgumentException if the maximum size is less than one
     */
    public LRUMap(int maxSize) {
        this(maxSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor. 
     *
     * @param maxSize  the maximum size of the map, -1 for no limit,
     * @param loadFactor  the load factor
     * @throws IllegalArgumentException if the maximum size is less than one
     * @throws IllegalArgumentException if the load factor is less than zero
     */
    public LRUMap(int maxSize, float loadFactor) {
        super((maxSize < 1 ? DEFAULT_CAPACITY : maxSize), loadFactor);
        if (maxSize < 1) {
            throw new IllegalArgumentException("LRUMap max size must be greater than 0");
        }
        this.maxSize = maxSize;
    }

    /**
     * Constructor copying elements from another map.
     * <p>
     * The maximum size is set from the map's size.
     *
     * @param map  the map to copy
     * @throws NullPointerException if the map is null
     * @throws IllegalArgumentException if the map is empty
     */
    public LRUMap(Map map) {
        this(map.size(), DEFAULT_LOAD_FACTOR);
        putAll(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the key specified.
     * <p>
     * This operation changes the position of the key in the map to the
     * most recently used position (first).
     * 
     * @param key  the key
     * @return the mapped value, null if no match
     */
    public Object get(Object key) {
        LinkEntry entry = (LinkEntry) getEntry(key);
        if (entry == null) {
            return null;
        }
        moveFirst(entry);
        return entry.getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Updates an existing key-value mapping.
     * This implementation moves the updated entry to the top of the list.
     * 
     * @param entry  the entry to update
     * @param newValue  the new value to store
     * @return value  the previous value
     */
    protected void moveFirst(LinkEntry entry) {
        if (entry.before != header) {
            modCount++;
            // remove
            entry.after.before = entry.before;
            entry.before.after = entry.after;
            // add first
            entry.before = header;
            entry.after = header.after;
            header.after.before = entry;
            header.after = entry;
        }
    }
    
    /**
     * Updates an existing key-value mapping.
     * This implementation moves the updated entry to the top of the list.
     * 
     * @param entry  the entry to update
     * @param newValue  the new value to store
     * @return value  the previous value
     */
    protected void updateEntry(HashEntry entry, Object newValue) {
        moveFirst((LinkEntry) entry);  // handles modCount
        entry.setValue(newValue);
    }
    
    /**
     * Adds a new key-value mapping into this map.
     * This implementation checks the LRU size and determines whether to
     * discard an entry or not.
     * 
     * @param hashIndex  the index into the data array to store at
     * @param hashCode  the hash code of the key to add
     * @param key  the key to add
     * @param value  the value to add
     * @return the value previously mapped to this key, null if none
     */
    protected void addMapping(int hashIndex, int hashCode, Object key, Object value) {
        if (size >= maxSize && removeLRU(header.before)) {
            LinkEntry entry = header.before;
            // remove from current location
            int removeIndex = hashIndex(entry.hashCode, data.length);
            HashEntry loop = data[removeIndex];
            HashEntry previous = null;
            while (loop != entry) {
                previous = loop;
                loop = loop.next;
            }
            modCount++;
            removeEntry(entry, removeIndex, previous);
            reuseEntry(entry, hashIndex, hashCode, key, value);
            addEntry(entry, hashIndex);
            
        } else {
            super.addMapping(hashIndex, hashCode, key, value);
        }
    }
    
    /**
     * Adds a new entry into this map using access order.
     * <p>
     * This implementation adds the entry to the data storage table and
     * to the start of the linked list.
     * 
     * @param entry  the entry to add
     * @param hashIndex  the index into the data array to store at
     */
    protected void addEntry(HashEntry entry, int hashIndex) {
        LinkEntry link = (LinkEntry) entry;
        link.before = header;
        link.after = header.after;
        header.after.before = link;
        header.after = link;
        data[hashIndex] = entry;
    }
    
    /**
     * Subclass method to control removal of the least recently used entry from the map.
     * <p>
     * This method exists for subclasses to override. A subclass may wish to
     * provide cleanup of resources when an entry is removed. For example:
     * <pre>
     * protected boolean removeLRU(LinkEntry entry) {
     *   releaseResources(entry.getValue());  // release resources held by entry
     *   return true;  // actually delete entry
     * }
     * </pre>
     * <p>
     * Alternatively, a subclass may choose to not remove the entry or selectively
     * keep certain LRU entries. For example:
     * <pre>
     * protected boolean removeLRU(LinkEntry entry) {
     *   if (entry.getKey().toString().startsWith("System.")) {
     *     return false;  // entry not removed from LRUMap
     *   } else {
     *     return true;  // actually delete entry
     *   }
     * }
     * </pre>
     * Note that the effect of not removing an LRU is for the Map to exceed the maximum size.
     * 
     * @param entry  the entry to be removed
     */
    protected boolean removeLRU(LinkEntry entry) {
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns true if this map is full and no new mappings can be added.
     *
     * @return <code>true</code> if the map is full
     */
    public boolean isFull() {
        return (size >= maxSize);
    }

    /**
     * Gets the maximum size of the map (the bound).
     *
     * @return the maximum number of elements the map can hold
     */
    public int maxSize() {
        return maxSize;
    }

    //-----------------------------------------------------------------------
    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    public Object clone() {
        return super.clone();
    }
    
    /**
     * Write the map out using a custom routine.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    /**
     * Read the map in using a custom routine.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }
    
    /**
     * Writes the data necessary for <code>put()</code> to work in deserialization.
     */
    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeInt(maxSize);
        super.doWriteObject(out);
    }

    /**
     * Reads the data necessary for <code>put()</code> to work in the superclass.
     */
    protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        maxSize = in.readInt();
        super.doReadObject(in);
    }
    
}
