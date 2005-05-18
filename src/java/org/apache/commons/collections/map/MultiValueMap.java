/*
 *  Copyright 2001-2005 The Apache Software Foundation
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

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.IteratorChain;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A MultiValueMap decorates another map, allowing it to have
 * more than one value for a key.  The values of the map will be
 * Collection objects.  The types of which can be specified using
 * either a Class object or a Factory which creates Collection
 * objects.
 *
 * @author <a href="mailto:jcarman@apache.org">James Carman</a>
 * @since Commons Collections 3.2
 */
public class MultiValueMap extends AbstractMapDecorator implements MultiMap {
    private final Factory collectionFactory;
    private Collection values;

    /**
     * Creates a map which wraps the given map and
     * maps keys to ArrayLists.
     *
     * @param map the map to wrap
     */
    public static Map decorate(Map map) {
        return new MultiValueMap(map);
    }

    /**
     * Creates a map which decorates the given <code>map</code> and
     * maps keys to collections of type <code>collectionClass</code>.
     *
     * @param map             the map to wrap
     * @param collectionClass the type of the collection class
     */
    public static Map decorate(Map map, Class collectionClass) {
        return new MultiValueMap(map, collectionClass);
    }

    /**
     * Creates a map which decorates the given <code>map</code> and
     * creates the value collections using the supplied <code>collectionFactory</code>.
     *
     * @param map               the map to decorate
     * @param collectionFactory the collection factory (must return a Collection object).
     */
    public static Map decorate(Map map, Factory collectionFactory) {
        return new MultiValueMap(map, collectionFactory);
    }

    /**
     * Creates a MultiValueMap which wraps the given map and
     * maps keys to ArrayLists.
     *
     * @param map the map to wrap
     */
    protected MultiValueMap(Map map) {
        this(map, ArrayList.class);
    }

    /**
     * Creates a MultiValueMap which decorates the given <code>map</code> and
     * maps keys to collections of type <code>collectionClass</code>.
     *
     * @param map             the map to wrap
     * @param collectionClass the type of the collection class
     */
    protected MultiValueMap(Map map, Class collectionClass) {
        this(map, new ReflectionFactory(collectionClass));
    }

    /**
     * Creates a MultiValueMap which decorates the given <code>map</code> and
     * creates the value collections using the supplied <code>collectionFactory</code>.
     *
     * @param map               the map to decorate
     * @param collectionFactory the collection factory (must return a Collection object).
     */
    protected MultiValueMap(Map map, Factory collectionFactory) {
        super(map);
        this.collectionFactory = collectionFactory;
    }

    /**
     * Clear the map.
     * <p>
     * This clears each collection in the map, and so may be slow.
     */
    public void clear() {
        Set pairs = getMap().entrySet();
        Iterator pairsIterator = pairs.iterator();
        while(pairsIterator.hasNext()) {
            Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
            Collection coll = (Collection) keyValuePair.getValue();
            coll.clear();
        }
        getMap().clear();
    }

    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p>
     * If the last value for a key is removed, <code>null</code> will be returned
     * from a subsequant <code>get(key)</code>.
     *
     * @param key  the key to remove from
     * @param value the value to remove
     * @return the value removed (which was passed in), null if nothing removed
     */
    public Object remove(Object key, Object value) {
        Collection valuesForKey = getCollection(key);
        if(valuesForKey == null) {
            return null;
        }
        boolean removed = valuesForKey.remove(value);
        if(removed == false) {
            return null;
        }
        if(valuesForKey.isEmpty()) {
            remove(key);
        }
        return value;
    }

    /**
     * Checks whether the map contains the value specified.
     * <p>
     * This checks all collections against all keys for the value, and thus could be slow.
     *
     * @param value  the value to search for
     * @return true if the map contains the value
     */
    public boolean containsValue(Object value) {
        Set pairs = getMap().entrySet();
        if(pairs == null) {
            return false;
        }
        Iterator pairsIterator = pairs.iterator();
        while(pairsIterator.hasNext()) {
            Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
            Collection coll = (Collection) keyValuePair.getValue();
            if(coll.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a specific value from map.
     * <p>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p>
     * If the last value for a key is removed, <code>null</code> will be returned
     * from a subsequant <code>get(key)</code>.
     *
     * @param key  the key to remove from
     * @param value  the value to remove
     * @return the value removed (which was passed in), null if nothing removed
     */
    public Object put(Object key, Object value) {
        Collection c = getCollection(key);
        if(c == null) {
            c = (Collection) collectionFactory.create();
            getMap().put(key, c);
        }
        boolean results = c.add(value);
        return (results ? value : null);
    }

    /**
     * Gets a collection containing all the values in the map.
     * <p>
     * This returns a collection containing the combination of values from all keys.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection values() {
        Collection vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    /**
     * Checks whether the collection at the specified key contains the value.
     *
     * @param value  the value to search for
     * @return true if the map contains the value
     */
    public boolean containsValue(Object key, Object value) {
        Collection coll = getCollection(key);
        if(coll == null) {
            return false;
        }
        return coll.contains(value);
    }

    /**
     * Gets the collection mapped to the specified key.
     * This method is a convenience method to typecast the result of <code>get(key)</code>.
     *
     * @param key  the key to retrieve
     * @return the collection mapped to the key, null if no mapping
     */
    public Collection getCollection(Object key) {
        return (Collection) getMap().get(key);
    }

    /**
     * Gets the size of the collection mapped to the specified key.
     *
     * @param key  the key to get size for
     * @return the size of the collection at the key, zero if key not in map
     */
    public int size(Object key) {
        Collection coll = getCollection(key);
        if(coll == null) {
            return 0;
        }
        return coll.size();
    }

    /**
     * Adds a collection of values to the collection associated with the specified key.
     *
     * @param key  the key to store against
     * @param values  the values to add to the collection at the key, null ignored
     * @return true if this map changed
     */
    public boolean putAll(Object key, Collection values) {
        if(values == null || values.size() == 0) {
            return false;
        }
        Collection coll = getCollection(key);
        if(coll == null) {
            coll = (Collection) collectionFactory.create();
            getMap().put(key, coll);
        }
        return coll.addAll(values);
    }

    /**
     * Gets an iterator for the collection mapped to the specified key.
     *
     * @param key  the key to get an iterator for
     * @return the iterator of the collection at the key, empty iterator if key not in map
     */
    public Iterator iterator(Object key) {
        if(!containsKey(key)) {
            return EmptyIterator.INSTANCE;
        }
        else {
            return new ValuesIterator(key);
        }
    }

    /**
     * Gets the total size of the map by counting all the values.
     *
     * @return the total size of the map counting all values
     */
    public int totalSize() {
        int total = 0;
        Collection values = getMap().values();
        for(Iterator it = values.iterator(); it.hasNext();) {
            Collection coll = (Collection) it.next();
            total += coll.size();
        }
        return total;
    }

    private class Values extends AbstractCollection {
        public Iterator iterator() {
            final IteratorChain chain = new IteratorChain();
            for(Iterator i = keySet().iterator(); i.hasNext();) {
                chain.addIterator(new ValuesIterator(i.next()));
            }
            return chain;
        }

        public int size() {
            return totalSize();
        }

        public void clear() {
            MultiValueMap.this.clear();
        }
    }

    private class ValuesIterator implements Iterator {
        private final Object key;
        private final Collection values;
        private final Iterator iterator;

        public ValuesIterator(Object key) {
            this.key = key;
            this.values = getCollection(key);
            this.iterator = values.iterator();
        }

        public void remove() {
            iterator.remove();
            if(values.isEmpty()) {
                MultiValueMap.this.remove(key);
            }
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Object next() {
            return iterator.next();
        }
    }

    private static class ReflectionFactory implements Factory {
        private final Class clazz;

        public ReflectionFactory(Class clazz) {
            this.clazz = clazz;
        }

        public Object create() {
            try {
                return clazz.newInstance();
            }
            catch(Exception e) {
                throw new RuntimeException("Cannot instantiate class " + clazz + ".", e);
            }
        }
    }
}
