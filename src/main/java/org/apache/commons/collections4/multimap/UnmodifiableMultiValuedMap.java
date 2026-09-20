/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.multimap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import org.apache.commons.collections4.set.UnmodifiableSet;

/**
 * Decorates another {@link MultiValuedMap} to ensure it can't be altered.
 * <p>
 * Attempts to modify it will result in an UnsupportedOperationException.
 * </p>
 *
 * @param <K> The type of key elements
 * @param <V> The type of value elements
 * @since 4.1
 */
public final class UnmodifiableMultiValuedMap<K, V>
        extends AbstractMultiValuedMapDecorator<K, V> implements Unmodifiable {

    /**
     * Read-only view of {@link MultiValuedMap#asMap()} whose value collections are unmodifiable as well.
     *
     * @param <K> The type of key elements
     * @param <V> The type of value elements
     */
    private static final class AsMapView<K, V> extends AbstractMap<K, Collection<V>> {

        private final Map<K, Collection<V>> map;

        AsMapView(final Map<K, Collection<V>> map) {
            this.map = map;
        }

        @Override
        public boolean containsKey(final Object key) {
            return map.containsKey(key);
        }

        @Override
        public Set<Entry<K, Collection<V>>> entrySet() {
            return new AbstractSet<Entry<K, Collection<V>>>() {

                @Override
                public Iterator<Entry<K, Collection<V>>> iterator() {
                    return new TransformIterator<>(map.entrySet().iterator(),
                            entry -> new UnmodifiableMapEntry<>(entry.getKey(), unmodifiableValues(entry.getValue())));
                }

                @Override
                public int size() {
                    return map.size();
                }
            };
        }

        @Override
        public Collection<V> get(final Object key) {
            final Collection<V> values = map.get(key);
            return values == null ? null : unmodifiableValues(values);
        }
    }

    /** Serialization version */
    private static final long serialVersionUID = 20150612L;

    /**
     * Factory method to create an unmodifiable MultiValuedMap.
     * <p>
     * If the map passed in is already unmodifiable, it is returned.
     * </p>
     *
     * @param <K> The type of key elements
     * @param <V> The type of value elements
     * @param map  The map to decorate, may not be null
     * @return An unmodifiable MultiValuedMap
     * @throws NullPointerException if map is null
     */
    @SuppressWarnings("unchecked")
    public static <K, V> UnmodifiableMultiValuedMap<K, V> unmodifiableMultiValuedMap(
            final MultiValuedMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            return (UnmodifiableMultiValuedMap<K, V>) map;
        }
        return new UnmodifiableMultiValuedMap<>(map);
    }

    /**
     * Wraps a value collection so it can't be altered, keeping its {@link List} or {@link Set} type.
     *
     * @param <V> The type of value elements
     * @param values The value collection to wrap
     * @return An unmodifiable view of the value collection
     */
    private static <V> Collection<V> unmodifiableValues(final Collection<V> values) {
        if (values instanceof List) {
            return UnmodifiableList.unmodifiableList((List<V>) values);
        }
        if (values instanceof Set) {
            return UnmodifiableSet.unmodifiableSet((Set<V>) values);
        }
        return UnmodifiableCollection.unmodifiableCollection(values);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param map  The MultiValuedMap to decorate, may not be null
     * @throws NullPointerException if the map is null
     */
    @SuppressWarnings("unchecked")
    private UnmodifiableMultiValuedMap(final MultiValuedMap<? extends K, ? extends V> map) {
        super((MultiValuedMap<K, V>) map);
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return UnmodifiableMap.unmodifiableMap(new AsMapView<>(decorated().asMap()));
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return UnmodifiableCollection.unmodifiableCollection(decorated().entries());
    }

    @Override
    public Collection<V> get(final K key) {
        return UnmodifiableCollection.unmodifiableCollection(decorated().get(key));
    }

    @Override
    public MultiSet<K> keys() {
        return UnmodifiableMultiSet.unmodifiableMultiSet(decorated().keys());
    }

    @Override
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet(decorated().keySet());
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned map iterator's {@link MapIterator#setValue(Object)} method is not supported
     * and will throw an {@link UnsupportedOperationException}.
     * </p>
     */
    @Override
    public MapIterator<K, V> mapIterator() {
        return UnmodifiableMapIterator.unmodifiableMapIterator(decorated().mapIterator());
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param key Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean put(final K key, final V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param key Ignored.
     * @param values Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param map Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean putAll(final Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param map Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean putAll(final MultiValuedMap<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param key Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public Collection<V> remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @param key Ignored.
     * @param item Ignored.
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public boolean removeMapping(final Object key, final Object item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection(decorated().values());
    }

}
