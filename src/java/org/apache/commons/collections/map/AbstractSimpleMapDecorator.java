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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.set.AbstractSetDecorator;

/**
 * An abstract base class that simplifies the task of creating map decorators.
 * <p>
 * The Map API is very difficult to decorate correctly, and involves implementing
 * lots of different classes. This class exists to provide a simpler API.
 * <p>
 * Special hook methods are provided that are called when events occur on the map.
 * By overriding these methods, the input and output can be validated or manipulated.
 * <p>
 * This class provides full implementations of the keySet, values and entrySet,
 * which means that your subclass decorator should not need any inner classes.
 *
 * @since Commons Collections 3.1
 * @version $Revision: 1.1 $ $Date: 2004/05/21 21:42:04 $
 * 
 * @author Stephen Colebourne
 */
public class AbstractSimpleMapDecorator
        extends AbstractMapDecorator {

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    protected AbstractSimpleMapDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected AbstractSimpleMapDecorator(Map map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Hook method called when a key is being retrieved from the map using
     * <code>put</code>, <code>keySet.iterator</code> or <code>entry.getKey</code>.
     * <p>
     * An implementation may validate the key and throw an exception
     * or it may transform the key into another object.
     * <p>
     * This implementation returns the input key.
     * 
     * @param key  the key to check
     * @throws UnsupportedOperationException if the map get is not supported
     * @throws IllegalArgumentException if the specified key is invalid
     * @throws ClassCastException if the class of the specified key is invalid
     * @throws NullPointerException if the specified key is null and nulls are invalid
     */
    protected Object checkGetKey(Object key) {
        return key;
    }

    /**
     * Hook method called when a value is being retrieved from the map using
     * <code>get</code>, <code>values.iterator</code> or <code>entry.getValue</code>.
     * <p>
     * An implementation may validate the value and throw an exception
     * or it may transform the value into another object.
     * <p>
     * This implementation returns the input value.
     * 
     * @param value  the value to check
     * @throws UnsupportedOperationException if the map get is not supported
     * @throws IllegalArgumentException if the specified value is invalid
     * @throws ClassCastException if the class of the specified value is invalid
     * @throws NullPointerException if the specified value is null and nulls are invalid
     */
    protected Object checkGetValue(Object value) {
        return value;
    }

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
     * Hook method called when the map is being queried using a key via the
     * contains and equals methods.
     * <p>
     * An implementation may validate the key and throw an exception
     * or it may transform the key into another object.
     * <p>
     * This implementation returns the input key.
     * 
     * @param key  the key to check
     * @throws UnsupportedOperationException if the method is not supported
     * @throws IllegalArgumentException if the specified key is invalid
     * @throws ClassCastException if the class of the specified key is invalid
     * @throws NullPointerException if the specified key is null and nulls are invalid
     */
    protected Object checkQueryKey(Object key) {
        return key;
    }

    /**
     * Hook method called when the map is being queried using a value via the
     * contains and equals methods.
     * <p>
     * An implementation may validate the value and throw an exception
     * or it may transform the value into another object.
     * <p>
     * This implementation returns the input value.
     * 
     * @param value  the value to check
     * @throws UnsupportedOperationException if the method is not supported
     * @throws IllegalArgumentException if the specified value is invalid
     * @throws ClassCastException if the class of the specified value is invalid
     * @throws NullPointerException if the specified value is null and nulls are invalid
     */
    protected Object checkQueryValue(Object value) {
        return value;
    }

    /**
     * Hook method called when the map is being queried using a key via the
     * remove methods.
     * <p>
     * An implementation may validate the key and throw an exception
     * or it may transform the key into another object.
     * <p>
     * This implementation returns the input key.
     * 
     * @param key  the key to check
     * @throws UnsupportedOperationException if the method is not supported
     * @throws IllegalArgumentException if the specified key is invalid
     * @throws ClassCastException if the class of the specified key is invalid
     * @throws NullPointerException if the specified key is null and nulls are invalid
     */
    protected Object checkRemoveKey(Object key) {
        return key;
    }

    /**
     * Hook method called when the map is being queried using a value via the
     * remove methods.
     * <p>
     * An implementation may validate the value and throw an exception
     * or it may transform the value into another object.
     * <p>
     * This implementation returns the input value.
     * 
     * @param value  the value to check
     * @throws UnsupportedOperationException if the method is not supported
     * @throws IllegalArgumentException if the specified value is invalid
     * @throws ClassCastException if the class of the specified value is invalid
     * @throws NullPointerException if the specified value is null and nulls are invalid
     */
    protected Object checkRemoveValue(Object value) {
        return value;
    }

    /**
     * Hook method called to determine if the keySet view should be decorated.
     * <p>
     * An implementation should return false if the there is no decoration of the keySet
     * view as this optimises the implementation.
     * <p>
     * This implementation returns <code>true</code>.
     * 
     * @param value  the value to check
     */
    protected boolean requiresKeySetDecorator() {
        return true;
    }

    /**
     * Hook method called to determine if the values view should be decorated.
     * <p>
     * An implementation should return false if the there is no decoration of the values
     * view as this optimises the implementation.
     * <p>
     * This implementation returns <code>true</code>.
     * 
     * @param value  the value to check
     */
    protected boolean requiresValuesDecorator() {
        return true;
    }

    /**
     * Hook method called to determine if the entrySet view should be decorated.
     * <p>
     * An implementation should return false if the there is no decoration of the entrySet
     * view as this optimises the implementation.
     * <p>
     * This implementation returns <code>true</code>.
     * 
     * @param value  the value to check
     */
    protected boolean requiresEntrySetDecorator() {
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
    public Object get(Object key) {
        return checkGetValue(getMap().get(key));
    }

    public boolean containsKey(Object key) {
        key = checkQueryKey(key);
        return getMap().containsKey(key);
    }

    public boolean containsValue(Object value) {
        value = checkQueryValue(value);
        return getMap().containsValue(value);
    }

    public Object put(Object key, Object value) {
        key = checkPutKey(key);
        value = checkPutValue(value);
        return checkGetKey(getMap().put(key, value));
    }

    public void putAll(Map mapToCopy) {
        if (mapToCopy.size() == 0) {
            return;
        } else {
            mapToCopy = checkMap(mapToCopy);
            getMap().putAll(mapToCopy);
        }
    }

    public Object remove(Object key) {
        key = checkRemoveKey(key);
        return checkGetKey(getMap().remove(key));
    }

    public Set keySet() {
        if (requiresKeySetDecorator()) {
            return new KeySet(map.keySet(), this);
        } else {
            return map.keySet();
        }
    }

    public Collection values() {
        if (requiresValuesDecorator()) {
            return new Values(map.values(), this);
        } else {
            return map.values();
        }
    }

    public Set entrySet() {
        if (requiresEntrySetDecorator()) {
            return new EntrySet(map.entrySet(), this);
        } else {
            return map.entrySet();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of an entry set that checks the returned keys.
     */
    static class KeySet extends AbstractSetDecorator {
        
        /** The parent map */
        private final AbstractSimpleMapDecorator parent;

        protected KeySet(Set set, AbstractSimpleMapDecorator parent) {
            super(set);
            this.parent = parent;
        }

        public Iterator iterator() {
            return new KeySetIterator(collection.iterator(), parent);
        }

        public boolean contains(Object key) {
            key = parent.checkQueryKey(key);
            return collection.contains(key);
        }

        public boolean containsAll(Collection coll) {
            List list = new ArrayList(coll);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, parent.checkQueryKey(list.get(i)));
            }
            return collection.containsAll(list);
        }

        public boolean remove(Object key) {
            key = parent.checkRemoveKey(key);
            return collection.remove(key);
        }

        public boolean removeAll(Collection coll) {
            List list = new ArrayList(coll);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, parent.checkRemoveKey(list.get(i)));
            }
            return collection.removeAll(list);
        }

        public boolean retainAll(Collection coll) {
            List list = new ArrayList(coll);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, parent.checkRemoveKey(list.get(i)));
            }
            return collection.retainAll(list);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Set == false) {
                return false;
            }
            Set other = (Set) object;
            Set set = new HashSet(other.size());
            for (Iterator it = other.iterator(); it.hasNext();) {
                set.add(parent.checkQueryKey(it.next()));
            }
            return collection.equals(set);
        }

        public Object[] toArray() {
            Object[] array = collection.toArray();
            for (int i = 0; i < array.length; i++) {
                array[i] = parent.checkGetKey(array[i]);
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
                result[i] = parent.checkGetKey(result[i]);
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

    //-----------------------------------------------------------------------
    /**
     * Implementation of a key set iterator that checks the returned keys.
     */
    static class KeySetIterator extends AbstractIteratorDecorator {
        
        /** The parent map */
        private final AbstractSimpleMapDecorator parent;
        
        protected KeySetIterator(Iterator iterator, AbstractSimpleMapDecorator parent) {
            super(iterator);
            this.parent = parent;
        }
        
        public Object next() {
            return parent.checkGetKey(iterator.next());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a values collection that checks the returned values.
     */
    static class Values extends AbstractCollectionDecorator {
        
        /** The parent map */
        private final AbstractSimpleMapDecorator parent;

        protected Values(Collection coll, AbstractSimpleMapDecorator parent) {
            super(coll);
            this.parent = parent;
        }

        public Iterator iterator() {
            return new ValuesIterator(collection.iterator(), parent);
        }

        public boolean contains(Object key) {
            key = parent.checkQueryValue(key);
            return collection.contains(key);
        }

        public boolean containsAll(Collection coll) {
            List list = new ArrayList(coll);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, parent.checkQueryValue(list.get(i)));
            }
            return collection.containsAll(list);
        }

        public boolean remove(Object key) {
            key = parent.checkRemoveValue(key);
            return collection.remove(key);
        }

        public boolean removeAll(Collection coll) {
            List list = new ArrayList(coll);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, parent.checkRemoveValue(list.get(i)));
            }
            return collection.removeAll(list);
        }

        public boolean retainAll(Collection coll) {
            List list = new ArrayList(coll);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, parent.checkRemoveValue(list.get(i)));
            }
            return collection.retainAll(list);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Collection == false) {
                return false;
            }
            Collection other = (Collection) object;
            Collection coll = new ArrayList(other.size());
            for (Iterator it = other.iterator(); it.hasNext();) {
                coll.add(parent.checkQueryValue(it.next()));
            }
            return collection.equals(coll);
        }

        public Object[] toArray() {
            Object[] array = collection.toArray();
            for (int i = 0; i < array.length; i++) {
                array[i] = parent.checkGetValue(array[i]);
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
                result[i] = parent.checkGetValue(result[i]);
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

    //-----------------------------------------------------------------------
    /**
     * Implementation of an value iterator that checks the returned values.
     */
    static class ValuesIterator extends AbstractIteratorDecorator {
        
        /** The parent map */
        private final AbstractSimpleMapDecorator parent;
        
        protected ValuesIterator(Iterator iterator, AbstractSimpleMapDecorator parent) {
            super(iterator);
            this.parent = parent;
        }
        
        public Object next() {
            return parent.checkGetValue(iterator.next());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of an entry set that calls hook methods from the map entry.
     */
    static class EntrySet extends AbstractSetDecorator {
        
        /** The parent map */
        private final AbstractSimpleMapDecorator parent;

        protected EntrySet(Set set, AbstractSimpleMapDecorator parent) {
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

    //-----------------------------------------------------------------------
    /**
     * Implementation of an entry set iterator that sets up a special map entry.
     */
    static class EntrySetIterator extends AbstractIteratorDecorator {
        
        /** The parent map */
        private final AbstractSimpleMapDecorator parent;
        
        protected EntrySetIterator(Iterator iterator, AbstractSimpleMapDecorator parent) {
            super(iterator);
            this.parent = parent;
        }
        
        public Object next() {
            Map.Entry entry = (Map.Entry) iterator.next();
            return new MapEntry(entry, parent);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a map entry that calls the hook methods.
     */
    static class MapEntry implements Map.Entry, KeyValue {

        /** The <code>Map.Entry</code> to decorate */
        protected final Map.Entry entry;
        /** The parent map */
        private final AbstractSimpleMapDecorator parent;

        protected MapEntry(Map.Entry entry, AbstractSimpleMapDecorator parent) {
            super();
            this.entry = entry;
            this.parent = parent;
        }

        public Object getKey() {
            return parent.checkGetKey(entry.getKey());
        }

        public Object getValue() {
            return parent.checkGetValue(entry.getValue());
        }

        public Object setValue(Object value) {
            value = parent.checkSetValue(value);
            return entry.setValue(value);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            return entry.equals(object);
        }

        public int hashCode() {
            return entry.hashCode();
        }

        public String toString() {
            return entry.toString();
        }
    }

}
