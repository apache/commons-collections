/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections.BoundedMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * Decorates another <code>SortedMap</code> to fix the size blocking add/remove.
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
 * <p>
 * <strong>Note that FixedSizeSortedMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedSortedMap}. This class may throw 
 * exceptions when accessed by concurrent threads without synchronization.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class FixedSizeSortedMap<K, V>
        extends AbstractSortedMapDecorator<K, V>
        implements SortedMap<K, V>, BoundedMap<K, V>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 3126019624511683653L;

    /**
     * Factory method to create a fixed size sorted map.
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static <K, V> SortedMap<K, V> decorate(SortedMap<K, V> map) {
        return new FixedSizeSortedMap<K, V>(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected FixedSizeSortedMap(SortedMap<K, V> map) {
        super(map);
    }

    /**
     * Gets the map being decorated.
     * 
     * @return the decorated map
     */
    protected SortedMap<K, V> getSortedMap() {
        return (SortedMap<K, V>) map;
    }

    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map) in.readObject();
    }

    //-----------------------------------------------------------------------
    @Override
    public V put(K key, V value) {
        if (map.containsKey(key) == false) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        if (CollectionUtils.isSubCollection(mapToCopy.keySet(), keySet())) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        map.putAll(mapToCopy);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return UnmodifiableSet.decorate(map.entrySet());
    }

    @Override
    public Set<K> keySet() {
        return UnmodifiableSet.decorate(map.keySet());
    }

    @Override
    public Collection<V> values() {
        return UnmodifiableCollection.decorate(map.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return new FixedSizeSortedMap<K, V>(getSortedMap().subMap(fromKey, toKey));
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return new FixedSizeSortedMap<K, V>(getSortedMap().headMap(toKey));
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return new FixedSizeSortedMap<K, V>(getSortedMap().tailMap(fromKey));
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }

}
