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

import org.apache.commons.collections.BoundedMap;
import org.apache.commons.collections.IterableMap;
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
 * <p>
 * <strong>Note that FixedSizeMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedMap(Map)}. This class may throw 
 * exceptions when accessed by concurrent threads without synchronization.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 * @author Paul Jack
 */
public class FixedSizeMap<K, V>
        extends AbstractMapDecorator<K, V>
        implements Map<K, V>, BoundedMap<K, V>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 7450927208116179316L;

    /**
     * Factory method to create a fixed size map.
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static <K, V> FixedSizeMap<K, V> fixedSizeMap(Map<K, V> map) {
        return new FixedSizeMap<K, V>(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected FixedSizeMap(Map<K, V> map) {
        super(map);
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
    @SuppressWarnings("unchecked") // (1) should only fail if input stream is incorrect 
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map<K, V>) in.readObject(); // (1)
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
        for (K key : mapToCopy.keySet()) {
            if (!containsKey(key)) {
                throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
            }
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
        Set<Map.Entry<K, V>> set = map.entrySet();
        // unmodifiable set will still allow modification via Map.Entry objects
        return UnmodifiableSet.unmodifiableSet(set);
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = map.keySet();
        return UnmodifiableSet.unmodifiableSet(set);
    }

    @Override
    public Collection<V> values() {
        Collection<V> coll = map.values();
        return UnmodifiableCollection.unmodifiableCollection(coll);
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }

}
