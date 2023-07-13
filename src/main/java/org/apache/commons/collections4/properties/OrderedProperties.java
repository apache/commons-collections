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

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A drop-in replacement for {@link Properties} for ordered keys.
 * <p>
 * Overrides methods to keep keys in insertion order. Allows other methods in the superclass to work with ordered keys.
 * </p>
 *
 * @see OrderedPropertiesFactory#INSTANCE
 * @since 4.5
 */
public class OrderedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    /**
     * Preserves the insertion order.
     */
    private final LinkedHashSet<Object> orderedKeys = new LinkedHashSet<>();

    @Override
    public synchronized void clear() {
        orderedKeys.clear();
        super.clear();
    }

    @Override
    public synchronized Object compute(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
        Object compute = super.compute(key, remappingFunction);
        if (compute != null) {
            orderedKeys.add(key);
        }
        return compute;
    }

    @Override
    public synchronized Object computeIfAbsent(Object key, Function<? super Object, ? extends Object> mappingFunction) {
        Object computeIfAbsent = super.computeIfAbsent(key, mappingFunction);
        if (computeIfAbsent != null) {
            orderedKeys.add(key);
        }
        return computeIfAbsent;
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
        Object put = super.put(key, value);
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
        Object putIfAbsent = super.putIfAbsent(key, value);
        if (putIfAbsent == null) {
            orderedKeys.add(key);
        }
        return putIfAbsent;
    }

    @Override
    public synchronized Object remove(final Object key) {
        Object remove = super.remove(key);
        if (remove != null) {
            orderedKeys.remove(key);
        }
        return remove;
    }

    @Override
    public synchronized boolean remove(final Object key, final Object value) {
        boolean remove = super.remove(key, value);
        if (remove) {
            orderedKeys.remove(key);
        }
        return remove;
    }
}
