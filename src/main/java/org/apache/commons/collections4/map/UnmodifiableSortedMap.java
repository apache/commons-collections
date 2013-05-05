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
package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;

/**
 * Decorates another <code>SortedMap</code> to ensure it can't be altered.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 *
 * @since 3.0
 * @version $Id$
 */
public final class UnmodifiableSortedMap<K, V>
        extends AbstractSortedMapDecorator<K, V>
        implements Unmodifiable, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = 5805344239827376360L;

    /**
     * Factory method to create an unmodifiable sorted map.
     *
     * @param <K>  the key type
     * @param <V>  the value type
     * @param map  the map to decorate, must not be null
     * @return a new unmodifiable sorted map
     * @throws IllegalArgumentException if map is null
     * @since 4.0
     */
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(final SortedMap<K, V> map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableSortedMap<K, V>(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    private UnmodifiableSortedMap(final SortedMap<K, V> map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     *
     * @param out  the output stream
     * @throws IOException
     * @since 3.1
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     * @since 3.1
     */
    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map<K, V>) in.readObject();
    }

    //-----------------------------------------------------------------------
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(final K key, final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> mapToCopy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return UnmodifiableEntrySet.unmodifiableEntrySet(super.entrySet());
    }

    @Override
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet(super.keySet());
    }

    @Override
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection(super.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public K firstKey() {
        return decorated().firstKey();
    }

    @Override
    public K lastKey() {
        return decorated().lastKey();
    }

    @Override
    public Comparator<? super K> comparator() {
        return decorated().comparator();
    }

    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return new UnmodifiableSortedMap<K, V>(decorated().subMap(fromKey, toKey));
    }

    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return new UnmodifiableSortedMap<K, V>(decorated().headMap(toKey));
    }

    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return new UnmodifiableSortedMap<K, V>(decorated().tailMap(fromKey));
    }

}
