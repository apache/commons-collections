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
package org.apache.commons.collections.bidimap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.OrderedBidiMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections.map.UnmodifiableEntrySet;
import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * Decorates another <code>OrderedBidiMap</code> to ensure it can't be altered.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException. 
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public final class UnmodifiableOrderedBidiMap<K, V>
        extends AbstractOrderedBidiMapDecorator<K, V> implements Unmodifiable {

    /** The inverse unmodifiable map */
    private UnmodifiableOrderedBidiMap<V, K> inverse;

    /**
     * Factory method to create an unmodifiable map.
     * <p>
     * If the map passed in is already unmodifiable, it is returned.
     *
     * @param map  the map to decorate, must not be null
     * @return an unmodifiable OrderedBidiMap
     * @throws IllegalArgumentException if map is null
     */
    public static <K, V> OrderedBidiMap<K, V> decorate(OrderedBidiMap<K, V> map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableOrderedBidiMap<K, V>(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param map  the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    private UnmodifiableOrderedBidiMap(OrderedBidiMap<K, V> map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = super.entrySet();
        return UnmodifiableEntrySet.decorate(set);
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = super.keySet();
        return UnmodifiableSet.decorate(set);
    }

    @Override
    public Collection<V> values() {
        Collection<V> coll = super.values();
        return UnmodifiableCollection.decorate(coll);
    }

    //-----------------------------------------------------------------------
    @Override
    public K removeValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OrderedBidiMap<V, K> inverseBidiMap() {
        return inverseOrderedBidiMap();
    }

    //-----------------------------------------------------------------------
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        OrderedMapIterator<K, V> it = decorated().mapIterator();
        return UnmodifiableOrderedMapIterator.decorate(it);
    }

    public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
        if (inverse == null) {
            inverse = new UnmodifiableOrderedBidiMap<V, K>(decorated().inverseBidiMap());
            inverse.inverse = this;
        }
        return inverse;
    }

}
