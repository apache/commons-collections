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
package org.apache.commons.collections.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.ResettableIterator;

/**
 * Adapts a Map entrySet to the MapIterator interface.
 *
 * @since Commons Collections 5
 * TODO fix version
 * @version $Revision$ $Date$
 *
 * @author Matt Benson
 */
public class EntrySetToMapIteratorAdapter<K, V> implements MapIterator<K, V>, ResettableIterator<K> {
    /** The adapted Map entry Set. */
    protected Set<Map.Entry<K, V>> entrySet;

    /** The resettable iterator in use. */
    protected transient Iterator<Map.Entry<K, V>> iterator;

    /** The currently positioned Map entry. */
    protected transient Map.Entry<K, V> entry;

    /**
     * Create a new EntrySetToMapIteratorAdapter.
     */
    public EntrySetToMapIteratorAdapter(Set<Map.Entry<K, V>> entrySet) {
        this.entrySet = entrySet;
        reset();
    }

    /**
     * {@inheritDoc}
     */
    public K getKey() {
        return current().getKey();
    }

    /**
     * {@inheritDoc}
     */
    public V getValue() {
        return current().getValue();
    }

    /**
     * {@inheritDoc}
     */
    public V setValue(V value) {
        return current().setValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public K next() {
        entry = iterator.next();
        return getKey();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void reset() {
        iterator = entrySet.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        iterator.remove();
        entry = null;
    }

    /**
     * Get the currently active entry.
     * @return Map.Entry<K, V>
     */
    protected synchronized Map.Entry<K, V> current() { 
        if (entry == null) {
            throw new IllegalStateException();
        }
        return entry;
    }
}
