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
package org.apache.commons.collections4.bidimap;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import org.apache.commons.collections4.set.UnmodifiableSet;

/**
 * Decorates another {@link BidiMap} to ensure it can't be altered.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 * </p>
 *
 * @param <K> the type of the keys in this map
 * @param <V> the type of the values in this map
 * @since 3.0
 */
public final class UnmodifiableBidiMap<K, V>
        extends AbstractBidiMapDecorator<K, V> implements Unmodifiable {

    /**
     * Factory method to create an unmodifiable map.
     * <p>
     * If the map passed in is already unmodifiable, it is returned.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map  the map to decorate, must not be null
     * @return an unmodifiable BidiMap
     * @throws NullPointerException if map is null
     * @since 4.0
     */
    public static <K, V> BidiMap<K, V> unmodifiableBidiMap(final BidiMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            @SuppressWarnings("unchecked") // safe to upcast
            final BidiMap<K, V> tmpMap = (BidiMap<K, V>) map;
            return tmpMap;
        }
        return new UnmodifiableBidiMap<>(map);
    }

    /** The inverse unmodifiable map */
    private UnmodifiableBidiMap<V, K> inverse;

    /**
     * Constructor that wraps (not copies).
     *
     * @param map  the map to decorate, must not be null
     * @throws NullPointerException if map is null
     */
    @SuppressWarnings("unchecked") // safe to upcast
    private UnmodifiableBidiMap(final BidiMap<? extends K, ? extends V> map) {
        super((BidiMap<K, V>) map);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> set = super.entrySet();
        return UnmodifiableEntrySet.unmodifiableEntrySet(set);
    }

    @Override
    public synchronized BidiMap<V, K> inverseBidiMap() {
        if (inverse == null) {
            inverse = new UnmodifiableBidiMap<>(decorated().inverseBidiMap());
            inverse.inverse = this;
        }
        return inverse;
    }

    @Override
    public Set<K> keySet() {
        final Set<K> set = super.keySet();
        return UnmodifiableSet.unmodifiableSet(set);
    }

    @Override
    public MapIterator<K, V> mapIterator() {
        final MapIterator<K, V> it = decorated().mapIterator();
        return UnmodifiableMapIterator.unmodifiableMapIterator(it);
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
    public K removeValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<V> values() {
        final Set<V> set = super.values();
        return UnmodifiableSet.unmodifiableSet(set);
    }

}
