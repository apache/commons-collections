/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.keyvalue.AbstractMapEntryDecorator;

/**
 * Decorates another <code>Map</code> to validate that additions
 * match a specified predicate.
 * <p>
 * If an object cannot be added to the map, an IllegalArgumentException
 * is thrown.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.8 $ $Date: 2004/04/09 09:43:09 $
 * 
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class PredicatedMap
        extends AbstractMapDecorator
        implements Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 7412622456128415156L;

    /** The key predicate to use */
    protected final Predicate keyPredicate;
    /** The value predicate to use */
    protected final Predicate valuePredicate;

    /**
     * Factory method to create a predicated (validating) map.
     * <p>
     * If there are any elements already in the list being decorated, they
     * are validated.
     * 
     * @param map  the map to decorate, must not be null
     * @param keyPredicate  the predicate to validate the keys, null means no check
     * @param valuePredicate  the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    public static Map decorate(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        return new PredicatedMap(map, keyPredicate, valuePredicate);
    }

    //-----------------------------------------------------------------------
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
    /**
     * Write the map out using a custom routine.
     * 
     * @param out  the output stream
     * @throws IOException
     * @since Commons Collections 3.1
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     * 
     * @param in  the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     * @since Commons Collections 3.1
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map) in.readObject();
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
    static class PredicatedMapEntrySet extends AbstractCollectionDecorator implements Set {
        
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
    static class PredicatedMapEntrySetIterator extends AbstractIteratorDecorator {
        
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
    static class PredicatedMapEntry extends AbstractMapEntryDecorator {

        /** The predicate to use */
        private final Predicate predicate;

        protected PredicatedMapEntry(Map.Entry entry, Predicate valuePredicate) {
            super(entry);
            this.predicate = valuePredicate;
        }

        public Object setValue(Object obj) {
            if (predicate != null && predicate.evaluate(obj) == false) {
                throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
            }
            return entry.setValue(obj);
        }
    }

}
