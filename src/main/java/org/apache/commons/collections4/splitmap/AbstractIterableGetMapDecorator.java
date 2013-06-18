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
package org.apache.commons.collections4.splitmap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.IterableGet;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;

/**
 * {@link IterableGet} that uses a {@link Map}<K, V> for the
 * {@link org.apache.commons.collections4.Get Get}<K, V> implementation.
 *
 * @since 4.0
 * @version $Id$
 */
public class AbstractIterableGetMapDecorator<K, V> implements IterableGet<K, V> {

    /** The map to decorate */
    transient Map<K, V> map;

    /**
     * Create a new AbstractSplitMapDecorator.
     * @param decorated the Map to decorate
     */
    public AbstractIterableGetMapDecorator(final Map<K, V> decorated) {
        this.map = decorated;
    }

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    protected AbstractIterableGetMapDecorator() {
        super();
    }

    /**
     * Gets the map being decorated.
     *
     * @return the decorated map
     */
    protected Map<K, V> decorated() {
        return map;
    }

    public boolean containsKey(final Object key) {
        return decorated().containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return decorated().containsValue(value);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return decorated().entrySet();
    }

    public V get(final Object key) {
        return decorated().get(key);
    }

    public V remove(final Object key) {
        return decorated().remove(key);
    }

    public boolean isEmpty() {
        return decorated().isEmpty();
    }

    public Set<K> keySet() {
        return decorated().keySet();
    }

    public int size() {
        return decorated().size();
    }

    public Collection<V> values() {
        return decorated().values();
    }

    /**
     * Get a MapIterator over this Get.
     * @return MapIterator<K, V>
     */
    public MapIterator<K, V> mapIterator() {
        return new EntrySetToMapIteratorAdapter<K, V>(entrySet());
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
