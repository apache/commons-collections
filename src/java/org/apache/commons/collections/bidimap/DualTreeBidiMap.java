/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/bidimap/DualTreeBidiMap.java,v 1.1 2003/11/16 20:35:46 scolebourne Exp $
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
package org.apache.commons.collections.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.collections.iterators.OrderedMapIterator;
import org.apache.commons.collections.iterators.ResettableIterator;
import org.apache.commons.collections.map.AbstractSortedMapDecorator;
import org.apache.commons.collections.map.OrderedMap;

/**
 * Implementation of <code>BidiMap</code> that uses two <code>TreeMap</code> instances.
 * <p>
 * The setValue() method on iterators will succeed only if the new value being set is
 * not already in the bidimap.
 * <p>
 * When considering whether to use this class, the {@link TreeBidiMap} class should
 * also be considered. It implements the interface using a dedicated design, and does
 * not store each object twice, which can save on memory use.
 * 
 * @since Commons Collections 3.0
 * @version $Id: DualTreeBidiMap.java,v 1.1 2003/11/16 20:35:46 scolebourne Exp $
 * 
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public class DualTreeBidiMap extends AbstractDualBidiMap implements SortedBidiMap, Serializable {

    /** Ensure serialization compatability */
    private static final long serialVersionUID = 721969328361809L;
    /** The comparator to use */
    protected final Comparator comparator;
    
    /**
     * Creates an empty <code>DualTreeBidiMap</code>
     */
    public DualTreeBidiMap() {
        super();
        this.comparator = null;
    }

    /** 
     * Constructs a <code>DualTreeBidiMap</code> and copies the mappings from
     * specified <code>Map</code>.  
     *
     * @param map  the map whose mappings are to be placed in this map
     */
    public DualTreeBidiMap(Map map) {
        super();
        putAll(map);
        this.comparator = null;
    }

    /** 
     * Constructs a <code>DualTreeBidiMap</code> using the specified Comparator.
     *
     * @param map  the map whose mappings are to be placed in this map
     */
    public DualTreeBidiMap(Comparator comparator) {
        super();
        this.comparator = comparator;
    }

    /** 
     * Constructs a <code>HashBidiMap</code> that decorates the specified maps.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseBidiMap  the inverse BidiMap
     */
    protected DualTreeBidiMap(Map normalMap, Map reverseMap, BidiMap inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
        this.comparator = ((SortedMap) normalMap).comparator();
    }
    
    /**
     * Creates a new instance of the map used by the subclass to store data.
     * 
     * @return the map to be used for internal storage
     */
    protected Map createMap() {
        return new TreeMap(comparator);
    }

    /**
     * Creates a new instance of this object.
     * 
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseBidiMap  the inverse BidiMap
     * @return new bidi map
     */
    protected BidiMap createBidiMap(Map normalMap, Map reverseMap, BidiMap inverseMap) {
        return new DualTreeBidiMap(normalMap, reverseMap, inverseMap);
    }

    //-----------------------------------------------------------------------
    public Comparator comparator() {
        return ((SortedMap) maps[0]).comparator();
    }

    public Object firstKey() {
        return ((SortedMap) maps[0]).firstKey();
    }

    public Object lastKey() {
        return ((SortedMap) maps[0]).lastKey();
    }

    public Object nextKey(Object key) {
        if (isEmpty()) {
            return null;
        }
        if (maps[0] instanceof OrderedMap) {
            return ((OrderedMap) maps[0]).nextKey(key);
        }
        SortedMap sm = (SortedMap) maps[0];
        Iterator it = sm.tailMap(key).keySet().iterator();
        it.next();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public Object previousKey(Object key) {
        if (isEmpty()) {
            return null;
        }
        if (maps[0] instanceof OrderedMap) {
            return ((OrderedMap) maps[0]).previousKey(key);
        }
        SortedMap sm = (SortedMap) maps[0];
        SortedMap hm = sm.headMap(key);
        if (hm.isEmpty()) {
            return null;
        }
        return hm.lastKey();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an ordered map iterator.
     * <p>
     * This implementation copies the elements to an ArrayList in order to
     * provide the forward/backward behaviour.
     */
    public OrderedMapIterator orderedMapIterator() {
        return new BidiOrderedMapIterator(this);
    }

    public SortedBidiMap inverseSortedBidiMap() {
        return (SortedBidiMap) inverseBidiMap();
    }

    public OrderedBidiMap inverseOrderedBidiMap() {
        return (OrderedBidiMap) inverseBidiMap();
    }

    //-----------------------------------------------------------------------
    public SortedMap headMap(Object toKey) {
        SortedMap sub = ((SortedMap) maps[0]).headMap(toKey);
        return new ViewMap(this, sub);
    }

    public SortedMap tailMap(Object fromKey) {
        SortedMap sub = ((SortedMap) maps[0]).tailMap(fromKey);
        return new ViewMap(this, sub);
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        SortedMap sub = ((SortedMap) maps[0]).subMap(fromKey, toKey);
        return new ViewMap(this, sub);
    }
    
    //-----------------------------------------------------------------------
    protected static class ViewMap extends AbstractSortedMapDecorator {
        final DualTreeBidiMap bidi;
        
        protected ViewMap(DualTreeBidiMap bidi, SortedMap sm) {
            // the implementation is not great here...
            // use the maps[0] as the filtered map, but maps[1] as the full map
            // this forces containsValue and clear to be overridden
            super((SortedMap) bidi.createBidiMap(sm, bidi.maps[1], bidi.inverseBidiMap));
            this.bidi = (DualTreeBidiMap) map;
        }
        
        public boolean containsValue(Object value) {
            // override as default implementation jumps to [1]
            return bidi.maps[0].containsValue(value);
        }
        
        public void clear() {
            // override as default implementation jumps to [1]
            for (Iterator it = keySet().iterator(); it.hasNext();) {
                it.next();
                it.remove();
            }
        }
        
        public SortedMap headMap(Object toKey) {
            return new ViewMap(bidi, super.headMap(toKey));
        }

        public SortedMap tailMap(Object fromKey) {
            return new ViewMap(bidi, super.tailMap(fromKey));
        }

        public SortedMap subMap(Object fromKey, Object toKey) {
            return new ViewMap(bidi, super.subMap(fromKey, toKey));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class MapIterator.
     */
    protected static class BidiOrderedMapIterator implements OrderedMapIterator, ResettableIterator {
        
        protected final AbstractDualBidiMap map;
        protected ListIterator iterator;
        private Map.Entry last = null;
        
        protected BidiOrderedMapIterator(AbstractDualBidiMap map) {
            super();
            this.map = map;
            iterator = new ListIteratorWrapper(map.entrySet().iterator());
        }
        
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        public Object next() {
            last = (Map.Entry) iterator.next();
            return last.getKey();
        }
        
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }
        
        public Object previous() {
            last = (Map.Entry) iterator.previous();
            return last.getKey();
        }
        
        public void remove() {
            iterator.remove();
            map.remove(last.getKey());
        }
        
        public Object getKey() {
            if (last == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return last.getKey();
        }

        public Object getValue() {
            if (last == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return last.getValue();
        }
        
        public Object setValue(Object value) {
            if (last == null) {
                throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
            }
            if (map.maps[1].containsKey(value) &&
                map.maps[1].get(value) != last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            return map.put(last.getKey(), value);
        }
        
        public void reset() {
            iterator = new ListIteratorWrapper(map.entrySet().iterator());
            last = null;
        }
        
        public String toString() {
            if (last == null) {
                return "MapIterator[" + getKey() + "=" + getValue() + "]";
            } else {
                return "MapIterator[]";
            }
        }
    }
    
    // Serialization
    //-----------------------------------------------------------------------
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(maps[0]);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Map map = (Map) in.readObject();
        putAll(map);
    }

}
