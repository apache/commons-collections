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
package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.bag.UnmodifiableBag;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;

/**
 * Decorates another {@link MultiValuedMap} to ensure it can't be altered.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 *
 * @param <K> the type of key elements
 * @param <V> the type of value elements
 *
 * @since 4.1
 * @version $Id$
 */
public class UnmodifiableMultiValuedMap<K, V>
        extends AbstractMultiValuedMapDecorator<K, V> implements Unmodifiable {

    /** Serialization version */
    private static final long serialVersionUID = 1418669828214151566L;

    /**
     * Factory method to create an unmodifiable MultiValuedMap.
     * <p>
     * If the map passed in is already unmodifiable, it is returned.
     *
     * @param <K> the type of key elements
     * @param <V> the type of value elements
     * @param map the map to decorate, must not be null
     * @return an unmodifiable MultiValuedMap
     * @throws IllegalArgumentException if map is null
     */
    @SuppressWarnings("unchecked")
    public static <K, V> UnmodifiableMultiValuedMap<K, V>
            unmodifiableMultiValuedMap(MultiValuedMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            return (UnmodifiableMultiValuedMap<K, V>) map;
        }
        return new UnmodifiableMultiValuedMap<K, V>(map);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param map the MultiValuedMap to decorate, must not be null
     * @throws IllegalArgumentException if the map is null
     */
    @SuppressWarnings("unchecked")
    private UnmodifiableMultiValuedMap(final MultiValuedMap<? extends K, ? extends V> map) {
        super((MultiValuedMap<K, V>) map);
    }

    @Override
    public Collection<V> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeMapping(K key, V item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        return UnmodifiableSet.<K>unmodifiableSet(decorated().keySet());
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return UnmodifiableCollection.<Entry<K, V>>unmodifiableCollection(decorated().entries());
    }

    @Override
    public Bag<K> keys() {
        return UnmodifiableBag.<K>unmodifiableBag(decorated().keys());
    }

    @Override
    public Collection<V> values() {
        return UnmodifiableCollection.<V>unmodifiableCollection(decorated().values());
    }

    @Override
    public boolean putAll(K key, Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(MultiValuedMap<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

}
