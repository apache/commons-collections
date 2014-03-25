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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.MultiValuedMap;

/**
 * Decorates another <code>MultiValuedMap</code> to provide additional behaviour.
 * <p>
 * Each method call made on this <code>MultiValuedMap</code> is forwarded to the
 * decorated <code>MultiValuedMap</code>. This class is used as a framework to
 * build to extensions such as synchronized and unmodifiable behaviour.
 *
 * @param <K> the type of key elements
 * @param <V> the type of value elements
 *
 * @since 4.1
 * @version $Id$
 */
public class AbstractMultiValuedMapDecorator<K, V>
        implements MultiValuedMap<K, V>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = -9184930955231260637L;

    /** MultiValuedMap to decorate */
    private final MultiValuedMap<K, V> map;

    /**
     * Constructor that wraps (not copies).
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if the map is null
     */
    protected AbstractMultiValuedMapDecorator(final MultiValuedMap<K, V> map) {
        if (map == null) {
            throw new IllegalArgumentException("MultiValuedMap must not be null");
        }
        this.map = map;
    }

    protected MultiValuedMap<K, V> decorated() {
        return map;
    }

    public int size() {
        return decorated().size();
    }

    public boolean isEmpty() {
        return decorated().isEmpty();
    }

    public boolean containsKey(Object key) {
        return decorated().containsKey(key);
    }

    public boolean containsValue(Object value) {
        return decorated().containsValue(value);
    }

    public boolean containsMapping(Object key, Object value) {
        return decorated().containsMapping(key, value);
    }

    public Collection<V> get(Object key) {
        return decorated().get(key);
    }

    public Collection<V> remove(Object key) {
        return decorated().remove(key);
    }

    public boolean removeMapping(K key, V item) {
        return decorated().removeMapping(key, item);
    }

    public void clear() {
        decorated().clear();
    }

    public V put(K key, V value) {
        return decorated().put(key, value);
    }

    public Set<K> keySet() {
        return decorated().keySet();
    }

    public Collection<Entry<K, V>> entries() {
        return decorated().entries();
    }

    public Bag<K> keys() {
        return decorated().keys();
    }

    public Collection<V> values() {
        return decorated().values();
    }

    public boolean putAll(K key, Iterable<? extends V> values) {
        return decorated().putAll(key, values);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        decorated().putAll(m);
    }

    public void putAll(MultiValuedMap<? extends K, ? extends V> m) {
        decorated().putAll(m);
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        return decorated().equals(object);
    }

    @Override
    public int hashCode() {
        return decorated().hashCode();
    }

    @Override
    public String toString() {
        return decorated().toString();
    }

}
