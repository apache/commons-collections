/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/HashBidiMap.java,v 1.4 2003/10/05 20:40:52 scolebourne Exp $
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.decorators.AbstractCollectionDecorator;
import org.apache.commons.collections.decorators.AbstractIteratorDecorator;
import org.apache.commons.collections.decorators.AbstractMapEntryDecorator;

/**
 * Default implementation of <code>BidiMap</code>.
 * 
 * @since Commons Collections 3.0
 * @version $Id: HashBidiMap.java,v 1.4 2003/10/05 20:40:52 scolebourne Exp $
 * 
 * @author Matthew Hawthorne
 */
public class HashBidiMap implements BidiMap {

    /**
     * Delegate map array.  The first map contains standard entries, and the 
     * second contains inverses.
     */
    protected final Map[] maps = new Map[2];
    /**
     * Inverse view of this map.
     */
    protected BidiMap inverseBidiMap = null;
    /**
     * View of the keys.
     */
    protected Set keySet = null;
    /**
     * View of the values.
     */
    protected Collection values = null;
    /**
     * View of the entries.
     */
    protected Set entrySet = null;

    /**
     * Creates an empty <code>HashBidiMap</code>
     */
    public HashBidiMap() {
        super();
        maps[0] = new HashMap();
        maps[1] = new HashMap();
    }

    /** 
     * Constructs a <code>HashBidiMap</code> and copies the mappings from
     * specified <code>Map</code>.  
     *
     * @param map  the map whose mappings are to be placed in this map
     */
    public HashBidiMap(Map map) {
        super();
        maps[0] = new HashMap();
        maps[1] = new HashMap();
        putAll(map);
    }

    /** 
     * Constructs a <code>HashBidiMap</code> that decorates the specified maps.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseBidiMap  the inverse BidiMap
     */
    protected HashBidiMap(Map normalMap, Map reverseMap, BidiMap inverseBidiMap) {
        super();
        maps[0] = normalMap;
        maps[1] = reverseMap;
        this.inverseBidiMap = inverseBidiMap;
    }

    // Map delegation
    //-----------------------------------------------------------------------
    public Object get(Object key) {
        return maps[0].get(key);
    }

    public int size() {
        return maps[0].size();
    }

    public boolean isEmpty() {
        return maps[0].isEmpty();
    }

    public boolean containsKey(Object key) {
        return maps[0].containsKey(key);
    }

    public boolean equals(Object obj) {
        return maps[0].equals(obj);
    }

    public int hashCode() {
        return maps[0].hashCode();
    }

    public String toString() {
        return maps[0].toString();
    }

    // BidiMap changes
    //-----------------------------------------------------------------------
    public Object put(Object key, Object value) {
        if (maps[0].containsKey(key)) {
            maps[1].remove(maps[0].get(key));
        }
        if (maps[1].containsKey(value)) {
            maps[0].remove(maps[1].get(value));
        }
        final Object obj = maps[0].put(key, value);
        maps[1].put(value, key);
        return obj;
    }
    
    public void putAll(Map map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        Object value = null;
        if (maps[0].containsKey(key)) {
            value = maps[0].remove(key);
            maps[1].remove(value);
        }
        return value;
    }

    public void clear() {
        maps[0].clear();
        maps[1].clear();
    }

    public boolean containsValue(Object value) {
        return maps[1].containsKey(value);
    }

    // BidiMap
    //-----------------------------------------------------------------------
    public Object getKey(Object value) {
        return maps[1].get(value);
    }

    public Object removeKey(Object value) {
        Object key = null;
        if (maps[1].containsKey(value)) {
            key = maps[1].remove(value);
            maps[0].remove(key);
        }
        return key;
    }

    public BidiMap inverseBidiMap() {
        if (inverseBidiMap == null) {
            inverseBidiMap = new HashBidiMap(maps[1], maps[0], this);
        }
        return inverseBidiMap;
    }

    // Map views
    //-----------------------------------------------------------------------
    public Set keySet() {
        if (keySet == null) {
            keySet = new KeySet(this);
        }
        return keySet;
    }

    public Collection values() {
        if (values == null) {
            values = new Values(this);
        }
        return values;
    }

    public Set entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet(this);
        }
        return entrySet;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Inner class View.
     */
    protected static abstract class View extends AbstractCollectionDecorator {
        
        protected final HashBidiMap map;
        
        protected View(Collection coll, HashBidiMap map) {
            super(coll);
            this.map = map;
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

        public void clear() {
            map.clear();
        }
    }
    
    /**
     * Inner class KeySet.
     */
    protected static class KeySet extends View implements Set {
        
        protected KeySet(HashBidiMap map) {
            super(map.maps[0].keySet(), map);
        }

        public Iterator iterator() {
            return new AbstractIteratorDecorator(super.iterator()) {
                private Object last;
                
                public Object next() {
                    last = super.next();
                    return last;
                }

                public void remove() {
                    Object value = map.maps[0].get(last);
                    super.remove();
                    map.maps[1].remove(value);
                }
            };
        }
        
        public boolean remove(Object key) {
            if (contains(key)) {
                Object value = map.maps[0].remove(key);
                map.maps[1].remove(value);
                return true;
            }
            return false;
        }
    }
    
    /**
     * Inner class Values.
     */
    protected static class Values extends View {
        
        protected Values(HashBidiMap map) {
            super(map.maps[0].values(), map);
        }

        public Iterator iterator() {
            return new AbstractIteratorDecorator(super.iterator()) {
                private Object last;
                
                public Object next() {
                    last = super.next();
                    return last;
                }

                public void remove() {
                    super.remove();
                    map.maps[1].remove(last);
                }
            };
        }
        
        public boolean remove(Object value) {
            if (contains(value)) {
                Object key = map.maps[1].remove(value);
                map.maps[0].remove(key);
                return true;
            }
            return false;
        }
        
    }
    
    /**
     * Inner class EntrySet.
     */
    protected static class EntrySet extends View implements Set {
        
        protected EntrySet(HashBidiMap map) {
            super(map.maps[0].entrySet(), map);
        }

        public Iterator iterator() {
            return new AbstractIteratorDecorator(super.iterator()) {
                private Map.Entry last;
                
                public Object next() {
                    last = new MapEntry((Map.Entry) super.next(), map);
                    return last;
                }

                public void remove() {
                    super.remove();
                    map.maps[0].remove(last.getValue());
                }
            };
        }
        
        public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            if (map.containsKey(entry.getKey())) {
                Object value = map.maps[0].remove(entry.getKey());
                map.maps[1].remove(value);
                return true;
            }
            return false;
        }
    }
    
    protected static class MapEntry extends AbstractMapEntryDecorator {
        
        protected final HashBidiMap map;
        
        protected MapEntry(Map.Entry entry, HashBidiMap map) {
            super(entry);
            this.map = map;
        }
        
        public Object setValue(Object value) {
            final Object oldValue = super.setValue(value);

            // Gets old key and pairs with new value
            final Object inverseKey = map.maps[1].remove(oldValue);
            map.maps[1].put(value, inverseKey);

            return oldValue;
        }
    }
    
}
