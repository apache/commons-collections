/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/OrderedMap.java,v 1.5 2003/11/04 23:36:23 scolebourne Exp $
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
package org.apache.commons.collections.decorators;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.iterators.DefaultMapIterator;
import org.apache.commons.collections.iterators.MapIterator;
import org.apache.commons.collections.pairs.AbstractMapEntry;

/**
 * Decorates a <code>Map</code> to ensure that the order of addition is retained.
 * <p>
 * The order will be used via the iterators and toArray methods on the views.
 * The order is also returned by the <code>MapIterator</code>.
 * <p>
 * If an object is added to the Map for a second time, it will remain in the
 * original position in the iteration.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2003/11/04 23:36:23 $
 * 
 * @author Henri Yandell
 * @author Stephen Colebourne
 */
public class OrderedMap extends AbstractMapDecorator implements Map {

    /** Internal list to hold the sequence of objects */
    protected final List insertOrder = new ArrayList();

    /**
     * Factory method to create an ordered map.
     * <p>
     * An <code>ArrayList</code> is used to retain order.
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static Map decorate(Map map) {
        return new OrderedMap(map);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected OrderedMap(Map map) {
        super(map);
        insertOrder.addAll(getMap().keySet());
    }

    //-----------------------------------------------------------------------
    public Object put(Object key, Object value) {
        if (getMap().containsKey(key)) {
            // re-adding doesn't change order
            return getMap().put(key, value);
        } else {
            // first add, so add to both map and list
            Object result = getMap().put(key, value);
            insertOrder.add(key);
            return result;
        }
    }

    public void putAll(Map map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        Object result = getMap().remove(key);
        insertOrder.remove(key);
        return result;
    }

    public void clear() {
        getMap().clear();
        insertOrder.clear();
    }

    //-----------------------------------------------------------------------
    public MapIterator mapIterator() {
        return new DefaultMapIterator(this);
    }
    
    public Set keySet() {
        return new KeySetView(this);
    }

    public Collection values() {
        return new ValuesView(this);
    }

    public Set entrySet() {
        return new EntrySetView(this, this.insertOrder);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Returns the Map as a string.
     * 
     * @return the Map as a String
     */
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer();
        buf.append('{');
        boolean first = true;
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            buf.append(key == this ? "(this Map)" : key);
            buf.append('=');
            buf.append(value == this ? "(this Map)" : value);
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
        }
        buf.append('}');
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    static class ValuesView extends AbstractCollection {
        private final OrderedMap parent;

        ValuesView(OrderedMap parent) {
            super();
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        public void clear() {
            this.parent.clear();
        }

        public Iterator iterator() {
            return new AbstractIteratorDecorator(parent.entrySet().iterator()) {
                public Object next() {
                    return ((Map.Entry) iterator.next()).getValue();
                }
            };
        }
    }
    
    //-----------------------------------------------------------------------
    static class KeySetView extends AbstractSet {
        private final OrderedMap parent;

        KeySetView(OrderedMap parent) {
            super();
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean contains(Object value) {
            return this.parent.containsKey(value);
        }

        public void clear() {
            this.parent.clear();
        }

        public Iterator iterator() {
            return new AbstractIteratorDecorator(parent.entrySet().iterator()) {
                public Object next() {
                    return ((Map.Entry) super.next()).getKey();
                }
            };
        }
    }

    //-----------------------------------------------------------------------    
    static class EntrySetView extends AbstractSet {
        private final OrderedMap parent;
        private final List insertOrder;
        private Set entrySet;

        public EntrySetView(OrderedMap parent, List insertOrder) {
            super();
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        private Set getEntrySet() {
            if (entrySet == null) {
                entrySet = parent.getMap().entrySet();
            }
            return entrySet;
        }
        
        public int size() {
            return this.parent.size();
        }
        public boolean isEmpty() {
            return this.parent.isEmpty();
        }

        public boolean contains(Object obj) {
            return getEntrySet().contains(obj);
        }

        public boolean containsAll(Collection coll) {
            return getEntrySet().containsAll(coll);
        }

        public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Object key = ((Map.Entry) obj).getKey();
            if (parent.getMap().containsKey(key) == false) {
                return false;
            }
            parent.remove(key);
            return true;
        }

        public void clear() {
            this.parent.clear();
        }
        
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return getEntrySet().equals(obj);
        }
        
        public int hashCode() {
            return getEntrySet().hashCode();
        }

        public String toString() {
            return getEntrySet().toString();
        }
        
        public Iterator iterator() {
            return new OrderedIterator(parent, insertOrder);
        }
    }
    
    static class OrderedIterator extends AbstractIteratorDecorator {
        private final OrderedMap parent;
        private Object last = null;
        
        OrderedIterator(OrderedMap parent, List insertOrder) {
            super(insertOrder.iterator());
            this.parent = parent;
        }
        
        public Object next() {
            last = super.next();
            return new OrderedMapEntry(parent, last);
        }

        public void remove() {
            super.remove();
            parent.getMap().remove(last);
        }
    }
    
    static class OrderedMapEntry extends AbstractMapEntry {
        private final OrderedMap parent;
        
        OrderedMapEntry(OrderedMap parent, Object key) {
            super(key, null);
            this.parent = parent;
        }
        
        public Object getValue() {
            return parent.get(key);
        }

        public Object setValue(Object value) {
            return parent.getMap().put(key, value);
        }
    }

}
