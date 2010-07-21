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
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.EntrySetToMapIteratorAdapter;

/**
 * {@link IterableGet} that uses a {@link Map}<K, V> for the {@link Get}<K, V>
 * implementation.
 *
 * @since Commons Collections 5
 * @TODO fix version
 * @version $Revision$ $Date$
 *
 * @author Matt Benson
 */
public class AbstractIterableGetMapDecorator<K, V> implements IterableGet<K, V> {
    /** The map to decorate */
    protected transient Map<K, V> map;

    /**
     * Create a new AbstractSplitMapDecorator.
     * @param decorated the Map to decorate
     */
    public AbstractIterableGetMapDecorator(Map<K, V> decorated) {
        this.map = decorated;
    }

    /**
     * Gets the map being decorated.
     *
     * @return the decorated map
     */
    protected Map<K, V> decorated() {
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return decorated().containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
        return decorated().containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return decorated().entrySet();
    }

    /**
     * {@inheritDoc}
     */
    public V get(Object key) {
        return decorated().get(key);
    }

    /**
     * {@inheritDoc}
     */
    public V remove(Object key) {
        return decorated().remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return decorated().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        return decorated().keySet();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return decorated().size();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return decorated().equals(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return decorated().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return decorated().toString();
    }

}
