/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/OrderedMap.java,v 1.1 2003/10/03 06:24:13 bayard Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.DefaultMapEntry;

/**
 * Decorates a <code>Map</code> to ensure that the order of addition
 * is retained and used by the values and keySet iterators.
 * <p>
 * If an object is added to the Map for a second time, it will remain in the
 * original position in the iteration.
 * <p>
 * The order can be observed via the iterator or toArray methods.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/10/03 06:24:13 $
 * 
 * @author Henri Yandell
 */
public class OrderedMap extends AbstractMapDecorator implements Map {

    /** Internal list to hold the sequence of objects */
    protected final List insertOrder = new ArrayList();

    /**
     * Factory method to create an ordered map.
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
        insertOrder.addAll( getMap().keySet() );
    }

    //-----------------------------------------------------------------------
    public void clear() {
        getMap().clear();
        insertOrder.clear();
    }

    public void putAll(Map m) {
        for (Iterator it = m.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            put(entry.getKey(), entry.getValue());
        }
    }

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

    public Object remove(Object key) {
        Object result = getMap().remove(key);
        insertOrder.remove(key);
        return result;
    }

    //// From here on down, this becomes hell.

    public Set keySet() {
        // TODO: calling remove on the Set needs to remove from this Map
        // Iterator.remove, Set.remove, removeAll retainAll, and clear 

        return new KeyView( this, this.insertOrder );

        /* OLD
        Set set = new java.util.HashSet( insertOrder.size() );
        set = OrderedSet.decorate( set );
        for (Iterator it = insertOrder.iterator(); it.hasNext();) {
            set.add( it.next() );
        }
        return set;
        */
    }

    public Collection values() {
        // TODO: calling remove on the Collection needs to remove from this Map
        // Iterator.remove, Collection.remove, removeAll, retainAll and clear
        return new ValuesView( this, this.insertOrder );
    }

    // QUERY: Should a change of value change insertion order?
    public Set entrySet() {
        // TODO: calling remove on the Set needs to remove from this Map
        // Iterator.remove, Set.remove, removeAll, retainAll and clear
        return new EntrySetView( this, this.insertOrder );
    }

    // TODO: Code a toString up. 
    //       It needs to retain the right order, else it will 
    //       look peculiar.
    public String toString() {
        return super.toString();
    }

    // class for handling the values() method's callback to this Map
    // THESE NEED UNIT TESTING as their own collections
    class ValuesView implements Collection {
        private OrderedMap parent;
        private List insertOrder;

        ValuesView(OrderedMap parent, List insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        // slow to call
        Collection _values() {
            Iterator keys = this.insertOrder.iterator();
            ArrayList list = new ArrayList( insertOrder.size() );
            while( keys.hasNext() ) {
                list.add( this.parent.getMap().get( keys.next() ) );
            }
            return list;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean isEmpty() {
            return this.parent.isEmpty();
        }

        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        public Iterator iterator() {
            // TODO: Allow this to be backed
            return _values().iterator();
//            return new ValuesViewIterator( who? );
        }

        public Object[] toArray() {
            return _values().toArray();
        }

        public Object[] toArray(Object[] array) {
            return _values().toArray(array);
        }

        public boolean add(Object obj) {
            throw new UnsupportedOperationException("Not allowed. ");
        }

        public boolean remove(Object obj) {
            // who?? which value do I choose? first one?
            for(Iterator itr = this.insertOrder.iterator(); itr.hasNext(); ) {
                Object key = itr.next();
                Object value = this.parent.get(key);

                // also handles null
                if(value == obj) {
                    return (this.parent.remove(key) != null);
                } 

                if( (value != null) && value.equals(obj) ) {
                    return (this.parent.remove(key) != null);
                }
            }

            return false;
        }

        public boolean containsAll(Collection coll) {
            // TODO: What does Collection spec say about null/empty?
            for(Iterator itr = coll.iterator(); itr.hasNext(); ) {
                if( !this.parent.containsValue( itr.next() ) ) {
                    return false;
                }
            }
            return true;
        }

        public boolean addAll(Collection coll) {
            throw new UnsupportedOperationException("Not allowed. ");
        }

        public boolean removeAll(Collection coll) {
            // not transactional. No idea if removeAll's boolean
            // reply is meant to be
            boolean ret = false;
            for( Iterator itr = coll.iterator(); itr.hasNext(); ) {
                ret = ret && remove(itr.next());
            }
            return ret;
        }

        public boolean retainAll(Collection coll) {
            // transactional?
            boolean ret = false;

            for( Iterator itr = this.insertOrder.iterator(); itr.hasNext(); ) {
                Object key = itr.next();
                Object value = this.parent.get(key);
                if( coll.contains(value) ) {
                    // retain
                } else {
                    ret = ret && (parent.remove(key) != null);
                }
            }

            return ret;
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean equals(Object obj) {
            // exactly what to do here?
            return super.equals(obj);
        }
        public int hashCode() {
            return _values().hashCode();
        }

        public String toString() {
            return _values().toString();
        }
    }

    class KeyView implements Set {

        private OrderedMap parent;
        private List insertOrder;

        public KeyView(OrderedMap parent, List insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        public int size() {
            return this.parent.size();
        }
        public boolean isEmpty() {
            return this.parent.isEmpty();
        }
        public boolean contains(Object obj) {
            return this.parent.containsKey(obj);
        }
        public Iterator iterator() {
            // TODO: Needs to return a KeyViewIterator, which 
            //       removes from this and from the Map
            return this.insertOrder.iterator();
        }
        public Object toArray()[] {
            return this.insertOrder.toArray();
        }
        public Object toArray(Object[] array)[] {
            return this.insertOrder.toArray(array);
        }
        public boolean add(Object obj) {
            throw new UnsupportedOperationException("Not allowed. ");
        }
        public boolean remove(Object obj) {
            return (this.parent.remove(obj) != null);
        }
        public boolean containsAll(Collection coll) {
            // TODO: What does Collection spec say about null/empty?
            for(Iterator itr = coll.iterator(); itr.hasNext(); ) {
                if( !this.parent.containsKey( itr.next() ) ) {
                    return false;
                }
            }
            return true;
        }
        public boolean addAll(Collection coll) {
            throw new UnsupportedOperationException("Not allowed. ");
        }
        public boolean removeAll(Collection coll) {
            // not transactional. No idea if removeAll's boolean
            // reply is meant to be
            boolean ret = false;
            for( Iterator itr = coll.iterator(); itr.hasNext(); ) {
                ret = ret && remove(itr.next());
            }
            return ret;
        }
        public boolean retainAll(Collection coll) {
            // transactional?
            boolean ret = false;

            for( Iterator itr = this.insertOrder.iterator(); itr.hasNext(); ) {
                Object key = itr.next();
                if( coll.contains(key) ) {
                    // retain
                } else {
                    ret = ret && (parent.remove(key) != null);
                }
            }

            return ret;
        }
        public void clear() {
            this.parent.clear();
        }
        public boolean equals(Object obj) {
            // exactly what to do here?
            return super.equals(obj);
        }
        public int hashCode() {
            return this.parent.getMap().keySet().hashCode();
        }

        public String toString() {
            return this.insertOrder.toString();
        }
    }

    class EntrySetView implements Set {

        private OrderedMap parent;
        private List insertOrder;

        public EntrySetView(OrderedMap parent, List insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        public int size() {
            return this.parent.size();
        }
        public boolean isEmpty() {
            return this.parent.isEmpty();
        }

        public boolean contains(Object obj) {
            if(obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) obj;
                if( this.parent.containsKey(entry.getKey()) ) {
                    Object value = this.parent.get(entry.getKey());
                    if( obj == null && value == null ) {
                        return true;
                    } else {
                        return obj.equals(value);
                    }
                } else {
                    return false;
                }
            } else {
                throw new IllegalArgumentException("Parameter must be a Map.Entry");
            }
        }
        // tmp
        public Set _entries() {
            // TODO: Needs to return a EntrySetViewIterator, which 
            //       removes from this and from the Map
            Set set = new java.util.HashSet( this.insertOrder.size() );
            set = OrderedSet.decorate( set );
            for (Iterator it = insertOrder.iterator(); it.hasNext();) {
                Object key = it.next();
                set.add( new DefaultMapEntry( key, getMap().get( key ) ) );
            }
            return set;
        }
        public Iterator iterator() {
            return _entries().iterator();
        }
        public Object toArray()[] {
            return _entries().toArray();
        }
        public Object toArray(Object[] array)[] {
            return _entries().toArray(array);
        }
        public boolean add(Object obj) {
            throw new UnsupportedOperationException("Not allowed. ");
        }
        public boolean remove(Object obj) {
            if(obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) obj;
                return (this.parent.remove(entry.getKey()) != null);
            } else {
                throw new IllegalArgumentException("Parameter must be a Map.Entry");
            }
        }
        // need to decide on IllegalArgument or ClassCast in this class
        // when not Map.Entry
        public boolean containsAll(Collection coll) {
            // TODO: What does Collection spec say about null/empty?
            for(Iterator itr = coll.iterator(); itr.hasNext(); ) {
                Map.Entry entry = (Map.Entry) itr.next();
                if( !this.parent.containsKey( entry.getKey() ) ) {
                    return false;
                }
            }
            return true;
        }
        public boolean addAll(Collection coll) {
            throw new UnsupportedOperationException("Not allowed. ");
        }
        public boolean removeAll(Collection coll) {
            // not transactional. No idea if removeAll's boolean
            // reply is meant to be
            boolean ret = false;
            for( Iterator itr = coll.iterator(); itr.hasNext(); ) {
                Map.Entry entry = (Map.Entry) itr.next();
                ret = ret && remove( entry.getKey() );
            }
            return ret;
        }
        public boolean retainAll(Collection coll) {
            // transactional?
            boolean ret = false;

            for( Iterator itr = this.insertOrder.iterator(); itr.hasNext(); ) {
                Map.Entry entry = (Map.Entry) itr.next();
                Object key = entry.getKey();
                if( coll.contains(key) ) {
                    // retain
                } else {
                    ret = ret && (parent.remove(key) != null);
                }
            }

            return ret;
        }
        public void clear() {
            this.parent.clear();
        }
        public boolean equals(Object obj) {
            // exactly what to do here?
            return super.equals(obj);
        }
        public int hashCode() {
            return this.parent.getMap().entrySet().hashCode();
        }

        public String toString() {
            return this._entries().toString();
        }
    }

}
