/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/map/FixedSizeMap.java,v 1.2 2003/12/11 22:55:25 scolebourne Exp $
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
package org.apache.commons.collections.map;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.BoundedMap;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * Decorates another <code>Map</code> to fix the size, preventing add/remove.
 * <p>
 * Any action that would change the size of the map is disallowed.
 * The put method is allowed to change the value associated with an existing
 * key however.
 * <p>
 * If trying to remove or clear the map, an UnsupportedOperationException is
 * thrown. If trying to put a new mapping into the map, an 
 * IllegalArgumentException is thrown. This is because the put method can 
 * succeed if the mapping's key already exists in the map, so the put method
 * is not always unsupported.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/12/11 22:55:25 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class FixedSizeMap extends AbstractMapDecorator
        implements Map, BoundedMap {

    /**
     * Factory method to create a fixed size map.
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static Map decorate(Map map) {
        return new FixedSizeMap(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected FixedSizeMap(Map map) {
        super(map);
    }
    
    //-----------------------------------------------------------------------
    public Object put(Object key, Object value) {
        if (map.containsKey(key) == false) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return map.put(key, value);
    }

    public void putAll(Map mapToCopy) {
        for (Iterator it = mapToCopy.keySet().iterator(); it.hasNext(); ) {
            if (mapToCopy.containsKey(it.next()) == false) {
                throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
            }
        }
        map.putAll(mapToCopy);
    }

    public void clear() {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public Set entrySet() {
        Set set = map.entrySet();
        return UnmodifiableSet.decorate(set);
    }

    public Set keySet() {
        Set set = map.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection values() {
        Collection coll = map.values();
        return UnmodifiableCollection.decorate(coll);
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }
   
}
