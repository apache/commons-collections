/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/PredicatedMap.java,v 1.5 2003/09/05 03:35:07 psteitz Exp $
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

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;

/**
 * <code>PredicatedMap</code> decorates another <code>Map</code>
 * to validate that additions match a specified predicate.
 * <p>
 * If an object cannot be added to the map, an IllegalArgumentException
 * is thrown.</p>
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2003/09/05 03:35:07 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class PredicatedMap extends AbstractMapDecorator {

    /** The key predicate to use */
    protected final Predicate keyPredicate;
    /** The value predicate to use */
    protected final Predicate valuePredicate;

    /**
     * Factory method to create a predicated (validating) map.
     * <p>
     * If there are any elements already in the list being decorated, they
     * are validated.</p>
     * 
     * @param map  the map to decorate, must not be null
     * @param keyPredicate  the predicate to validate the keys, null means no check
     * @param valuePredicate  the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    public static Map decorate(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        return new PredicatedMap(map, keyPredicate, valuePredicate);
    }
    
    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @param keyPredicate  the predicate to validate the keys, null means no check
     * @param valuePredicate  the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    protected PredicatedMap(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        super(map);
        this.keyPredicate = keyPredicate;
        this.valuePredicate = valuePredicate;
        
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            validate(key, value);
        }
    }

    protected void validate(Object key, Object value) {
        if (keyPredicate != null && keyPredicate.evaluate(key) == false) {
            throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
        }
        if (valuePredicate != null && valuePredicate.evaluate(value) == false) {
            throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
        }
    }

    //-----------------------------------------------------------------------
    public Object put(Object key, Object value) {
        validate(key, value);
        return map.put(key, value);
    }

    public void putAll(Map mapToCopy) {
        Iterator it = mapToCopy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            validate(key, value);
        }
        map.putAll(mapToCopy);
    }

    public Set entrySet() {
        if (valuePredicate == null) {
            return map.entrySet();
        }
        return new PredicatedMapEntrySet(map.entrySet(), valuePredicate);
    }


    //-----------------------------------------------------------------------
    /**
     * Implementation of an entry set that checks (predicates) additions.
     */
    protected static class PredicatedMapEntrySet extends AbstractCollectionDecorator implements Set {
        
        /** The predicate to use */
        private final Predicate valuePredicate;

        protected PredicatedMapEntrySet(Set set, Predicate valuePred) {
            super(set);
            this.valuePredicate = valuePred;
        }

        public Iterator iterator() {
            return new PredicatedMapEntrySetIterator(collection.iterator(), valuePredicate);
        }
        
        public Object[] toArray() {
            Object[] array = collection.toArray();
            for (int i = 0; i < array.length; i++) {
                array[i] = new PredicatedMapEntry((Map.Entry) array[i], valuePredicate);
            }
            return array;
        }
        
        public Object[] toArray(Object array[]) {
            Object[] result = array;
            if (array.length > 0) {
                // we must create a new array to handle multi-threaded situations
                // where another thread could access data before we decorate it
                result = (Object[]) Array.newInstance(array.getClass().getComponentType(), 0);
            }
            result = collection.toArray(result);
            for (int i = 0; i < result.length; i++) {
                result[i] = new PredicatedMapEntry((Map.Entry) result[i], valuePredicate);
            }

            // check to see if result should be returned straight
            if (result.length > array.length) {
                return result;
            }

            // copy back into input array to fulfil the method contract
            System.arraycopy(result, 0, array, 0, result.length);
            if (array.length > result.length) {
                array[result.length] = null;
            }
            return array;
        }
    }

    /**
     * Implementation of an entry set iterator.
     */
    protected static class PredicatedMapEntrySetIterator extends AbstractIteratorDecorator {
        
        /** The predicate to use */
        private final Predicate valuePredicate;
        
        protected PredicatedMapEntrySetIterator(Iterator iterator, Predicate valuePredicate) {
            super(iterator);
            this.valuePredicate = valuePredicate;
        }
        
        public Object next() {
            Map.Entry entry = (Map.Entry) iterator.next();
            return new PredicatedMapEntry(entry, valuePredicate);
        }
    }

    /**
     * Implementation of a map entry that checks (predicates) additions.
     */
    protected static class PredicatedMapEntry extends AbstractMapEntryDecorator {

        /** The predicate to use */
        private final Predicate predicate;

        protected PredicatedMapEntry(Map.Entry entry, Predicate valuePredicate) {
            super(entry);
            this.predicate = valuePredicate;
        }

        public Object setValue(Object o) {
            if (predicate != null && predicate.evaluate(o) == false) {
                throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
            }
            return entry.setValue(o);
        }
    }

}
