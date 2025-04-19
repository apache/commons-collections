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
package org.apache.commons.collections4.properties;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A drop-in replacement for {@link Properties} for ordered keys.
 * <p>
 * Overrides methods to keep keys in insertion order. Allows other methods in the superclass to work with ordered keys.
 * </p>
 *
 * @see OrderedPropertiesFactory#INSTANCE
 * @since 4.5.0-M1
 */
public class OrderedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    /**
     * Preserves the insertion order.
     */
    private final LinkedHashSet<Object> orderedKeys = new LinkedHashSet<>();

    /**
     * Constructs a new instance.
     */
    public OrderedProperties() {
        // empty
    }

    @Override
    public synchronized void clear() {
        orderedKeys.clear();
        super.clear();
    }

    @Override
    public synchronized Object compute(final Object key, final BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
        final Object compute = super.compute(key, remappingFunction);
        if (compute != null) {
            orderedKeys.add(key);
        }
        return compute;
    }

    @Override
    public synchronized Object computeIfAbsent(final Object key, final Function<? super Object, ? extends Object> mappingFunction) {
        final Object computeIfAbsent = super.computeIfAbsent(key, mappingFunction);
        if (computeIfAbsent != null) {
            orderedKeys.add(key);
        }
        return computeIfAbsent;
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return orderedKeys.stream().map(k -> new SimpleEntry<>(k, get(k))).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public synchronized void forEach(final BiConsumer<? super Object, ? super Object> action) {
        Objects.requireNonNull(action);
        orderedKeys.forEach(k -> action.accept(k, get(k)));
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(orderedKeys);
    }

    @Override
    public Set<Object> keySet() {
        return orderedKeys;
    }

    @Override
    public synchronized Object merge(final Object key, final Object value,
            final BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
        orderedKeys.add(key);
        return super.merge(key, value, remappingFunction);
    }

    @Override
    public Enumeration<?> propertyNames() {
        return Collections.enumeration(orderedKeys);
    }

    @Override
    public synchronized Object put(final Object key, final Object value) {
        final Object put = super.put(key, value);
        if (put == null) {
            orderedKeys.add(key);
        }
        return put;
    }

    @Override
    public synchronized void putAll(final Map<? extends Object, ? extends Object> t) {
        orderedKeys.addAll(t.keySet());
        super.putAll(t);
    }

    @Override
    public synchronized Object putIfAbsent(final Object key, final Object value) {
        final Object putIfAbsent = super.putIfAbsent(key, value);
        if (putIfAbsent == null) {
            orderedKeys.add(key);
        }
        return putIfAbsent;
    }

    @Override
    public synchronized Object remove(final Object key) {
        final Object remove = super.remove(key);
        if (remove != null) {
            orderedKeys.remove(key);
        }
        return remove;
    }

    @Override
    public synchronized boolean remove(final Object key, final Object value) {
        final boolean remove = super.remove(key, value);
        if (remove) {
            orderedKeys.remove(key);
        }
        return remove;
    }

    @Override
    public synchronized String toString() {
        // Must override for Java 17 to maintain order since the implementation is based on a map
        final int max = size() - 1;
        if (max == -1) {
            return "{}";
        }
        final StringBuilder sb = new StringBuilder();
        final Iterator<Map.Entry<Object, Object>> it = entrySet().iterator();
        sb.append('{');
        for (int i = 0;; i++) {
            final Map.Entry<Object, Object> e = it.next();
            final Object key = e.getKey();
            final Object value = e.getValue();
            sb.append(key == this ? "(this Map)" : key.toString());
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value.toString());
            if (i == max) {
                return sb.append('}').toString();
            }
            sb.append(", ");
        }
    }
}
