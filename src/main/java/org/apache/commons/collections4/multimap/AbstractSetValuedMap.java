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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.SetValuedMap;

/**
 * Abstract implementation of the {@link SetValuedMap} interface to simplify the
 * creation of subclass implementations.
 * <p>
 * Subclasses specify a Map implementation to use as the internal storage and
 * the Set implementation to use as values.
 *
 * @since 4.1
 * @version $Id$
 */
public abstract class AbstractSetValuedMap<K, V> extends AbstractMultiValuedMap<K, V>
    implements SetValuedMap<K, V> {

    /** Serialization version */
    private static final long serialVersionUID = 20150612L;

    /**
     * A constructor that wraps, not copies
     *
     * @param <C> the set type
     * @param map  the map to wrap, must not be null
     * @param setClazz  the collection class
     * @throws NullPointerException if the map is null
     */
    protected <C extends Set<V>> AbstractSetValuedMap(Map<K, ? super C> map, Class<C> setClazz) {
        super(map, setClazz);
    }

    /**
     * A constructor that wraps, not copies
     *
     * @param <C> the set type
     * @param map  the map to wrap, must not be null
     * @param setClazz  the collection class
     * @param initialSetCapacity  the initial size of the values set
     * @throws NullPointerException if the map is null
     * @throws IllegalArgumentException if initialSetCapacity is negative
     */
    protected <C extends Set<V>> AbstractSetValuedMap(Map<K, ? super C> map, Class<C> setClazz,
            int initialSetCapacity) {
        super(map, setClazz, initialSetCapacity);
    }

    /**
     * Gets the set of values associated with the specified key. This would
     * return an empty set in case the mapping is not present
     *
     * @param key  the key to retrieve
     * @return the <code>Set</code> of values, will return an empty
     *   <code>Set</code> for no mapping
     */
    @Override
    public Set<V> get(final K key) {
        return new WrappedSet(key);
    }

    /**
     * Removes all values associated with the specified key.
     * <p>
     * A subsequent <code>get(Object)</code> would return an empty set.
     *
     * @param key the key to remove values from
     * @return the <code>Set</code> of values removed, will return an empty,
     *   unmodifiable set for no mapping found.
     */
    @Override
    public Set<V> remove(Object key) {
        return SetUtils.emptyIfNull((Set<V>) getMap().remove(key));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SetValuedMap) {
            return asMap().equals(((SetValuedMap<?, ?>) obj).asMap());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return asMap().hashCode();
    }

    /**
     * Wrapped set to handle add and remove on the collection returned by
     * {@code get(Object)}.
     */
    protected class WrappedSet extends WrappedCollection implements Set<V> {

        public WrappedSet(final K key) {
            super(key);
        }

        @Override
        public boolean equals(Object other) {
            final Set<V> set = (Set<V>) getMapping();
            if (set == null) {
                return Collections.emptySet().equals(other);
            }
            if (!(other instanceof Set)) {
                return false;
            }
            Set<?> otherSet = (Set<?>) other;
            return SetUtils.isEqualSet(set, otherSet);
        }

        @Override
        public int hashCode() {
            final Set<V> set = (Set<V>) getMapping();
            return SetUtils.hashCodeForSet(set);
        }

    }

}
