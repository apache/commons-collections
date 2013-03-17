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
package org.apache.commons.collections.trie;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.collections.Trie;
import org.apache.commons.collections.collection.SynchronizedCollection;

/**
 * A synchronized {@link Trie}.
 * 
 * @since 4.0
 * @version $Id$
 */
public class SynchronizedTrie<K, V> implements Trie<K, V>, Serializable {
    
    private static final long serialVersionUID = 3121878833178676939L;
    
    private final Trie<K, V> delegate;
    
    /**
     * Factory method to create a synchronized trie.
     * 
     * @param <K>  the key type
     * @param <V>  the value type
     * @param trie  the trie to decorate, must not be null
     * @return a new synchronized trie
     * @throws IllegalArgumentException if trie is null
     */
    public static <K, V> SynchronizedTrie<K, V> synchronizedTrie(final Trie<K, V> trie) {
        return new SynchronizedTrie<K, V>(trie);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param trie  the trie to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public SynchronizedTrie(final Trie<K, V> trie) {
        if (trie == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.delegate = trie;
    }

    public synchronized Entry<K, V> select(final K key, final Cursor<? super K, ? super V> cursor) {
        return delegate.select(key, cursor);
    }

    public synchronized Entry<K, V> select(final K key) {
        return delegate.select(key);
    }

    public synchronized K selectKey(final K key) {
        return delegate.selectKey(key);
    }

    public synchronized V selectValue(final K key) {
        return delegate.selectValue(key);
    }

    public synchronized Entry<K, V> traverse(final Cursor<? super K, ? super V> cursor) {
        return delegate.traverse(cursor);
    }
    
    public synchronized Set<Entry<K, V>> entrySet() {
        return Collections.synchronizedSet(delegate.entrySet());
    }

    public synchronized Set<K> keySet() {
        return Collections.synchronizedSet(delegate.keySet());
    }

    public synchronized Collection<V> values() {
        return SynchronizedCollection.synchronizedCollection(delegate.values());
    }

    public synchronized void clear() {
        delegate.clear();
    }

    public synchronized boolean containsKey(final Object key) {
        return delegate.containsKey(key);
    }

    public synchronized boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    public synchronized V get(final Object key) {
        return delegate.get(key);
    }

    public synchronized boolean isEmpty() {
        return delegate.isEmpty();
    }

    public synchronized V put(final K key, final V value) {
        return delegate.put(key, value);
    }

    public synchronized void putAll(final Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    public synchronized V remove(final Object key) {
        return delegate.remove(key);
    }
    
    public synchronized K lastKey() {
        return delegate.lastKey();
    }

    public synchronized SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return Collections.synchronizedSortedMap(delegate.subMap(fromKey, toKey));
    }

    public synchronized SortedMap<K, V> tailMap(final K fromKey) {
        return Collections.synchronizedSortedMap(delegate.tailMap(fromKey));
    }
    
    public synchronized Comparator<? super K> comparator() {
        return delegate.comparator();
    }

    public synchronized K firstKey() {
        return delegate.firstKey();
    }

    public synchronized SortedMap<K, V> headMap(final K toKey) {
        return Collections.synchronizedSortedMap(delegate.headMap(toKey));
    }
    
    public synchronized SortedMap<K, V> getPrefixedBy(final K key, final int offset, final int length) {
        return Collections.synchronizedSortedMap(delegate.getPrefixedBy(key, offset, length));
    }

    public synchronized SortedMap<K, V> getPrefixedBy(final K key, final int length) {
        return Collections.synchronizedSortedMap(delegate.getPrefixedBy(key, length));
    }

    public synchronized SortedMap<K, V> getPrefixedBy(final K key) {
        return Collections.synchronizedSortedMap(delegate.getPrefixedBy(key));
    }

    public synchronized SortedMap<K, V> getPrefixedByBits(final K key, final int lengthInBits) {
        return Collections.synchronizedSortedMap(delegate.getPrefixedByBits(key, lengthInBits));
    }

    public synchronized SortedMap<K, V> getPrefixedByBits(final K key, 
            final int offsetInBits, final int lengthInBits) {
        return Collections.synchronizedSortedMap(delegate.getPrefixedByBits(key, offsetInBits, lengthInBits));
    }

    public synchronized int size() {
        return delegate.size();
    }

    @Override
    public synchronized int hashCode() {
        return delegate.hashCode();
    }
    
    @Override
    public synchronized boolean equals(final Object obj) {
        return delegate.equals(obj);
    }
    
    @Override
    public synchronized String toString() {
        return delegate.toString();
    }
}
