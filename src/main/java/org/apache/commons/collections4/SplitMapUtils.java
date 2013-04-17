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
package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;

/**
 * Utilities for working with "split maps:" objects that implement {@link Put}
 * and/or {@link Get} but not {@link Map}.
 *
 * @since 4.0
 * @version $Id$
 *
 * @see Get
 * @see Put
 */
public class SplitMapUtils {

    /**
     * <code>SplitMapUtils</code> should not normally be instantiated.
     */
    private SplitMapUtils() {}

    //-----------------------------------------------------------------------

    private static class WrappedGet<K, V> implements IterableMap<K, V>, Unmodifiable {
        private final Get<K, V> get;

        private WrappedGet(final Get<K, V> get) {
            this.get = get;
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(final Object key) {
            return get.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return get.containsValue(value);
        }

        public Set<java.util.Map.Entry<K, V>> entrySet() {
            return UnmodifiableEntrySet.unmodifiableEntrySet(get.entrySet());
        }

        @Override
        public boolean equals(final Object arg0) {
            if (arg0 == this) {
                return true;
            }
            return arg0 instanceof WrappedGet && ((WrappedGet<?, ?>) arg0).get.equals(this.get);
        }

        public V get(final Object key) {
            return get.get(key);
        }

        @Override
        public int hashCode() {
            return ("WrappedGet".hashCode() << 4) | get.hashCode();
        }

        public boolean isEmpty() {
            return get.isEmpty();
        }

        public Set<K> keySet() {
            return UnmodifiableSet.unmodifiableSet(get.keySet());
        }

        public V put(final K key, final V value) {
            throw new UnsupportedOperationException();
        }

        public void putAll(final Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException();
        }

        public V remove(final Object key) {
            return get.remove(key);
        }

        public int size() {
            return get.size();
        }

        public Collection<V> values() {
            return UnmodifiableCollection.unmodifiableCollection(get.values());
        }

        public MapIterator<K, V> mapIterator() {
            MapIterator<K, V> it;
            if (get instanceof IterableGet) {
                it = ((IterableGet<K, V>) get).mapIterator();
            } else {
                it = new EntrySetToMapIteratorAdapter<K, V>(get.entrySet());
            }
            return UnmodifiableMapIterator.unmodifiableMapIterator(it);
        }
    }

    private static class WrappedPut<K, V> implements Map<K, V>, Put<K, V> {
        private final Put<K, V> put;

        private WrappedPut(final Put<K, V> put) {
            this.put = put;
        }

        public void clear() {
            put.clear();
        }

        public boolean containsKey(final Object key) {
            throw new UnsupportedOperationException();
        }

        public boolean containsValue(final Object value) {
            throw new UnsupportedOperationException();
        }

        public Set<java.util.Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            return obj instanceof WrappedPut && ((WrappedPut<?, ?>) obj).put.equals(this.put);
        }

        public V get(final Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return ("WrappedPut".hashCode() << 4) | put.hashCode();
        }

        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        public Set<K> keySet() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        public V put(final K key, final V value) {
            return (V) put.put(key, value);
        }

        public void putAll(final Map<? extends K, ? extends V> t) {
            put.putAll(t);
        }

        public V remove(final Object key) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            throw new UnsupportedOperationException();
        }

        public Collection<V> values() {
            throw new UnsupportedOperationException();
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Get the specified {@link Get} as an instance of {@link IterableMap}.
     * If <code>get</code> implements {@link IterableMap} directly, no conversion will take place.
     * If <code>get</code> implements {@link Map} but not {@link IterableMap} it will be decorated.
     * Otherwise an {@link Unmodifiable} {@link IterableMap} will be returned.
     * @param <K> the key type
     * @param <V> the value type
     * @param get to wrap, must not be null
     * @return {@link IterableMap}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> IterableMap<K, V> readableMap(final Get<K, V> get) {
        if (get == null) {
            throw new IllegalArgumentException("Get must not be null");
        }
        if (get instanceof Map) {
            return get instanceof IterableMap ? ((IterableMap<K, V>) get) : MapUtils
                    .iterableMap((Map<K, V>) get);
        }
        return new WrappedGet<K, V>(get);
    }

    /**
     * Get the specified {@link Put} as an instanceof {@link Map}.
     * If <code>put</code> implements {@link Map} directly, no conversion will take place.
     * Otherwise a <em>write-only</em> {@link Map} will be returned.  On such a {@link Map}
     * it is recommended that the result of #put(K, V) be discarded as it likely will not
     * match <code>V</code> at runtime.
     *
     * @param <K> the key type
     * @param <V> the element type
     * @param put to wrap, must not be null
     * @return {@link Map}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> writableMap(final Put<K, V> put) {
        if (put == null) {
            throw new IllegalArgumentException("Put must not be null");
        }
        if (put instanceof Map) {
            return (Map<K, V>) put;
        }
        return new WrappedPut<K, V>(put);
    }

}
