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
package org.apache.commons.collections.splitmap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Get;
import org.apache.commons.collections.IterableGet;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Put;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections.map.EntrySetToMapIteratorAdapter;
import org.apache.commons.collections.map.UnmodifiableEntrySet;
import org.apache.commons.collections.set.UnmodifiableSet;

/**
 * Utilities for working with "split maps:" objects that implement {@link Put}
 * and/or {@link Get} but not {@link Map}.
 *
 * @since Commons Collections 5
 * @TODO fix version
 * @version $Revision$ $Date$
 * @see Get
 * @see Put
 * @author Matt Benson
 */
public class SplitMapUtils {

    /**
     * <code>SplitMapUtils</code> should not normally be instantiated.
     */
    public SplitMapUtils() {
    }

    private static class WrappedGet<K, V> implements IterableMap<K, V>, Unmodifiable {
        private Get<K, V> get;

        private WrappedGet(Get<K, V> get) {
            this.get = get;
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(Object key) {
            return get.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return get.containsValue(value);
        }

        public Set<java.util.Map.Entry<K, V>> entrySet() {
            return UnmodifiableEntrySet.decorate(get.entrySet());
        }

        @Override
        public boolean equals(Object arg0) {
            if (arg0 == this) {
                return true;
            }
            return arg0 instanceof WrappedGet && ((WrappedGet<?, ?>) arg0).get.equals(this.get);
        }

        public V get(Object key) {
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
            return UnmodifiableSet.decorate(get.keySet());
        }

        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        public void putAll(Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException();
        }

        public V remove(Object key) {
            return get.remove(key);
        }

        public int size() {
            return get.size();
        }

        public Collection<V> values() {
            return UnmodifiableCollection.decorate(get.values());
        }

        public MapIterator<K, V> mapIterator() {
            MapIterator<K, V> it;
            if (get instanceof IterableGet) {
                it = ((IterableGet<K, V>) get).mapIterator();
            } else {
                it = new EntrySetToMapIteratorAdapter<K, V>(get.entrySet());
            }
            return UnmodifiableMapIterator.decorate(it);
        }
    }

    private static class WrappedPut<K, V> implements Map<K, V>, Put<K, V> {
        private Put<K, V> put;

        private WrappedPut(Put<K, V> put) {
            this.put = put;
        }

        public void clear() {
            put.clear();
        }

        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException();
        }

        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        public Set<java.util.Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return obj instanceof WrappedPut && ((WrappedPut<?, ?>) obj).put.equals(this.put);
        }

        public V get(Object key) {
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
        public V put(K key, V value) {
            return (V) put.put(key, value);
        }

        public void putAll(Map<? extends K, ? extends V> t) {
            put.putAll(t);
        }

        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            throw new UnsupportedOperationException();
        }

        public Collection<V> values() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Get the specified {@link Get} as an instance of {@link IterableMap}.
     * If <code>get</code> implements {@link IterableMap} directly, no conversion will take place.
     * If <code>get</code> implements {@link Map} but not {@link IterableMap} it will be decorated.
     * Otherwise an {@link Unmodifiable} {@link IterableMap} will be returned.
     * @param <K>
     * @param <V>
     * @param get to wrap, must not be null
     * @return {@link IterableMap}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> IterableMap<K, V> readableMap(Get<K, V> get) {
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
     * @param <K>
     * @param <V>
     * @param put to wrap, must not be null
     * @return {@link Map}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> writableMap(Put<K, V> put) {
        if (put == null) {
            throw new IllegalArgumentException("Put must not be null");
        }
        if (put instanceof Map) {
            return (Map<K, V>) put;
        }
        return new WrappedPut<K, V>(put);
    }

}
