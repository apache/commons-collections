/*
 *  Copyright 2004 The Apache Software Foundation
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

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections.set.AbstractSetDecorator;

/**
 * An abstract base class that simplifies the task of creating map decorators.
 * <p>
 * The Map API is very difficult to decorate correctly, and involves implementing
 * lots of different classes. This class exists to provide a simpler API.
 * <p>
 * Special hook methods are provided that are called when objects are added to
 * the map. By overriding these methods, the input can be validated or manipulated.
 * In addition to the main map methods, the entrySet is also affected, which is
 * the hardest part of writing map implementations.
 *
 * @since Commons Collections 3.1
 * @version $Revision: 1.1 $ $Date: 2004/05/03 21:48:49 $
 * 
 * @author Stephen Colebourne
 */
public class AbstractInputCheckedMapDecorator
        extends AbstractMapDecorator {

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    protected AbstractInputCheckedMapDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected AbstractInputCheckedMapDecorator(Map map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Hook method called when a key is being added to the map using
     * <code>put</code> or <code>putAll</code>.
     * <p>
     * An implementation may validate the key and throw an exception
     * or it may transform the key into another object.
     * The key may already exist in the map.
     * <p>
     * This implementation returns the input key.
     * 
     * @param key  the key to check
     * @throws UnsupportedOperationException if the map may not be changed by put/putAll
     * @throws IllegalArgumentException if the specified key is invalid
     * @throws ClassCastException if the class of the specified key is invalid
     * @throws NullPointerException if the specified key is null and nulls are invalid
     */
    protected Object checkPutKey(Object key) {
        return key;
    }

    /**
     * Hook method called when a new value is being added to the map using
     * <code>put</code> or <code>putAll</code>.
     * <p>
     * An implementation may validate the value and throw an exception
     * or it may transform the value into another object.
     * <p>
     * This implementation returns the input value.
     * 
     * @param value  the value to check
     * @throws UnsupportedOperationException if the map may not be changed by put/putAll
     * @throws IllegalArgumentException if the specified value is invalid
     * @throws ClassCastException if the class of the specified value is invalid
     * @throws NullPointerException if the specified value is null and nulls are invalid
     */
    protected Object checkPutValue(Object value) {
        return value;
    }

    /**
     * Hook method called when a value is being set using <code>setValue</code>.
     * <p>
     * An implementation may validate the value and throw an exception
     * or it may transform the value into another object.
     * <p>
     * This implementation returns the input value.
     * 
     * @param value  the value to check
     * @throws UnsupportedOperationException if the map may not be changed by setValue
     * @throws IllegalArgumentException if the specified value is invalid
     * @throws ClassCastException if the class of the specified value is invalid
     * @throws NullPointerException if the specified value is null and nulls are invalid
     */
    protected Object checkSetValue(Object value) {
        return value;
    }

    /**
     * Hook method called to determine if <code>checkSetValue</code> has any effect.
     * <p>
     * An implementation should return false if the <code>checkSetValue</code> method
     * has no effect as this optimises the implementation.
     * <p>
     * This implementation returns <code>true</code>.
     * 
     * @param value  the value to check
     */
    protected boolean isSetValueChecking() {
        return true;
    }

    /**
     * Checks each element in the specified map, creating a new map.
     * <p>
     * This method is used by <code>putAll</code> to check all the elements
     * before adding them to the map.
     * <p>
     * This implementation builds a <code>LinkedMap</code> to preserve the order
     * of the input map.
     * 
     * @param map  the map to transform
     * @throws the transformed object
     */
    protected Map checkMap(Map map) {
        Map result = new LinkedMap(map.size());
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(checkPutKey(entry.getKey()), checkPutValue(entry.getValue()));
        }
        return result;
    }

    //-----------------------------------------------------------------------
    public Object put(Object key, Object value) {
        key = checkPutKey(key);
        value = checkPutValue(value);
        return getMap().put(key, value);
    }

    public void putAll(Map mapToCopy) {
        if (mapToCopy.size() == 0) {
            return;
        } else {
            mapToCopy = checkMap(mapToCopy);
            getMap().putAll(mapToCopy);
        }
    }

    public Set entrySet() {
        if (isSetValueChecking()) {
            return new EntrySet(map.entrySet(), this);
        } else {
            return map.entrySet();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of an entry set that checks additions via setValue.
     */
    static class EntrySet extends AbstractSetDecorator {
        
        /** The parent map */
        private final AbstractInputCheckedMapDecorator parent;

        protected EntrySet(Set set, AbstractInputCheckedMapDecorator parent) {
            super(set);
            this.parent = parent;
        }

        public Iterator iterator() {
            return new EntrySetIterator(collection.iterator(), parent);
        }
        
        public Object[] toArray() {
            Object[] array = collection.toArray();
            for (int i = 0; i < array.length; i++) {
                array[i] = new MapEntry((Map.Entry) array[i], parent);
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
                result[i] = new MapEntry((Map.Entry) result[i], parent);
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
     * Implementation of an entry set iterator that checks additions via setValue.
     */
    static class EntrySetIterator extends AbstractIteratorDecorator {
        
        /** The parent map */
        private final AbstractInputCheckedMapDecorator parent;
        
        protected EntrySetIterator(Iterator iterator, AbstractInputCheckedMapDecorator parent) {
            super(iterator);
            this.parent = parent;
        }
        
        public Object next() {
            Map.Entry entry = (Map.Entry) iterator.next();
            return new MapEntry(entry, parent);
        }
    }

    /**
     * Implementation of a map entry that checks additions via setValue.
     */
    static class MapEntry extends AbstractMapEntryDecorator {

        /** The parent map */
        private final AbstractInputCheckedMapDecorator parent;

        protected MapEntry(Map.Entry entry, AbstractInputCheckedMapDecorator parent) {
            super(entry);
            this.parent = parent;
        }

        public Object setValue(Object value) {
            value = parent.checkSetValue(value);
            return entry.setValue(value);
        }
    }

}
